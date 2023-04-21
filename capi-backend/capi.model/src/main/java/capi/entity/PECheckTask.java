package capi.entity;

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
@Table(name="PECheckTask")
public class PECheckTask extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PECheckTaskId")
	private Integer peCheckTaskId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="AssignmentId", nullable=true)
	private Assignment assignment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SurveyMonthId", nullable = true)
	private SurveyMonth surveyMonth;
	
	@Column(name="IsSelected")
	private boolean isSelected;
	
	@Column(name="IsFieldTeamHead")
	private boolean isFieldTeamHead;
	
	@Column(name="IsSectionHead")
	private boolean isSectionHead;
	
	@Column(name="IsCertaintyCase")
	private boolean isCertaintyCase;
	
	@Column(name="IsRandomCase")
	private boolean isRandomCase;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPeCheckTaskId();
	}

	public Integer getPeCheckTaskId() {
		return peCheckTaskId;
	}

	public void setPeCheckTaskId(Integer peCheckTaskId) {
		this.peCheckTaskId = peCheckTaskId;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isFieldTeamHead() {
		return isFieldTeamHead;
	}

	public void setFieldTeamHead(boolean isFieldTeamHead) {
		this.isFieldTeamHead = isFieldTeamHead;
	}

	public boolean isSectionHead() {
		return isSectionHead;
	}

	public void setSectionHead(boolean isSectionHead) {
		this.isSectionHead = isSectionHead;
	}

	public boolean isCertaintyCase() {
		return isCertaintyCase;
	}

	public void setCertaintyCase(boolean isCertaintyCase) {
		this.isCertaintyCase = isCertaintyCase;
	}

	public boolean isRandomCase() {
		return isRandomCase;
	}

	public void setRandomCase(boolean isRandomCase) {
		this.isRandomCase = isRandomCase;
	}

}
