package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.AllocationBatch;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryOfProgressReportByFieldOfficerCriteria;
import capi.model.report.SummaryOfProgressReport;
import capi.service.CommonService;

@Service("SummaryOfProgressReportByFieldOfficerService")
public class SummaryOfProgressReportByFieldOfficerService extends DataReportServiceBase{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private CommonService commonService;
	
	private final static String headers[] = {
			"Reference Month",
			"Field Officer Code",
			"Field Officer Name",
			"Team",
			"Survey",
			"Purpose",
			"Allocation Batch",
			"Post",
			"Total no. of assignments",
			"Total no. of outstanding assignments",
			"% of assignments completed",
			"Total no. of quotation records",
			"Total no. of outstanding quotation records",
			"% of quotation records completed"
	};
	
	private final static String headers1[] = {
			"Reference Month",
			"Survey",
			"Purpose",
			"Allocation Batch",
			"Team",
			"Total no. of assignments",
			"Total no. of outstanding assignments",
			"% of assignments completed",
			"Total no. of quotation records",
			"Total no. of outstanding quotation records",
			"% of quotation records completed"
	};
	
	private final static String headers2[] = {
			"Reference Month",
			"Survey",
			"Team",
			"Post",
			"Field Officer Code",
			"Field Officer Name",
			"Total no. of assignments",
			"Total no. of outstanding assignments",
			"% of assignments completed"
	};
	
	private final static String headers3[] = {
			"Reference Month",
			"Survey",
			"Team",
			"Total no. of assignments",
			"Total no. of outstanding assignments",
			"% of assignments completed"
	};
	
