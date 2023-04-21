package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.TimeLogDao;
import capi.entity.ImportExportTask;
import capi.model.dataImportExport.ExportTimeLogMaintenanceList;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("ExportTimeLogMaintenanceService")
public class ExportTimeLogMaintenanceService extends DataExportServiceBase{
	
	@Autowired
	private TimeLogDao dao;
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = 
			new String []{
					"Time log ID", "Date", "Staff code", "Working session start time", "Working session end time", 
					"OT claimed", "Time off taken", "Is training AM", "Is training PM", "Is VL/SL AM", 
					"Is VL/SL PM", "Status", "Itinerary check violation", "Approved by", "Assignment count deviation",
					"Assignment sequence deviation", "TPU deviation", "Reject reason", "Itinerary check remark", "Created date", 
					"Created by", "Modified date", "Modified by" };
	
	private static final String[] telHeaders = 
			new String[]{
					"Time log ID", "Telephone time log ID", "Reference month", "Survey", "Purpose",
					"Session",  "Assignment ID", "Case reference No", "Total quotation", "Quotation count", 
					"Enumeration outcome", "Created date", "Created by", "Modified date", "Modified by"
			};
	
	private static final String[] fieldworkHeaders = 
			new String[]{
					"Time log ID", "Fieldwork time log ID", "Reference month", "Survey", "Purpose",
					"Activity", "Start time", "Next activity time", "Assignment ID", "Case reference No",
					"Location", "Record type", "Total quotation", "Quotation count", "Building",
					"Enumeration Outcome",  "Remarks",  "From", "To", "Include in transport claim form", 
					"Transit", "Transport", "Expenses", "Created date", "Created by", 
					"Modified date", "Modified by"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 32;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		List<String> timeLogDate = new ArrayList<>();
		if(StringUtils.isNotBlank(task.getTimeLogDate())){
			timeLogDate = Arrays.asList(task.getTimeLogDate().split("\\s*,\\s*"));
		}
		List<String> timeLogUserId = new ArrayList<>();
		if(StringUtils.isNotBlank(task.getTimeLogUserId())){
			timeLogUserId = Arrays.asList(task.getTimeLogUserId().split("\\s*,\\s*"));
		}
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheetTimeLog = workBook.getSheetAt(0);
		workBook.setSheetName(0, "Time Log");
		
		SXSSFSheet sheetTelephone = createNewSheet(workBook, telHeaders, "Telephone", 1);
		SXSSFSheet sheetFieldwork = createNewSheet(workBook, fieldworkHeaders, "Fieldwork", 2);

		List<ExportTimeLogMaintenanceList> timeLogs = dao.getExportTimeLogList(timeLogDate,timeLogUserId);
		List<ExportTimeLogMaintenanceList> telTimeLogs = dao.getExportTelephoneTimeLogList(timeLogDate,timeLogUserId);
		List<ExportTimeLogMaintenanceList> fieldTimeLogs = dao.getExportFieldworkTimeLogList(timeLogDate,timeLogUserId);
		
		int rowCnt = 1;
		for(ExportTimeLogMaintenanceList record: timeLogs){
			SXSSFRow row = sheetTimeLog.createRow(rowCnt);
			int cellCnt = 0;
			
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getTimeLogId()));

			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(record.getDate()));
			
			if(record.getStaffCode() != null){
				cellCnt = 2;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getStaffCode());
			}
			
			if(record.getIsOtherWorkingSession() != true){
				if(record.getWorkingSessionFrom() != null){
					cellCnt = 3;
					cell = row.createCell(cellCnt);
					cell.setCellValue(commonService.formatTime(record.getWorkingSessionFrom()));
				}
				
				if(record.getWorkingSessionTo() != null){
					cellCnt = 4;
					cell = row.createCell(cellCnt);
					cell.setCellValue(commonService.formatTime(record.getWorkingSessionTo()));
				}
			} else {
				if(record.getOtherWorkingSessionFrom() != null){
					cellCnt = 3;
					cell = row.createCell(cellCnt);
					cell.setCellValue(commonService.formatTime(record.getOtherWorkingSessionFrom()));
				}
				
				if(record.getOtherWorkingSessionTo() != null){
					cellCnt = 4;
					cell = row.createCell(cellCnt);
					cell.setCellValue(commonService.formatTime(record.getOtherWorkingSessionTo()));
				}
			}
			
			if(record.getOtClaimed() != null){
				cellCnt = 5;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatTime(record.getOtClaimed()));
			}
			
			if(record.getTimeoffTaken() != null){
				cellCnt = 6;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatTime(record.getTimeoffTaken()));
			}
			
			if(record.getIsTrainingAM() != null){
				cellCnt = 7;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getIsTrainingAM()));
			}
			
			if(record.getIsTrainingPM() != null){
				cellCnt = 8;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getIsTrainingPM()));
			}

			cellCnt = 9;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getIsVLSLAM()));

			cellCnt = 10;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getIsVLSLPM()));
			
			if(record.getStatus() != null){
				cellCnt = 11;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getStatus());
			}

			cellCnt = 12;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getIsVoilateItineraryCheck()));

			if(record.getApprovedBy() != null){
				cellCnt = 13;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getApprovedBy()));
			}
			
			if(record.getAssignmentDeviation() != null){
				cellCnt = 14;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getAssignmentDeviation()));
			}
			
			if(record.getSequenceDeviation() != null){
				cellCnt = 15;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getSequenceDeviation()));
			}
			
			if(record.getTpuDeviation() != null){
				cellCnt = 16;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getTpuDeviation()));
			}
			
			if(record.getRejectReason() != null){
				cellCnt = 17;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getRejectReason());
			}
			
			if(record.getItineraryCheckRemark() != null){
				cellCnt = 18;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getItineraryCheckRemark());
			}
			
			if(record.getCreatedDate() != null){
				cellCnt = 19;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatDateTime(record.getCreatedDate()));
			}
			
			if(record.getCreatedBy() != null){
				cellCnt = 20;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getCreatedBy());
			}
			
			if(record.getModifiedDate() != null){
				cellCnt = 21;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatDateTime(record.getModifiedDate()));
			}
			
			if(record.getModifiedBy() != null){
				cellCnt = 22;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getModifiedBy());
			}
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheetTimeLog.flushRows();
			}
		}
		
		rowCnt = 1;
		for(ExportTimeLogMaintenanceList record: telTimeLogs){
			SXSSFRow row = sheetTelephone.createRow(rowCnt);
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			
			if(record.getTimeLogId() != null){
				cell.setCellValue(String.valueOf(record.getTimeLogId()));
			}
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getTelephoneTimeLogId()));
			
			if(record.getReferenceMonth() != null){
				cellCnt = 2;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatMonth(record.getReferenceMonth()));
			}
			
			if(record.getSurvey() != null){
				cellCnt = 3;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getSurvey());
			}
			
			if(record.getPurpose() != null){
				cellCnt = 4;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getPurpose());
			}
			
			if(record.getTelSession() != null){
				cellCnt = 5;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getTelSession());
			}
			
