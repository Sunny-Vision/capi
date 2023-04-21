package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryOfICPQuotationsByICPTypeCriteria;
import capi.model.report.SummaryOfICPQuotationsByICPTypeReport;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("SummaryOfICPQuotationsByICPTypeService")
public class SummaryOfICPQuotationsByICPTypeService extends DataReportServiceBase{

	@Autowired 
	private UserDao userDao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	private final static String[] headers = {
			"No.", "ICP type", "ICP product code", "ICP Product name", "Preferred quantity", "Preferred UOM",
			"No. of quotation", "Average price", "Stdev", "CV", "Min", "Max", "Min / Max ratio"
	};
	
	@Override
	public String getFunctionCode(){
		//TODO Auto-generated method stub
		return "RF9038";
	}
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfICPQuotationsByICPTypeCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfICPQuotationsByICPTypeCriteria.class);
		
		Date fromMonth = commonService.getMonth(criteria.getFromMonth());
		Date toMonth = commonService.getMonth(criteria.getToMonth());
		
		//fromMonth = DateUtils.addMonths(fromMonth, 1);
		//fromMonth = DateUtils.addDays(fromMonth, -1);
		
		// Get last day of the month
		toMonth = DateUtils.addMinutes(DateUtils.ceiling(toMonth, Calendar.MONTH), -1);
		
//		List<String> icpProductCode = new ArrayList<String>();
//		if(criteria.getIcpProductCode()!=null && criteria.getIcpProductCode().size()>0){
//			List<Unit> unitList = unitDao.getByIds(criteria.getIcpProductCode().toArray(new Integer[criteria.getIcpProductCode().size()]));
//			for(Unit unit : unitList){
//				icpProductCode.add(unit.getCode());
//			}
//		}
//		
//		List<String> icpProductCode = new ArrayList<String>();
//		if(criteria.getIcpProductCode()!=null && criteria.getIcpProductCode().size()>0){
//			List<Unit> unitList = unitDao.icpProductCodeToUnitCode(criteria.getIcpProductCode());
//			for(Unit unit : unitList){
//				icpProductCode.add(unit.getCode());
//			}
//		}
		
		List<SummaryOfICPQuotationsByICPTypeReport> data = unitDao.getICPQuotationByICPType(fromMonth, toMonth, criteria.getIcpType(), criteria.getIcpProductCode());
				
		SXSSFWorkbook workbook = prepareWorkbook();
		SXSSFSheet sheet = workbook.getSheetAt(0);
		
		int rowIndex = 1;
		
		for(SummaryOfICPQuotationsByICPTypeReport item : data) {
			SXSSFRow row = sheet.createRow(rowIndex);
			int cellIndex = 0;
			SXSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(rowIndex);
			
			cellIndex = 1;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getIcpType());
			
			cellIndex = 2;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getIcpProductCode());
			
			cellIndex = 3;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getIcpProductName());
			
			cellIndex = 4;
			cell = row.createCell(cellIndex);
			if(item.getPreferredQuantity() == null) {
				cell.setCellValue(cell.getStringCellValue());
			} else {
				cell.setCellValue(item.getPreferredQuantity());
			}
		
			cellIndex = 5;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getPreferredUOM());
			
			cellIndex = 6;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getNumOfQuotation());
			
			cellIndex = 7;
			cell = row.createCell(cellIndex);
			if(item.getAveragePrice() == null) {
				cell.setCellValue(cell.getNumericCellValue());
			} else {				
				cell.setCellValue(item.getAveragePrice());
			}
			
			cellIndex = 8;
			cell = row.createCell(cellIndex);
			if(item.getStandardDeviation() == null) {
				cell.setCellValue(cell.getNumericCellValue());
			} else {				
				cell.setCellValue(item.getStandardDeviation());
			}
			
			//Col. J
			cellIndex = 9;
			cell = row.createCell(cellIndex);
			double std = item.getStandardDeviation() == null? cell.getNumericCellValue() : item.getStandardDeviation();
			double avg = item.getAveragePrice() == null? cell.getNumericCellValue() : item.getAveragePrice();
			try {
				BigDecimal cv = BigDecimal.valueOf(std).divide(BigDecimal.valueOf(avg), 4, RoundingMode.HALF_UP);
				double retult = cv.doubleValue(); 
				cell.setCellValue(retult);
				
			} catch (Exception ex) {
				cell.setCellValue(0);
			}
