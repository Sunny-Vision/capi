package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SubPriceFieldMappingDao;
import capi.entity.ImportExportTask;
import capi.entity.SubPriceFieldMapping;
import capi.entity.SubPriceType;
import capi.service.CommonService;

@Service("ExportQuotationQRRUAService")
public class ExportQuotationQRRUAService extends DataExportServiceBase{

	@Autowired
	private QuotationRecordDao dao;
	
	@Autowired
	private SubPriceFieldMappingDao subPriceFieldMappingDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String[]{
			"Quotation record ID", "Reference Month", "Collection Date", "Reference Date", "Is Back No. quotation records"
			, "Is Backtrack quotation records", "Original Quotation Record ID (for Backtrack & BackNo.)"
			, "Is Product Change", "Is New Product", "Is New Recruitment", "Is New Outlet"
			, "Quotation record status", "Reject Reason", "Enumeration status", "Quotation ID", "Quotation Status"
			, "Quotation State", "RUA since", "Assignment ID", "Batch Code", "Variety Code", "Purpose"
			, "CPI Base Period", "Variety English Name", "Variety Chinese Name", "Variety Standard UOM value", "Variety Standard UOM"
			, "Seasonality", "Ordinary / Tour form"
			, "Outlet Code", 
			//"Outlet Name", 
			"Outlet Type Code", "Outlet Type English Name"
			, "District Code", "TPU Code"
			, "CPI Compilation Series", "CPI Quotation Type", "Is ICP", "ICP Product Code"
			, "ICP Product Name", "ICP Type (Unit)", "ICP Type (Quotation)"
			, "Product Group ID", "Product Group Code", "Product Group English Name", "Product Group Chinese Name"
			, "Product ID", "Country of Origin"
			, "Product Attribute 1", "Product Attribute 2", "Product Attribute 3", "Product Attribute 4", "Product Attribute 5"
			, "Product Remarks", "Product Barcode", "Product Position", "Availability", "N Price", "S Price"
			, "Is NS Price not applicable", "UOM Chinese Name & English Name", "UOM value", "Reason", "Discount"
			, "Discount remarks", "Quotation record remarks", "Outlet Discount Remarks", "Category remarks"
			//, "Contact person"
			, "Is collect FR", "FR", "Is FR in percentage", "Is consignment counter"
			//, "Consignment counter name"
			, "Consignment counter remarks", "FRAdmin", "FRField"
			, "Is Outlet Information verified", "Is Category Information verified", "Is Quotation Information verified"
			, "Verification Remarks", "Verification Reply", "Is visited for Verfication / Revisit"
			, "PE Check Remarks", "Responsible Field officer ID", "Last Modified By", "Last Modified Date" 
			};

	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 22;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		Date referenceMonth = task.getReferenceMonth();
		Integer purposeId = task.getPurposeId();
		
		//SubPriceField Header Create
		List<SubPriceFieldMapping> mappings = new ArrayList<SubPriceFieldMapping>();
		List<Integer> subPriceFieldMappingIds = new ArrayList<Integer>();
		List<String> subPriceFields = new ArrayList<String>();
		Integer subPriceTypeId = null;
		
		SubPriceType subPriceType = task.getSubPriceType();
		if(subPriceType != null){
			subPriceTypeId = subPriceType.getSubPriceTypeId();
			
			mappings = subPriceFieldMappingDao.getSubPriceFieldMappingByTypeId(subPriceType.getSubPriceTypeId());
			for(SubPriceFieldMapping mapping : mappings){
				subPriceFieldMappingIds.add(mapping.getSubPriceFieldMappingId());
				if(mapping.getSubPriceField()!=null)
					subPriceFields.add(mapping.getSubPriceField().getFieldName());
			}
			createSubPriceFieldHeader(sheet, subPriceFields);
		}
		
		List results = dao.getAllQuotationRecordResultByType(referenceMonth, subPriceTypeId, subPriceFieldMappingIds, purposeId);
		Iterator iter = results.iterator();
		int rowCnt = 1;
		
