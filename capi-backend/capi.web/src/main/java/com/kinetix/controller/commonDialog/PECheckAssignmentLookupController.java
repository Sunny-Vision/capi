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
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.PECheckAssignmentLookupTableList;
import capi.service.lookup.PECheckAssignmentLookupService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;
import capi.service.productMaintenance.ProductService;


/**
 * Post-Enumeration Certainty Case lookup
 */
@Secured("RF2003")
@Controller("PECheckAssignmentLookupController")
@RequestMapping("commonDialog/PECheckAssignmentLookup")
public class PECheckAssignmentLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(PECheckAssignmentLookupController.class);

	@Autowired
	private PECheckAssignmentLookupService service;

	@Autowired
	private OutletService outletService;

	@Autowired
	private ProductService productService;

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
	public @ResponseBody DatatableResponseModel<PECheckAssignmentLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, 
				Integer userId, Integer surveyMonthId, String assignmentIds, 
				String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId) {
		try {
			
			String[] ignoreAssignmentIdsAry;
			Integer[] ignoreAssignmentIds;
			
			if(assignmentIds == null || "".equals(assignmentIds)) ignoreAssignmentIds = null;
			else {
				ignoreAssignmentIdsAry = assignmentIds.split(",");
				ignoreAssignmentIds = new Integer[ignoreAssignmentIdsAry.length];
				for(int i = 0; i < ignoreAssignmentIds.length; i++) {
					ignoreAssignmentIds[i] = Integer.parseInt(ignoreAssignmentIdsAry[i]);
				}
			}
			return service.getLookupTableList(requestModel, userId, surveyMonthId, outletTypeId, productCategoryId, districtId, tpuId, ignoreAssignmentIds);
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
			Integer userId, Integer surveyMonth, 
			String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId,
			Integer[] ignoredAssignmentIds) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search, userId, surveyMonth, outletTypeId, productCategoryId, districtId, tpuId, ignoredAssignmentIds);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
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
	 * Get product category select format
	 */
	@RequestMapping(value = "queryProductCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryProductCategorySelect2(Select2RequestModel requestModel) {
		try {
			return productService.queryProductGroupSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryProductCategorySelect2", e);
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