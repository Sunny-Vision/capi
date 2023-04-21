package capi.model.dashboard;

public class DataConversionModel extends BaseSectionModel {
	private Long mrpsTotal;
	private Long mrpsCount;
	private Long othersTotal;
	private Long othersCount;
	
	public Long getMrpsTotal() {
		return mrpsTotal;
	}
	public void setMrpsTotal(Long mrpsTotal) {
		this.mrpsTotal = mrpsTotal;
	}
	public Long getMrpsCount() {
		return mrpsCount;
	}
	public void setMrpsCount(Long mrpsCount) {
		this.mrpsCount = mrpsCount;
	}
	public Long getOthersTotal() {
		return othersTotal;
	}
	public void setOthersTotal(Long othersTotal) {
		this.othersTotal = othersTotal;
	}
	public Long getOthersCount() {
		return othersCount;
	}
	public void setOthersCount(Long othersCount) {
		this.othersCount = othersCount;
	}
}