		while ( iter.hasNext() ){
			Map map = (Map) iter.next();
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			SXSSFCell cell = row.createCell(cellCnt);
			if(map.get("quotationRecordId")!=null)
				cell.setCellValue(String.valueOf((Integer)map.get("quotationRecordId")));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			if(map.get("surveyMonth")!=null)
				cell.setCellValue(commonService.formatMonth((Date)map.get("surveyMonth")));
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			if(map.get("collectionDate")!=null)
				cell.setCellValue(commonService.formatDate((Date)map.get("collectionDate")));
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			if(map.get("referenceDate")!=null)
				cell.setCellValue(commonService.formatDate((Date)map.get("referenceDate")));
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			if(map.get("isBackNo")!=null)
				cell.setCellValue((Boolean)map.get("isBackNo"));
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			if(map.get("isBackTrack")!=null)
				cell.setCellValue((Boolean)map.get("isBackTrack"));
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			if(map.get("originalQuotationRecordId")!=null)
				cell.setCellValue(String.valueOf((Integer)map.get("originalQuotationRecordId")));
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			if(map.get("isProductChange")!=null)
				cell.setCellValue((Boolean)map.get("isProductChange"));
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			if(map.get("isNewProduct")!=null)
				cell.setCellValue((Boolean)map.get("isNewProduct"));
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			if(map.get("isNewRecruitment")!=null)
				cell.setCellValue((Boolean)map.get("isNewRecruitment"));
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			if(map.get("isNewOutlet")!=null)
				cell.setCellValue((Boolean)map.get("isNewOutlet"));
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);	
			if(map.get("quotationRecordStatus")!=null)
				cell.setCellValue((String)map.get("quotationRecordStatus"));
			
			cellCnt = 12;
			cell = row.createCell(cellCnt);	
			if(map.get("rejectReason")!=null)
				cell.setCellValue((String)map.get("rejectReason"));
			
			cellCnt = 13;
			cell = row.createCell(cellCnt);	
			if(map.get("firmStatus")!=null){
				switch ((Integer)map.get("firmStatus")) {  //1.Enumeration (EN) 2.In Progress (IP) 3.Closed (CL) 
				 // 4.Move (MV) 5.Not Suitable (NS) 6.Refusal (NR) 
				 //7.Wrong Outlet (WO) 8.Door Lock (DL) 9.Non-contact (NC) 10.Duplication (DU) 
					case 1:
						cell.setCellValue("Enumeration (EN)");
						break;
					case 2:
						cell.setCellValue("Closed (CL)");
						break;
					case 3:
						cell.setCellValue("Move (MV)");
						break;
					case 4:
						cell.setCellValue("Not Suitable (NS)");
						break;
					case 5:
						cell.setCellValue("Refusal (NR)");
						break;
					case 6:
						cell.setCellValue("Wrong Outlet (WO)");
						break;
					case 7:
						cell.setCellValue("Door Lock (DL)");
						break;
					case 8:
						cell.setCellValue("Non-contact (NC)");
						break;
					case 9:
						cell.setCellValue("In Progress (IP)");
						break;
					case 10:
						cell.setCellValue("Duplication (DU)");
						break;
					default:
						cell.setCellValue("");
						break;
					}
			}
			
			
			cellCnt = 14;
			cell = row.createCell(cellCnt);
			if(map.get("quotationId")!=null){
				cell.setCellValue(String.valueOf((Integer)map.get("quotationId")));
			}
			
			cellCnt = 15;
			cell = row.createCell(cellCnt);
			if(map.get("quotationStatus")!=null)
				cell.setCellValue((String)map.get("quotationStatus"));
			
			cellCnt = 16;
			cell = row.createCell(cellCnt);	
			if(map.get("quotationState")!=null)
				cell.setCellValue((String)map.get("quotationState"));
			
			cellCnt = 17;
			cell = row.createCell(cellCnt);	
			if(map.get("ruaDate")!=null)
				cell.setCellValue(commonService.formatDate((Date)map.get("ruaDate")));
			
			cellCnt = 18;
			cell = row.createCell(cellCnt);
			if(map.get("assignmentId")!=null){
				cell.setCellValue(String.valueOf((Integer)map.get("assignmentId")));
			}
			
