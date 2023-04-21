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
@Table(name="AssignmentReallocation")
public class AssignmentReallocation extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AssignmentReallocationId")
	private Integer assignmentReallocationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OriginalUserId", nullable = true)
	private User originalUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TargetUserId", nullable = true)
	private User targetUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitToUserId", nullable = true)
	private User submitToUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitToApprove", nullable = true)
	private User submitToApprove;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CreatorId", nullable = true)
	private User creator;
	
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@Column(name="Status")
	private String status;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "ReallocatedAssignment", 
			joinColumns = { @JoinColumn(name = "AssignmentReallcationId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "AssignmentId", nullable = false, updatable = false) })
	private Set<Assignment> assignments = new HashSet<Assignment>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "ReallocatedQuotationRecord", 
			joinColumns = { @JoinColumn(name = "AssignmentReallcationId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "QuotationRecordId", nullable = false, updatable = false) })
	private Set<QuotationRecord> quotationRecords = new HashSet<QuotationRecord>();
	
	@Column(name="RecommendDate")	
	private Date recommendDate;
	
	@Column(name="ApprovedDate")	
	private Date approvedDate;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getAssignmentReallocationId();
	}


	public User getOriginalUser() {
		return originalUser;
	}

	public void setOriginalUser(User originalUser) {
		this.originalUser = originalUser;
	}

	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
	}

	public User getSubmitToUser() {
		return submitToUser;
	}

	public void setSubmitToUser(User submitToUser) {
		this.submitToUser = submitToUser;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public Integer getAssignmentReallocationId() {
		return assignmentReallocationId;
	}


	public void setAssignmentReallocationId(Integer assignmentReallocationId) {
		this.assignmentReallocationId = assignmentReallocationId;
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


	public User getCreator() {
		return creator;
	}


	public void setCreator(User creator) {
		this.creator = creator;
	}


	public Set<QuotationRecord> getQuotationRecords() {
		return quotationRecords;
	}


	public void setQuotationRecords(Set<QuotationRecord> quotationRecords) {
		this.quotationRecords = quotationRecords;
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
