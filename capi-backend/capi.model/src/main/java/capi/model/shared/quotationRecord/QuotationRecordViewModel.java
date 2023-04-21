package capi.model.shared.quotationRecord;

import java.util.List;

import capi.entity.QuotationRecord;
import capi.entity.SubPriceType;
import capi.model.masterMaintenance.SubPriceFieldList;

public class QuotationRecordViewModel extends QuotationRecord {
	
	private Integer subPriceTypeId;
	private String subPriceTypeName;
	private List<SubPriceModel> subPrices;
	private boolean isUnitSubPriceType;
	private List<SubPriceFieldList> fields;
	private Double historyNPricePercent;
	private Double historySPricePercent;
	private boolean showUomInput;
	private String uomCategoryIdCSV;
	private String outletTypeShortCodeCSV;
	private boolean isNPriceMandatory;
	private boolean isSPriceMandatory;
	private String reminderForPricingCycleMessage;
	private SubPriceType subPriceTypeDataModel;
	private int numberOfDaysOfSurveyMonth;
	private boolean provideRemarkForNotAvailableQuotation;
	private boolean isFRRequired;
	private boolean useFRAdmin;
	private boolean isCollectFR;
	private String assignmentCategoryRemark;
	private String assignmentContactPerson;
	private String fieldOfficer;
	private Integer seasonality;
	private Boolean showResetChangeProductButton;

	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}

	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}

	public List<SubPriceModel> getSubPrices() {
		return subPrices;
	}

	public void setSubPrices(List<SubPriceModel> subPrices) {
		this.subPrices = subPrices;
	}

	public String getSubPriceTypeName() {
		return subPriceTypeName;
	}

	public void setSubPriceTypeName(String subPriceTypeName) {
		this.subPriceTypeName = subPriceTypeName;
	}

	public boolean isUnitSubPriceType() {
		return isUnitSubPriceType;
	}

	public void setUnitSubPriceType(boolean isUnitSubPriceType) {
		this.isUnitSubPriceType = isUnitSubPriceType;
	}

	public List<SubPriceFieldList> getFields() {
		return fields;
	}

	public void setFields(List<SubPriceFieldList> fields) {
		this.fields = fields;
	}

	public Double getHistoryNPricePercent() {
		return historyNPricePercent;
	}

	public void setHistoryNPricePercent(Double historyNPricePercent) {
		this.historyNPricePercent = historyNPricePercent;
	}

	public Double getHistorySPricePercent() {
		return historySPricePercent;
	}

	public void setHistorySPricePercent(Double historySPricePercent) {
		this.historySPricePercent = historySPricePercent;
	}

	public boolean isShowUomInput() {
		return showUomInput;
	}

	public void setShowUomInput(boolean showUomInput) {
		this.showUomInput = showUomInput;
	}

	public String getUomCategoryIdCSV() {
		return uomCategoryIdCSV;
	}

	public void setUomCategoryIdCSV(String uomCategoryIdCSV) {
		this.uomCategoryIdCSV = uomCategoryIdCSV;
	}

	public String getOutletTypeShortCodeCSV() {
		return outletTypeShortCodeCSV;
	}

	public void setOutletTypeShortCodeCSV(String outletTypeShortCodeCSV) {
		this.outletTypeShortCodeCSV = outletTypeShortCodeCSV;
	}

	public boolean isNPriceMandatory() {
		return isNPriceMandatory;
	}

	public void setNPriceMandatory(boolean isNPriceMandatory) {
		this.isNPriceMandatory = isNPriceMandatory;
	}

	public boolean isSPriceMandatory() {
		return isSPriceMandatory;
	}

	public void setSPriceMandatory(boolean isSPriceMandatory) {
		this.isSPriceMandatory = isSPriceMandatory;
	}

	
	public String getReminderForPricingCycleMessage() {
		return reminderForPricingCycleMessage;
	}
	

	public void setReminderForPricingCycleMessage(String reminderForPricingCycleMessage) {
		this.reminderForPricingCycleMessage = reminderForPricingCycleMessage;
	}

	
	public SubPriceType getSubPriceTypeDataModel() {
		return subPriceTypeDataModel;
	}

	public void setSubPriceTypeDataModel(SubPriceType subPriceTypeDataModel) {
		this.subPriceTypeDataModel = subPriceTypeDataModel;
	}


	public int getNumberOfDaysOfSurveyMonth() {
		return numberOfDaysOfSurveyMonth;
	}

	public void setNumberOfDaysOfSurveyMonth(int numberOfDaysOfSurveyMonth) {
		this.numberOfDaysOfSurveyMonth = numberOfDaysOfSurveyMonth;
	}

	
	public boolean isProvideRemarkForNotAvailableQuotation() {
		return provideRemarkForNotAvailableQuotation;
	}

	public void setProvideRemarkForNotAvailableQuotation(boolean provideRemarkForNotAvailableQuotation) {
		this.provideRemarkForNotAvailableQuotation = provideRemarkForNotAvailableQuotation;
	}
	
	public boolean isFRRequired() {
		return isFRRequired;
	}

	public void setFRRequired(boolean isFRRequired) {
		this.isFRRequired = isFRRequired;
	}

	public boolean isUseFRAdmin() {
		return useFRAdmin;
	}

	public void setUseFRAdmin(boolean useFRAdmin) {
		this.useFRAdmin = useFRAdmin;
	}

	public boolean isCollectFR() {
		return isCollectFR;
	}

	public void setCollectFR(boolean isCollectFR) {
		this.isCollectFR = isCollectFR;
	}

	public String getAssignmentCategoryRemark() {
		return assignmentCategoryRemark;
	}

	public void setAssignmentCategoryRemark(String assignmentCategoryRemark) {
		this.assignmentCategoryRemark = assignmentCategoryRemark;
	}

	public String getAssignmentContactPerson() {
		return assignmentContactPerson;
	}

	public void setAssignmentContactPerson(String assignmentContactPerson) {
		this.assignmentContactPerson = assignmentContactPerson;
	}

	public String getFieldOfficer() {
		return fieldOfficer;
	}

	public void setFieldOfficer(String fieldOfficer) {
		this.fieldOfficer = fieldOfficer;
	}

	public Integer getSeasonality() {
		return seasonality;
	}

	public void setSeasonality(Integer seasonality) {
		this.seasonality = seasonality;
	}

	public Boolean getShowResetChangeProductButton() {
		return showResetChangeProductButton;
	}

	public void setShowResetChangeProductButton(Boolean showResetChangeProductButton) {
		this.showResetChangeProductButton = showResetChangeProductButton;
	}
}
