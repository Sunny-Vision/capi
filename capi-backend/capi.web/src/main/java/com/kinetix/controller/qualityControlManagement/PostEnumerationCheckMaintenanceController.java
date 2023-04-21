package com.kinetix.controller.qualityControlManagement;

import java.util.Locale;

import javax.annotation.Resource;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.PECheckFormModel;
import capi.model.qualityControlManagement.PECheckTableList;
import capi.model.qualityControlManagement.QuotationRecordTableList;
import capi.model.shared.quotationRecord.OutletPostModel;
import capi.service.AppConfigService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.qualityControlManagement.PECheckService;

import com.kinetix.component.FuncCode;

/**
 * MB-2009 Post-Enumeration Check List Maintenance
 */
@Secured("RF2003")
@FuncCode("RF2003")
@Controller("PostEnumerationCheckMaintenanceController")
@RequestMapping("qualityControlManagement/PostEnumerationCheckMaintenance")
public class PostEnumerationCheckMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(PostEnumerationCheckMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private PECheckService service;

	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;

	@Autowired
	private AppConfigService configService;
	
	/**
	 * List field experience
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {

	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<PECheckTableList> query(Locale locale, Model model,
			DatatableRequestModel requestModel, String referenceMonth, String certaintyCase, Integer roleHeader) {

		try {

			return service.getCheckTableList(requestModel, referenceMonth, certaintyCase, roleHeader);

		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit PE Check
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "peCheckFormId", required = true) Integer peCheckFormId, Model model,
			Locale locale, RedirectAttributes redirectAttributes) {

		try {

			PECheckFormModel peCheckFormModel = null;
			peCheckFormModel = service.getPECheckFormModel(peCheckFormId);
			if (peCheckFormModel == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/PostEnumerationCheckMaintenance/home";
			}

			model.addAttribute("model", peCheckFormModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute PECheckFormModel item, Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {

			if (item.getPeCheckFormId() != null) {
				if (service.getCheckFormById(item.getPeCheckFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/PostEnumerationCheckMaintenance/home";
				}
			}
			service.saveCheckForm(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/PostEnumerationCheckMaintenance/home";
	}

//	/**
//	 * Get Post-Enumeration Check Assignment Case detail
//	 */
//	@RequestMapping(value = "getPECheckFormDetail", method = RequestMethod.GET)
//	public @ResponseBody List<PECheckTaskList> getPECheckFormDetail(
//			@RequestParam(value = "surveyMonthId", required = true) Integer surveyMonthId,
//			@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
//		try {
//			if (assignmentIds == null || assignmentIds.length == 0)
//				return null;
//			return service.getPECheckFormList(assignmentIds, surveyMonthId);
//		} catch (Exception e) {
//			logger.error("query", e);
//		}
//		return null;
//	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "queryQuotationRecord")
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList> queryQuotationRecord(Locale locale,
			Model model, DatatableRequestModel requestModel, Authentication authentication) {

		try {

			return service.getQuotationRecordTableList(requestModel);

		} catch (Exception e) {
			logger.error("queryQuotationRecord", e);
		}
		return null;
	}

	/**
	 * Save outlet
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "saveOutlet", method = RequestMethod.POST)
	public @ResponseBody String saveOutlet(OutletPostModel item, Model model,
			@RequestParam(value = "assignmentId") Integer assignmentId, @RequestParam(value = "userId") Integer userId,
			@RequestParam(value = "peCheckFormId") Integer peCheckFormId,
			@RequestParam("outletImage") MultipartFile outletImage,
			Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (outletImage != null && !outletImage.isEmpty()) {
				if (!outletImage.getContentType().contains("image")) {
					return messageSource.getMessage("E00100", null, locale);
				}
			}
			service.saveOutletForPECheck(item, assignmentId, userId, peCheckFormId, outletImage != null && !outletImage.isEmpty() ? outletImage.getInputStream() : null, configService.getFileBaseLoc());
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		return "";
	}
}