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
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.DiscountFormula;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.service.masterMaintenance.DiscountFormulaService;

/**
 * Discount formula lookup
 */
@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
@Controller("DiscountFormulaLookupController")
@RequestMapping("commonDialog/DiscountFormulaLookup")
public class DiscountFormulaLookupController {

	private static final Logger logger = LoggerFactory.getLogger(DiscountFormulaLookupController.class);

	@Autowired
	private DiscountFormulaService service;
	
	/**
	 * dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<DiscountFormula>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getDiscountFormulaList(requestModel, "Enable");
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
}
