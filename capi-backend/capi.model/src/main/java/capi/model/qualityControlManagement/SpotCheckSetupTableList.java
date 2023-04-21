package capi.model.qualityControlManagement;

import java.util.Date;


public class SpotCheckSetupTableList {

	private Integer spotCheckSetupId;

	private Integer spotCheckDateId;

	private String spotCheckDate;

	private Integer fieldOfficerId;

	private String fieldOfficerCode;

	private String chineseName;

	private String fieldOfficer;

	private String notificationDate;

	private Integer notificationType;

	private String supervisor;

	private Date createdDate;

	private Date modifiedDate;

	private boolean deletable;
	
	public Integer getSpotCheckSetupId() {
		return spotCheckSetupId;
	}

	public void setSpotCheckSetupId(Integer spotCheckSetupId) {
		this.spotCheckSetupId = spotCheckSetupId;
	}

	public Integer getSpotCheckDateId() {
		return spotCheckDateId;
	}

	public void setSpotCheckDateId(Integer spotCheckDateId) {
		this.spotCheckDateId = spotCheckDateId;
	}

	public String getSpotCheckDate() {
		return spotCheckDate;
	}

	public void setSpotCheckDate(String spotCheckDate) {
		this.spotCheckDate = spotCheckDate;
	}

	public Integer getFieldOfficerId() {
		return fieldOfficerId;
	}

	public void setFieldOfficerId(Integer fieldOfficerId) {
		this.fieldOfficerId = fieldOfficerId;
	}

	public String getFieldOfficerCode() {
		return fieldOfficerCode;
	}

	public void setFieldOfficerCode(String fieldOfficerCode) {
		this.fieldOfficerCode = fieldOfficerCode;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getFieldOfficer() {
		return fieldOfficer;
	}

	public void setFieldOfficer(String fieldOfficer) {
		this.fieldOfficer = fieldOfficer;
	}

	public String getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(String notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Integer getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Integer notificationType) {
		this.notificationType = notificationType;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
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

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

}
