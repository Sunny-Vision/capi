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

import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.TpuDao;
import capi.entity.District;
import capi.entity.ImportExportTask;
import capi.entity.Tpu;

@Service("ImportDistrictTPUService")
public class ImportDistrictTPUService extends DataImportServiceBase{

	@Autowired
	private DistrictDao dao;
	@Autowired
	private TpuDao tpuDao;
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> tpuIds = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();		
			String [] values = getStringValues(row, 8);
			
			//For District
			District district = fillEntity(values, rowCnt);
			dao.save(district);
			if (district.getId() != null){
				ids.add(district.getId());
			}			
			
			//For Tpu
			Tpu tpu = fillEntityForTpu(values, rowCnt, district);
			if( tpu != null){
				tpuDao.save(tpu);
				if (tpu.getId() != null){
					tpuIds.add(tpu.getId());
				}		
			}
				
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
//		List<Tpu> tpus = tpuDao.getNotExistedTpu(tpuIds);
//		for (Tpu tpu : tpus){
//			tpuDao.delete(tpu);
//		}	
		
		List<Integer> existingTpu = tpuDao.getExistingTpuId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingTpu, tpuIds));
		
		deleteEntities(notExisting, tpuDao);
		
		List<Integer> existingDistrict = dao.getExistingDistrictId();
		List<Integer> notExistingDistrict = new ArrayList<Integer>(CollectionUtils.subtract(existingDistrict, ids));
		
		deleteEntities(notExistingDistrict, dao);
		
		dao.flush();
		
		workbook.close();
	}
	
	private District fillEntity(String [] values, int rowCnt) throws Exception{

		District entity = null;
		int col = 0;
		try{
			col = 3;
			
			String idStr = values[3];
			if (StringUtils.isEmpty(idStr)){
				entity = new District();
			}
			else{
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: District id="+id);
				}
			}
			
			col = 4;			
			if (StringUtils.isEmpty(values[4])){
				throw new RuntimeException("District Code cannot be empty");
			}
			String code = values[4].trim();
			
			District district = dao.getDistrictByCode(code);
			if (entity.getId() != null && district != null && !entity.getId().equals(district.getId())){
				throw new RuntimeException("District Code already existed: "+code);
			}
			if(entity.getId()==null && district != null){
				entity = district;
			}
			entity.setCode(code);
			
			col = 5;
			String chiName = values[5];
			entity.setChineseName(chiName);
			
			col = 6;
			String engName = values[6];
			entity.setEnglishName(engName);	
			
			col = 7;
			String coverage = values[7];
			entity.setCoverage(coverage);
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
	}

	
	private Tpu fillEntityForTpu(String [] values, int rowCnt, District district) throws Exception{
		
		Tpu children = null;
		
		int col = 0;		
		try{			
			col = 0;
			String tpuIdStr = values[0];
			if (StringUtils.isEmpty(tpuIdStr)){
				children = new Tpu();
			}
			else{		
				int id = (int)Double.parseDouble(tpuIdStr);
				children = tpuDao.findById(id);
				if(children == null){
					throw new RuntimeException("Record not found: Tpu id="+id);
				}
			}
			
			col = 1;
			String tpuCode = values[1];
			if (tpuCode != null){
				tpuCode = tpuCode.trim();
			}
			
			if (StringUtils.isEmpty(tpuCode)){
				throw new RuntimeException("Tpu Code cannot be empty");
			}
			
			Tpu tpu = tpuDao.getTpuByCode(tpuCode);
			if (children.getId() != null && tpu != null && !children.getId().equals(tpu.getId())){
				throw new RuntimeException("Tpu Code already existed: "+tpuCode);
			}
			if(children.getId() == null && tpu != null){
				children = tpu;
			}
			children.setCode(tpuCode);
			
			//col = 6;
			//String desc = values[6];
			
			col = 2;
			String council = values[2];
			
			//children.setDescription(desc);
			children.setCouncilDistrict(council);
			children.setDistrict(district);
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return children;
	}
}
