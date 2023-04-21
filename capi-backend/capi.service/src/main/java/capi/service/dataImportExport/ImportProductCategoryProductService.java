package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.ProductAttributeDao;
import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.dal.ProductSpecificationDao;
import capi.entity.ImportExportTask;
import capi.entity.Product;
import capi.entity.ProductAttribute;
import capi.entity.ProductGroup;
import capi.entity.ProductSpecification;
import capi.service.AppConfigService;
import capi.service.productMaintenance.ProductService;

@Service("ImportProductCategoryProductService")
public class ImportProductCategoryProductService extends DataImportServiceBase{

	@Autowired
	private ProductGroupDao dao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductAttributeDao productAttributeDao;
	
	@Autowired
	private ProductSpecificationDao productSpecificationDao;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 14;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		
		//Get Attribute		
//		int rowCnt = 0;
//		
//		int rowSize = row.getLastCellNum();
		
		List<Integer> id = new ArrayList<Integer>();
		id.add(task.getProductGroup().getId());
		List<ProductAttribute> attributes = productAttributeDao.getProductAttributeByProductGroupIds(id);
		
		
		//For Body
		
		int rowCnt = 1;
		rows.next();
		while(rows.hasNext()){
			Row row = rows.next();
			int rowSize = row.getLastCellNum();
			String[] values = getStringValues(row, rowSize);
			
			//For Product
			Product product = fillEntity(values, rowCnt, task.getProductGroup());
			productDao.save(product);
			if(product.getId()!=null){
				ids.add(product.getId());
			}
			
			//For ProductSpecification
//			ProductSpecification specification;
			fillEntityForSpecification(values, rowCnt, product, attributes);
			
			if(rowCnt % 20 == 0){
				productDao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		List<Product> products = productDao.getNotExistedProductByProductGroupIdAndProductId(ids, task.getProductGroup().getId());
		List<Integer> deleteIds = new ArrayList<Integer>();
		for(Product product : products){
			deleteIds.add(product.getProductId());
		}
//		if (deleteIds.size() > 0)
//			productService.deleteProduct(deleteIds, configService.getFileBaseLoc());
		
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = deleteIds.size() / maxSize;
		int remainder = deleteIds.size() % maxSize;
		
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		
		// Quotient
		for(int i = 0; i < times; i++) {
			splited = deleteIds.subList(fromIdx, toIdx);
			//Delete Outlet
			productService.deleteProduct(splited, configService.getFileBaseLoc());
			
			if(i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
		
		// Remainder
		if(times == 0) {
			if(remainder != 0) {
				splited = deleteIds.subList(fromIdx, remainder);
			}
		} else {
			if(remainder != 0) {
				splited = deleteIds.subList(toIdx, (toIdx + remainder));
			}
		}
		
		if(remainder != 0) {
			//Delete Product
			productService.deleteProduct(splited, configService.getFileBaseLoc());
		}
		
		
		productDao.flush();
		
		workbook.close();
	}
	
	private Product fillEntity(String[] values, int rowCnt, ProductGroup productGroup) throws Exception{
		Product entity = null;
		
		int col = 0;
		try{
			//For Product
			String idStr = values[0];
			if(StringUtils.isEmpty(idStr)){
				entity = new Product();
				//For ProductGroup
				entity.setProductGroup(productGroup);
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = productDao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: Product Id="+id);
				}
				if(!entity.getProductGroup().getProductGroupId().equals(productGroup.getProductGroupId())){
					throw new RuntimeException("Record not in this Product Group: Product Id="+id);
				}
			}
			
			col = 1;
			if(StringUtils.isEmpty(values[1])){
				throw new RuntimeException("Country of Origin should not be empty");
			}
			String countryOfOrigin = values[1].trim();
			entity.setCountryOfOrigin(countryOfOrigin);
			
			col = values.length-4;
			if(!StringUtils.isEmpty(values[col])){
				String remark = values[col];
				entity.setRemark(remark);
			} else {
				entity.setRemark(null);
			}
			
			col++;
			if(!StringUtils.isEmpty(values[col])){
				String barcode = values[col];
				entity.setBarcode(barcode);
			} else {
				entity.setBarcode(null);
			}
			
			col++;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Reviewed should not be empty");
			}
			boolean reviewed = Boolean.parseBoolean(values[col]);
			entity.setReviewed(reviewed);
			
			col++;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Status should not be empty");
			}
			String status = values[col].trim();
			if(!(status.equals("Active")||status.equals("Inactive"))){
				throw new RuntimeException("Status only Active or Inactive");
			}
			entity.setStatus(status);
			
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage()+" (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
	private void fillEntityForSpecification(String[] values, int rowCnt, Product product, List<ProductAttribute> attributes){
		int col = 0;
		
		if((values.length-6)!=attributes.size()){
			throw new RuntimeException("Attributes Length not same");
		}
		
		col = 2;
		
		for(ProductAttribute attribute : attributes){
			ProductSpecification specification = null;
			try{
				if(product.getId()!=null){
					specification = productSpecificationDao.getProductSpecificationByProductAndAttribute(product.getId(), attribute.getId());
					if(specification != null){
						specification.setValue(values[col]);
					} else {
						specification = new ProductSpecification();
						specification.setProduct(product);
						specification.setProductAttribute(attribute);
						specification.setValue(values[col]);
					}
				} else {
					specification = new ProductSpecification();
					specification.setProduct(product);
					specification.setProductAttribute(attribute);
					specification.setValue(values[col]);
				}
			} catch (Exception ex){
				throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
			}
			productSpecificationDao.save(specification);
			
			col++;
		}
		
	}
	
	/*
	private void fillEntityForAttribute(String[] values, List<ProductAttribute> attributes){
		int col = 6;
		
		
		int newAttributeSize = values.length-6;
		
		if(newAttributeSize<attributes.size()){
			int i = 0;
			for(ProductAttribute attribute : attributes){
				String specificationName = values[col].trim();
				attribute.setSpecificationName(specificationName);
				
				productAttributeDao.save(attribute);
				
				col++;
				
				i++;
			}
		}
		
		if(newAttributeSize==attributes.size()){
			for(ProductAttribute attribute : attributes){
				String specificationName = values[col].trim();
				attribute.setSpecificationName(specificationName);
				
				productAttributeDao.save(attribute);
				attributes.add(attribute);
				col++;
			}
		}
		
		
	}*/
	
}
