package capi.service.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.PECheckForm;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.PECheckSummaryCriteria;
import capi.model.report.PECheckSummaryReport;
import capi.service.CommonService;

@Service("PECheckSummaryService")
public class PECheckSummaryService extends ReportServiceBase{

	@Autowired
	private UserDao userDao;

	@Autowired
	private PECheckFormDao peCheckDao;
	
	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9034";
	}
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		PECheckSummaryCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), PECheckSummaryCriteria.class);
		List<PECheckSummaryReport> data = new ArrayList<PECheckSummaryReport>();
		
		List<User> users = null;
		
		if(criteria.getOfficerIds()!=null && criteria.getOfficerIds().length>0){
			users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
		} else {
			if(criteria.getTeams()!=null && criteria.getTeams().length>0){
				users = userDao.searchOfficerByTeam(null, 0, 0, criteria.getTeams(), null);
			} else {
				if((criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD) == 1 
						|| (criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD) == 2
//						|| (criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_ALLOCATION_COORDINATOR) == 8 
						|| (criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_BUSINESS_DATA_ADMINISTRATOR) == 256){
					users = userDao.findAll();
				} else if ((criteria.getAuthorityLevel() & 4) == 4){
					User tempUser = userDao.findById(criteria.getUserId());
					users = new ArrayList<User>();
					users.add(tempUser);
					users.addAll(tempUser.getSubordinates());
				}
			}
		}
		
		List <User> activeUsers = new ArrayList<User>();;
		
		Hashtable<Integer, List<PECheckForm>> mapPECheck = new Hashtable<Integer, List<PECheckForm>>();
		List<Integer> userIds = new ArrayList<Integer>();
		for(User user : users){
			if((user.getRank() != null && !commonService.stringEquals(user.getRank().getCode(), "CSO"))
					&& !commonService.stringEquals(user.getTeam(), "NA")) {
				userIds.add(user.getUserId());
				mapPECheck.put(user.getUserId(), new ArrayList<PECheckForm>());
				activeUsers.add(user);
			}
		}
		
		Collections.sort(activeUsers, new Comparator<User>(){
			@Override
			public int compare(User o1, User o2) {
				// TODO Auto-generated method stub
				return o1.getStaffCode().compareTo(o2.getStaffCode());
			}			
		});
		
		Collections.sort(activeUsers, new Comparator<User>(){
			@Override
			public int compare(User o1, User o2) {
				// TODO Auto-generated method stub
				return o1.getRank().getRankId().compareTo(o2.getRank().getRankId());
			}			
		});
		
		List<PECheckForm> peChecks = peCheckDao.getPECheckbyUserReferenceMonth(userIds.toArray(new Integer[0]), commonService.getMonth(criteria.getFromMonth()), commonService.getMonth(criteria.getToMonth()));
		List<PECheckSummaryReport> peCheckCountSummary = peCheckTaskDao.getPECheckSummaryReportByUserReferenceMonth(userIds.toArray(new Integer[0]), commonService.getMonth(criteria.getFromMonth()), commonService.getMonth(criteria.getToMonth()));
		HashMap<Integer, PECheckSummaryReport> peCheckCountSummaryMap = new HashMap<>();
		
		for(PECheckSummaryReport peCheckCount : peCheckCountSummary){
			peCheckCountSummaryMap.put(peCheckCount.getOfficerId(), peCheckCount);
		}
		
		for(PECheckForm peCheckForm : peChecks){
			List<PECheckForm> peCheck = mapPECheck.get(peCheckForm.getOfficer().getUserId());
			peCheck.add(peCheckForm);
			mapPECheck.put(peCheckForm.getOfficer().getUserId(), peCheck);
		}
		
		for(User user : activeUsers){
			PECheckSummaryReport report = new PECheckSummaryReport();
			List<PECheckForm> peCheckForms = mapPECheck.get(user.getUserId());
			report.setTeam(user.getTeam());
			report.setOfficerCode(user.getStaffCode());
			report.setOffcierName(user.getEnglishName());

			int contactDate = 0;
			int contactTime = 0;
			int contactDuration = 0;
			int contactMode = 0;
			int dateCollected = 0;
			int others = 0;
			int allInformationInOrder = 0;
			String otherResults = "";
			
			for(PECheckForm peCheckForm : peCheckForms){
				if(peCheckForm.getContactDateResult()!=null && peCheckForm.getContactDateResult()==2){
					contactDate++;
				}
				if(peCheckForm.getContactTimeResult()!=null && peCheckForm.getContactTimeResult()==2){
					contactTime++;
				}
				if(peCheckForm.getContactDurationResult()!=null && peCheckForm.getContactDurationResult()==2){
					contactDuration++;
				}
				if(peCheckForm.getContactModeResult()!=null && peCheckForm.getContactModeResult()==2){
					contactMode++;
				}
				if(peCheckForm.getDateCollectedResult()!=null && peCheckForm.getDateCollectedResult()==2){
					dateCollected++;
				}
				if(peCheckForm.getOthersResult()!=null && peCheckForm.getOthersResult()==2){
					others++;
				}
				if( (peCheckForm.getContactDateResult()!= null && peCheckForm.getContactDateResult() != 2) &&
					(peCheckForm.getContactTimeResult()!= null && peCheckForm.getContactTimeResult() != 2) &&
					(peCheckForm.getContactDurationResult()!= null && peCheckForm.getContactDurationResult() != 2) &&
				    (peCheckForm.getContactModeResult()!= null && peCheckForm.getContactModeResult() != 2) &&
				    (peCheckForm.getDateCollectedResult()!= null && peCheckForm.getDateCollectedResult() != 2) &&
				    (peCheckForm.getOthersResult()!= null && peCheckForm.getOthersResult() != 2)){
					allInformationInOrder++;
				}
				if(peCheckForm.getOtherRemark() != null && !StringUtils.isBlank(peCheckForm.getOtherRemark())){
					if(peCheckForm.getAssignment() != null && peCheckForm.getAssignment().getOutlet() != null) {
						otherResults += peCheckForm.getAssignment().getOutlet().getFirmCode() + " - " + String.valueOf(peCheckForm.getOtherRemark()) + ", \r\n";
					} else {
						otherResults += String.valueOf(peCheckForm.getOtherRemark()) + ", \r\n";
					}
				}
			}
			
			if(!StringUtils.isBlank(otherResults)) {
				otherResults = StringUtils.stripEnd(otherResults, ", \r\n") + "\r\n";
				report.setRemarks(otherResults);
			}
			
			report.setNoOfFirmsChecked(peCheckCountSummaryMap.get(user.getId())!=null ? peCheckCountSummaryMap.get(user.getId()).getNoOfFirmsChecked() : 0 );
			report.setNoOfFirmsNC(peCheckCountSummaryMap.get(user.getId()) != null ? peCheckCountSummaryMap.get(user.getId()).getNoOfFirmsNC() : 0);
			report.setTotalFirmEnumerated(peCheckCountSummaryMap.get(user.getId()) != null ? peCheckCountSummaryMap.get(user.getId()).getTotalFirmEnumerated() : 0);
			report.setContactDate(contactDate);
			report.setContactTime(contactTime);
			report.setContactDuration(contactDuration);
			report.setContactMode(contactMode);
			report.setDateCollected(dateCollected);
			report.setOthers(others);
			report.setAllInformationInOrder(allInformationInOrder);
			data.add(report);
		}
		
		/**
		 * Set Parameters
		 */
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		String reportTitle = "Record of MRPS Post-enumeration Check / Verification in CPI Field Pool" ;
		String period = (criteria.getFromMonth().equals(criteria.getToMonth())) ? 
				 commonService.formatMonthName(criteria.getFromMonth()) : commonService.formatMonthName(criteria.getFromMonth()) + " - " + commonService.formatMonthName(criteria.getToMonth());
		parameters.put("period", reportTitle + " " + period);
		if(task.getReportType() == ReportServiceBase.XLSX) {
			parameters.put("IS_IGNORE_PAGINATION", Boolean.TRUE);
		}
		String path = this.exportReport(task, "PECheckSummary", data, parameters);
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		// TODO Auto-generated method stub
		PECheckSummaryCriteria criteria = (PECheckSummaryCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getOfficerIds() != null && criteria.getOfficerIds().length > 0) {
			List<User> users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
			if(users != null && users.size() > 0) {
				List<String> userStr = new ArrayList<String>();
				for(User user : users) {
					String str = user.getStaffCode() + " - " + user.getEnglishName();
					userStr.add(str);
				}
				descBuilder.append(String.format("Officer: %s", StringUtils.join(userStr, ", ")));
			}
			descBuilder.append("\n");
		}
		descBuilder.append(String.format("Period: %s", criteria.getFromMonth()+" - "+criteria.getToMonth()));
		
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
