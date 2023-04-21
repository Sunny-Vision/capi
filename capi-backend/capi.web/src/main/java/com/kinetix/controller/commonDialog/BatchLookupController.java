package com.kinetix.controller.commonDialog;

import java.util.List;
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
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.masterMaintenance.BatchTableList;
import capi.service.masterMaintenance.BatchService;


/**
 * Batch lookup
 */
@Secured({"UF1301","RF9002","RF9003", "RF9004"})
@Controller("BatchLookupController")
@RequestMapping("commonDialog/BatchLookup")
public class BatchLookupController {
	
	@Autowired
	private BatchService service;

	private static final Logger logger = LoggerFactory.getLogger(UserLookupController.class);
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}
	
	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<BatchTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "surveyForm[]", required = false) String[] surveyForm) {

		try {
			return service.getBatchList(requestModel, surveyForm);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	
	/**
	 * Get survey form
	 */
	@RequestMapping(value = "querySurveyFormSelect", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySurveyFormSelect(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return service.querySureyFormSelect(requestModel);
		} catch (Exception e) {
			logger.error("querySureyFormSelect", e);
		}
		return null;
	}
	
	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search, String surveyForm) {
		try {					
			List<Integer> list = service.getLookupTableSelectAll(search, surveyForm);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
}
