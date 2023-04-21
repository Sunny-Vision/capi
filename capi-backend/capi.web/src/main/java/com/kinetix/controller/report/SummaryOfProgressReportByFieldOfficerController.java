package com.kinetix.controller.report;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.ReportTaskList;
import capi.model.report.SummaryOfProgressReportByFieldOfficerCriteria;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AllocationBatchService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.report.ReportService;

@Secured("RF9027")
@FuncCode("RF9027")
@Controller("SummaryOfProgressReportByFieldOfficerController")
@RequestMapping("report/SummaryOfProgressReportByFieldOfficer")
public class SummaryOfProgressReportByFieldOfficerController extends ReportBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SummaryOfProgressReportByFieldOfficerController.class);
	
	@Autowired
	@Qualifier("SummaryOfProgressReportByFieldOfficerService")
	private ReportService progressService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private PurposeService purposeService;

	@Autowired
	private AllocationBatchService allocationBatchService;
	
	@Autowired
	private CommonService commonService;
	
	@RequestMapping(value="home")
	public void home(){}
	
	@RequestMapping(value="query")
	public @ResponseBody DatatableResponseModel<ReportTaskList> query(DatatableRequestModel requestModel){
		try {
			return this.queryReportTask(requestModel, progressService.getFunctionCode());
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	@RequestMapping(value = "downloadFile", method = RequestMethod.GET)
	public ModelAndView downloadFile(Integer id, ModelAndView mav, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response){
		if (this.downloadFile(id, redirectAttributes, locale, response)){
			return null;
		}		
		return new ModelAndView("redirect:/report/SummaryOfProgressReportByFieldOfficer/home");
	}
	
	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(SummaryOfProgressReportByFieldOfficerCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);		
		return "redirect:/report/SummaryOfProgressReportByFieldOfficer/home";
	}
	
	/**
	 * Get District select format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryDistrictSelect2(Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel) {
		try {
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Allocation Batch select format
	 */
	@RequestMapping(value = "queryAllocationBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryAllocationBatchSelect2(Select2RequestModel requestModel, String refMonth) {
		try {
			return allocationBatchService.queryAllocationBatchByRefMonth(requestModel, refMonth);
		} catch (Exception e) {
			logger.error("queryAllocationBatchSelect2", e);
		}
		return null;
	}

	@Override
	public ReportService getReportService() {
		return progressService;
	}
	
	/**
	 * Get officer select format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOfficerSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "refMonth", required = false) String refMonth,
			@RequestParam(value = "userIds", required = false) Integer[] userIds,
			@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {
			Date referenceMonth = null;
			
			if(StringUtils.isNotEmpty(refMonth)) {
				referenceMonth = commonService.getMonth(refMonth);
			}

			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, null, null, referenceMonth, null, staffType);
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	
	@RequestMapping(value = "queryUserSelectMutiple")
	public @ResponseBody List<KeyValueModel> queryUserSelectMutiple(@RequestParam(value = "id[]") Integer[] ids) {
		try {
			return userService.getKeyValueByIds(ids);
			
		} catch (Exception e) {
			logger.error("queryUserSelectMutiple", e);
		}
		return null;
	}
}
