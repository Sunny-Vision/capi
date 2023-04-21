package capi.model.report;

import java.util.List;

public class InformationTimeLogFormCriteria {

	private String refMonth;

	private List<Integer> fieldOfficerId;

	private String timeLogDate;
	
	private Integer userId;
	
	private Integer authorityLevel;

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public List<Integer> getFieldOfficerId() {
		return fieldOfficerId;
	}

	public void setFieldOfficerId(List<Integer> fieldOfficerId) {
		this.fieldOfficerId = fieldOfficerId;
	}

	public String getTimeLogDate() {
		return timeLogDate;
	}

	public void setTimeLogDate(String timeLogDate) {
		this.timeLogDate = timeLogDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}

}
