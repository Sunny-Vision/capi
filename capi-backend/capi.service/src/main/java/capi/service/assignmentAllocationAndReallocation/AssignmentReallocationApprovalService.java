package capi.service.assignmentAllocationAndReallocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AssignmentDao;
import capi.dal.AssignmentReallocationDao;
import capi.dal.NotificationDao;
import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.dal.QuotationRecordDao;
import capi.dal.TpuDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.AssignmentReallocation;
import capi.entity.PECheckForm;
import capi.entity.PECheckTask;
import capi.entity.QuotationRecord;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationApprovalEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationApprovalTableList;
import capi.service.BaseService;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;

@Service("AssignmentReallocationApprovalService")
public class AssignmentReallocationApprovalService extends BaseService {

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
	private NotificationDao notificationDao;

	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;

	@Autowired
	private PECheckFormDao peCheckFormDao;

	@Autowired
	private PECheckTaskDao peCheckTaskDao;

	@Resource(name="messageSource")
	MessageSource messageSource;

	/**
	 * Get by ID
	 */
	public AssignmentReallocation getAssignmentReallocationById(int id) {
		return assignmentReallocationDao.findById(id);
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
	public DatatableResponseModel<AssignmentReallocationApprovalTableList> getAssignmentReallocationApproval(DatatableRequestModel model, List<Integer> actedUsers){

		Order order = this.getOrder(model, "originalFieldOfficer", "originalFieldOfficerName", "targetFieldOfficer", "targetFieldOfficerName",
									"countAssignment", "countQuotationRecord");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationApprovalTableList> result = 
				assignmentReallocationDao.selectAllAssignmentReallocationApproval(search, model.getStart(), model.getLength(), order, actedUsers);
		
		DatatableResponseModel<AssignmentReallocationApprovalTableList> response = new DatatableResponseModel<AssignmentReallocationApprovalTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countSelectAllAssignmentReallocationApproval("", actedUsers);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countSelectAllAssignmentReallocationApproval(search, actedUsers);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<AssignmentReallocationApprovalEditModel> getAssignmentReallocationApprovalList(DatatableRequestModel model, 
						Integer originalUserId, Integer targetUserId,
						List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate, String selected,
						Integer assignmentStatus, Integer surveyMonthId, Integer assignmentReallocationId){

		Order order = this.getOrder(model, "firm", "district", "tpu", "batchCode", "noOfQuotation",
									"startDate", "endDate", "collectionDate", "assignmentStatus", "selected");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationApprovalEditModel> result = assignmentReallocationDao.getAssignmentReallocationApprovalList(
														search, model.getStart(), model.getLength(), order, originalUserId, targetUserId, tpuIds, 
														outletTypeId, districtId, batchId, collectionDate, selected, assignmentStatus, surveyMonthId, assignmentReallocationId);
		List<AssignmentReallocationApprovalEditModel.BatchCodeMapping> batchs = assignmentReallocationDao.getAssignmentReallocationApprovalBatchList
				(search, model.getStart(), model.getLength(), order, originalUserId, targetUserId, tpuIds, outletTypeId, districtId, 
				batchId, collectionDate, selected, assignmentStatus, surveyMonthId);
		
		for(AssignmentReallocationApprovalEditModel consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(AssignmentReallocationApprovalEditModel.BatchCodeMapping batch : batchs) {
				if(batch.getAssignmentId().equals(consequence.getAssignmentId())) {
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
		
		DatatableResponseModel<AssignmentReallocationApprovalEditModel> response = new DatatableResponseModel<AssignmentReallocationApprovalEditModel>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countAssignmentReallocationApprovalList("", 
							originalUserId, targetUserId, null, null, null, null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countAssignmentReallocationApprovalList(search, 
								originalUserId, targetUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, selected, assignmentStatus, surveyMonthId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<AssignmentReallocationApprovalEditModel> getQuotationRecordReallocationApprovalList(DatatableRequestModel model, 
						Integer originalUserId, Integer targetUserId,
						List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate, String selected,
						String category, String quotationStatus, Integer assignmentReallocationId){

		Order order = this.getOrder(model, "firm", "district", "tpu", "batchCode", "collectionDate", "startDate", "endDate", 
									"displayName", "category", "quotationStatus", "selected");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationApprovalEditModel> result = assignmentReallocationDao.getQuotationRecordReallocationApprovalList(
														search, model.getStart(), model.getLength(), order, 
														originalUserId, targetUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, selected,
														category, quotationStatus, assignmentReallocationId);

		long haveSelected = assignmentReallocationDao.countSelectedQuotationRecordReallocation(originalUserId, targetUserId, assignmentReallocationId);
		boolean flag = true;
		/*
		if(result != null) {
			for(int i = 0; i < result.size(); i++) {
				if("Y".equals(result.get(i).getSelected())) {
					flag = false;
					break;
				}
			}
			if(flag) {
				result = new ArrayList<AssignmentReallocationApprovalEditModel>();
			}
		}
		*/
		if(haveSelected > 0) {
			flag = false;
		}
		if(flag) {
			result = new ArrayList<AssignmentReallocationApprovalEditModel>();
		}
		DatatableResponseModel<AssignmentReallocationApprovalEditModel> response = new DatatableResponseModel<AssignmentReallocationApprovalEditModel>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countQuotationRecordReallocationApprovalList("", 
							originalUserId, targetUserId, null, null, null, null, null, null, null, null);
		if(flag) {
			recordTotal = Long.valueOf("0");
		}
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countQuotationRecordReallocationApprovalList(search, 
								originalUserId, targetUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, selected, category, quotationStatus);
		if(flag) {
			recordFiltered = Long.valueOf("0");
		}
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * approve Assignment Reallocation
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean approveAssignmentReallocation(AssignmentReallocationApprovalEditModel model) throws Exception {

		AssignmentReallocation oldEntity = null;
		if (model.getAssignmentReallocationId() != null && model.getAssignmentReallocationId() > 0) {
			oldEntity = getAssignmentReallocationById(model.getAssignmentReallocationId());
			
			if(model.getTargetUserId() == null) return false;
			
			User targetUser = userService.getUserById(model.getTargetUserId());
			
			List<Integer> assignmentIds = model.getAssignmentIds();
			// remove null
			for(int i = 0; i < assignmentIds.size(); i++) {
				if(assignmentIds.get(i) == null) {
					assignmentIds.remove(i);
				} else {}
			}
			
			List<Integer> quotationRecordIds = model.getQuotationRecordIds();
			// remove null
			for(int i = 0; i < quotationRecordIds.size(); i++) {
				if(quotationRecordIds.get(i) == null) {
					quotationRecordIds.remove(i);
				} else {}
			}
			
			for(int i = 0; i < assignmentIds.size(); i++) {
				List<QuotationRecord> QuotationRecords = quotationRecordDao.getUndoQuotationRecordsByAssignmentId(assignmentIds.get(i));
				for(QuotationRecord quotationRecord : QuotationRecords) {
					quotationRecord.setUser(targetUser);
					quotationRecordDao.save(quotationRecord);
					for(QuotationRecord otherQuotationRecord : quotationRecord.getOtherQuotationRecords()) {
						otherQuotationRecord.setUser(targetUser);
						quotationRecordDao.save(otherQuotationRecord);
					}
				}
				
				Assignment assignment = assignmentDao.findById(assignmentIds.get(i));
				assignment.setUser(targetUser);
				
				assignment.setModifiedDate(assignment.getModifiedDate());
				assignment.setByPassModifiedDate(true);
				assignmentDao.save(assignment);
			}
			
			quotationRecordDao.flush();
			assignmentDao.flush();
			
			// handle pe case after reallocate
			List<Assignment> assignments = assignmentDao.getByIds(assignmentIds.toArray(new Integer[0]));
			runPELogic(assignments, model.getOriginalUserId(), model.getTargetUserId());
			
			if(quotationRecordIds!=null && quotationRecordIds.size()>0){
				List<QuotationRecord> QuotationRecords = quotationRecordDao.getQuotationRecordsByIds(quotationRecordIds);
				for(QuotationRecord quotationRecord : QuotationRecords) {
					quotationRecord.setUser(targetUser);
					quotationRecord.setModifiedDate(quotationRecord.getModifiedDate());
					quotationRecord.setByPassModifiedDate(true);
					quotationRecordDao.save(quotationRecord);
					for(QuotationRecord otherQuotationRecord : quotationRecord.getOtherQuotationRecords()) {
						otherQuotationRecord.setUser(targetUser);
						otherQuotationRecord.setModifiedDate(otherQuotationRecord.getModifiedDate());
						otherQuotationRecord.setByPassModifiedDate(true);
						quotationRecordDao.save(otherQuotationRecord);
					}
				}
				quotationRecordDao.flush();
			}
			
			oldEntity.setStatus("Approved");
			oldEntity.setApprovedDate(new Date());
			oldEntity.setRejectReason(model.getRejectReason());
			
			assignmentReallocationDao.save(oldEntity);
			assignmentReallocationDao.flush();
			
			return true;
		}
		
		return false;
	}

	/**
	 * reject Assignment Reallocation
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean rejectAssignmentReallocation(AssignmentReallocationApprovalEditModel model) throws Exception {

		AssignmentReallocation oldEntity = null;
		if (model.getAssignmentReallocationId() != null && model.getAssignmentReallocationId() > 0) {
			oldEntity = getAssignmentReallocationById(model.getAssignmentReallocationId());
			
			oldEntity.setSubmitToApprove(null);
			oldEntity.setSubmitToUser(null);
			
			oldEntity.setStatus("Rejected");
			oldEntity.setRejectReason(model.getRejectReason());
			
			assignmentReallocationDao.save(oldEntity);
			assignmentReallocationDao.flush();
			
			return true;
		}
		
		return false;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public AssignmentReallocationApprovalEditModel convertEntityToModel(AssignmentReallocation entity){

		AssignmentReallocationApprovalEditModel model = new AssignmentReallocationApprovalEditModel();
		BeanUtils.copyProperties(entity, model);

		if(entity.getCreator() != null) {
			model.setCreatorId(entity.getCreator().getUserId());
			model.setCreator(entity.getCreator().getStaffCode() + " - " + entity.getCreator().getChineseName());
		}
		if(entity.getOriginalUser() != null) {
			model.setOriginalUserId(entity.getOriginalUser().getUserId());
			model.setOriginalUser(entity.getOriginalUser().getStaffCode() + " - " + entity.getOriginalUser().getChineseName());
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

	@Transactional
	public void runPELogic(List<Assignment> entities, Integer fromUserId, Integer toUserId) {
		
		List<Integer> assignmentIds = new ArrayList<Integer> ();
		
		for (Assignment entity : entities) {
			if (entity.getPeCheckTask() != null && entity.getPeCheckTask().isSelected()) {
				
				// remove one PE of "to user"
				removePEFromRandom(toUserId, entity.getSurveyMonth().getSurveyMonthId());
				
				List<PECheckTask> randomTaskAndForms = assignmentMaintenanceService.generateRandomCase(fromUserId, entity.getSurveyMonth().getId(), 1);
				
				for (PECheckTask taskAndForm : randomTaskAndForms) {
					taskAndForm.setFieldTeamHead(entity.getPeCheckTask().isFieldTeamHead());
					taskAndForm.setSectionHead(entity.getPeCheckTask().isSectionHead());
					
					assignmentIds.add(taskAndForm.getAssignment().getAssignmentId());
				}
				
				entity.getPeCheckTask().setFieldTeamHead(false);
				entity.getPeCheckTask().setSectionHead(false);
			}
		}
		if(assignmentIds.size() > 0) {
			// generate PE form if the whole assignment is approved (i.e. all quotation records in the assignment is approved)
			generatePEForm(assignmentIds);
		}
	}
	
	/**
	 * remove pe case of one random case of "to user"
	 */
	@Transactional
	public void removePEFromRandom(Integer userId, Integer surveyMonthId) {
		 List<PECheckTask> list = peCheckTaskDao.getNotSubmittedRandomCase(userId, 1, surveyMonthId);
		 PECheckTask oldTask = null;
		 if (list.size() > 0){
			 oldTask = list.get(0);
			
			if (oldTask.getAssignment().getPeCheckForm() != null){
				peCheckFormDao.delete(oldTask.getAssignment().getPeCheckForm());			
			}
			oldTask.setSelected(false);
		}		
	}
	
	public void generatePEForm (List<Integer> assignmentIds){
		List<Integer> approvedIds = assignmentDao.getApprovedAssignment(assignmentIds);
		List<Assignment> approvedAssignments = assignmentDao.getByIds(approvedIds.toArray(new Integer[0]));		
		if (approvedAssignments != null && approvedAssignments.size() > 0){
			// get field head and section head if any
			List<User> heads = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD, null);
			User fieldHead = null;
			if (heads != null && heads.size() > 0){
				 fieldHead = heads.get(0);
			}
			User sectionHead = null;
			heads = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD, null);
			if (heads != null && heads.size() > 0){
				sectionHead = heads.get(0);
			}
			for (Assignment assignment : approvedAssignments){
				if (assignment.getPeCheckTask() != null && assignment.getPeCheckTask().isSelected() && assignment.getPeCheckForm() == null){
					PECheckForm form = assignment.getPeCheckForm();
					if (form == null) {
						form = new PECheckForm();
						form.setAssignment(assignment);
					}
					form.setOfficer(assignment.getUser());
					if (assignment.getPeCheckTask().isFieldTeamHead()) {
						form.setUser(fieldHead);
					} else if (assignment.getPeCheckTask().isSectionHead()) {
						form.setUser(sectionHead);
					} else {
						form.setUser(assignment.getUser().getSupervisor());
					}
					peCheckFormDao.save(form);
				}
			}
		}	
	}

}
