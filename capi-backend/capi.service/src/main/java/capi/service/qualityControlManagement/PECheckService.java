package capi.service.qualityControlManagement;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import capi.dal.AssignmentDao;
import capi.dal.OutletDao;
import capi.dal.OutletTypeDao;
import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.dal.PETopManagementDao;
import capi.dal.QCItineraryPlanDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SurveyMonthDao;
import capi.dal.TpuDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.Outlet;
import capi.entity.PECheckForm;
import capi.entity.PECheckTask;
import capi.entity.PETopManagement;
import capi.entity.SurveyMonth;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.PECheckFormSyncData;
import capi.model.qualityControlManagement.QuotationRecordTableList;
import capi.model.qualityControlManagement.PECheckFormModel;
import capi.model.qualityControlManagement.PECheckListTableList;
import capi.model.qualityControlManagement.PECheckTableList;
import capi.model.qualityControlManagement.PECheckTaskList;
import capi.model.qualityControlManagement.PECheckTaskModel;
import capi.model.shared.quotationRecord.OutletPostModel;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.OutletService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("PECheckService")
public class PECheckService extends BaseService {

	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private PECheckFormDao peCheckFormDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;

	@Autowired
	QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private QCItineraryPlanDao qcItineraryPlanDao;
	
	@Autowired
	QuotationRecordService quotationRecordService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PETopManagementDao peTopManagementDao; 
	
	@Autowired
	private TpuDao tpuDao;

	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;

	@Autowired
	private OutletService outletService;
	
	@Autowired
	private OutletTypeDao outletTypeDao;
	
