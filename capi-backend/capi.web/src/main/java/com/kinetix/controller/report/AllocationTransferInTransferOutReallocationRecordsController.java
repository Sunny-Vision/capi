package com.kinetix.controller.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
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

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.AllocationTransferInTransferOutReallocationRecordsCriteria;
import capi.model.report.ReportTaskList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AllocationBatchService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.report.ReportService;

@Secured("RF9031")
@FuncCode("RF9031")
@Controller("AllocationTransferInTransferOutReallocationRecordsController")
@RequestMapping("report/AllocationTransferInTransferOutReallocationRecords")
public class AllocationTransferInTransferOutReallocationRecordsController extends ReportBaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(AllocationTransferInTransferOutReallocationRecordsController.class);
	
	@Autowired
	@Qualifier("AllocationTransferInTransferOutReallocationRecordsService")
	private ReportService progressService;
	
	@Autowired
	private UserService userService;
	
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
		try{
			return this.queryReportTask(requestModel, progressService.getFunctionCode());
		} catch (Exception e){
			logger.error("query", e);
		}
		return null;
	}
	
	@RequestMapping(value="downloadFile", method = RequestMethod.GET)
	public ModelAndView downloadFile(Integer id, ModelAndView mav, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response){
		if(this.downloadFile(id, redirectAttributes, locale, response)){
			return null;
		}
		return new ModelAndView("redirect:/report/AllocationTransferInTransferOutReallocationRecords/home");
	}
	
	@RequestMapping(value="submitReportRequest")
	public String submitReportRequest(AllocationTransferInTransferOutReallocationRecordsCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		criteria.setUserId(userId);
		criteria.setAuthorityLevel(detail.getAuthorityLevel());
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);
		return "redirect:/report/AllocationTransferInTransferOutReallocationRecords/home";
	}
	
	@Override
	public ReportService getReportService(){
		//TODO Auto-generated method stub
		return progressService;
	}
	
	/**
	 * Get team select2 format
	 */
	@RequestMapping(value = "queryTeamSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTeamSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			return userService.queryTeamSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
//		queryOfficerSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth, String[] teams) {
		queryOfficerSelect2(Select2RequestModel requestModel,
				@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
				@RequestParam(value = "startDate", required = true) String startDate,
				@RequestParam(value = "endDate", required = true) String endDate,
				@RequestParam(value = "staffType", required = false) Integer staffType
				) {
		try {
//			return userService.queryOfficerSelect2ByTeamId(requestModel, teams);
			Date start = null;
			Date end = null;
			if(StringUtils.isNotEmpty(startDate)){
				start = commonService.getMonth(startDate);
				//start = DateUtils.addMonths(start, 1);
				//start = DateUtils.addDays(start, -1);
			}
			if(StringUtils.isNotEmpty(endDate)){
				end = commonService.getMonth(endDate);
				end = DateUtils.addMonths(end, 1);
				end = DateUtils.addDays(end, -1);
			}
			
			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, start, end, null, null, staffType);
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
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryAllocationBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryAllocationBatchSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth, String[] refMonth) {
		try {
			Date fromMonth = null;
			Date toMonth = null;
			if(!StringUtils.isEmpty(refMonth[0])){
				fromMonth = commonService.getMonth(refMonth[0]);
			}
			
			if(!StringUtils.isEmpty(refMonth[1])){
				toMonth = commonService.getMonth(refMonth[1]);
			}
			return allocationBatchService.queryAllocationBatchSelect2ByMonthRange(requestModel, fromMonth, toMonth);
		} catch (Exception e) {
			logger.error("queryAllocationBatchSelect2", e);
		}
		return null;
	}
}
