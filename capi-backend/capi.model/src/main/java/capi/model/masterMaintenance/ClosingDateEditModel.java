package capi.model.masterMaintenance;


public class ClosingDateEditModel {
	
	private Integer closingDateId;

	private String closingDate;
	
	private String referenceMonth;
	
	private String publishDate;
	
	private String modifiedDate;
	
	private String createdDate;
	
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

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	
}
