package capi.model;

import java.util.List;

public class JsTreeRequestGetBottomEntityIdsModel {
	private List<String> cpiBasePeriods;
	private List<Integer> sectionIds;
	private List<Integer> groupIds;
	private List<Integer> subGroupIds;
	private List<Integer> itemIds;
	private List<Integer> outletTypeIds;
	private List<Integer> subItemIds;
	private List<Integer> unitIds;
	private String bottomEntityClass;
	
	public List<Integer> getSectionIds() {
		return sectionIds;
	}
	public void setSectionIds(List<Integer> sectionIds) {
		this.sectionIds = sectionIds;
	}
	public List<Integer> getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(List<Integer> groupIds) {
		this.groupIds = groupIds;
	}
	public List<Integer> getSubGroupIds() {
		return subGroupIds;
	}
	public void setSubGroupIds(List<Integer> subGroupIds) {
		this.subGroupIds = subGroupIds;
	}
	public List<Integer> getItemIds() {
		return itemIds;
	}
	public void setItemIds(List<Integer> itemIds) {
		this.itemIds = itemIds;
	}
	public List<Integer> getOutletTypeIds() {
		return outletTypeIds;
	}
	public void setOutletTypeIds(List<Integer> outletTypeIds) {
		this.outletTypeIds = outletTypeIds;
	}
	public List<Integer> getSubItemIds() {
		return subItemIds;
	}
	public void setSubItemIds(List<Integer> subItemIds) {
		this.subItemIds = subItemIds;
	}
	public List<Integer> getUnitIds() {
		return unitIds;
	}
	public void setUnitIds(List<Integer> unitIds) {
		this.unitIds = unitIds;
	}
	public String getBottomEntityClass() {
		return bottomEntityClass;
	}
	public void setBottomEntityClass(String bottomEntityClass) {
		this.bottomEntityClass = bottomEntityClass;
	}
	public List<String> getCpiBasePeriods() {
		return cpiBasePeriods;
	}
	public void setCpiBasePeriods(List<String> cpiBasePeriods) {
		this.cpiBasePeriods = cpiBasePeriods;
	}
}
