package capi.model.assignmentManagement.assignmentManagement;

public class VerificationTypeGroupModel {
	private boolean verifyFirm;
	private boolean verifyCategory;
	private boolean verifyQuotation;
	
	public boolean isVerifyFirm() {
		return verifyFirm;
	}
	public void setVerifyFirm(boolean verifyFirm) {
		this.verifyFirm = verifyFirm;
	}
	public boolean isVerifyCategory() {
		return verifyCategory;
	}
	public void setVerifyCategory(boolean verifyCategory) {
		this.verifyCategory = verifyCategory;
	}
	public boolean isVerifyQuotation() {
		return verifyQuotation;
	}
	public void setVerifyQuotation(boolean verifyQuotation) {
		this.verifyQuotation = verifyQuotation;
	}
}
