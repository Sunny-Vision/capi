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

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitDao;
import capi.dal.ItemDao;
import capi.dal.UserDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SupermarketProductReview;
import capi.model.report.SupermarketProductReviewCriteria;
import capi.service.CommonService;

@Service("SupermarketProductReviewService")
public class SupermarketProductReviewService extends DataReportServiceBase{

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
	
	@Autowired
	private ItemDao itemDao;
	
	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9024";
	}

	private static final String[] headers = new String[]{
			"No.", "Indoor Quotation record Id", "Field Quotation record Id", "Quotation Id", "Reference Month", 
			"Reference Date", "Purpose", "CPI based period", "Group Code", "Group name", 
			"Item Code", "Item name", "Variety Code", "Variety Chinese name", "Variety English name", "Quotation Status",
			"Data Conversion Status", "Outlet Code", 
			//"Outlet Name", 
			"Outlet Type", "Outlet Type English Name",
			"Product Id", "Country of Origin", "Product Attributes 1", "Product Attributes 2", "Product Attributes 3",
			"Product Attributes 4", "Product Attributes 5", "Product Attributes 6", "Product Attributes 7",
			"Product Attributes 8", "Product Attributes 9", "Product Attributes 10", "Product Attributes 11",
			"Product Attributes 12", "Product Attributes 13", "Product Attributes 14", "Product Attributes 15",
			"Product Attributes 16", "Product Attributes 17", "Product Attributes 18", "Product Remark", "Is Product Not Available",
			"Product not avaiable from", "Availability", "Survey N Price", "Survey S Price", "UOM Chinese Name & English Name",
			"UOM value", "Last Edited N Price", "Last Edited S Price", "Previous Edited N Price", "Previous Edited S Price",
			"Current Edited N Price", "Current Edited S Price", "S Price PR (Last vs Current)", "S Price PR (Previous vs Current)",
			"Quotation's Average Price (Current Month)", "Quotation's Average Price (Previous Month)", 
			"Quotation's Average Price (Last Have Average Price)", "PR of Quotation's Average Price (Current month vs Previous month)",
			"PR of Quotation's Average Price (Current month vs Last Have Average Price)", "Variety Average Price (Current Month)",
			"Variety Average Price (Previous Month)", "Variety Average Price (Last Have Average Price)", 
			"PR of Variety Average Price (Current month vs Previous month)", "PR of Variety Average Price (Current month vs Last Have Average Price)",
			"Reason (Field)", "Price Remark (Field)", "Whether fieldwork is needed", "Data Conversion Remarks", 
			"Fresh item", "Compilation Method", "Responsible Field officer Code", "Allocated Indoor officer",
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SupermarketProductReviewCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SupermarketProductReviewCriteria.class);
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
//		workBook.setSheetName(0, "Assignment allocation Summary");
		workBook.setSheetName(0, "List");
		
		List<SupermarketProductReview> data = dao.getSupermarketProductReview(startMonth, endMonth, criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), criteria.getPriceRemarks(), criteria.getDataConversionRemarks());
		
		int rowCnt = 1;
		for(SupermarketProductReview record: data){
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			//No.
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(rowCnt));
			
			//Indoor Quotation record Id
			cell = row.createCell(cellCnt++);
			if(record.getIndoorQuotationRecordId() != null){
				//cell.setCellValue(String.valueOf(record.getIndoorQuotationRecordId()));
				cell.setCellValue(record.getIndoorQuotationRecordId());
			}
			
			//Field Quotation record Id
			cell = row.createCell(cellCnt++);
			if(record.getFieldQuotationRecordId() != null){
				//cell.setCellValue(String.valueOf(record.getFieldQuotationRecordId()));
				cell.setCellValue(record.getFieldQuotationRecordId());
			}
			
			//Quotation Id
			cell = row.createCell(cellCnt++);
			if(record.getQuotationId() != null){
				cell.setCellValue(record.getQuotationId());
			}
			
