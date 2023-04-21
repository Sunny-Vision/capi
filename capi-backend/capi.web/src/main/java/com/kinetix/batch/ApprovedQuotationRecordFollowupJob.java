package com.kinetix.batch;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.QuotationRecordDao;
import capi.entity.QuotationRecord;
import capi.service.CommonService;
import capi.service.assignmentManagement.AssignmentApprovalService;
import capi.service.assignmentManagement.DataConversionService;
import capi.service.assignmentManagement.QuotationRecordService;
import edu.emory.mathcs.backport.java.util.Collections;

@Component("ApprovedQuotationRecordFollowupJob")
public class ApprovedQuotationRecordFollowupJob {
private static final Logger logger = LoggerFactory.getLogger(ApprovedQuotationRecordFollowupJob.class);
	
	@Autowired
	private AssignmentApprovalService approvalService;
	@Autowired
	private DataConversionService dataConversionService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Async
	@Transactional
	public void followup(Integer id){
		
		try{
			QuotationRecord entity = quotationRecordService.getQuotationRecordById(id);			
			
			dataConversionService.convert(entity);
			
			quotationRecordDao.flush();
			
		}
		catch(Exception ex){
			logger.error("Approved Quotation Record Followup Job failed", ex);
		}
		
	}
	
	@Async
	@Transactional
	public void followupRUA(List<Integer> quotationRecordIds){
		try{
			List<List<Integer>> splitQuotationRecordIds = commonService.splitListByMaxSize(quotationRecordIds);
			for (List<Integer> subIds : splitQuotationRecordIds) {
				List<Integer> backtrackIds = quotationRecordDao.getBackTrackQuotationRecordByQuotationRecordIds(subIds);
				if (backtrackIds != null && backtrackIds.size() > 0) {
					quotationRecordIds.addAll(backtrackIds);
				}
			}
			List<QuotationRecord> entities = quotationRecordService.recursiveQuery(quotationRecordIds);
			
			Collections.sort(entities, new Comparator<QuotationRecord>(){
				@Override
				public int compare(QuotationRecord o1,
						QuotationRecord o2){
					// TODO Auto-generated method stub
					return o1.getReferenceDate().compareTo(o2.getReferenceDate());
				}
			});
			
			for (QuotationRecord entity : entities) {
				dataConversionService.convert(entity);
				approvalService.handleVerification(entity);
				if(entity.getIndoorQuotationRecord()!=null){
					entity.getIndoorQuotationRecord().setStatus("Complete");
				}
			}

			quotationRecordDao.flush();
		} catch(Exception ex) {
			logger.error("Approved RUA Quotation Record Followup Job failed", ex);
		}
		
	}
}
