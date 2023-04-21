package capi.model.report;

public class SummaryOfTimeoffOvertimeReportAccumulated {

	private Integer userId;
	
	private Long accumulatedTF;
	
	private Long accumulatedOT;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getAccumulatedTF() {
		return accumulatedTF;
	}

	public void setAccumulatedTF(Long accumulatedTF) {
		this.accumulatedTF = accumulatedTF;
	}

	public Long getAccumulatedOT() {
		return accumulatedOT;
	}

	public void setAccumulatedOT(Long accumulatedOT) {
		this.accumulatedOT = accumulatedOT;
	}

}
