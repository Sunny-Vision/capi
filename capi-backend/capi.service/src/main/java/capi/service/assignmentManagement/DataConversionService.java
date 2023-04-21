package capi.service.assignmentManagement;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.SurveyMonthDao;
import capi.dal.SystemConfigurationDao;
import capi.entity.IndoorQuotationRecord;
import capi.entity.PricingFrequency;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.entity.SystemConfiguration;
import capi.entity.Unit;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.DataConversionModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("DataConversionService")
public class DataConversionService extends BaseService {
	
	private static final Logger logger = LoggerFactory.getLogger(DataConversionService.class);
	
	@Autowired
	SurveyMonthDao surveyMonthDao;
	
	@Autowired
	IndoorQuotationRecordDao indoorQuotationRecordDao;
	
	@Autowired
	SystemConfigurationDao systemConfigurationDao;
	
	@Autowired
	QuotationRecordService quotationRecordService;
	
	@Autowired
	CommonService commonService;
	
	/**
	 * Convert quotation record to indoor quotation record (no include no field record)
	 */
	@Transactional
	public void convert(QuotationRecord entity) {
		Date today = new Date();
		today = commonService.getDateWithoutTime(today);
		
		if (today.after(entity.getAssignment().getSurveyMonth().getClosingDate().getClosingDate())) return;
		
		if (entity.getQuotation().getUnit().isConvertAfterClosingDate()) {
			Date referenceMonth = entity.getAssignment().getSurveyMonth().getReferenceMonth();
			Date lastSurveyMonthDate = DateUtils.addMonths(referenceMonth, -1);
			SurveyMonth lastSurveyMonth = surveyMonthDao.getSurveyMonthByReferenceMonth(lastSurveyMonthDate);
			
			if (lastSurveyMonth != null && (lastSurveyMonth.getClosingDate().getClosingDate().after(today) || lastSurveyMonth.getClosingDate().getClosingDate().equals(today))) return;
		}
		
		IndoorQuotationRecord indoorEntity = generateIndoorQuotationRecord(entity);
		entity.setIndoorQuotationRecord(indoorEntity);
		indoorEntity.setByPassLog(true);
		indoorQuotationRecordDao.save(indoorEntity);
		
		boolean isVerity = "Verify".equals(entity.getQuotationState());
		
		Date refMonth = entity.getAssignment().getSurveyMonth().getReferenceMonth();
		Date lastMonth = DateUtils.addMonths(refMonth, -1);
		SurveyMonth surveyMonth = surveyMonthDao.getSurveyMonthByReferenceMonth(lastMonth);
		DataConversionModel model = initDataConversionModel(entity.getQuotation(), surveyMonth, isVerity);
		
		procedureI1(entity, model);
		
		// 2021-03-19: catch-throw NumberFormatException (price calculation may give NaN/Infinity on divide-by-zero)
		// log processing record info for further troubleshoot
		try {
			entity.getIndoorQuotationRecord().setComputedNPrice(commonService.round2dp(model.getEditedNPrice()));
			entity.getIndoorQuotationRecord().setComputedSPrice(commonService.round2dp(model.getEditedSPrice()));
			entity.getIndoorQuotationRecord().setCurrentNPrice(commonService.round2dp(model.getEditedNPrice()));
			entity.getIndoorQuotationRecord().setCurrentSPrice(commonService.round2dp(model.getEditedSPrice()));
			entity.getIndoorQuotationRecord().setPreviousNPrice(commonService.round2dp(model.getPreviousEditedNPrice()));
			entity.getIndoorQuotationRecord().setPreviousSPrice(commonService.round2dp(model.getPreviousEditedSPrice()));
			entity.getIndoorQuotationRecord().setBackNoLastNPirce(commonService.round2dp(model.getNewProductLastMonthNPrice()));
			entity.getIndoorQuotationRecord().setBackNoLastSPrice(commonService.round2dp(model.getNewProductLastMonthSPrice()));
		} catch (NumberFormatException nfe) {
			logger.error(String.format(
					"Invalid Calculation -- check N/S price, FR value: indoor quotation record ID %s, quotation record ID %s",
					(entity.getIndoorQuotationRecord() != null ? entity.getIndoorQuotationRecord().getId() : "n/a"),
					entity.getId()));
			throw nfe;
		}
		
		if(model.getEditedNPrice()==null){
			entity.getIndoorQuotationRecord().setNullCurrentNPrice(true);
		} else {
			entity.getIndoorQuotationRecord().setNullCurrentNPrice(false);
		}
		if(model.getEditedSPrice()==null){
			entity.getIndoorQuotationRecord().setNullCurrentSPrice(true);
		} else {
			entity.getIndoorQuotationRecord().setNullCurrentSPrice(false);
		}
		if(model.getPreviousEditedNPrice()==null){
			entity.getIndoorQuotationRecord().setNullPreviousNPrice(true);
		} else {
			entity.getIndoorQuotationRecord().setNullPreviousNPrice(false);
		}
		if(model.getPreviousEditedSPrice()==null){
			entity.getIndoorQuotationRecord().setNullPreviousSPrice(true);
		} else {
			entity.getIndoorQuotationRecord().setNullPreviousSPrice(false);
		}
		
		if (entity.getAvailability() != null && entity.getAvailability() == 1) {
			if(entity.isProductChange()){
				if (entity.getQuotation().getKeepNoMonth() != null && entity.getQuotation().getKeepNoMonth() != 0){
					entity.getQuotation().setKeepNoMonth(0);
				}
			}
		}
		
		indoorQuotationRecordDao.save(entity.getIndoorQuotationRecord());
	}
	
	/**
	 * 
	 * @param quotation the quotation of the record
	 * @param referenceMonth the reference of the quotation record to be converted
	 * @param isVerify whether it is verify or revisit cases
	 * @return the model for data conversion
	 */
	public DataConversionModel initDataConversionModel(Quotation quotation, Date referenceMonth, boolean isVerify){
		SurveyMonth surveyMonth = surveyMonthDao.getSurveyMonthByReferenceMonth(referenceMonth);		
		return initDataConversionModel(quotation, surveyMonth, isVerify);
	}
	
	
	/**
	 * 
	 * @param quotation the quotation of the record
	 * @param surveyMonth the survey month of the quotation record to be converted
	 * @param isVerify whether it is verify or revisit cases
	 * @return the model for data conversion
	 */
	public DataConversionModel initDataConversionModel(Quotation quotation, SurveyMonth surveyMonth, boolean isVerify){
		DataConversionModel model = new DataConversionModel();
				
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Date closingDate = null;
		if (surveyMonth != null){
			closingDate = surveyMonth.getClosingDate().getClosingDate();
		}
		
		if (closingDate != null && isVerify && today.after(closingDate)){			
			model.setIsFRApplied(quotation.getTempIsFRApplied());
			model.setIsReturnGoods(quotation.getTempIsReturnGoods());
			model.setIsReturnNewGoods(quotation.getTempIsReturnNewGoods());
			model.setLastFRAppliedDate(quotation.getTempLastFRAppliedDate());
			model.setFrValue(quotation.getTempFRValue());
			model.setFrPercentage(quotation.isTempFRPercentage());
			model.setIsUseFRAdmin(quotation.isTempIsUseFRAdmin());
			model.setKeepNoMonth(quotation.getTempKeepNoMonth());
		}
		else{
			boolean isUseFRAdmin = quotation.isUseFRAdmin() == null && quotation.getOutlet() != null && quotation.getOutlet().isUseFRAdmin() 
					|| quotation.isUseFRAdmin() !=null && quotation.isUseFRAdmin();
			
			model.setIsFRApplied(quotation.isFRApplied());
			model.setIsReturnGoods(quotation.isReturnGoods());
			model.setIsReturnNewGoods(quotation.isReturnNewGoods());
			model.setLastFRAppliedDate(quotation.getLastFRAppliedDate());
			model.setIsUseFRAdmin(isUseFRAdmin);
			model.setKeepNoMonth(quotation.getKeepNoMonth());
			if (isUseFRAdmin){
				model.setFrValue(quotation.getFrAdmin());
				model.setFrPercentage(quotation.getIsFRAdminPercentage());
			}
			else{
				model.setFrValue(quotation.getFrField());
				model.setFrPercentage(quotation.getIsFRFieldPercentage());
			}			
		}
		
		return model;
	}
	
