package com.kinetix.controller.commonDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import capi.model.shared.quotationRecord.ProductPostModel;
import capi.service.assignmentManagement.QuotationRecordService;


/**
 * Product spec dialog
 */
@Secured({"UF1406", "UF2101", "UF2201", "RF2009", "RF2014", "UF1503", "UF1504", "UF1506", "UF1405", "UF2601", "UF2602", "UF1410"})
@Controller("ProductSpecDialogController")
@RequestMapping("commonDialog/ProductSpecDialog")
public class ProductSpecDialogController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductSpecDialogController.class);

	@Autowired
	private QuotationRecordService quotationRecordService;
	
	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, @RequestParam("productId") int productId) {
		try {
			ProductPostModel viewModel = quotationRecordService.prepareProductSpecModel(productId);
			model.addAttribute("product", viewModel);
		} catch (Exception e) {
			logger.error("getProductSpecDialog", e);
		}
	}
}