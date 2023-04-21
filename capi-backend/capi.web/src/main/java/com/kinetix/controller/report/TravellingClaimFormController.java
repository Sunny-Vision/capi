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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.report.ReportTaskList;
import capi.model.report.TravellingClaimFormCriteria;
import capi.service.report.ReportService;

import com.kinetix.component.FuncCode;

@Secured("RF9036")
@FuncCode("RF9036")
@Controller("TravellingClaimFormController")
@RequestMapping("report/TravellingClaimForm")
public class TravellingClaimFormController  extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(TravellingClaimFormController.class);

	@Autowired
	@Qualifier( "TravellingClaimFormService")
	private ReportService progressService;

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
		return new ModelAndView("redirect:/report/TravellingClaimForm/home");
	}

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(TravellingClaimFormCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);	
		return "redirect:/report/TravellingClaimForm/home";
	}

	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}
}
