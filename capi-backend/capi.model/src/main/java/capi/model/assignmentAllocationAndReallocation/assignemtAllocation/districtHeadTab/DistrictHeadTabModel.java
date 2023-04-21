package capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab;

import java.io.Serializable;

public class DistrictHeadTabModel implements Serializable{
	private Integer districtId;
	private Integer userId;
	
	public Integer getDistrictId() {
		return districtId;
	}
	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
}
