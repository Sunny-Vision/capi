package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.DistrictDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.report.DynamicExperienceSummaryReport;
import capi.model.report.ExperienceSummaryCriteria;
import capi.model.report.ExperienceSummaryReport;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("ExperienceSummaryService")
public class ExperienceSummaryService extends DataReportServiceBase{
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private DistrictDao districtDao;

	private List<String> headers;
	
	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		ExperienceSummaryCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), ExperienceSummaryCriteria.class);
		
		Date startReferenceMonth = this.commonService.getMonth(criteria.getStartMonthStr());
		Date endReferenceMonth = this.commonService.getMonth(criteria.getEndMonthStr());
		List<ExperienceSummaryReport> process = this.assignmentDao.getExperienceSummaryReport(criteria.getPurposeId(),criteria.getSurveyId(),criteria.getTeamId(),
				criteria.getOfficerId(),startReferenceMonth,endReferenceMonth,criteria.getMarketType(),criteria.getCpiQuotationType());
		
		List<DynamicExperienceSummaryReport> results = setExperienceSummaryReport(process);
		List<DistrictEditModel> district = districtDao.getAllDistrictCode();
		setHeader(district);
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (DynamicExperienceSummaryReport data : results){
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiQuotationType()==1? "Market":"Non-Market");
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiSurveyForm());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			for(DistrictEditModel d : district) {
				cellCnt++;
				cell = row.createCell(cellCnt);
				int value = 0;
				for(Map.Entry<String,Integer> entry : data.getDistrictAndCount().entrySet()) {
					if(entry.getKey().equals(d.getCode())) {
						value = entry.getValue();
						break;
					}
				}
				cell.setCellValue(value);
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		List<ExperienceSummaryReport> process1 = this.assignmentDao.getExperienceSummaryReportQuotation(criteria.getPurposeId(),criteria.getSurveyId(),criteria.getTeamId(),
				criteria.getOfficerId(),startReferenceMonth,endReferenceMonth,criteria.getMarketType(),criteria.getCpiQuotationType());
		
		List<DynamicExperienceSummaryReport> results1 = setExperienceSummaryReport(process1);
		
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFRow row = sheet1.createRow(0);
		int i = 0;
		for(String header : headers) {
			SXSSFCell cell = row.createCell(i);
			cell.setCellValue(header);
			i++;
		}
		
		rowCnt = 1;
		
		for (DynamicExperienceSummaryReport data : results1){
			
			row = sheet1.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiQuotationType()==1? "Market":"Non-Market");
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiSurveyForm());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			for(DistrictEditModel d : district) {
				cellCnt++;
				cell = row.createCell(cellCnt);
				int value = 0;
				for(Map.Entry<String,Integer> entry : data.getDistrictAndCount().entrySet()) {
					if(entry.getKey().equals(d.getCode())) {
						value = entry.getValue();
						break;
					}
				}
				cell.setCellValue(value);
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Assignments (by officers)");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Quotation Records (by officers)");
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
	public String getFunctionCode() {
		return "RF9029";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		ExperienceSummaryCriteria criteria = (ExperienceSummaryCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		List<Integer> officerId = criteria.getOfficerId();
		Integer cpiQuotationType = criteria.getCpiQuotationType();
		
		descBuilder.append("Purpose: ");
		Purpose p = null;
		if(criteria.getPurposeId() != null){
			p = this.purposeDao.findById(criteria.getPurposeId());
			descBuilder.append(String.format("%s ", p.getName()));
		}else{
			descBuilder.append("-");
		}
		
		descBuilder.append("\n");
		descBuilder.append("Survey: ");
		if(criteria.getSurveyId() != null){
			List<String> codes = new ArrayList<String>();
			for(String surveyName: criteria.getSurveyId()){
				codes.add(surveyName);
			}
			descBuilder.append(String.format("%s", StringUtils.join(codes, ", ")));
		}else{
			descBuilder.append("-");
		}
		descBuilder.append("\n");
		descBuilder.append("Market: ");
		if(criteria.getMarketType() == null){
			descBuilder.append("All");
		}else{
			 if(criteria.getMarketType() == 1){
				 descBuilder.append("Market");
			 }else if(criteria.getMarketType() == 2){
				 descBuilder.append("Non-Market");
			 }else{
				 descBuilder.append("-");
			 }
		}
		descBuilder.append("\n");
		descBuilder.append("ReferenceMonth: " + criteria.getStartMonthStr()+" to "+criteria.getEndMonthStr());
		descBuilder.append("\n");
		descBuilder.append("Field Staff: ");
		if(officerId != null){
			List<String> codes = new ArrayList<String>();
			for(Integer uid: officerId){
				User user = userDao.findById(uid);
				codes.add(String.format("%s - %s", user.getStaffCode(), user.getEnglishName()));
			}
			descBuilder.append(String.format("%s", StringUtils.join(codes, ", ")));
		}else{
			descBuilder.append("-");
		}
		descBuilder.append("\n");
		descBuilder.append("CPI Quotation Type: ");
		if(cpiQuotationType != null){
			switch (cpiQuotationType){
				case 1:
					descBuilder.append("Market");
					break;
				case 2:
					descBuilder.append("Supermarket");
					break;
				case 3:
					descBuilder.append("Batch");
					break;
				case 4:
					descBuilder.append("Others");
					break;
			}
		}else{
			descBuilder.append("-");
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
	
	@Override
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
	private void setHeader(List<DistrictEditModel> district) {
		String[] header = {"ReferenceMonth","Team","Post","Field Officer Code","Field Officer Name","Market","CPI Survey Form","Purpose"};
		List<String >temp = new ArrayList<String>();
		temp.addAll(Arrays.asList(header));
		for(DistrictEditModel d : district) {
			temp.add(d.getCode());
		}
		temp.add("Total");
		headers = temp;
	}
	
	private List<DynamicExperienceSummaryReport> setExperienceSummaryReport(List<ExperienceSummaryReport> results){
		List<DynamicExperienceSummaryReport> list = new ArrayList<DynamicExperienceSummaryReport>();
		Map<String,Integer> innerMap = new HashMap<String,Integer>();
			
		ExperienceSummaryReport temp = null;
		for(ExperienceSummaryReport data : results) {
			if(temp != null && (!data.getReferenceMonth().equals(temp.getReferenceMonth()) || !data.getTeam().equals(temp.getTeam()) ||
					!data.getRank().equals(temp.getRank()) || !data.getStaffCode().equals(temp.getStaffCode()) || 
					!data.getCpiQuotationType().equals(temp.getCpiQuotationType()) || !data.getPurpose().equals(temp.getPurpose()))) {
				DynamicExperienceSummaryReport d = new DynamicExperienceSummaryReport(temp,innerMap);
				list.add(d);
				innerMap = new HashMap<String,Integer>();	
			}
			innerMap.put(data.getDistrict(), data.getCount());
			temp = data;	
		}
		list.add(new DynamicExperienceSummaryReport(temp,innerMap));
		
		return list;
	}
}
