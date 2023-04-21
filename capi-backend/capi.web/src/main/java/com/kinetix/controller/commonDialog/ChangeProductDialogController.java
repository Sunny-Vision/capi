package com.kinetix.controller.commonDialog;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.QuotationRecord;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.ChangeProductViewModel;
import capi.model.shared.quotationRecord.CheckProductChangeResultModel;
import capi.model.shared.quotationRecord.ProductPostModel;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.productMaintenance.ProductService;

/**
 * Change product dialog
 */
@Secured({"UF1402", "UF1405", "UF2601", "RF2003", "UF2101", "UF2102", "UF2103", "UF2201"})
@Controller("ChangeProductDialogController")
@RequestMapping("commonDialog/ChangeProductDialog")
public class ChangeProductDialogController {

	private static final Logger logger = LoggerFactory.getLogger(ChangeProductDialogController.class);
	
	@Autowired
	private QuotationRecordService service;
	
	@Autowired
	private ProductService productService;
	
	/**
	 * dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {}
	
	/**
	 * body
	 */
	@RequestMapping(value = "body", method = RequestMethod.GET)
	public void body(Model model, @RequestParam("productId") Integer productId,
			@RequestParam("productGroupId") Integer productGroupId) {
		try {
			ChangeProductViewModel viewModel = service.prepareChangeProductViewModel(productId, productGroupId);
			model.addAttribute("model", viewModel);
		} catch (Exception e) {
			logger.error("home", e);
		}
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
			return productService.queryProdAttrValueSelect2(productGroupId, productAttributeId, requestModel);
		} catch (Exception e) {
			logger.error("queryProdAttrValueSelect2", e);
		}
		return null;
	}
	
	/**
	 * Check product change
	 */
	@RequestMapping(value = "checkProductChange", method = RequestMethod.POST)
	public @ResponseBody CheckProductChangeResultModel checkProductChange(ProductPostModel requestModel, @RequestParam("quotationRecordId") Integer quotationRecordId) {
		try {
			QuotationRecord record = service.getQuotationRecordById(quotationRecordId);
			CheckProductChangeResultModel resultModel = service.checkProductChange(requestModel, record.getProduct(), record.getQuotation().getUnit().getProductCategory());
			return resultModel;
		} catch (Exception e) {
			logger.error("checkProductChange", e);
		}
		return null;
	}
}
