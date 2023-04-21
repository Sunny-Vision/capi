package com.kinetix.controller.report;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.report.AccessLogCriteria;
import capi.model.report.ReportTaskList;
import capi.service.report.ReportService;

@Secured("UF9051")
@FuncCode("UF9051")
@Controller("AccessLogController")
@RequestMapping("report/AccessLog")
public class AccessLogController extends ReportBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(AccessLogController.class);
	
	@Autowired
	@Qualifier("AccessLogReportService")
	private ReportService reportService;

	@Override
	public ReportService getReportService() {
		return reportService;
	}
	
	@RequestMapping("home")
	public void home() {}
	
	@RequestMapping("query")
	public @ResponseBody DatatableResponseModel<ReportTaskList> query(DatatableRequestModel requestModel) {
		try {
			return this.queryReportTask(requestModel, reportService.getFunctionCode());
		} catch (Exception ex) {
			logger.error("query", ex);
		}
		
		return null;
	}
	
	@RequestMapping("downloadFile")
	public ModelAndView downloadFile(Integer id, ModelAndView mav, RedirectAttributes redirectAttributes, 
			HttpServletResponse response, Locale locale) {
		if (this.downloadFile(id, redirectAttributes, locale, response)) {
			return null;
		}
		
		return new ModelAndView("redirect:/report/AccessLog/home");
	}
	
	@RequestMapping("submitReportRequest")
	public String submitReportRequest(AccessLogCriteria criteria, Integer taskType, 
			Authentication authentication, RedirectAttributes redirectAttributes, Locale locale) {
		
		CapiWebAuthenticationDetails details = (CapiWebAuthenticationDetails) authentication.getDetails();
		this.generateReport(criteria, details.getUserId(), redirectAttributes, locale, taskType);
		
		return "redirect:/report/AccessLog/home";
	}

}
