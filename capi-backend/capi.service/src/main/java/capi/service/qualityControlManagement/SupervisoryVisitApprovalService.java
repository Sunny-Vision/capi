package capi.service.qualityControlManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.PurposeDao;
import capi.dal.RoleDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.UserDao;
import capi.entity.Role;
import capi.entity.SupervisoryVisitForm;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.qualityControlManagement.SupervisoryVisitApprovalTableList;
import capi.model.qualityControlManagement.SupervisoryVisitDetailTableList;
import capi.model.qualityControlManagement.SupervisoryVisitEditModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("SupervisoryVisitApprovalService")
public class SupervisoryVisitApprovalService extends BaseService {

	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitDao;
	
	@Autowired
	private UserDao UserDao;

	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private RoleDao roleDao;

	@Autowired
	private CommonService commonService;

	/**
	 * Get by ID
	 */
	public SupervisoryVisitForm getSupervisoryVisitById(int id) {
		return supervisoryVisitDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SupervisoryVisitApprovalTableList> getSupervisoryVisitApprovalList(DatatableRequestModel model
																				, boolean isBusinessData, List<Integer> actedUserIds, Integer userId, int authorityLevel){

		Order order = this.getOrder(model, "", "svf.visitDate", "fieldOfficer", "fieldOfficerName", "team", "supervisor", "checkerCode");
		
		String search = model.getSearch().get("value");
		
		User checker = UserDao.findById(userId);
		
		Role admin = roleDao.getRoleByCode("Admin");
		Role state = roleDao.getRoleByCode("Stat");
		Role scso = roleDao.getRoleByCode("SCSO");
		
		Boolean hasAuthorityViewAllRecord = (authorityLevel & admin.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & state.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & scso.getAuthorityLevel()) == authorityLevel ;
		
		List<SupervisoryVisitApprovalTableList> result = supervisoryVisitDao.selectAllSupervisoryVisitApproval(
															search, model.getStart(), model.getLength(), order, isBusinessData, actedUserIds, hasAuthorityViewAllRecord);
		
		DatatableResponseModel<SupervisoryVisitApprovalTableList> response = new DatatableResponseModel<SupervisoryVisitApprovalTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = supervisoryVisitDao.countSlectAllSupervisoryVisitApproval("", hasAuthorityViewAllRecord);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = supervisoryVisitDao.countSlectAllSupervisoryVisitApproval(search, hasAuthorityViewAllRecord);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Approve Supervisory Visit at edit.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean approveSupervisoryVisit(SupervisoryVisitEditModel model) throws Exception {

		SupervisoryVisitForm oldEntity = null;
		
		if (model.getSupervisoryVisitFormId() != null && model.getSupervisoryVisitFormId() > 0) {
			oldEntity = getSupervisoryVisitById(model.getSupervisoryVisitFormId());
			
			oldEntity.setStatus("Approved");
			oldEntity.setRejectReason(null);
			
			supervisoryVisitDao.save(oldEntity);
			supervisoryVisitDao.flush();
			
			return true;
		}
		return false;
	}

	/**
	 * Approve Supervisory Visit at home.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean approveSupervisoryVisit(ArrayList<Integer> ids) throws Exception {

		List<SupervisoryVisitForm> supervisoryVisitForms = supervisoryVisitDao.getSupervisoryVisitFormsByIds(ids);
		
		if (ids.size() != supervisoryVisitForms.size()){
			return false;
		}
		
		for (SupervisoryVisitForm supervisoryVisitForm : supervisoryVisitForms){
			supervisoryVisitForm.setStatus("Approved");
			supervisoryVisitForm.setRejectReason(null);
			supervisoryVisitDao.save(supervisoryVisitForm);
		}

		supervisoryVisitDao.flush();

		return true;
	}

	/**
	 * Reject Supervisory Visit at edit.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean rejectSupervisoryVisit(SupervisoryVisitEditModel model) throws Exception {

		SupervisoryVisitForm oldEntity = null;
		
		if (model.getSupervisoryVisitFormId() != null && model.getSupervisoryVisitFormId() > 0) {
			oldEntity = getSupervisoryVisitById(model.getSupervisoryVisitFormId());
			
			oldEntity.setStatus("Rejected");
			oldEntity.setRejectReason(model.getRejectReason());
			
			supervisoryVisitDao.save(oldEntity);
			supervisoryVisitDao.flush();
			
			return true;
		}
		return false;
	}

	/**
	 * Reject Spot Check at home.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean rejectSupervisoryVisit(ArrayList<Integer> ids, String rejectReason) throws Exception {

		List<SupervisoryVisitForm> supervisoryVisitForms = supervisoryVisitDao.getSupervisoryVisitFormsByIds(ids);
		
		if (ids.size() != supervisoryVisitForms.size()){
			return false;
		}
		
		for (SupervisoryVisitForm supervisoryVisitForm : supervisoryVisitForms){
			supervisoryVisitForm.setStatus("Rejected");
			supervisoryVisitForm.setRejectReason(rejectReason);
			supervisoryVisitDao.save(supervisoryVisitForm);
		}

		supervisoryVisitDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public SupervisoryVisitEditModel convertEntityToModel(SupervisoryVisitForm entity, Integer id, String fieldOfficer){

		SupervisoryVisitEditModel model = new SupervisoryVisitEditModel();
		BeanUtils.copyProperties(entity, model);

		model.setFieldOfficer(fieldOfficer);
		
		List<String> surveyList = purposeDao.getDistinctSurvey();
		model.setDetailSurveyList(surveyList);
		
		List<SupervisoryVisitDetailTableList> supervisoryVisitDetailList = supervisoryVisitDao.selectSupervisoryVisitDetailListBySupervisoryVisitFormId(id);
		model.setSupervisoryVisitDetailList(supervisoryVisitDetailList);
		
		if(entity.getVisitDate() != null) model.setVisitDate(commonService.formatDate(entity.getVisitDate()));
		if(entity.getFromTime() != null) model.setFromTime(commonService.formatTime(entity.getFromTime()));
		if(entity.getToTime() != null) model.setToTime(commonService.formatTime(entity.getToTime()));
		if(entity.getDiscussionDate() != null) model.setDiscussionDate(commonService.formatDate(entity.getDiscussionDate()));
		
		return model;
	}

}
