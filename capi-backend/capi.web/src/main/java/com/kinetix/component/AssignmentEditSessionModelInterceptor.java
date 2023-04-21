package com.kinetix.component;

import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author Joe
 * @date 2021/5/12
 **/
@Component("assignmentEditSessionModelInterceptor")
public class AssignmentEditSessionModelInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionModelId = request.getParameter("sessionModelId");
        HttpSession httpSession = request.getSession();
        if (StringUtils.isEmpty(sessionModelId)){
            sessionModelId = UUID.randomUUID().toString();
        }
        //replace original session if exist
        SessionModel mappingSession = (SessionModel) httpSession.getAttribute(sessionModelId);
        if (mappingSession == null){
            mappingSession = new SessionModel();
            SessionModel sessionModel = (SessionModel) httpSession.getAttribute("sessionModel");
            if (sessionModel != null){
                BeanUtils.copyProperties(sessionModel,mappingSession);
            }
        }

        httpSession.setAttribute(sessionModelId,mappingSession);
        httpSession.setAttribute("sessionModel",mappingSession);
        request.setAttribute("sessionModelId",sessionModelId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
