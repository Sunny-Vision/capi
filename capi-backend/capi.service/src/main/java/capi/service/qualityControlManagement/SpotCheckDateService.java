package capi.service.qualityControlManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.SpotCheckDateDao;
import capi.dal.SurveyMonthDao;
import capi.entity.SpotCheckDate;
import capi.entity.SurveyMonth;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.qualityControlManagement.SpotCheckDateTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("SpotCheckDateService")
public class SpotCheckDateService extends BaseService {

	@Autowired
	private SpotCheckDateDao spotCheckDateDao;

	@Autowired
	private SurveyMonthDao surveyMonthDao;

	@Autowired
	private CommonService commonService;

	/**
	 * Get by ID
	 */
	public SpotCheckDate getSpotCheckDateById(int id) {
		return spotCheckDateDao.findById(id);
	}

	public SurveyMonth getSurveyMonthById(int id) {
		return surveyMonthDao.findById(id);
	}

	public List<Integer> getSpotCheckDateIdsBySurveyMonthId(int id) {
		return spotCheckDateDao.findSpotCheckDateIdsBySurveyMonthId(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SpotCheckDateTableList> getSpotCheckDateList(DatatableRequestModel model){

		Order order = this.getOrder(model, "sm.referenceMonth", "noOfDays");
		
		String search = model.getSearch().get("value");
		
		List<SpotCheckDateTableList> result = surveyMonthDao.selectAllSpotCheckDate(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<SpotCheckDateTableList> response = new DatatableResponseModel<SpotCheckDateTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = surveyMonthDao.countSelectAllSpotCheckDate("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = surveyMonthDao.countSelectAllSpotCheckDate(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save Spot Check Date
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public void saveSpotCheckDate(SpotCheckDateTableList model) throws Exception {

		SpotCheckDate oldEntity = null;
		
		List<String> spotCheckDateFromDB = spotCheckDateDao.getSpotCheckDateOfReferenceMonth(model.getSurveyMonthId());
		
		List<String> newSpotCheckDates = model.getSpotCheckDates();
		
		Collection<String> spotCheckDatesToBeInsert = (Collection<String>)CollectionUtils.subtract(newSpotCheckDates, spotCheckDateFromDB);

		for(String newSpotCheckDate : spotCheckDatesToBeInsert) {
			
			oldEntity = new SpotCheckDate();
			SurveyMonth surveyMonth = new SurveyMonth();
			
			surveyMonth = surveyMonthDao.findById(model.getSurveyMonthId());
			
			oldEntity.setSurveyMonth(surveyMonth);
			
//			SimpleDateFormat formatter = new SimpleDateFormat(SystemConstant.DATE_FORMAT);
//			oldEntity.setDate(formatter.parse(newSpotCheckDate));
			
			oldEntity.setDate(commonService.getDate(newSpotCheckDate));
			
			spotCheckDateDao.save(oldEntity);
		}
		
		spotCheckDateDao.flush();
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public SpotCheckDateTableList convertEntityToModel(SurveyMonth entity){

		SpotCheckDateTableList model = new SpotCheckDateTableList();
		BeanUtils.copyProperties(entity, model);

		List<SpotCheckDateTableList> selectedSpotCheckDates = spotCheckDateDao.selectedSpotCheckDates(model.getSurveyMonthId());

		List<String> selectedSpotCheckDatelist = new ArrayList<String>();
		for(SpotCheckDateTableList selectedSpotCheckDate : selectedSpotCheckDates) {
			selectedSpotCheckDatelist.add(selectedSpotCheckDate.getSelectedSpotCheckDate());
		}
		String selected = StringUtils.join(selectedSpotCheckDatelist, ", ");

		model.setReferenceMonth(commonService.formatMonth(entity.getReferenceMonth()));
		model.setSpotCheckDatesList(selectedSpotCheckDatelist);
		model.setSelectedSpotCheckDate(selected);

		return model;
	}

}
