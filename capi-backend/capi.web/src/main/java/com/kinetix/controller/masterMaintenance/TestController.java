package com.kinetix.controller.masterMaintenance;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kinetix.component.FuncCode;

import capi.dal.OutletDao;
import capi.service.UserService;


/**
 * Handles requests for the application home page.
 */
@FuncCode("RF1901")
@Controller("TestController")
@RequestMapping("masterMaintenance/Test")
public class TestController {
	
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	UserService service;
	
	@Autowired
	OutletDao dao;
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model) {
		
		// get the message
		String message = messageSource.getMessage("E00018", null, locale);
		logger.info(message);		
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate);
	}
	
	@RequestMapping(value = "redirectTest", method = RequestMethod.GET)
	public String redirectTest(){
		return "redirect:/masterMaintenance/Test/test";
	}
	
	
	@Secured("UF1001") // user has the role UF1001
	@PreAuthorize("hasPermission(#user, 2)") // user has permission 2
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test(Locale locale, Model model) {
		
		logger.info("Welcome Test! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
		
	
}
