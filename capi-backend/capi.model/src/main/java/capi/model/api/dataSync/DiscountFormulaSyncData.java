package capi.model.api.dataSync;

import java.util.Date;

public class DiscountFormulaSyncData {
	private Integer discountFormulaId;
	
	private String formula;
	
	private String pattern;
	
	private String variable;
	
	private String status;
	
	private String displayPattern; 
	
	private Date createdDate;
	
	private Date modifiedDate;

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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
}
