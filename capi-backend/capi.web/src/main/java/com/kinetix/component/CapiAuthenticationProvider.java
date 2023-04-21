package com.kinetix.component;

import java.util.ArrayList;
import java.util.List;






import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;










import capi.entity.Role;
import capi.entity.SystemFunction;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.UserAccessModel;
import capi.service.UserService;
import capi.service.userAccountManagement.PasswordHistoryService;
import capi.service.userAccountManagement.RoleService;

@Component("auth-provider")
public class CapiAuthenticationProvider implements AuthenticationProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(CapiAuthenticationProvider.class);
	public static final String USER_PASSWORD_SESSION_ATTRIBUTE_KEY = "CR12-user-pw";
	
	@Autowired
	private UserService service;
	
	@Autowired
	private PasswordHistoryService passwordHistoryService;
	
	@Autowired
	private HttpServletRequest req;
	
	@Autowired
	private StandardPBEStringEncryptor pbeEncryptor;
		
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	logger.info("Authentication");
    	String name = authentication.getName();
    	String password = authentication.getCredentials().toString();

    	try{  
    		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)authentication.getDetails();
    		//boolean fromMobile = req.getRequestURI().startsWith(req.getContextPath()+"/api");
//    		System.out.println("From Mobile:"+fromMobile);
//    		System.out.println("Password:"+password);
    		
    		//TODO CR6 REQ 001
    		//TODO CR6 REQ 003
    		service.resetStatusAndAttemptByDuration(name);
    		
	    	User user = service.getActiveUser(name, password, detail.isFromMobile());
	    	if (user != null){
	    		
	    		//TODO CR6 REQ005 
	    		if (!passwordHistoryService.isUserPasswordHistoryExsit(user)) {
	    			passwordHistoryService.savePasswordHistory(user, password, false);
	    		}
	    		
	    		boolean isPasswordExpired = service.isPasswordExpired(user);
	    		
	    		if (detail.isFromMobile() && isPasswordExpired) {
	    			return null;
	    		}
	    		
	   
	    		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
	    		UserAccessModel model = service.gatherUserRequiredInfo(user);
	    		if (isPasswordExpired) {
		    		grantedAuths.add(new SimpleGrantedAuthority("UF1308"));
	    		} else {
		    		for (String code : model.getFunctionList()){
		    			grantedAuths.add(new SimpleGrantedAuthority(code));
		    		}
	    		}
	    		BeanUtils.copyProperties(model, detail);
	    		
	    		
	    		Authentication auth = new UsernamePasswordAuthenticationToken(name, user.getPassword(), grantedAuths);
	    		// Added 2020-09-29 Toby: (CR12) store encrypted user password (plaintext) in session for password-protected file in data export/report
	    		req.getSession().setAttribute(USER_PASSWORD_SESSION_ATTRIBUTE_KEY, pbeEncryptor.encrypt(password));
	    		
	            return auth;
	    	}
    	}
    	catch(Exception ex){
    		logger.error("Query user failure", ex);
    	}
    	
    	logger.info("Cannot find user");
        return null;        
    }
 
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
