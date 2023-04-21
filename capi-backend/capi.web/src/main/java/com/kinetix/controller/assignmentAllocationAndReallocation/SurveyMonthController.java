package com.kinetix.controller.assignmentAllocationAndReallocation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.batch.AssignmentGenerationTask;
import com.kinetix.component.FuncCode;

import capi.entity.Batch;
import capi.entity.ClosingDate;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffNameModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BackTrackDateDisplayModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchCategoryModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchQuotationActiveModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.IndexingModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthDatesModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthFormModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthListModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthSession;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.collectionDateChecking.BatchCodeModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.collectionDateChecking.CategoryModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDate;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDateFormModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
* UF-1501 Survey Month Maintenance
*/
@Secured("UF1501")
@FuncCode("UF1501")
@Controller("SurveyMonthController")
@RequestMapping("assignmentAllocationAndReallocation/SurveyMonth")
@SessionAttributes({"surveyMonthSession"})
public class SurveyMonthController {
	private static final Logger logger = LoggerFactory.getLogger(SurveyMonthController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private CommonService commonService;
	
	@Autowired 
	private SurveyMonthService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AssignmentGenerationTask generationTask;
	
	@InitBinder
	public void initListBinder(WebDataBinder binder) {
	    binder.setAutoGrowCollectionLimit(100000);
	}
	
	/**
	 * Init model attribute to session
	 */
	@ModelAttribute("surveyMonthSession")
    public SurveyMonthSession initSurveyMonthSession() {
        return new SurveyMonthSession();
    }

	/**
	 * SurveyMonth home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth) {
		try {
			Boolean isFreezedSurveyMonth = this.service.isFreezedSurveyMonth();
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			if(detail.getAuthorityLevel() == 8 || detail.getAuthorityLevel() == 256)
				isFreezedSurveyMonth = true;
			
			model.addAttribute("isFreezedSurveyMonth", isFreezedSurveyMonth);
			
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<SurveyMonthListModel>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.querySurveyMonth(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
//	/**
//	 * SurveyMonth edit
//	 */
//	@RequestMapping(value = "edit", method = RequestMethod.GET)
//	public void edit(Model model,
//			Locale locale,
//			SessionStatus sessionStatus,
//			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
//			@RequestParam(value = "id", required = false) Integer id ) {
//		try {
//			sessionStatus.setComplete();
//			surveyMonthSession = new SurveyMonthSession();
//			
//			Boolean readonly = this.service.checkReadonly(id);
//			model.addAttribute("readonly", readonly);
//			
//			List<User> officerList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
//			model.addAttribute("officerList", officerList);
//			
//			model.addAttribute("id", id);
//		} catch (Exception e) {
//			logger.error("edit", e);
//		}
//	}
	
	/**
	 * SurveyMonth edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(Model model,
			Locale locale,
			RedirectAttributes redirectAttributes,
			SessionStatus sessionStatus,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
			@RequestParam(value = "id", required = false) Integer id ) {
		try {
			sessionStatus.setComplete();
			surveyMonthSession = new SurveyMonthSession();

			if (id != null){
				if (service.getSurveyMonthById(id) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentAllocationAndReallocation/SurveyMonth/home";
				}
			}
			
			Boolean readonly = this.service.checkReadonly(id);
			Boolean isDraft = this.service.checkIsDraft(id);
			Boolean isCreated = this.service.checkIsCreated(id);
			List<User> officerList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
			
			model.addAttribute("readonly", readonly);
			model.addAttribute("officerList", officerList);
			model.addAttribute("isDraft", isDraft);
			model.addAttribute("isCreated", isCreated);
			model.addAttribute("id", id);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * SurveyMonth delete
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 8) or hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public String delete(Model model,
			Locale locale,
			@RequestParam(value = "id", required = true) Integer id,
			RedirectAttributes redirectAttributes) {
		try {
			this.service.deleteSurveyMonth(id);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("edit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			//return 0;
		}
		return "redirect:/assignmentAllocationAndReallocation/SurveyMonth/home";
	}
	
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 8) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveSession", method = RequestMethod.GET)
	public String saveSession(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "isDraft", required = false) Boolean isDraft,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession){
		
		try{		
			Integer surveyMonthId = this.service.generateDraftSurveyMonth(surveyMonthSession, true);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch(Exception e){
			logger.error("save survey month", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/assignmentAllocationAndReallocation/SurveyMonth/home";
	}
	
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 8) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveSurveyMonth", method = RequestMethod.POST)
	public @ResponseBody Boolean saveSurveyMonth(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			Authentication auth,
			RedirectAttributes redirectAttributes,
			SurveyMonthFormModel formModel,
			@RequestParam(value = "tabIndex", required = true) Integer tabIndex,
			@RequestParam(value = "completeTab", required = true) Boolean completeTab,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession){
		
		Boolean isSuccess = false;
		try{
			submitTabForm(model, locale, sessionStatus, formModel, surveyMonthSession, tabIndex, true);
			Integer id = this.service.generateDraftSurveyMonth(surveyMonthSession, false);
			isSuccess = id != null && id > 0;
			if(isSuccess){
				surveyMonthSession.getSessionSurveyMonth().setId(id);
			}
		}catch(Exception e){
			logger.error("saveSurveyMonth", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return isSuccess;
	}
	
	@RequestMapping(value = "initSession", method = RequestMethod.POST)
	public @ResponseBody IndexingModel initSession(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
			@RequestParam(value = "id", required = false) Integer id,
			Authentication auth) {
		IndexingModel indexingModel = new IndexingModel();
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();	
			if(id != null) {
				surveyMonthSession = this.service.prepareViewModel(surveyMonthSession, id, indexingModel, detail.getAuthorityLevel());
			}
		} catch (Exception e) {
			logger.error("initSession", e);
		}
		return indexingModel;
	}
	/**
	 * SurveyMonth get tab view
	 */
	@RequestMapping(value = "getTabItem", method = RequestMethod.POST)
	public String getTabItem(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
			@RequestParam(value = "index", required = true) Integer index ){
		try {
			switch(index){
				default:
				case 1:
					return this.surveyMonthTab(model, surveyMonthSession);
				case 2:
					return this.allocationBatch(model, surveyMonthSession);
				case 3:
					return this.assignmentAttributes(model, surveyMonthSession);
				case 4:
					return this.backTrackDate(model, surveyMonthSession);
			}
			
		} catch (Exception e) {
			logger.error("getTabItem", e);
		}
		return this.surveyMonthTab(model, surveyMonthSession);
	}
	
	private String surveyMonthTab(Model model, SurveyMonthSession surveyMonthSession){
		model.addAttribute("model", surveyMonthSession.getSessionSurveyMonth());
		Date restrictionDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(restrictionDate);
		c.add(Calendar.DATE, 1);
		restrictionDate = c.getTime();
		String currentDateStr = this.commonService.formatDate(restrictionDate);
		model.addAttribute("currentDateStr", currentDateStr);
		model.addAttribute("readonly", surveyMonthSession.getReadonly());
		model.addAttribute("isDraft", surveyMonthSession.getIsDraft());
		return "assignmentAllocationAndReallocation/SurveyMonth/partial/surveyMonth";
	}
	
	private String allocationBatch(Model model, SurveyMonthSession surveyMonthSession){
		model.addAttribute("allocationBatchList", surveyMonthSession.getSessionAllocationBatch());
		model.addAttribute("newAllocationBatchList", surveyMonthSession.getSessionNewAllocationBatch());
		model.addAttribute("surveyMonth", surveyMonthSession.getSessionSurveyMonth() );
		model.addAttribute("readonly", surveyMonthSession.getReadonly());
		return "assignmentAllocationAndReallocation/SurveyMonth/partial/allocationBatch";
	}
		
	private String assignmentAttributes(Model model, SurveyMonthSession surveyMonthSession){
		int surveyMonthId = (surveyMonthSession.getSessionSurveyMonth().getId() != null) ? surveyMonthSession.getSessionSurveyMonth().getId() : 0;
		List<BatchCategoryModel> batchCategoryList = service.getBatchWithCategory(surveyMonthId);
		List<User> officerList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
		
		//gen model list for Uncategorized AA.
		if(surveyMonthSession.getSessionNonCateAssignmentAttr() == null || surveyMonthSession.getSessionNonCateAssignmentAttr().size() == 0){
			List<BatchModel> uncategorizedList = new ArrayList<BatchModel>();
			for(BatchCategoryModel bcm : batchCategoryList){
				if(bcm.getBatchCategoryName().length() == 0){
					uncategorizedList = bcm.getBatchList();
				}
			}
			List<AssignmentAttributes> nonCateAssignmentAttrList = new ArrayList<AssignmentAttributes>();
			int index = 0;
			for(BatchModel bm : uncategorizedList){
				AssignmentAttributes aa = new AssignmentAttributes();
				aa.setReferenceId(index+"");
				aa.setBatchId(bm.getBatchId());
				aa.setSelectedBatchType(bm.getAssignmentType());
				nonCateAssignmentAttrList.add(aa);
				index++;
			}
			surveyMonthSession.setSessionNonCateAssignmentAttr(nonCateAssignmentAttrList);
		}
		//end gen model
		
		if (surveyMonthSession.getSessionNewAssignmentAttr() != null) {
			for (AssignmentAttributes attr : surveyMonthSession.getSessionNewAssignmentAttr()) {
				logger.debug(attr.getBatchId() + ":" + attr.getSelectedBatchType());
			}
		}
		
		// check whether all assignment type is same or not
		for(BatchCategoryModel batchCategory : batchCategoryList) {
			boolean allAssignmentTypeOne = false;
			int countAssignmentTypeOne = 0;
			
			boolean allAssignmentTypeNotOne = false;
			int countAssignmentTypeNotOne = 0;
			
			for(BatchModel batch : batchCategory.getBatchList()) {
				
				if(batch.getAssignmentType() == 1) {
					countAssignmentTypeOne++;
				} else if(batch.getAssignmentType() != 1) {
					countAssignmentTypeNotOne++;
				}
			}
			
			if(countAssignmentTypeOne == batchCategory.getBatchList().size()) {
				allAssignmentTypeOne = true;
			}
			if(countAssignmentTypeNotOne == batchCategory.getBatchList().size()) {
				allAssignmentTypeNotOne = true;
			}
			
			batchCategory.setAllAssignmentTypeOne(allAssignmentTypeOne);
			batchCategory.setAllAssignmentTypeNotOne(allAssignmentTypeNotOne);
		}
		
		model.addAttribute("batchCategoryList", batchCategoryList);
		model.addAttribute("surveyMonth", surveyMonthSession.getSessionSurveyMonth() );
		model.addAttribute("allocationBatchList", surveyMonthSession.getSessionAllocationBatch());
		model.addAttribute("newAllocationBatchList", surveyMonthSession.getSessionNewAllocationBatch());
		model.addAttribute("officerList", officerList);
		model.addAttribute("newAssignmentAllocationList", surveyMonthSession.getSessionNewAssignmentAttr());
		model.addAttribute("nonCateAssignmentAttr", surveyMonthSession.getSessionNonCateAssignmentAttr());
		model.addAttribute("readonly", surveyMonthSession.getReadonly());
		model.addAttribute("isDraft", surveyMonthSession.getIsDraft());
		
		Date today = new Date();
		String todayStr = commonService.formatDate(today);
		model.addAttribute("todayStr", todayStr);
		
		return "assignmentAllocationAndReallocation/SurveyMonth/partial/assignmentsAttributes";
	}
	
	private String backTrackDate(Model model, SurveyMonthSession surveyMonthSession){
		List<BackTrackDateDisplayModel> displayList = new ArrayList<BackTrackDateDisplayModel>();
		if(surveyMonthSession.getBackTrackDateModelList() != null){
			displayList = surveyMonthSession.getBackTrackDateModelList();
		}
		if(surveyMonthSession.getSessionNewAssignmentAttr() != null){
			for(AssignmentAttributes newAA : surveyMonthSession.getSessionNewAssignmentAttr()){
				if(newAA.getCollectionDateList() != null && newAA.getCollectionDateList().size() > 0){
					for(BackTrackDateDisplayModel btddm : newAA.getCollectionDateList()){
						Boolean uniqueBackTrackDateDisplayModel = true;
						for(BackTrackDateDisplayModel existbtdd : displayList){
							if(btddm.getBatchCode().equalsIgnoreCase(existbtdd.getBatchCode())){
								uniqueBackTrackDateDisplayModel = false;
								
								for(BackTrackDate formBtd : btddm.getBackTrackDayList()){
									Boolean uniqueBackTrackDate = true;
									for(BackTrackDate existBtd : existbtdd.getBackTrackDayList()){
										if(existBtd.getReferenceCollectionDateStr().equals(formBtd.getReferenceCollectionDateStr())){
											uniqueBackTrackDate = false;
										}
									}
									if(uniqueBackTrackDate){
										existbtdd.getBackTrackDayList().add(formBtd);
									}
								}
							}
						}
						if(uniqueBackTrackDateDisplayModel){
							displayList.add(btddm);
						}
					}
					
					//displayList.addAll(newAA.getCollectionDateList());
				}
			}
			
			List<BackTrackDateDisplayModel> delItems = new ArrayList<BackTrackDateDisplayModel>();
			Set<String> batchCodeSet = new HashSet<>();
			for(BackTrackDateDisplayModel backTrackDateDisplayModel : displayList){
				if(batchCodeSet.contains(backTrackDateDisplayModel.getBatchCode())){
					delItems.add(backTrackDateDisplayModel);
				} else {
					batchCodeSet.add(backTrackDateDisplayModel.getBatchCode());
				}
			}
			displayList.removeAll(delItems);
		}
		if(surveyMonthSession.getSessionNonCateAssignmentAttr() != null){
			for(AssignmentAttributes uncateAA : surveyMonthSession.getSessionNonCateAssignmentAttr()){
				if(uncateAA.getCollectionDateList() != null && uncateAA.getCollectionDateList().size() > 0){
					for(BackTrackDateDisplayModel btddm : uncateAA.getCollectionDateList()){
						Boolean uniqueBackTrackDateDisplayModel = true;
						for(BackTrackDateDisplayModel existbtdd : displayList){
							if(btddm.getBatchCode().equalsIgnoreCase(existbtdd.getBatchCode())){
								uniqueBackTrackDateDisplayModel = false;
								
								for(BackTrackDate formBtd : btddm.getBackTrackDayList()){
									Boolean uniqueBackTrackDate = true;
									for(BackTrackDate existBtd : existbtdd.getBackTrackDayList()){
										if(existBtd.getReferenceCollectionDateStr().compareTo(formBtd.getReferenceCollectionDateStr()) == 0){
											uniqueBackTrackDate = false;
										}
									}
									if(uniqueBackTrackDate){
										existbtdd.getBackTrackDayList().add(formBtd);
									}
								}
							}
						}
						if(uniqueBackTrackDateDisplayModel){
							displayList.add(btddm);
						}
					}
					//displayList.addAll(uncateAA.getCollectionDateList());
				}
			}
		}
		for(BackTrackDateDisplayModel displayModelA : displayList){
			for(BackTrackDateDisplayModel displayModelB : displayList){
				if(!displayModelA.equals(displayModelB) && displayModelA.getBatchCode().equalsIgnoreCase(displayModelB.getBatchCode())){
					displayModelA.getBackTrackDayList().addAll(displayModelB.getBackTrackDayList());
					displayModelB.setBackTrackDayList(new ArrayList<BackTrackDate>());
				}
			}
		}
		Integer RefId = 0;
		for(BackTrackDateDisplayModel displayModelA : displayList){
			Collections.sort(displayModelA.getBackTrackDayList(), new Comparator<BackTrackDate>(){
				@Override
				public int compare(BackTrackDate o1, BackTrackDate o2) {
					return o1.getReferenceCollectionDate().compareTo(o2.getReferenceCollectionDate());
				}
			});
			for(BackTrackDate btddm : displayModelA.getBackTrackDayList()){
				btddm.setReferenceId(RefId);
				RefId++;
			}
		}
		Collections.sort(displayList, new Comparator<BackTrackDateDisplayModel>(){
			@Override
			public int compare(BackTrackDateDisplayModel o1, BackTrackDateDisplayModel o2) {
				return o1.getBatchCode().compareTo(o2.getBatchCode());
			}
		});
		
		List<BackTrackDateDisplayModel> perviousDisplayList = surveyMonthSession.getBackTrackDateModelList();
		if(!perviousDisplayList.isEmpty()){
			for(BackTrackDateDisplayModel periousDisplayModel : perviousDisplayList){
				for(BackTrackDateDisplayModel currentDisplayModel : displayList){
					if(periousDisplayModel.getBatchCode().equalsIgnoreCase(currentDisplayModel.getBatchCode())){
						for(BackTrackDate periousBTD : periousDisplayModel.getBackTrackDayList()){
							for(BackTrackDate currentBTD : currentDisplayModel.getBackTrackDayList()){
								if(periousBTD.getReferenceCollectionDate().compareTo(currentBTD.getReferenceCollectionDate()) == 0){
									currentBTD.setBackTrackDateList(periousBTD.getBackTrackDateList());
									currentBTD.setBackTrackDateString(periousBTD.getBackTrackDateString());
									
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(periousBTD.getReferenceCollectionDate());
									Date prevDate = DateUtils.addDays(periousBTD.getReferenceCollectionDate(), -1);
									int currentRefMonth = calendar.get(Calendar.MONTH);
									calendar.setTime(prevDate);
									int prevDateRefMonth = calendar.get(calendar.MONTH);
									if(currentRefMonth == prevDateRefMonth){
										currentBTD.setPrevBackTrackDateString(commonService.formatDate(prevDate));										
									}
									currentBTD.setHasBackTrack(currentBTD.getBackTrackDateList()!=null && !currentBTD.getBackTrackDateList().isEmpty());
								}
							}
						}
					}
				}
			}
		}
		
		surveyMonthSession.setBackTrackDateModelList(displayList);
		model.addAttribute("displayList", displayList);
		model.addAttribute("readonly", surveyMonthSession.getReadonly());
		return "assignmentAllocationAndReallocation/SurveyMonth/partial/backTrackDate";
	}

	@RequestMapping(value="getClosingDate", method = RequestMethod.POST)
	public @ResponseBody SurveyMonthDatesModel getClosingDate(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
			@RequestParam(value = "monthStr", required = true) String monthStr ){
		SurveyMonthDatesModel returnModel = new SurveyMonthDatesModel();
		if(monthStr != ""){
			monthStr = "01-"+  monthStr;
			try {
				Date selectedMonth = commonService.getDate(monthStr);
				
				SurveyMonth sm = this.service.getSurveyMonthByReferenceMonth(selectedMonth);
				
				if(sm == null){
				
					ClosingDate closingDate = this.service.getClosingDateByReferenceMonth(selectedMonth);
					
					returnModel.setStartDate(monthStr);
	
					Calendar c = Calendar.getInstance();
					c.setTime(selectedMonth);
					c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
					
					returnModel.setEndDate(commonService.formatDate(c.getTime()));
					
					if(closingDate != null){
						returnModel.setClosingDate(commonService.formatDate(closingDate.getClosingDate()));
					
						surveyMonthSession.getSessionSurveyMonth().setClosingDate(closingDate.getClosingDate());
						surveyMonthSession.getSessionSurveyMonth().setStartDate(selectedMonth);
						surveyMonthSession.getSessionSurveyMonth().setEndDate(c.getTime());
					}else{
						surveyMonthSession.getSessionSurveyMonth().setClosingDate(null);
						surveyMonthSession.getSessionSurveyMonth().setStartDate(null);
						surveyMonthSession.getSessionSurveyMonth().setEndDate(null);
					}
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return returnModel;
			}
		}
		return returnModel;
		
	}
	
	@RequestMapping(value="submitTabForm", method = RequestMethod.POST)
	public @ResponseBody Integer submitTabForm(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			SurveyMonthFormModel formModel,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
			@RequestParam(value = "index", required = true) Integer index,
			@RequestParam(value = "completeTab", required = true) Boolean completeTab
			){
 		switch(index){
			case 1:
				try {
					if(surveyMonthSession.getReadonly()){
						return 0;
					}
					return this.updateSessionSurveyMonthTab(formModel, surveyMonthSession);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
			case 2:
				try {
					if(surveyMonthSession.getReadonly()){
						return 0;
					}
					return this.updateSessionAllocationBatchTab(formModel, surveyMonthSession);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
			case 3:
				try{
					if(surveyMonthSession.getReadonly()){
						return 0;
					}
					return this.updateSessionAssignmentAttrTab(formModel, surveyMonthSession, completeTab);
				}catch(ParseException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
			case 4:
				try{
					if(surveyMonthSession.getReadonly()){
						return 0;
					}
					return this.updateSessionBackTrackDateTab(formModel, surveyMonthSession);
				}catch(Exception e){
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
		}
		return 1;
	
	}
	
	private int updateSessionSurveyMonthTab( SurveyMonthFormModel formModel, SurveyMonthSession surveyMonthSession) throws ParseException{
		
		Date selectedMonth = commonService.getDate("01-"+formModel.getSurveyMonth().getReferenceMonthStr());
		ClosingDate closingDate = this.service.getClosingDateByReferenceMonth(selectedMonth);
		Date startDate = commonService.getDate(formModel.getSurveyMonth().getStartDateStr());
		Date endDate = commonService.getDate(formModel.getSurveyMonth().getEndDateStr());
		
		surveyMonthSession.getSessionSurveyMonth().setClosingDate(closingDate.getClosingDate());
		surveyMonthSession.getSessionSurveyMonth().setStartDate(startDate);
		surveyMonthSession.getSessionSurveyMonth().setEndDate(endDate);
		surveyMonthSession.getSessionSurveyMonth().setReferenceMonth(selectedMonth);
		surveyMonthSession.getSessionSurveyMonth().setEndDateStr(formModel.getSurveyMonth().getEndDateStr());
		surveyMonthSession.getSessionSurveyMonth().setStartDateStr(formModel.getSurveyMonth().getStartDateStr());
		surveyMonthSession.getSessionSurveyMonth().setClosingDateStr(formModel.getSurveyMonth().getClosingDateStr());
		surveyMonthSession.getSessionSurveyMonth().setReferenceMonthStr(formModel.getSurveyMonth().getReferenceMonthStr());
		
		return 0;
	}
	
	private int updateSessionAllocationBatchTab( SurveyMonthFormModel formModel, SurveyMonthSession surveyMonthSession) throws ParseException{
		surveyMonthSession.setSessionAllocationBatch(formModel.getAllocationBatch());
		surveyMonthSession.setSessionNewAllocationBatch(formModel.getNewAllocationBatch());
		List<AllocationBatch> skipList = new ArrayList<AllocationBatch>();
		for(AllocationBatch newEntry : surveyMonthSession.getSessionNewAllocationBatch()){
			if(newEntry.getId() == null){
				skipList.add(newEntry);
				continue;
			}
			if(newEntry.getStartDateStr().length() == 0 && newEntry.getEndDateStr().length() == 0 && newEntry.getNumberOfBatch().length() == 0){
				newEntry.setId(null);
			}else{
				if(newEntry.getStartDateStr().length() == 0){
					newEntry.setStartDate(null);
				}else{
					newEntry.setStartDate( commonService.getDate(newEntry.getStartDateStr()));
				}
				
				if(newEntry.getEndDateStr().length() == 0){
					newEntry.setEndDate(null);
				}else{
					newEntry.setEndDate( commonService.getDate(newEntry.getEndDateStr()));
				}
			}
		}
		
		surveyMonthSession.getSessionNewAllocationBatch().removeAll(skipList);
		
		List<AllocationBatch> diffElemList = surveyMonthSession.getSessionNewAllocationBatch();
		for(AllocationBatch workingElem : surveyMonthSession.getSessionNewAllocationBatch()){
			for(AllocationBatch diffElem : diffElemList){
				if(workingElem.equals(diffElem)){
					continue;
				}
				if( (workingElem.getStartDate().before(diffElem.getEndDate()) || workingElem.getStartDate().equals(diffElem.getEndDate()) )
					&& (workingElem.getEndDate().after(diffElem.getStartDate()) || workingElem.getEndDate().equals(diffElem.getStartDate()) ) 
						){
					return 15;
				}
			}
		}
		
		return 0;
	}
	
	private int updateSessionAssignmentAttrTab( SurveyMonthFormModel formModel, SurveyMonthSession surveyMonthSession, Boolean completeTab) throws ParseException{
		surveyMonthSession.setSessionNewAssignmentAttr(formModel.getNewAssignmentAttr());
		surveyMonthSession.setSessionAssignmentAttr(formModel.getAssignmentAttr());
		//surveyMonthSession.setSessionNonCateAssignmentAttr(formModel.getNonCateAssignmentAttr());
		
		List<AssignmentAttributes> tempList = new ArrayList<AssignmentAttributes>();
		if(formModel.getNonCateAssignmentAttr() != null){
			for(AssignmentAttributes nonCat : formModel.getNonCateAssignmentAttr()){
				if(nonCat.getSelectedBatchType() == 1 && nonCat.getCollectionDatesStr().length() == 0){
					return 10;
				}
				if(nonCat.getReferenceId().length() > 0){
					tempList.add(nonCat);
				}
			}
		}
		surveyMonthSession.setSessionNonCateAssignmentAttr(tempList);
		
		Map<Integer, Set<String>> oldDisplayModelIdBackTrackDateMap = new HashMap<>();
		Map<Integer, BackTrackDateDisplayModel> oldDisplayModelIdMap = new HashMap<>();
		Map<Integer, BackTrackDateDisplayModel> newDisplayModelIdMap = new HashMap<>();
		
		if(surveyMonthSession.getBackTrackDateModelList() != null && !surveyMonthSession.getBackTrackDateModelList().isEmpty()){
			for (BackTrackDateDisplayModel backTrackDateDisplayModel : surveyMonthSession.getBackTrackDateModelList()) {
				if(backTrackDateDisplayModel.getBackTrackDateDisplayModelId() != null){
					oldDisplayModelIdMap.put(backTrackDateDisplayModel.getBackTrackDateDisplayModelId(), backTrackDateDisplayModel);
					
					Set<String> list = oldDisplayModelIdBackTrackDateMap.get(backTrackDateDisplayModel.getBackTrackDateDisplayModelId());
					List<BackTrackDate> backTrackDates = backTrackDateDisplayModel.getBackTrackDayList();
					if(backTrackDates != null && !backTrackDates.isEmpty()){
						for (BackTrackDate backTrackDate : backTrackDates) {
							if(list != null){
								list.add(backTrackDate.getReferenceCollectionDateStr());
							} else {
								list = new HashSet<>();
								list.add(backTrackDate.getReferenceCollectionDateStr());
								oldDisplayModelIdBackTrackDateMap.put(backTrackDateDisplayModel.getBackTrackDateDisplayModelId(), list);
							}
						}
					}
				}
			}
		}
		
		Map<Integer, Set<String>> newDisplayModelIdBackTrackDateMap = new HashMap<>();
		if(surveyMonthSession.getSessionNewAssignmentAttr() != null){
			loopNewAA: for(AssignmentAttributes newEntry : surveyMonthSession.getSessionNewAssignmentAttr()){
				if(newEntry.getAllocationBatchRefId().length() == 0 &&
						(newEntry.getBatchId() == null || newEntry.getBatchId() == 0) &&
						newEntry.getCollectionDatesStr().length() == 0 &&
						newEntry.getEndDateStr().length() == 0 &&
						newEntry.getStartDateStr().length() == 0 &&
						newEntry.getReferenceId().length() == 0 &&
						newEntry.getOfficerIds() == 0 &&
						newEntry.getSession().length() == 0){
					continue loopNewAA;
				}
				String batchCode = "";
				if(newEntry.getBatchId() != null && newEntry.getBatchId() != 0){
					Batch b = this.service.getBatchById(newEntry.getBatchId());
					newEntry.setSelectedBatchType(b.getAssignmentType());
					newEntry.setCategory(b.getBatchCategory());
					batchCode = b.getCode();
				}
//				if(newEntry.getSelectedBatchType() == 1 && newEntry.getCollectionDatesStr().length() == 0){
//					return 10;
//				}
				if(newEntry.getStartDateStr().length() > 0){
					newEntry.setStartDate(commonService.getDate(newEntry.getStartDateStr()));
				}
				
				if(newEntry.getEndDateStr().length() > 0){
					newEntry.setEndDate(commonService.getDate(newEntry.getEndDateStr()));
				}
				int backTrackRefId = 0;
				if(newEntry.getCollectionDatesStr().length() > 0){
					List<String> dateStrList = Arrays.asList(newEntry.getCollectionDatesStr().split("\\s*,\\s*"));
					BackTrackDateDisplayModel displayModel = new BackTrackDateDisplayModel();
					for(String dateStr : dateStrList){
						BackTrackDate backTrackDateModel = new BackTrackDate();
						backTrackDateModel.setReferenceId(backTrackRefId);
						backTrackDateModel.setReferenceCollectionDateStr(dateStr);
						backTrackDateModel.setReferenceCollectionDate(commonService.getDate(dateStr));
						backTrackDateModel.setAssignmentAttributesReferenceId(newEntry.getReferenceId());
						backTrackDateModel.setBatchId(newEntry.getBatchId());
						backTrackDateModel.setBatchCode(batchCode);
						backTrackDateModel.setHasBackTrack(backTrackDateModel.getBackTrackDateList()!= null && !backTrackDateModel.getBackTrackDateList().isEmpty());
						displayModel.setBatchCode(batchCode);
						displayModel.getBackTrackDayList().add(backTrackDateModel);
						backTrackRefId++;
					}
					Collections.sort(displayModel.getBackTrackDayList(), new Comparator<BackTrackDate>(){
						@Override
						public int compare(BackTrackDate o1,
								BackTrackDate o2) {
							// TODO Auto-generated method stub
							return o1.getReferenceCollectionDate().compareTo(o2.getReferenceCollectionDate());
						}
						
					});
					
					Integer backTrackDateDisplayModelId = newEntry.getBackTrackDateDisplayModelId();
					displayModel.setBackTrackDateDisplayModelId(backTrackDateDisplayModelId);
					Set<String> tempSet = newDisplayModelIdBackTrackDateMap.get(backTrackDateDisplayModelId);
					if(tempSet != null){
						tempSet.addAll(dateStrList);
					} else {
						tempSet = new HashSet<>();
						tempSet.addAll(dateStrList);
						newDisplayModelIdBackTrackDateMap.put(backTrackDateDisplayModelId, tempSet);
					}
					
					newEntry.getCollectionDateList().add(displayModel);
					if(newDisplayModelIdMap.get(backTrackDateDisplayModelId) == null){
						newDisplayModelIdMap.put(backTrackDateDisplayModelId, displayModel);
					}
				}
			}
			
			if(completeTab){
				
				int errorCode = this.vaildateCollectionDates(surveyMonthSession);
				
				if(errorCode > 0){
					return errorCode;
				}
				
				if(surveyMonthSession.getSessionNewAssignmentAttr() != null){
					for(AssignmentAttributes newEntry : surveyMonthSession.getSessionNewAssignmentAttr()){
						for( BackTrackDateDisplayModel displayModel : newEntry.getCollectionDateList()){
							for( BackTrackDate backTrackDateModel : displayModel.getBackTrackDayList()){						
								backTrackDateModel.setBackTrackDateAvailableFrom(surveyMonthSession.getSessionSurveyMonth().getStartDate());
								backTrackDateModel.setBackTrackDateAvailableFromString(commonService.formatDate(surveyMonthSession.getSessionSurveyMonth().getStartDate()));
								backTrackDateModel.setBackTrackDateAvailableTo(surveyMonthSession.getSessionSurveyMonth().getEndDate());
								backTrackDateModel.setBackTrackDateAvailableToString(commonService.formatDate(surveyMonthSession.getSessionSurveyMonth().getEndDate()));
							}
						}
					}
				}
				
			}
		}
		
		if(surveyMonthSession.getSessionNonCateAssignmentAttr() != null){
			loopNonCatAA: for(AssignmentAttributes notCatEntry : surveyMonthSession.getSessionNonCateAssignmentAttr()){
				if(notCatEntry.getAllocationBatchRefId().length() == 0 &&
						(notCatEntry.getBatchId() == null || notCatEntry.getBatchId() == 0) &&
						notCatEntry.getCollectionDatesStr().length() == 0 &&
						notCatEntry.getEndDateStr().length() == 0 &&
						notCatEntry.getStartDateStr().length() == 0 &&
						notCatEntry.getReferenceId().length() == 0 &&
						notCatEntry.getOfficerIds() == 0 &&
						notCatEntry.getSession().length() == 0){
					continue loopNonCatAA;
				}
				String batchCode = "";
				if(notCatEntry.getBatchId() != null && notCatEntry.getBatchId() != 0){
					Batch b = this.service.getBatchById(notCatEntry.getBatchId());
					notCatEntry.setSelectedBatchType(b.getAssignmentType());
					notCatEntry.setCategory(b.getBatchCategory());
					batchCode = b.getCode();
				}
				if(notCatEntry.getSelectedBatchType() == 1 && notCatEntry.getCollectionDatesStr().length() == 0){
					return 10;
				}
				if(notCatEntry.getStartDateStr().length() > 0){
					notCatEntry.setStartDate(commonService.getDate(notCatEntry.getStartDateStr()));
				}
				
				if(notCatEntry.getEndDateStr().length() > 0){
					notCatEntry.setEndDate(commonService.getDate(notCatEntry.getEndDateStr()));
				}
				int backTrackRefId = 0;
				if(notCatEntry.getCollectionDatesStr().length() > 0){
					List<String> dateStrList = Arrays.asList(notCatEntry.getCollectionDatesStr().split("\\s*,\\s*"));
					BackTrackDateDisplayModel displayModel = new BackTrackDateDisplayModel();
					for(String dateStr : dateStrList){
						BackTrackDate backTrackDateModel = new BackTrackDate();
						backTrackDateModel.setReferenceId(backTrackRefId);
						backTrackDateModel.setReferenceCollectionDateStr(dateStr);
						backTrackDateModel.setReferenceCollectionDate(commonService.getDate(dateStr));
						backTrackDateModel.setAssignmentAttributesReferenceId(notCatEntry.getReferenceId());
						backTrackDateModel.setBatchId(notCatEntry.getBatchId());
						backTrackDateModel.setBatchCode(batchCode);
						backTrackDateModel.setHasBackTrack(backTrackDateModel.getBackTrackDateList()!= null && !backTrackDateModel.getBackTrackDateList().isEmpty());
						displayModel.setBatchCode(batchCode);
						displayModel.getBackTrackDayList().add(backTrackDateModel);
						backTrackRefId++;
					}
					Collections.sort(displayModel.getBackTrackDayList(), new Comparator<BackTrackDate>(){

						@Override
						public int compare(BackTrackDate o1,
								BackTrackDate o2) {
							// TODO Auto-generated method stub
							return o1.getReferenceCollectionDate().compareTo(o2.getReferenceCollectionDate());
						}
						
					});
					
					Integer backTrackDateDisplayModelId = notCatEntry.getBackTrackDateDisplayModelId();
					displayModel.setBackTrackDateDisplayModelId(backTrackDateDisplayModelId);
					Set<String> tempSet = newDisplayModelIdBackTrackDateMap.get(backTrackDateDisplayModelId);
					if(tempSet != null){
						tempSet.addAll(dateStrList);
					} else {
						tempSet = new HashSet<>();
						tempSet.addAll(dateStrList);
						newDisplayModelIdBackTrackDateMap.put(backTrackDateDisplayModelId, tempSet);
					}
					
					notCatEntry.getCollectionDateList().add(displayModel);
					if(newDisplayModelIdMap.get(backTrackDateDisplayModelId) == null){
						newDisplayModelIdMap.put(backTrackDateDisplayModelId, displayModel);
					}
					notCatEntry.getCollectionDateList().add(displayModel);
				}		
				
			}
		
			if(completeTab){
				int errorCode = this.vaildateCollectionDates(surveyMonthSession);
				if(errorCode > 0){
					return errorCode;
				}
				
				if(surveyMonthSession.getSessionNonCateAssignmentAttr() != null){
					for(AssignmentAttributes nonCat : surveyMonthSession.getSessionNonCateAssignmentAttr()){
						for( BackTrackDateDisplayModel displayModel : nonCat.getCollectionDateList()){
							for( BackTrackDate backTrackDateModel : displayModel.getBackTrackDayList()){
								backTrackDateModel.setBackTrackDateAvailableFrom(surveyMonthSession.getSessionSurveyMonth().getStartDate());
								backTrackDateModel.setBackTrackDateAvailableFromString(commonService.formatDate(surveyMonthSession.getSessionSurveyMonth().getStartDate()));
								backTrackDateModel.setBackTrackDateAvailableTo(surveyMonthSession.getSessionSurveyMonth().getEndDate());
								backTrackDateModel.setBackTrackDateAvailableToString(commonService.formatDate(surveyMonthSession.getSessionSurveyMonth().getEndDate()));
							}
						}
					}
				}
				
			}
			
			//Update the backTrackDateDisplayModel after submission
			Set<Integer> oldIds = new HashSet<Integer>();
			if(oldDisplayModelIdBackTrackDateMap != null){
				for(Integer key : oldDisplayModelIdBackTrackDateMap.keySet()){
					oldIds.add(key);
				}
			}
			Set<Integer> newIds = new HashSet<Integer>();
			if(newDisplayModelIdBackTrackDateMap != null){
				for(Integer key : newDisplayModelIdBackTrackDateMap.keySet()){
					newIds.add(key);
				}
			}
			Collection<Integer> insertIds = (Collection<Integer>) CollectionUtils.subtract(newIds, oldIds);
			if(insertIds != null && !insertIds.isEmpty()){
				List<BackTrackDateDisplayModel> insertModels = new ArrayList<BackTrackDateDisplayModel>();
				for(Integer id : insertIds){
					BackTrackDateDisplayModel model = newDisplayModelIdMap.get(id);
					insertModels.add(model);
				}
				surveyMonthSession.getBackTrackDateModelList().addAll(insertModels);
			}
			Collection<Integer> deleteIds = (Collection<Integer>) CollectionUtils.subtract(oldIds, newIds);
			if(deleteIds != null && !deleteIds.isEmpty()){
				List<BackTrackDateDisplayModel> delModels = new ArrayList<BackTrackDateDisplayModel>();
				for(Integer id : deleteIds){
					BackTrackDateDisplayModel model = oldDisplayModelIdMap.get(id);
					delModels.add(model);
				}
				surveyMonthSession.getBackTrackDateModelList().removeAll(delModels);
			}
			Collection<Integer> updatedIds = (Collection<Integer>) CollectionUtils.intersection(oldIds, newIds);
			if(updatedIds != null && !updatedIds.isEmpty()){
				for(Integer id : updatedIds){
					BackTrackDateDisplayModel model = oldDisplayModelIdMap.get(id);
					Set<String> refCollectionDates = oldDisplayModelIdBackTrackDateMap.get(id);
					if(newDisplayModelIdBackTrackDateMap.get(id) != null && refCollectionDates != null){
						refCollectionDates.retainAll(newDisplayModelIdBackTrackDateMap.get(id));

						List<BackTrackDate> backTrackDates = model.getBackTrackDayList();
						List<BackTrackDate> delElements = new ArrayList<>();
						if(backTrackDates != null && !backTrackDates.isEmpty()){
							for (BackTrackDate backTrackDate : backTrackDates) {
								if(!refCollectionDates.contains(backTrackDate.getReferenceCollectionDateStr())){
									delElements.add(backTrackDate);
								}
							}
							backTrackDates.removeAll(delElements);
						}
					}
				}
			}
			
		}
		
		return 0;
	}
	
	private int vaildateCollectionDates(SurveyMonthSession surveyMonthSession){
		// GENERATE COMPLETE COLLECTION DATE LIST FOR CHECKING
		List<CategoryModel> collectionDateCheckingList = new ArrayList<CategoryModel>();
		
		// For getting working date list;
		Date minCollectionDate = null;
		Date maxCollectionDate = null;
		if(surveyMonthSession.getSessionNewAssignmentAttr() != null){
			for(AssignmentAttributes aa : surveyMonthSession.getSessionNewAssignmentAttr()){
				
				if(aa.getCategory() == null || aa.getCategory().equalsIgnoreCase("") && aa.getSelectedBatchType() == 1){
					continue;
				}
				boolean findCategory = false;
				for(CategoryModel category : collectionDateCheckingList){
					if(category.getCategory().equalsIgnoreCase(aa.getCategory())){
						findCategory = true;
						List<BatchCodeModel> currentBatchCodeList = category.getBatchCodeList();
						boolean findBatch = false;
						for(BatchCodeModel batchCode : currentBatchCodeList){
							if(batchCode.getBatchCodeId().intValue() == (aa.getBatchId()).intValue()){
								findBatch = true;
								for(BackTrackDateDisplayModel btddm : aa.getCollectionDateList()){
									for(BackTrackDate btd : btddm.getBackTrackDayList()){
										if(batchCode.getCollectionDateList().contains(btd.getReferenceCollectionDate())){
											// The collection dates should be distinct in the same batch code (E00125)
											return 125; 
										}else{
											batchCode.getCollectionDateList().add(btd.getReferenceCollectionDate());
											if(minCollectionDate == null || btd.getReferenceCollectionDate().before(minCollectionDate)){
												minCollectionDate = btd.getReferenceCollectionDate();
											}
											if(maxCollectionDate == null || btd.getReferenceCollectionDate().after(maxCollectionDate)){
												maxCollectionDate = btd.getReferenceCollectionDate();
											}
										}
									}
								}
							}
						}
						
						if(!findBatch){
							BatchCodeModel bcm = new BatchCodeModel();
							bcm.setBatchCodeId(aa.getBatchId());
							bcm.setType(aa.getSelectedBatchType());
							for(BackTrackDateDisplayModel btddm : aa.getCollectionDateList()){
								for(BackTrackDate btd : btddm.getBackTrackDayList()){
									bcm.getCollectionDateList().add(btd.getReferenceCollectionDate());
									if(minCollectionDate == null || btd.getReferenceCollectionDate().before(minCollectionDate)){
										minCollectionDate = btd.getReferenceCollectionDate();
									}
									if(maxCollectionDate == null || btd.getReferenceCollectionDate().after(maxCollectionDate)){
										maxCollectionDate = btd.getReferenceCollectionDate();
									}
								}
							}
							category.getBatchCodeList().add(bcm);
						}
					}
				}
				
				
				//create new list for category;
				if(!findCategory){
					CategoryModel newCate = new CategoryModel();
					newCate.setCategory(aa.getCategory());
					BatchCodeModel bcm = new BatchCodeModel();
					bcm.setBatchCodeId(aa.getBatchId());
					bcm.setType(aa.getSelectedBatchType());
					for(BackTrackDateDisplayModel btddm : aa.getCollectionDateList()){
						for(BackTrackDate btd : btddm.getBackTrackDayList()){
							bcm.getCollectionDateList().add(btd.getReferenceCollectionDate());
							if(minCollectionDate == null || btd.getReferenceCollectionDate().before(minCollectionDate)){
								minCollectionDate = btd.getReferenceCollectionDate();
							}
							if(maxCollectionDate == null || btd.getReferenceCollectionDate().after(maxCollectionDate)){
								maxCollectionDate = btd.getReferenceCollectionDate();
							}
						}
					}
					newCate.getBatchCodeList().add(bcm);
					collectionDateCheckingList.add(newCate);
				}
			}
		}
		
		if(surveyMonthSession.getSessionNonCateAssignmentAttr() != null){
			for(AssignmentAttributes aa : surveyMonthSession.getSessionNonCateAssignmentAttr()){
				if(aa.getSelectedBatchType() == 1){
					CategoryModel newCate = new CategoryModel();
					newCate.setCategory("");
					BatchCodeModel bcm = new BatchCodeModel();
					for(BackTrackDateDisplayModel btddm : aa.getCollectionDateList()){
						bcm.setBatchCodeId(aa.getBatchId());
						bcm.setType(aa.getSelectedBatchType());
						for(BackTrackDate btd : btddm.getBackTrackDayList()){
							bcm.getCollectionDateList().add(btd.getReferenceCollectionDate());
							if(minCollectionDate == null || btd.getReferenceCollectionDate().before(minCollectionDate)){
								minCollectionDate = btd.getReferenceCollectionDate();
							}
							if(maxCollectionDate == null || btd.getReferenceCollectionDate().after(maxCollectionDate)){
								maxCollectionDate = btd.getReferenceCollectionDate();
							}
						}
					}
					newCate.getBatchCodeList().add(bcm);
					collectionDateCheckingList.add(newCate);
				}
			}
		}
		
		if(maxCollectionDate != null && minCollectionDate != null){
		
			//Get available working date list
//			int diffDays = (int)((maxCollectionDate.getTime() - minCollectionDate.getTime()) / (1000 * 60 * 60 * 24)) + 4;
//			List<Date> availableDateList = this.service.getPreviousWorkingDates(maxCollectionDate, diffDays);
			
			//Get availableDateList from current reference month
			Calendar calendar = Calendar.getInstance();
			Date startDate =  surveyMonthSession.getSessionSurveyMonth().getStartDate();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, 1);
			int refMonth = calendar.get(Calendar.MONTH);
			List<Date> availableDateList = new ArrayList<>();
			while (refMonth == calendar.get(Calendar.MONTH)) {
				availableDateList.add(calendar.getTime());
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			surveyMonthSession.setAvailableWorkingDateList(availableDateList);
			
			// checking (E00124)
			// group collection dates by market
			HashMap<String, HashMap<Integer, Set<Date>>> marketBatchCollectionDatesMap = new HashMap<String, HashMap<Integer, Set<Date>>>();
			
			for (CategoryModel workingCategory : collectionDateCheckingList) {
				if (StringUtils.isEmpty(workingCategory.getCategory())) continue; // skip uncategorize
				
				if (!marketBatchCollectionDatesMap.containsKey(workingCategory.getCategory())) {
					marketBatchCollectionDatesMap.put(workingCategory.getCategory(), new HashMap<Integer, Set<Date>>());
				}
				HashMap<Integer, Set<Date>> datesGroup = marketBatchCollectionDatesMap.get(workingCategory.getCategory());
				
				for (BatchCodeModel batchRow : workingCategory.getBatchCodeList()) {
					if (batchRow.getType() != 1) continue; // only for specified collection date batch
					
					if (!datesGroup.containsKey(batchRow.getBatchCodeId())) {
						datesGroup.put(batchRow.getBatchCodeId(), new HashSet<Date>());
					}
					Set<Date> dates = datesGroup.get(batchRow.getBatchCodeId());
					for (Date d : batchRow.getCollectionDateList()) {
						dates.add(d);
					}
				}
			}
			
			// check difference in group
			for (String market : marketBatchCollectionDatesMap.keySet()) {
				HashMap<Integer, Set<Date>> marketMap = marketBatchCollectionDatesMap.get(market);
				Set<Date> dates = null;
				for (Integer batchId : marketMap.keySet()) {
					if (dates == null) {
						dates = marketMap.get(batchId);
						continue;
					}
					if (!CollectionUtils.isEqualCollection(dates, marketMap.get(batchId))) {
						// The collection dates of batch codes should be the same within the same batch category (E00124)
						return 124;
					}
				}
			}
		}
		return 0;
	}
	
	private int updateSessionBackTrackDateTab( SurveyMonthFormModel formModel, SurveyMonthSession surveyMonthSession) throws ParseException{
		List<BackTrackDateFormModel> backTrackDateForm = formModel.getBackTrackDate();
		
		List<BackTrackDateDisplayModel> backTrackDates = surveyMonthSession.getBackTrackDateModelList();
		boolean done = false;
		for(BackTrackDateFormModel btdfm : backTrackDateForm){
			for(BackTrackDateDisplayModel batch : backTrackDates){
				if(btdfm.getBatchCode().equalsIgnoreCase(batch.getBatchCode())){
					for (BackTrackDate btd : batch.getBackTrackDayList()){
						if(btd.getReferenceId().intValue() == btdfm.getReferenceId().intValue()){
							btd.setBackTrackDateString(btdfm.getBackTrackDateStr());
							List<Date> backTrackDays = btd.getBackTrackDateList();
							List<String> backTrackDayStrings = Arrays.asList(btdfm.getBackTrackDateStr().split("\\s*,\\s*"));
							backTrackDays = new ArrayList<Date>();
							for(String backTrackDayString : backTrackDayStrings){
								if(backTrackDayString.length() > 0){
									backTrackDays.add(commonService.getDate(backTrackDayString));
								}
							}
							btd.setBackTrackDateList(backTrackDays);
							btd.setHasBackTrack(btdfm.getIsBackTrack());
							done = true;
						}
						if(done){
							break;
						}
					}
				}
				if(done){
					break;
				}
			}
			done = false;
		}
		
//		Map<Integer, BackTrackDateDisplayModel> backTrackDateDisplayModelMap = new HashMap<>();
//		if(backTrackDates != null && !backTrackDates.isEmpty()){
//			for (BackTrackDateDisplayModel backTrackDateDisplayModel : backTrackDates) {
//				backTrackDateDisplayModelMap.put(backTrackDateDisplayModel.getBackTrackDateDisplayModelId(), backTrackDateDisplayModel);
//			}
//		}	
		
		return 0;
	}
	
	@RequestMapping(value="addAssignmentAttrRow", method = RequestMethod.POST)
	public String addAssignmentAttrRow(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("surveyMonthSession") SurveyMonthSession surveyMonthSession,
			@RequestParam(value = "category", required = true) String category,
			@RequestParam(value = "referenceId", required = true) String referenceId,
			@RequestParam(value = "displayModelId", required = true) String displayModelId){
		
//		List<Batch> batchList = service.getBatchByCategory(category);
		List<BatchQuotationActiveModel> batchList = service.getBatchQuotationActiveByCategory(category);
		List<User> officerList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
		
		int assignmentAttributeId = 0;
		for(AssignmentAttributes item : surveyMonthSession.getSessionNewAssignmentAttr()){
			if(item.getReferenceId() == referenceId){
				assignmentAttributeId = item.getAssignmentAttributeId();
			}
		}
		
		boolean allAssignmentTypeOne = false;
		int countAssignmentTypeOne = 0;
		
		boolean allAssignmentTypeNotOne = false;
		int countAssignmentTypeNotOne = 0;
		
		for(int i = 0; i < batchList.size(); i++) {
			BatchQuotationActiveModel batch = batchList.get(i);
			if(batch.getAssignmentType() == 1) {
				countAssignmentTypeOne++;
			} else if(batch.getAssignmentType() != 1) {
				countAssignmentTypeNotOne++;
			}
		}
		
		if(countAssignmentTypeOne == batchList.size()) {
			allAssignmentTypeOne = true;
		}
		if(countAssignmentTypeNotOne == batchList.size()) {
			allAssignmentTypeNotOne = true;
		}
		
		model.addAttribute("surveyMonth", surveyMonthSession.getSessionSurveyMonth() );
		model.addAttribute("allocationBatchList", surveyMonthSession.getSessionAllocationBatch());
		model.addAttribute("newAllocationBatchList", surveyMonthSession.getSessionNewAllocationBatch());
		model.addAttribute("officerList", officerList);
		model.addAttribute("batchList", batchList);
		model.addAttribute("category", category);
		model.addAttribute("referenceId", referenceId);
		model.addAttribute("backTrackDateDisplayModelId", displayModelId);
		model.addAttribute("assignmentAttributeId", assignmentAttributeId);
		
		model.addAttribute("allAssignmentTypeOne", allAssignmentTypeOne);
		model.addAttribute("allAssignmentTypeNotOne", allAssignmentTypeNotOne);
		
		Date today = new Date();
		String todayStr = commonService.formatDate(today);
		model.addAttribute("todayStr", todayStr);
		
		return "assignmentAllocationAndReallocation/SurveyMonth/partial/assignmentAttributesRow";
	}
	
	/**
	 * getStaffName
	 */
	@RequestMapping(value = "getStaffsName", method = RequestMethod.POST)
	public @ResponseBody StaffNameModel[] getStaffsName(
			Model model,
			Locale locale,
			@RequestParam(value = "ids[]", required = false) String[] strIds,
			RedirectAttributes redirectAttributes) {
		if(strIds != null){
			List<StaffNameModel> nameList = new ArrayList<StaffNameModel>();
			Integer[] ids = new Integer[strIds.length];
			for(int i = 0;i < strIds.length;i++)
			{
				ids[i] = Integer.parseInt(strIds[i]);
			}
			try {    		
				nameList = this.service.getSelectedStaffName(new ArrayList<Integer>(Arrays.asList(ids)));
	 		} catch (Exception e) {
	 		}
			
			return nameList.toArray(new StaffNameModel[nameList.size()]);
		}
		return new StaffNameModel[0];
	}
	
	@RequestMapping(value = "getBatchType", method = RequestMethod.POST)
	public @ResponseBody Integer getBatchType(
			Model model,
			Locale locale,
			@RequestParam(value = "batchId", required = true) Integer batchId,
			RedirectAttributes redirectAttributes){
		
		try{
			Batch b = this.service.getBatchById(batchId);
			if(b != null){
				return b.getAssignmentType();
			}else{
				return 0;
			}
			
		}catch(Exception e){
			this.logger.error("getBatchType", e);
			return 0;
		}
		
	}
	
	/**
	 * Approve survey month
	 */
	@PreAuthorize("hasPermission(#user, 2)")
	@RequestMapping(value = "approveSurveyMonth", method = RequestMethod.POST)
	public String approveSurveyMonth(Integer id, Model model, Locale locale) {
		try {
			service.setSurveyMonthStatus(1, id); // pending
			this.generationTask.generateAssignment(id); // run background job
			
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("Approve Survey Month", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		return "/partial/messageRibbons";
	}	
	
	
}
