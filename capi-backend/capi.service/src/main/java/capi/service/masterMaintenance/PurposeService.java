package capi.service.masterMaintenance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import capi.dal.PurposeDao;
import capi.entity.Purpose;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.PurposeSyncData;
import capi.model.masterMaintenance.SurveyTypeTableList;
import capi.service.BaseService;

@Service("PurposeService")
public class PurposeService extends BaseService {

	@Autowired
	private PurposeDao purposeDao;
	
	/**
	 * Get by ID
	 */
	public Purpose getPurposeById(int id) {
		return purposeDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SurveyTypeTableList> getSurveyTypeList(DatatableRequestModel model){

		Order order = this.getOrder(model, "purposeId", "code", "name", "survey");
		
		String search = model.getSearch().get("value");
		
		List<SurveyTypeTableList> result = purposeDao.selectAllSurveyType(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<SurveyTypeTableList> response = new DatatableResponseModel<SurveyTypeTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = purposeDao.countSelectAllSurveyType("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = purposeDao.countSelectAllSurveyType(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save District
	 * @param model data model from ui
	 * @return true if update successfully, false if district code exist
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveSurveyType(SurveyTypeTableList model) throws Exception {

		Purpose oldEntity = null;
		if (model.getPurposeId() != null && model.getPurposeId() > 0) {
			oldEntity = getPurposeById(model.getPurposeId());
		} else {
			// create district
			Purpose surveyType = purposeDao.getSurveyTypeByCode(model.getCode());
			if (surveyType != null) return false;
			
			oldEntity = new Purpose();
		}

		oldEntity.setCode(model.getCode());
		oldEntity.setName(model.getName());
		oldEntity.setSurvey(model.getSurvey());
		oldEntity.setNote(model.getNote());
		String outcome = null;

		if (model.getOutcomes() != null && model.getOutcomes().size() > 0){
			outcome = StringUtils.join(model.getOutcomes(), ",");
		}
		
		if (!StringUtils.isEmpty(model.getSurvey())){
			List<Purpose> purposes = purposeDao.findPurposeBySurvey(model.getSurvey());
			for (Purpose purpose: purposes){
				purpose.setEnumerationOutcomes(outcome);
			}
		}
		
		oldEntity.setEnumerationOutcomes(outcome);

		purposeDao.save(oldEntity);
		purposeDao.flush();
		return true;
	}

	/**
	 * Delete district
	 */
	@Transactional
	public boolean deleteSurveyType(List<Integer> ids) {

		// Delete all the TPU which is linked with district		
		List<Purpose> surveyTypes = purposeDao.getSurveyTypesByIds(ids);
		if (surveyTypes.size() != ids.size()){
			return false;
		}
		
		for (Purpose surveyType: surveyTypes){
			purposeDao.delete(surveyType);
		}		
		
		purposeDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public SurveyTypeTableList convertEntityToModel(Purpose entity){

		SurveyTypeTableList model = new SurveyTypeTableList();
		BeanUtils.copyProperties(entity, model);
		
		List<String> surveyList = new ArrayList<String>();
		surveyList.add(new String(SystemConstant.SURVEY_1));
		surveyList.add(new String(SystemConstant.SURVEY_2));
		surveyList.add(new String(SystemConstant.SURVEY_3));
		surveyList.add(new String(SystemConstant.SURVEY_4));
		
		model.setSurveyList(surveyList);
		
		if (!StringUtils.isEmpty(entity.getEnumerationOutcomes())){
			model.setOutcomes(CollectionUtils.arrayToList(entity.getEnumerationOutcomes().split(",")));
		}
		
		return model;
	}
	

	/**
	 * Get Purpose select2 format
	 */
	public Select2ResponseModel queryPurposeSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<Purpose> purposes = purposeDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = purposeDao.countSearch(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Purpose purpose : purposes) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(purpose.getId()));
//			item.setText(purpose.getCode() + " " + purpose.getName());
			item.setText(purpose.getCode());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Purpose select2 format
	 */
	public Select2ResponseModel querySurveySelect2(Select2RequestModel queryModel,Integer purposeId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> surveys = purposeDao.searchSurvey(queryModel.getTerm(), purposeId, queryModel.getFirstRecord(), queryModel.getRecordsPerPage());

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = purposeDao.countSearch(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String survey : surveys) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(survey);
			item.setText(survey);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	
	public String getPurposeSelectSingle(Integer id){
		Purpose purpose= purposeDao.findById(id);
		if (purpose == null){
			return null;
		}
		else{
			return purpose.getCode() + " - " + purpose.getName();
		}
	}
	
	public List<PurposeSyncData> getUpdatePurpose(Date lastSyncTime){
		return purposeDao.getUpdatePurpose(lastSyncTime);
	}
}
