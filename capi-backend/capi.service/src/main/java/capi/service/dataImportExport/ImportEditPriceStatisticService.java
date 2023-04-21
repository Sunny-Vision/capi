package capi.service.dataImportExport;

import java.util.Date;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.ImputeQuotationDao;
import capi.dal.ImputeUnitDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UserDao;
import capi.entity.ImportExportTask;
import capi.entity.ImputeQuotation;
import capi.entity.ImputeUnit;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.Unit;
import capi.entity.User;
import capi.service.CommonService;

@Service("ImportEditPriceStatisticService")
public class ImportEditPriceStatisticService extends DataImportServiceBase{
	
	@Autowired
	private IndoorQuotationRecordDao dao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ImputeQuotationDao imputeQuotationDao;
	
	@Autowired
	private ImputeUnitDao imputeUnitDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 33;
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
			String [] values = getStringValues(row, 114);
			
			Quotation quotation = fillQuotation(values, rowCnt);
			quotationDao.save(quotation);
			
			IndoorQuotationRecord indoor = fillEntity(values, rowCnt, quotation);
			dao.save(indoor);
			
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		dao.flush();
		
		workbook.close();
	}
	
	private IndoorQuotationRecord fillEntity(String [] values, int rowCnt, Quotation quotation){
		IndoorQuotationRecord entity = null;
		int col = 0;
		try{
			col = 0;
			String indoorQuotationRecordId = values[col];
			if (StringUtils.isEmpty(indoorQuotationRecordId)){
				throw new RuntimeException("Indoor Quotation Record ID cannot be empty");
			}
			int id = (int)Double.parseDouble(indoorQuotationRecordId);
			entity = dao.findById(id);
			if (entity==null){
				throw new RuntimeException("Entity not found: Indoor Quotation Record ID = "+id);
			}
			
			col = 1;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Reference Month cannot be empty");
			}
			
			String date = values[col].trim();
			if(!(date.matches("\\d{2}-\\d{4}"))){
				throw new RuntimeException("Reference Month Format should be MM-YYYY");
			}
			Date refMonth = commonService.getMonth(date);
			entity.setReferenceMonth(refMonth);
			
			col = 2;
			if (!StringUtils.isEmpty(values[col])){
				int quotationRecordId = (int)Double.parseDouble(values[col]);
				QuotationRecord quotationRecord = quotationRecordDao.findById(quotationRecordId);
				if(quotationRecord == null){
					throw new RuntimeException("Quotation Record not found: Quotation Record Id = "+quotationRecordId);
				}
				entity.setQuotationRecord(quotationRecord);
			} else {
				entity.setQuotationRecord(null);
			}
			
			col = 3;
			date = null;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Reference Date cannot be empty");
			}
			date = values[col];
			if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
				throw new RuntimeException("Reference Date Format should be DD-MM-YYYY");
			}
			Date refDate = commonService.getDate(date);
			entity.setReferenceDate(refDate);
			
			col = 4;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Whether fieldwork is needed cannot be empty");
			}
			boolean isNoField = Boolean.parseBoolean(values[col]);
			entity.setNoField(!isNoField);
			
			col = 5;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Product Change cannot be empty");
			}
			boolean isProductChange = Boolean.parseBoolean(values[col]);
			entity.setProductChange(isProductChange);
			
			col = 6;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is New Product cannot be empty");
			}
			boolean isNewProduct = Boolean.parseBoolean(values[col]);
			entity.setNewProduct(isNewProduct);
			
			col = 7;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is New Recruitment cannot be empty");
			}
			boolean isNewRecruitment = Boolean.parseBoolean(values[col]);
			entity.setNewRecruitment(isNewRecruitment);
			
			col = 8;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is New Outlet cannot be empty");
			}
			boolean isNewOutlet = Boolean.parseBoolean(values[col]);
			entity.setNewOutlet(isNewOutlet);
			
			col = 9;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Data Conversion Status cannot be empty");
			String status = values[col].trim();
			if(!status.matches("Allocation|Conversion|Complete|Request Verification|"
					+ "Approve Verification|Reject Verification|Review Verification|Revisit"))
				throw new RuntimeException("Data Conversion Status should be "
						+ "Allocation, Conversion, Complete, Request Verification"
					+ ", Approve Verification, Reject Verification, Review Verification|Revisit");
			
			entity.setStatus(status);
			
			col = 10;
			entity.setQuotation(quotation);
			
			//col = 48;
			//col = 47;
			col = 45;
			if ("Allocation".equals(status)){
				entity.setUser(null);
			} else if (StringUtils.isEmpty(values[col])){
				if ("Conversion".equals(status)){
					throw new RuntimeException("Allocated Indoor officer ID can "
							+ "not be empty when Data Conversion Status = 'Conversion'");
				}
				entity.setUser(null);
			} else {
				String staffCode = values[col].trim();
				User user = userDao.getUserByStaffCode(staffCode);
				if(user==null)
					throw new RuntimeException("Officer not found: Staff Code = "+staffCode);
				entity.setUser(user);
			}
			
