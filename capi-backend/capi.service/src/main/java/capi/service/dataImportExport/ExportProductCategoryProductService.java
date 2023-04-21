package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.ProductAttributeDao;
import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.entity.ImportExportTask;
import capi.entity.Product;
import capi.entity.ProductAttribute;
import capi.entity.VwProductFullSpec;

@Service("ExportProductCategoryProductService")
public class ExportProductCategoryProductService extends DataExportServiceBase{

	@Autowired
	private ProductGroupDao dao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductAttributeDao productAttributeDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = new String[]{"Product ID", "Country of Origin"};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 14;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		ImportExportTask task = taskDao.findById(taskId);
		
		ScrollableResults results = productDao.getProductResultByProductGroupId(task.getProductGroup().getId());
		
		/**
		 * 0/ProductId
		 * 1/countryOfOrigin
		 * 2/remark
		 * 3/status
		 * 4/reviewed
		 * 5/barCode
		 * 
		 * After 6/Attribute
		 **/
		Map<Integer, Integer> mapAttribute = new Hashtable<Integer, Integer>(); // Attribute Id, cnt
		Map<String, Integer> mapProduct = new Hashtable<String, Integer>(); // Group Header Name, cnt
		
		sheet = createHeader2(sheet, mapAttribute, mapProduct, task.getProductGroup().getId());
		
		int rowCnt = 1;
		while(results.next()){
			Product code = (Product)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			
			//For Product
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(code.getId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(code.getCountryOfOrigin());
			
			
			SXSSFCell cell2 = row.createCell(mapProduct.get("Remarks"));
			cell2.setCellValue(code.getRemark());
			SXSSFCell cell3 = row.createCell(mapProduct.get("Barcode"));
			cell3.setCellValue(code.getBarcode());
			SXSSFCell cell4 = row.createCell(mapProduct.get("Is reviewed"));
			cell4.setCellValue(code.getReviewed());
			SXSSFCell cell5 = row.createCell(mapProduct.get("Status"));
			cell5.setCellValue(code.getStatus());
			
			
			//For Specification
			Set<VwProductFullSpec> specifications = code.getFullSpecifications();
			for(VwProductFullSpec specification : specifications){
				SXSSFCell specificationCell = row.createCell(mapAttribute.get(specification.getProductAttribute().getId()));
				specificationCell.setCellValue(specification.getValue());
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
	
	private SXSSFSheet createHeader2(SXSSFSheet sheet, Map<Integer, Integer> mapAttribute, Map<String, Integer> mapProduct, int productGroupId){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cellCnt = headers.length;
		List<Integer> id = new ArrayList<Integer>();
		
		id.add(productGroupId);
		
		List<ProductAttribute> attributeList = productAttributeDao.getProductAttributeByProductGroupIds(id);
		for(ProductAttribute attribute : attributeList){
			mapAttribute.put(attribute.getId(), cellCnt);
			
			SXSSFCell cellHeader = row.createCell(cellCnt);
			cellHeader.setCellValue(attribute.getSpecificationName());
			
			cellCnt++;
		}
		
		String[] headers2 = new String[]{"Remarks", "Barcode", "Is reviewed", "Status"}; 
		
		for(String header: headers2){
			SXSSFCell cellHeader = row.createCell(cellCnt);
			cellHeader.setCellValue(header);
			
			mapProduct.put(header, cellCnt);
			cellCnt++;
		}
		
		return sheet;
	}
	
}
