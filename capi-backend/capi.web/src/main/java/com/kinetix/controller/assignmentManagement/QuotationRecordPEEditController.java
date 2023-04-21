package com.kinetix.controller.assignmentManagement;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.entity.QuotationRecord;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.shared.quotationRecord.PagePostModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.AppConfigService;
import capi.service.assignmentManagement.DataConversionService;
import capi.service.assignmentManagement.QuotationRecordService;

@Secured("RF2003")
@FuncCode("RF2003")
@Controller("QuotationRecordPEEditController")
@RequestMapping("assignmentManagement/QuotationRecordPEEdit")
public class QuotationRecordPEEditController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotationRecordPEEditController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private QuotationRecordService service;

	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private DataConversionService dataConversionService;
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(@RequestParam(value = "id") int id, Model model, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			boolean readonly = false;
			if ((detail.getAuthorityLevel() & 1) != 1 && (detail.getAuthorityLevel() & 2) != 2 && (detail.getAuthorityLevel() & 4) != 4) {
				readonly = true;
			}
			boolean peCheckRemarkReturnRaw = true;
			PageViewModel viewModel = service.prepareViewModel(id, false, readonly, peCheckRemarkReturnRaw);
			viewModel.setHideOutlet(true);
			model.addAttribute("model", viewModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}

	/**
	 * Submit
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public String submit(@ModelAttribute PagePostModel item,
			@RequestParam(value = "btnSubmit", required = false) String btnSubmit,
			Model model, Locale locale, RedirectAttributes redirectAttributes,
			@RequestParam(value = "photo1", required = false) MultipartFile photo1,
			@RequestParam(value = "photo2", required = false) MultipartFile photo2) {
		try {
			boolean invalidImage = false;
			
			if (photo1 != null && !photo1.isEmpty()) {
				if (!photo1.getContentType().contains("image")) {
					invalidImage = true;
				}
			}
			
			if (photo2 != null && !photo2.isEmpty()) {
				if (!photo2.getContentType().contains("image")) {
					invalidImage = true;
				}
			}
			
			if (invalidImage) {
				throw new ServiceException("E00100");
			}
			service.submitByPE(item, photo1 != null ? photo1.getInputStream() : null, photo2 != null ? photo2.getInputStream() : null, configService.getFileBaseLoc());
			followup(item.getQuotationRecord().getQuotationRecordId());
		} catch (ServiceException e) {
			logger.error("submit", e);
			try {
				if (e.getMessages() != null) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, StringUtils.join(e.getMessages(), "<br/>"));
				} else {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
				}
				PageViewModel viewModel = service.prepareViewModel(item.getQuotationRecord().getQuotationRecordId(), false, false);
				service.prefillViewModelWithPost(viewModel, item);
				viewModel.setPeCheckRemark(item.getPeCheckRemark());
				model.addAttribute("model", viewModel);
			} catch (Exception e2) {
				logger.error("prefillViewModelWithPost", e2);
			}
			return "/assignmentManagement/QuotationRecordPEEdit/home";
		} catch (Exception e) {
			logger.error("submit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return null;
	}
	
	public void followup(Integer id){
		QuotationRecord entity = service.getQuotationRecordById(id);
		Session session = sessionFactory.openSession();
		Transaction trans = session.beginTransaction();
		try{
			dataConversionService.convert(entity);
			
			session.flush();
			trans.commit();
		}
		catch(Exception ex){
			trans.rollback();
			logger.error("Approved Quotation Record "+entity.getQuotationRecordId()+"Followup Job failed", ex);
		}finally{		
			session.close();
		}
	}
}
