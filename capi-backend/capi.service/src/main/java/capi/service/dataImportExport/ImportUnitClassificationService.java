package capi.service.dataImportExport;
 
import java.util.Date;
import java.util.Iterator;
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
import capi.dal.ProductGroupDao;
import capi.dal.PurposeDao;
import capi.dal.SectionDao;
import capi.dal.SubGroupDao;
import capi.dal.SubItemDao;
import capi.dal.UnitDao;
import capi.entity.Group;
import capi.entity.ImportExportTask;
import capi.entity.Item;
import capi.entity.OutletType;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.Section;
import capi.entity.SubGroup;
import capi.entity.SubItem;
import capi.entity.Unit;
import capi.service.CommonService;
 
@Service("ImportUnitClassificationService")
public class ImportUnitClassificationService extends DataImportServiceBase{
 
   @Autowired
   private UnitDao dao;
   
   @Autowired
   private ImportExportTaskDao taskDao;
   
   @Autowired
   private PurposeDao purposeDao;
   
   @Autowired
   private ProductGroupDao productGroupDao;
   
   @Autowired
   private SubItemDao subItemDao;
   
   @Autowired
   private OutletTypeDao outletTypeDao;
   
   @Autowired
   private ItemDao itemDao;
   
   @Autowired
   private SubGroupDao subGroupDao;
   
   @Autowired
   private GroupDao groupDao;
   
   @Autowired
   private SectionDao sectionDao;
   
   @Autowired
   private CommonService commonService;
   
   @Override
   public int getTaskNo() {
       // TODO Auto-generated method stub
       return 12;
   }
   
   @Override
   public void runTask(Integer taskId) throws Exception {
       // TODO Auto-generated method stub
       ImportExportTask task = taskDao.findById(taskId);
       
       XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
       XSSFSheet sheet = workbook.getSheetAt(0);
       Iterator<Row> rows = sheet.iterator();
       String cpiBasePeriod = task.getCpiBasePeriod();
       
       int rowCnt = 1;
       rows.next(); // remove column header
       while (rows.hasNext()){
           Row row = rows.next();
           String[] values = getStringValues(row, 37);
           
           //For Unit
           Unit unit = fillEntity(values, rowCnt, cpiBasePeriod);
           dao.save(unit);
           
           if(rowCnt%20 == 0){
               dao.flushAndClearCache();
           }
           rowCnt++;
       }
       
       dao.flush();
       
       workbook.close();
   }
   
   private Unit fillEntity(String[] values, int rowCnt, String basePeriod) throws Exception{
       Unit entity = null;
       int col = 0;
       try{
           //For Unit (col: 0-5,7,35-36)
           col = 0;
           String idStr = values[0];
           if (StringUtils.isEmpty(idStr)){
               throw new RuntimeException("Variety Id should not be empty");
           }
           int id = (int)Double.parseDouble(idStr);
           entity = dao.findById(id);
           if(entity == null){
               throw new RuntimeException("Record not found: Unit id="+id);
           }
           
           //Check isUnitCodeExist (col: 1, 5) update by 23/12/2015
           String oldCode = entity.getCode();
           String oldCpiBasePeriod = entity.getCpiBasePeriod();
           col = 1;
           if(StringUtils.isEmpty(values[1])){
               throw new RuntimeException("Variety Code should not be empty");
           }
           String code = values[1].trim();
           
           col = 5;
           if(StringUtils.isEmpty(values[5])){
               throw new RuntimeException("Cpi Base Period should not be empty");
           }
           String cpiBasePeriod = values[5].trim();
           
           if(!oldCode.equals(code) || !oldCpiBasePeriod.equals(cpiBasePeriod)){
               if(dao.isUnitCodeExist(code, cpiBasePeriod)){
                   throw new RuntimeException("Please enter a valid code for the unit");
               }
           }
           entity.setCode(code);
           entity.setCpiBasePeriod(cpiBasePeriod);
           
           col = 2;
           String chineseName = values[2];
           entity.setChineseName(chineseName);
           
           col = 3;
           String englishName = values[3];
           entity.setEnglishName(englishName);
           
           col = 4;
           String displayName = values[4];
           entity.setDisplayName(displayName);
           
           col = 7;
           if(StringUtils.isEmpty(values[7])){
               throw new RuntimeException("Status should not be empty");
           }
           String status = values[7].trim();
           if(!(status.equals("Active")||status.equals("Inactive"))){
               throw new RuntimeException("Status only Active or Inactive");
           }
           entity.setStatus(status);
           
           col = 35;
           if(!StringUtils.isEmpty(values[35])){
               String date = values[35].trim();
               if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
                   throw new RuntimeException("Obsolete Date Format should be DD-MM-YYYY");
               }
               Date obsoleteDate = commonService.getDate(date);
               entity.setObsoleteDate(obsoleteDate);
           }else {
               entity.setObsoleteDate(null);
           }
           
           col = 36;
           if(!StringUtils.isEmpty(values[36])){
               String date = values[36].trim();
               if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
                   throw new RuntimeException("Effective Date Format should be DD-MM-YYYY");
               }
               Date effectiveDate = commonService.getDate(date);
               entity.setEffectiveDate(effectiveDate);
           }else {
               entity.setEffectiveDate(null);
           }
           
