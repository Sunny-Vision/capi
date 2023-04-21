package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import capi.service.dataConversion.QuotationRecordDataConversionService;

@Service("ImportEditedPriceService")
public class ImportEditedPriceService  extends DataImportServiceBase{

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
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationRecordDataConversionService dataConvertService;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 24;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		
		Date referenceMonth = task.getReferenceMonth();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 96);
			
			//For IndoorQuotationRecord
			IndoorQuotationRecord indoorQuotationRecord = fillEntity(values, rowCnt, referenceMonth);
			dao.save(indoorQuotationRecord);
			
			if(indoorQuotationRecord.getIndoorQuotationRecordId()!=null){
				ids.add(indoorQuotationRecord.getIndoorQuotationRecordId());
			}
			
			if(rowCnt%20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
//		//Delete
//		List<Integer> existing = dao.getExistingIdsByReferenceMonth(referenceMonth);
//		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existing, ids));
//		
//		deleteEntities(notExisting, dao);
		
		dao.flush();
		
		workbook.close();
	}
	
	private IndoorQuotationRecord fillEntity(String[] values, int rowCnt, Date referenceMonth) throws Exception{
		IndoorQuotationRecord entity = null;
		int col = 0;
		
		try{
			col = 0;
			String id = values[col];
			if(StringUtils.isEmpty(id))
				entity = new IndoorQuotationRecord();
			else{
				entity = dao.findById((int)Double.parseDouble(id));
				if(entity==null)
					throw new RuntimeException("Record not found: IndoorQuotationRecord id="+id);
			}
			
			col = 3;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Reference Date cannot be empty");
				
			String date = values[col];
			if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
				throw new RuntimeException("Reference Date Format should be DD-MM-YYYY");
			}
			Date referenceDate = commonService.getDate(date);
			entity.setReferenceDate(referenceDate);
			
			col = 4;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Whether fieldwork is needed cannot be empty");
			boolean isNoField = Boolean.parseBoolean(values[col]);
			entity.setNoField(!isNoField);
			
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
			
//			col = 48;
//			col = 47;
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
			
//			col = 55;
//			col = 54;
			col = 52;
			if(!StringUtils.isEmpty(values[col])){
				Double currentNPrice = Double.parseDouble(values[col]);
				entity.setCurrentNPrice(currentNPrice);
			} else {
				entity.setCurrentNPrice(null);
			}
			
//			col = 56;
//			col = 55;
			col = 53;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is Null Current N Price cannot be empty");
				
			boolean isNullCurrentNPrice = Boolean.parseBoolean(values[col]);
			entity.setNullCurrentNPrice(isNullCurrentNPrice);
			
//			col = 57;
//			col = 56;
			col = 54;
			if(!StringUtils.isEmpty(values[col])){
				Double currentSPrice = Double.parseDouble(values[col]);
				entity.setCurrentSPrice(currentSPrice);
			} else {
				entity.setCurrentSPrice(null);
			}
			
//			col = 58;
//			col = 57;
			col = 55;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is Null Current S Price cannot be empty");
			boolean isNullCurrentSPrice = Boolean.parseBoolean(values[col]);
			entity.setNullCurrentSPrice(isNullCurrentSPrice);
			
//			col = 59;
//			col = 58;
			col = 56;
			if(!StringUtils.isEmpty(values[col])){
				Double previousNPrice = Double.parseDouble(values[col]);
				entity.setPreviousNPrice(previousNPrice);
			} else {
				entity.setPreviousNPrice(null);
			}
			
//			col = 60;
//			col = 59;
			col = 57;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is Null Previous N Price cannot be empty");
			boolean isNullPreviousNPrice = Boolean.parseBoolean(values[col]);
			entity.setNullPreviousNPrice(isNullPreviousNPrice);
			
//			col = 61;
//			col = 60;
			col = 58;
			if(!StringUtils.isEmpty(values[col])){
				Double previousSPrice = Double.parseDouble(values[col]);
				entity.setPreviousSPrice(previousSPrice);
			} else {
				entity.setPreviousSPrice(null);
			}
			
//			col = 62;
//			col = 61;
			col = 59;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is Null Previous S Price cannot be empty");
			boolean isNullPreviousSPrice = Boolean.parseBoolean(values[col]);
			entity.setNullPreviousSPrice(isNullPreviousSPrice);
			
//			col = 65;
//			col = 64;
			col = 62;
			if(!StringUtils.isEmpty(values[col])){
				Double lastNPrice = Double.parseDouble(values[col]);
				entity.setLastNPrice(lastNPrice);
			} else {
				entity.setLastNPrice(null);
			}
			
//			col = 66;
//			col = 65;
			col = 63;
			if(!StringUtils.isEmpty(values[col])){
				Double lastSPrice = Double.parseDouble(values[col]);
				entity.setLastSPrice(lastSPrice);
			} else {
				entity.setLastSPrice(null);
			}
			
//			col = 67;
//			col = 66;
			col = 64;
			if(!StringUtils.isEmpty(values[col])){
				date = values[col];
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Last Price Date Format should be DD-MM-YYYY");
				}
				Date lastPriceDate = commonService.getDate(date);
				entity.setLastPriceDate(lastPriceDate);
			} else {
				entity.setLastPriceDate(null);
			}
			
