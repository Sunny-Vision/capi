package capi.model.report;

import java.util.Date;

public class SummaryStatisticsOfPriceRelativesReport {

	private String staffCode;
	
	private String staffChinName;
	
	private String staffEngName;
	
	private Date referenceMonth;
	
	private String purpose;
	
	private String cpiBasePeriod;
	
	// Unit
	private String unitChinName;
	
	private String unitCode;
	
	private String unitEngName;
	
	// Unit Statistic
	private Double unitPR;
	
	private Double unitStandardDeviationPR;
	
	private Double unitMinPR;
	
	private Double unitMaxPR;
	
	private Double unitAvgCurrentSPrice;
	
	private Double unitAvgLastSPrice;
	
	private Double unitLastHasPriceAvgCurrSPrcie;

	// Sub Item
	private String subItemChinName;
	
	private String subItemCode;
	
	private String subItemEngName;
	
	private String compilationMethod;
	
	// Sub Item Statistic
	private Double subItemPR;
	
	// Outlet Type
	private String outletTypeChinName;
	
	private String outletTypeCode;
	
	private String outletTypeEngName;
	
	// Outlet Type Statistic
	private Double outletTypePR;
	
	// Item
	private String itemChinName;
	
	private String itemCode;
	
	private String itemEngName;
	
	// Item Statistic
	private Double itemPR;
	
	// Sub Group
	private String subGroupChinName;
	
	private String subGroupCode;

	private String subGroupEngName;
	
	// Sub Group Statistic
	private Double subGroupPR;
	
	// Group
	private String groupChinName;
	
	private String groupCode;
	
	private String groupEngName;
	
	// Group Statistic
	private Double groupPR;
	
	// Section
	private String sectionEngName;
	
	private String sectionCode;
	
	private String sectionChinName;
	
	// Section Statistic
	private Double sectionPR;

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getStaffChinName() {
		return staffChinName;
	}

	public void setStaffChinName(String staffChinName) {
		this.staffChinName = staffChinName;
	}

	public String getStaffEngName() {
		return staffEngName;
	}

