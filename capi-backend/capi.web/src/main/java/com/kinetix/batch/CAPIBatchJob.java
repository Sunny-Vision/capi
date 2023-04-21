package com.kinetix.batch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import capi.service.batchJob.BatchJobService;

public class CAPIBatchJob  extends QuartzJobBean {
	
	private static final Logger logger = LoggerFactory.getLogger(CAPIBatchJob.class);
	
	@Autowired(required=false)
	private List<BatchJobService> batchJobs;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		logger.info("Batch job execution start");
		if (batchJobs != null && batchJobs.size() > 0){
			for(BatchJobService job : batchJobs){
				try{
					if ("Approved Quotation Record Data Conversion".equals(job.getJobName())){
						continue;
					}
					if (job.canRun()){
						logger.info(job.getJobName() + " running");
						job.runTask();
						logger.info(job.getJobName() + " finished");
					}
				}
				catch(Exception ex){
					logger.error(String.format("batch Job fail: (%s)", job.getJobName()), ex);
				}
			}
		}
		
		for(BatchJobService job : batchJobs){
			int i = 0;
			if ("Approved Quotation Record Data Conversion".equals(job.getJobName())){
				while(job.canRun()){
					try{
						Date date = new Date() ;
						SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
						dateFormat.format(date);
						if(dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("05:00"))){
							break;
						}
							
						logger.info(job.getJobName() + i + " running");
						job.runTask();
						logger.info(job.getJobName() + i + " finished");
							
					} catch(Exception ex){
						logger.error(String.format("batch Job fail: (%s)" + i, job.getJobName()), ex);
					}
					
					i++;
				};
			}
		}
		
		logger.info("Batch job execution end");
		
	}

}
