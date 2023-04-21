package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.PricingMonthDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.UOMCategoryDao;
import capi.dal.UnitDao;
import capi.dal.UomDao;
import capi.entity.ImportExportTask;
import capi.entity.PricingFrequency;
import capi.entity.SubPriceType;
import capi.entity.UOMCategory;
import capi.entity.Unit;
import capi.entity.Uom;

@Service("ImportUnitMaintenanceService")
public class ImportUnitMaintenanceService extends DataImportServiceBase{
	
	@Autowired
	private UnitDao dao;
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	@Autowired
	private PricingMonthDao pricingMonthDao;
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 13;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);		
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 45);
			
			//For Unit
			Unit unit = fillEntity(values, rowCnt);
			dao.save(unit);
			
			
			if(rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;			
		}
		
		dao.flush();
		workbook.close();
	}
	
	private Unit fillEntity(String[] values, int rowCnt) throws Exception{
		Unit entity = null;
		
		int col = 0;
		try{
			//For Unit
			String idStr = values[0];
			if(StringUtils.isEmpty(idStr)){
				throw new RuntimeException("Unit Id should not be empty");
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: Unit Id ="+id);
				}
			}
//			
//			String oldCode = entity.getCode();
//			String oldCpiBasePeriod = entity.getCpiBasePeriod();
//			
//			col = 1; 
//			if(StringUtils.isEmpty(values[1])){
//				throw new RuntimeException("Unit Code should not be empty");
//			}
//			String code = values[1].trim();
//			entity.setCode(code);
//			
//			col = 2; 
//			String chineseName = values[2];
//			entity.setChineseName(chineseName);
//			
//			col = 3; 
//			String englishName = values[3];
//			entity.setEnglishName(englishName);
//			
//			col = 4; 
//			String displayName = values[4];
//			entity.setDisplayName(displayName);
//			
//			col = 6;
//			if(StringUtils.isEmpty(values[6])){
//				throw new RuntimeException("CPI Base Period should not be empty");
//			}
//			String cpiBasePeriod = values[6];
//			entity.setCpiBasePeriod(cpiBasePeriod);
//			
//			//Check isUnitCodeExist
//			if(!oldCode.equals(code) || !oldCpiBasePeriod.equals(cpiBasePeriod)){
//				if(dao.isUnitCodeExist(code, cpiBasePeriod)){
//					throw new RuntimeException("Please enter a valid code for the unit");
//				}
//			}

			col = 7;
			String unitCategory = values[7];
			entity.setUnitCategory(unitCategory);
			
			col = 10;
			if(StringUtils.isEmpty(values[10])){
				throw new RuntimeException("Ordinary / Tour Form should not be empty");
			}
			int formDisplay = (int)Double.parseDouble(values[10]);
			if(formDisplay<1 || formDisplay >2){
				throw new RuntimeException("Ordinary / Tour Form should be 1 or 2");
			}
			entity.setFormDisplay(formDisplay);
			
			col = 11; 
			if(!StringUtils.isEmpty(values[11])){
				int cpiQoutationType = (int)Double.parseDouble(values[11]);
				if(cpiQoutationType<1||cpiQoutationType>4){
					throw new RuntimeException("CPI Quotation Type should not be less than 1 or more than 4");
				}
				entity.setCpiQoutationType(cpiQoutationType);
			} else {
				entity.setCpiQoutationType(null);
			}
			
			col = 14; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Seasonality should not be empty");
			}
			int seasonality = (int)Double.parseDouble(values[col]);
			if(seasonality < 1 || seasonality > 4){
				throw new RuntimeException("Seasonality should not less than 1 or more than 4");
			}
			entity.setSeasonality(seasonality);
		
			if(seasonality==4){
				col = 15;
				if(StringUtils.isEmpty(values[col])){
					throw new RuntimeException("Season Start Month should not be empty");
				}
				int seasonStartMonth = (int)Double.parseDouble(values[col]);
				entity.setSeasonStartMonth(seasonStartMonth);
				
				col = 16;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Season End Month should not be empty");
				
				int seasonEndMonth = (int)Double.parseDouble(values[col]);
				entity.setSeasonEndMonth(seasonEndMonth);
			} else {
				col = 15;
				entity.setSeasonStartMonth(null);
				col = 16;
				entity.setSeasonEndMonth(null);
				
			}
			
			col = 17; 
			if(!StringUtils.isEmpty(values[col])){
				int rtnPeriod = (int)Double.parseDouble(values[col]);
				entity.setRtnPeriod(rtnPeriod);
			} else {
				entity.setRtnPeriod(null);
			}
			
			col = 20; 
			if(!StringUtils.isEmpty(values[col])){
				Double uomValue = Double.parseDouble(values[col]);
				entity.setUomValue(uomValue);
			} else {
				entity.setUomValue(null);
			}
			
			col = 23; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Data Transmission Rule should not be empty");
			}
			String dataTransmissionRule = values[col];
			if(!dataTransmissionRule.matches("A||B||C||D||E")){
				throw new RuntimeException("Data Transmission Rule should be A, B, C, D or E");
			}
			entity.setDataTransmissionRule(dataTransmissionRule);
			
			col = 24; 
			if(!StringUtils.isEmpty(values[col])){
				int productCycle = (int)Double.parseDouble(values[col]);
				entity.setProductCycle(productCycle);
			} else {
				entity.setProductCycle(null);
			}
			
			col = 25;
			String icpType = values[col];
			entity.setIcpType(icpType);
			
			col = 26; 
			String crossCheckGroup = values[col];
			entity.setCrossCheckGroup(crossCheckGroup);
			
			col = 28; 
			if(!StringUtils.isEmpty(values[col])){
				int maxQuotation = (int)Double.parseDouble(values[col]);
				entity.setMaxQuotation(maxQuotation);
			} else {
				entity.setMaxQuotation(null);
			}
			
			col = 29; 
			if(!StringUtils.isEmpty(values[col])){
				int minQuotation = (int)Double.parseDouble(values[col]);
				entity.setMinQuotation(minQuotation);
			} else {
				entity.setMinQuotation(null);
			}
			
			col = 30; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("N Price Mandatory should not be empty");
			}
			boolean isNPriceMandatory = Boolean.parseBoolean(values[col]);
			entity.setNPriceMandatory(isNPriceMandatory);
			
			col = 31; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("S Price Mandatory should not be empty");
			}
			boolean isSPriceMandatory = Boolean.parseBoolean(values[col]);
			entity.setSPriceMandatory(isSPriceMandatory);
			
			col = 32; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("FR Required should not be empty");
			}
			boolean frRequired = Boolean.parseBoolean(values[col]);
			entity.setFrRequired(frRequired);
			
			col = 33; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Backdate Required should not be empty");
			}
			boolean backdateRequired = Boolean.parseBoolean(values[col]);
			entity.setBackdateRequired(backdateRequired);
			
			col = 34; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Splicing Required should not be empty");
			}
			boolean spicingRequired = Boolean.parseBoolean(values[col]);
			entity.setSpicingRequired(spicingRequired);
			
			col = 35; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("RUA Allowed should not be empty");
			}
			boolean ruaAllowed = Boolean.parseBoolean(values[col]);
			entity.setRuaAllowed(ruaAllowed);
			
			col = 36; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Fresh Item should not be empty");
			}
			boolean isFreshItem = Boolean.parseBoolean(values[col]);
			entity.setFreshItem(isFreshItem);
			
			col = 37; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Allow Product Change should not be empty");
			}
			boolean allowProductChange = Boolean.parseBoolean(values[col]);
			entity.setAllowProductChange(allowProductChange);
			
			col = 38; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Allow Edit PM Price should not be empty");
			}
			boolean allowEditPMPrice = Boolean.parseBoolean(values[col]);
			entity.setAllowEditPMPrice(allowEditPMPrice);
			
			col = 39;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Data Conversion Held Until Previous Month's Closing Date should not be empty");
			}
			boolean convertAfterClosingDate = Boolean.parseBoolean(values[col]);
			entity.setConvertAfterClosingDate(convertAfterClosingDate);
			
