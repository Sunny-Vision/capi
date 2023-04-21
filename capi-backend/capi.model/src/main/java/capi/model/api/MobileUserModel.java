package capi.model.api;

import java.util.ArrayList;
import java.util.List;

public class MobileUserModel {

	private Integer userId;
	
	private String username;
	
	private String password;
	
	private String staffCode;
	
	private String destination;
	
	private String englishName;
	
	private String chineseName;
	
	private Double accumulatedOT;
	
	private String status;
	
	private String team;
	
	private Integer authorityLevel;
	
	private List<String> accessRight = new ArrayList<String>();
	

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public Double getAccumulatedOT() {
		return accumulatedOT;
	}

	public void setAccumulatedOT(Double accumulatedOT) {
		this.accumulatedOT = accumulatedOT;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public List<String> getAccessRight() {
		return accessRight;
	}

	public void setAccessRight(List<String> accessRight) {
		this.accessRight = accessRight;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}	
	
	
	
	
}
