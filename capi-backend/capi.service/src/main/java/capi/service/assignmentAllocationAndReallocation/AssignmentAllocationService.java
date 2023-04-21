package capi.service.assignmentAllocationAndReallocation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentAdjustmentDao;
import capi.dal.AssignmentDao;
import capi.dal.CalendarEventDao;
import capi.dal.DistrictDao;
import capi.dal.DistrictHeadAdjustmentDao;
import capi.dal.PECheckTaskDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UserDao;
import capi.entity.AllocationBatch;
import capi.entity.AssignmentAdjustment;
import capi.entity.District;
import capi.entity.DistrictHeadAdjustment;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.DistrictManDayRequiredModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationListModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationSession;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.AdjustmentModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.DistrictHeadRowModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictHeadTabModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchDetailsModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.qualityControlManagement.PostEnumerationCertaintyCaseService;

@Service("AssignmentAllocationService")
public class AssignmentAllocationService extends BaseService{
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	AllocationBatchDao allocationBatchDao;
	
	@Autowired
	CalendarEventDao calendarEventDao;
	
	@Autowired
	DistrictDao districtDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DistrictHeadAdjustmentDao districtHeadAdjustmentDao;
	
	@Autowired
	AssignmentAdjustmentDao assignmentAdjustmentDao;
	
	@Autowired
	AssignmentDao assignmentDao;
	
	@Autowired
	QuotationRecordDao quotationRecordDao;
	
	@Autowired
	PostEnumerationCertaintyCaseService peService;
	
