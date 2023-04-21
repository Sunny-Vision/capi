package capi.model.masterMaintenance;

import java.util.Date;
import java.util.List;


public class BatchTableList {

	public static class AssignmentType {

		private Integer id;

		private String name;

		public AssignmentType() {}

		public AssignmentType(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	private Integer batchId;
	
	private String code;
	
	/**
	 * - 1) Specified Collection Date & Officer
	 * - 2) Start/End Date & Specified Officer
	 * - 3) Start/End Date & Unspecified Officer
	 */
	private Integer assignmentType;
	
	private List<AssignmentType> assignmentTypeList;
	
	private String assignmentTypeLabel;
	
	private String surveyForm;
	
	private String batchCategory;
	
	private String description;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(Integer assignmentType) {
		this.assignmentType = assignmentType;
	}

	public List<AssignmentType> getAssignmentTypeList() {
		return assignmentTypeList;
	}

	public void setAssignmentTypeList(List<AssignmentType> assignmentTypeList) {
		this.assignmentTypeList = assignmentTypeList;
	}

	public String getAssignmentTypeLabel() {
		return assignmentTypeLabel;
	}

	public void setAssignmentTypeLabel(String assignmentTypeLabel) {
		this.assignmentTypeLabel = assignmentTypeLabel;
	}

	public String getSurveyForm() {
		return surveyForm;
	}

	public void setSurveyForm(String surveyForm) {
		this.surveyForm = surveyForm;
	}

	public String getBatchCategory() {
		return batchCategory;
	}

	public void setBatchCategory(String batchCategory) {
		this.batchCategory = batchCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
