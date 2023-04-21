package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.GroupDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.ItemDao;
import capi.dal.OnSpotValidationDao;
import capi.dal.OutletTypeDao;
import capi.dal.OutletTypeMappingDao;
import capi.dal.PricingMonthDao;
import capi.dal.ProductAttributeDao;
import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.dal.ProductSpecificationDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.RebasingDao;
import capi.dal.RebasingUnitMappingDao;
import capi.dal.SectionDao;
import capi.dal.SubGroupDao;
import capi.dal.SubItemDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.UOMCategoryDao;
import capi.dal.UnitDao;
import capi.dal.UomDao;
import capi.entity.ImportExportTask;
import capi.entity.Product;
import capi.entity.ProductAttribute;
import capi.entity.ProductGroup;
import capi.entity.ProductSpecification;
import capi.entity.Purpose;
import capi.entity.Rebasing;
import capi.entity.UOMCategory;
import capi.entity.Unit;
import capi.model.dataImportExport.ImportRebasingGroupList;
import capi.model.dataImportExport.ImportRebasingItemList;
import capi.model.dataImportExport.ImportRebasingOutletTypeList;
import capi.model.dataImportExport.ImportRebasingOutletTypeMappingList;
import capi.model.dataImportExport.ImportRebasingProductGroupList;
import capi.model.dataImportExport.ImportRebasingProductSpecificationList;
import capi.model.dataImportExport.ImportRebasingQuotationList;
import capi.model.dataImportExport.ImportRebasingSectionList;
import capi.model.dataImportExport.ImportRebasingSubGroupList;
import capi.model.dataImportExport.ImportRebasingSubItemList;
import capi.model.dataImportExport.ImportRebasingUnitList;
import capi.model.dataImportExport.ImportRebasingUnitMappingList;
import capi.service.CommonService;

@Service("ImportRebasingService")
public class ImportRebasingService extends DataImportServiceBase{

	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private ProductAttributeDao productAttributeDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductSpecificationDao productSpecificationDao;
	
	@Autowired
	private UnitDao unitDao;
	
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
	private PurposeDao purposeDao;
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;
	
	@Autowired
	private PricingMonthDao pricingMonthDao;
	
	@Autowired
	private OutletTypeMappingDao outletTypeMappingDao;
	
	@Autowired
	private OnSpotValidationDao onSpotValidationDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	@Autowired
	private RebasingUnitMappingDao rebasingUnitMappingDao;
	
	@Autowired
	private RebasingDao rebasingDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	// SQL Server max insert row 
	private static final int maxInsert = 300;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 26;
	}
	
	/**
	 * 1. get excel sheet
	 * 2. get all old data which are used in Rebasing
	 * 3. fill Excel sheet 2 (For Product Group)
	 * 4. insert and update Product Group 
	 * 5. insert Product Attribute
	 * 6. insert Product
	 * 7. insert Product Specification
	 * 8. fill Excel sheet 1 (For Outlet Type Mapping List)
	 * 9. insert Outlet Type Mapping List
	 * 10.fill Excel sheet 0 (For Unit)
	 * 11.insert Section
	 * 12.insert Group
	 * 13.insert SubGroup
	 * 14.insert Item
	 * 15.insert OutletType
	 * 16.insert SubItem
	 * 17.insert Unit
	 * 18.insert OnSpotValidation
	 * 19.update Quotation
	 * 20.insert RebasingUnitMapping
	 * 21.insert Rebasing
	 * 
	 **/
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		/**
		 * 1. get excel sheet
		 **/
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		Date effectiveDate = task.getEffectiveDate();
		String cpiBasePeriod = task.getCpiBasePeriod();
		//String cpiBasePeriod = "15-16"; 
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		XSSFSheet sheet1 = workbook.getSheetAt(1);
		XSSFSheet sheet2 = workbook.getSheetAt(2);
		Iterator<Row> rows = sheet.iterator();
		Iterator<Row> rows1 = sheet1.iterator();
		Iterator<Row> rows2 = sheet2.iterator();
		
		/**
		 * 2. get all old data which are used in Rebasing
		 **/
		//Get all Old Product Group and Make Mapping
		List<ProductGroup> productGroups = productGroupDao.getProductGroupByObsoleteDate();
		Map<String, Integer> oldProductGroupList = new Hashtable<String, Integer>();	//<old Product Group Code, old Product Group Id>
		for(ProductGroup productGroup : productGroups){
			oldProductGroupList.put(productGroup.getCode(), productGroup.getProductGroupId());
		}
		Map<String, ImportRebasingProductGroupList> newProductGroupList = new Hashtable<String, ImportRebasingProductGroupList>();	//<New Product Group Code, New Product Group>
		
		//Get All Unit and Make Mapping
		List<Unit> units = unitDao.getUnitByObsoleteDate();
		
		Map<String, Map<String, Unit>> oldUnitList = new Hashtable<String, Map<String, Unit>>();	//<oldUnitCode, Map<oldCPIBasePeriod, OldUnitId>>
