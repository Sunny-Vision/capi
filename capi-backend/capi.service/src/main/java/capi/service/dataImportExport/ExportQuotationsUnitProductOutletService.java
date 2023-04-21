package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationDao;
import capi.entity.ImportExportTask;
import capi.model.dataImportExport.ExportQuotationsUnitProductOutletList;
import capi.service.CommonService;

@Service("ExportQuotationsUnitProductOutletService")
public class ExportQuotationsUnitProductOutletService extends DataExportServiceBase{
	
	@Autowired
	private QuotationDao dao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = new String[]{
			"Quotation ID", "Purpose", "Variety Code", "CPI Base Period", "Variety English Name"
			, "Variety Chinese Name", "Outlet Code", 
			//"Outlet Name", 
			"Product ID", "Last Product Change Date"
			, "Batch Code", "CPI Compilation Series"
			, "CPI Form Type", "Quotation Loading", "Is ICP", "ICP Product Code", "ICP Product Name", "ICP Type"
			, "Old Form Bar Serial", "Old Form Sequence", "Status", "No. of month of keeping previous prices", "RUADate", "Is RUA opened for all District"
			, "Available District ID", "Available field officer ID"
			};
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 21;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		List<ExportQuotationsUnitProductOutletList> results = dao.getAllQuotation();
		
		/**
		 * 0/QuotationId
		 * 1/CPICompilationSeries
		 * 2/isICP
		 * 3/ICPProductCode 
		 * 
		 * 4/Unit.Code
		 * 5/Unit.CPIQuotationType 
		 * 
		 * 6/Batch.Code
		 * 
		 * 7/Product.ProductId
		 * 
		 * 8/Outlet.OutletId
		 * 
		 * Table:
		 * 	Main: Quotation
		 * 	Foreign: Unit
		 * 			 Batch
		 * 			 Product
		 * 			 Outlet
		 **/
		
		int rowCnt = 1;
		for(ExportQuotationsUnitProductOutletList code : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(code.getQuotationId()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getPurposeCode());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitCode());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiBasePeriod());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitEnglishName());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitChineseName());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			if(code.getFirmCode()!=null){
				cell.setCellValue(String.valueOf(code.getFirmCode()));
			}
			
//			cellCnt = 7;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(code.getOutletName());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			if(code.getProductId()!=null){
				cell.setCellValue(String.valueOf(code.getProductId()));
			}
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getLastProductChangeDate()));
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getBatchCode());
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiCompilationSeries());
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getFormType());
			
			cellCnt = 12;
			cell = row.createCell(cellCnt);
			if(code.getQuotationLoading()!=null)
				cell.setCellValue(code.getQuotationLoading());
			
			cellCnt = 13;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isICP());
			
			cellCnt = 14;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpProductCode());
			
			cellCnt = 15;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpProductName());
			
			cellCnt = 16;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpType());
			
			cellCnt = 17;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOldFormBarSerial());
			
			cellCnt = 18;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOldFormSequence());
			
			cellCnt = 19;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getQuotationStatus());
			
			cellCnt = 20;
			cell = row.createCell(cellCnt);
			if(code.getKeepNoMonth()!=null){
				cell.setCellValue(String.valueOf(code.getKeepNoMonth()));
			}
			
			cellCnt = 21;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getRuaDate()));
			
			cellCnt = 22;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isRUAAllDistrict());
			
			cellCnt = 23;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getDistrictCode());
			
			cellCnt = 24;
			cell = row.createCell(cellCnt);
			List<String> staffCode = dao.getRUAUserStaffCodeByQuotationId(code.getQuotationId());
			if (staffCode != null && staffCode.size()>0){
				cell.setCellValue(StringUtils.join(staffCode, ';'));
			}
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}

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
