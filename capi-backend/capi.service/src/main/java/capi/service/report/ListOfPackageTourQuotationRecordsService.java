package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import capi.model.report.ListOfPackageTourQuotationRecordsCriteria;
import capi.service.CommonService;

@Service("ListOfPackageTourQuotationRecordsService")
public class ListOfPackageTourQuotationRecordsService extends DataReportServiceBase{

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

	//2018-01-04 cheung_cheng [MB9012] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
	@Autowired
	private ItemDao itemDao;
	
	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9012";
	}
	
	private static final String[] headersA = new String[]{
			"No.", "Indoor Quotation record ID", "Field Quotation record ID", "Quotation ID", "Reference Month", 
			"Reference Date", "Purpose", "CPI based period", "Group Code", "Group name", 
			"Item Code", "Item name", "Variety Code", "Variety name", "Quotation Status", 
			"Data Conversion Status", "Outlet Code",
			//"Outlet Name", 
			"Outlet Type", "Outlet Type English Name", 
			"Product ID", "Country of Origin"
	};
	
	private static final String[] headersB = new String[]{
			"Availability", "Survey N Price", "Survey S Price", "Last Edited N Price", "Last Edited S Price", 
			"Previous Edited N Price", "Previous Edited S Price", "Current Edited N Price", "Current Edited S Price", "N Price PR", 
			"S Price PR", "Reason (Field)", "Price Remark (Field)", "Whether fieldwork is needed", "Data Conversion Remarks", 
			"CPI Series", "PR for A", "PR for B", "PR for C", "PR for AB", 
			"PR for AC", "PR for BC", "PR for ABC", "PR for M", "GM of PR for A (by Variety Code + Product Attributes 1)", 
			"GM of PR for B (by Variety Code + Product Attributes 1)", "GM of PR for C (by Variety Code + Product Attributes 1)", "GM of PR for AB (by Variety Code + Product Attributes 1)", "GM of PR for AC (by Variety Code + Product Attributes 1)", "GM of PR for BC (by Variety Code + Product Attributes 1)", 
			"GM of PR for ABC (by Variety Code + Product Attributes 1)", "GM of PR for M (by Variety Code + Product Attributes 1)", "GM of PR for A (by Variety Code)", "GM of PR for B (by Variety Code)", 
			"GM of PR for C (by Variety Code)", "GM of PR for AB (by Variety Code)", "GM of PR for AC (by Variety Code)", "GM of PR for BC (by Variety Code)", "GM of PR for ABC (by Variety Code)", 
			"GM of PR for M (by Variety Code)", "Tour Record Id", "Day1Price", "Day2Price", "Day3Price", 
			"Day4Price", "Day5Price", "Day6Price", "Day7Price", "Day8Price", 
			"Day9Price", "Day10Price", "Day11Price", "Day12Price", "Day13Price", 
			"Day14Price", "Day15Price", "Day16Price", "Day17Price", "Day18Price", 
			"Day19Price", "Day20Price", "Day21Price", "Day22Price", "Day23Price", 
			"Day24Price", "Day25Price", "Day26Price", "Day27Price", "Day28Price", 
			"Day29Price", "Day30Price", "Day31Price", "ExtraPrice1Name", "ExtraPrice1Value", 
			"Is ExtraPrice1 Include", "ExtraPrice2Name", "ExtraPrice2Value", "Is ExtraPrice2 Include", "ExtraPrice3Name", 
			"ExtraPrice3Value", "Is ExtraPrice3 Include", "ExtraPrice4Name", "ExtraPrice4Value", "Is ExtraPrice4 Include", 
			"ExtraPrice5Name", "ExtraPrice5Value", "Is ExtraPrice5 Include"
	};

	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		ListOfPackageTourQuotationRecordsCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), ListOfPackageTourQuotationRecordsCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		workBook.setSheetName(0, "List");

