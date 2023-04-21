package capi.model.dataConversion.quotationRecordDataConversion;

import java.util.Arrays;
import java.util.List;

public class PurposeIndoorQuotationRecordCountModel implements Comparable<PurposeIndoorQuotationRecordCountModel> {
	public static List<String> ordering = Arrays.asList("MRPS", "ICP", "WPU", "CEO");
	
	private Integer purposeId;
	private String purposeName;
	private Long countOfIndoorQuotationRecord;
	public Integer getPurposeId() {
		return purposeId; 
	}
	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}
	public String getPurposeName() {
		return purposeName;
	}
	public void setPurposeName(String purposeName) {
		this.purposeName = purposeName;
	}
	public Long getCountOfIndoorQuotationRecord() {
		return countOfIndoorQuotationRecord;
	}
	public void setCountOfIndoorQuotationRecord(Long countOfIndoorQuotationRecord) {
		this.countOfIndoorQuotationRecord = countOfIndoorQuotationRecord;
	}
	
	@Override
	public int compareTo(PurposeIndoorQuotationRecordCountModel o) {
		try {
			if (ordering.indexOf(this.getPurposeName()) > ordering.indexOf(o.getPurposeName()))
				return 1;
			else if (ordering.indexOf(this.getPurposeName()) < ordering.indexOf(o.getPurposeName()))
				return -1;
			else
				return 0;
		} catch (Exception e) {}
		return 0;
	}
}
