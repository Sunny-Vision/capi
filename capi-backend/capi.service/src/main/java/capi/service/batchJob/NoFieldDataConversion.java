package capi.service.batchJob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.QuotationDao;
import capi.dal.SurveyMonthDao;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Quotation;
import capi.entity.SurveyMonth;
import capi.service.CommonService;
import capi.service.assignmentManagement.DataConversionService;

/**
 * Perform Data conversion for no field quotation record
 * @author stanley_tsang
 *	
 */
@Service("NoFieldDataConversion")
public class NoFieldDataConversion implements BatchJobService{

//	@Autowired
//	private AssignmentAttributeDao assignmentAttributeDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private IndoorQuotationRecordDao indoorQuotationRecordDao;
		
	@Autowired
	private DataConversionService dataConversionService;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "No field quotation record data conversion";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		
		SurveyMonth curSurveyMonth = surveyMonthDao.getSurveyMonthByDate(today);
		
		Date lastMonth = DateUtils.addMonths(curSurveyMonth.getReferenceMonth(), -1);
		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(lastMonth);
		
		Date closingDate = month.getClosingDate().getClosingDate();
		closingDate = DateUtils.addDays(closingDate, 1);
		if (!today.equals(closingDate)){
			return;
		}
		List<Integer> quotationIds = quotationDao.getNoFieldQuotationIds(today);
		HashMap<Integer, Quotation> quotationMap = new HashMap<Integer, Quotation>();
		
		if(quotationIds!=null && quotationIds.size()>0){
			int i = 1;
			
			for(Integer id : quotationIds){
				Quotation quotation = null;
				if (quotationMap.containsKey(id)){
					quotation = quotationMap.get(id);
				}
				else{
					quotation = quotationDao.findById(id);	
					quotationMap.put(id, quotation);
				}
				
				IndoorQuotationRecord record = dataConversionService.generateIndoorQuotationRecord(quotation, today, commonService.getReferenceMonth(today));
				indoorQuotationRecordDao.save(record);
				
				if(i % 20==0){
					indoorQuotationRecordDao.flushAndClearCache();
				}
				i++;
			}
		}indoorQuotationRecordDao.flushAndClearCache();
	}