//		/*  
//		 * 	Dynamic Product Attributes Generation
//		 *  If dynamic product attributes generation required, 
//		 *  please uncomment the code below.	
//		 */
//		Integer MAX_ATTRIBUTE_SEQ = (dao.getProductAttributesMaxSeq(startMonth, endMonth, 
//				criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getUnitId(),
//				criteria.getDataCollection()));
//		
//		MAX_ATTRIBUTE_SEQ = (MAX_ATTRIBUTE_SEQ != null && MAX_ATTRIBUTE_SEQ > 0) ? MAX_ATTRIBUTE_SEQ : 1; 
		
		//Product Attributes Fixed Size: 18 
		Integer MAX_ATTRIBUTE_SEQ = 8;

		//2018-01-04 cheung_cheng [MB9012] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
		List<Map<String, Object>> records = dao.getListOfPackageTourQuotationRecords(startMonth, endMonth, 
				criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getItemId(),
				criteria.getDataCollection(), MAX_ATTRIBUTE_SEQ);
		
		SXSSFRow row = sheet.createRow(0);
		createDynamicHeader(row, MAX_ATTRIBUTE_SEQ);
		
		int rowCnt = 1;
		if(!records.isEmpty()){
			for (Map<String, Object> map : records) {
				
				Map<Integer, String> productAttributesMap = new HashMap<Integer, String>();
				
				//Get productAttrubutes N by Sequence
				for(String alias : map.keySet()){
					if(alias.toLowerCase().startsWith("productAttributes".toLowerCase())){
						int id = Integer.valueOf(StringUtils.substringAfter(alias, "productAttributes"));
						productAttributesMap.put(id, String.valueOf(map.get(alias)));
					}
				}
				
				List<String> productAttributes = new ArrayList<String>();
				if(!productAttributesMap.isEmpty()){
					for(int i=1; i<productAttributesMap.size()+1; i++) {
						productAttributes.add(productAttributesMap.get(i));
					}
				}
				
				row = sheet.createRow(rowCnt);
				int cellCnt = 0;
				
				//No.
				SXSSFCell cell = row.createCell(cellCnt++);
				cell.setCellValue(String.valueOf(rowCnt));
	
				//Indoor Quotation record ID
				cell = row.createCell(cellCnt++);
				if(map.get("indoorQuotationRecordId") != null){
					cell.setCellValue(String.valueOf(map.get("indoorQuotationRecordId")));
				}
				
				//Field Quotation Record ID
				cell = row.createCell(cellCnt++);
				if(map.get("fieldQuotationRecordId") != null){
					cell.setCellValue(String.valueOf(map.get("fieldQuotationRecordId")));
				}
				
				//Quotation ID
				cell = row.createCell(cellCnt++);
				if(map.get("quotationId") != null){
					cell.setCellValue(String.valueOf(map.get("quotationId")));
				}
				
				//Reference Month
				cell = row.createCell(cellCnt++);
				if(map.get("referenceMonth") != null){
					cell.setCellValue(String.valueOf(map.get("referenceMonth")));
				}
				
				//Reference Date
				cell = row.createCell(cellCnt++);
				if(map.get("referenceDate") != null){
					cell.setCellValue(String.valueOf(map.get("referenceDate")));
				}
				
				//Purpose
				cell = row.createCell(cellCnt++);
				if(map.get("purpose") != null){
					cell.setCellValue(String.valueOf(map.get("purpose")));
				}
				
				//CPI based period
				cell = row.createCell(cellCnt++);
				if(map.get("cpiBasedPeriod") != null){
					cell.setCellValue(String.valueOf(map.get("cpiBasedPeriod")));
				}
				
				//Group Code
				cell = row.createCell(cellCnt++);
				if(map.get("groupCode") != null){
					cell.setCellValue(String.valueOf(map.get("groupCode")));
				}
				
				//Group name
				cell = row.createCell(cellCnt++);
				if(map.get("groupName") != null){
					cell.setCellValue(String.valueOf(map.get("groupName")));
				}
				
				//Item Code
				cell = row.createCell(cellCnt++);
				if(map.get("itemCode") != null){
					cell.setCellValue(String.valueOf(map.get("itemCode")));
				}
				
				//Item name
				cell = row.createCell(cellCnt++);
				if(map.get("itemName") != null){
					cell.setCellValue(String.valueOf(map.get("itemName")));
				}
				
				//Variety Code
				cell = row.createCell(cellCnt++);
				if(map.get("varietyCode") != null){
					cell.setCellValue(String.valueOf(map.get("varietyCode")));
				}
				
				//Variety name
				cell = row.createCell(cellCnt++);
				if(map.get("varietyName") != null){
					cell.setCellValue(String.valueOf(map.get("varietyName")));
				}
				
				//Quotation Status
				cell = row.createCell(cellCnt++);
				if(map.get("quotationStatus") != null){
					cell.setCellValue(String.valueOf(map.get("quotationStatus")));
				}
				
				//Data Conversion Status
				cell = row.createCell(cellCnt++);
				if(map.get("dataConversionStatus") != null){
					cell.setCellValue(String.valueOf(map.get("dataConversionStatus")));
				}
				
				//Outlet Code
				cell = row.createCell(cellCnt++);
				if(map.get("outletCode") != null){
					cell.setCellValue(String.valueOf(map.get("outletCode")));
				}
				
				//Outlet Name
//				cell = row.createCell(cellCnt++);
//				if(map.get("outletName") != null){
//					cell.setCellValue(String.valueOf(map.get("outletName")));
//				}
				
				//Outlet Type
				cell = row.createCell(cellCnt++);
				if(map.get("outletType") != null){
					String outletType = String.valueOf(map.get("outletType"));
					if (outletType != null){
						cell.setCellValue(StringUtils.right(outletType, 3));	
					}
				}
				
				//Outlet Type English Name
				cell = row.createCell(cellCnt++);
				if(map.get("outletTypeEnglishName") != null){
					cell.setCellValue(String.valueOf(map.get("outletTypeEnglishName")));
				}
				
				//Product ID
				cell = row.createCell(cellCnt++);
				if(map.get("productId") != null){
					cell.setCellValue(String.valueOf(map.get("productId")));
				}
				
				//Country of Origin
				cell = row.createCell(cellCnt++);
				if(map.get("countryOfOrigin") != null){
					cell.setCellValue(String.valueOf(map.get("countryOfOrigin")));
				}
				
				int productRowCount = cellCnt;
				if(!productAttributes.isEmpty()){
					for (String productAttribute : productAttributes) {
						cell = row.createCell(productRowCount++);
						if(!productAttribute.isEmpty()){
							cell.setCellValue(productAttribute);
						}
					}
				}

				cellCnt+=MAX_ATTRIBUTE_SEQ;
				
				//Availability
				cell = row.createCell(cellCnt++);
				if(map.get("availability") != null){
					String availability = SystemConstant.QUOTATIONRECORD_AVALIBILITY1[(Integer) map.get("availability")];
					cell.setCellValue(availability);
				}
				
				//Survey N Price
				cell = row.createCell(cellCnt++);
				if(map.get("surveyNPrice") != null){
					cell.setCellValue((Double)map.get("surveyNPrice"));
				}
				
				//Survey S Price
				cell = row.createCell(cellCnt++);
				if(map.get("surveySPrice") != null){
					cell.setCellValue((Double)map.get("surveySPrice"));
				}
				
				//Last Edited N Price
				cell = row.createCell(cellCnt++);
				if(map.get("lastEditedNPrice") != null){
					cell.setCellValue((Double)map.get("lastEditedNPrice"));
				}
				
				//Last Edited S Price
				cell = row.createCell(cellCnt++);
				if(map.get("lastEditedSPrice") != null){
					cell.setCellValue((Double)map.get("lastEditedSPrice"));
				}
				
				//Previous Edited N Price
				cell = row.createCell(cellCnt++);
				if(map.get("previousEditedNPrice") != null){
					cell.setCellValue((Double)map.get("previousEditedNPrice"));
				}
				
				//Previous Edited S Price
				cell = row.createCell(cellCnt++);
				if(map.get("previousEditedSPrice") != null){
					cell.setCellValue((Double)map.get("previousEditedSPrice"));
				}
				
				//Current Edited N Price
				cell = row.createCell(cellCnt++);
				if(map.get("currentEditedNPrice") != null){
					cell.setCellValue((Double)map.get("currentEditedNPrice"));
				}
				
				//Current Edited S Price
				cell = row.createCell(cellCnt++);
				if(map.get("currentEditedSPrice") != null){
					cell.setCellValue((Double)map.get("currentEditedSPrice"));
				}
				
				//N Price PR
				cell = row.createCell(cellCnt++);
				if(map.get("nPricePR") != null){
					BigDecimal nPricePR = (BigDecimal)map.get("nPricePR");
					cell.setCellValue(nPricePR.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//S Price PR
				cell = row.createCell(cellCnt++);
				if(map.get("sPricePR") != null){
					BigDecimal sPricePR = (BigDecimal)map.get("sPricePR");
					cell.setCellValue(sPricePR.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//Reason (Field)
				cell = row.createCell(cellCnt++);
				if(map.get("reasonField") != null){
					cell.setCellValue(String.valueOf(map.get("reasonField")));
				}
				
				//Price Remark (Field)
				cell = row.createCell(cellCnt++);
				if(map.get("priceRemarkField") != null){
					cell.setCellValue(String.valueOf(map.get("priceRemarkField")));
				}
				
				//Whether fieldwork is needed
				cell = row.createCell(cellCnt++);
				if(map.get("whetherFieldworkIsNeeded") != null){
					cell.setCellValue(String.valueOf(map.get("whetherFieldworkIsNeeded")));
				}
				
				//Data Conversion Remarks
				cell = row.createCell(cellCnt++);
				if(map.get("dataConversionRemarks") != null){
					cell.setCellValue(String.valueOf(map.get("dataConversionRemarks")));
				}
				
				//CPI Series
				cell = row.createCell(cellCnt++);
				if(map.get("cpiSeries") != null){
					cell.setCellValue(String.valueOf(map.get("cpiSeries")));
				}
				
				//PR for A

				cell = row.createCell(cellCnt++);
				if(map.get("prForA") != null){
					BigDecimal prForA = (BigDecimal)map.get("prForA");
					cell.setCellValue(prForA.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//PR for B
				cell = row.createCell(cellCnt++);
				if(map.get("prForB") != null){
					BigDecimal prForB = (BigDecimal)map.get("prForB");
					cell.setCellValue(prForB.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//PR for C
				cell = row.createCell(cellCnt++);
				if(map.get("prForC") != null){
					BigDecimal prForC = (BigDecimal)map.get("prForC");
					cell.setCellValue(prForC.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//PR for AB
				cell = row.createCell(cellCnt++);
				if(map.get("prForAB") != null){
					BigDecimal prForAB = (BigDecimal)map.get("prForAB");
					cell.setCellValue(prForAB.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//PR for AC
				cell = row.createCell(cellCnt++);
				if(map.get("prForAC") != null){
					BigDecimal prForAC = (BigDecimal)map.get("prForAC");
					cell.setCellValue(prForAC.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}

				//PR for BC
				cell = row.createCell(cellCnt++);
				if(map.get("prForBC") != null){
					BigDecimal prForBC = (BigDecimal)map.get("prForBC");
					cell.setCellValue(prForBC.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//PR for ABC
				cell = row.createCell(cellCnt++);
				if(map.get("prForABC") != null){
					BigDecimal prForABC = (BigDecimal)map.get("prForABC");
					cell.setCellValue(prForABC.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//PR for M
				cell = row.createCell(cellCnt++);
				if(map.get("prForM") != null){
					BigDecimal prForM = (BigDecimal)map.get("prForM");
					cell.setCellValue(prForM.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for A (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForAByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForAByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForAByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForAByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for B (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForBByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForBByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForBByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForBByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for C (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForCByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForCByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForCByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForCByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for AB (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForABByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForABByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForABByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForABByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for AC (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForACByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForACByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForACByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForACByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for BC (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForBCByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForBCByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForBCByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForBCByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for ABC (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForABCByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForABCByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForABCByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForABCByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for M (by Variety Code + Product Attributes 1)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForMByVarietyCodeProductAttributes1") != null){
					BigDecimal gmOfPRForMByVarietyCodeProductAttributes1 = (BigDecimal)map.get("gmOfPRForMByVarietyCodeProductAttributes1");
					cell.setCellValue(gmOfPRForMByVarietyCodeProductAttributes1.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for A (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForAByVarietyCode") != null){
					BigDecimal gmOfPRForAByVarietyCode = (BigDecimal)map.get("gmOfPRForAByVarietyCode");
					cell.setCellValue(gmOfPRForAByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for B (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForBByVarietyCode") != null){
					BigDecimal gmOfPRForBByVarietyCode = (BigDecimal)map.get("gmOfPRForBByVarietyCode");
					cell.setCellValue(gmOfPRForBByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for C (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForCByVarietyCode") != null){
					BigDecimal gmOfPRForCByVarietyCode = (BigDecimal)map.get("gmOfPRForCByVarietyCode");
					cell.setCellValue(gmOfPRForCByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for AB (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForABByVarietyCode") != null){
					BigDecimal gmOfPRForABByVarietyCode = (BigDecimal)map.get("gmOfPRForABByVarietyCode");
					cell.setCellValue(gmOfPRForABByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for AC (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForACByVarietyCode") != null){
					BigDecimal gmOfPRForACByVarietyCode = (BigDecimal)map.get("gmOfPRForACByVarietyCode");
					cell.setCellValue(gmOfPRForACByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for BC (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForBCByVarietyCode") != null){
					BigDecimal gmOfPRForBCByVarietyCode = (BigDecimal)map.get("gmOfPRForBCByVarietyCode");
					cell.setCellValue(gmOfPRForBCByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for ABC (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForABCByVarietyCode") != null){
					BigDecimal gmOfPRForABCByVarietyCode = (BigDecimal)map.get("gmOfPRForABCByVarietyCode");
					cell.setCellValue(gmOfPRForABCByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//GM of PR for M (by Variety Code)
				cell = row.createCell(cellCnt++);
				if(map.get("gmOfPRForMByVarietyCode") != null){
					BigDecimal gmOfPRForMByVarietyCode = (BigDecimal)map.get("gmOfPRForMByVarietyCode");
					cell.setCellValue(gmOfPRForMByVarietyCode.setScale(3, RoundingMode.HALF_UP).doubleValue());
				}
				
				//Tour Record Id
				cell = row.createCell(cellCnt++);
				if(map.get("tourRecordId") != null){
					cell.setCellValue(String.valueOf(map.get("tourRecordId")));
				}
				
				//Day1Price
				cell = row.createCell(cellCnt++);
				if(map.get("day1Price") != null){
					cell.setCellValue((Double)map.get("day1Price"));
				}
				
				//Day2Price
				cell = row.createCell(cellCnt++);
				if(map.get("day2Price") != null){
					cell.setCellValue((Double)map.get("day2Price"));
				}
				
				//Day3Price
				cell = row.createCell(cellCnt++);
				if(map.get("day3Price") != null){
					cell.setCellValue((Double)map.get("day3Price"));
				}
				
				//Day4Price
				cell = row.createCell(cellCnt++);
				if(map.get("day4Price") != null){
					cell.setCellValue((Double)map.get("day4Price"));
				}
				
				//Day5Price
				cell = row.createCell(cellCnt++);
				if(map.get("day5Price") != null){
					cell.setCellValue((Double)map.get("day5Price"));
				}
				
				//Day6Price
				cell = row.createCell(cellCnt++);
				if(map.get("day6Price") != null){
					cell.setCellValue((Double)map.get("day6Price"));
				}
				
				//Day7Price
				cell = row.createCell(cellCnt++);
				if(map.get("day7Price") != null){
					cell.setCellValue((Double)map.get("day7Price"));
				}
				
				//Day8Price
				cell = row.createCell(cellCnt++);
				if(map.get("day8Price") != null){
					cell.setCellValue((Double)map.get("day8Price"));
				}
				
				//Day9Price
				cell = row.createCell(cellCnt++);
				if(map.get("day9Price") != null){
					cell.setCellValue((Double)map.get("day9Price"));
				}
				
				//Day10Price
				cell = row.createCell(cellCnt++);
				if(map.get("day10Price") != null){
					cell.setCellValue((Double)map.get("day10Price"));
				}
				
				//Day11Price
				cell = row.createCell(cellCnt++);
				if(map.get("day11Price") != null){
					cell.setCellValue((Double)map.get("day11Price"));
				}
				
				//Day12Price
				cell = row.createCell(cellCnt++);
				if(map.get("day12Price") != null){
					cell.setCellValue((Double)map.get("day12Price"));
				}
				
				//Day13Price
				cell = row.createCell(cellCnt++);
				if(map.get("day13Price") != null){
					cell.setCellValue((Double)map.get("day13Price"));
				}
				
				//Day14Price
				cell = row.createCell(cellCnt++);
				if(map.get("day14Price") != null){
					cell.setCellValue((Double)map.get("day14Price"));
				}
				
				//Day15Price
				cell = row.createCell(cellCnt++);
				if(map.get("day15Price") != null){
					cell.setCellValue((Double)map.get("day15Price"));
				}
				
				//Day16Price
				cell = row.createCell(cellCnt++);
				if(map.get("day16Price") != null){
					cell.setCellValue((Double)map.get("day16Price"));
				}
				
				//Day17Price
				cell = row.createCell(cellCnt++);
				if(map.get("day17Price") != null){
					cell.setCellValue((Double)map.get("day17Price"));
				}
				
				//Day18Price
				cell = row.createCell(cellCnt++);
				if(map.get("day18Price") != null){
					cell.setCellValue((Double)map.get("day18Price"));
				}
				
				//Day19Price
				cell = row.createCell(cellCnt++);
				if(map.get("day19Price") != null){
					cell.setCellValue((Double)map.get("day19Price"));
				}
				
				//Day20Price
				cell = row.createCell(cellCnt++);
				if(map.get("day20Price") != null){
					cell.setCellValue((Double)map.get("day20Price"));
				}
				
				//Day21Price
				cell = row.createCell(cellCnt++);
				if(map.get("day21Price") != null){
					cell.setCellValue((Double)map.get("day21Price"));
				}
				
				//Day22Price
				cell = row.createCell(cellCnt++);
				if(map.get("day22Price") != null){
					cell.setCellValue((Double)map.get("day22Price"));
				}
				
				//Day23Price
				cell = row.createCell(cellCnt++);
				if(map.get("day23Price") != null){
					cell.setCellValue((Double)map.get("day23Price"));
				}
				
				//Day24Price
				cell = row.createCell(cellCnt++);
				if(map.get("day24Price") != null){
					cell.setCellValue((Double)map.get("day24Price"));
				}
				
				//Day25Price
				cell = row.createCell(cellCnt++);
				if(map.get("day25Price") != null){
					cell.setCellValue((Double)map.get("day25Price"));
				}
				
				//Day26Price
				cell = row.createCell(cellCnt++);
				if(map.get("day26Price") != null){
					cell.setCellValue((Double)map.get("day26Price"));
				}
				
				//Day27Price
				cell = row.createCell(cellCnt++);
				if(map.get("day27Price") != null){
					cell.setCellValue((Double)map.get("day27Price"));
				}
				
				//Day28Price
				cell = row.createCell(cellCnt++);
				if(map.get("day28Price") != null){
					cell.setCellValue((Double)map.get("day28Price"));
				}
				
				//Day29Price
				cell = row.createCell(cellCnt++);
				if(map.get("day29Price") != null){
					cell.setCellValue((Double)map.get("day29Price"));
				}
				
				//Day30Price
				cell = row.createCell(cellCnt++);
				if(map.get("day30Price") != null){
					cell.setCellValue((Double)map.get("day30Price"));
				}
				
				//Day31Price
				cell = row.createCell(cellCnt++);
				if(map.get("day31Price") != null){
					cell.setCellValue((Double)map.get("day31Price"));
				}
				
				//ExtraPrice1Name
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice1Name") != null){
					cell.setCellValue(String.valueOf(map.get("extraPrice1Name")));
				}
				
				//ExtraPrice1Value
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice1Value") != null){
					cell.setCellValue((Double)map.get("extraPrice1Value"));
				}
				
				//Is ExtraPrice1 Include
				cell = row.createCell(cellCnt++);
				if(map.get("isExtraPrice1Include") != null){
					cell.setCellValue(String.valueOf(map.get("isExtraPrice1Include")));
				}
				
				//ExtraPrice2Name
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice2Name") != null){
					cell.setCellValue(String.valueOf(map.get("extraPrice2Name")));
				}
				
				//ExtraPrice2Value
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice2Value") != null){
					cell.setCellValue((Double)map.get("extraPrice2Value"));
				}
				
				//Is ExtraPrice2 Include
				cell = row.createCell(cellCnt++);
				if(map.get("isExtraPrice2Include") != null){
					cell.setCellValue(String.valueOf(map.get("isExtraPrice2Include")));
				}
				
				//ExtraPrice3Name
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice3Name") != null){
					cell.setCellValue(String.valueOf(map.get("extraPrice3Name")));
				}
				
				//ExtraPrice3Value
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice3Value") != null){
					cell.setCellValue((Double)map.get("extraPrice3Value"));
				}
				
				//Is ExtraPrice3 Include
				cell = row.createCell(cellCnt++);
				if(map.get("isExtraPrice3Include") != null){
					cell.setCellValue(String.valueOf(map.get("isExtraPrice3Include")));
				}
				
				//ExtraPrice4Name
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice4Name") != null){
					cell.setCellValue(String.valueOf(map.get("extraPrice4Name")));
				}
				
				//ExtraPrice4Value
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice4Value") != null){
					cell.setCellValue((Double)map.get("extraPrice4Value"));
				}
				
				//Is ExtraPrice4 Include
				cell = row.createCell(cellCnt++);
				if(map.get("isExtraPrice4Include") != null){
					cell.setCellValue(String.valueOf(map.get("isExtraPrice4Include")));
				}
				
				//ExtraPrice5Name
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice5Name") != null){
					cell.setCellValue(String.valueOf(map.get("extraPrice5Name")));
				}
				
				//ExtraPrice5Value
				cell = row.createCell(cellCnt++);
				if(map.get("extraPrice5Value") != null){
					cell.setCellValue((Double)map.get("extraPrice5Value"));
				}
				
				//Is ExtraPrice5 Include
				cell = row.createCell(cellCnt++);
				if(map.get("isExtraPrice5Include") != null){
					cell.setCellValue(String.valueOf(map.get("isExtraPrice5Include")));
				}
			
				rowCnt++;
				if (rowCnt % 2000 == 0){
					sheet.flushRows();
				}
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
		
		ListOfPackageTourQuotationRecordsCriteria criteria = (ListOfPackageTourQuotationRecordsCriteria)criteriaObject;
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
		//2018-01-04 cheung_cheng [MB9012] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
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
			if("Y".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "Field"));
			else if("N".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "No Field"));
			else if("All".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "All"));
		}
		
		if (descBuilder.length() > 0) descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		descBuilder.append("\n");
		descBuilder.append("Export Type: XLSX");

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
		for (String header : headersA){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		for (String header : headersB){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
	public void createDynamicHeader(SXSSFRow row, int prodAttrSize){
		int cnt = 0;
		for (String header : headersA){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		String productAttributeHeader = "Product Attributes ";
		for(int i=1; i<prodAttrSize+1; i++){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(productAttributeHeader+i);
			cnt++;
		}
		for (String header : headersB){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
}
