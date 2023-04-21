package com.kinetix.controller.commonDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.SurveyMonth;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.UserLookupTableList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import capi.service.lookup.ReportUserLookupService;


/**
 * User lookup
 */
@Secured({"UF1301", "UF1302", "UF1303", "UF1501", "UF1503", "UF1506", "UF2102", "UF2103", "RF2001", "RF2005", "RF2006", "RF9003", "RF9039", "RF9040", "RF9045", "RF9046"})
@Controller("ReportUserLookupController")
@RequestMapping("commonDialog/ReportUserLookup")
public class ReportUserLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReportUserLookupController.class);

	@Autowired
	private ReportUserLookupService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SurveyMonthService surveyMonthService;
	
	@Autowired
	private CommonService commonService;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<UserLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth,
				@RequestParam(value = "team", required = false) String team,
				@RequestParam(value = "excludedIds[]", required = false) Integer[] excludedIds,
				@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
				@RequestParam(value = "startDate", required = false) String startDate,
				@RequestParam(value = "endDate", required = false) String endDate,
				@RequestParam(value = "refMonth", required = false) String refMonth,
				@RequestParam(value = "userIds[]", required = false) Integer[] userIds,
				@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {	
			Date start = null;
			Date end = null;
			Date refMonthStart = null;
			Date refMonthEnd = null;
			
			if(StringUtils.isNotEmpty(startDate)){
				if ((startDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					start = commonService.getDate(startDate);
				} else {
					start = commonService.getMonth(startDate);
					//start = DateUtils.addMonths(start, 1);
					//start = DateUtils.addDays(start, -1);
				}
			}
			if(StringUtils.isNotEmpty(endDate)){
				if ((endDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					end = commonService.getDate(endDate);
				} else {
					end = commonService.getMonth(endDate);
					end = DateUtils.addMinutes(DateUtils.ceiling(end, Calendar.MONTH), -1);
				}
			}
			
			System.out.println("startDate"+startDate);
			System.out.println("endDate"+endDate);
			System.out.println("refMonthStart"+refMonthStart);
			System.out.println("refMonthEnd: " + refMonthEnd);
			if(StringUtils.isNotEmpty(refMonth)){
				Date referenceMonth = null;
				if ((refMonth.matches("\\d{2}-\\d{2}-\\d{4}"))){
					referenceMonth = commonService.getDate(refMonth);
				} else {
					referenceMonth = commonService.getMonth(refMonth);
				}
				
				SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(referenceMonth);
				refMonthStart = surveyMonth.getStartDate();
				refMonthEnd = surveyMonth.getEndDate();
			}
			
			return service.getReportLookupTableList(requestModel, authorityLevel
					, team, excludedIds, start, end, refMonthStart, refMonthEnd, userIds, staffType);
			
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search, Authentication auth,
			@RequestParam(value = "team", required = false) String team,
			@RequestParam(value = "excludedIds[]", required = false) Integer[] excludedIds,
			@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "refMonth", required = false) String refMonth,
			@RequestParam(value = "userIds[]", required = false) Integer[] userIds,
			@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {		
			Date start = null;
			Date end = null;
			Date refMonthStart = null;
			Date refMonthEnd = null;
			
			if(StringUtils.isNotEmpty(startDate)){
				if ((startDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					start = commonService.getDate(startDate);
				} else {
					start = commonService.getMonth(startDate);
					start = DateUtils.addMonths(start, 1);
					start = DateUtils.addDays(start, -1);
				}
			}
			if(StringUtils.isNotEmpty(endDate)){
				if ((endDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					end = commonService.getDate(endDate);
				} else {
					end = commonService.getMonth(endDate);
					end = DateUtils.addMinutes(DateUtils.ceiling(end, Calendar.MONTH), -1);
				}
			}
			if(StringUtils.isNotEmpty(refMonth)){
				Date referenceMonth = null;
				if ((refMonth.matches("\\d{2}-\\d{2}-\\d{4}"))){
					referenceMonth = commonService.getDate(refMonth);
				} else {
					referenceMonth = commonService.getMonth(refMonth);
				}
				
				SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(referenceMonth);
				refMonthStart = surveyMonth.getStartDate();
				refMonthEnd = surveyMonth.getEndDate();
			}
			
			List<Integer> list = service.getReportLookupTableSelectAll(search, authorityLevel
					, team, excludedIds, start, end, refMonthStart, refMonthEnd, userIds, staffType);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
	
	/**
	 * Get team select2 format
	 */
	@RequestMapping(value = "queryTeamSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryTeamSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return userService.queryTeamSelect2(requestModel, false);
		} catch (Exception e) {
			logger.error("queryTeamSelect2", e);
		}
		return null;
	}
	
	/**
	 * datatable query function with correct search
	 * Called by
	 * 		MB9032, 
	 */
	@RequestMapping(value = "query2")
	public @ResponseBody DatatableResponseModel<UserLookupTableList>
		query2(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth,
				@RequestParam(value = "team", required = false) String team,
				@RequestParam(value = "excludedIds[]", required = false) Integer[] excludedIds,
				@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
				@RequestParam(value = "startDate", required = false) String startDate,
				@RequestParam(value = "endDate", required = false) String endDate,
				@RequestParam(value = "refMonth", required = false) String refMonth,
				@RequestParam(value = "userIds[]", required = false) Integer[] userIds,
				@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {	
			Date start = null;
			Date end = null;
			Date refMonthStart = null;
			Date refMonthEnd = null;
			
			if(StringUtils.isNotEmpty(startDate)){
				if ((startDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					start = commonService.getDate(startDate);
				} else {
					start = commonService.getMonth(startDate);
//					start = DateUtils.addMonths(start, 1);
//					start = DateUtils.addDays(start, -1);
				}
			}
			if(StringUtils.isNotEmpty(endDate)){
				if ((endDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					end = commonService.getDate(endDate);
				} else {
					end = commonService.getMonth(endDate);
					end = DateUtils.addMinutes(DateUtils.ceiling(end, Calendar.MONTH), -1);
				}
			}
			if(StringUtils.isNotEmpty(refMonth)){
				Date referenceMonth = null;
				if ((refMonth.matches("\\d{2}-\\d{2}-\\d{4}"))){
					referenceMonth = commonService.getDate(refMonth);
				} else {
					referenceMonth = commonService.getMonth(refMonth);
				}
				
				SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(referenceMonth);
				refMonthStart = surveyMonth.getStartDate();
				refMonthEnd = surveyMonth.getEndDate();
			}
			
			return service.getReportLookupTableList2(requestModel, authorityLevel
					, team, excludedIds, start, end, refMonthStart, refMonthEnd, userIds, staffType);
			
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * datatable select all with correct search
	 * Called by
	 * 		MB9032, 
	 */
	@RequestMapping(value = "getLookupTableSelectAll2", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll2(String search, Authentication auth,
			@RequestParam(value = "team", required = false) String team,
			@RequestParam(value = "excludedIds[]", required = false) Integer[] excludedIds,
			@RequestParam(value = "authorityLevel", required = false) Integer authorityLevel,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "refMonth", required = false) String refMonth,
			@RequestParam(value = "userIds[]", required = false) Integer[] userIds,
			@RequestParam(value = "staffType", required = false) Integer staffType) {
		try {		
			Date start = null;
			Date end = null;
			Date refMonthStart = null;
			Date refMonthEnd = null;
			
			if(StringUtils.isNotEmpty(startDate)){
				if ((startDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					start = commonService.getDate(startDate);
				} else {
					start = commonService.getMonth(startDate);
//					start = DateUtils.addMonths(start, 1);
//					start = DateUtils.addDays(start, -1);
				}
			}
			if(StringUtils.isNotEmpty(endDate)){
				if ((endDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					end = commonService.getDate(endDate);
				} else {
					end = commonService.getMonth(endDate);
					end = DateUtils.addMinutes(DateUtils.ceiling(end, Calendar.MONTH), -1);
				}
			}
			if(StringUtils.isNotEmpty(refMonth)){
				Date referenceMonth = null;
				if ((refMonth.matches("\\d{2}-\\d{2}-\\d{4}"))){
					referenceMonth = commonService.getDate(refMonth);
				} else {
					referenceMonth = commonService.getMonth(refMonth);
				}
				
				SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(referenceMonth);
				refMonthStart = surveyMonth.getStartDate();
				refMonthEnd = surveyMonth.getEndDate();
			}
			
			List<Integer> list = service.getReportLookupTableSelectAll2(search, authorityLevel
					, team, excludedIds, start, end, refMonthStart, refMonthEnd, userIds, staffType);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
}