//			if(item.getCv() == null) {
//				double cv = Math.round(Math.round(cell.getNumericCellValue() * Math.pow(10, (4) + 1)) / 10) / Math.pow(10, (4));
//				//cell.setCellValue(cell.getNumericCellValue());
//			} else {			
//				double cv = Math.round(Math.round(item.getCv() * Math.pow(10, (4) + 1)) / 10) / Math.pow(10, (4));
//				cell.setCellValue(cv);
//			}
			
			cellIndex = 10;
			cell = row.createCell(cellIndex);
			if(item.getMinPrice() == null) {
				cell.setCellValue(cell.getNumericCellValue());
			} else {				
				cell.setCellValue(item.getMinPrice());
			}
			
			cellIndex = 11;
			cell = row.createCell(cellIndex);
			if(item.getMaxPrice() == null) {
				cell.setCellValue(cell.getNumericCellValue());
			} else {				
				cell.setCellValue(item.getMaxPrice());
			}
			
			cellIndex = 12;
			cell = row.createCell(cellIndex);
			if(item.getRadio() == null) {
				cell.setCellValue(cell.getNumericCellValue());
			} else {				
				cell.setCellValue(item.getRadio());
			}
			
			if(rowIndex % 2000 == 0) {
				sheet.flushRows();
			}
			
			rowIndex++;
			
		}
		
		workbook.setSheetName(workbook.getSheetIndex(sheet), "Period "+commonService.formatMonth(fromMonth)+" - "+commonService.formatMonth(toMonth));
		
		// Output Excel
		try {
			String filename = "SummaryOfICPQuotationsByICPType.xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			
			workbook.write(fileOutputStream);
			workbook.close();
			
			task.setPath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		
		
	}
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		//TODO Auto-generated method stub
		SummaryOfICPQuotationsByICPTypeCriteria criteria = (SummaryOfICPQuotationsByICPTypeCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if(criteria.getIcpType()!=null && criteria.getIcpType().size()>0){
			descBuilder.append(String.format("ICP Type: %s", StringUtils.join(criteria.getIcpType(), ", ")));
			descBuilder.append("\n");
		}
//		if(criteria.getUnitId()!=null && criteria.getUnitId().size()>0){
//			List<String> icpProductCode = new ArrayList<String>();
//			if(criteria.getUnitId()!=null && criteria.getUnitId().size()>0){
//				List<Unit> unitList = unitDao.getByIds(criteria.getUnitId().toArray(new Integer[criteria.getUnitId().size()]));
//				for(Unit unit : unitList){
//					icpProductCode.add(unit.getCode() + " - " + unit.getChineseName());
//				}
//			}
//			descBuilder.append(String.format("ICP Product Code: %s", StringUtils.join(icpProductCode, ", ")));
//			descBuilder.append("\n");
//		}
		
		if (criteria.getIcpProductCode() != null && criteria.getIcpProductCode().size() > 0) {
			descBuilder.append(
					String.format("ICP Product Code: %s", StringUtils.join(criteria.getIcpProductCode(), ", ")));
			descBuilder.append("\n");
		}		
		
		descBuilder.append(String.format("Period: %s", criteria.getFromMonth()+" - "+criteria.getToMonth()));
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
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
	public void createHeader(SXSSFRow row) {
		int cellIndex = 0;
		
		for(String header: headers) {
			SXSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(header);
			cellIndex++;
		}
		
	}
	
}
