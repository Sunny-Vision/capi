package capi.model.report;

import java.util.Date;

public class SummarySubGroupStatistic {
	
	private String subGroupStatisticId;
	
	private String subGroupCode;
	
	private String subGroupChinName;
	
	private String subGroupEngName;
	
	private String cpiBasePeriod;
	
	private Date referenceMonth;
	
	private Double sumCurrentSPrice;
	
	private Double countCurrentSPrice;
	
	private Double averageCurrentSPrice;
	
	private Double sumLastSPrice;
	
	private Double countLastSPrice;
	
	private Double averageLastSPrice;
	
	private Double finalPRSPrice;
	
	private Double standardDeviationSPrice;
	
	private Double medianSPrice;
	
	private Double minSPrice;
	
	private Double maxSPrice;
	
	private Double lastHasPriceSumCurrentSPrice;
	
	private Double lastHasPriceCountCurrentSPrice;
	
	private Double lastHasPriceAverageCurrentSPrice;
	
	private Date lastHasPriceReferenceMonth;
	
	private Double averagePRSPrice;
	
	private Double countPRSPrice;
	
	private Double sumPRSPrice;
	
	private Double standardDeviationPRSPrice;
	
	private Double medianPRPrice;
	
	private Double minPRPrice;
	
	private Double maxPRPrice;
	
	private String subGroupId;
	
	private Double deviationSum;
	
	private Double variance;
	
	private Double PRSPriceDeviationSum;
	
	private Double PRSPriceVariance;

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Double getSumCurrentSPrice() {
		return sumCurrentSPrice;
	}

	public void setSumCurrentSPrice(Double sumCurrentSPrice) {
		this.sumCurrentSPrice = sumCurrentSPrice;
	}

	public Double getCountCurrentSPrice() {
		return countCurrentSPrice;
	}

	public void setCountCurrentSPrice(Double countCurrentSPrice) {
		this.countCurrentSPrice = countCurrentSPrice;
	}

	public Double getAverageCurrentSPrice() {
		return averageCurrentSPrice;
	}

	public void setAverageCurrentSPrice(Double averageCurrentSPrice) {
		this.averageCurrentSPrice = averageCurrentSPrice;
	}

	public Double getSumLastSPrice() {
		return sumLastSPrice;
	}

	public void setSumLastSPrice(Double sumLastSPrice) {
		this.sumLastSPrice = sumLastSPrice;
	}

	public Double getCountLastSPrice() {
		return countLastSPrice;
	}

	public void setCountLastSPrice(Double countLastSPrice) {
		this.countLastSPrice = countLastSPrice;
	}

	public Double getAverageLastSPrice() {
		return averageLastSPrice;
	}

	public void setAverageLastSPrice(Double averageLastSPrice) {
		this.averageLastSPrice = averageLastSPrice;
	}

	public Double getFinalPRSPrice() {
		return finalPRSPrice;
	}

	public void setFinalPRSPrice(Double finalPRSPrice) {
		this.finalPRSPrice = finalPRSPrice;
	}

	public Double getStandardDeviationSPrice() {
		return standardDeviationSPrice;
	}

	public void setStandardDeviationSPrice(Double standardDeviationSPrice) {
		this.standardDeviationSPrice = standardDeviationSPrice;
	}

	public Double getMedianSPrice() {
		return medianSPrice;
	}

	public void setMedianSPrice(Double medianSPrice) {
		this.medianSPrice = medianSPrice;
	}

	public Double getMinSPrice() {
		return minSPrice;
	}

	public void setMinSPrice(Double minSPrice) {
		this.minSPrice = minSPrice;
	}

	public Double getMaxSPrice() {
		return maxSPrice;
	}

	public void setMaxSPrice(Double maxSPrice) {
		this.maxSPrice = maxSPrice;
	}

	public Double getLastHasPriceSumCurrentSPrice() {
		return lastHasPriceSumCurrentSPrice;
	}

	public void setLastHasPriceSumCurrentSPrice(Double lastHasPriceSumCurrentSPrice) {
		this.lastHasPriceSumCurrentSPrice = lastHasPriceSumCurrentSPrice;
	}

