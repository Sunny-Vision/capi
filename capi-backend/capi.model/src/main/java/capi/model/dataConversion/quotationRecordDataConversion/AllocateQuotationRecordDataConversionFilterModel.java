package capi.model.dataConversion.quotationRecordDataConversion;

import java.util.Date;
import java.util.List;

public class AllocateQuotationRecordDataConversionFilterModel {
	
	private List<Integer> batchCode;
	
	private List<Integer> unitIds;
	
	private Integer indoorOfficer;
	
	private String indoorAllocationCode;
	
	private Integer purposeId;
	
	private Integer groupId;
	
	private Integer subGroupId;
	
	private Integer itemId;
	
	private List<String> outletTypeId;
	
	private Double greaterThan;
	
	private Double lessThan;
	
	private Double equal;
	
	private Boolean ruaQuotationStatus;
	
	private Boolean quotationRecordStatus;
	
	private Integer seasonalItem;
	
	private Boolean allocatedIndoorOfficer;
	
	private Boolean outlectCategoryRemark;
	
	private Boolean withPriceRemark;
	
	private Boolean withOtherRemark;
	
	private Boolean withProductRemark;
	
	private Boolean withDiscountRemark;
	
	private Boolean newProductCase;
	
	private Boolean changeProductCase;
	
	private Boolean newRecruitmentCase;
	
	private Boolean spicing;
	
	private Boolean fr;
	
	private Boolean applicability;
	
	private String search;
	
	private String surveyForm;
	
	private Boolean consignmentCounter;
	
	private Boolean withPriceReason;

	private Boolean appliedFR;
	
	private Boolean withDiscountPattern;
	
	private Date referenceDate;
	
	private Integer availability;
	
	private Integer firmStatus;
	
	private Boolean pr;
	
	private List<Integer> quotationIds;
	
	private Boolean remark;
	
	public List<Integer> getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(List<Integer> batchCode) {
		this.batchCode = batchCode;
	}

	public String getIndoorAllocationCode() {
		return indoorAllocationCode;
	}

