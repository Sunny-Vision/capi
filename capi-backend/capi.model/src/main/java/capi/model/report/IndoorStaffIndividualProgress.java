package capi.model.report;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IndoorStaffIndividualProgress {

	private String username;
	
	private String referenceMonth;
	
	private String purpose;
	
	private String cpiBasePeriod;
	
	private String groupCode;
	
	private String groupChineseName;
	
	private String groupEnglishName;
	
	private String subGroupCode;
	
	private String subGroupChineseName;
	
	private String subGroupEnglishName;
	
	private Long allocator;
	
	private Long conversion;
	
	private Long complete;
	
	private Long verification;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
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

	public String getGroupChineseName() {
		return groupChineseName;
	}

	public void setGroupChineseName(String groupChineseName) {
		this.groupChineseName = groupChineseName;
	}

	public String getGroupEnglishName() {
		return groupEnglishName;
	}

	public void setGroupEnglishName(String groupEnglishName) {
		this.groupEnglishName = groupEnglishName;
	}

	public String getSubGroupCode() {
		return subGroupCode;
	}

	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}

	public String getSubGroupChineseName() {
		return subGroupChineseName;
	}

	public void setSubGroupChineseName(String subGroupChineseName) {
		this.subGroupChineseName = subGroupChineseName;
	}

	public String getSubGroupEnglishName() {
		return subGroupEnglishName;
	}

	public void setSubGroupEnglishName(String subGroupEnglishName) {
		this.subGroupEnglishName = subGroupEnglishName;
	}

	public Long getAllocator() {
		return allocator;
	}

	public void setAllocator(Long allocator) {
		this.allocator = allocator;
	}

	public Long getConversion() {
		return conversion;
	}

	public void setConversion(Long conversion) {
		this.conversion = conversion;
	}

	public Long getComplete() {
		return complete;
	}

	public void setComplete(Long complete) {
		this.complete = complete;
	}

	public Long getVerification() {
		return verification;
	}

	public void setVerification(Long verification) {
		this.verification = verification;
	}	
	
	public double getPercentageOustanding() {
		if(getAllocator() > 0){
			BigDecimal number = BigDecimal.valueOf(getConversion()).divide(BigDecimal.valueOf(getAllocator()), 5, RoundingMode.HALF_UP);
			return number.multiply(BigDecimal.valueOf(100)).doubleValue();
		}
		return 0;
	}
	
	public double getPercentageComplete() {		
		if(getAllocator() > 0){
			BigDecimal number = BigDecimal.valueOf(getComplete()).divide(BigDecimal.valueOf(getAllocator()), 5, RoundingMode.HALF_UP);
			return number.multiply(BigDecimal.valueOf(100)).doubleValue();		
		}
		return 0;
	}
}
