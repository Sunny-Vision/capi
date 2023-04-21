package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDate;

public class SurveyMonthEditModel implements Serializable{
	
	private Integer surveyMonthId;
	private Date referenceMonth;
	private Date startDate;
	private Date endDate;
	private Integer closingDateId;
	private List<AllocationBatchEditModel> allocationBatches;
	private List<AssignmentAttributeEditModel> assignmentAttributes;
	private List<BatchCollectionDateEditModel> batchCollectionDates;
	
	private Map<Integer,AllocationBatchEditModel> allocationBatchIdAllocationBatchMap;
	private Map<Integer,AssignmentAttributeEditModel> idAssignmentAttributeMap;
	private Map<Integer, BatchCollectionDateEditModel> idBatchCollectionDateMap;
	
	private Map<Integer,List<AssignmentAttributeEditModel>> allocationAttrBatchIdMap;
	private Map<Integer,List<BatchCollectionDateEditModel>> assignmentAttBatchCollectionDateMap;
	private Map<Integer,List<AssignmentAttributeEditModel>> batchIdAssignmentAttributeMap;
	
	private Collection<Integer> allocationBatchDeleteRecordIds;
	private Collection<Integer> allocationBatchUpdateRecordIds;
	private List<AllocationBatchEditModel> newAllocationBatchRecords;
	
	private Collection<Integer> assignmentAttributeDeleteRecordIds;
	private Collection<Integer> assignmentAttributeUpdateRecordIds;
	private List<AssignmentAttributeEditModel> newAssignmentAttributeRecords;
	
	private Collection<Integer> batchCollectionDateDeleteRecordIds;
	private Collection<Integer> batchCollectionDateUpdateRecordIds;
	private List<BatchCollectionDateEditModel> newBatchCollectionDateRecords;
	
