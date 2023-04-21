package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SystemConfiguration")
public class SystemConfiguration extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SystemConfigurationId")
	private Integer systemConfigurationId;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Value")
	private String value;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return this.getSystemConfigurationId();
	}

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
	
	
}