	@Autowired
	PECheckTaskDao peTaskDao;

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private NotificationService notifyService;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<AssignmentAllocationListModel> querySurveyMonth(DatatableRequestModel model){
		
		Order order = this.getOrder(model, "sm.referenceMonth", "ab.batchName");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentAllocationListModel> result = allocationBatchDao.listCompletedBatch(search, model.getStart(), model.getLength(), order);
		for(AssignmentAllocationListModel aalm : result){
			aalm.setReferenceMonthStr(commonService.formatMonth(aalm.getReferenceMonth()));
		}
		
		DatatableResponseModel<AssignmentAllocationListModel> response = new DatatableResponseModel<AssignmentAllocationListModel>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = allocationBatchDao.countAllocationBatch("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = allocationBatchDao.countAllocationBatch(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public AllocationBatchDetailsModel getAllocationBatchDetails(Integer allocationBatchId, String referenceMonthStr) throws Exception {
		Date referenceMonth = commonService.getMonth(referenceMonthStr + "-01");
		
		AllocationBatchDetailsModel abdm = new AllocationBatchDetailsModel();
		
		abdm = this.allocationBatchDao.getAllocationBatchDetails(allocationBatchId);
		
		Long noOfAssignment = this.allocationBatchDao.getNumberOfAssignmentInAllocationBatch(allocationBatchId, referenceMonth);
		
		Long noOfQuotation = this.allocationBatchDao.getNumberOfQuotationInAllocationBatch(allocationBatchId, referenceMonth);
		
		abdm.setNoOfAssignment(noOfAssignment);
		abdm.setNoOfQuotation(noOfQuotation);
		
		abdm.setAllocationBatchEndDateStr(commonService.formatDate(abdm.getAllocationBatchEndDate()));
		abdm.setAllocationBatchStartDateStr(commonService.formatDate(abdm.getAllocationBatchStartDate()));
		abdm.setSurveyMonthEndDateStr(commonService.formatDate(abdm.getSurveyMonthEndDate()));
		abdm.setSurveyMonthStartDateStr(commonService.formatDate(abdm.getSurveyMonthStartDate()));
		Double noOfManDayRequired = this.allocationBatchDao.getManDeyRequired(allocationBatchId);
		if(noOfManDayRequired == null){
			noOfManDayRequired = 0.0;
		}
		abdm.setNoOfManDayRequired(noOfManDayRequired);
		Double totalAvailableManDay = this.allocationBatchDao.getTotalAvailableManDay(abdm.getAllocationBatchStartDate(), abdm.getAllocationBatchEndDate());
		Double manDayForDayRangeSpecifiedDateUser = allocationBatchDao.getManDayForDateRangeSpecifedUserQuotationRecord(allocationBatchId);
		Double result = Math.round((totalAvailableManDay - manDayForDayRangeSpecifiedDateUser) * 10) / 10.0;
		
		abdm.setTotalAvailableManDay(result);
		return abdm;
	}
	
	public List<DistrictModel> getDistrictsAsModel(){
		return this.districtDao.getAllDistrictAsModel();
	}
	
	public List<DistrictHeadRowModel> getDistrictHeadRows(List<DistrictHeadTabModel> districts, Integer allocationBatchId){
		List<DistrictHeadRowModel> rows = new ArrayList<DistrictHeadRowModel>();

		AllocationBatch ab = this.allocationBatchDao.findById(allocationBatchId);
		
		//List<CalendarEvent> holidays = this.calendarEventDao.getCalendarHolidays(ab.getStartDate(), ab.getEndDate());
		
		List<User> userList = this.userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, null);
		for(User u : userList){
				DistrictHeadRowModel row = new DistrictHeadRowModel();
				Double officerAvailableManDay = calendarEventDao.getUserAvaliableManDayBetween(ab.getStartDate(), ab.getEndDate(), u.getUserId());
				officerAvailableManDay = officerAvailableManDay - allocationBatchDao.getManDayForDateRangeSpecifedUserQuotationRecord(allocationBatchId, u.getUserId());
				
				row.setUserId(u.getId());
				row.setUserName(u.getStaffCode() + " - " + (u.getChineseName()== null ? u.getEnglishName():u.getChineseName()) + " (" +u.getDestination()+")");
				row.setAvailableManDays(officerAvailableManDay);
				row.setManDayOfBalance(officerAvailableManDay);
				row.setManualAdjustment(0.0);
				row.setManDayRequired(0.0);
				row.setManDayOfTransferInOut(0.0);
				row.setAdjustedManDayRequiredForResponsibleDistricts(0.0);
				rows.add(row);
		}
		List<DistrictManDayRequiredModel> manDayList = this.districtDao.getManDayRequired(allocationBatchId);		
		HashMap<Integer,DistrictManDayRequiredModel> manDayMap = new HashMap<Integer,DistrictManDayRequiredModel>();
		if (manDayList != null && manDayList.size() > 0){
			for (DistrictManDayRequiredModel manDay: manDayList){
				manDayMap.put(manDay.getDistrictId(), manDay);
			}
		}		
		
		for(DistrictHeadTabModel dm : districts){
			Double districtRequiredManDay = 0.0;
			if (manDayMap.containsKey(dm.getDistrictId())){
				districtRequiredManDay = manDayMap.get(dm.getDistrictId()).getTotal();
			}			
			
//			List<ManDayRequiredModel> manDayList = this.districtDao.getManDayRequired(allocationBatchId, dm.getDistrictId());
//			Double districtRequiredManDay = 0.0;
//			
//			for(ManDayRequiredModel manDayModel : manDayList){
//				districtRequiredManDay = districtRequiredManDay + manDayModel.getQuotationLoading();
//			}

			for(DistrictHeadRowModel row : rows){
				if(row.getUserId().equals(dm.getUserId())){
					District d = this.districtDao.findById(dm.getDistrictId());
					
					int surveyMonthId = 0;
					if(ab.getSurveyMonth() != null) {
						surveyMonthId = ab.getSurveyMonth().getSurveyMonthId();
					}
					long noOfAssignment = this.assignmentDao.countAssignmentByDistrictIdUserId(dm.getDistrictId(), surveyMonthId, allocationBatchId);
					
					if(row.getManDayRequiredForResponsibleDistricts() == null){
						row.setManDayRequiredForResponsibleDistricts(districtRequiredManDay+"");
					}else{
						row.setManDayRequiredForResponsibleDistricts(row.getManDayRequiredForResponsibleDistricts()+", "+districtRequiredManDay+"");
					}
					row.setManDayRequired(row.getManDayRequired() + districtRequiredManDay);
					row.setAdjustedManDayRequiredForResponsibleDistricts(row.getManDayRequired());
					row.setManDayOfBalance(row.getManDayOfBalance()-districtRequiredManDay);
					if(row.getDistricts() == null){
						row.setDistricts(d.getCode());
						row.setTotalAssignment(noOfAssignment);
						row.setNoOfAssignment(String.valueOf(noOfAssignment));
					}else{
						row.setDistricts(row.getDistricts()+", "+d.getCode());
						row.setTotalAssignment(row.getTotalAssignment() + noOfAssignment);
						row.setNoOfAssignment(row.getNoOfAssignment() + ", " + noOfAssignment);
					}
					
				}
			}
			
		}
		for(DistrictHeadRowModel row : rows){
			row.setAvailableManDays(Math.round(row.getAvailableManDays() * 10) / 10.0);
			row.setManDayRequired(Math.round(row.getManDayRequired() * 10) / 10.0);
			row.setManualAdjustment(Math.round(row.getManualAdjustment() * 10) / 10.0);
			row.setAdjustedManDayRequiredForResponsibleDistricts(Math.round(row.getAdjustedManDayRequiredForResponsibleDistricts() * 10) / 10.0);
			row.setManDayOfTransferInOut(Math.round(row.getManDayOfTransferInOut() * 10) / 10.0);
			row.setManDayOfBalance(Math.round(row.getManDayOfBalance() * 10) / 10.0);
			
			row.setManDayRequiredForResponsibleDistricts(row.getManDayRequired()+" ("+(row.getManDayRequiredForResponsibleDistricts() == null ? 0 : row.getManDayRequiredForResponsibleDistricts()) +")");
		}
		
		Comparator<DistrictHeadRowModel> comparator = new Comparator<DistrictHeadRowModel>() {
		    @Override
		    public int compare(DistrictHeadRowModel o1, DistrictHeadRowModel o2) {
		    	if(o1.getDistricts() == null) {
		    		return 1;
		    	}
		    	if(o2.getDistricts() == null){
		    		return (o1.getDistricts() == null) ? 0 : -1 ;
		    	}
		    	return o1.getDistricts().compareToIgnoreCase(o2.getDistricts());
		    }
		};

		Collections.sort(rows, comparator);
		
		return rows;
		
	}
	
