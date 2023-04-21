package com.kinetix.component;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class DisableCacheFilter implements Filter {
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {      
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.addHeader("Pragma", "no-cache");
		httpResponse.addHeader("Cache-Control", "no-cache");
		httpResponse.addHeader("Cache-Control", "no-store");
		httpResponse.addHeader("Cache-Control", "must-revalidate");
		httpResponse.addHeader("Cache-Control", "private");
		httpResponse.addHeader("Expires", "Sat, 1 Jan 2000 00:00:00 GMT");
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
}
