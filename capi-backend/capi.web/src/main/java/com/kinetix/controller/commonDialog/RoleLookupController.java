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
import capi.model.commonLookup.RoleLookupTableList;
import capi.service.lookup.RoleLookupService;


/**
 * Role lookup
 */
@Secured("UF1301")
@Controller("RoleLookupController")
@RequestMapping("commonDialog/RoleLookup")
public class RoleLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(RoleLookupController.class);

	@Autowired
	private RoleLookupService service;

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
	public @ResponseBody DatatableResponseModel<RoleLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.getLookupTableList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
}