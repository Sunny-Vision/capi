package capi.model.api.dataSync;

public class RUAQuotationSyncData extends QuotationSyncData{

	private OutletSyncData outlet;

	public OutletSyncData getOutlet() {
		return outlet;
	}

	public void setOutlet(OutletSyncData outlet) {
		this.outlet = outlet;
	}
	
}
