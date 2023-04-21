package capi.model.userAccountManagement;

import java.util.Date;
import java.util.List;


public class RoleEditModel {

	private Integer roleId;

	private String roleName;

	private String roleDescription;

	private List<Integer> backendSystemFunctionId;

	private List<String> backendFunctionCode;

	private List<String> backendFunctionDescription;

	private List<Integer> frontendSystemFunctionId;

	private List<String> frontendFunctionCode;

	private List<String> frontendFunctionDescription;

	private List<Integer> authorityLevelId;
	
	private Integer authorityLevel;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public List<Integer> getBackendSystemFunctionId() {
		return backendSystemFunctionId;
	}

	public void setBackendSystemFunctionId(List<Integer> backendSystemFunctionId) {
		this.backendSystemFunctionId = backendSystemFunctionId;
	}

	public List<String> getBackendFunctionCode() {
		return backendFunctionCode;
	}

	public void setBackendFunctionCode(List<String> backendFunctionCode) {
		this.backendFunctionCode = backendFunctionCode;
	}

	public List<String> getBackendFunctionDescription() {
		return backendFunctionDescription;
	}

	public void setBackendFunctionDescription(
			List<String> backendFunctionDescription) {
		this.backendFunctionDescription = backendFunctionDescription;
	}

	public List<Integer> getFrontendSystemFunctionId() {
		return frontendSystemFunctionId;
	}

	public void setFrontendSystemFunctionId(List<Integer> frontendSystemFunctionId) {
		this.frontendSystemFunctionId = frontendSystemFunctionId;
	}

	public List<String> getFrontendFunctionCode() {
		return frontendFunctionCode;
	}

	public void setFrontendFunctionCode(List<String> frontendFunctionCode) {
		this.frontendFunctionCode = frontendFunctionCode;
	}

	public List<String> getFrontendFunctionDescription() {
		return frontendFunctionDescription;
	}

	public void setFrontendFunctionDescription(
			List<String> frontendFunctionDescription) {
		this.frontendFunctionDescription = frontendFunctionDescription;
	}

	public List<Integer> getAuthorityLevelId() {
		return authorityLevelId;
	}

	public void setAuthorityLevelId(List<Integer> authorityLevelId) {
		this.authorityLevelId = authorityLevelId;
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

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}

	
	public boolean hasAuthority(int auth){
		return this.getAuthorityLevel() != null && (this.getAuthorityLevel() & auth) == auth;
	}

}
