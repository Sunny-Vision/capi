package capi.service.timeLogManagement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AssignmentDao;
import capi.dal.CalendarEventDao;
import capi.dal.FieldworkTimeLogDao;
import capi.dal.ItineraryPlanDao;
import capi.dal.ItineraryPlanOutletDao;
import capi.dal.PurposeDao;
import capi.dal.RoleDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.TelephoneTimeLogDao;
import capi.dal.TimeLogDao;
import capi.dal.UserDao;
import capi.dal.WorkingSessionSettingDao;
import capi.entity.Assignment;
import capi.entity.CalendarEvent;
import capi.entity.FieldworkTimeLog;
import capi.entity.ItineraryPlanOutlet;
import capi.entity.Purpose;
import capi.entity.QuotationRecord;
import capi.entity.Role;
import capi.entity.SystemConfiguration;
import capi.entity.TelephoneTimeLog;
import capi.entity.TimeLog;
import capi.entity.User;
import capi.entity.WorkingSessionSetting;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.FieldworkTimeLogSyncData;
import capi.model.api.dataSync.TelephoneTimeLogSyncData;
import capi.model.api.dataSync.TimeLogSyncData;
import capi.model.assignmentManagement.TpuReferenceNoModel;
import capi.model.itineraryPlanning.ItineraryPlanEditModel;
import capi.model.itineraryPlanning.ItineraryPlanOutletModel;
import capi.model.itineraryPlanning.MajorLocationModel;
import capi.model.timeLogManagement.TimeLogSelect2ResponseModel;
import capi.model.timeLogManagement.AssignmentReferenceNoModel;
import capi.model.timeLogManagement.FieldworkTimeLogModel;
import capi.model.timeLogManagement.ItineraryCheckingTableList;
import capi.model.timeLogManagement.TelephoneTimeLogModel;
import capi.model.timeLogManagement.TimeLogSelect2Item;
import capi.model.timeLogManagement.TimeLogModel;
import capi.model.timeLogManagement.TimeLogTableList;
import capi.model.timeLogManagement.ValidationResultModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("TimeLogService")
public class TimeLogService extends BaseService {

	@Autowired
	private TimeLogDao timeLogDao;
	
	@Autowired
	private TelephoneTimeLogDao telephoneTimeLogDao;
	
	@Autowired
	private FieldworkTimeLogDao fieldworkTimeLogDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private WorkingSessionSettingDao workingSessionSettingDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ItineraryPlanDao itineraryPlanDao;
	
	@Autowired
	private ItineraryPlanOutletDao itineraryPlanOutletDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private SystemConfigurationDao systemConfigurationDao;

	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private CalendarEventService calendarEventService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private NotificationService notifyService;
	
