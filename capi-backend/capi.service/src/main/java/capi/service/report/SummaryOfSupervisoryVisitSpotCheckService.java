package capi.service.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.lang.Math;
import java.text.ParseException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.StaffReasonDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckCriteria;
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckData;
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckReport;
import net.sf.jasperreports.engine.JasperPrint;

@Service("SummaryOfSupervisoryVisitSpotCheckService")
public class SummaryOfSupervisoryVisitSpotCheckService extends ReportServiceBase{

	@Autowired
	private SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitFormDao;
	
	@Autowired
	private StaffReasonDao staffReasonDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private UserDao userDao;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9017";
	}

	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfSupervisoryVisitSpotCheckCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfSupervisoryVisitSpotCheckCriteria.class);
		
		List<SummaryOfSupervisoryVisitSpotCheckData> spotCheckReportData = spotCheckFormDao.getSummaryOfSupervisoryVisitSpotCheckReportData(Integer.parseInt(criteria.getYear()), criteria.getUserId());
		List<SummaryOfSupervisoryVisitSpotCheckData> supervisoryVisitReportData = supervisoryVisitFormDao.getSummaryOfSupervisoryVisitSpotCheckReportData(Integer.parseInt(criteria.getYear()), criteria.getUserId());
		List<SummaryOfSupervisoryVisitSpotCheckData> spotCheckYearlySummary = spotCheckFormDao.getSpotCheckReportYearlySummary(Integer.parseInt(criteria.getYear()), criteria.getUserId());
		List<SummaryOfSupervisoryVisitSpotCheckData> supervisoryVisitYearlySummary = supervisoryVisitFormDao.getSupervisoryVisitReportYearlySummary(Integer.parseInt(criteria.getYear()), criteria.getUserId());
//		List<SummaryOfSupervisoryVisitSpotCheckRemark> remarkReport = staffReasonDao.getSummaryOfSupervisoryVisitSpotCheckReportRemark(Integer.parseInt(criteria.getYear()), criteria.getUserId());
		
