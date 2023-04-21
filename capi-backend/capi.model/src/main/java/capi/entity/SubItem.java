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
@Table(name="SubItem")
public class SubItem extends capi.entity.EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SubItemId")
	private Integer subItemId;
	
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
	@JoinColumn(name = "OutletTypeId", nullable = true)
	private OutletType outletType;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subItem")
	private Set<Unit> units = new HashSet<Unit>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subItem")
	private Set<SubItemStatistic> subItemStaistics = new HashSet<SubItemStatistic>();
	
	/**
	 * 1- “A.M. (Supermarket, fresh)”, 
	 * 2- “A.M. (Supermarket, non-fresh)”, 
	 * 3- “A.M. (Market)”, 
	 * 4- “G.M. (Supermarket)”, 
	 * 5- “G.M. (Batch)”, 
	 * 6- “A.M. Batch”
	 */
	@Column(name="CompilationMethod")
	private Integer compilationMethod;

	public Integer getId(){
		return this.getSubItemId();
	}
	
	public Integer getSubItemId() {
		return subItemId;
	}

	public void setSubItemId(Integer subItemId) {
		this.subItemId = subItemId;
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

	public OutletType getOutletType() {
		return outletType;
	}

	public void setOutletType(OutletType outletType) {
		this.outletType = outletType;
	}

	public Set<Unit> getUnits() {
		return units;
	}

	public void setUnits(Set<Unit> units) {
		this.units = units;
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

	public Integer getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}

	public Set<SubItemStatistic> getSubItemStaistics() {
		return subItemStaistics;
	}

	public void setSubItemStaistics(Set<SubItemStatistic> subItemStaistics) {
		this.subItemStaistics = subItemStaistics;
	}
	
}
