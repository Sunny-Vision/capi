package com.kinetix.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import capi.service.userAccountManagement.PasswordPolicyService;

@Controller("AccessController")
@RequestMapping(value = "Access")
public class AccessController {

	private static final Logger logger = LoggerFactory.getLogger(AccessController.class);
	
	@Autowired
	private PasswordPolicyService service;
	
	@RequestMapping(value = "login")
	public void login (String error, String success, Model model){	
		model.addAttribute("error", error);
		model.addAttribute("success", success);
		
		model.addAttribute("lockoutDuration", service.getParameters().getUnlockDuration());
	}
		

}
