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
@Table(name="QuotationStatistic")
public class QuotationStatistic extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="QuotationStatisticId")
	private Integer quotationStatisticId;
	
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
	
	@Column(name="DeviationSum")
	private Double deviationSum;
	
	@Column(name="Variance")
	private Double variance;
	
	@Column(name="SumCurrentNPrice")
	private Double sumCurrentNPrice;
	
	@Column(name="CountCurrentNPrice")
	private Integer countCurrentNPrice;
	
	@Column(name="AverageCurrentNPrice")
	private Double averageCurrentNPrice;
	
	@Column(name="LastHasPriceSumCurrentNPrice")
	private Double lastHasPriceSumCurrentNPrice;
	
	@Column(name="LastHasPriceCountCurrentNPrice")
	private Integer lastHasPriceCountCurrentNPrice;
	
	@Column(name="LastHasPriceAverageCurrentNPrice")
	private Double lastHasPriceAverageCurrentNPrice;
	
	@Column(name="AverageLastNPrice")
	private Double averageLastNPrice;
	
	@Column(name="CountLastNPrice")
	private Integer countLastNPrice;
	
	@Column(name="SumLastNPrice")
	private Double sumLastNPrice;
	
	@Column(name="FinalPRNPrice")
	private Double finalPRNPrice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QuotationId", nullable = true)
	private Quotation quotation;
	
	@Column(name="KeepNoStartMonth")
	private Date keepNoStartMonth;
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getQuotationStatisticId();
	}

	public Integer getQuotationStatisticId() {
		return quotationStatisticId;
	}

	public void setQuotationStatisticId(Integer quotationStatisticId) {
		this.quotationStatisticId = quotationStatisticId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
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

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
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

	public Double getSumCurrentNPrice() {
		return sumCurrentNPrice;
	}

	public void setSumCurrentNPrice(Double sumCurrentNPrice) {
		this.sumCurrentNPrice = sumCurrentNPrice;
	}

	public Integer getCountCurrentNPrice() {
		return countCurrentNPrice;
	}

	public void setCountCurrentNPrice(Integer countCurrentNPrice) {
		this.countCurrentNPrice = countCurrentNPrice;
	}

	public Double getAverageCurrentNPrice() {
		return averageCurrentNPrice;
	}

	public void setAverageCurrentNPrice(Double averageCurrentNPrice) {
		this.averageCurrentNPrice = averageCurrentNPrice;
	}

	public Double getLastHasPriceSumCurrentNPrice() {
		return lastHasPriceSumCurrentNPrice;
	}

	public void setLastHasPriceSumCurrentNPrice(Double lastHasPriceSumCurrentNPrice) {
		this.lastHasPriceSumCurrentNPrice = lastHasPriceSumCurrentNPrice;
	}

	public Integer getLastHasPriceCountCurrentNPrice() {
		return lastHasPriceCountCurrentNPrice;
	}

	public void setLastHasPriceCountCurrentNPrice(
			Integer lastHasPriceCountCurrentNPrice) {
		this.lastHasPriceCountCurrentNPrice = lastHasPriceCountCurrentNPrice;
	}

	public Double getLastHasPriceAverageCurrentNPrice() {
		return lastHasPriceAverageCurrentNPrice;
	}

	public void setLastHasPriceAverageCurrentNPrice(
			Double lastHasPriceAverageCurrentNPrice) {
		this.lastHasPriceAverageCurrentNPrice = lastHasPriceAverageCurrentNPrice;
	}

	public Double getAverageLastNPrice() {
		return averageLastNPrice;
	}

	public void setAverageLastNPrice(Double averageLastNPrice) {
		this.averageLastNPrice = averageLastNPrice;
	}

	public Integer getCountLastNPrice() {
		return countLastNPrice;
	}

	public void setCountLastNPrice(Integer countLastNPrice) {
		this.countLastNPrice = countLastNPrice;
	}

	public Double getSumLastNPrice() {
		return sumLastNPrice;
	}

	public void setSumLastNPrice(Double sumLastNPrice) {
		this.sumLastNPrice = sumLastNPrice;
	}

	public Double getFinalPRNPrice() {
		return finalPRNPrice;
	}

	public void setFinalPRNPrice(Double finalPRNPrice) {
		this.finalPRNPrice = finalPRNPrice;
	}

	public Date getKeepNoStartMonth() {
		return keepNoStartMonth;
	}

	public void setKeepNoStartMonth(Date keepNoStartMonth) {
		this.keepNoStartMonth = keepNoStartMonth;
	}

}
