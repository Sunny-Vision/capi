package com.kinetix.controller.commonDialog;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.JsTreeRequestGetBottomEntityIdsModel;
import capi.model.JsTreeResponseModel;
import capi.service.lookup.UnitLookupService;


/**
 * Unit lookup
 */
@Secured({"UF1112", "UF1118", "UF1207", "UF1401", "UF1402", "UF1409", "UF1701", "UF1702", "UF1703", "UF2101", "UF2102", "UF2103", "UF2201", "RF9002", "RF9005", "RF9007", "RF9008", "RF9011", "RF9013", "RF9035", "RF9021","RF9035"})
@Controller("UnitLookupController")
@RequestMapping("commonDialog/UnitLookup")
public class UnitLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(UnitLookupController.class);

	@Autowired
	private UnitLookupService service;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}

	/**
	 * Query cpi base period
	 */
	@RequestMapping(value = "queryCpiBasePeriod", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> queryCpiBasePeriod(@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getAllCpiBasePeriods(new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getAllCpiBasePeriods(null, onlyActive);
		} catch (Exception e) {
			logger.error("queryCpiBasePeriod", e);
		}
		return null;
	}

	/**
	 * Query section
	 */
	@RequestMapping(value = "querySection", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> querySection(@RequestParam(value = "id") String id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getSectionsByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getSectionsByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("querySection", e);
		}
		return null;
	}

	/**
	 * Query group
	 */
	@RequestMapping(value = "queryGroup", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> queryGroup(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getGroupsByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getGroupsByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("queryGroup", e);
		}
		return null;
	}

	/**
	 * Query subgroup
	 */
	@RequestMapping(value = "querySubGroup", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> querySubGroup(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getSubGroupsByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getSubGroupsByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("querySubGroup", e);
		}
		return null;
	}

	/**
	 * Query item
	 */
	@RequestMapping(value = "queryItem", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> queryItem(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getItemsByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getItemsByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("queryItem", e);
		}
		return null;
	}

	/**
	 * Query outlet type
	 */
	@RequestMapping(value = "queryOutletType", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> queryOutletType(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getOutletTypesByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getOutletTypesByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("queryOutletType", e);
		}
		return null;
	}

	/**
	 * Query sub item
	 */
	@RequestMapping(value = "querySubItem", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> querySubItem(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getSubItemsByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getSubItemsByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("querySubItem", e);
		}
		return null;
	}

	/**
	 * Query unit
	 */
	@RequestMapping(value = "queryUnit", method = RequestMethod.GET)
	public @ResponseBody List<JsTreeResponseModel> queryUnit(@RequestParam(value = "id") Integer id,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds,
			@RequestParam(value = "onlyActive", required = true) boolean onlyActive) {
		try {
			if (purposeIds != null)
				return service.getUnitsByParentId(id, new ArrayList<Integer>(Arrays.asList(purposeIds)), onlyActive);
			else
				return service.getUnitsByParentId(id, null, onlyActive);
		} catch (Exception e) {
			logger.error("queryUnit", e);
		}
		return null;
	}
	
	/**
	 * Get unit ids
	 */
	@RequestMapping(value = "getBottomEntityIds", method = RequestMethod.POST)
	public @ResponseBody List<String> getBottomEntityIds(JsTreeRequestGetBottomEntityIdsModel requestModel) {
		try {
			return service.getBottomEntityIds(requestModel);
		} catch (Exception e) {
			logger.error("getBottomEntityIds", e);
		}
		return null;
	}
}