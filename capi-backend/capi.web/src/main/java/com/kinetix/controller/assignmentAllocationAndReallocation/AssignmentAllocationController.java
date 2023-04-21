package com.kinetix.controller.assignmentAllocationAndReallocation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.AllocationBatch;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationFormsModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationInitModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationListModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationSession;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.AdjustmentModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.DistrictHeadRowModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictHeadTabModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchDetailsModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchTabModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AllocationBatchService;
import capi.service.assignmentAllocationAndReallocation.AssignmentAllocationService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;

import com.kinetix.component.FuncCode;

/**
* UF-1502 Assignment Allocation
*/
@Secured("UF1502")
@FuncCode("UF1502")
@Controller("AssignmentAllocationController")
@RequestMapping("assignmentAllocationAndReallocation/AssignmentAllocation")
@SessionAttributes({"assignmentAllocationSession"})
public class AssignmentAllocationController {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentAllocationController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	AllocationBatchService allocationBatchService;
	
	@Autowired
	AssignmentAllocationService service;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SurveyMonthService surveyMonthService;
	
	/**
	 * Init model attribute to session
	 */
	@ModelAttribute("assignmentAllocationSession")
    public AssignmentAllocationSession initAssignmentAllocationSession() {
        return new AssignmentAllocationSession();
    }
	
	/**
	 * SurveyMonth home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model,
			Locale locale,
			SessionStatus sessionStatus) {
		try {
			
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<AssignmentAllocationListModel>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.querySurveyMonth(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Assignment edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession,
			@RequestParam(value = "id", required = false) Integer id ) {
		try {
			sessionStatus.setComplete();
			assignmentAllocationSession = new AssignmentAllocationSession();
			
			model.addAttribute("id", id);
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}
	
	@RequestMapping(value = "initSession", method = RequestMethod.POST)
	public @ResponseBody AssignmentAllocationInitModel initSession(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession,
			@RequestParam(value = "id", required = false) Integer id ) {
		AssignmentAllocationInitModel initModel = new AssignmentAllocationInitModel();
		try {
			assignmentAllocationSession = new AssignmentAllocationSession();
			if(id != null){
				assignmentAllocationSession = new AssignmentAllocationSession(); //this.service.bindDataFromDb(surveyMonthSession, id, initModel);
			}
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return initModel;
	}
	
	
	/**
	 * Assignment get tab view
	 */
	@RequestMapping(value = "getTabItem", method = RequestMethod.POST)
	public String getTabItem(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession,
			@RequestParam(value = "index", required = true) Integer index ){
		try {
			switch(index){
				default:
				case 1:
					return this.allocationBatchTab(model, assignmentAllocationSession);
				case 2:
					return this.districtHeadTab(model, assignmentAllocationSession);
				case 3:
					return this.adjustmentAssignmentTab(model, assignmentAllocationSession);
			}
			
		} catch (Exception e) {
			logger.error("getTabItem", e);
		}
		return this.allocationBatchTab(model, assignmentAllocationSession);
	}
	
	private String allocationBatchTab(Model model, AssignmentAllocationSession assignmentAllocationSession){
		AllocationBatchTabModel displayModel = new AllocationBatchTabModel();
		List<AllocationBatchModel> abmList = new ArrayList<AllocationBatchModel>();
		
		if(assignmentAllocationSession.getSessionAllocationBatchTabModel() != null){
			try{
				displayModel.setAllocationBatchId(assignmentAllocationSession.getSessionAllocationBatchTabModel().getAllocationBatchId());
				displayModel.setReferenceMonthStr(assignmentAllocationSession.getSessionAllocationBatchTabModel().getReferenceMonthStr());
				Date refMonth = commonService.getDate("01-"+displayModel.getReferenceMonthStr());
				
				SurveyMonth sm = this.surveyMonthService.getSurveyMonthByReferenceMonth(refMonth);
				List<AllocationBatch> abList = this.allocationBatchService.getUnassignedAllocationBatchBySurveyMonth(sm);
				for(AllocationBatch ab : abList){
					AllocationBatchModel abm = new AllocationBatchModel();
					abm.setAllocationBatchId(ab.getId());
					abm.setBatchName(ab.getBatchName());
					abmList.add(abm);
				}
			}catch(Exception e){
				
			}
		}
		
		model.addAttribute("displayModel", displayModel);
		model.addAttribute("abmList", abmList);
		
		
		return "assignmentAllocationAndReallocation/AssignmentAllocation/partial/allocationBatchTab";
	}
	
	private String districtHeadTab(Model model, AssignmentAllocationSession assignmentAllocationSession){
		
		List<DistrictModel> districtList = this.service.getDistrictsAsModel();
		

		List<User> officerList = this.userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
		
		for(DistrictModel displayModel :districtList ){
			for(DistrictHeadTabModel sessionModel : assignmentAllocationSession.getSessionDistrictHeadTabModel() ){
				if(displayModel.getDistrictId().intValue() == sessionModel.getDistrictId().intValue()){
					displayModel.setUserId(sessionModel.getUserId());
				}
			}
		}
		
		model.addAttribute("districtList", districtList);
		
		model.addAttribute("officerList", officerList);
		
		return "assignmentAllocationAndReallocation/AssignmentAllocation/partial/districtHeadTab";
	}
	
