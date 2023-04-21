package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import capi.dal.OutletDao;
import capi.entity.District;
import capi.entity.ImportExportTask;
import capi.entity.Outlet;
import capi.entity.Quotation;
import capi.entity.Tpu;
import capi.entity.VwOutletTypeShortForm;
import capi.service.CommonService;

@Service("ExportOutletService")
public class ExportOutletService extends DataExportServiceBase{
	
	@Autowired
	private OutletDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String[]{
			"Outlet Id", "Outlet Code"
//			, "Outlet Name"
			, "Outlet Type", "BR Code"
			, "District Code", "District English Name", "District Council", "Tpu Code", "Market / Non-market"
			, "Indoor Market Name", "Collection Method", "Detail Address", "Street Address"
			, "Latitude", "Longitude", "Default Major Location", "Main Contact", "Last Contact"
			, "Telephone No", "Fax", "Web Site", "Opening Start Time", "Opening End Time"
			, "Opening Start Time2", "Opening End Time2", "Converient Start Time", "Convenient End Time", "Converient Start Time2"
			, "Convenient End Time2", "Remarks", "Discount Remarks", "Use FR(Admin)", "Status", "CreatedDate"
			, "CreatedBy", "Active Outlet"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
				
		ScrollableResults results = dao.getAllOutletResult();
		
		int rowCnt = 1;
		while(results.next()){
			Outlet code = (Outlet)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			//For Outlet
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(code.getId()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(String.valueOf(code.getFirmCode()));
//			cell = row.createCell(cellCnt++);
//			cell.setCellValue(code.getName());
			// 3 Outlet Type
//			cellCnt = 4;
			cellCnt = 3;
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getBrCode());
			// 5 District
			// 6 District
			// 7 Tpu
			// 8 Tpu
//			cellCnt = 9;
			cellCnt = 8;
			cell = row.createCell(cellCnt++);
			if(code.getOutletMarketType()!=null){
				cell.setCellValue(String.valueOf(code.getOutletMarketType()));
			}
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getIndoorMarketName());
			cell = row.createCell(cellCnt++);
			if(code.getCollectionMethod()!=null){
				cell.setCellValue(String.valueOf(code.getCollectionMethod()));
			}
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getDetailAddress());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getStreetAddress());
			//For Latitude
			cell = row.createCell(cellCnt++);
			if(!StringUtils.isEmpty(code.getLatitude())){
				String latitude = code.getLatitude();
				cell.setCellValue(latitude);
				
			}
			//For Longitude
			cell = row.createCell(cellCnt++);
			if(!StringUtils.isEmpty(code.getLongitude())){
				String longitude = code.getLongitude();
				cell.setCellValue(longitude);
				
			}
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getMarketName());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getMainContact());
			cell = row.createCell(cellCnt++); 
			cell.setCellValue(code.getLastContact());
			cell = row.createCell(cellCnt++);  
			cell.setCellValue(code.getTel());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getFax());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getWebSite());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatTime(code.getOpeningStartTime()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatTime(code.getOpeningEndTime()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatTime(code.getOpeningStartTime2()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatTime(code.getOpeningEndTime2()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatTime(code.getConvenientStartTime()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatTime(code.getConvenientEndTime()));
			cell = row.createCell(cellCnt++); 
			cell.setCellValue(commonService.formatTime(code.getConvenientStartTime2()));
			cell = row.createCell(cellCnt++);  
			cell.setCellValue(commonService.formatTime(code.getConvenientEndTime2()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getRemark());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getDiscountRemark());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.isUseFRAdmin());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getStatus());
			cell = row.createCell(cellCnt++);
			cell.setCellValue(commonService.formatDateTime(code.getCreatedDate()));
			cell = row.createCell(cellCnt++);
			cell.setCellValue(code.getCreatedBy());
			//Added column Active Outlet
			cell = row.createCell(cellCnt++);
			String activeOutlet; 
			if(!code.getQuotations().isEmpty()){ 
				activeOutlet = "Y";
			} else { 
				activeOutlet = "N"; 
			}
			cell.setCellValue(activeOutlet);
			
			//For Tpu
			if(code.getTpu()!=null){
				Tpu tpu = code.getTpu();
//				cellCnt = 7;
				cellCnt = 6;
				cell = row.createCell(cellCnt++);
				cell.setCellValue(tpu.getCouncilDistrict());
				cell = row.createCell(cellCnt++);
				cell.setCellValue(tpu.getCode());
				
				//For District
				if(tpu.getDistrict()!=null){
					District district = tpu.getDistrict();
//					cellCnt = 5;
					cellCnt = 4;
					cell = row.createCell(cellCnt++);
					cell.setCellValue(district.getCode());
					cell = row.createCell(cellCnt++);
					cell.setCellValue(district.getEnglishName());
					dao.evit(district);
				}
				dao.evit(tpu);
			}
			
			//For OutletType
			if(!code.getOutletTypes().isEmpty()){
				Iterator<VwOutletTypeShortForm> outletTypes = code.getOutletTypes().iterator();
				List<String> codeList = new ArrayList<String>();
				while (outletTypes.hasNext()){
					VwOutletTypeShortForm outletType = outletTypes.next();
					codeList.add(outletType.getShortCode());
					dao.evit(outletType);
				}
				
				String outletTypeCode = StringUtils.join(codeList, ';');
//				cell = row.createCell(3);
				cell = row.createCell(2);
				cell.setCellValue(outletTypeCode);
			}
			 
				
				/*Iterator<Quotation> quotationId = code.getQuotations().iterator();
				List<Integer> codeList = new ArrayList<Integer>();
				while (quotationId.hasNext()){
					Quotation quotationIds = quotationId.next();
					codeList.add(quotationIds.getQuotationId());
					dao.evit(quotationIds);
				}
				
				String activeOutlet;
				if (!codeList.isEmpty()) {
					activeOutlet = "Y";
				} else {
					activeOutlet = "N";
				}
//				cell = row.createCell(3);
				cell = row.createCell(37);
				cell.setCellValue(activeOutlet);*/
			
			
			
			
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
