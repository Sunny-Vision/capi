package com.kinetix.controller.commonDialog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.commonLookup.DiscountFormulaLookupModel;
import capi.model.masterMaintenance.DiscountCalculatorItemsList;
import capi.service.lookup.DiscountCalculatorLookupService;
import capi.service.masterMaintenance.DiscountFormulaService;

/**
 * Discount calculator
 */
@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2102", "UF2101", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
@Controller("DiscountCalculatorController")
@RequestMapping("commonDialog/DiscountCalculator")
public class DiscountCalculatorController {

	private static final Logger logger = LoggerFactory.getLogger(DiscountCalculatorController.class);
	
	@Autowired
	private DiscountCalculatorLookupService service;
	
	@Autowired
	private DiscountFormulaService discountFormulaService;
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		try {
			DiscountCalculatorItemsList itemlist = discountFormulaService.getDiscountItems();
			model.addAttribute("itemlist", itemlist);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}

	/**
	 * Get all enabled formula
	 */
	@RequestMapping(value = "getAllEnabledFormula", method = RequestMethod.GET)
	public @ResponseBody List<DiscountFormulaLookupModel> getAllEnabledFormula() {
		try {
			List<DiscountFormulaLookupModel> list = service.getAllEnabledFormula();
			return list;
		} catch (Exception e) {
			logger.error("getAllEnabledFormula", e);
		}
		return null;
	}
}
