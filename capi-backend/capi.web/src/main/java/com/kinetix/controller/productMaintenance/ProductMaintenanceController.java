package com.kinetix.controller.productMaintenance;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.ProductGroup;
import capi.entity.Product;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.productMaintenance.ProductEditModel;
import capi.model.productMaintenance.ProductSpecificationEditModel;
import capi.model.productMaintenance.ProductTableList;
import capi.service.AppConfigService;
import capi.service.productMaintenance.ProductService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1209")
@FuncCode("UF1209")
@Controller("ProductMaintenanceController")
@RequestMapping("productMaintenance/ProductMaintenance")
public class ProductMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(ProductMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private ProductService service;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model, @RequestParam(value = "productGroupId", required=false) Integer productGroupId) {
		if (productGroupId != null && productGroupId > 0 ) {
			model.addAttribute("productGroupId", productGroupId);
			ProductGroup productGroup = service.getProductGroupById(productGroupId);
			model.addAttribute("productGroupText", service.getProductGroupSelectText(productGroup));
		}
	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ProductTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.queryProduct(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Get Product Group select2 format
	 */
	@RequestMapping(value = "queryProductGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryProductGroupSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return service.queryProductGroupSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryProductGroupSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Single Product Group
	 */
	@RequestMapping(value = "queryProductGroupSelectSingle", method = RequestMethod.GET)
	public @ResponseBody String queryProductGroupSingle(@RequestParam(value = "id") Integer id){
		try{
			return service.queryProductGroupSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryProductGroupSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get Product Attribute Value select2 format
	 */
	@RequestMapping(value = "queryProdAttrValueSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryProdAttrValueSelect2(Locale locale, Model model, Select2RequestModel requestModel,
			@RequestParam("productGroupId") Integer productGroupId,
			@RequestParam("productAttributeId") Integer productAttributeId) {
		try {
			return service.queryProdAttrValueSelect2(productGroupId, productAttributeId, requestModel);
		} catch (Exception e) {
			logger.error("queryProdAttrValueSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Product Specification List format
	 */
	@RequestMapping(value = "queryProductSpecificationList", method = RequestMethod.GET)
	public @ResponseBody List<ProductSpecificationEditModel>
	queryProductSpecificationList(Locale locale, Model model, @RequestParam(value="productId", required = false) Integer productId, @RequestParam("productGroupId") Integer productGroupId) {
		try {
			
			return service.getProductSpecificationListByIds(productId, productGroupId);
		} catch (Exception e) {
			logger.error("queryProductSpecificationList", e);
		}
		return null;
	}
	
	/**
	 * Get Product Attribute List format
	 */
	@RequestMapping(value = "queryProductAttributeList", method = RequestMethod.GET)
	public @ResponseBody List<ProductSpecificationEditModel>
	queryProductAttributeList(Locale locale, Model model, @RequestParam("productGroupId") Integer productGroupId) {
		try {
			
			return service.getProductAttributeByProductSpecificationId(productGroupId);
		} catch (Exception e) {
			logger.error("queryProductAttributeList", e);
		}
		return null;
	}
	
	/**
	 * Check Uniqueness of Product Group Code
	 * @throws IOException 
	 */
	/*
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "checkUnique", method = RequestMethod.POST)
	public String checkUnique( @RequestParam(value = "code", required = false) String code, HttpServletResponse response) throws IOException {
		ProductGroup productGroup = service.getProductGroupByCode(code);
		if (productGroup == null) {
			response.getWriter().write("true");
		} else {
			response.getWriter().write("false");
		}
		return null;
	}
	*/
	
	/**
	 * Edit Product 
	*/
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			ProductEditModel item = null;
			if (id != null && id > 0) {
				model.addAttribute("act", "edit");
				item = service.getProductEditModelById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/productMaintenance/ProductMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new ProductEditModel();
			}
			
			model.addAttribute("model", item);
//			model.addAttribute("countryList", service.getProductCountryList());
			
			String countryListString = service.getProductCountryList();
			String[] countryListArray = countryListString.split(";");
			
			ObjectMapper objectMapper = new ObjectMapper();
			String country = "";
			if(StringUtils.isEmpty(item.getCountryOfOrigin())) {
				country = objectMapper.writeValueAsString("");
			} else {
				country = objectMapper.writeValueAsString(item.getCountryOfOrigin());
			}
			String countrtList = objectMapper.writeValueAsString(countryListArray);
			
			model.addAttribute("country", country);
			model.addAttribute("countryList", countrtList);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save Product 
	*/
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@ModelAttribute ProductEditModel productEditModel,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes,
			@RequestParam("photo1") MultipartFile photo1,
			@RequestParam("photo2") MultipartFile photo2) {
		try {
			if (productEditModel.getProductId() != null) {
				if (!service.checkProductById(productEditModel.getProductId())) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/productMaintenance/ProductMaintenance/home";
				}
			}
			
			boolean invalidImage = false;
			
			if (photo1 != null && !photo1.isEmpty()) {
				if (!photo1.getContentType().contains("image")) {
					invalidImage = true;
				}
			}
			
			if (photo1 != null && !photo1.isEmpty()) {
				if (!photo1.getContentType().contains("image")) {
					invalidImage = true;
				}
			}
			
			if (invalidImage) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
				return "/productMaintenance/ProductMaintenance/edit";
			}
			
			
			service.saveProduct(productEditModel, photo1.getInputStream(), photo2.getInputStream(), configService.getFileBaseLoc());
		
			redirectAttributes.addFlashAttribute(
					SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
		}
		return "redirect:/productMaintenance/ProductMaintenance/home";
	}

	
	/**
	 * Delete
	*/
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> ids, Model model, Locale locale) {
		try {
			if (!service.deleteProduct((List<Integer>)ids, configService.getFileBaseLoc())) {
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
	 * Download image
	 */
	@RequestMapping(value = "getImage", method = RequestMethod.GET)
	public void getImage(@RequestParam("productId") int productId, @RequestParam("photoIndex") int photoIndex, HttpServletResponse response) {
		try {
			Product entity = service.getProductById(productId);
			String imagePath = null;
			if (photoIndex == 2) {
				imagePath = entity.getPhoto2Path();
			} else {
				imagePath = entity.getPhoto1Path();
			}
			File imageFile = new File(configService.getFileBaseLoc() + imagePath);
			if (imageFile.exists()) {
				//response.setContentType("image/png");	
		        response.setContentLength((int)imageFile.length());		        
		        response.setHeader("Content-Disposition","attachment; filename=\""+imageFile.getName()+"\"");
		 
		        FileCopyUtils.copy(new FileInputStream(imageFile), response.getOutputStream());	
			}
		} catch (Exception e) {
			logger.error("getImage", e);
		}
	}
//	public ResponseEntity<InputStreamResource> getImage(@RequestParam("productId") int productId, @RequestParam("photoIndex") int photoIndex) {
//		try {
//			Product entity = service.getProductById(productId);
//			String imagePath = null;
//			if (photoIndex == 2) {
//				imagePath = entity.getPhoto2Path();
//			} else {
//				imagePath = entity.getPhoto1Path();
//			}
//			File imageFile = new File(configService.getFileBaseLoc() + imagePath);
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getImage", e);
//		}
//		return null;
//	}
	
	/**
	 * Product Cleaning
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "cleaning", method = RequestMethod.GET)
	public String cleaning(@RequestParam(value = "id", required = false) Integer id, 
			Locale locale, Model model, RedirectAttributes redirectAttributes) {
		try {
			ProductEditModel item = null;
			
			if (id != null && id > 0) {
				model.addAttribute("act", "cleaning");
				item = service.getProductEditModelById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/productMaintenance/ProductMaintenance/home";
				}
			}
			
			model.addAttribute("model", item);
			//model.addAttribute("specifications", specifications);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Product Cleaning Save
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "cleaningSave", method = RequestMethod.GET)
	public String cleaningSave(Locale locale, Model model, 
			RedirectAttributes redirectAttributes, 
			@RequestParam("productId") Integer productId, 
			@RequestParam("selectedProductId[]") Integer[] replaceProductIds) {

		try {
			
			service.productCleaning(productId, replaceProductIds);
		
			redirectAttributes.addFlashAttribute(
					SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/productMaintenance/ProductMaintenance/home";
	}
	
	
	/**
	 * Product Bulk Update
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "bulk", method = RequestMethod.GET)
	public String bulk(@RequestParam(value = "id", required = false) Integer id,
			Locale locale, Model model, RedirectAttributes redirectAttributes) {

		ProductEditModel item = null;
		model.addAttribute("act", "edit");
		item = service.getProductEditModelById(id);
		if (item == null) {
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
			return "redirect:/productMaintenance/ProductMaintenance/home";
		}
		model.addAttribute("model", item);
		return null;
	}
	
	/**
	 * Product Bulk Update Save
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "bulkSave", method = RequestMethod.GET)
	public String bulkSave(@RequestParam(value = "id", required = false) Integer id, 
			Locale locale, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam(value = "productAttributeId", required = false) Integer productAttributeId, 
			@RequestParam(value = "selectedProductId[]", required = false) Integer[] productIds,
			@RequestParam(value = "attributeValue", required = false) String value) {

		try {
			
			service.productBulkUpdate(Arrays.asList(productIds), productAttributeId, value);
		
			redirectAttributes.addFlashAttribute(
					SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
			
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/productMaintenance/ProductMaintenance/home";
	}

}
