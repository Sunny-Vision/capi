package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VwSurveyFormBinded")
public class VwSurveyFormBinded implements ViewBase{
	
	@Id
	@Column(name="SurveyForm")
	private String surveyForm;
	
	@Column(name="[Count]")
	private Integer count;
	

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return getSurveyForm();
	}


	public String getSurveyForm() {
		return surveyForm;
	}


	public void setSurveyForm(String surveyForm) {
		this.surveyForm = surveyForm;
	}


	public Integer getCount() {
		return count;
	}


	public void setCount(Integer count) {
		this.count = count;
	}

}
