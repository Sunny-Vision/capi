package capi.model.report;

import java.util.Hashtable;
import java.util.Map;

public class DocxExportModel {

	private String filename;
	
	private Map<String, String> data = new Hashtable<String, String>();

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
}
