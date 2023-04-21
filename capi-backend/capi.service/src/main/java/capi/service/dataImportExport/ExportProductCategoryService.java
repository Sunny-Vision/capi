package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.List;
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
import capi.dal.ProductGroupDao;
import capi.entity.ImportExportTask;
import capi.entity.ProductGroup;
import capi.model.dataImportExport.ExportProductAttributeList;
import capi.service.CommonService;

@Service("ExportProductCategoryService")
public class ExportProductCategoryService extends DataExportServiceBase{
	
	@Autowired
	private ProductGroupDao dao;
	
	@Autowired
	private ProductAttributeDao productAttributeDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = new String []{
			"Product Category Id", "Product Category Code", "Chinese Name", "English Name", "EffectiveDate"
			, "ObsoleteDate", "Status"
			};
	
	private static final String[] sheet2Headers = new String[]{
			"Product Attribute ID", "Product Category Code", "Sequence", "Specification Name"
			, "Attribute Type (1 = Free-entry, 2 = Options, 3 = Options with others)", "Is Mandatory", "Options"
	};
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 15;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		/**
		 * Sheet 2 Head Building
		 */
		SXSSFSheet sheet2 = workBook.createSheet();
		createSheet2Header(sheet2.createRow(0));
		
		ScrollableResults results = dao.getAllProductGroupResult();
		
		/**
		 * 0/ProductGroupId
		 * 1/Code
		 * 2/ChineseName
		 * 3/EnglishName
		 * 4/Effective Date
		 * 5/Obsolete Date
		 * 6/Status
		 * 
		 **/
		
		int rowCnt = 1;
		
		while(results.next()){
			ProductGroup code = (ProductGroup)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			
			//For ProductGroup
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(code.getId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(code.getCode());
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(code.getChineseName());
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(code.getEnglishName());
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(commonService.formatDate(code.getEffectiveDate()));
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(commonService.formatDate(code.getObsoleteDate()));
			SXSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(code.getStatus());
			
			dao.evit(code);
			rowCnt++;
			
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		//For Product Attribute : Sheet 2
		List<ExportProductAttributeList> attributes = productAttributeDao.getAllProductAttributeResult();
		int Sheet2rowCnt = 1;
		/**
		 * 0/ProductAttributeId
		 * 1/ProductGroup.Code
		 * 2/Sequence
		 * 3/Specification Name
		 * 4/Attribute Type
		 * 5/IsMandatory
		 * 6/Option
		 **/
		
		for(ExportProductAttributeList attribute : attributes){
			
			SXSSFRow sheet2row = sheet2.createRow(Sheet2rowCnt);
			
			int cellCnt2 = 0;
			
			SXSSFCell sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(String.valueOf(attribute.getAttributeId()));
			sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(attribute.getCode());
			sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(String.valueOf(attribute.getSequence()));
			sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(attribute.getSpecificationName());
			sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(String.valueOf(attribute.getAttributeType()));
			sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(attribute.isMandatory());
			sheet2Cell = sheet2row.createCell(cellCnt2++);
			sheet2Cell.setCellValue(attribute.getOption());
			
			Sheet2rowCnt++;
			
			if (rowCnt % 2000 == 0){
				sheet2.flushRows();
			}
		}
		
		results.close();
		
		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			ImportExportTask task = taskDao.findById(taskId);
			task.setFilePath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		taskDao.flush();
	}
	
	@Override
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for(String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
	public void createSheet2Header(SXSSFRow row){
		int cnt = 0;
		for(String header : sheet2Headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
}
