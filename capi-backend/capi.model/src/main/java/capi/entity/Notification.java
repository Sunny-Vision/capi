package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Notification")
public class Notification extends capi.entity.EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="NotificationId")
	private Integer notificationId;
	
	@Column(name="Subject")
	private String subject;
	
	@Column(name="[Content]")
	private String content;
	
	@Column(name="IsRead")
	private boolean isRead;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Column(name="IsFlag")
	private boolean isFlag;
	
	@Column(name="RejectedQuotationIds")
	private String rejectedQuotationIds;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getNotificationId();
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public Integer getNotificationId() {
		return notificationId;
	}


	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}


	public boolean isFlag() {
		return isFlag;
	}


	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}


	public String getRejectedQuotationIds() {
		return rejectedQuotationIds;
	}


	public void setRejectedQuotationIds(String rejectedQuotationIds) {
		this.rejectedQuotationIds = rejectedQuotationIds;
	}

}
