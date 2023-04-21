package com.kinetix.controller.dataImportExport;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.ImportExportTask;
import capi.entity.ImportExportTaskDefinition;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.dataImportExport.ImportTaskList;
import capi.service.POIFileService;
import capi.service.UserService;
import capi.service.dataImportExport.ImportExportTaskService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.SubPriceService;
import capi.service.masterMaintenance.UnitService;
import capi.service.productMaintenance.ProductService;

import com.kinetix.batch.DataExportTask;
import com.kinetix.component.CapiAuthenticationProvider;
import com.kinetix.component.FuncCode;

@Secured("UF2402")
@FuncCode("UF2402")
@Controller("DataExportController")
@RequestMapping("dataImportExport/DataExport")
public class DataExportController {

	private static final Logger logger = LoggerFactory.getLogger(DataExportController.class);
	
	@Autowired
	private ImportExportTaskService importExportService;
	
	@Autowired
	private DataExportTask dataExportTask;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private SubPriceService subPriceService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private StandardPBEStringEncryptor pbeEncryptor;
	
	@Autowired
	private POIFileService poiFileService;
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, Integer dataType){		
		List<ImportExportTaskDefinition> defs = importExportService.getExportDefinition();
		model.addAttribute("definitions", defs);
		model.addAttribute("dataType", dataType);
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	public @ResponseBody DatatableResponseModel<ImportTaskList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		// 2020-09-29: CR12 - Hidden not related task with user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails details = (CapiWebAuthenticationDetails) authentication.getDetails();
		Integer creatorId = details.getUserId();
		
		try {
			return importExportService.getExportTaskList(requestModel, creatorId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	@RequestMapping(value = "exportFile", method = RequestMethod.POST)
	@PreAuthorize("hasPermission(#user, 256)")
	public String exportFile(Integer taskNo, Locale locale, 
			Integer productGroupId, String referenceMonth,
			String cpiBasePeriod, Integer subPriceTypeId, 
			@RequestParam(value = "timeLogPeriodStart", required = false) String timeLogPeriodStart,
			@RequestParam(value = "timeLogPeriodEnd", required = false) String timeLogPeriodEnd,
			@RequestParam(value = "timeLogUserIds", required = false) String timeLogUserIds,
			Integer purposeId,
			Authentication auth, RedirectAttributes redirectAttributes){
		try{
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			ImportExportTask task = importExportService.createExportTask(taskNo, userId, productGroupId, referenceMonth, subPriceTypeId, cpiBasePeriod, timeLogUserIds, timeLogPeriodStart, timeLogPeriodEnd, purposeId);
			dataExportTask.dataExportAsync(task.getId(), taskNo);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}
		catch (Exception ex){
			logger.error("Export file failed", ex);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00080", null, locale));
		}
		
		return "redirect:/dataImportExport/DataExport/home";
	}

	@RequestMapping(value = "downloadFile", method = RequestMethod.GET)
	@PreAuthorize("hasPermission(#user, 256)")
	public ModelAndView downloadFile(Integer id, ModelAndView mav, HttpServletRequest request,
			RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails details = (CapiWebAuthenticationDetails) authentication.getDetails();
		Integer creatorId = details.getUserId();
		
		try {
			File file = importExportService.getExportFileById(id);
			ImportExportTask task = importExportService.getImportExportTask(id);
			ImportExportTaskDefinition taskDefinition = importExportService.getImportExportTaskDefinitionByTaskNo(task.getTaskDefinition().getTaskNo());

			// 2020-07-17: add CR11 role check for CR11 export file (see PIR-236)
			if (taskDefinition.getTaskNo() == 34 && request.isUserInRole("CR11") == false) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00172", new String[] { "CR11" }, locale));
			
			} else if (taskDefinition.getTaskNo() == 1 && request.isUserInRole("CR12") == false) {
				// Added 2020-10-05 Toby: add CR12 role check for CR12 export file (Outlet Maintenance)
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00172", new String[] { "CR12" }, locale));
				
			} else if (!task.getUser().getId().equals(creatorId)) {
				// 2020-09-29: CR12 - Deny download if task is not belong to this user 
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00080", null, locale));
			
			} else if (file.exists()) {
				// Added 2020-09-29 Toby: (CR12) "Set password (user password) for downloaded file"
				boolean needToProtectFile = taskDefinition.getTaskNo() == 1 // is "Outlet Maintenance" file
											|| taskDefinition.getTaskNo() == 34; // is "Outlet Name & Outlet Code" file
				if (needToProtectFile) {
					// get encrypted user password (plaintext) from session attribute to make password-protected file for download
					String userPwEncrypted = (String) request.getSession().getAttribute(CapiAuthenticationProvider.USER_PASSWORD_SESSION_ATTRIBUTE_KEY);
					
					File tempFile = poiFileService.makePasswordProtectedTempFile(file, pbeEncryptor.decrypt(userPwEncrypted), ".xslx");
					
					response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			        response.setContentLength((int)tempFile.length());
			        response.setHeader("Content-Disposition","attachment; filename=\""+taskDefinition.getTaskName()+".xlsx"+"\"");

			        FileCopyUtils.copy(new FileInputStream(tempFile), response.getOutputStream());
			        // clean up temp file
			        tempFile.delete();
			        
				} else {
					response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			        response.setContentLength((int)file.length());
//			        response.setHeader("Content-Disposition","attachment; filename=\""+file.getName()+"\"");
			        response.setHeader("Content-Disposition","attachment; filename=\""+taskDefinition.getTaskName()+".xlsx"+"\"");

			        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
				}
				
		        return null;
			
			} else {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00080", null, locale));
			}
		} catch (Exception e) {
			logger.error("Download file fail", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00083", null, locale));
		}
		
		return new ModelAndView("redirect:/dataImportExport/DataExport/home");
	}
	

	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryProductCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryProductCategorySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return productService.queryProductGroupSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryProductGroupSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get sub price select2 format
	 */
	@RequestMapping(value = "querySubPriceTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubPriceTypeSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return subPriceService.querySubPriceTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("querySubPriceTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get cpi base period select2 format
	 */
	@RequestMapping(value = "queryCPIBasePeriodSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryCPIBasePeriodSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return unitService.queryCPIBasePeriodWithNonEffectiveItemSelect2(requestModel);
		} catch (Exception e) {
			logger.error("querySubPriceTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryTimeLogUserSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTimeLogUserSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return userService.queryOfficerSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryTimeLogUserSelect2", e);
		}
		return null;
	}
	/**
	 * Get officer key value pair
	 */
	@RequestMapping(value = "getTimeLogUserIds", method = RequestMethod.POST)
	public @ResponseBody List<KeyValueModel> getTimeLogUserIds(Integer[] ids) {
		try {
			return userService.getStaffCodeChineseNameByIds(ids);
		} catch (Exception e) {
			logger.error("getTimeLogUserIds", e);
		}
		return null;
	}
	/**
	 * Get Team select format
	 */
	@RequestMapping(value = "queryTeamSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTeamSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			return userService.queryTeamSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryTeamSelect2", e);
		}
		return null;
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
	 * Get single purpose
	 */
	@RequestMapping(value = "queryPurposeSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryPurposeSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return purposeService.getPurposeSelectSingle(id);
			
		} catch (Exception e) {
			logger.error("queryPurposeSelectSingle", e);
		}
		return null;
	}
	
}
