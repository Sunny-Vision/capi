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
 * Compare product dialog
 */
@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2102", "UF2101", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
@Controller("CompareProductDialogController")
@RequestMapping("commonDialog/CompareProductDialog")
public class CompareProductDialogController {

	private static final Logger logger = LoggerFactory.getLogger(CompareProductDialogController.class);

	@Autowired
	private QuotationRecordService service;
	
	/**
	 * dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home() {}

	/**
	 * body
	 */
	@RequestMapping(value = "body", method = RequestMethod.GET)
	public void body(Model model, @RequestParam("quotationRecordId") int quotationRecordId,
			@RequestParam("historyQuotationRecordId") int historyQuotationRecordId) {
		try {
			ProductPostModel current = service.prepareProductViewModel(quotationRecordId);
			model.addAttribute("current", current);
			
			ProductPostModel history = service.prepareProductViewModel(historyQuotationRecordId);
			model.addAttribute("history", history);
		} catch (Exception e) {
			logger.error("body");
		}
	}
}
