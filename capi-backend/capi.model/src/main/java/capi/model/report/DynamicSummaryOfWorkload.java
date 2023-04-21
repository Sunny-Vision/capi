package capi.model.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Map.Entry;

public class DynamicSummaryOfWorkload {

	private Integer id;

	private Map<String,Double> districtAndSpent;

	private String outletTypeCode;

	private String outletTypeName;
	
	private Double average;
	
	private Integer count;
	
	public DynamicSummaryOfWorkload(SummaryOfWorkload data, Map<String, Double> innerMap, Integer count, Double average) {
		this.id = data.getId();
		this.outletTypeCode = data.getOutletTypeCode();
		this.outletTypeName = data.getOutletTypeName();
		this.districtAndSpent = innerMap;
		this.count = count;
		this.average = average;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Map<String, Double> getDistrictAndSpent() {
		return districtAndSpent;
	}

	public void setDistrictAndSpent(Map<String, Double> districtAndSpent) {
		this.districtAndSpent = districtAndSpent;
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

	public Double getAverage() {
		return average;
	}
	
	public void setAverage(Double average) {
		this.average = average;
	}
}
