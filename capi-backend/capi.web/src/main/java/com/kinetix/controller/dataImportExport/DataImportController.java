package com.kinetix.controller.dataImportExport;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.ImportExportTask;
import capi.entity.ImportExportTaskDefinition;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.dataImportExport.ImportTaskList;
import capi.service.dataImportExport.ImportExportTaskService;
import capi.service.productMaintenance.ProductService;

import com.kinetix.batch.DataImportTask;
import com.kinetix.component.FuncCode;

@Secured("UF2401")
@FuncCode("UF2401")
@Controller("DataImportController")
@RequestMapping("dataImportExport/DataImport")
public class DataImportController {

	private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);
	
	@Autowired
	private ImportExportTaskService importExportService;
	
	@Autowired
	private DataImportTask dataImportTask;
	
	@Autowired
	private ProductService productService;
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, Integer dataType){		
		List<ImportExportTaskDefinition> defs = importExportService.getImportDefinition();
		model.addAttribute("definitions", defs);
		model.addAttribute("dataType", dataType);
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	public @ResponseBody DatatableResponseModel<ImportTaskList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return importExportService.getImportTaskList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	@RequestMapping(value = "importFile", method = RequestMethod.POST)
	@PreAuthorize("hasPermission(#user, 256)")
	public String importFile(Integer taskNo, @RequestParam("importData") MultipartFile importData, Locale locale, 
			Integer productGroupId, String referenceMonth, String effectiveDate, String cpiBasePeriod,
			Authentication auth, RedirectAttributes redirectAttributes){
		try{
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			
			String fileName = importData.getOriginalFilename();
			Pattern pattern = Pattern.compile(".*\\.([^.]+$)");
			Matcher matcher = pattern.matcher(fileName);
			if (matcher.matches()){
				String ext = matcher.group(1);
				if (!ext.equalsIgnoreCase("xlsx")){
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
					return "redirect:/dataImportExport/DataImport/home";
				}				
			}
			else{
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
				return "redirect:/dataImportExport/DataImport/home";
			}
			InputStream stream = importData.getInputStream();
			ImportExportTask task = importExportService.createImportTask(stream, taskNo, userId, productGroupId, referenceMonth, effectiveDate, cpiBasePeriod);
			dataImportTask.dataImportAsync(task.getId(), taskNo);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00008", null, locale));
		}
		catch (Exception ex){
			logger.error("Import file failed", ex);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00078", null, locale));
		}
		
		return "redirect:/dataImportExport/DataImport/home";
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
	
	
}
