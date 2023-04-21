package capi.service.report;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckPhoneCallDao;
import capi.dal.SpotCheckResultDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.SpotCheckPhoneCall;
import capi.entity.SpotCheckResult;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.InformationSpotCheckForm;
import capi.model.report.InformationSpotCheckFormCriteria;
import capi.service.CommonService;
import net.sf.jasperreports.engine.JasperPrint;

@Service("InformationSpotCheckFormService")
public class InformationSpotCheckFormService extends ReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private SpotCheckFormDao spotCheckFormDao;

	@Autowired
	private SpotCheckPhoneCallDao spotCheckPhoneCallDao;

	@Autowired
	private SpotCheckResultDao spotCheckResultDao;

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		InformationSpotCheckFormCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), InformationSpotCheckFormCriteria.class);
		
		List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>();
		
		List<InformationSpotCheckForm> progress = spotCheckFormDao.getInformationSpotCheckForm(
				criteria.getSurvey(), criteria.getFieldOfficerId(), criteria.getSpotCheckDates());
		
		List<Integer> spotCheckFormId = new ArrayList<Integer>();
		for(InformationSpotCheckForm spot : progress) {
			spotCheckFormId.add(spot.getSpotCheckFormId());
		}
		
		List<InformationSpotCheckForm> mapAddressProgress = spotCheckFormDao.getInformationSpotCheckFormMapAddress(spotCheckFormId);
		progress.stream().forEach(spot -> {
			Optional<InformationSpotCheckForm> a = mapAddressProgress.stream().filter(p -> p.getSpotCheckFormId().equals(spot.getSpotCheckFormId())).findAny();
			if (a.isPresent()) 
				spot.setMapAddress(a.get().getMapAddress());
		});
		
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		Resource resource = new ClassPathResource("report/"+"SpotCheckResult"+".jasper");
		parameters.put("scResultPath", resource.getFile().getPath());
		
		resource = new ClassPathResource("report/"+"SpotCheckPhoneCall"+".jasper");
		parameters.put("scPhoneCallPath", resource.getFile().getPath());
		
		String checkedImage = "checked";
		String uncheckedImage = "unchecked";
		Resource checkedResource = new ClassPathResource("report/"+checkedImage+".png");
		Resource uncheckedResource = new ClassPathResource("report/"+uncheckedImage+".png");
		String checkedImagePath = checkedResource.getFile().getPath();
		String uncheckedImagePath = uncheckedResource.getFile().getPath();
		
		for(InformationSpotCheckForm info : progress) {
			info.setScheduledTime(commonService.formatTime(info.getScheduledTimeDate()));
			info.setTurnUpTime(commonService.formatTime(info.getTurnUpTimeDate()));
			info.setCheckedImagePath(checkedImagePath);
			info.setUncheckedImagePath(uncheckedImagePath);
			
			List<SpotCheckPhoneCall> spotCheckPhoneCalls = spotCheckPhoneCallDao.getSCPhoneCallsBySCFormId(info.getSpotCheckFormId());
			List<InformationSpotCheckForm.InformationSCPhoneCall> scPhoneCalls = new ArrayList<InformationSpotCheckForm.InformationSCPhoneCall>();
			for(SpotCheckPhoneCall spotCheckPhoneCall : spotCheckPhoneCalls) {
				String time = commonService.formatTime(spotCheckPhoneCall.getPhoneCallTime());
				InformationSpotCheckForm.InformationSCPhoneCall scPhoneCall = new InformationSpotCheckForm.InformationSCPhoneCall();
				scPhoneCall.setScPhoneCallTime(time);
				scPhoneCall.setScPhoneCallResult(spotCheckPhoneCall.getResult());
				scPhoneCall.setCheckedImagePath(checkedImagePath);
				scPhoneCall.setUncheckedImagePath(uncheckedImagePath);
				scPhoneCalls.add(scPhoneCall);
			}
			parameters.put("scPhoneCalls", scPhoneCalls);
			
			int i = 1;
			List<SpotCheckResult> spotCheckResults = spotCheckResultDao.getSCResultsBySCFormId(info.getSpotCheckFormId());
			List<InformationSpotCheckForm.InformationSCResult> scResults = new ArrayList<InformationSpotCheckForm.InformationSCResult>();
			for(SpotCheckResult spotCheckResult : spotCheckResults) {
				InformationSpotCheckForm.InformationSCResult scResult = new InformationSpotCheckForm.InformationSCResult();
				
				scResult.setScResultResult(spotCheckResult.getResult());
				
				if (StringUtils.isNotEmpty(spotCheckResult.getOtherRemark())){
					scResult.setScResultRemark(spotCheckResult.getOtherRemark());
					String remark = String.format("Other %s: %s", i, spotCheckResult.getOtherRemark());
					if (StringUtils.isEmpty(info.getRemarksForNonContact())){
						info.setRemarksForNonContact(remark);
					} else {
						info.setRemarksForNonContact(info.getRemarksForNonContact()+", "+remark);
					}
				}
				scResult.setScResultReferenceNo(spotCheckResult.getReferenceNo());
				scResult.setScResultSurvey(info.getSurvey());
				scResult.setCheckedImagePath(checkedImagePath);
				scResult.setUncheckedImagePath(uncheckedImagePath);
				scResults.add(scResult);
				i++;
			}
			parameters.put("scResults", scResults);
			
			List<InformationSpotCheckForm> data = new ArrayList<InformationSpotCheckForm>();
			data.add(info);
			JasperPrint jasperPrint = this.getJasperPrint(task, "SpotCheckRecord", data, parameters, info.getOfficerCode() + "(" + info.getDateOfChecking() + ")");
			jasperPrints.add(jasperPrint);
		}
		
		if (jasperPrints.size() == 0){
			parameters = new Hashtable<String, Object>();
			List<InformationSpotCheckForm> data = new ArrayList<InformationSpotCheckForm>();
			JasperPrint jasperPrint = this.getJasperPrint(task, "SpotCheckRecord", data, parameters, "Sheet 1");
			jasperPrints.add(jasperPrint);
		}
		
		String path = this.exportReport(jasperPrints, task.getReportType(), task.getFunctionCode());
		
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9045";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		InformationSpotCheckFormCriteria criteria = (InformationSpotCheckFormCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));
		
		if (criteria.getSurvey() != null && criteria.getSurvey().size() > 0){
			descBuilder.append("\n");
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(criteria.getSurvey(), ", ")));
		}
		
		if(criteria.getFieldOfficerId() != null && criteria.getFieldOfficerId().size() > 0){
			descBuilder.append("\n");
			List<String> fieldOfficers = new ArrayList<String>();
			List<User> users = userDao.getUsersByIds(criteria.getFieldOfficerId());
			for(User user : users) {
				fieldOfficers.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Field Officer(s): %s", StringUtils.join(fieldOfficers, ", ")));
		}
		
		if (criteria.getSpotCheckDates() != null && criteria.getSpotCheckDates().size() > 0) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Spot Check Date(s): %s", StringUtils.join(criteria.getSpotCheckDates(), ", ")));
		}
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
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
