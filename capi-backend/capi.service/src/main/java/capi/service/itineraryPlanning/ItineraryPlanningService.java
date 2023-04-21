package capi.service.itineraryPlanning;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Order;
import org.jfree.util.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import capi.dal.DelinkTaskDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.ItineraryPlan;
import capi.entity.ItineraryPlanOutlet;
import capi.entity.MajorLocation;
import capi.entity.Outlet;
import capi.entity.QCItineraryPlan;
import capi.entity.QCItineraryPlanItem;
import capi.entity.SpotCheckForm;
import capi.entity.SupervisoryVisitForm;
import capi.entity.User;
import capi.dal.AssignmentDao;
import capi.dal.CalendarEventDao;
import capi.dal.ItineraryPlanDao;
import capi.dal.ItineraryPlanOutletDao;
import capi.dal.MajorLocationDao;
import capi.dal.OutletDao;
import capi.dal.OutletTypeDao;
import capi.dal.PECheckFormDao;
import capi.dal.QCItineraryPlanDao;
import capi.dal.QCItineraryPlanItemDao;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.ItineraryPlanAssignmentSyncData;
import capi.model.api.dataSync.ItineraryPlanOutletSyncData;
import capi.model.api.dataSync.ItineraryPlanSyncData;
import capi.model.api.dataSync.ItineraryUnPlanAssignmentSyncData;
import capi.model.api.dataSync.MajorLocationSyncData;
import capi.model.api.dataSync.QCItineraryPlanItemSyncData;
import capi.model.api.dataSync.QCItineraryPlanSyncData;
import capi.model.itineraryPlanning.ItineraryPlanOutletModel;
import capi.model.itineraryPlanning.ItineraryPlanEditModel;
import capi.model.itineraryPlanning.ItineraryPlanTableList;
import capi.model.itineraryPlanning.MajorLocationModel;
import capi.model.itineraryPlanning.OutletTypeFilter;
import capi.model.itineraryPlanning.QCItineraryPlanApprovalTableList;
import capi.model.itineraryPlanning.QCItineraryPlanItemModel;
import capi.model.itineraryPlanning.QCItineraryPlanModel;
import capi.model.itineraryPlanning.QCItineraryPlanTableList;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("ItineraryPlanningService")
public class ItineraryPlanningService extends BaseService {
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private DelinkTaskDao delinkTaskDao;
	
	@Autowired
	private ItineraryPlanDao itineraryPlanDao;

	@Autowired
	private ItineraryPlanOutletDao itineraryPlanOutletDao;

	@Autowired
	private QCItineraryPlanDao qcItineraryPlanDao;

	@Autowired
	private QCItineraryPlanItemDao qcItineraryPlanItemDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;

	@Autowired
	private OutletDao outletDao;

	@Autowired
	private OutletTypeDao outletTypeDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private MajorLocationDao majorLocationDao;
	
	@Autowired
	private	SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private	SupervisoryVisitFormDao supervisoryVisitFormDao;
	
	@Autowired
	private	PECheckFormDao peCheckFormDao;
	
	@Autowired
	private NotificationService  notifyService;
	
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;
	
	@Resource(name = "messageSource")
	MessageSource messageSource;

	
	/** 
	 *  Itinerary plan datatable
	 * @throws ParseException 
	 */
	public DatatableResponseModel<ItineraryPlanTableList> queryItineraryPlan(DatatableRequestModel model) throws ParseException{
		
		Order order = this.getOrder(model, "", "i.date", "noOfAssignment", "status", "fieldOfficerCode", "chineseName");
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		
		Date date = !StringUtils.isEmpty(model.getSearch().get("date")) ? commonService.getDate(model.getSearch().get("date")) : null;
		
		Integer filterUserId = null;
		if (!StringUtils.isEmpty(model.getSearch().get("fieldOfficerId"))) {
			filterUserId = Integer.parseInt(model.getSearch().get("fieldOfficerId"));
		}
		
		Integer fieldOfficerId = null;
		
		if (!((detail.getAuthorityLevel() & 256) == 256 || (detail.getAuthorityLevel() & 2048) == 2048 || (detail.getAuthorityLevel() & 2) == 2)){
			if ((detail.getAuthorityLevel() & 4) == 4){
				suerpervisorIds.add(userId);		
				suerpervisorIds.addAll(detail.getActedUsers());
			}
			
			if ((detail.getAuthorityLevel() & 16) == 16){
				fieldOfficerId = userId;
			}
		}
		
		String delinkDateStr = delinkTaskDao.getDelinkDate();
		
		List<ItineraryPlanTableList> result = itineraryPlanDao.getTableList(search, model.getStart(), model.getLength(), order, date, 
				suerpervisorIds, filterUserId,fieldOfficerId, null, delinkDateStr);
		
		DatatableResponseModel<ItineraryPlanTableList> response = new DatatableResponseModel<ItineraryPlanTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);

		Long recordFiltered = itineraryPlanDao.countLookupTableList(search, date, suerpervisorIds, filterUserId,fieldOfficerId, null, delinkDateStr);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		Long recordTotal = itineraryPlanDao.countLookupTableList("", date, suerpervisorIds, null,fieldOfficerId, null, delinkDateStr);
		response.setRecordsTotal(recordTotal.intValue());
		