	@Transactional
	public void saveAssignmentAdjustments(AssignmentAllocationSession assignmentAllocationSession) throws ParseException{
		AllocationBatch ab = this.allocationBatchDao.findById(assignmentAllocationSession.getSessionAllocationBatchTabModel().getAllocationBatchId());
		Date referenceMonth = new Date();
		
		referenceMonth = commonService.getMonth(assignmentAllocationSession.getSessionAllocationBatchTabModel().getReferenceMonthStr());
		
		
		//update district user
		HashMap<Integer, Integer> districtHeadUserMap = new HashMap<Integer, Integer>();
		for(DistrictHeadTabModel dhtm : assignmentAllocationSession.getSessionDistrictHeadTabModel()){
			District d = this.districtDao.findById(dhtm.getDistrictId());
			User districtHeadUser = this.userDao.findById(dhtm.getUserId());
			d.setUser(districtHeadUser);
			this.districtDao.save(d);
			districtHeadUserMap.put(dhtm.getDistrictId(), dhtm.getUserId());
		}

		
		for(DistrictHeadRowModel dhm : assignmentAllocationSession.getSessionDistrictHeadRows()){
			User user = this.userDao.findById(dhm.getUserId());
			
				DistrictHeadAdjustment dha = new DistrictHeadAdjustment();
				dha.setAdjustedManDay(dhm.getAdjustedManDayRequiredForResponsibleDistricts());
				dha.setAllocationBatch(ab);
				dha.setAvailableManDay(dhm.getAvailableManDays());
				dha.setDefaultAssignedManDay(dhm.getManDayRequiredForResponsibleDistricts());
				dha.setManDayBalance(dhm.getManDayOfBalance());
				dha.setManDayTransferred(dhm.getManDayOfTransferInOut());
				dha.setManualAdjustment(dhm.getManualAdjustment());
				dha.setReferenceMonth(referenceMonth);
				dha.setUser(user);
				dha.setDistrict(dhm.getDistricts());
				this.districtHeadAdjustmentDao.save(dha);
			
		}
		
		for(AdjustmentModel am : assignmentAllocationSession.getSessionAdjustmentModels()){
			User fromUser = this.userDao.findById(am.getFromUserId());
			User toUser = this.userDao.findById(am.getToUserId());
			AssignmentAdjustment aa = new AssignmentAdjustment();
			aa.setAllocationBatch(ab);
			aa.setFromUser(fromUser);
			aa.setToUser(toUser);
			aa.setManDay(am.getManDay());
			aa.setReferenceMonth(referenceMonth);
			aa.setRemark(am.getRemark());
			aa.setStatus("Pending");
			aa.setActualManDay(0D);
			this.assignmentAdjustmentDao.save(aa);
			
		}
		ab.setStatus(1);
		allocationBatchDao.flush();
		quotationRecordDao.flush();
		
		sendNotification(assignmentAllocationSession);
	}
	
	public List<AdjustmentModel> getAssignmentAdjustment(Integer allocationBatchId){
		return this.assignmentAdjustmentDao.getAssignmentAdjustmentView(allocationBatchId);
	}
	
