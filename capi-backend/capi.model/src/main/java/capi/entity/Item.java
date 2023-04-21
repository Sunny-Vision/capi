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
@Table(name="Item")
public class Item  extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ItemId")
	private Integer itemId;
	
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
	@JoinColumn(name = "SubGroupId", nullable = true)
	private SubGroup subGroup;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
	private Set<OutletType> outletTypes = new HashSet<OutletType>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
	private Set<ItemStatistic> itemStatistics = new HashSet<ItemStatistic>();

	public Integer getId(){
		return this.getItemId();
	}	
	
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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

	public SubGroup getSubGroup() {
		return subGroup;
	}

	public void setSubGroup(SubGroup subGroup) {
		this.subGroup = subGroup;
	}

	public Set<OutletType> getOutletTypes() {
		return outletTypes;
	}

	public void setOutletTypes(Set<OutletType> outletTypes) {
		this.outletTypes = outletTypes;
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

	public Set<ItemStatistic> getItemStatistics() {
		return itemStatistics;
	}

	public void setItemStatistics(Set<ItemStatistic> itemStatistics) {
		this.itemStatistics = itemStatistics;
	}
	
}
