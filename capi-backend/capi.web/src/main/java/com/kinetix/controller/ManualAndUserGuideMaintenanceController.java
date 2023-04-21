package com.kinetix.controller;

import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.SystemConstant;
import capi.service.OnlineHelpService;

@Secured("UF2301")
@FuncCode("UF2301")
@Controller("ManualAndUserGuideMaintenanceController")
@RequestMapping("ManualAndUserGuide")
public class ManualAndUserGuideMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManualAndUserGuideMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private OnlineHelpService service;
	
	@RequestMapping("home")
	public void home(){
		
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "uploadWebHelp", method = RequestMethod.POST)
	public String uploadWebHelp(Integer taskNo, @RequestParam("webHelp") MultipartFile webHelp, Locale locale, 
			Integer productGroupId, String referenceMonth, String effectiveDate, String cpiBasePeriod,
			Authentication auth, RedirectAttributes redirectAttributes){
		try{
			
			String fileName = webHelp.getOriginalFilename();
			Pattern pattern = Pattern.compile(".*\\.([^.]+$)");
			Matcher matcher = pattern.matcher(fileName);
			
			if (matcher.matches()){
				String ext = matcher.group(1);
				if (!ext.equalsIgnoreCase("zip")){
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
					return "redirect:/dataImportExport/DataImport/home";
				}				
			}
			else{
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
				return "redirect:/dataImportExport/DataImport/home";
			}
			InputStream stream = webHelp.getInputStream();
			service.uploadWebOnlineHelp(stream);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00008", null, locale));
		}
		catch (Exception ex){
			logger.error("Import file failed", ex);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00078", null, locale));
		}
		
		return "redirect:/ManualAndUserGuide/home";
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "exportWebHelp")
	public ModelAndView exportWebHelp(ModelAndView mav, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response){
		try {
			response.setContentType("application/zip");	
	       	        
	        response.setHeader("Content-Disposition","attachment; filename=\"onlineHelp.zip\"");
	 
	        service.exportWebOnlineHelp(response.getOutputStream());
	        return null;
	        
		} catch (Exception e) {
			logger.error("Download file fail", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00083", null, locale));
		}
		
		return new ModelAndView("redirect:/ManualAndUserGuide/home");
	}
	
}
