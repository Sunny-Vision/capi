package capi.model.api.onlineFunction;

import java.util.List;
import java.util.Map;

public class AssignmentDataReturn {

	private Map<Integer, Integer> outletIdMap;  // localId, outletId
	private Map<Integer, Integer> firmCodeMap;  // outletId, firmCode
	private Map<Integer, Integer> productIdMap; // localId, productId
	private Map<Integer, Integer> productSpecIdMap; // localId, productSpecId
	private List<Integer> skipIds;
	public Map<Integer, Integer> getOutletIdMap() {
		return outletIdMap;
	}
	public void setOutletIdMap(Map<Integer, Integer> outletIdMap) {
		this.outletIdMap = outletIdMap;
	}
	public Map<Integer, Integer> getProductIdMap() {
		return productIdMap;
	}
	public void setProductIdMap(Map<Integer, Integer> productIdMap) {
		this.productIdMap = productIdMap;
	}
	public Map<Integer, Integer> getProductSpecIdMap() {
		return productSpecIdMap;
	}
	public void setProductSpecIdMap(Map<Integer, Integer> productSpecIdMap) {
		this.productSpecIdMap = productSpecIdMap;
	}
	public Map<Integer, Integer> getFirmCodeMap() {
		return firmCodeMap;
	}
	public void setFirmCodeMap(Map<Integer, Integer> firmCodeMap) {
		this.firmCodeMap = firmCodeMap;
	}
	public List<Integer> getSkipIds() {
		return skipIds;
	}
	public void setSkipIds(List<Integer> skipIds) {
		this.skipIds = skipIds;
	}
	
}
