package com.kinetix.controller.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import capi.model.report.IndoorStaffIndividualProgressCriteria;
import capi.model.report.ReportTaskList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;
import capi.service.report.ReportService;
import com.kinetix.component.FuncCode;

@Secured("RF9003")
@FuncCode("RF9003")
@Controller("IndoorStaffIndividualProgressController")
@RequestMapping("report/IndoorStaffIndividualProgress")
public class IndoorStaffIndividualProgressController  extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(IndoorStaffIndividualProgressController.class);
		
	@Autowired
	@Qualifier( "IndoorStaffIndividualProgressService")
	private ReportService progressService;
		
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private BatchService batchService;
	
	@Autowired
	private UserService userService;
	
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
		return new ModelAndView("redirect:/report/IndoorStaffIndividualProgress/home");
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
	 * Get batch select format
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryBatchSelect2(Select2RequestModel requestModel) {
		try {
			return batchService.queryBatchSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
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
	public String submitReportRequest(IndoorStaffIndividualProgressCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		this.generateReport(criteria, userId, redirectAttributes, locale, 2);	
		return "redirect:/report/IndoorStaffIndividualProgress/home";
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
	
	/**
	 * Get officer select format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOfficerSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "refMonth", required = false) String refMonth,
			@RequestParam(value = "userIds", required = false) Integer[] userIds,
			@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {
			Date start = null;
			Date end = null;
			if (StringUtils.isNotEmpty(startDate)){
				start = commonService.getMonth(startDate);
			}
			if (StringUtils.isNotEmpty(endDate)){
				end = commonService.getMonth(endDate);
			}
			
			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, start, end, null, null, staffType);
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	
	@RequestMapping(value = "queryUserSelectMutiple")
	public @ResponseBody List<KeyValueModel> queryUserSelectMutiple(@RequestParam(value = "id[]") Integer[] ids) {
		try {
			return userService.getKeyValueByIds(ids);
			
		} catch (Exception e) {
			logger.error("queryUserSelectMutiple", e);
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
}
