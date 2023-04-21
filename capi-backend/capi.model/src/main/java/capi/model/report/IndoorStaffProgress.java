package capi.model.report;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IndoorStaffProgress {

	private String referenceMonth;
	
	private String purpose;
	
	private String cpiBasePeriod;
	
	private String staffCode;
	
	private String name;
	
	private Long allocator;
	
	private Long conversion;
	
	private Long complete;
	
	private Long verification;

	public String getReferenceMonth() {
		String[] temp = referenceMonth.split("-");
		return temp[0]+temp[1];
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

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setComplete(Long completed) {
		this.complete = completed;
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
