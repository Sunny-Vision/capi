package capi.model.masterMaintenance;


public class ClosingDateTableList {
	
	private Integer closingDateId;

	private String closingDate;
	
	private String referenceMonth;
	
	private String publishDate;
	
	private Long surveyMonthCnt;
	
	public Integer getId() {
		return closingDateId;
	}

	public Integer getClosingDateId() {
		return closingDateId;
	}

	public void setClosingDateId(Integer closingDateId) {
		this.closingDateId = closingDateId;
	}

	public String getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(String closingDate) {
		this.closingDate = closingDate;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public Long getSurveyMonthCnt() {
		return surveyMonthCnt;
	}

	public void setSurveyMonthCnt(Long surveyMonthCnt) {
		this.surveyMonthCnt = surveyMonthCnt;
	}
	
}
