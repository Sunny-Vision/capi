package capi.audit.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AccessLog")
public class AccessLog implements Serializable{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AccessLogId")
	private Integer accessLogId;
	
	@Column(name="Username")
	private String username;
	
	@Column(name="Url")
	private String url;
	
	@Column(name="CreatedDate")
	private Date createdDate;
	
	@Column(name="RemoteAddress")
	private String remoteAddress;

	public Integer getAccessLogId() {
		return accessLogId;
	}

	public void setAccessLogId(Integer accessLogId) {
		this.accessLogId = accessLogId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
}
