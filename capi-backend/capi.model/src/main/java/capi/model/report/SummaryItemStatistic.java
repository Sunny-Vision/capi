package capi.model.report;

import java.util.Date;

public class SummaryItemStatistic {
	
	private String itemStatisticId;
	
	private String itemCode;
	
	private String itemChinName;
	
	private String itemEngName;
	
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
	
	private String itemId;
	
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

	public String getItemStatisticId() {
		return itemStatisticId;
	}

	public void setItemStatisticId(String itemStatisticId) {
		this.itemStatisticId = itemStatisticId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemChinName() {
		return itemChinName;
	}

	public void setItemChinName(String itemChinName) {
		this.itemChinName = itemChinName;
	}

	public String getItemEngName() {
		return itemEngName;
	}

	public void setItemEngName(String itemEngName) {
		this.itemEngName = itemEngName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
}
