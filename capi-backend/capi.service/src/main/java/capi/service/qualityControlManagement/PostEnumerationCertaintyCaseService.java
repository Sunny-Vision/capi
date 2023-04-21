package capi.service.qualityControlManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentDao;
import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.dal.SurveyMonthDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.PECheckForm;
import capi.entity.PECheckTask;
import capi.entity.SurveyMonth;
import capi.entity.SystemConfiguration;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.AssignmentAllocationStatisiticModel;
import capi.model.assignmentAllocationAndReallocation.UnAssignedHeadPETask;
import capi.model.qualityControlManagement.InsufficientPEListModel;
import capi.model.qualityControlManagement.OverflowPETaskModel;
import capi.model.qualityControlManagement.PostEnumerationCertaintyCaseEditModel;
import capi.model.qualityControlManagement.PostEnumerationCertaintyCaseTableList;
import capi.service.BaseService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;

@Service("PostEnumerationCertaintyCaseService")
public class PostEnumerationCertaintyCaseService extends BaseService {

	@Autowired
	private SurveyMonthDao surveyMonthDao;

	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private SystemConfigurationDao systemParameterDao;
	
	@Autowired
	private AssignmentMaintenanceService assignmentService;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private PECheckFormDao peCheckFormDao;
	
	@Autowired
	private UserDao userDao;

	/**
	 * Get by ID
	 */
	public SurveyMonth getSurveyMonthById(int id) {
		return surveyMonthDao.findById(id);
	}

	/**
	 * Get PECheck Task ID by Survey Month ID
	 */
	public List<Integer> getSelectedPECheckTaskIdBySurveyMonthId(int surveyMonthId) {
		return peCheckTaskDao.getSelectedPECheckTaskIdBySurveyMonthId(surveyMonthId);
	}

	/**
	 * Get Certainty PECheck Task ID by Assignment ID
	 */
	public List<Integer> getSelectedPECheckTaskIdByAssignmentIds(List<Integer> ids) {
		return peCheckTaskDao.getSelectedPECheckTaskIdByAssignmentIds(ids);
	}
	
	/**
	 * Get Assignment ID by Survey Month ID
	 */
	public List<Integer> getSelectedAssignmentIdBySurveyMonthId(int surveyMonthId) {
		return peCheckTaskDao.getSelectedAssignmentIdBySurveyMonthId(surveyMonthId);
	}
	
	/**
	 * Get Random PECheck Task ID by Survey Month ID
	 */
	public List<Integer> getRandomPECheckTaskIdBySurveyMonthId(int surveyMonthId) {
		return peCheckTaskDao.getRandomPECheckTaskIdBySurveyMonthId(surveyMonthId);
	}
	