	/**
	 * DataTable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<PECheckListTableList> getCheckListTableList(DatatableRequestModel model) throws ParseException{

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> supervisorIds = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2 ||
				(detail.getAuthorityLevel() & 256) == 256 || (detail.getAuthorityLevel() & 2048) == 2048){
			// field team head / section head view all
		}
		else if ((detail.getAuthorityLevel() & 4) == 4){
			supervisorIds.add(userId);		
			supervisorIds.addAll(detail.getActedUsers());
		}
		
//		Order order = this.getOrder(model, "userId", "officerCode", "officerName", "allocatedAssignments", "checkedAssignments", "selectedAssignments", "incompleteAssignments");
		Order order = this.getOrder(model, "team", "officerCode", "officerName", "total", "excluded", "checked", "selected", "approved", "nonContact");

		String search = model.getSearch().get("value");
		Date referenceMonth = !StringUtils.isEmpty(model.getSearch().get("referenceMonth")) ? commonService.getMonth(model.getSearch().get("referenceMonth")) : null;		
		
		List<PECheckListTableList> result = peCheckTaskDao.getPECheckListTableList(search, model.getStart(), model.getLength(), order, referenceMonth, supervisorIds.toArray(new Integer[0]));
		
		DatatableResponseModel<PECheckListTableList> response = new DatatableResponseModel<PECheckListTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = peCheckTaskDao.countPECheckListTableList("", referenceMonth, supervisorIds.toArray(new Integer[0]));
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = peCheckTaskDao.countPECheckListTableList(search, referenceMonth, supervisorIds.toArray(new Integer[0]));
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Data Sync
	 */
	@Transactional
	public List<PECheckFormSyncData> syncPECheckFormData(List<PECheckFormSyncData> peCheckForms, Date lastSyncTime, Boolean dataReturn, Integer[] peCheckFormIds, Integer[] qcItineraryPlanIds){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> allPECheckFormIds = new ArrayList<Integer>();
		if(peCheckFormIds!=null && peCheckFormIds.length>0){
			allPECheckFormIds.addAll(Arrays.asList(peCheckFormIds));
		}
			
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if(peCheckForms!=null && peCheckForms.size()>0){
			for(PECheckFormSyncData peCheckForm : peCheckForms){
				if ("D".equals(peCheckForm.getLocalDbRecordStatus())){
					continue;
				}
				PECheckForm entity = null;
				if(peCheckForm.getPeCheckFormId()==null){
					continue;
				} else {
					entity = peCheckFormDao.findById(peCheckForm.getPeCheckFormId());
					if (entity !=null && entity.getModifiedDate() != null && entity.getModifiedDate().after(peCheckForm.getModifiedDate())){
						unUpdateIds.add(entity.getPeCheckFormId());
						table.put(entity.getPeCheckFormId(), peCheckForm.getLocalId());
						continue;
					}
				}
				
				BeanUtils.copyProperties(peCheckForm, entity);
				
				Date checkingTime = null;
				if(!StringUtils.isEmpty(peCheckForm.getCheckingTime())){
					try{
						checkingTime = commonService.getTime(peCheckForm.getCheckingTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setCheckingTime(checkingTime);
				
				if(peCheckForm.getAssignmentId()!=null){
					Assignment assignment = assignmentDao.findById(peCheckForm.getAssignmentId());
					if (assignment != null){
						entity.setAssignment(assignment);						
					}
				}
				
				if(peCheckForm.getOfficerId()!=null){
					User officer = userDao.findById(peCheckForm.getOfficerId());
					if (officer != null){
						entity.setOfficer(officer);						
					}
				}
				
				if(peCheckForm.getUserId()!=null){
					User user = userDao.findById(peCheckForm.getUserId());
					if (user != null){
						entity.setUser(user);						
					}
				}
				entity.setByPassModifiedDate(true);
				peCheckFormDao.save(entity);
				allPECheckFormIds.add(entity.getPeCheckFormId());
				table.put(entity.getPeCheckFormId(), peCheckForm.getLocalId());
			}
			peCheckFormDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			List<PECheckFormSyncData> updatedData = new ArrayList<PECheckFormSyncData>();
			
			if(allPECheckFormIds!=null && allPECheckFormIds.size()>0){
				updatedData.addAll(syncPECheckFormbyIdsRecursiveQuery(allPECheckFormIds, lastSyncTime));
			}
			
			if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
				updatedData.addAll(qcItineraryPlanDao.getUpdatedPECheckForm(lastSyncTime, null, qcItineraryPlanIds));
			}
			
			//unUpdate Id
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncPECheckFormbyIdsRecursiveQuery(unUpdateIds, null));
			}
			
			List<PECheckFormSyncData> unqiue = new ArrayList<PECheckFormSyncData>(new HashSet<PECheckFormSyncData>(updatedData));
			for(PECheckFormSyncData data : unqiue){
				if(table.containsKey(data.getPeCheckFormId())){
					data.setLocalId(table.get(data.getPeCheckFormId()));
				}
			}
			return unqiue;
		}
		
		return new ArrayList<PECheckFormSyncData>(); 
	}
	
	public PECheckTaskModel getPECheckTaskModel(Integer userId, Integer surveyMonthId) {
		
		PECheckTaskModel peCheckTaskModel = null; 
//				peCheckTaskDao.getPECheckTaskModel(userId, surveyMonthId);
		SurveyMonth month = surveyMonthDao.findById(surveyMonthId);
		if (peCheckTaskModel == null){
			peCheckTaskModel = new PECheckTaskModel();
			peCheckTaskModel.setUserId(userId);
			peCheckTaskModel.setSurveyMonthId(surveyMonthId);
			User user = userDao.findById(userId);			
			peCheckTaskModel.setFieldOfficer(String.format("%s - %s", user.getStaffCode(), user.getChineseName()));
			if (month != null){
				peCheckTaskModel.setReferenceMonth(commonService.formatMonth(month.getReferenceMonth()));				
			}
		}
		
		PETopManagement mang = peTopManagementDao.getByRefernceMonthUserId(userId, month.getReferenceMonth());
		if (mang != null){
			peCheckTaskModel.setIsFieldTeamHead(mang.isFieldHead()?1:0);
			peCheckTaskModel.setIsSectionHead(mang.isSectionHead()?1:0);
		}		
		
		List<PECheckTaskList> peCheckTaskLists = peCheckTaskDao.getPECheckFormList(userId, surveyMonthId);
		
		for(PECheckTaskList result : peCheckTaskLists){
			List<String> str = outletTypeDao.getShortCodeByAssignmentId(result.getAssignmentId());
			result.setOutletType(StringUtils.join(str, ','));
		}
		
//		Integer previousAssignmentId = 0;
//		for (int i=peCheckTaskLists.size()-1 ; i >= 0 ; i--) {
//			if (peCheckTaskLists.get(i).getAssignmentId() == previousAssignmentId) {
//				peCheckTaskLists.get(i).setBatchCode(peCheckTaskLists.get(i+1).getBatchCode()+","+peCheckTaskLists.get(i).getBatchCode());
//				peCheckTaskLists.remove(i+1);
//			}
//			previousAssignmentId = peCheckTaskLists.get(i).getAssignmentId();
//		}
		peCheckTaskModel.setPeCheckTaskList(peCheckTaskLists);
		return peCheckTaskModel;
	}
	
