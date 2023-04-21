package capi.model.api;

import java.util.List;

public class MobileResponseModel <T>{

	public static final int SUCCESS = 1;
	public static final int FAIL = 2;
	public static final int INVALID = 4;
	
	private List<T> data;
	
	private String message;
	
	private Integer status;
	
	private String statusString;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	} 
	
	
}