//			col = 51;
//			col = 50;
			col = 48;
			if (!StringUtils.isEmpty(values[col])){
				Double nPriceAfterUOMConversion = Double.parseDouble(values[col]);
				entity.setnPriceAfterUOMConversion(nPriceAfterUOMConversion);
			} else {
				entity.setnPriceAfterUOMConversion(null);
			}
			
//			col = 52;
//			col = 51;
			col = 49;
			if (!StringUtils.isEmpty(values[col])){
				Double sPriceAfterUOMConversion = Double.parseDouble(values[col]);
				entity.setsPriceAfterUOMConversion(sPriceAfterUOMConversion);
			} else {
				entity.setsPriceAfterUOMConversion(null);
			}
			
//			col = 53;
//			col = 52;
			col = 50;
			if (!StringUtils.isEmpty(values[col])){
				Double computedNPrice = Double.parseDouble(values[col]);
				entity.setComputedNPrice(computedNPrice);
			} else {
				entity.setComputedNPrice(null);
			}
			
//			col = 54;
//			col = 53;
			col = 51;
			if (!StringUtils.isEmpty(values[col])){
				Double computedSPrice = Double.parseDouble(values[col]);
				entity.setComputedSPrice(computedSPrice);
			} else {
				entity.setComputedSPrice(null);
			}
			
//			col = 55;
//			col = 54;
			col = 52;
			if (!StringUtils.isEmpty(values[col])){
				Double currentNPrice = Double.parseDouble(values[col]);
				entity.setCurrentNPrice(currentNPrice);
			} else {
				entity.setCurrentNPrice(null);
			}
			
//			col = 56;
//			col = 55;
			col = 53;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Null Current N Price cannot be empty");
			}
			boolean isNullCurrentNPrice = Boolean.parseBoolean(values[col]);
			entity.setNullCurrentNPrice(isNullCurrentNPrice);
			
//			col = 57;
//			col = 56;
			col = 54;
			if (!StringUtils.isEmpty(values[col])){
				Double currentSPrice = Double.parseDouble(values[col]);
				entity.setCurrentSPrice(currentSPrice);
			} else {
				entity.setCurrentSPrice(null);
			}
			
//			col = 58;
//			col = 57;
			col = 55;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Null Current S Price cannot be empty");
			}
			boolean isNullCurrentSPrice = Boolean.parseBoolean(values[col]);
			entity.setNullCurrentSPrice(isNullCurrentSPrice);
			
//			col = 59;
//			col = 58;
			col = 56;
			if (!StringUtils.isEmpty(values[col])){
				Double previousNPrice = Double.parseDouble(values[col]);
				entity.setPreviousNPrice(previousNPrice);
			} else {
				entity.setPreviousNPrice(null);
			}
			
//			col = 60;
//			col = 59;
			col = 57;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Null Previous N Price cannot be empty");
			}
			boolean isNullPreviousNPrice = Boolean.parseBoolean(values[col]);
			entity.setNullPreviousNPrice(isNullPreviousNPrice);
			
