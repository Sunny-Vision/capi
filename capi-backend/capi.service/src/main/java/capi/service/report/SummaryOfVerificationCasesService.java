package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.UserAccessModel;
import capi.model.report.IndividualQuotationRecordCriteria;
import capi.model.report.IndividualQuotationRecordReport;
import capi.model.report.SummaryOfVerificationCasesCriteria;
import capi.model.report.SummaryOfVerificationCasesReport;
import capi.service.CommonService;
import capi.service.UserService;

@Service("SummaryOfVerificationCasesService")
public class SummaryOfVerificationCasesService extends DataReportServiceBase {

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private UserService userService;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9049";
	}

	private static final String headers[] = new String[] { "Reference Month", "Purpose", "Team", "Post",
			"Field Officer Code", "Field Officer Name", "Firm Verification Cases (no. of Assignment)",
			"Firm Verification Cases (no. of Quotation Record)", "Category Verification Cases (no. of Assignment)",
			"Category Verification Cases (no. of Quotation Record)",
			"Quotation Verification Case (no. of Quotation Record)" };
	
	private static final String headers2[] = new String[] { "Reference Month", "Purpose", "Team", 
			"Firm Verification Cases (no. of Assignment)", "Firm Verification Cases (no. of Quotation Record)", 
			"Category Verification Cases (no. of Assignment)", "Category Verification Cases (no. of Quotation Record)", 
			"Quotation Verification Case (no. of Quotation Record)" };

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		SummaryOfVerificationCasesCriteria criteria = (SummaryOfVerificationCasesCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		List<Integer> officerId = criteria.getOfficerId();

		descBuilder.append("Purpose: ");
		Purpose p = null;
		if (criteria.getPurposeId() != null) {
			for (Integer purposeId : criteria.getPurposeId()) {
				p = this.purposeDao.findById(purposeId);
				descBuilder.append(String.format("%s, ", p.getName()));
			}

		}
		//descBuilder.append(String.format("%s, ", p != null ? p.getName() : "-"));
		descBuilder.append("\n");
		descBuilder.append("ReferenceMonth: " + criteria.getStartMonthStr() + " to " + criteria.getEndMonthStr());
		descBuilder.append("\n");
		descBuilder.append("Team: ");
		if (criteria.getTeamId() != null) {
			for (String teamName : criteria.getTeamId()) {
				descBuilder.append(String.format("%s, ", teamName));
			}
		} else {
			descBuilder.append("-");
		}
		descBuilder.append("\n");
		descBuilder.append("Field Staff: ");
		if (officerId != null) {
			for (Integer uid : officerId) {
				User user = userDao.findById(uid);
				descBuilder.append(String.format("%s - %s, ", user.getStaffCode(), user.getEnglishName()));
			}
		} else {
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
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria not defined");
		}

		SummaryOfVerificationCasesCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(),
				SummaryOfVerificationCasesCriteria.class);

		Date fromMonth = commonService.getMonth(criteria.getStartMonthStr());
		Date toMonth = commonService.getMonth(criteria.getEndMonthStr());

		List<SummaryOfVerificationCasesReport> result = userDao.getSummaryOfVerificationCasesReport1(
				criteria.getPurposeId(), criteria.getTeamId(), criteria.getOfficerId(), fromMonth, toMonth);
		List<SummaryOfVerificationCasesReport> result2 = userDao.getSummaryOfVerificationCasesReport2(
				criteria.getPurposeId(), criteria.getTeamId(), criteria.getOfficerId(), fromMonth, toMonth);

		// Start Generate Excel

		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet;
		SXSSFSheet sheet1;

		// Sheet 0
		sheet = workBook.getSheetAt(0);
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Verification Case (by officer)");
		sheet = createSheetHeader(sheet, headers);
		fillSheet0(sheet, result);
		
		// Sheet 1
		sheet1 = workBook.createSheet();
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Verification Case (by team)");
		sheet1 = createSheetHeader(sheet1, headers2);
		fillSheet1(sheet1, result2);
		
		
		// Output Excel
		try {
			String filename = UUID.randomUUID().toString() + ".xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();

			task.setPath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public void createHeader(SXSSFRow row) {
		int cnt = 0;
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

	public SXSSFSheet createSheetHeader(SXSSFSheet sheet, String[] headers) {
		int cnt = 0;

		SXSSFRow row = sheet.createRow(cnt);
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		return sheet;
	}

	public void fillSheet0(SXSSFSheet sheet, List<SummaryOfVerificationCasesReport> result) throws Exception {
		int rowCnt = 1;

		for (SummaryOfVerificationCasesReport data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getEngName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesAssignment1());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesQR1());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesQR2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesAssignment2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesQR3());

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
			// End Generate Sheet0
		}
	}
	
	public void fillSheet1(SXSSFSheet sheet, List<SummaryOfVerificationCasesReport> result) throws Exception {
		int rowCnt = 1;

		for (SummaryOfVerificationCasesReport data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesAssignment1());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesQR1());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesQR2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesAssignment2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFirmVerificationCasesQR3());

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
			// End Generate Sheet0
		}
	}
	
	

}
