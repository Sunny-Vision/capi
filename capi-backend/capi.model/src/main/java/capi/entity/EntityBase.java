package capi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class EntityBase implements Serializable{

	
	@Column(name="CreatedDate")
	private Date createdDate;

	@Column(name="ModifiedDate")
	private Date modifiedDate;
	
	@Column(name="CreatedBy")
	private String createdBy;

	@Column(name="ModifiedBy")
	private String modifiedBy;
	
	@Transient
	private boolean byPassLog;
	
	@Transient
	private boolean byPassModifiedDate;
	
//	@PreUpdate
//	public void preUpdate(){
//		System.out.println("preUpdate");
//		if (createdDate == null){
//			createdDate = new Date();
//		}
//		modifiedDate = new Date();
//	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public abstract Integer getId();

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isByPassLog() {
		return byPassLog;
	}

	public void setByPassLog(boolean byPassLog) {
		this.byPassLog = byPassLog;
	}

	public boolean isByPassModifiedDate() {
		return byPassModifiedDate;
	}

	public void setByPassModifiedDate(boolean byPassModifiedDate) {
		this.byPassModifiedDate = byPassModifiedDate;
	}
	
}
