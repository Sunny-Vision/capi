package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.OutletDao;
import capi.entity.ImportExportTask;
import capi.entity.Outlet;

@Service("ExportOutletCodeAndNameService")
public class ExportOutletNameService extends DataExportServiceBase {
	@Autowired
	private OutletDao dao;

	@Autowired
	private ImportExportTaskDao taskDao;


	private static final String[] headers = new String[] { "Outlet Code", "Outlet Name"};

	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 34;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllOutletResult();

		int rowCnt = 1;
		while (results.next()) {
			Outlet code = (Outlet) results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;

			// For Outlet
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(code.getFirmCode()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getName());
			
			dao.evit(code);
			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
		}

		results.close();

		try {
			String filename = UUID.randomUUID().toString() + ".xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();

			ImportExportTask task = taskDao.findById(taskId);
			task.setFilePath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		taskDao.flush();
	}

	@Override
	public void createHeader(SXSSFRow row) {
		int cnt = 0;
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
}
