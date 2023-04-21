package capi.model.api.dataSync;

import java.util.Date;

public class PurposeSyncData {
	private Integer purposeId;
	
	private String code;
	
	private String name;
	
	private String survey;
	
	private String note;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private boolean peIncluded;
	
	private String enumerationOutcomes;

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isPeIncluded() {
		return peIncluded;
	}

	public void setPeIncluded(boolean peIncluded) {
		this.peIncluded = peIncluded;
	}

	public String getEnumerationOutcomes() {
		return enumerationOutcomes;
	}

	public void setEnumerationOutcomes(String enumerationOutcomes) {
		this.enumerationOutcomes = enumerationOutcomes;
	}

}