	/**
	 * Generate indoor quotation record
	 */
	public IndoorQuotationRecord generateIndoorQuotationRecord(QuotationRecord entity) {
		IndoorQuotationRecord indoorEntity = entity.getIndoorQuotationRecord();
//		Quotation Inputquotation = new Quotation();
//		BeanUtils.copyProperties(entity.getQuotation(), Inputquotation);
//		indoorEntity.setQuotation(Inputquotation);
		
		if (indoorEntity == null){
			indoorEntity = new IndoorQuotationRecord();
			indoorEntity.setStatus("Allocation");
			indoorEntity.setNewRecruitment(entity.isNewRecruitment());
			indoorEntity.setNewOutlet(entity.isNewOutlet());
		}
		clearDataBeforeUpdate(indoorEntity, entity);
		indoorEntity.setQuotationRecord(entity);
		//indoorEntity.setUser(entity.getUser());
		indoorEntity.setQuotation(entity.getQuotation());
		indoorEntity.setReferenceDate(entity.getReferenceDate());
		indoorEntity.setReferenceMonth(entity.getAssignment().getSurveyMonth().getReferenceMonth());
		indoorEntity.setCopyPriceType(1);
		if(!("Verify".equals(entity.getQuotationState()) || "Revisit".equals(entity.getQuotationState()))){
			indoorEntity.setStatus("Allocation");
			indoorEntity.setUser(null);
		}
		indoorEntity.setRemark(entity.getReason());
		
		indoorEntity.setProductChange(entity.isProductChange());
		indoorEntity.setNewProduct(entity.isNewProduct());
		
		Quotation quotation = entity.getQuotation();
		if (quotation.getStatus().equals("RUA")){
			indoorEntity.setRUA(true);
			indoorEntity.setRuaDate(quotation.getRuaDate());
		}
		
		
		QuotationRecord backNoEntity = quotationRecordService.getBackNoRecord(entity);
		if (backNoEntity != null) {
			indoorEntity.setBackNoLastNPirce(backNoEntity.getnPrice());
			indoorEntity.setBackNoLastSPrice(backNoEntity.getsPrice());
		}
		
		IndoorQuotationRecord latestIndoor = indoorQuotationRecordDao.getLatestIndoorByQuotation(entity.getQuotation().getId(), entity.getReferenceDate());

		if (entity.getAvailability() !=null && entity.getAvailability() == 4){
			indoorEntity.setProductNotAvailable(true);
			if (latestIndoor != null) {
				if (latestIndoor.isProductNotAvailable() && latestIndoor.getProductNotAvailableFrom() != null){
					indoorEntity.setProductNotAvailableFrom(latestIndoor.getProductNotAvailableFrom());
				}
				else{
					indoorEntity.setProductNotAvailableFrom(entity.getCollectionDate());
				}
			}
			else{
				indoorEntity.setProductNotAvailableFrom(entity.getCollectionDate());
			}
		}
		
		if (latestIndoor != null) {
//			if (latestIndoor.getCurrentNPrice() == null) {
//				indoorEntity.setLastNPrice(latestIndoor.getLastNPrice());
//			} else {
				indoorEntity.setLastNPrice(latestIndoor.getCurrentNPrice());
//			}
//			if (latestIndoor.getCurrentSPrice() == null) {
//				indoorEntity.setLastSPrice(latestIndoor.getLastSPrice());
//				indoorEntity.setLastPriceDate(latestIndoor.getLastPriceDate());
//			} else {
				indoorEntity.setLastSPrice(latestIndoor.getCurrentSPrice());
				indoorEntity.setLastPriceDate(latestIndoor.getReferenceDate());
//			}
		}
		
		if (entity.isNewRecruitment()) {
			indoorEntity.setNewRecruitment(true);
		}
		
		return indoorEntity;
	}
	

	/**
	 * Generate indoor quotation record
	 */
	public IndoorQuotationRecord generateIndoorQuotationRecord(Quotation quotation, Date referenceDate, Date referenceMonth) {
		IndoorQuotationRecord indoorEntity = new IndoorQuotationRecord();
		indoorEntity.setQuotation(quotation);
		indoorEntity.setReferenceDate(referenceDate);
		indoorEntity.setReferenceMonth(referenceMonth);
		indoorEntity.setCopyPriceType(1);
		indoorEntity.setStatus("Complete");
		
		if (quotation.getStatus() != null && quotation.getStatus().equals("RUA")){
			indoorEntity.setRUA(true);
			indoorEntity.setRuaDate(quotation.getRuaDate());
		}
		
		
		IndoorQuotationRecord latestIndoor = indoorQuotationRecordDao.getLatestIndoorByQuotation(quotation.getId(), referenceDate);
		
		if (latestIndoor != null) {
				indoorEntity.setLastNPrice(latestIndoor.getCurrentNPrice());
				indoorEntity.setLastSPrice(latestIndoor.getCurrentSPrice());
				indoorEntity.setLastPriceDate(latestIndoor.getReferenceDate());
		}
		
		QuotationRecord q = new QuotationRecord();
		q.setIndoorQuotationRecord(indoorEntity);
		q.setQuotation(quotation);
		
		DataConversionModel model = initDataConversionModel(quotation, referenceMonth, false);
		
		procedureI1(q, model);
		
		// 2021-03-19: catch-throw NumberFormatException (price calculation may give NaN/Infinity on divide-by-zero)
		// log processing record info for further troubleshoot
		try {
			indoorEntity.setComputedNPrice(commonService.round2dp(model.getEditedNPrice()));
			indoorEntity.setComputedSPrice(commonService.round2dp(model.getEditedSPrice()));
			indoorEntity.setCurrentNPrice(commonService.round2dp(model.getEditedNPrice()));
			indoorEntity.setCurrentSPrice(commonService.round2dp(model.getEditedSPrice()));
			indoorEntity.setPreviousNPrice(commonService.round2dp(model.getPreviousEditedNPrice()));
			indoorEntity.setPreviousSPrice(commonService.round2dp(model.getPreviousEditedSPrice()));
			indoorEntity.setBackNoLastNPirce(commonService.round2dp(model.getNewProductLastMonthNPrice()));
			indoorEntity.setBackNoLastSPrice(commonService.round2dp(model.getNewProductLastMonthSPrice()));
		} catch (NumberFormatException nfe) {
			logger.error(String.format(
					"Invalid Calculation -- check N/S price, FR value: indoor quotation record ID %s, quotation record ID %s",
					indoorEntity.getId(),
					(indoorEntity.getQuotationRecord() != null ? indoorEntity.getQuotationRecord().getId() : "n/a")));
			throw nfe;
		}
		
		if(model.getEditedNPrice()==null){
			indoorEntity.setNullCurrentNPrice(true);
		} else {
			indoorEntity.setNullCurrentNPrice(false);
		}
		if(model.getEditedSPrice()==null){
			indoorEntity.setNullCurrentSPrice(true);
		} else {
			indoorEntity.setNullCurrentSPrice(false);
		}
		if(model.getPreviousEditedNPrice()==null){
			indoorEntity.setNullPreviousNPrice(true);
		} else {
			indoorEntity.setNullPreviousNPrice(false);
		}
		if(model.getPreviousEditedSPrice()==null){
			indoorEntity.setNullPreviousSPrice(true);
		} else {
			indoorEntity.setNullPreviousSPrice(false);
		}
		
		return indoorEntity;
	}
	
