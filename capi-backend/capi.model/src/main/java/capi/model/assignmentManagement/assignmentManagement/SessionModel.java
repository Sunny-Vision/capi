package capi.model.assignmentManagement.assignmentManagement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import capi.model.DatatableRequestModel;

public class SessionModel implements Serializable{
	private String tab;
	private List<Integer> ids;
	private String unitCategory;
	private Integer dateSelectedAssignmentId;
	private String dateSelected;
	private int assignmentId;
	private Integer userId;
	private Integer previousId;
	private Integer nextId;
	private Integer currentPositionInIds;
	private String consignmentCounter;
	private Integer verificationType;
	private Integer outletId;
	private List<Map<String, String>> order;
	private DatatableRequestModel lastRequestModel;
	private Integer personInChargeId; // 2020-06-23: filter new recruitment list by field officer (PIR-231)
	
	public String getTab() {
		return tab;
	}
	public void setTab(String tab) {
		this.tab = tab;
	}
	public List<Integer> getIds() {
		return ids;
	}
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	public String getUnitCategory() {
		return unitCategory;
	}
	public void setUnitCategory(String unitCategory) {
		this.unitCategory = unitCategory;
	}
	public Integer getDateSelectedAssignmentId() {
		return dateSelectedAssignmentId;
	}
	public void setDateSelectedAssignmentId(Integer dateSelectedAssignmentId) {
		this.dateSelectedAssignmentId = dateSelectedAssignmentId;
	}
	public String getDateSelected() {
		return dateSelected;
	}
	public void setDateSelected(String dateSelected) {
		this.dateSelected = dateSelected;
	}
	public int getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(int assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getNextId() {
		return nextId;
	}
	public void setNextId(Integer nextId) {
		this.nextId = nextId;
	}
	public Integer getPreviousId() {
		return previousId;
	}
	public void setPreviousId(Integer previousId) {
		this.previousId = previousId;
	}
	public Integer getCurrentPositionInIds() {
		return currentPositionInIds;
	}
	public void setCurrentPositionInIds(Integer currentPositionInIds) {
		this.currentPositionInIds = currentPositionInIds;
	}
	public String getConsignmentCounter() {
		return consignmentCounter;
	}
	public void setConsignmentCounter(String consignmentCounter) {
		this.consignmentCounter = consignmentCounter;
	}
	public Integer getVerificationType() {
		return verificationType;
	}
	public void setVerificationType(Integer verificationType) {
		this.verificationType = verificationType;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public List<Map<String, String>> getOrder() {
		return order;
	}
	public void setOrder(List<Map<String, String>> order) {
		this.order = order;
	}
	public DatatableRequestModel getLastRequestModel() {
		return lastRequestModel;
	}
	public void setLastRequestModel(DatatableRequestModel lastRequestModel) {
		this.lastRequestModel = lastRequestModel;
	}
	public Integer getPersonInChargeId() {
		return personInChargeId;
	}
	public void setPersonInChargeId(Integer personInChargeId) {
		this.personInChargeId = personInChargeId;
	}
}
