package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PasswordPolicy")
public class PasswordPolicy extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PasswordPolicyId")
	private Integer passwordPolicyId;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Value")
	private String value;

	@Override
	public Integer getId() {
		return this.getPasswordPolicyIdId();
	}

	public Integer getPasswordPolicyIdId() {
		return passwordPolicyId;
	}

	public void setPasswordPolicyIdId(Integer passwordPolicyId) {
		this.passwordPolicyId = passwordPolicyId;
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
