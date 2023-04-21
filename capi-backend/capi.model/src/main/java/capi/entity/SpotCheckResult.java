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
@Table(name="SpotCheckResult")
public class SpotCheckResult extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SpotCheckResultId")
	private Integer spotCheckResultId;
	
	/**
	 * P,C,N,R,D,L,M,Others
	 */
	@Column(name="Result")
	private String result;
	
	@Column(name="OtherRemark")
	private String otherRemark;
	
	@Column(name="ReferenceNo")
	private String referenceNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SpotCheckFormId", nullable = true)
	private SpotCheckForm spotCheckForm;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSpotCheckResultId();
	}

	public Integer getSpotCheckResultId() {
		return spotCheckResultId;
	}

	public void setSpotCheckResultId(Integer spotCheckResultId) {
		this.spotCheckResultId = spotCheckResultId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public SpotCheckForm getSpotCheckForm() {
		return spotCheckForm;
	}

	public void setSpotCheckForm(SpotCheckForm spotCheckForm) {
		this.spotCheckForm = spotCheckForm;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

}
