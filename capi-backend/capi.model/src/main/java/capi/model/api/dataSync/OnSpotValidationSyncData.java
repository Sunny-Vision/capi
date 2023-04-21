package capi.model.api.dataSync;

import java.util.Date;

public class OnSpotValidationSyncData {
	private Integer unitId;
	
	private boolean isUom1Reported;
	
	private boolean isUom2GreaterZero;
	
	private boolean isNPriceGreaterZero;
	
	private boolean isSPriceGreaterZero;
	
	private boolean provideReasonPRNPrice;
	
	private Double prNPriceThreshold;
	
	private boolean provideReasonPRSPrice;
	
	private Double prSPriceThreshold;
	
	private boolean provideReasonSPriceMaxMin;
	
	private boolean provideReasonNPriceMaxMin;
	
	private boolean nPriceGreaterSPrice;
	
	private boolean provideRemarkForNotSuitablePrice;
	
	private boolean reminderForPricingCycle;
	
	private boolean provideReasonPRSPriceSD;
	
	private boolean provideReasonPRNPriceSD;
	
	private Double prSPriceSDPositive;
	
	private Double prSPriceSDNegative;
	
	private Double prNPriceSDPositive;
	
	private Double prNPriceSDNegative;
	
	private Integer prSPriceMonth;
	
	private Integer prNPriceMonth;
	
	private boolean provideReasonSPriceSD;
	
	private boolean provideReasonNPriceSD;
	
	private Double sPriceSDPositive;
	
	private Double sPriceSDNegative;
	
	private Double nPriceSDPositive;
	
	private Double nPriceSDNegative;
	
	private Integer sPriceMonth;
	
	private Integer nPriceMonth;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private boolean provideReasonPRNPriceLower;
	
	private Double prNPriceLowerThreshold;
	
	private boolean provideReasonPRSPriceLower;
	
	private Double prSPriceLowerThreshold;
	
	private boolean provideRemarkForNotAvailableQuotation;

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public boolean isUom1Reported() {
		return isUom1Reported;
	}

	public void setUom1Reported(boolean isUom1Reported) {
		this.isUom1Reported = isUom1Reported;
	}

	public boolean isUom2GreaterZero() {
		return isUom2GreaterZero;
	}

	public void setUom2GreaterZero(boolean isUom2GreaterZero) {
		this.isUom2GreaterZero = isUom2GreaterZero;
	}

	public boolean isNPriceGreaterZero() {
		return isNPriceGreaterZero;
	}

	public void setNPriceGreaterZero(boolean isNPriceGreaterZero) {
		this.isNPriceGreaterZero = isNPriceGreaterZero;
	}

	public boolean isSPriceGreaterZero() {
		return isSPriceGreaterZero;
	}

	public void setSPriceGreaterZero(boolean isSPriceGreaterZero) {
		this.isSPriceGreaterZero = isSPriceGreaterZero;
	}

	public boolean isProvideReasonPRNPrice() {
		return provideReasonPRNPrice;
	}

	public void setProvideReasonPRNPrice(boolean provideReasonPRNPrice) {
		this.provideReasonPRNPrice = provideReasonPRNPrice;
	}

	public Double getPrNPriceThreshold() {
		return prNPriceThreshold;
	}

	public void setPrNPriceThreshold(Double prNPriceThreshold) {
		this.prNPriceThreshold = prNPriceThreshold;
	}

	public boolean isProvideReasonPRSPrice() {
		return provideReasonPRSPrice;
	}

	public void setProvideReasonPRSPrice(boolean provideReasonPRSPrice) {
		this.provideReasonPRSPrice = provideReasonPRSPrice;
	}

	public Double getPrSPriceThreshold() {
		return prSPriceThreshold;
	}

	public void setPrSPriceThreshold(Double prSPriceThreshold) {
		this.prSPriceThreshold = prSPriceThreshold;
	}

	public boolean isProvideReasonSPriceMaxMin() {
		return provideReasonSPriceMaxMin;
	}

	public void setProvideReasonSPriceMaxMin(boolean provideReasonSPriceMaxMin) {
		this.provideReasonSPriceMaxMin = provideReasonSPriceMaxMin;
	}

	public boolean isProvideReasonNPriceMaxMin() {
		return provideReasonNPriceMaxMin;
	}

	public void setProvideReasonNPriceMaxMin(boolean provideReasonNPriceMaxMin) {
		this.provideReasonNPriceMaxMin = provideReasonNPriceMaxMin;
	}

	public boolean isnPriceGreaterSPrice() {
		return nPriceGreaterSPrice;
	}

	public void setnPriceGreaterSPrice(boolean nPriceGreaterSPrice) {
		this.nPriceGreaterSPrice = nPriceGreaterSPrice;
	}

	public boolean isProvideRemarkForNotSuitablePrice() {
		return provideRemarkForNotSuitablePrice;
	}

	public void setProvideRemarkForNotSuitablePrice(boolean provideRemarkForNotSuitablePrice) {
		this.provideRemarkForNotSuitablePrice = provideRemarkForNotSuitablePrice;
	}

	public boolean isReminderForPricingCycle() {
		return reminderForPricingCycle;
	}

	public void setReminderForPricingCycle(boolean reminderForPricingCycle) {
		this.reminderForPricingCycle = reminderForPricingCycle;
	}

