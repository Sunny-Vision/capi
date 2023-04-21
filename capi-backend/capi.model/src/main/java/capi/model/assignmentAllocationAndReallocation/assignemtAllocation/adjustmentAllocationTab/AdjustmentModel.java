package capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab;

import java.io.Serializable;

public class AdjustmentModel implements Serializable{
	private Integer fromUserId;
	private Integer toUserId;
	private String fromUserName;
	private String toUserName;
	private Double manDay;
	private String remark;
	public Integer getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(Integer fromUserId) {
		this.fromUserId = fromUserId;
	}
	public Integer getToUserId() {
		return toUserId;
	}
	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}
	public Double getManDay() {
		return manDay;
	}
	public void setManDay(Double manDay) {
		this.manDay = manDay;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	
	
}
