package capi.model.assignmentAllocationAndReallocation.surveyMonth.collectionDateChecking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BatchCodeModel {
	private Integer batchCodeId;
	
	private List<Date> collectionDateList;
	
	private Integer type;
	
	public BatchCodeModel(){
		this.batchCodeId = 0;
		this.collectionDateList = new ArrayList<Date>();
	}

	public Integer getBatchCodeId() {
		return batchCodeId;
	}

	public void setBatchCodeId(Integer batchCodeId) {
		this.batchCodeId = batchCodeId;
	}
	
	public List<Date> getCollectionDateList() {
		return collectionDateList;
	}

	public void setCollectionDateList(List<Date> collectionDateList) {
		this.collectionDateList = collectionDateList;
	}

	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
}
