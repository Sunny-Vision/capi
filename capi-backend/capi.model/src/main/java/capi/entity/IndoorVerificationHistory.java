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
@Table(name="IndoorVerificationHistory")
public class IndoorVerificationHistory  extends EntityBase {
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="IndoorVerificationHistoryId")
	private Integer indoorVerificationHistoryId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
		
	/**
	 * 1- Firm Verify
	 * 2- Category Verify
	 * 3- Quotation Verify
	 */
	@Column(name="VerifyType")
	private Integer verifyType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "VerficiationQuotationRecordHistory", 
			joinColumns = { @JoinColumn(name = "IndoorVerificationHistoryId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "QuotationRecordId", nullable = false, updatable = false) })
	private Set<QuotationRecord> quotationRecords = new HashSet<QuotationRecord>();
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getIndoorVerificationHistoryId();
	}
	
	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Integer getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(Integer verifyType) {
		this.verifyType = verifyType;
	}

	public Integer getIndoorVerificationHistoryId() {
		return indoorVerificationHistoryId;
	}

	public void setIndoorVerificationHistoryId(Integer indoorVerificationHistoryId) {
		this.indoorVerificationHistoryId = indoorVerificationHistoryId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<QuotationRecord> getQuotationRecords() {
		return quotationRecords;
	}

	public void setQuotationRecords(Set<QuotationRecord> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}

}
