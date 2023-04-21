package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ProductGroup")
public class ProductGroup  extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ProductGroupId")
	private Integer productGroupId;
	
	@Column(name="Code")
	private String code;
	
	@Column(name="EnglishName")
	private String englishName;
	
	@Column(name="ChineseName")
	private String chineseName;
	
	/**
	 * Active, Inactive
	 */
	@Column(name="Status")
	private String status;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productGroup")
	private Set<ProductAttribute> productAttributes = new HashSet<ProductAttribute>();

	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productGroup")
	private Set<Product> products = new HashSet<Product>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productCategory")
	private Set<Unit> units = new HashSet<Unit>();
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;
	
	@Column(name="ObsoleteDate")
	private Date obsoleteDate;

	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getProductGroupId();
	}



	public Integer getProductGroupId() {
		return productGroupId;
	}



	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getEnglishName() {
		return englishName;
	}



	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}



	public String getChineseName() {
		return chineseName;
	}



	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}



	public Set<ProductAttribute> getProductAttributes() {
		return productAttributes;
	}



	public void setProductAttributes(Set<ProductAttribute> productAttributes) {
		this.productAttributes = productAttributes;
	}



	public Set<Product> getProducts() {
		return products;
	}



	public void setProducts(Set<Product> products) {
		this.products = products;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public Set<Unit> getUnits() {
		return units;
	}



	public void setUnits(Set<Unit> units) {
		this.units = units;
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

}
