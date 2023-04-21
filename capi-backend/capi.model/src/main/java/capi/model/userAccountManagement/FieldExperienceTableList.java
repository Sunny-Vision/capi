package capi.model.userAccountManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FieldExperienceTableList {

	private Integer userId;

	private String staffCode;

	private String team;

	private String chineseName;

	private String englishName;

	private String officePhoneNo;

	private String gender;

	private String destination;

	private Double accumulatedOT;

	private String omp;

	private String homeArea;

	private String gic;

//	private Integer rankId;

	private String rankName;

	private Integer supervisorId;

	private String supervisorStaffCode;

	private String supervisorChineseName;

	private String supervisorDestination;

	private Date dateOfEntry;

	private Date dateOfLeaving;

	private List<Integer> districtIds;

	private List<String> districtCode;

	private List<String> districtEnglishName;

	private Long countDistrict;

	private Date createdDate;

	private Date modifiedDate;
	
	private boolean isGHS;
	
	
	private ArrayList<StaffReasonModel> reasons =new ArrayList<StaffReasonModel>();
	

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Double getAccumulatedOT() {
		return accumulatedOT;
	}

	public void setAccumulatedOT(Double accumulatedOT) {
		this.accumulatedOT = accumulatedOT;
	}

	public String getOmp() {
		return omp;
	}

	public void setOmp(String omp) {
		this.omp = omp;
	}

	public String getHomeArea() {
		return homeArea;
	}

	public void setHomeArea(String homeArea) {
		this.homeArea = homeArea;
	}

	public String getGic() {
		return gic;
	}

	public void setGic(String gic) {
		this.gic = gic;
	}

//	public Integer getRankId() {
//		return rankId;
//	}
//
//	public void setRankId(Integer rankId) {
//		this.rankId = rankId;
//	}

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getSupervisorStaffCode() {
		return supervisorStaffCode;
	}

	public void setSupervisorStaffCode(String supervisorStaffCode) {
		this.supervisorStaffCode = supervisorStaffCode;
	}

	public String getSupervisorChineseName() {
		return supervisorChineseName;
	}

	public void setSupervisorChineseName(String supervisorChineseName) {
		this.supervisorChineseName = supervisorChineseName;
	}

	public String getSupervisorDestination() {
		return supervisorDestination;
	}

	public void setSupervisorDestination(String supervisorDestination) {
		this.supervisorDestination = supervisorDestination;
	}

	public Date getDateOfEntry() {
		return dateOfEntry;
	}

	public void setDateOfEntry(Date dateOfEntry) {
		this.dateOfEntry = dateOfEntry;
	}

	public Date getDateOfLeaving() {
		return dateOfLeaving;
	}

	public void setDateOfLeaving(Date dateOfLeaving) {
		this.dateOfLeaving = dateOfLeaving;
	}

	public List<Integer> getDistrictIds() {
		return districtIds;
	}

	public void setDistrictIds(List<Integer> districtIds) {
		this.districtIds = districtIds;
	}

	public List<String> getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(List<String> districtCode) {
		this.districtCode = districtCode;
	}

	public List<String> getDistrictEnglishName() {
		return districtEnglishName;
	}

	public void setDistrictEnglishName(List<String> districtEnglishName) {
		this.districtEnglishName = districtEnglishName;
	}

	public Long getCountDistrict() {
		return countDistrict;
	}

	public void setCountDistrict(Long countDistrict) {
		this.countDistrict = countDistrict;
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

	public ArrayList<StaffReasonModel> getReasons() {
		return reasons;
	}

	public void setReasons(ArrayList<StaffReasonModel> reasons) {
		this.reasons = reasons;
	}

	public boolean isGHS() {
		return isGHS;
	}

	public void setGHS(boolean isGHS) {
		this.isGHS = isGHS;
	}

}
