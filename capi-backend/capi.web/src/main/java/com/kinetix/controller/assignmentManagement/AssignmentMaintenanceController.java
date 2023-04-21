package com.kinetix.controller.assignmentManagement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;
import capi.model.assignmentManagement.assignmentManagement.EditAssignmentUnitCategoryModel;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.model.assignmentManagement.assignmentManagement.TableList;
import capi.model.shared.quotationRecord.OutletPostModel;
import capi.model.shared.quotationRecord.PagePostModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;

/**
 * UF-1405 Assignment Maintenance
 */
@Secured("UF1405")
@FuncCode("UF1405")
@Controller("AssignmentMaintenanceController")
@RequestMapping("assignmentManagement/AssignmentMaintenance")
@SessionAttributes({"sessionModel"})
public class AssignmentMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentMaintenanceController.class);

	@Resource(name="messageSource")
	private MessageSource messageSource;

	@Autowired
	private AssignmentMaintenanceService service;

	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private OutletService outletService;

	@Autowired
	private UserService userService;

	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private AppConfigService configService;
	
	/**
	 * Init session
	 */
	@ModelAttribute("sessionModel")
	public SessionModel initSessionModel() {
		return new SessionModel();
	}
	
	/**
	 * home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(@ModelAttribute("sessionModel") SessionModel sessionModel, SessionStatus sessionStatus,
			Model model,
			@RequestParam(value = "deadline", required = false) String deadline,
			@RequestParam(value = "keepSession", required = false) Boolean keepSession) {
		if (!(keepSession!=null && keepSession)){
			sessionStatus.setComplete();
			sessionModel = new SessionModel();
		}
		DatatableRequestModel lastRequestModel = sessionModel.getLastRequestModel();
		model.addAttribute("viewModel", service.prepareAssignmentViewModel(lastRequestModel));
		
		model.addAttribute("deadline", deadline);
	}
	
	/**
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOfficerSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return userService.queryOfficerSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	

	/**
	 * Get officer for imported select2 format
	 */
	@RequestMapping(value = "queryOfficerForImportedSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryOfficerForImportedSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return userService.queryOfficerForImportedSelect2(requestModel, 16);
		} catch (Exception e) {
			logger.error("queryOfficerForImportedSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get survey month select format
	 */
	@RequestMapping(value = "querySurveyMonthSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySurveyMonthSelect2(Select2RequestModel requestModel, Authentication auth) {
		try {
			List<Integer> userIds = new ArrayList<Integer>();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			if (detail.getAuthorityLevel() == 4 || detail.getAuthorityLevel() == 16) {
				userIds.add(detail.getUserId());
				List<Integer> subordinates = userService.getOfficerIdsBySupervisors(detail.getActedUsers());
				userIds.addAll(subordinates);
			}
			return service.querySurveyMonthSelect2(requestModel, userIds);
		} catch (Exception e) {
			logger.error("querySurveyMonthSelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet type select format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get district select format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get normal revisit verify distinct unit category select format
	 */
	@RequestMapping(value = "queryNormalRevisitVerifyDistinctUnitCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalRevisitVerifyDistinctUnitCategorySelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam(value = "verificationType", required = false) Integer verificationType,
			@RequestParam(value = "quotationState") String quotationState) {
		try {
			return service.queryNormalRevisitVerifyDistinctUnitCategorySelect2(requestModel, assignmentId, userId, verificationType, quotationState);
		} catch (Exception e) {
			logger.error("queryNormalRevisitVerifyDistinctUnitCategorySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get ip distinct unit category select format
	 */
	@RequestMapping(value = "queryIPDistinctUnitCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryIPDistinctUnitCategorySelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId") int userId) {
		try {
			return service.queryIPDistinctUnitCategorySelect2(requestModel, assignmentId, userId);
		} catch (Exception e) {
			logger.error("queryIPDistinctUnitCategorySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get verification type select format
	 */
	@RequestMapping(value = "queryVerificationTypeGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryVerificationTypeSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId") int userId) {
		try {
			return service.queryVerificationTypeGroupSelect2(requestModel, assignmentId, userId);
		} catch (Exception e) {
			logger.error("queryVerificationTypeGroupSelect2", e);
		}
		return null;
	}

	/**
	 * Get date selection for normal revisit verify select format
	 */
	@RequestMapping(value = "queryDateSelectionForNormalRevisitVerifySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryDateSelectionForNormalRevisitVerifySelect2(Select2RequestModel requestModel,
			@RequestParam(value = "userId") int userId,
			@RequestParam(value = "outletId") int outletId,
			@RequestParam(value = "quotationState") String quotationState) {
		try {
			return service.queryDateSelectionForNormalRevisitVerifySelect2(requestModel, userId, outletId, quotationState);
		} catch (Exception e) {
			logger.error("queryDateSelectionForNormalRevisitVerifySelect2", e);
		}
		return null;
	}

	/**
	 * Get date selection for IP select format
	 */
	@RequestMapping(value = "queryDateSelectionForIPSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryDateSelectionForIPSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "userId") int userId,
			@RequestParam(value = "outletId") int outletId) {
		try {
			return service.queryDateSelectionForIPSelect2(requestModel, userId, outletId);
		} catch (Exception e) {
			logger.error("queryDateSelectionForIPSelect2", e);
		}
		return null;
	}

	/**
	 * Get normal revisit verify consignment select format
	 */
	@RequestMapping(value = "queryNormalRevisitVerifyConsignmentSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryRevisitVerifyConsignmentSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam(value = "quotationState") String quotationState) {
		try {
			return service.queryNormalRevisitVerifyConsignmentSelect2(requestModel, assignmentId, userId, quotationState);
		} catch (Exception e) {
			logger.error("queryNormalRevisitVerifyConsignmentSelect2", e);
		}
		return null;
	}

	/**
	 * Get ip consignment select format
	 */
	@RequestMapping(value = "queryIPConsignmentSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryIPConsignmentSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId") int userId) {
		try {
			return service.queryIPConsignmentSelect2(requestModel, assignmentId, userId);
		} catch (Exception e) {
			logger.error("queryRevisitVerifyConsignmentSelect2", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<TableList>
		query(DatatableRequestModel requestModel,
				@RequestParam(value = "personInChargeId", required = false) Integer personInChargeId,
				@RequestParam(value = "surveyMonthId", required = false) Integer surveyMonthId,
				@RequestParam(value = "assignmentStatus", required = false) Integer assignmentStatus,
				@RequestParam(value = "deadline", required = false) String deadline,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "quotationState", required = false) String quotationState,
				Authentication auth) {
		try {
			List<Integer> userIds = new ArrayList<Integer>();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			boolean isBusinessAdmin = false;
			if((detail.getAuthorityLevel() & 256) == 256) {
				isBusinessAdmin = true;
			}
			
			if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2 || 
					(detail.getAuthorityLevel() & 256) == 256) {
				return service.getTableList(requestModel, null,
						personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId, districtId, isBusinessAdmin, quotationState);
			}
			else {
				userIds.add(detail.getUserId());
				List<Integer> actedUserIds = new ArrayList<Integer>();
				actedUserIds.add(detail.getUserId());
				actedUserIds.addAll(detail.getActedUsers());
				List<Integer> subordinates = userService.getOfficerIdsBySupervisors(actedUserIds);
				userIds.addAll(subordinates);

				return service.getTableList(requestModel, userIds,
						personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId, districtId, isBusinessAdmin, quotationState);
			}
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "queryImported", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<TableList>
		queryImported(DatatableRequestModel requestModel,
				@RequestParam(value = "personInChargeId", required = false) Integer personInChargeId,
				@RequestParam(value = "surveyMonthId", required = false) Integer surveyMonthId,
				Authentication auth) {
		try {
			List<Integer> userIds = null;
			return service.getImportedTableList(requestModel, userIds,
					personInChargeId, surveyMonthId);
		} catch (Exception e) {
			logger.error("queryImported", e);
		}
		return null;
	}
	
	/**
	 * Delete assignment
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "deleteAssignment", method = RequestMethod.POST)
	public String deleteAssignment(@RequestParam(value = "id") Integer id, Model model, Locale locale) {
		try {
			if(!service.deleteAssignment(id)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
			} else {
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE,
						messageSource.getMessage("I00002", null, locale));
			}
		} catch (Exception e) {
			logger.error("deleteAssignment", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00013", null, locale));
		}
		return "/partial/messageRibbons";
	}
	
	/**
	 * Get outlet types display
	 */
	@RequestMapping(value = "getOutletTypesDisplayByOutletId", method = RequestMethod.GET)
	public @ResponseBody List<String> getOutletTypesDisplayByOutletId(@RequestParam(value = "id") Integer id) {
		try {
			List<String> list = service.getOutletTypesDisplayByOutletId(id);
			return list;
		} catch (Exception e) {
			logger.error("getOutletTypesDisplayByOutletId", e);
		}
		return null;
	}
	
	/**
	 * edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "assignmentId") Integer assignmentId, @RequestParam(value = "userId", required = false) Integer userId,
			Model model,
			@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			EditModel viewModel = service.prepareViewModel(assignmentId, userId);
			viewModel.setPreSelectTab(sessionModel.getTab());
			viewModel.setPreSelectUnitCategory(sessionModel.getUnitCategory());
			viewModel.setPreSelectDateSelectedAssignmentId(sessionModel.getDateSelectedAssignmentId());
			viewModel.setPreSelectDateSelected(sessionModel.getDateSelected());
			viewModel.setPreSelectConsignmentCounter(sessionModel.getConsignmentCounter());
			viewModel.setPreSelectVerificationType(sessionModel.getVerificationType());
			model.addAttribute("model", viewModel);
			
			sessionModel.setAssignmentId(assignmentId);
			sessionModel.setUserId(viewModel.getUserId());
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}
	
	/**
	 * Save outlet
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "saveOutlet", method = RequestMethod.POST)
	public String saveOutlet(OutletPostModel item, @RequestParam("outletImage") MultipartFile outletImage
			, Model model, @RequestParam(value = "sessionModelId", required = false) String sessionModelId
			, @ModelAttribute("sessionModel") SessionModel sessionModel
			, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (outletImage != null && !outletImage.isEmpty()) {
				if (!outletImage.getContentType().contains("image")) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
					throw new Exception("outletImage not image");
				}
			}
			
			service.saveOutlet(item, sessionModel, outletImage != null && !outletImage.isEmpty() ? outletImage.getInputStream() : null, configService.getFileBaseLoc());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModelId=" + sessionModelId;
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		
		try {
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			
			int assignmentId = sessionModel.getAssignmentId();
			Integer userId = sessionModel.getUserId();

			EditModel viewModel = service.prepareViewModel(assignmentId, userId);
			
			service.prefillOutletViewModelWithPost(viewModel.getOutlet(), item);
			
			viewModel.setPreSelectTab(sessionModel.getTab());
			viewModel.setPreSelectUnitCategory(sessionModel.getUnitCategory());
			viewModel.setPreSelectDateSelectedAssignmentId(sessionModel.getDateSelectedAssignmentId());
			viewModel.setPreSelectDateSelected(sessionModel.getDateSelected());
			model.addAttribute("model", viewModel);
			
			return "/assignmentManagement/AssignmentMaintenance/edit";
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		return "/assignmentManagement/AssignmentMaintenance/home?keepSession=true";
	}

	/**
	 * save attachment
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "saveAttachment", method = RequestMethod.POST)
	public String saveAttachment(@RequestParam(value = "id") int id, @RequestParam(value = "redirectUrl") String redirectUrl, MultipartHttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Hashtable<Integer, InputStream> overwriteAttachments = new Hashtable<Integer, InputStream>();
//			List<InputStream> newAttachments = new ArrayList<InputStream>();
			Hashtable<Integer, InputStream> newAttachments = new Hashtable<Integer, InputStream>();
			
			if (request.getContextPath() != null && redirectUrl.startsWith(request.getContextPath())) {
				redirectUrl = redirectUrl.substring(request.getContextPath().length());
			}
			int i =0;
			Iterator<String> iterator = request.getFileNames();
			while (iterator.hasNext()) {
				i++;
				String fileId = iterator.next();
				MultipartFile multiFile = request.getFile(fileId);
				if (multiFile == null || multiFile.isEmpty()) {
					if(fileId.endsWith("del")){
						outletService.deleteOutletAttachment(Integer.parseInt(fileId.replace("file", "").replace("del", "")), configService.getFileBaseLoc());
					}
					continue;
				}
				
				if (!multiFile.getContentType().contains("image")) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
					return "redirect:" + redirectUrl;
				}
				
				if (fileId.startsWith("newAttachment"))
					newAttachments.put(i, multiFile.getInputStream());
//					newAttachments.add(multiFile.getInputStream());
				else
					overwriteAttachments.put(Integer.parseInt(fileId.replace("file", "").replace("del", "")), multiFile.getInputStream());
			}
			
			outletService.saveAttachment(id, overwriteAttachments, newAttachments, configService.getFileBaseLoc());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00008", null, locale));
			
			return "redirect:" + redirectUrl;
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00082", null, locale));
		}
		return "redirect:" + redirectUrl;
	}
	
	/**
	 * DataTable query revisit verify quotation record
	 */
	@RequestMapping(value = "queryNormalRevisitVerifyQuotationRecord", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList>
	queryNormalRevisitVerifyQuotationRecord(DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentId") Integer assignmentId,
				@RequestParam(value = "userId") Integer userId,
				@RequestParam(value = "consignmentCounter", required = false) String consignmentCounter,
				@RequestParam(value = "verificationType", required = false) Integer verificationType,
				@RequestParam(value = "unitCategory", required = false) String unitCategory,
				@RequestParam(value = "quotationState") String quotationState) {
		try {
			if (assignmentId == null) assignmentId = 0;
			return service.getNormalRevisitVerifyQuotationRecordTableList(requestModel, assignmentId, userId, consignmentCounter, verificationType, unitCategory, quotationState);
		} catch (Exception e) {
			logger.error("queryNormalRevisitVerifyQuotationRecord", e);
		}
		return null;
	}

	/**
	 * DataTable query IP quotation record
	 */
	@RequestMapping(value = "queryIPQuotationRecord", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList>
		queryIPQuotationRecord(DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentId") Integer assignmentId,
				@RequestParam(value = "userId") Integer userId,
				@RequestParam(value = "consignmentCounter", required = false) String consignmentCounter,
				@RequestParam(value = "unitCategory", required = false) String unitCategory) {
		try {
			if (assignmentId == null) assignmentId = 0;
			return service.getIPQuotationRecordTableList(requestModel, assignmentId, userId, consignmentCounter, unitCategory);
		} catch (Exception e) {
			logger.error("queryIPQuotationRecord", e);
		}
		return null;
	}

	/**
	 * Set quotation record flag
	 */
	@RequestMapping(value = "setQuotationRecordFlag", method = RequestMethod.POST)
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	public @ResponseBody boolean setQuotationRecordFlag(@RequestParam(value = "id") int id, @RequestParam(value = "flag") boolean flag) {
		try {
			service.setQuotationRecordFlag(id, flag);
			return true;
		} catch (Exception e) {
			logger.error("setQuotationRecordFlag", e);
		}
		return false;
	}
	
	/**
	 * Cache Quotation Record Search Filter And Result
	 */
	@RequestMapping(value = "cacheQuotationRecordSearchFilterAndResult", method = RequestMethod.POST)
	public @ResponseBody boolean cacheQuotationRecordSearchFilterAndResult(@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			List<Integer> ids = null;
			int assignmentId = sessionModel.getAssignmentId();
			Integer userId = sessionModel.getUserId();
			String unitCategory = sessionModel.getUnitCategory();
			String consignmentCounter = sessionModel.getConsignmentCounter();
			Integer verificationType = sessionModel.getVerificationType();
			DatatableRequestModel dataTableRequestModel = new DatatableRequestModel();
			dataTableRequestModel.setOrder(sessionModel.getOrder());
			
			if (sessionModel.getTab().equals("Normal")) {
				ids = service.getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), userId, consignmentCounter, null, unitCategory, "Normal", dataTableRequestModel);
			} else if (sessionModel.getTab().equals("Revisit")) {
				ids = service.getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), userId, consignmentCounter, verificationType, unitCategory, "Revisit", dataTableRequestModel);
			} else if (sessionModel.getTab().equals("Verify")) {
				ids = service.getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), userId, consignmentCounter, verificationType, unitCategory, "Verify", dataTableRequestModel);
			} else if (sessionModel.getTab().equals("IP")) {
				ids = service.getIPQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), userId, consignmentCounter, unitCategory, dataTableRequestModel);
			}
			if(ids!=null && ids.size()>0){
				sessionModel.setIds(ids);
			} else {
				sessionModel.setIds(new ArrayList<Integer>());
			}
			
		} catch (Exception e) {
			logger.error("cacheQuotationRecordSearchFilterAndResult", e);
		}
		return false;
	}

	/**
	 * Edit quotation record
	 */
	@RequestMapping(value = "editQuotationRecord", method = RequestMethod.GET)
	public void editQuotationRecord(Model model, @RequestParam(value = "quotationRecordId") Integer quotationRecordId,
			@ModelAttribute("sessionModel") SessionModel sessionModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
//			QuotationRecordPageViewModel viewModel = service.prepareQuotationRecordPageViewModel(quotationRecordId, sessionModel, false, false);
			QuotationRecordPageViewModel viewModel = null;
			if ((detail.getAuthorityLevel() & 256) != 256 && (detail.getAuthorityLevel() & 16) != 16) {
				viewModel = service.prepareQuotationRecordPageViewModel(quotationRecordId, sessionModel, false, true);
			} else {
				viewModel = service.prepareQuotationRecordPageViewModel(quotationRecordId, sessionModel, false, false);
			}
			
			viewModel.setHideOutlet(true);
			model.addAttribute("model", viewModel);
		} catch (Exception e) {
			logger.error("editQuotationRecord", e);
		}
	}
	
	/**
	 * Save quotation record
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "saveQuotationRecord", method = RequestMethod.POST)
	public String saveQuotationRecord(
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			@ModelAttribute PagePostModel item,
			@RequestParam(value = "btnSubmit", required = false) String btnSubmit,
			Model model, Locale locale, RedirectAttributes redirectAttributes,
			@RequestParam(value = "photo1", required = false) MultipartFile photo1,
			@RequestParam(value = "photo2", required = false) MultipartFile photo2,
			@RequestParam(value = "sessionModelId", required = false) String sessionModelId) {
		try {
			boolean invalidImage = false;
			
			if (photo1 != null && !photo1.isEmpty()) {
				if (!photo1.getContentType().contains("image")) {
					invalidImage = true;
				}
			}
			
			if (photo2 != null && !photo2.isEmpty()) {
				if (!photo2.getContentType().contains("image")) {
					invalidImage = true;
				}
			}
			
			if (invalidImage) {
				throw new ServiceException("E00100");
			}
			
			service.saveQuotationRecord(item, photo1 != null ? photo1.getInputStream() : null, photo2 != null ? photo2.getInputStream() : null, configService.getFileBaseLoc(), sessionModel, btnSubmit);
			
			if (btnSubmit.toLowerCase().contains("submit")) {
				service.updateSessionAfterSubmit(item.getQuotationRecord().getQuotationRecordId(), sessionModel);
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (ServiceException e) {
			logger.error("saveQuotationRecord", e);
			try {
				if (e.getMessages() != null) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, StringUtils.join(e.getMessages(), "<br/>"));
				} else {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
				}
				PageViewModel viewModel = service.prepareQuotationRecordPageViewModel(item.getQuotationRecord().getQuotationRecordId(), sessionModel, false, false);
				quotationRecordService.prefillViewModelWithPost(viewModel, item);
				model.addAttribute("model", viewModel);
			} catch (Exception e2) {
				logger.error("prefillViewModelWithPost", e2);
			}
			return "/assignmentManagement/AssignmentMaintenance/editQuotationRecord";
		} catch (Exception e) {
			logger.error("saveQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		try {
			if ("save".equals(btnSubmit))
				return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + item.getQuotationRecord().getQuotationRecordId() + "&sessionModel=" + sessionModelId;
			else if ("saveAndBack".equals(btnSubmit) || "submitAndBack".equals(btnSubmit))
				return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModel=" + sessionModelId;
			else if ("next".equals(btnSubmit) || "submitAndNext".equals(btnSubmit)) {
				if (sessionModel.getNextId() != null)
					return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getNextId() + "&sessionModel=" + sessionModelId;
				else if (sessionModel.getPreviousId() != null)
					return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getPreviousId() + "&sessionModel=" + sessionModelId;
				else
					return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModel=" + sessionModelId;
			} else if ("previous".equals(btnSubmit)) {
				if (sessionModel.getPreviousId() != null)
					return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getPreviousId() + "&sessionModel=" + sessionModelId;
				else if (sessionModel.getNextId() != null)
					return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getNextId() + "&sessionModel=" + sessionModelId;
				else
					return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModel=" + sessionModelId;
			} else if (StringUtils.isNumeric(btnSubmit)){
				return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + btnSubmit + "&sessionModel=" + sessionModelId;
			}
		} catch (Exception e) {
			logger.error("saveQuotationRecord", e);
		}
		return "redirect:/assignmentManagement/AssignmentMaintenance/home?keepSession=true";
	}
	
	/**
	 * AssignmentUnitCategoryInfo Dialog
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialog", method = RequestMethod.GET)
	public void assignmentUnitCategoryInfoDialog() {
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog Verify Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogVerifyContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogVerifyContent(Model model, @ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			if (sessionModel.getDateSelectedAssignmentId() != null) {
				List<AssignmentUnitCategoryInfoWithVerify> list = service.getUnitCategoryForVerify(sessionModel.getDateSelectedAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), sessionModel.getVerificationType(), sessionModel.getUnitCategory());
				model.addAttribute("model", list);
			}
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogVerifyContent", e);
		}
		return "/assignmentManagement/AssignmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}
	
	/**
	 * AssignmentUnitCategoryInfo Dialog revisit Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogRevisitContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogRevisitContent(Model model, @ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			if (sessionModel.getDateSelectedAssignmentId() != null) {
				List<AssignmentUnitCategoryInfoWithVerify> list = service.getUnitCategoryForRevisit(sessionModel.getDateSelectedAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
				model.addAttribute("model", list);
			}
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogRevisitContent", e);
		}
		return "/assignmentManagement/AssignmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog IP Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogIPContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogIPContent(Model model, @ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			if (sessionModel.getDateSelectedAssignmentId() != null) {
				List<AssignmentUnitCategoryInfoWithVerify> list = service.getUnitCategoryForIP(sessionModel.getDateSelectedAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
				model.addAttribute("model", list);
			}
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogIPContent", e);
		}
		return "/assignmentManagement/AssignmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog normal Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogNormalContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogNormalContent(Model model, @ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			if (sessionModel.getDateSelectedAssignmentId() != null) {
				List<AssignmentUnitCategoryInfoWithVerify> list = service.getUnitCategoryForNormal(sessionModel.getDateSelectedAssignmentId());
				model.addAttribute("model", list);
			}
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogNormalContent", e);
		}
		return "/assignmentManagement/AssignmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}
	
	/**
	 * Save AssignmentUnitCategory
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "saveAssignmentUnitCategory", method = RequestMethod.POST)
	public String saveAssignmentUnitCategory(@ModelAttribute EditAssignmentUnitCategoryModel editModel
			, @ModelAttribute("sessionModel") SessionModel sessionModel,@RequestParam(value = "sessionModelId", required = false) String sessionModelId
			, RedirectAttributes redirectAttributes, Locale locale) {
		try {
			service.saveAssignmentUnitCategory(sessionModel, editModel.getCategories());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("saveAssignmentUnitCategory", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModelId=" + sessionModelId;
	}
	

	
	/**
	 * Check all pass validation
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "checkAllPassValidation", method = RequestMethod.POST)
	public @ResponseBody boolean checkAllPassValidation(@RequestParam(value = "assignmentId") int assignmentId, @RequestParam(value = "userId") int userId) {
		try {
			boolean result = service.checkAllPassValidation(assignmentId, userId);
			return result;
		} catch (Exception e) {
			logger.error("checkAllPassValidation", e);
		}
		return false;
	}
	
	/**
	 * Big submit
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "bigSubmit", method = RequestMethod.POST)
	public String bigSubmit(@RequestParam(value = "assignmentId") int assignmentId, @RequestParam(value = "userId") int userId
			, RedirectAttributes redirectAttributes, Locale locale,@RequestParam(value = "sessionModelId", required = false) String sessionModelId) {
		try {
			if (!service.bigSubmit(assignmentId, userId)) {
				throw new ServiceException("E00134");
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			if (service.countNotSubmittedQuotationRecord(assignmentId) > 0) {
				return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + assignmentId + "&userId=" + userId + "&sessionModelId=" + sessionModelId;
			} else {
				return "redirect:/assignmentManagement/AssignmentMaintenance/home?keepSession=true";
			}
		} catch (Exception e) {
			logger.error("bigSubmit", e);
		}
		return "redirect:/assignmentManagement/AssignmentMaintenance/home?keepSession=true";
	}
	
	/**
	 * Small submit
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "smallSubmit", method = RequestMethod.GET)
	public String smallSubmit(@RequestParam(value = "quotationRecordId") int quotationRecordId, @ModelAttribute("sessionModel") SessionModel sessionModel
			, RedirectAttributes redirectAttributes, Locale locale,@RequestParam(value = "sessionModelId", required = false) String sessionModelId) {
		try {
			if (!service.smallSubmit(quotationRecordId, sessionModel.getUserId())) {
				throw new ServiceException("E00134");
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			if (service.countNotSubmittedQuotationRecord(sessionModel.getAssignmentId()) > 0) {
				return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId() + "&sessionModelId=" + sessionModelId;
			} else {
				return "redirect:/assignmentManagement/AssignmentMaintenance/home?keepSession=true";
			}
		} catch (ServiceException e) {
			try {
				if (e.getMessages() != null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, StringUtils.join(e.getMessages(), "<br/>"));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
				}
			} catch (Exception e2) {
				logger.error("smallSubmit", e2);
			}
		} catch (Exception e) {
			logger.error("smallSubmit", e);
		}
		return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId() + "&sessionModelId=" + sessionModelId;
	}
	
	/**
	 * Set visited
	 */
	@PreAuthorize("hasPermission(#user, 256)  or hasPermission(#user, 16)")
	@RequestMapping(value = "setVisited", method = RequestMethod.GET)
	public String setVisited(@ModelAttribute("sessionModel") SessionModel sessionModel
			, RedirectAttributes redirectAttributes, Locale locale,@RequestParam(value = "sessionModelId", required = false) String sessionModelId) {
		try {
			service.setVisited(sessionModel);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("setVisited", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModelId=" + sessionModelId;
	}
	
	/**
	 * Get status by assignment
	 */
	@RequestMapping(value = "getStatusByAssignment", method = RequestMethod.GET)
	public @ResponseBody int getStatusByAssignment(@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return service.getStatusByAssignment(assignmentId);
		} catch (Exception e) {
			logger.error("getStatusByAssignment", e);
		}
		return 0;
	}
	
	/**
	 * Check time log exists
	 */
	@RequestMapping(value = "checkTimeLogExists", method = RequestMethod.GET)
	public @ResponseBody boolean checkTimeLogExists(@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "selectedDate") String selectedDate,
			@RequestParam(value = "userId") int userId) {
		try {
			return service.checkTimeLogExists(assignmentId, selectedDate, userId);
		} catch (Exception e) {
			logger.error("checkTimeLogExists", e);
		}
		return false;
	}
	
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 16)")
	@RequestMapping(value = "resetChangeProduct", method = RequestMethod.POST)
	public String resetChangeProduct(@RequestParam(value = "id", required = true) int id, Locale locale, @ModelAttribute("sessionModel") SessionModel sessionModel,
									 RedirectAttributes redirectAttributes,@RequestParam(value = "sessionModelId", required = false) String sessionModelId) {
		try {
			quotationRecordService.resetChangeProduct(id);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + id + "&sessionModelId=" + sessionModelId;
		} catch (Exception e) {
			logger.error("resetChangeProduct", e);
		}
		return "redirect:/assignmentManagement/AssignmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + (sessionModel.getUserId() != null ? "&userId=" + sessionModel.getUserId() : "") + "&sessionModelId=" + sessionModelId;
	}

	/**
	 * Last Request Model 
	 */
	@RequestMapping(value = "saveLastRequestModel")
	public @ResponseBody void saveLastRequestModel(
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			SessionStatus sessionStatus,
			DatatableRequestModel requestModel){
		sessionModel.setLastRequestModel(requestModel);
	}
}
