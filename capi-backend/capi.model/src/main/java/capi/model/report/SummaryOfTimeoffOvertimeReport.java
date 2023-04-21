package capi.model.report;

public class SummaryOfTimeoffOvertimeReport {

	private String team;
	
	private String post;
	
	private String officer;
	
	private Integer userId;
	
	private String openingTimeOffBalance;
	
	private String cotDate;
	
	private String cotTime;
	
	private String cotDuration;
	
	private String ctoDate;
	
	private String ctoTime;
	
	private String ctoDuration;
	
	private String closingTimeOffBalance;
	
	private Double accumulatedOT;
	
	private String cotSubTotal;
	
	private String ctoSubTotal;

	private int cotSubTotalMinutes;
	
	private int ctoSubTotalMinutes;
	
	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getOfficer() {
		return officer;
	}

	public void setOfficer(String officer) {
		this.officer = officer;
	}	

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOpeningTimeOffBalance() {
		return openingTimeOffBalance;
	}

	public void setOpeningTimeOffBalance(String openingTimeOffBalance) {
		this.openingTimeOffBalance = openingTimeOffBalance;
	}

	public String getCotDate() {
		return cotDate;
	}

	public void setCotDate(String cotDate) {
		this.cotDate = cotDate;
	}

	public String getCotTime() {
		return cotTime;
	}

	public void setCotTime(String cotTime) {
		this.cotTime = cotTime;
	}

	public String getCotDuration() {
		return cotDuration;
	}

	public void setCotDuration(String cotDuration) {
		this.cotDuration = cotDuration;
	}

	public String getCtoDate() {
		return ctoDate;
	}

	public void setCtoDate(String ctoDate) {
		this.ctoDate = ctoDate;
	}

	public String getCtoTime() {
		return ctoTime;
	}

	public void setCtoTime(String ctoTime) {
		this.ctoTime = ctoTime;
	}

	public String getCtoDuration() {
		return ctoDuration;
	}

	public void setCtoDuration(String ctoDuration) {
		this.ctoDuration = ctoDuration;
	}

	public String getClosingTimeOffBalance() {
		return closingTimeOffBalance;
	}

	public void setClosingTimeOffBalancer(String closingTimeOffBalance) {
		this.closingTimeOffBalance = closingTimeOffBalance;
	}

	public Double getAccumulatedOT() {
		return accumulatedOT;
	}

	public void setAccumulatedOT(Double accumulatedOT) {
		this.accumulatedOT = accumulatedOT;
	}

	public void setClosingTimeOffBalance(String closingTimeOffBalance) {
		this.closingTimeOffBalance = closingTimeOffBalance;
	}

	public String getCotSubTotal() {
		return cotSubTotal;
	}

	public void setCotSubTotal(String cotSubTotal) {
		this.cotSubTotal = cotSubTotal;
	}

	public String getCtoSubTotal() {
		return ctoSubTotal;
	}

	public void setCtoSubTotal(String ctoSubTotal) {
		this.ctoSubTotal = ctoSubTotal;
	}

	public int getCotSubTotalMinutes() {
		return cotSubTotalMinutes;
	}

	public void setCotSubTotalMinutes(int cotSubTotalMinutes) {
		this.cotSubTotalMinutes = cotSubTotalMinutes;
	}

	public int getCtoSubTotalMinutes() {
		return ctoSubTotalMinutes;
	}

	public void setCtoSubTotalMinutes(int ctoSubTotalMinutes) {
		this.ctoSubTotalMinutes = ctoSubTotalMinutes;
	}
}
