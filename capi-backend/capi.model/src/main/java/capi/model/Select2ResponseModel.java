package capi.model;

import java.util.List;

public class Select2ResponseModel {
	private List<Select2Item> results;

    private Pagination pagination;

    public class Select2Item
    {
        private String id;
        private String text;
        private String param1;
        private String param2;
        private List<Select2Item> children;
        
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public List<Select2Item> getChildren() {
			return children;
		}
		public void setChildren(List<Select2Item> children) {
			this.children = children;
		}
		public String getParam1() {
			return param1;
		}
		public void setParam1(String param1) {
			this.param1 = param1;
		}
		public String getParam2() {
			return param2;
		}
		public void setParam2(String param2) {
			this.param2 = param2;
		}
    }

    public class Pagination
    {
    	private boolean more;

		public boolean isMore() {
			return more;
		}

		public void setMore(boolean more) {
			this.more = more;
		}
    }

	
    public List<Select2Item> getResults() {
		return results;
	}

	public void setResults(List<Select2Item> results) {
		this.results = results;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
}
