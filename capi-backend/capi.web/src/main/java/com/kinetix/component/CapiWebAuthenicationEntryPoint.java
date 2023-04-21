package com.kinetix.component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component("authenticationEntryPoint")
public class CapiWebAuthenicationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public CapiWebAuthenicationEntryPoint(){
		super("/Access/login");
	}
	
	@Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException authException) 
            throws IOException, ServletException {
        
		String contextPath = request.getContextPath();		
        String header = request.getHeader("x-requested-with");
	    if(header != null && header.equals("XMLHttpRequest") || request.getRequestURI().startsWith(contextPath+"/api")){
	    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    } else {
            super.commence(request, response, authException);
        }
	    
    }
	
}
