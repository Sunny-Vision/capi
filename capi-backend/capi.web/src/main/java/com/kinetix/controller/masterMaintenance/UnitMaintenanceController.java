package com.kinetix.controller.masterMaintenance;

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

import com.kinetix.component.FuncCode;

import capi.entity.Group;
import capi.entity.Item;
import capi.entity.OutletType;
import capi.entity.Section;
import capi.entity.SubGroup;
import capi.entity.SubItem;
import capi.entity.Unit;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.UnitCodeLookupModel;
import capi.model.masterMaintenance.UnitCommonModel;
import capi.model.masterMaintenance.UnitEditModel;
import capi.model.masterMaintenance.UnitTableList;
import capi.model.masterMaintenance.UnitTableRequestModel;
import capi.model.masterMaintenance.VarietySimpleModel;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PricingMonthService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.SubPriceService;
import capi.service.masterMaintenance.UOMCategoryService;
import capi.service.masterMaintenance.UOMConversionService;
import capi.service.masterMaintenance.UnitService;
import capi.service.masterMaintenance.UomService;
import capi.service.productMaintenance.ProductService;


/**
 * UF-1207 Unit Maintenance
 */
@Secured("UF1207")
@FuncCode("UF1207")
@Controller("UnitMaintenanceController")
@RequestMapping("masterMaintenance/UnitMaintenance")
public class UnitMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(UnitMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private UnitService service;

	@Autowired
	private UOMConversionService uomConversionService;
	
	@Autowired
	private UomService uomService;

	@Autowired
	private UOMCategoryService uomCategoryservice;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private SubPriceService subPriceService;
	
	@Autowired
	private PricingMonthService pricingMonthService;
	
	@Autowired
	private OutletService outletService;
	
	/**
	 * List
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		try {
		} catch (Exception e) {
			logger.error("home", e);
		}
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<UnitTableList>
		query(Locale locale, Model model, UnitTableRequestModel requestModel) {
		try {
			return service.getTableList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Unit item = null;
			if (id != null) {
				item = service.getUnitWithSubItem(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/UnitMaintenance/home";
				}
			} else {
				item = new Unit();
				item.setMRPS(true);
				item.setSeasonality(1);
				item.setFormDisplay(1);
				item.setStatus("Active");
			}
			
			UnitEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			model.addAttribute("E00097", messageSource.getMessage("E00097", new Object[]{"12"}, locale));
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save outlet fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute UnitEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.saveUnit(item, false);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (ServiceException e) {
			logger.error("save", e);
			if ("E00048".equals(e.getMessage()) || "E00155".equals(e.getMessage())) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
				model.addAttribute("model", item);
				model.addAttribute("E00097", messageSource.getMessage("E00097", new Object[]{"12"}, locale));
				return "/masterMaintenance/UnitMaintenance/edit";
			} else {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
			}
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/UnitMaintenance/home";
	}
	
	/**
	 * Get section by code
	 */
	@RequestMapping(value = "getSectionByCode")
	public @ResponseBody UnitCodeLookupModel getSectionByCode(@RequestParam(value = "code") String code, String basePeriod) {
		try {
			Section entity = service.getSectionByCode(code, basePeriod);
			if (entity == null) return null;
			UnitCodeLookupModel model = new UnitCodeLookupModel();
			model.setChineseName(entity.getChineseName());
			model.setEnglishName(entity.getEnglishName());
			return model;
		} catch (Exception e) {
			logger.error("getSectionByCode", e);
		}
		return null;
	}
	
	/**
	 * Get group by code
	 */
	@RequestMapping(value = "getGroupByCode")
	public @ResponseBody UnitCodeLookupModel getGroupByCode(@RequestParam(value = "code") String code, String basePeriod) {
		try {
			Group entity = service.getGroupByCode(code, basePeriod);
			if (entity == null) return null;
			UnitCodeLookupModel model = new UnitCodeLookupModel();
			model.setChineseName(entity.getChineseName());
			model.setEnglishName(entity.getEnglishName());
			return model;
		} catch (Exception e) {
			logger.error("getGroupByCode", e);
		}
		return null;
	}
	
	/**
	 * Get sub-group by code
	 */
	@RequestMapping(value = "getSubGroupByCode")
	public @ResponseBody UnitCodeLookupModel getSubGroupByCode(@RequestParam(value = "code") String code, String basePeriod) {
		try {
			SubGroup entity = service.getSubGroupByCode(code, basePeriod);
			if (entity == null) return null;
			UnitCodeLookupModel model = new UnitCodeLookupModel();
			model.setChineseName(entity.getChineseName());
			model.setEnglishName(entity.getEnglishName());
			return model;
		} catch (Exception e) {
			logger.error("getSubGroupByCode", e);
		}
		return null;
	}

	/**
	 * Get item by code
	 */
	@RequestMapping(value = "getItemByCode")
	public @ResponseBody UnitCodeLookupModel getItemByCode(@RequestParam(value = "code") String code, String basePeriod) {
		try {
			Item entity = service.getItemByCode(code, basePeriod);
			if (entity == null) return null;
			UnitCodeLookupModel model = new UnitCodeLookupModel();
			model.setChineseName(entity.getChineseName());
			model.setEnglishName(entity.getEnglishName());
			return model;
		} catch (Exception e) {
			logger.error("getSubGroupByCode", e);
		}
		return null;
	}
	
	/**
	 * Get item by code
	 */
	@RequestMapping(value = "getOutletTypeByCode")
	public @ResponseBody UnitCodeLookupModel getOutletTypeByCode(@RequestParam(value = "code") String code, String basePeriod) {
		try {
			OutletType entity = service.getOutletTypeByCode(code, basePeriod);
			if (entity == null) return null;
			UnitCodeLookupModel model = new UnitCodeLookupModel();
			model.setChineseName(entity.getChineseName());
			model.setEnglishName(entity.getEnglishName());
			return model;
		} catch (Exception e) {
			logger.error("getSubGroupByCode", e);
		}
		return null;
	}
	
	/**
	 * Get item by code
	 */
	@RequestMapping(value = "getSubItemByCode")
	public @ResponseBody UnitCodeLookupModel getSubItemByCode(@RequestParam(value = "code") String code, String basePeriod) {
		try {
			SubItem entity = service.getSubItemByCode(code, basePeriod);
			if (entity == null) return null;
			UnitCodeLookupModel model = new UnitCodeLookupModel();
			model.setChineseName(entity.getChineseName());
			model.setEnglishName(entity.getEnglishName());
			model.setCompilationMethod(entity.getCompilationMethod());
			return model;
		} catch (Exception e) {
			logger.error("getSubGroupByCode", e);
		}
		return null;
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
	 * Get single purpose
	 */
	@RequestMapping(value = "queryPurposeSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryPurposeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return purposeService.getPurposeSelectSingle(id);
			
		} catch (Exception e) {
			logger.error("queryPurposeSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get UOM select2 format
	 */
	@RequestMapping(value = "queryUomSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUomSelect2(Select2RequestModel requestModel) {
		try {
			return uomService.queryUomSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUomSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single uom
	 */
	@RequestMapping(value = "queryUomSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryUomSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return uomService.queryUomSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryUomSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get UOM Category select2 format
	 */
	@RequestMapping(value = "queryUOMCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUOMCategorySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return uomCategoryservice.queryUOMCategorySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUOMCategorySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get multiple uom category
	 */
	@RequestMapping(value = "queryUomCategorySelectByIds", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUomCategorySelectByIds(@RequestParam(value = "ids[]") Integer[] ids) {
		try {
			return uomCategoryservice.queryUomCategorySelectByIds(ids);
		} catch (Exception e) {
			logger.error("queryUomCategorySelectByIds", e);
		}
		return null;
	}

	/**
	 * Get sub-price type select format
	 */
	@RequestMapping(value = "querySubPriceTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubPriceTypeSelect2(Select2RequestModel requestModel) {
		try {
			return subPriceService.querySubPriceTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("querySubPriceTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single sub-price type
	 */
	@RequestMapping(value = "querySubPriceTypeSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String querySubPriceTypeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return subPriceService.querySubPriceTypeSelectSingle(id);
		} catch (Exception e) {
			logger.error("querySubPriceTypeSelectSingle", e);
		}
		return null;
	}

	/**
	 * Get pricing frequency select format
	 */
	@RequestMapping(value = "queryPricingFrequencySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPricingFrequencySelect2(Select2RequestModel requestModel) {
		try {
			return pricingMonthService.queryPricingFrequencySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPricingFrequencySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single pricing frequency
	 */
	@RequestMapping(value = "queryPricingFrequencySelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryPricingFrequencySelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return pricingMonthService.queryPricingFrequencySelectSingle(id);
		} catch (Exception e) {
			logger.error("queryPricingFrequencySelectSingle", e);
		}
		return null;
	}

	/**
	 * Get section select format
	 */
	@RequestMapping(value = "querySectionSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySectionSelect2(Select2RequestModel requestModel, String cpiBasePeriod) {
		try {
			return service.querySectionSelect2(requestModel, cpiBasePeriod);
		} catch (Exception e) {
			logger.error("querySectionSelect2", e);
		}
		return null;
	}

	/**
	 * Get section code
	 */
	@RequestMapping(value = "querySectionCodeSelectSingle", method = RequestMethod.GET)
	public @ResponseBody UnitCommonModel querySectionCodeSelectSingle(Integer id) {
		try {
			return service.getSectionCommonModelById(id);
		} catch (Exception e) {
			logger.error("querySectionCodeSelectSingle", e);
		}
		return null;
	}

	/**
	 * Get group select format
	 */
	@RequestMapping(value = "queryGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryGroupSelect2(Select2RequestModel requestModel, String cpiBasePeriod, String aboveCode) {
		try {
			return service.queryGroupSelect2(requestModel, cpiBasePeriod, aboveCode);
		} catch (Exception e) {
			logger.error("queryGroupSelect2", e);
		}
		return null;
	}

	/**
	 * Get group code
	 */
	@RequestMapping(value = "queryGroupCodeSelectSingle", method = RequestMethod.GET)
	public @ResponseBody UnitCommonModel queryGroupCodeSelectSingle(Integer id) {
		try {
			return service.getGroupCommonModelById(id);
		} catch (Exception e) {
			logger.error("queryGroupCodeSelectSingle", e);
		}
		return null;
	}

	/**
	 * Get group select format
	 */
	@RequestMapping(value = "querySubGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubGroupSelect2(Select2RequestModel requestModel, String cpiBasePeriod, String aboveCode) {
		try {
			return service.querySubGroupSelect2(requestModel, cpiBasePeriod, aboveCode);
		} catch (Exception e) {
			logger.error("querySubGroupSelect2", e);
		}
		return null;
	}

	/**
	 * Get sub-group code
	 */
	@RequestMapping(value = "querySubGroupCodeSelectSingle", method = RequestMethod.GET)
	public @ResponseBody UnitCommonModel querySubGroupCodeSelectSingle(Integer id) {
		try {
			return service.getSubGroupCommonModelById(id);
		} catch (Exception e) {
			logger.error("querySubGroupCodeSelectSingle", e);
		}
		return null;
	}

	/**
	 * Get item select format
	 */
	@RequestMapping(value = "queryItemSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryItemSelect2(Select2RequestModel requestModel, String cpiBasePeriod, String aboveCode) {
		try {
			return service.queryItemSelect2(requestModel, cpiBasePeriod, aboveCode);
		} catch (Exception e) {
			logger.error("queryItemSelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet type select format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel requestModel, String cpiBasePeriod, String aboveCode) {
		try {
			return service.queryOutletTypeSelect2(requestModel,cpiBasePeriod, aboveCode);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}

	/**
	 * Get sub-item select format
	 */
	@RequestMapping(value = "querySubItemSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubItemSelect2(Select2RequestModel requestModel, String cpiBasePeriod, String aboveCode) {
		try {
			return service.querySubItemSelect2(requestModel, cpiBasePeriod, aboveCode);
		} catch (Exception e) {
			logger.error("querySubItemSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single item
	 */
	@RequestMapping(value = "queryItemSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryItemSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			Item entity = service.getItemById(id);
			if (entity == null)
				return null;
			else
				return entity.getCode() + " - " + entity.getChineseName();
		} catch (Exception e) {
			logger.error("queryItemSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get single item code
	 */
	@RequestMapping(value = "queryItemCodeSelectSingle", method = RequestMethod.GET)
	public @ResponseBody UnitCommonModel queryItemCodeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return service.getItemCommonModelById(id);
			
		} catch (Exception e) {
			logger.error("queryItemCodeSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get single item
	 */
	@RequestMapping(value = "queryOutletTypeSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryOutletTypeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			OutletType entity = service.getOutletTypeById(id);
			if (entity == null)
				return null;
			else
				return entity.getCode() + " - " + entity.getChineseName();
		} catch (Exception e) {
			logger.error("queryOutletTypeSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get single item
	 */
	@RequestMapping(value = "queryOutletTypeCodeSelectSingle", method = RequestMethod.GET)
	public @ResponseBody UnitCommonModel queryOutletTypeCodeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return service.getOutletTypeCommonModelById(id);
		} catch (Exception e) {
			logger.error("queryOutletTypeCodeSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get single sub-item
	 */
	@RequestMapping(value = "querySubItemSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String querySubItemSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			SubItem entity = service.getSubItemById(id);
			if (entity == null)
				return null;
			else
				return entity.getCode() + " - " + entity.getChineseName();
		} catch (Exception e) {
			logger.error("querySubItemSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get single sub-item
	 */
	@RequestMapping(value = "querySubItemCodeSelectSingle", method = RequestMethod.GET)
	public @ResponseBody UnitCommonModel querySubItemCodeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return service.getSubItemCommonModelById(id);
		} catch (Exception e) {
			logger.error("querySubItemCodeSelectSingle", e);
		}
		return null;
	}

	/**
	 * Get Product Group select2 format
	 */
	@RequestMapping(value = "queryProductGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryProductGroupSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return productService.queryProductGroupSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryProductGroupSelect2", e);
		}
		return null;
	}

	/**
	 * Get single product group
	 */
	@RequestMapping(value = "queryProductGroupSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryProductGroupSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return productService.queryProductGroupSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryProductGroupSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get section code by group code
	 */
	@RequestMapping(value = "getSectionCodeByGroupCode", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String getSectionCodeByGroupCode(String code, String cpiBasePeriod) {
		try {
			return service.getSectionCodeByGroupCode(code, cpiBasePeriod);
		} catch (Exception e) {
			logger.error("getSectionCodeByGroupCode", e);
		}
		return null;
	}

	/**
	 * Get group code by sub-group code
	 */
	@RequestMapping(value = "getGroupCodeBySubGroupCode", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String getGroupCodeBySubGroupCode(String code, String cpiBasePeriod) {
		try {
			return service.getGroupCodeBySubGroupCode(code, cpiBasePeriod);
		} catch (Exception e) {
			logger.error("getGroupCodeBySubGroupCode", e);
		}
		return null;
	}

	/**
	 * Get sub-group code by item code
	 */
	@RequestMapping(value = "getSubGroupCodeByItemCode", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String getSubGroupCodeByItemCode(String code, String cpiBasePeriod) {
		try {
			return service.getSubGroupCodeByItemCode(code, cpiBasePeriod);
		} catch (Exception e) {
			logger.error("getSubGroupCodeByItemCode", e);
		}
		return null;
	}

	/**
	 * Get item code by outlet-type code
	 */
	@RequestMapping(value = "getItemCodeByOutletTypeCode", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String getItemCodeByOutletTypeCode(String code, String cpiBasePeriod) {
		try {
			return service.getItemCodeByOutletTypeCode(code, cpiBasePeriod);
		} catch (Exception e) {
			logger.error("getItemCodeByOutletTypeCode", e);
		}
		return null;
	}

	/**
	 * Get outlet-type code by sub-item code
	 */
	@RequestMapping(value = "getOutletTypeCodeBySubItemCode", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String getOutletTypeCodeBySubItemCode(String code, String cpiBasePeriod) {
		try {
			return service.getOutletTypeCodeBySubItemCode(code, cpiBasePeriod);
		} catch (Exception e) {
			logger.error("getOutletTypeCodeBySubItemCode", e);
		}
		return null;
	}
	
	@RequestMapping(value = "getItemDetailByIds")
	public @ResponseBody List<VarietySimpleModel> getItemDetailByIds(Integer[] ids){
		try {
			return service.getItemDetailByIds(ids);
		} catch (Exception e) {
			logger.error("getItemDetailByIds", e);
		}
		return null;		
	}
	
	@RequestMapping(value = "getSubItemDetailByIds")
	public @ResponseBody List<VarietySimpleModel> getSubItemDetailByIds(Integer[] ids){
		try {
			return service.getSubItemDetailByIds(ids);
		} catch (Exception e) {
			logger.error("getItemDetailByIds", e);
		}
		return null;
	}
	
	// for home.jsp
	@RequestMapping(value = "getOutletTypesSelect2")
	public @ResponseBody Select2ResponseModel getOutletTypesSelect2(Select2RequestModel requestModel){
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}
	
	@RequestMapping(value = "queryCPIBasePeriodSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryCPIBasePeriodSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return service.queryCPIBasePeriodSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryCPIBasePeriodSelect2", e);
		}
		return null;
	}
	
}