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
import capi.dal.QuotationRecordDao;
import capi.dal.TpuDao;
import capi.dal.UserDao;
import capi.entity.AssignmentReallocation;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationRecommendationEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationRecommendationTableList;
import capi.service.BaseService;
import capi.service.UserService;

@Service("AssignmentReallocationRecommendationService")
public class AssignmentReallocationRecommendationService extends BaseService {

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

	@Resource(name="messageSource")
	MessageSource messageSource;

	/**
	 * Get by ID
	 */
	public AssignmentReallocation getAssignmentReallocationById(int id) {
		return assignmentReallocationDao.findById(id);
	}

	/**
	 * Get all field team head and section head
	 */
	public List<User> getHeads() {
		return userDao.getActiveUsersWithAnyAuthorityLevel(1|2);
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
	public DatatableResponseModel<AssignmentReallocationRecommendationTableList> getAssignmentReallocationRecommendation(DatatableRequestModel model, List<Integer> actedUsers){

		Order order = this.getOrder(model, "createdDate", "originalFieldOfficer", "originalFieldOfficerName", "targetFieldOfficer", "targetFieldOfficerName",
									"countAssignment", "countQuotationRecord");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationRecommendationTableList> result = 
				assignmentReallocationDao.selectAllAssignmentReallocationRecommendation(search, model.getStart(), model.getLength(), order, actedUsers);
		
		DatatableResponseModel<AssignmentReallocationRecommendationTableList> response = new DatatableResponseModel<AssignmentReallocationRecommendationTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countSelectAllAssignmentReallocationRecommendation("", actedUsers);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countSelectAllAssignmentReallocationRecommendation(search, actedUsers);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<AssignmentReallocationRecommendationEditModel> getAssignmentReallocationRecommendationList(DatatableRequestModel model, 
						Integer originalUserId, Integer targetUserId,
						List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate, String selected,
						Integer assignmentStatus, Integer surveyMonthId, Integer assignmentReallocationId){

		Order order = this.getOrder(model, "firm", "district", "tpu", "batchCode", "noOfQuotation",
									"startDate", "endDate", "collectionDate", "assignmentStatus", "selected", "rejectReason");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationRecommendationEditModel> result = assignmentReallocationDao.getAssignmentReallocationRecommendationList(search, model.getStart(), model.getLength(), order, 
														originalUserId, targetUserId, tpuIds, outletTypeId, districtId
														, batchId, collectionDate, selected, assignmentStatus, surveyMonthId, assignmentReallocationId);
		List<AssignmentReallocationRecommendationEditModel.BatchCodeMapping> batchs = assignmentReallocationDao.getAssignmentReallocationRecommendationBatchList
				(search, model.getStart(), model.getLength(), order, 
				originalUserId, targetUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, selected, assignmentStatus, surveyMonthId);
		
		for(AssignmentReallocationRecommendationEditModel consequence : result) {
			StringBuilder sb = new StringBuilder();
			for(AssignmentReallocationRecommendationEditModel.BatchCodeMapping batch : batchs) {
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
		
		DatatableResponseModel<AssignmentReallocationRecommendationEditModel> response = new DatatableResponseModel<AssignmentReallocationRecommendationEditModel>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countAssignmentReallocationRecommendationList("", 
							originalUserId, targetUserId, null, null, null, null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countAssignmentReallocationRecommendationList(search, 
								originalUserId, targetUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, selected, assignmentStatus, surveyMonthId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<AssignmentReallocationRecommendationEditModel> getQuotationRecordReallocationRecommendationList(DatatableRequestModel model, 
						Integer originalUserId, Integer targetUserId,
						List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String collectionDate, String selected,
						String category, String quotationStatus, Integer assignmentReallocationId){

		Order order = this.getOrder(model, "firm", "district", "tpu", "batchCode", "collectionDate", "startDate", "endDate", 
									"displayName", "category", "quotationStatus", "selected");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentReallocationRecommendationEditModel> result = assignmentReallocationDao.getQuotationRecordReallocationRecommendationList(
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
				result = new ArrayList<AssignmentReallocationRecommendationEditModel>();
			}
		}
		*/
		if(haveSelected > 0) {
			flag = false;
		}
		if(flag) {
			result = new ArrayList<AssignmentReallocationRecommendationEditModel>();
		}
		DatatableResponseModel<AssignmentReallocationRecommendationEditModel> response = new DatatableResponseModel<AssignmentReallocationRecommendationEditModel>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentReallocationDao.countQuotationRecordReallocationRecommendationList("", 
								originalUserId, targetUserId, null, null, null, null, null, null, null, null);
		if(flag) {
			recordTotal = Long.valueOf("0");
		}
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentReallocationDao.countQuotationRecordReallocationRecommendationList(search, 
								originalUserId, targetUserId, tpuIds, outletTypeId, districtId, batchId, collectionDate, selected, category, quotationStatus);
		if(flag) {
			recordFiltered = Long.valueOf("0");
		}
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * recommend Assignment Reallocation
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public boolean recommendAssignmentReallocation(AssignmentReallocationRecommendationEditModel model) throws Exception {

		AssignmentReallocation oldEntity = null;
		if (model.getAssignmentReallocationId() != null && model.getAssignmentReallocationId() > 0) {
			oldEntity = getAssignmentReallocationById(model.getAssignmentReallocationId());
			
			if(model.getTargetUserId() == null) return false;
			if(model.getSubmitToApproveId() == null) return false;
			
			User submitToApprove = userService.getUserById(model.getSubmitToApproveId());
			
			oldEntity.setSubmitToApprove(submitToApprove);
			
			oldEntity.setStatus("Recommended");
			oldEntity.setRecommendDate(new Date());
			oldEntity.setRejectReason(null);
			
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
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public boolean rejectAssignmentReallocation(AssignmentReallocationRecommendationEditModel model) throws Exception {

		AssignmentReallocation oldEntity = null;
		if (model.getAssignmentReallocationId() != null && model.getAssignmentReallocationId() > 0) {
			oldEntity = getAssignmentReallocationById(model.getAssignmentReallocationId());
			
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
	public AssignmentReallocationRecommendationEditModel convertEntityToModel(AssignmentReallocation entity, Integer userId){

		AssignmentReallocationRecommendationEditModel model = new AssignmentReallocationRecommendationEditModel();
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
		User user = userDao.findById(userId);
		if(user!=null && user.getSupervisor()!=null){
			model.setSubmitToApproveId(user.getSupervisor().getUserId());
			model.setSubmitToApprove(user.getSupervisor().getStaffCode() + " - " + user.getSupervisor().getChineseName());
		}
		
		return model;
	}

}
