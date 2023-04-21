package capi.service.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.ReportTaskDao;
import capi.dal.TimeLogDao;
import capi.dal.UserDao;
import capi.entity.CalendarEvent;
import capi.entity.FieldworkTimeLog;
import capi.entity.ReportTask;
import capi.entity.TelephoneTimeLog;
import capi.entity.TimeLog;
import capi.entity.User;
import capi.entity.WorkingSessionSetting;
import capi.model.SystemConstant;
import capi.model.report.InformationTimeLogFormCriteria;
import capi.model.timeLogManagement.FieldworkTimeLogModel;
import capi.model.timeLogManagement.TelephoneTimeLogModel;
import capi.model.timeLogManagement.TimeLogModel;
import capi.service.CommonService;
import net.sf.jasperreports.engine.JasperPrint;

@Service("InformationTimeLogFormService")
public class InformationTimeLogFormService extends ReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TimeLogDao timeLogDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		InformationTimeLogFormCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), InformationTimeLogFormCriteria.class);
		List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>();	
		
		Date timeLogDate = commonService.getDate(criteria.getTimeLogDate());
		Integer authorityLevel = criteria.getAuthorityLevel();
		List<Integer> fieldOfficerIds = new ArrayList<Integer>();
		fieldOfficerIds.add(criteria.getUserId());
		
		if ((authorityLevel & 2) > 0){
			if (criteria.getFieldOfficerId()!=null && criteria.getFieldOfficerId().size()>0){
				fieldOfficerIds = criteria.getFieldOfficerId();
			} else {
				fieldOfficerIds = userDao.getReportLookupTableSelectAll(null, 20, null, null, null, timeLogDate, null, null, fieldOfficerIds.toArray(new Integer[0]), null);
			}
		} else if ((authorityLevel & 4) > 0){
			if (criteria.getFieldOfficerId()!=null && criteria.getFieldOfficerId().size()>0){
				fieldOfficerIds = criteria.getFieldOfficerId();
			} else {
				fieldOfficerIds = userDao.getReportLookupTableSelectAll(null, 16, null, null, null, timeLogDate, null, null, fieldOfficerIds.toArray(new Integer[0]), null);
			}
		}
		
		for (Integer fieldOfficerId : fieldOfficerIds) {
			TimeLog timeLog = timeLogDao.getTimeLogByUserIdAndDate(fieldOfficerId, timeLogDate);
			
			if (timeLog == null) {
				continue;
			}
			
			TimeLogModel timeLogModel = new TimeLogModel();
			User officer = timeLog.getUser();
			
			List<CalendarEvent> eventRecords = calendarEventDao.getTimeoffOvertimeEventsByUserId(timeLogDate,fieldOfficerId);
			if(eventRecords != null && eventRecords.size() > 0){
				for(CalendarEvent event: eventRecords){
					Integer hr = null;
					Integer min = null;
					if (event.getDuration() != null){
						String duration = commonService.formatTime(event.getDuration());
						hr = Integer.parseInt(duration.split(":")[0]);
						min = Integer.parseInt(duration.split(":")[1]);
					}
					if (event.getActivityType() ==  1) {
						timeLogModel.setOtClaimedStart(commonService.formatTime(event.getStartTime()));
						timeLogModel.setOtClaimedEnd(commonService.formatTime(event.getEndTime()));
						timeLogModel.setOtClaimedHr(hr == null ? "0" : hr.toString());
						timeLogModel.setOtClaimedMin(min == null ? "0" : min.toString());
					} else if (event.getActivityType() ==  2) {
						timeLogModel.setTimeoffTakenStart(commonService.formatTime(event.getStartTime()));
						timeLogModel.setTimeoffTakenEnd(commonService.formatTime(event.getEndTime()));
						timeLogModel.setTimeoffTakenHr(hr == null ? "0" : hr.toString());
						timeLogModel.setTimeoffTakenMin(min == null ? "0" : min.toString());
					}
				}
			}
			
			timeLogModel.setDate(commonService.formatReportDate(timeLog.getDate()));
			timeLogModel.setIsOtherWorkingSession(timeLog.isOtherWorkingSession());
			timeLogModel.setIsTrainingAM(timeLog.isTrainingAM());
			timeLogModel.setIsTrainingPM(timeLog.isTrainingPM());
			timeLogModel.setIsVLSLAM(timeLog.isVLSLAM());
			timeLogModel.setIsVLSLPM(timeLog.isVLSLPM());
			
			Hashtable<String, Object> parameters = new Hashtable<String, Object>();
			Hashtable<String, Object> parametersAM = new Hashtable<String, Object>();
			Hashtable<String, Object> parametersPM = new Hashtable<String, Object>();
			Hashtable<String, Object> parametersFieldwork = new Hashtable<String, Object>();
	
			Set<TelephoneTimeLog> telephoneTimeLogs = timeLog.getTelephoneTimeLogs();
			
			Set<TelephoneTimeLogModel> telephoneTimeLogModelsAM = new HashSet<TelephoneTimeLogModel>();
			Set<TelephoneTimeLogModel> telephoneTimeLogModelsPM = new HashSet<TelephoneTimeLogModel>();
			Map<String, Set<String>> visitCount = new HashMap<>();			

			Integer cpi_v = 0;
			Integer ghs_v = 0;
			Integer b_v	= 0;
			
			Integer tel_cpi_c = 0;
			Integer tel_cpi_t = 0;
			Integer tel_ghs_c = 0;
			Integer tel_ghs_t = 0;
			Integer tel_b_c = 0;
			Integer tel_b_t = 0;
			Integer ROW_COUNT = 2;
			
			for(TelephoneTimeLog telephoneTimeLog : telephoneTimeLogs ) {
				TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
				BeanUtils.copyProperties(telephoneTimeLogModel, telephoneTimeLog);
				
				if (telephoneTimeLog.getStatus().equals("C")) {
					if (telephoneTimeLog.getSurvey().equals("MRPS")) {
						tel_cpi_c += telephoneTimeLog.getQuotationCount();
						tel_cpi_t += telephoneTimeLog.getTotalQuotation();
					} else if (telephoneTimeLog.getSurvey().equals("GHS")) {
						tel_ghs_c += telephoneTimeLog.getQuotationCount();
						tel_ghs_t += telephoneTimeLog.getTotalQuotation();
					} else if (telephoneTimeLog.getSurvey().equals("BMWPS")) {
						tel_b_c += telephoneTimeLog.getQuotationCount();
						tel_b_t += telephoneTimeLog.getTotalQuotation();
					}
				}
				
				if (telephoneTimeLog.getStatus().equals("C")) {
					telephoneTimeLogModel.setCompletionQuotationCount(telephoneTimeLog.getQuotationCount());
					telephoneTimeLogModel.setCompletionTotalQuotation(telephoneTimeLog.getTotalQuotation());
				} else if (telephoneTimeLog.getStatus().equals("D")) {
					telephoneTimeLogModel.setDeletionQuotationCount(telephoneTimeLog.getQuotationCount());
					telephoneTimeLogModel.setDeletionTotalQuotation(telephoneTimeLog.getTotalQuotation());
				}
				
				if (telephoneTimeLog.getSession().equals("A")) {
					telephoneTimeLogModelsAM.add(telephoneTimeLogModel);
				} else {
					telephoneTimeLogModelsPM.add(telephoneTimeLogModel);
				}
				
				/*
				 * Count record of telephone enumeration Assignment
				 * An assignment = Survey + Case Reference No.
				 */
				if (telephoneTimeLog.getSurvey() != null && telephoneTimeLog.getCaseReferenceNo() != null){
					String text = telephoneTimeLog.getSurvey().trim() + telephoneTimeLog.getCaseReferenceNo().trim();
					Set<String> countSet = visitCount.get(telephoneTimeLog.getSurvey()); 
					if (countSet != null){
						countSet.add(text);
					} else {
						countSet = new HashSet<>();
						countSet.add(text);
						visitCount.put(telephoneTimeLog.getSurvey(), countSet);
					}
				}
			}

			//List<TelephoneTimeLog> telephoneTimeLogs = new ArrayList<>(timeLog.getTelephoneTimeLogs());
			List<TelephoneTimeLogModel> sortedTelephoneTimeLogModelsAM = new ArrayList<>(telephoneTimeLogModelsAM);
			Collections.sort(sortedTelephoneTimeLogModelsAM, new TelephoneTimeLogModel());
			
			List<TelephoneTimeLogModel> sortedTelephoneTimeLogModelsPM = new ArrayList<>(telephoneTimeLogModelsPM);
			Collections.sort(sortedTelephoneTimeLogModelsPM, new TelephoneTimeLogModel());
			
			if (sortedTelephoneTimeLogModelsAM.size() > sortedTelephoneTimeLogModelsPM.size()){
				List<TelephoneTimeLogModel> layoutRecord = new ArrayList<>();
				for(int i = sortedTelephoneTimeLogModelsPM.size(); i < sortedTelephoneTimeLogModelsAM.size(); i++){
					TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
					layoutRecord.add(telephoneTimeLogModel);
				}
				sortedTelephoneTimeLogModelsPM.addAll(layoutRecord);
			} else if (sortedTelephoneTimeLogModelsAM.size() < sortedTelephoneTimeLogModelsPM.size()){
				List<TelephoneTimeLogModel> layoutRecord = new ArrayList<>();
				for(int i = sortedTelephoneTimeLogModelsAM.size(); i < sortedTelephoneTimeLogModelsPM.size(); i++){
					TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
					layoutRecord.add(telephoneTimeLogModel);
				}
				sortedTelephoneTimeLogModelsAM.addAll(layoutRecord);
			}

			if (sortedTelephoneTimeLogModelsAM.size() < ROW_COUNT){
				List<TelephoneTimeLogModel> layoutRecord = new ArrayList<>();
				for(int i = sortedTelephoneTimeLogModelsAM.size(); i < ROW_COUNT; i++){
					TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
					layoutRecord.add(telephoneTimeLogModel);
				}
				sortedTelephoneTimeLogModelsAM.addAll(layoutRecord);
			}
			if (sortedTelephoneTimeLogModelsPM.size() < ROW_COUNT){
				List<TelephoneTimeLogModel> layoutRecord = new ArrayList<>();
				for(int i = sortedTelephoneTimeLogModelsPM.size(); i < ROW_COUNT; i++){
					TelephoneTimeLogModel telephoneTimeLogModel = new TelephoneTimeLogModel();
					layoutRecord.add(telephoneTimeLogModel);
				}
				sortedTelephoneTimeLogModelsPM.addAll(layoutRecord);
			}
				
			parameters.put("tel_cpi_c", tel_cpi_c);
			parameters.put("tel_cpi_t", tel_cpi_t);
			parameters.put("tel_ghs_c", tel_ghs_c);
			parameters.put("tel_ghs_t", tel_ghs_t);
			parameters.put("tel_b_c", tel_b_c);
			parameters.put("tel_b_t", tel_b_t);
			
			parameters.put("date", commonService.formatReportDate(timeLog.getDate()));
			
			parameters.put("telephoneTimeLogAM", sortedTelephoneTimeLogModelsAM);
			parameters.put("telephoneTimeLogPM", sortedTelephoneTimeLogModelsPM);
			
			Set<FieldworkTimeLog> fieldworkTimeLogs = timeLog.getFieldworkTimeLogs();
			List<FieldworkTimeLogModel> fieldworkTimeLogModels = new ArrayList<FieldworkTimeLogModel>();
			
			Integer field_cpi_c = 0;
			Integer field_cpi_t = 0;
			Integer field_ghs_c = 0;
			Integer field_ghs_t = 0;
			Integer field_b_t = 0;
			
			Set<String> compareEnumerationOutcome = new HashSet<>(Arrays.asList(new String[] { "C", "D", "U", "ND", "O" }));
			
			for (FieldworkTimeLog fieldworkTimeLog : fieldworkTimeLogs) {
				FieldworkTimeLogModel fieldworkTimeLogModel = new FieldworkTimeLogModel();
				BeanUtils.copyProperties(fieldworkTimeLogModel, fieldworkTimeLog);

				if (fieldworkTimeLog.getSurvey() != null && !fieldworkTimeLog.getSurvey().isEmpty()){
					//Survey Type equal to CPI (MRPS)
					if (fieldworkTimeLog.getSurvey().equals(SystemConstant.SURVEY_1)){
						if (fieldworkTimeLog.getEnumerationOutcome() != null && fieldworkTimeLog.getEnumerationOutcome().equals("C")) {
							field_cpi_c += fieldworkTimeLog.getQuotationCount() != null ? fieldworkTimeLog.getQuotationCount() : 0;
							field_cpi_t += fieldworkTimeLog.getTotalQuotation() != null ? fieldworkTimeLog.getTotalQuotation() : 0;
						}
					}
					//Survey Type equal to GHS, Activity = "FI"
					if (fieldworkTimeLog.getSurvey().equals(SystemConstant.SURVEY_2)){
						if (fieldworkTimeLog.getActivity() != null && fieldworkTimeLog.getActivity().equals(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_1)){
							field_ghs_t++;
							
							if(fieldworkTimeLogModel.getEnumerationOutcome() != null && 
									compareEnumerationOutcome.contains(fieldworkTimeLogModel.getEnumerationOutcome())){
								field_ghs_c++;
							}
						}
					}
					//Survey Type equal to BMWPS
					if (fieldworkTimeLog.getSurvey().equals(SystemConstant.SURVEY_3)){
						if (fieldworkTimeLog.getActivity() != null && fieldworkTimeLog.getActivity().equals(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_1)){
							field_b_t++;
						}
					}		
			
				}	
					
				if (fieldworkTimeLog.getSurvey() != null && fieldworkTimeLog.getCaseReferenceNo() != null
						&& fieldworkTimeLog.getActivity() != null){
					if (fieldworkTimeLog.getActivity().equals(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_1)){
						String text = fieldworkTimeLog.getSurvey().trim() + fieldworkTimeLog.getCaseReferenceNo().trim() 
								+ fieldworkTimeLog.getActivity().trim();

						Set<String> countSet = visitCount.get(fieldworkTimeLog.getSurvey()); 
						if (countSet != null){
							countSet.add(text);
						} else {
							countSet = new HashSet<>();
							countSet.add(text);
							visitCount.put(fieldworkTimeLog.getSurvey(), countSet);
						}
					}
				}
				
				if (fieldworkTimeLog.getRecordType() != null) {
					if (fieldworkTimeLog.getRecordType() == 1 ) {
						fieldworkTimeLogModel.setMarketQuotationCount(fieldworkTimeLog.getQuotationCount());
						fieldworkTimeLogModel.setMarketTotalQuotation(fieldworkTimeLog.getTotalQuotation());
					} else if (fieldworkTimeLog.getRecordType() == 2 ) {
						fieldworkTimeLogModel.setNonMarketQuotationCount(fieldworkTimeLog.getQuotationCount());
						fieldworkTimeLogModel.setNonMarketTotalQuotation(fieldworkTimeLog.getTotalQuotation());				
					}
				}
				if (fieldworkTimeLog.getStartTime() != null){
					String duration = commonService.formatTime(fieldworkTimeLog.getStartTime());
					String hour = duration.split(":")[0];
					String minutes = duration.split(":")[1];
					fieldworkTimeLogModel.setStartTimeHr(hour);
					fieldworkTimeLogModel.setStartTimeMin(minutes);
				}
				fieldworkTimeLogModels.add(fieldworkTimeLogModel);
				
				Collections.sort(fieldworkTimeLogModels);
				for(FieldworkTimeLogModel model : fieldworkTimeLogModels){				
					if (model.getActivity().equals(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_2)){
						if (model.getFromLocation() != null){
							model.setDestination("From:" + model.getFromLocation() + "\nTo:" + model.getToLocation());
						}
					}
				}
			}
			
			if (fieldworkTimeLogModels.size() < ROW_COUNT){
				List<FieldworkTimeLogModel> layoutRecord = new ArrayList<>();
				for(int i = fieldworkTimeLogModels.size(); i < ROW_COUNT; i++){
					FieldworkTimeLogModel fieldworkTimeLogModel = new FieldworkTimeLogModel();
					layoutRecord.add(fieldworkTimeLogModel);
				}
				fieldworkTimeLogModels.addAll(layoutRecord);
			}
			
			cpi_v = (visitCount.get(SystemConstant.SURVEY_1) != null) ? visitCount.get(SystemConstant.SURVEY_1).size() : 0; 
			ghs_v = (visitCount.get(SystemConstant.SURVEY_2) != null) ? visitCount.get(SystemConstant.SURVEY_2).size() : 0; 
			b_v = (visitCount.get(SystemConstant.SURVEY_3) != null) ? visitCount.get(SystemConstant.SURVEY_3).size() : 0; 

			
			parameters.put("cpi_v", cpi_v);
			parameters.put("field_cpi_c", field_cpi_c);
			parameters.put("field_cpi_t", field_cpi_t);
			parameters.put("ghs_v", ghs_v);
			parameters.put("field_ghs_c", field_ghs_c);
			parameters.put("field_ghs_t", field_ghs_t);
			parameters.put("b_v", b_v);
			parameters.put("field_b_t", field_b_t);
			
			if (timeLogModel.getIsTrainingAM()) {
				parameters.put("training_am", "AM");
				parameters.put("training_pm", "<style isStrikeThrough=\"true\">PM</style>");
			} else if (timeLogModel.getIsTrainingPM()) {
				parameters.put("training_pm", "PM");
				parameters.put("training_am", "<style isStrikeThrough=\"true\">AM</style>");
			} else if (!timeLogModel.getIsTrainingAM() && !timeLogModel.getIsTrainingPM()){
				parameters.put("training_am", "AM");
				parameters.put("training_pm", "PM");
			}
	
			if (timeLogModel.getIsVLSLAM()) {
				parameters.put("vl_am", "AM");
				parameters.put("vl_pm", "<style isStrikeThrough=\"true\">PM</style>");
			} else if (timeLogModel.getIsVLSLPM()) {
				parameters.put("vl_pm", "PM");
				parameters.put("vl_am", "<style isStrikeThrough=\"true\">AM</style>");
			} else if (!timeLogModel.getIsVLSLAM() && !timeLogModel.getIsVLSLPM()){
				parameters.put("vl_am", "AM");
				parameters.put("vl_pm", "PM");
			}
			
			if (timeLog.isOtherWorkingSession()) {
				timeLogModel.setOtherWorkingSessionFrom(commonService.formatTime(timeLog.getOtherWorkingSessionFrom()));
				timeLogModel.setOtherWorkingSessionTo(commonService.formatTime(timeLog.getOtherWorkingSessionTo()));
			} else {
				WorkingSessionSetting workingSession = timeLog.getSetting();
				timeLogModel.setOtherWorkingSessionFrom(commonService.formatTime(workingSession.getFromTime()));
				timeLogModel.setOtherWorkingSessionTo(commonService.formatTime(workingSession.getToTime()));
			}		
			parameters.put("session", timeLogModel.getOtherWorkingSessionFrom() + " - " + timeLogModel.getOtherWorkingSessionTo());
			
			if (timeLogModel.getOtClaimedStart() != null){
				parameters.put("overtimeStartTime", timeLogModel.getOtClaimedStart());
			}
			if (timeLogModel.getOtClaimedEnd() != null){
				parameters.put("overtimeEndTime", timeLogModel.getOtClaimedEnd());
			}
			if (timeLogModel.getOtClaimedHr() != null){
				parameters.put("overtimeHr", timeLogModel.getOtClaimedHr());
			}
			if (timeLogModel.getOtClaimedMin() != null){
				parameters.put("overtimeMin", timeLogModel.getOtClaimedMin());
			}
			if (timeLogModel.getTimeoffTakenStart() != null){
				parameters.put("timeoffStartTime", timeLogModel.getTimeoffTakenStart());
			}
			if (timeLogModel.getTimeoffTakenEnd() != null){
				parameters.put("timeoffEndTime", timeLogModel.getTimeoffTakenEnd());
			}
			if (timeLogModel.getTimeoffTakenHr() != null){
				parameters.put("timeoffHr", timeLogModel.getTimeoffTakenHr());
			}
			if (timeLogModel.getTimeoffTakenMin() != null){
				parameters.put("timeoffMin", timeLogModel.getTimeoffTakenMin());
			}
			
			parameters.put("team", officer.getTeam());
			parameters.put("officerDetail", officer.getEnglishName());
			parameters.put("staffCode", officer.getStaffCode());
			
			parametersAM.put("name", "A.M. Session");
			parametersPM.put("name", "P.M. Session");
			
			parameters.put("parametersAM",parametersAM);
			parameters.put("parametersPM",parametersPM);
			parameters.put("fieldworkTimeLog", fieldworkTimeLogModels);
			
			Resource resource = new ClassPathResource("report/"+"TimeLogReportTelephone"+".jasper");
			parameters.put("subreportPath", resource.getFile().getPath());
			resource = new ClassPathResource("report/"+"TimeLogReportFieldwork"+".jasper");
			parameters.put("subreportFieldworkPath", resource.getFile().getPath());
			
			if(task.getReportType() == ReportServiceBase.XLSX) {
				parameters.put("IS_IGNORE_PAGINATION", Boolean.TRUE);
			}
			
			List<FieldworkTimeLogModel> blankLayoutRecord = new ArrayList<FieldworkTimeLogModel>();
			blankLayoutRecord.add(new FieldworkTimeLogModel());
			
			JasperPrint jasperPrint = this.getJasperPrint(task, "TimeLogForm", blankLayoutRecord, parameters, officer.getEnglishName()+"("+officer.getStaffCode()+")");	
			jasperPrints.add(jasperPrint);
		}

		if (jasperPrints.size() == 0) {
			Hashtable<String, Object> parameters = new Hashtable<String, Object>();
			
			List<FieldworkTimeLogModel> data = new ArrayList<FieldworkTimeLogModel>();
			JasperPrint jasperPrint = this.getJasperPrint(task, "TimeLogForm", data, parameters, "Sheet 1");
			jasperPrints.add(jasperPrint);
		}
		
		String path = this.exportReport(jasperPrints, task.getReportType(), task.getFunctionCode());	
		
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	public String formatDuration (Date time) {
		if (time != null){
			String duration = commonService.formatTime(time);
			Integer hour = Integer.parseInt(duration.split(":")[0]);
			Integer minutes = Integer.parseInt(duration.split(":")[1]);
			String durationText = "";
			if (hour > 0) {
				durationText += String.format(" (   %d Hr ", hour);
			} else if (hour == 0){
				durationText += "  Hr ";
			}
			if (hour == 0 || minutes > 0 ) {
				durationText += String.format("%d Min) ", minutes);
			}
			if (minutes == 0) {
				durationText += "  Min )";
			}
			return durationText;
		}
		return "";
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9047";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		InformationTimeLogFormCriteria criteria = (InformationTimeLogFormCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Date: %s", criteria.getTimeLogDate()));
		
		if(criteria.getFieldOfficerId() != null && criteria.getFieldOfficerId().size() > 0){
			descBuilder.append("\n");
			List<String> fieldOfficers = new ArrayList<String>();
			List<User> users = userDao.getUsersByIds(criteria.getFieldOfficerId());
			for(User user : users) {
				fieldOfficers.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Field Officer(s): %s", StringUtils.join(fieldOfficers, ", ")));
		}
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		} else {
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
		}
		
		User creator = userDao.findById(userId);
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(taskType);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;
	}

}