	private Integer status;
	private Boolean isDraft;
	
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}
	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	public Date getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getClosingDateId() {
		return closingDateId;
	}
	public void setClosingDateId(Integer closingDateId) {
		this.closingDateId = closingDateId;
	}
	public List<AllocationBatchEditModel> getAllocationBatches() {
		return allocationBatches;
	}
	public void setAllocationBatches(List<AllocationBatchEditModel> allocationBatches) {
		this.allocationBatches = allocationBatches;
	}
	public Map<Integer,AllocationBatchEditModel> getAllocationBatchIdAllocationBatchMap() {
		return allocationBatchIdAllocationBatchMap;
	}
	public void setAllocationBatchIdAllocationBatchMap(Map<Integer,AllocationBatchEditModel> allocationBatchIdAllocationBatchMap) {
		this.allocationBatchIdAllocationBatchMap = allocationBatchIdAllocationBatchMap;
	}
	public Map<Integer,List<AssignmentAttributeEditModel>> getAllocationAttrBatchIdMap() {
		return allocationAttrBatchIdMap;
	}
	public void setAllocationAttrBatchIdMap(Map<Integer,List<AssignmentAttributeEditModel>> allocationAttrBatchIdMap) {
		this.allocationAttrBatchIdMap = allocationAttrBatchIdMap;
	}
	public Map<Integer, List<AssignmentAttributeEditModel>> getBatchIdAssignmentAttributeMap() {
		return batchIdAssignmentAttributeMap;
	}
	public void setBatchIdAssignmentAttributeMap(
			Map<Integer, List<AssignmentAttributeEditModel>> batchIdAssignmentAttributeMap) {
		this.batchIdAssignmentAttributeMap = batchIdAssignmentAttributeMap;
	}
	public Map<Integer,List<BatchCollectionDateEditModel>> getAssignmentAttBatchCollectionDateMap() {
		return assignmentAttBatchCollectionDateMap;
	}
	public void setAssignmentAttBatchCollectionDateMap(Map<Integer,List<BatchCollectionDateEditModel>> assignmentAttBatchCollectionDateMap) {
		this.assignmentAttBatchCollectionDateMap = assignmentAttBatchCollectionDateMap;
	}
	public Map<Integer,AssignmentAttributeEditModel> getIdAssignmentAttributeMap() {
		return idAssignmentAttributeMap;
	}
	public void setIdAssignmentAttributeMap(Map<Integer,AssignmentAttributeEditModel> idAssignmentAttributeMap) {
		this.idAssignmentAttributeMap = idAssignmentAttributeMap;
	}
	public Map<Integer, BatchCollectionDateEditModel> getIdBatchCollectionDateMap() {
		return idBatchCollectionDateMap;
	}
	public void setIdBatchCollectionDateMap(Map<Integer, BatchCollectionDateEditModel> idBatchCollectionDateMap) {
		this.idBatchCollectionDateMap = idBatchCollectionDateMap;
	}
	public List<AssignmentAttributeEditModel> getAssignmentAttributes() {
		return assignmentAttributes;
	}
	public void setAssignmentAttributes(List<AssignmentAttributeEditModel> assignmentAttributes) {
		this.assignmentAttributes = assignmentAttributes;
	}
	public List<BatchCollectionDateEditModel> getBatchCollectionDates() {
		return batchCollectionDates;
	}
	public void setBatchCollectionDates(List<BatchCollectionDateEditModel> batchCollectionDates) {
		this.batchCollectionDates = batchCollectionDates;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Boolean getIsDraft() {
		return isDraft;
	}
	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}
	public Collection<Integer> getAllocationBatchDeleteRecordIds() {
		return allocationBatchDeleteRecordIds;
	}
	public void setAllocationBatchDeleteRecordIds(Collection<Integer> allocationBatchDeleteRecordIds) {
		this.allocationBatchDeleteRecordIds = allocationBatchDeleteRecordIds;
	}
	public Collection<Integer> getAllocationBatchUpdateRecordIds() {
		return allocationBatchUpdateRecordIds;
	}
	public void setAllocationBatchUpdateRecordIds(Collection<Integer> allocationBatchUpdateRecordIds) {
		this.allocationBatchUpdateRecordIds = allocationBatchUpdateRecordIds;
	}
	public List<AllocationBatchEditModel> getNewAllocationBatchRecords() {
		return newAllocationBatchRecords;
	}
	public void setNewAllocationBatchRecords(List<AllocationBatchEditModel> newAllocationBatchRecords) {
		this.newAllocationBatchRecords = newAllocationBatchRecords;
	}
	public Collection<Integer> getAssignmentAttributeUpdateRecordIds() {
		return assignmentAttributeUpdateRecordIds;
	}
	public void setAssignmentAttributeUpdateRecordIds(Collection<Integer> assignmentAttributeUpdateRecordIds) {
		this.assignmentAttributeUpdateRecordIds = assignmentAttributeUpdateRecordIds;
	}
	public Collection<Integer> getAssignmentAttributeDeleteRecordIds() {
		return assignmentAttributeDeleteRecordIds;
	}
	public void setAssignmentAttributeDeleteRecordIds(Collection<Integer> assignmentAttributeDeleteRecordIds) {
		this.assignmentAttributeDeleteRecordIds = assignmentAttributeDeleteRecordIds;
	}
	public List<AssignmentAttributeEditModel> getNewAssignmentAttributeRecords() {
		return newAssignmentAttributeRecords;
	}
	public void setNewAssignmentAttributeRecords(List<AssignmentAttributeEditModel> newAssignmentAttributeRecords) {
		this.newAssignmentAttributeRecords = newAssignmentAttributeRecords;
	}
	
	public Collection<Integer> getBatchCollectionDateDeleteRecordIds() {
		return batchCollectionDateDeleteRecordIds;
	}
	public void setBatchCollectionDateDeleteRecordIds(Collection<Integer> batchCollectionDateDeleteRecordIds) {
		this.batchCollectionDateDeleteRecordIds = batchCollectionDateDeleteRecordIds;
	}
	public Collection<Integer> getBatchCollectionDateUpdateRecordIds() {
		return batchCollectionDateUpdateRecordIds;
	}
	public void setBatchCollectionDateUpdateRecordIds(Collection<Integer> batchCollectionDateUpdateRecordIds) {
		this.batchCollectionDateUpdateRecordIds = batchCollectionDateUpdateRecordIds;
	}
	public List<BatchCollectionDateEditModel> getNewBatchCollectionDateRecords() {
		return newBatchCollectionDateRecords;
	}
	public void setNewBatchCollectionDateRecords(List<BatchCollectionDateEditModel> newBatchCollectionDateRecords) {
		this.newBatchCollectionDateRecords = newBatchCollectionDateRecords;
	}
	@SuppressWarnings("unchecked")
	public void getDeleteUpdateAllocationBatchRecords(Set<capi.entity.AllocationBatch> oldEntities){
		Set<Integer> oldIds = new HashSet<Integer>();
		if(oldEntities != null){
			for(capi.entity.AllocationBatch entity : oldEntities) {
				oldIds.add(entity.getId());
			}
		}
		
		Set<Integer> modelIds = new HashSet<Integer>();
		List<AllocationBatchEditModel> newRecords = new ArrayList<AllocationBatchEditModel>();  
		if(this.getAllocationBatches() != null){
			for(AllocationBatchEditModel model: this.getAllocationBatches()){
				if(model.getAllocationBatchId() == null || model.getAllocationBatchId() == 0){
					newRecords.add(model);
				} else {
					modelIds.add(model.getAllocationBatchId());
				}
			}
			this.setNewAllocationBatchRecords(newRecords);
		}
		
		Collection<Integer> deleteIds = (Collection<Integer>) CollectionUtils.subtract(oldIds, modelIds);
		if(deleteIds != null && !deleteIds.isEmpty()){
			this.setAllocationBatchDeleteRecordIds(deleteIds);
		}
		Collection<Integer> updatedIds = (Collection<Integer>) CollectionUtils.intersection(oldIds, modelIds);
		if(updatedIds != null && !updatedIds.isEmpty()){
			this.setAllocationBatchUpdateRecordIds(updatedIds);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getDeleteUpdateAssignmentAttributeRecords(Set<capi.entity.AssignmentAttribute> oldEntities){
		Set<Integer> oldIds = new HashSet<Integer>();
		if(oldEntities != null){
			for(capi.entity.AssignmentAttribute entity : oldEntities) {
				oldIds.add(entity.getId());
			}
		}
		
		Set<Integer> modelIds = new HashSet<Integer>();
		List<AssignmentAttributeEditModel> newRecords = new ArrayList<AssignmentAttributeEditModel>();  
		if(this.getAssignmentAttributes() != null){
			for(AssignmentAttributeEditModel model: this.getAssignmentAttributes()){
				if(model.getAssignmentAttributeId() == null || model.getAssignmentAttributeId() == 0){
					newRecords.add(model);
				} else {
					modelIds.add(model.getAssignmentAttributeId());
				}
			}
			this.setNewAssignmentAttributeRecords(newRecords);
		}
		
		Collection<Integer> deleteIds = (Collection<Integer>) CollectionUtils.subtract(oldIds, modelIds);
		if(deleteIds != null && !deleteIds.isEmpty()){
			this.setAssignmentAttributeDeleteRecordIds(deleteIds);
		}
		Collection<Integer> updatedIds = (Collection<Integer>) CollectionUtils.intersection(oldIds, modelIds);
		if(updatedIds != null && !updatedIds.isEmpty()){
			this.setAssignmentAttributeUpdateRecordIds(updatedIds);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getDeleteUpdateBatchCollectionDateRecords(Integer assignmentAttributeId, Set<capi.entity.BatchCollectionDate> oldEntities){
		Set<Integer> oldIds = new HashSet<Integer>();
		if(oldEntities != null){
			for(capi.entity.BatchCollectionDate entity : oldEntities) {
				oldIds.add(entity.getId());
			}
		}
		if(this.getAssignmentAttBatchCollectionDateMap() != null){
			List<BatchCollectionDateEditModel> models = this.getAssignmentAttBatchCollectionDateMap().get(assignmentAttributeId);
			
			Set<Integer> modelIds = new HashSet<Integer>();
			List<BatchCollectionDateEditModel> newRecords = new ArrayList<BatchCollectionDateEditModel>();  
			if(models != null){
				for(BatchCollectionDateEditModel model: models){
					if(model.getBatchCollectionDateId() == null || model.getBatchCollectionDateId() == 0){
						newRecords.add(model);
					} else {
						modelIds.add(model.getBatchCollectionDateId());
					}
				}
				this.setNewBatchCollectionDateRecords(newRecords);
			}
			
			Collection<Integer> deleteIds = (Collection<Integer>) CollectionUtils.subtract(oldIds, modelIds);
			if(deleteIds != null && !deleteIds.isEmpty()){
				this.setBatchCollectionDateDeleteRecordIds(deleteIds);
			}
			Collection<Integer> updatedIds = (Collection<Integer>) CollectionUtils.intersection(oldIds, modelIds);
			if(updatedIds != null && !updatedIds.isEmpty()){
				this.setBatchCollectionDateUpdateRecordIds(updatedIds);
			}
		}
	}
	
	private Map<Integer, List<AssignmentAttributeEditModel>> groupAssignmentAttributesByBatchId(){
		Map<Integer, List<AssignmentAttributeEditModel>> editModels = new Hashtable<>();
		for (AssignmentAttributeEditModel item : this.assignmentAttributes) {
			List<AssignmentAttributeEditModel> assignmentAttributes = null;
			assignmentAttributes = editModels.get(item.getBatchId());

			if(assignmentAttributes != null){
				assignmentAttributes.add(item);
			} else {
				List<AssignmentAttributeEditModel> list = new ArrayList<>();
				list.add(item);
				editModels.put(item.getBatchId(), list);
			}
		}
		return editModels;
	}
	
	public List<AssignmentAttributeEditModel> getAssignmentAttributesByBatchId(Integer batchId){
		List<AssignmentAttributeEditModel> list = new ArrayList<>();
		if (this.getAllocationAttrBatchIdMap() != null && !this.getAllocationAttrBatchIdMap().isEmpty()){
			list = this.getAllocationAttrBatchIdMap().get(batchId);
		}
		return list; 
	}

	public List<BatchCollectionDateEditModel> getBatchCollectionDatesByBatch(Integer batchId){
		if(batchId != null && batchId > 0){
			for(AssignmentAttributeEditModel model : this.getAssignmentAttributes()){
				if(model.getBatchId()!= null && model.getBatchId().equals(batchId)){
					return model.getBatchCollectionDates();
				}
			}
		}
		return null;
	}
	
	private Map<Integer, AssignmentAttributeEditModel> getAssignmentAttributeByRefId(){
		Map<Integer, AssignmentAttributeEditModel> refIdAssignmentAttMap = new HashMap<>();
		if(this.getAssignmentAttributes() != null && !this.getAssignmentAttributes().isEmpty()){
			for(AssignmentAttributeEditModel item : this.getAssignmentAttributes()){
				refIdAssignmentAttMap.put(item.getAssignmentAttrRefId(), item);
			}
		}
		return refIdAssignmentAttMap;
	}
	
	public void convert(SurveyMonthSession sessionModel, Integer status, Boolean isDraft, capi.entity.SurveyMonth oldEntity) throws ParseException{
		this.surveyMonthId = sessionModel.getSessionSurveyMonth().getId();
		this.setReferenceMonth(sessionModel.getSessionSurveyMonth().getReferenceMonth());
		this.setStartDate(sessionModel.getSessionSurveyMonth().getStartDate());
		this.setEndDate(sessionModel.getSessionSurveyMonth().getEndDate());
		this.setClosingDateId(sessionModel.getSessionSurveyMonth().getClosingDateId());
		this.setStatus(status);
		this.setIsDraft(isDraft);
		
		List<AllocationBatchEditModel> allocationBatches = new ArrayList<AllocationBatchEditModel>();
		if(sessionModel.getSessionNewAllocationBatch() != null && !sessionModel.getSessionNewAllocationBatch().isEmpty()){
			Map<Integer, AllocationBatchEditModel> mapper = new HashMap<>();
			for(AllocationBatch model : sessionModel.getSessionNewAllocationBatch()){
				AllocationBatchEditModel editModel = new AllocationBatchEditModel();
				editModel.convert(model, this.getSurveyMonthId());
				allocationBatches.add(editModel);
				if(editModel.getAllocationBatchId() != null){
					mapper.put(editModel.getAllocationBatchId(), editModel);
				}
			}
			this.setAllocationBatchIdAllocationBatchMap(mapper);
		}
		this.setAllocationBatches(allocationBatches);
		//this.getDeleteUpdateAllocationBatchRecords(oldEntity.getAllocationBatches());
		
		Map<Integer, Map<String, BackTrackDate>> backTrackDateDisplayModelMap = new HashMap<>();
		for (BackTrackDateDisplayModel displayModel : sessionModel.getBackTrackDateModelList()) { 
			Map<String, BackTrackDate> backTrackDates = backTrackDateDisplayModelMap.get(displayModel.getBatchId());
			if(backTrackDates == null){
				backTrackDates = new HashMap<>();
			}
			Integer batchId = 0;
			if(displayModel.getBackTrackDayList() != null){
				for(BackTrackDate backTrackDate : displayModel.getBackTrackDayList()){
					batchId = backTrackDate.getBatchId();
					if(backTrackDate.getReferenceCollectionDateStr() != null && !backTrackDate.getReferenceCollectionDateStr().isEmpty()){
						backTrackDates.put(backTrackDate.getReferenceCollectionDateStr(), backTrackDate);
					}
				}
			}
			backTrackDateDisplayModelMap.put(batchId, backTrackDates);
		}
		
		List<AssignmentAttributeEditModel> assignmentAttributes = new ArrayList<AssignmentAttributeEditModel>();
		if(sessionModel.getSessionNewAssignmentAttr() != null && !sessionModel.getSessionNewAssignmentAttr().isEmpty()){
			Map<Integer, AssignmentAttributeEditModel> mapper = new HashMap<>();
			Map<Integer, List<AssignmentAttributeEditModel>> batchIdMapper = new HashMap<>();
			for(AssignmentAttributes model : sessionModel.getSessionNewAssignmentAttr()){
				AssignmentAttributeEditModel editModel = new AssignmentAttributeEditModel();
				Map<String, BackTrackDate> displayModel = backTrackDateDisplayModelMap.get(model.getBatchId());
				if(!editModel.convert(model, this.getSurveyMonthId(), displayModel)) { 
					assignmentAttributes.add(editModel);
					List<AssignmentAttributeEditModel> assignmentAttrList = batchIdMapper.get(editModel.getBatchId());
					if(assignmentAttrList != null){
						assignmentAttrList.add(editModel);
					} else {
						assignmentAttrList = new ArrayList<>(Arrays.asList(editModel));
						batchIdMapper.put(editModel.getBatchId(), assignmentAttrList);
					}
					
					if(editModel.getAssignmentAttributeId() != null){
						mapper.put(editModel.getAssignmentAttributeId(),editModel);
					}
				}
			}
			this.setIdAssignmentAttributeMap(mapper);
			this.setBatchIdAssignmentAttributeMap(batchIdMapper);
		}
		
		if(sessionModel.getSessionNonCateAssignmentAttr() != null && !sessionModel.getSessionNonCateAssignmentAttr().isEmpty()){
			Map<Integer, AssignmentAttributeEditModel> mapper = new HashMap<>();
			Map<Integer, List<AssignmentAttributeEditModel>> batchIdMapper = new HashMap<>();
			for(AssignmentAttributes model : sessionModel.getSessionNonCateAssignmentAttr()){
				AssignmentAttributeEditModel editModel = new AssignmentAttributeEditModel();
				Map<String, BackTrackDate> displayModel = backTrackDateDisplayModelMap.get(model.getBackTrackDateDisplayModelId());
				if(!editModel.convert(model, this.getSurveyMonthId(), displayModel)) { 
					assignmentAttributes.add(editModel);
					List<AssignmentAttributeEditModel> assignmentAttrList = batchIdMapper.get(editModel.getBatchId());
					if(assignmentAttrList != null){
						assignmentAttrList.add(editModel);
					} else {
						assignmentAttrList = new ArrayList<>(Arrays.asList(editModel));
						batchIdMapper.put(editModel.getBatchId(), assignmentAttrList);
					}

					if(editModel.getAssignmentAttributeId() != null){
						mapper.put(editModel.getAssignmentAttributeId(),editModel);
					}
				}
			}
			this.getIdAssignmentAttributeMap().putAll(mapper);
			this.getBatchIdAssignmentAttributeMap().putAll(batchIdMapper);
		}
		this.setAssignmentAttributes(assignmentAttributes);
		this.setAllocationAttrBatchIdMap(this.groupAssignmentAttributesByBatchId());
		
		if(this.getAssignmentAttributes() != null && !this.getAssignmentAttributes().isEmpty()){
			List<BatchCollectionDateEditModel> batchCollectionDates = new ArrayList<BatchCollectionDateEditModel>();
			Map<Integer, BatchCollectionDateEditModel> mapper = new HashMap<>();
			for(AssignmentAttributeEditModel model : this.getAssignmentAttributes()){
				List<BatchCollectionDateEditModel> dates = model.getBatchCollectionDates();
				if(dates != null && !dates.isEmpty()){
					for(BatchCollectionDateEditModel date : dates){
						batchCollectionDates.add(date);
						if(date.getBatchCollectionDateId() != null){
							mapper.put(date.getBatchCollectionDateId(), date);
						}
					}
				}
			}
			this.setIdBatchCollectionDateMap(mapper);
			this.setBatchCollectionDates(batchCollectionDates);
		}
		
		if(batchCollectionDates != null && batchCollectionDates.size()>0){
			Map<Integer, List<BatchCollectionDateEditModel>> mapper = new HashMap<Integer, List<BatchCollectionDateEditModel>>();
			for(BatchCollectionDateEditModel model : batchCollectionDates){
				List<BatchCollectionDateEditModel> list = mapper.get(model.getAssignmentAttributeId());
				if(list != null){
					list.add(model);
				} else {
					List<BatchCollectionDateEditModel> newList = new ArrayList<>();
					newList.add(model);
					mapper.put(model.getAssignmentAttributeId(), newList);
				}
			}
			this.setAssignmentAttBatchCollectionDateMap(mapper);
		}
		
		sessionModel.getBackTrackDateModelList();
	}
}
