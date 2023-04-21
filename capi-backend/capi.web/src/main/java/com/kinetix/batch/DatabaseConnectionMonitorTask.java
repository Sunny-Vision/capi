package com.kinetix.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.dbcp2.BasicDataSource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DatabaseConnectionMonitorTask extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionMonitorTask.class);

	@Autowired
	protected ApplicationContext applicationContext;

	@Autowired
	protected BasicDataSource dataSource;

	@Autowired
	protected BasicDataSource dataSource_audit;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			//BasicDataSource dataSource = applicationContext.getBean("dataSource");
			createLog("Main", dataSource);
		} catch (Exception ex) {
			logger.warn("DatabaseConnectionMonitorTask(Main) failed", ex);
		}
		try {
			BasicDataSource dataSource = (BasicDataSource) applicationContext.getBean("dataSource_audit");
			createLog("Audit", dataSource);
		} catch (Exception ex) {
			logger.warn("DatabaseConnectionMonitorTask(Audit) failed", ex);
		}
	}

	private void createLog(String dbName, BasicDataSource dataSource) {
		LocalDateTime now = LocalDateTime.now();
		int dbMax = dataSource.getMaxTotal();
		int dbActive = dataSource.getNumActive();
		int dbIdle = dataSource.getNumIdle();
		logger.info(String.format("Database usage:\t%s\t%s\t%s\t%s\t%s", now.format(DateTimeFormatter.ISO_DATE_TIME), dbName, dbActive, dbIdle, dbMax));
	}

}
