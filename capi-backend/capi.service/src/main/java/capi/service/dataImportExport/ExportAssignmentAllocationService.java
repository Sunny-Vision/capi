package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.OutletDao;
import capi.entity.ImportExportTask;
import capi.entity.Outlet;
import capi.entity.VwOutletTypeShortForm;
import capi.model.dataImportExport.ExportAssignmentAllocationList;
import capi.service.CommonService;

@Service("ExportAssignmentAllocationService")
public class ExportAssignmentAllocationService extends DataExportServiceBase{
	
	@Autowired
	private AssignmentDao dao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String[]{
			"Assignment ID", "Survey", "Is Imported Assignments", "Reference Month", "Start Date"
			, "End Date", "Collection Date", "Outlet Code", 
			//"Outlet Name", 
			"Outlet Type"
			, "District Code", "TPU", "Address", "Collection Method", "Batch Code", "Market / Non-market"
			, "No. of Quotation", "Enumeration Status", "Is Completed", "Assigned Field Officer Staff Code"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 27;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		Date referenceMonth = task.getReferenceMonth();
		List<ExportAssignmentAllocationList> results = dao.getAllAssignmentResult(referenceMonth);
		
		/**
		 * 
		 **/
		
		int rowCnt = 1;
		for (ExportAssignmentAllocationList code : results){
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			int col = 0;
			
			SXSSFCell cell = row.createCell(col++);
			cell.setCellValue(String.valueOf(code.getAssignmentId()));
			
			cell = row.createCell(col++);
			cell.setCellValue(code.getSurvey());
			
			cell = row.createCell(col++);
			cell.setCellValue(code.isImportedAssignment());
			
			cell = row.createCell(col++);
			cell.setCellValue(commonService.formatMonth(code.getReferenceMonth()));
			
			cell = row.createCell(col++);
			cell.setCellValue(commonService.formatDate(code.getStartDate()));
			
			cell = row.createCell(col++);
			cell.setCellValue(commonService.formatDate(code.getEndDate()));
			
			cell = row.createCell(col++);
			cell.setCellValue(commonService.formatDate(code.getAssignedCollectionDate()));
			
			cell = row.createCell(col++);
			//if(code.getFirmCode()!=null)
			if(code.getReferencefirmCode() != null)
//				cell.setCellValue(String.valueOf(code.getFirmCode()));
				cell.setCellValue(String.valueOf(code.getReferencefirmCode()));
			
//			cell = row.createCell(col++);
//			cell.setCellValue(code.getOutletName());
			
			cell = row.createCell(col++);
			//For OutletType
			if(code.getOutletId()!=null){
				Outlet outlet = outletDao.findById(code.getOutletId());
				if(outlet.getOutletTypes()!=null&&outlet.getOutletTypes().size()>0){
					Set<VwOutletTypeShortForm> outletTypes = outlet.getOutletTypes();
					List<String> codeList = new ArrayList<String>();
					for(VwOutletTypeShortForm outletType : outletTypes){
						codeList.add(outletType.getShortCode());
					}
					String outletTypeCode = StringUtils.join(codeList, ';');
					cell.setCellValue(outletTypeCode);
				}
			}
			
			cell = row.createCell(col++);
			cell.setCellValue(code.getDistrictCode());
			
			cell = row.createCell(col++);
			cell.setCellValue(code.getTpuCode());
			
			cell = row.createCell(col++);
			cell.setCellValue(code.getAddress());
			
			cell = row.createCell(col++);
			if(code.getCollectionMethod()!=null)
				cell.setCellValue(String.valueOf(code.getCollectionMethod()));
			
			cell = row.createCell(col++);
			cell.setCellValue(code.getBatchCode());
			
			cell = row.createCell(col++);
			if(code.getOutletMarketType()!=null)
				cell.setCellValue(String.valueOf(code.getOutletMarketType()));
			
			cell = row.createCell(col++);
			cell.setCellValue(String.valueOf(code.getNumOfQuotation()));
			
			cell = row.createCell(col++);
			if(code.getFirmStatus()!=null)
				cell.setCellValue(String.valueOf(code.getFirmStatus()));
			
			cell = row.createCell(col++);
			cell.setCellValue(code.isCompleted());
			
			cell = row.createCell(col++);
			cell.setCellValue(code.getStaffCode());
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		//results.close();

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
}
