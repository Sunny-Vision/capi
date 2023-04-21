package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ClosingDate")
public class ClosingDate extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ClosingDateId")
	private Integer closingDateId;
	
	@Column(name="ClosingDate")
	private Date closingDate;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Column(name="PublishDate")
	private Date publishDate;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "closingDate")
	private Set<SurveyMonth> surveyMonths = new HashSet<SurveyMonth>();
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getClosingDateId();
	}


	public Integer getClosingDateId() {
		return closingDateId;
	}


	public void setClosingDateId(Integer closingDateId) {
		this.closingDateId = closingDateId;
	}


	public Date getClosingDate() {
		return closingDate;
	}


	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}


	public Date getReferenceMonth() {
		return referenceMonth;
	}


	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}


	public Date getPublishDate() {
		return publishDate;
	}


	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}


	public Set<SurveyMonth> getSurveyMonths() {
		return surveyMonths;
	}


	public void setSurveyMonths(Set<SurveyMonth> surveyMonths) {
		this.surveyMonths = surveyMonths;
	}

}
