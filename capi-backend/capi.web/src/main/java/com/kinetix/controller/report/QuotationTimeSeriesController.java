package com.kinetix.controller.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import capi.model.report.QuotationTimeSeriesCriteria;
import capi.model.report.ReportTaskList;
import capi.service.assignmentManagement.QuotationService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;
import capi.service.report.ReportService;

import com.kinetix.component.FuncCode;

@Secured("RF9023")
@FuncCode("RF9023")
@Controller("QuotationTimeSeriesController")
@RequestMapping("report/QuotationTimeSeries")
public class QuotationTimeSeriesController  extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(QuotationTimeSeriesController.class);
	
	@Autowired
	@Qualifier( "QuotationTimeSeriesService")
	private ReportService reportService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private QuotationService quotationService;
	
	@RequestMapping(value="home")
	public void home(){}
	
	@RequestMapping(value="query")
	public @ResponseBody DatatableResponseModel<ReportTaskList> query(DatatableRequestModel requestModel){
		try {
			return this.queryReportTask(requestModel, reportService.getFunctionCode());
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
		return new ModelAndView("redirect:/report/QuotationTimeSeries/home");
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel) {
		try {
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get unit select format
	 */
	@RequestMapping(value = "queryUnitSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUnitSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "purposeIds[]", required = false) Integer[] purposeIds) {
		try {
			if (purposeIds != null)
				return unitService.queryUnitSelect2(requestModel, new ArrayList<Integer>(Arrays.asList(purposeIds)));
			else
				return unitService.queryUnitSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUnitSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryQuotationSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryQuotationSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
			@RequestParam(value = "unitId[]", required = false) Integer[] unitId) {
		try {
			return quotationService.queryQuotationSelect2(requestModel, purposeId, unitId);
		} catch (Exception e) {
			logger.error("queryQuotationSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single unit
	 */
	@RequestMapping(value = "getKeyValueByIds")
	public @ResponseBody List<KeyValueModel> getKeyValueByIds(@RequestParam(value = "id[]") Integer[] id) {
		try {
			return unitService.getKeyValueByIds(id);
		} catch (Exception e) {
			logger.error("getKeyValueByIds", e);
		}
		return null;
	}
	
	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(QuotationTimeSeriesCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);
		return "redirect:/report/QuotationTimeSeries/home";
	}
	
	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return reportService;
	}
	
}
