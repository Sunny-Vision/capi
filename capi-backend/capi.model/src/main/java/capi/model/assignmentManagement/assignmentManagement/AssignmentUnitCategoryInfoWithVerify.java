package capi.model.assignmentManagement.assignmentManagement;

import capi.entity.AssignmentUnitCategoryInfo;

public class AssignmentUnitCategoryInfoWithVerify extends AssignmentUnitCategoryInfo {
	private int verifyFirm;
	private int verifyCategory;
	private int verifyQuotation;
	
	public int getVerifyFirm() {
		return verifyFirm;
	}
	public void setVerifyFirm(int verifyFirm) {
		this.verifyFirm = verifyFirm;
	}
	public int getVerifyCategory() {
		return verifyCategory;
	}
	public void setVerifyCategory(int verifyCategory) {
		this.verifyCategory = verifyCategory;
	}
	public int getVerifyQuotation() {
		return verifyQuotation;
	}
	public void setVerifyQuotation(int verifyQuotation) {
		this.verifyQuotation = verifyQuotation;
	}
}
