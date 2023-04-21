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
@Table(name="ReportTask")
public class ReportTask extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ReportTaskId")
	private Integer reportTaskId;
	
	@Column(name="Description")
	private String description;
	
	/**
	 * Pending,
	 * Generating,
	 * Completed,
	 * Failed
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="CriteriaSerialize")
	private String criteriaSerialize;
	
	@Column(name="ExceptionMessage")
	private String exceptionMessage;
	
	@Column(name="FunctionCode")
	private String functionCode;
	
	/**
	 * 1 - pdf
	 * 2 - excel
	 * 3 - doc
	 */
	@Column(name="ReportType")
	private Integer reportType;
	
	@Column(name="Path")
	private String path;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getReportTaskId();
	}

	public Integer getReportTaskId() {
		return reportTaskId;
	}

	public void setReportTaskId(Integer reportTaskId) {
		this.reportTaskId = reportTaskId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCriteriaSerialize() {
		return criteriaSerialize;
	}

	public void setCriteriaSerialize(String criteriaSerialize) {
		this.criteriaSerialize = criteriaSerialize;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
