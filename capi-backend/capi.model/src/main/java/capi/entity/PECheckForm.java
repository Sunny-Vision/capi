package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="PECheckForm")
public class PECheckForm extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PECheckFormId")
	private Integer peCheckFormId;
	
	@Column(name="ContactPerson")
	private String contactPerson;
	
	@Column(name="CheckingDate")
	private Date checkingDate;
	
	@Column(name="CheckingTime")
	private Date checkingTime;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OfficerId", nullable = true)
	private User officer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	/**
	 * 1- Field
	 * 2- Telephone
	 */
	@Column(name="CheckingMode")
	private Integer checkingMode;
	
	@Column(name="PECheckRemark")
	private String peCheckRemark;
	
	@Column(name="OtherRemark")
	private String otherRemark;
	
	/**
	 * Draft
	 * Submitted
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="IsNonContact")
	private boolean isNonContact;	
	
	/**
	 * 1- All Matched
	 * 2- Not Matched
	 * 3- N.A.
	 */
	@Column(name="ContactDateResult")
	private Integer contactDateResult;

	/**
	 * 1- All Matched
	 * 2- Not Matched
	 * 3- N.A.
	 */
	@Column(name="ContactTimeResult")
	private Integer contactTimeResult;
	
	/**
	 * 1- All Matched
	 * 2- Not Matched
	 * 3- N.A.
	 */
	@Column(name="ContactDurationResult")
	private Integer contactDurationResult;
	
	/**
	 * 1- All Matched
	 * 2- Not Matched
	 * 3- N.A.
	 */
	@Column(name="ContactModeResult")
	private Integer contactModeResult;
	
	/**
	 * 1- All Matched
	 * 2- Not Matched
	 * 3- N.A.
	 */
	@Column(name="DateCollectedResult")
	private Integer dateCollectedResult;
	
	/**
	 * 1- All Matched
	 * 2- Not Matched
	 * 3- N.A.
	 */
	@Column(name="OthersResult")
	private Integer othersResult;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPeCheckFormId();
	}

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Date getCheckingDate() {
		return checkingDate;
	}

	public void setCheckingDate(Date checkingDate) {
		this.checkingDate = checkingDate;
	}

	public Date getCheckingTime() {
		return checkingTime;
	}

	public void setCheckingTime(Date checkingTime) {
		this.checkingTime = checkingTime;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public User getOfficer() {
		return officer;
	}

	public void setOfficer(User officer) {
		this.officer = officer;
	}

	public Integer getCheckingMode() {
		return checkingMode;
	}

	public void setCheckingMode(Integer checkingMode) {
		this.checkingMode = checkingMode;
	}

	public String getPeCheckRemark() {
		return peCheckRemark;
	}

	public void setPeCheckRemark(String peCheckRemark) {
		this.peCheckRemark = peCheckRemark;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isNonContact() {
		return isNonContact;
	}

	public void setNonContact(boolean isNonContact) {
		this.isNonContact = isNonContact;
	}

	public Integer getContactDateResult() {
		return contactDateResult;
	}

	public void setContactDateResult(Integer contactDateResult) {
		this.contactDateResult = contactDateResult;
	}

	public Integer getContactTimeResult() {
		return contactTimeResult;
	}

	public void setContactTimeResult(Integer contactTimeResult) {
		this.contactTimeResult = contactTimeResult;
	}

	public Integer getContactDurationResult() {
		return contactDurationResult;
	}

	public void setContactDurationResult(Integer contactDurationResult) {
		this.contactDurationResult = contactDurationResult;
	}

	public Integer getContactModeResult() {
		return contactModeResult;
	}

	public void setContactModeResult(Integer contactModeResult) {
		this.contactModeResult = contactModeResult;
	}

	public Integer getDateCollectedResult() {
		return dateCollectedResult;
	}

	public void setDateCollectedResult(Integer dateCollectedResult) {
		this.dateCollectedResult = dateCollectedResult;
	}

	public Integer getOthersResult() {
		return othersResult;
	}

	public void setOthersResult(Integer othersResult) {
		this.othersResult = othersResult;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
