package capi.service.lookup;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentReallocationDao;
import capi.dal.TpuDao;
import capi.entity.Tpu;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.OutstandingAssignmentLookupTableList;
import capi.service.BaseService;

@Service("OutstandingAssignmentLookupService")
public class OutstandingAssignmentLookupService extends BaseService {
	
	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;

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
	public DatatableResponseModel<OutstandingAssignmentLookupTableList> getLookupTableList(DatatableRequestModel model,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate, Integer[] oldAssignmentIds){
		//2018-01-15 cheung missing start end date
//		Order order = this.getOrder(model, "", "collectionDate","firm", "district", "tpu", "batchCode", "noOfQuotation", "pic");
		Order order = this.getOrder(model, "", "collectionDate", "seDate","firm", "district", "tpu", "batchCode", "noOfQuotation", "pic");
		
		String search = model.getSearch().get("value");
		
		List<OutstandingAssignmentLookupTableList> result = assignmentReallocationDao.getOutstandingAssignmentLookupTableList(search, model.getStart(), model.getLength()
				, order, tpuIds, outletTypeId, districtId, batchId, collectionDate, oldAssignmentIds);
		List<OutstandingAssignmentLookupTableList.BatchCodeMapping> batchs = assignmentReallocationDao.getOutstandingAssignmentBatchLookupTableList
				(search, model.getStart(), model.getLength()
				, order, tpuIds, outletTypeId, districtId, batchId, collectionDate, oldAssignmentIds);
		
		for(OutstandingAssignmentLookupTableList consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(OutstandingAssignmentLookupTableList.BatchCodeMapping batch : batchs) {
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
		
		DatatableResponseModel<OutstandingAssignmentLookupTableList> response = new DatatableResponseModel<OutstandingAssignmentLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countOutstandingAssignmentLookupTableList("", tpuIds, outletTypeId, districtId, batchId, collectionDate, oldAssignmentIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countOutstandingAssignmentLookupTableList(search, tpuIds, outletTypeId, districtId, batchId, collectionDate, oldAssignmentIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate){

		return assignmentReallocationDao.getOutstandingAssignmentLookupTableSelectAll(search,
				tpuIds, outletTypeId, districtId, batchId, collectionDate);
	}

	/**
	 * Get table list by ids
	 */
	public List<OutstandingAssignmentLookupTableList> getTableListByIds(Integer[] assignmentIds){
		
		List<OutstandingAssignmentLookupTableList> result = assignmentReallocationDao.getOutstandingAssignmentListByIds(assignmentIds);
		List<OutstandingAssignmentLookupTableList.BatchCodeMapping> batchs = assignmentReallocationDao.getOutstandingAssignmentBatchListByIds(assignmentIds);
		
		for(OutstandingAssignmentLookupTableList consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(OutstandingAssignmentLookupTableList.BatchCodeMapping batch : batchs) {
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
		
		//return assignmentReallocationDao.getOutstandingAssignmentListByIds(assignmentIds);
		return result;
	}

}
