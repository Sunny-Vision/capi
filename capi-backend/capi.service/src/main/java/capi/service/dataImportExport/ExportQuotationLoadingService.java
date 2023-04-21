package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationLoadingDao;
import capi.entity.District;
import capi.entity.ImportExportTask;
import capi.entity.QuotationLoading;
import capi.entity.VwOutletTypeShortForm;

@Service("ExportQuotationLoadingService")
public class ExportQuotationLoadingService extends DataExportServiceBase{
	@Autowired
	private QuotationLoadingDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = new String [] {"Quotation Loading Id", "District Code", "Outlet Type", "Quotation Per Man Day"};
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 23;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		ScrollableResults results =dao.getAllQuotationLoadingResult();
		
		/**
		 *	0/QuotationLoadingId
		 *	1/QuotationPerManDay
		 *	2/District Code
		 *	3/Outlet Type
		 **/
		
		int rowCnt = 1;
		while(results.next()){
			QuotationLoading code = (QuotationLoading)results.get()[0];
			
			Set<VwOutletTypeShortForm> outletTypes = code.getOutletTypes();
			List<String> codeList = new ArrayList<String>();
			for(VwOutletTypeShortForm outletType : outletTypes){
				codeList.add(outletType.getShortCode());
			}
			String outletTypeCode = StringUtils.join(codeList, ';');
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			int cellCnt = 0;
			
			//For QuotationLoading
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(code.getId()));
			
			//For District
			District district = code.getDistrict();
			cell = row.createCell(cellCnt++);
			cell.setCellValue(district.getCode());
			
			//For OutletTypeQuoatationLoading
			cell = row.createCell(cellCnt++);
			cell.setCellValue(outletTypeCode);
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(code.getQuotationPerManDay()));
			
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
}