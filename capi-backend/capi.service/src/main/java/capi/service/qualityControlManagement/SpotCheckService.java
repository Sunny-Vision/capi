package capi.service.qualityControlManagement;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ItineraryPlanDao;
import capi.dal.PurposeDao;
import capi.dal.QCItineraryPlanDao;
import capi.dal.RoleDao;
import capi.dal.ScSvPlanDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckPhoneCallDao;
import capi.dal.SpotCheckResultDao;
import capi.dal.UserDao;
import capi.entity.Role;
import capi.entity.ScSvPlan;
import capi.entity.SpotCheckForm;
import capi.entity.SpotCheckPhoneCall;
import capi.entity.SpotCheckResult;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.api.dataSync.SpotCheckFormSyncData;
import capi.model.api.dataSync.SpotCheckPhoneCallSyncData;
import capi.model.api.dataSync.SpotCheckResultSyncData;
import capi.model.qualityControlManagement.SpotCheckEditModel;
import capi.model.qualityControlManagement.SpotCheckPhoneCallTableList;
import capi.model.qualityControlManagement.SpotCheckResultTableList;
import capi.model.qualityControlManagement.SpotCheckTableList;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.UserService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("SpotCheckService")
public class SpotCheckService extends BaseService {

	@Autowired
	private SpotCheckFormDao spotCheckDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private ItineraryPlanDao itineraryPlanDao;

	@Autowired
	private UserService userService;

	@Autowired
	private SpotCheckPhoneCallDao spotCheckPhoneCallDao;
	
	@Autowired
	private SpotCheckResultDao spotCheckResultDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ScSvPlanDao scSvPlanDao;
	
	@Autowired
	private QCItineraryPlanDao qcItineraryPlanDao;
	
