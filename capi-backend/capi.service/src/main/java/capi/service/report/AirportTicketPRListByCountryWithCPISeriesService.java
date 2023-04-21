package capi.service.report;

import java.io.FileOutputStream;
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

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.ItemDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.AirportTicketPRListByCountryWithCPISeries;
import capi.model.report.AirportTicketPRListByCountryWithCPISeriesCriteria;
import capi.service.CommonService;

@Service("AirportTicketPRListByCountryWithCPISeriesService")
public class AirportTicketPRListByCountryWithCPISeriesService extends DataReportServiceBase{

	@Autowired
	private IndoorQuotationRecordDao dao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private UnitDao unitDao;

	//2018-01-04 chenug_cheng [MB9010]  The lookup table should show up to item level only  -
	@Autowired
	private ItemDao itemDao;
	
	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9010";
	}
	
	private static final String[] headers = new String[]{
			"No.", "Indoor Quotation record ID", "Field Quotation record ID", "Quotation ID", "Reference Month", 
			"Reference Date", "Purpose", "CPI base period", "CPI Series", "Group Code", 
			"Group English name", "Item Code", "Item English name", "Variety Code", "Variety English name", 
			"Quotation Status", "Data Conversion Status", "Outlet Code", 
			//"Outlet Name", 
			"Outlet Type", 
			"Product ID", "Country of Origin", "Product Attributes 1", "Product Attributes 2", "Product Attributes 3", 
			"Product Attributes 4", "Product Attributes 5", "Product Attributes 6", "Availability", "Survey N Price", 
			"Survey S Price", "Last Edited N Price", "Last Edited S Price", "Previous Edited N Price", "Previous Edited S Price", 
			"Current Edited N Price", "Current Edited S Price", "N Price PR", "S Price PR", "Price Reason", 
			"S Price PR (aggregated)", "Keep number", "Whether fieldwork is needed", "Data Conversion Remarks", "No. of Record (by Variety)", 
			"Total no. of record (by CPI series)"
	};

	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		AirportTicketPRListByCountryWithCPISeriesCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), AirportTicketPRListByCountryWithCPISeriesCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());

		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		workBook.setSheetName(0, "PR List");
		
		List<AirportTicketPRListByCountryWithCPISeries> records = dao.getAirportTicketPRListByCountryWithCPISeries(startMonth, endMonth, 
				//2018-01-04 cheung_cheng [MB9010]  The lookup table should show up to item level only  -
				criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getItemId(),
				criteria.getDataCollection(),
				criteria.getQuotationFormType());
		
		int rowCnt = 1;
		for(AirportTicketPRListByCountryWithCPISeries record: records){
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			//No.
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(rowCnt);

			//Indoor Quotation record ID
			cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(record.getIndoorQuotationRecordId()));
			
			//Field Quotation Record ID
			cell = row.createCell(cellCnt++);
			if(record.getFieldQuotationRecordId() != null){
				cell.setCellValue(String.valueOf(record.getFieldQuotationRecordId()));
			}
			
			//Quotation ID
			cell = row.createCell(cellCnt++);
			if(record.getQuotationId() != null){
				cell.setCellValue(String.valueOf(record.getQuotationId()));
			}
			
			//Reference Month
			cell = row.createCell(cellCnt++);
			if(record.getReferenceMonth() != null){
				cell.setCellValue(record.getReferenceMonth());
			}
			
			//Reference Date
			cell = row.createCell(cellCnt++);
			if(record.getReferenceDate() != null){
				cell.setCellValue(record.getReferenceDate());
			}
			
			//Purpose
			cell = row.createCell(cellCnt++);
			if(record.getPurpose() != null){
				cell.setCellValue(record.getPurpose());
			}
			
			//CPI base period
			cell = row.createCell(cellCnt++);
			if(record.getCpiBasePeriod() != null){
				cell.setCellValue(record.getCpiBasePeriod());
			}
			
			//CPI Series
			cell = row.createCell(cellCnt++);
			if(record.getCpiSeries() != null){
				cell.setCellValue(record.getCpiSeries());
			}
			
			//Group Code
			cell = row.createCell(cellCnt++);
			if(record.getGroupCode() != null){
				cell.setCellValue(record.getGroupCode());
			}

			//Group English name
			cell = row.createCell(cellCnt++);
			if(record.getGroupEnglishName() != null){
				cell.setCellValue(record.getGroupEnglishName());
			}
			
			//Item Code
			cell = row.createCell(cellCnt++);
			if(record.getItemCode() != null){
				cell.setCellValue(record.getItemCode());
			}

			//Item English name
			cell = row.createCell(cellCnt++);
			if(record.getItemEnglishName() != null){
				cell.setCellValue(record.getItemEnglishName());
			}
			
			//Variety Code
			cell = row.createCell(cellCnt++);
			if(record.getVarietyCode() != null){
				cell.setCellValue(record.getVarietyCode());
			}

			//Variety English name
			cell = row.createCell(cellCnt++);
			if(record.getVarietyEnglishName() != null){
				cell.setCellValue(record.getVarietyEnglishName());
			}
				
			//Quotation Status
			cell = row.createCell(cellCnt++);
			if(record.getQuotationStatus() != null){
				cell.setCellValue(record.getQuotationStatus());
			}
			
			//Data Conversion Status
			cell = row.createCell(cellCnt++);
			if(record.getDataConversionStatus() != null){
				cell.setCellValue(record.getDataConversionStatus());
			}

			//Outlet Code
			cell = row.createCell(cellCnt++);
			if(record.getOutletCode() != null){
				cell.setCellValue(record.getOutletCode());
			}
				
			//Outlet Name
