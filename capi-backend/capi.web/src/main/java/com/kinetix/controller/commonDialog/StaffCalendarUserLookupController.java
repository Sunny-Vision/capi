package com.kinetix.controller.commonDialog;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.UserLookupTableList;
import capi.service.UserService;
import capi.service.lookup.StaffCalendarUserLookupService;


/**
 * Staff Calendar User lookup
 */
@Secured({"UF1303"})
@Controller("StaffCalendarUserLookupController")
@RequestMapping("commonDialog/StaffCalendarUserLookup")
public class StaffCalendarUserLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(StaffCalendarUserLookupController.class);

	@Autowired
	private StaffCalendarUserLookupService service;

	@Autowired
	private UserService userService;

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
	public @ResponseBody DatatableResponseModel<UserLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Integer authorityLevel, 
				Boolean teamOnly, Boolean withSelf, Authentication auth,
				@RequestParam(value = "ghs", required = false) String ghs,
				@RequestParam(value = "team", required = false) String team) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer selfLevel = detail.getAuthorityLevel();
			String userame = auth.getName();						
			return service.getStaffCalendarLookupTableList(requestModel, authorityLevel, teamOnly, selfLevel, userame, withSelf, ghs, team);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search, Integer authorityLevel, 
			Boolean teamOnly, Boolean withSelf, Authentication auth,
			@RequestParam(value = "ghs", required = false) String ghs,
			@RequestParam(value = "team", required = false) String team) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer selfLevel = detail.getAuthorityLevel();
			String userame = auth.getName();						
			List<Integer> list = service.getStaffCalendarLookupTableSelectAll(search, authorityLevel, teamOnly, userame, withSelf, selfLevel, ghs, team);
			return list;
		} catch (Exception e) {
			logger.error("getStaffCalendarLookupTableSelectAll", e);
		}
		return null;
	}

	/**
	 * Get team select2 format
	 */
	@RequestMapping(value = "queryTeamSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryTeamSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return userService.queryTeamSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryTeamSelect2", e);
		}
		return null;
	}
}