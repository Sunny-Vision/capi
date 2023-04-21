package capi.model.report;

import java.util.List;

import net.sf.jasperreports.engine.JasperPrint;

public class JasperWorkbook {

	private List<JasperPrint> worksheets;
	
	private String workbookName;

	public List<JasperPrint> getWorksheets() {
		return worksheets;
	}

	public void setWorksheets(List<JasperPrint> worksheets) {
		this.worksheets = worksheets;
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public void setWorkbookName(String workbookName) {
		this.workbookName = workbookName;
	}
	
}
