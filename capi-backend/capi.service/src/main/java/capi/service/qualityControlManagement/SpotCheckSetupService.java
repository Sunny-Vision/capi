package capi.service.qualityControlManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.CalendarEventDao;
import capi.dal.ScSvPlanDao;
import capi.dal.SpotCheckDateDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckSetupDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.UserDao;
import capi.entity.ScSvPlan;
import capi.entity.SpotCheckDate;
import capi.entity.SpotCheckForm;
import capi.entity.SpotCheckSetup;
import capi.entity.SupervisoryVisitForm;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.qualityControlManagement.SpotCheckSetupTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("SpotCheckSetupService")
public class SpotCheckSetupService extends BaseService {

	@Autowired
	private SpotCheckSetupDao spotCheckSetupDao;

	@Autowired
	private SpotCheckDateDao spotCheckDateDao;
	
	@Autowired
	private ScSvPlanDao scSvPlanDao;
	
	@Autowired
	private SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitFormDao;
	
	@Autowired
	private ScSvPlanDao qcPlanDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CalendarEventDao calendarEventDao;

	@Autowired
	private CommonService commonService;

	/**
	 * Get by ID
	 */
	public SpotCheckSetup getSpotCheckSetupById(int id) {
		return spotCheckSetupDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SpotCheckSetupTableList> getSpotCheckSetupList(DatatableRequestModel model){

		Order order = this.getOrder(model, "spotCheckSetupId", "scd.date", "fieldOfficerCode", "chineseName", "scs.notificationDate");
		
		String search = model.getSearch().get("value");
		
		List<SpotCheckSetupTableList> result = spotCheckSetupDao.selectAllSpotCheckSetup(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<SpotCheckSetupTableList> response = new DatatableResponseModel<SpotCheckSetupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = spotCheckSetupDao.countSelectAllSpotCheckSetup("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = spotCheckSetupDao.countSelectAllSpotCheckSetup(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Get spot check date select format
	 */
	public Select2ResponseModel querySpotCheckDateSelect(Select2RequestModel queryModel) {

		queryModel.setRecordsPerPage(10);
		List<SpotCheckDate> entities = spotCheckDateDao.searchAvaliableSpotCheckDate(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = spotCheckDateDao.countAvaliableSpotCheckDate(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (SpotCheckDate s : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(s.getSpotCheckDateId()));
			item.setText(commonService.formatDate(s.getDate()));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Save Spot Check Setup
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public void saveSpotCheckSetup(SpotCheckSetupTableList model) throws Exception {

		SpotCheckSetup oldEntity = null;
		Date date = null;
		
		if (model.getSpotCheckSetupId() != null && model.getSpotCheckSetupId() > 0) {
			oldEntity = getSpotCheckSetupById(model.getSpotCheckSetupId());
		} else {
			// create spot check setup
			oldEntity = new SpotCheckSetup();
		}

		SpotCheckDate spotCheckDate = spotCheckDateDao.findById(model.getSpotCheckDateId());
		if(spotCheckDate != null) {
			oldEntity.setSpotCheckDate(spotCheckDate);
			date = spotCheckDate.getDate();
		}
		
		User user = userDao.findById(model.getFieldOfficerId());
		if(user != null) {
			oldEntity.setUser(user);
		}
		
		if(model.getNotificationType() == 1) {
			oldEntity.setNotificationDate(date);
		} else {
			date = calendarEventDao.findPreviousWorkingDate(date);
			oldEntity.setNotificationDate(date);
		}
		oldEntity.setNotificationType(model.getNotificationType());
		
		Date notifyDate = commonService.getDateWithoutTime(oldEntity.getNotificationDate());
		Date today = commonService.getDateWithoutTime(new Date());
		if ((notifyDate.equals(today) || notifyDate.before(today)) && (oldEntity.getScSvplans() == null || oldEntity.getScSvplans().size() == 0)){
			ScSvPlan plan = new ScSvPlan();
			plan.setMandatoryPlan(true);
			plan.setMandatoryPlan(true);
			plan.setOwner(oldEntity.getUser().getSupervisor());
			plan.setUser(oldEntity.getUser());
			plan.setQcType(1);
			plan.setVisitDate(oldEntity.getSpotCheckDate().getDate());
			plan.setSpotCheckSetup(oldEntity);
			qcPlanDao.save(plan);
			
			SpotCheckForm form = new SpotCheckForm();
			form.setOfficer(plan.getUser());
			form.setSupervisor(plan.getOwner());
			form.setSpotCheckDate(plan.getVisitDate());
			form.setScSvPlan(plan);
			spotCheckFormDao.save(form);
			
//			Date visitDate = commonService.getDateWithoutTime(plan.getVisitDate());
//			if (visitDate.equals(today) || visitDate.before(today)){
//				SpotCheckForm form = new SpotCheckForm();
//				form.setOfficer(plan.getUser());
//				form.setSupervisor(plan.getOwner());
//				form.setSpotCheckDate(plan.getVisitDate());
//				form.setScSvPlan(plan);
//				spotCheckFormDao.save(form);
//			}
		}		

		spotCheckSetupDao.save(oldEntity);
		spotCheckSetupDao.flush();
	}

	/**
	 * Delete spot check setup
	 */
	@Transactional
	public boolean deleteSpotCheckSetup(List<Integer> ids) {

		List<SpotCheckSetup> spotCheckSetups = spotCheckSetupDao.getSpotCheckSetupsByIds(ids, true);
		if (spotCheckSetups.size() != ids.size()){
			return false;
		}
		
		for (SpotCheckSetup spotCheckSetup: spotCheckSetups){
			for (ScSvPlan plan : spotCheckSetup.getScSvplans()){	
				for (SpotCheckForm form : plan.getSpotCheckForms()){
					spotCheckFormDao.delete(form);
				}
				for (SupervisoryVisitForm form : plan.getSupervisoryVisitForms()){
					supervisoryVisitFormDao.delete(form);
				}
				scSvPlanDao.delete(plan);
			}
			spotCheckSetupDao.delete(spotCheckSetup);
		}		
		
		spotCheckSetupDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public SpotCheckSetupTableList convertEntityToModel(SpotCheckSetup entity){

		SpotCheckSetupTableList model = new SpotCheckSetupTableList();
		BeanUtils.copyProperties(entity, model);

		if(entity.getSpotCheckSetupId() != null) {
			model.setNotificationDate(commonService.formatDate(entity.getNotificationDate()));
		}
		if(entity.getSpotCheckDate() != null) {
			model.setSpotCheckDateId(entity.getSpotCheckDate().getSpotCheckDateId());
			model.setSpotCheckDate(commonService.formatDate(entity.getSpotCheckDate().getDate()));
		}
		if(entity.getUser() != null) {
			model.setFieldOfficerId(entity.getUser().getUserId());
			model.setFieldOfficerCode(entity.getUser().getStaffCode());
			model.setChineseName(entity.getUser().getChineseName());
			model.setFieldOfficer(entity.getUser().getStaffCode()
					+ " - " + entity.getUser().getChineseName());
			if(entity.getUser().getSupervisor() != null) {
				model.setSupervisor(entity.getUser().getSupervisor().getStaffCode()
						+ " - " + entity.getUser().getSupervisor().getUsername());
			}
		}

		return model;
	}

	public String getSupervisorFromUserId(Integer userId) {
		return userDao.getSupervisorFromUserId(userId);
	}
}
