package capi.model.shared.quotationRecord;

import capi.entity.Outlet;

public class OutletViewModel extends Outlet {
	private String outletTypeFormatted;
	private String districtFormatted;
	private Integer districtId;
	private String quotationRecordOutletType;

	public String getOutletTypeFormatted() {
		return outletTypeFormatted;
	}

	public void setOutletTypeFormatted(String outletTypeFormatted) {
		this.outletTypeFormatted = outletTypeFormatted;
	}

	public String getDistrictFormatted() {
		return districtFormatted;
	}

	public void setDistrictFormatted(String districtFormatted) {
		this.districtFormatted = districtFormatted;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getQuotationRecordOutletType() {
		return quotationRecordOutletType;
	}

	public void setQuotationRecordOutletType(String quotationRecordOutletType) {
		this.quotationRecordOutletType = quotationRecordOutletType;
	}
}
