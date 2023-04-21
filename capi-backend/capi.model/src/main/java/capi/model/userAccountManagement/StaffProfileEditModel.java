package capi.model.userAccountManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaffProfileEditModel {

	private Integer userId;

	private String username;

	private String password;

	private List<Integer> userRoleIds;

	private String userRoleJSON;

	private String status;

	private String dateOfEntry;

	private String dateOfLeaving;

	private Integer staffType;

	private String staffCode;

	private String englishName;

	private String chineseName;

	private String officePhoneNo;

	private String gender;

	private ArrayList<Integer> batchCodeIds;

	private Integer rankId;

	private String rankLabel;

	private String destination;

	private String supervisor;

	private Integer supervisorId;

	private Date createdDate;

	private Date modifiedDate;
	
	private String office;
	
	private String office2;

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

	public List<Integer> getUserRoleIds() {
		return userRoleIds;
	}

	public void setUserRoleIds(List<Integer> userRoleIds) {
		this.userRoleIds = userRoleIds;
	}

	public String getUserRoleJSON() {
		return userRoleJSON;
	}

	public void setUserRoleJSON(String userRoleJSON) {
		this.userRoleJSON = userRoleJSON;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateOfEntry() {
		return dateOfEntry;
	}

	public void setDateOfEntry(String dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public String getDateOfLeaving() {
		return dateOfLeaving;
	}

	public void setDateOfLeaving(String dateOfLeaving) {
		this.dateOfLeaving = dateOfLeaving;
	}

	public Integer getStaffType() {
		return staffType;
	}

	public void setStaffType(Integer staffType) {
		this.staffType = staffType;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
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

	public String getOfficePhoneNo() {
		return officePhoneNo;
	}

	public void setOfficePhoneNo(String officePhoneNo) {
		this.officePhoneNo = officePhoneNo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public ArrayList<Integer> getBatchCodeIds() {
		return batchCodeIds;
	}

	public void setBatchCodeIds(ArrayList<Integer> batchCodeIds) {
		this.batchCodeIds = batchCodeIds;
	}

	public Integer getRankId() {
		return rankId;
	}

	public void setRankId(Integer rankId) {
		this.rankId = rankId;
	}

	public String getRankLabel() {
		return rankLabel;
	}

	public void setRankLabel(String rankLabel) {
		this.rankLabel = rankLabel;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
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

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getOffice2() {
		return office2;
	}

	public void setOffice2(String office2) {
		this.office2 = office2;
	}

}
