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

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.QuotationRecordHistoryTableListModel;
import capi.service.dataConversion.QuotationRecordDataConversionService;


/**
 * Quotation Record History Dialog
 */
@Secured({"UF2201", "UF2101"})
@Controller("QuotationRecordHistoryDialogController")
@RequestMapping("commonDialog/QuotationRecordHistoryDialog")
public class QuotationRecordHistoryDialogController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotationRecordHistoryDialogController.class);

	@Autowired
	private QuotationRecordDataConversionService service;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, @RequestParam(value = "indoorQuotationRecordId", required = true) Integer indoorQuotationRecordId) {
		try {
//			QuotationRecordHistoryStatisticModel statModel = this.service.calculateQuotationRecordHistoryStat(indoorQuotationRecordId);
//			
//			model.addAttribute("statModel", statModel);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<QuotationRecordHistoryTableListModel>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "indoorQuotationRecordId", required = true) Integer indoorQuotationRecordId) {
		try {
			return service.queryHistoryDialogList(requestModel, indoorQuotationRecordId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
}