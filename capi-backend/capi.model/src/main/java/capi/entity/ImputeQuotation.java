package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ImputeQuotation")
public class ImputeQuotation  extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ImputeQuotationId")
	private Integer imputeQuotationId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="QuotationId", nullable=true)
	private Quotation quotation;
	
	@Column(name="Price")
	private Double price;
	
	@Column(name="Remark")
	private String remark;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getImputeQuotationId();
	}

	public Integer getImputeQuotationId() {
		return imputeQuotationId;
	}

	public void setImputeQuotationId(Integer imputeQuotationId) {
		this.imputeQuotationId = imputeQuotationId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
