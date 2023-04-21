package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ActivityCode")
public class ActivityCode extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ActivityCodeId")
	private Integer activityCodeId;
	
	@Column(name="Code")
	private String code;
	
	@Column(name="Description")
	private String description;
	
	@Column(name="ManDay")
	private Double manDay;
	
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getActivityCodeId();
	}



	public Integer getActivityCodeId() {
		return activityCodeId;
	}



	public void setActivityCodeId(Integer activityCodeId) {
		this.activityCodeId = activityCodeId;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public Double getManDay() {
		return manDay;
	}



	public void setManDay(Double manDay) {
		this.manDay = manDay;
	}
	

}
