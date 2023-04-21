package capi.model.report;

public class SupermarketProductReview {

	private Integer outletId;

	private String outletName;

	//private Integer quotationId;

	//private Integer indoorQuotationRecordId;

	//private Integer fieldQuotationRecordId;
	
	private String quotationId;

	private String indoorQuotationRecordId;

	private String fieldQuotationRecordId;
	
	private String referenceMonth;
	
	/*private Date referenceMonthD;
	
	private Date referenceDate;*/
	
    //private String referenceMonthD;
	
	private String referenceDate;
	
	private String unit;

	private Integer outletCode;

	private Integer productId;

	/*private String productAttributes1;

	private String productAttributes2;

	private String productAttributes3;

	private String productAttributes4;

	private String productAttributes5;

	private String productAttributes6;
	
	private String productAttributes7;
	
	private String productAttributes8;
	
	private String productAttributes9;
	
	private String productAttributes10;
	
	private String productAttributes11;
	
	private String productAttributes12;
	
	private String productAttributes13;
	
	private String productAttributes14;

	private String productAttributes15;
	
	private String productAttributes16;
	
	private String productAttributes17;
	
	private String productAttributes18;*/
	
	private String productAttributesName1;
	private String productAttributesName2;
	private String productAttributesName3;
	private String productAttributesName4;
	private String productAttributesName5;
	private String productAttributesName6;
	private String productAttributesName7;
	private String productAttributesName8;
	private String productAttributesName9;
	private String productAttributesName10;
	private String productAttributesName11;
	private String productAttributesName12;
	private String productAttributesName13;
	private String productAttributesName14;
	private String productAttributesName15;
	private String productAttributesName16;
	private String productAttributesName17;
	private String productAttributesName18;

	private String productAttributesValue1;
	private String productAttributesValue2;
	private String productAttributesValue3;
	private String productAttributesValue4;
	private String productAttributesValue5;
	private String productAttributesValue6;
	private String productAttributesValue7;
	private String productAttributesValue8;
	private String productAttributesValue9;
	private String productAttributesValue10;
	private String productAttributesValue11;
	private String productAttributesValue12;
	private String productAttributesValue13;
	private String productAttributesValue14;
	private String productAttributesValue15;
	private String productAttributesValue16;
	private String productAttributesValue17;
	private String productAttributesValue18;
	
	private Integer availability;

	private String productNotAvailableFrom;

	private String productRemarks;

	private Double surveyNPrice;

	private Double surveySPrice;

	private Double editedNPrice;

	private Double editedSPrice;

	private Double pr;

	private String priceRemarks;

	private String freshItem;
	
	private String purpose;
	
	private String cpiBasePeriod;
	
	private String groupCode;
	
	private String groupName;

	private String itemCode;
	
	private String itemName;
	
	private String varietyCode;
	
	private String varietyChineseName;
	
	private String varietyEnglishName;
	
	private String quotationStatus;
	
	private String dataConversionStatus;
	
	private String outletTypeCode;
	
	private String outletTypeEnglishName;
	
	private String countryOfOrigin;
	
	private Boolean isProductNotavailable;
	
	//private Date prodNotAvailableFrom;
	
	private String prodNotAvailableFrom;
	
	private String uomName;
	
	private Double uomValue;
	
	private Double lastNPrice;
	
	private Double lastSPrice;
	
	private Double previousNPrice;
	
	private Double previousSPrice;
	
	private Double currentNPrice;
	
	private Double currentSPrice;
	
	private Double qsAverageCurrentSPrice;
	
	private Double qsAverageLastSPrice;
	
	private Double qsLastHasPriceAverageCurrentSPrice;
	
	private Double usAverageCurrentSPrice;
	
	private Double usAverageLastSPrice;
	
	private Double usLastHasPriceAverageCurrentSPrice;
	
	private String fieldReason;
	
	private Boolean isNoField;
	
	private String dataConversionRemark;
	
	private Boolean isFreshItem;
	
	private Integer compilationMethod;
	
	private String fieldOfficerCode;
	
	private String indoorOfficer;
	
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

