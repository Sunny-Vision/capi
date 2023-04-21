package capi.service.dataImportExport;

import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import capi.service.AppConfigService;

public abstract class DataExportServiceBase implements DataExportService{

	@Autowired
	private AppConfigService config;

	public SXSSFWorkbook prepareWorkbook() throws InvalidFormatException, IOException{
		String fileBase = getFileBase();
		File dir = new File(fileBase);
		if (!dir.exists()){
			dir.mkdirs();
		}
		SXSSFWorkbook workBook = new SXSSFWorkbook();		
		SXSSFSheet sheet = workBook.createSheet();
		SXSSFRow row = sheet.createRow(0);
		createHeader(row);
		return workBook;
	}
	

	public abstract void createHeader(SXSSFRow row);
		
	
	public String getFileBase(){
		return config.getExportFileLoc()+getFileRelativeBase();
	}
	
	public String getFileRelativeBase(){
		return "/"+String.valueOf(getTaskNo());
	}
}
