package capi.service.dataImportExport;

import java.util.ArrayList;
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
import capi.dal.ProductAttributeDao;
import capi.dal.ProductGroupDao;
import capi.dal.ProductSpecificationDao;
import capi.entity.ImportExportTask;
import capi.entity.ProductAttribute;
import capi.entity.ProductGroup;
import capi.entity.ProductSpecification;
import capi.service.CommonService;
import capi.service.productMaintenance.ProductService;

@Service("ImportProductCategoryService")
public class ImportProductCategoryService extends DataImportServiceBase{
	
	@Autowired
	private ProductGroupDao dao;
	
	@Autowired
	private ProductAttributeDao attributeDao;
	
	@Autowired
	private ProductSpecificationDao productSpecificationDao;
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 15;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> attributeIds = new ArrayList<Integer>();
		//For Product Attribute
		XSSFSheet sheet2 = workbook.getSheetAt(1);
		Iterator<Row> rows2 = sheet2.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 7); 
			
			//For ProductGroup
			ProductGroup productGroup = fillEntity(values, rowCnt);
			dao.save(productGroup);
			if(productGroup.getId() != null){
				ids.add(productGroup.getId());
			}
			
			if(rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		rowCnt = 1;
		rows2.next(); //remove column header
		while(rows2.hasNext()){
			Row row = rows2.next();
			String[] values = getStringValues(row, 7);
			
			//For ProductAttribute
			ProductAttribute productAttribute = fillEntityForAttribute(values, rowCnt);
			attributeDao.save(productAttribute);
			if(productAttribute.getProductAttributeId()!=null){
				attributeIds.add(productAttribute.getProductAttributeId());
			}
			if(rowCnt % 20 == 0){
				attributeDao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		
		List<Integer> existingAttribute = attributeDao.getExistingProductAttributeId();
		List<Integer> notExistingAttribute = new ArrayList<Integer>(CollectionUtils.subtract(existingAttribute, attributeIds));
		
		for(Integer attributeId : notExistingAttribute){
			List<ProductSpecification> specEntries = productSpecificationDao.getProductSpecificationByProductAttributeId(attributeId);
			for (ProductSpecification specEntry : specEntries){
				productSpecificationDao.delete(specEntry);
			}
		}
		
		deleteEntities(notExistingAttribute, attributeDao);
		
		List<Integer> existing = dao.getExistingProductGroupId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existing, ids));
		
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = notExisting.size() / maxSize;
		int remainder = notExisting.size() % maxSize;
		
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		
		// Quotient
		for(int i = 0; i < times; i++) {
			splited = notExisting.subList(fromIdx, toIdx);
			//Delete 
			service.deleteProductGroup(splited);
			
			if(i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
		
		// Remainder
		if(times == 0) {
			if(remainder != 0) {
				splited = notExisting.subList(fromIdx, remainder);
			}
		} else {
			if(remainder != 0) {
				splited = notExisting.subList(toIdx, (toIdx + remainder));
			}
		}
		
		if(remainder != 0) {
			//Delete 
			service.deleteProductGroup(splited);
		}
		
		
		dao.flush();
		
		workbook.close();
	}
	
	private ProductGroup fillEntity(String [] values, int rowCnt) throws Exception{
		ProductGroup entity = null;
		int col = 0;
		try{
			String idStr = values[0];
			if(StringUtils.isEmpty(idStr)){
				entity = new ProductGroup();
//				throw new RuntimeException("Product Category Id should not be empty");
			}  else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: Product Category Id="+id);
				}
			}
			
			col = 1;
			if(StringUtils.isEmpty(values[1])){
				throw new RuntimeException("Product Category Code should not be empty");
			}
			String code = values[1].trim();
			if(code.length()!=17){
				throw new RuntimeException("The length of the Product Category code should be equal to 17");
			}
			
			ProductGroup productGroup = dao.getProductGroupsByCode(code);
			if(productGroup != null && !entity.getId().equals(productGroup.getId())){
				throw new RuntimeException("Product Category Code already existed: "+code);
			}
			entity.setCode(code);
			
			col = 2;
			String chineseName = values[2];
			entity.setChineseName(chineseName);
			
			col = 3;
			String englishName = values[3];
			entity.setEnglishName(englishName);
			
			col = 4;
			if(!StringUtils.isEmpty(values[4])){
				String date = values[4].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Effective Date Format should be DD-MM-YYYY");
				}
				entity.setEffectiveDate(commonService.getDate(date));
			} else {
				entity.setEffectiveDate(null);
			}
			
			col = 5;
			if(!StringUtils.isEmpty(values[5])){
				String date = values[5].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Obsolete Date Format should be DD-MM-YYYY");
				}
				entity.setObsoleteDate(commonService.getDate(date));
			} else {
				entity.setObsoleteDate(null);
			}
			
			col = 6;
			if(!StringUtils.isEmpty(values[6])){
				String status = values[6].trim();
				entity.setStatus(status);
			} else {
				entity.setStatus(null);
			}
			
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (Sheet 1: row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
	private ProductAttribute fillEntityForAttribute(String[] values, int rowCnt) throws Exception{
		ProductAttribute entity = null;
		int col = 0;
		try{
			if(StringUtils.isEmpty(values[0])){
				entity = new ProductAttribute();
			} else {
				int attributeId = (int) Double.parseDouble(values[0].trim());
				entity = attributeDao.findById(attributeId);
				if(entity==null){
					throw new RuntimeException("Product Attribute not found: Product Attribute Id = "+ attributeId);
				}
			}
			
			col = 1;
			
			
			if(entity.getProductGroup()==null){
				String productGroupCode = values[col].trim();
				if(StringUtils.isEmpty(productGroupCode)){
					throw new RuntimeException("Product Group Code should not be empty");
				}
				ProductGroup productGroup = dao.getProductGroupsByCode(productGroupCode);
				
				if(productGroup==null){
					throw new RuntimeException("Product Group not found: Product Group Code = "+productGroupCode);
				}
				
				entity.setProductGroup(productGroup);
			}
			
			col = 2;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Sequence should not be empty");
			}
			int sequence = (int)Double.parseDouble(values[col]);
			entity.setSequence(sequence);
			
			col = 3;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Specification name should not be empty");
			}
			String specificationName = values[col];
			entity.setSpecificationName(specificationName);
			
			col = 4;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Attribute Type should not be empty");
			}
			int attributeType = (int)Double.parseDouble(values[col]);
			if(attributeType<1 || attributeType>3){
				throw new RuntimeException("Attribute Type should not be less than 1 or more than 3");
			}
			entity.setAttributeType(attributeType);
			
			col = 5;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Mandatory should not be empty");				
			}
			boolean isMandatory = Boolean.parseBoolean(values[col]);
			entity.setIsMandatory(isMandatory);
			
			col = 6;
			if(!StringUtils.isEmpty(values[col])){
				String option = values[col].trim();
				entity.setOption(option);
			}
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (Sheet 2: row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
	}
}
