package capi.service.assignmentManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.QuotationRecordDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UnitStatisticDao;
import capi.entity.OnSpotValidation;
import capi.entity.QuotationRecord;
import capi.entity.SystemConfiguration;
import capi.entity.UnitStatistic;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.service.CommonService;

@Service("OnSpotValidationService")
public class OnSpotValidationService {
	@Autowired
	QuotationRecordService recordService;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private SystemConfigurationDao systemConfigurationDao;

	@Autowired
	private UnitStatisticDao statDao;
	
	@Autowired
	CommonService commonService;
	
	/**
	 * Validate
	 */
	public List<String> validate(QuotationRecord entity) throws Exception {
		List<String> messages = new ArrayList<String>();

		if (entity.getQuotation() == null || entity.getQuotation().getUnit() == null || entity.getQuotation().getUnit().getOnSpotValidation() == null) return messages;
		
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		
		if(!entity.isSPricePeculiar()){
			if (model.isUom1Reported() && uom1Reported(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1));
			
			if (model.isUom2GreaterZero() && uom2GreaterZero(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2));
		
			if (model.isNPriceGreaterZero() && NPriceGreaterZero(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3));
			
			if (model.isSPriceGreaterZero() && SPriceGreaterZero(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4));
		}
		
		if (model.isProvideReasonPRNPrice() || model.isProvideReasonPRNPriceLower() || model.isProvideReasonPRSPrice() || model.isProvideReasonPRSPriceLower()
				|| model.isProvideReasonPRSPriceSD() || model.isProvideReasonPRNPriceSD()) {
			Double[] percentages = calculatePricePercentage(entity);
			Double historyNPricePercent = null;
			Double historySPricePercent = null;
			Double historyNPricePercent2 = null;
			Double historySPricePercent2 = null;
			if (percentages != null) {
				historyNPricePercent = percentages[0];
				historySPricePercent = percentages[1];
				historyNPricePercent2 = percentages[2];
				historySPricePercent2 = percentages[3];
			}
			
			if (model.isProvideReasonPRNPrice() && provideReasonPRNPrice(entity, historyNPricePercent))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_5));
			
