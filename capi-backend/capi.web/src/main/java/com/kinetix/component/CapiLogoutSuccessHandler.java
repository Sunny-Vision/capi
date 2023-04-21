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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import capi.audit.entity.AccessLog;

@Component("CapiLogoutSuccessHandler")
public class CapiLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CapiLogoutSuccessHandler.class);

	@Resource(name = "sessionFactory_audit")
	SessionFactory factory;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		if ( authentication != null ) {
			AccessLog log = new AccessLog();
			log.setUrl("Logout");
			log.setUsername(authentication.getName());
			log.setCreatedDate(new Date());
			
			String forwarded = request.getHeader("X-Forwarded-For");
			if (StringUtils.isEmpty(forwarded)) {
				log.setRemoteAddress(request.getRemoteAddr());
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
		}
		
		String contextPath = request.getContextPath();
		
		if (request.getRequestURI().startsWith(contextPath + "/api")) {
			// Redirect to link for mobile login
			response.sendRedirect(String.format("%s/api/ApiAccess/login", contextPath));
		} else {
			// Redirect to link for normal login
			response.sendRedirect(String.format("%s/Access/login", contextPath));
		}
	}

}