	public void setIndoorAllocationCode(String indoorAllocationCode) {
		this.indoorAllocationCode = indoorAllocationCode;
	}

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(Integer subGroupId) {
		this.subGroupId = subGroupId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public List<String> getOutletTypeId() {
		return outletTypeId;
	}

	public void setOutletTypeId(List<String> outletTypeId) {
		this.outletTypeId = outletTypeId;
	}
	
	public Double getGreaterThan() {
		return greaterThan;
	}

	public void setGreaterThan(Double greaterThan) {
		this.greaterThan = greaterThan;
	}

	public Double getLessThan() {
		return lessThan;
	}

	public void setLessThan(Double lessThan) {
		this.lessThan = lessThan;
	}

	public Double getEqual() {
		return equal;
	}

	public void setEqual(Double equal) {
		this.equal = equal;
	}

	public Boolean getRuaQuotationStatus() {
		return ruaQuotationStatus;
	}

	public void setRuaQuotationStatus(Boolean ruaQuotationStatus) {
		this.ruaQuotationStatus = ruaQuotationStatus;
	}

	public Boolean getQuotationRecordStatus() {
		return quotationRecordStatus;
	}

	public void setQuotationRecordStatus(Boolean quotationRecordStatus) {
		this.quotationRecordStatus = quotationRecordStatus;
	}

	public Integer getSeasonalItem() {
		return seasonalItem;
	}

	public void setSeasonalItem(Integer seasonalItem) {
		this.seasonalItem = seasonalItem;
	}

	public Boolean getAllocatedIndoorOfficer() {
		return allocatedIndoorOfficer;
	}

	public void setAllocatedIndoorOfficer(Boolean allocatedIndoorOfficer) {
		this.allocatedIndoorOfficer = allocatedIndoorOfficer;
	}

	public Boolean getOutlectCategoryRemark() {
		return outlectCategoryRemark;
	}

	public void setOutlectCategoryRemark(Boolean outlectCategoryRemark) {
		this.outlectCategoryRemark = outlectCategoryRemark;
	}

	public Boolean getWithPriceRemark() {
		return withPriceRemark;
	}

	public void setWithPriceRemark(Boolean withPriceRemark) {
		this.withPriceRemark = withPriceRemark;
	}

	public Boolean getWithOtherRemark() {
		return withOtherRemark;
	}

	public void setWithOtherRemark(Boolean withOtherRemark) {
		this.withOtherRemark = withOtherRemark;
	}

	public Boolean getWithProductRemark() {
		return withProductRemark;
	}

	public void setWithProductRemark(Boolean withProductRemark) {
		this.withProductRemark = withProductRemark;
	}

	public Boolean getWithDiscountRemark() {
		return withDiscountRemark;
	}

	public void setWithDiscountRemark(Boolean withDiscountRemark) {
		this.withDiscountRemark = withDiscountRemark;
	}

	public Boolean getNewProductCase() {
		return newProductCase;
	}

	public void setNewProductCase(Boolean newProductCase) {
		this.newProductCase = newProductCase;
	}

	public Boolean getChangeProductCase() {
		return changeProductCase;
	}

	public void setChangeProductCase(Boolean changeProductCase) {
		this.changeProductCase = changeProductCase;
	}

	public Boolean getNewRecruitmentCase() {
		return newRecruitmentCase;
	}

	public void setNewRecruitmentCase(Boolean newRecruitmentCase) {
		this.newRecruitmentCase = newRecruitmentCase;
	}

	public Boolean getSpicing() {
		return spicing;
	}

	public void setSpicing(Boolean spicing) {
		this.spicing = spicing;
	}

	public Boolean getFr() {
		return fr;
	}

	public void setFr(Boolean fr) {
		this.fr = fr;
	}

	public Boolean getApplicability() {
		return applicability;
	}

	public void setApplicability(Boolean applicability) {
		this.applicability = applicability;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public List<Integer> getUnitIds() {
		return unitIds;
	}

	public void setUnitIds(List<Integer> unitIds) {
		this.unitIds = unitIds;
	}

	public Integer getIndoorOfficer() {
		return indoorOfficer;
	}

	public void setIndoorOfficer(Integer indoorOfficer) {
		this.indoorOfficer = indoorOfficer;
	}

	public String getSurveyForm() {
		return surveyForm;
	}

	public void setSurveyForm(String surveyForm) {
		this.surveyForm = surveyForm;
	}

	public Boolean getConsignmentCounter() {
		return consignmentCounter;
	}

	public void setConsignmentCounter(Boolean consignmentCounter) {
		this.consignmentCounter = consignmentCounter;
	}

	public Boolean getWithPriceReason() {
		return withPriceReason;
	}

	public void setWithPriceReason(Boolean withPriceReason) {
		this.withPriceReason = withPriceReason;
	}

	public Boolean getAppliedFR() {
		return appliedFR;
	}

	public void setAppliedFR(Boolean appliedFR) {
		this.appliedFR = appliedFR;
	}

	public Boolean getWithDiscountPattern() {
		return withDiscountPattern;
	}

	public void setWithDiscountPattern(Boolean withDiscountPattern) {
		this.withDiscountPattern = withDiscountPattern;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Integer getFirmStatus() {
		return firmStatus;
	}

	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}

	public Boolean getPr() {
		return pr;
	}

	public void setPr(Boolean pr) {
		this.pr = pr;
	}

	public List<Integer> getQuotationIds() {
		return quotationIds;
	}

	public void setQuotationIds(List<Integer> quotationIds) {
		this.quotationIds = quotationIds;
	}

	public Boolean getRemark() {
		return remark;
	}

	public void setRemark(Boolean remark) {
		this.remark = remark;
	}
	
}
