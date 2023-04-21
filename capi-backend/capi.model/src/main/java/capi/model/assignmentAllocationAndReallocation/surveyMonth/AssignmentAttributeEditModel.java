package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDate;

public class AssignmentAttributeEditModel {

	private Integer assignmentAttributeId;
	private String batchCategory;
	private Date startDate;
	private Date endDate;
	private String session;
	private Integer userId;
	private Integer batchId;
	private Integer allocationBatchId;
	private Integer surveyMonthId;
	private Integer assignmentAttrRefId;
	private List<BatchCollectionDateEditModel> batchCollectionDates;
	
	private Collection<Integer> batchCollectionDateDeleteRecordIds;
	private Collection<Integer> batchCollectionDateUpdateRecordIds;
	private Collection<Integer> batchCollectionDateNewRecordIds;
	
	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}
	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}
	public String getBatchCategory() {
		return batchCategory;
	}
	public void setBatchCategory(String batchCategory) {
		this.batchCategory = batchCategory;
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
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}
	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}
	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	public Integer getAssignmentAttrRefId() {
		return assignmentAttrRefId;
	}
	public void setAssignmentAttrRefId(Integer assignmentAttrRefId) {
		this.assignmentAttrRefId = assignmentAttrRefId;
	}
	public List<BatchCollectionDateEditModel> getBatchCollectionDates() {
		return batchCollectionDates;
	}
	public void setBatchCollectionDates(List<BatchCollectionDateEditModel> batchCollectionDates) {
		this.batchCollectionDates = batchCollectionDates;
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
	public Collection<Integer> getBatchCollectionDateNewRecordIds() {
		return batchCollectionDateNewRecordIds;
	}
	public void setBatchCollectionDateNewRecordIds(Collection<Integer> batchCollectionDateNewRecordIds) {
		this.batchCollectionDateNewRecordIds = batchCollectionDateNewRecordIds;
	}
	
	@SuppressWarnings("unchecked")
	public void getDeleteUpdateBatchCollectionDateRecords(Set<capi.entity.BatchCollectionDate> oldEntities){
		Set<Integer> oldIds = new HashSet<Integer>();
		if(oldEntities != null){
			for(capi.entity.BatchCollectionDate entity : oldEntities) {
				oldIds.add(entity.getId());
			}
		}
		Set<Integer> modelIds = new HashSet<Integer>();
		if(this.getBatchCollectionDates() != null){
			for(BatchCollectionDateEditModel model: this.getBatchCollectionDates()){
				modelIds.add(model.getBatchCollectionDateId());
			}
		}
		Collection<Integer> deleteIds = (Collection<Integer>) CollectionUtils.subtract(oldIds, modelIds);
		if(deleteIds != null && !deleteIds.isEmpty()){
			this.setBatchCollectionDateDeleteRecordIds(deleteIds);
		}
		Collection<Integer> updatedIds = (Collection<Integer>) CollectionUtils.intersection(oldIds, modelIds);
		if(updatedIds != null && !updatedIds.isEmpty()){
			this.setBatchCollectionDateUpdateRecordIds(updatedIds);
		}
		Collection<Integer> newIds = (Collection<Integer>) CollectionUtils.subtract(modelIds, oldIds);
		if(newIds != null && !newIds.isEmpty()){
			this.setBatchCollectionDateNewRecordIds(newIds);
		}
	}

	private void convertCollectionDateStrToList(String collectionDates, String assignmentAttributesReferenceId, int batchId, String batchCode) throws ParseException{
		if(collectionDates.length() > 0){
			List<String> dateStrList = Arrays.asList(collectionDates.split("\\s*,\\s*"));
			BackTrackDateDisplayModel displayModel = new BackTrackDateDisplayModel();
			for(String dateStr : dateStrList){
				BackTrackDate backTrackDateModel = new BackTrackDate();
				backTrackDateModel.setReferenceCollectionDateStr(dateStr);
				SimpleDateFormat format = new SimpleDateFormat(SystemConstant.DATE_FORMAT);
				Date referenceCollectionDate = format.parse(dateStr);
				if(referenceCollectionDate != null){
					backTrackDateModel.setReferenceCollectionDate(referenceCollectionDate);
				}
				backTrackDateModel.setAssignmentAttributesReferenceId(assignmentAttributesReferenceId);
				backTrackDateModel.setBatchId(batchId);
				backTrackDateModel.setBatchCode(batchCode);
				displayModel.setBatchCode(batchCode);
				displayModel.getBackTrackDayList().add(backTrackDateModel);
			}
			Collections.sort(displayModel.getBackTrackDayList(), new Comparator<BackTrackDate>(){
				@Override
				public int compare(BackTrackDate o1,
						BackTrackDate o2) {
					// TODO Auto-generated method stub
					return o1.getReferenceCollectionDate().compareTo(o2.getReferenceCollectionDate());
				}
				
			});
		}
	}
	
	public boolean convert(AssignmentAttributes sessionModel, Integer surveyMonthId, Map<String, BackTrackDate> displayModel) throws ParseException{
		boolean isEmpty = sessionModel.getReferenceId().trim().isEmpty();
		if(!isEmpty){
			Integer refId = null;
			if(sessionModel.getReferenceId() != null && !sessionModel.getReferenceId().isEmpty() && sessionModel.getReferenceId().startsWith("new-")){
				refId = Integer.valueOf(sessionModel.getReferenceId().replace("new-", ""));
			}
			Integer allocationBatchId = null;
			if(sessionModel.getAllocationBatchRefId() != null && !sessionModel.getAllocationBatchRefId().isEmpty() && sessionModel.getAllocationBatchRefId().startsWith("new-")){
				allocationBatchId = Integer.valueOf(sessionModel.getAllocationBatchRefId().replace("new-", ""));
			}
			
			this.setAssignmentAttributeId(sessionModel.getAssignmentAttributeId());
			this.setBatchCategory(sessionModel.getCategory());
			this.setStartDate(sessionModel.getStartDate());
			this.setEndDate(sessionModel.getEndDate());
			this.setSession(sessionModel.getSession());
			this.setUserId(sessionModel.getOfficerIds());
			this.setBatchId(sessionModel.getBatchId());
			this.setAssignmentAttrRefId(refId);
			this.setAllocationBatchId(allocationBatchId);
			this.setSurveyMonthId(surveyMonthId);

			//Update CollectionDateList if CollectionDateStr is not null and CollectionDateList is null
			if(sessionModel.getCollectionDatesStr().length() > 0 && (sessionModel.getCollectionDateList() == null || (sessionModel.getCollectionDateList() != null && sessionModel.getCollectionDateList().isEmpty()))){
				List<String> dateStrList = Arrays.asList(sessionModel.getCollectionDatesStr().split("\\s*,\\s*"));
				BackTrackDateDisplayModel backTrackDateDisplayModel = new BackTrackDateDisplayModel();
				for(String dateStr : dateStrList){
					BackTrackDate backTrackDateModel = new BackTrackDate();
					backTrackDateModel.setReferenceCollectionDateStr(dateStr);
					SimpleDateFormat format = new SimpleDateFormat(SystemConstant.DATE_FORMAT);
					Date referenceCollectionDate = format.parse(dateStr);
					if(referenceCollectionDate != null){
						backTrackDateModel.setReferenceCollectionDate(referenceCollectionDate);
					}
					if(sessionModel.getReferenceId() != null){
						backTrackDateModel.setAssignmentAttributesReferenceId(sessionModel.getReferenceId());
					}
					backTrackDateModel.setBatchId(batchId);
					backTrackDateDisplayModel.getBackTrackDayList().add(backTrackDateModel);
				}
				Collections.sort(backTrackDateDisplayModel.getBackTrackDayList(), new Comparator<BackTrackDate>(){
					@Override
					public int compare(BackTrackDate o1,
							BackTrackDate o2) {
						// TODO Auto-generated method stub
						return o1.getReferenceCollectionDate().compareTo(o2.getReferenceCollectionDate());
					}
					
				});
				sessionModel.getCollectionDateList().add(backTrackDateDisplayModel);
			}
			
			if(sessionModel.getCollectionDateList() != null && !sessionModel.getCollectionDateList().isEmpty()){
				List<BatchCollectionDateEditModel> batchCollectionDates = new ArrayList<BatchCollectionDateEditModel>();
				for(BackTrackDateDisplayModel collectionDateList : sessionModel.getCollectionDateList()){
					if(collectionDateList.getBackTrackDayList() != null && !collectionDateList.getBackTrackDayList().isEmpty()){
						for(BackTrackDate batchCollectionDate : collectionDateList.getBackTrackDayList()){
							if(batchCollectionDate.getAssignmentAttributesReferenceId() != null && batchCollectionDate.getAssignmentAttributesReferenceId().equals(sessionModel.getReferenceId())){
								if(batchCollectionDate.getReferenceCollectionDateStr() != null && !batchCollectionDate.getReferenceCollectionDateStr().isEmpty()){
									if(displayModel != null){
										BackTrackDate backTrackDate = displayModel.get(batchCollectionDate.getReferenceCollectionDateStr());
										if(backTrackDate != null && backTrackDate.getAssignmentAttributesReferenceId().equals(sessionModel.getReferenceId())){
											if(backTrackDate.getBackTrackDateList() != null && !backTrackDate.getBackTrackDateList().isEmpty()){
												batchCollectionDate.setBackTrackDateList(backTrackDate.getBackTrackDateList());
											}
										}
									}
									BatchCollectionDateEditModel editModel = new BatchCollectionDateEditModel();
									editModel.convert(batchCollectionDate, this.assignmentAttributeId, refId);
									batchCollectionDates.add(editModel);
								}
							}
						}
					}
				}
				this.setBatchCollectionDates(batchCollectionDates);
			}
			
		}
		return isEmpty;
	}
}