//		Map<Integer, String> mapOldUnitId = new Hashtable<Integer, String>();	//<oldUnitId, oldCPIBasePeriod>
		for(Unit unit : units){
			String unitCode = unit.getCode();
			String oldCpiBasePeriod = null;
			if(unit.getCpiBasePeriod()==null){
				oldCpiBasePeriod = "";
			} else {
				oldCpiBasePeriod = unit.getCpiBasePeriod();
			}
			if(oldUnitList.get(unitCode)==null){
				Map<String, Unit> mapCpi = new Hashtable<String, Unit>();
				mapCpi.put(oldCpiBasePeriod, unit);
				oldUnitList.put(unitCode, mapCpi);
			} else {
				Map<String, Unit> mapCpi = oldUnitList.get(unitCode);
				mapCpi.put(oldCpiBasePeriod, unit);
				oldUnitList.put(unitCode, mapCpi);
			}
//			mapOldUnitId.put(unitId, oldCpiBasePeriod);
		}
		
		Map<Integer, Integer> mapPricingFrequency = new Hashtable<Integer, Integer>();	//<Pricing Frequency Id, Pricing Frequency Id>
	
		Map<String, Integer> mapPurpose = new Hashtable<String, Integer>();	//<Purpose Code, Purpose Id>
		
		Map<Integer, Integer> mapSubPriceType = new Hashtable<Integer, Integer>();	//<Sub Price Type Id, Sub Price Type Id>
		
		Map<Integer, Integer> mapUom = new Hashtable<Integer, Integer>();	//<Uom Id, Uom Id>
		
		Map<Integer, Integer> mapUomCategory = new Hashtable<Integer, Integer>(); //<UomCategory Id, UomCategory Id>
		
		Map<String, ImportRebasingUnitList> newUnitList = new Hashtable<String, ImportRebasingUnitList>();	//<New Unit Code, New Unit>
		Map<Integer, List<String>> mapUnitId = new Hashtable<Integer, List<String>>();	//<Old Unit Id, List<New Unit Code>>
		Map<String, ImportRebasingSectionList> newSectionList = new Hashtable<String, ImportRebasingSectionList>();	//<New Section Code, New Section>
		Map<String, ImportRebasingGroupList> newGroupList = new Hashtable<String, ImportRebasingGroupList>();	//<New Group Code, New Group>
		Map<String, ImportRebasingSubGroupList> newSubGroupList = new Hashtable<String, ImportRebasingSubGroupList>();	//<New Sub Group Code, New Sub Group>
		Map<String, ImportRebasingItemList> newItemList = new Hashtable<String, ImportRebasingItemList>();	//<New Item Code, New Item>
		Map<String, ImportRebasingOutletTypeList> newOutletTypeList = new Hashtable<String, ImportRebasingOutletTypeList>();	//<New Outlet Type Code, New Outlet Type>
		Map<String, ImportRebasingSubItemList> newSubItemList = new Hashtable<String, ImportRebasingSubItemList>();	//<New Sub Item Code, New Sub Item>
		List<Integer> updateUnitIds = new ArrayList<Integer>();
		List<ImportRebasingOutletTypeMappingList> newOutletTypeMappingList = new ArrayList<ImportRebasingOutletTypeMappingList>();
		
		/**
		 * 3. fill Excel sheet 2 (For Product Group)
		 **/
		int rowCnt = 1;
		rows2.next(); // remove column header
		
		//For Product Group
		while(rows2.hasNext()){
			Row row = rows2.next();
			String[] values = getStringValues(row, 5);
			
			fillProductGroupEntity(values, rowCnt, oldProductGroupList, newProductGroupList);
			rowCnt++;
		}
		/**
		 * 4. insert and update Product Group
		 **/
		saveAndUpdateProductGroup(newProductGroupList, effectiveDate);
		
		/**
		 * 5. insert Product Attribute
		 **/
		insertProductAttribute(newProductGroupList);
		
		/**
		 * 6. insert Product
		 **/
		insertProduct(newProductGroupList, effectiveDate);
		//Remove ProductGroup which is Delete
		if(newProductGroupList.containsKey("Delete")){
			newProductGroupList.remove("Delete");
		}
				
		/**
		 * 7. insert Product Specification
		 **/
		insertProductSpecification(newProductGroupList);
		
		productGroupDao.flush();
		
		/**
		 * 8. fill Excel sheet 1 (For Outlet Type Mapping List)
		 **/
		rowCnt = 1;
		rows1.next(); // remove column header
		while(rows1.hasNext()){
			Row row = rows1.next();
			String[] values = getStringValues(row, 2);
			
			ImportRebasingOutletTypeMappingList outletTypeMapping = fillOutletTypeMappingEntity(values, rowCnt);
			newOutletTypeMappingList.add(outletTypeMapping);
			rowCnt++;
		}
		/**
		 * 9. insert Outlet Type Mapping List
		 **/
		insertOutletTypeMapping(newOutletTypeMappingList, effectiveDate);
		
		/**
		 * 10.fill Excel sheet 0 (For Unit)
		 **/
		rowCnt = 1;
		rows.next(); //remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 68);
			
			fillUnitEntity(values, rowCnt, cpiBasePeriod, newUnitList, mapUnitId, oldUnitList
					, newProductGroupList, newSectionList, newGroupList, newSubGroupList
					, newItemList, newOutletTypeList, newSubItemList
					, mapPricingFrequency, mapPurpose, mapSubPriceType, mapUom, updateUnitIds, mapUomCategory);
			
			rowCnt++;
		}
		
		/**
		 * 11.insert Section
		 **/
		insertSection(newSectionList, effectiveDate);
		
		/**
		 * 12.insert Group
		 **/
		insertGroup(newGroupList, newSectionList, effectiveDate);
		
		/**
		 * 13.insert SubGroup
		 **/
		insertSubGroup(newSubGroupList, newGroupList, effectiveDate);
		
		/**
		 * 14.insert Item
		 **/
		insertItem(newItemList, newSubGroupList, effectiveDate);
		
		/**
		 * 15.insert OutletType
		 **/
		insertOutletType(newOutletTypeList, newItemList, effectiveDate);
		
		/**
		 * 16.insert SubItem
		 **/
		insertSubItem(newSubItemList, newOutletTypeList, effectiveDate);
		
		/**
		 * 17.insert Unit
		 **/
		insertUnit(newUnitList, newSubItemList, effectiveDate, cpiBasePeriod);
		
		updateOldUnitObsoleteDateRecursiveQuery(updateUnitIds, effectiveDate);
		
		/**
		 * 18.insert OnSpotValidation
		 **/
		insertOnSpotValidation(mapUnitId, newUnitList);
		
		/**
		 * 
		 **/
		insertUOMCategoryUnit(newUnitList);
		
		/**
		 * 19.update Quotation
		 **/
		updateQuotation(mapUnitId, newUnitList, newProductGroupList);
		
		/**
		 * 20.insert RebasingUnitMapping
		 **/
		insertRebasingUnitMapping(cpiBasePeriod, effectiveDate, newUnitList);
		unitDao.flush();
		
		/**
		 * 21.insert Rebasing
		 **/
		Rebasing rebasing = new Rebasing();
		rebasing.setEffectiveDate(effectiveDate);
		rebasing.setCpiBasePeriod(cpiBasePeriod);
		
		rebasingDao.save(rebasing);
		rebasingDao.flush();
		
		workbook.close();
	}
	
	/**
	 * 3. fill Excel sheet 2 (For Product Group)
	 * 3.1 map new Product Group <new Code, New Product Group>
	 * 
	 * Case Handle
	 * oldCode	newCode	isNewProductCategory	result
	 * A		A		False					Update
	 * A		B		False					Update
	 * A		B		True					Insert
	 * Null		B		True					Insert
	 * A		Null	newCode null Not Check	Delete (Update the obsoleteDate)
	 * A		A		True					Exception
	 * Null		B		False					Exception
	 * Null		Null	newCode null Not Check	Exception
	 * 
	 * @param values
	 * @param rowCnt
	 * @param oldProductGroupList
	 * @param newProductGroupList
	 * @throws Exception
	 */
	private void fillProductGroupEntity(String[] values, int rowCnt, Map<String, Integer> oldProductGroupList, Map<String, ImportRebasingProductGroupList> newProductGroupList) throws Exception{
		ImportRebasingProductGroupList newGroup = null;
		String oldCode = null, newCode = null;
		Boolean isNewProductCategory = null;
		
		int col = 0;
		try{
			if(!StringUtils.isEmpty(values[col])){
				oldCode = values[col].trim();
			}
			
			col = 1;
			if(!StringUtils.isEmpty(values[col])){
				newCode = values[col].trim();
			}
			
			col = 2;
			if(!StringUtils.isEmpty(values[col])){
				isNewProductCategory = Boolean.parseBoolean(values[col]);
			}
			
			//Null Point Exception Handle
			if(oldCode==null && newCode==null){
				throw new RuntimeException("Old Product Category and new Product Category both is empty");
			}
			
			if(oldCode==null && isNewProductCategory==null){
				throw new RuntimeException("Is New Product Category should not be empty");
			}
			
			if(newCode!=null && isNewProductCategory==null){
				throw new RuntimeException("Is New Product Category should not be empty");
			}
			
			//Other Exception
			if(oldCode==null && !isNewProductCategory){
				throw new RuntimeException("Is New Product Category = False, Old Product Category should not be empty");
			}
			
			if(oldCode!=null && !oldProductGroupList.containsKey(oldCode)){
				if(productGroupDao.getProductGroupsByCode(oldCode)==null){
					throw new RuntimeException("Old Product Category not found: Old Product Category Code = "+oldCode);
				}
				ProductGroup productGroup = productGroupDao.getProductGroupsByCode(oldCode);
				oldProductGroupList.put(productGroup.getCode(), productGroup.getProductGroupId());
			}
			
			if(newCode!=null && isNewProductCategory && oldProductGroupList.containsKey(newCode)){
				throw new RuntimeException("Is New Product Category = True, New Product Category Code is used: New Product Category Code = "+newCode);
			}
			
			//Delete
			if(newCode==null){
				if(newProductGroupList.get("Delete")!=null){
					newGroup = newProductGroupList.get(newCode);
				} else {
					newGroup = new ImportRebasingProductGroupList();
					newGroup.setNewCode("Delete");
				}
				
				List<Integer> oldIds = null;
				if(newGroup.getOldIds()==null){
					oldIds = new ArrayList<Integer>();
				} else {
					oldIds = newGroup.getOldIds();
				}
				oldIds.add(oldProductGroupList.get(oldCode));
				newGroup.setOldIds(oldIds);
				return;
			}
			
			if(newProductGroupList.get(newCode)!=null){
				newGroup = newProductGroupList.get(newCode);
			} else {
				newGroup = new ImportRebasingProductGroupList();
			}
			newGroup.setNewCode(newCode);
			
			newGroup.setIsNewProductCategory(isNewProductCategory);
			
			if(oldCode!=null){
				List<Integer> oldIds = null;
				if(newGroup.getOldIds()==null){
					oldIds = new ArrayList<Integer>();
				} else {
					oldIds = newGroup.getOldIds();
				}
				oldIds.add(oldProductGroupList.get(oldCode));
				newGroup.setOldIds(oldIds);
			}
			
			col = 3;
			String chineseName = values[3];
			newGroup.setChineseName(chineseName);
			
			col = 4;
			String englishName = values[4];
			newGroup.setEnglishName(englishName);
			
			newProductGroupList.put(newCode, newGroup);
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " Product Category Mapping (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return;
	}
	
	/**
	 * 4. insert and update Product Group
	 * 4.1  loop(make insert sql or update product Group)
	 * 4.2  insert new product Group
	 * 4.3  get new Product Group Id (fill the new Product Group Id)
	 * 4.4  update Old Product Group ObsoleteDate
	 * 
	 * @param newProductGroupList
	 * @param effectiveDate
	 **/
	private void saveAndUpdateProductGroup(Map<String, ImportRebasingProductGroupList> newProductGroupList, Date effectiveDate){
		ArrayList<ImportRebasingProductGroupList> query = new ArrayList<ImportRebasingProductGroupList>(newProductGroupList.values());
		saveProductGroupRecursiveQuery(query, effectiveDate);
		
		//fill ProductGroup Id
		for(String newCode : newProductGroupList.keySet()){
			ImportRebasingProductGroupList newGroup = newProductGroupList.get(newCode);
			if(newGroup.getIsNewProductCategory()){
				newGroup.setNewId(productGroupDao.getProductGroupsByCode(newCode).getProductGroupId());
				newProductGroupList.put(newCode, newGroup);
			} else {
				newGroup.setNewId(newGroup.getOldIds().get(0));
				newProductGroupList.put(newCode, newGroup);
			}
			
		}
		
		return;
	}
	
	private void saveProductGroupRecursiveQuery(List<ImportRebasingProductGroupList> query, Date effectiveDate){
		if(query.size()>maxInsert){
			List<ImportRebasingProductGroupList> subQuery = query.subList(0, maxInsert);
			saveProductGroupRecursiveQuery(subQuery, effectiveDate);
			
			List<ImportRebasingProductGroupList> remainQuery = query.subList(maxInsert, query.size());
			saveProductGroupRecursiveQuery(remainQuery, effectiveDate);
		} else if(query.size()>0) {
			List<String> groupList = new ArrayList<String>();
			List<Integer> oldGroupIds = new ArrayList<Integer>();
			int i =0;
			for(ImportRebasingProductGroupList newGroup : query){
				if("Delete".equals(newGroup.getNewCode())){
					if(newGroup.getOldIds()!=null){
						oldGroupIds.addAll(newGroup.getOldIds());
					}
				} else if(newGroup.getIsNewProductCategory()){
					String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , 'Active', :effectiveDate , getDate(), getDate())";
					groupList.add(insertSql);
					if(newGroup.getOldIds()!=null){
						oldGroupIds.addAll(newGroup.getOldIds());
					}
					i++;
				} else {
					//call update
					Integer oldId = newGroup.getOldIds().get(0);
					productGroupDao.updateProductGroupByRebasing(oldId, newGroup.getNewCode(), newGroup.getChineseName(), newGroup.getEnglishName(), effectiveDate);
				}
			}
			
			if(groupList!=null && groupList.size()>0){
				String groupSql = StringUtils.join(groupList, ',');
				//call insert
				productGroupDao.insertProductGroupByRebasing(groupSql, effectiveDate, query);
			}
			
			//update old ProductGroup obsoleteDate
			if(oldGroupIds!=null && oldGroupIds.size()>0){
				updateProductGroupRecursiveQuery(oldGroupIds, effectiveDate);
			}
		}
		return;
	}

	private void updateProductGroupRecursiveQuery(List<Integer> productGroupIds, Date effectiveDate){
		if(productGroupIds.size()>maxInsert){
			List<Integer> ids = productGroupIds.subList(0, maxInsert);
			updateProductGroupRecursiveQuery(ids, effectiveDate);
			
			List<Integer> remainIds = productGroupIds.subList(maxInsert, productGroupIds.size());
			updateProductGroupRecursiveQuery(remainIds, effectiveDate);
		} else if(productGroupIds.size()>0){
			productGroupDao.updateProductGroupObsoleteDateByRebasing(productGroupIds, effectiveDate);
		}
		return;
	}
	
	/**
	 * 5. insert Product Attribute
	 * 5.1  get New Product Group
	 * 5.2  check is or not insert
	 * 5.3  get New Product Group. Old Product Group List
	 * 5.4  insert New Product Attribute
	 * 5.5  get new Product Attribute
	 * 5.6  Map Product Attribute <old Product Attribute Id, new Product Attribute Id>
	 * 
	 * @param newProductGroupList
	 */
	private void insertProductAttribute(Map<String, ImportRebasingProductGroupList> newProductGroupList){
		for(String newCode : newProductGroupList.keySet()){
			ImportRebasingProductGroupList newGroup = newProductGroupList.get(newCode);
			if(newGroup.getIsNewProductCategory()!=null && newGroup.getIsNewProductCategory()){
				if(newGroup.getOldIds()!=null){
					//get old attributes
					List<ProductAttribute> oldAttributes = productAttributeDao.getProductAttributeByRebasing(newGroup.getOldIds());
					//insert new attributes
					productAttributeDao.insertProductAttributeByRebasing(newGroup.getOldIds(), newGroup.getNewId());
					//get new attributes
					List<ProductAttribute> newAttributes  = productAttributeDao.getProductAttributeByProductGroupId(newGroup.getNewId());
					
					//map old new attributes
					Map<Integer, Integer> mapAttributes = new Hashtable<Integer, Integer>();
					for(int i = 0; i<newAttributes.size(); i++){
						mapAttributes.put(oldAttributes.get(i).getProductAttributeId(), newAttributes.get(i).getProductAttributeId());
					}
					newGroup.setMapAttributes(mapAttributes);
				}
			}
		}
		return;
	}
	
	/**
	 * 6. insert Product
	 * 6.1  get New Product Group
	 * 6.2  check is insert
	 * 6.3  get New Product Group. Old Product Group List
	 * 6.4  insert New Product
	 * 6.5  get New Product
	 * 6.6  Map Product <Old Product Id, New Product Id>
	 * 6.7  update old Product obsoleteDate
	 * 
	 * @param newProductGroupList
	 * @param effectiveDate
	 */
	private void insertProduct(Map<String, ImportRebasingProductGroupList> newProductGroupList, Date effectiveDate){
		List<Integer> ids = new ArrayList<Integer>(); //for update old Product obsoleteDate
		for(String newCode : newProductGroupList.keySet()){
			ImportRebasingProductGroupList newGroup = newProductGroupList.get(newCode);
			if(newGroup.getIsNewProductCategory()!=null && newGroup.getIsNewProductCategory()){
				if(newGroup.getOldIds()!=null){
					//check the Product of ProductGroup is or not created
					List<Integer> oldGroupIds = newGroup.getOldIds();
				
					//get old Product
					List<Product> oldProducts = productDao.getProductByProductGroupIds(oldGroupIds);
					//insert new Product
					insertProductRecursiveQuery(oldGroupIds, newGroup.getNewId(), effectiveDate);
					//get new Product
					List<Product> newProducts  = productDao.getProductByProductGroupId(newGroup.getNewId());
						
					Map<Integer, Integer> mapProducts = new Hashtable<Integer, Integer>();
					for(int i = 0; i<newProducts.size(); i++){
						mapProducts.put(oldProducts.get(i).getProductId(), newProducts.get(i).getProductId());
					}
					newGroup.setMapProducts(mapProducts);

					ids.addAll(oldGroupIds);
				}
			}
			if("Delete".equals(newGroup.getNewCode())){
				if(newGroup.getOldIds()!=null){
					ids.addAll(newGroup.getOldIds());
				}
			}
		}
		
		if(!ids.isEmpty()){
			updateProductRecursiveQuery(ids, effectiveDate);
		}
		return;
	}
	
	private void insertProductRecursiveQuery(List<Integer> oldGroupIds, Integer newGroupId, Date effectiveDate){
		if(oldGroupIds.size()>maxInsert){
			List<Integer> ids = oldGroupIds.subList(0, maxInsert);
			insertProductRecursiveQuery(ids, newGroupId, effectiveDate);
			
			List<Integer> remainIds = oldGroupIds.subList(maxInsert, oldGroupIds.size());
			insertProductRecursiveQuery(remainIds, newGroupId, effectiveDate);
		} else if(oldGroupIds.size()>0){
			productDao.insertProductByRebasing(oldGroupIds, newGroupId, effectiveDate);
		}
		
		return;
	}
	
	private void updateProductRecursiveQuery(List<Integer> oldGroupIds, Date effectiveDate){
		if(oldGroupIds.size()>maxInsert){
			List<Integer> ids = oldGroupIds.subList(0, maxInsert);
			updateProductRecursiveQuery(ids, effectiveDate);
			
			List<Integer> remainIds = oldGroupIds.subList(maxInsert, oldGroupIds.size());
			updateProductRecursiveQuery(remainIds, effectiveDate);
		} else if(oldGroupIds.size()>0){
			productDao.updateProductByRebasing(oldGroupIds, effectiveDate);
		}
		return;
	}
	
	/**
	 * 7. insert Product Specification
	 * 7.1  get product specification by newProductGroup.oldProductGroup
	 * 7.2  find the new Product ID and new Attribute Id by new ProductGroup
	 * 7.2.1  make insert sql new Product Specification which is map 
	 * 7.3  insert New Product Specification
	 * 
	 * @param newProductGroupList
	 **/
	private void insertProductSpecification(Map<String, ImportRebasingProductGroupList> newProductGroupList){
		
		List<ImportRebasingProductSpecificationList> specificationLists = new ArrayList<ImportRebasingProductSpecificationList>();
		
		for(String key : newProductGroupList.keySet()){
			ImportRebasingProductGroupList productGroup = newProductGroupList.get(key);
			if(productGroup.getIsNewProductCategory()!=null && productGroup.getIsNewProductCategory()){
				if(productGroup.getOldIds()!=null && productGroup.getOldIds().size()>0){
					Map<Integer, Integer> mapAttributes = productGroup.getMapAttributes();
					Map<Integer, Integer> mapProducts = productGroup.getMapProducts();
					List<ProductSpecification> specifications = productSpecificationDao.getProductSpecificationByProductGroupIds(productGroup.getOldIds());
					for(ProductSpecification specification : specifications){
						Integer oldProductId = specification.getProduct().getProductId();
						Integer oldAttributeId = specification.getProductAttribute().getProductAttributeId();
						
						if(mapAttributes.containsKey(oldAttributeId) && mapProducts.containsKey(oldProductId)){
							ImportRebasingProductSpecificationList newSpecification = new ImportRebasingProductSpecificationList();
							if(specification.getValue()==null){
								newSpecification.setValue(null);
							} else {
								newSpecification.setValue(specification.getValue());
							}
							newSpecification.setProductAttributeId(mapAttributes.get(oldAttributeId));
							newSpecification.setProductId(mapProducts.get(oldProductId));
							specificationLists.add(newSpecification);
						}
					}
				}
			}
		}
		
		if(specificationLists.size()>0){
			insertSpecificationRecursiveQuery(specificationLists);
		}
	}
	
	private void insertSpecificationRecursiveQuery(List<ImportRebasingProductSpecificationList> entities){
		if(entities.size()>maxInsert){
			List<ImportRebasingProductSpecificationList> subEntities = entities.subList(0, maxInsert);
			insertSpecificationRecursiveQuery(subEntities);
			
			List<ImportRebasingProductSpecificationList> remainEntities = entities.subList(maxInsert, entities.size());
			insertSpecificationRecursiveQuery(remainEntities);
		} else if(entities.size()>0){
			List<String> specList = new ArrayList<String>();
			for(int i =0; i<entities.size();i++){
				String sql = "( :productId"+i+" , :productAttributeId"+i+", :value"+i+" , getDate(), getDate())";
				specList.add(sql);
			}
			String specSql = StringUtils.join(specList, ',');
			productSpecificationDao.insertProductSpecificationByRebasing(specSql, entities);
		}
		return;
	}
	
	/**
	 * 8. fill Excel sheet 1 (For Outlet Type Mapping List)
	 * 
	 * @param values
	 * @param rowCnt
	 * @return
	 */
	public ImportRebasingOutletTypeMappingList fillOutletTypeMappingEntity(String[] values, int rowCnt){
		ImportRebasingOutletTypeMappingList outletTypeMapping = new ImportRebasingOutletTypeMappingList();
		int col = 0;
		try{
			if(StringUtils.isEmpty(values[0])){
				throw new RuntimeException("Old Code should not be empty");
			}
			String oldCode = values[0].trim();
			outletTypeMapping.setOldCode(oldCode);
			
			col = 1;
			if(StringUtils.isEmpty(values[1])){
				throw new RuntimeException("New Code should not be empty");
			}
			String newCode = values[1].trim();
			outletTypeMapping.setNewCode(newCode);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage()+" Outlet Type Mapping (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return outletTypeMapping;
	}
	
	/**
	 * 9. insert Outlet Type Mapping List
	 * 9.1  make insert sql
	 * 9.2  insert outletTypeMappingList
	 * 
	 * @param newOutletTypeMappingList
	 * @param effectiveDate
	 */
	public void insertOutletTypeMapping(List<ImportRebasingOutletTypeMappingList> newOutletTypeMappingList, Date effectiveDate){
		if(newOutletTypeMappingList.size()>maxInsert){
			List<ImportRebasingOutletTypeMappingList> subEntities = newOutletTypeMappingList.subList(0, maxInsert);
			insertOutletTypeMapping(subEntities, effectiveDate);
			
			List<ImportRebasingOutletTypeMappingList> remainEntities = newOutletTypeMappingList.subList(maxInsert, newOutletTypeMappingList.size());
			insertOutletTypeMapping(remainEntities, effectiveDate);
		} else if(newOutletTypeMappingList.size()>0){
			List<String> outletTypeMappingList = new ArrayList<String>();
			
			for(int i=0; i<newOutletTypeMappingList.size();i++){
				String insertSql = "( :oldCode"+i+" , :newCode"+i+", :effectiveDate , getDate(), getDate())";
				outletTypeMappingList.add(insertSql);
			}
			
			if(!outletTypeMappingList.isEmpty()){
				String outletTypeMappingSql = StringUtils.join(outletTypeMappingList, ',');
				outletTypeMappingDao.insertOutletTypeMapping(outletTypeMappingSql, effectiveDate, newOutletTypeMappingList);
			}
		}
		return;
	}
	
	/**
	 * 10.fill Excel sheet 0 (For Unit) 
	 * 10.1  map new Unit <new code, new Unit>
	 * 10.2  if have old code and old cpibaseperiod
	 * 10.3  map unit <old unit Id, list<new unit Code>>
	 * 
	 * @param values
	 * @param rowCnt
	 * @param cpiBasePeriod
	 * @param newUnitList
	 * @param mapUnitId
	 * @param oldUnitList
	 * @param newProductGroupList
	 * @param newSectionList
	 * @param newGroupList
	 * @param newSubGroupList
	 * @param newItemList
	 * @param newOutletTypeList
	 * @param newSubItemList
	 * @param mapPricingFrequency
	 * @param mapPurpose
	 * @param mapSubPriceType
	 * @param mapUom
	 * @throws Exception
	 */
	private void fillUnitEntity(String[] values, int rowCnt, String cpiBasePeriod
			, Map<String, ImportRebasingUnitList> newUnitList
			//, Map<String, List<Integer>> mapUnitCode
			, Map<Integer, List<String>> mapUnitId
			, Map<String, Map<String, Unit>> oldUnitList
			, Map<String, ImportRebasingProductGroupList> newProductGroupList
			, Map<String, ImportRebasingSectionList> newSectionList
			, Map<String, ImportRebasingGroupList> newGroupList
			, Map<String, ImportRebasingSubGroupList> newSubGroupList
			, Map<String, ImportRebasingItemList> newItemList
			, Map<String, ImportRebasingOutletTypeList> newOutletTypeList
			, Map<String, ImportRebasingSubItemList> newSubItemList
			, Map<Integer, Integer> mapPricingFrequency
			, Map<String, Integer> mapPurpose
			, Map<Integer, Integer> mapSubPriceType
			, Map<Integer, Integer> mapUom
			, List<Integer> updateUnitIds
			, Map<Integer, Integer> mapUomCategory) throws Exception{
		ImportRebasingUnitList newUnit = null;
		ImportRebasingSectionList newSection = null;
		ImportRebasingGroupList newGroup = null;
		ImportRebasingSubGroupList newSubGroup = null;
		ImportRebasingItemList newItem = null;
		ImportRebasingOutletTypeList newOutletType = null;
		ImportRebasingSubItemList newSubItem = null;
		
		int col = 0;
		try{
			Unit oldUnit = null;
			Integer oldUnitId = null;
			
			col = 27; //AB
			String newUnitCode = null;
			if(!StringUtils.isEmpty(values[col])){
				newUnitCode = values[col].trim();
			}
			
			col = 6; //G
			String oldUnitCode = null;
			if(!StringUtils.isEmpty(values[col])){
				oldUnitCode = values[col].trim();
			}
			
			col = 7;//H
			String oldCpiBasePeriod = null;
			if(!StringUtils.isEmpty(values[col])){
				oldCpiBasePeriod = values[col].trim();
			}
			
			col = 30; //AE
			Date obsoleteDate = null;
			if(!StringUtils.isEmpty(values[col])){
				String date = values[col].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Obsolete Date Format should be DD-MM-YYYY");
				}
				obsoleteDate = commonService.getDate(date);
			}
			
			/**
			 * Exception Checking
			 */
			if(oldUnitCode ==null && newUnitCode ==null){
				col = 27;
				throw new RuntimeException("Old Unit Code and New Unit Code both is empty");
			}
			
			if(oldUnitCode!=null && oldCpiBasePeriod==null){
				col = 7;
				throw new RuntimeException("Old Cpi Base Period is Empty");
			}
			
			if(newUnitCode!=null){
				if(unitDao.getUnitByCode(newUnitCode, cpiBasePeriod)!=null){
					col = 27;
					throw new RuntimeException("Please enter a valid code for the unit");
				}
			}
			
			/**
			 * get Old Unit
			 */
			if(oldUnitCode!=null && oldCpiBasePeriod!=null){
				if(!oldUnitList.containsKey(oldUnitCode)){
					if(unitDao.getUnitByCode(oldUnitCode, oldCpiBasePeriod)==null){
						throw new RuntimeException("Unit not found: old Unit code = "+ oldUnitCode + ", old CPI Base Period = "+oldCpiBasePeriod);
					}
					oldUnit = unitDao.getUnitByCode(oldUnitCode, oldCpiBasePeriod);
					Map<String, Unit> mapCpi = new Hashtable<String, Unit>();
					mapCpi.put(oldCpiBasePeriod, oldUnit);
					oldUnitList.put(oldUnitCode, mapCpi);
				} else {
					Map<String, Unit> mapCpi = oldUnitList.get(oldUnitCode);
					if(!mapCpi.containsKey(oldCpiBasePeriod)){
						if(unitDao.getUnitByCode(oldUnitCode, oldCpiBasePeriod)==null){
							throw new RuntimeException("Unit not found: old Unit code = "+ oldUnitCode + ", old CPI Base Period = "+oldCpiBasePeriod);
						}
						oldUnit = unitDao.getUnitByCode(oldUnitCode, oldCpiBasePeriod);
						mapCpi.put(oldCpiBasePeriod, oldUnit);
						oldUnitList.put(oldUnitCode, mapCpi);
					} else {
						oldUnit = mapCpi.get(oldCpiBasePeriod);
					}
				}
			}
			
			/**
			 * New Unit Code is empty
			 */
			if(newUnitCode==null){
				if(obsoleteDate==null){
					updateUnitIds.add(oldUnit.getUnitId());
					return;
				}
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(oldUnit.getUnitId());
				updateOldUnitObsoleteDateRecursiveQuery(ids, obsoleteDate);
				return;
			}
			
			if(!newUnitList.containsKey(newUnitCode)){
				newUnit = new ImportRebasingUnitList();
			} else {
				newUnit = newUnitList.get(newUnitCode);
			}
			newUnit.setCode(newUnitCode);
			
			if(oldUnit!=null){
				List<Unit> oldUnits = newUnit.getOldUnits();
				if(oldUnits==null){
					oldUnits = new ArrayList<Unit>();
				}
				oldUnits.add(oldUnit);
				newUnit.setOldUnits(oldUnits);
				
				oldUnitId = oldUnit.getUnitId();
				//For Quotation
				if(mapUnitId.get(oldUnitId)==null){
					List<String> unitCodes = new ArrayList<String>();
					unitCodes.add(newUnitCode);
					mapUnitId.put(oldUnitId, unitCodes);
				} else {
					List<String> unitCodes = mapUnitId.get(oldUnitId);
					unitCodes.add(newUnitCode);
					mapUnitId.put(oldUnitId, unitCodes);
				}
//				updateUnitIds.add(oldUnit.getUnitId());
				if(obsoleteDate==null){
					updateUnitIds.add(oldUnit.getUnitId());
				} else {
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(oldUnit.getUnitId());
					updateOldUnitObsoleteDateRecursiveQuery(ids, obsoleteDate);
				}
			}
			
			col = 8; //I
			if(StringUtils.isEmpty(values[8])){
				throw new RuntimeException("Section Code should not be empty");
			}
			String sectionCode = values[8].trim();
			if(sectionDao.getByCode(sectionCode, cpiBasePeriod)!=null){
				throw new RuntimeException("Please enter a valid code for the Section");
			}
			if(newSectionList.get(sectionCode)==null){
				newSection = new ImportRebasingSectionList();
				newSection.setCode(sectionCode);
			} else {
				newSection = newSectionList.get(sectionCode);
			}
			
			col = 9; //J
			String sectionChineseName = values[9];
			newSection.setChineseName(sectionChineseName);
			
			col = 10; //K
			String sectionEnglishName = values[10];
			newSection.setEnglishName(sectionEnglishName);
			newSectionList.put(sectionCode, newSection);
			
			col = 11; //L
			if(StringUtils.isEmpty(values[11])){
				throw new RuntimeException("Group Code should not be empty");
			}
			String groupCode = values[11].trim();
			if(groupDao.getByCode(groupCode, cpiBasePeriod)!=null){
				throw new RuntimeException("Please enter a valid code for the Group");
			}
			if(newGroupList.get(groupCode)==null){
				newGroup = new ImportRebasingGroupList();
				newGroup.setCode(groupCode);
				newGroup.setSectionCode(sectionCode);
			} else {
				newGroup = newGroupList.get(groupCode);
				newGroup.setCode(groupCode);
				newGroup.setSectionCode(sectionCode);
			}
			
			col = 12; //M
			String groupChineseName = values[12];
			newGroup.setChineseName(groupChineseName);
			
			col = 13; //N
			String groupEnglishName = values[13];
			newGroup.setEnglishName(groupEnglishName);
			newGroupList.put(groupCode, newGroup);
			
			
			col = 14; //O
			if(StringUtils.isEmpty(values[14])){
				throw new RuntimeException("Sub-Group Code should not be empty");
			}
			String subGroupCode = values[14].trim();
			if(subGroupDao.getByCode(subGroupCode, cpiBasePeriod)!=null){
				throw new RuntimeException("Please enter a valid code for the Sub-Group");
			}
			if(newSubGroupList.get(subGroupCode)==null){
				newSubGroup = new ImportRebasingSubGroupList();
				newSubGroup.setCode(subGroupCode);
				newSubGroup.setGroupCode(groupCode);
			} else {
				newSubGroup = newSubGroupList.get(subGroupCode);
				newSubGroup.setCode(subGroupCode);
				newSubGroup.setGroupCode(groupCode);
			}
			
			col = 15; //P
			String subGroupChineseName = values[15];
			newSubGroup.setChineseName(subGroupChineseName);
			
			col = 16; //Q
			String subGroupEnglishName = values[16];
			newSubGroup.setEnglishName(subGroupEnglishName);
			newSubGroupList.put(subGroupCode, newSubGroup);
			
			col = 17; //R
			if(StringUtils.isEmpty(values[17])){
				throw new RuntimeException("Item Code should not be empty");
			}
			String itemCode = values[17].trim();
			if(itemDao.getByCode(itemCode, cpiBasePeriod)!=null){
				throw new RuntimeException("Plesae enter a valid code for the Item");
			}
			if(newItemList.get(itemCode)==null){
				newItem = new ImportRebasingItemList();
				newItem.setCode(itemCode);
				newItem.setSubGroupCode(subGroupCode);
			} else {
				newItem = newItemList.get(itemCode);
				newItem.setCode(itemCode);
				newItem.setSubGroupCode(subGroupCode);
			}
			
			col = 18; //S
			String itemChineseName = values[18];
			newItem.setChineseName(itemChineseName);
			
			col = 19; //T
			String itemEnglishName = values[19];
			newItem.setEnglishName(itemEnglishName);
			newItemList.put(itemCode, newItem);
			
			
			col = 20; //U
			if(StringUtils.isEmpty(values[20])){
				throw new RuntimeException("Outlet Type Code should not be empty");
			}
			String outletTypeCode = values[20].trim();
			if(outletTypeDao.getByCode(outletTypeCode, cpiBasePeriod)!=null){
				throw new RuntimeException("Please enter a valid code for the Outlet Type");
			}
			if(newOutletTypeList.get(outletTypeCode)==null){
				newOutletType = new ImportRebasingOutletTypeList();
				newOutletType.setCode(outletTypeCode);
				newOutletType.setItemCode(itemCode);
			} else {
				newOutletType = newOutletTypeList.get(outletTypeCode);
				newOutletType.setCode(outletTypeCode);
				newOutletType.setItemCode(itemCode);
			}
			
			col = 21; //V
			String outletTypeChineseName = values[21];
			newOutletType.setChineseName(outletTypeChineseName);
			
			col = 22; //W
			String outletTypeEnglishName = values[22];
			newOutletType.setEnglishName(outletTypeEnglishName);
			newOutletTypeList.put(outletTypeCode, newOutletType);
			
			col = 23; //X
			if(StringUtils.isEmpty(values[23])){
				throw new RuntimeException("Sub-Item Code should not be empty");
			}
			String subItemCode = values[23].trim();
			if(subItemDao.getByCode(subItemCode, cpiBasePeriod)!=null){
				throw new RuntimeException("Please enter a valid code for the Sub-Item");
			}
			if(newSubItemList.get(subItemCode)==null){
				newSubItem = new ImportRebasingSubItemList();
				newSubItem.setCode(subItemCode);
				newSubItem.setOutletTypeCode(outletTypeCode);
			} else {
				newSubItem = newSubItemList.get(subItemCode);
				newSubItem.setCode(subItemCode);
				newSubItem.setOutletTypeCode(outletTypeCode);
			}
			newUnit.setSubItemCode(subItemCode);
			
			col = 24; //Y
			String subItemChineseName = values[24];
			newSubItem.setChineseName(subItemChineseName);
			
			col = 25; //Z
			String subItemEnglishName = values[25];
			newSubItem.setEnglishName(subItemEnglishName);
			
			col = 26; //AA
			if(StringUtils.isEmpty(values[26])){
				throw new RuntimeException("Compilation Method should not be empty");
			}
			int compilationMethod = (int)Double.parseDouble(values[26]);
			newSubItem.setCompilationMethod(compilationMethod);
			newSubItemList.put(subItemCode, newSubItem);
			
			
			col = 28; //AC
			String unitChineseName = values[28];
			newUnit.setUnitChineseName(unitChineseName);
			
			col = 29; //AD
			String unitEnglishName = values[29];
			newUnit.setUnitEnglishName(unitEnglishName);
			
//			col = 30; //AE
//			if(!StringUtils.isEmpty(values[30])){
//				String date = values[30].trim();
//				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
//					throw new RuntimeException("Obsolete Date Format should be DD-MM-YYYY");
//				}
//				Date obsoleteDate = commonService.getDate(date);
//				newUnit.setObsoleteDate(obsoleteDate);
//			} else {
//				newUnit.setObsoleteDate(null);
//			}
			
			col = 31; //AF
			String displayName = values[31];
			newUnit.setDisplayName(displayName);
			
			col = 32; //AG
			if(StringUtils.isEmpty(values[32])){
				throw new RuntimeException("MRPS should not be empty");
			}
			boolean isMRPS = Boolean.parseBoolean(values[32]);
			newUnit.setIsMRPS(isMRPS);
			
			col = 33; //AH
			if(StringUtils.isEmpty(values[33])){
				throw new RuntimeException("Purpose Code should not be empty");
			}
			String purposeCode = values[33].trim();
			if(!mapPurpose.containsKey(purposeCode)){
				if(purposeDao.getSurveyTypeByCode(purposeCode)==null){
					throw new RuntimeException("Purpose not found: Purpose Code ="+purposeCode);
				}
				Purpose purpose = purposeDao.getSurveyTypeByCode(purposeCode);
				mapPurpose.put(purpose.getCode(), purpose.getPurposeId());
			}
			newUnit.setPurposeCode(purposeCode);
			newUnit.setPurposeId(mapPurpose.get(purposeCode));
			
			col = 34; //AI
			if(!StringUtils.isEmpty(values[34])){
				Integer maxQuotation = (int)Double.parseDouble(values[34]);
				newUnit.setMaxQuotation(maxQuotation);
			} else {
				newUnit.setMaxQuotation(null);
			}
			
			col = 35; //AJ
			if(!StringUtils.isEmpty(values[35])){
				Integer minQuotation = (int) Double.parseDouble(values[35]);
				newUnit.setMinQuotation(minQuotation);
			} else {
				newUnit.setMinQuotation(null);
			}
			
			col = 36; //AK
			String unitCategory = values[36];
			newUnit.setUnitCategory(unitCategory);
			
			col = 37; //AL
			if(!StringUtils.isEmpty(values[37])){
				Integer standardUOMId = (int) Double.parseDouble(values[37]);
				if(!mapUom.containsKey(standardUOMId)){
					if(uomDao.findById(standardUOMId)==null){
						throw new RuntimeException("Uom not found: Standard UOM Id ="+standardUOMId);
					}
					mapUom.put(standardUOMId, standardUOMId);
				}
				newUnit.setStandardUOMId(standardUOMId);
			} else {
				newUnit.setStandardUOMId(null);
			}
			
			col = 38; //AM
			if(!StringUtils.isEmpty(values[38])){
				Double uomValue = Double.parseDouble(values[38]);
				newUnit.setUomValue(uomValue);
			} else {
				newUnit.setUomValue(null);
			}
			
			col = 39; //AN
			if(!StringUtils.isEmpty(values[39])){
				String productCategoryCode = values[39].trim();
				if(oldUnit!=null && oldUnit.getProductCategory()!=null){
					if(productCategoryCode.equals(oldUnit.getProductCategory().getCode())){
						newUnit.setProductCategoryCode(oldUnit.getProductCategory().getCode());
						newUnit.setProductCategoryId(oldUnit.getProductCategory().getProductGroupId());
					} else if(newProductGroupList.containsKey(productCategoryCode)){
						 ImportRebasingProductGroupList newProductGroup = newProductGroupList.get(productCategoryCode);
						 ProductGroup oldProductGroup = oldUnit.getProductCategory();
						 if(newProductGroup.getOldIds()==null){
							 throw new RuntimeException("new Unit Product Category different old Unit Product Category");
						 }
						 List<Integer> oldProductGroupIds = newProductGroup.getOldIds();
						 boolean differentProductGroup = true;
						 for(Integer oldProductGroupId : oldProductGroupIds){
							 if(oldProductGroupId.equals(oldProductGroup.getProductGroupId())){
								 differentProductGroup = false;
							 }
						 }
						 if(differentProductGroup){
							 throw new RuntimeException("new Unit Product Category different old Unit Product Category");
						 }
						 newUnit.setProductCategoryCode(newProductGroup.getNewCode());
						 newUnit.setProductCategoryId(newProductGroup.getNewId());
					} else if(productGroupDao.getProductGroupsByCode(productCategoryCode)!=null){
						ProductGroup productGroup = productGroupDao.getProductGroupsByCode(productCategoryCode);
						newUnit.setProductCategoryCode(productCategoryCode);
						newUnit.setProductCategoryId(productGroup.getProductGroupId());
					} else {
						throw new RuntimeException("Product Category Code not found: Product Category Code ="+productCategoryCode);
					}
				} else {
					if(newProductGroupList.get(productCategoryCode)!=null){
						newUnit.setProductCategoryCode(productCategoryCode);
						newUnit.setProductCategoryId(newProductGroupList.get(productCategoryCode).getNewId());
					} else if(productGroupDao.getProductGroupsByCode(productCategoryCode)!=null){
						ProductGroup productGroup = productGroupDao.getProductGroupsByCode(productCategoryCode);
						newUnit.setProductCategoryCode(productCategoryCode);
						newUnit.setProductCategoryId(productGroup.getProductGroupId());
					} else {
						throw new RuntimeException("Product Category Code not found: Product Category Code ="+productCategoryCode);
					}
				}
			} else {
				newUnit.setProductCategoryCode(null);
				newUnit.setProductCategoryId(null);
			}
			
			col = 40; //AO
			if(!StringUtils.isEmpty(values[40])){
				Integer subPriceTypeId = (int) Double.parseDouble(values[40]);
				if(mapSubPriceType.get(subPriceTypeId)==null){
					if(subPriceTypeDao.findById(subPriceTypeId)==null){
						throw new RuntimeException("Sub Price Type not found: Sub Price Type id ="+subPriceTypeId);
					}
					mapSubPriceType.put(subPriceTypeId, subPriceTypeId);
				}
				newUnit.setSubPriceTypeId(subPriceTypeId);
			} else {
				newUnit.setSubPriceTypeId(null);
			}
			
			col = 41; //AP
			if(StringUtils.isEmpty(values[41])){
				throw new RuntimeException("Spicing Required should not be empty");
			}
			boolean spicingRequired = Boolean.parseBoolean(values[41]);
			newUnit.setSpicingRequired(spicingRequired);
			
			col = 42; //AQ
			if(StringUtils.isEmpty(values[42])){
				throw new RuntimeException("FR Required should not be empty");
			}
			boolean frRequired = Boolean.parseBoolean(values[42]);
			newUnit.setFrRequired(frRequired);
			
			col = 43; //AR
			if(StringUtils.isEmpty(values[43])){
				throw new RuntimeException("Seasonality should not be empty");
			}
			int seasonality = (int)Double.parseDouble(values[43]);
			if(seasonality < 1 || seasonality > 4){
				throw new RuntimeException("Seasonality cannot less than 1 or more than 4");
			}
			newUnit.setSeasonality(seasonality);
			
			col = 44; //AS
			if(seasonality == 4){
				if(StringUtils.isEmpty(values[44])){
					throw new RuntimeException("Season Start Month cannot be empty when Seasonality = 4 ");
				}
				
				int seasonStartMonth = (int)Double.parseDouble(values[44]);
				if(seasonStartMonth < 1 || seasonStartMonth > 12){
					throw new RuntimeException("Season Start Month cannot less than 1 or more than 12");
				}
				newUnit.setSeasonStartMonth(seasonStartMonth);
				
			} else {
				newUnit.setSeasonStartMonth(null);
			}
			
			col = 45; //AT
			if(seasonality == 4){
				if(StringUtils.isEmpty(values[45])){
					throw new RuntimeException("Season End Month cannot be empty when Seasonality = 4 ");
				}
				
				int seasonEndMonth = (int)Double.parseDouble(values[45]);
				if(seasonEndMonth < 1 || seasonEndMonth > 12){
					throw new RuntimeException("Season End Month cannot less than 1 or more than 12");
				}
				newUnit.setSeasonEndMonth(seasonEndMonth);
				
			} else {
				newUnit.setSeasonEndMonth(null);
			}
			
			col = 46; //AU
			if(!StringUtils.isEmpty(values[46])){
				int pricingFrequencyId = (int)Double.parseDouble(values[46]);
				if(mapPricingFrequency.get(pricingFrequencyId) == null){
					if(pricingMonthDao.findById(pricingFrequencyId)==null){
						throw new RuntimeException("Pricing Frequency not found Pricing Frequency Id="+pricingFrequencyId);
					}
					mapPricingFrequency.put(pricingFrequencyId, pricingFrequencyId);
				}
				newUnit.setPricingFrequencyId(pricingFrequencyId);
			} else {
				newUnit.setPricingFrequencyId(null);
			}
			
			col = 47; //AV
			if(!StringUtils.isEmpty(values[47])){
				int rtnPeriod = (int)Double.parseDouble(values[47]);
				newUnit.setRtnPeriod(rtnPeriod);
			} else {
				newUnit.setRtnPeriod(null);
			}
			
			col = 48; //AW
			if(StringUtils.isEmpty(values[48])){
				throw new RuntimeException("Backdate Required should not be empty");
			}
			boolean backdateRequired = Boolean.parseBoolean(values[48]);
			newUnit.setBackdateRequired(backdateRequired);
			
			col = 49; //AX
			if(StringUtils.isEmpty(values[49])){
				throw new RuntimeException("Allow Edit PM Price should not be empty");
			}
			boolean allowEditPMPrice = Boolean.parseBoolean(values[49]);
			newUnit.setAllowEditPMPrice(allowEditPMPrice);
			
			col = 50; //AY
			if(StringUtils.isEmpty(values[50])){
				throw new RuntimeException("RUA Allowed should not be empty");
			}
			boolean ruaAllowed = Boolean.parseBoolean(values[50]);
			newUnit.setRuaAllowed(ruaAllowed);
			
			col = 51; //AZ
			if(StringUtils.isEmpty(values[51])){
				throw new RuntimeException("Fresh Item should not be empty");
			}
			boolean isFreshItem = Boolean.parseBoolean(values[51]);
			newUnit.setIsFreshItem(isFreshItem);
			
			col = 52; //BA
			if(StringUtils.isEmpty(values[51])){
				throw new RuntimeException("Allow Product Change should not be empty");
			}
			boolean allowProductChange = Boolean.parseBoolean(values[52]);
			newUnit.setAllowProductChange(allowProductChange);
			
			col = 53; //BB
			if(StringUtils.isEmpty(values[53])){
				throw new RuntimeException("Form Display should not be empty");
			}
			int formDisplay = (int)Double.parseDouble(values[53]);
			if(formDisplay > 2 || formDisplay < 1){
				throw new RuntimeException("Form Display cannot less than 1 or more than 2");
			}
			newUnit.setFormDisplay(formDisplay);
			
			col = 54; //BC
			if(!StringUtils.isEmpty(values[54])){
				int productCycle = (int)Double.parseDouble(values[54]);
				newUnit.setProductCycle(productCycle);
			} else {
				newUnit.setProductCycle(null);
			}
			
			col = 55; //BD
			String crossCheckGroup = values[55];
			newUnit.setCrossCheckGroup(crossCheckGroup);
			
			col = 56; //BE
			if(!StringUtils.isEmpty(values[56])){
				int cpiQuotationType = (int) Double.parseDouble(values[56]);
				newUnit.setCpiQuotationType(cpiQuotationType);
			} else {
				newUnit.setCpiQuotationType(null);
			}
			
			col = 57; //BF
			if(StringUtils.isEmpty(values[57])){
				throw new RuntimeException("Temporary should not be empty");
			}
			boolean isTemporary = Boolean.parseBoolean(values[57]);
			newUnit.setIsTemporary(isTemporary);
			
			col = 58; //BG
			if(StringUtils.isEmpty(values[58])){
				throw new RuntimeException("N Price Mandatory should not be empty");
			}
			boolean isNPriceMandatory = Boolean.parseBoolean(values[58]);
			newUnit.setIsNPriceMandatory(isNPriceMandatory);
			
			col = 59; //BH
			if(StringUtils.isEmpty(values[59])){
				throw new RuntimeException("S Price Mandatory should not be empty");
			}
			boolean isSPriceMandatory = Boolean.parseBoolean(values[59]);
			newUnit.setIsSPriceMandatory(isSPriceMandatory);
			
			col = 60; //BI
			if(StringUtils.isEmpty(values[60])){
				throw new RuntimeException("Data Transmission Rule should not be empty");
			}
			String dataTransmissionRule = values[60].trim();
			if(!dataTransmissionRule.matches("A||B||C||D||E")){
				throw new RuntimeException("Data Transmission Rule should be A, B, C, D or E");
			}
			newUnit.setDataTransmissionRule(dataTransmissionRule);
			
			col = 61; //BJ
			if(!StringUtils.isEmpty(values[61])){
				Double consolidatedSPRMean = Double.parseDouble(values[61]);
				newUnit.setConsolidatedSPRMean(consolidatedSPRMean);
			} else {
				newUnit.setConsolidatedSPRMean(null);
			}
			
			col = 62; //BK
			if(!StringUtils.isEmpty(values[62])){
				Double consolidatedSPRSD = Double.parseDouble(values[62]);
				newUnit.setConsolidatedSPRSD(consolidatedSPRSD);
			} else {
				newUnit.setConsolidatedSPRSD(null);
			}
			
			col = 63; //BL
			if(!StringUtils.isEmpty(values[63])){
				Double consolidatedNPRMean = Double.parseDouble(values[63]);
				newUnit.setConsolidatedNPRMean(consolidatedNPRMean);
			} else {
				newUnit.setConsolidatedNPRMean(null);
			}
			
			col = 64; //BM
			if(!StringUtils.isEmpty(values[64])){
				Double consolidatedNPRSD = Double.parseDouble(values[64]);
				newUnit.setConsolidatedNPRSD(consolidatedNPRSD);
			} else {
				newUnit.setConsolidatedNPRSD(null);
			}
			
			col = 65; //BN
			if(!StringUtils.isEmpty(values[col])){
				String icpType = values[col];
				newUnit.setIcpType(icpType);
			}
			
			col = 66;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Data Conversion Held Until Previous Month's Closing Date should not be empty");
			}
			boolean convertAfterClosingDate = Boolean.parseBoolean(values[col]);
			newUnit.setConvertAfterClosingDate(convertAfterClosingDate);
			
			col = 67;
			if(!StringUtils.isEmpty(values[col])){
				List<String> uomCategoryString = new ArrayList<String>();
				uomCategoryString = Arrays.asList(values[col].split("\\s*;\\s*"));
				
				List<Integer> uomCategoryIds = new ArrayList<Integer>();
				for(String uom : uomCategoryString){
					Integer uomCategoryId = Integer.valueOf(uom);
					if(mapUomCategory.containsKey(uomCategoryId)){
						uomCategoryIds.add(Integer.valueOf(uom));
					} else {
						UOMCategory uomCategory = uomCategoryDao.findById(uomCategoryId);
						if(uomCategory == null){
							throw new RuntimeException("UOM Category not found: UOM Category Id = "+uomCategoryId);
						}
						mapUomCategory.put(uomCategoryId, uomCategoryId);
						uomCategoryIds.add(Integer.valueOf(uom));
					}
				}
				newUnit.setUomCategoryIds(uomCategoryIds);
			}
			
			newUnitList.put(newUnitCode, newUnit);
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage()+" Unit Mapping (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return;
	}
	
	/**
	 * 11.insert Section
	 * 11.1  make insert sql
	 * 11.2  insert Section
	 * 11.3  map Section <new Code, New Section>	
	 * 
	 * @param newSectionList
	 * @param effectiveDate
	 */
	public void insertSection(Map<String, ImportRebasingSectionList> newSectionList, Date effectiveDate){
		List<ImportRebasingSectionList> newSections = insertSectionRecursiveQuery(new ArrayList<ImportRebasingSectionList>(newSectionList.values()), effectiveDate);
		
//		List<String> sectionList = new ArrayList<String>();
//		for(int i = 0; i<newSectionList.size();i++){
//			String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :effectiveDate , getDate(), getDate())";
//			sectionList.add(insertSql);
//		}
//		
//		if(!sectionList.isEmpty()){
//			String sectionSql = StringUtils.join(sectionList, ',');
//			List<ImportRebasingSectionList> newSections = sectionDao.insertSectionByRebasing(sectionSql, effectiveDate, newSectionList);
//			
//		}
		
		for(ImportRebasingSectionList newSection : newSections){
			ImportRebasingSectionList section = newSectionList.get(newSection.getCode());
			section.setSectionId(newSection.getSectionId());
			newSectionList.put(section.getCode(), section);
		}
		
		return;
	}
	
	public List<ImportRebasingSectionList> insertSectionRecursiveQuery(List<ImportRebasingSectionList> entities, Date effectiveDate){
		List<ImportRebasingSectionList> newSections = new ArrayList<ImportRebasingSectionList>();
		if(entities.size()>maxInsert){
			List<ImportRebasingSectionList> subEntities = entities.subList(0, maxInsert);
			newSections.addAll(insertSectionRecursiveQuery(subEntities, effectiveDate));
			
			List<ImportRebasingSectionList> remainEntities = entities.subList(maxInsert, entities.size());
			newSections.addAll(insertSectionRecursiveQuery(remainEntities, effectiveDate));
		} else if(entities.size()>0){
			List<String> sectionList = new ArrayList<String>();
			for(int i = 0; i<entities.size();i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :effectiveDate , getDate(), getDate())";
				sectionList.add(insertSql);
			}
			
			String sectionSql = StringUtils.join(sectionList, ',');
			return sectionDao.insertSectionByRebasing(sectionSql, effectiveDate, entities);
		}
		
		return newSections;
	}
	
	/**
	 * 12.insert Group
	 * 12.1  make insert sql
	 * 12.2  insert Group
	 * 12.3  map Group <new Code, New Group>
	 * 
	 * @param newGroupList
	 * @param newSectionList
	 * @param effectiveDate
	 */
	public void insertGroup(Map<String, ImportRebasingGroupList> newGroupList, Map<String, ImportRebasingSectionList> newSectionList, Date effectiveDate){
		List<ImportRebasingGroupList> newGroups = insertGroupRecursiveQuery(new ArrayList<ImportRebasingGroupList>(newGroupList.values()), newSectionList, effectiveDate);
		
		for(ImportRebasingGroupList newGroup : newGroups){
			ImportRebasingGroupList group = newGroupList.get(newGroup.getCode());
			group.setGroupId(newGroup.getGroupId());
			newGroupList.put(group.getCode(), group);
		}
		return;
	}
	
	public List<ImportRebasingGroupList> insertGroupRecursiveQuery(List<ImportRebasingGroupList> entities, Map<String, ImportRebasingSectionList> newSectionList, Date effectiveDate){
		List<ImportRebasingGroupList> newGroups = new ArrayList<ImportRebasingGroupList>();
		if(entities.size()>maxInsert){
			List<ImportRebasingGroupList> subEntities = entities.subList(0, maxInsert);
			newGroups.addAll(insertGroupRecursiveQuery(subEntities, newSectionList, effectiveDate));
			
			List<ImportRebasingGroupList> remainEntities = entities.subList(maxInsert, entities.size());
			newGroups.addAll(insertGroupRecursiveQuery(remainEntities, newSectionList, effectiveDate));
		} else if (entities.size()>0){
			List<String> groupList = new ArrayList<String>();
			for(int i = 0; i<entities.size();i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :effectiveDate , :sectionId"+i+" , getDate(), getDate())";
				groupList.add(insertSql);
			}
			
			String groupSql = StringUtils.join(groupList, ',');
			return groupDao.insertGroupByRebasing(groupSql, effectiveDate, entities, newSectionList);
		}
		
		return newGroups;
	}
	
	/**
	 * 13.insert SubGroup
	 * 13.1  make insert sql
	 * 13.2  insert Sub Group
	 * 13.3  map SubGroup <new Code, New SubGroup>
	 * 
	 * @param newSubGroupList
	 * @param newGroupList
	 * @param effectiveDate
	 */
	public void insertSubGroup(Map<String, ImportRebasingSubGroupList> newSubGroupList, Map<String, ImportRebasingGroupList> newGroupList, Date effectiveDate){
		List<ImportRebasingSubGroupList> newSubGroups = insertSubGroupRecursiveQuery(new ArrayList<ImportRebasingSubGroupList>(newSubGroupList.values()), newGroupList, effectiveDate);
		
		for(ImportRebasingSubGroupList newSubGroup : newSubGroups){
			ImportRebasingSubGroupList subGroup = newSubGroupList.get(newSubGroup.getCode());
			subGroup.setSubGroupId(newSubGroup.getSubGroupId());
			newSubGroupList.put(subGroup.getCode(), subGroup);
		}
		
		return;
	}
	
	public List<ImportRebasingSubGroupList> insertSubGroupRecursiveQuery(List<ImportRebasingSubGroupList> entities, Map<String, ImportRebasingGroupList> newGroupList, Date effectiveDate){
		List<ImportRebasingSubGroupList> newSubGroupList = new ArrayList<ImportRebasingSubGroupList>();
		if(entities.size()>maxInsert){
			List<ImportRebasingSubGroupList> subEntities = entities.subList(0, maxInsert);
			newSubGroupList.addAll(insertSubGroupRecursiveQuery(subEntities, newGroupList, effectiveDate));
			
			List<ImportRebasingSubGroupList> remainEntities = entities.subList(maxInsert, entities.size());
			newSubGroupList.addAll(insertSubGroupRecursiveQuery(remainEntities, newGroupList, effectiveDate));
		} else if(entities.size()>0){
			List<String> subGroupList = new ArrayList<String>();
			for(int i = 0; i<entities.size();i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :effectiveDate , :groupId"+i+" , getDate(), getDate())";
				subGroupList.add(insertSql);
			}
			
			String subGroupSql = StringUtils.join(subGroupList, ',');
			return subGroupDao.insertSubGroupByRebasing(subGroupSql, effectiveDate, entities, newGroupList);
		}
		
		return newSubGroupList;
	}
	
	/**
	 * 14.insert Item
	 * 14.1  make insert sql
	 * 14.2  insert Item
	 * 14.3  map Item <new Code, New Item>
	 * 
	 * @param newItemList
	 * @param newSubGroupList
	 * @param effectiveDate
	 */
	public void insertItem(Map<String, ImportRebasingItemList> newItemList, Map<String, ImportRebasingSubGroupList> newSubGroupList, Date effectiveDate){
		List<ImportRebasingItemList> newItems = insertItemRecursiveQuery(new ArrayList<ImportRebasingItemList>(newItemList.values()), newSubGroupList, effectiveDate);
		
		for(ImportRebasingItemList newItem : newItems){
			ImportRebasingItemList item = newItemList.get(newItem.getCode());
			item.setItemId(newItem.getItemId());
			newItemList.put(item.getCode(), item);
		}
		
		return;
	}
	
	public List<ImportRebasingItemList> insertItemRecursiveQuery(List<ImportRebasingItemList> entities, Map<String, ImportRebasingSubGroupList> newSubGroupList, Date effectiveDate){
		List<ImportRebasingItemList> newItems = new ArrayList<ImportRebasingItemList>();
		if(entities.size()>maxInsert){
			List<ImportRebasingItemList> subEntities = entities.subList(0, maxInsert);
			newItems.addAll(insertItemRecursiveQuery(subEntities, newSubGroupList, effectiveDate));
			
			List<ImportRebasingItemList> remainEntities = entities.subList(maxInsert, entities.size());
			newItems.addAll(insertItemRecursiveQuery(remainEntities, newSubGroupList, effectiveDate));
		} else if(entities.size()>0){
			List<String> itemList = new ArrayList<String>();
			for(int i = 0; i<entities.size();i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :effectiveDate , :subGroupId"+i+" , getDate(), getDate())";
				itemList.add(insertSql);
			}
			
			String itemSql = StringUtils.join(itemList, ',');
			return itemDao.insertItemByRebasing(itemSql, effectiveDate, entities, newSubGroupList);
		}
		
		return newItems;
	}
	
	/**
	 * 15.insert OutletType
	 * 15.1  make insert sql
	 * 15.2  insert OutletType
	 * 15.3  map OutletType <new Code, New OutletType>
	 * 
	 * @param newOutletTypeList
	 * @param newItemList
	 * @param effectiveDate
	 */
	public void insertOutletType(Map<String, ImportRebasingOutletTypeList> newOutletTypeList, Map<String, ImportRebasingItemList> newItemList, Date effectiveDate){
		List<ImportRebasingOutletTypeList> newOutletTypes = insertOutletTypeRecursiveQuery(new ArrayList<ImportRebasingOutletTypeList>(newOutletTypeList.values()), newItemList, effectiveDate);

		for(ImportRebasingOutletTypeList newOutletType : newOutletTypes){
			ImportRebasingOutletTypeList outletType = newOutletTypeList.get(newOutletType.getCode());
			outletType.setOutletTypeId(newOutletType.getOutletTypeId());
			newOutletTypeList.put(outletType.getCode(), outletType);
		}
		
		return;
	}
	
	public List<ImportRebasingOutletTypeList> insertOutletTypeRecursiveQuery(List<ImportRebasingOutletTypeList> entities, Map<String, ImportRebasingItemList> newItemList, Date effectiveDate){
		List<ImportRebasingOutletTypeList> newOutletTypes = new ArrayList<ImportRebasingOutletTypeList>();
		if(entities.size()>maxInsert){
			List<ImportRebasingOutletTypeList> subEntities = entities.subList(0, maxInsert);
			newOutletTypes.addAll(insertOutletTypeRecursiveQuery(subEntities, newItemList, effectiveDate));
			
			List<ImportRebasingOutletTypeList> remainEntities = entities.subList(maxInsert, entities.size());
			newOutletTypes.addAll(insertOutletTypeRecursiveQuery(remainEntities, newItemList, effectiveDate));
		} else if(entities.size()>0){
			List<String> outletTypeList = new ArrayList<String>();
			for(int i = 0; i<entities.size();i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :effectiveDate , :itemId"+i+" , getDate(), getDate())";
				outletTypeList.add(insertSql);
			}
			
			String outletSql = StringUtils.join(outletTypeList, ',');
			return outletTypeDao.insertOutletTypeByRebasing(outletSql, effectiveDate, entities, newItemList);
		}
		
		return newOutletTypes;
	}
	
	/**
	 * 16.insert SubItem
	 * 16.1  make insert sql
	 * 16.2  insert SubItem
	 * 16.3  map SubItem <new Code, New SubItem>
	 * 
	 * @param newSubItemList
	 * @param newOutletTypeList
	 * @param effectiveDate
	 */
	public void insertSubItem(Map<String, ImportRebasingSubItemList> newSubItemList, Map<String, ImportRebasingOutletTypeList> newOutletTypeList, Date effectiveDate){
		List<ImportRebasingSubItemList> newSubItems = insertSubItemRecursiveQuery(new ArrayList<ImportRebasingSubItemList>(newSubItemList.values()), newOutletTypeList, effectiveDate);
		
		for(ImportRebasingSubItemList newSubItem : newSubItems){
			ImportRebasingSubItemList subItem = newSubItemList.get(newSubItem.getCode());
			subItem.setSubItemId(newSubItem.getSubItemId());
			newSubItemList.put(subItem.getCode(), subItem);
		}
		
		return;
	}
	
	public List<ImportRebasingSubItemList> insertSubItemRecursiveQuery(List<ImportRebasingSubItemList> entities, Map<String, ImportRebasingOutletTypeList> newOutletTypeList, Date effectiveDate){
		List<ImportRebasingSubItemList> subItems = new ArrayList<ImportRebasingSubItemList>();
		if(entities.size()>maxInsert){
			List<ImportRebasingSubItemList> subEntities = entities.subList(0, maxInsert);
			subItems.addAll(insertSubItemRecursiveQuery(subEntities, newOutletTypeList, effectiveDate));
			
			List<ImportRebasingSubItemList> remainEntities = entities.subList(maxInsert, entities.size());
			subItems.addAll(insertSubItemRecursiveQuery(remainEntities, newOutletTypeList, effectiveDate));
		} else if(entities.size()>0){
			List<String> subItemList = new ArrayList<String>();
			for(int i = 0; i<entities.size();i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :compilationMethod"+i+" , :effectiveDate , :outletTypeId"+i+" , getDate(), getDate())";
				subItemList.add(insertSql);
			}
			
			String subItemSql = StringUtils.join(subItemList, ',');
			
			return subItemDao.insertSubItemByRebasing(subItemSql, effectiveDate, entities, newOutletTypeList);
		}
		
		return subItems;
	}
	
	/**
	 * 17.insert Unit
	 * 17.1  make insert sql
	 * 17.2  insert Unit
	 * 17.3  fill new Unit Id
	 * 
	 * @param newUnitList
	 * @param newSubItemList
	 * @param effectiveDate
	 * @param cpiBasePeriod
	 */
	public void insertUnit(Map<String, ImportRebasingUnitList> newUnitList, Map<String, ImportRebasingSubItemList> newSubItemList, Date effectiveDate, String cpiBasePeriod){
		List<ImportRebasingUnitList> newUnits = insertUnitRecursiveQuery(new ArrayList<ImportRebasingUnitList>(newUnitList.values()), newSubItemList, effectiveDate, cpiBasePeriod);
		
		for(ImportRebasingUnitList newUnit : newUnits){
			ImportRebasingUnitList unit = newUnitList.get(newUnit.getCode());
			unit.setUnitId(newUnit.getUnitId());
			newUnitList.put(unit.getCode(), unit);
		}
		
		return;
	}
	
	public List<ImportRebasingUnitList> insertUnitRecursiveQuery(List<ImportRebasingUnitList> entities, Map<String, ImportRebasingSubItemList> newSubItemList, Date effectiveDate, String cpiBasePeriod){
		List<ImportRebasingUnitList> newUnits = new ArrayList<ImportRebasingUnitList>();
		if(entities.size()>40){
			List<ImportRebasingUnitList> subEntities = entities.subList(0, 40);
			newUnits.addAll(insertUnitRecursiveQuery(subEntities, newSubItemList, effectiveDate, cpiBasePeriod));
			
			List<ImportRebasingUnitList> remainEntities = entities.subList(40, entities.size());
			newUnits.addAll(insertUnitRecursiveQuery(remainEntities, newSubItemList, effectiveDate, cpiBasePeriod));
		} else if(entities.size()>0){
			List<String> unitList = new ArrayList<String>();
			for(int i=0; i<entities.size(); i++){
				String insertSql = "( :code"+i+" , :chineseName"+i+" , :englishName"+i+" , :displayName"+i
						+ " , :isMRPS"+i+" , :purposeId"+i+" , :maxQuotation"+i+" , :minQuotation"+i+" , :unitCategory"+i
						+ " , :standardUOMId"+i+" , :uomValue"+i+" , :subPriceTypeId"+i+" , :spicingRequired"+i+ " , :frRequired"+i
						+" , :seasonality"+i+" , :seasonStartMonth"+i+" , :seasonEndMonth"+i+" , :pricingFrequencyId"+i+ " , :rtnPeriod"+i
						+" , :backdateRequired"+i+" , :allowEditPMPrice"+i+" , :ruaAllowed"+i+" , :isFreshItem"+i+ " , :allowProductChange"+i
						+" , :formDisplay"+i+" , :productCycle"+i+" , :crossCheckGroup"+i+" , :cpiQuotationType"+i+ " , :isTemporary"+i
						+" , :isNPriceMandatory"+i+" , :isSPriceMandatory"+i+" , :dataTransmissionRule"+i+" , :consolidatedSPRMean"+i+ " , :consolidatedSPRSD"+i
						+" , :consolidatedNPRMean"+i+" , :consolidatedNPRSD"+i
						+" , 'Active', :subItemId"+i+" , :productCategoryId"+i+" , :effectiveDate , :cpiBasePeriod "
						+" , :icpType"+i+" , :convertAfterClosingDate"+i+" , getDate(), getDate())"; 	
				
				unitList.add(insertSql);
			}
			
			String unitSql = StringUtils.join(unitList, ',');
			
			return unitDao.insertUnitByRebasing(unitSql, effectiveDate, cpiBasePeriod, entities, newSubItemList);
		}
		
		return newUnits;
	}
	
	/**
	 * 18.insert OnSpotValidation
	 * 18.1  find old Unit, new Unit which is one to one
	 * 18.2  insert OpSpotValidation which is one to one
	 * 
	 * @param mapUnitId
	 * @param newUnitList
	 */
	public void insertOnSpotValidation(Map<Integer, List<String>> mapUnitId, Map<String, ImportRebasingUnitList> newUnitList){
		for(Integer oldUnitId : mapUnitId.keySet()){
			List<String> unitCodes = mapUnitId.get(oldUnitId);
			if(unitCodes.size() == 1){
				String code = unitCodes.get(0);
				ImportRebasingUnitList newUnit = newUnitList.get(code);
				List<Unit> oldUnits = newUnit.getOldUnits();
				if(oldUnits.size()==1){
					onSpotValidationDao.insertOnSpotValidationByRebasing(newUnit.getUnitId(), oldUnitId);
				}
			}
		}
	}
	
	/**
	 * Insert UOMCategoryUnit new 12/8/2016
	 */
	public void insertUOMCategoryUnit(Map<String, ImportRebasingUnitList> newUnitList){
		List<String> uomCategoryUnitList = new ArrayList<String>();
		for(String key : newUnitList.keySet()){
			ImportRebasingUnitList unit = newUnitList.get(key);
			if(unit.getUomCategoryIds()!=null && unit.getUomCategoryIds().size()>0){
				for(Integer id : unit.getUomCategoryIds()){
					String sql = "( "+id+" , "+unit.getUnitId()+" , getDate(), getDate())";
					uomCategoryUnitList.add(sql);
				}
			}
		}
		insertUOMCategoryUnitRecursiveQuery(uomCategoryUnitList);
	}
	
	public void insertUOMCategoryUnitRecursiveQuery(List<String> insertSQL){
		if(insertSQL.size()>maxInsert){
			List<String> sql = insertSQL.subList(0,  maxInsert);
			insertUOMCategoryUnitRecursiveQuery(sql);
			
			List<String> remainSql = insertSQL.subList(maxInsert, insertSQL.size());
			insertUOMCategoryUnitRecursiveQuery(remainSql);
		} else if(insertSQL.size()>0){
			String finalSQL = StringUtils.join(insertSQL, ',');
			unitDao.insertUOMCategoryUnitByRebasing(finalSQL);
		}
		
		return;
	}
	
	/**
	 * 19.update Quotation
	 * 19.1  map the productGroup Id which is in excel sheet 2, <old ProductGroupId, List<newProductGroupId>>
	 * 19.2  get all old unitId
	 * 19.3  get all quotation which are quotation.unitId in oldUnitId
	 * 19.4  check quotation.productId and quotation.unit.productGroupId is not null (false will set null in new product id)
	 * 19.5  check quotation.unit.productGroupId != newUnit.productGroupId (true will set the new product id = old product id)
	 * 19.6  find the new unit productGroup.old productGroup == quotation.old product Group
	 * 19.7  find the new product.oldproductId == quotation.productId
	 * 19.8  update the quotation
	 * 
	 * @param mapUnitId
	 * @param newUnitList
	 */
	public void updateQuotation(Map<Integer, List<String>> mapUnitId, Map<String, ImportRebasingUnitList> newUnitList, Map<String, ImportRebasingProductGroupList> newProductGroupList){
		List<Integer> oldUnitIds = new ArrayList<Integer>();
		
		Map<Integer, List<Integer>> mapOldProductGroupList = new Hashtable<Integer, List<Integer>>(); //<old ProductGroupId, List<newProductGroupId>>
		
		//make a map old productGroup to new ProductGroup
		for(String key : newProductGroupList.keySet()){
			ImportRebasingProductGroupList newProductGroup = newProductGroupList.get(key);
			if(newProductGroup.getOldIds()!=null && newProductGroup.getOldIds().size()>0){
				List<Integer> oldProductGroupIds = newProductGroup.getOldIds();
				for(Integer oldProductGroupId : oldProductGroupIds){
					if(mapOldProductGroupList.get(oldProductGroupId)!=null && mapOldProductGroupList.get(oldProductGroupId).size()>0){
						List<Integer> newProductGroupIds = mapOldProductGroupList.get(oldProductGroupId);
						newProductGroupIds.add(newProductGroup.getNewId());
						mapOldProductGroupList.put(oldProductGroupId, newProductGroupIds);
					} else {
						List<Integer> newProductGroupIds = new ArrayList<Integer>();
						newProductGroupIds.add(newProductGroup.getNewId());
						mapOldProductGroupList.put(oldProductGroupId, newProductGroupIds);
					}
				}
			}
		}
		
		//get All old Unit Id in excel
		for(String key : newUnitList.keySet()){
			ImportRebasingUnitList newUnit = newUnitList.get(key);
			if(newUnit.getOldUnits()!=null && newUnit.getOldUnits().size()>0){
				for(Unit unit : newUnit.getOldUnits()){
					oldUnitIds.add(unit.getUnitId());
				}
			}
		}
		
		if(oldUnitIds != null && oldUnitIds.size()>0){
			//Get All Quotation by old Unit ID
			List<ImportRebasingQuotationList> quotations = getQuotationRecursiveQuery(new ArrayList<Integer>(new HashSet<Integer>(oldUnitIds)));
			for(ImportRebasingQuotationList quotation : quotations){
				List<String> newUnitCodes = mapUnitId.get(quotation.getUnitId());
				Integer productId = null;
				Integer unitId = null;
				//Check the quotation have productGroup Id and Product Id
				if(quotation.getProductGroupId()!=null && quotation.getProductId()!=null){
					// loop Unit
					for(String newUnitCode : newUnitCodes){
						ImportRebasingUnitList newUnit = newUnitList.get(newUnitCode);
						Integer tempUnitId = newUnit.getUnitId();
						//check the product Group Id is not same, if same = product Group is update not insert
						if(newUnit.getProductCategoryId().equals(quotation.getProductGroupId())){
							productId = quotation.getProductId();
							unitId = tempUnitId;
							break;
						}
						
						List<Integer> newProductGroupIds = mapOldProductGroupList.get(quotation.getProductGroupId());
						if(newProductGroupIds!=null && newProductGroupIds.size()>0){
							for(Integer newProductGroupId : newProductGroupIds){
								if(newProductGroupId.equals(newUnit.getProductCategoryId())){
									ImportRebasingProductGroupList newProductGroup = newProductGroupList.get(newUnit.getProductCategoryCode());
									Map<Integer, Integer> mapProducts = newProductGroup.getMapProducts();
									//find and map the product Id
									for(Integer newProductId : mapProducts.keySet()){
										if(mapProducts.get(newProductId).equals(quotation.getProductId())){
											productId = newProductId;
											unitId = tempUnitId;
											break;
										}
									}
								}
								if(productId!=null){
									break;
								}
							}
						}
						if(productId!=null){
							break;
						}
					}
				} else {
					unitId = newUnitList.get(newUnitCodes.get(0)).getUnitId();
				}
				
				//updateQuotation
				quotationDao.updateQuotationUnitByRebasing(quotation.getUnitId(), unitId, quotation.getQuotationId(), productId);
			}
		}		
	}
	
	public List<ImportRebasingQuotationList> getQuotationRecursiveQuery(List<Integer> ids){
		List<ImportRebasingQuotationList> entities = new ArrayList<ImportRebasingQuotationList>();
		if(ids.size()>2000){
			List<Integer> subIds = ids.subList(0,  2000);
			entities.addAll(getQuotationRecursiveQuery(subIds));
			
			List<Integer> remainIds = ids.subList(2000, ids.size());
			entities.addAll(getQuotationRecursiveQuery(remainIds));
		} else if(ids.size()>0){
			return quotationDao.getAllQuotationByRebasing(ids);
		}
		return entities;
	}
	
	/**
	 * 20.insert RebasingUnitMapping
	 * 20.1  build old unit, new unit map
	 * 20.2  make insert sql
	 * 20.3  insert RebasingUnitMapping
	 * 
	 * @param cpiBasePeriod
	 * @param effectiveDate
	 * @param newUnitList
	 */
	public void insertRebasingUnitMapping(String cpiBasePeriod, Date effectiveDate, Map<String, ImportRebasingUnitList> newUnitList){
		//List<String> unitMappingList = new ArrayList<String>();
		List<ImportRebasingUnitMappingList> mappingList = new ArrayList<ImportRebasingUnitMappingList>();
		for(String code : newUnitList.keySet()){
			ImportRebasingUnitList newUnit = newUnitList.get(code);
			if(newUnit.getOldUnits()!=null && newUnit.getOldUnits().size()>0){
				List<Integer> oldUnitIds = new ArrayList<Integer>();
				List<String> oldCpiBasePeriods = new ArrayList<String>();
				for(Unit unit : newUnit.getOldUnits()){
					oldUnitIds.add(unit.getUnitId());
					oldCpiBasePeriods.add(unit.getCpiBasePeriod());
				}
				
				for(int i=0; i<oldUnitIds.size();i++){
					ImportRebasingUnitMappingList unitMapping = new ImportRebasingUnitMappingList();
					unitMapping.setOldUnitId(oldUnitIds.get(i));
					unitMapping.setOldCpiBasePeriod(oldCpiBasePeriods.get(i));
					unitMapping.setNewUnitId(newUnit.getUnitId());
					mappingList.add(unitMapping);
				}
			}
		}
		
		insertRebasingUnitMappingRecursiveQuery(mappingList, effectiveDate, cpiBasePeriod);
	}
	
	public void insertRebasingUnitMappingRecursiveQuery(List<ImportRebasingUnitMappingList> entities, Date effectiveDate, String cpiBasePeriod){
		if(entities.size()>maxInsert){
			List<ImportRebasingUnitMappingList> subEntities = entities.subList(0, maxInsert);
			insertRebasingUnitMappingRecursiveQuery(subEntities, effectiveDate, cpiBasePeriod);
			
			List<ImportRebasingUnitMappingList> remainEntities = entities.subList(maxInsert, entities.size());
			insertRebasingUnitMappingRecursiveQuery(remainEntities, effectiveDate, cpiBasePeriod);
		} else if(entities.size()>0){
			List<String> unitMappingList = new ArrayList<String>();
			
			for(int i=0; i<entities.size();i++){
				String insertSql = "( :oldUnitId"+i+" , :newUnitId"+i+" , :effectiveDate , :newCpiBasePeriod , :oldCpiBasePeriod"+i+" , getDate(), getDate())";
				unitMappingList.add(insertSql);
			}
			
			String unitMappingSql = StringUtils.join(unitMappingList, ',');
			rebasingUnitMappingDao.insertRebasingUnitMapping(unitMappingSql, effectiveDate, cpiBasePeriod, entities);
			
		}
		
		return;
	}
	