			if (model.isProvideReasonPRNPriceLower() && provideReasonPRNPriceLower(entity, historyNPricePercent))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_6));
			
			if (model.isProvideReasonPRSPrice() && provideReasonPRSPrice(entity, historySPricePercent))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_7));
			
			if (model.isProvideReasonPRSPriceLower() && provideReasonPRSPriceLower(entity, historySPricePercent))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_8));
			

			if (model.isProvideReasonPRSPriceSD() && provideReasonPRSPriceSD(entity, historySPricePercent2))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_15));

			if (model.isProvideReasonPRNPriceSD() && provideReasonPRNPriceSD(entity, historyNPricePercent2))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_16));
		}
		
		if (model.isProvideReasonSPriceMaxMin() || model.isProvideReasonNPriceMaxMin()) {
			UnitStatistic stat = loadStat(entity);
			if (stat != null) {
				if (model.isProvideReasonSPriceMaxMin() && provideReasonSPriceMaxMin(entity, stat))
					messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_9));
				
				if (model.isProvideReasonNPriceMaxMin() && provideReasonNPriceMaxMin(entity, stat))
					messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_10));
			}
		}
		
		if (model.isnPriceGreaterSPrice() && nPriceGreaterSPrice(entity))
			messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11));
		
		if (model.isProvideRemarkForNotSuitablePrice() && provideRemarkForNotSuitablePrice(entity))
			messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12));
		
		if (model.isProvideReasonSPriceSD() && provideReasonSPriceSD(entity))
			messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_17));
		
		if (model.isProvideReasonNPriceSD() && provideReasonNPriceSD(entity))
			messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_18));
		/*
		if (entity.getQuotation().getUnit().getProductCycle()!=null && !entity.isProductChange()){
			if (entity.getQuotation().getLastProductChangeDate()==null){
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14));
			} else if (commonService.countDifferencesBetweenMonth(entity.getReferenceDate(), entity.getQuotation().getLastProductChangeDate())
					> entity.getQuotation().getUnit().getProductCycle()){
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14));
			}
		}*/
		
		return messages;
	}
	
	public List<String> validateBackNo(QuotationRecord entity) throws Exception {
		List<String> messages = new ArrayList<String>();

		if (entity.getQuotation() == null || entity.getQuotation().getUnit() == null || entity.getQuotation().getUnit().getOnSpotValidation() == null) return messages;
		
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		
		if(!entity.isSPricePeculiar()){
			if (model.isUom1Reported() && uom1Reported(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1));
			
			if (model.isUom2GreaterZero() && uom2GreaterZero(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2));
			
			if (model.isNPriceGreaterZero() && NPriceGreaterZero(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3));
			
			if (model.isSPriceGreaterZero() && SPriceGreaterZero(entity))
				messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4));
		}	
		
		if (model.isnPriceGreaterSPrice() && nPriceGreaterSPrice(entity))
			messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11));
		
		if (model.isProvideRemarkForNotSuitablePrice() && provideRemarkForNotSuitablePrice(entity))
			messages.add(getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12));
		
		return messages;
	}
	
	/**
	 * Get message
	 */
	public String getMessage(String name) {
		SystemConfiguration config = systemConfigurationDao.findByName(name);
		return config == null ? name : config.getValue();
	}
	
	/**
	 * Load statistics
	 */
	private UnitStatistic loadStat(QuotationRecord entity) {
		if (entity.getQuotation() == null || entity.getQuotation().getUnit() == null) return null;
		
		if (entity.getAssignment() == null || entity.getAssignment().getSurveyMonth() == null) return null;
		Date referenceMonth = entity.getAssignment().getSurveyMonth().getReferenceMonth();
		Date lastReferenceMonth = DateUtils.addMonths(referenceMonth, -2);
		
		//statDao.calculateUnitStatistic(lastReferenceMonth);
		return statDao.getByUnitAndReferenceMonth(entity.getQuotation().getUnit().getId(), lastReferenceMonth);
	}
	
	/**
	 * Unit of measurement (UOM1) should be reported
	 */
	public boolean uom1Reported(QuotationRecord entity) {
		return entity.getUom() == null;
	}
	
	/**
	 * Unit of measurement (UOM2) >= 0
	 */
	public boolean uom2GreaterZero(QuotationRecord entity) {
		return entity.getUomValue() == null || entity.getUomValue() <= 0;
	}

	/**
	 * Normal Price (N Price) > 0
	 */
	public boolean NPriceGreaterZero(QuotationRecord entity) {
		if(entity.getQuotation().getUnit().isNPriceMandatory() == true){
			return entity.getnPrice() == null || entity.getnPrice() <= 0;
		}
		return entity.getnPrice() != null && entity.getnPrice() <= 0;
	}

	/**
	 * Special Price (S Price) > 0
	 */
	public boolean SPriceGreaterZero(QuotationRecord entity) {
		if(entity.getQuotation().getUnit().isSPriceMandatory() == true){
			return entity.getsPrice() == null || entity.getsPrice() <= 0;
		}
		return entity.getsPrice() != null && entity.getsPrice() <= 0;
	}

	/**
	 * Calculate price percentage
	 */
	public Double[] calculatePricePercentage(QuotationRecord entity) {
		Double historyNPricePercent = null;
		Double historySPricePercent = null;
		Double historyNPricePercent2 = null;
		Double historySPricePercent2 = null;
		
		List<QuotationRecordHistoryDateModel> histories = recordService.getHistoryDatesAndRecordId(entity);
		
		if (histories.size() == 0) return null;
		
		QuotationRecord historyEntity = quotationRecordDao.findById(histories.get(0).getId());
		
		if (entity.getProduct() == null || historyEntity.getProduct() == null) return null;
		
		if (entity.getProduct().getId().intValue() != historyEntity.getProduct().getId().intValue()
				|| entity.getOutlet().getId().intValue() != historyEntity.getOutlet().getId().intValue()) return null;
		
		if (entity.getnPrice() != null && historyEntity.getnPrice() != null) {
			double currentNPriceUOM = 0;
			double historyNPriceUOM = 0;
			if (entity.getQuotation().getUnit() != null && entity.getQuotation().getUnit().getStandardUOM() != null) {
				currentNPriceUOM = commonService.convertUom(entity.getQuotation().getUnit(), entity.getnPrice(), entity.getUom(), entity.getUomValue());
			} else {
				currentNPriceUOM = entity.getnPrice();
			}
			if (historyEntity.getQuotation().getUnit() != null && historyEntity.getQuotation().getUnit().getStandardUOM() != null) {
				historyNPriceUOM = commonService.convertUom(historyEntity.getQuotation().getUnit(), historyEntity.getnPrice(), historyEntity.getUom(), historyEntity.getUomValue());
			} else {
				historyNPriceUOM = historyEntity.getnPrice();
			}
			historyNPricePercent = Math.round(((currentNPriceUOM - historyNPriceUOM) / historyNPriceUOM * 100) * 100) / 100.0;
			historyNPricePercent2 = Math.round((currentNPriceUOM / historyNPriceUOM * 100) * 100) / 100.0;
		}
		
		if (entity.getsPrice() != null && historyEntity.getsPrice() != null) {
			double currentSPriceUOM = 0;
			double historySPriceUOM = 0;
			if (entity.getQuotation().getUnit() != null && entity.getQuotation().getUnit().getStandardUOM() != null) {
				currentSPriceUOM = commonService.convertUom(entity.getQuotation().getUnit(), entity.getsPrice(), entity.getUom(), entity.getUomValue());
			} else {
				currentSPriceUOM = entity.getsPrice();
			}
			if (historyEntity.getQuotation().getUnit() != null && historyEntity.getQuotation().getUnit().getStandardUOM() != null) {
				historySPriceUOM = commonService.convertUom(historyEntity.getQuotation().getUnit(), historyEntity.getsPrice(), historyEntity.getUom(), historyEntity.getUomValue());
			} else {
				historySPriceUOM = historyEntity.getsPrice();
			}
			historySPricePercent = Math.round(((currentSPriceUOM - historySPriceUOM) / historySPriceUOM * 100) * 100) / 100.0;
			historySPricePercent2 = Math.round((currentSPriceUOM / historySPriceUOM * 100) * 100) / 100.0;
		}
		
		return new Double[] {historyNPricePercent, historySPricePercent, historyNPricePercent2, historySPricePercent2};
	}
	
	/**
	 * Provide Reason if percentage change of N Price > threshold
	 */
	public boolean provideReasonPRNPrice(QuotationRecord entity, Double historyNPricePercent) {
		if (historyNPricePercent == null) return false;
		
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		if (model.getPrNPriceThreshold() == null) return false;
		if (historyNPricePercent > model.getPrNPriceThreshold() && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}
	
	/**
	 * Provide Reason if percentage change of N Price < threshold
	 */
	public boolean provideReasonPRNPriceLower(QuotationRecord entity, Double historyNPricePercent) {
		if (historyNPricePercent == null) return false;
		
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		if (model.getPrNPriceLowerThreshold() == null) return false;
		if (historyNPricePercent < model.getPrNPriceLowerThreshold() && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}
	
	/**
	 * Provide Reason if percentage change of S Price > threshold
	 */
	public boolean provideReasonPRSPrice(QuotationRecord entity, Double historySPricePercent) {
		if (historySPricePercent == null) return false;
		
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		if (model.getPrSPriceThreshold() == null) return false;
		if (historySPricePercent > model.getPrSPriceThreshold() && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}
	
	/**
	 * Provide Reason if percentage change of S Price < threshold
	 */
	public boolean provideReasonPRSPriceLower(QuotationRecord entity, Double historySPricePercent) {
		if (historySPricePercent == null) return false;
		
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		if (model.getPrSPriceLowerThreshold() == null) return false;
		if (historySPricePercent < model.getPrSPriceLowerThreshold() && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}
	
	/**
	 * Provide Reason if S price is > max or < min S price of the same Variety in the last month
	 */
	public boolean provideReasonSPriceMaxMin(QuotationRecord entity, UnitStatistic stat) {
		if (stat == null || entity.getsPrice() == null) return false;
		if ((entity.getsPrice() > stat.getMaxSPrice() || entity.getsPrice() < stat.getMinSPrice()) && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}

	/**
	 * Provide Reason if N price is > max or < min N price of the same Variety in the last month
	 */
	public boolean provideReasonNPriceMaxMin(QuotationRecord entity, UnitStatistic stat) {
		if (stat == null || entity.getnPrice() == null) return false;
		if ((entity.getnPrice() > stat.getMaxNPrice() || entity.getnPrice() < stat.getMinNPrice()) && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}

	/**
	 * Normal price (N Price) >= Special Price (S Price)
	 */
	public boolean nPriceGreaterSPrice(QuotationRecord entity) {
		if (entity.getnPrice() == null || entity.getsPrice() == null) return false;
		if (entity.getnPrice() < entity.getsPrice())
			return true;
		else
			return false;
	}

	/**
	 * If "Not suitable" is chosen for N and S price, remarks have to be provided.
	 */
	public boolean provideRemarkForNotSuitablePrice(QuotationRecord entity) {
		if (entity.isSPricePeculiar() && StringUtils.isWhitespace(entity.getRemark()))
			return true;
		else
			return false;
	}

	/**
	 * Provide Reason if percentage change of S price exceed the ranges of (mean +- SD)
	 */
	public boolean provideReasonPRSPriceSD(QuotationRecord entity, Double historySPricePercent) {
		if (historySPricePercent == null) return false;
		if (entity.getQuotation() == null || entity.getQuotation().getUnit() == null) return false;
		if (entity.getQuotation().getUnit().getConsolidatedSPRMean() == null || entity.getQuotation().getUnit().getConsolidatedSPRSD() == null) return false;
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		double max = entity.getQuotation().getUnit().getConsolidatedSPRMean() + model.getPrSPriceSDPositive() * entity.getQuotation().getUnit().getConsolidatedSPRSD();
		double min = entity.getQuotation().getUnit().getConsolidatedSPRMean() - model.getPrSPriceSDNegative() * entity.getQuotation().getUnit().getConsolidatedSPRSD();
		
		if ((historySPricePercent < min || historySPricePercent > max) && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}

	/**
	 * Provide Reason if percentage change of N price exceed the ranges of (mean +- SD)
	 */
	public boolean provideReasonPRNPriceSD(QuotationRecord entity, Double historyNPricePercent) {
		if (historyNPricePercent == null) return false;
		if (entity.getQuotation() == null || entity.getQuotation().getUnit() == null) return false;
		if (entity.getQuotation().getUnit().getConsolidatedNPRMean() == null|| entity.getQuotation().getUnit().getConsolidatedNPRSD() == null) return false;
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		double max = entity.getQuotation().getUnit().getConsolidatedNPRMean() + model.getPrNPriceSDPositive() * entity.getQuotation().getUnit().getConsolidatedNPRSD();
		double min = entity.getQuotation().getUnit().getConsolidatedNPRMean() - model.getPrNPriceSDNegative() * entity.getQuotation().getUnit().getConsolidatedNPRSD();
		
		if ((historyNPricePercent < min || historyNPricePercent > max) && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}

	/**
	 * Provide Reason if S price exceed the ranges of
	 */
	public boolean provideReasonSPriceSD(QuotationRecord entity) {
		if (entity.getsPrice() == null) return false;
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		double min = model.getsPriceSDNegative();
		double max = model.getsPriceSDPositive();
		
		if ((entity.getsPrice() < min || entity.getsPrice() > max) && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}

	/**
	 * Provide Reason if N price exceed the ranges of
	 */
	public boolean provideReasonNPriceSD(QuotationRecord entity) {
		if (entity.getnPrice() == null) return false;
		OnSpotValidation model = entity.getQuotation().getUnit().getOnSpotValidation();
		double min = model.getnPriceSDNegative();
		double max = model.getnPriceSDPositive();
		
		if ((entity.getnPrice() < min || entity.getnPrice() > max) && StringUtils.isWhitespace(entity.getReason()))
			return true;
		else
			return false;
	}
}
