package capi.model.assignmentAllocationAndReallocation.surveyMonth;

public class IndexingModel {
	public Integer newBatchAllocationId;
	public Integer newAlloactionAttributeId;
	public Integer newBackTrackDateDisplayModelId;
	public Boolean readonly;
	public Boolean isDraft;
	public Boolean tab1;
	public Boolean tab2;
	public Boolean tab3;
	public Boolean tab4;
	
	public IndexingModel(){
		this.newAlloactionAttributeId = 0;
		this.newBatchAllocationId = 0;
		this.newBackTrackDateDisplayModelId = 0;
		this.readonly = false;
		this.isDraft = true;
		this.tab1 = false;
		this.tab2 = false;
		this.tab3 = false;
		this.tab4 = false;
	}
}
