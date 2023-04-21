package capi.service.dataImportExport;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.entity.ImportExportTask;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.service.CommonService;
import capi.service.assignmentManagement.DataConversionService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("ImportQuotationFRRelatedService")
public class ImportQuotationFRRelatedService extends DataImportServiceBase{

	@Autowired
	private QuotationDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
	@Autowired
	private DataConversionService dataConversionService;
	@Autowired
	private QuotationRecordDao quotationRecodDao;
	@Autowired
	private CommonService commonService;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		if(task.getSurveyMonth().getSurveyMonthId()!=null)
		System.out.println(task.getSurveyMonth().getSurveyMonthId());
		
		SurveyMonth surveyMonth = task.getSurveyMonth();
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();
			String [] values = getStringValues(row, 27);
			Quotation quotation = fillEntity(values, rowCnt, surveyMonth);
			dao.save(quotation);
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		dao.flush();
		workbook.close();
	}

	private Quotation fillEntity(String[] values, int rowCnt, SurveyMonth surveyMonth) throws Exception{
		Quotation entity = null;
		int col = 0;
		try{
			String idStr = values[0];
			if (StringUtils.isEmpty(idStr)) {
				throw new RuntimeException("Quotation Id is empty");
			}
			
			int id = (int)Double.parseDouble(idStr);
			entity = dao.findById(id);
			if(entity == null){
				throw new RuntimeException("Record not found: id="+id);
			}
			
//			col = 16;
//			if(!StringUtils.isEmpty(values[17])){
//				String date = values[16].trim();
//				entity.setSeasonalWithdrawal(commonService.getDate(date));
//			} else {
//				entity.setSeasonalWithdrawal(null);
//			}
			
//			col = 17;
			col = 16;
			String isReturnGoods = values[col];
			entity.setReturnGoods(Boolean.parseBoolean(isReturnGoods));
			
//			col = 18;
			col = 17;
			String isReturnNewGoods = values[col];
			entity.setReturnNewGoods(Boolean.parseBoolean(isReturnNewGoods));
			
//			col = 19;
			col = 18;
			String isLastSeasonReturnGoods = values[col];
			entity.setLastSeasonReturnGoods(Boolean.parseBoolean(isLastSeasonReturnGoods));
			
//			col = 20;
			col = 19;
			String isFRApplied = values[col];
			entity.setTempIsFRApplied(Boolean.parseBoolean(isFRApplied));
//			entity.setFRApplied(Boolean.parseBoolean(isFRApplied));
			
//			col = 21;
			col = 20;
			if(!StringUtils.isEmpty(values[col])){
				String isUseFRAdmin = values[col];
				entity.setUseFRAdmin(Boolean.parseBoolean(isUseFRAdmin));
			} else {
				entity.setUseFRAdmin(null);
			}
			
//			col = 22;
			col = 21;
			if(!StringUtils.isEmpty(values[col])){
				Double usedFRValue = Double.parseDouble(values[col]);
				entity.setUsedFRValue(usedFRValue);
			} else {
				entity.setUsedFRValue(null);
			}
			
//			col = 23;
			col = 22;
			if(!StringUtils.isEmpty(values[col])){
				String isUsedFRPercentage = values[col];
				entity.setIsUsedFRPercentage(Boolean.parseBoolean(isUsedFRPercentage));
			} else {
				entity.setIsUsedFRPercentage(null);
			}
			
			// 24
			// Last FR Applied Date (no need import)
//			col = 24;
			col = 23;
			if(!StringUtils.isEmpty(values[col])){
				String date = values[col].trim();
				entity.setLastFRAppliedDate(commonService.getDate(date));
			} else {
				entity.setLastFRAppliedDate(null);
			}
			
//			col = 25;
			col = 24;
			if(!StringUtils.isEmpty(values[col])){
				Double frAdmin = Double.parseDouble(values[col]);
				entity.setFrAdmin(frAdmin);
			} else {
				entity.setFrAdmin(null);
			}
			
//			col = 26;
			col = 25;
			if(!StringUtils.isEmpty(values[col])){
				String isFRAdminPercentage = values[col];
				entity.setIsFRAdminPercentage(Boolean.parseBoolean(isFRAdminPercentage));
			} else {
				entity.setIsFRAdminPercentage(null);
			}
//			Quotation newEntity = new Quotation();
			//BeanUtils.copyProperties(entity, newEntity);
			
			
//            collectionExcelData(newEntity, entity);
			entity.setFRApplied(Boolean.parseBoolean(isFRApplied));
			
			//surveyMonth = Web filter (find quotationRecord)
			performDataConversion(entity, surveyMonth);
			
			
			//B135 - To avoid Import data reset (CAPI_UAT_Release_Notes_20180731)
			//col 17
			entity.setReturnGoods(Boolean.parseBoolean(isReturnGoods));

			//col 18
			entity.setReturnNewGoods(Boolean.parseBoolean(isReturnNewGoods));

			//col 20
			entity.setTempIsFRApplied(Boolean.parseBoolean(isFRApplied));
			//entity.setFRApplied(Boolean.parseBoolean(isFRApplied));

			//col 21;
//			if(!StringUtils.isEmpty(values[21])){
//				String isUseFRAdmin = values[21];
			if(!StringUtils.isEmpty(values[20])){
				String isUseFRAdmin = values[20];
				entity.setUseFRAdmin(Boolean.parseBoolean(isUseFRAdmin));
			} else {
				entity.setUseFRAdmin(null);
			}

			//col 24
//			if(!StringUtils.isEmpty(values[24])){
//				String date = values[24].trim();
			if(!StringUtils.isEmpty(values[23])){
				String date = values[23].trim();
				entity.setLastFRAppliedDate(commonService.getDate(date));
			} else {
				entity.setLastFRAppliedDate(null);
			}
			//End of B135
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
	}

	private void performDataConversion(Quotation quotation, SurveyMonth surveyMonth){
		List<QuotationRecord> quotationRecords = quotationRecodDao.getApprovedRecordBySurveyMonthQuotation(quotation, surveyMonth);
		if (quotationRecords != null && quotationRecords.size() > 0){
			Collections.sort(quotationRecords, new Comparator<QuotationRecord>(){
				@Override
				public int compare(QuotationRecord o1,
						QuotationRecord o2){
					// TODO Auto-generated method stub
					return o1.getReferenceDate().compareTo(o2.getReferenceDate());
				}
			});
			
			for (QuotationRecord record : quotationRecords){
				dataConversionService.convert(record);
//				dataConversionService.convertForFRReport(record);
				quotationRecodDao.flush();
			}
		}		
	}

}
