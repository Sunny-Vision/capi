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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="AssignmentAdjustment")
public class AssignmentAdjustment extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AssignmentAdjustmentId")
	private Integer assignmentAdjustmentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FromUserId", nullable = true)
	private User fromUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ToUserId", nullable = true)
	private User toUser;
	
	@Column(name="ManDay")
	private Double manDay;
	
	@Column(name="ActualManDay")
	private Double actualManDay;
	
	@Column(name="Remark")
	private String remark;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AllocationBatchId", nullable = true)
	private AllocationBatch allocationBatch;	
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitToRecommend", nullable = true)
	private User submitToRecommend;	
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitToApprove", nullable = true)
	private User submitToApprove;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "ReleasedAssignment", 
			joinColumns = { @JoinColumn(name = "AssignmentAdjustmentId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "AssignmentId", nullable = false, updatable = false) })
	private Set<Assignment> assignments = new HashSet<Assignment>();

	/**
	 * Submitted
	 * Pending
	 * Approved
	 * Recommended
	 * Rejected
	 */
	@Column(name="Status")	
	private String status;
	
	@Column(name="RecommendDate")	
	private Date recommendDate;
	
	@Column(name="ApprovedDate")	
	private Date approvedDate;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getAssignmentAdjustmentId();
	}


	public Integer getAssignmentAdjustmentId() {
		return assignmentAdjustmentId;
	}


	public void setAssignmentAdjustmentId(Integer assignmentAdjustmentId) {
		this.assignmentAdjustmentId = assignmentAdjustmentId;
	}


	public Double getManDay() {
		return manDay;
	}


	public void setManDay(Double manDay) {
		this.manDay = manDay;
	}

	public User getFromUser() {
		return fromUser;
	}


	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}


	public User getToUser() {
		return toUser;
	}


	public void setToUser(User toUser) {
		this.toUser = toUser;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public AllocationBatch getAllocationBatch() {
		return allocationBatch;
	}


	public void setAllocationBatch(AllocationBatch allocationBatch) {
		this.allocationBatch = allocationBatch;
	}


	public Date getReferenceMonth() {
		return referenceMonth;
	}


	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}


	public User getSubmitToRecommend() {
		return submitToRecommend;
	}


	public void setSubmitToRecommend(User submitToRecommend) {
		this.submitToRecommend = submitToRecommend;
	}


	public String getRejectReason() {
		return rejectReason;
	}


	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}


	public Set<Assignment> getAssignments() {
		return assignments;
	}


	public void setAssignments(Set<Assignment> assignments) {
		this.assignments = assignments;
	}


	public User getSubmitToApprove() {
		return submitToApprove;
	}


	public void setSubmitToApprove(User submitToApprove) {
		this.submitToApprove = submitToApprove;
	}


	public Double getActualManDay() {
		return actualManDay;
	}


	public void setActualManDay(Double actualManDay) {
		this.actualManDay = actualManDay;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Date getRecommendDate() {
		return recommendDate;
	}


	public void setRecommendDate(Date recommendDate) {
		this.recommendDate = recommendDate;
	}


	public Date getApprovedDate() {
		return approvedDate;
	}


	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

}
