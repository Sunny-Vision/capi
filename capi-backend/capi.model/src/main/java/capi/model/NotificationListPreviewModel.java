package capi.model;

import java.util.List;

public class NotificationListPreviewModel {

	private List<NotifcationPreviewModel> notification;
	
	private Long unReadCount;

	public List<NotifcationPreviewModel> getNotification() {
		return notification;
	}

	public void setNotification(List<NotifcationPreviewModel> notification) {
		this.notification = notification;
	}

	public Long getUnReadCount() {
		return unReadCount;
	}

	public void setUnReadCount(Long unReadCount) {
		this.unReadCount = unReadCount;
	}
	
}
