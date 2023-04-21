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
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.SystemFunctionLookupTableList;
import capi.service.lookup.SystemFunctionLookupService;


/**
 * System Function lookup
 */
@Secured("UF1304")
@Controller("SystemFunctionLookupController")
@RequestMapping("commonDialog/SystemFunctionLookup")
public class SystemFunctionLookupController {

	private static final Logger logger = LoggerFactory.getLogger(SystemFunctionLookupController.class);

	@Autowired
	private SystemFunctionLookupService service;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<SystemFunctionLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Boolean isMobile) {
		try {
			return service.getLookupTableList(requestModel, isMobile);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search, Boolean isMobile) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search, isMobile);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}

}