	public boolean isProvideReasonPRSPriceSD() {
		return provideReasonPRSPriceSD;
	}

	public void setProvideReasonPRSPriceSD(boolean provideReasonPRSPriceSD) {
		this.provideReasonPRSPriceSD = provideReasonPRSPriceSD;
	}

	public boolean isProvideReasonPRNPriceSD() {
		return provideReasonPRNPriceSD;
	}

	public void setProvideReasonPRNPriceSD(boolean provideReasonPRNPriceSD) {
		this.provideReasonPRNPriceSD = provideReasonPRNPriceSD;
	}

	public Double getPrSPriceSDPositive() {
		return prSPriceSDPositive;
	}

	public void setPrSPriceSDPositive(Double prSPriceSDPositive) {
		this.prSPriceSDPositive = prSPriceSDPositive;
	}

	public Double getPrSPriceSDNegative() {
		return prSPriceSDNegative;
	}

	public void setPrSPriceSDNegative(Double prSPriceSDNegative) {
		this.prSPriceSDNegative = prSPriceSDNegative;
	}

	public Double getPrNPriceSDPositive() {
		return prNPriceSDPositive;
	}

	public void setPrNPriceSDPositive(Double prNPriceSDPositive) {
		this.prNPriceSDPositive = prNPriceSDPositive;
	}

	public Double getPrNPriceSDNegative() {
		return prNPriceSDNegative;
	}

	public void setPrNPriceSDNegative(Double prNPriceSDNegative) {
		this.prNPriceSDNegative = prNPriceSDNegative;
	}

	public Integer getPrSPriceMonth() {
		return prSPriceMonth;
	}

	public void setPrSPriceMonth(Integer prSPriceMonth) {
		this.prSPriceMonth = prSPriceMonth;
	}

	public Integer getPrNPriceMonth() {
		return prNPriceMonth;
	}

	public void setPrNPriceMonth(Integer prNPriceMonth) {
		this.prNPriceMonth = prNPriceMonth;
	}

	public boolean isProvideReasonSPriceSD() {
		return provideReasonSPriceSD;
	}

	public void setProvideReasonSPriceSD(boolean provideReasonSPriceSD) {
		this.provideReasonSPriceSD = provideReasonSPriceSD;
	}

	public boolean isProvideReasonNPriceSD() {
		return provideReasonNPriceSD;
	}

	public void setProvideReasonNPriceSD(boolean provideReasonNPriceSD) {
		this.provideReasonNPriceSD = provideReasonNPriceSD;
	}

	public Double getsPriceSDNegative() {
		return sPriceSDNegative;
	}

	public void setsPriceSDNegative(Double sPriceSDNegative) {
		this.sPriceSDNegative = sPriceSDNegative;
	}
	
	public Double getnPriceSDNegative() {
		return nPriceSDNegative;
	}

	public void setnPriceSDNegative(Double nPriceSDNegative) {
		this.nPriceSDNegative = nPriceSDNegative;
	}

	public Integer getsPriceMonth() {
		return sPriceMonth;
	}

	public void setsPriceMonth(Integer sPriceMonth) {
		this.sPriceMonth = sPriceMonth;
	}

	public Integer getnPriceMonth() {
		return nPriceMonth;
	}

	public void setnPriceMonth(Integer nPriceMonth) {
		this.nPriceMonth = nPriceMonth;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isProvideReasonPRNPriceLower() {
		return provideReasonPRNPriceLower;
	}

	public void setProvideReasonPRNPriceLower(boolean provideReasonPRNPriceLower) {
		this.provideReasonPRNPriceLower = provideReasonPRNPriceLower;
	}

	public Double getPrNPriceLowerThreshold() {
		return prNPriceLowerThreshold;
	}

	public void setPrNPriceLowerThreshold(Double prNPriceLowerThreshold) {
		this.prNPriceLowerThreshold = prNPriceLowerThreshold;
	}

	public boolean isProvideReasonPRSPriceLower() {
		return provideReasonPRSPriceLower;
	}

	public void setProvideReasonPRSPriceLower(boolean provideReasonPRSPriceLower) {
		this.provideReasonPRSPriceLower = provideReasonPRSPriceLower;
	}

	public Double getPrSPriceLowerThreshold() {
		return prSPriceLowerThreshold;
	}

	public void setPrSPriceLowerThreshold(Double prSPriceLowerThreshold) {
		this.prSPriceLowerThreshold = prSPriceLowerThreshold;
	}

	public boolean isProvideRemarkForNotAvailableQuotation() {
		return provideRemarkForNotAvailableQuotation;
	}

	public void setProvideRemarkForNotAvailableQuotation(boolean provideRemarkForNotAvailableQuotation) {
		this.provideRemarkForNotAvailableQuotation = provideRemarkForNotAvailableQuotation;
	}

	public Double getsPriceSDPositive() {
		return sPriceSDPositive;
	}

	public void setsPriceSDPositive(Double sPriceSDPositive) {
		this.sPriceSDPositive = sPriceSDPositive;
	}

	public Double getnPriceSDPositive() {
		return nPriceSDPositive;
	}

	public void setnPriceSDPositive(Double nPriceSDPositive) {
		this.nPriceSDPositive = nPriceSDPositive;
	}

	
}
