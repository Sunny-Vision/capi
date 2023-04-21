package capi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("AppConfigService")
public class AppConfigService {
	// database setting
	@Value("${db.url}")
	private String dbUrl;	
	@Value("${db.username}")
	private String dbUsername;	
	@Value("${db.password}")
	private String dbPassword;
	
	// audit database setting
	@Value("${db.audit.url}")
	private String dbAuditUrl;	
	@Value("${db.audit.username}")
	private String dbAuditUsername;	
	@Value("${db.audit.password}")
	private String dbAuditPassword;
		
	@Value("${db.showSQL}")
	private String showSQL;
	
	@Value("${log4j.output}")
	private String log4jOutputType;
	
	// attachment
	@Value("${file.baseLocation}")
	private String fileBaseLoc;
	@Value("${file.importLocation}")
	private String importFileLoc;
	@Value("${file.exportLocation}")
	private String exportFileLoc;
	@Value("${file.reportLocation}")
	private String reportLocation;
	@Value("${file.onlineHelp}")
	private String onlineHelpDir;
	
	// google api
	@Value("${google.serverKey}")
	private String googleServerKey;	
	@Value("${google.browserKey}")
	private String googleBroswerKey;
	
	// quartz
	@Value("${quartz.schedulerName}")
	private String quartzSchedulerName;
	
	// push notification (APN)
	@Value("${push.certificateName}")
	private String pushCertificateName;
	@Value("${push.password}")
	private String pushPassword;
	@Value("${push.useSandbox}")
	private boolean useSandbox;
	
	
	@Value("${proxy.useProxy}")
	private boolean useProxy;
	
	@Value("${proxy.host}")
	private String proxyHost;
	
	@Value("${proxy.port}")
	private Integer proxyPort;
	
	@Value("${proxy.host2}")
	private String proxyHost2;
	
	@Value("${proxy.port2}")
	private Integer proxyPort2;
	
	@Value("${server.port}")
	private String serverPort;

	public String getDbUrl() {
		return dbUrl;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public String getDbAuditUrl() {
		return dbAuditUrl;
	}

	public String getDbAuditUsername() {
		return dbAuditUsername;
	}

	public String getDbAuditPassword() {
		return dbAuditPassword;
	}

	public String getShowSQL() {
		return showSQL;
	}

	public String getLog4jOutputType() {
		return log4jOutputType;
	}

	public String getFileBaseLoc() {
		return fileBaseLoc;
	}

	public String getGoogleServerKey() {
		return googleServerKey;
	}

	public String getGoogleBroswerKey() {
		return googleBroswerKey;
	}

	public String getQuartzSchedulerName() {
		return quartzSchedulerName;
	}

	public String getPushCertificateName() {
		return pushCertificateName;
	}

	public String getPushPassword() {
		return pushPassword;
	}

	public String getImportFileLoc() {
		return importFileLoc;
	}

	public String getExportFileLoc() {
		return exportFileLoc;
	}

	public String getReportLocation() {
		return reportLocation;
	}

	public boolean isUseSandbox() {
		return useSandbox;
	}

	public String getServerPort() {
		return serverPort;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public String getProxyHost2() {
		return proxyHost2;
	}

	public Integer getProxyPort2() {
		return proxyPort2;
	}

	public String getOnlineHelpDir() {
		return onlineHelpDir;
	}
}
