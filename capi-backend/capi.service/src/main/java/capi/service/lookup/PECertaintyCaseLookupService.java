package capi.service.lookup;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.PECheckTaskDao;
import capi.dal.SurveyMonthDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.PECertaintyCaseLookupTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("PECertaintyCaseLookupService")
public class PECertaintyCaseLookupService extends BaseService {
	
	@Autowired
	private PECheckTaskDao peCheckTaskDao;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<PECertaintyCaseLookupTableList> getLookupTableList(DatatableRequestModel model, String refMonth
			, String outletTypeId, Integer purposeId, Integer districtId, Integer tpuId, String certaintyCase
			, Integer[] assignmentIds) throws Exception{

		Order order = this.getOrder(model, "", "outletCode", "firm", "", "collectionDate", "district", "tpu", "address", "noOfQuotation", "isCertaintyCase", "lastPECheckMonth");
		
		String search = model.getSearch().get("value");
		
		List<PECertaintyCaseLookupTableList> results = peCheckTaskDao.getPECertaintyCaseLookupTableList(search, model.getStart(), model.getLength(), order,
				commonService.getMonth(refMonth), outletTypeId, purposeId, districtId, tpuId, certaintyCase, assignmentIds);
		
		List<Integer> ids = new ArrayList<Integer>();
		for(PECertaintyCaseLookupTableList result : results){
			ids.add(result.getAssignmentId());
		}
		
		List<PECertaintyCaseLookupTableList.BatchCodeMapping> batchs = assignmentDao.getAssignmentBatchListByIds(surveyMonthDao.getSurveyMonthByReferenceMonth(commonService.getMonth(refMonth)).getSurveyMonthId(), ids.toArray(new Integer[0]));
		
		for(PECertaintyCaseLookupTableList consequence : results) {
			StringBuilder sb = new StringBuilder();
			for(PECertaintyCaseLookupTableList.BatchCodeMapping batch : batchs) {
				if(batch.getAssignmentId().equals(consequence.getAssignmentId())) {
					sb.append(batch.getBatchCode());
					sb.append(", ");
				}
			}
			sb.delete(sb.length()-2, sb.length());
			consequence.setBatchCode(sb.toString());
		}
		
		DatatableResponseModel<PECertaintyCaseLookupTableList> response = new DatatableResponseModel<PECertaintyCaseLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(results);
		Long recordTotal = peCheckTaskDao.countPECertaintyCaseLookupTableList("",commonService.getMonth(refMonth), outletTypeId, purposeId, districtId, tpuId, certaintyCase, assignmentIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = peCheckTaskDao.countPECertaintyCaseLookupTableList(search,commonService.getMonth(refMonth), outletTypeId, purposeId, districtId, tpuId, certaintyCase, assignmentIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, String refMonth
			, String outletTypeId, Integer purposeId, Integer districtId, Integer tpuId, String certaintyCase
			, Integer[] assignmentIds) throws Exception{
		
		
		return peCheckTaskDao.getPECertaintyCaseLookupTableSelectAll(search
				, commonService.getMonth(refMonth), outletTypeId, purposeId
				, districtId, tpuId, certaintyCase, assignmentIds);
	}

	/**
	 * Get table list by ids
	 */
	public List<PECertaintyCaseLookupTableList> getTableListByIds(Integer surveyMonthId, Integer[] assignmentIds){
		
		List<PECertaintyCaseLookupTableList> result = peCheckTaskDao.getPECertaintyCaseListByIds(surveyMonthId, assignmentIds);
		List<PECertaintyCaseLookupTableList.BatchCodeMapping> batchs = assignmentDao.getAssignmentBatchListByIds(surveyMonthId, assignmentIds);
		
		for(PECertaintyCaseLookupTableList consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(PECertaintyCaseLookupTableList.BatchCodeMapping batch : batchs) {
				if(batch.getAssignmentId().equals(consequence.getAssignmentId())) {
					sb.append(batch.getBatchCode());
					sb.append(", ");
				}
			}
			sb.delete(sb.length()-2, sb.length());
			consequence.setBatchCode(sb.toString());
		}
		
		return result;
	}

}
