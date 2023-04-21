package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="UOMCategory")
public class UOMCategory extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UOMCategoryId")
	private Integer uomCategoryId;
	
	@Column(name="Description")
	private String description;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "uomCategory")
	private Set<Uom> uoms = new HashSet<Uom>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "uomCategories")
	private Set<Unit> units = new HashSet<Unit>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getUomCategoryId();
	}

	public Integer getUomCategoryId() {
		return uomCategoryId;
	}

	public void setUomCategoryId(Integer uomCategoryId) {
		this.uomCategoryId = uomCategoryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Uom> getUoms() {
		return uoms;
	}

	public void setUoms(Set<Uom> uoms) {
		this.uoms = uoms;
	}

}
