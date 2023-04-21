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
import capi.dal.PriceReasonDao;
import capi.entity.ImportExportTask;
import capi.entity.PriceReason;
import capi.entity.VwOutletTypeShortForm;

@Service("ExportPriceReasonService")
public class ExportPriceReasonService extends DataExportServiceBase{
	
	@Autowired
	private PriceReasonDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
		
	private static final String[] headers = new String []{
			"Price Reason Id", "Discount description", "Reason Type", "Sequence", "Is all outlet types"
			, "Outlet type"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 4;
	}

	/**
	 * 0/ Price Reason Id
	 * 1/ Discount description
	 * 2/ Reason Type
	 * 3/ Sequence
	 * 4/ Is all outlet types
	 * 
	 * 5/ Outlet Type
	 */
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllPriceReasonResult();

		
		int rowCnt = 1;
		while (results.next()){
			PriceReason entity = (PriceReason)results.get()[0];
			
			
			Set<VwOutletTypeShortForm> outletTypes = entity.getOutletTypes();
			List<String> codeList = new ArrayList<String>();
			for (VwOutletTypeShortForm outletType:outletTypes){
				codeList.add(outletType.getShortCode());
			}
			
			String outletTypeCode = StringUtils.join(codeList, ';');
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(entity.getId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(entity.getDescription());
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(entity.getReasonType());
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(entity.getSequence());
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(entity.isAllOutletType());
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(outletTypeCode);
			dao.evit(entity);
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
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

}
