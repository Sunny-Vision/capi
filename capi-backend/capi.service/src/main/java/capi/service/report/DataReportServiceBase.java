package capi.service.report;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import capi.service.AppConfigService;

public abstract class DataReportServiceBase implements ReportService{

	@Autowired
	private AppConfigService appConfig;
	
	public String serializeObject(Object obj) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	public <T> T deserializeObject(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		return  mapper.readValue(str, clazz);
	}
	
	public abstract void createHeader(SXSSFRow row);
	
	public String getFileBase(){
		return appConfig.getReportLocation()+getFileRelativeBase();
	}
	
	public String getFileRelativeBase(){
		return "/"+String.valueOf(getFunctionCode());
	}
	
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
}
