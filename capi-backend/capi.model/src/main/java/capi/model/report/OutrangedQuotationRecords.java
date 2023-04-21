package capi.model.report;

public class OutrangedQuotationRecords {

	private String purpose;
	
	private String referenceMonth;
	
	private String referenceDate;
	
	private String cpiBasePeriod;
	
	private String groupCode;
	
	private String groupName;
	
	private String itemCode;
	
	private String itemName;
	
	private String unitCode;
	
	private String unitName;
	
	private String quotationId;
	
	private String quotationRecordId;
	
	private String indoorQuotationRecordId;
	
	private String outletCode;
	
	private String outletName;
	
	private String district;
	
	private String outletTypeCode;
	
	private String outletTypeName;
	
	private String productId;
	
	private String countryOfOrigin;
	
	private String pa1Value;
	
	private String pa2Value;
	
	private String pa3Value;
	
	private String pa4Value;
	
	private String pa5Value;
	
	private String ps1Value;
	
	private String ps2Value;
	
	private String ps3Value;
	
	private String ps4Value;
	
	private String ps5Value;
	
	private Double lastNPrice;
	
	private Double lastSPrice;
	
	private Double previousNPrice;
	
	private Double previousSPrice;
	
	private Double currentNPrice;
	
	private Double currentSPrice;
	
	private String indoorRemark;
	
	private String qrRemark;
	
	private String availability;
	
	private String isProductChange;
	
	private String isNewRecruitment;
	
	private String frApplied;
	
	private String cpiCompilationSeries;
	
	private String uSeasonality;
	
	private Double averageCurrentSPrice;
	
	private Double standardDeviationSPrice;
	
	private Double countCurrentSPrice;
	
	private Double averagePRSPrice;
	
	private Double standardDeviationPRSPrice;
	
	private Double countPRSPrice;
	
	private String keepNumber;
	
	private String staffCode;
	
	private String staffName;
	
	private String noField;
	
	private String cpiQuotationType;
	
