package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="SubPriceType")
public class SubPriceType extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SubPriceTypeId")
	private Integer subPriceTypeId;
	
	@Column(name="NumeratorFormula")
	private String numeratorFormula;
	
	@Column(name="DenominatorFormula")
	private String denominatorFormula;
	
	@Column(name="Category")
	private String category;
	
	@Column(name="Name")
	private String name;
	
	/**
	 * Enable, Disable
	 */
	@Column(name="Status")
	private String status;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GroupByFieldId", nullable = true)
	private SubPriceField groupByField;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DividedByFieldId", nullable = true)
	private SubPriceField dividedByField;	
	
	@Column(name="HideNPrice")
	private boolean hideNPrice;
	
	@Column(name="HideSPrice")
	private boolean hideSPrice;
	
	@Column(name="HideDiscount")
	private boolean hideDiscount;
	
	@Column(name="UseMaxNPrice")
	private boolean useMaxNPrice;

	@Column(name="UseMaxSPrice")
	private boolean useMaxSPrice;
	
	@Column(name="UseMinNPrice")
	private boolean useMinNPrice;

	@Column(name="UseMinSPrice")
	private boolean useMinSPrice;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subPriceType")
	private Set<SubPriceFieldMapping> fields = new HashSet<SubPriceFieldMapping>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSubPriceTypeId();
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

	public SubPriceField getGroupByField() {
		return groupByField;
	}

	public void setGroupByField(SubPriceField groupByField) {
		this.groupByField = groupByField;
	}

	public SubPriceField getDividedByField() {
		return dividedByField;
	}

	public void setDividedByField(SubPriceField dividedByField) {
		this.dividedByField = dividedByField;
	}

	public Set<SubPriceFieldMapping> getFields() {
		return fields;
	}

	public void setFields(Set<SubPriceFieldMapping> fields) {
		this.fields = fields;
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
