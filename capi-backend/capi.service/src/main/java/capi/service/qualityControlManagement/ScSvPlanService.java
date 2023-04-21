package capi.service.qualityControlManagement;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.RoleDao;
import capi.dal.ScSvPlanDao;
import capi.dal.SpotCheckDateDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckSetupDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UserDao;
import capi.entity.Role;
import capi.entity.ScSvPlan;
import capi.entity.SpotCheckForm;
import capi.entity.SupervisoryVisitForm;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.qualityControlManagement.ScSvPlanEditModel;
import capi.model.qualityControlManagement.ScSvPlanTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("ScSvPlanService")
public class ScSvPlanService extends BaseService {
	
	@Autowired
	private SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitFormDao;
	
	@Autowired
	private ScSvPlanDao scSvPlanDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SpotCheckDateDao spotCheckDateDao;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private SpotCheckSetupDao spotCheckSetupDao;
	
	@Autowired
	private RoleDao roleDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<ScSvPlanTableList> queryScSvPlan(DatatableRequestModel model, Integer userId, Integer qcType
														, boolean aboveSupervisor, List<Integer> actedUserIds, int authorityLevel){
		
		Order order = this.getOrder(model,"scSvPlanId", "visitDate2", "u.staffCode", "u.chineseName","u.englishName",
				"u.team","sup.staffCode","o.staffCode","s.qcType");
		
		String search = model.getSearch().get("value");
		
		Role admin = roleDao.getRoleByCode("Admin");
		Role state = roleDao.getRoleByCode("Stat");
		Role scso = roleDao.getRoleByCode("SCSO");
		
		Boolean hasAuthorityViewAllRecord = (authorityLevel & admin.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & state.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & scso.getAuthorityLevel()) == authorityLevel ;
		
		List<ScSvPlanTableList> result = scSvPlanDao.listScSvPlan(search, model.getStart(), model.getLength(), order, userId, qcType
											, aboveSupervisor, actedUserIds, hasAuthorityViewAllRecord);
		
		DatatableResponseModel<ScSvPlanTableList> response = new DatatableResponseModel<ScSvPlanTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Integer recordTotal = scSvPlanDao.countScSvPlan("", userId, null, aboveSupervisor, actedUserIds, hasAuthorityViewAllRecord);
		response.setRecordsTotal(recordTotal);
		Integer recordFiltered = scSvPlanDao.countScSvPlan(search,userId, qcType, aboveSupervisor, actedUserIds, hasAuthorityViewAllRecord);
		response.setRecordsFiltered(recordFiltered);
		
		return response;
	}
	

	/**
	 * Get by ID
	 */
	public ScSvPlan getScSvPlanById(int id) {
		return scSvPlanDao.findById(id);
	}


	/**
	 * Save
	 * @throws ParseException 
	 */
	@Transactional
	public boolean saveScSvPlan(ScSvPlanEditModel model, Integer userId) throws ParseException {
		
		ScSvPlan oldEntity = null;
		User owner = userDao.findById((model.getCheckerId() != null) ? model.getCheckerId() : userId);
		User user = userDao.findById(model.getUserId());
		
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getScSvPlanById(model.getId());
			if (oldEntity == null){
				return false;
			}
			
		} else {
			oldEntity = new ScSvPlan();
		}
		
		oldEntity.setQcType(model.getQcType());
		oldEntity.setOwner(owner);
		oldEntity.setUser(user);
		
		Date visitDate = commonService.getDate(model.getVisitDate());
		oldEntity.setVisitDate(visitDate); 	

