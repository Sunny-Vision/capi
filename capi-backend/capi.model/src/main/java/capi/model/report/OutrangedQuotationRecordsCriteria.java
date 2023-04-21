package capi.model.report;

import java.util.ArrayList;

public class OutrangedQuotationRecordsCriteria {

	public static class CriteriaList {

		private String open;

		private String field;

		private String operator;

		private String value;

		private String close;

		private String logic;

		public String getOpen() {
			return open;
		}

		public void setOpen(String open) {
			this.open = open;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getClose() {
			return close;
		}

		public void setClose(String close) {
			this.close = close;
		}

		public String getLogic() {
			return logic;
		}

		public void setLogic(String logic) {
			this.logic = logic;
		}

	}

	private ArrayList<Integer> purpose;

	private ArrayList<Integer> itemId;

	private ArrayList<Integer> cpiSurveyForm;

	private String startMonth;

	private String endMonth;

	private String dataCollection;

	private ArrayList<CriteriaList> criteriaList;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getItemId() {
		return itemId;
	}

	public void setItemId(ArrayList<Integer> itemId) {
		this.itemId = itemId;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(String dataCollection) {
		this.dataCollection = dataCollection;
	}

	public ArrayList<CriteriaList> getCriteriaList() {
		return criteriaList;
	}

	public void setCriteriaList(ArrayList<CriteriaList> criteriaList) {
		this.criteriaList = criteriaList;
	}

}
