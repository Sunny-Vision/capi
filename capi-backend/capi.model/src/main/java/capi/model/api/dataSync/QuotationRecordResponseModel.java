package capi.model.api.dataSync;

import java.util.List;

public class QuotationRecordResponseModel {

	private List<Integer> skipIds;
	
	private List<QuotationRecordSyncData> quotationRecords;

	public List<Integer> getSkipIds() {
		return skipIds;
	}

	public void setSkipIds(List<Integer> skipIds) {
		this.skipIds = skipIds;
	}

	public List<QuotationRecordSyncData> getQuotationRecords() {
		return quotationRecords;
	}

	public void setQuotationRecords(List<QuotationRecordSyncData> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}
	
	
}