	private final static String headers4[] = {
			"Reference Month",
			"Survey",
			"Total no. of assignments",
			"Total no. of outstanding assignments",
			"% of assignments completed"
	};

	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfProgressReportByFieldOfficerCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfProgressReportByFieldOfficerCriteria.class);
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		List<String> purposeCode = new ArrayList<String>();
		
		if (criteria.getPurposeIds() != null && criteria.getPurposeIds().size() > 0) {
			List<Purpose> purposes = purposeDao.getByIds(criteria.getPurposeIds());
			for (Purpose p : purposes) {
				purposeCode.add(p.getCode());
			}
		}
		
		List<SummaryOfProgressReport> progress = assignmentDao.getSummaryOfProgressReportByFieldOfficer(criteria.getPurposeIds(), criteria.getAllocationBatchIds(), criteria.getOfficerIds(), refMonth);
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFSheet sheet2 = workBook.createSheet();
		SXSSFSheet sheet3 = workBook.createSheet();
		SXSSFSheet sheet4 = workBook.createSheet();
		
		sheet1 = createSheetHeader(sheet1, headers1);
		sheet2 = createSheetHeader(sheet2, headers2);
		sheet3 = createSheetHeader(sheet3, headers3);
		sheet4 = createSheetHeader(sheet4, headers4);
		
		// Sheet 0
		fillSheet0(sheet, progress);
		
		// Sheet 1
		progress = assignmentDao.getSummaryOfProgressReportByTeam(criteria.getPurposeIds(), criteria.getAllocationBatchIds(), criteria.getOfficerIds(), refMonth);
		fillSheet1(sheet1, progress);
		
		// Sheet 2
		progress = assignmentDao.getSummaryOfProgressImportedReportByFieldOfficer(purposeCode, criteria.getOfficerIds(), refMonth);
		fillSheet2(sheet2, progress);
		
		// Sheet 3
		progress = assignmentDao.getSummaryOfProgressImportedReportByTeam(purposeCode, criteria.getOfficerIds(), refMonth);
		fillSheet3(sheet3, progress);
		
		// Sheet 4
		progress = assignmentDao.getSummaryOfProgressImportedReportBySurvey(purposeCode, criteria.getOfficerIds(), refMonth);
		fillSheet4(sheet4, progress);
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "MRPS Summary(by officer)");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "MRPS Summary (by team)");
		workBook.setSheetName(workBook.getSheetIndex(sheet2), "Import Assignment (by officer)");
		workBook.setSheetName(workBook.getSheetIndex(sheet3), "Import Assignment (by team)");
		workBook.setSheetName(workBook.getSheetIndex(sheet4), "Import Assignment (by purpose)");
		
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
	
	public void fillSheet0(SXSSFSheet sheet, List<SummaryOfProgressReport> results) throws Exception {
		int rowCnt = 1;
		
		for (SummaryOfProgressReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data Start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBatchName());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment());
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment() - data.getCompletedAssignment());
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			if (data.getTotalAssignment() != null && data.getTotalAssignment()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedAssignment(), data.getTotalAssignment()));
			} else {
				cell.setCellValue(0);
			}
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalQuotationRecord());
			
			cellCnt = 12;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalQuotationRecord() - data.getCompletedQuotationRecord());
			
			cellCnt = 13;
			cell = row.createCell(cellCnt);
			if (data.getTotalQuotationRecord() != null && data.getTotalQuotationRecord()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedQuotationRecord(), data.getTotalQuotationRecord()));
			} else {
				cell.setCellValue(0);
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet
	}
	
	public void fillSheet1(SXSSFSheet sheet, List<SummaryOfProgressReport> results) throws Exception {
		int rowCnt = 1;
		
		for (SummaryOfProgressReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data Start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));

			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBatchName());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment() - data.getCompletedAssignment());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			if (data.getTotalAssignment() != null && data.getTotalAssignment()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedAssignment(), data.getTotalAssignment()));
			} else {
				cell.setCellValue(0);
			}
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalQuotationRecord());
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalQuotationRecord() - data.getCompletedQuotationRecord());
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			if (data.getTotalQuotationRecord() != null && data.getTotalQuotationRecord()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedQuotationRecord(), data.getTotalQuotationRecord()));
			} else {
				cell.setCellValue(0);
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet
	}

	public void fillSheet2(SXSSFSheet sheet, List<SummaryOfProgressReport> results) throws Exception {
		int rowCnt = 1;
		
		for (SummaryOfProgressReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data Start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment() - data.getCompletedAssignment());
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			if (data.getTotalAssignment() != null && data.getTotalAssignment()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedAssignment(), data.getTotalAssignment()));
			} else {
				cell.setCellValue(0);
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet
	}
	
	public void fillSheet3(SXSSFSheet sheet, List<SummaryOfProgressReport> results) throws Exception {
		int rowCnt = 1;
		
		for (SummaryOfProgressReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data Start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment() - data.getCompletedAssignment());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			if (data.getTotalAssignment() != null && data.getTotalAssignment()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedAssignment(), data.getTotalAssignment()));
			} else {
				cell.setCellValue(0);
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet
	}
	
	public void fillSheet4(SXSSFSheet sheet, List<SummaryOfProgressReport> results) throws Exception {
		int rowCnt = 1;
		
		for (SummaryOfProgressReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data Start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotalAssignment() - data.getCompletedAssignment());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			if (data.getTotalAssignment() != null && data.getTotalAssignment()>0) {
				cell.setCellValue(calendatePercentage(data.getCompletedAssignment(), data.getTotalAssignment()));
			} else {
				cell.setCellValue(0);
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet
	}
	
	@Override
	public String getFunctionCode() {
		return "RF9027";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		SummaryOfProgressReportByFieldOfficerCriteria criteria = (SummaryOfProgressReportByFieldOfficerCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getPurposeIds() != null && criteria.getPurposeIds().size() > 0) {
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurposeIds());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes) {
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
			descBuilder.append("\n");
		}

		if (criteria.getAllocationBatchIds() != null && criteria.getAllocationBatchIds().size() > 0) {
			List<AllocationBatch> allocationBatchs = allocationBatchDao
					.getAllocationBatchByIds(criteria.getAllocationBatchIds());
			List<String> names = new ArrayList<String>();
			for (AllocationBatch allocationBatch : allocationBatchs) {
				names.add(allocationBatch.getBatchName());
			}
			descBuilder.append(String.format("Allocation Batch: %s ", StringUtils.join(names, ", ")));
			descBuilder.append("\n");
		}

		if (criteria.getOfficerIds() != null && criteria.getOfficerIds().size() > 0) {
			List<User> users = userDao.getUsersByIds(criteria.getOfficerIds());
			List<String> officers = new ArrayList<String>();
			for (User user : users) {
				officers.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Officer: %s ", StringUtils.join(officers, ", ")));
			descBuilder.append("\n");
		}
		
		descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));

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
	public void createHeader(SXSSFRow row) {
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
	public SXSSFSheet createSheetHeader(SXSSFSheet sheet, String[] headers){
		int cnt = 0;
		
		SXSSFRow row = sheet.createRow(cnt);
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		return sheet;
	}

	private Double calendatePercentage(Integer completed, Integer total) {
		BigDecimal results = null;
		results = BigDecimal.valueOf(completed).divide(BigDecimal.valueOf(total), 6, RoundingMode.HALF_UP);
		return results.multiply(BigDecimal.valueOf(100)).doubleValue();
	}
}
