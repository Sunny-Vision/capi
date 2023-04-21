package capi.model;

public class MessageRibbonModel {
	private String messageType;
	private String messageTitle;
	private String iconClass;
	private String message;
	
	public static String MESSAGE_TYPE_ALERT = "alert-danger";
	public static String MESSAGE_TYPE_SUCCESS = "alert-success";
	
	public static String MESSAGE_TITLE_ALERT = "Alert!";
	public static String MESSAGE_TITLE_SUCCESS = "Information"; 

	public static String ICON_CLASS_ALERT = "fa-warning";
	public static String ICON_CLASS_SUCCESS = "fa-check";
	
	
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	public String getIconClass() {
		return iconClass;
	}
	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
