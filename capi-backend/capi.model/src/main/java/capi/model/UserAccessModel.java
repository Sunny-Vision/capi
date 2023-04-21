package capi.model;

import java.util.ArrayList;
import java.util.List;

public class UserAccessModel {

	private Integer userId;
	
	private Integer orgAuthorityLevel;
	
	private Integer authorityLevel;
	
	private List<String> functionList = new ArrayList<String>();
	
	private List<Integer> actedUsers = new ArrayList<Integer>();
	
	private String username;
	
	private String chineseName;
	
	private String englishName;
	
	private String destination;
	
	private String staffCode;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}

	public List<String> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<String> functionList) {
		this.functionList = functionList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public List<Integer> getActedUsers() {
		return actedUsers;
	}

	public void setActedUsers(List<Integer> actedUsers) {
		this.actedUsers = actedUsers;
	}

	public Integer getOrgAuthorityLevel() {
		return orgAuthorityLevel;
	}

	public void setOrgAuthorityLevel(Integer orgAuthorityLevel) {
		this.orgAuthorityLevel = orgAuthorityLevel;
	}

}
