package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.SubPriceColumnSyncData;

public class SubPriceRecordOnlineModel {

	private Integer subPriceRecordId;
	
	private Integer subPriceTypeId;
	
	private Integer sequence;
	
	private Integer quotationRecordId;
	
	private Double sPrice;
	
	private Double nPrice;
	
	private String discount;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	private List<SubPriceColumnSyncData> subPriceColumns; 

	public Integer getSubPriceRecordId() {
		return subPriceRecordId;
	}

	public void setSubPriceRecordId(Integer subPriceRecordId) {
		this.subPriceRecordId = subPriceRecordId;
	}

	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}

	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public Double getsPrice() {
		return sPrice;
	}

	public void setsPrice(Double sPrice) {
		this.sPrice = sPrice;
	}

	public Double getnPrice() {
		return nPrice;
	}

	public void setnPrice(Double nPrice) {
		this.nPrice = nPrice;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public List<SubPriceColumnSyncData> getSubPriceColumns() {
		return subPriceColumns;
	}

	public void setSubPriceColumns(List<SubPriceColumnSyncData> subPriceColumns) {
		this.subPriceColumns = subPriceColumns;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
