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

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.ReportTaskList;
import capi.model.report.SummaryOfVerificationCasesCriteria;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.report.ReportService;

import com.kinetix.component.FuncCode;

@Secured("RF9049")
@FuncCode("RF9049")
@Controller("SummaryOfVerificationCasesController.java")
@RequestMapping("report/SummaryOfVerificationCases")
public class SummaryOfVerificationCasesController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(SummaryOfVerificationCasesController.class);
	
	@Autowired
	@Qualifier("SummaryOfVerificationCasesService")
	private ReportService progressService;
	
	@Autowired
	private CalendarEventService calendarEventService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private	UserService userService;
	
	@Autowired
	private CommonService commonService;
	
	@RequestMapping("home")
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
		return new ModelAndView("redirect:/report/SummaryOfVerificationCases/home");
	}
	

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(SummaryOfVerificationCasesCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);		
		return "redirect:/report/SummaryOfVerificationCases/home";
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
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOfficerSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth, @RequestParam(value = "teamId[]", required = false) String[] teamId,
				@RequestParam(value = "staffType", required = false) Integer staffType, @RequestParam(value = "authorityLevel", required = false) Integer authorityLevel) {
		try {
			return userService.queryOfficerSelect2ByTeamWithoutIndoor(requestModel, teamId, authorityLevel, staffType);
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	@RequestMapping(value = "queryOfficerSelect3", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOfficerSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth, @RequestParam(value = "teamId[]", required = false) String[] teamId,
				@RequestParam(value = "staffType", required = false) Integer staffType, @RequestParam(value = "authorityLevel", required = false) Integer authorityLevel, 
				@RequestParam(value = "startDate", required = true) String startDate, @RequestParam(value = "endDate", required = true) String endDate) {
		try {
			Date start = null;
			Date end = null;
			if(StringUtils.isNotEmpty(startDate)){
					start = commonService.getMonth(startDate);
					//start = DateUtils.addMonths(start, 1);
					//start = DateUtils.addDays(start, -1);
			}
			if(StringUtils.isNotEmpty(endDate)){
					end = commonService.getMonth(endDate);
					//end = DateUtils.addMinutes(DateUtils.ceiling(end, Calendar.MONTH), -1);
					end = DateUtils.addMonths(end, 1);
					end = DateUtils.addDays(end, -1);
			}
			return userService.queryOfficerSelect2ByTeamWithoutIndoor(requestModel, teamId, authorityLevel, staffType, start, end);
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
	 * Get officer select2 format
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
	
}
