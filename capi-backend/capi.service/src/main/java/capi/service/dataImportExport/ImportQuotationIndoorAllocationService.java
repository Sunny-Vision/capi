package capi.service.dataImportExport;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationDao;
import capi.entity.ImportExportTask;
import capi.entity.Quotation;

@Service("ImportQuotationIndoorAllocationService")
public class ImportQuotationIndoorAllocationService extends DataImportServiceBase{

	@Autowired
	private QuotationDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 19;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();
			Quotation quotation = fillEntity(row, rowCnt);
			dao.save(quotation);
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		dao.flush();
		workbook.close();
	}

	private Quotation fillEntity(Row row, int rowCnt) throws Exception{
		Quotation entity = null;
		int columnNo = 0;
		try{
//			String [] values = getStringValues(row, 16);
			String [] values = getStringValues(row, 15);
			String idStr = values[0];
			columnNo = 0;
			if (StringUtils.isEmpty(idStr)) {
				throw new RuntimeException("Quotation Id is empty");
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: id="+id);
				}
//				String indoorAllocationCode = values[15];
//				columnNo = 15;

				String indoorAllocationCode = values[14];
				columnNo = 14;
				entity.setIndoorAllocationCode(indoorAllocationCode);
			}
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(columnNo+1)+")");
		}
		return entity;
	}

}
