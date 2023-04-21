package capi.model.api.dataSync;

import java.util.Date;

public class QuotationSyncData {
	
	private Integer quotationId;
	
	private Integer outletId;
	
	private Integer unitId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String status;
	
	private Integer productId;
	
	private Integer batchId;
	
	private Double quotationLoading;
	
	private String indoorAllocationCode;
	
	private boolean isICP;
	
	private String cpiCompilationSeries;
	
	private String oldFormBarSerial;
	
	private String oldFormSequence;
	
	private Double frAdmin;
	
	private Double frField;
	
	private Boolean isUseFRAdmin;
	
	private Date seasonalWithdrawal;
	
	private Date lastFRAppliedDate;
	
	private boolean isFRApplied;
	
	private boolean isReturnGoods;
	
	private boolean isReturnNewGoods;
	
	private Integer oldUnitId;
	
	private Double usedFRValue;
	
	private Boolean isUsedFRPercentage;
	
	private Boolean isFRAdminPercentage;
	
	private Boolean isFRFieldPercentage;
	
	private Date ruaDate;
	
	private boolean isRUAAllDistrict;
	
	private Integer districtId;
	
	private String icpProductCode;
	
	private Integer newUnitId;
	
	private String productPosition;
	
	private String productRemark;
	
	private Date lastProductChangeDate;
	
	private Boolean tempIsFRApplied;
	
	private Boolean tempIsReturnGoods;
	
	private Boolean tempIsReturnNewGoods;
	
	private Date tempLastFRAppliedDate;
	
	private Integer newProductId;
	
	private Boolean tempIsUseFRAdmin;
	
	private Integer keepNoMonth;
	
	private Integer tempKeepNoMonth;
	
	private boolean lastSeasonReturnGoods;
	
	private Double tempFRValue;
	
	private Boolean tempFRPercentage;
	
	private Integer localId;

	private String formType;
	
	private String icpType;
	
	private String icpProductName;
	
	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Double getQuotationLoading() {
		return quotationLoading;
	}

	public void setQuotationLoading(Double quotationLoading) {
		this.quotationLoading = quotationLoading;
	}

	public String getIndoorAllocationCode() {
		return indoorAllocationCode;
	}

	public void setIndoorAllocationCode(String indoorAllocationCode) {
		this.indoorAllocationCode = indoorAllocationCode;
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

	public Boolean getIsUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setIsUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public Date getSeasonalWithdrawal() {
		return seasonalWithdrawal;
	}

	public void setSeasonalWithdrawal(Date seasonalWithdrawal) {
		this.seasonalWithdrawal = seasonalWithdrawal;
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}

	public Integer getOldUnitId() {
		return oldUnitId;
	}

	public void setOldUnitId(Integer oldUnitId) {
		this.oldUnitId = oldUnitId;
	}

	public Double getUsedFRValue() {
		return usedFRValue;
	}

	public void setUsedFRValue(Double usedFRValue) {
		this.usedFRValue = usedFRValue;
	}

	public Boolean getIsUsedFRPercentage() {
		return isUsedFRPercentage;
	}

	public void setIsUsedFRPercentage(Boolean isUsedFRPercentage) {
		this.isUsedFRPercentage = isUsedFRPercentage;
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

	public Date getRuaDate() {
		return ruaDate;
	}

	public void setRuaDate(Date ruaDate) {
		this.ruaDate = ruaDate;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getIcpProductCode() {
		return icpProductCode;
	}

	public void setIcpProductCode(String icpProductCode) {
		this.icpProductCode = icpProductCode;
	}

	public Integer getNewUnitId() {
		return newUnitId;
	}

	public void setNewUnitId(Integer newUnitId) {
		this.newUnitId = newUnitId;
	}

	public String getProductPosition() {
		return productPosition;
	}

	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
	}

	public String getProductRemark() {
		return productRemark;
	}

	public void setProductRemark(String productRemark) {
		this.productRemark = productRemark;
	}

	public Date getLastProductChangeDate() {
		return lastProductChangeDate;
	}

	public void setLastProductChangeDate(Date lastProductChangeDate) {
		this.lastProductChangeDate = lastProductChangeDate;
	}

	public Boolean getTempIsFRApplied() {
		return tempIsFRApplied;
	}

	public void setTempIsFRApplied(Boolean tempIsFRApplied) {
		this.tempIsFRApplied = tempIsFRApplied;
	}

	public Boolean getTempIsReturnGoods() {
		return tempIsReturnGoods;
	}

	public void setTempIsReturnGoods(Boolean tempIsReturnGoods) {
		this.tempIsReturnGoods = tempIsReturnGoods;
	}

	public Boolean getTempIsReturnNewGoods() {
		return tempIsReturnNewGoods;
	}

	public void setTempIsReturnNewGoods(Boolean tempIsReturnNewGoods) {
		this.tempIsReturnNewGoods = tempIsReturnNewGoods;
	}

	public Date getTempLastFRAppliedDate() {
		return tempLastFRAppliedDate;
	}

	public void setTempLastFRAppliedDate(Date tempLastFRAppliedDate) {
		this.tempLastFRAppliedDate = tempLastFRAppliedDate;
	}

	public Integer getNewProductId() {
		return newProductId;
	}

	public void setNewProductId(Integer newProductId) {
		this.newProductId = newProductId;
	}

	public Integer getKeepNoMonth() {
		return keepNoMonth;
	}

	public void setKeepNoMonth(Integer keepNoMonth) {
		this.keepNoMonth = keepNoMonth;
	}

	public Integer getTempKeepNoMonth() {
		return tempKeepNoMonth;
	}

	public void setTempKeepNoMonth(Integer tempKeepNoMonth) {
		this.tempKeepNoMonth = tempKeepNoMonth;
	}

	public boolean isFRApplied() {
		return isFRApplied;
	}

	public void setFRApplied(boolean isFRApplied) {
		this.isFRApplied = isFRApplied;
	}

	public boolean isReturnGoods() {
		return isReturnGoods;
	}

	public void setReturnGoods(boolean isReturnGoods) {
		this.isReturnGoods = isReturnGoods;
	}

	public boolean isReturnNewGoods() {
		return isReturnNewGoods;
	}

	public void setReturnNewGoods(boolean isReturnNewGoods) {
		this.isReturnNewGoods = isReturnNewGoods;
	}

	public boolean isRUAAllDistrict() {
		return isRUAAllDistrict;
	}

	public void setRUAAllDistrict(boolean isRUAAllDistrict) {
		this.isRUAAllDistrict = isRUAAllDistrict;
	}

	public boolean isLastSeasonReturnGoods() {
		return lastSeasonReturnGoods;
	}

	public void setLastSeasonReturnGoods(boolean lastSeasonReturnGoods) {
		this.lastSeasonReturnGoods = lastSeasonReturnGoods;
	}

	public boolean isICP() {
		return isICP;
	}

	public void setICP(boolean isICP) {
		this.isICP = isICP;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public Double getTempFRValue() {
		return tempFRValue;
	}

	public void setTempFRValue(Double tempFRValue) {
		this.tempFRValue = tempFRValue;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Boolean getTempFRPercentage() {
		return tempFRPercentage;
	}

	public void setTempFRPercentage(Boolean tempFRPercentage) {
		this.tempFRPercentage = tempFRPercentage;
	}

	public Boolean getTempIsUseFRAdmin() {
		return tempIsUseFRAdmin;
	}

	public void setTempIsUseFRAdmin(Boolean tempIsUseFRAdmin) {
		this.tempIsUseFRAdmin = tempIsUseFRAdmin;
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


	
}
