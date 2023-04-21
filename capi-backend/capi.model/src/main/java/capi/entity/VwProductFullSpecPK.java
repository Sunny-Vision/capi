package capi.entity;

import java.io.Serializable;

public class VwProductFullSpecPK implements Serializable {

	private Integer productId;
		
	private Integer productAttributeId;
	
	private Integer productSpecificationId;
	
	public VwProductFullSpecPK() {}

    public VwProductFullSpecPK(Integer productId, Integer productAttributeId, Integer productSpecificationId) {
       this.productId = productId;
       this.productAttributeId = productAttributeId;
       this.productSpecificationId = productSpecificationId;
    }
	

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
	public Integer getProductAttributeId() {
		return productAttributeId;
	}

	public void setProductAttributeId(Integer productAttributeId) {
		this.productAttributeId = productAttributeId;
	}

	public Integer getProductSpecificationId() {
		return productSpecificationId;
	}

	public void setProductSpecificationId(Integer productSpecificationId) {
		this.productSpecificationId = productSpecificationId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((productAttributeId == null) ? 0 : productAttributeId
						.hashCode());
		result = prime * result
				+ ((productId == null) ? 0 : productId.hashCode());
		result = prime
				* result
				+ ((productSpecificationId == null) ? 0
						: productSpecificationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VwProductFullSpecPK other = (VwProductFullSpecPK) obj;
		if (productAttributeId == null) {
			if (other.productAttributeId != null)
				return false;
		} else if (!productAttributeId.equals(other.productAttributeId))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (productSpecificationId == null) {
			if (other.productSpecificationId != null)
				return false;
		} else if (!productSpecificationId.equals(other.productSpecificationId))
			return false;
		return true;
	}
	
}
