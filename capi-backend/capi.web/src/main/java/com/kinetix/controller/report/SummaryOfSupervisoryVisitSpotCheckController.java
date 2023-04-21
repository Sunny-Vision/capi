package com.kinetix.controller.report;

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
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckCriteria;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.report.ReportService;

@Secured("RF9017")
@FuncCode("RF9017")
@Controller("SummaryOfSupervisoryVisitSpotCheckController")
@RequestMapping("report/SummaryOfSupervisoryVisitSpotCheck")
public class SummaryOfSupervisoryVisitSpotCheckController extends ReportBaseController{

private static final Logger logger = LoggerFactory.getLogger(SummaryOfSupervisoryVisitSpotCheckController.class);
	
	@Autowired
	@Qualifier( "SummaryOfSupervisoryVisitSpotCheckService")
	private ReportService progressService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;
	
	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}
	
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
		return new ModelAndView("redirect:/report/SummaryOfSupervisoryVisitSpotCheck/home");
	}
	

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(SummaryOfSupervisoryVisitSpotCheckCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);
		return "redirect:/report/SummaryOfSupervisoryVisitSpotCheck/home";
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
			@RequestParam(value = "userIds[]", required = false) Integer[] userIds,
			@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {
			// fix for PIR-266: end date not needed to search officer
//			Date end = null;
//			if(StringUtils.isNotEmpty(endDate)){
//				end = commonService.getMonth(endDate);
//			}
//			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, null, end, null, userIds, staffType);
			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, null, null, null, userIds, staffType);
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
