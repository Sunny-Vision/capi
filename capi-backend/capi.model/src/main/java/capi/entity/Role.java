package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="Role")
public class Role  extends capi.entity.EntityBase{
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="RoleId")
	private Integer roleId;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Description")
	private String description;

	@Column(name="AuthorityLevel")
	private Integer authorityLevel;


	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "RoleFunction", 
			joinColumns = { @JoinColumn(name = "RoleId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "SystemFunctionId", nullable = false, updatable = false) })
	private Set<SystemFunction> functions = new HashSet<SystemFunction>();
	
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
	private Set<User> users = new HashSet<User>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
	private Set<Acting> actings = new HashSet<Acting>();
	
	public Integer getId(){
		return this.getRoleId();
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}


	public Set<SystemFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(Set<SystemFunction> functions) {
		this.functions = functions;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Acting> getActings() {
		return actings;
	}

	public void setActings(Set<Acting> actings) {
		this.actings = actings;
	}
}