//			cell = row.createCell(cellCnt++);
//			if(record.getOutletName() != null){
//				cell.setCellValue(record.getOutletName());
//			}
			
			//Outlet Type(Outlet type code (3 digits) & Outlet Type English Name)
			cell = row.createCell(cellCnt++);
			if(record.getOutletTypeCode() != null && record.getOutletTypeEnglishName() != null){
				cell.setCellValue(record.getOutletTypeCode() + " - " +record.getOutletTypeEnglishName());
			}

			//Product ID
			cell = row.createCell(cellCnt++);
			if(record.getProductId() != null){
				cell.setCellValue(String.valueOf(record.getProductId()));
			}
			
			//Country of Origin
			cell = row.createCell(cellCnt++);
			if(record.getCountryOfOrigin() != null){
				cell.setCellValue(record.getCountryOfOrigin());
			}
			
			//Product Attributes 1
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes1() != null){
				cell.setCellValue(record.getProductAttributes1());
			}
			
			//Product Attributes 2
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes2() != null){
				cell.setCellValue(record.getProductAttributes2());
			}
			
			//Product Attributes 3
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes3() != null){
				cell.setCellValue(record.getProductAttributes3());
			}
			
			//Product Attributes 4
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes4() != null){
				cell.setCellValue(record.getProductAttributes4());
			}
			
			//Product Attributes 5
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes5() != null){
				cell.setCellValue(record.getProductAttributes5());
			}
			
			//Product Attributes 6
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes6() != null){
				cell.setCellValue(record.getProductAttributes6());
			}
			
			//2018-01-04 cheung_cheng [MB9010] "Availability" should show "Available", "缺貨", "Not Suitable", "無團", "未返", "回倉, "New" instead of number