	public String getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}

	public String getIndoorQuotationRecordId() {
		return indoorQuotationRecordId;
	}

	public void setIndoorQuotationRecordId(String indoorQuotationRecordId) {
		this.indoorQuotationRecordId = indoorQuotationRecordId;
	}

	public String getFieldQuotationRecordId() {
		return fieldQuotationRecordId;
	}

	public void setFieldQuotationRecordId(String fieldQuotationRecordId) {
		this.fieldQuotationRecordId = fieldQuotationRecordId;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	/*public String getReferenceMonthD() {
		return referenceMonthD;
	}

	public void setReferenceMonthD(String referenceMonthD) {
		this.referenceMonthD = referenceMonthD;
	}*/

	public String getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(String referenceDate) {
		this.referenceDate = referenceDate;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(Integer outletCode) {
		this.outletCode = outletCode;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	/*public String getProductAttributes1() {
		return productAttributes1;
	}

	public void setProductAttributes1(String productAttributes1) {
		this.productAttributes1 = productAttributes1;
	}

	public String getProductAttributes2() {
		return productAttributes2;
	}

	public void setProductAttributes2(String productAttributes2) {
		this.productAttributes2 = productAttributes2;
	}

	public String getProductAttributes3() {
		return productAttributes3;
	}

	public void setProductAttributes3(String productAttributes3) {
		this.productAttributes3 = productAttributes3;
	}

	public String getProductAttributes4() {
		return productAttributes4;
	}

	public void setProductAttributes4(String productAttributes4) {
		this.productAttributes4 = productAttributes4;
	}

	public String getProductAttributes5() {
		return productAttributes5;
	}

	public void setProductAttributes5(String productAttributes5) {
		this.productAttributes5 = productAttributes5;
	}

	public String getProductAttributes6() {
		return productAttributes6;
	}

	public void setProductAttributes6(String productAttributes6) {
		this.productAttributes6 = productAttributes6;
	}

	public String getProductAttributes7() {
		return productAttributes7;
	}

	public void setProductAttributes7(String productAttributes7) {
		this.productAttributes7 = productAttributes7;
	}

	public String getProductAttributes8() {
		return productAttributes8;
	}

	public void setProductAttributes8(String productAttributes8) {
		this.productAttributes8 = productAttributes8;
	}

	public String getProductAttributes9() {
		return productAttributes9;
	}

	public void setProductAttributes9(String productAttributes9) {
		this.productAttributes9 = productAttributes9;
	}

	public String getProductAttributes10() {
		return productAttributes10;
	}

	public void setProductAttributes10(String productAttributes10) {
		this.productAttributes10 = productAttributes10;
	}

	public String getProductAttributes11() {
		return productAttributes11;
	}

	public void setProductAttributes11(String productAttributes11) {
		this.productAttributes11 = productAttributes11;
	}

	public String getProductAttributes12() {
		return productAttributes12;
	}

	public void setProductAttributes12(String productAttributes12) {
		this.productAttributes12 = productAttributes12;
	}

	public String getProductAttributes13() {
		return productAttributes13;
	}

	public void setProductAttributes13(String productAttributes13) {
		this.productAttributes13 = productAttributes13;
	}

	public String getProductAttributes14() {
		return productAttributes14;
	}

	public void setProductAttributes14(String productAttributes14) {
		this.productAttributes14 = productAttributes14;
	}

	public String getProductAttributes15() {
		return productAttributes15;
	}

	public void setProductAttributes15(String productAttributes15) {
		this.productAttributes15 = productAttributes15;
	}

	public String getProductAttributes16() {
		return productAttributes16;
	}

	public void setProductAttributes16(String productAttributes16) {
		this.productAttributes16 = productAttributes16;
	}

	public String getProductAttributes17() {
		return productAttributes17;
	}

	public void setProductAttributes17(String productAttributes17) {
		this.productAttributes17 = productAttributes17;
	}

	public String getProductAttributes18() {
		return productAttributes18;
	}

	public void setProductAttributes18(String productAttributes18) {
		this.productAttributes18 = productAttributes18;
	}*/

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public String getProductNotAvailableFrom() {
		return productNotAvailableFrom;
	}

	public void setProductNotAvailableFrom(String productNotAvailableFrom) {
		this.productNotAvailableFrom = productNotAvailableFrom;
	}

	public String getProductRemarks() {
		return productRemarks;
	}

	public void setProductRemarks(String productRemarks) {
		this.productRemarks = productRemarks;
	}

	public Double getSurveyNPrice() {
		return surveyNPrice;
	}

	public void setSurveyNPrice(Double surveyNPrice) {
		this.surveyNPrice = surveyNPrice;
	}

	public Double getSurveySPrice() {
		return surveySPrice;
	}

	public void setSurveySPrice(Double surveySPrice) {
		this.surveySPrice = surveySPrice;
	}

	public Double getEditedNPrice() {
		return editedNPrice;
	}

	public void setEditedNPrice(Double editedNPrice) {
		this.editedNPrice = editedNPrice;
	}

	public Double getEditedSPrice() {
		return editedSPrice;
	}

	public void setEditedSPrice(Double editedSPrice) {
		this.editedSPrice = editedSPrice;
	}

	public Double getPr() {
		return pr;
	}

	public void setPr(Double pr) {
		this.pr = pr;
	}

	public String getPriceRemarks() {
		return priceRemarks;
	}

	public void setPriceRemarks(String priceRemarks) {
		this.priceRemarks = priceRemarks;
	}

	public String getFreshItem() {
		return freshItem;
	}

	public void setFreshItem(String freshItem) {
		this.freshItem = freshItem;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
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

	public String getVarietyCode() {
		return varietyCode;
	}

	public void setVarietyCode(String varietyCode) {
		this.varietyCode = varietyCode;
	}

	public String getVarietyChineseName() {
		return varietyChineseName;
	}

	public void setVarietyChineseName(String varietyChineseName) {
		this.varietyChineseName = varietyChineseName;
	}

	public String getVarietyEnglishName() {
		return varietyEnglishName;
	}

	public void setVarietyEnglishName(String varietyEnglishName) {
		this.varietyEnglishName = varietyEnglishName;
	}

	public String getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public String getDataConversionStatus() {
		return dataConversionStatus;
	}

	public void setDataConversionStatus(String dataConversionStatus) {
		this.dataConversionStatus = dataConversionStatus;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public String getOutletTypeEnglishName() {
		return outletTypeEnglishName;
	}

	public void setOutletTypeEnglishName(String outletTypeEnglishName) {
		this.outletTypeEnglishName = outletTypeEnglishName;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public Boolean isProductNotavailable() {
		return isProductNotavailable;
	}

	public void setProductNotavailable(Boolean isProductNotavailable) {
		this.isProductNotavailable = isProductNotavailable;
	}

	public String getProdNotAvailableFrom() {
		return prodNotAvailableFrom;
	}

	public void setProdNotAvailableFrom(String prodNotAvailableFrom) {
		this.prodNotAvailableFrom = prodNotAvailableFrom;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public Double getUomValue() {
		return uomValue;
	}

	public void setUomValue(Double uomValue) {
		this.uomValue = uomValue;
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

	public Double getQsAverageCurrentSPrice() {
		return qsAverageCurrentSPrice;
	}

	public void setQsAverageCurrentSPrice(Double qsAverageCurrentSPrice) {
		this.qsAverageCurrentSPrice = qsAverageCurrentSPrice;
	}

	public Double getQsAverageLastSPrice() {
		return qsAverageLastSPrice;
	}

	public void setQsAverageLastSPrice(Double qsAverageLastSPrice) {
		this.qsAverageLastSPrice = qsAverageLastSPrice;
	}

	public Double getQsLastHasPriceAverageCurrentSPrice() {
		return qsLastHasPriceAverageCurrentSPrice;
	}

	public void setQsLastHasPriceAverageCurrentSPrice(Double qsLastHasPriceAverageCurrentSPrice) {
		this.qsLastHasPriceAverageCurrentSPrice = qsLastHasPriceAverageCurrentSPrice;
	}

	public Double getUsAverageCurrentSPrice() {
		return usAverageCurrentSPrice;
	}

	public void setUsAverageCurrentSPrice(Double usAverageCurrentSPrice) {
		this.usAverageCurrentSPrice = usAverageCurrentSPrice;
	}

	public Double getUsAverageLastSPrice() {
		return usAverageLastSPrice;
	}

	public void setUsAverageLastSPrice(Double usAverageLastSPrice) {
		this.usAverageLastSPrice = usAverageLastSPrice;
	}

	public Double getUsLastHasPriceAverageCurrentSPrice() {
		return usLastHasPriceAverageCurrentSPrice;
	}

	public void setUsLastHasPriceAverageCurrentSPrice(Double usLastHasPriceAverageCurrentSPrice) {
		this.usLastHasPriceAverageCurrentSPrice = usLastHasPriceAverageCurrentSPrice;
	}

	public String getFieldReason() {
		return fieldReason;
	}

	public void setFieldReason(String fieldReason) {
		this.fieldReason = fieldReason;
	}

	public Boolean isNoField() {
		return isNoField;
	}

	public void setNoField(Boolean isNoField) {
		this.isNoField = isNoField;
	}

	public String getDataConversionRemark() {
		return dataConversionRemark;
	}

	public void setDataConversionRemark(String dataConversionRemark) {
		this.dataConversionRemark = dataConversionRemark;
	}

	public Boolean isFreshItem() {
		return isFreshItem;
	}

	public void setFreshItem(Boolean isFreshItem) {
		this.isFreshItem = isFreshItem;
	}

	public Integer getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}

	public String getFieldOfficerCode() {
		return fieldOfficerCode;
	}

	public void setFieldOfficerCode(String fieldOfficerCode) {
		this.fieldOfficerCode = fieldOfficerCode;
	}

	public String getIndoorOfficer() {
		return indoorOfficer;
	}

	public void setIndoorOfficer(String indoorOfficer) {
		this.indoorOfficer = indoorOfficer;
	}

	public String getProductAttributesName1() {
		return productAttributesName1;
	}

	public void setProductAttributesName1(String productAttributesName1) {
		this.productAttributesName1 = productAttributesName1;
	}

	public String getProductAttributesName2() {
		return productAttributesName2;
	}

	public void setProductAttributesName2(String productAttributesName2) {
		this.productAttributesName2 = productAttributesName2;
	}

	public String getProductAttributesName3() {
		return productAttributesName3;
	}

	public void setProductAttributesName3(String productAttributesName3) {
		this.productAttributesName3 = productAttributesName3;
	}

	public String getProductAttributesName4() {
		return productAttributesName4;
	}

	public void setProductAttributesName4(String productAttributesName4) {
		this.productAttributesName4 = productAttributesName4;
	}

	public String getProductAttributesName5() {
		return productAttributesName5;
	}

	public void setProductAttributesName5(String productAttributesName5) {
		this.productAttributesName5 = productAttributesName5;
	}

	public String getProductAttributesName6() {
		return productAttributesName6;
	}

	public void setProductAttributesName6(String productAttributesName6) {
		this.productAttributesName6 = productAttributesName6;
	}

	public String getProductAttributesName7() {
		return productAttributesName7;
	}

	public void setProductAttributesName7(String productAttributesName7) {
		this.productAttributesName7 = productAttributesName7;
	}

	public String getProductAttributesName8() {
		return productAttributesName8;
	}

	public void setProductAttributesName8(String productAttributesName8) {
		this.productAttributesName8 = productAttributesName8;
	}

	public String getProductAttributesName9() {
		return productAttributesName9;
	}

	public void setProductAttributesName9(String productAttributesName9) {
		this.productAttributesName9 = productAttributesName9;
	}

	public String getProductAttributesName10() {
		return productAttributesName10;
	}

	public void setProductAttributesName10(String productAttributesName10) {
		this.productAttributesName10 = productAttributesName10;
	}

	public String getProductAttributesName11() {
		return productAttributesName11;
	}

	public void setProductAttributesName11(String productAttributesName11) {
		this.productAttributesName11 = productAttributesName11;
	}

	public String getProductAttributesName12() {
		return productAttributesName12;
	}

	public void setProductAttributesName12(String productAttributesName12) {
		this.productAttributesName12 = productAttributesName12;
	}

	public String getProductAttributesName13() {
		return productAttributesName13;
	}

	public void setProductAttributesName13(String productAttributesName13) {
		this.productAttributesName13 = productAttributesName13;
	}

	public String getProductAttributesName14() {
		return productAttributesName14;
	}

	public void setProductAttributesName14(String productAttributesName14) {
		this.productAttributesName14 = productAttributesName14;
	}

	public String getProductAttributesName15() {
		return productAttributesName15;
	}

	public void setProductAttributesName15(String productAttributesName15) {
		this.productAttributesName15 = productAttributesName15;
	}

	public String getProductAttributesName16() {
		return productAttributesName16;
	}

	public void setProductAttributesName16(String productAttributesName16) {
		this.productAttributesName16 = productAttributesName16;
	}

	public String getProductAttributesName17() {
		return productAttributesName17;
	}

	public void setProductAttributesName17(String productAttributesName17) {
		this.productAttributesName17 = productAttributesName17;
	}

	public String getProductAttributesName18() {
		return productAttributesName18;
	}

	public void setProductAttributesName18(String productAttributesName18) {
		this.productAttributesName18 = productAttributesName18;
	}

	public String getProductAttributesValue1() {
		return productAttributesValue1;
	}

	public void setProductAttributesValue1(String productAttributesValue1) {
		this.productAttributesValue1 = productAttributesValue1;
	}

	public String getProductAttributesValue2() {
		return productAttributesValue2;
	}

	public void setProductAttributesValue2(String productAttributesValue2) {
		this.productAttributesValue2 = productAttributesValue2;
	}

	public String getProductAttributesValue3() {
		return productAttributesValue3;
	}

	public void setProductAttributesValue3(String productAttributesValue3) {
		this.productAttributesValue3 = productAttributesValue3;
	}

	public String getProductAttributesValue4() {
		return productAttributesValue4;
	}

	public void setProductAttributesValue4(String productAttributesValue4) {
		this.productAttributesValue4 = productAttributesValue4;
	}

	public String getProductAttributesValue5() {
		return productAttributesValue5;
	}

	public void setProductAttributesValue5(String productAttributesValue5) {
		this.productAttributesValue5 = productAttributesValue5;
	}

	public String getProductAttributesValue6() {
		return productAttributesValue6;
	}

	public void setProductAttributesValue6(String productAttributesValue6) {
		this.productAttributesValue6 = productAttributesValue6;
	}

	public String getProductAttributesValue7() {
		return productAttributesValue7;
	}

	public void setProductAttributesValue7(String productAttributesValue7) {
		this.productAttributesValue7 = productAttributesValue7;
	}

	public String getProductAttributesValue8() {
		return productAttributesValue8;
	}

	public void setProductAttributesValue8(String productAttributesValue8) {
		this.productAttributesValue8 = productAttributesValue8;
	}

	public String getProductAttributesValue9() {
		return productAttributesValue9;
	}

	public void setProductAttributesValue9(String productAttributesValue9) {
		this.productAttributesValue9 = productAttributesValue9;
	}

	public String getProductAttributesValue10() {
		return productAttributesValue10;
	}

	public void setProductAttributesValue10(String productAttributesValue10) {
		this.productAttributesValue10 = productAttributesValue10;
	}

	public String getProductAttributesValue11() {
		return productAttributesValue11;
	}

	public void setProductAttributesValue11(String productAttributesValue11) {
		this.productAttributesValue11 = productAttributesValue11;
	}

	public String getProductAttributesValue12() {
		return productAttributesValue12;
	}

	public void setProductAttributesValue12(String productAttributesValue12) {
		this.productAttributesValue12 = productAttributesValue12;
	}

	public String getProductAttributesValue13() {
		return productAttributesValue13;
	}

	public void setProductAttributesValue13(String productAttributesValue13) {
		this.productAttributesValue13 = productAttributesValue13;
	}

	public String getProductAttributesValue14() {
		return productAttributesValue14;
	}

	public void setProductAttributesValue14(String productAttributesValue14) {
		this.productAttributesValue14 = productAttributesValue14;
	}

	public String getProductAttributesValue15() {
		return productAttributesValue15;
	}

	public void setProductAttributesValue15(String productAttributesValue15) {
		this.productAttributesValue15 = productAttributesValue15;
	}

	public String getProductAttributesValue16() {
		return productAttributesValue16;
	}

	public void setProductAttributesValue16(String productAttributesValue16) {
		this.productAttributesValue16 = productAttributesValue16;
	}

	public String getProductAttributesValue17() {
		return productAttributesValue17;
	}

	public void setProductAttributesValue17(String productAttributesValue17) {
		this.productAttributesValue17 = productAttributesValue17;
	}

	public String getProductAttributesValue18() {
		return productAttributesValue18;
	}

	public void setProductAttributesValue18(String productAttributesValue18) {
		this.productAttributesValue18 = productAttributesValue18;
	}

}
