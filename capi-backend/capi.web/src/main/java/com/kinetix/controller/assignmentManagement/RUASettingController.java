package com.kinetix.controller.assignmentManagement;

import java.util.ArrayList;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.Quotation;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffNameModel;
import capi.model.assignmentManagement.RUASettingEditModel;
import capi.model.assignmentManagement.RUASettingTableList;
import capi.service.UserService;
import capi.service.assignmentManagement.RUASettingService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PricingMonthService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;
import capi.service.productMaintenance.ProductService;

import com.kinetix.component.FuncCode;

import edu.emory.mathcs.backport.java.util.Arrays;


/**
 * UF-1409 RUA Setting
 */
@Secured("UF1409")
@FuncCode("UF1409")
@Controller("RUASettingController")
@RequestMapping("assignmentManagement/RUASetting")
public class RUASettingController {
	
	private static final Logger logger = LoggerFactory.getLogger(RUASettingController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PurposeService purposeService;

	@Autowired
	private RUASettingService service;

	@Autowired
	private UnitService unitService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OutletService outletService;
	
	@Autowired
	private BatchService batchService;
	
	@Autowired
	private PricingMonthService pricingFrequencyService;

	@Autowired
	private UserService userService;

	@Autowired
	private DistrictService districtService;

	/**
	 * List RUA Setting
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<RUASettingTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel/*,
				@RequestParam(value = "purpose", required = false) String purpose,
				@RequestParam(value = "unitId", required = false) Integer unitId,
				@RequestParam(value = "productId", required = false) Integer productId,
				@RequestParam(value = "outletId", required = false) Integer outletId,
				@RequestParam(value = "batchId", required = false) Integer batchId,
				@RequestParam(value = "pricingFrequencyId", required = false) Integer pricingFrequencyId,
				@RequestParam(value = "cpiQoutationType", required = false) Integer cpiQoutationType*/) {

		try {
			return service.getRUASettingList(requestModel);//, purpose, unitId, productId, outletId, batchId, pricingFrequencyId, cpiQoutationType);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get purpose
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryPurposeSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}

	/**
	 * Get batch
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryBatchSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return batchService.queryBatchSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}

	/**
	 * Get pricing frequency
	 */
	@RequestMapping(value = "queryPricingFrequencySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryPricingFrequencySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return pricingFrequencyService.queryPricingFrequencySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPricingFrequencySelect2", e);
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
	@RequestMapping(value = "queryUnitSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryUnitSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return unitService.queryUnitSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryUnitSelectSingle", e);
		}
		return null;
	}

	/**
	 * Get product select format
	 */
	@RequestMapping(value = "queryProductSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryProductSelect2(Select2RequestModel requestModel, @RequestParam(value="productGroupId", required = false) Integer productGroupId) {
		try {
			return productService.queryProductSelect2(requestModel, productGroupId);
		} catch (Exception e) {
			logger.error("queryProductSelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet select format
	 */
	@RequestMapping(value = "queryOutletSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletSelect2", e);
		}
		return null;
	}

	/**
	 * Get single outlet
	 */
	@RequestMapping(value = "queryOutletSelectSingle", produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryOutletSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return outletService.getOutletSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryOutletSelectSingle", e);
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
				nameList = service.getSelectedStaffName(new ArrayList<Integer>(Arrays.asList(ids)));
	 		} catch (Exception e) {
	 		}
			
			return nameList.toArray(new StaffNameModel[nameList.size()]);
		}
		return new StaffNameModel[0];
	}

	/**
	 * Edit RUA Setting
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model,  DatatableRequestModel requestModel, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Quotation item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getRUASettingById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentManagement/RUASetting/home";
				}
			}
			
			RUASettingEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
			List<User> userFilterList = new ArrayList<User>();
			userFilterList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
			
			model.addAttribute("userFilterList", userFilterList);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save RUA Setting
	 */
	@PreAuthorize("(hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute RUASettingEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getQuotationId() != null) {
				if (service.getRUASettingById(item.getQuotationId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentManagement/RUASetting/home";
				}
			}
			
			if (!service.saveRUASetting(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00018", null, locale));
				return "/assignmentManagement/RUASetting/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/RUASetting/home";
	}

}
