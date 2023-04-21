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
import capi.model.report.QuotationRecordProgressCriteria;
import capi.model.report.ReportTaskList;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;
import capi.service.report.ReportService;

import com.kinetix.component.FuncCode;

@Secured("RF9002")
@FuncCode("RF9002")
@Controller("QuotationRecordProgressController")
@RequestMapping("report/QuotationRecordProgress")
public class QuotationRecordProgressController  extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(QuotationRecordProgressController.class);
	
	@Autowired
	@Qualifier( "QuotationRecordProgressService")
	private ReportService progressService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private BatchService batchService;
	
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
		return new ModelAndView("redirect:/report/QuotationRecordProgress/home");
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

	/**
	 * Get batch select format
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryBatchSelect2(Select2RequestModel requestModel,
										@RequestParam(value = "cpiQuotationTypes", required = false) Integer[] cpiQuotationTypes) {
		try {
			return batchService.queryBatchSelect2ForReport(requestModel, cpiQuotationTypes);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get group select format
	 */
	@RequestMapping(value = "queryGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryGroupSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.queryGroupSelect2(requestModel, "", "");
		} catch (Exception e) {
			logger.error("queryGroupSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get sub group select format
	 */
	@RequestMapping(value = "querySubGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubGroupSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.querySubGroupSelect2(requestModel, "", "");
		} catch (Exception e) {
			logger.error("querySubGroupSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get cpi base period select format
	 */
	@RequestMapping(value = "queryCPIBasePeriodSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryCPIBasePeriodSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.queryCPIBasePeriodSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryCPIBasePeriodSelect2", e);
		}
		return null;
	}
	
	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(QuotationRecordProgressCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);	
		return "redirect:/report/QuotationRecordProgress/home";
	}
	
	@RequestMapping(value = "getBatchCodes")
	public @ResponseBody List<KeyValueModel> getBatchCodes(Integer[] ids){
		try{
			return batchService.getBatchCodes(ids);
		}
		catch(Exception ex){
			logger.error("getBatchCodes", ex);
		}
		
		return null;
	}
	
	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}
	
}