	/**
	 * Get Total No. of Assignments (isSelected = false)
	 * Get Selected Assignments (isSelected = true)
	 */
	public long getNoOfAssignmentOfSurveyMonth(int surveyMonthId, boolean isSelected) {
		return peCheckTaskDao.getNoOfAssignmentOfSurveyMonth(surveyMonthId, isSelected);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<PostEnumerationCertaintyCaseTableList> getSurveyMonthList(DatatableRequestModel model){

		Order order = this.getOrder(model, "sm.referenceMonth", "sm.startDate", "sm.endDate");
		
		String search = model.getSearch().get("value");
		
		List<PostEnumerationCertaintyCaseTableList> result = surveyMonthDao.selectAllPostEnumerationCertaintyCase(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<PostEnumerationCertaintyCaseTableList> response = new DatatableResponseModel<PostEnumerationCertaintyCaseTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = surveyMonthDao.countSelectAllPostEnumerationCertaintyCase("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = surveyMonthDao.countSelectAllPostEnumerationCertaintyCase(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Confirm Post-Enumeration Certainty Case
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean confirmPECertaintyCase(List<Integer> ids, Integer surveyMonthId) throws Exception {

		//if(ids == null || ids.size() < 1) return false;
		if(ids == null || ids.size() < 1) ids = new ArrayList<Integer> ();
		
		List<Integer> oldIds = getSelectedPECheckTaskIdBySurveyMonthId(surveyMonthId);
		List<Integer> newIds = getSelectedPECheckTaskIdByAssignmentIds(ids);
		
		Collection<Integer> deSelectedIds = (Collection<Integer>)CollectionUtils.subtract(oldIds, newIds);
		
//		List<PECheckTask> deSelectedtasks = peCheckTaskDao.getPECheckTasksByIds(deSelectedIds.toArray(new Integer[0]));
		List<PECheckTask> deSelectedtasks = new ArrayList<PECheckTask> ();
		
		if(deSelectedIds != null && deSelectedIds.size() > 0) {
			deSelectedtasks = peCheckTaskDao.getPECheckTasksByIds(deSelectedIds.toArray(new Integer[0]));
		}
		
		for(PECheckTask peCheckTask : deSelectedtasks) {
			//PECheckTask peCheckTask = peCheckTaskDao.findById(deSelectedId);
			peCheckTask.setSelected(false);
			peCheckTask.setFieldTeamHead(false);
			peCheckTask.setSectionHead(false);
			peCheckTaskDao.save(peCheckTask);
		}
		
//		List<PECheckTask> tasks = peCheckTaskDao.getPECheckTasksByIds(ids.toArray(new Integer[0]));
//		for(PECheckTask peCheckTask : tasks) {
//			//PECheckTask peCheckTask = peCheckTaskDao.findById(id);
//			peCheckTask.setSelected(true);
//			peCheckTaskDao.save(peCheckTask);
//		}
		
		for(Integer id : ids){
			Assignment assignment = assignmentDao.findById(id);
			PECheckTask task = assignment.getPeCheckTask();
			if(task == null){
				task = new PECheckTask();
				task.setAssignment(assignment);
				task.setSurveyMonth(assignment.getSurveyMonth());
			}
			task.setRandomCase(false);
			task.setCertaintyCase(true);
			task.setSelected(true);
			peCheckTaskDao.save(task);
		}
		
		peCheckTaskDao.flush();
		
		adjustPETask(surveyMonthId);

		return true;
	}
	
	/**
	 * 1. generate PE random case if the PE case for a field officer is not enough
	 * 2. remove PE random case if the number of PE case is over the pre-defined value
	 * 3. Assign Field Head and Section Head PE task if it is not assigned yet
	 * 4. Generate PE check form if it is not generated and the whole assignment is approved
	 * @param surveyMonthId
	 */
	@Transactional
	public void adjustPETask(Integer surveyMonthId){
		//List<PECheckTask> result = new ArrayList<PECheckTask>();
		AssignmentAllocationStatisiticModel statistic = allocationBatchDao.getAllocationStatistic(surveyMonthId);
		if (statistic != null && statistic.getAllocationBatchCnt() > 0 && statistic.getTotalAdjustment() == statistic.getApprovedAdjustment()){
			SystemConfiguration setting = systemParameterDao.findByName(SystemConstant.PE_PARAM_SELECT_PE_CHECK_PERCENTAGE);
			if (setting == null || StringUtils.isEmpty(setting.getValue())){
				return;
			}
			
			Double percentage = Double.parseDouble(setting.getValue());
			// generate random case for insufficient PE cases
			List<InsufficientPEListModel> list = peCheckTaskDao.getInsufficientPEList(surveyMonthId, percentage);
			if (list != null && list.size() > 0){
				for (InsufficientPEListModel item : list){
					assignmentService.generateRandomCase(item.getUserId(), surveyMonthId, item.getRemaining());
				}
			}			
			allocationBatchDao.flush();
			peCheckTaskDao.flush();
			
			// remove overflow PE cases
			List<OverflowPETaskModel> overflows =peCheckTaskDao.getOverflowPETask(surveyMonthId);
			for (OverflowPETaskModel overflow: overflows){
				List<PECheckTask> removes = peCheckTaskDao.getNotSubmittedRandomCase(overflow.getUserId(), overflow.getOverflow(), surveyMonthId);
				
				for (PECheckTask task : removes){
					task.setSelected(false);
					task.setFieldTeamHead(false);
					task.setSectionHead(false);
					if (task.getAssignment().getPeCheckForm() != null){
						peCheckFormDao.delete(task.getAssignment().getPeCheckForm());
					}
					peCheckTaskDao.save(task);
				}
			}
			peCheckTaskDao.flush();
			
			
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
						
			// assign section head and field head PE check task
			List<UnAssignedHeadPETask> unallocated = peCheckTaskDao.getUnassignedHeadPETask(surveyMonthId);
			if (unallocated != null && unallocated.size() > 0){				
				for (UnAssignedHeadPETask task : unallocated){
					if (task.getIsFieldHead() && fieldHead != null){
						List<PECheckTask> notSubmittedlist = peCheckTaskDao.getNotSubmittedPE(task.getUserId(), 1, surveyMonthId);
						if (notSubmittedlist != null && notSubmittedlist.size() > 0){
							if (notSubmittedlist != null && notSubmittedlist.size() > 0){
								PECheckTask notSubmitted = notSubmittedlist.get(0);
								notSubmitted.setFieldTeamHead(true);
								if (notSubmitted.getAssignment().getPeCheckForm() != null){
									notSubmitted.getAssignment().getPeCheckForm().setUser(fieldHead);
								}
								peCheckTaskDao.save(notSubmitted);
							}
						}
					}
					else if (task.getIsSectionHead() && sectionHead != null){
						List<PECheckTask> notSubmittedlist = peCheckTaskDao.getNotSubmittedPE(task.getUserId(), 1, surveyMonthId);
						if (notSubmittedlist != null && notSubmittedlist.size() > 0){
							PECheckTask notSubmitted = notSubmittedlist.get(0);
							notSubmitted.setSectionHead(true);
							if (notSubmitted.getAssignment().getPeCheckForm() != null){
								notSubmitted.getAssignment().getPeCheckForm().setUser(sectionHead);
							}
							peCheckTaskDao.save(notSubmitted);
						}
					}
				}
			}
			peCheckTaskDao.flush();
			
			// generate the PE check form if the assignment is approved
			List<PECheckTask> unGenerated = peCheckTaskDao.getUnGeneratedPECheckTask();
			if (unGenerated != null && unGenerated.size() > 0){
				for (PECheckTask peCheckTask : unGenerated){
					PECheckForm form = new PECheckForm();
					form.setAssignment(peCheckTask.getAssignment());
					form.setOfficer(peCheckTask.getAssignment().getUser());
					if (peCheckTask.isFieldTeamHead()) {						
						form.setUser(fieldHead);
					} else if (peCheckTask.isSectionHead()) {
						form.setUser(sectionHead);
					} else {
						form.setUser(peCheckTask.getAssignment().getUser().getSupervisor());
					}
					peCheckFormDao.save(form);
				}
			}
			
		}
	}
	

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public PostEnumerationCertaintyCaseEditModel convertEntityToModel(Integer surveyMonthId){

		PostEnumerationCertaintyCaseEditModel model = new PostEnumerationCertaintyCaseEditModel();
		
		model.setSurveyMonthId(surveyMonthId);
		
		List<Integer> assignmentIds = getSelectedAssignmentIdBySurveyMonthId(surveyMonthId);
		List<Integer> ids = getRandomPECheckTaskIdBySurveyMonthId(surveyMonthId);
		Long totalNoOfAssignments = assignmentDao.countTotalAssignmentBySurveyMonth(surveyMonthId);
		Long selectedAssignments = getNoOfAssignmentOfSurveyMonth(surveyMonthId, true);
		
		if(ids!=null && ids.size()>0){
			model.setRandomCreated(true);
		} else {
			model.setRandomCreated(false);
		}
		model.setAssignmentIds(assignmentIds);
		model.setTotalNoOfAssignments(totalNoOfAssignments);
		model.setSelectedAssignments(selectedAssignments);
		
		return model;
	}

}