           //Purpose
           col = 6;
           //boolean isPurposeChanged = false;
           if(StringUtils.isEmpty(values[6])){
               throw new RuntimeException("Purpose Code should not be empty");
           }
           String purposeCode = values[6].trim();
           Purpose purpose = purposeDao.getSurveyTypeByCode(purposeCode);
           if(purpose == null){
               throw new RuntimeException("Record not found: Purpose Code ="+purposeCode);
           }
           entity.setPurpose(purpose);
           
           //Product Group
           col = 8;
           if(!StringUtils.isEmpty(values[8])){
               int productGroupId = (int)Double.parseDouble(values[8]);
               ProductGroup productGroup = productGroupDao.findById(productGroupId);
               if(productGroup == null){
                   throw new RuntimeException("Record not found: Product Group Id = "+ productGroupId);
               }
               entity.setProductCategory(productGroup);
           } else {
               entity.setProductCategory(null);
           }
           
           
           //For SubItem
           SubItem subItem = entity.getSubItem();
           if(subItem == null){
               throw new RuntimeException("SubItem not found: Unit id="+id);
           }
           col = 30; //SubItem Id Not check
           col = 31; 
           if(StringUtils.isEmpty(values[col])){
        	   throw new RuntimeException("SubItem Code should not be empty");
           }
           String subItemCode = values[col].trim();
           if(!subItemCode.equals(subItem.getCode())){
        	   if(subItemDao.getByCode(subItemCode, basePeriod)!=null){
        		   throw new RuntimeException("Please enter a valid code for the Sub Item");
        	   }
        	   subItem.setCode(subItemCode);
           }
           
           col = 32;
           String subItemChineseName = values[32];
           subItem.setChineseName(subItemChineseName);
           
           col = 33;
           String subItemEnglishName = values[33];
           subItem.setEnglishName(subItemEnglishName);
           
