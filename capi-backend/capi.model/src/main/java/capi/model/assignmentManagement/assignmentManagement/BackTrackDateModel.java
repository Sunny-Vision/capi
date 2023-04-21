package capi.model.assignmentManagement.assignmentManagement;

public class BackTrackDateModel {
	private Integer quotationRecordId;
	private String date;
	private boolean passValidation;
	
	public BackTrackDateModel(){}
	
	public BackTrackDateModel(Integer quotationRecordId, String date, boolean passValidation) {
		super();
		this.quotationRecordId = quotationRecordId;
		this.date = date;
		this.passValidation = passValidation;
	}
	
	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}
	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isPassValidation() {
		return passValidation;
	}
	public void setPassValidation(boolean passValidation) {
		this.passValidation = passValidation;
	}
}
