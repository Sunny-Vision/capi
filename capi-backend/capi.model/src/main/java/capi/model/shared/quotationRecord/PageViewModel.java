package capi.model.shared.quotationRecord;

import java.util.List;

import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.model.assignmentManagement.assignmentManagement.BackTrackDateModel;

public class PageViewModel {
	private OutletViewModel outlet;
	private ProductPostModel product;
	private QuotationRecordViewModel quotationRecord;
	private QuotationRecordViewModel backNoQuotationRecord;
	private List<QuotationRecordHistoryDateModel> histories;
	private boolean readonly;
	private String pointToNote;
	private String verificationRemark;
	private String rejectReason;
	private String peCheckRemark;
	private boolean history;
	private boolean dirty;
	private boolean hideOutlet;
	private Integer quotationId;
	private Integer quotationRecordId;
	private String officer;
	private String validationError;
	private Boolean isHiddenFieldOfficer;
	private List<BackTrackDateModel> backTracks;

	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
	}

	public OutletViewModel getOutlet() {
		return outlet;
	}

	public void setOutlet(OutletViewModel outlet) {
		this.outlet = outlet;
	}

	public QuotationRecordViewModel getQuotationRecord() {
		return quotationRecord;
	}

	public void setQuotationRecord(QuotationRecordViewModel quotationRecord) {
		this.quotationRecord = quotationRecord;
	}

	public QuotationRecordViewModel getBackNoQuotationRecord() {
		return backNoQuotationRecord;
	}

	public void setBackNoQuotationRecord(QuotationRecordViewModel backNoQuotationRecord) {
		this.backNoQuotationRecord = backNoQuotationRecord;
	}

	public ProductPostModel getProduct() {
		return product;
	}

	public void setProduct(ProductPostModel product) {
		this.product = product;
	}

	public List<QuotationRecordHistoryDateModel> getHistories() {
		return histories;
	}

	public void setHistories(List<QuotationRecordHistoryDateModel> histories) {
		this.histories = histories;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getPointToNote() {
		return pointToNote;
	}

	public void setPointToNote(String pointToNote) {
		this.pointToNote = pointToNote;
	}

	public String getVerificationRemark() {
		return verificationRemark;
	}

	public void setVerificationRemark(String verificationRemark) {
		this.verificationRemark = verificationRemark;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getPeCheckRemark() {
		return peCheckRemark;
	}

	public void setPeCheckRemark(String peCheckRemark) {
		this.peCheckRemark = peCheckRemark;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}


	public boolean isHideOutlet() {
		return hideOutlet;
	}

	public void setHideOutlet(boolean hideOutlet) {
		this.hideOutlet = hideOutlet;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public String getOfficer() {
		return officer;
	}

	public void setOfficer(String officer) {
		this.officer = officer;
	}

	public String getValidationError() {
		return validationError;
	}

	public void setValidationError(String validationError) {
		this.validationError = validationError;
	}

	public Boolean getIsHiddenFieldOfficer() {
		return isHiddenFieldOfficer;
	}

	public void setIsHiddenFieldOfficer(Boolean isHiddenFieldOfficer) {
		this.isHiddenFieldOfficer = isHiddenFieldOfficer;
	}

	public List<BackTrackDateModel> getBackTracks() {
		return backTracks;
	}

	public void setBackTracks(List<BackTrackDateModel> backTracks) {
		this.backTracks = backTracks;
	}
	
}
