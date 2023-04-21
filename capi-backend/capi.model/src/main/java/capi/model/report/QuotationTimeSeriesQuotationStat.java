package capi.model.report;

public class QuotationTimeSeriesQuotationStat {

	private Integer quotationId;

	private String qsReferenceMonth;

	private Double qsAveragePrice;

	private Double qsAveragePricePR;

	private Double qsAveragePriceSD;

	private Double qsMinPrice;

	private Double qsMaxPrice;

	private Integer qsCount;

	private Double qsSumPrice;

	private String qsKeepMonth;

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public String getQsReferenceMonth() {
		return qsReferenceMonth;
	}

	public void setQsReferenceMonth(String qsReferenceMonth) {
		this.qsReferenceMonth = qsReferenceMonth;
	}

	public Double getQsAveragePrice() {
		return qsAveragePrice;
	}

	public void setQsAveragePrice(Double qsAveragePrice) {
		this.qsAveragePrice = qsAveragePrice;
	}

	public Double getQsAveragePricePR() {
		return qsAveragePricePR;
	}

	public void setQsAveragePricePR(Double qsAveragePricePR) {
		this.qsAveragePricePR = qsAveragePricePR;
	}

	public Double getQsAveragePriceSD() {
		return qsAveragePriceSD;
	}

	public void setQsAveragePriceSD(Double qsAveragePriceSD) {
		this.qsAveragePriceSD = qsAveragePriceSD;
	}

	public Double getQsMinPrice() {
		return qsMinPrice;
	}

	public void setQsMinPrice(Double qsMinPrice) {
		this.qsMinPrice = qsMinPrice;
	}

	public Double getQsMaxPrice() {
		return qsMaxPrice;
	}

	public void setQsMaxPrice(Double qsMaxPrice) {
		this.qsMaxPrice = qsMaxPrice;
	}

	public Integer getQsCount() {
		return qsCount;
	}

	public void setQsCount(Integer qsCount) {
		this.qsCount = qsCount;
	}

	public Double getQsSumPrice() {
		return qsSumPrice;
	}

	public void setQsSumPrice(Double qsSumPrice) {
		this.qsSumPrice = qsSumPrice;
	}

	public String getQsKeepMonth() {
		return qsKeepMonth;
	}

	public void setQsKeepMonth(String qsKeepMonth) {
		this.qsKeepMonth = qsKeepMonth;
	}

}