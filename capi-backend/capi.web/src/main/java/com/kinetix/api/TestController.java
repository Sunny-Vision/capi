package com.kinetix.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import capi.service.AppConfigService;
import capi.service.CommonService;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;

@Controller("APITestController")
@RequestMapping("api/Test")
public class TestController {

	@Autowired
	CommonService commonService;
	@Autowired
	AppConfigService appConfig;
	
	@RequestMapping("apns")
	public String apns(){
		return "/masterMaintenance/Test/apns";
	}
	
	@RequestMapping("push")
	public String push(String deviceKey) throws FileNotFoundException, CommunicationException, KeystoreException, URISyntaxException, IOException, JSONException{
		
			PushNotificationPayload payload = PushNotificationPayload.complex();
	    	payload.addAlert("APNS uat testing");
	    	payload.addCustomDictionary("id", "testt1");
	        payload.addCustomDictionary("Subject", "testt2");
	        payload.addCustomDictionary("Content", "testt3");

	        Resource resource = new ClassPathResource(appConfig.getPushCertificateName());
			//InputStream in = new FileInputStream(resource.getFile().getPath());
	       // Push.payload(payload, in, appConfig.getPushPassword(), !useSandBox, new String[]{user.getDeviceKey()});
			commonService.sendAPNsPushMessage(payload, resource.getFile().getPath(), appConfig.getPushPassword(), !appConfig.isUseSandbox(), new String[]{deviceKey});
		
		return "redirect:/api/Test/apns";
	}
}
