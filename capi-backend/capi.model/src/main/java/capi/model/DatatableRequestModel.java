package capi.model;

import java.util.List;
import java.util.Map;

public class DatatableRequestModel {

	//private List<Map<String, String>> columns;
	
	private int draw;
	
	private List<Map<String, String>> order;
	
	private int start;
	
	private int length;
	
	private Map<String, String> search;

//	public List<Map<String, String>> getColumns() {
//		return columns;
//	}
//
//	public void setColumns(List<Map<String, String>> columns) {
//		this.columns = columns;
//	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public List<Map<String, String>> getOrder() {
		return order;
	}

	public void setOrder(List<Map<String, String>> order) {
		this.order = order;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Map<String, String> getSearch() {
		return search;
	}

	public void setSearch(Map<String, String> search) {
		this.search = search;
	}
	
}