	/** 
	 * query time log table list
	 */
	public DatatableResponseModel<TimeLogTableList> queryTimeLog(DatatableRequestModel model, Integer[] userIds){
		
		Order order = this.getOrder(model, "t.date", "officerCode", "officerChineseName", "status", "approvedByCode");
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		List<Integer> supervisorIds = new ArrayList<Integer>();
		Integer selfId = null;
		
		if (!((detail.getAuthorityLevel() & 256) == 256 || 
				(detail.getAuthorityLevel() & 2048) == 2048 || 
				(detail.getAuthorityLevel() & 2) == 2 || 
				(detail.getAuthorityLevel() & 1) == 1)){
			
			if ((detail.getAuthorityLevel() & 4) == 4){
				supervisorIds.add(detail.getUserId());
				supervisorIds.addAll(detail.getActedUsers());
			}		
			
			if ((detail.getAuthorityLevel() & 16) == 16 || (detail.getAuthorityLevel() & 4) == 4){
				selfId = detail.getUserId();
			}
		}
		Integer ownerId = detail.getUserId();
		
		//boolean superAdmin = (detail.getAuthorityLevel() & 256) == 256 || (detail.getAuthorityLevel() & 2048) == 2048;
		
		List<TimeLogTableList> result = timeLogDao
				.getTimeLogTableList(search, model.getStart(), model.getLength(), order, userIds, supervisorIds.toArray(new Integer[0]), selfId, ownerId);
					
		DatatableResponseModel<TimeLogTableList> response = new DatatableResponseModel<TimeLogTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal =timeLogDao.countTimeLogTableList("", userIds, supervisorIds.toArray(new Integer[0]), selfId, ownerId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = timeLogDao.countTimeLogTableList(search, userIds, supervisorIds.toArray(new Integer[0]), selfId, ownerId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/** 
	 * query itinerary checking table list 
	 */
	public DatatableResponseModel<ItineraryCheckingTableList> queryItineraryCheckingTable(DatatableRequestModel model){
		
		Order order = this.getOrder(model, "", "date", "officerCode", "officerChineseName", "assignmentDeviation", "sequenceDeviation", "tpuDeviation", "itineraryCheckRemark" );
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> supervisorIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 256) == 256 || 
				(detail.getAuthorityLevel() & 2048) == 2048 || 
				(detail.getAuthorityLevel() & 2) == 2 || 
				(detail.getAuthorityLevel() & 1) == 1)){
			
			if ((detail.getAuthorityLevel() & 4) == 4){
				supervisorIds.add(userId);		
				supervisorIds.addAll(detail.getActedUsers());
			}
			
		}
		
		//Assignment Deviation;
		SystemConfiguration systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS);
		SystemConfiguration assignmentDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION);
		Double noOfAssignmentDeviationLimit = null;
		if (assignmentDeviationConfig != null && assignmentDeviationConfig.getValue() != null && assignmentDeviationConfig.getValue().equals("1"))
			noOfAssignmentDeviationLimit = Double.parseDouble(systemConfiguration.getValue());
		
		//Sequence Deviation;
		systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS);
		SystemConfiguration sequenceDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION);
		Double sequenceDeviationLimit = null;
		if (sequenceDeviationConfig != null && sequenceDeviationConfig.getValue() != null && sequenceDeviationConfig.getValue().equals("1"))
			sequenceDeviationLimit = Double.parseDouble(systemConfiguration.getValue());
		
		//TPU Deviation
		systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES);
		SystemConfiguration tpuDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION);
		Integer tpuDeviationLimit = null;
		if (tpuDeviationConfig != null && tpuDeviationConfig.getValue() != null && tpuDeviationConfig.getValue().equals("1"))
			tpuDeviationLimit = Integer.parseInt(systemConfiguration.getValue());
		
		List<ItineraryCheckingTableList> result = timeLogDao.getItineraryCheckingTableList(supervisorIds.toArray(new Integer[0]), 
				noOfAssignmentDeviationLimit, sequenceDeviationLimit, tpuDeviationLimit, search, model.getStart(), model.getLength(), order);
					
		DatatableResponseModel<ItineraryCheckingTableList> response = new DatatableResponseModel<ItineraryCheckingTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal =timeLogDao.countItineraryCheckingTableList(supervisorIds.toArray(new Integer[0]),"");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = timeLogDao.countItineraryCheckingTableList(supervisorIds.toArray(new Integer[0]),search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public String getEnumerationOutcomeBySurvey(String survey) {
		List<Purpose> purposes = purposeDao.findPurposeBySurvey(survey);
		if (purposes.size() > 0)
			return purposes.get(0).getEnumerationOutcomes();
		else
			return "";
	}
	
	public Integer levenshteinDistance(List<String> list1, List<String> list2)
    {
		
		/* base case: empty strings */
		if (list1.size() == 0) return list2.size();
		if (list2.size() == 0) return list1.size();
		  
		int d[][] = new int [list1.size() + 1][];
		
        for (int i = 0; i <= list1.size(); i++){
            d[i] = new int[list2.size() + 1];
            d[i][0] = i;
        }
        for (int i = 0; i <= list2.size(); i++){
            d[0][i] = i;
        }
        
        for (int j = 1; j <= list2.size(); j++)
            for (int i = 1; i <= list1.size(); i++)
                if (list1.get(i - 1).equals(list2.get(j - 1)))
                    d[i][j] = d[i - 1][j - 1];  //no operation
                else
                {
                    d[i][j] = Math.min(
                       d[i - 1][j] + 1,    //a deletion
                       d[i][j - 1] + 1     //an insertion
                       );
                }
        return d[list1.size()][list2.size()];
    }
	
	public ValidationResultModel validateTimeLogDeviation(List<String> logReferenceNos, Integer userId, Date planDate) {
		
		ValidationResultModel validationResultModel = new ValidationResultModel();
		
		List<String> stringBuilder = new ArrayList<String>();
		
		List<Role> roles = roleDao.getRolesByUser(userId);
		if (roles != null && roles.size() > 0){
			for (Role role : roles){
				if (role.getAuthorityLevel() != null && (role.getAuthorityLevel() & 4) == 4){
					validationResultModel.setValid(true);
					return validationResultModel;
				}
			}
			
		}
		
		logReferenceNos = new ArrayList<String>(CollectionUtils.select(logReferenceNos, new Predicate<String>(){

			@Override
			public boolean evaluate(String arg0) {
				// TODO Auto-generated method stub
				return !StringUtils.isEmpty(arg0)&&!arg0.startsWith("NR-");
			}
			
		}));

		boolean valid = true;
		
		//Assignment Deviation;
		SystemConfiguration systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS);
		SystemConfiguration assignmentDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION);
		Double noOfAssignmentDeviationLimit = Double.parseDouble(systemConfiguration.getValue());
		
		List<String> planCaseReferenceNo = itineraryPlanDao.getReferenceList(userId, planDate);
		List<String> validLogReferenceNo = new ArrayList<String>();
		if(logReferenceNos!=null && logReferenceNos.size()>0)
			validLogReferenceNo = assignmentDao.getValidReferenceCode(logReferenceNos);
		
		List<String> orderedLogReferenceNo = new ArrayList<String>();
		for (String log : logReferenceNos){
			if (validLogReferenceNo.contains(log)){
				orderedLogReferenceNo.add(log);
			}
		}
				
		if (assignmentDeviationConfig != null && assignmentDeviationConfig.getValue() != null && assignmentDeviationConfig.getValue().equals("1")){
			Set<String> uniqueCaseReferenceNos = new HashSet<String>(orderedLogReferenceNo);
			Set<String> uniquePlanCaseReferenceNos = new HashSet<String>(planCaseReferenceNo);
			
			Set<String> diffLogReferenceNos = new HashSet<String>(CollectionUtils.subtract(uniqueCaseReferenceNos, uniquePlanCaseReferenceNos));
//			Set<String> diffLogReferenceNos = new HashSet<String>(uniqueCaseReferenceNos);
//			diffLogReferenceNos.removeAll(uniquePlanCaseReferenceNos);
			
			Set<String> diffPlanCaseReferenceNos = new HashSet<String>(CollectionUtils.subtract(uniquePlanCaseReferenceNos, uniqueCaseReferenceNos));
//			Set<String> diffPlanCaseReferenceNos = new HashSet<String>(uniquePlanCaseReferenceNos);
//			diffPlanCaseReferenceNos.removeAll(uniquePlanCaseReferenceNos);
			
			if (uniquePlanCaseReferenceNos.size() != 0){
				Double noOfAssignmentDeviation = (diffLogReferenceNos.size() + diffPlanCaseReferenceNos.size()) / (double) uniquePlanCaseReferenceNos.size() * 100;
				
				validationResultModel.setAssignmentDeviation(noOfAssignmentDeviation);
				if (noOfAssignmentDeviation > noOfAssignmentDeviationLimit) {
					valid = false;
					stringBuilder.add("Deviation No. of Assignment");
				}
			} else if (uniqueCaseReferenceNos.size() != 0){
				validationResultModel.setAssignmentDeviation((double)100);
				if ((double)100 > noOfAssignmentDeviationLimit) {
					valid = false;
					stringBuilder.add("Deviation No. of Assignment");
				}
			}
			
		}
		
		
		//Sequence Deviation;
		systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS);
		SystemConfiguration sequenceDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION);
		
		if (sequenceDeviationConfig!= null && sequenceDeviationConfig.getValue() != null && sequenceDeviationConfig.getValue().equals("1")){
			Double sequenceDeviationLimit = Double.parseDouble(systemConfiguration.getValue());
			
			if(planCaseReferenceNo.size()==0 && orderedLogReferenceNo.size()>0 
					|| planCaseReferenceNo.size()>0 && orderedLogReferenceNo.size()==0){
				if ((double)100 > sequenceDeviationLimit) {
					validationResultModel.setSequenceDeviation((double)100);
					valid = false;
				}
			} else if (planCaseReferenceNo.size()>0 && orderedLogReferenceNo.size()>0){
				Collection<String> intersectPlan = CollectionUtils.intersection(planCaseReferenceNo, orderedLogReferenceNo);
				
				List<ItineraryPlanOutlet> outlets = itineraryPlanOutletDao.getItineraryPlanOutlet(userId, planDate);
				HashMap<String, ItineraryPlanOutlet> referenceCodeMap = new HashMap<String, ItineraryPlanOutlet>();
				
				for (ItineraryPlanOutlet outlet : outlets){
					if (!referenceCodeMap.containsKey(outlet.getReferenceNo())){
						referenceCodeMap.put(outlet.getReferenceNo(), outlet);
					}
				}			

				List<String> orderedIntersectPlan = new ArrayList<String>();
				for (String log : planCaseReferenceNo){
					if (intersectPlan.contains(log)){
						orderedIntersectPlan.add(log);
						intersectPlan.remove(log);
					}
				}
				
				List<String> majorlocationPlan = new ArrayList<String>();
				for (int i = 0 ; i < orderedIntersectPlan.size() ; i++ ) {
					if (referenceCodeMap.containsKey(orderedIntersectPlan.get(i))){
						String code = orderedIntersectPlan.get(i);
						Integer mlId = referenceCodeMap.get(code).getMajorLocation().getMajorLocationId();
						majorlocationPlan.add(mlId.toString());
					}
					//majorlocationPlan.add(itineraryPlanDao.getMajorLocationIdByReferenceNo(userId, planDate, orderedIntersectPlan.get(i)).toString());
				}
						
				intersectPlan = CollectionUtils.intersection(planCaseReferenceNo, orderedLogReferenceNo);
				
				List<String> orderedLogPlan = new ArrayList<String>();
				for (String log : orderedLogReferenceNo){
					if (intersectPlan.contains(log)){
						orderedLogPlan.add(log);
						intersectPlan.remove(log);
					}
				}
//				List<String> intersectLog = new ArrayList<String>(logReferenceNos);
//				intersectLog.removeAll(diffLogReferenceNos);
				List<String> majorlocationLog= new ArrayList<String>();
				for (int i = 0 ; i < orderedLogPlan.size() ; i++ ) {
					if (referenceCodeMap.containsKey(orderedLogPlan.get(i))){
						String code = orderedLogPlan.get(i);
						Integer mlId = referenceCodeMap.get(code).getMajorLocation().getMajorLocationId();
						majorlocationLog.add(mlId.toString());
					}
					//majorlocationLog.add(itineraryPlanDao.getMajorLocationIdByReferenceNo(userId, planDate, orderedLogPlan.get(i)).toString());
				}		
				
				Double sequenceDeviation;
				if (orderedIntersectPlan.size()>0){
					sequenceDeviation = levenshteinDistance(majorlocationPlan,majorlocationLog) / (double)2 / (double)orderedIntersectPlan.size() * 100;
				} else {
					sequenceDeviation = 0.0;
				}
				
				validationResultModel.setSequenceDeviation(sequenceDeviation);
				if (sequenceDeviation > sequenceDeviationLimit) {
					valid = false;
					stringBuilder.add("Deviation of Sequence");
				}
			}
		}
		
		
		//TPU Deviation
		systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES);
		SystemConfiguration tpuDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION);
		
		if (tpuDeviationConfig!= null && tpuDeviationConfig.getValue() != null && tpuDeviationConfig.getValue().equals("1")){
			Integer tpuDeviationLimit = Integer.parseInt(systemConfiguration.getValue());
			
			List<TpuReferenceNoModel> tpuReferenceNos = new ArrayList<TpuReferenceNoModel>();
			if(orderedLogReferenceNo!=null && orderedLogReferenceNo.size()>0)
				tpuReferenceNos = assignmentDao.getReferenceNoRelatedTpu(orderedLogReferenceNo);
			
			HashMap<String, String> tpuMap = new HashMap<String, String>();
			for (TpuReferenceNoModel model: tpuReferenceNos){
				tpuMap.put(model.getReferenceCode(), model.getTpuCode());
			}
			
			List<String> tpuLog = new ArrayList<String>();
			
			for (int i=0 ; i < orderedLogReferenceNo.size() ; i++ ) {
				String code = orderedLogReferenceNo.get(i);
				if (tpuMap.containsKey(code)){
					tpuLog.add(tpuMap.get(code));
				}
				// time log reference no.
				//tpuLog.add(assignmentDao.getTpuByReferenceNo(orderedLogReferenceNo.get(i)));
			}
			
			List<String> tpuPlan = itineraryPlanDao.getTpuList(userId, planDate );
			
			List<String> dedupTpuLog = dedupConsecutiveCode(tpuLog);
			List<String> dedupTpuPlan = dedupConsecutiveCode(tpuPlan);
			
			Integer tpuDeviation = levenshteinDistance(dedupTpuLog,dedupTpuPlan);
			
			validationResultModel.setTpuDeviation(tpuDeviation);
			if (tpuDeviation > tpuDeviationLimit ) {
				valid = false;
				stringBuilder.add("Deviation of Tpu");
			}	
		}
		
		validationResultModel.setValid(valid);
		validationResultModel.setStatus(StringUtils.join(stringBuilder, " & "));
		return validationResultModel;
	}
	
	
	public List<String> dedupConsecutiveCode(List<String> codes){
		String tempCode = "";
		List<String> results = new ArrayList<String>();
		for (String code : codes){
			if (tempCode.equals(code)){
				continue;
			}
			tempCode = code;
			results.add(code);
		}
		
		return results;
	}
	
	
	public List<WorkingSessionSetting> getWorkingSessionSettings() {
		return workingSessionSettingDao.getWorkingSessionSettings();
	}
	
	public WorkingSessionSetting getWorkingSessionSettings(Integer id) {
		return workingSessionSettingDao.findById(id);
	}
	
	/**
	 * Get Case Reference select2 format
	 */
	public TimeLogSelect2ResponseModel queryCaseReferenceNoSelect2(Date referenceMonth, String survey, Integer userId, String type, Select2RequestModel queryModel) {
		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
//		List<Integer> ownerIds = new ArrayList<Integer>();
//		ownerIds.add(detail.getUserId());
		
		queryModel.setRecordsPerPage(10);
		TimeLogSelect2ResponseModel responseModel = new TimeLogSelect2ResponseModel();
		List<TimeLogSelect2Item> items;
		if (type == "Telephone") {
			items = assignmentDao.getTimeLogSelect2ItemByTelephoneReferenceNo(
						referenceMonth, survey, userId, queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		} else {
			items = assignmentDao.getTimeLogSelect2ItemByFieldworkReferenceNo(
						referenceMonth, survey, userId, queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		}
		long recordsTotal = 0;
		if (type == "Telephone") {
			recordsTotal = assignmentDao.countTimeLogSelect2ItemByTelephoneReferenceNo(referenceMonth, survey, userId, queryModel.getTerm());
		} else {
			recordsTotal = assignmentDao.countTimeLogSelect2ItemByFieldworkReferenceNo(referenceMonth, survey, userId, queryModel.getTerm());
		}
		queryModel.setRecordsTotal(recordsTotal);
		//queryModel.setRecordsTotal(items.size());
		boolean more = queryModel.hasMore();
		TimeLogSelect2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		responseModel.setResults(items);
		return responseModel;	
	}	
	
	public TimeLogSelect2Item getTelephoneReferenceNoDetailById(Date referenceMonth, String referenceNo) {

		TimeLogSelect2Item result = assignmentDao.getTimeLogTelephoneReferenceNoDetailById(referenceMonth, referenceNo);
		return result;
	}
	
	public TimeLogSelect2Item getFieldworkReferenceNoDetailById(Date referenceMonth, String referenceNo) {

		TimeLogSelect2Item result = null;
		if(referenceMonth != null)
			result = assignmentDao.getTimeLogFieldworkReferenceNoDetailById(referenceMonth, referenceNo);
		else
			result = assignmentDao.getTimeLogFieldworkReferenceNoDetailById(null, referenceNo);
		
		return result;
	}
	
	public TimeLogModel getTimeLogModel(Integer id) {
		
		TimeLogModel timeLogModel = new TimeLogModel();
		TimeLog timeLog = timeLogDao.findById(id);
		
		BeanUtils.copyProperties(timeLog, timeLogModel);
		if(timeLog.getApprovedBy() != null && timeLog.getStatus().equals(SystemConstant.TIMELOG_STATUS_REJECTED)){
			timeLogModel.setApprovedBy(timeLog.getApprovedBy().getStaffCode() + " - " + timeLog.getApprovedBy().getChineseName());
		}
		
		timeLogModel.setIsTrainingAM(timeLog.isTrainingAM());
		timeLogModel.setIsTrainingPM(timeLog.isTrainingPM());
		timeLogModel.setIsVLSLAM(timeLog.isVLSLAM());
		timeLogModel.setIsVLSLPM(timeLog.isVLSLPM());
		timeLogModel.setIsClaimOT(timeLog.isClaimOT());
		
		timeLogModel.setUserId(timeLog.getUser().getId());
		timeLogModel.setUserCode(timeLog.getUser().getStaffCode() + " - " + timeLog.getUser().getChineseName());
		
		timeLogModel.setCreatedDateDisplay(commonService.formatDateTime(timeLog.getCreatedDate()));
		timeLogModel.setModifiedDateDisplay(commonService.formatDateTime(timeLog.getModifiedDate()));
		
		if (timeLog.getSetting() != null) {
			timeLogModel.setWorkingSessionId(timeLog.getSetting().getId());
		} else {
			timeLogModel.setWorkingSessionId(0);
		}
		
		timeLogModel.setDate(commonService.formatDate(timeLog.getDate()));
		if (timeLog.isOtherWorkingSession() == true) {
			timeLogModel.setIsOtherWorkingSession(true);
			timeLogModel.setOtherWorkingSessionFrom(commonService.formatTime(timeLog.getOtherWorkingSessionFrom()));
			timeLogModel.setOtherWorkingSessionTo(commonService.formatTime(timeLog.getOtherWorkingSessionTo()));
		}

		if (timeLog.getOtClaimed() != null)
			timeLogModel.setOtClaimed(commonService.formatTime(timeLog.getOtClaimed()));

		if (timeLog.getTimeoffTaken() != null)
			timeLogModel.setTimeoffTaken(commonService.formatTime(timeLog.getTimeoffTaken()));
		
		List<TelephoneTimeLogModel> telephoneTimeLogs = new ArrayList<TelephoneTimeLogModel>();		
		for (TelephoneTimeLog telephoneTimeLog : timeLog.getTelephoneTimeLogs()) {
			TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
			BeanUtils.copyProperties(telephoneTimeLog, telephoneTimeLogModel);
			if (telephoneTimeLog.getStatus().equals("C")) {
				telephoneTimeLogModel.setCompletionQuotationCount(telephoneTimeLog.getQuotationCount());
				telephoneTimeLogModel.setCompletionTotalQuotation(telephoneTimeLog.getTotalQuotation());
			} else if (telephoneTimeLog.getStatus().equals("D")) {
				telephoneTimeLogModel.setDeletionQuotationCount(telephoneTimeLog.getQuotationCount());
				telephoneTimeLogModel.setDeletionTotalQuotation(telephoneTimeLog.getTotalQuotation());
			}
			if (telephoneTimeLog.getAssignment() != null) {
				telephoneTimeLogModel.setAssignmentId(telephoneTimeLog.getAssignment().getAssignmentId());
			}
			telephoneTimeLogModel.setReferenceMonth(commonService.formatMonth(telephoneTimeLog.getReferenceMonth()));
			telephoneTimeLogs.add(telephoneTimeLogModel);
		}
		timeLogModel.setTelephoneTimeLogs(telephoneTimeLogs);
		
		List<FieldworkTimeLogModel> fieldworkTimeLogs = new ArrayList<FieldworkTimeLogModel>();	
		for (FieldworkTimeLog fieldworkTimeLog : timeLog.getFieldworkTimeLogs()) {
			FieldworkTimeLogModel fieldworkTimeLogModel = new FieldworkTimeLogModel();
			BeanUtils.copyProperties(fieldworkTimeLog, fieldworkTimeLogModel);
			if (fieldworkTimeLog.getRecordType() != null) {
				if (fieldworkTimeLog.getRecordType() == 1 ) {
					fieldworkTimeLogModel.setMarketQuotationCount(fieldworkTimeLog.getQuotationCount());
					fieldworkTimeLogModel.setMarketTotalQuotation(fieldworkTimeLog.getTotalQuotation());
				} else if (fieldworkTimeLog.getRecordType() == 2 ) {
					fieldworkTimeLogModel.setNonMarketQuotationCount(fieldworkTimeLog.getQuotationCount());
					fieldworkTimeLogModel.setNonMarketTotalQuotation(fieldworkTimeLog.getTotalQuotation());				
				}
			}
			if (fieldworkTimeLog.getAssignment() != null) {
				fieldworkTimeLogModel.setAssignmentId(fieldworkTimeLog.getAssignment().getAssignmentId());
			}
			fieldworkTimeLogModel.setReferenceMonth(commonService.formatMonth(fieldworkTimeLog.getReferenceMonth()));
			fieldworkTimeLogModel.setStartTime(commonService.formatTime(fieldworkTimeLog.getStartTime()));
			fieldworkTimeLogs.add(fieldworkTimeLogModel);
		}
		timeLogModel.setFieldworkTimeLogs(fieldworkTimeLogs);
		
		return timeLogModel;
	}
	@Transactional
	public Integer saveTimeLog(TimeLogModel timeLogModel) throws ParseException{
		
		TimeLog timeLog;
		Integer checkTimeLogId = timeLogDao.getTimeLogIdByUserIdAndDate(timeLogModel.getUserId(), commonService.getDate(timeLogModel.getDate()));
		
		if (timeLogModel.getTimeLogId() != null) {
			timeLog = timeLogDao.findById(timeLogModel.getTimeLogId());
			if (timeLog == null) {
				return 1;
			} else if (checkTimeLogId != null && timeLog.getTimeLogId().compareTo(checkTimeLogId) != 0) {
				return 2;
			}
		} else {
			if (checkTimeLogId != null) {
				return 2;
			} else {
				timeLog = new TimeLog();
			}
		}
		
		List<String> completedReferenceNo = new ArrayList<String>();
		String status = timeLogModel.getStatus();
		

		BeanUtils.copyProperties(timeLogModel, timeLog);
		User user = userDao.findById(timeLogModel.getUserId());
		timeLog.setUser(user);
		
		timeLog.setDate(commonService.getDate(timeLogModel.getDate()));
		
		timeLog.setTrainingAM(timeLogModel.getIsTrainingAM()==null?false:true);
		timeLog.setTrainingPM(timeLogModel.getIsTrainingPM()==null?false:true);
		timeLog.setVLSLAM(timeLogModel.getIsVLSLAM()==null?false:true);
		timeLog.setVLSLPM(timeLogModel.getIsVLSLPM()==null?false:true);
		timeLog.setClaimOT(timeLogModel.getIsClaimOT()==null?false:true);
		
		timeLog.setOtherWorkingSession(timeLogModel.getIsOtherWorkingSession()==null?false:timeLogModel.getIsOtherWorkingSession());
		
		
		if (timeLogModel.getWorkingSessionId() != null) {
			timeLog.setSetting(workingSessionSettingDao.findById(timeLogModel.getWorkingSessionId()));
		}
		
		if (timeLogModel.getIsOtherWorkingSession() != null && timeLogModel.getIsOtherWorkingSession()) {
			timeLog.setOtherWorkingSessionFrom(commonService.getTime(timeLogModel.getOtherWorkingSessionFrom()));
			timeLog.setOtherWorkingSessionTo(commonService.getTime(timeLogModel.getOtherWorkingSessionTo()));
		} else {
			timeLog.setOtherWorkingSessionFrom(null);
			timeLog.setOtherWorkingSessionTo(null);
		}
		
		if (timeLogModel.getIsClaimOT() != null && timeLogModel.getIsClaimOT() ) {
			timeLog.setOtClaimed(commonService.getTime(timeLogModel.getOtClaimed()));
		} else {
			timeLog.setOtClaimed(null);
		}
		
		if (!StringUtils.isEmpty(timeLogModel.getTimeoffTaken())) {
			timeLog.setTimeoffTaken(commonService.getTime(timeLogModel.getTimeoffTaken()));
		}

		timeLogDao.save(timeLog);
		
		Set<TelephoneTimeLog> oldTelephoneTimeLogs;
		Set<FieldworkTimeLog> oldFieldworkTimeLogs;
		
		if(timeLog.getTimeLogId() != null && timeLog.getTimeLogId() > 0){
			oldTelephoneTimeLogs = timeLog.getTelephoneTimeLogs();
			oldFieldworkTimeLogs = timeLog.getFieldworkTimeLogs();
		} else {		
			oldTelephoneTimeLogs = new HashSet<TelephoneTimeLog>();
			oldFieldworkTimeLogs = new HashSet<FieldworkTimeLog>();
		}

		// TelephoneTimeLog 
		
		Set<TelephoneTimeLogModel> newTelephoneTimeLogModels = new HashSet<TelephoneTimeLogModel>();

		List<Integer> oldTelephoneIds = new ArrayList<Integer>();
		List<Integer> newTelephoneIds = new ArrayList<Integer>();
		
		if(timeLogModel.getTelephoneTimeLogs() != null) {
			newTelephoneTimeLogModels.addAll(timeLogModel.getTelephoneTimeLogs());
		}
		for(TelephoneTimeLog oldTelephoneTimeLog: oldTelephoneTimeLogs){
			oldTelephoneIds.add(oldTelephoneTimeLog.getTelephoneTimeLogId());
		}
			
		if(newTelephoneTimeLogModels.size() > 0){
			for(TelephoneTimeLogModel newTelephoneTimeLogModel: newTelephoneTimeLogModels){
				TelephoneTimeLog saveTelephoneTimeLog;
				if (newTelephoneTimeLogModel.getTelephoneTimeLogId() != null && newTelephoneTimeLogModel.getTelephoneTimeLogId() > 0) {
					newTelephoneIds.add(newTelephoneTimeLogModel.getTelephoneTimeLogId());
					saveTelephoneTimeLog = telephoneTimeLogDao.findById(newTelephoneTimeLogModel.getTelephoneTimeLogId());
				} else {
					saveTelephoneTimeLog = new TelephoneTimeLog();
				}
				BeanUtils.copyProperties(newTelephoneTimeLogModel, saveTelephoneTimeLog);
				saveTelephoneTimeLog.setQuotationCount((newTelephoneTimeLogModel.getCompletionQuotationCount()==null?0:newTelephoneTimeLogModel.getCompletionQuotationCount())
						+ (newTelephoneTimeLogModel.getDeletionQuotationCount()==null?0:newTelephoneTimeLogModel.getDeletionQuotationCount()));
				saveTelephoneTimeLog.setTotalQuotation((newTelephoneTimeLogModel.getCompletionTotalQuotation()==null?0:newTelephoneTimeLogModel.getCompletionTotalQuotation())
						+ (newTelephoneTimeLogModel.getDeletionTotalQuotation()==null?0:newTelephoneTimeLogModel.getDeletionTotalQuotation()));
				if (newTelephoneTimeLogModel.getDeletionQuotationCount() != null || newTelephoneTimeLogModel.getDeletionTotalQuotation() != null ) {
					saveTelephoneTimeLog.setStatus("D");
				}
				if (newTelephoneTimeLogModel.getCompletionQuotationCount() != null || newTelephoneTimeLogModel.getCompletionTotalQuotation() != null ) {
					saveTelephoneTimeLog.setStatus("C");
					completedReferenceNo.add(newTelephoneTimeLogModel.getCaseReferenceNo());
				}
//				if (newTelephoneTimeLogModel.getAssignmentId() != null ) {
//					Assignment assignment = assignmentDao.findById(newTelephoneTimeLogModel.getAssignmentId());
//					saveTelephoneTimeLog.setAssignment(assignment);
//				}
				saveTelephoneTimeLog.setReferenceMonth(commonService.getMonth(newTelephoneTimeLogModel.getReferenceMonth()));
				saveTelephoneTimeLog.setTimeLog(timeLog);
				telephoneTimeLogDao.save(saveTelephoneTimeLog);	
			}
		}
		
		// FieldworkTimeLog 

		List<FieldworkTimeLogModel> newFieldworkTimeLogModels = new ArrayList<FieldworkTimeLogModel>();

		List<Integer> oldFieldworkIds = new ArrayList<Integer>();
		List<Integer> newFieldworkIds = new ArrayList<Integer>();
		
		if(timeLogModel.getFieldworkTimeLogs() != null) {
			newFieldworkTimeLogModels.addAll(timeLogModel.getFieldworkTimeLogs());
		}
		for(FieldworkTimeLog oldFieldworkTimeLog: oldFieldworkTimeLogs){
			oldFieldworkIds.add(oldFieldworkTimeLog.getFieldworkTimeLogId());
		}
			
		if(newFieldworkTimeLogModels.size() > 0){
			Collections.sort(newFieldworkTimeLogModels);
			//Date endTime = null;
			//for(FieldworkTimeLogModel newFieldworkTimeLogModel: newFieldworkTimeLogModels){
			for (int i = 0; i < newFieldworkTimeLogModels.size(); i++){
				FieldworkTimeLogModel newFieldworkTimeLogModel = newFieldworkTimeLogModels.get(i);
				FieldworkTimeLog saveFieldworkTimeLog;
				if (newFieldworkTimeLogModel.getFieldworkTimeLogId() != null && newFieldworkTimeLogModel.getFieldworkTimeLogId() > 0) {
					newFieldworkIds.add(newFieldworkTimeLogModel.getFieldworkTimeLogId());
					saveFieldworkTimeLog = fieldworkTimeLogDao.findById(newFieldworkTimeLogModel.getFieldworkTimeLogId());
				} else {
					saveFieldworkTimeLog = new FieldworkTimeLog();
				}
				
				
				BeanUtils.copyProperties(newFieldworkTimeLogModel, saveFieldworkTimeLog);
				
				if(newFieldworkTimeLogModel.getNonMarketQuotationCount()!=null || newFieldworkTimeLogModel.getMarketQuotationCount()!=null){
					saveFieldworkTimeLog.setQuotationCount((newFieldworkTimeLogModel.getMarketQuotationCount()==null?0:newFieldworkTimeLogModel.getMarketQuotationCount())
							+ (newFieldworkTimeLogModel.getNonMarketQuotationCount()==null?0:newFieldworkTimeLogModel.getNonMarketQuotationCount()));
				}

				if(newFieldworkTimeLogModel.getMarketTotalQuotation()!=null || newFieldworkTimeLogModel.getNonMarketTotalQuotation()!=null){
					saveFieldworkTimeLog.setTotalQuotation((newFieldworkTimeLogModel.getMarketTotalQuotation()==null?0:newFieldworkTimeLogModel.getMarketTotalQuotation())
							+ (newFieldworkTimeLogModel.getNonMarketTotalQuotation()==null?0:newFieldworkTimeLogModel.getNonMarketTotalQuotation()));
				}
				
				// no need to relate time log to assignment
//				if (newFieldworkTimeLogModel.getAssignmentId() != null ) {
//					Assignment assignment = assignmentDao.findById(newFieldworkTimeLogModel.getAssignmentId());
//					saveFieldworkTimeLog.setAssignment(assignment);
//				}
				if (!newFieldworkTimeLogModel.getReferenceMonth().equals("")) {
					saveFieldworkTimeLog.setReferenceMonth(commonService.getMonth(newFieldworkTimeLogModel.getReferenceMonth()));
				}
				saveFieldworkTimeLog.setStartTime(commonService.getTime(newFieldworkTimeLogModel.getStartTime()));
				if (i+1 < newFieldworkTimeLogModels.size()){
					FieldworkTimeLogModel nextTimeLog = newFieldworkTimeLogModels.get(i+1);
					saveFieldworkTimeLog.setEndTime(commonService.getTime(nextTimeLog.getStartTime()));
				}
				
				if ("C".equals(newFieldworkTimeLogModel.getEnumerationOutcome())){
					completedReferenceNo.add(newFieldworkTimeLogModel.getCaseReferenceNo());
				}
				
				if ("D".equals(newFieldworkTimeLogModel.getEnumerationOutcome())){
					completedReferenceNo.add(newFieldworkTimeLogModel.getCaseReferenceNo());
				}
				
				//endTime = saveFieldworkTimeLog.getStartTime();
				saveFieldworkTimeLog.setTimeLog(timeLog);
				fieldworkTimeLogDao.save(saveFieldworkTimeLog);	
			}
		}
		//B162 Add "Voilated" to update assignment field(IsCompleted)
		if (("Submitted".equals(status)||"Voilated".equals(status)) && completedReferenceNo.size() > 0){
			List<Assignment> completedAssignments = assignmentDao.getImportedAssignmentByReferenceNo(completedReferenceNo);
			if (completedAssignments != null && completedAssignments.size() > 0){
				for (Assignment assignment : completedAssignments){
					assignment.setCompleted(true);
					assignmentDao.save(assignment);
				}
			}
		}
		
		Collection<Integer> removeTelephoneTimeLogs = CollectionUtils.subtract(oldTelephoneIds, newTelephoneIds);
		if (removeTelephoneTimeLogs.size() > 0){
			for (Integer removeTelephoneTimeLog: removeTelephoneTimeLogs){
				TelephoneTimeLog telephoneTimeLog = telephoneTimeLogDao.findById(removeTelephoneTimeLog);
				telephoneTimeLogDao.delete(telephoneTimeLog);
			}
		}
		
		Collection<Integer> removeFieldworkTimeLogs = CollectionUtils.subtract(oldFieldworkIds, newFieldworkIds);
		if (removeFieldworkTimeLogs.size() > 0){
			for (Integer removeFieldworkTimeLog: removeFieldworkTimeLogs){
				FieldworkTimeLog fieldworkTimeLog = fieldworkTimeLogDao.findById(removeFieldworkTimeLog);
				fieldworkTimeLogDao.delete(fieldworkTimeLog);
			}
		}
		
//		if ("Voilated".equals(status)){
//			String subject = messageSource.getMessage("N00054", null, Locale.ENGLISH);
//			String message = messageSource.getMessage("N00055", 
//					new Object[]{
//							timeLog.getUser().getStaffCode(),
//							timeLog.getUser().getEnglishName(),
//							commonService.formatDate(timeLog.getDate())
//						}, Locale.ENGLISH);
//			
//			User receiver = timeLog.getUser().getSupervisor() != null ? timeLog.getUser().getSupervisor() : timeLog.getUser() ;
//			notifyService.sendNotification(receiver, subject, message, true);
//		}
		
		timeLogDao.flush();
		return 0;
	}
	/**
	 * Delete Time Log 
	 */
	@Transactional
	public boolean deleteTimeLog(List<Integer> ids ) {
		
		
		List<TimeLog> timeLogs = timeLogDao.getTimeLogsByIds(ids);
		if (timeLogs.size() != ids.size()){
			return false;
		}

		for ( TimeLog timeLog : timeLogs){
			if (!timeLog.getStatus().equals(SystemConstant.TIMELOG_STATUS_DRAFT) && !timeLog.getStatus().equals(SystemConstant.TIMELOG_STATUS_REJECTED)) 
				return false;
			Set<TelephoneTimeLog> telephoneTimeLogs = timeLog.getTelephoneTimeLogs();
			for ( TelephoneTimeLog telephoneTimeLog : telephoneTimeLogs){
				telephoneTimeLogDao.delete(telephoneTimeLog);
			}
			Set<FieldworkTimeLog> fieldworkTimeLogs = timeLog.getFieldworkTimeLogs();
			for ( FieldworkTimeLog fieldworkTimeLog : fieldworkTimeLogs) {
				fieldworkTimeLogDao.delete(fieldworkTimeLog);
			}
			timeLogDao.delete(timeLog);
		}
		
		timeLogDao.flush();
		return true;
	}	
	/**
	 * Change Time Log Status
	 */
	@Transactional
	public boolean setTimeLogStatus(Integer userId, List<Integer> ids, String status, String rejectReason, Boolean preApproval ) {
		
		
		List<TimeLog> timeLogs = timeLogDao.getTimeLogsByIds(ids);
		if (timeLogs.size() != ids.size()){
			return false;
		}

		for ( TimeLog timeLog : timeLogs){
			timeLog.setStatus(status);
			if (rejectReason != null) {
				timeLog.setRejectReason(rejectReason);
			}
			if (preApproval != null) {
				timeLog.setPreApproval(preApproval);
			}
			if (status.equals(SystemConstant.TIMELOG_STATUS_APPROVED) || status.equals(SystemConstant.TIMELOG_STATUS_REJECTED)) {
				timeLog.setApprovedBy(userDao.findById(userId));
			}
			
			timeLogDao.save(timeLog);
			if (status.equals(SystemConstant.TIMELOG_STATUS_REJECTED)){
				//N00066 = Please be informed that your time log on {0} has been rejected.
				String code = "N00066" ;
				String message = messageSource.getMessage(code, new Object[]{commonService.formatDate(timeLog.getDate())}, Locale.ENGLISH);				
				notifyService.sendNotification(timeLog.getUser(), message, message, true);
			}
			
		}
		
		timeLogDao.flush();
		return true;
	}
	
	public FieldworkTimeLogModel getFieldworkTimeLogModel(Assignment assignment) {
		FieldworkTimeLogModel fieldworkTimeLogModel = new FieldworkTimeLogModel();
		fieldworkTimeLogModel.setEndTime(commonService.formatTime(new Date()));
		Date referenceMonth = assignment.getSurveyMonth().getReferenceMonth();
		fieldworkTimeLogModel.setReferenceMonth(commonService.formatMonth(referenceMonth));
		QuotationRecord quotationRecord = assignment.getQuotationRecords().iterator().next();
		Integer firmStatus = assignment.getStatus();
		fieldworkTimeLogModel.setSurvey(quotationRecord.getQuotation().getUnit().getPurpose().getSurvey());
		fieldworkTimeLogModel.setActivity("FI");
		String referenceNo = assignment.getReferenceNo();
		fieldworkTimeLogModel.setCaseReferenceNo(referenceNo);
		fieldworkTimeLogModel.setDestination(assignment.getOutlet().getStreetAddress());
		fieldworkTimeLogModel.setAssignmentId(assignment.getAssignmentId());
		
		String enumerationOutcome = "";
		if(firmStatus != null) {
			switch (firmStatus) {
				case 1: //EN
					enumerationOutcome = "C";
					break;
				case 9: //IP
					enumerationOutcome = "P";
					break;
				case 2: //CL
					enumerationOutcome = "D";
					break;
				case 3: //MV
					enumerationOutcome = "M";
					break;
				case 4: //NS
					enumerationOutcome = "D";
					break;
				case 5: //NR
					enumerationOutcome = "D";
					break;
				case 6: //WO
					enumerationOutcome = "D";
					break;
				case 7: //DL
					enumerationOutcome = "L";
					break;
				case 8: //NC
					enumerationOutcome = "N";
					break;
				case 10: //DU
					enumerationOutcome = "D";
					break;
				default: break;
			}
		}
		fieldworkTimeLogModel.setEnumerationOutcome(enumerationOutcome);
		
//		List<TimeLogSelect2Item> timeLogSelect2Items = assignmentDao.getTimeLogSelect2ItemByFieldworkReferenceNo(referenceMonth, referenceNo, 0, 0);
		TimeLogSelect2Item timeLogSelect2Item = assignmentDao.getTimeLogFieldworkReferenceNoDetailById(referenceMonth, referenceNo);
//		if (timeLogSelect2Items.size() > 0) {
//			TimeLogSelect2Item timeLogSelect2Item = timeLogSelect2Items.get(0);
			if (timeLogSelect2Item.getMarketType() == 1) {
				fieldworkTimeLogModel.setMarketQuotationCount(timeLogSelect2Item.getCount().intValue());
				fieldworkTimeLogModel.setMarketTotalQuotation(timeLogSelect2Item.getTotal().intValue());
			} else if (timeLogSelect2Item.getMarketType() == 2) {
				fieldworkTimeLogModel.setNonMarketQuotationCount(timeLogSelect2Item.getCount().intValue());
				fieldworkTimeLogModel.setNonMarketTotalQuotation(timeLogSelect2Item.getTotal().intValue());
			} else {
				fieldworkTimeLogModel.setBuilding(timeLogSelect2Item.getBuilding().intValue());
			}
//		}
		return fieldworkTimeLogModel;
	}
	
	public TelephoneTimeLogModel getTelephoneTimeLogModel(Assignment assignment) {
		TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
		Date referenceMonth = assignment.getSurveyMonth().getReferenceMonth();
		telephoneTimeLogModel.setReferenceMonth(commonService.formatMonth(referenceMonth));
//		QuotationRecord quotationRecord = ((QuotationRecord[])assignment.getQuotationRecords().toArray())[0];
		QuotationRecord quotationRecord = assignment.getQuotationRecords().iterator().next();
		telephoneTimeLogModel.setSurvey(quotationRecord.getQuotation().getUnit().getPurpose().getSurvey());
		String referenceNo = assignment.getReferenceNo();
		telephoneTimeLogModel.setCaseReferenceNo(referenceNo);
		
//		List<TimeLogSelect2Item> timeLogSelect2Items = assignmentDao.getTimeLogSelect2ItemByFieldworkReferenceNo(referenceMonth, referenceNo, 0, 0);
		TimeLogSelect2Item timeLogSelect2Item = assignmentDao.getTimeLogTelephoneReferenceNoDetailById(referenceMonth, referenceNo);
//		if (timeLogSelect2Items.size() > 0) {
//			TimeLogSelect2Item timeLogSelect2Item = timeLogSelect2Items.get(0);
			if (timeLogSelect2Item.getFirmStatus() == 1) {
				telephoneTimeLogModel.setCompletionQuotationCount(timeLogSelect2Item.getCount().intValue());
				telephoneTimeLogModel.setCompletionTotalQuotation(timeLogSelect2Item.getTotal().intValue());
			} else if ((timeLogSelect2Item.getFirmStatus() >= 2 && timeLogSelect2Item.getFirmStatus() <= 6) || (timeLogSelect2Item.getFirmStatus() == 10)) {
				telephoneTimeLogModel.setDeletionQuotationCount(timeLogSelect2Item.getCount().intValue());
				telephoneTimeLogModel.setDeletionTotalQuotation(timeLogSelect2Item.getTotal().intValue());
			}
//		}
		return telephoneTimeLogModel;
	}
	
	public Integer getTimeLogIdByUserIdAndDate(Integer userId, Date date) {
		return timeLogDao.getTimeLogIdByUserIdAndDate(userId, date);
	}
	
	public TimeLog getTimeLogByUserIdAndDate(Integer userId, Date date) {
		return timeLogDao.getTimeLogByUserIdAndDate(userId, date);
	}
	
	public List<String> getCreatedTimeLogDate(Integer userId) {
		return timeLogDao.getTimeLogDateByUserId(userId);
	}
	
	public Integer getVerifiedCount(Date referenceMonth, String referenceNo) {
		return assignmentDao.getVerifiedQuotationCount(referenceMonth, referenceNo);
	}
	
	public ItineraryPlanEditModel getItineraryPlanEditModel(TimeLogModel timeLogModel) throws ParseException {
		ItineraryPlanEditModel visited = new ItineraryPlanEditModel();
		List<MajorLocationModel> majorLocations = new ArrayList<MajorLocationModel>();
		//majorLocations.get(0).getItineraryPlanOutletModels().
		List<FieldworkTimeLogModel> fieldworkTimeLogModels = timeLogModel.getFieldworkTimeLogs();
		List<String> referenceNo = new ArrayList<String>();
		for (FieldworkTimeLogModel fieldworkTimeLogModel : fieldworkTimeLogModels) {
			if (!StringUtils.isEmpty(fieldworkTimeLogModel.getCaseReferenceNo())){
				referenceNo.add(fieldworkTimeLogModel.getCaseReferenceNo());				
			}
		}
		
		if (referenceNo.size() > 0){
			List<AssignmentReferenceNoModel> models = assignmentDao.getAssignmentsByReferenceNo(referenceNo);
			Map<String, AssignmentReferenceNoModel> map = new HashMap<String, AssignmentReferenceNoModel>();
			for (AssignmentReferenceNoModel model : models) {
				map.put(model.getReferenceNo(), model);				
			}
			

			Integer majorLocationSeq = 1;
			for (FieldworkTimeLogModel fieldworkTimeLogModel : fieldworkTimeLogModels) {
				if (StringUtils.isEmpty(fieldworkTimeLogModel.getCaseReferenceNo()))
					continue;
				if (!map.containsKey(fieldworkTimeLogModel.getCaseReferenceNo())){
					continue;
				}
				
				//Assignment assignment = assignmentDao.findById(fieldworkTimeLogModel.getAssignmentId());
//				List<Outlet> outlets = assignmentDao.getOutletByAssignmentReferenceNo(commonService.getMonth(fieldworkTimeLogModel.getReferenceMonth()), fieldworkTimeLogModel.getCaseReferenceNo());
//				if (outlets == null || outlets.size() == 0 )
//					continue;
				
				AssignmentReferenceNoModel ref = map.get(fieldworkTimeLogModel.getCaseReferenceNo());	
				if (StringUtils.isEmpty(ref.getLatitude()) || StringUtils.isEmpty(ref.getLongitude())){
					continue;
				}
				
				//Outlet outlet = outlets.get(0);
				List<ItineraryPlanOutletModel> itineraryPlanOutletModels = new ArrayList<ItineraryPlanOutletModel>();
				ItineraryPlanOutletModel itineraryPlanOutletModel = new ItineraryPlanOutletModel();
				BeanUtils.copyProperties(ref, itineraryPlanOutletModel);
				itineraryPlanOutletModel.setItineraryPlanOutletId(1);
				itineraryPlanOutletModel.setSequence(1);
//				itineraryPlanOutletModel.setOutletId(outlet.getId());
//				itineraryPlanOutletModel.setFirm(outlet.getName());
//				itineraryPlanOutletModel.setMarketName(outlet.getMarketName());
//				itineraryPlanOutletModel.setTpu(outlet.getTpu().getCode());
//				itineraryPlanOutletModel.setAddress(outlet.getStreetAddress());
//				itineraryPlanOutletModel.setDetailAddress(outlet.getDetailAddress());
//				itineraryPlanOutletModel.setLatitude(outlet.getLatitude());
//				itineraryPlanOutletModel.setLongitude(outlet.getLongitude());
				itineraryPlanOutletModels.add(itineraryPlanOutletModel);
				
				MajorLocationModel majorLocationModel = new MajorLocationModel();
				majorLocationModel.setMajorLocationId(majorLocationSeq);
				majorLocationModel.setSequence(majorLocationSeq);
				majorLocationModel.setIsFreeEntryTask(false);
				majorLocationModel.setIsNewRecruitmentTask(false);
				majorLocationModel.setTaskName(itineraryPlanOutletModel.getMarketName());
				majorLocationModel.setMarketName(itineraryPlanOutletModel.getMarketName());
				majorLocationModel.setTpu(itineraryPlanOutletModel.getTpu());
				majorLocationModel.setItineraryPlanOutletModels(itineraryPlanOutletModels);
				majorLocations.add(majorLocationModel);
				majorLocationSeq++;
			}
		}
		
		
		
		visited.setMajorLocations(majorLocations);
		return visited;		
	}
	@Transactional
	public void addOTCalendarEvent(TimeLogModel timeLogModel) throws ParseException {

		if (timeLogModel.getIsClaimOT() == null || !timeLogModel.getIsClaimOT()){
			return;
		}
		CalendarEvent calendarEvent = calendarEventService.findOrCreate(timeLogModel.getUserId(), SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT, timeLogModel.getDate());
		User user = userDao.findById(timeLogModel.getUserId());
		Integer oldDurationMinutes = 0;
		if (calendarEvent.getDuration() != null) {
			String oldDuration = commonService.formatTime(calendarEvent.getDuration());
			oldDurationMinutes = Integer.parseInt(oldDuration.split(":")[0]) * 60 + Integer.parseInt(oldDuration.split(":")[1]);
		}
		Double newManDay = 0.0;
		
		String newDuration = timeLogModel.getOtClaimed();
		if (StringUtils.isEmpty(newDuration)){
			return;
		}
		
		Integer newDurationMinutes = Integer.parseInt(newDuration.split(":")[0]) * 60 + Integer.parseInt(newDuration.split(":")[1]);
		newManDay = newDurationMinutes / 480.0;
		
		calendarEvent.setActivityType(SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT);
		calendarEvent.setDuration(commonService.getTime(newDuration));
		calendarEvent.setManDay(newManDay);
		calendarEvent.setUser(user);
		calendarEvent.setEventDate(commonService.getDate(timeLogModel.getDate()));
		if(timeLogModel.getFieldworkTimeLogs()!=null && timeLogModel.getFieldworkTimeLogs().size()>0){
			FieldworkTimeLogModel lastTimeLogModel = timeLogModel.getFieldworkTimeLogs().get(timeLogModel.getFieldworkTimeLogs().size() - 1);
			Date startTime = null;
			if (timeLogModel.getWorkingSessionId() != null && timeLogModel.getWorkingSessionId() != 0){
				WorkingSessionSetting workingSessionSetting = workingSessionSettingDao.findById(timeLogModel.getWorkingSessionId());
				if (commonService.getTime(lastTimeLogModel.getStartTime()).after(workingSessionSetting.getToTime())){
					startTime = workingSessionSetting.getToTime();
				} else {
					startTime = DateUtils.addMinutes(workingSessionSetting.getFromTime(), (-1 * newDurationMinutes));
				}
			} else if (timeLogModel.getOtherWorkingSessionTo()!=null){
				if (commonService.getTime(lastTimeLogModel.getStartTime()).after(commonService.getTime(timeLogModel.getOtherWorkingSessionTo()))){
					startTime = commonService.getTime(timeLogModel.getOtherWorkingSessionTo());
				} else {
					startTime = DateUtils.addMinutes(commonService.getTime(timeLogModel.getOtherWorkingSessionFrom()), (-1 * newDurationMinutes));
				}
			} else {
				startTime = commonService.getTime(lastTimeLogModel.getStartTime());
			}
			
			Date endTime = DateUtils.addMinutes(startTime, newDurationMinutes);
			calendarEvent.setStartTime(startTime);
			calendarEvent.setEndTime(endTime);
			calendarEvent.setSession(commonService.getSession(commonService.formatTime(startTime)));
		} else if (timeLogModel.getWorkingSessionId() != null && timeLogModel.getWorkingSessionId() != 0){
			WorkingSessionSetting workingSessionSetting = workingSessionSettingDao.findById(timeLogModel.getWorkingSessionId());
			Date startTime = workingSessionSetting.getToTime();
			Date endTime = DateUtils.addMinutes(startTime, newDurationMinutes);
			calendarEvent.setStartTime(startTime);
			calendarEvent.setEndTime(endTime);
			calendarEvent.setSession(commonService.getSession(commonService.formatTime(startTime)));
		} else {
			Date startTime = commonService.getTime(timeLogModel.getOtherWorkingSessionTo());
			Date endTime = DateUtils.addMinutes(startTime, newDurationMinutes);
			calendarEvent.setStartTime(startTime);
			calendarEvent.setEndTime(endTime);
			calendarEvent.setSession(commonService.getSession(commonService.formatTime(startTime)));
		}
		
		calendarEventDao.save(calendarEvent);
		
		user.setAccumulatedOT(user.getAccumulatedOT() - oldDurationMinutes + newDurationMinutes);		
		userDao.save(user);
		
		calendarEventDao.flush();
		userDao.flush();
	}
	
	/**
	 * Data Sync
	 */
	@Transactional
	public List<TimeLogSyncData> syncTimeLogData(List<TimeLogSyncData> timeLogs
			, Date lastSyncTime, Boolean dataReturn, Integer[] timeLogIds, Date[] dates){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> allTimeLogIds = new ArrayList<Integer>();
		if(timeLogIds!=null && timeLogIds.length>0){
			allTimeLogIds.addAll(Arrays.asList(timeLogIds));
		}
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if(timeLogs!=null && timeLogs.size()>0){
			for(TimeLogSyncData timeLog : timeLogs){
				if ("D".equals(timeLog.getLocalDbRecordStatus())){
					continue;
				}
				TimeLog entity = null;
				if(timeLog.getTimeLogId()==null){
//					continue;
					entity = timeLogDao.getTimeLogByUserIdAndDate(timeLog.getUserId(), DateUtils.truncate(timeLog.getDate(), Calendar.DATE));
					if (entity == null){
						entity = new TimeLog();						
					} else {
						if("Submitted".equals(entity.getStatus()) || "Approved".equals(entity.getStatus()) || "Voilated".equals(entity.getStatus())){
							unUpdateIds.add(entity.getTimeLogId());
							table.put(entity.getTimeLogId(), timeLog.getLocalId());
							continue;
						}
						if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(timeLog.getModifiedDate())){
							unUpdateIds.add(entity.getTimeLogId());
							table.put(entity.getTimeLogId(), timeLog.getLocalId());
							continue;
						}
					}
				} else {
					entity = timeLogDao.findById(timeLog.getTimeLogId());
					if("Submitted".equals(entity.getStatus()) || "Approved".equals(entity.getStatus()) || "Voilated".equals(entity.getStatus())){
						unUpdateIds.add(entity.getTimeLogId());
						table.put(entity.getTimeLogId(), timeLog.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(timeLog.getModifiedDate())){
						unUpdateIds.add(entity.getTimeLogId());
						table.put(entity.getTimeLogId(), timeLog.getLocalId());
						continue;
					}
				}
				
				// property name to ignore => to prevent overwrite the Time Log Primary Key
				BeanUtils.copyProperties(timeLog, entity, "timeLogId");
				
				Date otherWorkingSessionFrom = null;
				if(!StringUtils.isEmpty(timeLog.getOtherWorkingSessionFrom())){
					try{
						otherWorkingSessionFrom = commonService.getTime(timeLog.getOtherWorkingSessionFrom());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				entity.setOtherWorkingSessionFrom(otherWorkingSessionFrom);
				
				Date otherWorkingSessionTo = null;
				if(!StringUtils.isEmpty(timeLog.getOtherWorkingSessionTo())){
					try{
						otherWorkingSessionTo = commonService.getTime(timeLog.getOtherWorkingSessionTo());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setOtherWorkingSessionTo(otherWorkingSessionTo);
				
				Date otClaimed = null;
				if(!StringUtils.isEmpty(timeLog.getOtClaimed())){
					try{
						otClaimed = commonService.getTime(timeLog.getOtClaimed());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setOtClaimed(otClaimed);
				
				Date timeoffTaken = null;
				if(!StringUtils.isEmpty(timeLog.getTimeoffTaken())){
					try{
						timeoffTaken = commonService.getTime(timeLog.getTimeoffTaken());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setTimeoffTaken(timeoffTaken);
				

				if(timeLog.getWorkingSessionSettingId()!=null){

					WorkingSessionSetting workingSessionSetting = workingSessionSettingDao.findById(timeLog.getWorkingSessionSettingId());
					if (workingSessionSetting != null){
						entity.setSetting(workingSessionSetting);
					}
				}				
				

				if(timeLog.getUserId()!=null){

					User user = userDao.findById(timeLog.getUserId());
					if (user != null){
						entity.setUser(user);
					}
				}
				
				if(timeLog.getApprovedBy()!=null && !timeLog.getStatus().equals(SystemConstant.TIMELOG_STATUS_REJECTED)){
					User approvedBy = userDao.findById(timeLog.getApprovedBy());
					entity.setApprovedBy(approvedBy);
				}
				entity.setByPassModifiedDate(true);
				timeLogDao.save(entity);
				allTimeLogIds.add(entity.getTimeLogId());
				table.put(entity.getTimeLogId(), timeLog.getLocalId());
			}
			timeLogDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
			List<TimeLogSyncData> updatedData = new ArrayList<TimeLogSyncData>();
			if(allTimeLogIds!=null && allTimeLogIds.size()>0){
				updatedData.addAll(syncTimeLogByIdRecursiveQuery(allTimeLogIds, lastSyncTime));
			}
			if(dates!=null && dates.length>0 && detail.getUserId()!=null){
				updatedData.addAll(syncTimeLogByDateRecursiveQuery(Arrays.asList(dates), lastSyncTime, detail.getUserId()));
			}
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncTimeLogByIdRecursiveQuery(unUpdateIds, null));
			}
			
			List<TimeLogSyncData> unique = new ArrayList<TimeLogSyncData>(new HashSet<TimeLogSyncData>(updatedData));
			
			for(TimeLogSyncData data : unique){
				if(table.containsKey(data.getTimeLogId())){
					data.setLocalId(table.get(data.getTimeLogId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<TimeLogSyncData>();
	}
	
	@Transactional
	public List<FieldworkTimeLogSyncData> syncFieldworkTimeLogData(List<FieldworkTimeLogSyncData> fieldworkTimeLogs

			, Date lastSyncTime, Boolean dataReturn, Integer[] timeLogIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> unUpdateIds = new ArrayList<Integer>(); //For import fieldworkTimeLogs id
		
		if(fieldworkTimeLogs!=null && fieldworkTimeLogs.size()>0){
			for(FieldworkTimeLogSyncData fieldworkTimeLog : fieldworkTimeLogs){
				FieldworkTimeLog entity = null;

				if(fieldworkTimeLog.getFieldworkTimeLogId()==null){
					if ("D".equals(fieldworkTimeLog.getLocalDbRecordStatus())){
						continue;
					}
					entity = new FieldworkTimeLog();
				} else {

					entity = fieldworkTimeLogDao.findById(fieldworkTimeLog.getFieldworkTimeLogId());
					if("Submitted".equals(entity.getTimeLog().getStatus()) || "Approved".equals(entity.getTimeLog().getStatus()) || "Voilated".equals(entity.getTimeLog().getStatus())){
						unUpdateIds.add(entity.getFieldworkTimeLogId());
						table.put(entity.getFieldworkTimeLogId(), fieldworkTimeLog.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(fieldworkTimeLog.getModifiedDate())){
						unUpdateIds.add(entity.getFieldworkTimeLogId());
						table.put(entity.getFieldworkTimeLogId(), fieldworkTimeLog.getLocalId());
						continue;
					}
					if (entity!= null && "D".equals(fieldworkTimeLog.getLocalDbRecordStatus())){
						fieldworkTimeLogDao.delete(entity);
						continue;
					}
					else if ("D".equals(fieldworkTimeLog.getLocalDbRecordStatus())){						
						continue;
					}
					
					
				}
				
				BeanUtils.copyProperties(fieldworkTimeLog, entity);
				
				Date startTime = null;
				if(!StringUtils.isEmpty(fieldworkTimeLog.getStartTime())){
					try{
						startTime = commonService.getTime(fieldworkTimeLog.getStartTime());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				entity.setStartTime(startTime);
					
				Date endTime = null;
				if(!StringUtils.isEmpty(fieldworkTimeLog.getEndTime())){
					try{
						endTime = commonService.getTime(fieldworkTimeLog.getEndTime());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				entity.setEndTime(endTime);
				
//				if(fieldworkTimeLog.getAssignmentId()!=null){
//					Assignment assignment = assignmentDao.findById(fieldworkTimeLog.getAssignmentId());
//					if (assignment != null){
//						entity.setAssignment(assignment);						
//					}
//				}
				
				if(fieldworkTimeLog.getTimeLogId()!=null){
					TimeLog timeLog = timeLogDao.findById(fieldworkTimeLog.getTimeLogId());
					if (timeLog != null){
						entity.setTimeLog(timeLog);
					}
				}
				entity.setByPassModifiedDate(true);
				fieldworkTimeLogDao.save(entity); 
				unUpdateIds.add(entity.getFieldworkTimeLogId());
				table.put(entity.getFieldworkTimeLogId(), fieldworkTimeLog.getLocalId());
			}
	
		}
		
		if(dataReturn!=null && dataReturn){
			List<FieldworkTimeLogSyncData> updatedData = new ArrayList<FieldworkTimeLogSyncData>();
			if(timeLogIds!=null && timeLogIds.length>0){
				updatedData.addAll(syncFieldworkByTimeLogRecursiveQuery(Arrays.asList(timeLogIds), lastSyncTime));
			}
			
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncFieldworkByIdsRecursiveQuery(unUpdateIds));
			}
			
			List<FieldworkTimeLogSyncData> unqiue = new ArrayList<FieldworkTimeLogSyncData>(
					new HashSet<FieldworkTimeLogSyncData>(updatedData));
			for(FieldworkTimeLogSyncData data : unqiue){
				if(table.containsKey(data.getFieldworkTimeLogId())){
					data.setLocalId(table.get(data.getFieldworkTimeLogId()));
				}
			}
			return unqiue;
		}
		
		return new ArrayList<FieldworkTimeLogSyncData>();
	}
	
	@Transactional
	public List<TelephoneTimeLogSyncData> syncTelephoneTimeLogData(List<TelephoneTimeLogSyncData> telephoneTimeLogs
			, Date lastSyncTime, Boolean dataReturn, Integer[] timeLogIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();
		if(telephoneTimeLogs!=null && telephoneTimeLogs.size()>0){
			for(TelephoneTimeLogSyncData telephoneTimeLog : telephoneTimeLogs){
				TelephoneTimeLog entity = null;
			
				if(telephoneTimeLog.getTelephoneTimeLogId()==null){
					if ("D".equals(telephoneTimeLog.getLocalDbRecordStatus())){
						continue;
					}
					entity = new TelephoneTimeLog();
				} else {
					entity = telephoneTimeLogDao.findById(telephoneTimeLog.getTelephoneTimeLogId());
					if("Submitted".equals(entity.getTimeLog().getStatus()) || "Approved".equals(entity.getTimeLog().getStatus()) || "Voilated".equals(entity.getTimeLog().getStatus())){
						updateIds.add(entity.getTelephoneTimeLogId());
						table.put(entity.getTelephoneTimeLogId(), telephoneTimeLog.getLocalId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(telephoneTimeLog.getModifiedDate())){
						updateIds.add(entity.getTelephoneTimeLogId());
						table.put(entity.getTelephoneTimeLogId(), telephoneTimeLog.getLocalId());
						continue;
					}
					if (entity!= null && "D".equals(telephoneTimeLog.getLocalDbRecordStatus())){
						telephoneTimeLogDao.delete(entity);
						continue;
					}
					else if ("D".equals(telephoneTimeLog.getLocalDbRecordStatus())){						
						continue;
					}
					
				}
				
				
				BeanUtils.copyProperties(telephoneTimeLog, entity);
				
				if(telephoneTimeLog.getAssignmentId()!=null){
					Assignment assignment = assignmentDao.findById(telephoneTimeLog.getAssignmentId());
					if (assignment != null){
						entity.setAssignment(assignment);
					}
				}
				
				if(telephoneTimeLog.getTimeLogId()!=null){
					TimeLog timeLog = timeLogDao.findById(telephoneTimeLog.getTimeLogId());
					if (timeLog != null){
						entity.setTimeLog(timeLog);
					}
				}
				entity.setByPassModifiedDate(true);
				telephoneTimeLogDao.save(entity); 
				updateIds.add(entity.getTelephoneTimeLogId());
				table.put(entity.getTelephoneTimeLogId(), telephoneTimeLog.getLocalId());
			}
			
		}
		
		if(dataReturn!=null && dataReturn){
			List<TelephoneTimeLogSyncData> updatedData = new ArrayList<TelephoneTimeLogSyncData>();
			if(timeLogIds!=null && timeLogIds.length>0){
				updatedData.addAll(syncTelephoneByTimeLogRecursiveQuery(Arrays.asList(timeLogIds), lastSyncTime));
			}
			
			if(updateIds!=null && updateIds.size()>0){
				updatedData.addAll(syncTelephoneByIdRecursiveQuery(updateIds));
			}
			
			List<TelephoneTimeLogSyncData> unique = new ArrayList<TelephoneTimeLogSyncData>(new HashSet<TelephoneTimeLogSyncData>(updatedData));
			
			for(TelephoneTimeLogSyncData data : unique){
				if(table.containsKey(data.getTelephoneTimeLogId())){
					data.setLocalId(table.get(data.getTelephoneTimeLogId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<TelephoneTimeLogSyncData>();
	}
	
	public void setUserCode(TimeLogModel timeLogModel) {
		User user = userDao.findById(timeLogModel.getUserId());
		timeLogModel.setUserCode(user.getStaffCode() + " - " + user.getChineseName());
	}

	/** 
	 * query itinerary checking table count 
	 */
	public Long queryItineraryCheckingTableCount(){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> supervisorIds = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & 4) == 4){
			supervisorIds.add(userId);		
			supervisorIds.addAll(detail.getActedUsers());
		}
		
		if((detail.getAuthorityLevel() & 2) == 2) {
			supervisorIds.add(userId);
			supervisorIds.addAll(detail.getActedUsers());
			List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(supervisorIds);
			supervisorIds = actedUserSubordinateIds;
		}
		
		return timeLogDao.countItineraryCheckingTableList(supervisorIds.toArray(new Integer[0]),"");
	}
	
	public List<FieldworkTimeLogSyncData> syncFieldworkByTimeLogRecursiveQuery(List<Integer> timeLogIds, Date lastSyncTime){
		List<FieldworkTimeLogSyncData> entities = new ArrayList<FieldworkTimeLogSyncData>();
		if(timeLogIds.size()>2000){
			List<Integer> ids = timeLogIds.subList(0, 2000);
			entities.addAll(syncFieldworkByTimeLogRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = timeLogIds.subList(2000, timeLogIds.size());
			entities.addAll(syncFieldworkByTimeLogRecursiveQuery(remainIds, lastSyncTime));
		} else if (timeLogIds.size()>0){
			return fieldworkTimeLogDao.getUpdatedFielworkTimeLog(lastSyncTime, timeLogIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public List<FieldworkTimeLogSyncData> syncFieldworkByIdsRecursiveQuery(List<Integer> fieldworkTimeLogIds){
		List<FieldworkTimeLogSyncData> entities = new ArrayList<FieldworkTimeLogSyncData>();
		if(fieldworkTimeLogIds.size()>2000){
			List<Integer> ids = fieldworkTimeLogIds.subList(0, 2000);
			entities.addAll(syncFieldworkByIdsRecursiveQuery(ids));
			
			List<Integer> remainIds = fieldworkTimeLogIds.subList(2000, fieldworkTimeLogIds.size());
			entities.addAll(syncFieldworkByIdsRecursiveQuery(remainIds));
		} else if (fieldworkTimeLogIds.size()>0){
			return fieldworkTimeLogDao.getUpdatedFielworkTimeLog(null, null, fieldworkTimeLogIds);
		}
		
		return entities;
	}
	
	public List<TelephoneTimeLogSyncData> syncTelephoneByTimeLogRecursiveQuery(List<Integer> timeLogIds, Date lastSyncTime){
		List<TelephoneTimeLogSyncData> entities = new ArrayList<TelephoneTimeLogSyncData>();
		if(timeLogIds.size()>2000){
			List<Integer> ids = timeLogIds.subList(0, 2000);
			entities.addAll(syncTelephoneByTimeLogRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = timeLogIds.subList(2000, timeLogIds.size());
			entities.addAll(syncTelephoneByTimeLogRecursiveQuery(remainIds, lastSyncTime));
		} else if (timeLogIds.size()>0){
			return telephoneTimeLogDao.getUpdatedTelephoneTimeLog(lastSyncTime, timeLogIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public List<TelephoneTimeLogSyncData> syncTelephoneByIdRecursiveQuery(List<Integer> telephoneTimeLogIds){
		List<TelephoneTimeLogSyncData> entities = new ArrayList<TelephoneTimeLogSyncData>();
		if(telephoneTimeLogIds.size()>2000){
			List<Integer> ids = telephoneTimeLogIds.subList(0, 2000);
			entities.addAll(syncTelephoneByIdRecursiveQuery(ids));
			
			List<Integer> remainIds = telephoneTimeLogIds.subList(2000, telephoneTimeLogIds.size());
			entities.addAll(syncTelephoneByIdRecursiveQuery(remainIds));
		} else if (telephoneTimeLogIds.size()>0){
			return telephoneTimeLogDao.getUpdatedTelephoneTimeLog(null, null, telephoneTimeLogIds.toArray(new Integer[0]));
		}
		
		return entities;
	}
	
	public List<TimeLogSyncData> syncTimeLogByIdRecursiveQuery(List<Integer> timeLogIds, Date lastSyncTime){
		List<TimeLogSyncData> entities = new ArrayList<TimeLogSyncData>();
		if(timeLogIds.size()>2000){
			List<Integer> ids = timeLogIds.subList(0, 2000);
			entities.addAll(syncTimeLogByIdRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = timeLogIds.subList(2000, timeLogIds.size());
			entities.addAll(syncTimeLogByIdRecursiveQuery(remainIds, lastSyncTime));
		} else if (timeLogIds.size()>0){
			return timeLogDao.getUpdatedTimeLog(lastSyncTime, timeLogIds.toArray(new Integer[0]), null, null);
		}
		
		return entities;
	}
	
	public List<TimeLogSyncData> syncTimeLogByDateRecursiveQuery(List<Date> dates, Date lastSyncTime, Integer userId){
		List<TimeLogSyncData> entities = new ArrayList<TimeLogSyncData>();
		if(dates.size()>2000){
			List<Date> ids = dates.subList(0, 2000);
			entities.addAll(syncTimeLogByDateRecursiveQuery(ids, lastSyncTime, userId));
			
			List<Date> remainIds = dates.subList(2000, dates.size());
			entities.addAll(syncTimeLogByDateRecursiveQuery(remainIds, lastSyncTime, userId));
		} else if (dates.size()>0){
			return timeLogDao.getUpdatedTimeLog(lastSyncTime, null, dates.toArray(new Date[0]), userId);
		}
		
		return entities;
	}
	
	public void resetTimeLogStatus(int id) {
		TimeLog timeLog = timeLogDao.findById(id);
		
		timeLog.setStatus(SystemConstant.TIMELOG_STATUS_DRAFT);
		timeLogDao.save(timeLog);
		timeLogDao.flush();
	}
}