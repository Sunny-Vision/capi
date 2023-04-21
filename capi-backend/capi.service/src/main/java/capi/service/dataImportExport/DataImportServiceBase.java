package capi.service.dataImportExport;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import capi.dal.GenericDao;
import capi.service.AppConfigService;
import capi.service.CommonService;

public abstract class DataImportServiceBase implements DataImportService{

	@Autowired
	private AppConfigService config;
	@Autowired
	private CommonService commonService;

	public XSSFWorkbook getWorkbook(String path) throws InvalidFormatException, IOException{
		String file = config.getImportFileLoc()+path;
		XSSFWorkbook workBook = new XSSFWorkbook(file);	
		return workBook;
	}
	
	public String[] getStringValues(Row row, int columnCnt){
		String[] values = new String[columnCnt];
		
		Iterator<Cell> cells = row.cellIterator();
		int i = 0;
		while(cells.hasNext()){
			if (i >= columnCnt)
				break;
			
			Cell cell = cells.next();
			cell.getColumnIndex();
			if (cell.getCellType() == Cell.CELL_TYPE_BLANK){
				values[cell.getColumnIndex()] = "";
			}
			else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
				values[cell.getColumnIndex()] = String.valueOf(cell.getBooleanCellValue());
			}			 
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
				if (HSSFDateUtil.isCellDateFormatted(cell)){
					Date date = cell.getDateCellValue();
					values[cell.getColumnIndex()] = commonService.formatDateTime(date);
				}
				else{
					values[cell.getColumnIndex()] = String.valueOf(cell.getNumericCellValue());
				}
			}
			else{
				values[cell.getColumnIndex()] = cell.getStringCellValue();
			}
			
			i++;
		}
		return values;
		
	}
	
	public Object[] getObjectValues(Row row, int columnCnt){
		Object[] values = new Object[columnCnt];
		
		Iterator<Cell> cells = row.cellIterator();
		int i = 0;
		while(cells.hasNext()){
			if (i >= columnCnt)
				break;
			Cell cell = cells.next();
			cell.getColumnIndex();
			if (cell.getCellType() == Cell.CELL_TYPE_BLANK){
				values[cell.getColumnIndex()] = "";
			}
			else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
				values[cell.getColumnIndex()] = cell.getBooleanCellValue();
			}			 
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
				if (HSSFDateUtil.isCellDateFormatted(cell)){
					Date date = cell.getDateCellValue();
					values[cell.getColumnIndex()] = date;
				}
				else{
					values[cell.getColumnIndex()] = cell.getNumericCellValue();
				}
			}
			else{
				values[cell.getColumnIndex()] = cell.getStringCellValue();
			}
			i++;
		}
		return values;
		
	}
		

	public <T extends Serializable> void deleteEntities(List<Integer> notExisting, GenericDao<T> delDao ){
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
			List<T> entites = delDao.getEntityByIds(splited);
			//Delete 
			for(T entity : entites){
				delDao.delete(entity);
			}
			
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
			List<T> entites = delDao.getEntityByIds(splited);
			//Delete 
			for(T entity : entites){
				delDao.delete(entity);
			}
		}
		
	}
		
}
