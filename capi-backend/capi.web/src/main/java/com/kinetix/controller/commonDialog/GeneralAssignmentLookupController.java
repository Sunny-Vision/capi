package com.kinetix.controller.commonDialog;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.masterMaintenance.BatchTableList;

@Secured({"RF9035"})
@Controller("GeneralAssignmentLookupController")
@RequestMapping("commonDialog/GeneralAssignmentLookup")
public class GeneralAssignmentLookupController {

private static final Logger logger = LoggerFactory.getLogger(UserLookupController.class);
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}
	
	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	public @ResponseBody DatatableResponseModel<BatchTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "surveyForm", required = false) String surveyForm) {

		try {
			
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
}