//		Integer remarkIndex = 1;
//		for (SummaryOfSupervisoryVisitSpotCheckRemark remark : remarkReport) {
//			remark.setSequence(getString(remarkIndex));
//			remarkIndex++;	
//		}
		
		HashMap<Integer, SummaryOfSupervisoryVisitSpotCheckReport> spotCheckReport = new HashMap<Integer, SummaryOfSupervisoryVisitSpotCheckReport>();
		List<Integer> spotCheckReportIndex = new ArrayList<Integer>();
		for (SummaryOfSupervisoryVisitSpotCheckData spotCheck : spotCheckReportData) {
			Integer userId = spotCheck.getUserId();
			SummaryOfSupervisoryVisitSpotCheckReport item = null;
			if (spotCheckReport.size() > 0 && spotCheckReport.containsKey(userId)) {
				item = spotCheckReport.get(userId);
				setSpotCheckReport(criteria.getYear(), spotCheck, item);
			} else {
				item = setSpotCheckReport(criteria.getYear(), spotCheck, item);
				spotCheckReport.put(userId, item);
				spotCheckReportIndex.add(userId);
			}
		}
		
		HashMap<Integer,SummaryOfSupervisoryVisitSpotCheckReport> supervisoryVisitReport = new HashMap<Integer,SummaryOfSupervisoryVisitSpotCheckReport>();
		List<Integer> supervisoryVisitReportIndex = new ArrayList<Integer>();
		for (SummaryOfSupervisoryVisitSpotCheckData supervisoryVisit : supervisoryVisitReportData) {
			Integer userId = supervisoryVisit.getUserId();
			SummaryOfSupervisoryVisitSpotCheckReport item = null;
			if (supervisoryVisitReport.size() > 0 && supervisoryVisitReport.containsKey(userId)) {
				item = supervisoryVisitReport.get(userId);		
				setSpotCheckReport(criteria.getYear(), supervisoryVisit, item);
			} else {
				item = setSpotCheckReport(criteria.getYear(), supervisoryVisit, item);
				supervisoryVisitReport.put(userId, item);
				supervisoryVisitReportIndex.add(userId);	
			}
		}
		
		HashMap<Integer, SummaryOfSupervisoryVisitSpotCheckData> scMonthlyCountMap = new HashMap<Integer, SummaryOfSupervisoryVisitSpotCheckData>();
		for (SummaryOfSupervisoryVisitSpotCheckData count : spotCheckYearlySummary) {
			scMonthlyCountMap.put(count.getMonth(), count);
		}
		
		HashMap<Integer, SummaryOfSupervisoryVisitSpotCheckData> svMonthlyCountMap = new HashMap<Integer, SummaryOfSupervisoryVisitSpotCheckData>();
		for (SummaryOfSupervisoryVisitSpotCheckData count : supervisoryVisitYearlySummary) {
			svMonthlyCountMap.put(count.getMonth(), count);
		}
		
		List<SummaryOfSupervisoryVisitSpotCheckReport> seqSpotCheckReport= new ArrayList<SummaryOfSupervisoryVisitSpotCheckReport>();
		for(Integer index : spotCheckReportIndex){
			seqSpotCheckReport.add(spotCheckReport.get(index));
		}
	    Collection<SummaryOfSupervisoryVisitSpotCheckReport> spotCheckCollection = seqSpotCheckReport;
		
		List<SummaryOfSupervisoryVisitSpotCheckReport> seqSupervisoryVisitReport= new ArrayList<SummaryOfSupervisoryVisitSpotCheckReport>();
		for(Integer index : supervisoryVisitReportIndex){
			seqSupervisoryVisitReport.add(supervisoryVisitReport.get(index));
		}
	    Collection<SummaryOfSupervisoryVisitSpotCheckReport> supervisoryVisitCollection = seqSupervisoryVisitReport;
	    
		List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>();	
		
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		if (criteria.getTeam() != null && criteria.getTeam().size() > 0){
			parameters.put("team", StringUtils.join(criteria.getTeam(), ","));
		}
		else{
			parameters.put("team", "-");
		}
		
		parameters.put("period", criteria.getYear());
		
		parameters.put("year", criteria.getYear());
		
		parameters.put("type", "Spot Checks");
		
		String[] reportParGHS = {"janGHSSubCount", "febGHSSubCount", "marGHSSubCount", "aprGHSSubCount", "mayGHSSubCount", "junGHSSubCount",
				  "julGHSSubCount", "augGHSSubCount", "sepGHSSubCount", "octGHSSubCount", "novGHSSubCount", "devGHSSubCount" };

		String[] reportParCPI = {"janCPISubCount", "febCPISubCount", "marCPISubCount", "aprCPISubCount", "mayCPISubCount", "junCPISubCount",
			  "julCPISubCount", "augCPISubCount", "sepCPISubCount", "octCPISubCount", "novCPISubCount", "devCPISubCount" };
		
		if(task.getReportType() == ReportServiceBase.XLSX) {
			parameters.put("IS_IGNORE_PAGINATION", Boolean.TRUE);
		}
		
		int sumCpi = 0;
		int sumGhs = 0;
		int sumDayShift = 0;
		int sumNightShift = 0;
		for (int i = 0; i<12; i++){
			parameters.put(reportParGHS[i], String.valueOf(scMonthlyCountMap.get(i+1) != null ? scMonthlyCountMap.get(i+1).getGhsCount() : 0 ));
			parameters.put(reportParCPI[i], String.valueOf(scMonthlyCountMap.get(i+1) != null ? scMonthlyCountMap.get(i+1).getCpiCount() : 0 ));
			sumCpi += (scMonthlyCountMap.get(i+1) != null ? scMonthlyCountMap.get(i+1).getCpiCount() : 0);
			sumGhs += (scMonthlyCountMap.get(i+1) != null ? scMonthlyCountMap.get(i+1).getGhsCount() : 0);
			sumDayShift += (scMonthlyCountMap.get(i+1) != null ? scMonthlyCountMap.get(i+1).getDayShiftCount() : 0);
			sumNightShift += (scMonthlyCountMap.get(i+1) != null ? scMonthlyCountMap.get(i+1).getNightShiftCount() : 0);
		}
		parameters.put("sumCpi", sumCpi);
		parameters.put("sumGhs", sumGhs);
		parameters.put("sumDayShift", sumDayShift);
		parameters.put("sumNightShift", sumNightShift);
		
		JasperPrint scReport = this.getJasperPrint(task, "SummaryOfSupervisoryVisitSpotCheckReport", spotCheckCollection, parameters, "SC_" + criteria.getYear());		
		jasperPrints.add(scReport);
		
