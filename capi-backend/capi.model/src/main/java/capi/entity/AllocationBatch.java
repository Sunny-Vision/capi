package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="AllocationBatch")
public class AllocationBatch extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AllocationBatchId")
	private Integer allocationBatchId;
	
	@Column(name="BatchName")
	private String batchName;
	
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="EndDate")
	private Date endDate;
	
	/**
	 * 1- Allocated
	 * 2- Approved
	 */
	@Column(name="Status")
	private Integer status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SurveyMonthId", nullable = true)
	private SurveyMonth surveyMonth;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "allocationBatch")
	private Set<AssignmentAttribute> assignmentAttributes = new HashSet<AssignmentAttribute>();
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "allocationBatch")
	private Set<DistrictHeadAdjustment> districtHeadAdjustments = new HashSet<DistrictHeadAdjustment>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "allocationBatch")
	private Set<AssignmentAdjustment> assignmentAdjustments = new HashSet<AssignmentAdjustment>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getAllocationBatchId();
	}

	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}

	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Set<AssignmentAttribute> getAssignmentAttributes() {
		return assignmentAttributes;
	}

	public void setAssignmentAttributes(Set<AssignmentAttribute> assignmentAttributes) {
		this.assignmentAttributes = assignmentAttributes;
	}

	public Set<DistrictHeadAdjustment> getDistrictHeadAdjustments() {
		return districtHeadAdjustments;
	}

	public void setDistrictHeadAdjustments(
			Set<DistrictHeadAdjustment> districtHeadAdjustments) {
		this.districtHeadAdjustments = districtHeadAdjustments;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Set<AssignmentAdjustment> getAssignmentAdjustments() {
		return assignmentAdjustments;
	}

	public void setAssignmentAdjustments(Set<AssignmentAdjustment> assignmentAdjustments) {
		this.assignmentAdjustments = assignmentAdjustments;
	}

}