/*			//Reference Month
			cell = row.createCell(cellCnt++);
			if(record.getReferenceMonthD() != null){
				//cell.setCellValue(String.valueOf(commonService.formatShortMonth(record.getReferenceMonthD())));
				cell.setCellValue(record.getReferenceMonthD());
			}*/
			
			//Reference Month
			cell = row.createCell(cellCnt++);
			if(record.getReferenceMonth() != null){
				//cell.setCellValue(String.valueOf(commonService.formatShortMonth(record.getReferenceMonthD())));
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
			
			//Group Code
			cell = row.createCell(cellCnt++);
			if(record.getGroupCode() != null){
				cell.setCellValue(record.getGroupCode());
			}
			
			//Group Name
			cell = row.createCell(cellCnt++);
			if(record.getGroupName() != null){
				cell.setCellValue(record.getGroupName());
			}
			
			//Item Code
			cell = row.createCell(cellCnt++);
			if(record.getItemCode() != null){
				cell.setCellValue(record.getItemCode());
			}
			
			//Item Name
			cell = row.createCell(cellCnt++);
			if(record.getItemName() != null){
				cell.setCellValue(record.getItemName());
			}
			
			//Variety Code
			cell = row.createCell(cellCnt++);
			if(record.getVarietyCode() != null){
				cell.setCellValue(record.getVarietyCode());
			}
			
			//Variety Chinese name
			cell = row.createCell(cellCnt++);
			if(record.getVarietyChineseName() != null){
				cell.setCellValue(record.getVarietyChineseName());
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
				cell.setCellValue(String.valueOf(record.getOutletCode()));
			}
			
			//Outlet Name
//			cell = row.createCell(cellCnt++);
//			if(record.getOutletName() != null){
//				cell.setCellValue(record.getOutletName());
//			}
			
			//Outlet Type
			cell = row.createCell(cellCnt++);
			if(record.getOutletTypeCode() != null && record.getItemCode() != null){
//				cell.setCellValue(record.getOutletTypeCode().substring(record.getItemCode().length(), record.getOutletTypeCode().length()));
				cell.setCellValue(record.getOutletTypeCode());
			}
			
			//Outlet Type English Name
			cell = row.createCell(cellCnt++);
			if(record.getOutletTypeEnglishName() != null){
				cell.setCellValue(record.getOutletTypeEnglishName());
			}
			
			//Product Id
			cell = row.createCell(cellCnt++);
			if(record.getProductId() != null){
				cell.setCellValue(String.valueOf(record.getProductId()));
			}
			
			//Country of Origin
			cell = row.createCell(cellCnt++);
			if(record.getCountryOfOrigin() != null){
				cell.setCellValue(record.getCountryOfOrigin());
			}
			
			/*//Product Attributes 1
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes1() != null && record.getProductAttributes1() != ": " ){
				cell.setCellValue(record.getProductAttributes1());
			}
			
			//Product Attributes 2
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes2() != null && record.getProductAttributes2() != ": " ){
				cell.setCellValue(record.getProductAttributes2());
			}
			
			//Product Attributes 3
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes3() != null && record.getProductAttributes3() != ": " ){
				cell.setCellValue(record.getProductAttributes3());
			}
			
			//Product Attributes 4
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes4() != null && record.getProductAttributes4() != ": " ){
				cell.setCellValue(record.getProductAttributes4());
			}
			
			//Product Attributes 5
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes5() != null && record.getProductAttributes5() != ": " ){
				cell.setCellValue(record.getProductAttributes5());
			}
			
			//Product Attributes 6
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes6() != null && record.getProductAttributes6() != ": " ){
				cell.setCellValue(record.getProductAttributes6());
			}
			
			//Product Attributes 7
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes7() != null && record.getProductAttributes7() != ": " ){
				cell.setCellValue(record.getProductAttributes7());
			}
			
			//Product Attributes 8
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes8() != null && record.getProductAttributes8() != ": " ){
				cell.setCellValue(record.getProductAttributes8());
			}
			
			//Product Attributes 9
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes9() != null && record.getProductAttributes9() != ": " ){
				cell.setCellValue(record.getProductAttributes9());
			}
			
			//Product Attributes 10
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes10() != null && record.getProductAttributes10() != ": " ){
				cell.setCellValue(record.getProductAttributes10());
			}
			
			//Product Attributes 11
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes11() != null && record.getProductAttributes11() != ": " ){
				cell.setCellValue(record.getProductAttributes11());
			}
			
			//Product Attributes 12
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes12() != null && record.getProductAttributes12() != ": " ){
				cell.setCellValue(record.getProductAttributes12());
			}
			
			//Product Attributes 13
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes13() != null && record.getProductAttributes13() != ": " ){
				cell.setCellValue(record.getProductAttributes13());
			}
			
			//Product Attributes 14
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes14() != null && record.getProductAttributes14() != ": " ){
				cell.setCellValue(record.getProductAttributes14());
			}
			
			//Product Attributes 15
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes15() != null && record.getProductAttributes15() != ": " ){
				cell.setCellValue(record.getProductAttributes15());
			}
			
			//Product Attributes 16
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes16() != null && record.getProductAttributes16() != ": " ){
				cell.setCellValue(record.getProductAttributes16());
			}
			
			//Product Attributes 17
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes17() != null && record.getProductAttributes17() != ": " ){
				cell.setCellValue(record.getProductAttributes17());
			}
			
			//Product Attributes 18
			cell = row.createCell(cellCnt++);
			if(record.getProductAttributes18() != null && record.getProductAttributes18() != ": " ){
				cell.setCellValue(record.getProductAttributes18());
			}*/
			
			//Product Attributes 1
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes1() != null && record.getProductAttributes1() != ": " ){
				cell.setCellValue(record.getProductAttributes1());
			}*/
			if(record.getProductAttributesName1() != null && record.getProductAttributesValue1() != null){
				cell.setCellValue(record.getProductAttributesName1() + ":" + record.getProductAttributesValue1());
			}  else if (record.getProductAttributesName1() != null ){
				cell.setCellValue(record.getProductAttributesName1() + ":" );
			}
			
			//Product Attributes 2
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes2() != null && record.getProductAttributes2() != ": " ){
				cell.setCellValue(record.getProductAttributes2());
			}*/
			if(record.getProductAttributesName2() != null && record.getProductAttributesValue2() != null){
				cell.setCellValue(record.getProductAttributesName2() + ":" + record.getProductAttributesValue2());
			}  else if (record.getProductAttributesName2() != null ){
				cell.setCellValue(record.getProductAttributesName2() + ":" );
			}
			
			//Product Attributes 3
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes3() != null && record.getProductAttributes3() != ": " ){
				cell.setCellValue(record.getProductAttributes3());
			}*/
			if(record.getProductAttributesName3() != null && record.getProductAttributesValue3() != null){
				cell.setCellValue(record.getProductAttributesName3() + ":" + record.getProductAttributesValue3());
			}  else if (record.getProductAttributesName3() != null ){
				cell.setCellValue(record.getProductAttributesName3() + ":" );
			}

			//Product Attributes 4
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes4() != null && record.getProductAttributes4() != ": " ){
				cell.setCellValue(record.getProductAttributes4());
			}*/
			if(record.getProductAttributesName4() != null && record.getProductAttributesValue4() != null){
				cell.setCellValue(record.getProductAttributesName4() + ":" + record.getProductAttributesValue4());
			}  else if (record.getProductAttributesName4() != null ){
				cell.setCellValue(record.getProductAttributesName4() + ":" );
			}

			//Product Attributes 5
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes5() != null && record.getProductAttributes5() != ": " ){
				cell.setCellValue(record.getProductAttributes5());
			}*/
			if(record.getProductAttributesName5() != null && record.getProductAttributesValue5() != null){
				cell.setCellValue(record.getProductAttributesName5() + ":" + record.getProductAttributesValue5());
			}  else if (record.getProductAttributesName5() != null ){
				cell.setCellValue(record.getProductAttributesName5() + ":" );
			}

			//Product Attributes 6
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes6() != null && record.getProductAttributes6() != ": " ){
				cell.setCellValue(record.getProductAttributes6());
			}*/
			if(record.getProductAttributesName6() != null && record.getProductAttributesValue6() != null){
				cell.setCellValue(record.getProductAttributesName6() + ":" + record.getProductAttributesValue6());
			}  else if (record.getProductAttributesName6() != null ){
				cell.setCellValue(record.getProductAttributesName6() + ":" );
			}

			//Product Attributes 7
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes7() != null && record.getProductAttributes7() != ": " ){
				cell.setCellValue(record.getProductAttributes7());
			}*/
			if(record.getProductAttributesName7() != null && record.getProductAttributesValue7() != null){
				cell.setCellValue(record.getProductAttributesName7() + ":" + record.getProductAttributesValue7());
			}  else if (record.getProductAttributesName7() != null ){
				cell.setCellValue(record.getProductAttributesName7() + ":" );
			}

			//Product Attributes 8
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes8() != null && record.getProductAttributes8() != ": " ){
				cell.setCellValue(record.getProductAttributes8());
			}*/
			if(record.getProductAttributesName8() != null && record.getProductAttributesValue8() != null){
				cell.setCellValue(record.getProductAttributesName8() + ":" + record.getProductAttributesValue8());
			}  else if (record.getProductAttributesName8() != null ){
				cell.setCellValue(record.getProductAttributesName8() + ":" );
			}

			//Product Attributes 9
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes9() != null && record.getProductAttributes9() != ": " ){
				cell.setCellValue(record.getProductAttributes9());
			}*/
			if(record.getProductAttributesName9() != null && record.getProductAttributesValue9() != null){
				cell.setCellValue(record.getProductAttributesName9() + ":" + record.getProductAttributesValue9());
			}  else if (record.getProductAttributesName9() != null ){
				cell.setCellValue(record.getProductAttributesName9() + ":" );
			}

			//Product Attributes 10
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes10() != null && record.getProductAttributes10() != ": " ){
				cell.setCellValue(record.getProductAttributes10());
			}*/
			if(record.getProductAttributesName10() != null && record.getProductAttributesValue10() != null){
				cell.setCellValue(record.getProductAttributesName10() + ":" + record.getProductAttributesValue10());
			}  else if (record.getProductAttributesName10() != null ){
				cell.setCellValue(record.getProductAttributesName10() + ":" );
			}

			//Product Attributes 11
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes11() != null && record.getProductAttributes11() != ": " ){
				cell.setCellValue(record.getProductAttributes11());
			}*/
			if(record.getProductAttributesName11() != null && record.getProductAttributesValue11() != null){
				cell.setCellValue(record.getProductAttributesName11() + ":" + record.getProductAttributesValue11());
			}  else if (record.getProductAttributesName11() != null ){
				cell.setCellValue(record.getProductAttributesName11() + ":" );
			}

			//Product Attributes 12
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes12() != null && record.getProductAttributes12() != ": " ){
				cell.setCellValue(record.getProductAttributes12());
			}*/
			if(record.getProductAttributesName12() != null && record.getProductAttributesValue12() != null){
				cell.setCellValue(record.getProductAttributesName12() + ":" + record.getProductAttributesValue12());
			}  else if (record.getProductAttributesName12() != null ){
				cell.setCellValue(record.getProductAttributesName12() + ":" );
			}

			//Product Attributes 13
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes13() != null && record.getProductAttributes13() != ": " ){
				cell.setCellValue(record.getProductAttributes13());
			}*/
			if(record.getProductAttributesName13() != null && record.getProductAttributesValue13() != null){
				cell.setCellValue(record.getProductAttributesName13() + ":" + record.getProductAttributesValue13());
			}  else if (record.getProductAttributesName13() != null ){
				cell.setCellValue(record.getProductAttributesName13() + ":" );
			}

			//Product Attributes 14
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes14() != null && record.getProductAttributes14() != ": " ){
				cell.setCellValue(record.getProductAttributes14());
			}*/
			if(record.getProductAttributesName14() != null && record.getProductAttributesValue14() != null){
				cell.setCellValue(record.getProductAttributesName14() + ":" + record.getProductAttributesValue14());
			}  else if (record.getProductAttributesName14() != null ){
				cell.setCellValue(record.getProductAttributesName14() + ":" );
			}

			//Product Attributes 15
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes15() != null && record.getProductAttributes15() != ": " ){
				cell.setCellValue(record.getProductAttributes15());
			}*/
			if(record.getProductAttributesName15() != null && record.getProductAttributesValue15() != null){
				cell.setCellValue(record.getProductAttributesName15() + ":" + record.getProductAttributesValue15());
			}  else if (record.getProductAttributesName15() != null ){
				cell.setCellValue(record.getProductAttributesName15() + ":" );
			}

			//Product Attributes 16
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes16() != null && record.getProductAttributes16() != ": " ){
				cell.setCellValue(record.getProductAttributes16());
			}*/
			if(record.getProductAttributesName16() != null && record.getProductAttributesValue16() != null){
				cell.setCellValue(record.getProductAttributesName16() + ":" + record.getProductAttributesValue16());
			}  else if (record.getProductAttributesName16() != null ){
				cell.setCellValue(record.getProductAttributesName16() + ":" );
			}

			//Product Attributes 17
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes17() != null && record.getProductAttributes17() != ": " ){
				cell.setCellValue(record.getProductAttributes17());
			}*/
			if(record.getProductAttributesName17() != null && record.getProductAttributesValue17() != null){
				cell.setCellValue(record.getProductAttributesName17() + ":" + record.getProductAttributesValue17());
			}  else if (record.getProductAttributesName17() != null ){
				cell.setCellValue(record.getProductAttributesName17() + ":" );
			}

			//Product Attributes 18
			cell = row.createCell(cellCnt++);
			/*if(record.getProductAttributes18() != null && record.getProductAttributes18() != ": " ){
				cell.setCellValue(record.getProductAttributes18());
			}*/
			if(record.getProductAttributesName18() != null && record.getProductAttributesValue18() != null){
				cell.setCellValue(record.getProductAttributesName18() + ":" + record.getProductAttributesValue18());
			}  else if (record.getProductAttributesName18() != null ){
				cell.setCellValue(record.getProductAttributesName18() + ":" );
			}
			
			//Product Remark
			cell = row.createCell(cellCnt++);
			if(record.getProductRemarks() != null){
				cell.setCellValue(record.getProductRemarks());
			}
			
			//Is Product Not Available
			cell = row.createCell(cellCnt++);
			if(record.isProductNotavailable() != null){
				if (record.isProductNotavailable()) {
					cell.setCellValue("Not Available");
				} else {
					cell.setCellValue("Available");
				}	
			}
			
			//Product not Available From
			cell = row.createCell(cellCnt++);
			if(record.getProdNotAvailableFrom() != null){
				//cell.setCellValue(String.valueOf(commonService.formatDateStr(record.getProdNotAvailableFrom())));
				cell.setCellValue(record.getProdNotAvailableFrom());
			}
			
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
			
			//UOM Chinese Name & English Name
			cell = row.createCell(cellCnt++);
			//if(record.getUomName() != null && record.getUomName() != "&"){
			if(record.getUomName() != null && record.getUomName().indexOf("&") > 1) {
				cell.setCellValue(record.getUomName());
			}
			
			//UOM value
			cell = row.createCell(cellCnt++);
			if(record.getUomValue() != null){
				BigDecimal bd = new BigDecimal(record.getUomValue());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			
			//Last Edited N Price
			cell = row.createCell(cellCnt++);
			if(record.getLastNPrice() != null){
				cell.setCellValue(record.getLastNPrice());
			}
			
			//Last Edited S Price
			cell = row.createCell(cellCnt++);
			if(record.getLastSPrice() != null){
				cell.setCellValue(record.getLastSPrice());
			}
			
			//Previous Edited N Price
			cell = row.createCell(cellCnt++);
			if(record.getPreviousNPrice() != null){
				cell.setCellValue(record.getPreviousNPrice());
			}
			
			//Previous Edited S Price
			cell = row.createCell(cellCnt++);
			if(record.getPreviousSPrice() != null){
				cell.setCellValue(record.getPreviousSPrice());
			}
			
			//Current Edited N Price
			cell = row.createCell(cellCnt++);
			if(record.getCurrentNPrice() != null){
				cell.setCellValue(record.getCurrentNPrice());
			}
			
			//Current Edited S Price
			cell = row.createCell(cellCnt++);
			if(record.getCurrentSPrice() != null){
				cell.setCellValue(record.getCurrentSPrice());
			}
			
			//S Price PR (Last vs Current)
			cell = row.createCell(cellCnt++);
			if(record.getLastSPrice() != null && record.getCurrentSPrice() != null){
				if (record.getLastSPrice() != 0) {
//					BigDecimal bd = new BigDecimal(100.0 * record.getCurrentSPrice()/record.getLastSPrice());
//					bd = bd.setScale(3, RoundingMode.HALF_UP);
//					cell.setCellValue(bd.doubleValue());
					/*BigDecimal bd = new BigDecimal(10.0 * record.getCurrentSPrice()/record.getLastSPrice()).setScale(4, BigDecimal.ROUND_HALF_UP);
					cell.setCellValue(BigDecimal.valueOf((bd.doubleValue())*10.0).setScale(3, RoundingMode.HALF_UP).doubleValue());*/
					BigDecimal bd = new BigDecimal(100.0 * record.getCurrentSPrice()/record.getLastSPrice()).setScale(9, BigDecimal.ROUND_HALF_UP);
					cell.setCellValue(new BigDecimal(bd.doubleValue()).setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
			}
			
			//S Price PR (Previous vs Current)
			cell = row.createCell(cellCnt++);
			if(record.getPreviousSPrice() != null && record.getCurrentSPrice() != null){
				if (record.getPreviousSPrice() != 0) {
					BigDecimal bd = new BigDecimal(100.0 * record.getCurrentSPrice()/record.getPreviousSPrice());
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			
			//Quotation's Average Price (Current Month)
			cell = row.createCell(cellCnt++);
			if(record.getQsAverageCurrentSPrice() != null){
				cell.setCellValue(record.getQsAverageCurrentSPrice());
			}
			
			//Quotation's Average Price (Previous Month)
			cell = row.createCell(cellCnt++);
			if(record.getQsAverageLastSPrice() != null){
				cell.setCellValue(record.getQsAverageLastSPrice());
			}
			
			//Quotation's Average Price (Last Have Average Price)
			cell = row.createCell(cellCnt++);
			if(record.getQsLastHasPriceAverageCurrentSPrice() != null){
				cell.setCellValue(record.getQsLastHasPriceAverageCurrentSPrice());
			}
			
			//PR of Quotation's Average Price (Current month vs Previous month)
			cell = row.createCell(cellCnt++);
			if(record.getQsAverageLastSPrice() != null && record.getQsAverageCurrentSPrice() != null){
				if (record.getQsAverageLastSPrice() != 0) {
					BigDecimal bd = new BigDecimal(100.0 * record.getQsAverageCurrentSPrice()/record.getQsAverageLastSPrice());
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			
			//PR of Quotation's Average Price (Current month vs Last Have Average Price)
			cell = row.createCell(cellCnt++);
			if(record.getQsLastHasPriceAverageCurrentSPrice() != null && record.getQsAverageCurrentSPrice() != null){
				if (record.getQsLastHasPriceAverageCurrentSPrice() != 0) {
					BigDecimal bd = new BigDecimal(100.0 * record.getQsAverageCurrentSPrice()/record.getQsLastHasPriceAverageCurrentSPrice()).setScale(9, RoundingMode.HALF_UP);
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			
			//Variety Average Price (Current Month)
			cell = row.createCell(cellCnt++);
			if(record.getUsAverageCurrentSPrice() != null){
				cell.setCellValue(record.getUsAverageCurrentSPrice());
			}
			
			//Variety Average Price (Previous Month)
			cell = row.createCell(cellCnt++);
			if(record.getUsAverageLastSPrice() != null){
				cell.setCellValue(record.getUsAverageLastSPrice());
			}
			
			//Variety Average Price (Last Have Average Price)
			cell = row.createCell(cellCnt++);
			if(record.getUsLastHasPriceAverageCurrentSPrice() != null){
				cell.setCellValue(record.getUsLastHasPriceAverageCurrentSPrice());
			}
			
			//PR of Variety Average Price (Current month vs Previous month)
			cell = row.createCell(cellCnt++);
			if(record.getUsAverageLastSPrice() != null && record.getUsAverageCurrentSPrice() != null){
				if (record.getUsAverageLastSPrice() != 0) {
					BigDecimal bd = new BigDecimal(100.0 * record.getUsAverageCurrentSPrice()/record.getUsAverageLastSPrice());
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			
			//PR of Variety Average Price (Current month vs Last Have Average Price)
			cell = row.createCell(cellCnt++);
			if(record.getUsLastHasPriceAverageCurrentSPrice() != null && record.getUsAverageCurrentSPrice() != null){
				if (record.getUsLastHasPriceAverageCurrentSPrice() != 0) {
					BigDecimal bd = new BigDecimal(100.0 * record.getUsAverageCurrentSPrice()/record.getUsLastHasPriceAverageCurrentSPrice());
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			
			//Reason (Field)
			cell = row.createCell(cellCnt++);
			if(record.getFieldReason() != null){
				cell.setCellValue(record.getFieldReason());
			}
			
			//Price Remark (Field)
			cell = row.createCell(cellCnt++);
			if(record.getPriceRemarks() != null){
				cell.setCellValue(record.getPriceRemarks());
			}
			
			//Whether fieldwork is needed
			cell = row.createCell(cellCnt++);
			if(Boolean.toString(record.isNoField()) != null){
				cell.setCellValue(!record.isNoField());
			}
			
			//Data Conversion Remarks
			cell = row.createCell(cellCnt++);
			if(record.getDataConversionRemark() != null){
				cell.setCellValue(record.getDataConversionRemark());
			}
			
			//Fresh item
			cell = row.createCell(cellCnt++);
			if (record.isFreshItem() == null ){
				cell.setCellValue("N");
			} else if(!record.isFreshItem()){
				cell.setCellValue("N");
			} else if (record.isFreshItem()) {
				cell.setCellValue("Y");
			}
			
			//Compilation Method
			cell = row.createCell(cellCnt++);
			if(record.getCompilationMethod() != null){
				switch (record.getCompilationMethod()) {
				case 1 : cell.setCellValue("A.M. (Supermarket, fresh)");
				break;
				case 2 : cell.setCellValue("A.M. (Supermarket, non-fresh)");
				break;
				case 3 : cell.setCellValue("A.M. (Market)");
				break;
				case 4 : cell.setCellValue("G.M. (Supermarket)");
				break;
				case 5 : cell.setCellValue("G.M. (Batch)");
				break;
				case 6 : cell.setCellValue("A.M. (Batch)");
				break;
				default : //do nothing
				break;
				}
			}
			
			//Responsible Field officer Code
			cell = row.createCell(cellCnt++);
			if(record.getFieldOfficerCode() != null){
				cell.setCellValue(record.getFieldOfficerCode());
			}
			
			//Allocated Indoor officer
			cell = row.createCell(cellCnt++);
			if(record.getIndoorOfficer() != null){
				cell.setCellValue(record.getIndoorOfficer());
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
		
		SupermarketProductReviewCriteria criteria = (SupermarketProductReviewCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		System.out.print("Unit: "+ criteria.getItemId());
		
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
		/*if (criteria.getItemId() != null && criteria.getItemId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Unit> units = unitDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Unit unit : units){
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}*/
		if (criteria.getItemId() != null && criteria.getItemId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Item> items = itemDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Item item : items) {
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
		if (criteria.getPriceRemarks() != null && criteria.getPriceRemarks().length() > 0){
			descBuilder.append("\n");
			if("Y".equals(criteria.getPriceRemarks())) descBuilder.append(String.format("Price remarks: %s", "Not Null"));
			else if("N".equals(criteria.getPriceRemarks())) descBuilder.append(String.format("Price remarks: %s", "Null"));
			else if("ALL".equals(criteria.getPriceRemarks())) descBuilder.append(String.format("Price remarks: %s", "All"));
		}
		
		if (criteria.getDataConversionRemarks() != null && criteria.getDataConversionRemarks().length() > 0){
			descBuilder.append("\n");
			if("Y".equals(criteria.getDataConversionRemarks())) descBuilder.append(String.format("Data Conversion Remarks: %s", "Not Null"));
			else if("N".equals(criteria.getDataConversionRemarks())) descBuilder.append(String.format("Data Conversion Remarks: %s", "Null"));
			else if("ALL".equals(criteria.getDataConversionRemarks())) descBuilder.append(String.format("Data Conversion Remarks: %s", "All"));
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
