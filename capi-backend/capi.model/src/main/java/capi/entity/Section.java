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
@Table(name="Section")
public class Section  extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SectionId")
	private Integer sectionId;
	
	@Column(name="Code")
	private String code;
	
	@Column(name="ChineseName")
	private String chineseName;

	@Column(name="EnglishName")
	private String englishName;
	
	@Column(name="ObsoleteDate")
	private Date obsoleteDate;
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
	private Set<Group> groups = new HashSet<Group>();
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
	private Set<SectionStatistic> sectionStatistics = new HashSet<SectionStatistic>();

	
	public Integer getId(){
		return this.getSectionId();
	}


	public Integer getSectionId() {
		return sectionId;
	}


	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getChineseName() {
		return chineseName;
	}


	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}


	public String getEnglishName() {
		return englishName;
	}


	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}


	public Set<Group> getGroups() {
		return groups;
	}


	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}


	public Date getObsoleteDate() {
		return obsoleteDate;
	}


	public void setObsoleteDate(Date obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
	}


	public Date getEffectiveDate() {
		return effectiveDate;
	}


	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public Set<SectionStatistic> getSectionStatistics() {
		return sectionStatistics;
	}


	public void setSectionStatistics(Set<SectionStatistic> sectionStatistics) {
		this.sectionStatistics = sectionStatistics;
	}
	
	
	
}