			cellCnt = 19;
			cell = row.createCell(cellCnt);	
			if(map.get("batchCode")!=null)
				cell.setCellValue((String)map.get("batchCode"));
			
			cellCnt = 20;
			cell = row.createCell(cellCnt);	
			if(map.get("unitCode")!=null)
				cell.setCellValue((String)map.get("unitCode"));
			
			cellCnt = 21;
			cell = row.createCell(cellCnt);	
			if(map.get("purposeCode")!=null)
				cell.setCellValue((String)map.get("purposeCode"));
			
			cellCnt = 22;
			cell = row.createCell(cellCnt);	
			if(map.get("cpiBasePeriod")!=null)
				cell.setCellValue((String)map.get("cpiBasePeriod"));
			
			cellCnt = 23;
			cell = row.createCell(cellCnt);	
			if(map.get("unitEnglishName")!=null)
				cell.setCellValue((String)map.get("unitEnglishName"));
			
			cellCnt = 24;
			cell = row.createCell(cellCnt);	
			if(map.get("unitChineseName")!=null)
				cell.setCellValue((String)map.get("unitChineseName"));
			
			cellCnt = 25;
			cell = row.createCell(cellCnt);
			if(map.get("unitUOMValue")!=null)
				cell.setCellValue((Double)map.get("unitUOMValue"));
			
			cellCnt = 26;
			cell = row.createCell(cellCnt);
			if(map.get("unitUOMEnglishName")!=null)
				cell.setCellValue((String)map.get("unitUOMEnglishName"));
			
			cellCnt = 27;
			cell = row.createCell(cellCnt);	
			if(map.get("seasonality")!=null){
				switch((Integer)map.get("seasonality")){  //1- All-time, 2- Summer, 3- Winter, 4- Occasional
				case 1:
					cell.setCellValue("All-time");
					break;
				case 2:
					cell.setCellValue("Summer");
					break;
				case 3:
					cell.setCellValue("Winter");
					break;
				case 4:
					cell.setCellValue("Occasional");
					break;
				default: 
					cell.setCellValue("");
					break;
				}
			}
		
			
			cellCnt = 28;
			cell = row.createCell(cellCnt);	
			if(map.get("formDisplay")!=null){
				switch((Integer)map.get("formDisplay")){  //1 - normal, 2 - tour
				case 1:
					cell.setCellValue("Normal");
					break;
				case 2:
					cell.setCellValue("Tour");
					break;
				default:
					cell.setCellValue("");
					break;
				}
			}
			
			
			cellCnt = 29;
			cell = row.createCell(cellCnt);	
			if(map.get("outletCode")!=null){
				cell.setCellValue(String.valueOf((Integer)map.get("outletCode")));
			}
			
//			cellCnt = 30;
//			cell = row.createCell(cellCnt);	
//			if(map.get("outletName")!=null)
//				cell.setCellValue((String)map.get("outletName"));
			
			cellCnt = 30;
			cell = row.createCell(cellCnt);	
			if(map.get("outletTypeCode")!=null)
				cell.setCellValue(String.valueOf(map.get("outletTypeCode")));
			
			cellCnt = 31;
			cell = row.createCell(cellCnt);
			if(map.get("outletTypeEnglishName")!=null)
				cell.setCellValue((String)map.get("outletTypeEnglishName"));
			
			cellCnt = 32;
			cell = row.createCell(cellCnt);	
			if(map.get("districtCode")!=null)
				cell.setCellValue((String)map.get("districtCode"));
			
			cellCnt = 33;
			cell = row.createCell(cellCnt);
			if(map.get("tpuCode")!=null)
				cell.setCellValue((String)map.get("tpuCode"));
			
			cellCnt = 34;
			cell = row.createCell(cellCnt);	
			if(map.get("cpiCompilationSeries")!=null)
				cell.setCellValue((String)map.get("cpiCompilationSeries"));
			
			cellCnt = 35;
			cell = row.createCell(cellCnt);	
			if(map.get("cpiQuotationType")!=null)
				cell.setCellValue(String.valueOf((Integer)map.get("cpiQuotationType")));
			
			cellCnt = 36;
			cell = row.createCell(cellCnt);	
			if(map.get("isICP")!=null)
				cell.setCellValue((Boolean)map.get("isICP"));
			
