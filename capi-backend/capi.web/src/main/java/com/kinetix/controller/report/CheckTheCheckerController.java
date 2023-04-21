package com.kinetix.controller.report;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.dal.ReportTaskDao;
import capi.entity.ReportTask;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.report.CheckTheCheckerCriteria;
import capi.model.report.ReportTaskList;
import capi.service.report.ReportService;
import capi.service.report.ReportServiceBase;
import capi.service.report.ReportTaskService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;

@Secured("RF9050")
@FuncCode("RF9050")
@Controller("CheckTheCheckerController")
@RequestMapping("report/CheckTheChecker")
public class CheckTheCheckerController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(CheckTheCheckerController.class);

	@Autowired
	@Qualifier( "CheckTheCheckerService")
	private ReportService progressService;

	@Autowired
	private ReportTaskService reportTaskService;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@RequestMapping("home")
	public void home(Locale locale, Model model) throws JsonParseException, JsonMappingException, IOException{

		ReportTask previousTask = reportTaskService.getLatestReportTask(this.getReportService().getFunctionCode());
		CheckTheCheckerCriteria previousCriteria = null;
		if(previousTask != null) {
			ObjectMapper mapper = new ObjectMapper();
			previousCriteria = mapper.readValue(previousTask.getCriteriaSerialize(), CheckTheCheckerCriteria.class);
		} else
			previousCriteria = new CheckTheCheckerCriteria();
		
		model.addAttribute("previousCriteria", previousCriteria);
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
		return new ModelAndView("redirect:/report/CheckTheChecker/home");
	}

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(CheckTheCheckerCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		this.generateReport(criteria, userId, redirectAttributes, locale, ReportServiceBase.DOCX);		
		return "redirect:/report/CheckTheChecker/home";
	}

	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}

}
