package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ImportExportTask")
public class ImportExportTask extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ImportExportTaskId")
	private Integer importExportTaskId;
		
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="FinishedDate")
	private Date finishedDate;
	
	@Column(name="Status")
	private String status;
	
	@Column(name="ErrorMessage")
	private String errorMessage;
	
	@Column(name="TaskType")
	private String taskType;
	
	@Column(name="FilePath")
	private String filePath;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ImportExportTaskDefinitionId", nullable = true)
	private ImportExportTaskDefinition taskDefinition;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductGroupId", nullable = true)
	private ProductGroup productGroup;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SurveyMonthId", nullable = true)
	private SurveyMonth surveyMonth;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceTypeId", nullable = true)
	private SubPriceType subPriceType;
	
	@Column(name="CPIBasePeriod")
	private String cpiBasePeriod;
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;

	@Column(name="TimeLogDate", nullable = true)
	private String timeLogDate;

	@Column(name="TimeLogUserId", nullable = true)
	private String timeLogUserId;
	
	@Column(name="PurposeId", nullable = true)
	private Integer purposeId;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getImportExportTaskId();
	}


	public Integer getImportExportTaskId() {
		return importExportTaskId;
	}


	public void setImportExportTaskId(Integer importExportTaskId) {
		this.importExportTaskId = importExportTaskId;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getFinishedDate() {
		return finishedDate;
	}


	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public String getTaskType() {
		return taskType;
	}


	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}


	public ImportExportTaskDefinition getTaskDefinition() {
		return taskDefinition;
	}


	public void setTaskDefinition(ImportExportTaskDefinition taskDefinition) {
		this.taskDefinition = taskDefinition;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public ProductGroup getProductGroup() {
		return productGroup;
	}


	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}


	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}


	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}


	public Date getReferenceMonth() {
		return referenceMonth;
	}


	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}


	public SubPriceType getSubPriceType() {
		return subPriceType;
	}


	public void setSubPriceType(SubPriceType subPriceType) {
		this.subPriceType = subPriceType;
	}


	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}


	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}


	public Date getEffectiveDate() {
		return effectiveDate;
	}


	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public String getTimeLogDate() {
		return timeLogDate;
	}


	public void setTimeLogDate(String timeLogDate) {
		this.timeLogDate = timeLogDate;
	}


	public String getTimeLogUserId() {
		return timeLogUserId;
	}


	public void setTimeLogUserId(String timeLogUserId) {
		this.timeLogUserId = timeLogUserId;
	}

	public Integer getPurposeId() {
		return purposeId;
	}


	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}
	

}
