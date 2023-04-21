package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.entity.ImportExportTask;
import capi.model.dataImportExport.ExportEditedPriceList;
import capi.service.CommonService;

@Service("ExportEditedPriceService")
public class ExportEditedPriceService extends DataExportServiceBase{

	@Autowired
	private IndoorQuotationRecordDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String[]{
			"Indoor Quotation Record ID","Reference Month", "Quotation record ID", "Reference Date", "Whether fieldwork is needed"
			, "Is Product Change", "Is New Product", "Is New Recruitment", "Is New Outlet", "Data Conversion Status"
			, "Quotation ID", "Quotation Status", "Batch Code", "CPI Form Type", "Variety Code"
			, "Purpose", "CPI Base Period", "Variety English Name", "Variety Chinese Name", "Seasonality"
			, "CPI Compilation Series", "CPI Quotation Type", "Is ICP", "ICP Product Code", "ICP Product Name"
			, "ICP Type (Data Export: Unit/Quotation)", "Outlet Code", 
			//"Outlet Name", 
			"Outlet Type Code", "Outlet Type English Name"
			, "District Code", "TPU Code", "District Council", "District Name (English)", 
			//"Outlet Detail Address"
			//, "Indoor Market Name", 
			"Product Group id", "Product Group Code", "Product Group English Name", "Product Group Chinese Name"
			, "Product ID", "Country of Origin", "Product Attribute 1", "Product Attribute 2", "Product Attribute 3"
			, "Product Attribute 4", "Product Attribute 5", "Last Product Date", "Allocated Indoor officer ID", "Original N Price"
			, "Original S Price", "N Price After UOM Conversion", "S Price After UOM Conversion", "Computed N Price", "Computed S Price"
			, "Current N Price", "Is Null Current N Price", "Current S Price", "Is Null Current S Price", "Previous N Price"
			, "Is Null Previous N Price", "Previous S Price", "Is Null Previous S Price", "Back No Last N Pirce", "Back No Last S Price"
			, "Last N Price", "Last S Price", "Last Price Date", "Copy Price Type", "Copy Last Price Type"
			, "Data Conversion Remarks", "Is Current Price Keep No", "Is RUA", "RUA Date", "Is Product Not Available"
			, "Product Not Available From", "Firm Verify", "Category Verify", "Quotation Verify", "Verification Reason"
			, "Is Splicing", "Is Outlier", "Outlier Remarks", "FR", "Is Apply FR"
			, "Is FR Percentage", "Last FR Applied Date", "Imputed Quotation Price", "Imputed Quotation Remarks", "Imputed Unit Price"
			, "Imputed Unit Remarks", "Firm Remark", "Category Remark", "Quotation Remark"
			, "Last Modified By", "Last Modified Date"
	};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 24;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		Date referenceMonth = task.getReferenceMonth();
		Integer purposeId = task.getPurposeId();
		
		List<ExportEditedPriceList> results = dao.getResultsByMonth(referenceMonth, purposeId);
		
		int rowCnt = 1;
		for (ExportEditedPriceList code : results){
			
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			
			//IndoorQuotationRecord
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(code.getIndoorQuotationRecordId()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			if(code.getReferenceMonth()!=null)
				cell.setCellValue(commonService.formatMonth(code.getReferenceMonth()));
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			if(code.getQuotationRecordId()!=null)
				cell.setCellValue(String.valueOf(code.getQuotationRecordId()));
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			if(code.getReferenceDate()!=null)
				cell.setCellValue(commonService.formatDate(code.getReferenceDate()));
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(!code.isNoField());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isProductChange());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNewProduct());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNewRecruitment());
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNewOutlet());
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getStatus());
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			if(code.getQuotationId()!=null)
				cell.setCellValue(String.valueOf(code.getQuotationId()));
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getQuotationStatus());
			
			cellCnt = 12;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getBatchCode());
			
			cellCnt = 13;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getFormType());
			
			cellCnt = 14;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitCode());
			
			cellCnt = 15;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getPurposeCode());
			
			cellCnt = 16;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiBasePeriod());
			
			cellCnt = 17;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitEnglishName());
			
			cellCnt = 18;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitChineseName());
			
			cellCnt = 19;
			cell = row.createCell(cellCnt);
			if(code.getSeasonality()!=null)
				cell.setCellValue(String.valueOf(code.getSeasonality()));
			
			cellCnt = 20;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiCompilationSeries());
			
			cellCnt = 21;
			cell = row.createCell(cellCnt);
			if(code.getCpiQuotationType()!=null)
				cell.setCellValue(String.valueOf(code.getCpiQuotationType()));
			
			cellCnt = 22;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isICP());
			
			cellCnt = 23;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpProductCode());
			
			cellCnt = 24;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpProductName());
			
			cellCnt = 25;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpType());
			
			cellCnt = 26;
			cell = row.createCell(cellCnt);
			if(code.getOutletFirmCode()!=null)
				cell.setCellValue(String.valueOf(code.getOutletFirmCode()));
			
