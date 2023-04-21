package capi.model.masterMaintenance;

public class UOMConversionEditModel {

	private Integer uomConversionId;

	private Integer baseUOMId;

	private String baseUOMChineseName;
	
	private Integer targetUOMId;

	private String targetUOMChineseName;

	private Double factor;
	
	private String createdDate;
	
	private String modifiedDate;

	public Integer getId() {
		return uomConversionId;
	}
	
	public Integer getUomConversionId() {
		return uomConversionId;
	}

	public void setUomConversionId(Integer uomConversionId) {
		this.uomConversionId = uomConversionId;
	}

	public Integer getBaseUOMId() {
		return baseUOMId;
	}

	public void setBaseUOMId(Integer baseUOMId) {
		this.baseUOMId = baseUOMId;
	}

	public String getBaseUOMChineseName() {
		return baseUOMChineseName;
	}

	public void setBaseUOMChineseName(String baseUOMChineseName) {
		this.baseUOMChineseName = baseUOMChineseName;
	}

	public Integer getTargetUOMId() {
		return targetUOMId;
	}

	public void setTargetUOMId(Integer targetUOMId) {
		this.targetUOMId = targetUOMId;
	}

	public String getTargetUOMChineseName() {
		return targetUOMChineseName;
	}

	public void setTargetUOMChineseName(String targetUOMChineseName) {
		this.targetUOMChineseName = targetUOMChineseName;
	}

	public Double getFactor() {
		return factor;
	}

	public void setFactor(Double factor) {
		this.factor = factor;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	
}
