package capi.model.api.dataSync;

import java.util.Date;
import java.util.List;

public class OutletAttachmentSyncModel {

	private List<OutletAttachmentSyncData> attachments;
	private Date lastSyncTime;
	private Boolean dataReturn;
	public List<OutletAttachmentSyncData> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<OutletAttachmentSyncData> attachments) {
		this.attachments = attachments;
	}
	public Date getLastSyncTime() {
		return lastSyncTime;
	}
	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
	public Boolean getDataReturn() {
		return dataReturn;
	}
	public void setDataReturn(Boolean dataReturn) {
		this.dataReturn = dataReturn;
	}
	
}
