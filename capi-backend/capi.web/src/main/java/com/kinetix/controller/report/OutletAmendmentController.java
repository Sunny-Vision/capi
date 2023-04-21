package com.kinetix.controller.report;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
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
import capi.model.report.OutletAmendmentCriteria;
import capi.model.report.ReportTaskList;
import capi.service.report.ReportService;

import com.kinetix.component.CapiAuthenticationProvider;
import com.kinetix.component.FuncCode;

@Secured("RF9018")
@FuncCode("RF9018")
@Controller("OutletAmendmentController")
@RequestMapping("report/OutletAmendment")
public class OutletAmendmentController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(OutletAmendmentController.class);
	
	@Autowired
	@Qualifier( "OutletAmendmentService")
	private ReportService progressService;
	
	@Autowired
	private StandardPBEStringEncryptor pbeEncryptor;
	
	
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
	public ModelAndView downloadFile(Integer id, ModelAndView mav, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response, 
			javax.servlet.http.HttpSession session){
		// Added 2020-09-29 Toby: (CR12) get encrypted user password (plaintext) in session for password-protected file
		String userPwEncrypted = (String) session.getAttribute(CapiAuthenticationProvider.USER_PASSWORD_SESSION_ATTRIBUTE_KEY);
		if (this.addPasswordForDownloadFile(id, pbeEncryptor.decrypt(userPwEncrypted), redirectAttributes, locale, response)){
			return null;
		}		
		return new ModelAndView("redirect:/report/OutletAmendment/home");
	}
			
	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(OutletAmendmentCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, 2);	
		return "redirect:/report/OutletAmendment/home";
	}

	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}
	
}
