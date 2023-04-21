package capi.service.lookup;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.AssignmentReallocationDao;
import capi.dal.TpuDao;
import capi.entity.Tpu;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.AssignmentReallocationLookupTableList;
import capi.service.BaseService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("AssignmentReallocationLookupService")
public class AssignmentReallocationLookupService extends BaseService {
	
	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;

	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private TpuDao tpuDao;

	/**
	 * Get all tpus
	 */
	public List<Tpu> getTpus() {
		return tpuDao.getAll();
	}
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<AssignmentReallocationLookupTableList> getLookupTableList(DatatableRequestModel model,
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, 
			String collectionDate, Integer[] oldAssignmentIds, Integer assignmentStatus, Integer surveyMonthId){

		Order order = this.getOrder(model, "", "a.collectionDate", "a.startDate", "a.endDate", "firm", "district", 
							"tpu", "batchCode", "noOfQuotation", "assignmentStatus");
		
		String search = model.getSearch().get("value");
		
		List<Integer> excludeAssignmentIds = new ArrayList<Integer>();
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0){
			excludeAssignmentIds.addAll(Arrays.asList(oldAssignmentIds));
		}
		
		excludeAssignmentIds.addAll(assignmentReallocationDao.getSubmittedRecommendedAssignmentId());
		
		List<AssignmentReallocationLookupTableList> result = assignmentReallocationDao.getAssignmentReallocationLookupTableList(search, model.getStart(), model.getLength(), order,
				originalUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, excludeAssignmentIds, assignmentStatus, surveyMonthId);
		List<AssignmentReallocationLookupTableList.BatchCodeMapping> batchs = assignmentReallocationDao.getAssignmentReallocationBatchLookupTableList
				(search, model.getStart(), model.getLength(), order,
				originalUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, excludeAssignmentIds, assignmentStatus, surveyMonthId);
		
		for(AssignmentReallocationLookupTableList consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(AssignmentReallocationLookupTableList.BatchCodeMapping batch : batchs) {
				if(batch.getAssignmentId().equals(consequence.getId())) {
					sb.append(batch.getBatchCode());
					sb.append(", ");
				}
			}
			if(sb.length() > 2) {
				sb.delete(sb.length()-2, sb.length());
				if("null".equals(sb.toString()))
					consequence.setBatchCode(null);
				else
					consequence.setBatchCode(sb.toString());
			} else {
				consequence.setBatchCode(null);
			}
		}
		
		DatatableResponseModel<AssignmentReallocationLookupTableList> response = new DatatableResponseModel<AssignmentReallocationLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countAssignmentReallocationLookupTableList("", originalUserId, null, null, null, null, null, 
				excludeAssignmentIds, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countAssignmentReallocationLookupTableList(search, originalUserId, tpuIds, outletTypeId, 
														districtId, batchId, collectionDate, excludeAssignmentIds, assignmentStatus, surveyMonthId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search,
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate,
			Integer assignmentStatus, Integer surveyMonthId){

		List<Integer> excludedAssignmentIds = assignmentReallocationDao.getSubmittedRecommendedAssignmentId();
		
		return assignmentReallocationDao.getAssignmentReallocationLookupTableSelectAll(search,
				originalUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate,assignmentStatus, surveyMonthId, excludedAssignmentIds);
	}

	/**
	 * Get table list by ids
	 */
	public List<AssignmentReallocationLookupTableList> getTableListByIds(Integer originalUserId, Integer[] assignmentIds){
		List<Integer> excludedAssignmentIds = assignmentReallocationDao.getSubmittedRecommendedAssignmentId();
		List<AssignmentReallocationLookupTableList> result = assignmentReallocationDao.getAssignmentReallocationListByIds(originalUserId, assignmentIds, excludedAssignmentIds);
		List<AssignmentReallocationLookupTableList.BatchCodeMapping> batchs 
				= assignmentDao.getAssignmentReallocationBatchListByIds(assignmentIds);
		
		for(AssignmentReallocationLookupTableList consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(AssignmentReallocationLookupTableList.BatchCodeMapping batch : batchs) {
				if(batch.getAssignmentId().equals(consequence.getId())) {
					sb.append(batch.getBatchCode());
					sb.append(", ");
				}
			}
			if(sb.length() > 2) {
				sb.delete(sb.length()-2, sb.length());
				if("null".equals(sb.toString()))
					consequence.setBatchCode(null);
				else
				consequence.setBatchCode(sb.toString());
			} else {
				consequence.setBatchCode(null);
			}
		}
		
		//return assignmentReallocationDao.getAssignmentReallocationListByIds(originalUserId, assignmentIds);
		return result;
	}

}
