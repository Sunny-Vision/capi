package com.kinetix.component;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import capi.audit.entity.AccessLog;
import capi.service.CommonService;

@Component("CapiWebInterceptor")
public class CapiWebInterceptor implements HandlerInterceptor{
	

	private static final Logger logger = LoggerFactory.getLogger(CapiWebInterceptor.class);
	
	@Resource(name="sessionFactory_audit")
	SessionFactory factory;
	
	@Autowired
	private CommonService commonService;

	
	private String getUserName(){
		SecurityContext context = SecurityContextHolder.getContext();
		if (context!=null){
			Authentication auth = context.getAuthentication();
			if (auth != null){
				return auth.getName();
			}			
		}		
		return null;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		
		if (handler instanceof HandlerMethod){
			String url =request.getRequestURI();
			
			AccessLog log = new AccessLog();
			log.setUrl(url);
			log.setUsername(getUserName());
			log.setCreatedDate(new Date());
			
			String forwarded = request.getHeader("X-Forwarded-For");			
			if (StringUtils.isEmpty(forwarded)){
				log.setRemoteAddress(request.getRemoteAddr());
			}
			else{
				log.setRemoteAddress(forwarded);
			}
			
			
			Session session =factory.openSession();
			try{
				session.save(log);
				session.flush();
			}
			catch(Exception ex){
				logger.error("Access Log error", ex);
			}
			finally{
				session.close();
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// TODO Auto-generated method stub
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			Object controller = method.getBean();
			FuncCode code = controller.getClass().getAnnotation(FuncCode.class);

			if (code != null && modelAndView != null) {
				String viewName = modelAndView.getViewName();
				if (modelAndView.getView() instanceof RedirectView
						|| viewName != null && viewName.startsWith("redirect:")) {
					return;
				}
				String value = code.value();
				modelAndView.addObject("currentFunc", value);
				modelAndView.addObject("commonService", commonService);
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		if (ex != null){
			logger.error("Unhandle error", ex);
		}
	}

}
