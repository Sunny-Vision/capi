package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ReportReasonSetting")
public class ReportReasonSetting extends EntityBase {
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ReportReasonSettingId")
	private Integer reportReasonSettingId;
	
	@Column(name="Reason")
	private String reason;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getReportReasonSettingId();
	}

	public Integer getReportReasonSettingId() {
		return reportReasonSettingId;
	}

	public void setReportReasonSettingId(Integer reportReasonSettingId) {
		this.reportReasonSettingId = reportReasonSettingId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