	public boolean checkIfNoField(QuotationRecord entity){
		IndoorQuotationRecord indoorEntity = entity.getIndoorQuotationRecord();
		boolean isNofield = false;
		Date refMonth = indoorEntity.getReferenceMonth();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(refMonth);
		int month = calendar.get(Calendar.MONTH) + 1;
		
		Unit unit = entity.getQuotation().getUnit();
		PricingFrequency freq = unit.getPricingFrequency();
		if (freq != null){
			switch (month){
				case 1: 
					isNofield = !freq.isJan();
				break;
				case 2: 
					isNofield = !freq.isFeb();
				break;
				case 3: 
					isNofield = !freq.isMar();
				break;
				case 4: 
					isNofield = !freq.isApr();
				break;
				case 5: 
					isNofield = !freq.isMay();
				break;
				case 6: 
					isNofield = !freq.isJun();
				break;
				case 7: 
					isNofield = !freq.isJul();
				break;
				case 8: 
					isNofield = !freq.isAug();
				break;
				case 9: 
					isNofield = !freq.isSep();
				break;
				case 10: 
					isNofield = !freq.isOct();
				break;
				case 11: 
					isNofield = !freq.isNov();
				break;
				case 12: 
					isNofield = !freq.isDec();
				break;
			}
		}
		
		if (isNofield){
			return true;
		}
		
		Integer summerStartMonth = Integer.parseInt(systemConfigurationDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE).getValue());
		Integer summerEndMonth = Integer.parseInt(systemConfigurationDao.findByName(SystemConstant.BUS_PARAM_SUMMER_END_DATE).getValue());
		Integer winterStartMonth = Integer.parseInt(systemConfigurationDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE).getValue());
		Integer winterEndMonth = Integer.parseInt(systemConfigurationDao.findByName(SystemConstant.BUS_PARAM_WINTER_END_DATE).getValue());
		// check seasonality
		Integer seasonStartMonth = 0; 
		Integer seasonEndMonth = 0; 
		switch(unit.getSeasonality()){
			case 2: //case summer
				seasonStartMonth = summerStartMonth;
				seasonEndMonth = summerEndMonth;
				break;
			case 3: //case winter
				seasonStartMonth = winterStartMonth;
				seasonEndMonth = winterEndMonth;
				break;
			case 4: //case custom
				seasonStartMonth = unit.getSeasonStartMonth();
				break;
		}
		
		if(unit.getSeasonality() != 1 && seasonStartMonth != 0 && seasonEndMonth != 0){
			if (seasonStartMonth <= seasonEndMonth){
				if (seasonStartMonth > month || seasonEndMonth < month){
					isNofield = true; // not in seasonality range
				}
			}
			else if (seasonStartMonth > seasonEndMonth){ // across year
				if (seasonStartMonth > month && seasonEndMonth < month){
					isNofield = true; // not in seasonality range
				}
			}
			
		}
		
		// seasonal withdrawal
		if (entity.getQuotation().getSeasonalWithdrawal() != null){
			isNofield = true;
		}
		 
		return isNofield;
		
	}
	
	/**
	 * Procedure i-1
	 */
	public void procedureI1(QuotationRecord entity, DataConversionModel model) {
		IndoorQuotationRecord indoorEntity = entity.getIndoorQuotationRecord();
		
		// Update on 2017-08-22 for NoField IndoorQuotationRecord Revisit
		boolean isNofield = false;
		if (!indoorEntity.isNoField()){
			isNofield = this.checkIfNoField(entity);
		}
		
		// step 0
		model.setPreviousEditedNPrice(null);
		model.setPreviousEditedSPrice(null);
		
		// step 1
		if (entity.getQuotation().getUnit().getDataTransmissionRule() == null) return;
		
		if (entity.getQuotation().getUnit().getDataTransmissionRule().equals("A")) {
			// step 2
			if (entity.getAvailability() != null && entity.getAvailability() == 3) { // case 5
				model.setEditedNPrice(null);
				model.setEditedSPrice(null);
				procedureIIFr(entity, model);
			} 
			else if (isNofield){  // case 6
				model.setEditedNPrice(null);
				model.setEditedSPrice(null);
				indoorEntity.setNoField(true);
				return;
			}
			else {
				if (entity.getnPrice() != null && entity.getsPrice() != null) { // case 1
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getsPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() != null && entity.getsPrice() == null) { // case 2
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getnPrice());
					indoorEntity.setCopyPriceType(2);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() != null) { // case 3
					model.setEditedNPrice(entity.getsPrice());
					model.setEditedSPrice(entity.getsPrice());
					indoorEntity.setCopyPriceType(3);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() == null) { // case 4
					model.setEditedNPrice(null);
					model.setEditedSPrice(null);
					procedureIIFr(entity, model);
				}
			}
		} else if (entity.getQuotation().getUnit().getDataTransmissionRule().equals("B")) { // step 3
			// step 4
//			if (entity.getAvailability() != null && entity.getAvailability() == 3) { // case 5
//				model.setEditedNPrice(null);
//				model.setEditedSPrice(null);
//				procedureI1Step11(entity, model);
//			} else 
//			case 5 should run Follow Case 1-4
			if (isNofield){  // case 6
				model.setEditedNPrice(null);
				model.setEditedSPrice(null);
				indoorEntity.setNoField(true);
				return;
			}
			else {
				if (entity.getnPrice() != null && entity.getsPrice() != null) { // case 1
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getsPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() != null && entity.getsPrice() == null) { // case 2
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getnPrice());
					indoorEntity.setCopyPriceType(2);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() != null) { // case 3
					model.setEditedNPrice(entity.getsPrice());
					model.setEditedSPrice(entity.getsPrice());
					indoorEntity.setCopyPriceType(3);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() == null) { // case 4
					model.setEditedNPrice(null);
					model.setEditedSPrice(null);
					procedureIIFr(entity, model);
				} 
			}
		} else if (entity.getQuotation().getUnit().getDataTransmissionRule().equals("C")) { // step 5
			QuotationRecord backNoEntity = quotationRecordService.getBackNoRecord(entity);
			if (backNoEntity != null) {
				procedureI2(entity, backNoEntity, model);
			}
			
			// step 6
			model.setPreviousEditedNPrice(indoorEntity.getLastNPrice());
			model.setPreviousEditedSPrice(indoorEntity.getLastSPrice());
			
			if (entity.getAvailability() != null && entity.getAvailability() == 3) { // case 5
				model.setEditedNPrice(indoorEntity.getLastNPrice());
				model.setEditedSPrice(indoorEntity.getLastSPrice());
				indoorEntity.setIsCurrentPriceKeepNo(true);
				procedureIIFr(entity, model);
			} else if (isNofield){ // case 6
				model.setEditedNPrice(indoorEntity.getLastNPrice());
				model.setEditedSPrice(indoorEntity.getLastSPrice());
				indoorEntity.setCopyPriceType(1);
				indoorEntity.setNoField(true);
				indoorEntity.setIsCurrentPriceKeepNo(true);
				Quotation quotation = indoorEntity.getQuotation();
				Integer keepNoMonth = (quotation.getKeepNoMonth()==null?0:quotation.getKeepNoMonth()) + 1;
				//quotation.setTempKeepNoMonth(keepNoMonth);
				quotation.setKeepNoMonth(keepNoMonth);
				return;
			} else {
				if (entity.getnPrice() != null && entity.getsPrice() != null) { // case 1
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getsPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() != null && entity.getsPrice() == null) { // case 2
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getnPrice());
					indoorEntity.setCopyPriceType(2);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() != null) { // case 3
					model.setEditedNPrice(entity.getsPrice());
					model.setEditedSPrice(entity.getsPrice());
					indoorEntity.setCopyPriceType(3);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() == null) { // case 4
					model.setEditedNPrice(indoorEntity.getLastNPrice());
					model.setEditedSPrice(indoorEntity.getLastSPrice());
					indoorEntity.setIsCurrentPriceKeepNo(true);
					procedureIIFr(entity, model);
				}
			}
		} else if (entity.getQuotation().getUnit().getDataTransmissionRule().equals("D")) { // step 7
			// step 8
			if (entity.getAvailability() != null && entity.getAvailability() == 3) { // case 5
				model.setEditedNPrice(indoorEntity.getLastNPrice());
				model.setEditedSPrice(indoorEntity.getLastSPrice());
				procedureIIFr(entity, model);
			} else if (isNofield){ // case 6
				model.setEditedNPrice(null);
				model.setEditedSPrice(null);
				indoorEntity.setNoField(true);
				return;
			} else {
				if (entity.getnPrice() != null && entity.getsPrice() != null) { // case 1
					model.setEditedNPrice(indoorEntity.getLastNPrice());
					model.setEditedSPrice(indoorEntity.getLastSPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() != null && entity.getsPrice() == null) { // case 2
					model.setEditedNPrice(indoorEntity.getLastNPrice());
					model.setEditedSPrice(indoorEntity.getLastSPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() != null) { // case 3
					model.setEditedNPrice(indoorEntity.getLastNPrice());
					model.setEditedSPrice(indoorEntity.getLastSPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() == null) { // case 4
					model.setEditedNPrice(null);
					model.setEditedSPrice(null);
					procedureIIFr(entity, model);
				} 
			}
		} else if (entity.getQuotation().getUnit().getDataTransmissionRule().equals("E")) { // step 9
			// step 10
			if (entity.getAvailability() != null && entity.getAvailability() == 3) { // case 5
				model.setEditedNPrice(null);
				model.setEditedSPrice(null);
				procedureIIFr(entity, model);
			} else if (isNofield){ // case 6
				model.setEditedNPrice(null);
				model.setEditedSPrice(null);
				return;
			} else {
				if (entity.getnPrice() != null && entity.getsPrice() != null) { // case 1
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(entity.getsPrice());
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() != null && entity.getsPrice() == null) { // case 2
					model.setEditedNPrice(entity.getnPrice());
					model.setEditedSPrice(null);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() != null) { // case 3
					model.setEditedNPrice(entity.getsPrice());
					model.setEditedSPrice(null);
					indoorEntity.setCopyPriceType(3);
					procedureI1Step11(entity, model);
				} else if (entity.getnPrice() == null && entity.getsPrice() == null) { // case 4
					model.setEditedNPrice(null);
					model.setEditedSPrice(null);
					procedureIIFr(entity, model);
				} 
			}
		}
	}
	
	/**
	 * Procedure i-1 step 11
	 */
	public void procedureI1Step11(QuotationRecord entity, DataConversionModel model) {
		if (entity.getQuotation().getUnit().getStandardUOM() != null) {
			// step 12
			if (model.getEditedNPrice() == null && model.getEditedSPrice() == null) {
				procedureIIFr(entity, model);
			} else {
				// step 13
				if (entity.getUom() == null || entity.getUomValue() == null) {
					procedureIIFr(entity, model);
				} else {
					// step 14
					Double nPriceAfterUOMConversion = commonService.convertUom(entity.getQuotation().getUnit(), model.getEditedNPrice(), entity.getUom(), entity.getUomValue());
					entity.getIndoorQuotationRecord().setnPriceAfterUOMConversion(nPriceAfterUOMConversion);
					model.setEditedNPrice(nPriceAfterUOMConversion);
					
					Double sPriceAfterUOMConversion = commonService.convertUom(entity.getQuotation().getUnit(), model.getEditedSPrice(), entity.getUom(), entity.getUomValue());
					entity.getIndoorQuotationRecord().setsPriceAfterUOMConversion(sPriceAfterUOMConversion);
					model.setEditedSPrice(sPriceAfterUOMConversion);
					
					procedureIIFr(entity, model);
				}
			}
		} else {
			procedureIIFr(entity, model);
		}
	}

	/**
	 * Procedure i-2
	 */
	public void procedureI2(QuotationRecord entity, QuotationRecord backNoEntity, DataConversionModel model) {
		// step 1
		if (backNoEntity == null) return;
		
		// step 2
		if (backNoEntity.getAvailability() != null && backNoEntity.getAvailability() == 3) { // case 5
			model.setEditedNPrice(null);
			model.setEditedSPrice(null);
			return;
		} else {
			if (backNoEntity.getnPrice() != null && backNoEntity.getsPrice() != null) { // case 1
				model.setNewProductLastMonthNPrice(backNoEntity.getnPrice());
				model.setNewProductLastMonthSPrice(backNoEntity.getsPrice());
				procedureI2Step3(entity, backNoEntity, model);
			} else if (backNoEntity.getnPrice() != null && backNoEntity.getsPrice() == null) { // case 2
				model.setNewProductLastMonthNPrice(backNoEntity.getnPrice());
				model.setNewProductLastMonthSPrice(backNoEntity.getnPrice());
				//indoorEntity.setCopyPriceType(2);
				procedureI2Step3(entity, backNoEntity, model);
			} else if (backNoEntity.getnPrice() == null && backNoEntity.getsPrice() != null) { // case 3
				model.setNewProductLastMonthNPrice(backNoEntity.getsPrice());
				model.setNewProductLastMonthSPrice(backNoEntity.getsPrice());
				//indoorEntity.setCopyPriceType(3);
				procedureI2Step3(entity, backNoEntity, model);
			} else if (backNoEntity.getnPrice() == null && backNoEntity.getsPrice() == null) { // case 4
				model.setNewProductLastMonthNPrice(null);
				model.setNewProductLastMonthSPrice(null);
				return;
			}
		}
	}
	
	/**
	 * Procedure i-2 step 3
	 */
	public void procedureI2Step3(QuotationRecord entity, QuotationRecord backNoEntity, DataConversionModel model) {
		if (backNoEntity.getQuotation().getUnit().getStandardUOM() != null) {
			// step 4
			if (model.getNewProductLastMonthNPrice() == null && model.getNewProductLastMonthSPrice() == null) {
				return;
			} else {
				// step 5
				if (backNoEntity.getUom() == null || backNoEntity.getUomValue() == null) {
					return;
				} else {
					// step 14
					Double nPriceAfterUOMConversion = commonService.convertUom(backNoEntity.getQuotation().getUnit(), model.getNewProductLastMonthNPrice(), backNoEntity.getUom(), backNoEntity.getUomValue());
					model.setNewProductLastMonthNPrice(nPriceAfterUOMConversion);
					
					Double sPriceAfterUOMConversion = commonService.convertUom(backNoEntity.getQuotation().getUnit(), model.getNewProductLastMonthSPrice(), backNoEntity.getUom(), backNoEntity.getUomValue());
					model.setNewProductLastMonthSPrice(sPriceAfterUOMConversion);
					
					return;
				}
			}
		}
	}
	
	/**
	 * Procedure ii - FR
	 */
	public void procedureIIFr(QuotationRecord entity, DataConversionModel model) {
		// step 1
		if (entity.getQuotation().getUnit().isFrRequired()){
			// step 2
			if (model.getIsFRApplied()){
				// step 3
				if (entity.isProductChange()){
					procedureIIISplicingNormal(entity, model);
				} else {
					procedureIVRTN(entity, model);
				}
			}
			else{
				// step 4
				if (model.getIsReturnGoods()){
					// step 5
					if (model.getIsReturnNewGoods()){
						// step 6 + step 7
						if ((model.getIsUseFRAdmin() && model.getFrValue() != null) 
								&& (model.getEditedNPrice() != null && model.getEditedSPrice() != null)){
							// step 6 = yes and step 7 = yes
							entity.getQuotation().setUseFRAdmin(true);
							model.setIsUseFRAdmin(true);
							procedureIIFrAppliedAdminFieldFr(entity, model);
						}
						else{
							// step 8
							if (entity.isProductChange()){
								procedureIIISplicingFr(entity, model);
							} else {
								procedureIVRTN(entity, model);
							}
						}
					}
					else{
						procedureIIISplicingNormal(entity, model);
					}
				}
				else{
					// step 9 return goods in this month
					if (entity.getAvailability() != null && entity.getAvailability() == 1) { 
						// step 10
						if (entity.isProductChange()){
							// step 12 + step 13
							if (model.getEditedNPrice() != null && model.getEditedSPrice() != null
									&& (model.getIsUseFRAdmin() != null && 
										model.getIsUseFRAdmin() && model.getFrValue() != null || entity.getFr() != null)){
								//  step 14 => step 12 = true and step 13 = true
								if (entity.getOutlet().isUseFRAdmin() && entity.isConsignmentCounter()){
									// step 15
									if (entity.getFr() != null){
										// step 16
										entity.getQuotation().setReturnGoods(true);
										entity.getQuotation().setReturnNewGoods(true);
										entity.getQuotation().setUseFRAdmin(false);
										model.setIsUseFRAdmin(false);
										procedureIIFrAppliedAdminFieldFr(entity, model);
									}
									else{
										// step 25
										procedureIIFrStep25(entity, model);
									}
								}
								else{
									// step 17
									if (entity.getQuotation().getOutlet().isUseFRAdmin() && model.getFrValue() == null 
											&& entity.getFr() != null && entity.getFr() > 0){
										// step 26
										procedureIIFrStep26(entity, model);
									}
									else {
										// step 18
										if (!entity.getQuotation().getOutlet().isUseFRAdmin() && 
												model.getFrValue()!= null && model.getFrValue() > 0 && entity.getFr() == null){
											// step 25
											procedureIIFrStep25(entity, model);
										}
										else{
											// step 19
											if (entity.getOutlet().isUseFRAdmin() 
													&& model.getFrValue() != null && model.getFrValue() >= 0){
												// step 20
												entity.getQuotation().setReturnGoods(true);
												entity.getQuotation().setReturnNewGoods(true);
												entity.getQuotation().setUseFRAdmin(true);
												model.setIsUseFRAdmin(true);
												procedureIIFrAppliedAdminFieldFr(entity, model);
											}
											else{
												// step 21
												if (!entity.getOutlet().isUseFRAdmin()){
													// step 22
													entity.getQuotation().setReturnGoods(true);
													entity.getQuotation().setReturnNewGoods(true);
													entity.getQuotation().setUseFRAdmin(false);
													model.setIsUseFRAdmin(false);
													procedureIIFrAppliedAdminFieldFr(entity, model);
												}
												else{
													// step 25
													procedureIIFrStep25(entity, model);
												}
											}
										}
									}				
								}								
							}
							else{
								// step 23
								if (entity.getOutlet().isUseFRAdmin()){
									// step 24
									if (entity.isConsignmentCounter()){
										// step 25
										procedureIIFrStep25(entity, model);
									}
									else{
										// step 26
										procedureIIFrStep26(entity, model);
									}
								}
								else{
									// step 25
									procedureIIFrStep25(entity, model);
								}
							}
						}
						else{
							// step 11
							entity.getQuotation().setFRApplied(true);
							entity.getQuotation().setReturnGoods(true);
							entity.getQuotation().setReturnNewGoods(false);
							entity.getQuotation().setFrField(0.0);
							//entity.setFr(0.0);
							model.setFrValue(0.0);
							model.setIsFRApplied(true);
							model.setLastFRAppliedDate(new Date());
							entity.getQuotation().setIsFRFieldPercentage(true);
							entity.getQuotation().setLastFRAppliedDate(new Date());
							
							//IndoorQuotationRecord
							entity.getIndoorQuotationRecord().setFr(0.0);
							entity.getIndoorQuotationRecord().setFRPercentage(true);
							entity.getIndoorQuotationRecord().setApplyFR(true);
							
							procedureIVRTN(entity, model);							
						}
					}
					else{
						procedureIVRTN(entity, model);
					}
				}				
			}
		}
		else{
			procedureIIISplicingNormal(entity, model);
		}
		
	}
	
	public void procedureIIFrStep25(QuotationRecord entity, DataConversionModel model){
		entity.getQuotation().setReturnGoods(true);
		entity.getQuotation().setReturnNewGoods(true);
		entity.getQuotation().setUseFRAdmin(false);
		model.setIsUseFRAdmin(false);
		procedureIIISplicingFr(entity, model);
	}
	
	public void procedureIIFrStep26(QuotationRecord entity, DataConversionModel model){
		entity.getQuotation().setReturnGoods(true);
		entity.getQuotation().setReturnNewGoods(true);
		entity.getQuotation().setUseFRAdmin(true);
		model.setIsUseFRAdmin(true);
		procedureIIISplicingFr(entity, model);
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR)
	 */
	public void procedureIIFrAppliedAdminFieldFr(QuotationRecord entity, DataConversionModel model) {
		if (entity.getQuotation().getIsUseFRAdmin() != null){
			if (entity.getQuotation().getIsUseFRAdmin()){
				// step 4
				procedureIIFrAppliedAdminFieldFrStep4(entity, model);
			}
			else{
				// step 5
				procedureIIFrAppliedAdminFieldFrStep5(entity, model);
			}
		}
		else{
			// step 2
			if (entity.getOutlet().isUseFRAdmin()){
				// step 3
				if (entity.isConsignmentCounter()){
					// step 5
					procedureIIFrAppliedAdminFieldFrStep5(entity, model);
				}
				else{
					// step 4
					procedureIIFrAppliedAdminFieldFrStep4(entity, model);
				}
			}
			else{
				// step 5
				procedureIIFrAppliedAdminFieldFrStep5(entity, model);
			}
		}		
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 4
	 */
	public void procedureIIFrAppliedAdminFieldFrStep4(QuotationRecord entity, DataConversionModel model) {
		if (model.getFrValue() != null){
			// step 7
			procedureIIFrAppliedAdminFieldFrStep7(entity, model);
		}
		else{
			// step 6
			procedureIIFrAppliedAdminFieldFrStep6(entity, model);
		}
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 5
	 */
	public void procedureIIFrAppliedAdminFieldFrStep5(QuotationRecord entity, DataConversionModel model) {
		if (entity.getFr() != null){
			// step 7
			procedureIIFrAppliedAdminFieldFrStep7(entity, model);
		}
		else{
			// step 6
			procedureIIFrAppliedAdminFieldFrStep6(entity, model);
		}
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 6
	 */
	public void procedureIIFrAppliedAdminFieldFrStep6(QuotationRecord entity, DataConversionModel model) {
		if (entity.isProductChange()) {
			procedureIIISplicingFr(entity, model);
		} else {
			procedureIVRTN(entity, model);
		}
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 7
	 */
	public void procedureIIFrAppliedAdminFieldFrStep7(QuotationRecord entity, DataConversionModel model) {
		if (entity.getIndoorQuotationRecord().getLastNPrice() != null && entity.getIndoorQuotationRecord().getLastSPrice() != null) {
			// step 8
//			if (entity.getQuotation().getIsUseFRAdmin() && entity.getQuotation().getIsFRAdminPercentage() || 
//					!entity.getQuotation().getIsUseFRAdmin() && entity.isFRPercentage()) {
			if (model.getIsUseFRAdmin() != null && model.getIsUseFRAdmin() && model.getFrPercentage() || !model.getIsUseFRAdmin() && entity.isFRPercentage()){
				// step 9
				procedureIIFrAppliedAdminFieldFrStep9(entity, model);
			} else {
				// step 10
				procedureIIFrAppliedAdminFieldFrStep10(entity, model);
			}
		} else {
			// step 11
			//if (entity.getQuotation().getIsUsedFRPercentage()) {
			if (model.getIsUseFRAdmin() != null && model.getIsUseFRAdmin() && model.getFrPercentage() || !model.getIsUseFRAdmin() && entity.isFRPercentage()){
				// step 12
				procedureIIFrAppliedAdminFieldFrStep12(entity, model);
			} else {
				// step 13
				procedureIIFrAppliedAdminFieldFrStep13(entity, model);
			}
		}
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 9
	 */
	public void procedureIIFrAppliedAdminFieldFrStep9(QuotationRecord entity, DataConversionModel model) {
		
		Date today = new Date();
		today = commonService.getDateWithoutTime(today);
		
		//Date closingDate = entity.getAssignment().getSurveyMonth().getClosingDate().getClosingDate();
//		
//		entity.getQuotation().setTempLastFRAppliedDate(today);
//		entity.getQuotation().setTempIsFRApplied(true);
		entity.getQuotation().setLastFRAppliedDate(today);
		entity.getQuotation().setFRApplied(true);
		entity.getIndoorQuotationRecord().setApplyFR(true);
		if (model.getIsUseFRAdmin()){
			entity.getIndoorQuotationRecord().setFr(model.getFrValue());
			entity.getIndoorQuotationRecord().setFRPercentage(true);
		}
		else{
			entity.getQuotation().setFrField(entity.getFr());
			entity.getQuotation().setIsFRFieldPercentage(true);
			entity.getIndoorQuotationRecord().setFr(entity.getFr());
			entity.getIndoorQuotationRecord().setFRPercentage(true);
		}
		
		double editedPreviousNPrice = 0;
		double editedPreviousSPrice = 0;
		double x = 0.0;
		
		//if (entity.getQuotation().getIsUseFRAdmin()){
		if (model.getIsUseFRAdmin()){
			//x = entity.getQuotation().getFrAdmin() / 100.0;
			x = model.getFrValue() / 100.0;
		}
		else{
			x = entity.getFr() / 100.0;
		}
		//entity.getIndoorQuotationRecord().setFr(x);
		
		
		editedPreviousNPrice = model.getEditedNPrice() / (1 + x);
		editedPreviousSPrice = model.getEditedNPrice() / (1 + x) * entity.getIndoorQuotationRecord().getLastSPrice() / entity.getIndoorQuotationRecord().getLastNPrice();
		
		if (!Double.isFinite(editedPreviousNPrice)) {
			logger.error(String.format("Invalid Calculation -- previous edited N price: %s from (edited N price / (1 + FR / 100))"
					+ ", where (FR / 100) = %s ... indoor quotation record ID %s, quotation record ID %s", 
					editedPreviousNPrice, x, entity.getIndoorQuotationRecord().getId(), entity.getId()));
		}
		if (!Double.isFinite(editedPreviousSPrice)) {
			logger.error(String.format("Invalid Calculation -- previous edited S price: %s from (edited S price / (1 + FR / 100)"
					+ " * last S price / last N price), where (FR / 100) = %s, last N price = %s "
					+ "... indoor quotation record ID %s, quotation record ID %s", 
					editedPreviousSPrice, x, entity.getIndoorQuotationRecord().getLastNPrice(), 
					entity.getIndoorQuotationRecord().getId(), entity.getId()));
		}
		
		model.setPreviousEditedNPrice(editedPreviousNPrice);
		model.setPreviousEditedSPrice(editedPreviousSPrice);
	}
	
	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 10
	 */
	public void procedureIIFrAppliedAdminFieldFrStep10(QuotationRecord entity, DataConversionModel model) {
		//entity.getQuotation().setTempIsFRApplied(true);
		entity.getQuotation().setFRApplied(true);
		Date today = new Date();
		today = commonService.getDateWithoutTime(today);
		entity.getQuotation().setLastFRAppliedDate(today);
		entity.getIndoorQuotationRecord().setApplyFR(true);
		if (model.getIsUseFRAdmin()){
			entity.getIndoorQuotationRecord().setFr(model.getFrValue());
			entity.getIndoorQuotationRecord().setFRPercentage(false);
		}
		else{
			entity.getQuotation().setFrField(entity.getFr());
			entity.getQuotation().setIsFRFieldPercentage(false);
			entity.getIndoorQuotationRecord().setFr(entity.getFr());
			entity.getIndoorQuotationRecord().setFRPercentage(false);
		}
		
		double editedPreviousNPrice = 0;
		double editedPreviousSPrice = 0;
		Double x = model.getIsUseFRAdmin()? model.getFrValue() : entity.getFr();
		
		editedPreviousNPrice = (model.getEditedNPrice() - x);
		editedPreviousSPrice = (model.getEditedNPrice() - x) * entity.getIndoorQuotationRecord().getLastSPrice() / entity.getIndoorQuotationRecord().getLastNPrice();
		
		if (!Double.isFinite(editedPreviousSPrice)) {
			logger.error(String.format("Invalid Calculation -- previous edited S price: %s from ((edited N price - FR) * "
					+ "last S price / last N price), where last N price = %s "
					+ "... indoor quotation record ID %s, quotation record ID %s", 
					editedPreviousSPrice, 
					entity.getIndoorQuotationRecord().getLastNPrice(), 
					entity.getIndoorQuotationRecord().getId(), entity.getId()));
		}
		
		model.setPreviousEditedNPrice(editedPreviousNPrice);
		model.setPreviousEditedSPrice(editedPreviousSPrice);
	}

	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 12
	 */
	public void procedureIIFrAppliedAdminFieldFrStep12(QuotationRecord entity, DataConversionModel model) {
//		entity.getQuotation().setTempIsFRApplied(true);
//		Date today = new Date();
//		today = commonService.getDateWithoutTime(today);
//		entity.getQuotation().setTempLastFRAppliedDate(today);
//		entity.getIndoorQuotationRecord().setApplyFR(true);
//		entity.getIndoorQuotationRecord().setFRPercentage(true);
//		entity.getIndoorQuotationRecord().setFr(0D);
		entity.getQuotation().setFRApplied(true);
		Date today = new Date();
		today = commonService.getDateWithoutTime(today);
		entity.getQuotation().setLastFRAppliedDate(today);
		entity.getIndoorQuotationRecord().setApplyFR(true);
		if (model.getIsUseFRAdmin() && !entity.isConsignmentCounter()){
			entity.getIndoorQuotationRecord().setFr(model.getFrValue());
			entity.getIndoorQuotationRecord().setFRPercentage(model.getFrPercentage());
		}
		else{
			entity.getQuotation().setFrField(entity.getFr());
			entity.getIndoorQuotationRecord().setFr(entity.getFr());
			entity.getIndoorQuotationRecord().setFRPercentage(entity.isFRPercentage());
		}
		
		double editedPreviousNPrice = 0;
		double editedPreviousSPrice = 0;
		//double x = entity.getQuotation().getFrAdmin() / 100.0;
		Double x = model.getIsUseFRAdmin() && !entity.isConsignmentCounter()? model.getFrValue() : entity.getFr();
		x = x / 100.0;
		
		editedPreviousNPrice = model.getEditedNPrice() / (1 + x);
		editedPreviousSPrice = model.getEditedNPrice() / (1 + x) ;
		
		if (!Double.isFinite(editedPreviousNPrice)) {
			logger.error(String.format("Invalid Calculation -- previous edited N price: %s from (edited N price / (1 + FR / 100))"
					+ ", where (FR / 100) = %s ... indoor quotation record ID %s, quotation record ID %s", 
					editedPreviousNPrice, 
					x, entity.getIndoorQuotationRecord().getId(), entity.getId()));
		}
		if (!Double.isFinite(editedPreviousSPrice)) {
			logger.error(String.format("Invalid Calculation -- previous edited S price: %s from (edited N price / (1 + FR / 100))"
					+ ", where (FR / 100) = %s ... indoor quotation record ID %s, quotation record ID %s", 
					editedPreviousNPrice, 
					x, entity.getIndoorQuotationRecord().getId(), entity.getId()));
		}
		
		model.setPreviousEditedNPrice(editedPreviousNPrice);
		model.setPreviousEditedSPrice(editedPreviousSPrice);
	}

	/**
	 * Procedure ii - FR applied (Admin/Field FR) Step 13
	 */
	public void procedureIIFrAppliedAdminFieldFrStep13(QuotationRecord entity, DataConversionModel model) {
//		entity.getQuotation().setTempIsFRApplied(true);
//		Date today = new Date();
//		today = commonService.getDateWithoutTime(today);
//		entity.getQuotation().setTempLastFRAppliedDate(today);
//		entity.getIndoorQuotationRecord().setApplyFR(true);
//		entity.getIndoorQuotationRecord().setFRPercentage(false);
//		entity.getIndoorQuotationRecord().setFr(0D);
		entity.getQuotation().setFRApplied(true);
		Date today = new Date();
		today = commonService.getDateWithoutTime(today);
		entity.getQuotation().setLastFRAppliedDate(today);
		entity.getIndoorQuotationRecord().setApplyFR(true);
		if (model.getIsUseFRAdmin() && !entity.isConsignmentCounter()){
			entity.getIndoorQuotationRecord().setFr(model.getFrValue());
			entity.getIndoorQuotationRecord().setFRPercentage(model.getFrPercentage());
		}
		else{
			entity.getQuotation().setFrField(entity.getFr());
			entity.getIndoorQuotationRecord().setFr(entity.getFr());
			entity.getIndoorQuotationRecord().setFRPercentage(entity.isFRPercentage());
		}
		
		double editedPreviousNPrice = 0;
		double editedPreviousSPrice = 0;
		//double x = entity.getQuotation().getFrAdmin();
		Double x = model.getIsUseFRAdmin() && !entity.isConsignmentCounter()? model.getFrValue() : entity.getFr();
		
		editedPreviousNPrice = model.getEditedNPrice() - x;
		editedPreviousSPrice = (model.getEditedNPrice() - x);
		
		model.setPreviousEditedNPrice(editedPreviousNPrice);
		model.setPreviousEditedSPrice(editedPreviousSPrice);
	}
	
	/**
	 * Procedure iii - splicing normal
	 */
	public void procedureIIISplicingNormal(QuotationRecord entity, DataConversionModel model) {
		IndoorQuotationRecord indoor = entity.getIndoorQuotationRecord();
		QuotationRecord backNo = quotationRecordService.getBackNoRecord(entity);
		Double backNoNPrice = model.getNewProductLastMonthNPrice();
		Double backNoSPrice = model.getNewProductLastMonthSPrice();
		
		// step 1
		if (entity.getQuotation().getUnit().isSpicingRequired()) {
			// step 2
			boolean backNoPeculiar = true;
			if (backNo != null){
				backNoPeculiar = backNo.isSPricePeculiar();
			}
			
			if (entity.isSPricePeculiar() || backNoPeculiar) {
				procedureIVRTN(entity, model);
			} else {
				// step 3
				// splicing normal
				// step 4
				if (entity.isProductChange()) {
					// step 5					
					if (backNoNPrice != null && backNoSPrice != null) {
						// step 10
						if (indoor.getLastNPrice() != null && indoor.getLastSPrice() != null) {
							// step 11
							model.setPreviousEditedNPrice(backNoNPrice);
							double previousEditedSPrice = backNoNPrice * indoor.getLastSPrice() / indoor.getLastNPrice();
							
							if (!Double.isFinite(previousEditedSPrice)) {
								logger.error(String.format("Invalid Calculation -- previous edited S price: %s from (back no N price * "
										+ "last S price / last N price), where last N price = %s "
										+ "... indoor quotation record ID %s, quotation record ID %s", 
										previousEditedSPrice, 
										indoor.getLastNPrice(), 
										indoor.getId(), entity.getId()));
							}
							
							model.setPreviousEditedSPrice(previousEditedSPrice);
							indoor.setSpicing(true);
						} else {
							// step 12
							model.setPreviousEditedNPrice(backNoNPrice);
							model.setPreviousEditedSPrice(backNoSPrice);
							indoor.setSpicing(true);
						}
					} else {
						// step 6
						procedureIIISplicingFrStep6(entity, model);
					}
				} else {
					procedureIVRTN(entity, model);
				}
			}
		} else {
			procedureIVRTN(entity, model);
		}
	}
	
	/**
	 * Procedure iii - splicing fr
	 */
	public void procedureIIISplicingFr(QuotationRecord entity, DataConversionModel model) {
		// step 1
		if (entity.getQuotation().getUnit().isSpicingRequired()) {
			QuotationRecord backNo = quotationRecordService.getBackNoRecord(entity);
			boolean backNoPeculiar = true;
			if (backNo != null){
				backNoPeculiar = backNo.isSPricePeculiar();
			}
			
			// step 2
			if (entity.isSPricePeculiar() || backNoPeculiar) {
				procedureIVRTN(entity, model);
			} else {
				// step 3
				// splicing fr
				// step 6
				procedureIIISplicingFrStep6(entity, model);
			}
		} else {
			procedureIVRTN(entity, model);
		}
	}
	
	/**
	 * Procedure iii - splicing fr step 6
	 */
	public void procedureIIISplicingFrStep6(QuotationRecord entity, DataConversionModel model) {
		IndoorQuotationRecord indoor = entity.getIndoorQuotationRecord();
		if (model.getPreviousEditedNPrice() != null && model.getPreviousEditedSPrice() != null) {
			// step 7
			if (model.getEditedNPrice() != null && model.getEditedSPrice() != null) {
				// step 8
				model.setPreviousEditedNPrice(model.getEditedNPrice());
				double previousEditedSPrice = model.getEditedNPrice() * indoor.getLastSPrice() / indoor.getLastNPrice();
				
				if (!Double.isFinite(previousEditedSPrice)) {
					logger.error(String.format("Invalid Calculation -- previous edited S price: %s from (edited N price * "
							+ "last S price / last N price), where last N price = %s "
							+ "... indoor quotation record ID %s, quotation record ID %s", 
							previousEditedSPrice, 
							indoor.getLastNPrice(), 
							indoor.getId(), entity.getId()));
				}
				
				model.setPreviousEditedSPrice(previousEditedSPrice);
				indoor.setSpicing(true);
				return;
			} else {
				procedureIVRTN(entity, model);
			}
		} else {
			// step 9
			indoor.setSpicing(true);
			model.setPreviousEditedNPrice(null);
			model.setPreviousEditedSPrice(null);
			return;
		}
	}
	
	/**
	 * Procedure iv - RTN
	 */
	public void procedureIVRTN(QuotationRecord entity, DataConversionModel model) {
		IndoorQuotationRecord indoor = entity.getIndoorQuotationRecord();
		Integer season = entity.getQuotation().getUnit().getSeasonality();
		
		// step 1
		if (entity.getAvailability() != null && entity.getAvailability() == 1) {
			entity.getQuotation().setKeepNoMonth(0);
			return;
		} else {
//			Integer keepNoMonth = entity.getQuotation().getKeepNoMonth();
//			entity.getQuotation().setTempKeepNoMonth((keepNoMonth==null?0:keepNoMonth) + 1);
			Integer keepNoMonth = model.getKeepNoMonth();
			entity.getQuotation().setKeepNoMonth((keepNoMonth==null?0:keepNoMonth) + 1);
			
			// step 2
			if (indoor.getLastNPrice() != null && indoor.getLastSPrice() != null) {
				// step 3
				if (season == 2 || season == 3) {
					// step 10
					Calendar referenceCal = Calendar.getInstance();
					referenceCal.setTime(indoor.getReferenceMonth());
					int referenceMonth = referenceCal.get(Calendar.MONTH) + 1;
					
					// winter
					if (season == 3) {
						// step 11
						SystemConfiguration winterStartDateConfig = systemConfigurationDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE);
						int referenceSurveyMonth = Integer.parseInt(winterStartDateConfig.getValue()) + 2;
						if (referenceSurveyMonth == referenceMonth
								&& !entity.getQuotation().isReturnGoods()
								&& !entity.getQuotation().isReturnNewGoods()
								&& indoor.getLastNPrice() > indoor.getLastSPrice()) {
							// step 7
//							entity.setnPrice(indoor.getLastNPrice());
//							entity.setsPrice(indoor.getLastSPrice());
							procedureIVRTNStep7(entity, model);
							
							return;
						} else {
							// step 12
							referenceSurveyMonth = Integer.parseInt(winterStartDateConfig.getValue()) + 3;
							if (referenceSurveyMonth == referenceMonth
									&& !entity.getQuotation().isReturnGoods()
									&& !entity.getQuotation().isLastSeasonReturnGoods()) {
								// step 9
//								entity.setnPrice(null);
//								entity.setsPrice(null);
								procedureIVRTNStep9(entity, model);
							} else {
								return;
							}
						}
					} else {
						// step 13
						// step 14
						SystemConfiguration summerStartDateConfig = systemConfigurationDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE);
						int referenceSurveyMonth = Integer.parseInt(summerStartDateConfig.getValue()) + 1;
						if (referenceSurveyMonth == referenceMonth
								&& !entity.getQuotation().isReturnGoods()
								&& !entity.getQuotation().isReturnNewGoods()
								&& indoor.getLastNPrice() > indoor.getLastSPrice()) {
							// step 7
//							entity.setnPrice(indoor.getLastNPrice());
//							entity.setsPrice(indoor.getLastSPrice());
							procedureIVRTNStep7(entity, model);							
							
							return;
						} else {
							// step 15
							referenceSurveyMonth = Integer.parseInt(summerStartDateConfig.getValue()) + 2;
							if (referenceSurveyMonth == referenceMonth
									&& !entity.getQuotation().isReturnGoods()
									&& !entity.getQuotation().isLastSeasonReturnGoods()) {
								// step 9
//								entity.setnPrice(null);
//								entity.setsPrice(null);
								procedureIVRTNStep9(entity, model);
							} else {
								return;
							}
						}
					}
				} else {
					// step 4
					if (entity.getQuotation().getUnit().getRtnPeriod() != null){
						if (entity.getQuotation().getUnit().getRtnPeriod() > 0) {
							// step 5
							if (entity.getQuotation().getKeepNoMonth() <= entity.getQuotation().getUnit().getRtnPeriod()) {
								return;
							} else {
								// step 6
								if ((entity.getQuotation().getKeepNoMonth() - entity.getQuotation().getUnit().getRtnPeriod()) == 1
										&& model.getEditedNPrice() > model.getEditedSPrice()) {
									// step 7
									//entity.setnPrice(indoor.getLastNPrice());
									//entity.setsPrice(indoor.getLastSPrice());
									procedureIVRTNStep7(entity, model);
									return;
								} else {
									// step 8
									if (entity.getAvailability() != null && entity.getAvailability() == 5 || 
											(entity.getQuotation().getKeepNoMonth() - entity.getQuotation().getUnit().getRtnPeriod()) > 2) {
										return;
									} else {
										// step 9
										if(!"RUA".equals(entity.getQuotation().getStatus())){
											procedureIVRTNStep9(entity, model);
										}
//										entity.setnPrice(null);
//										entity.setsPrice(null);
										return;
									}
								}
							}
						}
						else if (entity.getQuotation().getUnit().getRtnPeriod() < 0){
							procedureIVRTNStep9(entity, model);
							return;
						}					
						else {
							return;
						}
					}					
				}
			} else {
				return;
			}
		}
	}
	
	public void procedureIVRTNStep7(QuotationRecord entity, DataConversionModel model) {
		IndoorQuotationRecord indoor = entity.getIndoorQuotationRecord();
		model.setEditedNPrice(indoor.getLastNPrice());
		model.setEditedSPrice(indoor.getLastNPrice());
	}
	
	public void procedureIVRTNStep9 (QuotationRecord entity, DataConversionModel model) {
		model.setEditedNPrice(null);
		model.setEditedSPrice(null);
	}
	
	// Update on 2017-08-22 for Quotation FR Re convert 
	public void clearDataBeforeUpdate (IndoorQuotationRecord entity, QuotationRecord qr){
		if (qr.getQuotation().getUnit().isFrRequired()){
			Date today = new Date();
			today = commonService.getDateWithoutTime(today);
			
			if (!today.after(qr.getAssignment().getSurveyMonth().getClosingDate().getClosingDate())){
				Quotation quotation = qr.getQuotation();
				quotation.setUseFRAdmin(quotation.isTempIsUseFRAdmin());
				quotation.setFRApplied(quotation.isTempIsFRApplied() == null ? false : quotation.isTempIsFRApplied());
				quotation.setReturnGoods(quotation.isTempIsReturnGoods() == null ? false : quotation.isTempIsReturnGoods());
				quotation.setReturnNewGoods(quotation.isTempIsReturnNewGoods() == null ? false : quotation.isTempIsReturnNewGoods());
				quotation.setIsUsedFRPercentage(quotation.isTempFRPercentage());
				if (quotation.isTempIsUseFRAdmin() != null && quotation.isTempIsUseFRAdmin()){
					quotation.setFrAdmin(quotation.getTempFRValue());
					quotation.setIsFRAdminPercentage(quotation.isTempFRPercentage());
					quotation.setIsFRFieldPercentage(null);
					quotation.setFrField(null);
				} else {
					quotation.setFrAdmin(null);
					quotation.setIsFRAdminPercentage(null);
					quotation.setIsFRFieldPercentage(quotation.isTempFRPercentage());
					quotation.setFrField(quotation.getTempFRValue());
				}
			}
		}
		
		entity.setProductChange(false);
		entity.setNewProduct(false);
		entity.setOriginalNPrice(null);
		entity.setOriginalSPrice(null);
		entity.setnPriceAfterUOMConversion(null);
		entity.setsPriceAfterUOMConversion(null);
		entity.setComputedNPrice(null);
		entity.setComputedSPrice(null);
		entity.setCurrentNPrice(null);
		entity.setCurrentSPrice(null);
		entity.setNullCurrentNPrice(false);
		entity.setNullCurrentSPrice(false);
		entity.setPreviousNPrice(null);
		entity.setPreviousSPrice(null);
		entity.setNullPreviousNPrice(false);
		entity.setNullPreviousSPrice(false);
		entity.setBackNoLastNPirce(null);
		entity.setBackNoLastSPrice(null);
		entity.setLastNPrice(null);
		entity.setLastSPrice(null);
		entity.setLastPriceDate(null);
		entity.setCopyPriceType(null);
		entity.setCopyLastPriceType(null);
		entity.setRemark(null);
		entity.setIsCurrentPriceKeepNo(null);
		entity.setRUA(false);
		entity.setRuaDate(null);
		entity.setProductNotAvailable(false);
		entity.setProductNotAvailableFrom(null);
		entity.setSpicing(false);
		entity.setOutlier(false);
		entity.setOutlierRemark(null);
		entity.setFr(null);
		entity.setApplyFR(false);
		entity.setFRPercentage(false);
		entity.setFirmRemark(null);
		entity.setCategoryRemark(null);
		entity.setQuotationRemark(null);
	}
	
}
	