		if (oldEntity.getQcType() == 1) {
			if (oldEntity.getSpotCheckForms() != null && oldEntity.getSpotCheckForms().size() > 0) {
				for (SpotCheckForm form : oldEntity.getSpotCheckForms()) {
					form.setOfficer(oldEntity.getUser());
					form.setSupervisor(oldEntity.getOwner());
					form.setSpotCheckDate(oldEntity.getVisitDate());
				}
			}
			if (oldEntity.getSupervisoryVisitForms() != null && oldEntity.getSupervisoryVisitForms().size() > 0) {
				for (SupervisoryVisitForm form : oldEntity.getSupervisoryVisitForms()) {
					supervisoryVisitFormDao.delete(form);
				}
			}
		} else if (oldEntity.getQcType() == 2) {
			if (oldEntity.getSpotCheckForms() != null && oldEntity.getSpotCheckForms().size() > 0) {
				for (SpotCheckForm form : oldEntity.getSpotCheckForms()) {
					spotCheckFormDao.delete(form);
				}
			}
			if (oldEntity.getSupervisoryVisitForms() != null && oldEntity.getSupervisoryVisitForms().size() > 0) {
				for (SupervisoryVisitForm form : oldEntity.getSupervisoryVisitForms()) {
					form.setSupervisor(oldEntity.getOwner());
					form.setUser(oldEntity.getUser());
					form.setVisitDate(oldEntity.getVisitDate());
				}
			}
		}
		
		scSvPlanDao.save(oldEntity);
		//Date today = commonService.getDateWithoutTime(new Date());
		
		//if (today.after(visitDate) || visitDate.equals(today)){
		if (oldEntity.getQcType() == 1 && (oldEntity.getSpotCheckForms() == null || oldEntity.getSpotCheckForms().size() == 0)){
			SpotCheckForm form = new SpotCheckForm();
			form.setOfficer(oldEntity.getUser());
			form.setSupervisor(oldEntity.getOwner());
			form.setSpotCheckDate(oldEntity.getVisitDate());
			form.setScSvPlan(oldEntity);
			spotCheckFormDao.save(form);
		}
		else if (oldEntity.getQcType() == 2 && (oldEntity.getSupervisoryVisitForms() == null || oldEntity.getSupervisoryVisitForms().size() == 0)){
			SupervisoryVisitForm form = new SupervisoryVisitForm();
			form.setSupervisor(oldEntity.getOwner());
			form.setUser(oldEntity.getUser());
			form.setVisitDate(oldEntity.getVisitDate());
			form.setScSvPlan(oldEntity);
			supervisoryVisitFormDao.save(form);
		}
		//}

		scSvPlanDao.flush();
		
		return true;
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteScSvPlans(List<Integer> id) {
		List<ScSvPlan> scSvPlans = scSvPlanDao.getScSvPlanByIds(id);
		if (id.size() != scSvPlans.size()){
			return false;
		}
		
		for (ScSvPlan scSvPlan : scSvPlans){			
			for (SpotCheckForm form : scSvPlan.getSpotCheckForms()){
				spotCheckFormDao.delete(form);
			}
			for (SupervisoryVisitForm form : scSvPlan.getSupervisoryVisitForms()){
				supervisoryVisitFormDao.delete(form);
			}
			scSvPlanDao.delete(scSvPlan);
		}

		scSvPlanDao.flush();

		return true;
	}
	
	
	/**
	 * Convert entity to model
	 */
	public ScSvPlanEditModel convertEntityToModel(ScSvPlan entity) {
		
		ScSvPlanEditModel model = new ScSvPlanEditModel();
		BeanUtils.copyProperties(entity, model);

		User user = entity.getUser();
		
		if (user != null){
			model.setUserId(user.getId());
			model.setUserNameDisplay(user.getStaffCode() + " - " +user.getChineseName());
		}
		
		if (entity.getOwner() != null){
			model.setCheckerId(entity.getOwner().getId());
			model.setCheckerNameDisplay(entity.getOwner().getStaffCode() + " - " + entity.getOwner().getChineseName());
		}
		
		if (entity.getVisitDate() != null){
			model.setVisitDate(commonService.formatDate(entity.getVisitDate()));
			model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));	
			model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
		}
				
		return model;
	
	}
	
	public List<String> getSpotCheckDatesCurrentMonth(){		
		return spotCheckDateDao.getCurrentMonthSpotCheckDates();	
	}
	
	public List<String> getSpotCheckDateByUserIdFromSCSetup(Integer userId) {
		return spotCheckSetupDao.getSpotCheckDateByUserId(userId);
	}
	
	public List<String> getSpotCheckDateByUserIdFromSCForm(Integer userId) {
		return spotCheckFormDao.getSpotCheckDateByUserId(userId);
	}
	
	public List<String> getSupervisoryVisitDateByUserIdFromSVForm(Integer userId) {
		return supervisoryVisitFormDao.getSupervisoryVisitDateByUserId(userId);
	}
}
