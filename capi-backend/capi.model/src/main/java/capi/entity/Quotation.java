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
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="Quotation")
public class Quotation extends capi.entity.EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="QuotationId")
	private Integer quotationId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UnitId", nullable = true)
	private Unit unit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OldUnitId", nullable = true)
	private Unit oldUnit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductId", nullable = true)
	private Product product;	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BatchId", nullable = true)
	private Batch batch;
	
	@Column(name="QuotationLoading")
	private Double quotationLoading;
	
	@Column(name="IndoorAllocationCode")
	private String indoorAllocationCode;
	
	@Column(name="IsICP")
	private boolean isICP;
	
	@Column(name="CPICompilationSeries")
	private String cpiCompilationSeries;
	
	@Column(name="OldFormBarSerial")
	private String oldFormBarSerial;
	
	@Column(name="OldFormSequence")
	private String oldFormSequence;
	
	// reset when season start
	@Column(name="FRAdmin")
	private Double frAdmin;
	
	// reset when season start
	@Column(name="FRField")
	private Double frField;
	
	@Column(name="IsUseFRAdmin")
	private Boolean isUseFRAdmin;
	
	// reset when season start
	@Column(name="SeasonalWithdrawal")
	private Date seasonalWithdrawal;
	
	@Column(name="LastFRAppliedDate")
	private Date lastFRAppliedDate;
	
	// reset when season start
	@Column(name="IsFRApplied")
	private boolean isFRApplied;
	
	// reset when season start
	@Column(name="IsReturnGoods")
	private boolean isReturnGoods;
	
	// reset when season start
	@Column(name="IsReturnNewGoods")
	private boolean isReturnNewGoods;
	
	/**
	 * RUA,Active,Inactive
	 */
	@Column(name="Status")
	private String status;
	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quotation")
	private Set<QuotationRecord> quotationRecords = new HashSet<QuotationRecord>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quotation")
	private Set<IndoorQuotationRecord> indoorQuotationRecords = new HashSet<IndoorQuotationRecord>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "quotations")
	private Set<PointToNote> pointToNotes = new HashSet<PointToNote>();
	
	@Column(name="UsedFRValue")
	private Double usedFRValue;
	
	@Column(name="IsUsedFRPercentage")
	private Boolean isUsedFRPercentage;
	
	@Column(name="IsFRAdminPercentage")
	private Boolean isFRAdminPercentage;
	
	@Column(name="IsFRFieldPercentage")
	private Boolean isFRFieldPercentage;

	@Column(name="RUADate")
	private Date ruaDate;
	
	@Column(name="IsRUAAllDistrict")
	private boolean isRUAAllDistrict;
	
	@Column(name="ICPProductCode")
	private String icpProductCode;
	
	@Column(name="ProductPosition")
	private String productPosition;
	
	@Column(name="ProductRemark")
	private String productRemark;
	
	@Column(name="LastProductChangeDate")
	private Date lastProductChangeDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DistrictId", nullable = true)
	private District district;
	
		
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "RUAUser", 
			joinColumns = { @JoinColumn(name = "QuotationId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "UserId", nullable = false, updatable = false) })
	private Set<User> users = new HashSet<User>();
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NewUnitId", nullable = true)
	private Unit newUnit;
	
	@Column(name="TempIsFRApplied")
	private Boolean tempIsFRApplied;
	
	@Column(name="TempIsReturnGoods")
	private Boolean tempIsReturnGoods;
	
	@Column(name="TempIsReturnNewGoods")
	private Boolean tempIsReturnNewGoods;
	
	@Column(name="TempLastFRAppliedDate")
	private Date tempLastFRAppliedDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NewProductId", nullable = true)
	private Product newProduct;
	
	@Column(name="TempIsUseFRAdmin")
	private Boolean tempIsUseFRAdmin;
	
	@Column(name="KeepNoMonth")
	private Integer keepNoMonth;
	
	@Column(name="TempKeepNoMonth")
	private Integer tempKeepNoMonth;
	
	@Column(name="LastSeasonReturnGoods")
	private boolean lastSeasonReturnGoods;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quotation")
	private Set<ImputeQuotation> imputeQuotations = new HashSet<ImputeQuotation>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quotation")
	private Set<QuotationStatistic> quotationStatistics = new HashSet<QuotationStatistic>();
	
	@Column(name="TempFRValue")	
	private Double tempFRValue;
	
	@Column(name="TempFRPercentage")	
	private Boolean tempFRPercentage;
	
	@Column(name="IcpType")	
	private String icpType;
	
	@Column(name="IcpProductName")	
	private String icpProductName;
	
	@Column(name="FormType")	
	private String formType;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getQuotationId();
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Set<QuotationRecord> getQuotationRecords() {
		return quotationRecords;
	}

	public void setQuotationRecords(Set<QuotationRecord> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Double getQuotationLoading() {
		return quotationLoading;
	}

	public void setQuotationLoading(Double quotationLoading) {
		this.quotationLoading = quotationLoading;
	}

	public String getIndoorAllocationCode() {
		return indoorAllocationCode;
	}

	public void setIndoorAllocationCode(String indoorAllocationCode) {
		this.indoorAllocationCode = indoorAllocationCode;
	}

	public boolean isICP() {
		return isICP;
	}

	public void setICP(boolean isICP) {
		this.isICP = isICP;
	}

	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}

	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
	}

	public String getOldFormBarSerial() {
		return oldFormBarSerial;
	}

	public void setOldFormBarSerial(String oldFormBarSerial) {
		this.oldFormBarSerial = oldFormBarSerial;
	}

	public String getOldFormSequence() {
		return oldFormSequence;
	}

	public void setOldFormSequence(String oldFormSequence) {
		this.oldFormSequence = oldFormSequence;
	}

	public Double getFrAdmin() {
		return frAdmin;
	}

	public void setFrAdmin(Double frAdmin) {
		this.frAdmin = frAdmin;
	}

	public Double getFrField() {
		return frField;
	}

	public void setFrField(Double frField) {
		this.frField = frField;
	}

	public Boolean isUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public Date getSeasonalWithdrawal() {
		return seasonalWithdrawal;
	}

	public void setSeasonalWithdrawal(Date seasonalWithdrawal) {
		this.seasonalWithdrawal = seasonalWithdrawal;
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}

	public Set<PointToNote> getPointToNotes() {
		return pointToNotes;
	}

	public void setPointToNotes(Set<PointToNote> pointToNotes) {
		this.pointToNotes = pointToNotes;
	}

	public boolean isFRApplied() {
		return isFRApplied;
	}

	public void setFRApplied(boolean isFRApplied) {
		this.isFRApplied = isFRApplied;
	}

	public boolean isReturnGoods() {
		return isReturnGoods;
	}

	public void setReturnGoods(boolean isReturnGoods) {
		this.isReturnGoods = isReturnGoods;
	}

	public boolean isReturnNewGoods() {
		return isReturnNewGoods;
	}

	public void setReturnNewGoods(boolean isReturnNewGoods) {
		this.isReturnNewGoods = isReturnNewGoods;
	}

	public Unit getOldUnit() {
		return oldUnit;
	}

	public void setOldUnit(Unit oldUnit) {
		this.oldUnit = oldUnit;
	}

	public Boolean getIsUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setIsUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public Double getUsedFRValue() {
		return usedFRValue;
	}

	public void setUsedFRValue(Double usedFRValue) {
		this.usedFRValue = usedFRValue;
	}

	public Boolean getIsUsedFRPercentage() {
		return isUsedFRPercentage;
	}

	public void setIsUsedFRPercentage(Boolean isUsedFRPercentage) {
		this.isUsedFRPercentage = isUsedFRPercentage;
	}

	public Boolean getIsFRAdminPercentage() {
		return isFRAdminPercentage;
	}

	public void setIsFRAdminPercentage(Boolean isFRAdminPercentage) {
		this.isFRAdminPercentage = isFRAdminPercentage;
	}

	public Boolean getIsFRFieldPercentage() {
		return isFRFieldPercentage;
	}

	public void setIsFRFieldPercentage(Boolean isFRFieldPercentage) {
		this.isFRFieldPercentage = isFRFieldPercentage;
	}

	public Date getRuaDate() {
		return ruaDate;
	}

	public void setRuaDate(Date ruaMonth) {
		this.ruaDate = ruaMonth;
	}

	public boolean isRUAAllDistrict() {
		return isRUAAllDistrict;
	}

	public void setRUAAllDistrict(boolean isRUAAllDistrict) {
		this.isRUAAllDistrict = isRUAAllDistrict;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getIcpProductCode() {
		return icpProductCode;
	}

	public void setIcpProductCode(String icpProductCode) {
		this.icpProductCode = icpProductCode;
	}

	public Set<IndoorQuotationRecord> getIndoorQuotationRecords() {
		return indoorQuotationRecords;
	}

	public void setIndoorQuotationRecords(
			Set<IndoorQuotationRecord> indoorQuotationRecords) {
		this.indoorQuotationRecords = indoorQuotationRecords;
	}

	public String getProductPosition() {
		return productPosition;
	}

	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
	}

	public String getProductRemark() {
		return productRemark;
	}

	public void setProductRemark(String productRemark) {
		this.productRemark = productRemark;
	}

	public Unit getNewUnit() {
		return newUnit;
	}

	public void setNewUnit(Unit newUnit) {
		this.newUnit = newUnit;
	}

	public Date getLastProductChangeDate() {
		return lastProductChangeDate;
	}

	public void setLastProductChangeDate(Date lastProductChangeDate) {
		this.lastProductChangeDate = lastProductChangeDate;
	}

	public Boolean isTempIsFRApplied() {
		return tempIsFRApplied;
	}

	public void setTempIsFRApplied(Boolean tempIsFRApplied) {
		this.tempIsFRApplied = tempIsFRApplied;
	}

	public Boolean isTempIsReturnGoods() {
		return tempIsReturnGoods;
	}

	public void setTempIsReturnGoods(Boolean tempIsReturnGoods) {
		this.tempIsReturnGoods = tempIsReturnGoods;
	}

	public Boolean isTempIsReturnNewGoods() {
		return tempIsReturnNewGoods;
	}

	public void setTempIsReturnNewGoods(Boolean tempIsReturnNewGoods) {
		this.tempIsReturnNewGoods = tempIsReturnNewGoods;
	}

	public Date getTempLastFRAppliedDate() {
		return tempLastFRAppliedDate;
	}

	public void setTempLastFRAppliedDate(Date tempLastFRAppliedDate) {
		this.tempLastFRAppliedDate = tempLastFRAppliedDate;
	}

	public Product getNewProduct() {
		return newProduct;
	}

	public void setNewProduct(Product newProduct) {
		this.newProduct = newProduct;
	}

	public Boolean isTempIsUseFRAdmin() {
		return tempIsUseFRAdmin;
	}

	public void setTempIsUseFRAdmin(Boolean tempIsUseFRAdmin) {
		this.tempIsUseFRAdmin = tempIsUseFRAdmin;
	}

	public Boolean getTempIsFRApplied() {
		return tempIsFRApplied;
	}

	public Boolean getTempIsReturnGoods() {
		return tempIsReturnGoods;
	}

	public Boolean getTempIsReturnNewGoods() {
		return tempIsReturnNewGoods;
	}

	public Integer getKeepNoMonth() {
		return keepNoMonth;
	}

	public void setKeepNoMonth(Integer keepNoMonth) {
		this.keepNoMonth = keepNoMonth;
	}

	public Integer getTempKeepNoMonth() {
		return tempKeepNoMonth;
	}

	public void setTempKeepNoMonth(Integer tempKeepNoMonth) {
		this.tempKeepNoMonth = tempKeepNoMonth;
	}

	public boolean isLastSeasonReturnGoods() {
		return lastSeasonReturnGoods;
	}

	public void setLastSeasonReturnGoods(boolean lastSeasonReturnGoods) {
		this.lastSeasonReturnGoods = lastSeasonReturnGoods;
	}


	public Double getTempFRValue() {
		return tempFRValue;
	}

	public void setTempFRValue(Double tempFRValue) {
		this.tempFRValue = tempFRValue;
	}

	public Boolean isTempFRPercentage() {
		return tempFRPercentage;
	}

	public void setTempFRPercentage(Boolean tempFRPercentage) {
		this.tempFRPercentage = tempFRPercentage;
	}

	public Set<ImputeQuotation> getImputeQuotations() {
		return imputeQuotations;
	}

	public void setImputeQuotations(Set<ImputeQuotation> imputeQuotations) {
		this.imputeQuotations = imputeQuotations;
	}

	public Set<QuotationStatistic> getQuotationStatistics() {
		return quotationStatistics;
	}

	public void setQuotationStatistics(Set<QuotationStatistic> quotationStatistics) {
		this.quotationStatistics = quotationStatistics;
	}

	public String getIcpType() {
		return icpType;
	}

	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}

	public String getIcpProductName() {
		return icpProductName;
	}

	public void setIcpProductName(String icpProductName) {
		this.icpProductName = icpProductName;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

}
