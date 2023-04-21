package capi.service.qualityControlManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ItineraryPlanDao;
import capi.dal.PurposeDao;
import capi.dal.RoleDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckPhoneCallDao;
import capi.dal.SpotCheckResultDao;
import capi.dal.UserDao;
import capi.entity.Role;
import capi.entity.SpotCheckForm;
import capi.entity.SpotCheckPhoneCall;
import capi.entity.SpotCheckResult;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.qualityControlManagement.SpotCheckApprovalTableList;
import capi.model.qualityControlManagement.SpotCheckEditModel;
import capi.model.qualityControlManagement.SpotCheckPhoneCallTableList;
import capi.model.qualityControlManagement.SpotCheckResultTableList;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.UserService;

@Service("SpotCheckApprovalService")
public class SpotCheckApprovalService extends BaseService {

	@Autowired
	private SpotCheckFormDao spotCheckDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private ItineraryPlanDao itineraryPlanDao;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private UserService userService;

	@Autowired
	private SpotCheckPhoneCallDao spotCheckPhoneCallDao;
	
	@Autowired
	private SpotCheckResultDao spotCheckResultDao;

	@Autowired
	private CommonService commonService;

	/**
	 * Get by ID
	 */
	public SpotCheckForm getSpotCheckById(int id) {
		return spotCheckDao.findById(id);
	}

	/**
	 * Get Spot Check Phone Call by ID
	 */
	public SpotCheckPhoneCall getSpotCheckPhoneCallById(int id) {
		return spotCheckPhoneCallDao.findById(id);
	}