//		JasperPrint scRemark = this.getJasperPrint(task, "SummaryOfSupervisoryVisitSpotCheckRemark", remarkReport, parameters, "SC_Remarks");		
//		jasperPrints.add(scRemark);
		
		parameters.put("type", "Supervisory Visits");
		
		sumCpi = 0;
		sumGhs = 0;
		sumDayShift = 0;
		sumNightShift = 0;
		for (int i = 0; i<12; i++){
			parameters.put(reportParGHS[i], String.valueOf(svMonthlyCountMap.get(i+1) != null ? svMonthlyCountMap.get(i+1).getGhsCount() : 0 ));
			parameters.put(reportParCPI[i], String.valueOf(svMonthlyCountMap.get(i+1) != null ? svMonthlyCountMap.get(i+1).getCpiCount() : 0 ));
			sumCpi += (svMonthlyCountMap.get(i+1) != null ? svMonthlyCountMap.get(i+1).getCpiCount() : 0);
			sumGhs += (svMonthlyCountMap.get(i+1) != null ? svMonthlyCountMap.get(i+1).getGhsCount() : 0);
			sumDayShift += (svMonthlyCountMap.get(i+1) != null ? svMonthlyCountMap.get(i+1).getDayShiftCount() : 0);
			sumNightShift += (svMonthlyCountMap.get(i+1) != null ? svMonthlyCountMap.get(i+1).getNightShiftCount() : 0);
		}
		parameters.put("sumCpi", sumCpi);
		parameters.put("sumGhs", sumGhs);
		parameters.put("sumDayShift", sumDayShift);
		parameters.put("sumNightShift", sumNightShift);
		
		JasperPrint svReport = this.getJasperPrint(task, "SummaryOfSupervisoryVisitSpotCheckReport", supervisoryVisitCollection, parameters, "SV_" + criteria.getYear());		
		jasperPrints.add(svReport);
		