	public List<PECheckTaskList> getPECheckFormList(Integer[] assignmentIds, Integer surveyMonthId) {
		return peCheckTaskDao.getPECheckFormListDetail(assignmentIds, surveyMonthId);
	}
	
	/**
	 * DataTable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<PECheckTableList> getCheckTableList(DatatableRequestModel model, String referenceMonth, String certaintyCase, Integer roleHeader) throws ParseException{

		Order order = this.getOrder(model, "referenceMonth", "referenceNo", "outletName", "userStaffCode"
				,"userChineseName", "status", "modifiedDate", "modifiedBy", "isCertaintyCase", "isSectionHead", "isFieldTeamHead");

		String search = model.getSearch().get("value");
		Date refMonth = null;
		
		if (!StringUtils.isEmpty(referenceMonth)){
			refMonth = commonService.getMonth(referenceMonth);
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		int authorityLevel = (1|2|256|2048);
		
		List<Integer> supervisorList = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & authorityLevel) == 0){
			if ((detail.getAuthorityLevel() & 4) == 4){
				supervisorList.add(detail.getUserId());
				supervisorList.addAll(detail.getActedUsers());
			}
		}
		
		List<PECheckTableList> result = peCheckTaskDao.getPECheckTableList(search, model.getStart(), model.getLength(), order, refMonth, supervisorList, certaintyCase, roleHeader);
		
		DatatableResponseModel<PECheckTableList> response = new DatatableResponseModel<PECheckTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = peCheckTaskDao.countPECheckTableList("", refMonth, supervisorList, certaintyCase, roleHeader);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = peCheckTaskDao.countPECheckTableList(search, refMonth, supervisorList, certaintyCase, roleHeader);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public PECheckForm getCheckFormById(Integer peCheckFormId) {
		return peCheckFormDao.findById(peCheckFormId);
	}
	
	@Transactional
	public void saveCheckTaskList(PECheckTaskModel peCheckTaskModel, Integer userId) {
		User officer = userDao.findById(peCheckTaskModel.getUserId());
		User user = userDao.findById(userId);
		SurveyMonth surveyMonth = surveyMonthDao.findById(peCheckTaskModel.getSurveyMonthId());
				
		List<PECheckTaskList> newPECheckTaskLists = new ArrayList<PECheckTaskList>();

		List<Integer> oldPECheckTaskIds = new ArrayList<Integer>();
		List<Integer> newPECheckTaskIds = new ArrayList<Integer>();
//		List<Integer> oldPECheckFormIds = new ArrayList<Integer>();
//		List<Integer> newPECheckFormIds = new ArrayList<Integer>();

		PECheckTaskModel oldPECheckTaskModel = this.getPECheckTaskModel(peCheckTaskModel.getUserId(), peCheckTaskModel.getSurveyMonthId());
		
		for(PECheckTaskList oldPECheckTaskList: oldPECheckTaskModel.getPeCheckTaskList()){		
			if(oldPECheckTaskList.getPeCheckTaskId()!=null){
				oldPECheckTaskIds.add(oldPECheckTaskList.getPeCheckTaskId());
			}
//			if (oldPECheckTaskList.getPeCheckFormId() != null) {
//				oldPECheckFormIds.add(oldPECheckTaskList.getPeCheckFormId());
//			}
		}

		if(peCheckTaskModel.getPeCheckTaskList() != null) {
			newPECheckTaskLists.addAll(peCheckTaskModel.getPeCheckTaskList());
		}
			
		if(newPECheckTaskLists.size() > 0){
			for(PECheckTaskList newPECheckTaskList: newPECheckTaskLists){
				PECheckTask savePECheckTask;
				PECheckForm savePECheckForm;
				Assignment assignment = assignmentDao.findById(newPECheckTaskList.getAssignmentId());
				
				if (newPECheckTaskList.getPeCheckFormId() != null && newPECheckTaskList.getPeCheckFormId() > 0) {
//					newPECheckFormIds.add(newPECheckTaskList.getPeCheckFormId());
				} else {
					List<Integer> assignmentIds = new ArrayList<Integer>();
					assignmentIds.add(newPECheckTaskList.getAssignmentId());
					if (assignmentDao.getApprovedAssignment(assignmentIds).size() > 0) {
						savePECheckForm = new PECheckForm();
						savePECheckForm.setUser(user);
						savePECheckForm.setAssignment(assignment);
						savePECheckForm.setOfficer(officer);
						savePECheckForm.setStatus("Draft");
						peCheckFormDao.save(savePECheckForm);
					}
				}
				
				if (newPECheckTaskList.getPeCheckTaskId() != null && newPECheckTaskList.getPeCheckTaskId() > 0) {
					savePECheckTask = peCheckTaskDao.findById(newPECheckTaskList.getPeCheckTaskId());
					if(!savePECheckTask.isSelected() && savePECheckTask.isCertaintyCase()){
						savePECheckTask.setCertaintyCase(false);
					}
					newPECheckTaskIds.add(newPECheckTaskList.getPeCheckTaskId());
				} else {
					savePECheckTask = new PECheckTask();
					savePECheckTask.setAssignment(assignment);
					savePECheckTask.setSurveyMonth(surveyMonth);
				}
				savePECheckTask.setSelected(true);
				savePECheckTask.setSectionHead(newPECheckTaskList.isSectionHead());
				savePECheckTask.setFieldTeamHead(newPECheckTaskList.isFieldTeamHead());

				peCheckTaskDao.save(savePECheckTask);
			}
		}
		
//		Collection<Integer> removePECheckFormIds = CollectionUtils.subtract(oldPECheckFormIds, newPECheckFormIds);
//		if (removePECheckFormIds.size() > 0){
//			for (Integer removePECheckFormId: removePECheckFormIds){
//				PECheckForm PECheckForm = peCheckFormDao.findById(removePECheckFormId);
//				peCheckFormDao.delete(PECheckForm);
//			}
//		}
		
		Collection<Integer> removePECheckTaskIds = CollectionUtils.subtract(oldPECheckTaskIds, newPECheckTaskIds);
		if (removePECheckTaskIds.size() > 0){
			for (Integer removePECheckTaskId: removePECheckTaskIds){
				PECheckTask peCheckTask = peCheckTaskDao.findById(removePECheckTaskId);
				if(peCheckTask.isSelected() && (peCheckTask.isCertaintyCase() || peCheckTask.isRandomCase())){
					continue;
				}
				PECheckForm peCheckForm = peCheckTask.getAssignment().getPeCheckForm();
				if(peCheckForm!=null && "Submitted".equals(peCheckForm.getStatus())){
					continue;
				}
				peCheckFormDao.delete(peCheckForm);
				peCheckTaskDao.delete(peCheckTask);
			}
		}		
		
		peCheckFormDao.flush();
		peCheckTaskDao.flush();
		
	}
	
	public PECheckFormModel getPECheckFormModel(Integer peCheckFormId) {
		PECheckFormModel peCheckFormModel = new PECheckFormModel();
		PECheckForm peCheckForm = peCheckFormDao.findById(peCheckFormId);
		BeanUtils.copyProperties(peCheckForm, peCheckFormModel);
		peCheckFormModel.setCheckingDateText(commonService.formatDate(peCheckForm.getCheckingDate()));
		peCheckFormModel.setCheckingTimeText(commonService.formatTime(peCheckForm.getCheckingTime()));
		User officer = peCheckForm.getOfficer();
		User user = peCheckForm.getUser();
		peCheckFormModel.setOfficerId(officer.getUserId());
		peCheckFormModel.setUserId(user.getUserId());
		peCheckFormModel.setOfficerText(officer.getStaffCode() + " - " + officer.getChineseName());
		peCheckFormModel.setUserText(user.getStaffCode() + " - " + user.getChineseName());
		
		Assignment assignment = peCheckForm.getAssignment();
		peCheckFormModel.setAssignmentId(assignment.getAssignmentId());
		peCheckFormModel.setFirmStatus(assignment.getStatus());
		
		Date enumerationDate = assignmentDao.getEnumerationDate(assignment.getAssignmentId());
		peCheckFormModel.setEnumerationDate(commonService.formatDate(enumerationDate));
		
		if (assignment.getCollectionMethod() != null) {
			peCheckFormModel.setCollectionMethod(commonService.getCollectionMethod(assignment.getCollectionMethod()));
		}
		OutletViewModel outletModel = quotationRecordService.prepareOutletViewModel(assignment.getOutlet());
		outletModel.setDiscountRemark(assignment.getOutletDiscountRemark());
		peCheckFormModel.setOutlet(outletModel);
		
		return peCheckFormModel;
	}

	/**
	 * DataTable query
	 */
	public Long getCheckTableCount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		int authorityLevel = (1|2|256|2048);
		
