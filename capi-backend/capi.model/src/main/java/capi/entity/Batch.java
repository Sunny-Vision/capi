package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Batch")
public class Batch extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="BatchId")
	private Integer batchId;
	
	@Column(name="Code")
	private String code;
	
	/**
	 * - 1) Specified Collection Date & Officer
	 * - 2) Start/End Date & Specified Officer
	 * - 3) Start/End Date & Unspecified Officer
	 */
	@Column(name="AssignmentType")
	private Integer assignmentType;
	
	@Column(name="SurveyForm")
	private String surveyForm;
	
	@Column(name="BatchCategory")
	private String batchCategory;
	
	@Column(name="Description")
	private String description;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "batches")
	private Set<User> users = new HashSet<User>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "batch")
	private Set<Quotation> quotations = new HashSet<Quotation>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getBatchId();
	}

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

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Quotation> getQuotations() {
		return quotations;
	}

	public void setQuotations(Set<Quotation> quotations) {
		this.quotations = quotations;
	}

}
