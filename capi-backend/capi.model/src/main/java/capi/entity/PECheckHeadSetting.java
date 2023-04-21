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
@Table(name="PECheckHeadSetting")
public class PECheckHeadSetting extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PECheckHeadSettingId")
	private Integer peCheckHeadSettingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Column(name="IsFieldHead")
	private boolean isFieldHead;	
	
	@Column(name="IsSectionHead")
	private boolean isSectionHead;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AllocationBatchId", nullable = true)
	private AllocationBatch allocationBatch;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPeCheckHeadSettingId();
	}

	public Integer getPeCheckHeadSettingId() {
		return peCheckHeadSettingId;
	}

	public void setPeCheckHeadSettingId(Integer peCheckHeadSettingId) {
		this.peCheckHeadSettingId = peCheckHeadSettingId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isFieldHead() {
		return isFieldHead;
	}

	public void setFieldHead(boolean isFieldHead) {
		this.isFieldHead = isFieldHead;
	}

	public boolean isSectionHead() {
		return isSectionHead;
	}

	public void setSectionHead(boolean isSectionHead) {
		this.isSectionHead = isSectionHead;
	}

	public AllocationBatch getAllocationBatch() {
		return allocationBatch;
	}

	public void setAllocationBatch(AllocationBatch allocationBatch) {
		this.allocationBatch = allocationBatch;
	}

}