//			if(record.getTelSession() != null){
//			cellCnt = 27;
//			cell = row.createCell(cellCnt);
//			switch(record.getTelSession()){
//				case "A": cell.setCellValue("AM"); break;
//				case "P": cell.setCellValue("PM"); break;
//				default: cell.setCellValue(""); break;
//			}
			
			if(record.getAssignmentId() != null){
				cellCnt = 6;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getAssignmentId()));
			}
			
			if(record.getCaseReferenceNo() != null){
				cellCnt = 7;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getCaseReferenceNo());
			}
			
			if(record.getTotalQuotation() != null){
				cellCnt = 8;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getTotalQuotation()));
			}
			
			if(record.getQuotationCount() != null){
				cellCnt = 9;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getQuotationCount()));
			}
			
			if(record.getEnumerationOutcome() != null){
				cellCnt = 10;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getEnumerationOutcome()));
			}

			if(record.getCreatedDate() != null){
				cellCnt = 11;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatDateTime(record.getCreatedDate()));
			}
			
			if(record.getCreatedBy() != null){
				cellCnt = 12;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getCreatedBy());
			}
			
			if(record.getModifiedDate() != null){
				cellCnt = 13;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatDateTime(record.getModifiedDate()));
			}
			
			if(record.getModifiedBy() != null){
				cellCnt = 14;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getModifiedBy());
			}
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheetTelephone.flushRows();
			}
		}