//			col = 61;
//			col = 60;
			col = 58;
			if (!StringUtils.isEmpty(values[col])){
				Double previousSPrice = Double.parseDouble(values[col]);
				entity.setPreviousSPrice(previousSPrice);
			} else {
				entity.setPreviousSPrice(null);
			}
			
//			col = 62;
//			col = 61;
			col = 59;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Null Previous S Price cannot be empty");
			}
			boolean isNullPreviousSPrice = Boolean.parseBoolean(values[col]);
			entity.setNullPreviousSPrice(isNullPreviousSPrice);
			
//			col = 63;
//			col = 62;
			col = 60;
			if (!StringUtils.isEmpty(values[col])){
				Double backNoLastNPirce = Double.parseDouble(values[col]);
				entity.setBackNoLastNPirce(backNoLastNPirce);
			} else {
				entity.setBackNoLastNPirce(null);
			}
			
//			col = 64;
//			col = 63;
			col = 61;
			if (!StringUtils.isEmpty(values[col])){
				Double backNoLastSPrice = Double.parseDouble(values[col]);
				entity.setBackNoLastSPrice(backNoLastSPrice);
			} else {
				entity.setBackNoLastSPrice(null);
			}
			
//			col = 65;
//			col = 64;
			col = 62;
			if (!StringUtils.isEmpty(values[col])){
				Double lastNPrice = Double.parseDouble(values[col]);
				entity.setLastNPrice(lastNPrice);
			} else {
				entity.setLastNPrice(null);
			}
			
//			col = 66;
//			col = 65;
			col = 63;
			if (!StringUtils.isEmpty(values[col])){
				Double lastSPrice = Double.parseDouble(values[col]);
				entity.setLastSPrice(lastSPrice);
			} else {
				entity.setLastSPrice(null);
			}
			
//			col = 67;
//			col = 66;
			col = 64;
			date = null;
			if (!StringUtils.isEmpty(values[col])){
				date = values[col].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Last Price Date Format should be DD-MM-YYYY");
				}
				Date lastPriceDate = commonService.getDate(date);
				entity.setLastPriceDate(lastPriceDate);
			} else {
				entity.setLastPriceDate(null);
			}
			
//			col = 68;
//			col = 67;
			col = 65;
			if (!StringUtils.isEmpty(values[col])){
				int copyPriceType = (int)Double.parseDouble(values[col]);
				entity.setCopyPriceType(copyPriceType);
			} else {
				entity.setCopyPriceType(null);
			}
			
//			col = 69;
//			col = 68;
			col = 66;
			if (!StringUtils.isEmpty(values[col])){
				int copyLastPriceType = (int)Double.parseDouble(values[col]);
				entity.setCopyLastPriceType(copyLastPriceType);
			} else {
				entity.setCopyLastPriceType(null);
			}
			
//			col = 70;
//			col = 69;
			col = 67;
			String remark = values[col].trim();
			entity.setRemark(remark);
			
//			col = 71;
//			col = 70;
			col = 68;
			if (!StringUtils.isEmpty(values[col])){
				Boolean isCurrentPriceKeepNo = Boolean.parseBoolean(values[col]);
				entity.setIsCurrentPriceKeepNo(isCurrentPriceKeepNo);
			} else {
				entity.setIsCurrentPriceKeepNo(null);
			}
			
//			col = 72;
//			col = 71;
			col = 69;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is RUA cannot be empty");
			}
			boolean isRUA = Boolean.parseBoolean(values[col]);
			entity.setRUA(isRUA);
			
//			col = 73;
//			col = 72;
			col = 70;
			date = null;
			if (!StringUtils.isEmpty(values[col])){
				date = values[col].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("RUA Date Format should be DD-MM-YYYY");
				}
				Date ruaDate = commonService.getDate(date);
				entity.setRuaDate(ruaDate);
			} else {
				entity.setRuaDate(null);
			}
			
//			col = 74;
//			col = 73;
			col = 71;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("is Product Not Available cannot be empty");
			}
			boolean isProductNotAvailable = Boolean.parseBoolean(values[col]);
			entity.setProductNotAvailable(isProductNotAvailable);
			
