package capi.service.report;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.OutletDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.OutletAmendmentCriteria;
import capi.model.report.OutletAmendmentReport;
import capi.service.CommonService;

@Service("OutletAmendmentService")
public class OutletAmendmentService  extends DataReportServiceBase{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode() {
		return "RF9018";
	}

	private static final String[] headers = new String[]{
			"No.", "Outlet Id", "Outlet Code", 
			//"Outlet Name", 
			"Original Information"
			, "Amended Information", "Rank (Amended By)", "Officer Code (Amended By)", "Name (Amended By)", "Amendment date/time" 
		};
	
	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		OutletAmendmentCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), OutletAmendmentCriteria.class);
		Date fromDate = commonService.getDate(criteria.getFromDate());
		Date toDate = commonService.getDate(criteria.getToDate());	
		
		List<OutletAmendmentReport> results = outletDao.generateOutletAmendmentReport(fromDate, toDate);
		
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for(OutletAmendmentReport data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(data.getRowNum()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(data.getOutletId()));
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(data.getFirmCode()));
			
//			cellCnt = 3;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getOutletName());
			
//			cellCnt = 4;
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOldDetail());
			
//			cellCnt = 5;
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getChangeDetail());
			
//			cellCnt = 6;
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRankCode());
			
//			cellCnt = 7;
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
//			cellCnt = 8;
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getName());
			
//			cellCnt = 9;
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(formater.format(data.getAmendmentDate()));
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Excel
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "OutletAmendmentReport");
		
		// Output Excel 
		try {
		    String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			task.setPath(this.getFileRelativeBase()+"/"+filename);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType,
			Integer userId) throws Exception {
		// TODO Auto-generated method stub		
		OutletAmendmentCriteria criteria = (OutletAmendmentCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getFromDate(), criteria.getToDate()));

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
}
