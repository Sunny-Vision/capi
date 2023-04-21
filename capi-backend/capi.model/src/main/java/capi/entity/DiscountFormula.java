package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="DiscountFormula")
public class DiscountFormula  extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="DiscountFormulaId")
	private Integer discountFormulaId;
	
	@Column(name="Formula")
	private String formula;
	
	@Column(name="Pattern")
	private String pattern;
	
	@Column(name="DisplayPattern")
	private String displayPattern;
	
	@Column(name="Variable")
	private String variable;
	
	/**
	 * Enable, Disable
	 */
	@Column(name="Status")
	private String status;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getDiscountFormulaId();
	}

	public Integer getDiscountFormulaId() {
		return discountFormulaId;
	}

	public void setDiscountFormulaId(Integer discountFormulaId) {
		this.discountFormulaId = discountFormulaId;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDisplayPattern() {
		return displayPattern;
	}

	public void setDisplayPattern(String displayPattern) {
		this.displayPattern = displayPattern;
	}

}
