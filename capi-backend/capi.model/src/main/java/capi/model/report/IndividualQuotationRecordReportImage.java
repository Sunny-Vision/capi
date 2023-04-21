package capi.model.report;

import java.util.ArrayList;

public class IndividualQuotationRecordReportImage {
	//private Integer quotationRecordId;
	
	private Integer assignmentId;
	
	private String photo1Path;
	
	private String photo2Path;
	
	private String outletPath;
	
	private String outletAttachmentPath;
	
	private ArrayList<String> imagePath; 

	/*public Integer getQuotationRecordId() {
		return quotationRecordId;
	}
	*/
	public Integer getAssignmentId() {
		return assignmentId;
	}
	
	public String getPhoto1Path() {
		return photo1Path;
	}
	
	public String getPhoto2Path() {
		return photo2Path;
	}
	
	public String getOutletPath() {
		return outletPath;
	}
	
	public ArrayList<String> getImagePath() {
		return imagePath;
	}

	public void setImagePath(ArrayList<String> imagePath) {
		this.imagePath = imagePath;
	}
	
	/*public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}
	*/
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	
	public void setPhoto1Path(String photo1Path) {
		this.photo1Path = photo1Path;
	}
	
	public void setPhoto2Path(String photo2Path) {
		this.photo2Path = photo2Path;
	}
	
	public void setOutletPath(String outletPath) {
		this.outletPath = outletPath;
	}

	public String getOutletAttachmentPath() {
		return outletAttachmentPath;
	}

	public void setOutletAttachmentPath(String outletAttachmentPath) {
		this.outletAttachmentPath = outletAttachmentPath;
	}

}
