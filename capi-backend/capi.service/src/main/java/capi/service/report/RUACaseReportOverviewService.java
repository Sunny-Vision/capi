package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.DistrictDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ReportTaskDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UserDao;
import capi.entity.District;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.RUACaseReportOverviewCriteria;
import capi.model.report.RUACaseReportOverviewReport;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentManagement.QuotationService;

@Service("RUACaseReportOverviewService")
public class RUACaseReportOverviewService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private QuotationDao quotationDao;

	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	SystemConfigurationDao systemConfigurationDao;
	
	@Autowired
	private CommonService commonService;

	@Autowired
	private UserService userService;

	@Autowired
	private QuotationService quotationService;
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9019";
	}
	
	private static final String[] headers = new String[] {
			"Reference Month", "Purpose", "Team", "Rank", "Field Officer Name",
			"Field Officer Code"
			};
	
	private static final String[] headers2 = new String[] {
			"Reference Month", "Purpose", "Team"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		RUACaseReportOverviewCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), RUACaseReportOverviewCriteria.class);

		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		List<RUACaseReportOverviewReport> results = quotationRecordDao.getRUAOverviewReport(refMonth, criteria.getOfficerIds(), criteria.getDistrictIds());
		
		List<RUACaseReportOverviewReport> teamResults = quotationRecordDao.getRUAOverviewTeamReport(refMonth, criteria.getOfficerIds(), criteria.getDistrictIds());
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		SXSSFSheet sheet1 = workBook.createSheet();
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "RUA Case Report");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Summary (by team)");
		
		List<String> districts = districtDao.getDistrictCodeByIds(criteria.getDistrictIds());
		sheet = createDistrictHeader(sheet, districts, headers);
		
		fillSheet0(sheet, results, criteria.getDistrictIds());
		
		sheet1 = createHeader2(sheet1);
		sheet1 = createDistrictHeader(sheet1, districts, headers2);
		
		fillSheet1(sheet1, teamResults, criteria.getDistrictIds());
		
		// Output Excel 
		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			task.setPath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	public void fillSheet0(SXSSFSheet sheet, List<RUACaseReportOverviewReport> results, List<Integer> districtIds) throws Exception{
		int rowCnt = 1;
		
		for (RUACaseReportOverviewReport data : results){
			List<RUACaseReportOverviewReport.districtCodeMapping> districtCodeMapping = 
					districtDao.getRUAOverviewReportNewRecruitment(data.getSurveyMonthId(), data.getUserId()
							, data.getPurposeId(), districtIds, null);
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt++;
			int newRecruitment = 0;
			for (RUACaseReportOverviewReport.districtCodeMapping district : districtCodeMapping) {
				newRecruitment += district.getNewRecruitment();
				SXSSFCell cellHeader = row.createCell(cellCnt);
				cellHeader.setCellValue(district.getNewRecruitment());
				cellCnt++;
			}
			
			cell = row.createCell(cellCnt);
			cell.setCellValue(newRecruitment);
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet0
	}
	
	public void fillSheet1(SXSSFSheet sheet, List<RUACaseReportOverviewReport> results, List<Integer> districtIds) throws Exception{
		int rowCnt = 1;
		
		/**
		 * Keys(String):	Purpose, Team, District
		 * Value(Integer):	NewRecuritment		
		 */
		MultiKeyMap<String, Integer> multiKeyMap = new MultiKeyMap<>();
		
		// Group data
		for(RUACaseReportOverviewReport data : results) {
			List<RUACaseReportOverviewReport.districtCodeMapping> districtCodeMapping = 
					districtDao.getRUAOverviewReportNewRecruitment(data.getSurveyMonthId(), data.getUserId()
							, data.getPurposeId(), districtIds, data.getTeam());
			
			for (RUACaseReportOverviewReport.districtCodeMapping district : districtCodeMapping) {
				
				if(multiKeyMap.containsKey(data.getPurpose(), data.getTeam(), district.getDistrict())) {
					
					int tempNewTempRecruitment = multiKeyMap.get(data.getPurpose(), data.getTeam(), district.getDistrict());
					tempNewTempRecruitment += district.getNewRecruitment();
					multiKeyMap.put(data.getPurpose(), data.getTeam(), district.getDistrict(), tempNewTempRecruitment);
					
				} else {
					
					multiKeyMap.put(data.getPurpose(), data.getTeam(), district.getDistrict(), district.getNewRecruitment());
					
				}
			}
			
		}

		// For displaying non-duplicate team
		MultiKeyMap<String, Boolean> flagMap = new MultiKeyMap<>();
		
		// Generate Excel
		for(RUACaseReportOverviewReport data : results) {
			
			// If purpose code with a team has been displayed
			//	then next data, because same purpose code and team has been grouped
			if(flagMap.containsKey(data.getPurpose(), data.getTeam())) {
				if(flagMap.get(data.getPurpose(), data.getTeam())) {
					continue;
				}
			}
			
			List<RUACaseReportOverviewReport.districtCodeMapping> districtCodeMapping = 
					districtDao.getRUAOverviewReportNewRecruitment(data.getSurveyMonthId(), data.getUserId()
							, data.getPurposeId(), districtIds, data.getTeam());
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt++;
			int newRecruitment = 0;
			for (RUACaseReportOverviewReport.districtCodeMapping district : districtCodeMapping) {
				
				int newRecuritmentInMap = multiKeyMap.get(data.getPurpose(), data.getTeam(), district.getDistrict());
				newRecruitment += newRecuritmentInMap;
				SXSSFCell cellHeader = row.createCell(cellCnt);
				cellHeader.setCellValue(newRecuritmentInMap);
				cellCnt++;
				flagMap.put(data.getPurpose(), data.getTeam(), true);
				
			}
			
			cell = row.createCell(cellCnt);
			cell.setCellValue(newRecruitment);
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
	
	}
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		//TODO Auto-generated method stub
		RUACaseReportOverviewCriteria criteria = (RUACaseReportOverviewCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if(criteria.getOfficerIds() != null && criteria.getOfficerIds().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			List<User> users = userDao.getUsersByIds(criteria.getOfficerIds());
			for (User user : users) {
				codes.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Officers: %s", StringUtils.join(codes, ", ")));
		}
		if(criteria.getDistrictIds() != null && criteria.getDistrictIds().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			List<District> entities = districtDao.getDistrictsByIds(criteria.getDistrictIds());
			for (District item : entities) {
				codes.add(item.getCode());
			}
			descBuilder.append(String.format("Districts: %s", StringUtils.join(codes, ", ")));
		}
		if (descBuilder.length() > 0) descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));
		
		if (descBuilder.length() > 0) descBuilder.append("\n");
		
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
	
	@Override
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
	public SXSSFSheet createHeader2(SXSSFSheet sheet){
		int cnt = 0;
		
		SXSSFRow row = sheet.createRow(cnt);
		for (String header : headers2){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		return sheet;
	}
	
	public SXSSFSheet createDistrictHeader(SXSSFSheet sheet, List<String> districts, String[] header){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cnt = header.length;
		
		for (String code : districts) {
			SXSSFCell cellHeader = row.createCell(cnt);
			cellHeader.setCellValue(code);
			cnt++;
		}
		
		SXSSFCell cellHeader = row.createCell(cnt);
		cellHeader.setCellValue("Total");
		
		return sheet;
	}
}
