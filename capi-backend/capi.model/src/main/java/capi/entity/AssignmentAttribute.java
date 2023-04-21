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
@Table(name="AssignmentAttribute")
public class AssignmentAttribute extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AssignmentAttributeId")
	private Integer assignmentAttributeId;
	
	@Column(name="BatchCategory")
	private String batchCategory;
	
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="EndDate")
	private Date endDate;
	
	@Column(name="Session")
	private String session;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BatchId", nullable = true)
	private Batch batch;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AllocationBatchId", nullable = true)
	private AllocationBatch allocationBatch;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SurveyMonthId", nullable = true)
	private SurveyMonth surveyMonth;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "assignmentAttribute")
	private Set<BatchCollectionDate> batchCollectionDates = new HashSet<BatchCollectionDate>();
	

	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getAssignmentAttributeId();
	}

	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}

	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}

	public String getBatchCategory() {
		return batchCategory;
	}

	public void setBatchCategory(String batchCategory) {
		this.batchCategory = batchCategory;
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public AllocationBatch getAllocationBatch() {
		return allocationBatch;
	}

	public void setAllocationBatch(AllocationBatch allocationBatch) {
		this.allocationBatch = allocationBatch;
	}

	public Set<BatchCollectionDate> getBatchCollectionDates() {
		return batchCollectionDates;
	}

	public void setBatchCollectionDates(
			Set<BatchCollectionDate> batchCollectionDates) {
		this.batchCollectionDates = batchCollectionDates;
	}

	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

}