			cellCnt = 37;
			cell = row.createCell(cellCnt);	
			if(map.get("icpProductCode")!=null)
				cell.setCellValue((String)map.get("icpProductCode"));
			
			cellCnt = 38;
			cell = row.createCell(cellCnt);	
			if(map.get("icpProductName")!=null)
				cell.setCellValue((String)map.get("icpProductName"));
			
			cellCnt = 39;
			cell = row.createCell(cellCnt);	
			if(map.get("unitICPType")!=null)
				cell.setCellValue((String)map.get("unitICPType"));
			
			cellCnt = 40;
			cell = row.createCell(cellCnt);	
			if(map.get("quotationICPType")!=null)
				cell.setCellValue((String)map.get("quotationICPType"));
			
			cellCnt = 41;
			cell = row.createCell(cellCnt);
			if(map.get("productGroupId")!=null)
				cell.setCellValue(String.valueOf((Integer)map.get("productGroupId")));
			
			cellCnt = 42;
			cell = row.createCell(cellCnt);
			if(map.get("productGroupCode")!=null)
				cell.setCellValue((String)map.get("productGroupCode"));
			
			cellCnt = 43;
			cell = row.createCell(cellCnt);	
			if(map.get("productGroupEnglishName")!=null)
				cell.setCellValue((String)map.get("productGroupEnglishName"));
			
			cellCnt = 44;
			cell = row.createCell(cellCnt);	
			if(map.get("productGroupChineseName")!=null)
				cell.setCellValue((String)map.get("productGroupChineseName"));
			
			cellCnt = 45;
			cell = row.createCell(cellCnt);
			if(map.get("productId")!=null){
				cell.setCellValue(String.valueOf((Integer)map.get("productId")));
			}
			
			cellCnt = 46;
			cell = row.createCell(cellCnt);	
			if(map.get("countryOfOrigin")!=null)
				cell.setCellValue((String)map.get("countryOfOrigin"));
			
			cellCnt = 47;
			cell = row.createCell(cellCnt);
			if(map.get("pa1Name")!=null){
				String ps1Value = (String)map.get("ps1Value") == null ? "" : (String)map.get("ps1Value");
				cell.setCellValue((String)map.get("pa1Name") + " : " + ps1Value);
			}
				
			
			cellCnt = 48;
			cell = row.createCell(cellCnt);
			if(map.get("pa2Name")!=null){
				String ps2Value = (String)map.get("ps2Value") == null ? "" : (String)map.get("ps2Value");
				cell.setCellValue((String)map.get("pa2Name") + " : " + ps2Value);
			}
				

			cellCnt = 49;
			cell = row.createCell(cellCnt);
			if(map.get("pa3Name")!=null){
				String ps3Value = (String)map.get("ps3Value") == null ? "" : (String)map.get("ps3Value");
				cell.setCellValue((String)map.get("pa3Name") + " : " + ps3Value);
			}
				

			cellCnt = 50;
			cell = row.createCell(cellCnt);
			if(map.get("pa4Name")!=null){
				String ps4Value = (String)map.get("ps4Value") == null ? "" : (String)map.get("ps4Value");
				cell.setCellValue((String)map.get("pa4Name") + " : " + ps4Value);
			}

			cellCnt = 51;
			cell = row.createCell(cellCnt);
			if(map.get("pa5Name")!=null){
				String ps5Value = (String)map.get("ps5Value") == null ? "" : (String)map.get("ps5Value");
				cell.setCellValue((String)map.get("pa5Name") + " : " + ps5Value);
			}
			
			cellCnt = 52;
			cell = row.createCell(cellCnt);	
			if(map.get("productRemark")!=null)
				cell.setCellValue((String)map.get("productRemark"));
			
			cellCnt = 53;
			cell = row.createCell(cellCnt);	
			if(map.get("barcode")!=null)
				cell.setCellValue((String)map.get("barcode"));
			
			cellCnt = 54;
			cell = row.createCell(cellCnt);
			if(map.get("productPosition")!=null)
				cell.setCellValue((String)map.get("productPosition"));
			
