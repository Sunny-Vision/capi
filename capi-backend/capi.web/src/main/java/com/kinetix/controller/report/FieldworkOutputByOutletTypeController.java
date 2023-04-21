package com.kinetix.controller.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.FieldworkOutputByOutletTypeCriteria;
import capi.model.report.ReportTaskList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AllocationBatchService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.report.ReportService;

import com.kinetix.component.FuncCode;

@Secured("RF9044")
@FuncCode("RF9044")
@Controller("FieldworkOutputByOutletTypeController")
@RequestMapping("report/FieldworkOutputByOutletType")
public class FieldworkOutputByOutletTypeController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(FieldworkOutputByOutletTypeController.class);
	
	@Autowired
	@Qualifier("FieldworkOutputByOutletTypeService")
	private ReportService progressService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private	UserService userService;
	
	@Autowired
	private	OutletService outletService;
	
	@Autowired
	private	CommonService commonService;
	
	@Autowired
	private	AllocationBatchService allocationBatchService;
	
	@RequestMapping("home")
	public void home(Model model, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		model.addAttribute("userIds", userIds);
		int authorityLevel = 0;
		if ((detail.getAuthorityLevel() & 4) > 0){
			authorityLevel = 16;
		}
		else if ((detail.getAuthorityLevel() & 771) > 0){
			authorityLevel = 20;
		} 
		model.addAttribute("authorityLevel", authorityLevel);
	}
	
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
		return new ModelAndView("redirect:/report/FieldworkOutputByOutletType/home");
	}
	

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(FieldworkOutputByOutletTypeCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		criteria.setUserId(userId);
		criteria.setAuthorityLevel(detail.getAuthorityLevel());
		this.generateReport(criteria, userId, redirectAttributes, locale, 2);		
		return "redirect:/report/FieldworkOutputByOutletType/home";
	}

	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
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
	 * Get outlet type select format
	 */
	@RequestMapping(value = "queryAllocationBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryAllocationBatchSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "referenceMonthStr", required = false) String referenceMonthStr) {
		try {
			Date referenceMonth = null;
			if(referenceMonthStr != null && !StringUtils.isEmpty(referenceMonthStr)){
				referenceMonth = this.commonService.getMonth(referenceMonthStr);
			}
			return allocationBatchService.queryAllocationBatchSelect2(requestModel, referenceMonth);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
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
			if(StringUtils.isNotEmpty(refMonth)){
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
