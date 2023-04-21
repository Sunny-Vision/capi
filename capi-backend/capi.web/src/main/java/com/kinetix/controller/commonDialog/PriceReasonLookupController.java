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

import capi.entity.PriceReason;
import capi.service.masterMaintenance.PriceReasonService;

/**
 * Price reason dialog
 */
@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
@Controller("PriceReasonLookupController")
@RequestMapping("commonDialog/PriceReasonLookup")
public class PriceReasonLookupController {

	private static final Logger logger = LoggerFactory.getLogger(PriceReasonLookupController.class);

	@Autowired
	private PriceReasonService service;

	/**
	 * dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.POST)
	public void home(Model model,
			@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
			@RequestParam(value = "reasonType", required = false) String reasonType) {
		try {
			List<PriceReason> list = service.getAllByType(outletTypeId, reasonType);
			model.addAttribute("list", list);
			model.addAttribute("lookuptitle", reasonType);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
}
