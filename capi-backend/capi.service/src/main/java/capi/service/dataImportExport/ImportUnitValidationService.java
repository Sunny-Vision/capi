package capi.service.dataImportExport;

import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.OnSpotValidationDao;
import capi.dal.UnitDao;
import capi.entity.ImportExportTask;
import capi.entity.OnSpotValidation;
import capi.entity.Unit;

@Service("ImportUnitValidationService")
public class ImportUnitValidationService extends DataImportServiceBase{

	@Autowired
	private OnSpotValidationDao dao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 11;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();		
			String [] values = getStringValues(row, 36);
			
			OnSpotValidation validation = fillEntity(values, rowCnt);
			dao.save(validation);
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		dao.flush();
		workbook.close();
	}
	 
	private OnSpotValidation fillEntity(String[] values, int rowCnt) throws Exception{
		OnSpotValidation entity = null;
		int col = 0;
		try{
			String idStr = values[0];
			if (StringUtils.isEmpty(idStr)){
				throw new RuntimeException("Unit Id cannot be empty: row no.="+rowCnt);
			}
			else{
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
//				if(entity==null){
//					throw new RuntimeException("Validation not found: Variety Id = "+id);
//				}
				if(entity == null){
					entity = new OnSpotValidation();
					Unit unit = unitDao.findById(id);
					if(unit == null){
						throw new RuntimeException("Unit not found: Unit id="+id);
					} else {
						entity.setUnit(unit);
					}
				}
			}
						
			//Unit of measurement (UOM1) should be reported
			col = 4;
			String uom1 = values[4];
			entity.setUom1Reported(Boolean.parseBoolean(uom1));
			
			//Unit of measurement (UOM2) >= 0
			col = 5;
			String uom2 = values[5];
			entity.setUom2GreaterZero(Boolean.parseBoolean(uom2));
			
			//Normal Price (N Price) > 0
			col = 6;
			String nPriceGreaterZero = values[6];
			entity.setNPriceGreaterZero(Boolean.parseBoolean(nPriceGreaterZero));

			//Special Price (S Price) > 0
			col = 7;
			String sPriceGreaterZero = values[7];
			entity.setSPriceGreaterZero(Boolean.parseBoolean(sPriceGreaterZero));

			//Provide Reason if percentage change of N Price >
			col = 8;
			String provideReasonPRNPrice = values[8];
			entity.setProvideReasonPRNPrice(Boolean.parseBoolean(provideReasonPRNPrice));
			col = 9;
			String prNPriceThresholdStr = values[9];
			if (StringUtils.isEmpty(prNPriceThresholdStr)){
				entity.setPrNPriceThreshold(null);
			} else {
				Double prNPriceThreshold = Double.parseDouble(prNPriceThresholdStr);
				entity.setPrNPriceThreshold(prNPriceThreshold);
			}

			//Provide Reason if percentage change of N Price <
			col = 10;
			String provideReasonPRNPriceLower = values[10];
			entity.setProvideReasonPRNPriceLower(Boolean.parseBoolean(provideReasonPRNPriceLower));
			col = 11;
			String prNPriceLowerThresholdStr = values[11];
			if (StringUtils.isEmpty(prNPriceLowerThresholdStr)){
				entity.setPrNPriceLowerThreshold(null);
			} else {
				Double prNPriceLowerThreshold = Double.parseDouble(prNPriceLowerThresholdStr);
				entity.setPrNPriceLowerThreshold(prNPriceLowerThreshold);
			}
			
			//Provide Reason if percentage change of S Price >
			col = 12;
			String provideReasonPRSPrice = values[12];
			entity.setProvideReasonPRSPrice(Boolean.parseBoolean(provideReasonPRSPrice));
			col = 13;
			String prSPriceThresholdStr = values[13];
			if (StringUtils.isEmpty(prSPriceThresholdStr)){
				entity.setPrSPriceThreshold(null);
			} else {
				Double prSPriceThreshold = Double.parseDouble(prSPriceThresholdStr);
				entity.setPrSPriceThreshold(prSPriceThreshold);
			}
			
			//Provide Reason if percentage change of S Price <
			col = 14;
			String provideReasonPRSPriceLower = values[14];
			entity.setProvideReasonPRSPriceLower(Boolean.parseBoolean(provideReasonPRSPriceLower));
			col = 15;
			String prSPriceLowerThresholdStr = values[15];
			if (StringUtils.isEmpty(prSPriceLowerThresholdStr)){
				entity.setPrSPriceLowerThreshold(null);
			} else {
				Double prSPriceThreshold = Double.parseDouble(prSPriceLowerThresholdStr);
				entity.setPrSPriceLowerThreshold(prSPriceThreshold);
			}
			
			//Provide Reason if S price is > max or < min S price of the same Unit in the last month
			col = 16;
			String provideReasonSPriceMaxMin = values[16];
			entity.setProvideReasonSPriceMaxMin(Boolean.parseBoolean(provideReasonSPriceMaxMin));
			
			//Provide Reason if N price is > max or < min N price of the same Unit in the last month
			col = 17;
			String provideReasonNPriceMaxMin = values[17];
			entity.setProvideReasonNPriceMaxMin(Boolean.parseBoolean(provideReasonNPriceMaxMin));
			
			//Normal price (N Price) >= Special Price (S Price)
			col = 18;
			String nPriceGreaterSPrice = values[18];
			entity.setnPriceGreaterSPrice(Boolean.parseBoolean(nPriceGreaterSPrice));
			
			//If "Not suitable" is chosen for N and S price, remarks have to be provided.
			col = 19;
			String provideRemarkForNotSuitablePrice = values[19];
			entity.setProvideRemarkForNotSuitablePrice(Boolean.parseBoolean(provideRemarkForNotSuitablePrice));
			
			//17 If the quotation record is "Not available", no field except remarks should be filled.
			col = 20;
			String provideRemarkForNotAvailableQuotation = values[20];
			entity.setProvideRemarkForNotAvailableQuotation(Boolean.parseBoolean(provideRemarkForNotAvailableQuotation));
			
			//18 If the pricing cycle of a product is longer than the specified product cycle for different Units, reminder will be provided.
			col = 21;
			String reminderForPricingCycle = values[21];
			entity.setReminderForPricingCycle(Boolean.parseBoolean(reminderForPricingCycle));
			
			//19 Provide Reason if percentage change of S price exceed the ranges of (mean +/- S.D.) in last y month
			col = 22;
			String provideReasonPRSPriceSD = values[22];
			entity.setProvideReasonPRSPriceSD(Boolean.parseBoolean(provideReasonPRSPriceSD));
			col = 23;
			String prSPriceSDPositiveStr = values[23];
			if (StringUtils.isEmpty(prSPriceSDPositiveStr)){
				entity.setPrSPriceSDPositive(null);
			} else {
				Double prSPriceSDPositive = Double.parseDouble(prSPriceSDPositiveStr);
				entity.setPrSPriceSDPositive(prSPriceSDPositive);
			}
			col = 24;
			String prSPriceSDNegativeStr = values[24];
			if (StringUtils.isEmpty(prSPriceSDNegativeStr)){
				entity.setPrSPriceSDNegative(null);
			} else {
				Double prSPriceSDNegative = Double.parseDouble(prSPriceSDNegativeStr);
				entity.setPrSPriceSDNegative(prSPriceSDNegative);
			}
			col = 25;
			String prSPriceMonthStr = values[25];
			if (StringUtils.isEmpty(prSPriceMonthStr)){
				entity.setPrSPriceMonth(null);
			} else {
				Integer prSPriceMonth = (int) Double.parseDouble(prSPriceMonthStr);
				entity.setPrSPriceMonth(prSPriceMonth);
			}
			
			//Provide Reason if percentage change of N price exceed the ranges of (mean +/- S.D.) in last y month
			col = 26;
			String provideReasonPRNPriceSD = values[26];
			entity.setProvideReasonPRNPriceSD(Boolean.parseBoolean(provideReasonPRNPriceSD));
			col = 27;
			String prNPriceSDPositiveStr = values[27];
			if (StringUtils.isEmpty(prNPriceSDPositiveStr)){
				entity.setPrNPriceSDPositive(null);
			} else {
				Double prSPriceSDPositive = Double.parseDouble(prNPriceSDPositiveStr);
				entity.setPrNPriceSDPositive(prSPriceSDPositive);
			}
			col = 28;
			String prNPriceSDNegativeStr = values[28];
			if (StringUtils.isEmpty(prNPriceSDNegativeStr)){
				entity.setPrNPriceSDNegative(null);
			} else {
				Double prNPriceSDNegative = Double.parseDouble(prNPriceSDNegativeStr);
				entity.setPrNPriceSDNegative(prNPriceSDNegative);
			}
			col = 29;
			String prNPriceMonthStr = values[29];
			if (StringUtils.isEmpty(prNPriceMonthStr)){
				entity.setPrNPriceMonth(null);
			} else {
				Integer prNPriceMonth = (int) Double.parseDouble(prNPriceMonthStr);
				entity.setPrNPriceMonth(prNPriceMonth);
			}
			
			//Provide Reason if S price exceed the ranges of (x - y) in last k months
			col = 30;
			String provideReasonSPriceSD = values[30];
			entity.setProvideReasonSPriceSD(Boolean.parseBoolean(provideReasonSPriceSD));
			col = 31;
			String sPriceSDPositiveStr = values[31];
			if (StringUtils.isEmpty(sPriceSDPositiveStr)){
				entity.setsPriceSDPositive(null);
			} else {
				Double sPriceSDPositive = Double.parseDouble(sPriceSDPositiveStr);
				entity.setsPriceSDPositive(sPriceSDPositive);
			}
			col = 32;
			String sPriceSDNegativeStr = values[32];
			if (StringUtils.isEmpty(sPriceSDNegativeStr)){
				entity.setsPriceSDNegative(null);
			} else {
				Double sPriceSDNegative = Double.parseDouble(sPriceSDNegativeStr);
				entity.setsPriceSDNegative(sPriceSDNegative);
			}
			
			//Provide Reason if N price exceed the ranges of (x - y) in last k months
			col = 33;
			String provideReasonNPriceSD = values[33];
			entity.setProvideReasonNPriceSD(Boolean.parseBoolean(provideReasonNPriceSD));
			col = 34;
			String nPriceSDPositiveStr = values[34];
			if (StringUtils.isEmpty(nPriceSDPositiveStr)){
				entity.setnPriceSDPositive(null);
			} else {
				Double setnPriceSDPositive = Double.parseDouble(nPriceSDPositiveStr);
				entity.setnPriceSDPositive(setnPriceSDPositive);
			}
			col = 35;
			String nPriceSDNegativeStr = values[35];
			if (StringUtils.isEmpty(nPriceSDNegativeStr)){
				entity.setnPriceSDNegative(null);
			} else {
				Double nPriceSDNegative = Double.parseDouble(nPriceSDNegativeStr);
				entity.setnPriceSDNegative(nPriceSDNegative);
			}
			
			
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
	}

}
