package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ProductCleaning")
public class ProductCleaning extends capi.entity.EntityBase{
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ProductCleaningId")
	private Integer productCleaningId;
	
	@Column(name="OldProductId")
	private Integer oldProductId;
	
	@Column(name="NewProductId")
	private Integer newProductId;
	
	public Integer getProductCleaningId() {
		return productCleaningId;
	}

	public void setProductCleaningId(Integer productCleaningId) {
		this.productCleaningId = productCleaningId;
	}

	public Integer getOldProductId() {
		return oldProductId;
	}

	public void setOldProductId(Integer oldProductId) {
		this.oldProductId = oldProductId;
	}

	public Integer getNewProductId() {
		return newProductId;
	}

	public void setNewProductId(Integer newProductId) {
		this.newProductId = newProductId;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getProductCleaningId();
	}
}