		return response;
	}
	
	/** 
	 *  Itinerary plan datatable for approval
	 * @throws ParseException 
	 */
	public DatatableResponseModel<ItineraryPlanTableList> queryItineraryPlanApproval(DatatableRequestModel model) throws ParseException{
		
		Order order = this.getOrder(model, "", "i.date", "noOfAssignment", "status", "fieldOfficerCode", "chineseName");
		String search = model.getSearch().get("value");
		
		Date date = !StringUtils.isEmpty(model.getSearch().get("date")) ? commonService.getDate(model.getSearch().get("date")) : null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 256) == 256 || (detail.getAuthorityLevel() & 2048) == 2048) || 
				(detail.getAuthorityLevel() & 2) == 2 || (detail.getAuthorityLevel() & 1) == 1 ){
			if ((detail.getAuthorityLevel() & 4) == 4){
				suerpervisorIds.add(userId);		
				suerpervisorIds.addAll(detail.getActedUsers());
			}
		}
		
		
		Integer filterUserId = null;
		if (!StringUtils.isEmpty(model.getSearch().get("fieldOfficerId"))) {
			filterUserId = Integer.parseInt(model.getSearch().get("fieldOfficerId"));
		}
		
		
		String[] status = { "Submitted" };
		
		List<ItineraryPlanTableList> result = itineraryPlanDao.getApprovalTableList(search, model.getStart(), model.getLength(), order, date, 
				suerpervisorIds, filterUserId, status);
		
		DatatableResponseModel<ItineraryPlanTableList> response = new DatatableResponseModel<ItineraryPlanTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);

		Long recordFiltered = itineraryPlanDao.countLookupApprovalTableList(search, date, suerpervisorIds, filterUserId, status);
		response.setRecordsFiltered(recordFiltered.intValue());
		Long recordTotal = itineraryPlanDao.countLookupApprovalTableList("", date, suerpervisorIds, null, status);
		response.setRecordsTotal(recordTotal.intValue());
		return response;
	}
	
	/**
	 * Get Date select format
	 */
	public Select2ResponseModel queryDateSelect2(Select2RequestModel queryModel, Integer officerId) {
		queryModel.setRecordsPerPage(10);
		
		List<Date> entities = calendarEventDao.getNextNWorkdingDate(new Date(), 3);
		List<Date> existingDate = itineraryPlanDao.getPlanDateByOfficerId(officerId);
		
		entities.removeAll(existingDate);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = entities.size();
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Date entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(commonService.formatDate(entity));
			item.setText(commonService.formatDate(entity));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	
	/**
	 * Get Date select format
	 */
	public Select2ResponseModel querySupervisorDateSelect2(Select2RequestModel queryModel, Integer userId) {
		queryModel.setRecordsPerPage(10);
		
		List<Date> entities = calendarEventDao.getNextNWorkdingDate(new Date(), 3);
		List<Date> existingDate = qcItineraryPlanDao.getPlanDateByUserId(userId);
		
		entities.removeAll(existingDate);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = entities.size();
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Date entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(commonService.formatDate(entity));
			item.setText(commonService.formatDate(entity));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Outlet Type Filter select format
	 */
	public Select2ResponseModel queryOutletTypeFilterSelect2(Select2RequestModel queryModel, Integer[] outletIds) {
		queryModel.setRecordsPerPage(10);
		

		List<OutletTypeFilter> outletTypeFilters = outletTypeDao.searchOutletTypeWithOutletIds(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), outletIds);

		List<OutletTypeFilter> entities = new ArrayList<OutletTypeFilter>();
		String shortCode = "";
		
		for (OutletTypeFilter item : outletTypeFilters ) {
			if (shortCode.equals(item.getShortCode())){
				OutletTypeFilter lastEntry = entities.get(entities.size()-1);
				lastEntry.setFilter(lastEntry.getFilter()+"|^"+item.getOutletId()+"$");
			} else {
				item.setFilter(item.getShortCode() + "|^"+item.getOutletId()+"$");
				entities.add(item);
				shortCode = item.getShortCode();
			}
		}
				
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = entities.size();
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (OutletTypeFilter entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(entity.getFilter());
			item.setText(entity.getShortCode()+ " - " + entity.getChineseName());
			Log.info("item: "  +item.getText());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Plan Assignment
	 */
	public List<ItineraryPlanOutletModel> getPlanAssignment(Integer officerId, List<Integer> outletIds, Date planDate, Date collectionDate) {
		if(outletIds==null){ //Only Default Case have not the outletId
			outletIds = assignmentDao.getPlanOutletIds(officerId, collectionDate);
			if(outletIds==null||outletIds.size()==0)
				return new ArrayList<ItineraryPlanOutletModel>();
		}
		
		List<ItineraryPlanOutletModel> planAssignments = itineraryPlanDao.getPlanOutlet(officerId, outletIds, planDate==null ? collectionDate : planDate, null);
		
		List<Assignment> assignments = assignmentDao.getPlanAssignment(officerId, outletIds, planDate==null ? collectionDate : planDate);
		
		Hashtable<Integer, ItineraryPlanOutletModel> outletLookupTable = new Hashtable<Integer, ItineraryPlanOutletModel>();
		for (ItineraryPlanOutletModel model : planAssignments){
			outletLookupTable.put(model.getId(), model);
		}
		for (Assignment assignment : assignments){
			if (outletLookupTable.containsKey(assignment.getOutlet().getId())){
				ItineraryPlanOutletModel model = outletLookupTable.get(assignment.getOutlet().getId());
				model.getAssignmentIds().add(assignment.getId());
			}
		}
		
		return planAssignments;
	}

	/**
	 * Get Imported Assignment
	 */
	public List<ItineraryPlanOutletModel> getImportedAssignment(Integer officerId, List<Integer> assignmentIds, Date planDate, Date collectionDate) {
	
		List<ItineraryPlanOutletModel> planAssignments = itineraryPlanDao.getImportedAssignment(officerId, assignmentIds, planDate, collectionDate);
		
		return planAssignments;
	}
	
	/**
	 * Get ItineraryPlanEditModel
	 */
	public ItineraryPlanEditModel getItineraryPlanEditModel(Integer id) {
		ItineraryPlanEditModel itineraryPlanEditModel = new ItineraryPlanEditModel();
		ItineraryPlan itineraryPlan = itineraryPlanDao.findById(id);
		Set<Assignment> assignments = itineraryPlan.getAssignments();
		Hashtable<Outlet, List<Assignment>> assignmentMap = new Hashtable<Outlet, List<Assignment>>();
		for (Assignment assignment : assignments) {
			if (assignment.getOutlet() != null ){
				if (assignmentMap.containsKey(assignment.getOutlet())){
					List<Assignment> assignemnts = assignmentMap.get(assignment.getOutlet());
					assignemnts.add(assignment);
				}
				else{
					List<Assignment> assignemnts = new ArrayList<Assignment>();
					assignemnts.add(assignment);
					assignmentMap.put(assignment.getOutlet(), assignemnts);
				}
			}
		}
		
		BeanUtils.copyProperties(itineraryPlan, itineraryPlanEditModel);
		itineraryPlanEditModel.setUserId(itineraryPlan.getUser().getUserId());
		if (itineraryPlan.getSupervisor() != null){
			itineraryPlanEditModel.setSupervisorId(itineraryPlan.getSupervisor().getUserId());
		}
		List<MajorLocation> majorLocations = majorLocationDao.getMajorLocationByItineraryPlanId(itineraryPlan.getItineraryPlanId());
		List<MajorLocationModel> majorLocationModels = new ArrayList<MajorLocationModel>();
		for (MajorLocation majorLocation : majorLocations) {
			MajorLocationModel majorLocationModel = new MajorLocationModel();
			BeanUtils.copyProperties(majorLocation, majorLocationModel);
			majorLocationModels.add(majorLocationModel);
			List<ItineraryPlanOutletModel> itineraryPlanOutletModels = new ArrayList<ItineraryPlanOutletModel>();
			List<ItineraryPlanOutlet> itineraryPlanOutlets = itineraryPlanOutletDao.getOuletByMajorLocationId(majorLocationModel.getMajorLocationId());
			for (ItineraryPlanOutlet itineraryPlanOutlet : itineraryPlanOutlets) {
				ItineraryPlanOutletModel itineraryPlanOutletModel = new ItineraryPlanOutletModel();
				itineraryPlanOutletModel.setRemovable(true);
				Outlet outlet = itineraryPlanOutlet.getOutlet();
				List<Integer> assignmentIds = new ArrayList<Integer>();
				if (outlet != null && assignmentMap.containsKey(outlet)){
					List<Assignment> assignmentList = assignmentMap.get(outlet);
					for (Assignment assignment : assignmentList) {
						assignmentIds.add(assignment.getAssignmentId());
						if (assignment.getAssignedCollectionDate() != null && assignment.getAssignedCollectionDate().equals(itineraryPlan.getDate())){
							itineraryPlanOutletModel.setRemovable(false);
						}
					}
				}
//				for (Assignment assignment : assignments) {
//					if (assignment.getOutlet() != null && outlet != null &&
//							assignment.getOutlet().getOutletId() == outlet.getOutletId()){
//						assignmentIds.add(assignment.getAssignmentId());
//						
//						if (assignment.getAssignedCollectionDate() != null && assignment.getAssignedCollectionDate().equals(itineraryPlan.getDate())){
//							itineraryPlanOutletModel.setRemovable(false);
//						}
//					}
//				}
				
				if (itineraryPlanOutlet.getPlanType() == 1) {
					List<Integer> outletIds = new ArrayList<Integer>();
					outletIds.add(outlet.getOutletId());
					ItineraryPlanOutletModel itineraryPlanOutletModelDetails = null;
					if (itineraryPlan.getStatus() != null && itineraryPlan.getStatus().equals("Approved")){
						//2018-01-17 cheung fix if multiple users in 1 assignment, the no. of quotations will be wrong after approved itinerary plan.
//						List<ItineraryPlanOutletModel> models = itineraryPlanDao.getPlanOutlet(null, outletIds, null, assignmentIds);
						List<ItineraryPlanOutletModel> models = itineraryPlanDao.getPlanOutlet(itineraryPlan.getUser().getUserId(), outletIds, null, assignmentIds);
						if (models.size() == 0) continue;
						itineraryPlanOutletModelDetails = models.get(0);
					}
					else{
						List<ItineraryPlanOutletModel> models =itineraryPlanDao.getPlanOutlet(itineraryPlanEditModel.getUserId(), outletIds, itineraryPlan.getDate(), assignmentIds);
						if (models.size() == 0) continue;
						itineraryPlanOutletModelDetails = models.get(0);
					}
					
					//ItineraryPlanOutletModel itineraryPlanOutletModelDetails = itineraryPlanDao.getPlanOutlet(itineraryPlanEditModel.getUserId(), outletIds, itineraryPlan.getDate(), null, assignmentIds).get(0);
					BeanUtils.copyProperties(itineraryPlanOutletModelDetails, itineraryPlanOutletModel);
					itineraryPlanOutletModel.setConvenientTime(
							(itineraryPlanOutletModelDetails.getConvenientStartTime() == null ? "" : commonService.formatTime(itineraryPlanOutletModelDetails.getConvenientStartTime()))+"-"
							+(itineraryPlanOutletModelDetails.getConvenientEndTime() == null ? "" : commonService.formatTime(itineraryPlanOutletModelDetails.getConvenientEndTime())));
					itineraryPlanOutletModel.setConvenientTime2(
							(itineraryPlanOutletModelDetails.getConvenientStartTime2() == null ? "" : commonService.formatTime(itineraryPlanOutletModelDetails.getConvenientStartTime2()))+"-"
							+(itineraryPlanOutletModelDetails.getConvenientEndTime2() == null ? "" : commonService.formatTime(itineraryPlanOutletModelDetails.getConvenientEndTime2())));
					BeanUtils.copyProperties(itineraryPlanOutlet, itineraryPlanOutletModel);
				} else {
					assignmentIds.add(itineraryPlanOutlet.getAssignment().getId());
					ItineraryPlanOutletModel itineraryPlanOutletModelDetails = null;
					if (itineraryPlan.getStatus() != null && itineraryPlan.getStatus().equals("Approved")){
						List<ItineraryPlanOutletModel> models = itineraryPlanDao.getImportedAssignment(null, assignmentIds, itineraryPlan.getDate(), null);
						if (models.size() == 0) continue;
						itineraryPlanOutletModelDetails = models.get(0);
					} else {
						List<ItineraryPlanOutletModel> models = itineraryPlanDao.getImportedAssignment(itineraryPlanEditModel.getUserId(), assignmentIds, itineraryPlan.getDate(), null);
						if (models.size() == 0) continue;
						itineraryPlanOutletModelDetails = models.get(0);
					}
					BeanUtils.copyProperties(itineraryPlanOutletModelDetails, itineraryPlanOutletModel);
					BeanUtils.copyProperties(itineraryPlanOutlet, itineraryPlanOutletModel);
				}
				
//				for (Assignment assignment : assignments) {
//					if (assignment.getAssignedCollectionDate() != null && assignment.getAssignedCollectionDate().equals(itineraryPlan.getDate())){
//						itineraryPlanOutletModel.setRemovable(false);
//					}
//				}
				
				if (outlet != null && assignmentMap.containsKey(outlet)){
					List<Assignment> assignmentList = assignmentMap.get(outlet);
					for (Assignment assignment : assignmentList) {
						if (assignment.getAssignedCollectionDate() != null && assignment.getAssignedCollectionDate().equals(itineraryPlan.getDate())){
							itineraryPlanOutletModel.setRemovable(false);
						}
					}
				}
				
				
				itineraryPlanOutletModel.setAssignmentIds(assignmentIds);
				Log.info("itineraryPlanOutletModel.getAssignmentIds().size():"+itineraryPlanOutletModel.getAssignmentIds().size());
				itineraryPlanOutletModels.add(itineraryPlanOutletModel);
			}
			Collections.sort(itineraryPlanOutletModels);
			majorLocationModel.setItineraryPlanOutletModels(itineraryPlanOutletModels);
			Log.info("majorLocationModel.getItineraryPlanOutletModels().size():"+majorLocationModel.getItineraryPlanOutletModels().size());
		
		}
		Collections.sort(majorLocationModels);
		itineraryPlanEditModel.setMajorLocations(majorLocationModels);
		//Log.info("itineraryPlanEditModel.getMajorLocations().size():"+itineraryPlanEditModel.getMajorLocations().size());

		return itineraryPlanEditModel;
	}
	
	/**
	 * Save
	 * @throws ParseException 
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Integer saveItineraryPlan(ItineraryPlanEditModel itineraryPlanEditModel) throws ParseException {
		ItineraryPlan plan;
		
		itineraryPlanEditModel.setDate(commonService.getDate(itineraryPlanEditModel.getInputDate()));
		
		if (itineraryPlanEditModel.getItineraryPlanId() != null) {
			plan = itineraryPlanDao.findById(itineraryPlanEditModel.getItineraryPlanId());
			if (plan == null) {
				return 1;
			}
		} else {
			if ( itineraryPlanDao.countPlanByOfficerIdAndDate(itineraryPlanEditModel.getUserId(), itineraryPlanEditModel.getDate()) != 0) {
				return 2;
			}
			plan = new ItineraryPlan();
			User user = userDao.findById(itineraryPlanEditModel.getUserId());
			plan.setUser(user);
		}

		BeanUtils.copyProperties(itineraryPlanEditModel, plan);
		if (itineraryPlanEditModel.getSupervisorId() != null){
			User supervisor = userDao.findById(itineraryPlanEditModel.getSupervisorId());
			plan.setSupervisor(supervisor);	
		}
		List<Assignment> assignments = new ArrayList<Assignment>();
		if (itineraryPlanEditModel.getMajorLocations() != null) {
			for(MajorLocationModel majorLocationModel: itineraryPlanEditModel.getMajorLocations()){
				if (!majorLocationModel.getIsFreeEntryTask()) {
					for(ItineraryPlanOutletModel itineraryPlanOutletModel : majorLocationModel.getItineraryPlanOutletModels()) {
						assignments.addAll(assignmentMaintenanceService.getAssignmentByIds(itineraryPlanOutletModel.getAssignmentIds()));
//						assignments.addAll(assignmentDao.getByIds( itineraryPlanOutletModel.getAssignmentIds().toArray(new Integer[itineraryPlanOutletModel.getAssignmentIds().size()])));
					}
				}
			}
		}
		plan.setAssignments(new HashSet<Assignment>(assignments));
		itineraryPlanDao.save(plan);
		
		// MajorLocation 
		List<MajorLocation> oldMajorLocations = new ArrayList<MajorLocation>();
		if(plan.getItineraryPlanId() != null && plan.getItineraryPlanId() > 0){
			oldMajorLocations = majorLocationDao.getMajorLocationByItineraryPlanId(plan.getItineraryPlanId());
		}

		List<MajorLocationModel> newMajorLocationModels = new ArrayList<MajorLocationModel>();
		List<Integer> oldIds = new ArrayList<Integer>();
		List<Integer> newIds = new ArrayList<Integer>();
		
		List<Integer> oldOutletIds = new ArrayList<Integer>();
		List<Integer> newOutletIds = new ArrayList<Integer>();
		
		if(itineraryPlanEditModel.getMajorLocations() != null) {
			newMajorLocationModels.addAll(itineraryPlanEditModel.getMajorLocations());
			for(MajorLocation oldMajorLocation: oldMajorLocations){
				oldIds.add(oldMajorLocation.getMajorLocationId());
			}
		}
			
		if(newMajorLocationModels.size() > 0){
			for(MajorLocationModel newMajorLocationModel: newMajorLocationModels){
				MajorLocation saveMajorLocation;
				if (newMajorLocationModel.getMajorLocationId() != null && newMajorLocationModel.getMajorLocationId() > 0) {
					newIds.add(newMajorLocationModel.getMajorLocationId());
					saveMajorLocation = majorLocationDao.findById(newMajorLocationModel.getMajorLocationId());
				} else {
					saveMajorLocation = new MajorLocation();
				}
				BeanUtils.copyProperties(newMajorLocationModel, saveMajorLocation);
				saveMajorLocation.setItineraryPlan(plan);
				majorLocationDao.save(saveMajorLocation);
				
				// ItineraryPlanOutlet 
				List <ItineraryPlanOutlet> oldItineraryPlanOutlets = new ArrayList<ItineraryPlanOutlet>();
				if(newMajorLocationModel.getMajorLocationId() != null && newMajorLocationModel.getMajorLocationId()  > 0){
					oldItineraryPlanOutlets = itineraryPlanOutletDao.getOuletByMajorLocationId(newMajorLocationModel.getMajorLocationId() );
				}
				
				List<ItineraryPlanOutletModel> newItineraryPlanOutletModels = new ArrayList<ItineraryPlanOutletModel>();
				
				
				if(newMajorLocationModel.getItineraryPlanOutletModels() != null) {
					newItineraryPlanOutletModels.addAll(newMajorLocationModel.getItineraryPlanOutletModels());
					for(ItineraryPlanOutlet oldItineraryPlanOutlet: oldItineraryPlanOutlets){
						oldOutletIds.add(oldItineraryPlanOutlet.getItineraryPlanOutletId());
					}
				}
				
				if(newItineraryPlanOutletModels.size() > 0){
					for(ItineraryPlanOutletModel newItineraryPlanOutletModel: newItineraryPlanOutletModels){
						ItineraryPlanOutlet saveItineraryPlanOutlet;
						if (newItineraryPlanOutletModel.getItineraryPlanOutletId() != null && newItineraryPlanOutletModel.getItineraryPlanOutletId() > 0) {
							newOutletIds.add(newItineraryPlanOutletModel.getItineraryPlanOutletId());
							saveItineraryPlanOutlet = itineraryPlanOutletDao.findById(newItineraryPlanOutletModel.getItineraryPlanOutletId());
						} else {
							saveItineraryPlanOutlet = new ItineraryPlanOutlet();
						}
						BeanUtils.copyProperties(newItineraryPlanOutletModel, saveItineraryPlanOutlet);
						saveItineraryPlanOutlet.setItineraryPlan(plan);
						saveItineraryPlanOutlet.setMajorLocation(saveMajorLocation);
						saveItineraryPlanOutlet.setMajorLocationSequence(newMajorLocationModel.getSequence());
						if (saveItineraryPlanOutlet.getPlanType() == 1 ) {
							Outlet outlet = outletDao.findById(newItineraryPlanOutletModel.getOutletId());
							saveItineraryPlanOutlet.setOutlet(outlet);
							String referenceNo = outlet.getTpu().getDistrict().getCode()+outlet.getFirmCode();
							saveItineraryPlanOutlet.setReferenceNo(referenceNo);
						}
						else{
							List<Integer> importedAssignments = newItineraryPlanOutletModel.getAssignmentIds();
							if (importedAssignments.size() > 0){
								Assignment assignment = assignmentDao.findById(importedAssignments.get(0));
								saveItineraryPlanOutlet.setAssignment(assignment);				
								saveItineraryPlanOutlet.setReferenceNo(assignment.getReferenceNo());
							}
						}
						itineraryPlanOutletDao.save(saveItineraryPlanOutlet);
					}
					
				}
				

			}
		}
		
		Collection<Integer> removeEntries = CollectionUtils.subtract(oldIds, newIds);
		if (removeEntries.size() > 0){
			for (Integer removeEntry: removeEntries){
				MajorLocation majorLocation = majorLocationDao.findById(removeEntry);
				for (ItineraryPlanOutlet itineraryPlanOutlet: majorLocation.getOutlets()){
					oldOutletIds.add(itineraryPlanOutlet.getItineraryPlanOutletId());
				}
				majorLocationDao.delete(majorLocation);
			}
		}
		
		Collection<Integer> removeOutletIds = CollectionUtils.subtract(oldOutletIds, newOutletIds);
		if (removeOutletIds.size() > 0){
			for (Integer removeOutletId: removeOutletIds){
				ItineraryPlanOutlet itineraryPlanOutlet = itineraryPlanOutletDao.findById(removeOutletId);
				itineraryPlanOutletDao.delete(itineraryPlanOutlet);
			}
		}
		
//		if (itineraryPlanEditModel.getStatus().equals("Submitted")){
//			String subject = messageSource.getMessage("N00028", null, Locale.ENGLISH);
//			String user = String.format("%s - %s", plan.getUser().getStaffCode(), plan.getUser().getChineseName());
//			String date = commonService.formatDate(plan.getDate());
//			String content = messageSource.getMessage("N00029", new Object[]{user, date}, Locale.ENGLISH);
//			notifyService.sendNotification(plan.getSupervisor(), subject, content, false);
//		}
		
		itineraryPlanDao.flush();

		return 0;
	}
	
	/**
	 * Undo
	 */
	@Transactional
	public boolean undoItineraryPlan(Integer id) {
		
		ItineraryPlan plan = itineraryPlanDao.findById(id);
		if (plan == null) {
			return false;
		}
		plan.setStatus("Draft");
		itineraryPlanDao.save(plan);	
		itineraryPlanDao.flush();
		return true;
	}
	
	/**
	 * Cancel (changed to delete logic 20160202)
	 */
	@Transactional
	public boolean cancelItineraryPlan(List<Integer> ids) {
		List<ItineraryPlan> plans = itineraryPlanDao.getItineraryByIds(ids);
		if (plans.size() != ids.size()){
			return false;
		}
		
		for (ItineraryPlan plan: plans){
			Set<ItineraryPlanOutlet> outlets = plan.getOutlets();
			if (outlets != null && outlets.size() > 0){
				for (ItineraryPlanOutlet outlet : outlets){
					itineraryPlanOutletDao.delete(outlet);
				}
			}
			
			Set<MajorLocation> majorLocations = plan.getMajorLocations();
			if (majorLocations != null && majorLocations.size() > 0){
				for (MajorLocation majorLocation : majorLocations){
					majorLocationDao.delete(majorLocation);
				}
			}
			plan.getUnPlanAssignments().clear();
			plan.getAssignments().clear();
			
			itineraryPlanDao.delete(plan);
		} 
		
		
		/*
		List<ItineraryPlan> plans = itineraryPlanDao.getItineraryByIds(ids);
		if (plans.size() != ids.size()){
			return false;
		}

		for (ItineraryPlan plan: plans){
			plan.setStatus("Cancelled");
			itineraryPlanDao.save(plan);
		}
		*/
		itineraryPlanDao.flush();

		return true;
	}
	
	/**
	 * Change Itinerary Plan Status
	 */
	@Transactional
	public boolean setItineraryPlanStatus(Map<Integer,Integer> itineraryPlanIdWithVersion, String status, String rejectReason) {
		
		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(itineraryPlanIdWithVersion.keySet());
		
		List<ItineraryPlan> plans = itineraryPlanDao.getItineraryByIds(ids);
		if (plans.size() != ids.size()){
			return false;
		}

		for (ItineraryPlan plan : plans){
			plan.setVersion(itineraryPlanIdWithVersion.get(plan.getItineraryPlanId()));
			plan.setStatus(status);
			if (rejectReason != null) {
				plan.setRejectReason(rejectReason);
			}
			itineraryPlanDao.save(plan);
			if (status.equals("Rejected")){
				String code = "N00027";
				String message = messageSource.getMessage(code, new Object[]{commonService.formatDate(plan.getDate())}, Locale.ENGLISH);				
				notifyService.sendNotification(plan.getUser(), message, message, true);
			}
			
		}
		
		itineraryPlanDao.flush();
		return true;
	}
	
	/** 
	 *  QC Itinerary plan datatable
	 * @throws ParseException 
	 */
	public DatatableResponseModel<QCItineraryPlanTableList> queryQCItineraryPlan(DatatableRequestModel model) throws ParseException{
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Order order = this.getOrder(model, "", "q.date","scCount","svCount","peCount", "status" );
		String search = model.getSearch().get("value");
		
		Integer userId = null;
		
		if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2){
			
		} else {
			userId = detail.getUserId();
		}
		
		List<QCItineraryPlanTableList> result = qcItineraryPlanDao.getTableList(search, model.getStart(), model.getLength(), order, userId );
		
		DatatableResponseModel<QCItineraryPlanTableList> response = new DatatableResponseModel<QCItineraryPlanTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = qcItineraryPlanDao.countTableList("", userId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = qcItineraryPlanDao.countTableList(search, userId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 *  QC Itinerary plan approval datatable
	 * @throws ParseException 
	 */
	public DatatableResponseModel<QCItineraryPlanApprovalTableList> queryQCItineraryPlanApproval(DatatableRequestModel model) {
		
		Order order = this.getOrder(model, "", "q.date", "submitTo", "status" );
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		List<Integer> userIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 256) == 256 || (detail.getAuthorityLevel() & 2048) == 2048)){
			List<Integer> acted = detail.getActedUsers();
			userIds.add(detail.getUserId());
			userIds.addAll(acted);
		}
		
		List<QCItineraryPlanApprovalTableList> result = qcItineraryPlanDao.getApprovalTableList(search, model.getStart(), model.getLength(), order, userIds);
		
		DatatableResponseModel<QCItineraryPlanApprovalTableList> response = new DatatableResponseModel<QCItineraryPlanApprovalTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = qcItineraryPlanDao.countApprovalTableList("", userIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = qcItineraryPlanDao.countApprovalTableList(search, userIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	/**
	 * Get QCItineraryPlanEditModel
	 */
	public QCItineraryPlanModel getQCItineraryPlanModel(Integer id) {
		
		QCItineraryPlan qcItineraryPlan = qcItineraryPlanDao.findById(id);
		QCItineraryPlanModel qcItineraryPlanModel = new QCItineraryPlanModel();
		User user = qcItineraryPlan.getUser();
		User submitTo = qcItineraryPlan.getSubmitTo();
		
		BeanUtils.copyProperties(qcItineraryPlan, qcItineraryPlanModel);
		if (user != null) {
			qcItineraryPlanModel.setUser(user.getStaffCode() + " - " + user.getChineseName());
			qcItineraryPlanModel.setUserId(user.getUserId());
		}
		if (submitTo != null) {
			qcItineraryPlanModel.setSubmitTo(submitTo.getStaffCode() + " - " + submitTo.getChineseName());
			qcItineraryPlanModel.setSubmitToId(submitTo.getUserId());
		}	
		qcItineraryPlanModel.setSubmitStatus(qcItineraryPlan.getStatus());
		
		Set<QCItineraryPlanItem> qcItineraryPlanItems = qcItineraryPlan.getQcItineraryPlanItems();
		List<QCItineraryPlanItemModel> qcItineraryPlanItemModels = new ArrayList<QCItineraryPlanItemModel>();

		for (QCItineraryPlanItem qcItineraryPlanItem : qcItineraryPlanItems) {
			QCItineraryPlanItemModel qcItineraryPlanItemModel = new QCItineraryPlanItemModel();
			BeanUtils.copyProperties(qcItineraryPlanItem, qcItineraryPlanItemModel);
			
			if (qcItineraryPlanItem.getSpotCheckForm() != null) {
				qcItineraryPlanItemModel.setSpotCheckFormId(qcItineraryPlanItem.getSpotCheckForm().getSpotCheckFormId());
				qcItineraryPlanItemModel.setOfficerName(qcItineraryPlanItem.getSpotCheckForm().getOfficer().getChineseName());
				
				String displayItem = qcItineraryPlanItem.getSpotCheckForm().getOfficer().getChineseName();
				qcItineraryPlanItemModel.setDisplayItem(displayItem);
			}
			if (qcItineraryPlanItem.getSupervisoryVisitForm() != null) {
				qcItineraryPlanItemModel.setSupervisoryVisitFormId(qcItineraryPlanItem.getSupervisoryVisitForm().getSupervisoryVisitFormId());
				qcItineraryPlanItemModel.setOfficerName(qcItineraryPlanItem.getSupervisoryVisitForm().getUser().getChineseName());
				
				String displayItem = qcItineraryPlanItem.getSupervisoryVisitForm().getUser().getChineseName();
				qcItineraryPlanItemModel.setDisplayItem(displayItem);
			}
			if (qcItineraryPlanItem.getPeCheckForm() != null) {
				qcItineraryPlanItemModel.setPeCheckFormId(qcItineraryPlanItem.getPeCheckForm().getPeCheckFormId());
				qcItineraryPlanItemModel.setOfficerName(qcItineraryPlanItem.getPeCheckForm().getOfficer().getChineseName());
				
				String displayItem = qcItineraryPlanItem.getPeCheckForm().getOfficer().getChineseName();
				displayItem += ", Firm : " + qcItineraryPlanItem.getPeCheckForm().getAssignment().getOutlet().getName();
				displayItem += ", District : " + qcItineraryPlanItem.getPeCheckForm().getAssignment().getOutlet().getTpu().getDistrict().getChineseName();
				displayItem += ", TPU : " + qcItineraryPlanItem.getPeCheckForm().getAssignment().getOutlet().getTpu().getCode();
				
				qcItineraryPlanItemModel.setDisplayItem(displayItem);
			}
			qcItineraryPlanItemModels.add(qcItineraryPlanItemModel);
		}
		Collections.sort(qcItineraryPlanItemModels);
		qcItineraryPlanModel.setQcItineraryPlanItemModel(qcItineraryPlanItemModels);

		return qcItineraryPlanModel;
	}

	/**
	 * Unod QC Itinerary Plan
	 */
	@Transactional
	public void undoQCItineraryPlan(Integer id) {
		
		QCItineraryPlan qcItineraryPlan = qcItineraryPlanDao.findById(id);		
		qcItineraryPlan.setStatus("Draft");
		qcItineraryPlanDao.save(qcItineraryPlan);
		qcItineraryPlanDao.flush();
		
	}
	
	/**
	 * Change QC Itinerary Plan Status
	 */
	@Transactional
	public boolean setQCItineraryPlanStatus(List<Integer> qcItineraryPlanIds, String status, String rejectReason) {
		
		List<QCItineraryPlan> plans = qcItineraryPlanDao.getQCItineraryByIds(qcItineraryPlanIds);
		if (plans.size() != qcItineraryPlanIds.size()){
			return false;
		}

		for (QCItineraryPlan plan : plans){
			plan.setStatus(status);
			if (rejectReason != null) {
				plan.setRejectReason(rejectReason);
			}
			qcItineraryPlanDao.save(plan);
			if (status.equals("Rejected")){
				String code = "N00027";
				String message = messageSource.getMessage(code, new Object[]{commonService.formatDate(plan.getDate())}, Locale.ENGLISH);				
				notifyService.sendNotification(plan.getUser(), message, message, true);
			}
		}
		
		qcItineraryPlanDao.flush();
		
		return true;
	}
	
	/* 
	 * Get Spot and Supervisory check items
	 */
	public List<QCItineraryPlanItemModel> getCheckItems(Date date) {
		List<QCItineraryPlanItemModel>  items = new ArrayList<QCItineraryPlanItemModel>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		List<SpotCheckForm> spotChecks = spotCheckFormDao.getSpotCheckFormsByCheckDate(date,detail.getUserId());
		List<SupervisoryVisitForm> supervisoryChecks = supervisoryVisitFormDao.getSupervisoryVisitFormsByVisitDate(date,detail.getUserId());
		
		for (SpotCheckForm check : spotChecks) {
			QCItineraryPlanItemModel item = new QCItineraryPlanItemModel();
			item.setSpotCheckFormId(check.getSpotCheckFormId());
			item.setOfficerName(check.getOfficer().getChineseName());
			item.setDisplayItem(check.getOfficer().getChineseName());
			item.setItemType(1);
			items.add(item);
		}
		
		for (SupervisoryVisitForm check : supervisoryChecks) {
			QCItineraryPlanItemModel item = new QCItineraryPlanItemModel();
			item.setSupervisoryVisitFormId(check.getSupervisoryVisitFormId());
			item.setOfficerName(check.getUser().getChineseName());
			item.setDisplayItem(check.getUser().getChineseName());
			item.setItemType(2);
			items.add(item);
		}
				
		return items;	
	}
	
	/**
	 * Save QC Itinerary Plan
	 * @throws ParseException 
	 */
	@Transactional
	public String saveQCItineraryPlan(QCItineraryPlanModel qcItineraryPlanModel) throws ParseException {

		QCItineraryPlan plan;
		
		qcItineraryPlanModel.setDate(commonService.getDate(qcItineraryPlanModel.getInputDate()));
		
		if (qcItineraryPlanModel.getQcItineraryPlanId() != null) {
			plan = qcItineraryPlanDao.findById(qcItineraryPlanModel.getQcItineraryPlanId());
			if (plan == null) {
				return "E00011";
			}
			//Logger.getGlobal().info(commonService.formatDateTime(plan.getModifiedDate()) + " ? " + qcItineraryPlanModel.getInputModifiedDate());
//			if (!commonService.formatDateTime(plan.getModifiedDate()).equals(qcItineraryPlanModel.getInputModifiedDate().trim())) {
//				return "E00105";
//			}
		} else {
			plan = new QCItineraryPlan();
		}

		BeanUtils.copyProperties(qcItineraryPlanModel, plan);
		User user = userDao.findById(qcItineraryPlanModel.getUserId());
		plan.setUser(user);
		if (qcItineraryPlanModel.getSubmitToId() != null){
			User submitTo = userDao.findById(qcItineraryPlanModel.getSubmitToId());
			plan.setSubmitTo(submitTo);
		}
		
		if (qcItineraryPlanModel.getSubmitStatus().equals("Submitted")){
			plan.setStatus("Submitted");
		}
		else{
			plan.setStatus("Draft");
		}

		qcItineraryPlanDao.save(plan);
		
		// Items 
		Set<QCItineraryPlanItem> oldItems = new HashSet<QCItineraryPlanItem>();
		if(plan.getQcItineraryPlanItems() != null && plan.getQcItineraryPlanItems().size() > 0){
			oldItems = plan.getQcItineraryPlanItems();
		}

		List<QCItineraryPlanItemModel> newModels = new ArrayList<QCItineraryPlanItemModel>();
		List<Integer> oldIds = new ArrayList<Integer>();
		List<Integer> newIds = new ArrayList<Integer>();

		for(QCItineraryPlanItem oldItem: oldItems){
			oldIds.add(oldItem.getQcItineraryPlanItemId());
		}
		
		if(qcItineraryPlanModel.getQcItineraryPlanItemModel() != null) {
			newModels.addAll(qcItineraryPlanModel.getQcItineraryPlanItemModel());
		}
			
		if(newModels.size() > 0){
			for(QCItineraryPlanItemModel newModel: newModels){
				QCItineraryPlanItem saveItem;
				if (newModel.getQcItineraryPlanItemId() != null && newModel.getQcItineraryPlanItemId() > 0) {
					newIds.add(newModel.getQcItineraryPlanItemId());
					saveItem = qcItineraryPlanItemDao.findById(newModel.getQcItineraryPlanItemId());
				} else {
					saveItem = new QCItineraryPlanItem();
				}
				BeanUtils.copyProperties(newModel, saveItem);
				switch (newModel.getItemType()) {
				case 1:
					saveItem.setSpotCheckForm(spotCheckFormDao.findById(newModel.getSpotCheckFormId()));
					break;
				case 2:
					saveItem.setSupervisoryVisitForm(supervisoryVisitFormDao.findById(newModel.getSupervisoryVisitFormId()));
					break;
				case 3:
					saveItem.setPeCheckForm(peCheckFormDao.findById(newModel.getPeCheckFormId()));
					break;
				}
				saveItem.setQcItineraryPlan(plan);
				qcItineraryPlanItemDao.save(saveItem);
			}
		}
		
		Collection<Integer> removeEntries = CollectionUtils.subtract(oldIds, newIds);
		if (removeEntries.size() > 0){
			for (Integer removeEntry: removeEntries){
				QCItineraryPlanItem item = qcItineraryPlanItemDao.findById(removeEntry);
				qcItineraryPlanItemDao.delete(item);
			}
		}
		
//		if (plan.getStatus().equals("Submitted")){
//			String subject = messageSource.getMessage("N00028", null, Locale.ENGLISH);
//			String name = String.format("%s - %s", plan.getUser().getStaffCode(), plan.getUser().getChineseName());
//			String date = commonService.formatDate(plan.getDate());
//			String content = messageSource.getMessage("N00029", new Object[]{name, date}, Locale.ENGLISH);
//			notifyService.sendNotification(plan.getSubmitTo(), subject, content, false);
//		}
		
		qcItineraryPlanDao.flush();

		return null;
	}
	
	
	public List<String> getFuturePlanDate(Integer userId){
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		return itineraryPlanDao.getFuturePlanDate(today, userId);		
		
	}
	
	public List<String> getQCFuturePlanDate(Integer userId){
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		return qcItineraryPlanDao.getFuturePlanDate(today, userId);		
		
	}
	
	public List<ItineraryPlanSyncData> getUpdateItineraryPlan(Date lastSyncTime, Integer[] itineraryPlanIds){
		return itineraryPlanDao.getUpdateItineraryPlan(lastSyncTime, itineraryPlanIds);
	}
	
	public List<ItineraryPlanAssignmentSyncData> getUpdateItineraryPlanAssignment(Date lastSyncTime, Integer[] itineraryPlanIds){
		return itineraryPlanDao.getUpdateItineraryPlanAssignment(lastSyncTime, itineraryPlanIds);
	}
	
	public List<MajorLocationSyncData> getUpdateMajorLocation(Date lastSyncTime, Integer[] itineraryPlanIds){
		return majorLocationDao.getUpdateMajorLocation(lastSyncTime, itineraryPlanIds);
	}
	
	public List<ItineraryPlanOutletSyncData> getUpdateItineraryPlanOutlet(Date lastSyncTime, Integer[] itineraryPlanIds){
		return itineraryPlanOutletDao.getUpdateItineraryPlanOutlet(lastSyncTime, itineraryPlanIds);
	}
	
	public List<ItineraryUnPlanAssignmentSyncData> getUpdateItineraryUnPlanAssignmentSyncData(Date lastSyncTime, Integer[] itineraryPlanIds){
		return itineraryPlanDao.getUpdateItineraryUnPlanAssignment(lastSyncTime, itineraryPlanIds);
	}
	
	public List<QCItineraryPlanSyncData> getUpdateQCItineraryPlan(Date lastSyncTime, Integer[] qcItineraryPlanIds){
		return qcItineraryPlanDao.getUpdateQCItineraryPlan(lastSyncTime, qcItineraryPlanIds);
	}
	
	public List<QCItineraryPlanItemSyncData> getUpdateQCItineraryPlanItem(Date lastSyncTime, Integer[] qcItineraryPlanIds){
		return qcItineraryPlanItemDao.getUpdateQCItineraryPlanItem(lastSyncTime, qcItineraryPlanIds);
	}
	
	public Integer getPlanId(Integer userId, Date date) {
		return itineraryPlanDao.getPlanId(userId, date);
	}
	
	/** 
	 *  Itinerary plan datatable for approval
	 * @throws ParseException 
	 */
	public Long queryItineraryPlanApprovalCount() {
		
		Date date = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & 4) == 4){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		}
		
		if((detail.getAuthorityLevel() & 2) == 2) {
			suerpervisorIds.add(userId);
			suerpervisorIds.addAll(detail.getActedUsers());
			List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(suerpervisorIds);
			suerpervisorIds = actedUserSubordinateIds;
		}
		
		String[] status = { "Submitted" };
		
		return itineraryPlanDao.countLookupApprovalTableList("", date, suerpervisorIds, null, status);
	}
	
	@Transactional
	public void deleteQCItineraryPlan(List<Integer> ids){
		if (ids != null && ids.size() > 0){
			List<QCItineraryPlan> list = qcItineraryPlanDao.getQCItineraryByIds(ids);
			if (list != null && list.size() > 0){
				for (QCItineraryPlan plan : list){
					Set<QCItineraryPlanItem> items = plan.getQcItineraryPlanItems();
					if (items != null && items.size() > 0){
						for (QCItineraryPlanItem item : items){
							qcItineraryPlanItemDao.delete(item);
						}
					}
					qcItineraryPlanDao.delete(plan);
				}
			}
			qcItineraryPlanDao.flush();
		}
	}
	
	public boolean checkItineraryReferenceNo(List<String> referenceNo, Integer userId, Date date){
		List<String> planRefNo = itineraryPlanDao.getDistinctReferenceNo(userId, date);
		
		if(referenceNo == null)
			referenceNo = new ArrayList<String>();
		
		Collection<String> newRef =  CollectionUtils.subtract(referenceNo, planRefNo);
		
		return newRef.size() == 0;
	}
}
