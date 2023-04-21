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
@Table(name="OutletType")
public class OutletType extends capi.entity.EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="OutletTypeId")
	private Integer outletTypeId;
	
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
	@JoinColumn(name = "ItemId", nullable = true)
	private Item item;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outletType")
	private Set<SubItem> subItems = new HashSet<SubItem>();
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outletType")
	private Set<OutletTypeStatistic> outletTypeStatistics = new HashSet<OutletTypeStatistic>();

	public Integer getId(){
		return this.getOutletTypeId();
	}
	
	public Integer getOutletTypeId() {
		return outletTypeId;
	}

	public void setOutletTypeId(Integer outletTypeId) {
		this.outletTypeId = outletTypeId;
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

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Set<SubItem> getSubItems() {
		return subItems;
	}

	public void setSubItems(Set<SubItem> subItems) {
		this.subItems = subItems;
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

	public Set<OutletTypeStatistic> getOutletTypeStatistics() {
		return outletTypeStatistics;
	}

	public void setOutletTypeStatistics(Set<OutletTypeStatistic> outletTypeStatistics) {
		this.outletTypeStatistics = outletTypeStatistics;
	}
	
}
