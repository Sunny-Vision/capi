package com.kinetix.controller.commonDialog;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.OutletAttachment;
import capi.service.AppConfigService;
import capi.service.masterMaintenance.OutletService;


/**
 * Outlet attachment dialog
 */
@Secured({"UF1405", "UF2601", "RF2009", "RF2003", "UF1402", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
@Controller("OutletAttachmentDialogController")
@RequestMapping("commonDialog/OutletAttachmentDialog")
public class OutletAttachmentDialogController {
	
	private static final Logger logger = LoggerFactory.getLogger(OutletAttachmentDialogController.class);

	@Autowired
	private OutletService service;

	@Autowired
	private AppConfigService configService;

	@Resource(name="messageSource")
	MessageSource messageSource;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model, @RequestParam("outletId") int outletId) {
		model.addAttribute("outletId", outletId);
	}
	
	/**
	 * Get attachment ids
	 */
	@RequestMapping(value = "getAttachmentIds", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getAttachmentIds(@RequestParam("id") int id) {
		try {
			List<OutletAttachment> list = service.getAttachmentsByOutletId(id);
			List<Integer> attachmentIds = new ArrayList<Integer>();
//			for (OutletAttachment entity : list) {
//				attachmentIds.add(entity.getId());
//			}
			Hashtable<Integer, Integer> sequence = new Hashtable<Integer, Integer>();
			for (OutletAttachment entity : list) {
				sequence.put(entity.getSequence(), entity.getOutletAttachmentId());
			}
			for(int i=1;i<=10;i++){
				if(sequence.containsKey(i)){
					attachmentIds.add(sequence.get(i));
				} else {
					attachmentIds.add(null);
				}
			}
			
			return attachmentIds;
		} catch (Exception e) {
			logger.error("getAttachmentIds", e);
		}
		return null;
	}
	
	/**
	 * Download attachment
	 */
	@RequestMapping(value = "getAttachment", method = RequestMethod.GET)
	public void getAttachment(@RequestParam("id") int id, HttpServletResponse response) {
		try {
			OutletAttachment entity = service.getAttachmentById(id);
			File imageFile = new File(configService.getFileBaseLoc() + entity.getPath());
			if (imageFile.exists()) {
				//response.setContentType("image/png");	
		        response.setContentLength((int)imageFile.length());		        
		        response.setHeader("Content-Disposition","attachment; filename=\""+imageFile.getName()+"\"");
		 
		        FileCopyUtils.copy(new FileInputStream(imageFile), response.getOutputStream());	
			}
		} catch (Exception e) {
			logger.error("getImage", e);
		}
		
	}
}