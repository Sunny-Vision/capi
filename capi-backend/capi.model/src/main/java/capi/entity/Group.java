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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="[Group]")
public class Group  extends capi.entity.EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="GroupId")
	private Integer groupId;
	
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SectionId", nullable = true)
	private Section section;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
	private Set<SubGroup> subGroups = new HashSet<SubGroup>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
	private Set<GroupStatistic> groupStatistics = new HashSet<GroupStatistic>();
	
	public Integer getId(){
		return this.getGroupId();
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
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

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public Set<SubGroup> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(Set<SubGroup> subGroups) {
		this.subGroups = subGroups;
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

	public Set<GroupStatistic> getGroupStatistics() {
		return groupStatistics;
	}

	public void setGroupStatistics(Set<GroupStatistic> groupStatistics) {
		this.groupStatistics = groupStatistics;
	}
	
}