			cellCnt = 55;
			cell = row.createCell(cellCnt);
			if(map.get("availability")!=null){
				switch((Integer)map.get("availability")){  // 1- Available2- IP3- 有價無貨4- 無價無貨 -> 缺貨5- Not Suitable6- 回倉7- 無團8- 未返9- New
				case 1:
					cell.setCellValue("Available");
					break;
				case 2:
					cell.setCellValue("IP");
					break;
				case 3:
					cell.setCellValue("有價無貨");
					break;
				case 4:
					cell.setCellValue("缺貨");
					break;
				case 5:
					cell.setCellValue("Not Suitable");
					break;
				case 6:
					cell.setCellValue("回倉");
					break;
				case 7:
					cell.setCellValue("無團");
					break;
				case 8:
					cell.setCellValue("未返");
					break;
				case 9:
					cell.setCellValue("New");
					break;
				default:
					cell.setCellValue("");
					break;
				}
			}
			
			
			cellCnt = 56;
			cell = row.createCell(cellCnt);
			if(map.get("nPrice")!=null){
				cell.setCellValue((Double)map.get("nPrice"));
			}
			
			cellCnt = 57;
			cell = row.createCell(cellCnt);
			if(map.get("sPrice")!=null){
				cell.setCellValue((Double)map.get("sPrice"));
			}
			
			cellCnt = 58;
			cell = row.createCell(cellCnt);
			if(map.get("isSPricePeculiar")!=null)
				cell.setCellValue((Boolean)map.get("isSPricePeculiar"));
			
			cellCnt = 59;
			cell = row.createCell(cellCnt);
			String uomName = "";
			if(map.get("uomChineseName")!=null)
				uomName = (String)map.get("uomChineseName");
			
			if(map.get("uomEnglishName")!=null){
				if(map.get("uomChineseName")!=null){
					uomName += " & ";
				}
				uomName += (String)map.get("uomEnglishName");
			}
				
			cell.setCellValue(uomName);
			
			cellCnt = 60;
			cell = row.createCell(cellCnt);
			if(map.get("uomValue")!=null){	
				cell.setCellValue((Double)map.get("uomValue"));
			}
			
			cellCnt = 61;
			cell = row.createCell(cellCnt);
			if(map.get("reason")!=null)
				cell.setCellValue((String)map.get("reason"));
			
			cellCnt = 62;
			cell = row.createCell(cellCnt);
			if(map.get("discount")!=null){
				cell.setCellValue((String)map.get("discount"));
			}
			
			cellCnt = 63;
			cell = row.createCell(cellCnt);
			if(map.get("discountRemark")!=null)
				cell.setCellValue((String)map.get("discountRemark"));
			
			cellCnt = 64;
			cell = row.createCell(cellCnt);
			if(map.get("quotationRecordRemark")!=null)
				cell.setCellValue((String)map.get("quotationRecordRemark"));
			
			cellCnt = 65;
			cell = row.createCell(cellCnt);
			if(map.get("outletDiscountRemark")!=null)
				cell.setCellValue((String)map.get("outletDiscountRemark"));
			
			cellCnt = 66;
			cell = row.createCell(cellCnt);
			if(map.get("categoryRemark")!=null)
				cell.setCellValue((String)map.get("categoryRemark"));
			
//			cellCnt = 67;
//			cell = row.createCell(cellCnt);
//			if(map.get("contactPerson")!=null)
//				cell.setCellValue((String)map.get("contactPerson"));
			
			cellCnt = 67;
			cell = row.createCell(cellCnt);
			if(map.get("isCollectFR")!=null)
				cell.setCellValue((Boolean)map.get("isCollectFR"));
			
			cellCnt = 68;
			cell = row.createCell(cellCnt);
			if(map.get("fr")!=null){
				cell.setCellValue((Double)map.get("fr"));
			}
			
			cellCnt = 69;
			cell = row.createCell(cellCnt);
			if(map.get("isFRPercentage")!=null)
				cell.setCellValue((Boolean)map.get("isFRPercentage"));
			