//		
		rowCnt = 1;
		for(ExportTimeLogMaintenanceList record: fieldTimeLogs){
			SXSSFRow row = sheetFieldwork.createRow(rowCnt);
			int cellCnt = 0;
			
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getTimeLogId()));

			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(record.getFieldworkTimeLogId()));
			
			if(record.getReferenceMonth() != null){
				cellCnt = 2;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatMonth(record.getReferenceMonth()));
			}
			
			if(record.getSurvey() != null){
				cellCnt = 3;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getSurvey());
			}
			
			if(record.getPurpose() != null){
				cellCnt = 4;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getPurpose());
			}
			
			if(record.getFieldActivity() != null){
				cellCnt = 5;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getFieldActivity());
			}
			
			if(record.getFieldStartTime() != null){
				cellCnt = 6;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatTime(record.getFieldStartTime()));
			}
			
			if(record.getFieldNextActivityTime() != null){
				cellCnt = 7;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatTime(record.getFieldNextActivityTime()));
			}
			
			if(record.getAssignmentId() != null){
				cellCnt = 8;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getAssignmentId()));
			}
			
			if(record.getCaseReferenceNo() != null){
				cellCnt = 9;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getCaseReferenceNo());
			}
			
			if(record.getFieldDestination() != null){
				cellCnt = 10;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getFieldDestination()));
			}
			
			if(record.getFieldRecordType() != null){
				cellCnt = 11;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getFieldRecordType()));
			}
			
			if(record.getTotalQuotation() != null){
				cellCnt = 12;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getTotalQuotation()));
			}
			
			if(record.getQuotationCount() != null){
				cellCnt = 13;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getQuotationCount()));
			}
			
			if(record.getFieldBuilding() != null){
				cellCnt = 14;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getFieldBuilding()));
			}
			
			if(record.getEnumerationOutcome() != null){
				cellCnt = 15;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getEnumerationOutcome()));
			}

			if(record.getFieldRemark() != null){
				cellCnt = 16;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getFieldRemark()));
			}

			if(record.getFieldFromLocation() != null){
				cellCnt = 17;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getFieldFromLocation());
			}
			
			if(record.getFieldToLocation() != null){
				cellCnt = 18;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getFieldToLocation());
			}
			
			cellCnt = 19;
			cell = row.createCell(cellCnt);
			cell.setCellValue(record.getFieldIncludeInTransportForm());

			cellCnt = 20;
			cell = row.createCell(cellCnt);
			cell.setCellValue(record.getFieldTransit());
			
			if(record.getFieldTransport() != null){
				cellCnt = 21;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getFieldTransport());
			}
			
			if(record.getFieldExpenses() != null){
				cellCnt = 22;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(record.getFieldExpenses()));
			}
			
			if(record.getCreatedDate() != null){
				cellCnt = 23;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatDateTime(record.getCreatedDate()));
			}
			
			if(record.getCreatedBy() != null){
				cellCnt = 24;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getCreatedBy());
			}
			
			if(record.getModifiedDate() != null){
				cellCnt = 25;
				cell = row.createCell(cellCnt);
				cell.setCellValue(commonService.formatDateTime(record.getModifiedDate()));
			}
			
			if(record.getModifiedBy() != null){
				cellCnt = 26;
				cell = row.createCell(cellCnt);
				cell.setCellValue(record.getModifiedBy());
			}
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheetFieldwork.flushRows();
			}
		}
	
		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			task.setFilePath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		taskDao.flush();
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

	public SXSSFSheet createNewSheet(SXSSFWorkbook workBook, String[] headers, String sheetName, Integer order){
		SXSSFSheet sheet = workBook.createSheet();
		workBook.setSheetName(order, sheetName);	
		workBook.setSheetOrder(sheetName, order);

		SXSSFRow row = sheet.createRow(0);
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		return sheet;
	}

}