//			col = 35; 
//			if(!StringUtils.isEmpty(values[35])){
//				Double consolidatedSPRMean = Double.parseDouble(values[35]);
//				entity.setConsolidatedSPRMean(consolidatedSPRMean);
//			} else {
//				entity.setConsolidatedSPRMean(null);
//			}
//			
//			col = 36; 
//			if(!StringUtils.isEmpty(values[36])){
//				Double consolidatedSPRSD = Double.parseDouble(values[36]);
//				entity.setConsolidatedSPRSD(consolidatedSPRSD);
//			} else {
//				entity.setConsolidatedSPRSD(null);
//			}
//			
//			col = 37; 
//			if(!StringUtils.isEmpty(values[37])){
//				Double consolidatedNPRMean = Double.parseDouble(values[37]);
//				entity.setConsolidatedNPRMean(consolidatedNPRMean);
//			} else {
//				entity.setConsolidatedNPRMean(null);
//			}
//			
//			col = 38; 
//			if(!StringUtils.isEmpty(values[38])){
//				Double consolidatedNPRSD = Double.parseDouble(values[38]);
//				entity.setConsolidatedNPRSD(consolidatedNPRSD);
//			} else {
//				entity.setConsolidatedNPRSD(null);
//			}
			
			col = 44; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Status should not be empty"); 
			}
			String status = values[col].trim();
			if(!(status.equals("Active") || status.equals("Inactive"))){
				throw new RuntimeException("Status only Active or Inactive");
			}
			entity.setStatus(status);
			
