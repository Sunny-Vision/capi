package com.kinetix.controller.masterMaintenance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.entity.Outlet;
import capi.entity.OutletAttachment;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.OutletEditModel;
import capi.model.masterMaintenance.OutletTableList;
import capi.service.AppConfigService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;


/**
 * UF-1101 Outlet Maintenance
 */
@Secured("UF1101")
@FuncCode("UF1101")
@Controller("OutletMaintenanceController")
@RequestMapping("masterMaintenance/OutletMaintenance")
public class OutletMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(OutletMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private OutletService service;

	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private TpuService tpuService;
	
	/**
	 * List outlet
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		try {
			List<VwOutletTypeShortForm> outletTypes = service.getOutletTypes();
			model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<OutletTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "activeOutlet", required = false) String activeOutlet) {
		try {
			return service.getOutletList(requestModel, outletTypeId, districtId, tpuId, activeOutlet);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit outlet
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Outlet item = null;
			if (id != null) {
				item = service.getOutletById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/OutletMaintenance/home";
				}
			} else {
				item = new Outlet();
				item.setCollectionMethod(1);
				item.setStatus("Valid");
			}
			
			OutletEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
			model.addAttribute("googleBrowserKey", configService.getGoogleBroswerKey());

			List<VwOutletTypeShortForm> outletTypes = service.getOutletTypes();
			model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
		
	
	/**
	 * Save outlet fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute OutletEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes,
            @RequestParam("outletImage") MultipartFile outletImage) {
		try {
			if (item.getOutletId() != null) {
				if (service.getOutletById(item.getOutletId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/OutletMaintenance/home";
				}
			}
						
			if (outletImage != null && !outletImage.isEmpty()) {
				if (!outletImage.getContentType().contains("image")) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
					
					service.prepareModelForPostback(item);
					model.addAttribute("model", item);
					
					model.addAttribute("googleBrowserKey", configService.getGoogleBroswerKey());

					List<VwOutletTypeShortForm> outletTypes = service.getOutletTypes();
					model.addAttribute("outletTypes", outletTypes);
					
					return "/masterMaintenance/OutletMaintenance/edit";
				}
			}
			
			if (item.getOutletId() != null && !service.validateQuotationOutletType(item.getOutletId(), item.getOutletTypeIds().toArray(new String[0]))) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00088", null, locale));

				service.prepareModelForPostback(item);
				model.addAttribute("model", item);
				
				model.addAttribute("googleBrowserKey", configService.getGoogleBroswerKey());

				List<VwOutletTypeShortForm> outletTypes = service.getOutletTypes();
				model.addAttribute("outletTypes", outletTypes);
				
				return "/masterMaintenance/OutletMaintenance/edit";
			}
			
			service.saveOutlet(item, outletImage != null && !outletImage.isEmpty() ? outletImage.getInputStream() : null, configService.getFileBaseLoc());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/OutletMaintenance/home";
	}

	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get tpu select2 format
	 */
	@RequestMapping(value = "queryTpuSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSelect2(Locale locale, Model model, Select2RequestModel requestModel,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId) {
		try {
			return tpuService.queryTpuSelect2(requestModel, districtId);
		} catch (Exception e) {
			logger.error("queryTpuSelect2", e);
		}
		return null;
	}

	/**
	 * Get tpu by specific district select2 format
	 */
	@RequestMapping(value = "queryTpuSingleDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSingleDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel,
				@RequestParam(value = "districtId", required = false) Integer districtId) {
		try {
			if (districtId != null)
				return tpuService.queryTpuSelect2(requestModel, new Integer[] {districtId});
			else
				return tpuService.queryTpuSelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("queryTpuSingleDistrictSelect2", e);
		}
		return null;
	}
	
	/**
	 * Download image
	 */
	@RequestMapping(value = "getImage", method = RequestMethod.GET)
	public void getImage(@RequestParam("id") int id, HttpServletResponse response) {
		try {
			Outlet entity = service.getOutletById(id);
			File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
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
//	public ResponseEntity<InputStreamResource> getImage(@RequestParam("id") int id) {
//		try {
//			Outlet entity = service.getOutletById(id);
//			File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
//			if (!imageFile.exists()) return null;
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getImage", e);
//		}
//		return null;
//	}

	/**
	 * Get attachment ids
	 */
	@RequestMapping(value = "getAttachmentIds", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getAttachmentIds(@RequestParam("id") int id) {
		try {
			List<OutletAttachment> list = service.getAttachmentsByOutletId(id);
			List<Integer> attachmentIds = new ArrayList<Integer>();
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
//	public ResponseEntity<InputStreamResource> getAttachment(@RequestParam("id") int id) {
//		try {
//			OutletAttachment entity = service.getAttachmentById(id);
//			File imageFile = new File(configService.getFileBaseLoc() + entity.getPath());
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getAttachment", e);
//		}
//		return null;
//	}
	
	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public String delete(@RequestParam(value = "id") int id, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (!service.deleteOutlet(id, configService.getFileBaseLoc())) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/masterMaintenance/OutletMaintenance/home";
			}
			redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", messageSource.getMessage("I00002", null, locale));
			return "redirect:/masterMaintenance/OutletMaintenance/home";
		} catch (Exception e) {
			logger.error("delete", e);
		}
		redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		return "redirect:/masterMaintenance/OutletMaintenance/home";
	}
	
	/**
	 * save attachment
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "saveAttachment", method = RequestMethod.POST)
	public String saveAttachment(@RequestParam(value = "id") int id, MultipartHttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Hashtable<Integer, InputStream> overwriteAttachments = new Hashtable<Integer, InputStream>();
			Hashtable<Integer, InputStream> newAttachments = new Hashtable<Integer, InputStream>();
			
			Iterator<String> iterator = request.getFileNames();
			int i =0;
			while (iterator.hasNext()) {
				i++;
				String fileId = iterator.next();
				MultipartFile multiFile = request.getFile(fileId);
				if (multiFile == null || multiFile.isEmpty()) {
					if(fileId.endsWith("del")){
						service.deleteOutletAttachment(Integer.parseInt(fileId.replace("file", "").replace("del", "")), configService.getFileBaseLoc());
					}
					continue;
				}
				
				if (!multiFile.getContentType().contains("image")) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00100", null, locale));
					return "redirect:/masterMaintenance/OutletMaintenance/edit?id=" + id;
				}
				
				if (fileId.startsWith("newAttachment"))
					newAttachments.put(i, multiFile.getInputStream());
				else
					overwriteAttachments.put(Integer.parseInt(fileId.replace("file", "").replace("del", "")), multiFile.getInputStream());
			}
			
			service.saveAttachment(id, overwriteAttachments, newAttachments, configService.getFileBaseLoc());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00008", null, locale));
			
			return "redirect:/masterMaintenance/OutletMaintenance/edit?id=" + id;
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00082", null, locale));
		}
		return "redirect:/masterMaintenance/OutletMaintenance/edit?id=" + id;
	}
	

	@RequestMapping(value = "queryOutletTypeSelect2")
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel queryModel){
		try{
			return service.queryOutletTypeSelect2(queryModel);
		}
		catch(Exception ex){
			logger.error("queryOutletTypeSelect2", ex);
		}
		return null;
	}
}