		List<Integer> supervisorList = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & authorityLevel) == 0){
			if ((detail.getAuthorityLevel() & 4) == 4){
				supervisorList.add(detail.getUserId());
				supervisorList.addAll(detail.getActedUsers());
			}
			
			if((detail.getAuthorityLevel() & 2) == 2) {
				supervisorList.add(detail.getUserId());
				supervisorList.addAll(detail.getActedUsers());
				List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(supervisorList);
				supervisorList = actedUserSubordinateIds;
			}
		}
		//Outstanding PE Check form should count from now to 3 months ago only
		Date toDate = commonService.getDateWithoutTime(new Date());
		Date fromDate = DateUtils.truncate(DateUtils.addMonths(toDate, -3), Calendar.DATE);
				
		return peCheckTaskDao.countOutstandingPECheckTableList("", null, supervisorList, null, null, fromDate, toDate);
	}
	
	/**
	 * DataTable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<QuotationRecordTableList> getQuotationRecordTableList(DatatableRequestModel model) throws ParseException{

		Order order = this.getOrder(model, "quotationId", "unitName", "productAttribute","nPrice","nPrice","nPrice","nPrice","discount","subPrice","discountReason");

		String search = model.getSearch().get("value");
		
		if (StringUtils.isEmpty(model.getSearch().get("assignmentId"))) {
			return null;
		}
		
		Integer assignmentId = Integer.parseInt(model.getSearch().get("assignmentId"));
	
		List<QuotationRecordTableList> result = quotationRecordDao.getTableListForPECheck(search, model.getStart(), model.getLength(), order, assignmentId);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countTableListForPECheck("", assignmentId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countTableListForPECheck(search, assignmentId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Save Check Form
	 * @throws ParseException 
	 */
	public void saveCheckForm(PECheckFormModel peCheckFormModel) throws ParseException {
		PECheckForm peCheckForm = peCheckFormDao.findById(peCheckFormModel.getPeCheckFormId());
		BeanUtils.copyProperties(peCheckFormModel, peCheckForm);
		if (StringUtils.isNotEmpty(peCheckFormModel.getCheckingDateText())){
			peCheckForm.setCheckingDate(commonService.getDate(peCheckFormModel.getCheckingDateText()));	
		}
		if (StringUtils.isNotEmpty(peCheckFormModel.getCheckingTimeText())){
			peCheckForm.setCheckingTime(commonService.getTime(peCheckFormModel.getCheckingTimeText()));
		}
		peCheckFormDao.save(peCheckForm);
		peCheckFormDao.flush();
	}
	
	public List<PECheckFormSyncData> syncPECheckFormbyIdsRecursiveQuery(List<Integer> peCheckFormIds, Date lastSyncTime){
		List<PECheckFormSyncData> entities = new ArrayList<PECheckFormSyncData>();
		if(peCheckFormIds.size()>2000){
			List<Integer> ids = peCheckFormIds.subList(0, 2000);
			entities.addAll(syncPECheckFormbyIdsRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = peCheckFormIds.subList(2000, peCheckFormIds.size());
			entities.addAll(syncPECheckFormbyIdsRecursiveQuery(remainIds, lastSyncTime));
		} if(peCheckFormIds.size()>0){
			return qcItineraryPlanDao.getUpdatedPECheckForm(lastSyncTime, peCheckFormIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public String createPECheckRemark(Outlet outlet, Assignment assignment, OutletPostModel model, boolean isImageUploaded) {
		StringBuilder builder = new StringBuilder();
		
		String template = "\n%s: %s => %s";
		
		if (isImageUploaded) {
			builder.append("\nNew image uploaded");
		}
		
		if (!StringUtils.equals(outlet.getName(), model.getName())) {
			String content = String.format(template, "Name", outlet.getName(), model.getName());
			builder.append(content);
		}
		
		if (!integerEquals(outlet.getTpu().getId(), model.getTpuId())) {
			Tpu tpu = tpuDao.findById(model.getTpuId());
			
			String content = String.format(template, "TPU", outlet.getTpu().getCode(), tpu.getCode());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getStreetAddress(), model.getStreetAddress())) {
			String content = String.format(template, "Map Address", outlet.getStreetAddress(), model.getStreetAddress());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getDetailAddress(), model.getDetailAddress())) {
			String content = String.format(template, "Detail Address", outlet.getDetailAddress(), model.getDetailAddress());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getBrCode(), model.getBrCode())) {
			String content = String.format(template, "BR Code", outlet.getBrCode(), model.getBrCode());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getRemark(), model.getRemark())) {
			String content = String.format(template, "Outlet Remarks", outlet.getRemark(), model.getRemark());
			builder.append(content);
		}
		
		if (!integerEquals(assignment.getStatus(), model.getFirmStatus())) {
			String content = String.format(template, "Firm Status", SystemConstant.FIRM_STATUS[assignment.getStatus()], SystemConstant.FIRM_STATUS[model.getFirmStatus()]);
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getLastContact(), model.getLastContact())) {
			String content = String.format(template, "Last Contact", outlet.getLastContact(), model.getLastContact());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getMainContact(), model.getMainContact())) {
			String content = String.format(template, "Main Contact", outlet.getMainContact(), model.getMainContact());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getTel(), model.getTel())) {
			String content = String.format(template, "Telephone No.", outlet.getTel(), model.getTel());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getFax(), model.getFax())) {
			String content = String.format(template, "Fax No.", outlet.getFax(), model.getFax());
			builder.append(content);
		}
		
		if (!StringUtils.equals(commonService.formatTime(outlet.getOpeningStartTime()), model.getOpeningStartTime())
				|| !StringUtils.equals(commonService.formatTime(outlet.getOpeningEndTime()), model.getOpeningEndTime())) {
			String content = String.format(template, "Opening Hours", commonService.formatTime(outlet.getOpeningStartTime()) + " - " + commonService.formatTime(outlet.getOpeningEndTime()), model.getOpeningStartTime() + " - " + model.getOpeningEndTime());
			builder.append(content);
		}
		
		if (!StringUtils.equals(commonService.formatTime(outlet.getOpeningStartTime2()), model.getOpeningStartTime2())
				|| !StringUtils.equals(commonService.formatTime(outlet.getOpeningEndTime2()), model.getOpeningEndTime2())) {
			String content = String.format(template, "Opening Hours 2", commonService.formatTime(outlet.getOpeningStartTime2()) + " - " + commonService.formatTime(outlet.getOpeningEndTime2()), model.getOpeningStartTime2() + " - " + model.getOpeningEndTime2());
			builder.append(content);
		}
		
		if (!StringUtils.equals(commonService.formatTime(outlet.getConvenientStartTime()), model.getConvenientStartTime())
				|| !StringUtils.equals(commonService.formatTime(outlet.getConvenientEndTime()), model.getConvenientEndTime())) {
			String content = String.format(template, "Convenient Time", commonService.formatTime(outlet.getConvenientStartTime()) + " - " + commonService.formatTime(outlet.getConvenientEndTime()), model.getConvenientStartTime() + " - " + model.getConvenientEndTime());
			builder.append(content);
		}
		
		if (!StringUtils.equals(commonService.formatTime(outlet.getConvenientStartTime2()), model.getConvenientStartTime2())
				|| !StringUtils.equals(commonService.formatTime(outlet.getConvenientEndTime2()), model.getConvenientEndTime2())) {
			String content = String.format(template, "Convenient Time 2", commonService.formatTime(outlet.getConvenientStartTime2()) + " - " + commonService.formatTime(outlet.getConvenientEndTime2()), model.getConvenientStartTime2() + " - " + model.getConvenientEndTime2());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getWebSite(), model.getWebSite())) {
			String content = String.format(template, "Website", outlet.getWebSite(), model.getWebSite());
			builder.append(content);
		}
		
		if (!StringUtils.equals(outlet.getDiscountRemark(), model.getDiscountRemark())) {
			String content = String.format(template, "Discount Remarks", outlet.getDiscountRemark(), model.getDiscountRemark());
			builder.append(content);
		}

		if (!integerEquals(assignment.getCollectionMethod(), model.getCollectionMethod())) {
			String content = String.format(template, "Collection Method", SystemConstant.COLLECTION_METHOD[assignment.getCollectionMethod()], SystemConstant.COLLECTION_METHOD[model.getCollectionMethod()]);
			builder.append(content);
		}
		
		return builder.toString();
	}

	/**
	 * Integer equals
	 */
	public boolean integerEquals(Integer d1, Integer d2) {
		if (d1 == null && d2 == null) return true;
		if ((d1 == null && d2 != null) || (d1 != null && d2 == null)) return false;
		if (d1.doubleValue() != d2.doubleValue())
			return false;
		else
			return true;
	}

	@Transactional
	public void saveOutletForPECheck(OutletPostModel model, int assignmentId, int userId, int peCheckFormId, InputStream outletImageStream, String fileBaseLoc) throws Exception {
		Outlet entity = outletDao.findById(model.getOutletId());
		
		Assignment assignment = assignmentDao.findById(assignmentId);
		PECheckForm peCheckForm = peCheckFormDao.findById(peCheckFormId);
		
		String newPECheckRemark = createPECheckRemark(entity, assignment, model, outletImageStream != null);
		if (StringUtils.isNotBlank(newPECheckRemark)) {
			String datetime = commonService.formatDateTime(new Date());
			String title = String.format("[%s]", datetime);
			newPECheckRemark = title + newPECheckRemark;
			peCheckForm.setPeCheckRemark((StringUtils.isEmpty(peCheckForm.getPeCheckRemark()) ? newPECheckRemark : peCheckForm.getPeCheckRemark() + "\n" + newPECheckRemark));
			peCheckFormDao.save(peCheckForm);
		}
		
		assignmentMaintenanceService.copyOutletPostToEntity(model, entity);
		if (model.getCollectionMethod() != null){
			entity.setCollectionMethod(model.getCollectionMethod());
		}
		
//		if (outletImageStream != null) {
		if (!StringUtils.isEmpty(model.getIdenfityDel()) && model.getIdenfityDel().equals("del")){
			if (entity.getOutletImagePath() != null) {
				outletService.deleteOutletImage(entity.getOutletImagePath(), fileBaseLoc);
				entity.setOutletImagePath(null);
				entity.setImageModifiedTime(null);
			}
		}
		if (outletImageStream != null) {
			String outletImagePath = outletService.saveOutletImage(outletImageStream, fileBaseLoc, entity.getFirmCode());
			entity.setOutletImagePath(outletImagePath);
			entity.setImageModifiedTime(new java.util.Date());
		}
		
		assignment.setOutletDiscountRemark(entity.getDiscountRemark());
		assignment.setStatus(model.getFirmStatus());
		assignment.setCollectionMethod(model.getCollectionMethod());
		
		assignmentDao.save(assignment);
		outletDao.save(entity);
		outletDao.flush();
	}
}
