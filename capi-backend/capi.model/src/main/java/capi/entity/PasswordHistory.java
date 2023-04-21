package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PasswordHistory")
public class PasswordHistory extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PasswordHistoryId")
	private Integer passwordHistoryId;
	
	
	@Column(name="UserId")
	private Integer userId;
	
	@Column(name="Password")
	private String password;
	
	@Column(name="PasswordUpdateDate")
	private Date passwordUpdateDate;

	@Override
	public Integer getId() {
		return this.getPasswordHistoryId();
	}

	public Integer getPasswordHistoryId() {
		return passwordHistoryId;
	}

	public void setPasswordHistoryId(Integer passwordHistoryId) {
		this.passwordHistoryId = passwordHistoryId;
	}
	
	public Integer getUserId(){
		return this.userId;
	}
	
	public void setUserId(Integer userId){
		this.userId = userId;
	}
	
	
	public String getPassword(){
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	
	public Date getPasswordUpdateDate(){
		return this.passwordUpdateDate;
	}
	
	public void setPasswordUpdatedDate(Date passwordUpdateDate){
		this.passwordUpdateDate = passwordUpdateDate;
	}
	
	
	
	
	
	
	
	
	
}
