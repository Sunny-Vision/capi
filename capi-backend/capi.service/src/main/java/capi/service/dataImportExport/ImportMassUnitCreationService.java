package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.GroupDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.ItemDao;
import capi.dal.OutletTypeDao;
import capi.dal.PricingMonthDao;
import capi.dal.ProductGroupDao;
import capi.dal.PurposeDao;
import capi.dal.SectionDao;
import capi.dal.SubGroupDao;
import capi.dal.SubItemDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.UOMCategoryDao;
import capi.dal.UnitDao;
import capi.dal.UomDao;
import capi.entity.Group;
import capi.entity.ImportExportTask;
import capi.entity.Item;
import capi.entity.OutletType;
import capi.entity.PricingFrequency;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.Section;
import capi.entity.SubGroup;
import capi.entity.SubItem;
import capi.entity.SubPriceType;
import capi.entity.UOMCategory;
import capi.entity.Unit;
import capi.entity.Uom;
import capi.service.CommonService;

@Service("ImportMassUnitCreationService")
public class ImportMassUnitCreationService extends DataImportServiceBase{

	@Autowired
	private UnitDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private SectionDao sectionDao;
	
	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private SubGroupDao subGroupDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private OutletTypeDao outletTypeDao;
	
	@Autowired
	private SubItemDao subItemDao;
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;
	
	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private PricingMonthDao pricingMonthDao;
	
	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 31;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		//TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next();
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 62);
			
			//For Section
			Section section = fillEntityForSection(values, rowCnt);
			sectionDao.save(section);
			
			//For Group
			Group group = fillEntityForGroup(values, rowCnt, section);
			groupDao.save(group);
			
			//For SubGroup
			SubGroup subGroup = fillEntityForSubGroup(values, rowCnt, group);
			subGroupDao.save(subGroup);
			
			//For Item
			Item item = fillEntityForItem(values, rowCnt, subGroup);
			itemDao.save(item);
			
			//For OutletType
			OutletType outletType = fillEntityForOutletType(values, rowCnt, item);
			outletTypeDao.save(outletType);
			
			//For SubItem
			SubItem subItem = fillEntityForSubItem(values, rowCnt, outletType);
			subItemDao.save(subItem);
			
			//For Unit
			Unit unit = fillEntity(values, rowCnt, subItem);
			dao.save(unit);
			
			if(rowCnt%20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
			
		}
		
		sectionDao.flush();
		
		workbook.close();
	}
	
	private Section fillEntityForSection(String[] values, int rowCnt) throws Exception{
		
		Section section = null;
		
		int col = 0;
		try{
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			col = 0;
			if(StringUtils.isEmpty(values[0])){
				throw new RuntimeException("Section Code should not be empty");
			}
			String code = values[0].trim();
			section = sectionDao.getByCodeWithoutDate(code, cpiBasePeriod);
			if(section==null){
				section = new Section();
				//throw new RuntimeException("Please enter a valid code for the Section");
			}
			section.setCode(code);
			
			col = 1;
			if(!StringUtils.isEmpty(values[1])){
				String chineseName = values[1];
				section.setChineseName(chineseName);
			} else {
				section.setChineseName(null);
			}
			
			col = 2;
			if(!StringUtils.isEmpty(values[2])){
				String englishName = values[2];
				section.setEnglishName(englishName);
			} else {
				section.setEnglishName(null);
			}
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return section;
	}
	
	private Group fillEntityForGroup(String[] values, int rowCnt, Section section){
		Group group = null;
		
		int col = 0;
		try{
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			col = 3;
			if(StringUtils.isEmpty(values[3])){
				throw new RuntimeException("Group Code should not be empty");
			}
			String code = values[3].trim();
			group = groupDao.getByCodeWithoutDate(code, cpiBasePeriod);
			if(group==null){
				group = new Group();
			}
			group.setCode(code);
			
			col = 4;
			if(!StringUtils.isEmpty(values[4])){
				String chineseName = values[4].trim();
				group.setChineseName(chineseName);
			} else {
				group.setChineseName(null);
			}
			
			col = 5;
			if(!StringUtils.isEmpty(values[5])){
				String englishName = values[5].trim();
				group.setEnglishName(englishName);
			} else {
				group.setEnglishName(null);
			}
			
			group.setSection(section);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return group;
	}
	
	private SubGroup fillEntityForSubGroup(String[] values, int rowCnt, Group group) throws Exception{
		SubGroup subGroup = new SubGroup();
		
		int col = 0;
		
		try{
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			col = 6;
			if(StringUtils.isEmpty(values[6])){
				throw new RuntimeException("SubGroup Code should not be empty");
			}
			String code = values[6].trim();
			subGroup = subGroupDao.getByCodeWithoutDate(code, cpiBasePeriod);
			if(subGroup==null){
				subGroup = new SubGroup();
			}
			subGroup.setCode(code);
			
			col = 7;
			if(!StringUtils.isEmpty(values[7])){
				String chineseName = values[7];
				subGroup.setChineseName(chineseName);
			} else {
				subGroup.setChineseName(null);
			}
			
			col = 8;
			if(!StringUtils.isEmpty(values[8])){
				String englishName = values[8];
				subGroup.setEnglishName(englishName);
			} else {
				subGroup.setEnglishName(null);
			}
			
			subGroup.setGroup(group);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return subGroup;
	}
	
	private Item fillEntityForItem(String[] values, int rowCnt, SubGroup subGroup){
		Item item = new Item();
		
		int col = 0;
		try{
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			col = 9;
			if(StringUtils.isEmpty(values[9])){
				throw new RuntimeException("Item Code should not be empty");
			}
			String code = values[9].trim();
			item = itemDao.getByCodeWithoutDate(code, cpiBasePeriod);
			if(item==null){
				item = new Item();
			}
			item.setCode(code);
			
			col = 10;
			if(!StringUtils.isEmpty(values[10])){
				String chineseName = values[10];
				item.setChineseName(chineseName);
			} else {
				item.setChineseName(null);
			}
			
			col = 11;
			if(!StringUtils.isEmpty(values[11])){
				String englishName = values[11];
				item.setEnglishName(englishName);
			} else {
				item.setEnglishName(null);
			}
			
			item.setSubGroup(subGroup);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return item;
	}
	
	private OutletType fillEntityForOutletType(String[] values, int rowCnt, Item item){
		OutletType outletType = new OutletType();
		
		int col = 0;
		try{
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			col = 12;
			if(StringUtils.isEmpty(values[12])){
				throw new RuntimeException("OutletType Code should not be empty");
			}
			String code = values[12].trim();
			outletType = outletTypeDao.getByCodeWithoutDate(code, cpiBasePeriod);
			if(outletType==null){
				outletType = new OutletType();
			}
			outletType.setCode(code);
			
			col = 13;
			if(!StringUtils.isEmpty(values[13])){
				String chineseName = values[13];
				outletType.setChineseName(chineseName);
			} else {
				outletType.setChineseName(null);
			}
			
			col = 14;
			if(!StringUtils.isEmpty(values[14])){
				String englishName = values[14];
				outletType.setEnglishName(englishName);
			} else {
				outletType.setEnglishName(null);
			}
			
			outletType.setItem(item);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return outletType;
	}
	
	private SubItem fillEntityForSubItem(String[] values, int rowCnt, OutletType outletType){
		SubItem subItem = new SubItem();
		
		int col = 0;
		try{
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			col = 15;
			if(StringUtils.isEmpty(values[15])){
				throw new RuntimeException("SubItem Code should not be empty");
			}
			String code = values[15].trim();
			subItem = subItemDao.getByCodeWithoutDate(code, cpiBasePeriod);
			if(subItem==null){
				subItem = new SubItem();
			}
			subItem.setCode(code);
			
			col = 16;
			if(!StringUtils.isEmpty(values[16])){
				String chineseName = values[16];
				subItem.setChineseName(chineseName);
			} else {
				subItem.setChineseName(null);
			}
			
			col = 17;
			if(!StringUtils.isEmpty(values[17])){
				String englishName = values[17];
				subItem.setEnglishName(englishName);
			} else {
				subItem.setEnglishName(null);
			}
			
			col = 18;
			if(StringUtils.isEmpty(values[18])){
				throw new RuntimeException("Compilation Method should not be empty");
			}
			int compilationMethod = (int)Double.parseDouble(values[18]);
			subItem.setCompilationMethod(compilationMethod);
			
			subItem.setOutletType(outletType);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return subItem;
	}
	
	private Unit fillEntity(String[] values, int rowCnt, SubItem subItem) throws Exception{
		Unit entity = new Unit();
		int col = 0;
		try{
			col = 19;
			if(StringUtils.isEmpty(values[19])){
				throw new RuntimeException("Unit Code should not be empty");
			}
			String code = values[19];
			if (!code.startsWith(subItem.getCode())) {
				throw new RuntimeException("The first character of Variety should be the same as the code in subitem");
			}
			
			col = 20;
			if(!StringUtils.isEmpty(values[20])){
				String chineseName = values[20];
				entity.setChineseName(chineseName);
			} else {
				entity.setChineseName(null);
			}
			
			col = 21;
			if(!StringUtils.isEmpty(values[21])){
				String englishName = values[21];
				entity.setEnglishName(englishName);
			} else {
				entity.setEnglishName(null);
			}
			
			col = 22;
			if(StringUtils.isEmpty(values[22])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[22].trim();
			
			if(dao.isUnitCodeExist(code, cpiBasePeriod)){
				throw new RuntimeException("Please enter a valid code for the unit");
			}
			entity.setCode(code);
			entity.setCpiBasePeriod(cpiBasePeriod);
			
			col = 23;
			if(!StringUtils.isEmpty(values[23])){
				String date = values[23].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Obsolete Date Format should be DD-MM-YYYY");
				}
				Date obsoleteDate = commonService.getDate(date);
				entity.setObsoleteDate(obsoleteDate);
			} else {
				entity.setObsoleteDate(null);
			}
			
			col = 24;
			if(!StringUtils.isEmpty(values[24])){
				String date = values[24].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Effective Date Format should be DD-MM-YYYY");
				}
				Date effectiveDate = commonService.getDate(date);
				entity.setEffectiveDate(effectiveDate);
			} else {
				entity.setEffectiveDate(null);
			}
			
			col = 25;
			String displayName = values[25];
			entity.setDisplayName(displayName);
			
			col = 26;
			if(StringUtils.isEmpty(values[26])){
				throw new RuntimeException("MPRS should not be empty");
			}
			boolean isMPRS = Boolean.parseBoolean(values[26]);
			entity.setMRPS(isMPRS);
			
			col = 27;
			if(!StringUtils.isEmpty(values[27])){
				int maxQuotation = (int)Double.parseDouble(values[27]);
				entity.setMaxQuotation(maxQuotation);
			} else {
				entity.setMaxQuotation(null);
			}
			
			col = 28;
			if(!StringUtils.isEmpty(values[28])){
				int minQuotation = (int)Double.parseDouble(values[28]);
				entity.setMinQuotation(minQuotation);
			} else {
				entity.setMinQuotation(null);
			}
			
			col = 29;
			String unitCategory = values[29];
			entity.setUnitCategory(unitCategory);
			
			col = 30;
			if(!StringUtils.isEmpty(values[30])){
				Double uomValue = Double.parseDouble(values[30]);
				entity.setUomValue(uomValue);
			} else {
				entity.setUomValue(null);
			}
			
			col = 31;
			if(StringUtils.isEmpty(values[31])){
				throw new RuntimeException("Spicing Required should not be empty");
			}
			boolean spicingRequired = Boolean.parseBoolean(values[31]);
			entity.setSpicingRequired(spicingRequired);
			
			col = 32;
			if(StringUtils.isEmpty(values[32])){
				throw new RuntimeException("FR Required should not be empty");
			}
			boolean frRequired = Boolean.parseBoolean(values[32]);
			entity.setFrRequired(frRequired);
			
			col = 33; 
			if(StringUtils.isEmpty(values[33])){
				throw new RuntimeException("Seasonality should not be empty");
			}
			int seasonality = (int)Double.parseDouble(values[33]);
			if(seasonality < 1 || seasonality > 4){
				throw new RuntimeException("Seasonality should not less than 1 or more than 4");
			}
			entity.setSeasonality(seasonality);
		
			col = 34; 
			if(seasonality == 4){
				if(StringUtils.isEmpty(values[34])){
					throw new RuntimeException("Season Start Month should not be empty when Seasonality = 4 ");
				}
				
				int seasonStartMonth = (int)Double.parseDouble(values[34]);
				if(seasonStartMonth < 1 || seasonStartMonth > 12){
					throw new RuntimeException("Season Start Month should not less than 1 or more than 12");
				}
				entity.setSeasonStartMonth(seasonStartMonth);
				
			} else {
				entity.setSeasonStartMonth(null);
			}
			
			col = 35; 
			if(seasonality == 4){
				if(StringUtils.isEmpty(values[35])){
					throw new RuntimeException("Season End Month should not be empty when Seasonality = 4 ");
				}
				
				int seasonEndMonth = (int)Double.parseDouble(values[35]);
				if(seasonEndMonth < 1 || seasonEndMonth > 12){
					throw new RuntimeException("Season End Month should not less than 1 or more than 12");
				}
				entity.setSeasonEndMonth(seasonEndMonth);
				
			} else {
				entity.setSeasonEndMonth(null);
			}
			
			col = 36;
			if(!StringUtils.isEmpty(values[36])){
				int rtnPeriod = (int) Double.parseDouble(values[36]);
				entity.setRtnPeriod(rtnPeriod);
			} else {
				entity.setRtnPeriod(null);
			}
			
			col = 37;
			if(StringUtils.isEmpty(values[37])){
				throw new RuntimeException("Backdate Required should not be empty");
			}
			boolean backdateRequired = Boolean.parseBoolean(values[37]);
			entity.setBackdateRequired(backdateRequired);
			
			col = 38;
			if(StringUtils.isEmpty(values[38])){
				throw new RuntimeException("Allow Edit PM Price should not be empty");
			}
			boolean allowEditPMPrice = Boolean.parseBoolean(values[38]);
			entity.setAllowEditPMPrice(allowEditPMPrice);
			
			col = 39;
			if(StringUtils.isEmpty(values[39])){
				throw new RuntimeException("RUA Allowed should not be empty");
			}
			boolean ruaAllowed = Boolean.parseBoolean(values[39]);
			entity.setRuaAllowed(ruaAllowed);
			
			col = 40;
			if(StringUtils.isEmpty(values[40])){
				throw new RuntimeException("Fresh Item should not be empty");
			}
			boolean isFreshItem = Boolean.parseBoolean(values[40]);
			entity.setFreshItem(isFreshItem);
			
			col = 41;
			if(StringUtils.isEmpty(values[41])){
				throw new RuntimeException("Allow Product Change should not be empty");
			}
			boolean allowProductChange = Boolean.parseBoolean(values[41]);
			entity.setAllowProductChange(allowProductChange);
			
			col = 42;
			if(StringUtils.isEmpty(values[42])){
				throw new RuntimeException("Form Display should not be empty");
			} //1 - normal			2 - tour
			int formDisplay = (int) Double.parseDouble(values[42]);
			if(formDisplay<1 || formDisplay>2){
				throw new RuntimeException("Form Display should not be less than 1 or more than 2");
			}
			entity.setFormDisplay(formDisplay);
			
			col = 43;
			if(!StringUtils.isEmpty(values[43])){
				int productCycle = (int) Double.parseDouble(values[43]);
				entity.setProductCycle(productCycle);
			} else {
				entity.setProductCycle(null);
			}
			
			col = 44;
			if(StringUtils.isEmpty(values[44])){
				throw new RuntimeException("Status should not be Empty");
			} //Active, Inactive
			String status = values[44].trim();
			if(!status.matches("Active||Inactive")){
				throw new RuntimeException("Status should be Active or Inactive");
			}
			entity.setStatus(status);
			
			col = 45;
			String crossCheckGroup = values[45];
			entity.setCrossCheckGroup(crossCheckGroup);
			
			col = 46;//1- Market, 			2- Supermarket, 			3- Batch, 			4- Others
			if(!StringUtils.isEmpty(values[46])){
				int cpiQuotationType = (int)Double.parseDouble(values[46]);
				if(cpiQuotationType<1 || cpiQuotationType>4){
					throw new RuntimeException("CPI Quotation Type should not be less than 1 or more than 4");
				}
				entity.setCpiQoutationType(cpiQuotationType);
			} else {
				entity.setCpiQoutationType(null);
			}
			
			col = 47;
			if(StringUtils.isEmpty(values[47])){
				throw new RuntimeException("Temporary should not be empty");
			}
			boolean isTemporary = Boolean.parseBoolean(values[47]);
			entity.setTemporary(isTemporary);
			
			col = 48;
			if(StringUtils.isEmpty(values[48])){
				throw new RuntimeException("N Price Mandatory should not be empty");
			}
			boolean isNPriceMandatory = Boolean.parseBoolean(values[48]);
			entity.setNPriceMandatory(isNPriceMandatory);
			
			col = 49;
			if(StringUtils.isEmpty(values[49])){
				throw new RuntimeException("S Price Mandatory should not be empty");
			}
			boolean isSPriceMandatory = Boolean.parseBoolean(values[49]);
			entity.setSPriceMandatory(isSPriceMandatory);
			
			col = 50;
			if(StringUtils.isEmpty(values[50])){
				throw new RuntimeException("Data Transmission Rule should not be empty");
			}
			String dataTransmissionRule = values[50].trim();
			if(!dataTransmissionRule.matches("A||B||C||D||E")){
				throw new RuntimeException("Data Transmission Rule should be A, B, C, D or E");
			}
			entity.setDataTransmissionRule(dataTransmissionRule);
			
			col = 51;
			if(!StringUtils.isEmpty(values[51])){
				Double consolidatedSPRMean = Double.parseDouble(values[51]);
				entity.setConsolidatedSPRMean(consolidatedSPRMean);
			} else {
				entity.setConsolidatedSPRMean(null);
			}
			
			col = 52;
			if(!StringUtils.isEmpty(values[52])){
				Double consolidatedSPRSD = Double.parseDouble(values[52]);
				entity.setConsolidatedSPRSD(consolidatedSPRSD);
			} else {
				entity.setConsolidatedSPRSD(null);
			}
			
			col = 53;
			if(!StringUtils.isEmpty(values[53])){
				Double consolidatedNPRMean = Double.parseDouble(values[53]);
				entity.setConsolidatedNPRMean(consolidatedNPRMean);
			} else {
				entity.setConsolidatedNPRMean(null);
			}
			
			col = 54;
			if(!StringUtils.isEmpty(values[54])){
				Double consolidatedNPRSD = Double.parseDouble(values[54]);
				entity.setConsolidatedNPRSD(consolidatedNPRSD);
			} else {
				entity.setConsolidatedNPRSD(null);
			}
			
			col = 61;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Data Conversion Held Until Previous Month's Closing Date should not be empty");
			}
			boolean convertAfterClosingDate = Boolean.parseBoolean(values[col]);
			entity.setConvertAfterClosingDate(convertAfterClosingDate);
			
			//For Purpose
			col = 55;
			if(StringUtils.isEmpty(values[55])){
				throw new RuntimeException("Purpose code should not be empty");
			}
			String purposeCode = values[55];
			Purpose purpose = purposeDao.getSurveyTypeByCode(purposeCode);
			if(purpose == null){
				throw new RuntimeException("Purpose not found: Purpose code ="+purposeCode);
			}
			entity.setPurpose(purpose);
			
			//For Uom
			col = 56;
			if(!StringUtils.isEmpty(values[56])){
				int uomId = (int)Double.parseDouble(values[56]);
				Uom uom = uomDao.findById(uomId);
				if(uom == null){
					throw new RuntimeException("Standard Uom not found StandardUOMId="+uomId);
				}
				entity.setStandardUOM(uom);
			} else {
				entity.setStandardUOM(null);
			}
			
			//For Pricing Frequency
			col = 57; 
			if(StringUtils.isEmpty(values[57])){
				throw new RuntimeException("Pricing Frequency Id should not be empty");
			}
			int pricingFrequencyId = (int)Double.parseDouble(values[57]);
			PricingFrequency pricingFrequency = pricingMonthDao.findById(pricingFrequencyId);
			if(pricingFrequency == null){
				throw new RuntimeException("Pricing Frequency not found Pricing Frequency Id="+pricingFrequencyId);
			}
			entity.setPricingFrequency(pricingFrequency);
			
			//For Product Category
			col = 58;
			if(!StringUtils.isEmpty(values[58])){
				String productGroupCode = values[58].trim();
				ProductGroup productGroup = productGroupDao.getProductGroupsByCode(productGroupCode);
				if(productGroup == null){
					throw new RuntimeException("Product Category not found Product Category Code="+productGroupCode);
				}
				entity.setProductCategory(productGroup);
			} else {
				entity.setProductCategory(null);
			}
			
			//For Sub Price Type
			col = 59; 
			if(!StringUtils.isEmpty(values[59])){
				int subPriceTypeId = (int)Double.parseDouble(values[59]);
				SubPriceType subPriceType = subPriceTypeDao.findById(subPriceTypeId);
				if(subPriceType == null){
					throw new RuntimeException("Sub Price Type not found Sub Price Type Id="+subPriceTypeId);
				}
				entity.setSubPriceType(subPriceType);
			} else {
				entity.setSubPriceType(null);
			}
			
			entity.setSubItem(subItem);
			
			col = 60;
			updateUomCategory(entity, values[col]);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		
		return entity;
	}
	
	private void updateUomCategory(Unit unit, String codes){
		List<String> newUomCategoryIds = new ArrayList<String>();
		if (!StringUtils.isEmpty(codes)){
			newUomCategoryIds = Arrays.asList(codes.split("\\s*;\\s*"));
			
			List<Integer> ids = new ArrayList<Integer>();
			for(String id : newUomCategoryIds){
				ids.add(Integer.valueOf(id));
			}
			
			List<UOMCategory> uomCategory = uomCategoryDao.getByIds(ids);
			unit.getUomCategories().addAll(uomCategory);
		}
		
	}
	
}
