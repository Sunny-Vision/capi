package capi.service.assignmentAllocationAndReallocation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AllocationBatchDao;
import capi.entity.AllocationBatch;
import capi.entity.SurveyMonth;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("AllocationBatchService")
public class AllocationBatchService extends BaseService {
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private CommonService commonService;
	
	public List<AllocationBatch> getUnassignedAllocationBatchBySurveyMonth(SurveyMonth sm){
		Date today = commonService.getDateWithoutTime(new Date());
		return this.allocationBatchDao.getUnassignedAllocationBatchBySurveyMonth(sm, today);
	}
	
	public AllocationBatch getAllocationBatchById(Integer allocationBatchId){
		return this.allocationBatchDao.findById(allocationBatchId);
		
	}

	public Select2ResponseModel queryAllocationBatchSelect2(
			Select2RequestModel queryModel, Date referenceMonth) {
		
		queryModel.setRecordsPerPage(10);
		List<AllocationBatch> entities = this.allocationBatchDao.searchAllocationBatch(queryModel.getTerm(), referenceMonth, queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = allocationBatchDao.countSearch(queryModel.getTerm(), referenceMonth);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (AllocationBatch d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getBatchName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Allocation Batch select format by reference Month range
	 */
	public Select2ResponseModel queryAllocationBatchSelect2ByMonthRange(Select2RequestModel queryModel, Date fromMonth, Date toMonth){
		queryModel.setRecordsPerPage(10);
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<String> entities = null;
		
		entities = allocationBatchDao.searchAllocationBatchByMonthRange(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), fromMonth, toMonth);
		
		long recordsTotal = allocationBatchDao.countSearchAllocationBatchByMonthRange(queryModel.getTerm(), fromMonth, toMonth);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for(String d : entities){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	public Select2ResponseModel queryAllocationBatchByRefMonth(Select2RequestModel queryModel, String refMonthStr) throws Exception {
		queryModel.setRecordsPerPage(10);
		queryModel.setRecordsTotal(0);
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		
		if (StringUtils.isNotBlank(refMonthStr)) {
			Date refMonth = commonService.getMonth(refMonthStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(refMonth);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			refMonth = calendar.getTime();
			
			List<AllocationBatch> allocationBatchLists = allocationBatchDao.searchAllocationBatch(queryModel.getTerm(), refMonth, queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
			long recordsTotal = allocationBatchDao.countSearch(queryModel.getTerm(), refMonth);
			queryModel.setRecordsTotal(recordsTotal);
			boolean hasMore = queryModel.hasMore();
			Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
			pagination.setMore(hasMore);
			responseModel.setPagination(pagination);
			
			for(AllocationBatch batch : allocationBatchLists) {
				Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
				item.setId(String.valueOf(batch.getAllocationBatchId()));
				item.setText(batch.getBatchName());
				items.add(item);
			}
			
		}
		
		responseModel.setResults(items);
		
		return responseModel;
	}
	
}
