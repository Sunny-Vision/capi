package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="vwOutletTypeShortForm")
public class VwOutletTypeShortForm implements ViewBase{
	// This is indexed view in database. i.e. no insert, update, delete operation can perform on this entity

	@Id
	@Column(name="ShortCode")
	private String shortCode;	
	
	@Column(name="ChineseName")
	private String chineseName;

	@Column(name="EnglishName")
	private String englishName;		
	
	@Column(name="Count")
	private Integer count;

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.getShortCode();
	}


}
