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
import capi.dal.UOMCategoryDao;
import capi.dal.UomDao;
import capi.entity.ImportExportTask;
import capi.entity.UOMCategory;
import capi.entity.Uom;

@Service("ImportUOMCategoryUomService")
public class ImportUOMCategoryUomService extends DataImportServiceBase{

	@Autowired
	private UOMCategoryDao dao;
	@Autowired
	private UomDao uomDao;
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		List<Integer> uomIds = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();		
		
			String [] values = getStringValues(row, 5);
			//For UOMCategory
			UOMCategory uomCategory = fillEntity(values, rowCnt);
			dao.save(uomCategory);
			if (uomCategory.getId() != null){
				ids.add(uomCategory.getId());
			}			
			
			//For uom
			Uom uom = fillEntityForUom(values, rowCnt, uomCategory);
			if( uom != null){
				uomDao.save(uom);
				if (uom.getId() != null){
					uomIds.add(uom.getId());
				}		
			}
				
			if (rowCnt % 20 == 0){
				uomDao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		
		List<Integer> existingUom = uomDao.getExistingUomId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingUom, uomIds));
		
		deleteEntities(notExisting, uomDao);
		
		
		List<Integer> existingUOMCategory = dao.getExistingUOMCategoryId();
		List<Integer> notExistingUOMCategory = new ArrayList<Integer>(CollectionUtils.subtract(existingUOMCategory, ids));
		
		deleteEntities(notExistingUOMCategory, dao);
		
		dao.flush();		
		workbook.close();
	}
	
	private UOMCategory fillEntity(String[] values, int rowCnt) throws Exception{

		UOMCategory entity = null;
		int col = 0;
		try{
			String idStr = values[3];
			
			if (StringUtils.isEmpty(idStr)){
				entity = new UOMCategory();
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: UOM Category Code="+id);
				}
			}
			col = 4;
			String desc = values[4];
			UOMCategory uomCategory = dao.getUOMCategoryByDescription(desc);
			if(entity.getUomCategoryId()!=null && uomCategory!=null && !uomCategory.getUomCategoryId().equals(entity.getUomCategoryId()))
				throw new RuntimeException("UOM Category Description already existed: "+desc);
			if(entity.getUomCategoryId()==null && uomCategory!=null){
				entity = uomCategory;
			}
			
			entity.setDescription(desc);
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
	}

	
	private Uom fillEntityForUom(String[] values, int rowCnt,UOMCategory uomCategory) throws Exception{
		
		Uom children = null;
		int col = 0;
		try{			
			String uomIdStr = values[0];
			if (StringUtils.isEmpty(uomIdStr)){
				if (!(StringUtils.isEmpty(values[1]) && StringUtils.isEmpty(values[2]))){
					children = new Uom();
				} else {
					return children;
				}
			} else {		
				int id = (int) Double.parseDouble(uomIdStr);
				children = uomDao.findById(id);
				if(children == null){
					throw new RuntimeException("Record not found: Uom Code="+id);
				}
			}
			
			col = 1;
			String chiName = values[1];
//			Uom uom = uomDao.getUOMByChineseName(chiName);
//			if(uom != null && !uom.getUomId().equals(children.getUomId()))
//				throw new RuntimeException("UOM Chinese Name already existed: "+chiName);
			children.setChineseName(chiName);
			
			col = 2;
			String engName = values[2];
//			uom = uomDao.getUOMByEnglishName(engName);
//			if(uom != null && !uom.getUomId().equals(children.getUomId()))
//				throw new RuntimeException("UOM English Name already existed: "+engName);
			
			children.setEnglishName(engName);
			
			children.setUomCategory(uomCategory);
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return children;
	}
	
}
