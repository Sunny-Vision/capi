package com.kinetix.controller.commonDialog;

import java.util.ArrayList;
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
import capi.service.lookup.SystemFunctionLookupService;


/**
 * Authority lookup
 */
@Secured("UF1304")
@Controller("AuthorityLookupController")
@RequestMapping("commonDialog/AuthorityLookup")
public class AuthorityLookupController {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityLookupController.class);

	public class AuthorityCheckbox {
		private Integer authorityLevel;
		public Integer getAuthorityLevel() {
			return authorityLevel;
		}
		public void setAuthorityLevel(Integer authorityLevel) {
			this.authorityLevel = authorityLevel;
		}
		public boolean hasAuthority(int auth){
			return this.getAuthorityLevel() != null && (this.getAuthorityLevel() & auth) == auth;
		}
	}

	@Autowired
	private SystemFunctionLookupService service;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, @RequestParam(value = "selectedIds[]", required = false) Integer[] ids) {
		Integer authorityLevel = 0;
		if (ids !=null){
			for(int i = 0; i < ids.length; i++) {
				if(ids[i] != null) {
					authorityLevel += ids[i];
				}
			}
		}
		AuthorityCheckbox checkboxChecking = new AuthorityCheckbox();
		checkboxChecking.setAuthorityLevel(authorityLevel);
		model.addAttribute("checkboxChecking", checkboxChecking);
	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	public @ResponseBody /*DatatableResponseModel<SystemFunctionLookupTableList>*/String
		query(Locale locale, Model model, DatatableRequestModel requestModel, @RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			System.out.println("test");
//			return service.getLookupTableList(requestModel);
			return "Success";
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
			List<Integer> list = new ArrayList<Integer> ();
			for(int i = 1; i <= 2048; i *= 2) {
				list.add(new Integer(i));
			}
			return list;
//			List<Integer> list = service.getLookupTableSelectAll(search, isMobile);
//			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}

}
