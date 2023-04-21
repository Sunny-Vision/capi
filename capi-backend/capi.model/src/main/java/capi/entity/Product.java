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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Product")
public class Product extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ProductId")
	private Integer productId;
	
	@Column(name="CountryOfOrigin")
	private String countryOfOrigin;
	
	@Column(name="Photo1Path")
	private String photo1Path;
	
	@Column(name="Photo1ModifiedTime")
	private Date photo1ModifiedTime;
	
	@Column(name="Photo2Path")
	private String photo2Path;
	
	@Column(name="Photo2ModifiedTime")
	private Date photo2ModifiedTime;
	
	@Column(name="Remark")
	private String remark;
	
	/**
	 * Active, Inactive
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="Reviewed")
	private boolean reviewed;
	
	@Column(name="Barcode")
	private String barcode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductGroupId", nullable = true)
	private ProductGroup productGroup;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<ProductSpecification> productSpecifications = new HashSet<ProductSpecification>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<Quotation> quotations = new HashSet<Quotation>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "newProduct")
	private Set<Quotation> quotationsNewProduct = new HashSet<Quotation>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<QuotationRecord> quotationRecords = new HashSet<QuotationRecord>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "products")
	private Set<PointToNote> pointToNotes = new HashSet<PointToNote>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<VwProductSpec> specificationViews = new HashSet<VwProductSpec>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<VwProductFullSpec> fullSpecifications = new HashSet<VwProductFullSpec>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<VwProductSpecDistinct> distinctSpecificationViews = new HashSet<VwProductSpecDistinct>();
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;
	
	@Column(name="ObsoleteDate")
	private Date obsoleteDate;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getProductId();
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public String getPhoto1Path() {
		return photo1Path;
	}

	public void setPhoto1Path(String photo1Path) {
		this.photo1Path = photo1Path;
	}

	public Date getPhoto1ModifiedTime() {
		return photo1ModifiedTime;
	}

	public void setPhoto1ModifiedTime(Date photo1ModifiedTime) {
		this.photo1ModifiedTime = photo1ModifiedTime;
	}

	public String getPhoto2Path() {
		return photo2Path;
	}

	public void setPhoto2Path(String photo2Path) {
		this.photo2Path = photo2Path;
	}

	public Date getPhoto2ModifiedTime() {
		return photo2ModifiedTime;
	}

	public void setPhoto2ModifiedTime(Date photo2ModifiedTime) {
		this.photo2ModifiedTime = photo2ModifiedTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Set<ProductSpecification> getProductSpecifications() {
		return productSpecifications;
	}

	public void setProductSpecifications(
			Set<ProductSpecification> productSpecifications) {
		this.productSpecifications = productSpecifications;
	}

	public Set<Quotation> getQuotations() {
		return quotations;
	}

	public void setQuotations(Set<Quotation> quotations) {
		this.quotations = quotations;
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

	public Set<VwProductSpec> getSpecificationViews() {
		return specificationViews;
	}

	public Set<VwProductFullSpec> getFullSpecifications() {
		return fullSpecifications;
	}

	public void setPointToNotes(Set<PointToNote> pointToNotes) {
		this.pointToNotes = pointToNotes;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getObsoleteDate() {
		return obsoleteDate;
	}

	public void setObsoleteDate(Date obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
	}

	public void setSpecificationViews(Set<VwProductSpec> specificationViews) {
		this.specificationViews = specificationViews;
	}

	public void setFullSpecifications(Set<VwProductFullSpec> fullSpecifications) {
		this.fullSpecifications = fullSpecifications;
	}

	public Set<Quotation> getQuotationsNewProduct() {
		return quotationsNewProduct;
	}

	public void setQuotationsNewProduct(Set<Quotation> quotationsNewProduct) {
		this.quotationsNewProduct = quotationsNewProduct;
	}
}