	private String batch;

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		String[] a = referenceMonth.split("-");
		this.referenceMonth = a[0]+a[1];
	}

	public String getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(String referenceDate) {
		this.referenceDate = referenceDate;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}

	public String getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(String quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public String getIndoorQuotationRecordId() {
		return indoorQuotationRecordId;
	}

	public void setIndoorQuotationRecordId(String indoorQuotationRecordId) {
		this.indoorQuotationRecordId = indoorQuotationRecordId;
	}

	public String getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public String getOutletTypeName() {
		return outletTypeName;
	}

	public void setOutletTypeName(String outletTypeName) {
		this.outletTypeName = outletTypeName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public String getPa1Value() {
		return pa1Value;
	}

	public void setPa1Value(String pa1Value) {
		this.pa1Value = pa1Value;
	}

	public String getPa2Value() {
		return pa2Value;
	}

	public void setPa2Value(String pa2Value) {
		this.pa2Value = pa2Value;
	}

	public String getPa3Value() {
		return pa3Value;
	}

	public void setPa3Value(String pa3Value) {
		this.pa3Value = pa3Value;
	}

	public String getPa4Value() {
		return pa4Value;
	}

	public void setPa4Value(String pa4Value) {
		this.pa4Value = pa4Value;
	}

	public String getPa5Value() {
		return pa5Value;
	}

	public void setPa5Value(String pa5Value) {
		this.pa5Value = pa5Value;
	}

	public String getPs1Value() {
		return ps1Value;
	}

	public void setPs1Value(String ps1Value) {
		this.ps1Value = ps1Value;
	}

	public String getPs2Value() {
		return ps2Value;
	}

	public void setPs2Value(String ps2Value) {
		this.ps2Value = ps2Value;
	}

	public String getPs3Value() {
		return ps3Value;
	}

	public void setPs3Value(String ps3Value) {
		this.ps3Value = ps3Value;
	}

	public String getPs4Value() {
		return ps4Value;
	}

	public void setPs4Value(String ps4Value) {
		this.ps4Value = ps4Value;
	}

	public String getPs5Value() {
		return ps5Value;
	}

	public void setPs5Value(String ps5Value) {
		this.ps5Value = ps5Value;
	}

	public Double getLastNPrice() {
		return lastNPrice;
	}

	public void setLastNPrice(Double lastNPrice) {
		this.lastNPrice = lastNPrice;
	}

	public Double getLastSPrice() {
		return lastSPrice;
	}

	public void setLastSPrice(Double lastSPrice) {
		this.lastSPrice = lastSPrice;
	}

	public Double getPreviousNPrice() {
		return previousNPrice;
	}

	public void setPreviousNPrice(Double previousNPrice) {
		this.previousNPrice = previousNPrice;
	}

	public Double getPreviousSPrice() {
		return previousSPrice;
	}

	public void setPreviousSPrice(Double previousSPrice) {
		this.previousSPrice = previousSPrice;
	}

	public Double getCurrentNPrice() {
		return currentNPrice;
	}

	public void setCurrentNPrice(Double currentNPrice) {
		this.currentNPrice = currentNPrice;
	}

	public Double getCurrentSPrice() {
		return currentSPrice;
	}

	public void setCurrentSPrice(Double currentSPrice) {
		this.currentSPrice = currentSPrice;
	}

	public String getIndoorRemark() {
		return indoorRemark;
	}

	public void setIndoorRemark(String indoorRemark) {
		this.indoorRemark = indoorRemark;
	}

	public String getQrRemark() {
		return qrRemark;
	}

	public void setQrRemark(String qrRemark) {
		this.qrRemark = qrRemark;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getIsProductChange() {
		return isProductChange;
	}

	public void setIsProductChange(String isProductChange) {
		this.isProductChange = isProductChange;
	}

	public String getIsNewRecruitment() {
		return isNewRecruitment;
	}

	public void setIsNewRecruitment(String isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public String getFrApplied() {
		return frApplied;
	}

	public void setFrApplied(String frApplied) {
		this.frApplied = frApplied;
	}

	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}

	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
	}

	public String getuSeasonality() {
		return uSeasonality;
	}

	public void setuSeasonality(String uSeasonality) {
		this.uSeasonality = uSeasonality;
	}

	public Double getAverageCurrentSPrice() {
		return averageCurrentSPrice;
	}

	public void setAverageCurrentSPrice(Double averageCurrentSPrice) {
		this.averageCurrentSPrice = averageCurrentSPrice;
	}

	public Double getStandardDeviationSPrice() {
		return standardDeviationSPrice;
	}

	public void setStandardDeviationSPrice(Double standardDeviationSPrice) {
		this.standardDeviationSPrice = standardDeviationSPrice;
	}

	public Double getCountCurrentSPrice() {
		return countCurrentSPrice;
	}

	public void setCountCurrentSPrice(Double countCurrentSPrice) {
		this.countCurrentSPrice = countCurrentSPrice;
	}

	public Double getAveragePRSPrice() {
		return averagePRSPrice;
	}

	public void setAveragePRSPrice(Double averagePRSPrice) {
		this.averagePRSPrice = averagePRSPrice;
	}

	public Double getStandardDeviationPRSPrice() {
		return standardDeviationPRSPrice;
	}

	public void setStandardDeviationPRSPrice(Double standardDeviationPRSPrice) {
		this.standardDeviationPRSPrice = standardDeviationPRSPrice;
	}

	public Double getCountPRSPrice() {
		return countPRSPrice;
	}

	public void setCountPRSPrice(Double countPRSPrice) {
		this.countPRSPrice = countPRSPrice;
	}

	public String getKeepNumber() {
		return keepNumber;
	}

	public void setKeepNumber(String keepNumber) {
		this.keepNumber = keepNumber;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getNoField() {
		return noField;
	}

	public void setNoField(String noField) {
		this.noField = noField;
	}

	public String getCpiQuotationType() {
		return cpiQuotationType;
	}

	public void setCpiQuotationType(String cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}
	
}
