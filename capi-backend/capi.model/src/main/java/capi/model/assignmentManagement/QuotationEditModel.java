package capi.model.assignmentManagement;

import java.util.Date;

public class QuotationEditModel {

	private Integer quotationId;
	
	private Integer unitId;
	
	private String unitName;
	
	private Integer outletId;
	
	private String outletName;
	
	private Integer productId;
	
	private String productName;

	private Integer batchId;
	
	private String batchName;
	
	private Double quotationLoading;
	
	private String status;
	
	private String indoorAllocationCode;
	
	private Boolean isICP;
	
	private String cpiCompilationSeries;
	
	private String oldFormBarSerial;
	
	private String oldFormSequence;
	
	private Double frAdmin;
	
	private Double frField;
	
	private Boolean isUseFRAdmin;
	
	private Date seasonalWithdrawal;
	
	private String seasonalWithdrawalMonth;
	
	private Date lastFRAppliedDate;
	
	private Date displayCreatedDate;
	
	private Date displayModifiedDate;
	
	private String icpProductCode;
	
	private Boolean isFRApplied;
	
	private Boolean isReturnGoods;
	
	private Boolean isReturnNewGoods;
	
	private Boolean isFRAdminPercentage;
	
	private Boolean isFRFieldPercentage;
	
	private String formType;
	

	private String icpType;
	
	private String icpProductName;

	private RUASettingEditModel ruaSettingEditModel;

	private boolean frRequired;
	
	private Boolean isTempIsUseFRAdmin;
	
	private Boolean isTempIsFRApplied;
	
	private Boolean isTempIsReturnGoods;
	
	private Boolean isTempIsReturnNewGoods;
	
	private Boolean isLastSeasonReturnGoods;

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Double getQuotationLoading() {
		return quotationLoading;
	}

	public void setQuotationLoading(Double quotationLoading) {
		this.quotationLoading = quotationLoading;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIndoorAllocationCode() {
		return indoorAllocationCode;
	}

	public void setIndoorAllocationCode(String indoorAllocationCode) {
		this.indoorAllocationCode = indoorAllocationCode;
	}

	public boolean isICP() {
		return isICP;
	}

	public void setICP(boolean isICP) {
		this.isICP = isICP;
	}

	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}

	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
	}

	public String getOldFormBarSerial() {
		return oldFormBarSerial;
	}

	public void setOldFormBarSerial(String oldFormBarSerial) {
		this.oldFormBarSerial = oldFormBarSerial;
	}

	public String getOldFormSequence() {
		return oldFormSequence;
	}

	public void setOldFormSequence(String oldFormSequence) {
		this.oldFormSequence = oldFormSequence;
	}

	public Double getFrAdmin() {
		return frAdmin;
	}

	public void setFrAdmin(Double frAdmin) {
		this.frAdmin = frAdmin;
	}

	public Double getFrField() {
		return frField;
	}

	public void setFrField(Double frField) {
		this.frField = frField;
	}

	public Boolean getUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public Date getSeasonalWithdrawal() {
		return seasonalWithdrawal;
	}

	public String getSeasonalWithdrawalMonth() {
		return seasonalWithdrawalMonth;
	}

	public void setSeasonalWithdrawal(Date seasonalWithdrawal) {
		this.seasonalWithdrawal = seasonalWithdrawal;
	}

	public void setSeasonalWithdrawalMonth(String seasonalWithdrawalMonth) {
			this.seasonalWithdrawalMonth = seasonalWithdrawalMonth;	
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}

	public Date getDisplayCreatedDate() {
		return displayCreatedDate;
	}

	public void setDisplayCreatedDate(Date displayCreatedDate) {
		this.displayCreatedDate = displayCreatedDate;
	}

	public Date getDisplayModifiedDate() {
		return displayModifiedDate;
	}

	public void setDisplayModifiedDate(Date displayModifiedDate) {
		this.displayModifiedDate = displayModifiedDate;
	}

	public Boolean getIsICP() {
		return isICP;
	}

	public void setIsICP(Boolean isICP) {
		this.isICP = isICP;
	}

	public Boolean getIsUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setIsUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public String getIcpProductCode() {
		return icpProductCode;
	}

	public void setIcpProductCode(String icpProductCode) {
		this.icpProductCode = icpProductCode;
	}

	public Boolean getIsFRApplied() {
		return isFRApplied;
	}

	public void setIsFRApplied(Boolean isFRApplied) {
		this.isFRApplied = isFRApplied;
	}

	public Boolean getIsTempIsFRApplied() {
		return isTempIsFRApplied;
	}

	public void setIsTempIsFRApplied(Boolean isTempIsFRApplied) {
		this.isTempIsFRApplied = isTempIsFRApplied;
	}

	public Boolean getIsTempIsReturnGoods() {
		return isTempIsReturnGoods;
	}

	public void setIsTempIsReturnGoods(Boolean isTempIsReturnGoods) {
		this.isTempIsReturnGoods = isTempIsReturnGoods;
	}

	public Boolean getIsTempIsReturnNewGoods() {
		return isTempIsReturnNewGoods;
	}

	public void setIsTempIsReturnNewGoods(Boolean isTempIsReturnNewGoods) {
		this.isTempIsReturnNewGoods = isTempIsReturnNewGoods;
	}

	public Boolean getIsReturnGoods() {
		return isReturnGoods;
	}

	public void setIsReturnGoods(Boolean isReturnGoods) {
		this.isReturnGoods = isReturnGoods;
	}

	public Boolean getIsReturnNewGoods() {
		return isReturnNewGoods;
	}

	public void setIsReturnNewGoods(Boolean isReturnNewGoods) {
		this.isReturnNewGoods = isReturnNewGoods;
	}

	public String getIcpType() {
		return icpType;
	}

	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}

	public String getIcpProductName() {
		return icpProductName;
	}

	public void setIcpProductName(String icpProductName) {
		this.icpProductName = icpProductName;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public RUASettingEditModel getRuaSettingEditModel() {
		return ruaSettingEditModel;
	}

	public void setRuaSettingEditModel(RUASettingEditModel ruaSettingEditModel) {
		this.ruaSettingEditModel = ruaSettingEditModel;
	}

	public Boolean getIsFRAdminPercentage() {
		return isFRAdminPercentage;
	}

	public void setIsFRAdminPercentage(Boolean isFRAdminPercentage) {
		this.isFRAdminPercentage = isFRAdminPercentage;
	}

	public Boolean getIsFRFieldPercentage() {
		return isFRFieldPercentage;
	}

	public void setIsFRFieldPercentage(Boolean isFRFieldPercentage) {
		this.isFRFieldPercentage = isFRFieldPercentage;
	}

	public boolean isFrRequired() {
		return frRequired;
	}

	public void setFrRequired(boolean frRequired) {
		this.frRequired = frRequired;
	}

	public Boolean getIsTempIsUseFRAdmin() {
		return isTempIsUseFRAdmin;
	}

	public void setIsTempIsUseFRAdmin(Boolean isTempIsUseFRAdmin) {
		this.isTempIsUseFRAdmin = isTempIsUseFRAdmin;
	}

	public Boolean getIsLastSeasonReturnGoods() {
		return isLastSeasonReturnGoods;
	}

	public void setIsLastSeasonReturnGoods(Boolean isLastSeasonReturnGoods) {
		this.isLastSeasonReturnGoods = isLastSeasonReturnGoods;
	}


}
