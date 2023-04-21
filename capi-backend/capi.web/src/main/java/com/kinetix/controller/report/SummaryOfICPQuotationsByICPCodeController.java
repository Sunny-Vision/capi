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

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.ReportTaskList;
import capi.model.report.SummaryOfICPQuotationsByICPCodeCriteria;
import capi.service.assignmentManagement.QuotationService;
import capi.service.report.ReportService;

@Secured("RF9037")
@FuncCode("RF9037")
@Controller("SummaryOfICPQuotationsByICPCodeController")
@RequestMapping("report/SummaryOfICPQuotationsByICPCode")
public class SummaryOfICPQuotationsByICPCodeController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(SummaryOfICPQuotationsByICPCodeController.class);
	
	@Autowired
	@Qualifier("SummaryOfICPQuotationsByICPCodeService")
	private ReportService progressService;
	
	@Autowired
	private QuotationService quotationService;
	
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
		return new ModelAndView("redirect:/report/SummaryOfICPQuotationsByICPCode/home");
	}
	
	@RequestMapping(value="submitReportRequest")
	public String submitReportRequsest(SummaryOfICPQuotationsByICPCodeCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria,  userId, redirectAttributes, locale, taskType);
		return "redirect:/report/SummaryOfICPQuotationsByICPCode/home";
	}
	
	/**
	 * Get ICP Type select format
	 */
	@RequestMapping(value = "queryQuotationICPTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryQuotationICPTypeSelect2(Select2RequestModel requestModel){
		try{
			return quotationService.queryICPTypeSelect2(requestModel);
		} catch (Exception e){
			logger.error("queryQuotationICPTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get ICP Product Code select format
	 */
	@RequestMapping(value = "queryQuotationICPProductCodeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryQuotationICPProductCodeSelect2(Select2RequestModel requestModel){
		try{
			return quotationService.queryICPProductCodeSelect2(requestModel);
		} catch (Exception e){
			logger.error("queryICPProductCodeSelect2", e);
		}
		return null;
	}
		
	@Override
	public ReportService getReportService(){
		// TODO Auto-generated method stub
		return progressService;
	}
}
