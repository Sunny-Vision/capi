package com.kinetix.controller.commonDialog;

import java.util.ArrayList;
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

import capi.entity.Tpu;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.OutstandingAssignmentLookupTableList;
import capi.service.UserService;
import capi.service.lookup.OutstandingAssignmentLookupService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;


/**
 * Outstanding Assignment lookup
 */
@Secured("UF1505")
@Controller("OutstandingAssignmentLookupController")
@RequestMapping("commonDialog/OutstandingAssignmentLookup")
public class OutstandingAssignmentLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(OutstandingAssignmentLookupController.class);

	@Autowired
	private OutstandingAssignmentLookupService service;

	@Autowired
	private OutletService outletService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private BatchService batchService;

	@Autowired
	private UserService userService;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {

		List<Tpu> tpus = service.getTpus();
		
		model.addAttribute("tpus", tpus);
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<OutstandingAssignmentLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				String assignmentIdList, 
				@RequestParam(value = "tpuIds[]", required = false) Integer[] tpuId, 
				String outletTypeId, Integer districtId, Integer batchId, String collectionDate) {
		try {
			List<Integer> tpuIds = new ArrayList<Integer> ();
			if(tpuId == null) {
				tpuIds = null;
			} else {
				for(int i = 0; i < tpuId.length; i++) {
					tpuIds.add(new Integer(tpuId[i]));
				}
			}
			
			String[] assignmentIdsAry = assignmentIdList.split(",");
			Integer[] assignmentId = new Integer[assignmentIdsAry.length];
			
			if("".equals(assignmentIdList)) assignmentId = null;
			else {
				for(int i = 0; i < assignmentId.length; i++) {
					assignmentId[i] = Integer.parseInt(assignmentIdsAry[i]);
				}
			}
			
			return service.getLookupTableList(requestModel,
					tpuIds, outletTypeId, districtId, batchId, collectionDate, assignmentId);
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
			Integer originalUserId, 
			@RequestParam(value = "tpuIds[]", required = false) Integer[] tpuId, 
			String outletTypeId, Integer districtId, Integer batchId, String collectionDate) {
		try {
			List<Integer> tpuIds = new ArrayList<Integer> ();
			if(tpuId == null) {
				tpuIds = null;
			} else {
				for(int i = 0; i < tpuId.length; i++) {
					tpuIds.add(new Integer(tpuId[i]));
				}
			}
			List<Integer> list = service.getLookupTableSelectAll(search,
					tpuIds, outletTypeId, districtId, batchId, collectionDate);
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