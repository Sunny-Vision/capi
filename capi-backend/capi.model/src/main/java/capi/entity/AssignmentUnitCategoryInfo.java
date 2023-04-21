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
@Table(name="AssignmentUnitCategoryInfo")
public class AssignmentUnitCategoryInfo extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AssignmentUnitCategoryInfoId")
	private Integer assignmentUnitCategoryInfoId;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;
	
	@Column(name="ContactPerson")
	private String contactPerson;
	
	@Column(name="Remark")
	private String remark;
	
	@Column(name="UnitCategory")
	private String unitCategory;

	@Column(name="Sequence")
	private Integer sequence;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getAssignmentUnitCategoryInfoId();
	}
	
	public Integer getAssignmentUnitCategoryInfoId() {
		return assignmentUnitCategoryInfoId;
	}

	public void setAssignmentUnitCategoryInfoId(Integer assignmentUnitCategoryInfoId) {
		this.assignmentUnitCategoryInfoId = assignmentUnitCategoryInfoId;
	}

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUnitCategory() {
		return unitCategory;
	}

	public void setUnitCategory(String unitCategory) {
		this.unitCategory = unitCategory;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}
}
