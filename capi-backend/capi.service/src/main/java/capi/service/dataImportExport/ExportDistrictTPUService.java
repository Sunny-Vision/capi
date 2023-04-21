package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.entity.District;
import capi.entity.ImportExportTask;
import capi.entity.Tpu;

@Service("ExportDistrictTPUService")
public class ExportDistrictTPUService extends DataExportServiceBase{
	
	@Autowired
	private DistrictDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
		
	private static final String[] headers = new String []{
			"TPU Id", "TPU Code", "District Council", "District Id", "District Code"
			, "District Chinese Name", "District English Name", "District Coverage"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllDistrictResult();

		/**
		 * 3/ District Id
		 * 4/ District Code
		 * 5/ District ChineseName
		 * 6/ District EnglishName
		 * 7/ District Coverage
		 * 
		 * 0/ Tpu Id
		 * 1/ Tpu Code
		 * 2/ Tpu CouncilDistrict
		 **/
		
		int rowCnt = 1;
		while (results.next()){
			District code = (District)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			
			//For District
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(String.valueOf(code.getId()));
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(code.getCode());
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(code.getChineseName());
			SXSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(code.getEnglishName());
			SXSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(code.getCoverage());
			
			if(!code.getTpus().isEmpty()){
				Iterator<Tpu> tpuList = code.getTpus().iterator();
				while (tpuList.hasNext()){
					Tpu tpu = tpuList.next();
										
					//For TPU
					SXSSFCell cell = row.createCell(0);
					cell.setCellValue(String.valueOf(tpu.getId()));
					SXSSFCell cell1 = row.createCell(1);
					cell1.setCellValue(tpu.getCode());
					SXSSFCell cell2 = row.createCell(2);
					cell2.setCellValue(tpu.getCouncilDistrict());
					dao.evit(tpu);
				} 
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
