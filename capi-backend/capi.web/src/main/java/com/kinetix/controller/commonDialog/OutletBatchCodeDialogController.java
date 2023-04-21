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
import org.springframework.web.bind.annotation.RequestParam;

import capi.service.masterMaintenance.OutletService;


/**
 * Outlet batch code dialog
 */
@Secured({"UF2602"})
@Controller("OutletBatchCodeDialogController")
@RequestMapping("commonDialog/OutletBatchCodeDialog")
public class OutletBatchCodeDialogController {
	
	private static final Logger logger = LoggerFactory.getLogger(OutletBatchCodeDialogController.class);

	@Autowired
	private OutletService service;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, @RequestParam("outletId") int outletId) {
		try {
			List<String> codes = service.getBatchCodes(outletId);
			model.addAttribute("codes", codes);	
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
}