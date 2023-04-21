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
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.UOMCategoryDao;
import capi.dal.UnitDao;
import capi.entity.ImportExportTask;
import capi.entity.PricingFrequency;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.SubItem;
import capi.entity.SubPriceType;
import capi.entity.UOMCategory;
import capi.entity.Unit;
import capi.entity.Uom;

@Service("ExportUnitMaintenanceService")
public class ExportUnitMaintenanceService extends DataExportServiceBase{
	@Autowired
	private UnitDao dao;
	
	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers={
			"Variety Id", "Variety Code", "Chinese Name", "English Name", "DisplayName"
			, "Purpose", "CPI Base Period", "Variety Category", "Product Group Id", "Product Group Code"
			, "Ordinary / Tour Form", "CPI Quotation Type (Market / Supermarket / Batch / Others)", "Pricing Frequency Id"
			, "Pricing Frequency Name", "Seasonality"
			, "Season Start Month (For Seasonality = Occasional)", "Season End Month (For Seasonality = Occasional)", "RTN Period"
			, "Standard UOM Id", "Standard UOM Name", "Standard UOM Value", "UOM Category allowed", "SubItem Compilation Method", "Data Transmission Rule"
			, "Product Cycle", "ICP Type", "Cross Check Group", "Allowed Sub-Price Type Id", "Max Quotation Allowed In An Outlet", "Min Quotation Allowed In An Outlet"
			, "Is N Price Mandatory", "Is S Price Mandatory", "Is FR Required", "Is Backdate Required", "Is Splicing Required"
			, "Is RUA Allowed", "Is Fresh Item", "Is Product Change Allowed", "Are Previous Month Prices Editable During Conversion", "Is Data Conversion Held Until Previous Month's Closing Date"
			, "Variety Mean of S Price PR", "Variety SD of S Price PR", "Variety Mean of N Price PR", "Variety SD of N Price PR", "Status"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 13;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		String cpiBasePeriod = task.getCpiBasePeriod();
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllUnitResult(cpiBasePeriod);
		
		int rowCnt = 1;
		while (results.next()){
			Unit code = (Unit)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			//For Unit
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(code.getId()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCode());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getChineseName());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getEnglishName());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getDisplayName());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiBasePeriod());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitCategory());
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(code.getFormDisplay()));
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			if(code.getCpiQoutationType()!=null){
				cell.setCellValue(String.valueOf(code.getCpiQoutationType()));
			}
			
			cellCnt = 14;
			cell = row.createCell(cellCnt);
			if(code.getSeasonality()!=null){
				cell.setCellValue(String.valueOf(code.getSeasonality()));
			}
			
			cellCnt = 15;
			cell = row.createCell(cellCnt);
			if(code.getSeasonStartMonth()!=null){
				cell.setCellValue(String.valueOf(code.getSeasonStartMonth()));
			}
			
			cellCnt = 16;
			cell = row.createCell(cellCnt);
			if(code.getSeasonEndMonth()!=null){
				cell.setCellValue(String.valueOf(code.getSeasonEndMonth()));
			}

			cellCnt = 17;
			cell = row.createCell(cellCnt);
			if(code.getRtnPeriod()!=null){
				cell.setCellValue(String.valueOf(code.getRtnPeriod()));
			}
			
			cellCnt = 20;
			cell = row.createCell(cellCnt++);
			if(code.getUomValue()!=null){
				cell.setCellValue(String.valueOf(code.getUomValue()));
			}
			
			cellCnt = 23;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getDataTransmissionRule());
			
			cellCnt = 24;
			cell = row.createCell(cellCnt);
			if(code.getProductCycle()!=null){
				cell.setCellValue(String.valueOf(code.getProductCycle()));
			}
			
			cellCnt = 25;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpType());
			
			cellCnt = 26;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCrossCheckGroup());
			
			cellCnt = 28;
			cell = row.createCell(cellCnt);
			if(code.getMaxQuotation()!=null){
				cell.setCellValue(String.valueOf(code.getMaxQuotation()));
			}
			
			cellCnt = 29;
			cell = row.createCell(cellCnt);
			if(code.getMinQuotation()!=null){
				cell.setCellValue(String.valueOf(code.getMinQuotation()));
			}
			
			cellCnt = 30;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNPriceMandatory());
			
			cellCnt = 31;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isSPriceMandatory());
			
			cellCnt = 32;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isFrRequired());
			
			cellCnt = 33;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isBackdateRequired());
			
			cellCnt = 34;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isSpicingRequired());

			cellCnt = 35;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isRuaAllowed());
			
			cellCnt = 36;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isFreshItem());
			
			cellCnt = 37;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isAllowProductChange());
			
			cellCnt = 38;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isAllowEditPMPrice());
			
			cellCnt = 39;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isConvertAfterClosingDate());
			
			cellCnt = 40;
			cell = row.createCell(cellCnt);
			if(code.getConsolidatedSPRMean()!=null){
				cell.setCellValue(String.valueOf(code.getConsolidatedSPRMean()));
			}
			
			cellCnt = 41;
			cell = row.createCell(cellCnt);
			if(code.getConsolidatedSPRSD()!=null){
				cell.setCellValue(String.valueOf(code.getConsolidatedSPRSD()));
			}
			
			cellCnt = 42;
			cell = row.createCell(cellCnt);
			if(code.getConsolidatedNPRMean()!=null){
				cell.setCellValue(String.valueOf(code.getConsolidatedNPRMean()));
			}
			
			cellCnt = 43;
			cell = row.createCell(cellCnt);
			if(code.getConsolidatedNPRSD()!=null){
				cell.setCellValue(String.valueOf(code.getConsolidatedNPRSD()));
			}
			
			cellCnt = 44;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getStatus());
			
			
			//For UOM Category
			List<UOMCategory> uomCategorys = uomCategoryDao.getUOMCategoryByUnitId(code.getUnitId());
			List<String> codeList = new ArrayList<String>();
			for(UOMCategory uomCategory : uomCategorys){
				codeList.add(String.valueOf(uomCategory.getUomCategoryId()));
				uomCategoryDao.evit(uomCategory);
			}
			String uomCategoryCode = StringUtils.join(codeList, ';');
			
			cellCnt = 21;
			cell = row.createCell(cellCnt);
			cell.setCellValue(uomCategoryCode);
			
			//For Purpose
			if(code.getPurpose()!=null){
				Purpose purpose = code.getPurpose();
				cell = row.createCell(5);
				cell.setCellValue(purpose.getCode());
				dao.evit(purpose);
			}
			
			//For Uom
			if(code.getStandardUOM()!=null){
				Uom uom = code.getStandardUOM();
				cellCnt = 18;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(uom.getId()));
				
				cellCnt = 19;
				cell = row.createCell(cellCnt);
				cell.setCellValue(uom.getEnglishName());
				dao.evit(uom);
			}
			
			//For SubItem
			if(code.getSubItem()!=null){
				SubItem subItem = code.getSubItem();
				cellCnt = 22;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(subItem.getCompilationMethod()));
			}
			
			//For PricingFrequency
			if(code.getPricingFrequency()!=null){
				PricingFrequency pricingFrequency = code.getPricingFrequency();
				cellCnt = 12;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(pricingFrequency.getPricingFrequencyId()));
				cellCnt = 13;
				cell = row.createCell(cellCnt);
				cell.setCellValue(pricingFrequency.getName());
				dao.evit(pricingFrequency);
			}
			
			//For ProductGroup
			if(code.getProductCategory()!=null){
				ProductGroup productGroup = code.getProductCategory();
				cellCnt = 8;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(productGroup.getProductGroupId()));
				
				cellCnt = 9;
				cell = row.createCell(cellCnt);
				cell.setCellValue(productGroup.getCode());
				
				dao.evit(productGroup);
			}
			
			//For SubPriceType
			if(code.getSubPriceType()!=null){
				SubPriceType subPriceType = code.getSubPriceType();
				cellCnt = 27;
				cell = row.createCell(cellCnt);
				cell.setCellValue(String.valueOf(subPriceType.getId()));
				dao.evit(subPriceType);
			}
			
			dao.evit(code);
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		results.close();
	
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