			cellCnt = 70;
			cell = row.createCell(cellCnt);
			if(map.get("isConsignmentCounter")!=null)
				cell.setCellValue((Boolean)map.get("isConsignmentCounter"));
			
//			cellCnt = 72;
//			cell = row.createCell(cellCnt);
//			if(map.get("consignmentCounterName")!=null)
//				cell.setCellValue((String)map.get("consignmentCounterName"));
			
			cellCnt = 71;
			cell = row.createCell(cellCnt);
			if(map.get("consignmentCounterRemark")!=null)
				cell.setCellValue((String)map.get("consignmentCounterRemark"));
			
			cellCnt = 72;
			cell = row.createCell(cellCnt);
			if(map.get("frAdmin")!=null){
				cell.setCellValue((Double)map.get("frAdmin"));
			}
			
			cellCnt = 73;
			cell = row.createCell(cellCnt);
			if(map.get("frField")!=null){
				cell.setCellValue((Double)map.get("frField"));
			}
			
			cellCnt = 74;
			cell = row.createCell(cellCnt);
			if(map.get("isVerifyFirm")!=null)
				cell.setCellValue((Boolean)map.get("isVerifyFirm"));
			
			cellCnt = 75;
			cell = row.createCell(cellCnt);
			if(map.get("isVerifyCategory")!=null)
				cell.setCellValue((Boolean)map.get("isVerifyCategory"));
			
			cellCnt = 76;
			cell = row.createCell(cellCnt);
			if(map.get("isVerifyQuotation")!=null)
				cell.setCellValue((Boolean)map.get("isVerifyQuotation"));
			
			cellCnt = 77;
			cell = row.createCell(cellCnt);
			if(map.get("verificationRemark")!=null)
				cell.setCellValue((String)map.get("verificationRemark"));
			
			cellCnt = 78;
			cell = row.createCell(cellCnt);
			if(map.get("verificationReply")!=null)
				cell.setCellValue((String)map.get("verificationReply"));
			
			cellCnt = 79;
			cell = row.createCell(cellCnt);
			if(map.get("isVisited")!=null)
				cell.setCellValue((Boolean)map.get("isVisited"));
			
			cellCnt = 80;
			cell = row.createCell(cellCnt);
			if(map.get("peCheckRemark")!=null)
				cell.setCellValue((String)map.get("peCheckRemark"));
				
			cellCnt = 81;
			cell = row.createCell(cellCnt);
			if(map.get("staffCode")!=null)
				cell.setCellValue((String)map.get("staffCode"));
			
			cellCnt = 82;
			cell = row.createCell(cellCnt);
			cell.setCellValue((String)map.get("modifiedBy"));
			
			cellCnt = 83;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate((Date)map.get("modifiedDate")));
			
			if(subPriceTypeId!=null){
				cellCnt = 84;
				cell = row.createCell(cellCnt);
				if(map.get("subPriceRecordId")!=null)
					cell.setCellValue(String.valueOf((Integer)map.get("subPriceRecordId")));
				
				cellCnt = 85;
				for(int i=0; i < subPriceFieldMappingIds.size(); i++){
					cell = row.createCell(cellCnt++);
					if(map.get("columnValue"+i)!=null)
						cell.setCellValue((String)map.get("columnValue"+i));
				}
				cell = row.createCell(cellCnt++);
				cell.setCellValue((String)map.get("srSPrice"));
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue((String)map.get("srNPrice"));
				
				cell = row.createCell(cellCnt++);
				cell.setCellValue((String)map.get("srDiscount"));
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
	
	public void createSubPriceFieldHeader(SXSSFSheet sheet, List<String> subPriceHeaders){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cellCnt = headers.length;
		SXSSFCell cellHeader = row.createCell(cellCnt++);
		cellHeader.setCellValue("Sub Price Record Id");
		for(String header : subPriceHeaders){
			cellHeader = row.createCell(cellCnt);
			cellHeader.setCellValue(header);
			
			cellCnt++;
		}
		cellHeader = row.createCell(cellCnt++);
		cellHeader.setCellValue("Sub Price Record S Price");
		cellHeader = row.createCell(cellCnt++);
		cellHeader.setCellValue("Sub Price Record N Price");
		cellHeader = row.createCell(cellCnt++);
		cellHeader.setCellValue("Sub Price Record Discount");
	}
}