//			//For Purpose
//			col = 5; 
//			if(StringUtils.isEmpty(values[5])){
//				throw new RuntimeException("Purpose should not be empty");
//			}
//			String purposeCode = values[5].trim();
//			Purpose purpose = purposeDao.getSurveyTypeByCode(purposeCode);
//			if(purpose == null){
//				throw new RuntimeException("Purpose Code not found Code="+purposeCode);
//			}
//			entity.setPurpose(purpose);
		
			// For UOM
			col = 18; 
			if(!StringUtils.isEmpty(values[col])){
				int uomId = (int)Double.parseDouble(values[col]);
				Uom uom = uomDao.findById(uomId);
				if(uom == null){
					throw new RuntimeException("Standard Uom not found StandardUOMId="+uomId);
				}
				entity.setStandardUOM(uom);
			} else {
				entity.setStandardUOM(null);
			}
			// 16
			
			// For Pricing Frequency
			col = 12; 
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Pricing Frequency should not be empty");
			}
			int pricingFrequencyId = (int)Double.parseDouble(values[col]);
			PricingFrequency pricingFrequency = pricingMonthDao.findById(pricingFrequencyId);
			if(pricingFrequency == null){
				throw new RuntimeException("Pricing Frequency not found Pricing Frequency Id="+pricingFrequencyId);
			}
			entity.setPricingFrequency(pricingFrequency);
			
			// For Product Group
//			col = 8; 
//			if(!StringUtils.isEmpty(values[8])){
//				int productGroupId = (int)Double.parseDouble(values[8]);
//				ProductGroup productGroup = productGroupDao.findById(productGroupId);
//				if(productGroup == null){
//					throw new RuntimeException("Product Group not found Product Group Id = "+productGroupId);
//				}
//				entity.setProductCategory(productGroup);
//			} else {
//				entity.setProductCategory(null);
//			}
			// 9
			
			// For SubPriceType
			col = 27; 
			if(!StringUtils.isEmpty(values[col])){
				int subPriceTypeId = (int)Double.parseDouble(values[col]);
				SubPriceType subPriceType = subPriceTypeDao.findById(subPriceTypeId);
				if(subPriceType == null){
					throw new RuntimeException("Sub Price Type not found Sub Price Type Id="+subPriceTypeId);
				}
				entity.setSubPriceType(subPriceType);
			} else {
				entity.setSubPriceType(null);
			}
			
			col = 21;
			updateUomCategory(entity, values[col]);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage()+" (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
	private void updateUomCategory(Unit unit, String codes){
		List<String> newUomCategoryIds = new ArrayList<String>();
		if (!StringUtils.isEmpty(codes)){
			newUomCategoryIds = Arrays.asList(codes.split("\\s*;\\s*"));
		}
		
		ArrayList<String> oldUomCategoryIds = new ArrayList<String>();
		for(UOMCategory uomCategory : unit.getUomCategories()){
			oldUomCategoryIds.add(String.valueOf(uomCategory.getUomCategoryId()));
		}
		
		Collection<String> deleteIds = (Collection<String>)CollectionUtils.subtract(oldUomCategoryIds, newUomCategoryIds);
		Collection<String> newIds = (Collection<String>)CollectionUtils.subtract(newUomCategoryIds, oldUomCategoryIds);
		
		if(deleteIds.size()>0){
			List<Integer> delIds = new ArrayList<Integer>();
			for(String s : deleteIds)
				delIds.add(Integer.valueOf(s));
			List<UOMCategory> deletedUomCategorys = uomCategoryDao.getByIds(delIds);
			for(UOMCategory uomCategory : deletedUomCategorys)
				unit.getUomCategories().remove(uomCategory);
		}
		
		if(newIds.size()>0){
			List<Integer> ids = new ArrayList<Integer>();
			for(String s : newIds)
				ids.add(Integer.valueOf(s));
			List<UOMCategory> uomCategory = uomCategoryDao.getByIds(ids);
			unit.getUomCategories().addAll(uomCategory);
		}
	}
}
