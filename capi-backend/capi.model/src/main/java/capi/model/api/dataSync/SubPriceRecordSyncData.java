package capi.model.api.dataSync;

import java.util.Date;

public class SubPriceRecordSyncData {

	private Integer subPriceRecordId;
	
	private Integer subPriceTypeId;
	
	private Integer sequence;
	
	private Integer quotationRecordId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Double sPrice;
	
	private Double nPrice;
	
	private String discount;
	
	private Integer localId;
	
	private String localDbRecordStatus;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((nPrice == null) ? 0 : nPrice.hashCode());
		result = prime * result + ((quotationRecordId == null) ? 0 : quotationRecordId.hashCode());
		result = prime * result + ((sPrice == null) ? 0 : sPrice.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + ((subPriceRecordId == null) ? 0 : subPriceRecordId.hashCode());
		result = prime * result + ((subPriceTypeId == null) ? 0 : subPriceTypeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubPriceRecordSyncData other = (SubPriceRecordSyncData) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (discount == null) {
			if (other.discount != null)
				return false;
		} else if (!discount.equals(other.discount))
			return false;
		if (localDbRecordStatus == null) {
			if (other.localDbRecordStatus != null)
				return false;
		} else if (!localDbRecordStatus.equals(other.localDbRecordStatus))
			return false;
		if (localId == null) {
			if (other.localId != null)
				return false;
		} else if (!localId.equals(other.localId))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (nPrice == null) {
			if (other.nPrice != null)
				return false;
		} else if (!nPrice.equals(other.nPrice))
			return false;
		if (quotationRecordId == null) {
			if (other.quotationRecordId != null)
				return false;
		} else if (!quotationRecordId.equals(other.quotationRecordId))
			return false;
		if (sPrice == null) {
			if (other.sPrice != null)
				return false;
		} else if (!sPrice.equals(other.sPrice))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (subPriceRecordId == null) {
			if (other.subPriceRecordId != null)
				return false;
		} else if (!subPriceRecordId.equals(other.subPriceRecordId))
			return false;
		if (subPriceTypeId == null) {
			if (other.subPriceTypeId != null)
				return false;
		} else if (!subPriceTypeId.equals(other.subPriceTypeId))
			return false;
		return true;
	}
	
}
