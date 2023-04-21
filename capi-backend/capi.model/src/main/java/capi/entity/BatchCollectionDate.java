package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="BatchCollectionDate")
public class BatchCollectionDate extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="BatchCollectionDateId")
	private Integer batchCollectionDateId;
	
	@Column(name="[Date]")
	private Date date;

	@Column(name="HasBackTrack")
	private boolean hasBackTrack;
	
	@Column(name="BackTrackDate1")
	private Date backTrackDate1;
	
	@Column(name="BackTrackDate2")
	private Date backTrackDate2;
	
	@Column(name="BackTrackDate3")
	private Date backTrackDate3;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentAttributeId", nullable = true)
	private AssignmentAttribute assignmentAttribute;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getBatchCollectionDateId();
	}

	public Integer getBatchCollectionDateId() {
		return batchCollectionDateId;
	}

	public void setBatchCollectionDateId(Integer batchCollectionDateId) {
		this.batchCollectionDateId = batchCollectionDateId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public AssignmentAttribute getAssignmentAttribute() {
		return assignmentAttribute;
	}

	public void setAssignmentAttribute(AssignmentAttribute assignmentAttribute) {
		this.assignmentAttribute = assignmentAttribute;
	}

	public boolean isHasBackTrack() {
		return hasBackTrack;
	}

	public void setHasBackTrack(boolean hasBackTrack) {
		this.hasBackTrack = hasBackTrack;
	}

	public Date getBackTrackDate1() {
		return backTrackDate1;
	}

	public void setBackTrackDate1(Date backTrackDate1) {
		this.backTrackDate1 = backTrackDate1;
	}

	public Date getBackTrackDate2() {
		return backTrackDate2;
	}

	public void setBackTrackDate2(Date backTrackDate2) {
		this.backTrackDate2 = backTrackDate2;
	}

	public Date getBackTrackDate3() {
		return backTrackDate3;
	}

	public void setBackTrackDate3(Date backTrackDate3) {
		this.backTrackDate3 = backTrackDate3;
	}

}
