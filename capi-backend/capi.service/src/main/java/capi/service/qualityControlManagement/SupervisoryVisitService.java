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
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AssignmentDao;
import capi.dal.PurposeDao;
import capi.dal.QCItineraryPlanDao;
import capi.dal.RoleDao;
import capi.dal.ScSvPlanDao;
import capi.dal.SupervisoryVisitDetailDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.Role;
import capi.entity.ScSvPlan;
import capi.entity.SupervisoryVisitDetail;
import capi.entity.SupervisoryVisitForm;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.SupervisoryVisitDetailSyncData;
import capi.model.api.dataSync.SupervisoryVisitFormSyncData;
import capi.model.qualityControlManagement.SupervisoryVisitDetailTableList;
import capi.model.qualityControlManagement.SupervisoryVisitEditModel;
import capi.model.qualityControlManagement.SupervisoryVisitTableList;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.UserService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("SupervisoryVisitService")
public class SupervisoryVisitService extends BaseService {

	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private SurveyMonthDao surveyMonthDao;

	@Autowired
	private SupervisoryVisitDetailDao supervisoryVisitDetailDao;
	
	@Autowired
	private AssignmentDao assignmentDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ScSvPlanDao scSvPlanDao;
	
	@Autowired
	private QCItineraryPlanDao qcItineraryPlanDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private MessageSource messageSource;

	/**
	 * Get by ID
	 */
	public SupervisoryVisitForm getSupervisoryVisitById(int id) {
		return supervisoryVisitDao.findById(id);
	}

