package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="UnitStatistic")
public class UnitStatistic  extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UnitStatisticId")
	private Integer unitStatisticId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Column(name="SumCurrentSPrice")
	private Double sumCurrentSPrice;
	
	@Column(name="CountCurrentSPrice")
	private Integer countCurrentSPrice;
	
	@Column(name="AverageCurrentSPrice")
	private Double averageCurrentSPrice;
	
	@Column(name="SumLastSPrice")
	private Double sumLastSPrice;
	
	@Column(name="CountLastSPrice")
	private Integer countLastSPrice;
	
	@Column(name="AverageLastSPrice")
	private Double averageLastSPrice;
	
	@Column(name="FinalPRSPrice")
	private Double finalPRSPrice;
			
	@Column(name="StandardDeviationSPrice")
	private Double standardDeviationSPrice;
	
	@Column(name="MedianSPrice")
	private Double medianSPrice;
	
	@Column(name="MinSPrice")
	private Double minSPrice;
	
	@Column(name="MaxSPrice")
	private Double maxSPrice;
	
	@Column(name="LastHasPriceSumCurrentSPrice")
	private Double lastHasPriceSumCurrentSPrice;
	
	@Column(name="LastHasPriceCountCurrentSPrice")
	private Integer lastHasPriceCountCurrentSPrice;
	
	@Column(name="LastHasPriceAverageCurrentSPrice")
	private Double lastHasPriceAverageCurrentSPrice;
	
	@Column(name="LastHasPriceReferenceMonth")
	private Date lastHasPriceReferenceMonth;
	
	@Column(name="AveragePRSPrice")
	private Double averagePRSPrice;
	
	@Column(name="CountPRSPrice")
	private Integer countPRSPrice;
	
	@Column(name="SumPRSPrice")
	private Double sumPRSPrice;
	
	@Column(name="StandardDeviationPRSPrice")
	private Double standardDeviationPRSPrice;
	
	@Column(name="MedianPRPrice")
	private Double medianPRPrice;
	
	@Column(name="MinPRPrice")
	private Double minPRPrice;
	
	@Column(name="MaxPRPrice")
	private Double maxPRPrice;
	
	@Column(name="DeviationSum")
	private Double deviationSum;
	
	@Column(name="Variance")
	private Double variance;
	
	@Column(name="PRSPriceDeviationSum")
	private Double prSPriceDeviationSum;
		
	@Column(name="PRSPriceVariance")
	private Double prSPriceVariance;
	
	@Column(name="CountPRNPrice")
	private Integer countPRNPrice;
		
	@Column(name="PRNPriceDeviationSum")
	private Double prNPriceDeviationSum;	
	
	@Column(name="PRNPriceVariance")
	private Double prNPriceVariance;
	
	@Column(name="StandardDeviationPRNPrice")
	private Double standardDeviationPRNPrice;
	
	@Column(name="SumPRNPrice")
	private Double sumPRNPrice;
	
	@Column(name="MinNPrice")
	private Double minNPrice;
	
	@Column(name="MaxNPrice")
	private Double maxNPrice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UnitId", nullable = true)
	private Unit unit;
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getUnitStatisticId();
	}

	public Integer getUnitStatisticId() {
		return unitStatisticId;
	}

	public void setUnitStatisticId(Integer unitStatisticId) {
		this.unitStatisticId = unitStatisticId;
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

	public Integer getCountCurrentSPrice() {
		return countCurrentSPrice;
	}

	public void setCountCurrentSPrice(Integer countCurrentSPrice) {
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

	public Integer getCountLastSPrice() {
		return countLastSPrice;
	}

	public void setCountLastSPrice(Integer countLastSPrice) {
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

	public Integer getLastHasPriceCountCurrentSPrice() {
		return lastHasPriceCountCurrentSPrice;
	}

	public void setLastHasPriceCountCurrentSPrice(
			Integer lastHasPriceCountCurrentSPrice) {
		this.lastHasPriceCountCurrentSPrice = lastHasPriceCountCurrentSPrice;
	}

	public Double getLastHasPriceAverageCurrentSPrice() {
		return lastHasPriceAverageCurrentSPrice;
	}

	public void setLastHasPriceAverageCurrentSPrice(
			Double lastHasPriceAverageCurrentSPrice) {
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

	public Integer getCountPRSPrice() {
		return countPRSPrice;
	}

	public void setCountPRSPrice(Integer countPRSPrice) {
		this.countPRSPrice = countPRSPrice;
	}

	public Double getSumPRSPrice() {
		return sumPRSPrice;
	}

	public void setSumPRSPrice(Double sumPRSPrice) {
		this.sumPRSPrice = sumPRSPrice;
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

	public Double getStandardDeviationPRSPrice() {
		return standardDeviationPRSPrice;
	}

	public void setStandardDeviationPRSPrice(Double standardDeviationPRSPrice) {
		this.standardDeviationPRSPrice = standardDeviationPRSPrice;
	}

	public Double getPrSPriceDeviationSum() {
		return prSPriceDeviationSum;
	}

	public void setPrSPriceDeviationSum(Double prSPriceDeviationSum) {
		this.prSPriceDeviationSum = prSPriceDeviationSum;
	}

	public Double getPrSPriceVariance() {
		return prSPriceVariance;
	}

	public void setPrSPriceVariance(Double prSPriceVariance) {
		this.prSPriceVariance = prSPriceVariance;
	}

	public Integer getCountPRNPrice() {
		return countPRNPrice;
	}

	public void setCountPRNPrice(Integer countPRNPrice) {
		this.countPRNPrice = countPRNPrice;
	}

	public Double getPrNPriceDeviationSum() {
		return prNPriceDeviationSum;
	}

	public void setPrNPriceDeviationSum(Double prNPriceDeviationSum) {
		this.prNPriceDeviationSum = prNPriceDeviationSum;
	}

	public Double getPrNPriceVariance() {
		return prNPriceVariance;
	}

	public void setPrNPriceVariance(Double prNPriceVariance) {
		this.prNPriceVariance = prNPriceVariance;
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

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

}
