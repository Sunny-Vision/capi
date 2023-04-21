package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PricingFrequency")
public class PricingFrequency extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PricingFrequencyId")
	private Integer pricingFrequencyId;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="IsJan")
	private boolean isJan;
	
	@Column(name="IsFeb")
	private boolean isFeb;
	
	@Column(name="IsMar")
	private boolean isMar;
	
	@Column(name="IsApr")
	private boolean isApr;
	
	@Column(name="IsMay")
	private boolean isMay;
	
	@Column(name="IsJun")
	private boolean isJun;
	
	@Column(name="IsJul")
	private boolean isJul;
	
	@Column(name="IsAug")
	private boolean isAug;
	
	@Column(name="IsSep")
	private boolean isSep;
	
	@Column(name="IsOct")
	private boolean isOct;
	
	@Column(name="IsNov")
	private boolean isNov;
	
	@Column(name="IsDec")
	private boolean isDec;
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPricingFrequencyId();
	}


	public Integer getPricingFrequencyId() {
		return pricingFrequencyId;
	}


	public void setPricingFrequencyId(Integer pricingFrequencyId) {
		this.pricingFrequencyId = pricingFrequencyId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isJan() {
		return isJan;
	}


	public void setJan(boolean isJan) {
		this.isJan = isJan;
	}


	public boolean isFeb() {
		return isFeb;
	}


	public void setFeb(boolean isFeb) {
		this.isFeb = isFeb;
	}


	public boolean isMar() {
		return isMar;
	}


	public void setMar(boolean isMar) {
		this.isMar = isMar;
	}


	public boolean isApr() {
		return isApr;
	}


	public void setApr(boolean isApr) {
		this.isApr = isApr;
	}


	public boolean isMay() {
		return isMay;
	}


	public void setMay(boolean isMay) {
		this.isMay = isMay;
	}


	public boolean isJun() {
		return isJun;
	}


	public void setJun(boolean isJun) {
		this.isJun = isJun;
	}


	public boolean isJul() {
		return isJul;
	}


	public void setJul(boolean isJul) {
		this.isJul = isJul;
	}


	public boolean isAug() {
		return isAug;
	}


	public void setAug(boolean isAug) {
		this.isAug = isAug;
	}


	public boolean isSep() {
		return isSep;
	}


	public void setSep(boolean isSep) {
		this.isSep = isSep;
	}


	public boolean isOct() {
		return isOct;
	}


	public void setOct(boolean isOct) {
		this.isOct = isOct;
	}


	public boolean isNov() {
		return isNov;
	}


	public void setNov(boolean isNov) {
		this.isNov = isNov;
	}


	public boolean isDec() {
		return isDec;
	}


	public void setDec(boolean isDec) {
		this.isDec = isDec;
	}
	

}
