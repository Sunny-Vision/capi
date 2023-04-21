package com.kinetix.component;

import java.io.IOException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import capi.entity.User;
import capi.service.UserLockedTimeLogService;
import capi.service.UserService;
import capi.service.userAccountManagement.PasswordPolicyService;

@Component("authenticationFailureHandler")
public class CapiFailureHandler implements AuthenticationFailureHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(CapiFailureHandler.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
		
	@Autowired
	private UserService service;
	
	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	@Autowired
	private UserLockedTimeLogService userLockedTimeLogService;
	
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest arg0,
			HttpServletResponse arg1, AuthenticationException arg2)
			throws IOException, ServletException {
				
		System.out.println("=====================CapiFailureHandler===========onAuthenticationFailure");
		// TODO Auto-generated method stub
		String message = "";
		logger.info("Login Failure: "+arg0.getParameter("username"));
		User user = service.getUser(arg0.getParameter("username"));
		if (user == null || user.getStatus().equals("Inactive")){
			message = "E00067";
		}
		else if (user.getStatus().equals("Locked")){
			message = "E00167";
			//message = "E00068";
		}
		
		else {
			User activeUser = service.getActiveUser(arg0.getParameter("username"), arg0.getParameter("password"), true);
			
			System.out.println("====password in Fail Handler"+arg0.getParameter("password"));
			 if (activeUser != null) { 
					System.out.println("====right password but expired");
					message = "E00159";
			} else {
				Integer attempt = user.getAttemptNumber();
				boolean locked = false;
				if (attempt == null){
					user.setAttemptNumber(1); 
				}
				else {
					attempt++;
					
					//TODO CR6 REQ002
					Integer maxAttempt = passwordPolicyService.getMaxAttemptInteger();
					System.out.println("maxAttempt:"+maxAttempt);
					
					if (attempt >= maxAttempt){
						user.setStatus("Locked");
						locked = true;
						//TODO CR6 REQ01
						userLockedTimeLogService.saveUserLockedTime(user.getUsername());
					}
					//TODO CR6 REQ003
					//userLockedTimeLogService.saveUserLastAttemptTime(user.getUsername());
					user.setAttemptNumber(attempt);
				}	
				
				service.saveUser(user);
				
				if (locked) {
					message = "E00167";
				} else {
					message = "E00067";
				}
			}
			
			
			
		}
		
		String contextPath = arg0.getContextPath();
		message = URLEncoder.encode(message, "UTF-8");
	
		
		if (arg0.getRequestURI().startsWith(contextPath+"/api")){
			arg1.sendRedirect(contextPath+"/api/ApiAccess/loginError?error="+message);
		}
		else{		
			arg1.sendRedirect(contextPath+"/Access/login?error="+message);
			  
		}
		
	}

}
