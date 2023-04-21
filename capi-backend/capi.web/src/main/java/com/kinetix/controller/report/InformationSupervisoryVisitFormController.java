package com.kinetix.controller.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.report.InformationSupervisoryVisitFormCriteria;
import capi.model.report.ReportTaskList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.qualityControlManagement.SupervisoryVisitService;
import capi.service.report.ReportService;
import capi.service.report.ReportServiceBase;

import capi.model.CapiWebAuthenticationDetails;
import com.kinetix.component.FuncCode;

@Secured("RF9046")
@FuncCode("RF9046")
@Controller("InformationSupervisoryVisitFormController")
@RequestMapping("report/InformationSupervisoryVisitForm")
public class InformationSupervisoryVisitFormController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(InformationSupervisoryVisitFormController.class);

	@Autowired
	@Qualifier( "InformationSupervisoryVisitFormService")
	private ReportService progressService;

	@Autowired
	private SupervisoryVisitService supervisoryVisitService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;

	@RequestMapping("home")
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
		return new ModelAndView("redirect:/report/InformationSupervisoryVisitForm/home");
	}

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(InformationSupervisoryVisitFormCriteria criteria, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		this.generateReport(criteria, userId, redirectAttributes, locale, ReportServiceBase.DOCX);		
		return "redirect:/report/InformationSupervisoryVisitForm/home";
	}

	@RequestMapping(value = "getSVDates")
	public @ResponseBody List<String> getSVDates(String date){
		if(!StringUtils.isEmpty(date)) {
			try{
				return supervisoryVisitService.getVisitDatesByRefMonth(date);
			}
			catch(Exception ex){
				logger.error("get Supervisory Visit Date(s) fail", ex);
			}
		}
		return new ArrayList<String>();
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
			if(StringUtils.isNotEmpty(startDate)){
				start = commonService.getMonth(startDate);
				start = DateUtils.addMonths(start, 1);
				start = DateUtils.addDays(start, -1);
			}
			if(StringUtils.isNotEmpty(endDate)){
				end = commonService.getMonth(endDate);
			}
			
			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, start, end, null, null, staffType);
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single unit
	 */
	@RequestMapping(value = "queryUserSelectMutiple")
	public @ResponseBody List<KeyValueModel> queryUserSelectMutiple(@RequestParam(value = "id[]") Integer[] ids) {
		try {
			return userService.getKeyValueByIds(ids);
			
		} catch (Exception e) {
			logger.error("queryUserSelectMutiple", e);
		}
		return null;
	}
}
