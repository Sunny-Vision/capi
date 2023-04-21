package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.BatchDao;
import capi.dal.VwSurveyFormBindedDao;
import capi.entity.Batch;
import capi.entity.VwSurveyFormBinded;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.BatchTableList;
import capi.service.BaseService;

@Service("BatchService")
public class BatchService extends BaseService {

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private VwSurveyFormBindedDao surveyFormDao;

	/**
	 * Get by ID
	 */
	public Batch getBatchById(int id) {
		return batchDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<BatchTableList> getBatchList(DatatableRequestModel model, String[] surveyForm){
	
		Order order = this.getOrder(model, "batchId", "code", "description", "assignmentType", "surveyForm", "batchCategory");
		
		String search = model.getSearch().get("value");

		List<String> surveyForms = new ArrayList<String>();
		if (surveyForm != null && surveyForm.length > 0){
			if(!surveyForm[0].isEmpty()){
				surveyForms = Arrays.asList(surveyForm);
			}
		}
		
		List<BatchTableList> result = batchDao.selectAllBatch(search, model.getStart(), model.getLength(), order, surveyForms);
		
		DatatableResponseModel<BatchTableList> response = new DatatableResponseModel<BatchTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = batchDao.countSelectAllBatch("", surveyForms);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = batchDao.countSelectAllBatch(search, surveyForms);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Get survey form
	 */
	public Select2ResponseModel querySureyFormSelect(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<VwSurveyFormBinded> entities = surveyFormDao.getAll();
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = surveyFormDao.countSurveyForm(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (VwSurveyFormBinded s : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(s.getSurveyForm());
			item.setText(s.getSurveyForm());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Save Batch
	 * @param model data model from ui
	 * @return true if update success, false if the code of Batch exist
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveBatch(BatchTableList model) throws Exception {

		Batch oldEntity = null;
		if (model.getBatchId() != null && model.getBatchId() > 0) {
			oldEntity = getBatchById(model.getBatchId());
		} else {
			// use sql to check code already exist or not
			Batch batch = batchDao.getBatchByCode(model.getCode());
			if (batch != null) return false;
			// create batch
			oldEntity = new Batch();
		}

		oldEntity.setCode(model.getCode());
		oldEntity.setDescription(model.getDescription());
		oldEntity.setAssignmentType(model.getAssignmentType());
		oldEntity.setSurveyForm(model.getSurveyForm());
		oldEntity.setBatchCategory(model.getBatchCategory());

		batchDao.save(oldEntity);
		batchDao.flush();
		
		return true;
	}

	/**
	 * Delete Batch
	 */
	@Transactional
	public boolean deleteBatch(List<Integer> ids) {
		
		List<Batch> batchs = batchDao.getBatchsByIds(ids);
		if (ids.size() != batchs.size()){
			return false;
		}
		
		for (Batch batch : batchs){
			batchDao.delete(batch);
		}

		batchDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 */
	public BatchTableList convertEntityToModel(Batch entity) {

		BatchTableList model = new BatchTableList();
		BeanUtils.copyProperties(entity, model);
		
		List<BatchTableList.AssignmentType> assignmentTypeList = new ArrayList<BatchTableList.AssignmentType>();
		assignmentTypeList.add(new BatchTableList.AssignmentType(1, SystemConstant.ASSIGNMENT_TYPE_1));
		assignmentTypeList.add(new BatchTableList.AssignmentType(2, SystemConstant.ASSIGNMENT_TYPE_2));
		assignmentTypeList.add(new BatchTableList.AssignmentType(3, SystemConstant.ASSIGNMENT_TYPE_3));
		
		model.setAssignmentTypeList(assignmentTypeList);
		
		return model;
	}
	

	/**
	 * Get Batch select2 format
	 */
	public Select2ResponseModel queryBatchSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<BatchTableList> batches = batchDao.selectAllBatchSelect2(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), Order.asc("code"), null);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = batchDao.countSelectAllBatchSelect2(queryModel.getTerm(), null);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (BatchTableList batch : batches) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(batch.getBatchId()));
			item.setText(batch.getCode());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	/**
	 * Get Batch select2 format
	 */
	public Select2ResponseModel queryBatchByUserSelect2(Select2RequestModel queryModel, Integer userId) {
				
		queryModel.setRecordsPerPage(10);
		
		List<BatchTableList> batches = batchDao.selectBatchByUserSelect2(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), Order.asc("code"), null, userId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = batchDao.countSelectBatchByUserSelect2(queryModel.getTerm(), null, userId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (BatchTableList batch : batches) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(batch.getBatchId()));
			item.setText(batch.getCode());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	public String queryBatchSelectSingle(Integer id){
		Batch entity = getBatchById(id);
		if (entity == null)
			return null;
		else
			return entity.getCode();
	}
	
	
	public List<Integer> getLookupTableSelectAll(String search, String surveyForm){
		return batchDao.getLookupTableSelectAll(search, surveyForm);
	}
	
	
	public List<KeyValueModel> getBatchCodes(Integer[] ids){
		List<Batch> batches = batchDao.getByIds(ids);
		if (batches != null && batches.size() > 0){
			List<KeyValueModel> models = new ArrayList<KeyValueModel>();
			for (Batch batch : batches){
				KeyValueModel model = new KeyValueModel();
				model.setKey(batch.getBatchId().toString());
				model.setValue(batch.getCode());
				models.add(model);
			}
			return models;
		}
		
		return null;
	}
	
	/**
	 * Get Batch select2 format
	 */
	public Select2ResponseModel queryBatchSelect2ForReport(Select2RequestModel queryModel, Integer[] cpiQuotationType) {
		queryModel.setRecordsPerPage(10);
		
		List<BatchTableList> batches = batchDao.selectBatchForReportCriteria(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), cpiQuotationType);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = batchDao.countSelectBatchForReportCriteria(queryModel.getTerm(), cpiQuotationType);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (BatchTableList batch : batches) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(batch.getBatchId()));
			item.setText(batch.getCode());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public List<String> getSurveyForm(){
		List<String> surveyForms = batchDao.getSurveyFormList();
		return surveyForms;
	}
	
	public boolean validateBatch(Integer outletId, List<Integer> batchIds){
		List<Boolean> countOutlets = batchDao.validateBatch(outletId, batchIds);
		for(Boolean countOutlet : countOutlets){
			if(countOutlet){
				return true;
			}
		}
		return false;
	}

}
