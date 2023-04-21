package capi.model;

import java.util.List;

public class ServiceException extends RuntimeException {
	private List<String> messages;
	public ServiceException(String message) {
		super(message);
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
