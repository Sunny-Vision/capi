package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="SystemFunction", uniqueConstraints = {@UniqueConstraint(columnNames = "Code")})
public class SystemFunction extends capi.entity.EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SystemFunctionId")
	private Integer systemFunctionId;
	
	@Column(name="Code")
	private String code;
	
	@Column(name="Description")
	private String description;
	
	@Column(name="IsMobile")
	private boolean isMobile;
	

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "functions")
	private Set<Role> roles = new HashSet<Role>();
	
	public Integer getId(){
		return this.getSystemFunctionId();
	}
	
	
	public Integer getSystemFunctionId() {
		return systemFunctionId;
	}

	public void setSystemFunctionId(Integer systemFunctionId) {
		this.systemFunctionId = systemFunctionId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}


	public boolean getIsMobile() {
		return isMobile;
	}


	public void setIsMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
	
}
