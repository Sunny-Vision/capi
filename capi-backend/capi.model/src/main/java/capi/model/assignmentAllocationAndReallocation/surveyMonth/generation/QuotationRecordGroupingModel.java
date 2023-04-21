package capi.model.assignmentAllocationAndReallocation.surveyMonth.generation;

import java.util.Date;

import capi.entity.Outlet;
import capi.entity.User;

public class QuotationRecordGroupingModel {

	private Outlet outlet;
	private Date collectionDate;
	private Date assignedStartDate;
	private Date assignedEndDate;
	private User user;
	
	public Outlet getOutlet() {
		return outlet;
	}
	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}
	public Date getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}
	public Date getAssignedStartDate() {
		return assignedStartDate;
	}
	public void setAssignedStartDate(Date assignedStartDate) {
		this.assignedStartDate = assignedStartDate;
	}
	public Date getAssignedEndDate() {
		return assignedEndDate;
	}
	public void setAssignedEndDate(Date assignedEndDate) {
		this.assignedEndDate = assignedEndDate;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assignedEndDate == null) ? 0 : assignedEndDate.hashCode());
		result = prime
				* result
				+ ((assignedStartDate == null) ? 0 : assignedStartDate
						.hashCode());
		result = prime * result
				+ ((collectionDate == null) ? 0 : collectionDate.hashCode());
		result = prime * result + ((outlet == null) ? 0 : outlet.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuotationRecordGroupingModel other = (QuotationRecordGroupingModel) obj;
		if (assignedEndDate == null) {
			if (other.assignedEndDate != null)
				return false;
		} else if (!assignedEndDate.equals(other.assignedEndDate))
			return false;
		if (assignedStartDate == null) {
			if (other.assignedStartDate != null)
				return false;
		} else if (!assignedStartDate.equals(other.assignedStartDate))
			return false;
		if (collectionDate == null) {
			if (other.collectionDate != null)
				return false;
		} else if (!collectionDate.equals(other.collectionDate))
			return false;
		if (outlet == null) {
			if (other.outlet != null)
				return false;
		} else if (!outlet.equals(other.outlet))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	
}
