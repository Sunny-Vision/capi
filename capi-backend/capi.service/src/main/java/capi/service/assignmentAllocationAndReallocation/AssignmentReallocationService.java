package capi.service.assignmentAllocationAndReallocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AssignmentDao;
import capi.dal.AssignmentReallocationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.TpuDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.AssignmentReallocation;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationTableList;
import capi.model.commonLookup.AssignmentReallocationLookupTableList;
import capi.model.commonLookup.QuotationRecordReallocationLookupTableList;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.UserService;

@Service("AssignmentReallocationService")
public class AssignmentReallocationService extends BaseService {

	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TpuDao tpuDao;

	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private AssignmentDao assignmentDao;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;

	/**
	 * Get by ID
	 */
	public AssignmentReallocation getAssignmentReallocationById(int id) {
		return assignmentReallocationDao.findById(id);
	}

	/**
	 * Get all supervisor
	 */
	public List<User> getSupervisors() {
		return userDao.getActiveUsersWithAnyAuthorityLevel(4);
	}

	/**
	 * Get all tpus
	 */
	public List<Tpu> getTpus() {
		return tpuDao.getAll();
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<AssignmentReallocationTableList> getAssignmentReallocationList(
													DatatableRequestModel model, boolean isFieldOfficer, Integer originalUserId){

		Order order = this.getOrder(model, "createdDate2", "originalUser", "targetUser", "status");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationTableList> result = assignmentReallocationDao.selectAllAssignmentReallocation(
														search, model.getStart(), model.getLength(), order, isFieldOfficer, originalUserId);
		
		DatatableResponseModel<AssignmentReallocationTableList> response = new DatatableResponseModel<AssignmentReallocationTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countSelectAllAssignmentReallocation("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countSelectAllAssignmentReallocation(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save Assignment Reallocation
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void saveAssignmentReallocation(AssignmentReallocationEditModel model) throws Exception {

		AssignmentReallocation oldEntity = null;
		if (model.getAssignmentReallocationId() != null && model.getAssignmentReallocationId() > 0) {
			oldEntity = getAssignmentReallocationById(model.getAssignmentReallocationId());
		} else {
			oldEntity = new AssignmentReallocation();
		}

		if(model.getCreatorId() != null) {
			User creator = userService.getUserById(model.getCreatorId());
			oldEntity.setCreator(creator);
		}
		
		if(model.getOriginalUserId() != null) {
			User originalUser = userService.getUserById(model.getOriginalUserId());
			oldEntity.setOriginalUser(originalUser);
		}
		
		User targetUser = userService.getUserById(model.getTargetUserId());
		if(targetUser != null) {
			oldEntity.setTargetUser(targetUser);
		}
		
		User submitToUser = userService.getUserById(model.getSubmitToUserId());
		if(submitToUser != null) {
			oldEntity.setSubmitToUser(submitToUser);
		}
		
		if(model.getAssignmentIds() != null) {
			ArrayList<Integer> oldAssignmentIds = new ArrayList<Integer>();
			for (Assignment assignment : oldEntity.getAssignments()) {
				oldAssignmentIds.add(assignment.getId());
			}
			
			Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldAssignmentIds, model.getAssignmentIds());
			Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getAssignmentIds(), oldAssignmentIds);
			
			if (deletedIds.size() > 0){
				List<Assignment> deletedAssignments = assignmentDao.getByIds(deletedIds.toArray(new Integer[0]));
				for (Assignment assignment: deletedAssignments){
					oldEntity.getAssignments().remove(assignment);
				}
			}
			
			if (newIds.size() > 0) {
				List<Assignment> newAssignments = assignmentDao.getByIds(newIds.toArray(new Integer[0]));
				oldEntity.getAssignments().addAll(newAssignments);
			}
		} else {
			oldEntity.getAssignments().clear();
		}
		
		if(model.getQuotationRecordIds() != null) {
			ArrayList<Integer> oldQuotationRecordIds = new ArrayList<Integer>();
			for (QuotationRecord quotationRecord : oldEntity.getQuotationRecords()) {
				oldQuotationRecordIds.add(quotationRecord.getId());
			}
			
			Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldQuotationRecordIds, model.getQuotationRecordIds());
			Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getQuotationRecordIds(), oldQuotationRecordIds);
			
			if (deletedIds.size() > 0){
				List<QuotationRecord> deletedQuotationRecords = quotationRecordDao.getByIds(deletedIds.toArray(new Integer[0]));
				for (QuotationRecord quotationRecord: deletedQuotationRecords){
					oldEntity.getQuotationRecords().remove(quotationRecord);
				}
			}
			
			if (newIds.size() > 0) {
				List<QuotationRecord> newQuotationRecords = quotationRecordDao.getByIds(newIds.toArray(new Integer[0]));
				oldEntity.getQuotationRecords().addAll(newQuotationRecords);
			}
		} else {
			oldEntity.getQuotationRecords().clear();
		}
		
