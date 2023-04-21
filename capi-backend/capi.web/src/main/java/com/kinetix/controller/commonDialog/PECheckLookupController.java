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

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.PECheckLookupTableList;
import capi.service.lookup.PECheckLookupService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;


/**
 * Post-Enumeration Check lookup
 */
@Secured("UF1703")
@Controller("PECheckLookupController")
@RequestMapping("commonDialog/PECheckLookup")
public class PECheckLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(PECheckLookupController.class);

	@Autowired
	private PECheckLookupService service;

	@Autowired
	private OutletService outletService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private TpuService tpuService;

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
	public @ResponseBody DatatableResponseModel<PECheckLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId, 
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId, 
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "excludedPEFormIds[]", required = false)Integer[] excludedPEFormIds,
				@RequestParam(value = "userId", required = false)Integer userId) {
		try {
		
			return service.getLookupTableList(requestModel, outletTypeId, districtId, tpuId, excludedPEFormIds, userId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Get outlet type select format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}

	/**
	 * Get district select format
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
	 * Get batch
	 */
	@RequestMapping(value = "queryTpuSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryBatchSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return tpuService.queryTpuSelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("queryTpuSelect2", e);
		}
		return null;
	}

}