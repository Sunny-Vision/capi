package capi.model.api.onlineFunction;

import java.util.Date;

public class NonScheduleAssignmentList {

	//For Assignment
	private Integer assignmentId;
	
	private Date assignedCollectionDate;
	
	private Date startDate;
	
	private Date endDate;

	//For User
	private Integer userId;
	
	//For Outlet
	private Integer outletId;
	
	private String outletName;
	
	private Integer collectionMethod;
	
	private String streetAddress;
	
	private String detailAddress;
	
	//For Tpu
	private Integer tpuId;
	
	private String tpuCode;
	
	private String tpuDescription;
	
	//For District
	private Integer districtId;
	
	private String districtCode;
	
	private String districtChineseName;
	
	private String districtEnglishName;
	
	//For QuotationRecord
	private Long unstartedCnt;
	
	private Long totalCnt;
	
	private Long normalCnt;
	
	private Long verifyCnt;
	
	private Long revisitCnt;
	
	//For OutletType
	private String outletTypeCode;
	
	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Date getAssignedCollectionDate() {
		return assignedCollectionDate;
	}

	public void setAssignedCollectionDate(Date assignedCollectionDate) {
		this.assignedCollectionDate = assignedCollectionDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public Integer getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(Integer collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getTpuCode() {
		return tpuCode;
	}

	public void setTpuCode(String tpuCode) {
		this.tpuCode = tpuCode;
	}

	public String getTpuDescription() {
		return tpuDescription;
	}

	public void setTpuDescription(String tpuDescription) {
		this.tpuDescription = tpuDescription;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictChineseName() {
		return districtChineseName;
	}

	public void setDistrictChineseName(String districtChineseName) {
		this.districtChineseName = districtChineseName;
	}

	public String getDistrictEnglishName() {
		return districtEnglishName;
	}

	public void setDistrictEnglishName(String districtEnglishName) {
		this.districtEnglishName = districtEnglishName;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getTpuId() {
		return tpuId;
	}

	public void setTpuId(Integer tpuId) {
		this.tpuId = tpuId;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public Long getUnstartedCnt() {
		return unstartedCnt;
	}

	public void setUnstartedCnt(Long unstartedCnt) {
		this.unstartedCnt = unstartedCnt;
	}

	public Long getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(Long totalCnt) {
		this.totalCnt = totalCnt;
	}

	public Long getNormalCnt() {
		return normalCnt;
	}

	public void setNormalCnt(Long normalCnt) {
		this.normalCnt = normalCnt;
	}

	public Long getVerifyCnt() {
		return verifyCnt;
	}

	public void setVerifyCnt(Long verifyCnt) {
		this.verifyCnt = verifyCnt;
	}

	public Long getRevisitCnt() {
		return revisitCnt;
	}

	public void setRevisitCnt(Long revisitCnt) {
		this.revisitCnt = revisitCnt;
	}
	
}
