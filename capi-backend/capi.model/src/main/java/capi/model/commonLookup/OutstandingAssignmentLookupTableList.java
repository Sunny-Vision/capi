package capi.model.commonLookup;

import java.util.List;

public class OutstandingAssignmentLookupTableList {

	public static class BatchCodeMapping {

		private Integer assignmentId;

		private String batchCode;

		public Integer getAssignmentId() {
			return assignmentId;
		}

		public void setAssignmentId(Integer assignmentId) {
			this.assignmentId = assignmentId;
		}

		public String getBatchCode() {
			return batchCode;
		}

		public void setBatchCode(String batchCode) {
			this.batchCode = batchCode;
		}

	}
	
	private Integer id;

	private String collectionDate;

	private String seDate;

	private String firm;

	private String district;

	private String tpu;

	private String batchCode;

	private Long noOfQuotation;

	private String pic;

	private List<Integer> assignmentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getSeDate() {
		return seDate;
	}

	public void setSeDate(String seDate) {
		this.seDate = seDate;
	}

	public String getFirm() {
		return firm;
	}

	public void setFirm(String firm) {
		this.firm = firm;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTpu() {
		return tpu;
	}

	public void setTpu(String tpu) {
		this.tpu = tpu;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Long getNoOfQuotation() {
		return noOfQuotation;
	}

	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public List<Integer> getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(List<Integer> assignmentId) {
		this.assignmentId = assignmentId;
	}

}
