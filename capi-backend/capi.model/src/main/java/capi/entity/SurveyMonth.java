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
@Table(name="SurveyMonth")
public class SurveyMonth extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SurveyMonthId")
	private Integer surveyMonthId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="EndDate")
	private Date endDate;
	
	@Column(name="FlagTransferred")
	private boolean flagTransferred;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ClosingDateId", nullable = true)
	private ClosingDate closingDate;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "surveyMonth")
	private Set<SpotCheckDate> spotCheckDates = new HashSet<SpotCheckDate>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "surveyMonth")
	private Set<AllocationBatch> allocationBatches = new HashSet<AllocationBatch>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "surveyMonth")
	private Set<AssignmentAttribute> assignmentAttributes = new HashSet<AssignmentAttribute>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "surveyMonth")
	private Set<PECheckTask> peCheckTasks = new HashSet<PECheckTask>();
	
	/**
	 * 1- pending
	 * 2- in progress
	 * 3- finished
	 * 4- failed
	 * 5- approved
	 * 6- created
	 */
	@Column(name="Status")
	private Integer status;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSurveyMonthId();
	}

	
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}


	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}


	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Set<SpotCheckDate> getSpotCheckDates() {
		return spotCheckDates;
	}

	public void setSpotCheckDates(Set<SpotCheckDate> spotCheckDates) {
		this.spotCheckDates = spotCheckDates;
	}

	public Set<AllocationBatch> getAllocationBatches() {
		return allocationBatches;
	}

	public void setAllocationBatches(Set<AllocationBatch> allocationBatches) {
		this.allocationBatches = allocationBatches;
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


	public ClosingDate getClosingDate() {
		return closingDate;
	}


	public void setClosingDate(ClosingDate closingDate) {
		this.closingDate = closingDate;
	}


	public Set<PECheckTask> getPeCheckTasks() {
		return peCheckTasks;
	}


	public void setPeCheckTasks(Set<PECheckTask> peCheckTasks) {
		this.peCheckTasks = peCheckTasks;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public Set<AssignmentAttribute> getAssignmentAttributes() {
		return assignmentAttributes;
	}


	public void setAssignmentAttributes(
			Set<AssignmentAttribute> assignmentAttributes) {
		this.assignmentAttributes = assignmentAttributes;
	}


	public boolean isFlagTransferred() {
		return flagTransferred;
	}


	public void setFlagTransferred(boolean flagTransferred) {
		this.flagTransferred = flagTransferred;
	}

	
	
}