//			col = 75;
//			col = 74;
			col = 72;
			if (!StringUtils.isEmpty(values[col])){
				date = values[col].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Product Not Available From Format should be DD-MM-YYYY");
				}
				Date productNotAvailableFrom = commonService.getDate(date);
				entity.setProductNotAvailableFrom(productNotAvailableFrom);
			} else {
				entity.setProductNotAvailableFrom(null);
			}
			
//			col = 80;
//			col = 79;
			col = 77;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Spicing cannot be empty");
			}
			boolean isSpicing = Boolean.parseBoolean(values[col]);
			entity.setSpicing(isSpicing);
			
//			col = 81;
//			col = 80;
			col = 78;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Outlier cannot be empty");
			}
			boolean isOutlier = Boolean.parseBoolean(values[col]);
			entity.setOutlier(isOutlier);
			
//			col = 82;
//			col = 81;
			col = 79;
			String outlierRemark = values[col].trim();
			entity.setOutlierRemark(outlierRemark);
			
//			col = 83;
//			col = 82;
			col = 80;
			if (!StringUtils.isEmpty(values[col])){
				Double fr = Double.parseDouble(values[col]);
				entity.setFr(fr);
			} else{
				entity.setFr(null);
			}
			
//			col = 84;
//			col = 83;
			col = 81;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is Apply FR cannot be empty");
			}
			boolean isApplyFR = Boolean.parseBoolean(values[col]);
			entity.setApplyFR(isApplyFR);
			
//			col = 85;
//			col = 84;
			col = 82;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Is FR Percentage cannot be empty");
			}
			boolean isFRPercentage = Boolean.parseBoolean(values[col]);
			entity.setFRPercentage(isFRPercentage);
			
			ImputeQuotation imputeQuotation = imputeQuotationDao.getImputeQuotation(quotation.getQuotationId(), refMonth);
			if (imputeQuotation == null){
				imputeQuotation = new ImputeQuotation();
				imputeQuotation.setQuotation(quotation);
				imputeQuotation.setReferenceMonth(entity.getReferenceMonth());
			}
//			col = 87;
//			col = 86;
			col = 84;
			if(!StringUtils.isEmpty(values[col])){
				Double imputedQuotationPrice = Double.parseDouble(values[col]);
				imputeQuotation.setPrice(imputedQuotationPrice);
			} else {
				imputeQuotation.setPrice(null);
			}
			
//			col = 88;
//			col = 87;
			col = 85;
			String imputedQuotationRemark = values[col];
			imputeQuotation.setRemark(imputedQuotationRemark);
			
			imputeQuotationDao.save(imputeQuotation);
			
			Unit unit = quotation.getUnit();
			ImputeUnit imputeUnit = imputeUnitDao.getImputeUnit(unit.getUnitId(), refMonth);
			if (imputeUnit == null){
				imputeUnit = new ImputeUnit();
				imputeUnit.setUnit(unit);
				imputeUnit.setReferenceMonth(entity.getReferenceMonth());
			}
//			col = 89;
//			col = 88;
			col = 86;
			if(!StringUtils.isEmpty(values[col])){
				Double imputedUnitPrice = Double.parseDouble(values[col]);
				imputeUnit.setPrice(imputedUnitPrice);
			} else {
				imputeUnit.setPrice(null);
			}
			
//			col = 90;
//			col = 89;
			col = 87;
			String imputedUnitRemark = values[col];
			imputeUnit.setRemark(imputedUnitRemark);
			
			imputeUnitDao.save(imputeUnit);
			
		} catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return entity;
		
	}
	
	private Quotation fillQuotation(String[] values, int rowCnt){
		Quotation quotation = null;
		int col = 0;
		try{
			
			col = 10;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Quotation ID cannot be empty");
			}
			int quotationId = (int)Double.parseDouble(values[col]);
			quotation = quotationDao.findById(quotationId);
			if (quotation == null){
				throw new RuntimeException("Quotation not found: Quotation ID = "+quotationId);
			}
			
//			col = 111;
//			col = 110;
			col = 108;
			String oldFormSequence = values[col].trim();
			quotation.setOldFormSequence(oldFormSequence);
		} catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		return quotation;
	}
	
}
