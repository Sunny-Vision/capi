package com.kinetix.controller.assignmentManagement;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.QuotationRecordMaintenanceFilter;
import capi.model.assignmentManagement.QuotationRecordTableList;
import capi.model.shared.quotationRecord.PagePostModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.TpuService;
import capi.service.masterMaintenance.UnitService;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * UF-1402 Quotation Record Maintenance
 */
@Secured("UF1402")
@FuncCode("UF1402")
@Controller("QuotationRecordMaintenanceController")
@RequestMapping("assignmentManagement/QuotationRecordMaintenance")
@SessionAttributes({"filterSession"})
public class QuotationRecordMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotationRecordMaintenanceController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private UnitService unitService;

	@Autowired
	private OutletService outletService;

	@Autowired
	private QuotationRecordService service;

	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private TpuService tpuService;

	@Autowired
	private UserService userService;

	/**
	 * Init filter session
	 */
	@ModelAttribute("filterSession")
	public QuotationRecordMaintenanceFilter initFilterSession() {
		return new QuotationRecordMaintenanceFilter();
	}
	
	/**
	 * Survey Type Selection
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(@ModelAttribute("filterSession") QuotationRecordMaintenanceFilter filterSession, SessionStatus sessionStatus) {
		try {
			sessionStatus.setComplete();
			filterSession = new QuotationRecordMaintenanceFilter();
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel) {
		try {
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}

	/**
	 * List
	 */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public void list(@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
			@RequestParam(value = "purposeId", required = false) Integer purposeId,
			@ModelAttribute("filterSession") QuotationRecordMaintenanceFilter filterSession,
			DatatableRequestModel requestModel) {
		try {
			if (referenceMonth != null) {
				filterSession.setReferenceMonth(referenceMonth);
				filterSession.setPurposeId(purposeId);
			}
		} catch (Exception e) {
			logger.error("list", e);
		}
	}

	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get tpu select2 format
	 */
	@RequestMapping(value = "queryTpuSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSelect2(Select2RequestModel requestModel) {
		try {
			return tpuService.queryTpuSelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get outlet select2 format
	 */
	@RequestMapping(value = "queryOutletSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOutletSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletSelect2", e);
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
	 * Get unit select format
	 */
	@RequestMapping(value = "queryUnitSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUnitSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.queryUnitSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUnitSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single unit
	 */
	@RequestMapping(value = "getKeyValueByIds")
	public @ResponseBody List<KeyValueModel> getKeyValueByIds(@RequestParam(value = "id[]") Integer[] id) {
		try {
			return unitService.getKeyValueByIds(id);
		} catch (Exception e) {
			logger.error("getKeyValueByIds", e);
		}
		return null;
	}

	/**
	 * Get officer select format
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOfficerSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "teamName[]", required = false) String[] teamName) {
		try {
			if(teamName == null){
				teamName = new String[0];
			}
			return userService.queryOfficerSelect2(requestModel, Arrays.asList(teamName));
		} catch (Exception e) {
			logger.error("queryTeamSelect2", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList>
		query(DatatableRequestModel requestModel,
				@ModelAttribute("filterSession") QuotationRecordMaintenanceFilter filterSession,
				@RequestParam(value = "outletId", required = false) Integer outletId,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "unitId[]", required = false) Integer[] unitId,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "status", required = false) String status,
				@RequestParam(value = "userId[]", required = false) Integer[] userId) {
		try {
			return service.getTableList(requestModel, filterSession.getReferenceMonth(), filterSession.getPurposeId(),
					outletId, outletTypeId, unitId, districtId, tpuId, status, userId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit page
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "id") int id, Model model, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			boolean readonly = false;
			if ((detail.getAuthorityLevel() & 256) != 256) {
				readonly = true;
			}
			PageViewModel viewModel = service.prepareViewModel(id, false, readonly);
			model.addAttribute("model", viewModel);
			if (viewModel.getQuotationRecord().isProductChange()) {
				model.addAttribute("showResetChangeProductButton", true);
			}
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}

	/**
	 * Submit
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public String submit(@ModelAttribute PagePostModel item,
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
			
			service.submit(item, photo1 != null ? photo1.getInputStream() : null, photo2 != null ? photo2.getInputStream() : null, configService.getFileBaseLoc());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (ServiceException e) {
			logger.error("submit", e);
			try {
				if (e.getMessages() != null) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, StringUtils.join(e.getMessages(), "<br/>"));
				} else {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
				}
				PageViewModel viewModel = service.prepareViewModel(item.getQuotationRecord().getQuotationRecordId(), false, false);
				service.prefillViewModelWithPost(viewModel, item);
				model.addAttribute("model", viewModel);
			} catch (Exception e2) {
				logger.error("prefillViewModelWithPost", e2);
			}
			return "/assignmentManagement/QuotationRecordMaintenance/edit";
		} catch (Exception e) {
			logger.error("submit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		if ("submit".equals(btnSubmit))
			return "redirect:/assignmentManagement/QuotationRecordMaintenance/list";
		else
			return "redirect:/assignmentManagement/QuotationRecordMaintenance/home";
	}

	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "resetChangeProduct", method = RequestMethod.POST)
	public String resetChangeProduct(@RequestParam(value = "id", required = true) int id, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.resetChangeProduct(id);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/assignmentManagement/QuotationRecordMaintenance/edit?id=" + id;
		} catch (Exception e) {
			logger.error("resetChangeProduct", e);
		}
		return "redirect:/assignmentManagement/QuotationRecordMaintenance/home";
	}
}
