package capi.model.api.dataSync;

import java.util.Date;

public class BusinessParameterSyncData {
	
	private Integer systemConfigurationId;
	
	private String name;
	
	private String value;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getSystemConfigurationId() {
		return systemConfigurationId;
	}

	public void setSystemConfigurationId(Integer systemConfigurationId) {
		this.systemConfigurationId = systemConfigurationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	
	
}
