package com.kinetix.controller.report;

import java.util.ArrayList;
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
import org.springframework.ui.Model;
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
import capi.model.report.InformationTimeLogFormCriteria;
import capi.model.report.ReportTaskList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import capi.service.report.ReportService;
import capi.service.timeLogManagement.TimeLogService;
import capi.model.CapiWebAuthenticationDetails;
import com.kinetix.component.FuncCode;

@Secured("RF9047")
@FuncCode("RF9047")
@Controller("InformationTimeLogFormController")
@RequestMapping("report/InformationTimeLogForm")
public class InformationTimeLogFormController extends ReportBaseController{

	private static final Logger logger = LoggerFactory.getLogger(InformationTimeLogFormController.class);

	@Autowired
	@Qualifier( "InformationTimeLogFormService")
	private ReportService progressService;

	@Autowired
	private TimeLogService timeLogService;
	
	@Autowired
	private CalendarEventService calendarEventService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;
	
	@RequestMapping("home")
	public void home(Model model, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		model.addAttribute("userIds", userIds);
		int authorityLevel = 0;
		if ((detail.getAuthorityLevel() & 2) > 0){
			authorityLevel = 20;
		} else if ((detail.getAuthorityLevel() & 4) > 0){
			authorityLevel = 16;
		}
		model.addAttribute("authorityLevel", authorityLevel);
	}

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
		return new ModelAndView("redirect:/report/InformationTimeLogForm/home");
	}

	@RequestMapping(value = "submitReportRequest")
	public String submitReportRequest(InformationTimeLogFormCriteria criteria, Integer taskType, Authentication auth, RedirectAttributes redirectAttributes, Locale locale){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		Integer userId = detail.getUserId();		
		criteria.setUserId(userId);
		criteria.setAuthorityLevel(detail.getAuthorityLevel());
		this.generateReport(criteria, userId, redirectAttributes, locale, taskType);		
		return "redirect:/report/InformationTimeLogForm/home";
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
			Date referenceMonth = null;
			if(StringUtils.isNotEmpty(startDate)){
				start = commonService.getDate(startDate);
			}
			if(StringUtils.isNotEmpty(endDate)){
				end = commonService.getDate(endDate);
			}
			if(StringUtils.isNotEmpty(refMonth)){
				referenceMonth = commonService.getDate(refMonth);
			}
			
			return userService.queryReportOfficerSelect2(requestModel, authorityLevel, start, end, referenceMonth, userIds, staffType);
		} catch (Exception e) {
			logger.error("queryTeamSelect2", e);
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
	
	@Override
	public ReportService getReportService() {
		// TODO Auto-generated method stub
		return progressService;
	}

}
