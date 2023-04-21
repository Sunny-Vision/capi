package capi.entity;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="District", uniqueConstraints = {@UniqueConstraint(columnNames = "Code")})
public class District extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="DistrictId")
	private Integer districtId;	
	
	@Column(name="Code")
	private String code;
	
	@Column(name="ChineseName")
	private String chineseName;
	
	@Column(name="EnglishName")
	private String englishName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
	private Set<Tpu> tpus = new HashSet<Tpu>();
	
	@Column(name="Coverage")
	private String coverage;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getDistrictId();
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Tpu> getTpus() {
		return tpus;
	}

	public void setTpus(Set<Tpu> tpus) {
		this.tpus = tpus;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

}
