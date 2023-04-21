package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.QuotationDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryOfICPQuotationsByICPCodeCriteria;
import capi.model.report.SummaryOfICPQuotationsByICPCodeReport;
import capi.service.CommonService;

@Service("SummaryOfICPQuotationsByICPCodeService")
public class SummaryOfICPQuotationsByICPCodeService extends DataReportServiceBase {

	@Autowired
	private UserDao userDao;

	@Autowired
	private QuotationDao quotationDao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Override
	public String getFunctionCode() {
		return "RF9037";
	}

	private final static String[] headers = { "No.", "ICP type", "ICP product code", "ICP Product name",
			"Overall no. of outlets among all purpose", "Overall no. of quotations among all purpose", "Purpose",
			"No. of outlet", "No. of quotation", "Outlet percentage", "Quotation percentage" };

	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria not defined");
		}

		SummaryOfICPQuotationsByICPCodeCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(),
				SummaryOfICPQuotationsByICPCodeCriteria.class);

		Date fromMonth = commonService.getMonth(criteria.getFromMonth());
		Date toMonth = commonService.getMonth(criteria.getToMonth());

		List<SummaryOfICPQuotationsByICPCodeReport> result = quotationDao.countQuotationByICPTypeAndCode(
				criteria.getIcpType(), criteria.getIcpProductCode(), fromMonth, toMonth);

		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		int rowCnt = 1;

		for (SummaryOfICPQuotationsByICPCodeReport data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);

			if (data.getIcpProductCode().length() > 1 && data.getIcpProductCode() != null) {
				// Input Row of Data start
				int cellCnt = 0;
				SXSSFCell cell = row.createCell(cellCnt);
				cell.setCellValue(rowCnt);

				//1
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getIcpType());

				//2
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getIcpProductCode());

				//3
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getIcpProductName());

				//4
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getIcpOutletCount());

				//5
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getIcpQuotationCount());

				//6
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getPurpose());

				//7
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getCpiOutletCount());

				//8
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getCpiQuotationCount());

				//9
				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIcpOutletCount() != 0) {
					//BigDecimal outletPercentage = BigDecimal.valueOf(data.getCpiOutletCount()).divide(BigDecimal.valueOf(data.getIcpOutletCount()), 5,RoundingMode.HALF_UP);
					//double outleResult = outletPercentage.multiply(BigDecimal.valueOf(100)).doubleValue();
					BigDecimal outletPercentage = BigDecimal.valueOf(data.getCpiOutletCount())
							.divide(BigDecimal.valueOf(data.getIcpOutletCount()), 6, RoundingMode.HALF_UP)
							.multiply(BigDecimal.valueOf(100))
							.setScale(2, BigDecimal.ROUND_HALF_EVEN);
//					double outletPercentage = Math.round(Math.round(data.getIcpOutletCount() * Math.pow(10, (2) + 1)) / 10) / Math.pow(10, (2));	
					cell.setCellValue(outletPercentage.toString());
//					cell.setCellValue(Double.toString(outletPercentage));
				}

				//10
				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIcpQuotationCount() != 0) {
					//BigDecimal quotationPercentage = BigDecimal.valueOf(data.getCpiQuotationCount()).divide(BigDecimal.valueOf(data.getIcpQuotationCount()), 5,RoundingMode.HALF_UP);
					//double quotationResult = quotationPercentage.multiply(BigDecimal.valueOf(100)).doubleValue();
					BigDecimal quotationPercentage = BigDecimal.valueOf(data.getCpiQuotationCount())
							.divide(BigDecimal.valueOf(data.getIcpQuotationCount()), 6, RoundingMode.HALF_UP)
							.multiply(BigDecimal.valueOf(100))
							.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					//double quotationPercentage = (data.getIcpQuotationCount()/data.getCpiQuotationCount());
							//double quotationPercentage = Math.round(Math.round((data.getIcpQuotationCount()/data.getCpiQuotationCount()) * Math.pow(10, (2) + 1)) / 10) / Math.pow(10, (2));	
					cell.setCellValue(quotationPercentage.toString());
					//cell.setCellValue(Double.toString(quotationPercentage));
				}
				
				rowCnt++;
				if (rowCnt % 2000 == 0) {
					sheet.flushRows();
				}
			}
		}

		workBook.setSheetName(workBook.getSheetIndex(sheet), "ICP quotations by ICP Type");
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
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		SummaryOfICPQuotationsByICPCodeCriteria criteria = (SummaryOfICPQuotationsByICPCodeCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();

		if (criteria.getIcpType() != null && criteria.getIcpType().size() > 0) {
			descBuilder.append(String.format("ICP Type: %s", StringUtils.join(criteria.getIcpType(), ", ")));
			descBuilder.append("\n");
		}
		if (criteria.getIcpProductCode() != null && criteria.getIcpProductCode().size() > 0) {
			descBuilder.append(
					String.format("ICP Product Code: %s", StringUtils.join(criteria.getIcpProductCode(), ", ")));
			descBuilder.append("\n");
		}
		descBuilder.append(String.format("Period: %s", criteria.getFromMonth() + " - " + criteria.getToMonth()));

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
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
}
