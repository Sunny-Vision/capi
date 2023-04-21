package capi.model.masterMaintenance;



public class UOMConversionTableList {
	
	private Integer uomConversionId;
	
	private String baseUOM;
	
	private String targetUOM;
	
	private Double factor;
	
	public Integer getId() {
		return uomConversionId;
	}

	public Integer getUomConversionId() {
		return uomConversionId;
	}

	public void setUomConversionId(Integer uomConversionId) {
		this.uomConversionId = uomConversionId;
	}

	public String getBaseUOM() {
		return baseUOM;
	}

	public void setBaseUOM(String baseUOM) {
		this.baseUOM = baseUOM;
	}

	public String getTargetUOM() {
		return targetUOM;
	}

	public void setTargetUOM(String targetUOM) {
		this.targetUOM = targetUOM;
	}

	public Double getFactor() {
		return factor;
	}

	public void setFactor(Double factor) {
		this.factor = factor;
	}


	
}
