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
@Table(name="SpotCheckDate")
public class SpotCheckDate extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SpotCheckDateId")
	private Integer spotCheckDateId;
	
	@Column(name="[Date]")
	private Date date;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SurveyMonthId", nullable = true)
	private SurveyMonth surveyMonth;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSpotCheckDateId();
	}

	public Integer getSpotCheckDateId() {
		return spotCheckDateId;
	}

	public void setSpotCheckDateId(Integer spotCheckDateId) {
		this.spotCheckDateId = spotCheckDateId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

}
