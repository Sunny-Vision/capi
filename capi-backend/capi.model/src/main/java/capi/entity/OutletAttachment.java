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
@Table(name="OutletAttachment")
public class OutletAttachment extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="OutletAttachmentId")
	private Integer outletAttachmentId;
	
	@Column(name="Path")
	private String path;
	
	@Column(name="Sequence")
	private Integer sequence;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;

	public Integer getOutletAttachmentId() {
		return outletAttachmentId;
	}

	public void setOutletAttachmentId(Integer outletAttachmentId) {
		this.outletAttachmentId = outletAttachmentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getOutletAttachmentId();
	}
	
}
