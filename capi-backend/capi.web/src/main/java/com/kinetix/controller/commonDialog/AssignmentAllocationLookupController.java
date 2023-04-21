package com.kinetix.controller.commonDialog;

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

import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;

@Secured({"UF1503"})
@Controller("AssignmentAllocationLookupController")
@RequestMapping("commonDialog/AssignmentAllocationLookup")
public class AssignmentAllocationLookupController {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentAllocationLookupController.class);

	@Autowired
	private OutletService outletService;
	
	@Autowired
	private TpuService tpuService;
	
	@Autowired
	private DistrictService districtService;

	@Autowired
	private BatchService batchService;

	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home() {
	}

	/**
	 * Get tpu select2 format
	 */
	@RequestMapping(value = "queryTpuSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSelect2(Locale locale, Model model, Select2RequestModel requestModel,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId) {
		try {
			return tpuService.queryTpuSelect2(requestModel, districtId);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet type select2 format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOutletTypeSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get batch select2 format
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryBatchSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return batchService.queryBatchSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}
}
