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

import capi.entity.Purpose;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.QuotationLookupTableList;
import capi.service.lookup.QuotationLookupService;
import capi.service.masterMaintenance.OutletService;


/**
 * Quotation lookup
 */
@Secured({"UF1118", "RF9021", "RF9023", "RF9035"})
@Controller("QuotationLookupController")
@RequestMapping("commonDialog/QuotationLookup")
public class QuotationLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotationLookupController.class);

	@Autowired
	private QuotationLookupService service;

	@Autowired
	private OutletService outletService;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		try {
			List<Purpose> purposes = service.getPurposes();
			model.addAttribute("purposes", purposes);
			
			List<VwOutletTypeShortForm> outletTypes = outletService.getOutletTypes();
			model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<QuotationLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "unitId[]", required = false) Integer[] unitId) {
		try {
			return service.getLookupTableList(requestModel, purposeId, outletTypeId, unitId);
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
			@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
			@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search, purposeId, outletTypeId);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
	
	/**
	 * datatable query function with CPI Survey Form
	 * Called by 
	 * 	MB9021,
	 */
	@RequestMapping(value = "queryWithCPISurveyForm")
	public @ResponseBody DatatableResponseModel<QuotationLookupTableList>
		queryWithCPISurveyForm(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "unitId[]", required = false) Integer[] unitId,
				@RequestParam(value = "cpiSurveyForm[]", required = false) String[] cpiSurveyForm) {
		try {
			return service.getLookupTableListWithCPISurveyForm(requestModel, purposeId, outletTypeId, unitId, cpiSurveyForm);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * datatable select all with CPI Survey Form
	 * Called by
	 * 	MB9021, 
	 */
	@RequestMapping(value = "getLookupTableSelectAllWithCPISurveyForm", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAllWithCPISurveyForm(String search,
			@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
			@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
			@RequestParam(value = "cpiSurveyForm[]", required = false) String[] cpiSurveyForm) {
		
		try {
			List<Integer> list = service.getLookupTableSelectAllWithCPISurveyForm(search, purposeId, outletTypeId, cpiSurveyForm);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAllWithCPISurveyForm", e);
		}
		
		return null;
		
	}
	
	/**
	 * datatable query function with AssignmentIds
	 * Called by 
	 * 	MB9035,
	 */
	@RequestMapping(value = "queryWithAssignmentIds")
	public @ResponseBody DatatableResponseModel<QuotationLookupTableList>
	queryWithAssignmentIds(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "unitId[]", required = false) Integer[] unitId,
				@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
		try {
			return service.getLookupTableListWithAssignmentIds(requestModel, purposeId, outletTypeId, unitId, assignmentIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * datatable select all with AssignmentIds
	 * Called by
	 * 	MB9035, 
	 */
	@RequestMapping(value = "getLookupTableSelectAllWithAssignmentIds", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAllWithAssignmentIds(String search,
			@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
			@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
			@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
		
		try {
			List<Integer> list = service.getLookupTableSelectAllWithAssignmentIds(search, purposeId, outletTypeId, assignmentIds);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAllWithCPISurveyForm", e);
		}
		
		return null;
		
	}
	
}
