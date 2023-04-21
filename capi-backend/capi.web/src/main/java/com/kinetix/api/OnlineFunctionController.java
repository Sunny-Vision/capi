package com.kinetix.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.dal.UserDao;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.SystemConstant;
import capi.model.api.LoginResultModel;
import capi.model.api.MobileResponseModel;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.QuotationRecordSyncData;
import capi.model.api.dataSync.SyncModel;
import capi.model.api.onlineFunction.AssignmentDataReturn;
import capi.model.api.onlineFunction.AssignmentOnlineModel;
import capi.model.api.onlineFunction.ItineraryPlanOnlineModel;
import capi.model.api.onlineFunction.NonScheduleAssignmentDetail;
import capi.model.api.onlineFunction.NonScheduleAssignmentDetailList;
import capi.model.api.onlineFunction.NonScheduleAssignmentList;
import capi.model.api.onlineFunction.PECheckFormOnlineModel;
import capi.model.api.onlineFunction.QCItineraryPlanOnlineModel;
import capi.model.api.onlineFunction.QuotationOnlineModel;
import capi.model.api.onlineFunction.QuotationRecordOnlineModel;
import capi.model.api.onlineFunction.SpotCheckFormOnlineModel;
import capi.model.api.onlineFunction.SupervisoryVisitFormOnlineModel;
import capi.model.api.onlineFunction.TimeLogOnlineModel;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.MobileDataConversionService;
import capi.service.masterMaintenance.OutletService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.model.api.onlineFunction.CheckAssignmentAndQuotationRecordStatus;
import capi.model.api.onlineFunction.CheckStatusSyncModel;

@Controller("OnlineFunctionController")
@RequestMapping("api/OnlineFunction")
public class OnlineFunctionController {

