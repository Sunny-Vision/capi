package capi.model.timeLogManagement;

public class TimeLogSelect2Item {

    private String id;
    private Integer firmStatus;
    private String address;
    private Integer marketType;
    private Long count;
    private Long total;
    private Long building;
    private Integer assignmentId;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return id;
	}
	public void setText(String text) {
		this.id = text;
	}
	public Integer getFirmStatus() {
		return firmStatus;
	}
	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getMarketType() {
		return marketType;
	}
	public void setMarketType(Integer marketType) {
		this.marketType = marketType;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public Long getBuilding() {
		return building;
	}
	public void setBuilding(Long building) {
		this.building = building;
	}
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

}
