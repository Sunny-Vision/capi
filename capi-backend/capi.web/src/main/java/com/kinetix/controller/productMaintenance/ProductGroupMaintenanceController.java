package com.kinetix.controller.productMaintenance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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

import capi.entity.ProductAttribute;
import capi.entity.ProductGroup;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.productMaintenance.ProductAttributeModel;
import capi.model.productMaintenance.ProductGroupEditModel;
import capi.model.productMaintenance.ProductGroupTableList;
import capi.service.productMaintenance.ProductService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1208")
@FuncCode("UF1208")
@Controller("ProductGroupMaintenanceController")
@RequestMapping("productMaintenance/ProductGroupMaintenance")
public class ProductGroupMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(ProductGroupMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ProductService service;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model) {
	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ProductGroupTableList> query(Locale locale, Model model,
			DatatableRequestModel requestModel) {
		try {
			return service.queryProductGroup(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Check Uniqueness of Product Group Code
	 * 
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "checkUnique", method = RequestMethod.POST)
	public String checkUnique(@RequestParam(value = "code", required = false) String code, HttpServletResponse response)
			throws IOException {
		try {
			ProductGroup productGroup = service.getProductGroupByCode(code);
			if (productGroup == null) {
				response.getWriter().write("true");
			} else {
				response.getWriter().write("false");
			}
		} catch (Exception e) {
			logger.error("checkUnique", e);
		}
		return null;
	}
	
	/**
	 * Check Product Attribute in-use
	 * 
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "attributeInUse", method = RequestMethod.POST)
	public String checkUnique(@RequestParam("productAttributeId") Integer productAttributeId, HttpServletResponse response){
		try {
			long noOfProdSpec = service.countProductSpecByProductAttrId(productAttributeId);
			if (noOfProdSpec > 0) {
				response.getWriter().write("true");
			} else {
				response.getWriter().write("false");
			}
		} catch (Exception e) {
			logger.error("attributeInUse", e);
		}
		return null;
	}

	/**
	 * Edit Product Group
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			ProductGroupEditModel item = null;
			List<ProductAttribute> attributes = new ArrayList<ProductAttribute>();
			if (id != null) {
				model.addAttribute("act", "edit");
				item = service.getProductGroupEditModelById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/productMaintenance/ProductGroupMaintenance/home";
				}
				attributes = service.getProductAttributesByProductGroupId(id);
			} else {
				model.addAttribute("act", "add");
				item = new ProductGroupEditModel();
			}

			ObjectMapper objectMapper = new ObjectMapper();
			String json = "";
			ArrayList<ProductAttributeModel> productAttributes = new ArrayList<ProductAttributeModel> ();
			if(attributes.size() > 0) {
				for(int i = 0; i < attributes.size(); i++) {
					ProductAttribute attribute = attributes.get(i);
					ProductAttributeModel productAttribute = new ProductAttributeModel();
					
					productAttribute.setIndex(i);
					productAttribute.setProductAttributeId(attribute.getProductAttributeId());
					productAttribute.setSequence(attribute.getSequence());
					if(attribute.getSpecificationName() != null)
						productAttribute.setSpecificationName(attribute.getSpecificationName());
					else
						productAttribute.setSpecificationName("");
					productAttribute.setIsMandatory(attribute.getIsMandatory() ? "checked" : "");
					productAttribute.setAttributeType(attribute.getAttributeType());
					if(attribute.getOption() != null)
						productAttribute.setOption(attribute.getOption());
					else
						productAttribute.setOption("");
					
					productAttributes.add(productAttribute);
				}
				json = objectMapper.writeValueAsString(productAttributes);
			}
			
			model.addAttribute("model", item);
			model.addAttribute("attributes", attributes);
			model.addAttribute("productAttributes", json);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save Product Group
	 */

	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute ProductGroupEditModel productGroupModel, Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			if (productGroupModel.getProductGroupId() != null) {
				if (service.getProductGroupById(productGroupModel.getProductGroupId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/productMaintenance/ProductGroupMaintenance/home";
				}
			} 

			if (!service.saveProductGroup(productGroupModel)) {
				
				model.addAttribute("act", "add");
				model.addAttribute("model",productGroupModel);
				model.addAttribute("attributes", new ArrayList<ProductAttribute>(productGroupModel.getProductAttributes().values()));
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00049", null, locale));
				return "/productMaintenance/ProductGroupMaintenance/edit";
			}

			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/productMaintenance/ProductGroupMaintenance/home";
	}

	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> ids, Model model, Locale locale) {
		try {
			if (!service.deleteProductGroup(ids)) {
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
}
