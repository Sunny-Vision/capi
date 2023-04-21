package capi.model.batch;

import java.util.Date;

import capi.entity.Assignment;
import capi.entity.Outlet;
import capi.entity.User;

public class AssignmentDeadlineModel {

	private User user;
	
	private Assignment assignment;
	
	private Date collectionDate;
	
	private Date startDate;
	
	private Date endDate;
	
	private Outlet outlet;
	
	private Integer quotationCnt;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
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

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public Integer getQuotationCnt() {
		return quotationCnt;
	}

	public void setQuotationCnt(Integer quotationCnt) {
		this.quotationCnt = quotationCnt;
	}
	
}
