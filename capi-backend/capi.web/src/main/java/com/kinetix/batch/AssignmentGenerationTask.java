package com.kinetix.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;

@Component("AssignmentGenerationTask")
public class AssignmentGenerationTask {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentGenerationTask.class);
	
	@Autowired
	private SurveyMonthService surveyMonthService;
	
	
	@Async
	public void generateAssignment(Integer surveyMonthId){
		
		try{
			surveyMonthService.setSurveyMonthStatus(2, surveyMonthId);
			surveyMonthService.generateSurveyMonthDetails(surveyMonthId);
			surveyMonthService.setSurveyMonthStatus(5, surveyMonthId);
		}
		catch(Exception ex){
			logger.error("Assignment generation failed", ex);
			surveyMonthService.setSurveyMonthStatus(4, surveyMonthId);
		}
		
	}
	
}