	private String adjustmentAssignmentTab(Model model, AssignmentAllocationSession assignmentAllocationSession){
		
		List<DistrictHeadRowModel> districtHeadList = assignmentAllocationSession.getSessionDistrictHeadRows(); 
		if(districtHeadList == null){
			districtHeadList = this.service.getDistrictHeadRows(assignmentAllocationSession.getSessionDistrictHeadTabModel()
					, assignmentAllocationSession.getSessionAllocationBatchTabModel().getAllocationBatchId());
		}
		
		List<AdjustmentModel> adjustmentModelList = assignmentAllocationSession.getSessionAdjustmentModels();
		if(adjustmentModelList == null){
			adjustmentModelList = new ArrayList<AdjustmentModel>();
		}
		
		List<User> officerList = this.userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
		
		model.addAttribute("districtHeadList", districtHeadList);
		
		model.addAttribute("adjustmentModelList", adjustmentModelList);
		
		model.addAttribute("officerList", officerList);
		
		return "assignmentAllocationAndReallocation/AssignmentAllocation/partial/adjustmentAssignmentTab";
	}
	
	@RequestMapping(value="getAllocationBatch", method = RequestMethod.POST)
	public @ResponseBody List<AllocationBatchModel> getAllocationBatch(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession,
			@RequestParam(value = "monthStr", required = true) String referenceMonthStr ){
		
		List<AllocationBatchModel> abmList = new ArrayList<AllocationBatchModel>();
		
		Date refMonth;
		try {
			if(referenceMonthStr.length() > 0){
				refMonth = commonService.getMonth(referenceMonthStr);
				SurveyMonth sm = this.surveyMonthService.getSurveyMonthByReferenceMonth(refMonth);
				if (sm == null){
					return abmList;
				}
				
				if (sm.getStatus() != null && sm.getStatus() != 5){
					return abmList;
				}
				
				if(sm != null){
					List<AllocationBatch> abList = this.allocationBatchService.getUnassignedAllocationBatchBySurveyMonth(sm);
					for(AllocationBatch ab : abList){
						AllocationBatchModel abm = new AllocationBatchModel();
						abm.setAllocationBatchId(ab.getId());
						abm.setBatchName(ab.getBatchName());
						abmList.add(abm);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("getAllocationBatch", e);
		}
		return abmList;
		
	}
	
	@RequestMapping(value="getAllocationBatchDetails", method = RequestMethod.POST)
	public String getAllocationBatchDetails(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession,
			@RequestParam(value = "allocationBatchId", required = true) Integer allocationBatchId,
			@RequestParam(value = "monthStr", required = true) String referenceMonthStr){
		
		try {
			AllocationBatchDetailsModel abdm = this.service.getAllocationBatchDetails(allocationBatchId, referenceMonthStr);
			
			model.addAttribute("displayModel", abdm);
			
			return "assignmentAllocationAndReallocation/AssignmentAllocation/partial/allocationBatchDetails";
		} catch (Exception e) {
			logger.error("getAllocationBatchDetails", e);
		}
		return "";
	}	

	@RequestMapping(value="submitTabForm", method = RequestMethod.POST)
	public @ResponseBody String submitTabForm(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute AssignmentAllocationFormsModel formModel,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession,
			@RequestParam(value = "index", required = true) Integer index,
			@RequestParam(value = "completeTab", required = true) Boolean completeTab
			) throws ParseException{
		
		switch(index){
			case 1:
				if(assignmentAllocationSession.getReadonly()){
					return "0";
				}
				return this.updateTabSurveyMonth(formModel.getAllocationBatchTabModel(), assignmentAllocationSession).toString();
			case 2:
				if(assignmentAllocationSession.getReadonly()){
					return "0";
				}
				return this.updateTabDistrictHead(formModel.getDistrictHeadTabModel(), assignmentAllocationSession).toString();
			case 3:
				return this.updateTabAssignmentAllocation(formModel.getAdjustment(), formModel.getDistrictHead(), assignmentAllocationSession);
		}
		return "1";
	
	}
	
	private Integer updateTabSurveyMonth(AllocationBatchTabModel formModel,
			AssignmentAllocationSession assignmentAllocationSession){
		
		/*
		AllocationBatch ab = this.allocationBatchService.getAllocationBatchById(formModel.getAllocationBatchId());
		formModel.setAb(ab);*/
		
		assignmentAllocationSession.setSessionAllocationBatchTabModel(formModel);
		
		return 0;
	}
	
	private Integer updateTabDistrictHead(List<DistrictHeadTabModel> formModelList, 
			AssignmentAllocationSession assignmentAllocationSession){
			assignmentAllocationSession.setSessionDistrictHeadTabModel(new ArrayList<DistrictHeadTabModel>());
			for(DistrictHeadTabModel formModel : formModelList){
				if(formModel.getDistrictId() != null && formModel.getUserId() == null){
					return 10;
				}
				if(formModel.getDistrictId() != null && formModel.getUserId() != null){
					assignmentAllocationSession.getSessionDistrictHeadTabModel().add(formModel);
				}
			}

			assignmentAllocationSession.setSessionDistrictHeadRows(null);
			assignmentAllocationSession.setSessionAdjustmentModels(null);
		
		return 0;
	}
	
	private String updateTabAssignmentAllocation(List<AdjustmentModel> formAdjs,
			List<DistrictHeadRowModel> formDHs,
			AssignmentAllocationSession assignmentAllocationSession){
		
		assignmentAllocationSession.setSessionDistrictHeadRows(new ArrayList<DistrictHeadRowModel>());
		assignmentAllocationSession.setSessionAdjustmentModels(new ArrayList<AdjustmentModel>());
		
		int errorcode = 0;
		String errorMessages = "";
		
		Double sumOfManDayOfBalance = 0.0 ;
		// Total transfer-out man day > Man day required for responsible district
		for(DistrictHeadRowModel dhm : formDHs) {
			if(dhm.getManDayOfBalance() == null) {
				continue;
			}
			if (dhm.getManDayOfTransferInOut() > dhm.getAdjustedManDayRequiredForResponsibleDistricts()) {
				errorcode = 150;
			}
			sumOfManDayOfBalance += dhm.getManDayOfBalance() != null ? dhm.getManDayOfBalance() : 0.0 ;
		}
		if (errorcode==150){
			errorMessages += messageSource.getMessage("E00150", null, Locale.ENGLISH) + " <br> ";
		}
		
		if(formAdjs != null){
			for(AdjustmentModel am : formAdjs){
				if(am.getFromUserId() != null){
					
					assignmentAllocationSession.getSessionAdjustmentModels().add(am);
				}
			}
		}
		for(DistrictHeadRowModel dhm : formDHs){
			if(dhm.getUserId() == null){
				continue;
			}
			assignmentAllocationSession.getSessionDistrictHeadRows().add(dhm);
			if(dhm.getManDayOfBalance() >= 0){
				
			}else{
				errorcode = 7;
			}
		}
		if (errorcode==7){
			errorMessages += messageSource.getMessage("E00007", null, Locale.ENGLISH) + " <br> ";
		}
		
		//The average of "Man-day Balance" (mean) should be within the range (mean - 0.5, mean + 0.5)
		int countOfDistrictHead = (assignmentAllocationSession.getSessionDistrictHeadRows() != null) ? assignmentAllocationSession.getSessionDistrictHeadRows().size() : 0;		

		if( countOfDistrictHead > 0 ){
			double mean = sumOfManDayOfBalance/countOfDistrictHead;
			double lowerBound = mean - 0.5;
			double upperBound = mean + 0.5;
			
			for(DistrictHeadRowModel dhm : formDHs) {
				if(dhm.getManDayOfBalance() != null && (dhm.getManDayOfBalance() < lowerBound || dhm.getManDayOfBalance() > upperBound)) {
					errorcode = 157;
				}
			}
		}
		if (errorcode==157){
			errorMessages += messageSource.getMessage("E00157", null, Locale.ENGLISH) + " <br> ";
		}
		
		return (errorcode > 1) ? "2" + "|" + errorMessages : "0" + "|" + errorMessages;
	}
	
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 8) or hasPermission(#user, 256)")
	@RequestMapping(value="saveSession", method = RequestMethod.GET)
	public String saveSession(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			@ModelAttribute("assignmentAllocationSession") AssignmentAllocationSession assignmentAllocationSession){
		
		try{
			this.service.saveAssignmentAdjustments(assignmentAllocationSession);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}
		catch (Exception ex){
			logger.error("Assignment adjustment failure", ex);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
				
		return "redirect:/assignmentAllocationAndReallocation/AssignmentAllocation/home";
	}
	
	@RequestMapping(value="view", method = RequestMethod.GET)
	public void view(Model model,
			Locale locale,
			@RequestParam(value = "id", required = false) Integer id){
		
		List<AdjustmentModel> adjustmentModelList = this.service.getAssignmentAdjustment(id);
		List<DistrictHeadRowModel> districtHeadList = this.service.getDistrictHeadAdjustment(id);
		
		model.addAttribute("adjustmentModelList", adjustmentModelList);
		model.addAttribute("districtHeadList", districtHeadList);
	
	}
	
	/**
	 * Delete assignment allocation 
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(Integer id, Model model, Locale locale) {
		try {
			if (!service.removeAlloactedAssignmentRecord(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}		
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("delete", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		return "/partial/messageRibbons";
	}	
	
	/**
	 * Approve assignment allocation 
	 */
	@PreAuthorize("hasPermission(#user, 2)")
	@RequestMapping(value = "approveAssignmentAlllocation", method = RequestMethod.POST)
	public String approveAssignmentAllocation(Integer id, Model model, Locale locale) {
		try {
			if (!service.approveAssignmentAllocation(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}		
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("approveAssignmentAlllocation", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		return "/partial/messageRibbons";
	}	
	
}
