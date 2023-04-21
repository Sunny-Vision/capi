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
import capi.model.report.SummaryOfICPQuotationsByICPTypeCriteria;
import capi.service.assignmentManagement.QuotationService;
import capi.service.masterMaintenance.UnitService;
import capi.service.report.ReportService;

@Secured("RF9038")
@FuncCode("RF9038")
@Controller("SummaryOfICPQuotationsByICPTypeController")
@RequestMapping("report/SummaryOfICPQuotationsByICPType")
public class SummaryOfICPQuotationsByICPTypeController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(SummaryOfICPQuotationsByICPTypeController.class);
	
	@Autowired
	@Qualifier("SummaryOfICPQuotationsByICPTypeService")
	private ReportService progressService;
	
	@Autowired
	private UnitService unitService;
	
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
		return new ModelAndView("redirect:/report/SummaryOfICPQuotationsByICPType/home");
	}
	
	@RequestMapping(value="submitReportRequest")
	public String submitReportRequsest(SummaryOfICPQuotationsByICPTypeCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria,  userId, redirectAttributes, locale, taskType);
		return "redirect:/report/SummaryOfICPQuotationsByICPType/home";
	}
	
	/**
	 * Get ICP Type select format
	 */
	@RequestMapping(value = "queryUnitICPTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUnitICPTypeSelect2(Select2RequestModel requestModel){
		try{
			return unitService.queryICPTypeSelect2(requestModel);
		} catch (Exception e){
			logger.error("queryUnitICPTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get unit select format
	 */
	@RequestMapping(value = "queryUnitSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUnitSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.queryUnitSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUnitSelect2", e);
		}
		return null;
	}
	
	@Override
	public ReportService getReportService(){
		// TODO Auto-generated method stub
		return progressService;
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
}
