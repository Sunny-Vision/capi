package com.kinetix.controller.commonDialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import capi.model.masterMaintenance.SubPriceComparisonModel;
import capi.service.masterMaintenance.SubPriceService;

@Secured({"UF2101","UF2201"})
@Controller("SubPriceDetailsController")
@RequestMapping("commonDialog/SubPriceDetails")
public class SubPriceDetailsController {
	
	@Autowired
	private SubPriceService subPriceService;

	@RequestMapping("home")
	public void home (Model model, Integer quotationRecordId1){
		
		SubPriceComparisonModel record = subPriceService.prepareSubPriceDetailsModel(quotationRecordId1);
		model.addAttribute("model", record);
	}
	
}
