package capi.model;

public class Select2RequestModel {

    private String term;
    private int page;
    int recordsPerPage;
    long recordsTotal;
    
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRecordsPerPage() {
		return recordsPerPage;
	}
	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}
	public long getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public int getFirstRecord() {
		return ((this.page == 0 ? 1 : this.page) - 1) * this.recordsPerPage;
	}
	public boolean hasMore() {
		return ((this.page == 0 ? 1 : this.page) * this.recordsPerPage) < this.recordsTotal;
	}
}
