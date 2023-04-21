package capi.model.commonLookup;

import capi.entity.UnitStatistic;

public class QuotationRecordHistoryStatisticModel {
	private QuotationRecordHistoryStatisticLast2Year last2Year;
	private UnitStatistic currentStatistic;
	private UnitStatistic previousStatistic;
	private Integer currentCount;
	private Integer previousCount;
	public QuotationRecordHistoryStatisticLast2Year getLast2Year() {
		return last2Year;
	}
	public void setLast2Year(QuotationRecordHistoryStatisticLast2Year last2Year) {
		this.last2Year = last2Year;
	}
	public UnitStatistic getCurrentStatistic() {
		return currentStatistic;
	}
	public void setCurrentStatistic(UnitStatistic currentStatistic) {
		this.currentStatistic = currentStatistic;
	}
	public UnitStatistic getPreviousStatistic() {
		return previousStatistic;
	}
	public void setPreviousStatistic(UnitStatistic previousStatistic) {
		this.previousStatistic = previousStatistic;
	}
	public Integer getCurrentCount() {
		return currentCount;
	}
	public void setCurrentCount(Integer currentCount) {
		this.currentCount = currentCount;
	}
	public Integer getPreviousCount() {
		return previousCount;
	}
	public void setPreviousCount(Integer previousCount) {
		this.previousCount = previousCount;
	}
	
}
