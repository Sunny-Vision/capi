package capi.model.itineraryPlanning;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItineraryPlanOutletModel implements Comparable<ItineraryPlanOutletModel>{
	private Integer itineraryPlanOutletId;
	private Integer sequence;
	private Integer id;
	private Integer outletId;
	private String firmCode;
	private String firm;
	private String marketName;
	private Integer districtId;
	private String district;
	private Integer tpuId;
	private String tpu;
	private String address;
	private String detailAddress;
	private Long noOfQuotation;
	private Long noOfAssignment;
	private Date convenientStartTime;
	private Date convenientEndTime;
	private Date convenientStartTime2;
	private Date convenientEndTime2;
	private String convenientTime;
	private String convenientTime2;
	private String latitude;
	private String longitude;
	private String outletRemark;
	private String deadline;
	private String status;
	private Integer planType;
	private String outletType;
	private List<Integer> assignmentIds = new ArrayList<Integer>();
	private String referenceNo;
	
	private Boolean removable;
	
	private Long countAssignedDate;
	
	public Integer getItineraryPlanOutletId() {
		return itineraryPlanOutletId;
	}
	public void setItineraryPlanOutletId(Integer itineraryPlanOutletId) {
		this.itineraryPlanOutletId = itineraryPlanOutletId;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public String getFirmCode() {
		return firmCode;
	}
	public void setFirmCode(String firmCode) {
		this.firmCode = firmCode;
	}
	public String getFirm() {
		return firm;
	}
	public void setFirm(String firm) {
		this.firm = firm;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public Integer getDistrictId() {
		return districtId;
	}
	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public Integer getTpuId() {
		return tpuId;
	}
	public void setTpuId(Integer tpuId) {
		this.tpuId = tpuId;
	}
	public String getTpu() {
		return tpu;
	}
	public void setTpu(String tpu) {
		this.tpu = tpu;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getNoOfQuotation() {
		return noOfQuotation;
	}
	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}
	public Long getNoOfAssignment() {
		return noOfAssignment;
	}
	public void setNoOfAssignment(Long noOfAssignment) {
		this.noOfAssignment = noOfAssignment;
	}
	public Date getConvenientStartTime() {
		return convenientStartTime;
	}
	public void setConvenientStartTime(Date convenientStartTime) {
		this.convenientStartTime = convenientStartTime;
	}
	public Date getConvenientEndTime() {
		return convenientEndTime;
	}
	public void setConvenientEndTime(Date convenientEndTime) {
		this.convenientEndTime = convenientEndTime;
	}
	public String getConvenientTime() {
		return convenientTime;
	}
	public void setConvenientTime(String convenientTime) {
		this.convenientTime = convenientTime;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getOutletRemark() {
		return outletRemark;
	}
	public void setOutletRemark(String outletRemark) {
		this.outletRemark = outletRemark;
	}
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getPlanType() {
		return planType;
	}
	public void setPlanType(Integer planType) {
		this.planType = planType;
	}
	public String getOutletType() {
		return outletType;
	}
	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}
	public List<Integer> getAssignmentIds() {
		return assignmentIds;
	}
	public void setAssignmentIds(List<Integer> assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	@Override
    public int compareTo(ItineraryPlanOutletModel another) {
		return this.sequence - another.sequence;
    }
	public Date getConvenientStartTime2() {
		return convenientStartTime2;
	}
	public void setConvenientStartTime2(Date convenientStartTime2) {
		this.convenientStartTime2 = convenientStartTime2;
	}
	public Date getConvenientEndTime2() {
		return convenientEndTime2;
	}
	public void setConvenientEndTime2(Date convenientEndTime2) {
		this.convenientEndTime2 = convenientEndTime2;
	}
	public String getConvenientTime2() {
		return convenientTime2;
	}
	public void setConvenientTime2(String convenientTime2) {
		this.convenientTime2 = convenientTime2;
	}
	public String getDetailAddress() {
		return detailAddress;
	}
	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
	public Boolean getRemovable() {
		return removable;
	}
	public void setRemovable(Boolean removable) {
		this.removable = removable;
	}
	public Long getCountAssignedDate() {
		return countAssignedDate;
	}
	public void setCountAssignedDate(Long countAssignedDate) {
		this.countAssignedDate = countAssignedDate;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
}
