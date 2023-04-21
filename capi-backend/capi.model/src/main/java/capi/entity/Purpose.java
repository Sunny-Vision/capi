package capi.entity;

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
@Table(name="Purpose")
public class Purpose  extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PurposeId")
	private Integer purposeId;
	
	@Column(name="Code")
	private String code;
	
	@Column(name="Name")
	private String name;
	
	/**
	 * -CPI
	 * -GHS
	 * -BMWPS
	 * -Others
	 */
	@Column(name="Survey")
	private String survey;
	
	@Column(name="Note")
	private String note;
	
	@Column(name="PEIncluded")
	private boolean peIncluded;
	
	@Column(name="EnumerationOutcomes")
	private String enumerationOutcomes;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "purpose")
	private Set<Unit> units = new HashSet<Unit>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPurposeId();
	}

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isPeIncluded() {
		return peIncluded;
	}

	public void setPeIncluded(boolean peIncluded) {
		this.peIncluded = peIncluded;
	}

	public String getEnumerationOutcomes() {
		return enumerationOutcomes;
	}

	public void setEnumerationOutcomes(String enumerationOutcomes) {
		this.enumerationOutcomes = enumerationOutcomes;
	}

	public Set<Unit> getUnits() {
		return this.units;
	}

	public void setUnits(Set<Unit> units) {
		this.units = units;
	}

}
