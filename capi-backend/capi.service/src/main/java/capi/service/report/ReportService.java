package capi.service.report;

import javax.transaction.Transactional;

import capi.entity.ReportTask;


public interface ReportService {
	
	@Transactional
	public void generateReport(Integer taskId) throws Exception;
	
	public String getFunctionCode();
	
	@Transactional
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception ;
}
