package capi.model.masterMaintenance;

public class OutletBatchCodeModel {
	private String key;
	private String value;
	private Boolean emptyOutlet;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getEmptyOutlet() {
		return emptyOutlet;
	}
	public void setEmptyOutlet(Boolean emptyOutlet) {
		this.emptyOutlet = emptyOutlet;
	}
}
