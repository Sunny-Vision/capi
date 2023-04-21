package capi.model.itineraryPlanning;

public class QCItineraryPlanItemModel implements Comparable<QCItineraryPlanItemModel> {
	
	private Integer qcItineraryPlanItemId;
	private String remark;
	private Integer sequence;
	private String taskName;
	private String location;
	private String session;
	private Integer itemType;
	private Integer spotCheckFormId;
	private Integer supervisoryVisitFormId;
	private Integer peCheckFormId;
	private String officerName;
	private String displayItem;
	
	public Integer getQcItineraryPlanItemId() {
		return qcItineraryPlanItemId;
	}
	public void setQcItineraryPlanItemId(Integer qcItineraryPlanItemId) {
		this.qcItineraryPlanItemId = qcItineraryPlanItemId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public Integer getItemType() {
		return itemType;
	}
	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}
	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}
	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}
	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}
	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
	}
	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}
	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}
	public String getOfficerName() {
		return officerName;
	}
	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}
	
	public String getDisplayItem() {
		return displayItem;
	}
	public void setDisplayItem(String displayItem) {
		this.displayItem = displayItem;
	}
	@Override
    public int compareTo(QCItineraryPlanItemModel another) {
        return (this.getSession() + this.getSequence().toString()).compareTo((another.getSession()+another.getSequence().toString()));

    }
}
