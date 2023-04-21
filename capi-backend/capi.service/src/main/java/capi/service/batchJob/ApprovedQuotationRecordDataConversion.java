package capi.service.batchJob;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.QuotationRecordDao;
import capi.dal.SurveyMonthDao;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.service.CommonService;
import capi.service.assignmentManagement.AssignmentApprovalService;
import capi.service.assignmentManagement.DataConversionService;

/**
 * Data conversion for not Current Generate Approved QuotationRecord 
 * @author man Leung
 *	
 */
@Service("ApprovedQuotationRecordDataConversion")
public class ApprovedQuotationRecordDataConversion implements BatchJobService{
	
	private static final Logger logger = LoggerFactory.getLogger(ApprovedQuotationRecordDataConversion.class);
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private AssignmentApprovalService approvalService;
	
	@Autowired
	private DataConversionService conversionService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Approved Quotation Record Data Conversion";
	}
	
	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		today = DateUtils.addDays(today, -1);
		
		SurveyMonth curSurveyMonth = surveyMonthDao.getSurveyMonthByDate(today);
		
		Date lastMonth = DateUtils.addMonths(curSurveyMonth.getReferenceMonth(), -1);
		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(lastMonth);
		
		Date closingDate = month.getClosingDate().getClosingDate();
		
		boolean convertClosingDate = false;
		
		if (today.before(closingDate) || today.equals(closingDate)){
			convertClosingDate = true;
		}
		
		// Get Approved QuotationRecord Id which no IndoorQuotationRecord
		if (curSurveyMonth != null){
			List<QuotationRecord> records = null;
			if(quotationRecordDao.countAprovedQuotationRecordNotConversion(month.getSurveyMonthId()
				, false)>0){
				records = quotationRecordDao.getApprovedQuotationRecordForConversion(month.getSurveyMonthId()
						, false);
			} else {
				records = quotationRecordDao.getApprovedQuotationRecordForConversion(curSurveyMonth.getSurveyMonthId()
						, convertClosingDate);
			}
			
			if (records != null && records.size() > 0){
				for (QuotationRecord record : records){
					conversionService.convert(record);
				}
			}
				
			quotationRecordDao.flush();
			
//			if(records != null && records.size() > 0){
//				approvalService.runPELogic(records);
//			}
		} 
	}
	
	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		today = DateUtils.addDays(today, -1);
		
		SurveyMonth curSurveyMonth = surveyMonthDao.getSurveyMonthByDate(today);
		
		Date lastMonth = DateUtils.addMonths(curSurveyMonth.getReferenceMonth(), -1);
		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(lastMonth);
		
		Date closingDate = month.getClosingDate().getClosingDate();
		
		boolean convertClosingDate = false;
		
		if (today.before(closingDate) || today.equals(closingDate)){
			convertClosingDate = true;
		}
		
		Integer count = quotationRecordDao.countAprovedQuotationRecordNotConversion(month.getSurveyMonthId()
				, false);
		
		if(!(count > 0)){
			count = quotationRecordDao.countAprovedQuotationRecordNotConversion(curSurveyMonth.getSurveyMonthId()
					, convertClosingDate);
		}
		
		return count > 0;
	}
	
}
