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

import capi.entity.VwOutletTypeShortForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.AssignmentLookupSurveyMonthTableList;
import capi.model.commonLookup.AssignmentLookupTableList;
import capi.model.itineraryPlanning.MetricSelectedAssignmentModel;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import capi.service.lookup.AssignmentLookupService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.TpuService;


/**
 * Outlet lookup
 */
@Secured({"UF1701"})
@Controller("AssignmentLookupController")
@RequestMapping("commonDialog/AssignmentLookup")
public class AssignmentLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(AssignmentLookupController.class);

	@Autowired
	private AssignmentLookupService service;

	@Autowired
	private OutletService outletService;
	
	@Autowired
	private TpuService tpuService;
	
	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private PurposeService purposeService;

	@Autowired
	private SurveyMonthService surveyMonthService;
	
	/**
	 * CPI Outlet Lookup dialog
	 */
	@RequestMapping(value = "outletHome", method = RequestMethod.GET)
	public void outlet(Model model) {
		try {
			List<VwOutletTypeShortForm> outletTypes = outletService.getOutletTypes();
			model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("outletHome", e);
		}
	}
	
	/**
	 * DataTable query Outlet
	 */
	@RequestMapping(value = "outletQuery")
	public @ResponseBody DatatableResponseModel<AssignmentLookupTableList>
		outletQuery(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth,
				@RequestParam(value = "date") String date,
				@RequestParam(value = "officerId", required = false) Integer officerId,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "excludedOutletIds[]", required = false) Integer[] excludedOutletIds
				) {
		try {
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			
			if ((detail.getAuthorityLevel() & 4) == 4) {
				userId = officerId;
			}
			
			return service.getLookupOutletTableList(requestModel, userId, outletTypeId, districtId, tpuId, date, excludedOutletIds);
		} catch (Exception e) {
			logger.error("outletQuery", e);
		}
		return null;
	}
	
	/**
	 * Building & GHS Lookup dialog
	 */
	@RequestMapping(value = "buildingHome", method = RequestMethod.GET)
	public void building(Model model) {
		try {

		} catch (Exception e) {
			logger.error("buildingHome", e);
		}
	}
	
	/**
	 * DataTable query Outlet
	 */
	@RequestMapping(value = "buildingQuery")
	public @ResponseBody DatatableResponseModel<AssignmentLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth,
				@RequestParam(value = "date") String date,
				@RequestParam(value = "officerId", required = false) Integer officerId,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "survey[]", required = false) String[] survey,
				@RequestParam(value = "surveyMonthId[]", required = false) Integer[] surveyMonthId,
				@RequestParam(value = "excludedAssignmentIds[]", required = false) Integer[] excludedAssignmentIds
				) {
		try {
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			
			if ((detail.getAuthorityLevel() & 4) == 4) {
				userId = officerId;
			}
			
			return service.getLookupBuildingTableList(requestModel, userId, districtId, tpuId, survey, surveyMonthId, date, excludedAssignmentIds);
		} catch (Exception e) {
			logger.error("buildingQuery", e);
		}
		return null;
	}
	
	/**
	 * Assignment (with Survey Month) Lookup dialog
	 */
	@RequestMapping(value = "surveyMonthHome", method = RequestMethod.GET)
	public void surveyMonth(Model model) {
	}
	
	/**
	 * DataTable query Outlet
	 */
	@RequestMapping(value = "surveyMonthQuery")
	public @ResponseBody DatatableResponseModel<AssignmentLookupSurveyMonthTableList>
		surveyMonthQuery(Locale locale, Model model, DatatableRequestModel requestModel, 
				Integer surveyMonthId, Integer currentIdx,
				Integer officerId) {
		try {
			model.addAttribute("currentIdx", currentIdx);
			return service.getLookupSurveyMonthTableList(requestModel, surveyMonthId, officerId);
		} catch (Exception e) {
			logger.error("surveyMonthQuery", e);
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
	
	/**
	 * Get Survey select2 format
	 */
	@RequestMapping(value = "querySurveySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySurveySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return purposeService.querySurveySelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("querySurveySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Survey select2 format
	 */
	@RequestMapping(value = "querySurveyMonthSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySurveyMonthSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return surveyMonthService.querySurveyMonthSelect2(requestModel);
		} catch (Exception e) {
			logger.error("querySurveyMonthSelect2", e);
		}
		return null;
	}

	/**
	 * Metric Selected Assignments
	 */
	@RequestMapping(value = "metricForAssignmentSelectionPopup", method = RequestMethod.POST)
	public @ResponseBody MetricSelectedAssignmentModel metricForAssignmentSelectionPopup(Locale locale, Model model,
			@RequestParam(value = "date", required = false) String date,
			@RequestParam(value = "officerId", required = false) Integer officerId,
			@RequestParam(value = "excludedOutletIds[]", required = false) Integer[] excludedOutletIds,
			@RequestParam(value = "excludedAssignmentIds[]", required = false) Integer[] excludedAssignmentIds) {
		try {
			return service.metricForAssignmentSelectionPopup(officerId, date, excludedOutletIds, excludedAssignmentIds);
		} catch (Exception e) {
			logger.error("metricForAssignmentSelectionPopup", e);
		}
		return null;
	}
}
