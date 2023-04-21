package capi.model.assignmentAllocationAndReallocation.surveyMonth.generation;

import java.util.ArrayList;
import java.util.List;

import capi.entity.Outlet;
import capi.entity.PECheckUnitCriteria;
import capi.entity.Quotation;

public class OutletQuotationModel {
	private Outlet outlet;
	private List<Quotation> quotations;
	private PECheckUnitCriteria pecuc;
	
	public OutletQuotationModel(){
		this.quotations = new ArrayList<Quotation>();
	}
	
	public Outlet getOutlet() {
		return outlet;
	}
	public void setOutlet(Outlet o) {
		this.outlet = o;
	}

	public List<Quotation> getQuotations() {
		return quotations;
	}

	public void setQuotations(List<Quotation> relatedQ) {
		this.quotations = relatedQ;
	}

	public PECheckUnitCriteria getPecuc() {
		return pecuc;
	}

	public void setPecuc(PECheckUnitCriteria pecuc) {
		this.pecuc = pecuc;
	}
	
	
}
