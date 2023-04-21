package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import capi.model.report.RUACaseReportIndividualCriteria;
import capi.model.report.RUACaseReportIndividualReport;
import capi.service.CommonService;
import capi.service.assignmentManagement.QuotationService;

@Service("RUACaseReportIndividualService")
public class RUACaseReportIndividualService extends DataReportServiceBase{

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
	private QuotationService quotationService;
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9020";
	}
	
	private static final String[] headers = new String[]{
			"Reference Month", "Purpose", "Team", "Rank", "Field Officer Name", "Field Officer Code"
			, "Outlet Type", "Outlet Type English name"
		};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		RUACaseReportIndividualCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), RUACaseReportIndividualCriteria.class);
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		List<RUACaseReportIndividualReport> results = quotationRecordDao.getRUAIndividualReport(refMonth, criteria.getOfficerIds()
				, criteria.getOutletTypeShortCode(), criteria.getDistrictIds());
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "RUA Case Report (Individual)");
		
		List<String> districts = districtDao.getDistrictCodeByIds(criteria.getDistrictIds());
	
		sheet = createHeader2(sheet, districts);
		
		int rowCnt = 1;
		
		for (RUACaseReportIndividualReport data : results){
			List<RUACaseReportIndividualReport.districtCodeMapping> districtCodeMapping = districtDao.getRUAIndividualReportNewRecruitment(data.getSurveyMonthId()
					, data.getUserId(), data.getOutletTypeCode(), data.getPurposeId(), criteria.getDistrictIds());
			
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
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeName());
			
			cellCnt++;
			int newRecruitment = 0;
			for (RUACaseReportIndividualReport.districtCodeMapping district : districtCodeMapping) {
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
		// End Generate Excel
		
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
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		//TODO Auto-generated method stub
		RUACaseReportIndividualCriteria criteria = (RUACaseReportIndividualCriteria)criteriaObject;
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
			descBuilder.append(String.format("Officer: %s", StringUtils.join(codes, ", ")));
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
		if(criteria.getOutletTypeShortCode() != null && criteria.getOutletTypeShortCode().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (String item : criteria.getOutletTypeShortCode()) {
				codes.add(item);
			}
			descBuilder.append(String.format("Outlet Types: %s", StringUtils.join(codes, ", ")));
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
	
	public SXSSFSheet createHeader2(SXSSFSheet sheet, List<String> districts){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cnt = headers.length;
		
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
