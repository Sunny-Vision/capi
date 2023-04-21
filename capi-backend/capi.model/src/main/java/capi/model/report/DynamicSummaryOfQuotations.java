package capi.model.report;

import java.util.Map;

public class DynamicSummaryOfQuotations {
	
	private String id;

	private String referenceMonth;

	private String purpose;
	
	private String outletTypeCode;
	
	private String outletTypeName;
	
	private Map<String, Integer> distrcitAndQuantity;
	
	private Integer total;

	public DynamicSummaryOfQuotations(SummaryOfQuotations data, Map<String, Integer> innerMap) {
		this.referenceMonth = data.getReferenceMonth();
		this.purpose = data.getPurpose();
		this.outletTypeCode = data.getOutletTypeCode();
		this.outletTypeName = data.getOutletTypeName();
		this.distrcitAndQuantity = innerMap;
		setTotal();
	}
	
	public String getId() {
		if(this.getReferenceMonth() != null && this.getOutletTypeCode()!=null){
			return this.getReferenceMonth() + this.getOutletTypeCode();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Map<String, Integer> getDistrcitAndQuantity() {
		return distrcitAndQuantity;
	}

	public void setDistrcitAndQuantity(Map<String, Integer> distrcitAndQuantity) {
		this.distrcitAndQuantity = distrcitAndQuantity;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal() {
		int i = 0;
		for(Map.Entry<String,Integer> entry : distrcitAndQuantity.entrySet()) {
			i+=entry.getValue();
		}
		this.total = i;
	}
	
}