		oldEntity.setStatus("Submitted");
		
		assignmentReallocationDao.save(oldEntity);
		assignmentReallocationDao.flush();
	}

	/**
	 * Save Outstanding Assignment
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void saveOutstandingAssignment(AssignmentReallocationEditModel model) throws Exception {

		List<Integer> assignmentIdList = model.getAssignmentIds();
		Integer[] assignmentIds = new Integer[assignmentIdList.size()];
		for(int i = 0; i < assignmentIds.length; i++) {
			assignmentIds[i] = assignmentIdList.get(i);
		}
		
		List<Assignment> assignments = assignmentDao.getByIds(assignmentIds);
		for(Assignment assignment : assignments) {
			List<QuotationRecord> QuotationRecords = quotationRecordDao.getUndoQuotationRecordsByAssignmentId(assignment.getAssignmentId());
			for(QuotationRecord quotationRecord : QuotationRecords) {
				if (!StringUtils.isEmpty(model.getNewDate())){
//					quotationRecord.setCollectionDate(commonService.getDate(model.getNewDate()));
					quotationRecord.setReferenceDate(commonService.getDate(model.getNewDate()));
					quotationRecord.setHistoryDate(commonService.getDate(model.getNewDate()));
					quotationRecord.setAssignedCollectionDate(commonService.getDate(model.getNewDate()));
					quotationRecord.setAssignedStartDate(null);
					quotationRecord.setAssignedEndDate(null);
				} else {
//					quotationRecord.setCollectionDate(null);
//					quotationRecord.setReferenceDate(null);
					quotationRecord.setAssignedCollectionDate(null);
				}
				
				if (!StringUtils.isEmpty(model.getStartDate())){
					quotationRecord.setAssignedStartDate(commonService.getDate(model.getStartDate()));
					quotationRecord.setReferenceDate(commonService.getDate(model.getStartDate()));
					quotationRecord.setHistoryDate(commonService.getDate(model.getStartDate()));
				}
				if (!StringUtils.isEmpty(model.getEndDate())){
					quotationRecord.setAssignedEndDate(commonService.getDate(model.getEndDate()));
				}
				
				quotationRecord.setModifiedDate(quotationRecord.getModifiedDate());
				quotationRecord.setByPassModifiedDate(true);
				quotationRecordDao.save(quotationRecord);
			}
			
			if (!StringUtils.isEmpty(model.getNewDate())){
				assignment.setCollectionDate(commonService.getDate(model.getNewDate()));
				assignment.setAssignedCollectionDate(commonService.getDate(model.getNewDate()));
				assignment.setStartDate(null);
				assignment.setEndDate(null);
			} else {
				assignment.setCollectionDate(null);
				assignment.setAssignedCollectionDate(null);
			}
			
			if (!StringUtils.isEmpty(model.getStartDate())){
				assignment.setStartDate(commonService.getDate(model.getStartDate()));
			}
			if (!StringUtils.isEmpty(model.getEndDate())){
				assignment.setEndDate(commonService.getDate(model.getEndDate()));
			}
			
			assignment.setModifiedDate(assignment.getModifiedDate());
			assignment.setByPassModifiedDate(true);
			assignmentDao.save(assignment);
		}
		quotationRecordDao.flush();
		assignmentDao.flush();
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public AssignmentReallocationEditModel convertEntityToModel(AssignmentReallocation entity, Integer creatorId, Integer authorityLevel){

		AssignmentReallocationEditModel model = new AssignmentReallocationEditModel();
		BeanUtils.copyProperties(entity, model);

		if((authorityLevel & 4) == 4) model.setFieldSupervisor(true);
		else model.setFieldSupervisor(false);
		
		if((authorityLevel & 16) == 16) model.setFieldOfficer(true);
		else model.setFieldOfficer(false);
		
		if((authorityLevel & 8) == 8 || (authorityLevel & 256) == 256) {
			model.setFieldSupervisor(false);
			model.setFieldOfficer(false);
		}
		
		model.setCreatorId(creatorId);
		
		if(entity.getOriginalUser() != null) {
			model.setOriginalUserId(entity.getOriginalUser().getUserId());
			model.setOriginalUser(entity.getOriginalUser().getStaffCode() + " - " + entity.getOriginalUser().getChineseName());
		} else if( (!model.isFieldSupervisor()) && model.isFieldOfficer() ) {
			User user = userDao.findById(creatorId);
			model.setOriginalUserId(user.getUserId());
			model.setOriginalUser(user.getStaffCode() + " - " + user.getChineseName());
		}
		
		if(entity.getTargetUser() != null) {
			model.setTargetUserId(entity.getTargetUser().getUserId());
			model.setTargetUser(entity.getTargetUser().getStaffCode() + " - " + entity.getTargetUser().getChineseName());
		}
		if(entity.getSubmitToUser() != null) {
			model.setSubmitToUserId(entity.getSubmitToUser().getUserId());
		}
		
		List<Integer> assignmentIdList = assignmentReallocationDao.getAssignmentIdsFromReallocatedAssignment(model.getAssignmentReallocationId());
		model.setAssignmentIds(assignmentIdList);
		
		List<Integer> quotationRecordIdList = assignmentReallocationDao.getQuotationRecordIdsFromReallocatedQuotationRecord(model.getAssignmentReallocationId());
		model.setQuotationRecordIds(quotationRecordIdList);
		
		return model;
	}
	
	public String getReferenceMonthStrByAssignmentReallocationId(AssignmentReallocation assignmentReallocation){
		String refMonthStr = "";
		Set<SurveyMonth> surveyMonths = new HashSet<SurveyMonth>();
		
		Set<Assignment> assignmnets = assignmentReallocation.getAssignments();
		for (Assignment assignment : assignmnets) {
			if (!surveyMonths.contains(assignment.getSurveyMonth())){
				surveyMonths.add(assignment.getSurveyMonth());
				refMonthStr += commonService.formatMonth(assignment.getSurveyMonth().getReferenceMonth()) + ", ";
			}
		}
		
		if(StringUtils.isNotBlank(refMonthStr)) {
			refMonthStr = StringUtils.stripEnd(refMonthStr, ", ");
		}
		
		return refMonthStr;
	}
	
	public DatatableResponseModel<AssignmentReallocationLookupTableList> getReallocationAssignmentList(DatatableRequestModel model
			, Integer assignmentReallocationId){
		Order order = this.getOrder(model, "firm", "district", "tpu", "batchCode", "noOfQuotation",
				"startDate", "endDate", "collectionDate", "assignmentStatus");
		
		String search = model.getSearch().get("value");
		List<AssignmentReallocationLookupTableList> result = assignmentReallocationDao.getReallocationAssignmentList(search, model.getStart(), model.getLength(), order, assignmentReallocationId);
		
		List<AssignmentReallocationLookupTableList.BatchCodeMapping> batchs = assignmentReallocationDao.getReallocationAssignmentBatchCodeList(search, assignmentReallocationId);
		
		for(AssignmentReallocationLookupTableList consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(AssignmentReallocationLookupTableList.BatchCodeMapping batch : batchs) {
				if(batch.getAssignmentId().equals(consequence.getId())) {
					sb.append(batch.getBatchCode());
					sb.append(", ");
				}
			}
			if(sb.length() > 2) {
				sb.delete(sb.length()-2, sb.length());
				if("null".equals(sb.toString()))
					consequence.setBatchCode(null);
				else
					consequence.setBatchCode(sb.toString());
			} else {
				consequence.setBatchCode(null);
			}
		}
		
		DatatableResponseModel<AssignmentReallocationLookupTableList> response = new DatatableResponseModel<AssignmentReallocationLookupTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countReallocationAssignmentList("", assignmentReallocationId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countReallocationAssignmentList(search, assignmentReallocationId);;
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	public DatatableResponseModel<QuotationRecordReallocationLookupTableList> getReallocationQuotationRecordList(DatatableRequestModel model
			, Integer assignmentReallocationId){
		Order order = this.getOrder(model, "firm", "district", "tpu", "batchCode", "displayName",
				"category", "startDate", "endDate", "collectionDate");
		
		String search = model.getSearch().get("value");
		List<QuotationRecordReallocationLookupTableList> result = assignmentReallocationDao.getReallocationQuotationRecordList(search, model.getStart(), model.getLength(), order, assignmentReallocationId);
		
		DatatableResponseModel<QuotationRecordReallocationLookupTableList> response = new DatatableResponseModel<QuotationRecordReallocationLookupTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countReallocationQuotationRecordList("", assignmentReallocationId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countReallocationQuotationRecordList(search, assignmentReallocationId);;
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
}
