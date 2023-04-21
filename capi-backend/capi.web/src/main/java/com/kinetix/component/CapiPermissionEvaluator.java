package com.kinetix.component;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import capi.model.CapiWebAuthenticationDetails;

@Component("permissionEvaluator")
public class CapiPermissionEvaluator implements PermissionEvaluator{

	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		// TODO Auto-generated method stub

		if (authentication != null && permission instanceof Integer){
			Integer perm = (Integer)permission;
			CapiWebAuthenticationDetails details = (CapiWebAuthenticationDetails)authentication.getDetails();
			Integer roleList = details.getAuthorityLevel();		
			
			return (perm&roleList) == perm.intValue();
		}
		
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		// TODO Auto-generated method stub
		//return false;
		throw new RuntimeException("Id and Class permissions are not supperted by this application");
	}

}