//	@Override
//	public void runTask() throws Exception {
//		// TODO Auto-generated method stub
//		Date today = commonService.getDateWithoutTime(new Date());
//		
//		SurveyMonth surveyMonth = surveyMonthService.getRelativeSurveyMonth(today);
//		
//		if (surveyMonth == null){
//			return;
//		}
//		
//		Date curMonth = surveyMonth.getReferenceMonth();
//		Date lastMonth = DateUtils.addMonths(curMonth, -1);
//		List<NoFieldQuotationResult> results = new ArrayList<NoFieldQuotationResult>();
//		
//		results.addAll(quotationDao.generateNoFieldIndoorQuotationRecord(lastMonth));
//		results.addAll(quotationDao.generateNoFieldIndoorQuotationRecord(curMonth));
//		
//		HashMap<Integer, Quotation> quotationMap = new HashMap<Integer, Quotation>();
//		
//		if (results != null && results.size() > 0){
//			
//			int i = 1; 
//			for (NoFieldQuotationResult result : results){	
//				Quotation quotation = null;
//				if (quotationMap.containsKey(result.getQuotationId())){
//					quotation = quotationMap.get(result.getQuotationId());
//				}
//				else{
//					quotation = quotationDao.findById(result.getQuotationId());	
//					quotationMap.put(result.getQuotationId(), quotation);
//				}
//				
//				IndoorQuotationRecord record = generateIndoorQuotationRecord(quotation, result.getRecordDate(), result.getReferenceMonth());
//				indoorQuotationRecordDao.save(record);
//				if (result.getBackTrack1() != null){
//					record = generateIndoorQuotationRecord(quotation, result.getBackTrack1(), result.getReferenceMonth());
//					indoorQuotationRecordDao.save(record);
//				}
//				if (result.getBackTrack2() != null){
//					record = generateIndoorQuotationRecord(quotation, result.getBackTrack2(), result.getReferenceMonth());
//					indoorQuotationRecordDao.save(record);
//				}
//				if (result.getBackTrack3() != null){
//					record = generateIndoorQuotationRecord(quotation, result.getBackTrack3(), result.getReferenceMonth());
//					indoorQuotationRecordDao.save(record);
//				}
//				//quotationDao.save(quotation);
//				
//				if(i % 20==0){
//					indoorQuotationRecordDao.flushAndClearCache();
//				}
//				i++;
//			}
//			
//			
////			for (Quotation quotation: quotationMap.values()){
////				quotationDao.save(quotation);
////			}
//			
//			indoorQuotationRecordDao.flushAndClearCache();
//		}
		
		
//		List<AssignmentAttribute> attrs = assignmentAttributeDao.getNoFieldRelatedAttribute(today);
//		if (attrs != null && attrs.size() > 0){
//			for (AssignmentAttribute attr : attrs){
//				List<Quotation> quotations = quotationDao.getNoFieldQuotaionByAssignmentAttribute(attr.getAssignmentAttributeId());
//				Date refMonth = attr.getSurveyMonth().getReferenceMonth();
//				Date endDate = attr.getEndDate();
//				Set<BatchCollectionDate> dates = attr.getBatchCollectionDates();
//				for (Quotation quotation : quotations){
//					if (dates != null){
//						for (BatchCollectionDate date : dates){
//							if (date.getDate().after(today)){
//								continue;
//							}
//							IndoorQuotationRecord record = generateIndoorQuotationRecord(quotation, date.getDate(), refMonth);
//							indoorQuotationRecordDao.save(record);
//							if (date.getBackTrackDate1() != null){
//								record = generateIndoorQuotationRecord(quotation, date.getBackTrackDate1(), refMonth);
//								indoorQuotationRecordDao.save(record);
//							}
//							if (date.getBackTrackDate2() != null){
//								record = generateIndoorQuotationRecord(quotation, date.getBackTrackDate2(), refMonth);
//								indoorQuotationRecordDao.save(record);
//							}
//							if (date.getBackTrackDate3() != null){
//								record = generateIndoorQuotationRecord(quotation, date.getBackTrackDate3(), refMonth);
//								indoorQuotationRecordDao.save(record);
//							}
//						}
//					}
//					else{
//						IndoorQuotationRecord record = generateIndoorQuotationRecord(quotation, endDate, refMonth);
//						indoorQuotationRecordDao.save(record);
//					}
//					quotationDao.save(quotation);
//				}
//				
//			}
//			
//			indoorQuotationRecordDao.flush();
//		}
//	}
//	
//	/**
//	 * Generate indoor quotation record
//	 */
//	public IndoorQuotationRecord generateIndoorQuotationRecord(Quotation quotation, Date referenceDate, Date referenceMonth) {
//		IndoorQuotationRecord indoorEntity = new IndoorQuotationRecord();
//		indoorEntity.setQuotation(quotation);
//		indoorEntity.setReferenceDate(referenceDate);
//		indoorEntity.setReferenceMonth(referenceMonth);
//		indoorEntity.setCopyPriceType(1);
//		indoorEntity.setStatus("Complete");
//		
//		if (quotation.getStatus() != null && quotation.getStatus().equals("RUA")){
//			indoorEntity.setRUA(true);
//			indoorEntity.setRuaDate(quotation.getRuaDate());
//		}
//		
//		
//		IndoorQuotationRecord latestIndoor = indoorQuotationRecordDao.getLatestIndoorByQuotation(quotation.getId());
//		if (latestIndoor != null) {
//			if (latestIndoor.getCurrentNPrice() == null) {
//				indoorEntity.setLastNPrice(latestIndoor.getLastNPrice());
//			} else {
//				indoorEntity.setLastNPrice(latestIndoor.getCurrentNPrice());
//			}
//			if (latestIndoor.getCurrentSPrice() == null) {
//				indoorEntity.setLastSPrice(latestIndoor.getLastSPrice());
//				indoorEntity.setLastPriceDate(latestIndoor.getLastPriceDate());
//			} else {
//				indoorEntity.setLastSPrice(latestIndoor.getCurrentSPrice());
//				indoorEntity.setLastPriceDate(latestIndoor.getReferenceDate());
//			}
//		}
//		
//		QuotationRecord q = new QuotationRecord();
//		q.setIndoorQuotationRecord(indoorEntity);
//		q.setQuotation(quotation);
////		DataConversionModel model = new DataConversionModel();
//		
//		DataConversionModel model = dataConversionService.initDataConversionModel(quotation, referenceMonth, false);
//		
//		dataConversionService.procedureI1(q, model);
//		
//		indoorEntity.setComputedNPrice(commonService.round2dp(model.getEditedNPrice()));
//		indoorEntity.setComputedSPrice(commonService.round2dp(model.getEditedSPrice()));
//		indoorEntity.setCurrentNPrice(commonService.round2dp(model.getEditedNPrice()));
//		indoorEntity.setCurrentSPrice(commonService.round2dp(model.getEditedSPrice()));
//		indoorEntity.setPreviousNPrice(commonService.round2dp(model.getPreviousEditedNPrice()));
//		indoorEntity.setPreviousSPrice(commonService.round2dp(model.getPreviousEditedSPrice()));
//		
//		if(model.getEditedNPrice()==null){
//			indoorEntity.setNullCurrentNPrice(true);
//		} else {
//			indoorEntity.setNullCurrentNPrice(false);
//		}
//		if(model.getEditedSPrice()==null){
//			indoorEntity.setNullCurrentSPrice(true);
//		} else {
//			indoorEntity.setNullCurrentSPrice(false);
//		}
//		if(model.getPreviousEditedNPrice()==null){
//			indoorEntity.setNullPreviousNPrice(true);
//		} else {
//			indoorEntity.setNullPreviousNPrice(false);
//		}
//		if(model.getPreviousEditedSPrice()==null){
//			indoorEntity.setNullPreviousSPrice(true);
//		} else {
//			indoorEntity.setNullPreviousSPrice(false);
//		}
//		
//		return indoorEntity;
//	}
	
	
	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
