package com.kinetix.controller.assignmentManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.entity.Quotation;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.QuotationEditModel;
import capi.model.assignmentManagement.QuotationTableList;
import capi.service.UserService;
import capi.service.assignmentManagement.QuotationService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PricingMonthService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;
import capi.service.productMaintenance.ProductService;


/**
 * UF-1401 Quotation Maintenance
 */
@Secured("UF1401")
@FuncCode("UF1401")
@Controller("QuotationMaintenanceController")
@RequestMapping("assignmentManagement/QuotationMaintenance")
public class QuotationMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotationMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private QuotationService service;
	
	@Autowired
	private UnitService unitService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OutletService outletService;
	
	@Autowired
	private BatchService batchService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private PricingMonthService pricingMonthService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * List
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, Authentication auth, @RequestParam(value = "selectRUA", required = false) Boolean selectRUA) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			// if not in work practice model and field officer 16 then
			if (!((detail.getAuthorityLevel() & 256) == 256 || (detail.getAuthorityLevel() & 2048) == 2048)
					&& (detail.getAuthorityLevel() & 16) == 16) {
				model.addAttribute("isRUAMode", true);
			} else {
				model.addAttribute("isRUAMode", false);
			}
			model.addAttribute("selectRUA", selectRUA);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.queryQuotation(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "queryRUA", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationTableList>
		queryRUA(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.queryQuotationRUA(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryRUA", e);
		}
		return null;
	}
	
	/**
	 * 
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Quotation item = null;
			if (id != null) {
				item = service.getQuotationById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentManagement/QuotationMaintenance/home";
				}
			} else {
				item = new Quotation();
				item.setICP(false);
				item.setStatus("Active");
			}
			
			QuotationEditModel editModel = service.convertEntityToModel(item);

			
			model.addAttribute("model", editModel);
			//model.addAttribute("E00097", messageSource.getMessage("E00097", null, locale).replace("%s", "12"));

			List<User> userFilterList = new ArrayList<User>();
			userFilterList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
			model.addAttribute("userFilterList", userFilterList);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save quotation
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute QuotationEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			
			if (item.getOutletId() != null && !(service.vilidateOutletType(item))) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00122", null, locale));
					model.addAttribute("model", item);
					return "/assignmentManagement/QuotationMaintenance/edit";
			}
			
			service.save(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));

		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/QuotationMaintenance/home";
	}

	/**
	 * Delete
	*/
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> ids, Model model, Locale locale) {
		try {
			if (!service.deleteQuotation((List<Integer>)ids)) {
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
	 * Delete
	*/
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "toRUA", method = RequestMethod.POST)
	public String toRUA(@RequestParam(value = "id") ArrayList<Integer> ids, Model model, Locale locale) {
		try {
			if (!service.changeStatus((List<Integer>)ids,"RUA")) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}		
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("toRUA", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		return "/partial/messageRibbons";
	}	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Batch select2 format
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryBatchSelect2(Locale locale, Model model, Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return batchService.queryBatchSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single batch
	 */
	@RequestMapping(value = "queryBatchSelectSingle", method = RequestMethod.GET)
	public @ResponseBody String queryBatchSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return batchService.queryBatchSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryBatchSelectSingle", e);
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
	public @ResponseBody Select2ResponseModel queryProductSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search, @RequestParam(value = "productGroupId", required = false) Integer productGroupId) {
		try {
			if (requestModel.getTerm() == null && search != null) {
				requestModel.setTerm(search);
			}
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
	public @ResponseBody Select2ResponseModel queryOutletSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
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
	 * Get pricing frequency select format
	 */
	@RequestMapping(value = "queryPricingFrequencySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPricingFrequencySelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return pricingMonthService.queryPricingFrequencySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPricingFrequencySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Indoor Allocation Code select format
	 */
	@RequestMapping(value = "queryIndoorAllocationCodeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryIndoorAllocationCodeSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return service.queryIndoorAllocationCodeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryIndoorAllocationCodeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Unit Data
	 */
	@RequestMapping(value = "queryUnitData", method = RequestMethod.GET)
	public @ResponseBody HashMap
	queryUnitData(Locale locale, Model model, @RequestParam(value="unitId") Integer unitId) {
		try {
			HashMap data = new HashMap();
			Unit unit = unitService.getById(unitId);
			if (unit != null) {
				if (unit.getCpiBasePeriod() != null) {
					data.put("cpiBasePeriod", unit.getCpiBasePeriod());
				}
				if (unit.getPricingFrequency() != null) {
					data.put("pricingMonth", unit.getPricingFrequency().getName());
				}
				if (unit.getProductCategory() != null) {
					data.put("productGroupId", unit.getProductCategory().getProductGroupId());
				}
				String seasonality = null;
				switch(unit.getSeasonality()) {
				case 1:
					seasonality = "All-time";
					break;
				case 2:
					seasonality = "Summer";
					break;
				case 3:
					seasonality = "Winter";
					break;
				case 4:
					seasonality = "Occasional";
					break;
				}
				data.put("seasonality", seasonality);
				data.put("seasonalityCode", unit.getSeasonality());
				data.put("isFrRequired", unit.isFrRequired());
				
				if (unit.getProductCategory() != null)
					data.put("productGroupId", unit.getProductCategory().getId());
				
			}
			return data;
		} catch (Exception e) {
			logger.error("queryUnitData", e);
		}
		return null;
	}

	/**
	 * Get Quotation Loading Data
	 */
	@RequestMapping(value = "queryQuotationLoading", method = RequestMethod.POST)
	public @ResponseBody Double
	queryQuotationLoading(Locale locale, Model model, @RequestParam(value="unitId") Integer unitId, @RequestParam(value="outletId") Integer outletId) {
		try {
			Double quotationLoading = service.queryQuotationLoading(unitId, outletId);		
			return quotationLoading;
		} catch (Exception e) {
			logger.error("queryQuotationLoading", e);
		}
		return null;
	}	
}