           col = 34; 
           if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Compilation Method should not be empty");
			}
			int compilationMethod = (int) Double.parseDouble(values[col]);
			if(compilationMethod<1 || compilationMethod >6){
				throw new RuntimeException("Compilation Method should not be less than 1 or more than 6");
			}
			subItem.setCompilationMethod(compilationMethod);
           
           //For OutletType
           OutletType outletType = subItem.getOutletType();
           if(outletType == null){
               throw new RuntimeException("OutletType not found: Unit id="+id);
           }
           col = 26; //OutletType Id not check
           col = 27; 
           if(StringUtils.isEmpty(values[col])){
        	   throw new RuntimeException("Outlet Type Code should not be empty");
           }
           String outletTypeCode = values[col].trim();
           if(!outletTypeCode.equals(outletType.getCode())){
        	   if(outletTypeDao.getByCode(outletTypeCode, basePeriod)!=null){
        		   throw new RuntimeException("Please enter a valid code for the Outlet Type");
        	   }
        	   outletType.setCode(outletTypeCode);
           }
           
           col = 28;
           String outletTypeChineseName = values[28];
           outletType.setChineseName(outletTypeChineseName);
           
           col = 29;
           String outletTypeEnglishName = values[29];
           outletType.setEnglishName(outletTypeEnglishName);
           
           //For Item
           Item item = outletType.getItem();
           if(item == null){
               throw new RuntimeException("Item not found: Unit id="+id);
           }
           col = 22; //Item Id not check
           col = 23; 
           if(StringUtils.isEmpty(values[col])){
        	   throw new RuntimeException("Item Code should not be empty");
           }
           String itemCode = values[col].trim();
           if(!itemCode.equals(item.getCode())){
        	   if(itemDao.getByCode(itemCode, basePeriod)!=null){
        		   throw new RuntimeException("Please enter a valid code for the Item");
        	   }
        	   item.setCode(itemCode);
           }
           
           col = 24;
           String itemChineseName = values[24];
           item.setChineseName(itemChineseName);
           
           col = 25;
           String itemEnglishName = values[25];
           item.setEnglishName(itemEnglishName);
           
           //For SubGroup
           SubGroup subGroup = item.getSubGroup();
           if(subGroup == null){
               throw new RuntimeException("SubGroup not found: Unit id="+id);
           }
           col = 18; //SubGroup Id not check
           col = 19; 
           if(StringUtils.isEmpty(values[col])){
        	   throw new RuntimeException("Sub Group Code should not be empty");
           }
           String subGroupCode = values[col].trim();
           if(!subGroupCode.equals(subGroup.getCode())){
        	   if(subGroupDao.getByCode(subGroupCode, basePeriod)!=null){
        		   throw new RuntimeException("Please enter a valid code for the Sub Group");
        	   }
        	   subGroup.setCode(subGroupCode);
           }
           
           col = 20;
           String subGroupChineseName = values[20];
           subGroup.setChineseName(subGroupChineseName);
           
           col = 21;
           String subGroupEnglishName = values[21];
           subGroup.setEnglishName(subGroupEnglishName);
           
           //For Group
           Group group = subGroup.getGroup();
           if(group == null){
               throw new RuntimeException("Group not found: Unit id="+id);
           }
           col = 14; //Group Id not check
           col = 15; 
           if(StringUtils.isEmpty(values[col])){
        	   throw new RuntimeException("Group Code should not be empty");
           }
           String groupCode = values[col].trim();
           if(!groupCode.equals(group.getCode())){
        	   if(groupDao.getByCode(groupCode, basePeriod)!=null){
        		   throw new RuntimeException("Please enter a valid code for the Group");
        	   }
        	   group.setCode(groupCode);
           }
           
           col = 16;
           String groupChineseName = values[16];
           group.setChineseName(groupChineseName);
           
           col = 17;
           String groupEnglishName = values[17];
           group.setEnglishName(groupEnglishName);
           
           //For Section
           Section section = group.getSection();
           if(section == null){
               throw new RuntimeException("Section not found: Unit id="+id);
           }
           col = 10; //Section Id not check
           col = 11; 
           if(StringUtils.isEmpty(values[col])){
        	   throw new RuntimeException("Section Code should not be empty");
           }
           String sectionCode = values[col].trim();
           if(!sectionCode.equals(section.getCode())){
        	   if(sectionDao.getByCode(sectionCode, basePeriod)!=null){
        		   throw new RuntimeException("Please enter a valid code for the Section");
        	   }
        	   section.setCode(sectionCode);
           }
           
           col = 12;
           String sectionChineseName = values[12];
           section.setChineseName(sectionChineseName);
           
           col = 13;
           String sectionEnglishName = values[13];
           section.setEnglishName(sectionEnglishName);
           
       } catch (Exception ex){
           throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
       }
       
       return entity;
   }
}