package capi.model.api.dataSync;

import java.util.Date;

public class NotificationSyncData {
	private Integer notificationId;
	
	private String subject;
	
	private String content;
	
	private boolean isRead;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer userId;
	
	private String rejectedQuotationIds;
	
	private boolean isFlag;

	public Integer getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getRejectedQuotationIds() {
		return rejectedQuotationIds;
	}

	public void setRejectedQuotationIds(String rejectedQuotationIds) {
		this.rejectedQuotationIds = rejectedQuotationIds;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}
	
	
}