//			col = 70;
//			col = 69;
			col = 67;
			String remark = values[col];
			entity.setRemark(remark);

//			col = 81;
//			col = 80;
			col = 78;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is Outlier cannot be empty");
			boolean isOutlier = Boolean.parseBoolean(values[col]);
			entity.setOutlier(isOutlier);
			
//			col = 82;
//			col = 81;
			col = 79;
			String outlierRemark = values[col];
			entity.setOutlierRemark(outlierRemark);
			
//			col = 83;
//			col = 82;
			col = 80;
			if(!StringUtils.isEmpty(values[col])){
				Double fr = Double.parseDouble(values[col]);
				entity.setFr(fr);
			} else {
				entity.setFr(null);
			}
			
//			col = 84;
//			col = 83;
			col = 81;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is Apply FR cannot be empty");
			boolean isApplyFR = Boolean.parseBoolean(values[col]);
			entity.setApplyFR(isApplyFR);
			
//			col = 85;
//			col = 84;
			col = 82;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is FR Percentage cannot be empty");
			boolean isFRPercentage = Boolean.parseBoolean(values[col]);
			entity.setFRPercentage(isFRPercentage);
			
			col = 10;
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Quotation Id cannot be empty");
			int quotationId = (int)Double.parseDouble(values[col]);
			Quotation quotation = quotationDao.findById(quotationId);
			if (quotation==null){
				throw new RuntimeException("Quotation not found: Quotation Id = "+ quotationId);
			}
			
			ImputeQuotation imputeQuotation = imputeQuotationDao.getImputeQuotation(quotationId, referenceMonth);
			if (imputeQuotation == null){
				imputeQuotation = new ImputeQuotation();
				imputeQuotation.setQuotation(quotation);
				imputeQuotation.setReferenceMonth(referenceMonth);
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
			ImputeUnit imputeUnit = imputeUnitDao.getImputeUnit(unit.getUnitId(), referenceMonth);
			if (imputeUnit == null){
				imputeUnit = new ImputeUnit();
				imputeUnit.setUnit(unit);
				imputeUnit.setReferenceMonth(referenceMonth);
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
			
			if(StringUtils.isEmpty(id)){
				col = 1;
				entity.setReferenceMonth(referenceMonth);
				
				QuotationRecord quotationRecord = null;
				col = 2;
				if(!StringUtils.isEmpty(values[col])){
					int quotationRecordId = (int)Double.parseDouble(values[col]);
					quotationRecord = quotationRecordDao.findById(quotationRecordId);
					if(quotationRecord == null){
						throw new RuntimeException("Quotation Record not found: Quotation Record Id = "+quotationRecordId);
					}
					entity.setQuotationRecord(quotationRecord);
				} else {
					entity.setQuotationRecord(null);
				}
					
				col = 5;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is Product Change cannot be empty");
				boolean isProductChange = Boolean.parseBoolean(values[col]);
				entity.setProductChange(isProductChange);
				
				col = 6;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is New Product cannot not be empty");
				boolean isNewProduct = Boolean.parseBoolean(values[col]);
				entity.setNewProduct(isNewProduct);
				
				col = 7;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is New Recruitment cannot be empty");
				boolean isNewRecruitment = Boolean.parseBoolean(values[col]);
				entity.setNewRecruitment(isNewRecruitment);
				
				col = 8;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is New Outlet cannot be empty");
				boolean isNewOutlet = Boolean.parseBoolean(values[col]);
				entity.setNewOutlet(isNewOutlet);
				
				col = 10;
				if(quotationRecord !=null && quotationRecord.getQuotation().getQuotationId() != quotationId)
					throw new RuntimeException("Quotation Record Quotation and Quotation not same");
				entity.setQuotation(quotation);
				
				//col = 51;
				//col = 50;
				col = 48;
				if(!StringUtils.isEmpty(values[col])){
					Double nPriceAfterUOMConversion = Double.parseDouble(values[col]);
					entity.setnPriceAfterUOMConversion(nPriceAfterUOMConversion);
				} else {
					entity.setnPriceAfterUOMConversion(null);
				}
				
//				col = 52;
//				col = 51;
				col = 49;
				if(!StringUtils.isEmpty(values[col])){
					Double sPriceAfterUOMConversion = Double.parseDouble(values[col]);
					entity.setsPriceAfterUOMConversion(sPriceAfterUOMConversion);
				} else {
					entity.setsPriceAfterUOMConversion(null);
				}
				
//				col = 53;
//				col = 52;
				col = 50;
				if(!StringUtils.isEmpty(values[col])){
					Double computedNPrice = Double.parseDouble(values[col]);
					entity.setComputedNPrice(computedNPrice);
				} else {
					entity.setComputedNPrice(null);
				}
				
//				col = 54;
//				col = 53;
				col = 51;
				if(!StringUtils.isEmpty(values[col])){
					Double computedSPrice = Double.parseDouble(values[col]);
					entity.setComputedSPrice(computedSPrice);
				} else {
					entity.setComputedSPrice(null);
				}
				
//				col = 63;
//				col = 62;
				col = 60;
				if(!StringUtils.isEmpty(values[col])){
					Double backNoLastNPirce = Double.parseDouble(values[col]);
					entity.setBackNoLastNPirce(backNoLastNPirce);
				} else {
					entity.setBackNoLastNPirce(null);
				}
				
//				col = 64;
//				col = 63;
				col = 61;
				if(!StringUtils.isEmpty(values[col])){
					Double backNoLastSPrice = Double.parseDouble(values[col]);
					entity.setBackNoLastSPrice(backNoLastSPrice);
				} else {
					entity.setBackNoLastSPrice(null);
				}
				
//				col = 68;
//				col = 67;
				col = 65;
				if(!StringUtils.isEmpty(values[col])){
					int copyPriceType = (int)Double.parseDouble(values[col]);
					if(copyPriceType<1 || copyPriceType>3)
						throw new RuntimeException("Copy Price Type cannot be smaller than 1 larger than 3");
					entity.setCopyPriceType(copyPriceType);
				} else {
					entity.setCopyPriceType(null);
				}
				
//				col = 69;				
//				col = 68;
				col = 66;
				if(!StringUtils.isEmpty(values[col])){
					int copyLastPriceType = (int)Double.parseDouble(values[col]);
					if(copyLastPriceType<1 || copyLastPriceType>3)
						throw new RuntimeException("Copy Last Price Type cannot be smaller than 1 larger than 3");
					entity.setCopyLastPriceType(copyLastPriceType);
				} else {
					entity.setCopyLastPriceType(null);
				}
				
//				col = 71;
//				col = 70;
				col = 68;
				if(!StringUtils.isEmpty(values[col])){
					boolean isCurrentPriceKeepNo = Boolean.parseBoolean(values[col]);
					entity.setIsCurrentPriceKeepNo(isCurrentPriceKeepNo);
				} else {
					entity.setIsCurrentPriceKeepNo(null);
				}
				
//				col = 72;
//				col = 71;
				col = 69;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is RUA cannot be empty");
				boolean isRUA = Boolean.parseBoolean(values[col]);
				entity.setRUA(isRUA);
				
//				col = 73;
//				col = 72;
				col = 70;
				if(!StringUtils.isEmpty(values[col])){
					date = values[col].trim();
					if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
						throw new RuntimeException("RUA Date Format should be DD-MM-YYYY");
					}
					Date ruaDate = commonService.getDate(date);
					entity.setRuaDate(ruaDate);
				} else {
					entity.setRuaDate(null);
				}
				
//				col = 74;
//				col = 73;
				col = 71;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is Product Not Available cannot be empty");
				boolean isProductNotAvailable = Boolean.parseBoolean(values[col]);
				entity.setProductNotAvailable(isProductNotAvailable);
				
//				col = 75;
//				col = 74;
				col = 72;
				if(!StringUtils.isEmpty(values[col])){
					date = values[col].trim();
					if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
						throw new RuntimeException("Product Not Available From Format should be DD-MM-YYYY");
					}
					Date productNotAvailableFrom = commonService.getDate(date);
					entity.setProductNotAvailableFrom(productNotAvailableFrom);
				} else {
					entity.setProductNotAvailableFrom(null);
				}
				
//				col = 80;
//				col = 79;
				col = 77;
				if(StringUtils.isEmpty(values[col]))
					throw new RuntimeException("Is Spicing cannot be empty");
				boolean isSpicing = Boolean.parseBoolean(values[col]);
				entity.setSpicing(isSpicing);
			}
			
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
}