//			cell = row.createCell(cellCnt++);
//			if(record.getAvailability() != null){
//				cell.setCellValue(String.valueOf(record.getAvailability()));
//			}
			//Availability
			cell = row.createCell(cellCnt++);
			if(record.getAvailability() != null){
				switch (record.getAvailability()) {
					case 1 : cell.setCellValue("Available");
					break;
					case 2 : cell.setCellValue("IP");
					break;
					case 3 : cell.setCellValue("有價無貨");
					break;
					case 4 : cell.setCellValue("缺貨");
					break;
					case 5 : cell.setCellValue("Not Suitable");
					break;
					case 6 : cell.setCellValue("回倉");
					break;
					case 7 : cell.setCellValue("無團");
					break;
					case 8 : cell.setCellValue("未返");
					break;
					case 9 : cell.setCellValue("New");
					break;					
					default : //do nothing
					break;
				}
			}
			
			//Survey N Price
			cell = row.createCell(cellCnt++);
			if(record.getSurveyNPrice() != null){
				cell.setCellValue(record.getSurveyNPrice());
			}
			
			//Survey S Price
			cell = row.createCell(cellCnt++);
			if(record.getSurveySPrice() != null){
				cell.setCellValue(record.getSurveySPrice());
			}
			
			//Last Edited N Price
			cell = row.createCell(cellCnt++);
			if(record.getLastEditedNPrice() != null){
				cell.setCellValue(record.getLastEditedNPrice());
			}
			
			//Last Edited S Price
			cell = row.createCell(cellCnt++);
			if(record.getLastEditedSPrice() != null){
				cell.setCellValue(record.getLastEditedSPrice());
			}
			
			//Previous Edited N Price
			cell = row.createCell(cellCnt++);
			if(record.getPreviousEditedNPrice() != null){
				cell.setCellValue(record.getPreviousEditedNPrice());
			}
			
			//Previous Edited S Price
			cell = row.createCell(cellCnt++);
			if(record.getPreviousEditedSPrice() != null){
				cell.setCellValue(record.getPreviousEditedSPrice());
			}
			
			//Current Edited N Price
			cell = row.createCell(cellCnt++);
			if(record.getCurrentEditedNPrice() != null){
				cell.setCellValue(record.getCurrentEditedNPrice());
			}
			
			//Current Edited S Price
			cell = row.createCell(cellCnt++);
			if(record.getCurrentEditedSPrice() != null){
				cell.setCellValue(record.getCurrentEditedSPrice());
			}
			
			//N Price PR
			cell = row.createCell(cellCnt++);
			if(record.getnPricePr() != null){
				cell.setCellValue(record.getnPricePr().setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			//S Price PR
			cell = row.createCell(cellCnt++);
			if(record.getsPricePr() != null){
				cell.setCellValue(record.getsPricePr().setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			//Price Reason
			cell = row.createCell(cellCnt++);
			if(record.getPriceReason() != null){
				cell.setCellValue(record.getPriceReason());
			}
			
			//S Price PR (aggregated)
			cell = row.createCell(cellCnt++);
			if(record.getsPricePrAggregated() != null){
				cell.setCellValue(record.getsPricePrAggregated().setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			//Keep number
			cell = row.createCell(cellCnt++);
			if(record.getKeepNumber() != null){
				cell.setCellValue(record.getKeepNumber());
			}
			
			//Whether fieldwork is needed
			cell = row.createCell(cellCnt++);
			cell.setCellValue(record.getWhetherFieldworkIsNeeded());
			
			////Data Conversion Remarks
			cell = row.createCell(cellCnt++);
			if(record.getDataConversionRemarks() != null){
				cell.setCellValue(record.getDataConversionRemarks());
			}
			
			//No. of Record (by Variety)
			cell = row.createCell(cellCnt++);
			if(record.getNoOfRecordByVariety() != null){
				cell.setCellValue(record.getNoOfRecordByVariety());
			}
			
			//Total no. of record (by CPI series)
			cell = row.createCell(cellCnt++);
			if(record.getNoOfRecordByVariety() != null){
				cell.setCellValue(record.getTotalNoOfRecordByCpiSeries());
			}
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
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
		
		AirportTicketPRListByCountryWithCPISeriesCriteria criteria = (AirportTicketPRListByCountryWithCPISeriesCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		

		//2018-01-04 chenug_cheng [MB9010]  The lookup table should show up to item level only  -
		if (criteria.getItemId() != null && criteria.getItemId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Item> items = itemDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Item item : items){
				codes.add(item.getCode() + " - " + item.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getCpiSurveyForm() != null && criteria.getCpiSurveyForm().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCpiSurveyForm()){
				switch (form){
					case 1:codes.add("Market"); break;
					case 2:codes.add("Supermarket"); break;
					case 3:codes.add("Batch"); break;
					default:codes.add("Others"); break;
				}
				
			}
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(codes, ", ")));
		}
		
		if (StringUtils.isNotEmpty(criteria.getDataCollection())) {
			if (descBuilder.length() > 0) descBuilder.append("\n");
			if("Y".equals(criteria.getDataCollection())) { 
				descBuilder.append(String.format("Data collection: %s", "Field"));
			} else if("N".equals(criteria.getDataCollection())) {
				descBuilder.append(String.format("Data collection: %s", "No Field"));
			} else if("All".equals(criteria.getDataCollection())) {
				descBuilder.append(String.format("Data collection: %s", "All"));
			}
		}
		
		if (descBuilder.length() > 0) descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		if (criteria.getQuotationFormType() != null && criteria.getQuotationFormType().size() > 0) {
			if (descBuilder.length() > 0) descBuilder.append("\n");
			
			//descBuilder.append(String.format("Quotation Form Type : %s", StringUtils.join(criteria.getQuotationFormType(), ", ")));
			descBuilder.append(String.format("CPI Compilation Series : %s", StringUtils.join(criteria.getQuotationFormType(), ", ")));
		}
		
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
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

}
