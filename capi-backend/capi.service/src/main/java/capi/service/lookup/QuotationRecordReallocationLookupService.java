package capi.service.lookup;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentReallocationDao;
import capi.dal.TpuDao;
import capi.entity.Tpu;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.QuotationRecordReallocationLookupTableList;
import capi.service.BaseService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("QuotationRecordReallocationLookupService")
public class QuotationRecordReallocationLookupService extends BaseService {
	
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
	public DatatableResponseModel<QuotationRecordReallocationLookupTableList> getLookupTableList(DatatableRequestModel model,
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, 
			String collectionDate, Integer[] oldQuotationRecordIds, String category, String quotationStatus){

		Order order = this.getOrder(model, "", "collectionDate2", "startDate2", "endDate2", "firm", "district", 
									"tpu", "batchCode", "displayName", "category", "quotationStatus");
		
		String search = model.getSearch().get("value");
		
		List<Integer> excludeQuotationRecordIds = new ArrayList<Integer>();
		if(oldQuotationRecordIds != null && oldQuotationRecordIds.length > 0){
			excludeQuotationRecordIds.addAll(Arrays.asList(oldQuotationRecordIds));
		}
		
		excludeQuotationRecordIds.addAll(assignmentReallocationDao.getSubmittedRecommendedQuotationRecordId());
		
		List<QuotationRecordReallocationLookupTableList> result = assignmentReallocationDao.getQuotationRecordReallocationLookupTableList(
				search, model.getStart(), model.getLength(), order,
				originalUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, excludeQuotationRecordIds,category, quotationStatus);
		
		DatatableResponseModel<QuotationRecordReallocationLookupTableList> response = new DatatableResponseModel<QuotationRecordReallocationLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countQuotationRecordReallocationLookupTableList("", originalUserId, null, null, 
															null, null, null, excludeQuotationRecordIds,null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countQuotationRecordReallocationLookupTableList(search, originalUserId, tpuIds, 
															outletTypeId, districtId, batchId, collectionDate, excludeQuotationRecordIds,category, quotationStatus);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search,
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate,
			String category, String quotationStatus){

		List<Integer> excludedQuotationRecordIds = assignmentReallocationDao.getSubmittedRecommendedQuotationRecordId(); 
		
		return assignmentReallocationDao.getQuotationRecordReallocationLookupTableSelectAll(search,
				originalUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, category, quotationStatus, excludedQuotationRecordIds);
	}

	/**
	 * Get table list by ids
	 */
	public List<QuotationRecordReallocationLookupTableList> getTableListByIds(Integer originalUserId, Integer[] quotationRecordIds){
		List<Integer> excludedQuotationRecordIds = assignmentReallocationDao.getSubmittedRecommendedQuotationRecordId();
		
		return assignmentReallocationDao.getQuotationRecordReallocationListByIds(originalUserId, quotationRecordIds, excludedQuotationRecordIds);
	}

}
