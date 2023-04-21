package capi.model.masterMaintenance;

public class SubPriceTypeModel {
	private Integer subPriceTypeId;
	
	private String numeratorFormula;
	
	private String denominatorFormula;
	
	private String category;
	
	private String name;
	
	private String status;	
	
	private String groupByField;
	
	private String dividedByField;
	
	private boolean hideNPrice;
	
	private boolean hideSPrice;
	
	private boolean hideDiscount;
	
	private boolean useMaxNPrice;

	private boolean useMaxSPrice;
	
	private boolean useMinNPrice;

	private boolean useMinSPrice;

	public Integer getId() {
		return subPriceTypeId;
	}
	
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

	public String getGroupByField() {
		return groupByField;
	}

	public void setGroupByField(String groupByField) {
		this.groupByField = groupByField;
	}

	public String getDividedByField() {
		return dividedByField;
	}

	public void setDividedByField(String dividedByField) {
		this.dividedByField = dividedByField;
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
