package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="SupervisoryVisitDetail")
public class SupervisoryVisitDetail  extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SupervisoryVisitDetailId")
	private Integer supervisoryVisitDetailId;
		
	@Deprecated
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;
	
	@Column(name="Survey")
	private String survey;
	
	@Column(name="Result")
	private Integer result;
	
	@Column(name="OtherRemark")
	private String otherRemark;
		
	@Column(name="ReferenceNo")
	private String referenceNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SupervisoryVisitFormId", nullable = true)
	private SupervisoryVisitForm supervisoryVisitForm;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSupervisoryVisitDetailId();
	}

	public Integer getSupervisoryVisitDetailId() {
		return supervisoryVisitDetailId;
	}

	public void setSupervisoryVisitDetailId(Integer supervisoryVisitDetailId) {
		this.supervisoryVisitDetailId = supervisoryVisitDetailId;
	}

	@Deprecated
	public Assignment getAssignment() {
		return assignment;
	}

	@Deprecated
	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public SupervisoryVisitForm getSupervisoryVisitForm() {
		return supervisoryVisitForm;
	}

	public void setSupervisoryVisitForm(SupervisoryVisitForm supervisoryVisitForm) {
		this.supervisoryVisitForm = supervisoryVisitForm;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

}
