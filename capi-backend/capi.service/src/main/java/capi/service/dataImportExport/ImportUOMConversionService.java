package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.UOMConversionDao;
import capi.dal.UomDao;
import capi.entity.ImportExportTask;
import capi.entity.UOMConversion;
import capi.entity.Uom;

@Service("ImportUOMConversionService")
public class ImportUOMConversionService extends DataImportServiceBase{

	@Autowired
	private UOMConversionDao dao;
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();			
			UOMConversion entity = fillEntity(row, rowCnt);
			dao.save(entity);
			if (entity.getId() != null){
				ids.add(entity.getId());
			}			
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
//		List<UOMConversion> uomConversions = dao.getNotExistedUOMConversion(ids);
//		for (UOMConversion uomConversion : uomConversions){
//			dao.delete(uomConversion);
//		}
		
		List<Integer> existingUOMConversion = dao.getExistingUOMConversionId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingUOMConversion, ids));
		
		deleteEntities(notExisting, dao);

		dao.flush();
		workbook.close();
	}
	
	private UOMConversion fillEntity(Row row, int rowCnt) throws Exception{
		UOMConversion entity = null;
		Uom baseUom = null;
		Uom targetUom = null;
		
		int col = 0;
		try{
			String [] values = getStringValues(row, 8);
			String idStr = values[0];
			if (StringUtils.isEmpty(idStr)){
				entity = new UOMConversion();
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: id="+id);
				}
			}
			
			col = 1;
			String baseUomidStr = values[1];
			if (StringUtils.isEmpty(baseUomidStr)){
				throw new RuntimeException("Base UOM Id cannot be empty");
			} else {
				int id = (int)Double.parseDouble(baseUomidStr);
				baseUom = uomDao.findById(id);
				if(baseUom == null){
					throw new RuntimeException("Base UOM Record not found: id="+id);
				}
			}
			
//			col = 2;
//			String baseUomChiName = values[2];
//			if (baseUomChiName != null){
//				baseUomChiName.trim();
//			}		
//			baseUom.setChineseName(baseUomChiName);
//			
//			col = 3;
//			String baseUomEngName = values[3];
//			if (baseUomEngName != null){
//				baseUomEngName.trim();
//			}		
//			baseUom.setEnglishName(baseUomEngName);
			entity.setBaseUOM(baseUom);
			
			col = 4;
			String targetUomidStr = values[4];
			if (StringUtils.isEmpty(targetUomidStr)){
				throw new RuntimeException("Target UOM Id cannot be empty");
			} else {
				int id = (int)Double.parseDouble(targetUomidStr);
				targetUom = uomDao.findById(id);
				if(targetUom == null){
					throw new RuntimeException("Target UOM Record not found: id="+id);
				}
			}
			
//			col = 5;
//			String targetUomChiName = values[5];
//			if (targetUomChiName != null){
//				targetUomChiName.trim();
//			}		
//			targetUom.setChineseName(targetUomChiName);
//			
//			col = 6;
//			String targetUomEngName = values[6];
//			if (targetUomEngName != null){
//				targetUomEngName.trim();
//			}		
//			targetUom.setEnglishName(targetUomEngName);
			entity.setTargetUOM(targetUom);
			
			col = 7;
			String factorStr = values[7];
			if (StringUtils.isEmpty(factorStr)){
				throw new RuntimeException("Conversion Factor cannot be empty");
			} else {		
				double factor = Double.parseDouble(factorStr);
				
				if (factor < 0){
					throw new RuntimeException("Invalid Conversion Factor");
				}
				
				entity.setFactor(factor);			
			}
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		uomDao.save(baseUom);
		uomDao.save(targetUom);
		return entity;
	}

}
