package capi.model.report;

import java.util.Date;

public class OutletAmendmentReport {

	private Integer rowNum;
	private Integer outletId;
	private String outletName;
	private String username;
	private Date amendmentDate;
	private String name;
	private String staffCode;
	private String rankCode;
	private String changeDetail;
	private String oldDetail;
	private Integer firmCode;
	
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getAmendmentDate() {
		return amendmentDate;
	}
	public void setAmendmentDate(Date amendmentDate) {
		this.amendmentDate = amendmentDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStaffCode() {
		return staffCode;
	}
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}
	public String getRankCode() {
		return rankCode;
	}
	public void setRankCode(String rankCode) {
		this.rankCode = rankCode;
	}
	public String getChangeDetail() {
		return changeDetail;
	}
	public void setChangeDetail(String changeDetail) {
		this.changeDetail = changeDetail;
	}
	public String getOldDetail() {
		return oldDetail;
	}
	public void setOldDetail(String oldDetail) {
		this.oldDetail = oldDetail;
	}
	public Integer getRowNum() {
		return rowNum;
	}
	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
	public Integer getFirmCode() {
		return firmCode;
	}
	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}
	public String getOutletName() {
		return outletName;
	}
	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}
	
	
	
}