	/**
	 * Get Spot Check Result by ID
	 */
	public SpotCheckResult getSpotCheckResultById(int id) {
		return spotCheckResultDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SpotCheckApprovalTableList> getSpotCheckApprovalList(DatatableRequestModel model
																, boolean isBusinessData, List<Integer> actedUserIds, Integer userId, int authorityLevel){

		Order order = this.getOrder(model, "", "officerCode", "officerName", "officerTeam", "survey", "supervisorCode", "checkerCode", "scf.spotCheckDate");
		
		String search = model.getSearch().get("value");
		
		Role admin = roleDao.getRoleByCode("Admin");
		Role state = roleDao.getRoleByCode("Stat");
		Role scso = roleDao.getRoleByCode("SCSO");
		
		Boolean hasAuthorityViewAllRecord = (authorityLevel & admin.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & state.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & scso.getAuthorityLevel()) == authorityLevel ;
		
		List<SpotCheckApprovalTableList> result = spotCheckDao.selectAllSpotCheckApproval(
													search, model.getStart(), model.getLength(), order, isBusinessData, actedUserIds, hasAuthorityViewAllRecord);
		
		DatatableResponseModel<SpotCheckApprovalTableList> response = new DatatableResponseModel<SpotCheckApprovalTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = spotCheckDao.countSelectAllSpotCheckApproval("", hasAuthorityViewAllRecord);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = spotCheckDao.countSelectAllSpotCheckApproval(search, hasAuthorityViewAllRecord);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Approve Spot Check at edit.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean approveSpotCheck(SpotCheckEditModel model) throws Exception {

		SpotCheckForm oldEntity = null;
		
		if (model.getSpotCheckFormId() != null && model.getSpotCheckFormId() > 0) {
			oldEntity = getSpotCheckById(model.getSpotCheckFormId());
			
			oldEntity.setStatus("Approved");
			oldEntity.setRejectReason(null);
			
			spotCheckDao.save(oldEntity);
			spotCheckDao.flush();
			
			return true;
		}
		return false;
	}

	/**
	 * Approve Spot Check at home.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean approveSpotCheck(ArrayList<Integer> ids) throws Exception {

		List<SpotCheckForm> spotCheckForms = spotCheckDao.getSpotCheckFormsByIds(ids);
		
		if (ids.size() != spotCheckForms.size()){
			return false;
		}
		
		for (SpotCheckForm spotCheckForm : spotCheckForms){
			spotCheckForm.setStatus("Approved");
			spotCheckForm.setRejectReason(null);
			spotCheckDao.save(spotCheckForm);
		}

		spotCheckDao.flush();

		return true;
	}

	/**
	 * Reject Spot Check at edit.jsp
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean rejectSpotCheck(SpotCheckEditModel model) throws Exception {

		SpotCheckForm oldEntity = null;
		
		if (model.getSpotCheckFormId() != null && model.getSpotCheckFormId() > 0) {
			oldEntity = getSpotCheckById(model.getSpotCheckFormId());
			
			oldEntity.setStatus("Rejected");
			oldEntity.setRejectReason(model.getRejectReason());
			
			spotCheckDao.save(oldEntity);
			spotCheckDao.flush();
			
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
	public boolean rejectSpotCheck(ArrayList<Integer> ids, String rejectReason) throws Exception {

		List<SpotCheckForm> spotCheckForms = spotCheckDao.getSpotCheckFormsByIds(ids);
		
		if (ids.size() != spotCheckForms.size()){
			return false;
		}
		
		for (SpotCheckForm spotCheckForm : spotCheckForms){
			spotCheckForm.setStatus("Rejected");
			spotCheckForm.setRejectReason(rejectReason);
			spotCheckDao.save(spotCheckForm);
		}

		spotCheckDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public SpotCheckEditModel convertEntityToModel(SpotCheckForm entity, Integer id, Integer officerId, String officerCode, String spotCheckDate){

		SpotCheckEditModel model = new SpotCheckEditModel();
		BeanUtils.copyProperties(entity, model);

		model.setOfficerCode(officerCode);
		model.setSpotCheckDate(spotCheckDate);
		
		model.setReasonable(entity.isReasonable());
		model.setIrregular(entity.isIrregular());
		model.setVerCheck1IsIrregular(entity.isVerCheck1IsIrregular());
		model.setVerCheck2IsIrregular(entity.isVerCheck2IsIrregular());
		model.setSuccessful(entity.isSuccessful());
		
		List<String> surveyList = purposeDao.getDistinctSurvey();
		model.setSurveyList(surveyList);
		
		List<SpotCheckPhoneCallTableList> spotCheckPhoneCallListFromDB = spotCheckDao.selectSpotCheckPhoneCallListBySpotCheckId(id);
		List<SpotCheckPhoneCallTableList> spotCheckPhoneCallList = new ArrayList<SpotCheckPhoneCallTableList>();
		for(int i = 0; i < spotCheckPhoneCallListFromDB.size(); i++) {
			SpotCheckPhoneCallTableList spotCheckPhoneCall = new SpotCheckPhoneCallTableList();
			spotCheckPhoneCall.setPhoneCallTime(commonService.formatTime(spotCheckPhoneCallListFromDB.get(i).getPhoneCallTimeDate()));
			spotCheckPhoneCall.setPhoneCallResult(spotCheckPhoneCallListFromDB.get(i).getPhoneCallResult());
			spotCheckPhoneCall.setSpotCheckPhoneCallId(spotCheckPhoneCallListFromDB.get(i).getSpotCheckPhoneCallId());
			spotCheckPhoneCallList.add(spotCheckPhoneCall);
		}
		model.setSpotCheckPhoneCallList(spotCheckPhoneCallList);
		
		List<String> referenceNoList = itineraryPlanDao.getReferenceList(officerId);
		if(entity.getInterviewReferenceNo() != null) {
			referenceNoList.add(entity.getInterviewReferenceNo());
		}
		if(entity.getCaseReferenceNo() != null) {
			model.setCaseReferenceNo(entity.getCaseReferenceNo());
			referenceNoList.add(entity.getCaseReferenceNo());
		}
		if(entity.getVerCheck1ReferenceNo() != null) {
			model.setVerCheck1RefenceNo(entity.getVerCheck1ReferenceNo());
			referenceNoList.add(entity.getVerCheck1ReferenceNo());
		}
		if(entity.getVerCheck2ReferenceNo() != null) {
			model.setVerCheck2RefenceNo(entity.getVerCheck2ReferenceNo());
			referenceNoList.add(entity.getVerCheck2ReferenceNo());
		}
		model.setReferenceNoList(referenceNoList);
		
		List<SpotCheckResultTableList> spotCheckResultList = spotCheckDao.selectSpotCheckResultListBySpotCheckId(id);
		model.setSpotCheckResultList(spotCheckResultList);
		
		if(entity.getScheduledTime() != null) {
			model.setScheduledTime(commonService.formatTime(entity.getScheduledTime()));
		}
		if(entity.getTurnUpTime() != null) {
			model.setTurnUpTime(commonService.formatTime(entity.getTurnUpTime()));
		}
		
		if(entity.getSubmitTo() != null) {
			model.setSubmitToId(entity.getSubmitTo().getId());
			model.setSubmitTo(entity.getSubmitTo().getStaffCode() + " - " + entity.getSubmitTo().getChineseName());
		}
		
		model.setUnSuccessfulRemark(entity.getUnsuccessfulRemark());

		return model;
	}

}
