package capi.model.api;

public class LoginResultModel {

	public static final int SUCCESS = 1;
	public static final int FAIL = 2;
	public static final int INVALID = 4;
		
	
	private MobileUserModel user;
	
	private String message;
	
	private Integer status;
	
	private String statusString;

	public MobileUserModel getUser() {
		return user;
	}

	public void setUser(MobileUserModel user) {
		this.user = user;
	}

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

	
	
}
