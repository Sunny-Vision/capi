package com.kinetix.api;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.api.LoginResultModel;
import capi.model.api.MobileUserModel;
import capi.service.UserService;
import capi.service.userAccountManagement.PasswordPolicyService;

@Controller("ApiAccessController")
@RequestMapping("api/ApiAccess")
public class ApiAccessController {

	@Autowired
	private UserService service;
	
	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	
	@Resource(name = "messageSource")
	private MessageSource messageSource;
	
	@RequestMapping("login")
	public @ResponseBody String login(){
		return "OK";
	}
	
	@RequestMapping("invalidLogin")
	public @ResponseBody LoginResultModel invalidLogin(Locale locale){
		LoginResultModel model = new LoginResultModel();
		model.setStatusString("INVALID");
		model.setStatus(LoginResultModel.INVALID);
		model.setMessage(messageSource.getMessage("E00103", null, locale));
		return model;				
	}
	
	@RequestMapping("loginError")
	public @ResponseBody LoginResultModel loginError(String error, Locale locale){
		LoginResultModel model = new LoginResultModel();
		model.setStatusString("FAIL");
		model.setStatus(LoginResultModel.FAIL);
		
		//TODO CR6 REQ001
		if (error.equals("E00167")) {
			model.setMessage(messageSource.getMessage("E00167", new Object[]{passwordPolicyService.getParameters().getUnlockDuration()}, locale));
		} else {
			model.setMessage(messageSource.getMessage(error, null, locale));
		}
		
		return model;
	}
	
	@RequestMapping("loginSuccess")
	public @ResponseBody LoginResultModel loginSuccess(Authentication auth){
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		User user = service.getUserById(detail.getUserId());
		MobileUserModel userModel = new MobileUserModel();
		BeanUtils.copyProperties(user, userModel);
		CollectionUtils.addAll(userModel.getAccessRight(), detail.getFunctionList().iterator());
		userModel.setAuthorityLevel(detail.getAuthorityLevel());
		LoginResultModel model = new LoginResultModel();
		model.setMessage("Login Successfully");
		model.setUser(userModel);
		model.setStatus(LoginResultModel.SUCCESS);
		model.setStatusString("SUCCESS");
		return model;
	}
}
