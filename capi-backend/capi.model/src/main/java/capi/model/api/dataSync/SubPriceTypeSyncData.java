package capi.model.api.dataSync;

import java.util.Date;

public class SubPriceTypeSyncData {
	private Integer subPriceTypeId;
	
	private String numeratorFormula;
	
	private String denominatorFormula;
	
	private String category;
	
	private Integer groupByFieldId;
	
	private Integer dividedByFieldId;
	
	private String name;
	
	private String status;
	
	private Date createdDate;
	
	private Date modifiedDate;

	private boolean hideNPrice;
	
	private boolean hideSPrice;
	
	private boolean hideDiscount;
	
	private boolean useMaxNPrice;

	private boolean useMaxSPrice;
	
	private boolean useMinNPrice;

	private boolean useMinSPrice;
	
	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}

	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}

	public String getNumeratorFormula() {
		return numeratorFormula;
	}

	public void setNumeratorFormula(String numeratorFormula) {
		this.numeratorFormula = numeratorFormula;
	}

	public String getDenominatorFormula() {
		return denominatorFormula;
	}

	public void setDenominatorFormula(String denominatorFormula) {
		this.denominatorFormula = denominatorFormula;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getGroupByFieldId() {
		return groupByFieldId;
	}

	public void setGroupByFieldId(Integer groupByFieldId) {
		this.groupByFieldId = groupByFieldId;
	}

	public Integer getDividedByFieldId() {
		return dividedByFieldId;
	}

	public void setDividedByFieldId(Integer dividedByFieldId) {
		this.dividedByFieldId = dividedByFieldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public boolean isHideNPrice() {
		return hideNPrice;
	}

	public void setHideNPrice(boolean hideNPrice) {
		this.hideNPrice = hideNPrice;
	}

	public boolean isHideSPrice() {
		return hideSPrice;
	}

	public void setHideSPrice(boolean hideSPrice) {
		this.hideSPrice = hideSPrice;
	}

	public boolean isHideDiscount() {
		return hideDiscount;
	}

	public void setHideDiscount(boolean hideDiscount) {
		this.hideDiscount = hideDiscount;
	}

	public boolean isUseMaxNPrice() {
		return useMaxNPrice;
	}

	public void setUseMaxNPrice(boolean useMaxNPrice) {
		this.useMaxNPrice = useMaxNPrice;
	}

	public boolean isUseMaxSPrice() {
		return useMaxSPrice;
	}

	public void setUseMaxSPrice(boolean useMaxSPrice) {
		this.useMaxSPrice = useMaxSPrice;
	}

	public boolean isUseMinNPrice() {
		return useMinNPrice;
	}

	public void setUseMinNPrice(boolean useMinNPrice) {
		this.useMinNPrice = useMinNPrice;
	}

	public boolean isUseMinSPrice() {
		return useMinSPrice;
	}

	public void setUseMinSPrice(boolean useMinSPrice) {
		this.useMinSPrice = useMinSPrice;
	}
	
}
