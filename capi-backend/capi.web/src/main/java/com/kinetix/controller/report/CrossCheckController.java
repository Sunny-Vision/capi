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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.CrossCheckCriteria;
import capi.model.report.ReportTaskList;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;
import capi.service.report.ReportService;

import com.kinetix.component.FuncCode;

@Secured("RF9006")
@FuncCode("RF9006")
@Controller("CrossCheckController")
@RequestMapping("report/CrossCheck")
public class CrossCheckController  extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(CrossCheckController.class);

	@Autowired
	@Qualifier( "CrossCheckService")
	private ReportService progressService;

	@Autowired
	private PurposeService purposeService;

	@Autowired
	private UnitService unitService;

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
		return new ModelAndView("redirect:/report/CrossCheck/home");
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
	 * Get cross check group select format
	 */
	@RequestMapping(value = "queryCrossCheckGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryCrossCheckGroupSelect2(Select2RequestModel requestModel,
										@RequestParam(value = "purposeIds", required = false) Integer[] purposeIds) {
		try {
			return unitService.queryCrossCheckGroupSelect2(requestModel, purposeIds);
		} catch (Exception e) {
			logger.error("queryCrossCheckGroupSelect2", e);
		}
		return null;
	}

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(CrossCheckCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, 2);	
		return "redirect:/report/CrossCheck/home";
	}

	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}

}
