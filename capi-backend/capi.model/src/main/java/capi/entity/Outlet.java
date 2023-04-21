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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="Outlet", uniqueConstraints = {@UniqueConstraint(columnNames = "FirmCode")})
public class Outlet  extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="OutletId")
	private Integer outletId;
		
	@Column(name="FirmCode")
	private Integer firmCode;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Tel")
	private String tel;
	
	@Column(name="DetailAddress")
	private String detailAddress;
	
	@Column(name="StreetAddress")
	private String streetAddress;
	
	@Column(name="MainContact")
	private String mainContact;
	
	@Column(name="LastContact")
	private String lastContact;
	
	@Column(name="Fax")
	private String fax;
	
	@Column(name="WebSite")
	private String webSite;
	
	@Column(name="MarketName")
	private String marketName;
	
	@Column(name="Remark")
	private String remark;
	
	@Column(name="DiscountRemark")
	private String discountRemark;
	
	@Column(name="Latitude")
	private String latitude;
	
	@Column(name="Longitude")
	private String longitude;
		
	@Column(name="OutletImagePath")
	private String outletImagePath;
	
	@Column(name="ImageModifiedTime")
	private Date imageModifiedTime;
	
	/**
	 * 1: Field visit
	 * 2: Telephone
	 * 3: Fax
	 * 4: Others 
	 */
	@Column(name="CollectionMethod")
	private Integer collectionMethod;
	
	/**
	 * Valid, Invalid
	 */
	@Column(name="Status")
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TpuId", nullable = true)
	private Tpu tpu;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "OutletTypeOutlet", 
			joinColumns = { @JoinColumn(name = "OutletId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "ShortCode", nullable = false, updatable = false) })
	private Set<VwOutletTypeShortForm> outletTypes = new HashSet<VwOutletTypeShortForm>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outlet")
	private Set<Assignment> assignments = new HashSet<Assignment>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outlet")
	private Set<Quotation> quotations = new HashSet<Quotation>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outlet")
	private Set<OutletAttachment> outletAttachments = new HashSet<OutletAttachment>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outlet")
	private Set<QuotationRecord> quotationRecords = new HashSet<QuotationRecord>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "outlets")
	private Set<PointToNote> pointToNotes = new HashSet<PointToNote>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "outlet")
	private Set<UnitCategoryInfo> unitCategoryInfo = new HashSet<UnitCategoryInfo>();
	
	@Temporal(TemporalType.TIME)
	@Column(name="OpeningStartTime")
	private Date openingStartTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="OpeningEndTime")
	private Date openingEndTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="ConvenientStartTime")
	private Date convenientStartTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="ConvenientEndTime")
	private Date convenientEndTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="OpeningStartTime2")
	private Date openingStartTime2;
	
	@Temporal(TemporalType.TIME)
	@Column(name="OpeningEndTime2")
	private Date openingEndTime2;
	
	@Temporal(TemporalType.TIME)
	@Column(name="ConvenientStartTime2")
	private Date convenientStartTime2;
	
	@Temporal(TemporalType.TIME)
	@Column(name="ConvenientEndTime2")
	private Date convenientEndTime2;
	
	@Column(name="BRCode")
	private String brCode;
	
	@Column(name="IsUseFRAdmin")
	private boolean isUseFRAdmin;
	
	@Column(name="IndoorMarketName")
	private String indoorMarketName;
	
	@Column(name="OutletMarketType")
	private Integer outletMarketType;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getOutletId();
	}


	public Integer getOutletId() {
		return outletId;
	}


	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}


	public Integer getFirmCode() {
		return firmCode;
	}


	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getTel() {
		return tel;
	}


	public void setTel(String tel) {
		this.tel = tel;
	}


	public String getDetailAddress() {
		return detailAddress;
	}


	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}


	public String getStreetAddress() {
		return streetAddress;
	}


	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}


	public String getMainContact() {
		return mainContact;
	}


	public void setMainContact(String mainContact) {
		this.mainContact = mainContact;
	}


	public String getLastContact() {
		return lastContact;
	}


	public void setLastContact(String lastContact) {
		this.lastContact = lastContact;
	}


	public String getFax() {
		return fax;
	}


	public void setFax(String fax) {
		this.fax = fax;
	}


	public String getWebSite() {
		return webSite;
	}


	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}


	public String getMarketName() {
		return marketName;
	}


	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getDiscountRemark() {
		return discountRemark;
	}


	public void setDiscountRemark(String discountRemark) {
		this.discountRemark = discountRemark;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public String getOutletImagePath() {
		return outletImagePath;
	}


	public void setOutletImagePath(String outletImagePath) {
		this.outletImagePath = outletImagePath;
	}


	public Date getImageModifiedTime() {
		return imageModifiedTime;
	}


	public void setImageModifiedTime(Date imageModifiedTime) {
		this.imageModifiedTime = imageModifiedTime;
	}


	public Integer getCollectionMethod() {
		return collectionMethod;
	}


	public void setCollectionMethod(Integer collectionMethod) {
		this.collectionMethod = collectionMethod;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Tpu getTpu() {
		return tpu;
	}


	public void setTpu(Tpu tpu) {
		this.tpu = tpu;
	}


	public Set<VwOutletTypeShortForm> getOutletTypes() {
		return outletTypes;
	}


	public void setOutletTypes(Set<VwOutletTypeShortForm> outletTypes) {
		this.outletTypes = outletTypes;
	}


	public Set<Quotation> getQuotations() {
		return quotations;
	}


	public void setQuotations(Set<Quotation> quotations) {
		this.quotations = quotations;
	}


	public Set<OutletAttachment> getOutletAttachments() {
		return outletAttachments;
	}


	public void setOutletAttachments(Set<OutletAttachment> outletAttachments) {
		this.outletAttachments = outletAttachments;
	}


	public Set<QuotationRecord> getQuotationRecords() {
		return quotationRecords;
	}


	public void setQuotationRecords(Set<QuotationRecord> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}


	public Set<PointToNote> getPointToNotes() {
		return pointToNotes;
	}


	public void setPointToNotes(Set<PointToNote> pointToNotes) {
		this.pointToNotes = pointToNotes;
	}


	public Date getOpeningStartTime() {
		return openingStartTime;
	}


	public void setOpeningStartTime(Date openingStartTime) {
		this.openingStartTime = openingStartTime;
	}


	public Date getOpeningEndTime() {
		return openingEndTime;
	}


	public void setOpeningEndTime(Date openingEndTime) {
		this.openingEndTime = openingEndTime;
	}


	public Date getConvenientStartTime() {
		return convenientStartTime;
	}


	public void setConvenientStartTime(Date convenientStartTime) {
		this.convenientStartTime = convenientStartTime;
	}


	public Date getConvenientEndTime() {
		return convenientEndTime;
	}


	public void setConvenientEndTime(Date convenientEndTime) {
		this.convenientEndTime = convenientEndTime;
	}


	public String getBrCode() {
		return brCode;
	}


	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}


	public boolean isUseFRAdmin() {
		return isUseFRAdmin;
	}


	public void setUseFRAdmin(boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}


	public Set<UnitCategoryInfo> getUnitCategoryInfo() {
		return unitCategoryInfo;
	}


	public void setUnitCategoryInfo(Set<UnitCategoryInfo> unitCategoryInfo) {
		this.unitCategoryInfo = unitCategoryInfo;
	}


	public Date getOpeningStartTime2() {
		return openingStartTime2;
	}


	public void setOpeningStartTime2(Date openingStartTime2) {
		this.openingStartTime2 = openingStartTime2;
	}


	public Date getOpeningEndTime2() {
		return openingEndTime2;
	}


	public void setOpeningEndTime2(Date openingEndTime2) {
		this.openingEndTime2 = openingEndTime2;
	}


	public Date getConvenientStartTime2() {
		return convenientStartTime2;
	}


	public void setConvenientStartTime2(Date convenientStartTime2) {
		this.convenientStartTime2 = convenientStartTime2;
	}


	public Date getConvenientEndTime2() {
		return convenientEndTime2;
	}


	public void setConvenientEndTime2(Date convenientEndTime2) {
		this.convenientEndTime2 = convenientEndTime2;
	}


	public String getIndoorMarketName() {
		return indoorMarketName;
	}


	public void setIndoorMarketName(String indoorMarketName) {
		this.indoorMarketName = indoorMarketName;
	}


	public Integer getOutletMarketType() {
		return outletMarketType;
	}


	public void setOutletMarketType(Integer outletMarketType) {
		this.outletMarketType = outletMarketType;
	}


	public Set<Assignment> getAssignments() {
		return assignments;
	}


	public void setAssignments(Set<Assignment> assignments) {
		this.assignments = assignments;
	}

	
	
}
