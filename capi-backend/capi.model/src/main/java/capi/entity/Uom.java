package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="UOM")
public class Uom extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UOMId")
	private Integer uomId;
	
	@Column(name="EnglishName")
	private String englishName;
	
	@Column(name="ChineseName")
	private String chineseName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UOMCategoryId", nullable = true)
	private UOMCategory uomCategory;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getUomId();
	}

	public Integer getUomId() {
		return uomId;
	}

	public void setUomId(Integer uomId) {
		this.uomId = uomId;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public UOMCategory getUomCategory() {
		return uomCategory;
	}

	public void setUomCategory(UOMCategory uomCategory) {
		this.uomCategory = uomCategory;
	}
	

}
