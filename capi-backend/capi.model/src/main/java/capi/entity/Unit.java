package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


@Entity
@Table(name="Unit",schema="DBO")
public class Unit extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UnitId")
	private Integer unitId;
	
	@Column(name="Code")
	private String code;
	
	@Column(name="ChineseName")
	private String chineseName;

	@Column(name="EnglishName")
	private String englishName;	
	
	@Column(name="ObsoleteDate")
	private Date obsoleteDate;
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;
	
	@Column(name="DisplayName")
	private String displayName;
	
	@Deprecated
	@Column(name="IsMRPS")
	private boolean isMRPS;
	
	@Column(name="MaxQuotation")
	private Integer maxQuotation;
	
	@Column(name="MinQuotation")
	private Integer minQuotation;
	
	@Column(name="UnitCategory")
	private String unitCategory;
	
	@Column(name="UOMValue")
	private Double uomValue;
	
	@Column(name="SpicingRequired")
	private boolean spicingRequired;
	
	@Column(name="FRRequired")
	private boolean frRequired;
	
	/**
	 * 1- All-time
	 * 2- Summer
	 * 3- Winter
	 * 4- Occasional
	 */
	@Column(name="Seasonality")
	private Integer seasonality;
	
	@Column(name="SeasonStartMonth")
	private Integer seasonStartMonth;
	
	@Column(name="SeasonEndMonth")
	private Integer seasonEndMonth;
	
	@Column(name="RTNPeriod")
	private Integer rtnPeriod;
	
	@Column(name="BackdateRequired")
	private boolean backdateRequired;
	
	@Column(name="AllowEditPMPrice")
	private boolean allowEditPMPrice;
	
	/**
	 * 1 - normal
	 * 2 - tour
	 */
	@Column(name="FormDisplay")
	private Integer formDisplay;
	
	/**
	 * Active, Inactive
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="CPIBasePeriod")
	private String cpiBasePeriod;
	
	@Column(name="CrossCheckGroup")
	private String crossCheckGroup;
	
	/**
	 * 1- Market, 
	 * 2- Supermarket, 
	 * 3- Batch, 
	 * 4- Others
	 */
	@Column(name="CPIQoutationType")
	private Integer cpiQoutationType;
	
	@Column(name="IsTemporary")
	private boolean isTemporary;
	
	@Column(name="IsNPriceMandatory")
	private boolean isNPriceMandatory;
	
	@Column(name="IsSPriceMandatory")
	private boolean isSPriceMandatory;
	
	@Column(name="RUAAllowed")
	private boolean ruaAllowed;
	
	@Column(name="IsFreshItem")
	private boolean isFreshItem;
	
	@Column(name="AllowProductChange")
	private boolean allowProductChange;

	@Column(name="ProductCycle")
	private Integer productCycle;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubItemId", nullable = true)
	private SubItem subItem;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unit")
	private Set<Quotation> quotations = new HashSet<Quotation>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "units")
	private Set<PointToNote> pointToNotes = new HashSet<PointToNote>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "UOMCategoryUnit", 
			joinColumns = { @JoinColumn(name = "UnitId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "UOMCategoryId", nullable = false, updatable = false) })
	private Set<UOMCategory> uomCategories = new HashSet<UOMCategory>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "StandardUOMId", nullable = true)
	private Uom standardUOM;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PricingFrequencyId", nullable = true)
	private PricingFrequency pricingFrequency;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceTypeId", nullable = true)
	private SubPriceType subPriceType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductCategoryId", nullable = true)
	private ProductGroup productCategory;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PurposeId", nullable = true)
	private Purpose purpose;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "unit")
	private OnSpotValidation onSpotValidation;
	
	
	@Column(name="DataTransmissionRule")
	private String dataTransmissionRule;
	
	
	@Column(name="ConsolidatedSPRMean")
	private Double consolidatedSPRMean;
	
	@Column(name="ConsolidatedSPRSD")
	private Double consolidatedSPRSD;
	
	@Column(name="ConsolidatedNPRMean")
	private Double consolidatedNPRMean;
	
	@Column(name="ConsolidatedNPRSD")
	private Double consolidatedNPRSD;
	
	@Column(name="ConvertAfterClosingDate")
	private boolean convertAfterClosingDate;
	
	@Column(name="IcpType")
	private String icpType;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unit")
	private Set<ImputeUnit> imputeUnits = new HashSet<ImputeUnit>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unit")
	private Set<UnitStatistic> unitStatistics = new HashSet<UnitStatistic>();

	public Integer getId(){
		return this.getUnitId();
	}
	
	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public SubItem getSubItem() {
		return subItem;
	}

	public void setSubItem(SubItem subItem) {
		this.subItem = subItem;
	}

	public Date getObsoleteDate() {
		return obsoleteDate;
	}

	public void setObsoleteDate(Date obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
	}

	public Set<Quotation> getQuotations() {
		return quotations;
	}

	public void setQuotations(Set<Quotation> quotations) {
		this.quotations = quotations;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Set<PointToNote> getPointToNotes() {
		return pointToNotes;
	}

	public void setPointToNotes(Set<PointToNote> pointToNotes) {
		this.pointToNotes = pointToNotes;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Deprecated
	public boolean isMRPS() {
		return isMRPS;
	}

	@Deprecated
	public void setMRPS(boolean isMRPS) {
		this.isMRPS = isMRPS;
	}

	public Integer getMaxQuotation() {
		return maxQuotation;
	}

	public void setMaxQuotation(Integer maxQuotation) {
		this.maxQuotation = maxQuotation;
	}

	public Integer getMinQuotation() {
		return minQuotation;
	}

	public void setMinQuotation(Integer minQuotation) {
		this.minQuotation = minQuotation;
	}

	public String getUnitCategory() {
		return unitCategory;
	}

	public void setUnitCategory(String unitCategory) {
		this.unitCategory = unitCategory;
	}

	public Double getUomValue() {
		return uomValue;
	}

	public void setUomValue(Double uomValue) {
		this.uomValue = uomValue;
	}

	public boolean isSpicingRequired() {
		return spicingRequired;
	}

	public void setSpicingRequired(boolean spicingRequired) {
		this.spicingRequired = spicingRequired;
	}

	public boolean isFrRequired() {
		return frRequired;
	}

	public void setFrRequired(boolean frRequired) {
		this.frRequired = frRequired;
	}

	public Integer getSeasonality() {
		return seasonality;
	}

	public void setSeasonality(Integer seasonality) {
		this.seasonality = seasonality;
	}

	public Integer getSeasonStartMonth() {
		return seasonStartMonth;
	}

	public void setSeasonStartMonth(Integer seasonStartMonth) {
		this.seasonStartMonth = seasonStartMonth;
	}

	public Integer getSeasonEndMonth() {
		return seasonEndMonth;
	}

	public void setSeasonEndMonth(Integer seasonEndMonth) {
		this.seasonEndMonth = seasonEndMonth;
	}

	public Integer getRtnPeriod() {
		return rtnPeriod;
	}

	public void setRtnPeriod(Integer rtnPeriod) {
		this.rtnPeriod = rtnPeriod;
	}

	public boolean isBackdateRequired() {
		return backdateRequired;
	}

	public void setBackdateRequired(boolean backdateRequired) {
		this.backdateRequired = backdateRequired;
	}

	public boolean isAllowEditPMPrice() {
		return allowEditPMPrice;
	}

	public void setAllowEditPMPrice(boolean allowEditPMPrice) {
		this.allowEditPMPrice = allowEditPMPrice;
	}

	public Integer getFormDisplay() {
		return formDisplay;
	}

	public void setFormDisplay(Integer formDisplay) {
		this.formDisplay = formDisplay;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getCrossCheckGroup() {
		return crossCheckGroup;
	}

	public void setCrossCheckGroup(String crossCheckGroup) {
		this.crossCheckGroup = crossCheckGroup;
	}

	public Integer getCpiQoutationType() {
		return cpiQoutationType;
	}

	public void setCpiQoutationType(Integer cpiQoutationType) {
		this.cpiQoutationType = cpiQoutationType;
	}

	public boolean isTemporary() {
		return isTemporary;
	}

	public void setTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	public boolean isNPriceMandatory() {
		return isNPriceMandatory;
	}

	public void setNPriceMandatory(boolean isNPriceMandatory) {
		this.isNPriceMandatory = isNPriceMandatory;
	}

	public boolean isSPriceMandatory() {
		return isSPriceMandatory;
	}

	public void setSPriceMandatory(boolean isSPriceMandatory) {
		this.isSPriceMandatory = isSPriceMandatory;
	}

	public Set<UOMCategory> getUomCategories() {
		return uomCategories;
	}

	public void setUomCategories(Set<UOMCategory> uomCategories) {
		this.uomCategories = uomCategories;
	}

	public Uom getStandardUOM() {
		return standardUOM;
	}

	public void setStandardUOM(Uom standardUOM) {
		this.standardUOM = standardUOM;
	}

	public PricingFrequency getPricingFrequency() {
		return pricingFrequency;
	}

	public void setPricingFrequency(PricingFrequency pricingFrequency) {
		this.pricingFrequency = pricingFrequency;
	}

	public SubPriceType getSubPriceType() {
		return subPriceType;
	}

	public void setSubPriceType(SubPriceType subPriceType) {
		this.subPriceType = subPriceType;
	}

	public ProductGroup getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductGroup productCategory) {
		this.productCategory = productCategory;
	}

	public Purpose getPurpose() {
		return purpose;
	}

	public void setPurpose(Purpose purpose) {
		this.purpose = purpose;
	}

	public boolean isRuaAllowed() {
		return ruaAllowed;
	}

	public void setRuaAllowed(boolean ruaAllowed) {
		this.ruaAllowed = ruaAllowed;
	}

	public boolean isFreshItem() {
		return isFreshItem;
	}

	public void setFreshItem(boolean isFreshItem) {
		this.isFreshItem = isFreshItem;
	}

	public boolean isAllowProductChange() {
		return allowProductChange;
	}

	public void setAllowProductChange(boolean allowProductChange) {
		this.allowProductChange = allowProductChange;
	}

	public Integer getProductCycle() {
		return productCycle;
	}

	public void setProductCycle(Integer productCycle) {
		this.productCycle = productCycle;
	}

	public OnSpotValidation getOnSpotValidation() {
		return onSpotValidation;
	}

	public void setOnSpotValidation(OnSpotValidation onSpotValidation) {
		this.onSpotValidation = onSpotValidation;
	}

	public String getDataTransmissionRule() {
		return dataTransmissionRule;
	}

	public void setDataTransmissionRule(String dataTransmissionRule) {
		this.dataTransmissionRule = dataTransmissionRule;
	}

	public Double getConsolidatedSPRMean() {
		return consolidatedSPRMean;
	}

	public void setConsolidatedSPRMean(Double consolidatedSPRMean) {
		this.consolidatedSPRMean = consolidatedSPRMean;
	}

	public Double getConsolidatedSPRSD() {
		return consolidatedSPRSD;
	}

	public void setConsolidatedSPRSD(Double consolidatedSPRSD) {
		this.consolidatedSPRSD = consolidatedSPRSD;
	}

	public Double getConsolidatedNPRMean() {
		return consolidatedNPRMean;
	}

	public void setConsolidatedNPRMean(Double consolidatedNPRMean) {
		this.consolidatedNPRMean = consolidatedNPRMean;
	}

	public Double getConsolidatedNPRSD() {
		return consolidatedNPRSD;
	}

	public void setConsolidatedNPRSD(Double consolidatedNPRSD) {
		this.consolidatedNPRSD = consolidatedNPRSD;
	}

	public boolean isConvertAfterClosingDate() {
		return convertAfterClosingDate;
	}

	public void setConvertAfterClosingDate(boolean convertAfterClosingDate) {
		this.convertAfterClosingDate = convertAfterClosingDate;
	}

	public Set<ImputeUnit> getImputeUnits() {
		return imputeUnits;
	}

	public void setImputeUnits(Set<ImputeUnit> imputeUnits) {
		this.imputeUnits = imputeUnits;
	}

	public String getIcpType() {
		return icpType;
	}

	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}

	public Set<UnitStatistic> getUnitStatistics() {
		return unitStatistics;
	}

	public void setUnitStatistics(Set<UnitStatistic> unitStatistics) {
		this.unitStatistics = unitStatistics;
	}



}
