package capi.model.api.dataSync;

public class SupervisoryVisitDetailSyncModel extends SyncModel<SupervisoryVisitDetailSyncData>{

	private Integer[] supervisoryVisitFormIds;

	public Integer[] getSupervisoryVisitFormIds() {
		return supervisoryVisitFormIds;
	}

	public void setSupervisoryVisitFormIds(Integer[] supervisoryVisitFormIds) {
		this.supervisoryVisitFormIds = supervisoryVisitFormIds;
	}
	
}
