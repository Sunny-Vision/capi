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
import capi.model.report.NewRecruitmentsAndProductReplacements;
import capi.model.report.NewRecruitmentsAndProductReplacementsCriteria;
import capi.service.CommonService;

@Service("NewRecruitmentsAndProductReplacementsService")
public class NewRecruitmentsAndProductReplacementsService extends DataReportServiceBase{

	@Autowired
	private IndoorQuotationRecordDao dao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;
	
	//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
//	@Autowired
//	private UnitDao unitDao;
	@Autowired
	private ItemDao itemDao;

	@Autowired
	private PurposeDao purposeDao;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9008";
	}

	private static final String[] headers = new String[]{
		"No.", "Indoor Quotation record ID", "Field Quotation Record ID", "Quotation ID", "Reference Month", 
		"Reference Date", "Purpose", "CPI base period", "Variety Code", "Variety English name", 
		"Quotation Status", "Data Conversion Status", "Outlet Code",
		//"Outlet Name", 
		"Outlet Type", 
		"Product ID", "Country of Origin", "Product Attributes 1", "Product Attributes 2", "Product Attributes 3", 
		"Product Attributes 4", "Product Attributes 5", "Survey N Price", "Survey S Price", "Last Edited N Price", 
		"Last Edited S Price", "Previous Edited N Price", "Previous Edited S Price", "Current Edited N Price", "Current Edited S Price", 
		"N Price PR", "S Price PR", "Quotation record reason", "Quotation record remarks", "New Recruitment case", 
		"Product change", "Product remarks", "Data Conversion Remarks"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		NewRecruitmentsAndProductReplacementsCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), NewRecruitmentsAndProductReplacementsCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		workBook.setSheetName(0, "List");
		//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
		List<NewRecruitmentsAndProductReplacements> records = dao.getNewRecruitmentsAndProductReplacementsReport(
				criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), startMonth, endMonth);
		
		int rowCnt = 1;
		for(NewRecruitmentsAndProductReplacements record: records){
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			//No.
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(rowCnt));

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
			
			//CPI based period
			cell = row.createCell(cellCnt++);
			if(record.getCpiBasePeriod() != null){
				cell.setCellValue(record.getCpiBasePeriod());
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
			if(record.getOutletType() != null && record.getOutletTypeEnglishName() != null){
				cell.setCellValue(record.getOutletType() + " - " +record.getOutletTypeEnglishName());
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
			if(record.getnPricePr() != null ){
				cell.setCellValue(record.getnPricePr().setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			//S Price PR
			cell = row.createCell(cellCnt++);
			if(record.getsPricePr() != null ){
				cell.setCellValue(record.getsPricePr().setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			//Quotation record reason
			cell = row.createCell(cellCnt++);
			if(record.getQuotationRecordReason() != null){
				cell.setCellValue(record.getQuotationRecordReason());
			}
			
			//Quotation record remarks
			cell = row.createCell(cellCnt++);
			if(record.getQuotationRecordRemarks() != null){
				cell.setCellValue(record.getQuotationRecordRemarks());
			}
			
			//New Recruitment case
			cell = row.createCell(cellCnt++);
			if(record.getNewRecruitmentCase() != null){
				cell.setCellValue(record.getNewRecruitmentCase());
			}
			
			//Product change
			cell = row.createCell(cellCnt++);
			if(record.getProductChange() != null){
				cell.setCellValue(record.getProductChange());
			}
			
			//Product remarks
			cell = row.createCell(cellCnt++);
			if(record.getProductRemarks() != null){
				cell.setCellValue(record.getProductRemarks());
			}
			
			//Data Conversion Remarks
			cell = row.createCell(cellCnt++);
			if(record.getDataConversionRemarks() != null){
				cell.setCellValue(record.getDataConversionRemarks());
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
		
		NewRecruitmentsAndProductReplacementsCriteria criteria = (NewRecruitmentsAndProductReplacementsCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
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
			descBuilder.append("\n");
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