//			cellCnt = 27;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(code.getOutletName());
			
			cellCnt = 27;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOutletTypeCode());
			
			cellCnt = 28;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOutletTypeEnglishName());
			
			cellCnt = 29;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getDistrictCode());
			
			cellCnt = 30;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getTpuCode());
			
			cellCnt = 31;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCouncilDistrict());
			
			cellCnt = 32;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getDistrictEnglishName());
			
//			cellCnt = 33;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(code.getDetailAddress());
			
//			cellCnt = 34;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(code.getIndoorMarketName());
			
			cellCnt = 33;
			cell = row.createCell(cellCnt);
			if(code.getProductGroupId()!=null)
				cell.setCellValue(String.valueOf(code.getProductGroupId()));

			cellCnt = 34;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getProductGroupCode());
			
			cellCnt = 35;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getProductGroupEnglishName());
			
			cellCnt = 36;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getProductGroupChineseName());
			
			cellCnt = 37;
			cell = row.createCell(cellCnt);
			if(code.getProductId()!=null)
				cell.setCellValue(String.valueOf(code.getProductId()));
			
			cellCnt = 38;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCountryOfOrigin());
			
			cellCnt = 39;
			cell = row.createCell(cellCnt);
			if (code.getPa1Name()!=null){
				String ps1Value = code.getPs1Value() == null ? "" : code.getPs1Value();
				cell.setCellValue(code.getPa1Name() + " : " + ps1Value);
			}
			
			cellCnt = 40;
			cell = row.createCell(cellCnt);
			if (code.getPa2Name()!=null){
				String ps2Value = code.getPs2Value() == null ? "" : code.getPs2Value();
				cell.setCellValue(code.getPa2Name() + " : " + ps2Value);
			}

			cellCnt = 41;
			cell = row.createCell(cellCnt);
			if (code.getPa3Name()!=null){
				String ps3Value = code.getPs3Value() == null ? "" : code.getPs3Value();
				cell.setCellValue(code.getPa3Name() + " : " + ps3Value);
			}

			cellCnt = 42;
			cell = row.createCell(cellCnt);
			if (code.getPa4Name()!=null){
				String ps4Value = code.getPs4Value() == null ? "" : code.getPs4Value();
				cell.setCellValue(code.getPa4Name() + " : " + ps4Value);
			}

			cellCnt = 43;
			cell = row.createCell(cellCnt);
			if (code.getPa5Name()!=null){
				String ps5Value = code.getPs5Value() == null ? "" : code.getPs5Value();
				cell.setCellValue(code.getPa5Name() + " : " + ps5Value);
			}
			
			cellCnt = 44;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getLastProductChangeDate()));
			
			cellCnt = 45;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getStaffCode());
			
			cellCnt = 46;
			cell = row.createCell(cellCnt);
			if(code.getOriginalNPrice()!=null)
				cell.setCellValue(code.getOriginalNPrice());
			
			cellCnt = 47;
			cell = row.createCell(cellCnt);
			if(code.getOriginalSPrice()!=null)
				cell.setCellValue(code.getOriginalSPrice());
			
			cellCnt = 48;
			cell = row.createCell(cellCnt);
			if(code.getnPriceAfterUOMConversion()!=null)
				cell.setCellValue(code.getnPriceAfterUOMConversion());
			
			cellCnt = 49;
			cell = row.createCell(cellCnt);
			if(code.getsPriceAfterUOMConversion()!=null)
				cell.setCellValue(code.getsPriceAfterUOMConversion());
			
			cellCnt = 50;
			cell = row.createCell(cellCnt);
			if(code.getComputedNPrice()!=null)
				cell.setCellValue(code.getComputedNPrice());
			
			cellCnt = 51;
			cell = row.createCell(cellCnt);
			if(code.getComputedSPrice()!=null)
				cell.setCellValue(code.getComputedSPrice());
			
			cellCnt = 52;
			cell = row.createCell(cellCnt);
			if(code.getCurrentNPrice()!=null)
				cell.setCellValue(code.getCurrentNPrice());
			
			cellCnt = 53;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNullCurrentNPrice());
			
			cellCnt = 54;
			cell = row.createCell(cellCnt);
			if(code.getCurrentSPrice()!=null)
				cell.setCellValue(code.getCurrentSPrice());
			
			cellCnt = 55;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNullCurrentSPrice());
			
			cellCnt = 56;
			cell = row.createCell(cellCnt);
			if(code.getPreviousNPrice()!=null)
				cell.setCellValue(code.getPreviousNPrice());
			
			cellCnt = 57;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNullPreviousNPrice());
			
			cellCnt = 58;
			cell = row.createCell(cellCnt);
			if(code.getPreviousSPrice()!=null)
				cell.setCellValue(code.getPreviousSPrice());
			
			cellCnt = 59;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNullPreviousSPrice());
			
			cellCnt = 60;
			cell = row.createCell(cellCnt);
			if(code.getBackNoLastNPirce()!=null)
				cell.setCellValue(code.getBackNoLastNPirce());
			
			cellCnt = 61;
			cell = row.createCell(cellCnt);
			if(code.getBackNoLastSPrice()!=null)
				cell.setCellValue(code.getBackNoLastSPrice());
			
			cellCnt = 62;
			cell = row.createCell(cellCnt);
			if(code.getLastNPrice()!=null)
				cell.setCellValue(code.getLastNPrice());
			
			cellCnt = 63;
			cell = row.createCell(cellCnt);
			if(code.getLastSPrice()!=null)
				cell.setCellValue(code.getLastSPrice());
			
			cellCnt = 64;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getLastPriceDate()));
			
			cellCnt = 65;
			cell = row.createCell(cellCnt);
			if(code.getCopyPriceType()!=null)
				cell.setCellValue(String.valueOf(code.getCopyPriceType()));
			
			cellCnt = 66;
			cell = row.createCell(cellCnt);
			if(code.getCopyLastPriceType()!=null)
				cell.setCellValue(String.valueOf(code.getCopyLastPriceType()));
			
			cellCnt = 67;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getRemark());
			
			cellCnt = 68;
			cell = row.createCell(cellCnt);
			if(code.getIsCurrentPriceKeepNo()!=null)
				cell.setCellValue(code.getIsCurrentPriceKeepNo());
			
			cellCnt = 69;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isRUA());
			
			cellCnt = 70;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getRuaDate()));
			
			cellCnt = 71;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isProductNotAvailable());
			
			cellCnt = 72;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getProductNotAvailableFrom()));
			
			cellCnt = 73;
			if (code.getFirmVerify()!=null){
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(code.getFirmVerify()));
			}
			
			cellCnt = 74;
			if (code.getCategoryVerify()!=null){
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(code.getCategoryVerify()));
			}
			
			cellCnt = 75;
			if (code.getQuotationVerify()!=null){
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(code.getQuotationVerify()));
			}
			
			cellCnt = 76;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getRejectReason());
			
			cellCnt = 77;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isSpicing());
			
			cellCnt = 78;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isOutlier());
			
			cellCnt = 79;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOutlierRemark());
			
			cellCnt = 80;
			cell = row.createCell(cellCnt);
			if(code.getFr()!=null)
				cell.setCellValue(code.getFr());
			
			cellCnt = 81;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isApplyFR());
			
			cellCnt = 82;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isFRPercentage());
			
			cellCnt = 83;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getLastFRAppliedDate()));
			
			cellCnt = 84;
			cell = row.createCell(cellCnt);
			if(code.getImputedQuotationPrice()!=null)
				cell.setCellValue(code.getImputedQuotationPrice());
			
			cellCnt = 85;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getImputedQuotationRemark());
			
			cellCnt = 86;
			cell = row.createCell(cellCnt);
			if(code.getImputedUnitPrice()!=null)
				cell.setCellValue(code.getImputedUnitPrice());
			
			cellCnt = 87;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getImputedUnitRemark());
			
			cellCnt = 88;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getFirmRemark());
			
			cellCnt = 89;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCategoryRemark());
			
			cellCnt = 90;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getQuotationRemark());
			
			cellCnt = 91;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getModifiedBy());
			
			cellCnt = 92;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getModifiedDate()));
			
//			//For ProductAttribute
//			if(code.getProductId()!=null){
//				cellCnt = 42;
//				
//				List<VwProductFullSpec> productAttributes = vwProductFullSpecDao.GetAllByProductId(code.getProductId(), 5);
//				for(VwProductFullSpec productAttribute : productAttributes){
//					cell = row.createCell(cellCnt++);
//					cell.setCellValue(productAttribute.getSpecificationName());
//				}
//			}
			
			//outletType
//			Set<VwOutletTypeShortForm> outletTypes = outlet.getOutletTypes();
//			
//			List<String> outletTypeCodeList = new ArrayList<String>();
//			List<String> outletTypeEngNameList = new ArrayList<String>();
//			
//			for(VwOutletTypeShortForm outletType : outletTypes){
//				outletTypeCodeList.add(outletType.getShortCode());
//				outletTypeEngNameList.add(outletType.getEnglishName());
//			}
//			String outletTypeCode = StringUtils.join(outletTypeCodeList, ',');
//			String outletTypeEngName = StringUtils.join(outletTypeEngNameList, ',');
			
			rowCnt++;
			
		}
		
		///results.close();
		
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
}
