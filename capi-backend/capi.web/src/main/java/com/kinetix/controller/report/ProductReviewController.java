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

import com.kinetix.component.FuncCode;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.ProductReviewCriteria;
import capi.model.report.ReportTaskList;
import capi.service.masterMaintenance.PurposeService;
import capi.service.productMaintenance.ProductService;
import capi.service.report.ReportService;

@Secured("RF9009")
@FuncCode("RF9009")
@Controller("ProductReviewController")
@RequestMapping("report/ProductReview")
public class ProductReviewController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(ProductReviewController.class);
	
	@Autowired
	@Qualifier("ProductReviewService")
	private ReportService progressService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private PurposeService purposeService;
	
	@RequestMapping(value="home")
	public void home(){
		
	}
	
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
		return new ModelAndView("redirect:/report/ProductReview/home");
	}
	
	@RequestMapping(value="submitReportRequest")
	public String submitReportRequest(ProductReviewCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, 2);
		return "redirect:/report/ProductReview/home";
	}
	
	/**
	 * Get Purpose select format
	 **/
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel){
		try{
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Product Group select format
	 **/
	@RequestMapping(value = "queryProductCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryProductCategorySelect2(Select2RequestModel requestModel){
		try{
			return productService.queryProductGroupSelect2(requestModel);
		} catch (Exception e){
			logger.error("queryProductCategorySelect2", e);
		}
		return null;
	}
	
	@Override
	public ReportService getReportService(){
		// TODO Auto-generated method stub
		return progressService;
	}
}
