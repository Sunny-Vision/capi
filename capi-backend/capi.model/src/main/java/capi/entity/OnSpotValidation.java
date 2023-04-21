package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import  org.hibernate.annotations.Parameter;

@Entity
@Table(name="OnSpotValidation")
public class OnSpotValidation extends capi.entity.EntityBase{

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "unit"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "UnitId", unique = true, nullable = false)
	private Integer unitId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private Unit unit;
	
	//Unit of measurement (UOM1) should be reported
	@Column(name="IsUom1Reported")
	private boolean isUom1Reported;
	//***********************
	
	//Unit of measurement (UOM2) >= 0
	@Column(name="IsUom2GreaterZero")
	private boolean isUom2GreaterZero;
	//***********************
	
	//Normal Price (N Price) > 0
	@Column(name="IsNPriceGreaterZero")
	private boolean isNPriceGreaterZero;
	//***********************
	
	//Special Price (S Price) > 0
	@Column(name="IsSPriceGreaterZero")
	private boolean isSPriceGreaterZero;
	//***********************
	
	
	//Provide Reason if percentage change of N Price >
	@Column(name="ProvideReasonPRNPrice")
	private boolean provideReasonPRNPrice;
	
	@Column(name="PRNPriceThreshold")
	private Double prNPriceThreshold;
	//***********************
	
	//Provide Reason if percentage change of S Price >
	@Column(name="ProvideReasonPRSPrice")
	private boolean provideReasonPRSPrice;
	
	@Column(name="PRSPriceThreshold")
	private Double prSPriceThreshold;
	//***********************
	
	//Provide Reason if percentage change of N Price <
	@Column(name="ProvideReasonPRNPriceLower")
	private boolean provideReasonPRNPriceLower;
	
	@Column(name="PRNPriceLowerThreshold")
	private Double prNPriceLowerThreshold;
	//***********************
	
	//Provide Reason if percentage change of S Price <
	@Column(name="ProvideReasonPRSPriceLower")
	private boolean provideReasonPRSPriceLower;
	
	@Column(name="PRSPriceLowerThreshold")
	private Double prSPriceLowerThreshold;
	//***********************
	
		
	//Provide Reason if S price is > max or < min S price of the same Unit in the last month
	@Column(name="ProvideReasonSPriceMaxMin")
	private boolean provideReasonSPriceMaxMin;
	//***********************
	
	//Provide Reason if N price is > max or < min N price of the same Unit in the last month
	@Column(name="ProvideReasonNPriceMaxMin")
	private boolean provideReasonNPriceMaxMin;
	//***********************
	
	//Normal price (N Price) >= Special Price (S Price)
	@Column(name="NPriceGreaterSPrice")
	private boolean nPriceGreaterSPrice;
	//***********************
	
	//If "Not suitable" is chosen for N and S price, remarks have to be provided.
	@Column(name="ProvideRemarkForNotSuitablePrice")
	private boolean provideRemarkForNotSuitablePrice;
	//***********************
	
	//If the quotation record is "Not available", no field except remarks should be filled.
	@Column(name="ProvideRemarkForNotAvailableQuotation")
	private boolean provideRemarkForNotAvailableQuotation;
	//***********************	
	
	//If the pricing cycle of a product is longer than the specified product cycle for different Units, reminder will be provided.
	@Column(name="ReminderForPricingCycle")
	private boolean reminderForPricingCycle;
	//***********************
	
	//Provide Reason if percentage change of S price exceed the ranges of (mean +/- S.D.) in last y month
	@Column(name="ProvideReasonPRSPriceSD")
	private boolean provideReasonPRSPriceSD;
	
	@Column(name="PRSPriceSDPositive")
	private Double prSPriceSDPositive;
	
	@Column(name="PRSPriceSDNegative")
	private Double prSPriceSDNegative;
	
	@Column(name="PRSPriceMonth")
	private Integer prSPriceMonth;	
	//***********************

	//Provide Reason if percentage change of N price exceed the ranges of (mean +/- S.D.) in last y month
	@Column(name="ProvideReasonPRNPriceSD")
	private boolean provideReasonPRNPriceSD;	
	
	@Column(name="PRNPriceSDPositive")
	private Double prNPriceSDPositive;
	
	@Column(name="PRNPriceSDNegative")
	private Double prNPriceSDNegative;
	
	@Column(name="PRNPriceMonth")
	private Integer prNPriceMonth;
	//***********************
	
	//Provide Reason if S price exceed the ranges of (x - y) in last k months
	@Column(name="ProvideReasonSPriceSD")
	private boolean provideReasonSPriceSD;	
	
	@Column(name="SPriceMonth")
	private Integer sPriceMonth;	

	@Column(name="SPriceSDPositive")
	private Double sPriceSDPositive;
	
	@Column(name="SPriceSDNegative")
	private Double sPriceSDNegative;
	//***********************
	
	//Provide Reason if N price exceed the ranges of (x - y) in last k months
	@Column(name="ProvideReasonNPriceSD")
	private boolean provideReasonNPriceSD;
	
	@Column(name="NPriceSDPositive")
	private Double nPriceSDPositive;
	
	@Column(name="NPriceSDNegative")
	private Double nPriceSDNegative;
	
	@Column(name="NPriceMonth")
	private Integer nPriceMonth;
	//***********************
	
	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public boolean isUom1Reported() {
		return isUom1Reported;
	}

	public void setUom1Reported(boolean isUom1Reported) {
		this.isUom1Reported = isUom1Reported;
	}

	public boolean isUom2GreaterZero() {
		return isUom2GreaterZero;
	}

	public void setUom2GreaterZero(boolean isUom2GreaterZero) {
		this.isUom2GreaterZero = isUom2GreaterZero;
	}

	public boolean isNPriceGreaterZero() {
		return isNPriceGreaterZero;
	}

	public void setNPriceGreaterZero(boolean isNPriceGreaterZero) {
		this.isNPriceGreaterZero = isNPriceGreaterZero;
	}

	public boolean isSPriceGreaterZero() {
		return isSPriceGreaterZero;
	}

	public void setSPriceGreaterZero(boolean isSPriceGreaterZero) {
		this.isSPriceGreaterZero = isSPriceGreaterZero;
	}

	public boolean isProvideReasonPRNPrice() {
		return provideReasonPRNPrice;
	}

	public void setProvideReasonPRNPrice(boolean provideReasonPRNPrice) {
		this.provideReasonPRNPrice = provideReasonPRNPrice;
	}

	public Double getPrNPriceThreshold() {
		return prNPriceThreshold;
	}

	public void setPrNPriceThreshold(Double prNPriceThreshold) {
		this.prNPriceThreshold = prNPriceThreshold;
	}

	public boolean isProvideReasonPRSPrice() {
		return provideReasonPRSPrice;
	}

	public void setProvideReasonPRSPrice(boolean provideReasonPRSPrice) {
		this.provideReasonPRSPrice = provideReasonPRSPrice;
	}

	public Double getPrSPriceThreshold() {
		return prSPriceThreshold;
	}

	public void setPrSPriceThreshold(Double prSPriceThreshold) {
		this.prSPriceThreshold = prSPriceThreshold;
	}

	public boolean isProvideReasonSPriceMaxMin() {
		return provideReasonSPriceMaxMin;
	}

	public void setProvideReasonSPriceMaxMin(boolean provideReasonSPriceMaxMin) {
		this.provideReasonSPriceMaxMin = provideReasonSPriceMaxMin;
	}

	public boolean isProvideReasonNPriceMaxMin() {
		return provideReasonNPriceMaxMin;
	}

	public void setProvideReasonNPriceMaxMin(boolean provideReasonNPriceMaxMin) {
		this.provideReasonNPriceMaxMin = provideReasonNPriceMaxMin;
	}

	public boolean isnPriceGreaterSPrice() {
		return nPriceGreaterSPrice;
	}

	public void setnPriceGreaterSPrice(boolean nPriceGreaterSPrice) {
		this.nPriceGreaterSPrice = nPriceGreaterSPrice;
	}

	public boolean isProvideRemarkForNotSuitablePrice() {
		return provideRemarkForNotSuitablePrice;
	}

	public void setProvideRemarkForNotSuitablePrice(
			boolean provideRemarkForNotSuitablePrice) {
		this.provideRemarkForNotSuitablePrice = provideRemarkForNotSuitablePrice;
	}

	public boolean isReminderForPricingCycle() {
		return reminderForPricingCycle;
	}

	public void setReminderForPricingCycle(boolean reminderForPricingCycle) {
		this.reminderForPricingCycle = reminderForPricingCycle;
	}

	public boolean isProvideReasonPRSPriceSD() {
		return provideReasonPRSPriceSD;
	}

	public void setProvideReasonPRSPriceSD(boolean provideReasonPRSPriceSD) {
		this.provideReasonPRSPriceSD = provideReasonPRSPriceSD;
	}

	public boolean isProvideReasonPRNPriceSD() {
		return provideReasonPRNPriceSD;
	}

	public void setProvideReasonPRNPriceSD(boolean provideReasonPRNPriceSD) {
		this.provideReasonPRNPriceSD = provideReasonPRNPriceSD;
	}

	public Double getPrSPriceSDPositive() {
		return prSPriceSDPositive;
	}

	public void setPrSPriceSDPositive(Double prSPriceSDPositive) {
		this.prSPriceSDPositive = prSPriceSDPositive;
	}

	public Double getPrSPriceSDNegative() {
		return prSPriceSDNegative;
	}

	public void setPrSPriceSDNegative(Double prSPriceSDNegative) {
		this.prSPriceSDNegative = prSPriceSDNegative;
	}

	public Double getPrNPriceSDPositive() {
		return prNPriceSDPositive;
	}

	public void setPrNPriceSDPositive(Double prNPriceSDPositive) {
		this.prNPriceSDPositive = prNPriceSDPositive;
	}

	public Double getPrNPriceSDNegative() {
		return prNPriceSDNegative;
	}

	public void setPrNPriceSDNegative(Double prNPriceSDNegative) {
		this.prNPriceSDNegative = prNPriceSDNegative;
	}

	public Integer getPrSPriceMonth() {
		return prSPriceMonth;
	}

	public void setPrSPriceMonth(Integer prSPriceMonth) {
		this.prSPriceMonth = prSPriceMonth;
	}

	public Integer getPrNPriceMonth() {
		return prNPriceMonth;
	}

	public void setPrNPriceMonth(Integer prNPriceMonth) {
		this.prNPriceMonth = prNPriceMonth;
	}

	public boolean isProvideReasonSPriceSD() {
		return provideReasonSPriceSD;
	}

	public void setProvideReasonSPriceSD(boolean provideReasonSPriceSD) {
		this.provideReasonSPriceSD = provideReasonSPriceSD;
	}

	public boolean isProvideReasonNPriceSD() {
		return provideReasonNPriceSD;
	}

	public void setProvideReasonNPriceSD(boolean provideReasonNPriceSD) {
		this.provideReasonNPriceSD = provideReasonNPriceSD;
	}

	public Double getsPriceSDPositive() {
		return sPriceSDPositive;
	}

	public void setsPriceSDPositive(Double sPriceSDPositive) {
		this.sPriceSDPositive = sPriceSDPositive;
	}

	public Double getsPriceSDNegative() {
		return sPriceSDNegative;
	}

	public void setsPriceSDNegative(Double sPriceSDNegative) {
		this.sPriceSDNegative = sPriceSDNegative;
	}

	public Double getnPriceSDPositive() {
		return nPriceSDPositive;
	}

	public void setnPriceSDPositive(Double nPriceSDPositive) {
		this.nPriceSDPositive = nPriceSDPositive;
	}

	public Double getnPriceSDNegative() {
		return nPriceSDNegative;
	}

	public void setnPriceSDNegative(Double nPriceSDNegative) {
		this.nPriceSDNegative = nPriceSDNegative;
	}

	public Integer getsPriceMonth() {
		return sPriceMonth;
	}

	public void setsPriceMonth(Integer sPriceMonth) {
		this.sPriceMonth = sPriceMonth;
	}

	public Integer getnPriceMonth() {
		return nPriceMonth;
	}

	public void setnPriceMonth(Integer nPriceMonth) {
		this.nPriceMonth = nPriceMonth;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getUnitId();
	}

	public boolean isProvideReasonPRNPriceLower() {
		return provideReasonPRNPriceLower;
	}

	public void setProvideReasonPRNPriceLower(boolean provideReasonPRNPriceLower) {
		this.provideReasonPRNPriceLower = provideReasonPRNPriceLower;
	}

	public Double getPrNPriceLowerThreshold() {
		return prNPriceLowerThreshold;
	}

	public void setPrNPriceLowerThreshold(Double prNPriceLowerThreshold) {
		this.prNPriceLowerThreshold = prNPriceLowerThreshold;
	}

	public boolean isProvideReasonPRSPriceLower() {
		return provideReasonPRSPriceLower;
	}

	public void setProvideReasonPRSPriceLower(boolean provideReasonPRSPriceLower) {
		this.provideReasonPRSPriceLower = provideReasonPRSPriceLower;
	}

	public Double getPrSPriceLowerThreshold() {
		return prSPriceLowerThreshold;
	}

	public void setPrSPriceLowerThreshold(Double prSPriceLowerThreshold) {
		this.prSPriceLowerThreshold = prSPriceLowerThreshold;
	}

	public boolean isProvideRemarkForNotAvailableQuotation() {
		return provideRemarkForNotAvailableQuotation;
	}

	public void setProvideRemarkForNotAvailableQuotation(
			boolean provideRemarkForNotAvailableQuotation) {
		this.provideRemarkForNotAvailableQuotation = provideRemarkForNotAvailableQuotation;
	}
	
}
