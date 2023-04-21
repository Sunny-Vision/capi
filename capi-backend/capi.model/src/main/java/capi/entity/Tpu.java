package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="Tpu", uniqueConstraints = {@UniqueConstraint(columnNames = "Code")})
public class Tpu extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="TpuId")
	private Integer tpuId;	
	
	@Column(name="Code")
	private String code;
	
	@Deprecated
	@Column(name="Description")
	private String description;
	
	@Column(name="CouncilDistrict")
	private String councilDistrict;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DistrictId", nullable = true)
	private District district;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getTpuId();
	}

	public Integer getTpuId() {
		return tpuId;
	}

	public void setTpuId(Integer tpuId) {
		this.tpuId = tpuId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Deprecated
	public String getDescription() {
		return description;
	}

	@Deprecated
	public void setDescription(String description) {
		this.description = description;
	}

	public String getCouncilDistrict() {
		return councilDistrict;
	}

	public void setCouncilDistrict(String councilDistrict) {
		this.councilDistrict = councilDistrict;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

}
