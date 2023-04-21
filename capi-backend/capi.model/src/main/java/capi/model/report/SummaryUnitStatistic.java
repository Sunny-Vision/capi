package capi.model.report;

import java.util.Date;

public class SummaryUnitStatistic {
	
	private String unitStatisticId;
	
	private Date referenceMonth;
	
	private String unitCode;
	
	private String unitChineseName;

	private String unitEnglishName;
	
	private String cpiBasePeriod;
	
	private String compilationMethod;
	
	private String cpiQuotationType;

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
	
	private Date createDate;
	
	private Date modifyDate;
	
	private String createBy;
	
	private String modifyBy;
	
	private String unitId;
	
	private Double deviationSum;
	
	private Double variance;
	
	private Double PRSPriceDeviationSum;
	
	private Double PRSPriceVariance;
	
	private Double countPRNPrice;
	
	private Double PRNPriceDeviationSum;
	
	private Double PRNPriceVariance;
	
	private Double standardDeviationPRNPrice;
	
	private Double sumPRNPrice;
	
	private Double minNPrice;
	
	private Double maxNPrice;

	public String getUnitStatisticId() {
		return unitStatisticId;
	}

	public void setUnitStatisticId(String unitStatisticId) {
		this.unitStatisticId = unitStatisticId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitChineseName() {
		return unitChineseName;
	}

	public void setUnitChineseName(String unitChineseName) {
		this.unitChineseName = unitChineseName;
	}

	public String getUnitEnglishName() {
		return unitEnglishName;
	}

	public void setUnitEnglishName(String unitEnglishName) {
		this.unitEnglishName = unitEnglishName;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getCpiQuotationType() {
		return cpiQuotationType;
	}

	public void setCpiQuotationType(String cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
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

	public Double getCountPRNPrice() {
		return countPRNPrice;
	}

	public void setCountPRNPrice(Double countPRNPrice) {
		this.countPRNPrice = countPRNPrice;
	}

	public Double getPRNPriceDeviationSum() {
		return PRNPriceDeviationSum;
	}

	public void setPRNPriceDeviationSum(Double pRNPriceDeviationSum) {
		PRNPriceDeviationSum = pRNPriceDeviationSum;
	}

	public Double getPRNPriceVariance() {
		return PRNPriceVariance;
	}

	public void setPRNPriceVariance(Double pRNPriceVariance) {
		PRNPriceVariance = pRNPriceVariance;
	}

	public Double getStandardDeviationPRNPrice() {
		return standardDeviationPRNPrice;
	}

	public void setStandardDeviationPRNPrice(Double standardDeviationPRNPrice) {
		this.standardDeviationPRNPrice = standardDeviationPRNPrice;
	}

	public Double getSumPRNPrice() {
		return sumPRNPrice;
	}

	public void setSumPRNPrice(Double sumPRNPrice) {
		this.sumPRNPrice = sumPRNPrice;
	}

	public Double getMinNPrice() {
		return minNPrice;
	}

	public void setMinNPrice(Double minNPrice) {
		this.minNPrice = minNPrice;
	}

	public Double getMaxNPrice() {
		return maxNPrice;
	}

	public void setMaxNPrice(Double maxNPrice) {
		this.maxNPrice = maxNPrice;
	}

	public String getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(String compilationMethod) {
		this.compilationMethod = compilationMethod;
	}
}
