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
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.UserLookupTableList;
import capi.service.lookup.UserLookupService;


/**
 * User lookup
 */
@Secured({"UF1301", "UF1302", "UF1303", "UF1501", "UF1503", "UF1506", "UF2102", "UF2103", "RF2001", "RF2005", "RF2006", "RF9003", "RF9039", "RF9040", "RF9045", "RF9046"})
@Controller("UserLookupController")
@RequestMapping("commonDialog/UserLookup")
public class UserLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserLookupController.class);

	@Autowired
	private UserLookupService service;

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
				Boolean teamOnly, Boolean withSelf, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer selfLevel = detail.getAuthorityLevel();
			String userame = auth.getName();						
			return service.getLookupTableList(requestModel, authorityLevel, teamOnly, selfLevel, userame, withSelf);
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
			Boolean teamOnly, Boolean withSelf, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer selfLevel = detail.getAuthorityLevel();
			String userame = auth.getName();						
			List<Integer> list = service.getLookupTableSelectAll(search, authorityLevel, teamOnly, userame, withSelf, selfLevel);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
}