	@Autowired
	private RoleDao roleDao;
	
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
	public DatatableResponseModel<SpotCheckTableList> getSpotCheckList(DatatableRequestModel model, boolean aboveSupervisor, 
			List<Integer> userId, Integer checkerId, int authorityLevel){

		Order order = null;
		
		if(aboveSupervisor) order = this.getOrder(model, "officerCode", "officerName", "supervisorCode", "scf.spotCheckDate", "checkerCode", "status");
		else order = this.getOrder(model, "officerCode", "officerName", "scf.spotCheckDate", "checkerCode", "status");
		
		String search = model.getSearch().get("value");
		
		Role admin = roleDao.getRoleByCode("Admin");
		Role state = roleDao.getRoleByCode("Stat");
		Role scso = roleDao.getRoleByCode("SCSO");
		
		Boolean hasAuthorityViewAllRecord = (authorityLevel & admin.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & state.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & scso.getAuthorityLevel()) == authorityLevel ;
		
		List<SpotCheckTableList> result = spotCheckDao.selectAllSpotCheck(search, model.getStart(), model.getLength(), order, aboveSupervisor, userId, hasAuthorityViewAllRecord);
		
		DatatableResponseModel<SpotCheckTableList> response = new DatatableResponseModel<SpotCheckTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = spotCheckDao.countSelectAllSpotCheck("", aboveSupervisor, userId, hasAuthorityViewAllRecord);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = spotCheckDao.countSelectAllSpotCheck(search, aboveSupervisor, userId, hasAuthorityViewAllRecord);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save Spot Check
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public void saveSpotCheck(SpotCheckEditModel model, String submitOrSave) throws Exception {

		SpotCheckForm oldEntity = null;
		
		if (model.getSpotCheckFormId() != null && model.getSpotCheckFormId() > 0) {
			oldEntity = getSpotCheckById(model.getSpotCheckFormId());
			
			oldEntity.setSession(model.getSession());
			oldEntity.setSurvey(model.getSurvey());
			oldEntity.setTimeCallback(model.getTimeCallback());
			oldEntity.setActivityBeingPerformed(model.getActivityBeingPerformed());
			if(model.getActivityBeingPerformed() != null && model.getActivityBeingPerformed() == 3) {
				oldEntity.setInterviewReferenceNo(model.getInterviewReferenceNo());
			} else {
				oldEntity.setInterviewReferenceNo(null);
			}
			oldEntity.setLocation(model.getLocation());
			oldEntity.setCaseReferenceNo(model.getCaseReferenceNo());
			oldEntity.setRemarksForNonContact(model.getRemarksForNonContact());
			oldEntity.setScheduledPlace(model.getScheduledPlace());
			if(!StringUtils.isEmpty(model.getScheduledTime())) {
				oldEntity.setScheduledTime(commonService.getTime(model.getScheduledTime()));
			}
			if (!StringUtils.isEmpty(model.getTurnUpTime())){
				oldEntity.setTurnUpTime(commonService.getTime(model.getTurnUpTime()));
			}
			oldEntity.setReasonable(model.getReasonable());
			oldEntity.setIrregular(model.getIrregular());
			oldEntity.setRemarkForTurnUpTime(model.getRemarkForTurnUpTime());
			if(!StringUtils.isEmpty(model.getVerCheck1RefenceNo())) {
				oldEntity.setVerCheck1ReferenceNo(model.getVerCheck1RefenceNo());
				oldEntity.setVerCheck1IsIrregular(model.getVerCheck1IsIrregular());
				oldEntity.setVerCheck1Remark(model.getVerCheck1Remark());
			} else {
				oldEntity.setVerCheck1ReferenceNo(null);
				oldEntity.setVerCheck1IsIrregular(null);
				oldEntity.setVerCheck1Remark(null);
			}
			
			if(!StringUtils.isEmpty(model.getVerCheck2RefenceNo())) {
				oldEntity.setVerCheck2ReferenceNo(model.getVerCheck2RefenceNo());
				oldEntity.setVerCheck2IsIrregular(model.getVerCheck2IsIrregular());
				oldEntity.setVerCheck2Remark(model.getVerCheck2Remark());
			} else {
				oldEntity.setVerCheck2ReferenceNo(null);
				oldEntity.setVerCheck2IsIrregular(null);
				oldEntity.setVerCheck2Remark(null);
			}
			
			if (StringUtils.isNotEmpty(model.getSubmitTo())){
				User submitTo = userService.getUserById(model.getSubmitToId());
			
				if(submitTo != null) {
					oldEntity.setSubmitTo(submitTo);
				}
			}
			oldEntity.setSuccessful(model.getSuccessful());
			if(model.getSuccessful() != null && model.getSuccessful() == false) {
				oldEntity.setUnsuccessfulRemark(model.getUnSuccessfulRemark());
			} else {
				oldEntity.setUnsuccessfulRemark(null);
			}
			
			if(model.getSpotCheckPhoneCallList() != null) {
				
				ArrayList<Integer> oldSpotCheckPhoneCallIds = new ArrayList<Integer>();
				for (SpotCheckPhoneCall spotCheckPhoneCall : oldEntity.getSpotCheckPhoneCalls()) {
					oldSpotCheckPhoneCallIds.add(spotCheckPhoneCall.getId());
				}
				
				ArrayList<Integer> currentSpotCheckPhoneCallIds = new ArrayList<Integer> ();
				for(SpotCheckPhoneCallTableList spotCheckPhoneCallTableList : model.getSpotCheckPhoneCallList()) {
					if(spotCheckPhoneCallTableList.getSpotCheckPhoneCallId() != null) {
						currentSpotCheckPhoneCallIds.add(spotCheckPhoneCallTableList.getSpotCheckPhoneCallId());
					}
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldSpotCheckPhoneCallIds, currentSpotCheckPhoneCallIds);
				
				if (deletedIds.size() > 0){
					List<SpotCheckPhoneCall> deletedSpotCheckPhoneCalls = spotCheckPhoneCallDao.getByIds(deletedIds.toArray(new Integer[0]));
					for (SpotCheckPhoneCall spotCheckPhoneCall: deletedSpotCheckPhoneCalls){
						oldEntity.getSpotCheckPhoneCalls().remove(spotCheckPhoneCall);
						spotCheckPhoneCallDao.delete(spotCheckPhoneCall);
					}
				}
				
				for(int i = 0; i < model.getSpotCheckPhoneCallList().size(); i++) {
					SpotCheckPhoneCall spotCheckPhoneCall = null;
					
					SpotCheckPhoneCallTableList spotCheckPhoneCallTableList = model.getSpotCheckPhoneCallList().get(i);
					
					if(spotCheckPhoneCallTableList.getSpotCheckPhoneCallId() != null && spotCheckPhoneCallTableList.getSpotCheckPhoneCallId() > 0) {
						spotCheckPhoneCall = getSpotCheckPhoneCallById(spotCheckPhoneCallTableList.getSpotCheckPhoneCallId());
					} else {
						spotCheckPhoneCall = new SpotCheckPhoneCall();
					}
					
					spotCheckPhoneCall.setResult(spotCheckPhoneCallTableList.getPhoneCallResult());
					spotCheckPhoneCall.setPhoneCallTime(commonService.getTime(spotCheckPhoneCallTableList.getPhoneCallTime()));
					spotCheckPhoneCall.setSpotCheckForm(oldEntity);
					
					spotCheckPhoneCallDao.save(spotCheckPhoneCall);
				}
				spotCheckPhoneCallDao.flush();
			}
			
			if(model.getSpotCheckResultList() != null) {
				
				ArrayList<Integer> oldSpotCheckResultIds = new ArrayList<Integer>();
				for (SpotCheckResult spotCheckResult : oldEntity.getSpotCheckResults()) {
					oldSpotCheckResultIds.add(spotCheckResult.getId());
				}
				
				ArrayList<Integer> currentSpotCheckResultIds = new ArrayList<Integer> ();
				for(SpotCheckResultTableList spotCheckResultTableList : model.getSpotCheckResultList()) {
					if(spotCheckResultTableList.getSpotCheckResultId() != null) {
						currentSpotCheckResultIds.add(spotCheckResultTableList.getSpotCheckResultId());
					}
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldSpotCheckResultIds, currentSpotCheckResultIds);
				
				if (deletedIds.size() > 0){
					List<SpotCheckResult> deletedSpotCheckResults = spotCheckResultDao.getByIds(deletedIds.toArray(new Integer[0]));
					for (SpotCheckResult spotCheckResult: deletedSpotCheckResults){
						oldEntity.getSpotCheckResults().remove(spotCheckResult);
						spotCheckResultDao.delete(spotCheckResult);
					}
				}
				
				for(int i = 0; i < model.getSpotCheckResultList().size(); i++) {
					SpotCheckResult spotCheckResult = null;
					
					SpotCheckResultTableList spotCheckResultTableList = model.getSpotCheckResultList().get(i);
					
					if(spotCheckResultTableList.getSpotCheckResultId() != null && spotCheckResultTableList.getSpotCheckResultId() > 0) {
						spotCheckResult = getSpotCheckResultById(spotCheckResultTableList.getSpotCheckResultId());
					} else {
						spotCheckResult = new SpotCheckResult();
					}
					
					spotCheckResult.setResult(spotCheckResultTableList.getResult());
					if("Others".equals(spotCheckResultTableList.getResult())) {
						spotCheckResult.setOtherRemark(spotCheckResultTableList.getOtherRemark());
					} else {
						spotCheckResult.setOtherRemark(null);
					}
					spotCheckResult.setReferenceNo(spotCheckResultTableList.getReferenceNo());
					spotCheckResult.setSpotCheckForm(oldEntity);
					
					spotCheckResultDao.save(spotCheckResult);
				}
				spotCheckResultDao.flush();
			}
			
			oldEntity.setRejectReason(null);
			
			if("submit".equals(submitOrSave)) {
				Date today = new Date();
				today = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);
				oldEntity.setSubmittedDate(today);
				oldEntity.setStatus("Submitted");
			} else {
				oldEntity.setStatus("Draft");
			}
			
			spotCheckDao.save(oldEntity);
			spotCheckDao.flush();
		}
		
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
			if(!referenceNoList.contains(entity.getInterviewReferenceNo()))
				referenceNoList.add(entity.getInterviewReferenceNo());
		}
		if(entity.getCaseReferenceNo() != null) {
			model.setCaseReferenceNo(entity.getCaseReferenceNo());
			if(!referenceNoList.contains(entity.getCaseReferenceNo()))
				referenceNoList.add(entity.getCaseReferenceNo());
		}
		if(entity.getVerCheck1ReferenceNo() != null) {
			model.setVerCheck1RefenceNo(entity.getVerCheck1ReferenceNo());
			if(!referenceNoList.contains(entity.getVerCheck1ReferenceNo()))
				referenceNoList.add(entity.getVerCheck1ReferenceNo());
		}
		if(entity.getVerCheck2ReferenceNo() != null) {
			model.setVerCheck2RefenceNo(entity.getVerCheck2ReferenceNo());
			if(!referenceNoList.contains(entity.getVerCheck2ReferenceNo()))
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

	public List<String> getSpotCheckDatesByRefMonth(String refMonthStr) {
		Date refMonth = null;
		try {
			refMonth = commonService.getMonth(refMonthStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return spotCheckDao.getSpotCheckDatesByRefMonth(refMonth);
	}

	/**
	 * Data Sync
	 */
	@Transactional
	public List<SpotCheckFormSyncData> syncSpotCheckFormData(List<SpotCheckFormSyncData> spotCheckForms
			, Date lastSyncTime, Boolean dataReturn, Integer[] spotCheckFormIds, Integer[] qcItineraryPlanIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> allSpotCheckFormIds = new ArrayList<Integer>();
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
			allSpotCheckFormIds.addAll(Arrays.asList(spotCheckFormIds));
		}
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if(spotCheckForms!=null && spotCheckForms.size()>0){
			for(SpotCheckFormSyncData spotCheckForm : spotCheckForms){
				if ("D".equals(spotCheckForm.getLocalDbRecordStatus())){
					continue;
				}
				SpotCheckForm entity = null;

				if(spotCheckForm.getSpotCheckFormId()==null){
					// spot check form id should never be null
					throw new RuntimeException("Spot Check Form Id should not be empty");
				} else {
					entity = spotCheckDao.findById(spotCheckForm.getSpotCheckFormId());
					if("Submitted".equals(entity.getStatus()) || "Approved".equals(entity.getStatus())){
						unUpdateIds.add(entity.getSpotCheckFormId());
						table.put(entity.getSpotCheckFormId(), spotCheckForm.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(spotCheckForm.getModifiedDate())){
						unUpdateIds.add(entity.getSpotCheckFormId());
						table.put(entity.getSpotCheckFormId(), spotCheckForm.getLocalId());
						continue;
					}
					
				}
				
				BeanUtils.copyProperties(spotCheckForm, entity);
				
				Date turnUpTime = null;
				if(!StringUtils.isEmpty(spotCheckForm.getTurnUpTime())){
					try {
						turnUpTime = commonService.getTime(spotCheckForm.getTurnUpTime());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				
				}
				entity.setTurnUpTime(turnUpTime);
				
				Date scheduledTime = null;
				if(!StringUtils.isEmpty(spotCheckForm.getScheduledTime())){
					try {
						scheduledTime = commonService.getTime(spotCheckForm.getScheduledTime());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					
				}
				entity.setScheduledTime(scheduledTime);
				
				if(spotCheckForm.getOfficerId()!=null){
					User officer = userDao.findById(spotCheckForm.getOfficerId());
					if (officer != null){
						entity.setOfficer(officer);						
					}

				}
				
				if(spotCheckForm.getSupervisorId()!=null){
					User supervisor = userDao.findById(spotCheckForm.getSupervisorId());
					if (supervisor != null){
						entity.setSupervisor(supervisor);
					}

				}
				
				if(spotCheckForm.getSubmitTo()!=null){
					User submitTo = userDao.findById(spotCheckForm.getSubmitTo());
					if (submitTo != null){
						entity.setSubmitTo(submitTo);						
					}

				}
				
				if(spotCheckForm.getScSvPlanId()!=null){
					ScSvPlan scSvPlan = scSvPlanDao.findById(spotCheckForm.getScSvPlanId());
					if (scSvPlan != null){
						entity.setScSvPlan(scSvPlan);						
					}

				}
				entity.setByPassModifiedDate(true);
				spotCheckDao.save(entity);
				allSpotCheckFormIds.add(entity.getSpotCheckFormId());
				table.put(entity.getSpotCheckFormId(), spotCheckForm.getLocalId());
				
			}
			spotCheckDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			List<SpotCheckFormSyncData> updatedData = new ArrayList<SpotCheckFormSyncData>();
			if(allSpotCheckFormIds!=null && allSpotCheckFormIds.size()>0){
				updatedData.addAll(syncSpotCheckFormRecursiveQuery(allSpotCheckFormIds, lastSyncTime));
			}
			
			if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
				updatedData.addAll(qcItineraryPlanDao.getUpdatedSpotCheckForm(lastSyncTime, null, qcItineraryPlanIds));
			}
			
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncSpotCheckFormRecursiveQuery(unUpdateIds, null));
			}
			
			List<SpotCheckFormSyncData> unique = new ArrayList<SpotCheckFormSyncData>(new HashSet<SpotCheckFormSyncData>(updatedData));
			
			for(SpotCheckFormSyncData data : unique){
				if(table.containsKey(data.getSpotCheckFormId())){
					data.setLocalId(table.get(data.getSpotCheckFormId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<SpotCheckFormSyncData>();
	}
	
	@Transactional
	public List<SpotCheckPhoneCallSyncData> syncSpotCheckPhoneCallData(List<SpotCheckPhoneCallSyncData> spotCheckPhoneCalls
			, Date lastSyncTime, Boolean dataReturn, Integer[] spotCheckFormIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();
		if(spotCheckPhoneCalls!=null && spotCheckPhoneCalls.size()>0){
			for(SpotCheckPhoneCallSyncData spotCheckPhoneCall : spotCheckPhoneCalls){
				SpotCheckPhoneCall entity = null;
				if(spotCheckPhoneCall.getSpotCheckPhoneCallId()==null){
					if ("D".equals(spotCheckPhoneCall.getLocalDbRecordStatus())){
						continue;
					}
					entity = new SpotCheckPhoneCall();
				} else {
					entity = spotCheckPhoneCallDao.findById(spotCheckPhoneCall.getSpotCheckPhoneCallId());
					if("Submitted".equals(entity.getSpotCheckForm().getStatus()) || "Approved".equals(entity.getSpotCheckForm().getStatus())){
						updateIds.add(entity.getSpotCheckPhoneCallId());
						table.put(entity.getSpotCheckPhoneCallId(), spotCheckPhoneCall.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(spotCheckPhoneCall.getModifiedDate())){
						updateIds.add(entity.getSpotCheckPhoneCallId());
						table.put(entity.getSpotCheckPhoneCallId(), spotCheckPhoneCall.getLocalId());
						continue;
					}
					if (entity != null && "D".equals(spotCheckPhoneCall.getLocalDbRecordStatus())){
						spotCheckPhoneCallDao.delete(entity);
						continue;
					}
					else if ("D".equals(spotCheckPhoneCall.getLocalDbRecordStatus())){
						continue;
					}
					
				}
				
				BeanUtils.copyProperties(spotCheckPhoneCall, entity);
				
				Date phoneCallTime = null;
				if(!StringUtils.isEmpty(spotCheckPhoneCall.getPhoneCallTime())){
					try {
						phoneCallTime = commonService.getTime(spotCheckPhoneCall.getPhoneCallTime());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				entity.setPhoneCallTime(phoneCallTime);
				

				if(spotCheckPhoneCall.getSpotCheckFormId()!=null){

					SpotCheckForm spotCheckForm = spotCheckDao.findById(spotCheckPhoneCall.getSpotCheckFormId());
					if (spotCheckForm != null){
						entity.setSpotCheckForm(spotCheckForm);						
					}
				}

				entity.setByPassModifiedDate(true);
				spotCheckPhoneCallDao.save(entity);
				updateIds.add(entity.getSpotCheckPhoneCallId());
				table.put(entity.getSpotCheckPhoneCallId(), spotCheckPhoneCall.getLocalId());
			}
			spotCheckPhoneCallDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			List<SpotCheckPhoneCallSyncData> updatedData = new ArrayList<SpotCheckPhoneCallSyncData>();
			
			if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
				updatedData.addAll(syncPhoneCallBySpotCheckFormRecursiveQuery(Arrays.asList(spotCheckFormIds), lastSyncTime));
			}
			if(updateIds!=null && updateIds.size()>0){
				updatedData.addAll(syncPhoneCallByIdRecursiveQuery(updateIds));
			}
			
			//syncPhoneCallByIdRecursiveQuery
			List<SpotCheckPhoneCallSyncData> unique = new ArrayList<SpotCheckPhoneCallSyncData>(new HashSet<SpotCheckPhoneCallSyncData>(updatedData));
			for(SpotCheckPhoneCallSyncData data : unique){
				if(table.containsKey(data.getSpotCheckPhoneCallId())){
					data.setLocalId(table.get(data.getSpotCheckPhoneCallId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<SpotCheckPhoneCallSyncData>();
	}
	
	@Transactional
	public List<SpotCheckResultSyncData> syncSpotCheckResultData(List<SpotCheckResultSyncData> spotCheckResults
			, Date lastSyncTime, Boolean dataReturn, Integer[] spotCheckFormIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();
		if(spotCheckResults!=null && spotCheckResults.size()>0){
			for(SpotCheckResultSyncData spotCheckResult : spotCheckResults){
				SpotCheckResult entity = null;
				if(spotCheckResult.getSpotCheckResultId()==null){
					if ("D".equals(spotCheckResult.getLocalDbRecordStatus())){
						continue;
					}
					entity = new SpotCheckResult();
				} else {
					entity = spotCheckResultDao.findById(spotCheckResult.getSpotCheckResultId());
					if ("Submitted".equals(entity.getSpotCheckForm().getStatus()) || "Approved".equals(entity.getSpotCheckForm().getStatus())){
						updateIds.add(entity.getSpotCheckResultId());
						table.put(entity.getSpotCheckResultId(), spotCheckResult.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(spotCheckResult.getModifiedDate())){
						updateIds.add(entity.getSpotCheckResultId());
						table.put(entity.getSpotCheckResultId(), spotCheckResult.getLocalId());
						continue;
					}
					if (entity != null && "D".equals(spotCheckResult.getLocalDbRecordStatus())){
						spotCheckResultDao.delete(entity);
						continue;
					}
					else if ("D".equals(spotCheckResult.getLocalDbRecordStatus())){
						continue;
					}
					
				}
				
				BeanUtils.copyProperties(spotCheckResult, entity);
				

				if(spotCheckResult.getSpotCheckFormId()!=null){

					SpotCheckForm spotCheckForm = spotCheckDao.findById(spotCheckResult.getSpotCheckFormId());
					if (spotCheckForm != null){
						entity.setSpotCheckForm(spotCheckForm);						
					}
				}

				entity.setByPassModifiedDate(true);
				spotCheckResultDao.save(entity);
				updateIds.add(entity.getSpotCheckResultId());
				table.put(entity.getSpotCheckResultId(), spotCheckResult.getLocalId());
			}
			spotCheckResultDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			List<SpotCheckResultSyncData> updatedData = new ArrayList<SpotCheckResultSyncData>();
			
			if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
				updatedData.addAll(syncResultBySpotCheckFormRecursiveQuery(Arrays.asList(spotCheckFormIds), lastSyncTime));
			}
			
			if(updateIds!=null && updateIds.size()>0){
				updatedData.addAll(syncResultByIdRecursiveQuery(updateIds));
			}
			
			List<SpotCheckResultSyncData> unique = new ArrayList<SpotCheckResultSyncData>(new HashSet<SpotCheckResultSyncData>(updatedData));
			
			for(SpotCheckResultSyncData data : unique){
				if(table.containsKey(data.getSpotCheckResultId())){
					data.setLocalId(table.get(data.getSpotCheckResultId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<SpotCheckResultSyncData>();
	}
	
	public Long getSpotCheckCount(boolean isFieldTeamHead) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails capiWebAuthenticationDetails = (CapiWebAuthenticationDetails) auth.getDetails();
		int authorityLevel = capiWebAuthenticationDetails.getAuthorityLevel();
		
		boolean aboveSupervisor = false;
		if( ((authorityLevel & 1) == 1) || ((authorityLevel & 2) == 2) ) aboveSupervisor = true;
		
		List<Integer> actedUsers = capiWebAuthenticationDetails.getActedUsers();
		actedUsers.add(capiWebAuthenticationDetails.getUserId());
		
		//Outstanding Spot check should count from now to 3 months ago only
		Date toDate = commonService.getDateWithoutTime(new Date());
		Date fromDate = DateUtils.truncate(DateUtils.addMonths(toDate, -3), Calendar.DATE);
		
		return spotCheckDao.countSelectOutstandingSpotCheck("", aboveSupervisor, actedUsers, isFieldTeamHead, fromDate, toDate);
	}
	
	public List<SpotCheckFormSyncData> syncSpotCheckFormRecursiveQuery(List<Integer> spotCheckFormIds, Date lastSyncTime){
		List<SpotCheckFormSyncData> entities = new ArrayList<SpotCheckFormSyncData>();
		if(spotCheckFormIds.size()>2000){
			List<Integer> ids = spotCheckFormIds.subList(0, 2000);
			entities.addAll(syncSpotCheckFormRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = spotCheckFormIds.subList(2000, spotCheckFormIds.size());
			entities.addAll(syncSpotCheckFormRecursiveQuery(remainIds, lastSyncTime));
		} else if(spotCheckFormIds.size()>0){
			return qcItineraryPlanDao.getUpdatedSpotCheckForm(lastSyncTime, spotCheckFormIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public List<SpotCheckPhoneCallSyncData> syncPhoneCallBySpotCheckFormRecursiveQuery(List<Integer> spotCheckFormIds, Date lastSyncTime){
		List<SpotCheckPhoneCallSyncData> entities = new ArrayList<SpotCheckPhoneCallSyncData>();
		if(spotCheckFormIds.size()>2000){
			List<Integer> ids = spotCheckFormIds.subList(0, 2000);
			entities.addAll(syncPhoneCallBySpotCheckFormRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = spotCheckFormIds.subList(2000, spotCheckFormIds.size());
			entities.addAll(syncPhoneCallBySpotCheckFormRecursiveQuery(remainIds, lastSyncTime));
		} else if(spotCheckFormIds.size()>0){
			return spotCheckPhoneCallDao.getUpdatedSpotCheckPhoneCall(lastSyncTime, spotCheckFormIds.toArray(new Integer[0]), null);
		}
		return entities;
	}
	
	public List<SpotCheckPhoneCallSyncData> syncPhoneCallByIdRecursiveQuery(List<Integer> spotCheckPhoneCallIds){
		List<SpotCheckPhoneCallSyncData> entities = new ArrayList<SpotCheckPhoneCallSyncData>();
		if(spotCheckPhoneCallIds.size()>2000){
			List<Integer> ids = spotCheckPhoneCallIds.subList(0, 2000);
			entities.addAll(syncPhoneCallByIdRecursiveQuery(ids));
			
			List<Integer> remainIds = spotCheckPhoneCallIds.subList(2000, spotCheckPhoneCallIds.size());
			entities.addAll(syncPhoneCallByIdRecursiveQuery(remainIds));
		} else if(spotCheckPhoneCallIds.size()>0){
			return spotCheckPhoneCallDao.getUpdatedSpotCheckPhoneCall(null, null, spotCheckPhoneCallIds.toArray(new Integer[0]));
		}
		return entities;
	}
	
	public List<SpotCheckResultSyncData> syncResultBySpotCheckFormRecursiveQuery(List<Integer> spotCheckFormIds, Date lastSyncTime){
		List<SpotCheckResultSyncData> entities = new ArrayList<SpotCheckResultSyncData>();
		if(spotCheckFormIds.size()>2000){
			List<Integer> ids = spotCheckFormIds.subList(0, 2000);
			entities.addAll(syncResultBySpotCheckFormRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = spotCheckFormIds.subList(2000, spotCheckFormIds.size());
			entities.addAll(syncResultBySpotCheckFormRecursiveQuery(remainIds, lastSyncTime));
		} else if (spotCheckFormIds.size()>0){
			return spotCheckResultDao.getUpdatedSpotCheckResult(lastSyncTime, spotCheckFormIds.toArray(new Integer[0]), null);
		}
		return entities;
	}
	
	public List<SpotCheckResultSyncData> syncResultByIdRecursiveQuery(List<Integer> spotCheckResultIds){
		List<SpotCheckResultSyncData> entities = new ArrayList<SpotCheckResultSyncData>();
		if(spotCheckResultIds.size()>2000){
			List<Integer> ids = spotCheckResultIds.subList(0, 2000);
			entities.addAll(syncResultByIdRecursiveQuery(ids));
			
			List<Integer> remainIds = spotCheckResultIds.subList(2000, spotCheckResultIds.size());
			entities.addAll(syncResultByIdRecursiveQuery(remainIds));
		} else if (spotCheckResultIds.size()>0){
			return spotCheckResultDao.getUpdatedSpotCheckResult(null, null, spotCheckResultIds.toArray(new Integer[0]));
		}
		return entities;
	}
}