	/**
	 * Get Supervisory Visit Detail by ID
	 */
	public SupervisoryVisitDetail getSupervisoryVisitDetailById(int id) {
		return supervisoryVisitDetailDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SupervisoryVisitTableList> getSupervisoryVisitList(DatatableRequestModel model
																, boolean aboveSupervisor, List<Integer> actedUserIds, Integer userId, int authorityLevel){

		Order order = this.getOrder(model, "svf.visitDate", "fieldOfficer", "fieldOfficerName", "checkerCode", "status");
		
		String search = model.getSearch().get("value");
		
		Role admin = roleDao.getRoleByCode("Admin");
		Role state = roleDao.getRoleByCode("Stat");
		Role scso = roleDao.getRoleByCode("SCSO");
		
		Boolean hasAuthorityViewAllRecord = (authorityLevel & admin.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & state.getAuthorityLevel()) == authorityLevel || 
											(authorityLevel & scso.getAuthorityLevel()) == authorityLevel ;
		
		List<SupervisoryVisitTableList> result = supervisoryVisitDao.selectAllSupervisoryVisit(search, model.getStart(), model.getLength(), order
													, aboveSupervisor, actedUserIds, hasAuthorityViewAllRecord);
		
		DatatableResponseModel<SupervisoryVisitTableList> response = new DatatableResponseModel<SupervisoryVisitTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = supervisoryVisitDao.countSelectAllSupervisoryVisit("", aboveSupervisor, actedUserIds, hasAuthorityViewAllRecord);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = supervisoryVisitDao.countSelectAllSupervisoryVisit(search, aboveSupervisor, actedUserIds, hasAuthorityViewAllRecord);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save Supervisory Visit
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public void saveSupervisoryVisit(SupervisoryVisitEditModel model, String submitOrSave) throws Exception {

		SupervisoryVisitForm oldEntity = null;
		
		if (model.getSupervisoryVisitFormId() != null && model.getSupervisoryVisitFormId() > 0) {
			oldEntity = getSupervisoryVisitById(model.getSupervisoryVisitFormId());
			
			oldEntity.setSession(model.getSession());
			
			oldEntity.setVisitDate(commonService.getDate(model.getVisitDate()));
			oldEntity.setFromTime(commonService.getTime(model.getFromTime()));
			oldEntity.setToTime(commonService.getTime(model.getToTime()));
			
			oldEntity.setDiscussionDate(commonService.getDate(model.getDiscussionDate()));
			oldEntity.setRemark(model.getRemark());
			
			oldEntity.setKnowledgeOfWorkResult(model.getKnowledgeOfWorkResult());
			if(model.getKnowledgeOfWorkResult() == 1) oldEntity.setKnowledgeOfWorkRemark(model.getKnowledgeOfWorkRemark());
			else oldEntity.setKnowledgeOfWorkRemark(null);
			
			oldEntity.setInterviewTechniqueResult(model.getInterviewTechniqueResult());
			if(model.getInterviewTechniqueResult() == 1) oldEntity.setInterviewTechniqueRemark(model.getInterviewTechniqueRemark());
			else oldEntity.setInterviewTechniqueRemark(null);
			
			oldEntity.setHandleDifficultInterviewResult(model.getHandleDifficultInterviewResult());
			if(model.getHandleDifficultInterviewResult() == 1) oldEntity.setHandleDifficultInterviewRemark(model.getHandleDifficultInterviewRemark());
			else oldEntity.setHandleDifficultInterviewRemark(null);
			
			oldEntity.setDataRecordingResult(model.getDataRecordingResult());
			if(model.getDataRecordingResult() == 1) oldEntity.setDataRecordingRemark(model.getDataRecordingRemark());
			else oldEntity.setDataRecordingRemark(null);
			
			oldEntity.setLocalGeographyResult(model.getLocalGeographyResult());
			if(model.getLocalGeographyResult() == 1) oldEntity.setLocalGeographyRemark(model.getLocalGeographyRemark());
			else oldEntity.setLocalGeographyRemark(null);
			
			oldEntity.setMannerWithPublicResult(model.getMannerWithPublicResult());
			if(model.getMannerWithPublicResult() == 1) oldEntity.setMannerWithPublicRemark(model.getMannerWithPublicRemark());
			else oldEntity.setMannerWithPublicRemark(null);
			
			oldEntity.setJudgmentResult(model.getJudgmentResult());
			if(model.getJudgmentResult() == 1) oldEntity.setJudgmentRemark(model.getJudgmentRemark());
			else oldEntity.setJudgmentRemark(null);
			
			oldEntity.setOrganizationOfWorkResult(model.getOrganizationOfWorkResult());
			if(model.getOrganizationOfWorkResult() == 1) oldEntity.setOrganizationOfWorkRemark(model.getOrganizationOfWorkRemark());
			else oldEntity.setOrganizationOfWorkRemark(null);
			
			oldEntity.setOtherResult(model.getOtherResult());
			if(model.getOtherResult() == 1) oldEntity.setOtherRemark(model.getOtherRemark());
			else oldEntity.setOtherRemark(null);
			
			User submitTo = userService.getUserById(model.getSubmitToId());
			if(submitTo != null) {
				oldEntity.setSubmitTo(submitTo);
			}
			
			if(model.getSupervisoryVisitDetailList() != null) {
				
				ArrayList<Integer> oldSupervisoryVisitDetailIds = new ArrayList<Integer>();
				for (SupervisoryVisitDetail supervisoryVisitDetail : oldEntity.getDetails()) {
					oldSupervisoryVisitDetailIds.add(supervisoryVisitDetail.getId());
				}
				
				ArrayList<Integer> currentSupervisoryVisitDetailIds = new ArrayList<Integer> ();
				for(SupervisoryVisitDetailTableList supervisoryVisitDetailTableList : model.getSupervisoryVisitDetailList()) {
					if(supervisoryVisitDetailTableList.getSupervisoryVisitDetailId() != null) {
						currentSupervisoryVisitDetailIds.add(supervisoryVisitDetailTableList.getSupervisoryVisitDetailId());
					}
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldSupervisoryVisitDetailIds, currentSupervisoryVisitDetailIds);
				
				if (deletedIds.size() > 0){
					List<SupervisoryVisitDetail> deletedSupervisoryVisitDetails = supervisoryVisitDetailDao.getByIds(deletedIds.toArray(new Integer[0]));
					for (SupervisoryVisitDetail supervisoryVisitDetail: deletedSupervisoryVisitDetails){
						oldEntity.getDetails().remove(supervisoryVisitDetail);
						supervisoryVisitDetailDao.delete(supervisoryVisitDetail);
					}
				}
				
				for(int i = 0; i < model.getSupervisoryVisitDetailList().size(); i++) {
					SupervisoryVisitDetail supervisoryVisitDetail = null;
					
					SupervisoryVisitDetailTableList supervisoryVisitDetailTableList = model.getSupervisoryVisitDetailList().get(i);
					
					if(supervisoryVisitDetailTableList.getSupervisoryVisitDetailId() != null && supervisoryVisitDetailTableList.getSupervisoryVisitDetailId() > 0) {
						supervisoryVisitDetail = getSupervisoryVisitDetailById(supervisoryVisitDetailTableList.getSupervisoryVisitDetailId());
					} else {
						supervisoryVisitDetail = new SupervisoryVisitDetail();
					}
					
//					Assignment assignment = assignmentDao.findById(supervisoryVisitDetailTableList.getAssignmentId());
//					supervisoryVisitDetail.setAssignment(assignment);
					supervisoryVisitDetail.setReferenceNo(supervisoryVisitDetailTableList.getAssignmentId());
					
					supervisoryVisitDetail.setSurvey(supervisoryVisitDetailTableList.getSurvey());
					supervisoryVisitDetail.setResult(supervisoryVisitDetailTableList.getResult());
					if(supervisoryVisitDetailTableList.getResult() == 3) supervisoryVisitDetail.setOtherRemark(supervisoryVisitDetailTableList.getOtherRemark());
					else supervisoryVisitDetail.setOtherRemark(null);
					
					SupervisoryVisitForm supervisoryVisitForm = supervisoryVisitDao.findById(model.getSupervisoryVisitFormId());
					supervisoryVisitDetail.setSupervisoryVisitForm(supervisoryVisitForm);
					
					supervisoryVisitDetailDao.save(supervisoryVisitDetail);
				}
				supervisoryVisitDetailDao.flush();
			}
			
			oldEntity.setRejectReason(null);
			
			if("submit".equals(submitOrSave)) {
				oldEntity.setStatus("Submitted");
				String subject = messageSource.getMessage("N00056", null, Locale.ENGLISH);
				User user = oldEntity.getUser();
				User receiver = oldEntity.getSubmitTo();
				if (user != null){
					String code = user.getStaffCode();
					String name = user.getEnglishName();
					String content = messageSource.getMessage("N00057", new String[]{code,name}, Locale.ENGLISH);
					notifyService.sendNotification(receiver, subject, content, false);
				}
				
			} else {
				oldEntity.setStatus("Draft");
			}
			
			supervisoryVisitDao.save(oldEntity);
			supervisoryVisitDao.flush();
		}
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public SupervisoryVisitEditModel convertEntityToModel(SupervisoryVisitForm entity, Integer id, Integer fieldOfficerId, Integer supervisorId){

		SupervisoryVisitEditModel model = new SupervisoryVisitEditModel();
		BeanUtils.copyProperties(entity, model);

		if(entity.getVisitDate() != null) {
			SurveyMonth surveyMonth = surveyMonthDao.getSurveyMonthByDate(entity.getVisitDate());
			model.setSurveyMonthId(surveyMonth.getSurveyMonthId());
		}
		
		User fieldOfficer = null;
		if(fieldOfficerId != null) {
			fieldOfficer = userService.getUserById(fieldOfficerId);
			model.setFieldOfficerId(fieldOfficerId);
			model.setFieldOfficer(fieldOfficer.getChineseName() + " (" + fieldOfficer.getStaffCode() + ")");
			model.setFieldOfficerPost(fieldOfficer.getDestination());
		}
		
		User supervisor = null;
		if(supervisorId != null) {
			supervisor = userService.getUserById(supervisorId);
			model.setSupervisor(supervisor.getChineseName());
			model.setSupervisorPost(supervisor.getDestination());
		}
		
		User submitTo = null;
		if(entity.getSubmitTo() != null) {
			submitTo = userService.getUserById(entity.getSubmitTo().getUserId());
			model.setSubmitToId(submitTo.getUserId());
			model.setSubmitTo(submitTo.getStaffCode() + " - " + submitTo.getChineseName());
		}
		
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

	/**
	 * Get assignment (with survey month) select format
	 */
	public Select2ResponseModel queryAssignmentWithSurveyMonthSelect2(Select2RequestModel queryModel, Integer surveyMonthId, Integer fieldOfficerId) {
		queryModel.setRecordsPerPage(10);
		List<Assignment> entities = assignmentDao.searchAssignmentBySurveyMonthId(
				queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), surveyMonthId, fieldOfficerId);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = assignmentDao.countSearchAssignmentBySurveyMonthId(queryModel.getTerm(), surveyMonthId, fieldOfficerId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Assignment a : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
//			item.setId(String.valueOf(a.getId()));
			item.setId(a.getReferenceNo());
			item.setText(a.getReferenceNo());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get assignment (with survey month) from lookup
	 */
	public List<SupervisoryVisitEditModel.AssignmentWithSurveyMonth> getAssignmentWithSurveyMonthById(Integer assignmentId){
		return assignmentDao.getAssignmentWithSurveyMonthById(assignmentId);	
	}

	public List<String> getVisitDatesByRefMonth(String refMonthStr) {
		Date refMonth = null;
		try {
			refMonth = commonService.getMonth(refMonthStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return supervisoryVisitDao.getVisitDatesByRefMonth(refMonth);
	}

	/**
	 * Data Sync
	 */
	@Transactional
	public List<SupervisoryVisitFormSyncData> syncSupervisoryVisitFormData(List<SupervisoryVisitFormSyncData> supervisoryVisitForms
			, Date lastSyncTime, Boolean dataReturn, Integer[] supervisoryVisitFormIds, Integer[] qcItineraryPlanIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> allSupervisoryVisitFormIds = new ArrayList<Integer>();
		if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.length>0){
			allSupervisoryVisitFormIds.addAll(Arrays.asList(supervisoryVisitFormIds));
		}
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if(supervisoryVisitForms!=null && supervisoryVisitForms.size()>0){
			for(SupervisoryVisitFormSyncData supervisoryVisitForm : supervisoryVisitForms){
				if ("D".equals(supervisoryVisitForm.getLocalDbRecordStatus())){
					continue;
				}
				SupervisoryVisitForm entity = null;
				if(supervisoryVisitForm.getSupervisoryVisitFormId()==null){
					continue;
				} else {
					entity = supervisoryVisitDao.findById(supervisoryVisitForm.getSupervisoryVisitFormId());
					if("Submitted".equals(entity.getStatus()) || "Approved".equals(entity.getStatus())){
						unUpdateIds.add(entity.getSupervisoryVisitFormId());
						table.put(entity.getSupervisoryVisitFormId(), supervisoryVisitForm.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(supervisoryVisitForm.getModifiedDate())){
						unUpdateIds.add(entity.getSupervisoryVisitFormId());
						table.put(entity.getSupervisoryVisitFormId(), supervisoryVisitForm.getLocalId());
						continue;
					}
				}
				
				BeanUtils.copyProperties(supervisoryVisitForm, entity);
				
				Date fromTime = null;
				if(!StringUtils.isEmpty(supervisoryVisitForm.getFromTime())){
					try{
						fromTime = commonService.getTime(supervisoryVisitForm.getFromTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setFromTime(fromTime);
				
				Date toTime = null;
				if(!StringUtils.isEmpty(supervisoryVisitForm.getToTime())){
					try{
						toTime = commonService.getTime(supervisoryVisitForm.getToTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setToTime(toTime);
				
				if (supervisoryVisitForm.getOfficerId() != null){
					User user = userDao.findById(supervisoryVisitForm.getOfficerId());
					if (user != null){
						entity.setUser(user);						
					}
				}
				
				if (supervisoryVisitForm.getSupervisorId() != null){
					User user = userDao.findById(supervisoryVisitForm.getSupervisorId());
					if (user != null){
						entity.setSupervisor(user);						
					}
				}
				
				if (supervisoryVisitForm.getSubmitTo() != null){
					User user = userDao.findById(supervisoryVisitForm.getSubmitTo());
					if (user != null){
						entity.setSubmitTo(user);						
					}
				}
				
				if(supervisoryVisitForm.getScSvPlanId()!=null){

					ScSvPlan scSvPlan = scSvPlanDao.findById(supervisoryVisitForm.getScSvPlanId());
					if (scSvPlan != null){
						entity.setScSvPlan(scSvPlan);
					}
				}
				entity.setByPassModifiedDate(true);
				supervisoryVisitDao.save(entity);
				allSupervisoryVisitFormIds.add(entity.getSupervisoryVisitFormId());
				table.put(entity.getSupervisoryVisitFormId(), supervisoryVisitForm.getLocalId());
			}
			supervisoryVisitDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			List<SupervisoryVisitFormSyncData> updatedData = new ArrayList<SupervisoryVisitFormSyncData>();
			
			if(allSupervisoryVisitFormIds!=null && allSupervisoryVisitFormIds.size()>0){
				updatedData.addAll(syncSupervisoryVisitFormRecursiveQuery(allSupervisoryVisitFormIds, lastSyncTime));
			}
			if(qcItineraryPlanIds!=null&&qcItineraryPlanIds.length>0){
				updatedData.addAll(qcItineraryPlanDao.getUpdatedSupervisoryVisitForm(lastSyncTime, null, qcItineraryPlanIds));
			}
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncSupervisoryVisitFormRecursiveQuery(unUpdateIds, null));
			}
			
			List<SupervisoryVisitFormSyncData> unique = new ArrayList<SupervisoryVisitFormSyncData>(new HashSet<SupervisoryVisitFormSyncData>(updatedData));
			
			for(SupervisoryVisitFormSyncData data : unique){
				if(table.containsKey(data.getSupervisoryVisitFormId())){
					data.setLocalId(table.get(data.getSupervisoryVisitFormId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<SupervisoryVisitFormSyncData>();
	}
	
	@Transactional
	public List<SupervisoryVisitDetailSyncData> syncSupervisoryVisitDetailData(List<SupervisoryVisitDetailSyncData> supervisoryVisitDetails
			, Date lastSyncTime, Boolean dataReturn, Integer[] supervisoryVisitFormIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();
		if(supervisoryVisitDetails!=null && supervisoryVisitDetails.size()>0){
			for(SupervisoryVisitDetailSyncData supervisoryVisitDetail : supervisoryVisitDetails){
				SupervisoryVisitDetail entity = null;
	
				if(supervisoryVisitDetail.getSupervisoryVisitDetailId()==null){
					if ("D".equals(supervisoryVisitDetail.getLocalDbRecordStatus())){
						continue;
					}
					entity = new SupervisoryVisitDetail();
				} else {
					entity = supervisoryVisitDetailDao.findById(supervisoryVisitDetail.getSupervisoryVisitDetailId());
					if("Submitted".equals(entity.getSupervisoryVisitForm().getStatus()) || "Approved".equals(entity.getSupervisoryVisitForm().getStatus())){
						updateIds.add(entity.getSupervisoryVisitDetailId());
						table.put(entity.getSupervisoryVisitDetailId(), supervisoryVisitDetail.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(supervisoryVisitDetail.getModifiedDate())){
						updateIds.add(entity.getSupervisoryVisitDetailId());
						table.put(entity.getSupervisoryVisitDetailId(), supervisoryVisitDetail.getLocalId());
						continue;
					}
					if (entity != null && "D".equals(supervisoryVisitDetail.getLocalDbRecordStatus())){
						supervisoryVisitDetailDao.delete(entity);
						continue;
					}
					else if ("D".equals(supervisoryVisitDetail.getLocalDbRecordStatus())){
						continue;
					}
				}
				
				
				BeanUtils.copyProperties(supervisoryVisitDetail, entity);
				
				if(supervisoryVisitDetail.getAssignmentId()!=null){
					Assignment assignment = assignmentDao.findById(supervisoryVisitDetail.getAssignmentId());
					if (assignment != null){
						entity.setAssignment(assignment);
					}
				}
				
				if(supervisoryVisitDetail.getSupervisoryVisitFormId()!=null){
					SupervisoryVisitForm supervisoryVisitForm = supervisoryVisitDao.findById(supervisoryVisitDetail.getSupervisoryVisitFormId());
					if (supervisoryVisitForm != null){
						entity.setSupervisoryVisitForm(supervisoryVisitForm);						
					}
				}
				entity.setByPassModifiedDate(true);
				supervisoryVisitDetailDao.save(entity);
				updateIds.add(entity.getSupervisoryVisitDetailId());
				table.put(entity.getSupervisoryVisitDetailId(), supervisoryVisitDetail.getLocalId());
			}
			
		}
		
		if(dataReturn!=null && dataReturn){
			List<SupervisoryVisitDetailSyncData> updatedData = new ArrayList<SupervisoryVisitDetailSyncData>();
			if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.length>0){
				updatedData.addAll(syncDetailBySupervisoryVisitFormRecursiveQuery(Arrays.asList(supervisoryVisitFormIds), lastSyncTime));
			}
			
			if(updateIds!=null && updateIds.size()>0){
				updatedData.addAll(syncDetailByIdRecursiveQuery(updateIds));
			}
			
			List<SupervisoryVisitDetailSyncData> unique = new ArrayList<SupervisoryVisitDetailSyncData>(new HashSet<SupervisoryVisitDetailSyncData>(updatedData));
			for(SupervisoryVisitDetailSyncData data : unique){
				if(table.containsKey(data.getSupervisoryVisitDetailId())){
					data.setLocalId(table.get(data.getSupervisoryVisitDetailId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<SupervisoryVisitDetailSyncData>();
	}

	/**
	 * DataTable count
	 */
	public Long getSupervisoryVisitCount(int level, List<Integer> userIdList, Boolean isFieldTeamHead){
		boolean aboveSupervisor = false;
		if( ((level & 1) == 1) || ((level & 2) == 2)
				|| ((level & 256) == 256) || ((level & 2048) == 2048) )
			aboveSupervisor = true;

		//Outstanding Supervisory Visit from should count from now to 3 months ago only
		Date toDate = commonService.getDateWithoutTime(new Date());
		Date fromDate = DateUtils.truncate(DateUtils.addMonths(toDate, -3), Calendar.DATE);
		
		return supervisoryVisitDao.countSelectOutstandingSupervisoryVisit("", aboveSupervisor, userIdList, isFieldTeamHead, fromDate, toDate);
	}
	
	public List<SupervisoryVisitDetailSyncData> syncDetailBySupervisoryVisitFormRecursiveQuery(List<Integer> supervisoryVisitFormIds, Date lastSyncTime){
		List<SupervisoryVisitDetailSyncData> entities = new ArrayList<SupervisoryVisitDetailSyncData>();
		if(supervisoryVisitFormIds.size()>2000){
			List<Integer> ids = supervisoryVisitFormIds.subList(0, 2000);
			entities.addAll(syncDetailBySupervisoryVisitFormRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = supervisoryVisitFormIds.subList(2000, supervisoryVisitFormIds.size());
			entities.addAll(syncDetailBySupervisoryVisitFormRecursiveQuery(remainIds, lastSyncTime));
		} else if (supervisoryVisitFormIds.size()>0){
			return supervisoryVisitDetailDao.getUpdatedSupervisoryVisitDetail(lastSyncTime, supervisoryVisitFormIds.toArray(new Integer[0]), null);
		}
		return entities;
	}
	
	public List<SupervisoryVisitDetailSyncData> syncDetailByIdRecursiveQuery(List<Integer> supervisoryVisitDetailIds){
		List<SupervisoryVisitDetailSyncData> entities = new ArrayList<SupervisoryVisitDetailSyncData>();
		if(supervisoryVisitDetailIds.size()>2000){
			List<Integer> ids = supervisoryVisitDetailIds.subList(0, 2000);
			entities.addAll(syncDetailByIdRecursiveQuery(ids));
			
			List<Integer> remainIds = supervisoryVisitDetailIds.subList(2000, supervisoryVisitDetailIds.size());
			entities.addAll(syncDetailByIdRecursiveQuery(remainIds));
		} else if (supervisoryVisitDetailIds.size()>0){
			return supervisoryVisitDetailDao.getUpdatedSupervisoryVisitDetail(null, null, supervisoryVisitDetailIds.toArray(new Integer[0]));
		}
		return entities;
	}
	
	public List<SupervisoryVisitFormSyncData> syncSupervisoryVisitFormRecursiveQuery(List<Integer> supervisoryVisitFormIds, Date lastSyncTime){
		List<SupervisoryVisitFormSyncData> entities = new ArrayList<SupervisoryVisitFormSyncData>();
		if(supervisoryVisitFormIds.size()>2000){
			List<Integer> ids = supervisoryVisitFormIds.subList(0, 2000);
			entities.addAll(syncSupervisoryVisitFormRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = supervisoryVisitFormIds.subList(2000, supervisoryVisitFormIds.size());
			entities.addAll(syncSupervisoryVisitFormRecursiveQuery(remainIds, lastSyncTime));
		} else if(supervisoryVisitFormIds.size()>0){
			return qcItineraryPlanDao.getUpdatedSupervisoryVisitForm(lastSyncTime, supervisoryVisitFormIds.toArray(new Integer[0]), null);
		}
		return entities;
	}
}