	private static final Logger logger = LoggerFactory.getLogger(OnlineFunctionController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private AssignmentMaintenanceService assignmentService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private MobileDataConversionService mobileService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
		
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                  new CustomDateEditor(new SimpleDateFormat(SystemConstant.DATE_TIME_FORMAT),true));
    }	
	
	@RequestMapping("getNonScheduleAssignments")
	public @ResponseBody MobileResponseModel<NonScheduleAssignmentList> getNonScheduleAssignments(Authentication auth){
		MobileResponseModel<NonScheduleAssignmentList> model = new MobileResponseModel<NonScheduleAssignmentList>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getNonScheduleAssignments start");
			List<NonScheduleAssignmentList> data = assignmentService.getOnlineNonScheduleAssignment(userId);
			model.setData(data);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getNonScheduleAssignments end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getNonScheduleAssignments failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("getNonScheduleAssignmentDetails")
	public @ResponseBody MobileResponseModel<NonScheduleAssignmentDetail> getNonScheduleAssignmentDetails(
			@RequestParam(value = "assignmentId", required=false) Integer assignmentId
			, @RequestParam(value = "userIds", required=false) Integer[] userIds
			, Authentication auth, Locale locale){
		MobileResponseModel<NonScheduleAssignmentDetail> model = new MobileResponseModel<NonScheduleAssignmentDetail>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer submitUserId = detail.getUserId();
		
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetails start");
			if(assignmentId==null || assignmentId<0){
				model.setMessage("Data sync getNonScheduleAssignmentDetails failure: Assignment Id should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetails failure: "
						+ "Assignment Id should not be empty");
			} else {
				NonScheduleAssignmentDetail data = assignmentService.getDownloadNonScheduleAssignmentDetail(assignmentId);

				
				/**
				 * Send Notification
				 */
				User submitUser = userDao.findById(submitUserId);
				if (userIds != null && userIds.length > 0){
					List<User> users = userDao.getUsersByIds(Arrays.asList(userIds));
					String subject = messageSource.getMessage("N00049", null, locale);
					for(User user : users){
						String contentHeader = messageSource.getMessage("N00048", new Object[]{submitUser.getStaffCode()}, Locale.ENGLISH);	
						String content = messageSource.getMessage("N00067", new Object[]{
																				data.getOutlet().getFirmCode(), 
																				data.getOutlet().getName(),
																				//data.getQuotationRecords().size()
																				data.getNotificationQuotationRecords()
																			}, Locale.ENGLISH);
						notificationService.sendNotification(user, subject, contentHeader+"\n"+content, true);
					}				
				}
				
				List<NonScheduleAssignmentDetail> datas = new ArrayList<NonScheduleAssignmentDetail>();
				datas.add(data);
				model.setData(datas);
				model.setMessage("Online function Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetails end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetails failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	/*
	@RequestMapping("getNonScheduleAssignmentDetailsByQuotationIdHistoryDate")
	public @ResponseBody MobileResponseModel<NonScheduleAssignmentDetailList> getNonScheduleAssignmentDetailsByQuotationIdHistoryDate(
			@RequestBody SyncModel<QuotationOnlineModel> syncModel
			, Authentication auth, Locale locale){
		MobileResponseModel<NonScheduleAssignmentDetailList> model = new MobileResponseModel<NonScheduleAssignmentDetailList>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer submitUserId = detail.getUserId();
		
		try{			
			NonScheduleAssignmentDetailList returnData = new NonScheduleAssignmentDetailList();
			returnData.setAssignments(new ArrayList<AssignmentSyncData>());
			returnData.setOutlets(new ArrayList<OutletSyncData>());
			returnData.setQuotationRecords(new ArrayList<QuotationRecordSyncData>());
			returnData.setQuotations(new ArrayList<QuotationSyncData>());
			returnData.setSubPriceColumns(new ArrayList<SubPriceColumnSyncData>());
			returnData.setSubPriceRecords(new ArrayList<SubPriceRecordSyncData>());
			returnData.setTourRecords(new ArrayList<TourRecordSyncData>());
			returnData.setAssignmentUnitCategoryInfos(new ArrayList<AssignmentUnitCategoryInfoSyncData>());			
			
			List<QuotationOnlineModel> requestParams = syncModel.getData();
			int i = 0;
			for (QuotationOnlineModel requestPara : requestParams) {
				NonScheduleAssignmentDetailList data = null;
				data = assignmentService.getQuotationHistoryByAssignment(requestPara);
				
				if (data.getAssignments() != null)
					returnData.getAssignments().addAll(data.getAssignments());
				if (data.getOutlets() != null)
					returnData.getOutlets().addAll(data.getOutlets());
				if (data.getQuotationRecords() != null)
					returnData.getQuotationRecords().addAll(data.getQuotationRecords());
				if (data.getQuotations() != null)
					returnData.getQuotations().addAll(data.getQuotations());
				if (data.getSubPriceColumns() != null)
					returnData.getSubPriceColumns().addAll(data.getSubPriceColumns());
				if (data.getSubPriceRecords() != null)
					returnData.getSubPriceRecords().addAll(data.getSubPriceRecords());
				if (data.getTourRecords() != null)
					returnData.getTourRecords().addAll(data.getTourRecords());
				if (data.getAssignmentUnitCategoryInfos() != null)
					returnData.getAssignmentUnitCategoryInfos().addAll(data.getAssignmentUnitCategoryInfos());
			}
			
			List<NonScheduleAssignmentDetailList> datas = new ArrayList<NonScheduleAssignmentDetailList>();
			datas.add(returnData);
			
			model.setData(datas);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
		}
		catch(Exception ex){
			logger.error("Online function failure", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	*/
	
	@RequestMapping("getNonScheduleAssignmentDetailsByQuotationIdHistoryDate")
	public @ResponseBody MobileResponseModel<NonScheduleAssignmentDetailList> getNonScheduleAssignmentDetailsByQuotationIdHistoryDate(
			@RequestBody SyncModel<QuotationOnlineModel> syncModel
			, Authentication auth, Locale locale){
		MobileResponseModel<NonScheduleAssignmentDetailList> model = new MobileResponseModel<NonScheduleAssignmentDetailList>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		// Integer submitUserId = detail.getUserId();
		
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetailsByQuotationIdHistoryDate start");
			List<QuotationOnlineModel> requestParams = syncModel.getData();
			NonScheduleAssignmentDetailList returnData = assignmentService.getQuotationHistoryByAssignment(requestParams);
			
			List<NonScheduleAssignmentDetailList> datas = new ArrayList<NonScheduleAssignmentDetailList>();
			datas.add(returnData);
			
			model.setData(datas);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetailsByQuotationIdHistoryDate end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getNonScheduleAssignmentDetailsByQuotationIdHistoryDate failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("mapOutlets")
	public @ResponseBody MobileResponseModel<OutletSyncData> mapOutlets(String name, String phone, String brCode){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletSyncData> model = new MobileResponseModel<OutletSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync mapOutlets start");
			if(StringUtils.isEmpty(name) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(brCode)){
				model.setMessage("Data sync mapOutlets failure: Name, Phone number, BR Code at least one should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync mapOutlets failure: "
						+ "Name, Phone number, BR Code at least one should not be empty");
			} else {
				List<OutletSyncData> data = outletService.getOutletbyNamePhoneBRCode(name, phone, brCode);
				model.setData(data);
				model.setMessage("Online function Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync mapOutlets end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync mapOutlets failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("submitQuotationRecords")
	public @ResponseBody MobileResponseModel<AssignmentDataReturn> submitQuotationRecords(@RequestBody SyncModel<QuotationRecordOnlineModel> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<AssignmentDataReturn> model = new MobileResponseModel<AssignmentDataReturn>();
		try{
			try {
				String syncIds = syncModel.getData().stream().map(qr->{
					String parentId = qr.getQuotationRecordId() != null? qr.getQuotationRecordId().toString(): "newID";
					if (qr.getOriginalQuotationRecordId() != null && qr.getOriginalQuotationRecordId() > 0) {
						return qr.getOriginalQuotationRecordId()+"->"+parentId;
					}
					return parentId;
				}).collect(java.util.stream.Collectors.joining(","));
				logger.info("User: "+detail.getUserId()+" SubmitQuotationRecords start for IDs: "+syncIds);
			}
			catch (Exception ex){
				logger.warn("User: "+detail.getUserId()+" SubmitQuotationRecords failed to log: ", ex);
				logger.info("User: "+detail.getUserId()+" SubmitQuotationRecords start");
			}
			AssignmentDataReturn ret = mobileService.onlineFunctionQuotationRecord(syncModel.getData());
			List<AssignmentDataReturn> list = new ArrayList<AssignmentDataReturn>();
			list.add(ret);
			model.setData(list);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" SubmitQuotationRecords end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" SubmitQuotationRecords failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("submitSpotChecks")
	public @ResponseBody MobileResponseModel<SpotCheckFormOnlineModel> submitSpotChecks(@RequestBody SyncModel<SpotCheckFormOnlineModel> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SpotCheckFormOnlineModel> model = new MobileResponseModel<SpotCheckFormOnlineModel>();
		try{
			logger.info("User: "+detail.getUserId()+" SubmitSpotChecks start");
			mobileService.submitSpotCheckForm(syncModel.getData());
			
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" SubmitSpotChecks end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" SubmitSpotChecks failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("submitSupervisoryVisitForms")
	public @ResponseBody MobileResponseModel<SupervisoryVisitFormOnlineModel> submitSupervisoryVisitForms(@RequestBody SyncModel<SupervisoryVisitFormOnlineModel> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SupervisoryVisitFormOnlineModel> model = new MobileResponseModel<SupervisoryVisitFormOnlineModel>();
		try{	
			logger.info("User: "+detail.getUserId()+" SubmitSupervisoryVisitForms start");
			mobileService.submitSupervisoryVisitForm(syncModel.getData());
			
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" SubmitSupervisoryVisitForms end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" SubmitSupervisoryVisitForms failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("submitTimeLogs")
	public @ResponseBody MobileResponseModel<TimeLogOnlineModel> submitTimeLogs(@RequestBody SyncModel<TimeLogOnlineModel> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<TimeLogOnlineModel> model = new MobileResponseModel<TimeLogOnlineModel>();
		try{	
			logger.info("User: "+detail.getUserId()+" SubmitTimeLogs start");
			mobileService.submitTimeLog(syncModel.getData());
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" SubmitTimeLogs end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" SubmitTimeLogs failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("submitAssignments")
	public @ResponseBody MobileResponseModel<AssignmentDataReturn> submitAssignments(@RequestBody SyncModel<AssignmentOnlineModel> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<AssignmentDataReturn> model = new MobileResponseModel<AssignmentDataReturn>();
		try{
			try{
				StringBuilder builder = new StringBuilder();
				for (AssignmentOnlineModel assignment : syncModel.getData()){
					String qrIds = assignment.getQuotationRecords().stream().map(qr->{
						String parentId = qr.getQuotationRecordId() != null? qr.getQuotationRecordId().toString(): "newQR";
						if (qr.getOtherQuotationRecords() != null && !qr.getOtherQuotationRecords().isEmpty()) {
							return parentId+"[OtherQRs: "+qr.getOtherQuotationRecords().size()+"]";
						}
						return parentId;
					}).collect(java.util.stream.Collectors.joining(","));
					builder.append("Assignment: "+assignment.getAssignmentId()+" No of QuotationRecord: "+assignment.getQuotationRecords().size()+" (IDs: "+qrIds+")");
				}
				logger.info("User: "+detail.getUserId()+" SubmitAssignments start "+builder.toString());
			}
			catch(Exception ex){
				logger.info("User: "+detail.getUserId()+" SubmitAssignments start");
				logger.warn("User: "+detail.getUserId()+" SubmitAssignments failed to log: ", ex);
			}
			AssignmentDataReturn ret = mobileService.onlineFunctionAssignment(syncModel.getData());
			List<AssignmentDataReturn> list = new ArrayList<AssignmentDataReturn>();
			list.add(ret);
			model.setData(list);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" SubmitAssignments end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" SubmitAssignments failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("downloadItineraryPlans")
	public @ResponseBody MobileResponseModel<ItineraryPlanOnlineModel> downloadItineraryPlans(Authentication auth, 
			@RequestParam(value = "userId", required=false) Integer userId,
			@RequestParam(value = "date", required=false) Date date){
		MobileResponseModel<ItineraryPlanOnlineModel> model = new MobileResponseModel<ItineraryPlanOnlineModel>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer[] userIds = new Integer[1];
		userIds[0] = userId==null?detail.getUserId():userId;
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync downloadItineraryPlans start");
			List<ItineraryPlanOnlineModel> data = mobileService.downloadItineraryPlan(userIds, date);
			model.setData(data);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync downloadItineraryPlans end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync downloadItineraryPlans failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("downloadQCItineraryPlans")
	public @ResponseBody MobileResponseModel<QCItineraryPlanOnlineModel> downloadQCItineraryPlans(Authentication auth){
		MobileResponseModel<QCItineraryPlanOnlineModel> model = new MobileResponseModel<QCItineraryPlanOnlineModel>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync downloadQCItineraryPlans start");
			List<QCItineraryPlanOnlineModel> data = mobileService.downloarQCItineraryPlan(userId);
			model.setData(data);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync downloadQCItineraryPlans end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync downloadQCItineraryPlans failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}
	
	@RequestMapping("recruitmentFirmStatus")
	public @ResponseBody MobileResponseModel<OutletSyncData> recruitmentFirmStatus(
			@RequestBody SyncModel<OutletSyncData> syncModel
			, Authentication auth, Locale locale){
		MobileResponseModel<OutletSyncData> model = new MobileResponseModel<OutletSyncData>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer submitUserId = detail.getUserId();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync recruitmentFirmStatus start");
			
			List<OutletSyncData> data = outletService.syncOutletData(syncModel.getData(), null, true, new Integer[1]);
			
//			/**
//			 * Send Notification
//			 */
//			User submitUser = userDao.findById(submitUserId);
//			User supervisor = submitUser.getSupervisor();
//			
//			if (supervisor != null){
//				String subject = messageSource.getMessage("N00052", null, locale);
//				notificationService.sendNotification(supervisor, subject, 
//						messageSource.getMessage("N00053", new Object[]{
//								submitUser.getStaffCode(), data.size()
//						}, locale), true);							
//			}
			model.setData(data);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync recruitmentFirmStatus end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync recruitmentFirmStatus failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("submitPECheckForms")
	public @ResponseBody MobileResponseModel<AssignmentDataReturn> submitPECheckForms(@RequestBody SyncModel<PECheckFormOnlineModel> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<AssignmentDataReturn> model = new MobileResponseModel<AssignmentDataReturn>();
		try{		
			logger.info("User: "+detail.getUserId()+" SubmitPECheckForms start");
			AssignmentDataReturn ret = mobileService.onlineFunctionPECheck(syncModel.getData());
			List<AssignmentDataReturn> list = new ArrayList<AssignmentDataReturn>();
			list.add(ret);
			model.setData(list);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" SubmitPECheckForms end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" SubmitPECheckForms failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
	}

	// CR11 - Check Status of Assignment and Quotation Status
	@RequestMapping("checkAssignmentAndQuotationRecordStatus")
	public @ResponseBody MobileResponseModel<CheckAssignmentAndQuotationRecordStatus> checkAssignmentAndQuotationRecordStatus(
			@RequestBody CheckStatusSyncModel syncModel
	// Date lastSyncTime, Integer[] assignmentIds, Integer[] quotationRecordIds
	) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();

		MobileResponseModel<CheckAssignmentAndQuotationRecordStatus> model = new MobileResponseModel<CheckAssignmentAndQuotationRecordStatus>();

		try {
			logger.info("User: " + detail.getUserId() + " Data sync checkAssignmentAndQuotationRecordStatus start");

			List<CheckAssignmentAndQuotationRecordStatus> data = new ArrayList<CheckAssignmentAndQuotationRecordStatus>();

			Integer[] assignmentIds = syncModel.getAssignmentIds();
			Integer[] quotationRecordIds = syncModel.getQuotationRecordIds();
			
			List<Integer> assignmentIdsList = new ArrayList<Integer>();
			
			List<QuotationRecordSyncData> ruaQuotationRecordData = new ArrayList<QuotationRecordSyncData>();
			
			if (assignmentIds != null && assignmentIds.length > 0) {
				for (int i = 0; i < assignmentIds.length; i++) {
					assignmentIdsList.add(assignmentIds[i]);
				}

				ruaQuotationRecordData = quotationRecordService.getRUAQuotationRecordIds(assignmentIdsList);
			}
			
			if (!ruaQuotationRecordData.isEmpty() && ruaQuotationRecordData.size() > 0) {
				CheckAssignmentAndQuotationRecordStatus statusItem = new CheckAssignmentAndQuotationRecordStatus();
				statusItem.setQuotationRecords(ruaQuotationRecordData);
				statusItem.setType("RUA");
				
				data.add(statusItem);		
			}
			
			
			// Quotation Record ID
			
			List<Integer> quotationRecordIdList = new ArrayList<Integer>();
			List<QuotationRecordSyncData> quotationRecordData = new ArrayList<QuotationRecordSyncData>();

			
			if ((quotationRecordIds != null && quotationRecordIds.length > 0) || (ruaQuotationRecordData.size() > 0 && ruaQuotationRecordData != null)) {
				for (int i = 0; i < quotationRecordIds.length; i++) {
					quotationRecordIdList.add(quotationRecordIds[i]);
				}
				
				//Add RUA - QuotationRecord to check list 
				for(QuotationRecordSyncData a : ruaQuotationRecordData){
					if(a.getQuotationRecordId() != null && !a.getQuotationRecordId().equals("")){
						quotationRecordIdList.add(a.getQuotationRecordId());
					}
				}

				quotationRecordData = quotationRecordService.getQuotationRecordIds(quotationRecordIdList);
			}
			if (!quotationRecordData.isEmpty() && quotationRecordData.size() > 0) {
				CheckAssignmentAndQuotationRecordStatus statusItem = new CheckAssignmentAndQuotationRecordStatus();
				statusItem.setQuotationRecords(quotationRecordData);
				statusItem.setType("QuotationRecord");
				
				data.add(statusItem);		
			}

			
			model.setData(data);
			model.setMessage("Online function Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: " + detail.getUserId() + " Data sync checkAssignmentAndQuotationRecordStatus end");
		} catch (Exception ex) {
			logger.error("User: " + detail.getUserId() + " Data sync checkAssignmentAndQuotationRecordStatus failure: ",
					ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
}
