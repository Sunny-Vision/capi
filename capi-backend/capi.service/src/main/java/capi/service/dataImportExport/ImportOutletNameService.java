package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.OutletDao;
import capi.dal.TpuDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.ImportExportTask;
import capi.entity.Outlet;
import capi.entity.Tpu;
import capi.entity.VwOutletTypeShortForm;
import capi.model.GeoLocation;
import capi.service.AppConfigService;
import capi.service.CommonService;
import capi.service.masterMaintenance.OutletService;

@Service("ImportOutletNameService")
public class ImportOutletNameService extends DataImportServiceBase{

	@Autowired
	private OutletDao dao;
	
	@Autowired
	private TpuDao tpuDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 34;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
				
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); //remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String [] values = getStringValues(row, 36);
			Outlet outlet = fillEntity(values, rowCnt);
			
			dao.save(outlet);
			
			if(outlet.getId() != null){
				ids.add(outlet.getId());
			}
			
			if(rowCnt % 20==0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		List<Integer> existingOutlet = dao.getExistingOutletId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingOutlet, ids));
		
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = notExisting.size() / maxSize;
		int remainder = notExisting.size() % maxSize;
		
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		
		// Quotient
		for(int i = 0; i < times; i++) {
			splited = notExisting.subList(fromIdx, toIdx);
			List<Outlet> outlets = dao.getByIds(splited);
			//Delete Outlet
			for(Outlet outlet : outlets){
				outletService.deleteOutlet(outlet.getOutletId(), configService.getFileBaseLoc());
			}
			
			if(i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
		
		// Remainder
		if(times == 0) {
			if(remainder != 0) {
				splited = notExisting.subList(fromIdx, remainder);
			}
		} else {
			if(remainder != 0) {
				splited = notExisting.subList(toIdx, (toIdx + remainder));
			}
		}
		
		if(remainder != 0) {
			List<Outlet> outlets = dao.getByIds(splited);
			//Delete Outlet
			for(Outlet outlet : outlets){
				outletService.deleteOutlet(outlet.getOutletId(), configService.getFileBaseLoc());
			}
		}
		
		
		dao.flush();
		workbook.close();
	}
	
	public Outlet fillEntity(String[] values, int rowCnt) throws Exception{
		Outlet entity = null;
		int col = 0;
		try{
			String idStr = values[0];
			
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Outlet Code should not be empty");
			}
			int firmCode = (int)Double.parseDouble(values[col]);
			Outlet outlet = dao.getOutletbyFirmCode(firmCode);
			if(outlet != null && !outlet.getOutletId().equals(entity.getOutletId()))
				throw new RuntimeException("Outlet Code already existed: "+firmCode);
			entity.setFirmCode(firmCode);
			
			col = 1;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Outlet Name should be empty");
			}
			String name = values[col].trim();
			entity.setName(name);
			

		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
}