//	public void updateOldUnitObsoleteDate(Map<Integer, Date> unitIds, Date effectiveDate){
//		List<Integer> updateIds = new ArrayList<Integer>();
//		for(Integer key : unitIds.keySet()){
//			Date obsoleteDate = unitIds.get(key);
//			if(obsoleteDate != null){
//				List<Integer> ids = new ArrayList<Integer>();
//				ids.add(key);
//				
//				unitDao.updateUnitObsoleteDateByRebasing(ids, obsoleteDate);
//			} else {
//				updateIds.add(key);
//			}
//		}
//		updateOldUnitObsoleteDateRecursiveQuery(updateIds, effectiveDate);
//	}
	
	public void updateOldUnitObsoleteDateRecursiveQuery(List<Integer> unitIds, Date effectiveDate){
		if(unitIds.size()>maxInsert){
			List<Integer> ids = unitIds.subList(0, maxInsert);
			updateOldUnitObsoleteDateRecursiveQuery(ids, effectiveDate);
			
			List<Integer> remainIds = unitIds.subList(maxInsert, unitIds.size());
			updateOldUnitObsoleteDateRecursiveQuery(remainIds, effectiveDate);
		} else if(unitIds.size()>0){
			unitDao.updateUnitObsoleteDateByRebasing(unitIds, effectiveDate);
		}
	}
}
