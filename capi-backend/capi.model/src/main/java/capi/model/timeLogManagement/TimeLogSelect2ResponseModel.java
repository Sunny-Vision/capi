package capi.model.timeLogManagement;

import java.util.List;

public class TimeLogSelect2ResponseModel {
	private List<TimeLogSelect2Item> results;

    private Pagination pagination;

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

    public List<TimeLogSelect2Item> getResults() {
		return results;
	}

	public void setResults(List<TimeLogSelect2Item> results) {
		this.results = results;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
}
