package com.kinetix.component;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import capi.audit.entity.AccessLog;
import capi.entity.User;
import capi.service.UserService;

@Component("authenticationSuccessHandler")
public class CapiSuccessHandler implements AuthenticationSuccessHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(CapiSuccessHandler.class);
	@Autowired
	private UserService service;	
	
	@Resource(name = "sessionFactory_audit")
	SessionFactory factory;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest arg0,
			HttpServletResponse arg1, Authentication arg2) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		logger.info("Login Success: "+arg0.getParameter("username"));		
		String contextPath = arg0.getContextPath();		
		
		// Create Access Log
		AccessLog log = new AccessLog();
		log.setUrl("Login");
		log.setUsername(arg2.getName());
		log.setCreatedDate(new Date());
		
		String forwarded = arg0.getHeader("X-Forwarded-For");
		if (StringUtils.isEmpty(forwarded)) {
			log.setRemoteAddress(arg0.getRemoteAddr());
		} else {
			log.setRemoteAddress(forwarded);
		}
		
		Session session = factory.openSession();
		
		try {
			session.save(log);
			session.flush();
		} catch (HibernateException ex) {
			logger.error("Access Log Error", ex);
		} finally {
			session.close();
		}
		
		if (arg0.getRequestURI().startsWith(contextPath+"/api")){			
			String deviceToken = arg0.getParameter("deviceToken");
			service.successfullLogin(arg2.getName(), deviceToken);			
			arg1.sendRedirect(contextPath+"/api/ApiAccess/loginSuccess");
		}
		else{
			
			service.successfullLogin(arg2.getName());
			
			//TODO CR6 REQ005
			User user = service.getUser(arg0.getParameter("username"));
			if (service.isPasswordExpired(user)) {
				arg1.sendRedirect(contextPath+"/userAccountManagement/ChangeExpiredPassword/edit");
			} else {
				service.sendChangePasswordReminder(user);
				arg1.sendRedirect(contextPath+"/");
			}

			
			
		}
		
	}

}
