package capi.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import capi.dal.PECheckTaskDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.CheckTheChecker;
import capi.model.report.CheckTheCheckerCriteria;
import capi.model.report.DocxExportModel;
import capi.service.CommonService;

@Service("CheckTheCheckerService")
public class CheckTheCheckerService extends ReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private PECheckTaskDao peCheckTaskDao;

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		CheckTheCheckerCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), CheckTheCheckerCriteria.class);
		
		String telNo = criteria.getTelNo();
		String faxNo = criteria.getFaxNo();
		String signatory = criteria.getSignatory();
		String chineseOfficeAddress = criteria.getChineseOfficeAddress();
		String englishOfficeAddress = criteria.getEnglishOfficeAddress();
		
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		parameters.put("refMonth", criteria.getRefMonth());
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		int noOfCases = criteria.getNoOfCases();
		
		List<User> fieldHeads = userDao.getActiveUsersWithAnyAuthorityLevel(2);
		User fieldHead = null;
		if(fieldHeads != null && fieldHeads.size() > 0) {
			fieldHead = fieldHeads.get(0);
		}
		
		User user = userDao.getActiveSCSOUserForReport();
		
		List<DocxExportModel> wordDatas = new ArrayList<DocxExportModel>();
		
		List<CheckTheChecker> excelDatas = new ArrayList<CheckTheChecker> ();
		
		//Check the checker is from each team if the no of cases is >= 4
		Boolean isFromSameTeam = false;
		Set<String> allTeam = Sets.newHashSet(userDao.getAllTeamForCheckerReport());
		Set<String> fullPECheckTeamSet = Sets.newHashSet(peCheckTaskDao.getFullPECheckListForCheckTheCheckerReport(refMonth));
		List<CheckTheChecker> progress = new ArrayList<CheckTheChecker>();
		
		if (fullPECheckTeamSet.equals(allTeam) && noOfCases >= 4) {
			while(!isFromSameTeam){
				progress = peCheckTaskDao.getCheckTheCheckerReport(refMonth, noOfCases);
				if (progress.size() == 0 || progress.size() < 4) break;
				Set<String> checkerTeam = new HashSet<>();
				for(CheckTheChecker checker: progress) {
					checkerTeam.add(checker.getTeam());
				}
				isFromSameTeam = checkerTeam.equals(allTeam);
			}
		} else if (noOfCases < 4) {
			progress = peCheckTaskDao.getCheckTheCheckerReport(refMonth, noOfCases);
		}
		
		List<CheckTheChecker.OutletTypeMapping> outletTypes = peCheckTaskDao.getOutletTypeOfCheckTheCheckerReport(refMonth);
		for(CheckTheChecker consequence : progress) {
			StringBuilder sb = new StringBuilder();
			for(CheckTheChecker.OutletTypeMapping outletType : outletTypes) {
				if(outletType.getPeCheckTaskId().equals(consequence.getPeCheckTaskId())) {
					sb.append(outletType.getShortCode());
					sb.append(", ");
				}
			}
			if(sb.length() > 2) {
				sb.delete(sb.length()-2, sb.length());
				if("null".equals(sb.toString()))
					consequence.setOutletType(null);
				else
					consequence.setOutletType(sb.toString());
			} else {
				consequence.setOutletType(null);
			}
		}
		
		for(int i = 0; i < progress.size(); i++) {
			CheckTheChecker excelData = progress.get(i);
			
			DocxExportModel wordData = new DocxExportModel();
			Hashtable<String, String> table = new Hashtable<String, String>();
			
			table.put("telNo", commonService.formatTelNo(telNo));
			table.put("faxNo", commonService.formatTelNo(faxNo));
			
			table.put("firmName", excelData.getFirmName());
			
			String peCheckDate = excelData.getPeCheckDate();
			Date peCheck = commonService.getDate(peCheckDate);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(peCheck);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			
			table.put("peCheckDateYear", String.valueOf(year));
			table.put("peCheckDateMonth", String.valueOf(month));
			table.put("peCheckDateDay", String.valueOf(day));
			
			String enumerationDate = excelData.getEnumerationDate();
			Date enumeration = commonService.getDate(enumerationDate);
			
			cal.setTime(enumeration);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			
			table.put("enumerationDateYear", String.valueOf(year));
			table.put("enumerationDateMonth", String.valueOf(month));
			table.put("enumerationDateDay", String.valueOf(day));
			
			//table.put("fieldHead", fieldHead.getChineseName());
			
			if("F".equals(user.getGender()))
				//table.put("gender", "女士");
				table.put("fieldHead", user.getChineseName() + "女士");
			else if("M".equals(fieldHead.getGender()))
				//table.put("gender", "先生");
				table.put("fieldHead", user.getChineseName() + "先生");
			else
				//table.put("gender", "");
				table.put("fieldHead", user.getChineseName() + "");
			
			table.put("officePhoneNo", commonService.formatTelNo(user.getOfficePhoneNo()));
			
			table.put("signatory", signatory);
			
			Date today = new Date();
			
			cal.setTime(today);
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			
			table.put("generationYear", String.valueOf(year));
			table.put("generationMonth", String.valueOf(month));
			table.put("generationDay", String.valueOf(day));
			
			table.put("chineseOfficeAddress", chineseOfficeAddress);
			table.put("englishOfficeAddress", englishOfficeAddress);
			
			wordData.setFilename("LETTER_" + excelData.getPeCheckTaskId() + ".docx");
			wordData.setData(table);
			wordDatas.add(wordData);
			
			excelDatas.add(excelData);
			
			if(criteria.getNoOfCases() != null && criteria.getNoOfCases() > 0) {
				if(i == criteria.getNoOfCases() - 1) {
					break;
				}
			}
		}
		
		parameters.put("IS_IGNORE_PAGINATION", Boolean.TRUE);
		JasperPrint jasperPrint = this.getJasperPrint(task, "CheckTheChecker", excelDatas, parameters, "CheckTheChecker.xlsx");
		
		String path = this.exportMultiDocx(task, "MB9050", wordDatas, this.getFunctionCode(), jasperPrint);
		
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9050";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		CheckTheCheckerCriteria criteria = (CheckTheCheckerCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Ref. Month: %s", criteria.getRefMonth()));
		
		if(criteria.getNoOfCases() != null && criteria.getNoOfCases() > 0) {
			descBuilder.append("\n");
			descBuilder.append(String.format("No. of Case(s): %s", criteria.getNoOfCases()));
		}
		if(!StringUtils.isEmpty(criteria.getTelNo())) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Tel. No.: %s", criteria.getTelNo()));
		}
		if(!StringUtils.isEmpty(criteria.getFaxNo())) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Fax No.: %s", criteria.getFaxNo()));
		}
		if(!StringUtils.isEmpty(criteria.getSignatory())) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Name of signatory: %s", criteria.getSignatory()));
		}
		if(!StringUtils.isEmpty(criteria.getChineseOfficeAddress())) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Office(Chinese): %s", criteria.getChineseOfficeAddress()));
		}
		if(!StringUtils.isEmpty(criteria.getEnglishOfficeAddress())) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Office(English): %s", criteria.getEnglishOfficeAddress()));
		}
		
		User creator = userDao.findById(userId);
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(ReportServiceBase.ZIP);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;
	}

}
