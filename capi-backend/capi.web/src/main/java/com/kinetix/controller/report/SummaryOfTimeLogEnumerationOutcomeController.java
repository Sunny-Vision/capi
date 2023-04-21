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

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.ReportTaskList;
import capi.model.report.SummaryOfTimeLogEnumerationOutcomeCriteria;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.report.ReportService;

@Secured("RF9015")
@FuncCode("RF9015")
@Controller("SummaryOfTimeLogEnumerationOutcomeController")
@RequestMapping("report/SummaryOfTimeLogEnumerationOutcome")
public class SummaryOfTimeLogEnumerationOutcomeController extends ReportBaseController{

private static final Logger logger = LoggerFactory.getLogger(SummaryOfTimeLogEnumerationOutcomeController.class);
	
	@Autowired
	@Qualifier( "SummaryOfTimeLogEnumerationOutcomeService")
	private ReportService progressService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private PurposeService purposeService;

	@Autowired
	private CommonService commonService;
	
	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}
	
	@RequestMapping("home")
	public void home(Model model, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		model.addAttribute("userIds", userIds);
		int authorityLevel = 0;
		if ((detail.getAuthorityLevel() & 771) > 0){
			authorityLevel = 20;
		} else if ((detail.getAuthorityLevel() & 4) > 0){
			authorityLevel = 16;
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
		return new ModelAndView("redirect:/report/SummaryOfTimeLogEnumerationOutcome/home");
	}
	

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(SummaryOfTimeLogEnumerationOutcomeCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		criteria.setUserId(userId);
		criteria.setAuthorityLevel(detail.getAuthorityLevel());
		this.generateReport(criteria, userId, redirectAttributes, locale, 2);
		return "redirect:/report/SummaryOfTimeLogEnumerationOutcome/home";
	}
	
	/**
	 * Get survey select format
	 */
	@RequestMapping(value = "querySurveySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySurveySelect2(Select2RequestModel requestModel,
			@RequestParam(value = "purposeId", required = false) Integer purposeId) {
		try {
			return purposeService.querySurveySelect2(requestModel, purposeId);
		} catch (Exception e) {
			logger.error("querySurveySelect2", e);
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
			Date start = null;
			Date end = null;
			if(StringUtils.isNotEmpty(startDate)){
				start = commonService.getDate(startDate);
			}
			if(StringUtils.isNotEmpty(endDate)){
				end = commonService.getDate(endDate);
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
}
