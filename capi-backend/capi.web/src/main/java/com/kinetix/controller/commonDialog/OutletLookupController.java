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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.masterMaintenance.OutletTableList;
import capi.service.lookup.OutletLookupService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;


/**
 * Outlet lookup
 */
@Secured({"UF1118", "UF1401", "UF1409", "UF2201", "UF2102", "UF2101"})
@Controller("OutletLookupController")
@RequestMapping("commonDialog/OutletLookup")
public class OutletLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(OutletLookupController.class);

	@Autowired
	private OutletLookupService service;

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
		try {
			List<VwOutletTypeShortForm> outletTypes = outletService.getOutletTypes();
			model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<OutletTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "activeOutlet", required = false) String activeOutlet,
				@RequestParam(value = "name", required = false) String name,
				@RequestParam(value = "tel", required = false) String tel) {
		try {
			return service.getLookupTableList(requestModel, outletTypeId, districtId, tpuId, activeOutlet, name, tel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search,
			@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
			@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
			@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
			@RequestParam(value = "activeOutlet", required = false) String activeOutlet) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search, outletTypeId, districtId, tpuId, activeOutlet);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
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
	
	@RequestMapping(value = "queryOutletTypeSelect2")
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel queryModel){
		try{
			return outletService.queryOutletTypeSelect2(queryModel);
		}
		catch(Exception ex){
			logger.error("queryOutletTypeSelect2", ex);
		}
		return null;
	}
}
