package capi.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("OnlineHelpService")
public class OnlineHelpService {
	
	@Autowired
	private AppConfigService appConfig;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private ZipService zipService;
		

	public void uploadWebOnlineHelp(InputStream stream) throws Exception{
		String dirname = appConfig.getImportFileLoc()+File.separator+UUID.randomUUID().toString();
		zipService.unZip(stream, dirname);
		//String targetDir = servletContext.getRealPath("/WEB-INF/onlineHelp");
//		String targetDir = servletContext.getRealPath(appConfig.getOnlineHelpDir());
//		URL url = servletContext.getResource(appConfig.getOnlineHelpDir());
		String targetDir = "";
		if (appConfig.getOnlineHelpDir().matches("^file:.*")){
			targetDir = appConfig.getOnlineHelpDir().replaceFirst("file:", "");
		}	
		else{
			targetDir = servletContext.getRealPath(appConfig.getOnlineHelpDir());
		}
		
		File target = new File(targetDir);
		File source = new File(dirname);
//		File target = new File(targetDir);
		FileUtils.cleanDirectory(target);
		FileUtils.copyDirectory(source, target);
		FileUtils.deleteDirectory(source);
	}
	
	public void exportWebOnlineHelp(OutputStream stream) throws IOException{
		String dirname = appConfig.getImportFileLoc()+File.separator+UUID.randomUUID().toString();
		File target = new File(dirname);
		String sourceDir = "";
		if (appConfig.getOnlineHelpDir().matches("^file:.*")){
			sourceDir = appConfig.getOnlineHelpDir().replaceFirst("file:", "");
		}	
		else{
			sourceDir = servletContext.getRealPath(appConfig.getOnlineHelpDir());
		}
		File source = new File(sourceDir);
		FileUtils.copyDirectory(source, target);
		zipService.zipFolder(target, stream);
		FileUtils.deleteDirectory(target);
	}
	
	
}