	public List<DistrictHeadRowModel> getDistrictHeadAdjustment(Integer allocationBatchId){
		List<DistrictHeadAdjustment> entities = this.districtHeadAdjustmentDao.getAssignmentAllocationView(allocationBatchId);
		List<DistrictHeadRowModel> displayList = new ArrayList<DistrictHeadRowModel>();
		for(DistrictHeadAdjustment entity : entities ){
			DistrictHeadRowModel existModel = new DistrictHeadRowModel();
			User u = entity.getUser();
			existModel.setUserId(u.getId());
			existModel.setUserName(u.getStaffCode()+" - "+(u.getChineseName()==null? u.getEnglishName():u.getChineseName())+" ("+u.getDestination()+")");
			existModel.setDistricts(entity.getDistrict());
			existModel.setManDayOfBalance(entity.getManDayBalance());
			existModel.setAvailableManDays(entity.getAvailableManDay());
			existModel.setManDayRequiredForResponsibleDistricts(entity.getDefaultAssignedManDay());
			existModel.setManDayOfTransferInOut(entity.getManDayTransferred());
			existModel.setManualAdjustment(entity.getManualAdjustment());
			existModel.setAdjustedManDayRequiredForResponsibleDistricts(entity.getAdjustedManDay());
			displayList.add(existModel);
		}
		return displayList;
	}
	
	@Transactional
	public boolean removeAlloactedAssignmentRecord(Integer id){
		AllocationBatch batch = allocationBatchDao.findById(id);
		
		if (batch == null){
			return false;
		}
		
//		batch.getDistrictHeadAdjustments().clear();
//		batch.getAssignmentAdjustments().clear();
		
		List<AssignmentAdjustment> assignmentAdjustments = assignmentAdjustmentDao.getAssignmentAdjustmentByAllocationBatchId(id);
		for(AssignmentAdjustment assignmentAdjustment : assignmentAdjustments) {
			assignmentAdjustmentDao.delete(assignmentAdjustment);
		}
		
		List<Integer> allocationBatchIds = new ArrayList<Integer> ();
		allocationBatchIds.add(id);
		
		List<DistrictHeadAdjustment> districtHeadAdjustments = districtHeadAdjustmentDao.findDistrictHeadAdjustmentByAllocationBatchIds(allocationBatchIds);
		for(DistrictHeadAdjustment districtHeadAdjustment : districtHeadAdjustments) {
			districtHeadAdjustmentDao.delete(districtHeadAdjustment);
		}
		
		batch.setStatus(null);
		allocationBatchDao.save(batch);
		
		allocationBatchDao.flush();
		
		return true;
	}
	
	@Transactional
	public boolean approveAssignmentAllocation(Integer id){
		AllocationBatch batch = allocationBatchDao.findById(id);
		
		if (batch == null){
			return false;
		}
		batch.setStatus(2);
		
		// update assignment and quotation record users
		quotationRecordDao.overwriteDistrictHeadInQuotationRecordAndAssignment(id);
				
		allocationBatchDao.save(batch);		
		allocationBatchDao.flush();
		
		// check peCheckTask Certainty case has not generate
		if(peTaskDao.countSelectedCertaintyCase(batch.getSurveyMonth().getSurveyMonthId())>0){
			peService.adjustPETask(batch.getSurveyMonth().getSurveyMonthId());
		}
			
		return true;
	}
	
	public void sendNotification(AssignmentAllocationSession assignmentAllocationSession) {
		HashMap<Integer, List<AdjustmentModel>> fromUserGroup = new HashMap<Integer, List<AdjustmentModel>>();
		
		for (AdjustmentModel am : assignmentAllocationSession.getSessionAdjustmentModels()) {
			if (!fromUserGroup.containsKey(am.getFromUserId())) {
				fromUserGroup.put(am.getFromUserId(), new ArrayList<AdjustmentModel>());
			}
			List<AdjustmentModel> list = fromUserGroup.get(am.getFromUserId());
			list.add(am);
		}
		
		String subject = messageSource.getMessage("N00062", null, Locale.ENGLISH);
		String contentHeader = messageSource.getMessage("N00063", null, Locale.ENGLISH);
		
		for (Integer fromUserId : fromUserGroup.keySet()) {
			User fromUser = this.userDao.findById(fromUserId);
			StringBuilder builder = new StringBuilder();
			for (AdjustmentModel model : fromUserGroup.get(fromUserId)) {
				User toUser = this.userDao.findById(model.getToUserId());
				String message = messageSource.getMessage("N00064", 
						new Object[]{
							model.getManDay(), 
							toUser.getEnglishName()
						},
						Locale.ENGLISH);
				
				builder.append(message + "\n");
			}
			String content = builder.toString();
			notifyService.sendNotification(fromUser, subject, contentHeader+"\n"+content, false);
		}
	}
}