//		JasperPrint svRemark = this.getJasperPrint(task, "SummaryOfSupervisoryVisitSpotCheckRemark", remarkReport, parameters, "SV_Remarks");	
//		jasperPrints.add(svRemark);
		
		String path = this.exportReport(jasperPrints, task.getReportType(), task.getFunctionCode());	
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	private SummaryOfSupervisoryVisitSpotCheckReport setSpotCheckReport(String year, SummaryOfSupervisoryVisitSpotCheckData data, SummaryOfSupervisoryVisitSpotCheckReport report) throws ParseException {
		if ( report == null ) {
			report = new SummaryOfSupervisoryVisitSpotCheckReport();
			report.setUserStaffCode(data.getUserStaffCode());
			report.setUserEnglishName(data.getUserEnglishName());
			report.setUserTeam(data.getUserTeam());
			if (data.getIsGHS() != null && !data.getIsGHS().booleanValue()) {
				report.setIsGHS("-");
			}
			report.setGhsCount(data.getGhsCount());
			report.setDayShiftCount(data.getDayShiftCount());
			report.setNightShiftCount(data.getNightShiftCount());
			report.setCpiCount(data.getCpiCount());
		} else {
			Integer sumGhsCount = report.getGhsCount() != null ? report.getGhsCount() : 0;
			Integer sumCpiCount = report.getCpiCount() != null ? report.getCpiCount() : 0;
			Integer sumDayShiftCountCount = report.getDayShiftCount() != null ? report.getDayShiftCount() : 0;
			Integer sumNightShiftCount = report.getNightShiftCount() != null ? report.getNightShiftCount() : 0;
			
			sumGhsCount += data.getGhsCount() != null ? data.getGhsCount() : 0;
			sumCpiCount += data.getCpiCount() != null ? data.getCpiCount() : 0;
			sumDayShiftCountCount += data.getDayShiftCount() != null ? data.getDayShiftCount() : 0;
			sumNightShiftCount += data.getNightShiftCount() != null ? data.getNightShiftCount() : 0;
			
			report.setGhsCount(sumGhsCount);
			report.setDayShiftCount(sumDayShiftCountCount);
			report.setNightShiftCount(sumNightShiftCount);
			report.setCpiCount(sumCpiCount);
		}
		
//		for (SummaryOfSupervisoryVisitSpotCheckRemark remark : remarks) {
//
//			if (data.getUserId() == remark.getUserId()) {
//				
//				Calendar calFrom = Calendar.getInstance();
//				calFrom.setTime(remark.getFromDate());
//				
//				Calendar calTo = Calendar.getInstance();
//				calTo.setTime(remark.getToDate());
//				
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
//				
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0101")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0101")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 0 || calTo.get(Calendar.MONTH) == 0){
//						report.setJanStyled((report.getJanStyled()==null?"":report.getJanStyled() + " , ") + remark.getSequence()+ " ");
//					}
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0201")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0201")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 1 || calTo.get(Calendar.MONTH) == 1){
//						report.setFebStyled((report.getFebStyled()==null?"":report.getFebStyled() + " , ") + remark.getSequence()+ " ");
//					} 
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0301")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0301")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 2 || calTo.get(Calendar.MONTH) == 2){
//						report.setMarStyled((report.getMarStyled()==null?"":report.getMarStyled() + " , ") + remark.getSequence()+ " ");
//					}					
//
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0401")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0401")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 3 || calTo.get(Calendar.MONTH) == 3){
//						report.setAprStyled((report.getAprStyled()==null?"":report.getAprStyled() + " , ") + remark.getSequence()+ " ");
//					}		
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0501")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0501")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 4 || calTo.get(Calendar.MONTH) == 4) {
//						report.setMayStyled((report.getMayStyled()==null?"":report.getMayStyled() + " , ") + remark.getSequence()+ " ");
//					}		
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0601")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0601")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 5 || calTo.get(Calendar.MONTH) == 5){
//						report.setJunStyled((report.getJunStyled()==null?"":report.getJunStyled() + " , ") + remark.getSequence()+ " ");
//					}		
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0701")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0701")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 6 || calTo.get(Calendar.MONTH) == 6){
//						report.setJulStyled((report.getJulStyled()==null?"":report.getJulStyled() + " , ") + remark.getSequence()+ " ");
//					}	
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0801")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0801")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 7 || calTo.get(Calendar.MONTH) == 7){
//						report.setAugStyled((report.getAugStyled()==null?"":report.getAugStyled() + " , ") + remark.getSequence()+ " ");
//					}					
//
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"0901")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"0901")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 8 || calTo.get(Calendar.MONTH) == 8){
//						report.setSepStyled((report.getSepStyled()==null?"":report.getSepStyled() + " , ") + remark.getSequence()+ " ");
//					}		
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"1001")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"1001")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 9 || calTo.get(Calendar.MONTH) == 9){
//						report.setOctStyled((report.getOctStyled()==null?"":report.getOctStyled() + " , ") + remark.getSequence()+ " ");
//					}		
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"1101")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"1101")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 10 || calTo.get(Calendar.MONTH) == 10){
//						report.setNovStyled((report.getNovStyled()==null?"":report.getNovStyled() + " , ") + remark.getSequence()+ " ");
//					}		
//					
//					if ((remark.getFromDate().compareTo(formatter.parse(year +"1201")) < 0 && remark.getToDate().compareTo(formatter.parse(year +"1201")) > 0 )
//						|| calFrom.get(Calendar.MONTH) == 11 || calTo.get(Calendar.MONTH) == 11){
//						report.setDecStyled((report.getDecStyled()==null?"":report.getDecStyled() + " , ") +remark.getSequence()+ " ");
//					}
//
//			}
//		}

		if (data.getMonth() != null) {
			switch (data.getMonth()) {
				case 1:	report.setJanStyled((report.getJanStyled()==null?"":report.getJanStyled() + " , ") +  getStyledText(data));
						break;
				case 2:	report.setFebStyled((report.getFebStyled()==null?"":report.getFebStyled() + " , ") +getStyledText(data));
						break;
				case 3:	report.setMarStyled((report.getMarStyled()==null?"":report.getMarStyled() + " , ") +getStyledText(data));
						break;
				case 4:	report.setAprStyled((report.getAprStyled()==null?"":report.getAprStyled() + " , ") +getStyledText(data));
						break;
				case 5:	report.setMayStyled((report.getMayStyled()==null?"":report.getMayStyled() + " , ") +getStyledText(data));
						break;
				case 6:	report.setJunStyled((report.getJunStyled()==null?"":report.getJunStyled() + " , ") +getStyledText(data));
						break;
				case 7:	report.setJulStyled((report.getJulStyled()==null?"":report.getJulStyled() + " , ") +getStyledText(data));
						break;
				case 8:	report.setAugStyled((report.getAugStyled()==null?"":report.getAugStyled() + " , ") +getStyledText(data));
						break;
				case 9:	report.setSepStyled((report.getSepStyled()==null?"":report.getSepStyled() + " , ") +getStyledText(data));
						break;
				case 10:	report.setOctStyled((report.getOctStyled()==null?"":report.getOctStyled() + " , ") +getStyledText(data));
							break;
				case 11:	report.setNovStyled((report.getNovStyled()==null?"":report.getNovStyled() + " , ") +getStyledText(data));
							break;
				case 12:	report.setDecStyled((report.getDecStyled()==null?"":report.getDecStyled() + " , ") +getStyledText(data));
							break;
			}
		}
		return report;
	}
	
	private String getStyledText(SummaryOfSupervisoryVisitSpotCheckData data) {

		String styledText = null;
		
		if (data.getGhsSession() == null || data.getGhsSession() > 2 || data.getGhsSession() < 1 || 
				(data.getSurvey() != null && (data.getSurvey().equals(SystemConstant.SURVEY_1)||data.getSurvey().equals(SystemConstant.SURVEY_3)))) {
			styledText = "<style forecolor=\"black\">" + data.getDate() + "</style> ";
		} else if (data.getGhsSession() == 1) {
			styledText = data.getDate() + "<sup>*</sup>";
		} else if (data.getGhsSession() == 2 ) {
			styledText = data.getDate() + "<sup>#</sup>";
		}

		if (data.getSuperRankCode() != null && data.getSuperRankCode().equals("SCSO")) {
			styledText += " (SCSO)";
		}
		
		if (data.getSuperRankCode() != null && data.getSuperRankCode().equals("STAT")) {
			styledText += " (Stat)";
		}
		
		return styledText;
	}
	
	private static String getString(int n) {
	    char[] buf = new char[(int) Math.floor(Math.log(25 * (n + 1)) / Math.log(26))];
	    for (int i = buf.length - 1; i >= 0; i--) {
	        n--;
	        buf[i] = (char) ('a' + n % 26);
	        n /= 26;
	    }
	    return new String(buf);
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		SummaryOfSupervisoryVisitSpotCheckCriteria criteria = (SummaryOfSupervisoryVisitSpotCheckCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s", criteria.getYear()));
		descBuilder.append("\n");

		if (criteria.getUserId() != null && criteria.getUserId().size() > 0) {
			List<User> users = userDao.getUsersByIds(criteria.getUserId());
			if(users != null && users.size() > 0) {
				List<String> userStr = new ArrayList<String>();
				for(User user : users) {
					String str = user.getStaffCode() + " - " + user.getEnglishName();
					userStr.add(str);
				}
				descBuilder.append(String.format("Officer: %s", StringUtils.join(userStr, ", ")));
			}
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
