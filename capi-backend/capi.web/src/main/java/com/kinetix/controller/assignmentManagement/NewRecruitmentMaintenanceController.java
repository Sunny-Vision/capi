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
import capi.model.shared.quotationRecord.MapOutletModel;
import capi.model.shared.quotationRecord.OutletPostModel;
import capi.model.shared.quotationRecord.PagePostModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * UF-2601 Recruitment Maintenance
 */
@Secured("UF2601")
@FuncCode("UF2601")
@Controller("NewRecruitmentMaintenanceController")
@RequestMapping("assignmentManagement/NewRecruitmentMaintenance")
@SessionAttributes({"sessionModel"})
public class NewRecruitmentMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(NewRecruitmentMaintenanceController.class);

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
	private TpuService tpuService;

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
	public void home(@ModelAttribute("sessionModel") SessionModel sessionModel, SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		sessionModel = new SessionModel();
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
	 * Get normal distinct unit category select format
	 */
	@RequestMapping(value = "queryNormalDistinctUnitCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalDistinctUnitCategorySelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId") int userId) {
		try {
			return service.queryNewRecruitmentDistinctUnitCategorySelect2(requestModel, assignmentId, userId);
		} catch (Exception e) {
			logger.error("queryNormalDistinctUnitCategorySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get tpu by specific district select2 format
	 */
	@RequestMapping(value = "queryTpuSingleDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSingleDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel,
				@RequestParam(value = "districtId", required = false) Integer districtId) {
		try {
			if (districtId != null)
				return tpuService.queryTpuSelect2(requestModel, new Integer[] {districtId});
			else
				return tpuService.queryTpuSelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("queryTpuSingleDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get normal consignment select format
	 */
	@RequestMapping(value = "queryNormalConsignmentSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalConsignmentSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId,
			@RequestParam(value = "userId") int userId) {
		try {
			return service.queryNewRecruitmentConsignmentSelect2(requestModel, assignmentId, userId);
		} catch (Exception e) {
			logger.error("queryNormalConsignmentSelect2", e);
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
				Authentication auth) {
		try {
			List<Integer> userIds = new ArrayList<Integer>();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2) {
				return service.getNewRecruitmentTableList(requestModel, null,
						personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId);
			}
			else {
				userIds.add(detail.getUserId());
				List<Integer> actedUserIds = new ArrayList<Integer>();
				actedUserIds.add(detail.getUserId());
				actedUserIds.addAll(detail.getActedUsers());
				List<Integer> subordinates = userService.getOfficerIdsBySupervisors(actedUserIds);
				userIds.addAll(subordinates);

				return service.getNewRecruitmentTableList(requestModel, userIds,
						personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId);
			}
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
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
	public void edit(@RequestParam(value = "assignmentId") Integer assignmentId, @RequestParam(value = "userId") Integer userId,
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
			sessionModel.setUserId(userId);
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}
	
	/**
	 * Save outlet
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveOutlet", method = RequestMethod.POST)
	public String saveOutlet(OutletPostModel item, @RequestParam("outletImage") MultipartFile outletImage
			, Model model
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
			return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		
		try {
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			
			int assignmentId = sessionModel.getAssignmentId();
			int userId = sessionModel.getUserId();

			EditModel viewModel = service.prepareViewModel(assignmentId, userId);
			
			service.prefillOutletViewModelWithPost(viewModel.getOutlet(), item);
			
			viewModel.setPreSelectTab(sessionModel.getTab());
			viewModel.setPreSelectUnitCategory(sessionModel.getUnitCategory());
			viewModel.setPreSelectDateSelectedAssignmentId(sessionModel.getDateSelectedAssignmentId());
			viewModel.setPreSelectDateSelected(sessionModel.getDateSelected());
			model.addAttribute("model", viewModel);
			
			return "/assignmentManagement/NewRecruitmentMaintenance/edit";
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		return "/assignmentManagement/NewRecruitmentMaintenance/home";
	}

	/**
	 * save attachment
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveAttachment", method = RequestMethod.POST)
	public String saveAttachment(@RequestParam(value = "id") int id, @RequestParam(value = "redirectUrl") String redirectUrl, MultipartHttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Hashtable<Integer, InputStream> overwriteAttachments = new Hashtable<Integer, InputStream>();
//			List<InputStream> newAttachments = new ArrayList<InputStream>();
			Hashtable<Integer, InputStream> newAttachments = new Hashtable<Integer, InputStream>();

			if (request.getContextPath() != null && redirectUrl.startsWith(request.getContextPath())) {
				redirectUrl = redirectUrl.substring(request.getContextPath().length());
			}
			
			Iterator<String> iterator = request.getFileNames();
			int i = 0;
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
	 * DataTable query normal quotation record
	 */
	@RequestMapping(value = "queryNormalQuotationRecord", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList>
		queryNormalQuotationRecord(DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentId") Integer assignmentId,
				@RequestParam(value = "userId") Integer userId,
				@RequestParam(value = "consignmentCounter", required = false) String consignmentCounter,
				@RequestParam(value = "unitCategory", required = false) String unitCategory) {
		try {
			return service.getNewRecruitmentQuotationRecordTableList(requestModel, assignmentId, userId, consignmentCounter, unitCategory);
		} catch (Exception e) {
			logger.error("queryNormalQuotationRecord", e);
		}
		return null;
	}

	/**
	 * Set quotation record flag
	 */
	@RequestMapping(value = "setQuotationRecordFlag", method = RequestMethod.POST)
	@PreAuthorize("hasPermission(#user, 16)")
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
			int userId = sessionModel.getUserId();
			String unitCategory = sessionModel.getUnitCategory();
			String consignmentCounter = sessionModel.getConsignmentCounter();
			DatatableRequestModel dataTableRequestModel = new DatatableRequestModel();
			dataTableRequestModel.setOrder(sessionModel.getOrder());
			
			if (sessionModel.getTab().equals("Normal")) {
				ids = service.getNormalRevisitVerifyQuotationRecordTableListAllIds(assignmentId, userId, consignmentCounter, null, unitCategory, "Normal", dataTableRequestModel);
			}
			sessionModel.setIds(ids);
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
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveQuotationRecord", method = RequestMethod.POST)
	public String saveQuotationRecord(
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			@ModelAttribute PagePostModel item,
			@RequestParam(value = "btnSubmit", required = false) String btnSubmit,
			Model model, Locale locale, RedirectAttributes redirectAttributes,
			@RequestParam(value = "photo1", required = false) MultipartFile photo1,
			@RequestParam(value = "photo2", required = false) MultipartFile photo2) {
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
			return "/assignmentManagement/NewRecruitmentMaintenance/editQuotationRecord";
		} catch (Exception e) {
			logger.error("saveQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		try {
			if ("save".equals(btnSubmit))
				return "redirect:/assignmentManagement/NewRecruitmentMaintenance/editQuotationRecord?quotationRecordId=" + item.getQuotationRecord().getQuotationRecordId();
			else if ("saveAndBack".equals(btnSubmit) || "submitAndBack".equals(btnSubmit))
				return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
			else if ("next".equals(btnSubmit) || "submitAndNext".equals(btnSubmit)) {
				if (sessionModel.getNextId() != null)
					return "redirect:/assignmentManagement/NewRecruitmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getNextId();
				else if (sessionModel.getPreviousId() != null)
					return "redirect:/assignmentManagement/NewRecruitmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getPreviousId();
				else
					return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
			} else if ("previous".equals(btnSubmit)) {
				if (sessionModel.getPreviousId() != null)
					return "redirect:/assignmentManagement/NewRecruitmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getPreviousId();
				else if (sessionModel.getNextId() != null)
					return "redirect:/assignmentManagement/NewRecruitmentMaintenance/editQuotationRecord?quotationRecordId=" + sessionModel.getNextId();
				else
					return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
			} else if (StringUtils.isNumeric(btnSubmit)){
				return "redirect:/assignmentManagement/AssignmentMaintenance/editQuotationRecord?quotationRecordId=" + btnSubmit;
			}
		} catch (Exception e) {
			logger.error("saveQuotationRecord", e);
		}
		return "redirect:/assignmentManagement/NewRecruitmentMaintenance/home";
	}
	
	/**
	 * AssignmentUnitCategoryInfo Dialog
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialog", method = RequestMethod.GET)
	public void assignmentUnitCategoryInfoDialog() {
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog normal Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogNormalContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogNormalContent(Model model, @ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			List<AssignmentUnitCategoryInfoWithVerify> list = service.getUnitCategoryForNewRecruitmentNormal(sessionModel.getAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
			model.addAttribute("model", list);
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogNormalContent", e);
		}
		return "/assignmentManagement/NewRecruitmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}
	
	/**
	 * Save AssignmentUnitCategory
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveAssignmentUnitCategory", method = RequestMethod.POST)
	public String saveAssignmentUnitCategory(@ModelAttribute EditAssignmentUnitCategoryModel editModel
			, @ModelAttribute("sessionModel") SessionModel sessionModel
			, RedirectAttributes redirectAttributes, Locale locale) {
		try {
			service.saveNewRecruitmentAssignmentUnitCategory(sessionModel, editModel.getCategories());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("saveAssignmentUnitCategory", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
	}
	

	
	/**
	 * Check all pass validation
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
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
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "bigSubmit", method = RequestMethod.POST)
	public String bigSubmit(@RequestParam(value = "assignmentId") int assignmentId, @RequestParam(value = "userId") int userId
			, RedirectAttributes redirectAttributes, Locale locale) {
		try {
			if (!service.bigSubmit(assignmentId, userId)) {
				throw new ServiceException("E00134");
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + assignmentId + "&userId=" + userId;
		} catch (Exception e) {
			logger.error("bigSubmit", e);
		}
		return "redirect:/assignmentManagement/NewRecruitmentMaintenance/home";
	}

	/**
	 * Small submit
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "smallSubmit", method = RequestMethod.GET)
	public String smallSubmit(@RequestParam(value = "quotationRecordId") int quotationRecordId, @ModelAttribute("sessionModel") SessionModel sessionModel
			, RedirectAttributes redirectAttributes, Locale locale) {
		try {
			if (!service.smallSubmit(quotationRecordId, sessionModel.getUserId())) {
				throw new ServiceException("E00134");
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
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
		return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
	}
	
	/**
	 * Set visited
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "setVisited", method = RequestMethod.GET)
	public String setVisited(@ModelAttribute("sessionModel") SessionModel sessionModel
			, RedirectAttributes redirectAttributes, Locale locale) {
		try {
			service.setVisited(sessionModel);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("setVisited", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
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
	 * Prepare map outlet
	 */
	@RequestMapping(value = "prepareMapOutlet")
	public @ResponseBody MapOutletModel prepareMapOutlet(int id) {
		try {
			MapOutletModel model = outletService.prepareMapOutlet(id);
			return model;
		} catch (Exception e) {
			logger.error("prepareMapOutlet", e);
		}
		return null;
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
	
	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id[]") Integer[] id,
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try{

			List<Integer> ids = Arrays.asList(id);
			quotationRecordService.deleteQuotationRecordByIds(ids);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
			if (service.countNotSubmittedNewRecruitment(sessionModel.getAssignmentId()) == 0){
				return "redirect:/assignmentManagement/NewRecruitmentMaintenance/home";
			}
		} catch (Exception e){
			logger.error("delete", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		}
		return "redirect:/assignmentManagement/NewRecruitmentMaintenance/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
	}
}