	public Double getLastHasPriceCountCurrentSPrice() {
		return lastHasPriceCountCurrentSPrice;
	}

	public void setLastHasPriceCountCurrentSPrice(Double lastHasPriceCountCurrentSPrice) {
		this.lastHasPriceCountCurrentSPrice = lastHasPriceCountCurrentSPrice;
	}

	public Double getLastHasPriceAverageCurrentSPrice() {
		return lastHasPriceAverageCurrentSPrice;
	}

	public void setLastHasPriceAverageCurrentSPrice(Double lastHasPriceAverageCurrentSPrice) {
		this.lastHasPriceAverageCurrentSPrice = lastHasPriceAverageCurrentSPrice;
	}

	public Date getLastHasPriceReferenceMonth() {
		return lastHasPriceReferenceMonth;
	}

	public void setLastHasPriceReferenceMonth(Date lastHasPriceReferenceMonth) {
		this.lastHasPriceReferenceMonth = lastHasPriceReferenceMonth;
	}

	public Double getAveragePRSPrice() {
		return averagePRSPrice;
	}

	public void setAveragePRSPrice(Double averagePRSPrice) {
		this.averagePRSPrice = averagePRSPrice;
	}

	public Double getCountPRSPrice() {
		return countPRSPrice;
	}

	public void setCountPRSPrice(Double countPRSPrice) {
		this.countPRSPrice = countPRSPrice;
	}

	public Double getSumPRSPrice() {
		return sumPRSPrice;
	}

	public void setSumPRSPrice(Double sumPRSPrice) {
		this.sumPRSPrice = sumPRSPrice;
	}

	public Double getStandardDeviationPRSPrice() {
		return standardDeviationPRSPrice;
	}

	public void setStandardDeviationPRSPrice(Double standardDeviationPRSPrice) {
		this.standardDeviationPRSPrice = standardDeviationPRSPrice;
	}

	public Double getMedianPRPrice() {
		return medianPRPrice;
	}

	public void setMedianPRPrice(Double medianPRPrice) {
		this.medianPRPrice = medianPRPrice;
	}

	public Double getMinPRPrice() {
		return minPRPrice;
	}

	public void setMinPRPrice(Double minPRPrice) {
		this.minPRPrice = minPRPrice;
	}

	public Double getMaxPRPrice() {
		return maxPRPrice;
	}

	public void setMaxPRPrice(Double maxPRPrice) {
		this.maxPRPrice = maxPRPrice;
	}



	public Double getDeviationSum() {
		return deviationSum;
	}

	public void setDeviationSum(Double deviationSum) {
		this.deviationSum = deviationSum;
	}

	public Double getVariance() {
		return variance;
	}

	public void setVariance(Double variance) {
		this.variance = variance;
	}

	public Double getPRSPriceDeviationSum() {
		return PRSPriceDeviationSum;
	}

	public void setPRSPriceDeviationSum(Double pRSPriceDeviationSum) {
		PRSPriceDeviationSum = pRSPriceDeviationSum;
	}

	public Double getPRSPriceVariance() {
		return PRSPriceVariance;
	}

	public void setPRSPriceVariance(Double pRSPriceVariance) {
		PRSPriceVariance = pRSPriceVariance;
	}

	public String getSubGroupStatisticId() {
		return subGroupStatisticId;
	}

	public void setSubGroupStatisticId(String subGroupStatisticId) {
		this.subGroupStatisticId = subGroupStatisticId;
	}

	public String getSubGroupCode() {
		return subGroupCode;
	}

	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}

	public String getSubGroupChinName() {
		return subGroupChinName;
	}

	public void setSubGroupChinName(String subGroupChinName) {
		this.subGroupChinName = subGroupChinName;
	}

	public String getSubGroupEngName() {
		return subGroupEngName;
	}

	public void setSubGroupEngName(String subGroupEngName) {
		this.subGroupEngName = subGroupEngName;
	}

	public String getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(String subGroupId) {
		this.subGroupId = subGroupId;
	}
}