	public void setStaffEngName(String staffEngName) {
		this.staffEngName = staffEngName;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Double getUnitPR() {
		return unitPR;
	}

	public void setUnitPR(Double unitPR) {
		this.unitPR = unitPR;
	}

	public Double getUnitStandardDeviationPR() {
		return unitStandardDeviationPR;
	}

	public void setUnitStandardDeviationPR(Double unitStandardDeviationPR) {
		this.unitStandardDeviationPR = unitStandardDeviationPR;
	}

	public Double getUnitMinPR() {
		return unitMinPR;
	}

	public void setUnitMinPR(Double unitMinPR) {
		this.unitMinPR = unitMinPR;
	}

	public Double getUnitMaxPR() {
		return unitMaxPR;
	}

	public void setUnitMaxPR(Double unitMaxPR) {
		this.unitMaxPR = unitMaxPR;
	}

	public String getSubItemCode() {
		return subItemCode;
	}

	public void setSubItemCode(String subItemCode) {
		this.subItemCode = subItemCode;
	}

	public String getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(String compilationMethod) {
		this.compilationMethod = compilationMethod;
	}

	public Double getSubItemPR() {
		return subItemPR;
	}

	public void setSubItemPR(Double subItemPR) {
		this.subItemPR = subItemPR;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}
	
	public String getOutletType() {
		return outletTypeCode.substring(outletTypeCode.length() - 3);
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public Double getOutletTypePR() {
		return outletTypePR;
	}

	public void setOutletTypePR(Double outletTypePR) {
		this.outletTypePR = outletTypePR;
	}


	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Double getItemPR() {
		return itemPR;
	}

	public void setItemPR(Double itemPR) {
		this.itemPR = itemPR;
	}


	public String getSubGroupCode() {
		return subGroupCode;
	}

	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}

	public Double getSubGroupPR() {
		return subGroupPR;
	}

	public void setSubGroupPR(Double subGroupPR) {
		this.subGroupPR = subGroupPR;
	}



	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public Double getGroupPR() {
		return groupPR;
	}

	public void setGroupPR(Double groupPR) {
		this.groupPR = groupPR;
	}



	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public Double getSectionPR() {
		return sectionPR;
	}

	public void setSectionPR(Double sectionPR) {
		this.sectionPR = sectionPR;
	}

	public String getUnitChinName() {
		return unitChinName;
	}

	public void setUnitChinName(String unitChinName) {
		this.unitChinName = unitChinName;
	}

	public String getSubItemChinName() {
		return subItemChinName;
	}

	public void setSubItemChinName(String subItemChinName) {
		this.subItemChinName = subItemChinName;
	}

	public String getOutletTypeChinName() {
		return outletTypeChinName;
	}

	public void setOutletTypeChinName(String outletTypeChinName) {
		this.outletTypeChinName = outletTypeChinName;
	}

	public String getItemChinName() {
		return itemChinName;
	}

	public void setItemChinName(String itemChinName) {
		this.itemChinName = itemChinName;
	}

	public String getSubGroupChinName() {
		return subGroupChinName;
	}

	public void setSubGroupChinName(String subGroupChinName) {
		this.subGroupChinName = subGroupChinName;
	}

	public String getGroupChinName() {
		return groupChinName;
	}

	public void setGroupChinName(String groupChinName) {
		this.groupChinName = groupChinName;
	}

	public String getSectionChinName() {
		return sectionChinName;
	}

	public void setSectionChinName(String sectionChinName) {
		this.sectionChinName = sectionChinName;
	}
	
	public Double getUnitAvgCurrentSPrice() {
		return unitAvgCurrentSPrice;
	}

	public void setUnitAvgCurrentSPrice(Double unitAvgCurrentSPrice) {
		this.unitAvgCurrentSPrice = unitAvgCurrentSPrice;
	}

	public Double getUnitAvgLastSPrice() {
		return unitAvgLastSPrice;
	}

	public void setUnitAvgLastSPrice(Double unitAvgLastSPrice) {
		this.unitAvgLastSPrice = unitAvgLastSPrice;
	}

	public Double getUnitLastHasPriceAvgCurrSPrcie() {
		return unitLastHasPriceAvgCurrSPrcie;
	}

	public void setUnitLastHasPriceAvgCurrSPrcie(Double unitLastHasPriceAvgCurrSPrcie) {
		this.unitLastHasPriceAvgCurrSPrcie = unitLastHasPriceAvgCurrSPrcie;
	}

	public String getUnitEngName() {
		return unitEngName;
	}

	public void setUnitEngName(String unitEngName) {
		this.unitEngName = unitEngName;
	}

	public String getSubItemEngName() {
		return subItemEngName;
	}

	public void setSubItemEngName(String subItemEngName) {
		this.subItemEngName = subItemEngName;
	}

	public String getOutletTypeEngName() {
		return outletTypeEngName;
	}

	public void setOutletTypeEngName(String outletTypeEngName) {
		this.outletTypeEngName = outletTypeEngName;
	}

	public String getItemEngName() {
		return itemEngName;
	}

	public void setItemEngName(String itemEngName) {
		this.itemEngName = itemEngName;
	}

	public String getSubGroupEngName() {
		return subGroupEngName;
	}

	public void setSubGroupEngName(String subGroupEngName) {
		this.subGroupEngName = subGroupEngName;
	}

	public String getGroupEngName() {
		return groupEngName;
	}

	public void setGroupEngName(String groupEngName) {
		this.groupEngName = groupEngName;
	}

	public String getSectionEngName() {
		return sectionEngName;
	}

	public void setSectionEngName(String sectionEngName) {
		this.sectionEngName = sectionEngName;
	}

}
