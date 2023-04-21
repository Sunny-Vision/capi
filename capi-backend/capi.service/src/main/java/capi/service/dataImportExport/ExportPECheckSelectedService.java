package capi.service.dataImportExport;

import java.io.FileOutputStream;
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

import capi.dal.AssignmentDao;
import capi.dal.BatchDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.OutletTypeDao;
import capi.dal.PECheckTaskDao;
import capi.entity.ImportExportTask;
import capi.model.dataImportExport.ExportPECheckSelectedList;
import capi.service.CommonService;

@Service("ExportPECheckSelectedService")
public class ExportPECheckSelectedService extends DataExportServiceBase {

	@Autowired
	private AssignmentDao dao;

	@Autowired
	private PECheckTaskDao peCheckTaskDao;

	@Autowired
	private OutletTypeDao outletTypeDao;

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private ImportExportTaskDao taskDao;

	@Autowired
	private CommonService commonService;

	private static final String[] headers = new String[] { "Assignment ID", "Survey", "Reference Month", "Start Date",
			"End Date", "Collection Date", "Outlet Code",
			// "Outlet Name",
			"Outlet Type", "Staff Code", "Field Team", "District Code", "TPU",
			// "Detail Address",
			"Collection Method", "Batch Code", "Market / Non-market", "No. of Quotation", "Is Selected for PE Check",
			"Is Performed by Section Head", "Is Performed by Field Team Head", "Is Certainty Case", "Is Random Case" };

	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 29;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		Date referenceMonth = task.getReferenceMonth();
		List<ExportPECheckSelectedList> results = dao.getPECheckTaskByAssignment(referenceMonth);

		/**
		 * 
		 **/

		int rowCnt = 1;
		// while(results.next()){
		for (ExportPECheckSelectedList code : results) {

			SXSSFRow row = sheet.createRow(rowCnt);

			int cellCnt = 0;

			// For Assignment
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(code.getAssignmentId()));

			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getSurvey());

			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatMonth(code.getReferenceMonth()));

			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getStartDate()));

			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getEndDate()));

			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getCollectionDate()));

			// For Outlet
			cellCnt = 6;
			cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(code.getOutletCode()));

//			cellCnt = 7;
//			cell = row.createCell(cellCnt++);
//			cell.setCellValue(code.getOutletName());

			// For OutletType
			List<String> outletTypeCode = outletTypeDao.getShortCodeByAssignmentId(code.getAssignmentId());
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			if (outletTypeCode != null && outletTypeCode.size() > 0) {
				cell.setCellValue(StringUtils.join(outletTypeCode, ';'));
			}

			// For User
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getStaffCode());

			cellCnt = 9;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getTeam());

			// For District
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getDistrict());

			// For Tpu
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getTpu());

			
//			cellCnt = 12; 
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(code.getAddress());
			

			cellCnt = 12;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCollectionMethod());

			List<String> batchCode = batchDao.getBatchCodeByAssignmentId(code.getAssignmentId());
			cellCnt = 13;
			cell = row.createCell(cellCnt);
			if (batchCode != null && batchCode.size() > 0) {
				cell.setCellValue(StringUtils.join(batchCode, ';'));
			}

			cellCnt = 14;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOutletMarketType());

			// For QuotationRecord(Count assignment)
			cellCnt = 15;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(code.getNoOfQuotation()));

			// For PECheckTask
			cellCnt = 16;
			cell = row.createCell(cellCnt);
			if (code.getIsSelected() != null) {
				cell.setCellValue(code.getIsSelected());
			}

			cellCnt = 17;
			cell = row.createCell(cellCnt);
			if (code.getIsSelected() != null) {
				cell.setCellValue(code.getIsSectionHead());
			}

			cellCnt = 18;
			cell = row.createCell(cellCnt);
			if (code.getIsFieldTeamHead() != null) {
				cell.setCellValue(code.getIsFieldTeamHead());
			}

			cellCnt = 19;
			cell = row.createCell(cellCnt);
			if (code.getIsCertaintyCase() != null) {
				cell.setCellValue(code.getIsCertaintyCase());
			}

			cellCnt = 20;
			cell = row.createCell(cellCnt);
			if (code.getIsRandomCase() != null) {
				cell.setCellValue(code.getIsRandomCase());
			}

			rowCnt++;

			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
		}

		/// results.close();

		try {
			String filename = UUID.randomUUID().toString() + ".xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();

			task.setFilePath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		taskDao.flush();
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
