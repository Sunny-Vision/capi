package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.PriceReasonDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.ImportExportTask;
import capi.entity.PriceReason;
import capi.entity.VwOutletTypeShortForm;

@Service("ImportPriceReasonService")
public class ImportPriceReasonService extends DataImportServiceBase{

	@Autowired
	private PriceReasonDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 4;
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
			PriceReason code = fillEntity(row, rowCnt);			
			dao.save(code);

			if (code.getPriceReasonId()!= null){
				ids.add(code.getPriceReasonId());
			}			
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
//		List<PriceReason> codes = dao.getNotExistedPriceReason(ids);
//		for (PriceReason code : codes){
//			code.getOutletTypes().clear();
//			dao.delete(code);
//		}
		
		List<Integer> existingPriceReason = dao.getExistingPriceReasonId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingPriceReason, ids));
		
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
			List<PriceReason> priceReasons = dao.getByIds(splited);
			//Delete PriceReason
			for(PriceReason priceReason : priceReasons){
				if(priceReason.getOutletTypes()!=null){
					priceReason.getOutletTypes().clear();
				}
				dao.delete(priceReason);
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
			List<PriceReason> priceReasons = dao.getByIds(splited);
			//Delete PriceReason
			for(PriceReason priceReason : priceReasons){
				if(priceReason.getOutletTypes()!=null){
					priceReason.getOutletTypes().clear();
				}
				dao.delete(priceReason);
			}
		}
		
		dao.flush();
		workbook.close();
	}
	
	private PriceReason fillEntity(Row row, int rowCnt) throws Exception{
		PriceReason entity = null;
		
		int col = 0;
		try{
			String [] values = getStringValues(row, 6);
			String idStr = values[0];
			if (StringUtils.isEmpty(idStr)){
				entity = new PriceReason();
			}
			else{
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: id="+id);
				}
			}
			
			col = 1;
			String desc = values[1];
			entity.setDescription(desc);
			
			/**
			 * 1- Price
			 * 2-Discount
			 */
			col = 2;
			String typeStr = values[2];
			if (StringUtils.isEmpty(typeStr)){
				throw new RuntimeException("Reason Type cannot be empty");
			}
			int type = (int)Double.parseDouble(typeStr);
			if ( !(type == 1 || type ==2 )){
				throw new RuntimeException("Please enter either 1 or 2 for Reason Type (1- Price, 2-Discount)");
			}
			entity.setReasonType(type);
			
			col = 3;
			String seqStr = values[3];
			if (StringUtils.isEmpty(seqStr)){
				throw new RuntimeException("Sequence cannot be empty");
			}
			int seq = (int)Double.parseDouble(seqStr);
			entity.setSequence(seq);
			
			col = 4;
			String isAllOutletTypeStr = values[4];
			if (!StringUtils.isEmpty(typeStr)){
				boolean isAllOutletType = Boolean.parseBoolean(isAllOutletTypeStr);
				entity.setAllOutletType(isAllOutletType);
				entity.getOutletTypes().clear();
			}
			else{
				entity.setAllOutletType(false);
			}
			
			col = 5;
			updateOutletType(values[5], entity);
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
	}

	
	private void updateOutletType(String codes, PriceReason priceReason) throws Exception{		
		
		if (codes != null && !StringUtils.isEmpty(codes)){
			List<String> newOutletTypeIds = Arrays.asList(codes.split("\\s*;\\s*"));
			
			ArrayList<String> oldOutletTypeIds = new ArrayList<String>();
			for (VwOutletTypeShortForm outletType : priceReason.getOutletTypes()) {
				oldOutletTypeIds.add(outletType.getId());
			}
			
			Collection<String> deletedIds = (Collection<String>)CollectionUtils.subtract(oldOutletTypeIds, newOutletTypeIds);
			Collection<String> newIds = (Collection<String>)CollectionUtils.subtract(newOutletTypeIds, oldOutletTypeIds);
				
			if (deletedIds.size() > 0){
				List<VwOutletTypeShortForm> deletedOutletTypes = outletTypeDao.getByIds(deletedIds.toArray(new String[0]));
				for (VwOutletTypeShortForm outletType: deletedOutletTypes){
					priceReason.getOutletTypes().remove(outletType);
				}
			}			
			
			if (newIds.size() > 0) {
				List<VwOutletTypeShortForm> newOutletTypes = outletTypeDao.getByIds(newIds.toArray(new String[0]));
				priceReason.getOutletTypes().addAll(newOutletTypes);
			}
		} else {
			if (priceReason.isAllOutletType()){
				priceReason.getOutletTypes().clear();
			}
			else{
				throw new RuntimeException("Outlet Types cannot be empty");				
			}
		}
	}
	
}
