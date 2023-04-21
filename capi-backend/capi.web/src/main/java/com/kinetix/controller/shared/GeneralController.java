package com.kinetix.controller.shared;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.entity.Outlet;
import capi.entity.Product;
import capi.entity.SubPriceType;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.DiscountFormulaLookupModel;
import capi.model.masterMaintenance.SubPriceFieldList;
import capi.model.masterMaintenance.SubPriceTypeModel;
import capi.model.productMaintenance.ProductHasPhoto;
import capi.service.AppConfigService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.lookup.DiscountCalculatorLookupService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.SubPriceService;
import capi.service.masterMaintenance.TpuService;
import capi.service.masterMaintenance.UomService;
import capi.service.productMaintenance.ProductService;

@Controller("GeneralController")
@RequestMapping("shared/General")
public class GeneralController {

	private static final Logger logger = LoggerFactory.getLogger(GeneralController.class);

	@Autowired
	private OutletService outletService;

	@Autowired
	private AppConfigService configService;

	@Autowired
	private ProductService productService;

	@Autowired
	private SubPriceService subPriceService;
	
	@Autowired
	private DiscountCalculatorLookupService discountCalculatorService;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private UomService uomService;

	@Autowired
	private TpuService tpuService;

	/**
	 * Get outlet image
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getOutletImage", method = RequestMethod.GET)
	public void getOutletImage(@RequestParam("id") int id, HttpServletResponse response) {
		try {
			Outlet entity = outletService.getOutletById(id);
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
//	public ResponseEntity<InputStreamResource> getOutletImage(@RequestParam("id") int id) {
//		try {
//			Outlet entity = outletService.getOutletById(id);
//			File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
//			if (!imageFile.exists()) return null;
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getOutletImage", e);
//		}
//		return null;
//	}
	
	/**
	 * Get product image
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getProductImage", method = RequestMethod.GET)
	public void getProductImage(@RequestParam("productId") int productId, @RequestParam("photoIndex") int photoIndex, HttpServletResponse response) {
		try {
			Product entity = productService.getProductById(productId);
			String imagePath = null;
			if (photoIndex == 2) {
				imagePath = entity.getPhoto2Path();
			} else {
				imagePath = entity.getPhoto1Path();
			}
			File imageFile = new File(configService.getFileBaseLoc() + imagePath);
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
	
//	public ResponseEntity<InputStreamResource> getProductImage(@RequestParam("productId") int productId, @RequestParam("photoIndex") int photoIndex) {
//		try {
//			Product entity = productService.getProductById(productId);
//			String imagePath = null;
//			if (photoIndex == 2) {
//				imagePath = entity.getPhoto2Path();
//			} else {
//				imagePath = entity.getPhoto1Path();
//			}
//			File imageFile = new File(configService.getFileBaseLoc() + imagePath);
//			if (!imageFile.exists()) return null;
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getProductImage", e);
//		}
//		return null;
//	}
	
	/**
	 * Get sub-price type select format
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "querySubPriceTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubPriceTypeSelect2(Select2RequestModel requestModel) {
		try {
			return subPriceService.querySubPriceTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("querySubPriceTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get sub price field by type
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getSubPriceFieldByType", method = RequestMethod.GET)
	public @ResponseBody List<SubPriceFieldList> getSubPriceFieldByType(@RequestParam("id") int id) {
		try {
			List<SubPriceFieldList> list = subPriceService.getSubPriceFieldByType(id);
			return list;
		} catch (Exception e) {
			logger.error("getSubPriceFieldByType");
		}
		return null;
	}

	/**
	 * Get all enabled formula
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getAllEnabledFormula", method = RequestMethod.GET)
	public @ResponseBody List<DiscountFormulaLookupModel> getAllEnabledFormula() {
		try {
			List<DiscountFormulaLookupModel> list = discountCalculatorService.getAllEnabledFormula();
			return list;
		} catch (Exception e) {
			logger.error("getAllEnabledFormula", e);
		}
		return null;
	}

	/**
	 * Get sub price type
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getSubPriceType", method = RequestMethod.GET)
	public @ResponseBody SubPriceTypeModel getSubPriceType(@RequestParam("id") int id) {
		try {
			SubPriceType entity = subPriceService.getSubPriceById(id);
			SubPriceTypeModel model = new SubPriceTypeModel();
			BeanUtils.copyProperties(entity, model);
			model.setGroupByField(entity.getGroupByField() != null ? entity.getGroupByField().getVariableName() : null);
			model.setDividedByField(entity.getDividedByField() != null ? entity.getDividedByField().getVariableName() : null);
			return model;
		} catch (Exception e) {
			logger.error("getSubPriceType", e);
		}
		return null;
	}

	/**
	 * Get product has photo
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF2101", "UF1406", "UF1410", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getProductHasPhoto", method = RequestMethod.GET)
	public @ResponseBody ProductHasPhoto getProductHasPhoto(@RequestParam("id") int id) {
		try {
			Product entity = productService.getProductById(id);
			ProductHasPhoto model = new ProductHasPhoto();
			model.setPhoto1(entity.getPhoto1Path() != null);
			model.setPhoto2(entity.getPhoto2Path() != null);
			return model;
		} catch (Exception e) {
			logger.error("getProductHasPhoto", e);
		}
		return null;
	}
	
	/**
	 * Get history quotation record
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "getHistoryQuotationRecord", method = RequestMethod.GET)
	public String getHistoryQuotationRecord(Model model, @RequestParam("id") int id) {
		try {
			capi.model.shared.quotationRecord.PageViewModel viewModel = quotationRecordService.prepareViewModel(id, true, true);
			model.addAttribute("model", viewModel);
			return "shared/quotationRecord/editForm";
		} catch (Exception e) {
			logger.error("getHistoryQuotationRecord", e);
		}
		return null;
	}
	
	/**
	 * Get UOM select2 format
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "queryUomSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUomSelect2(Select2RequestModel requestModel, @RequestParam(value = "categoryIds[]", required = false) Integer[] categoryIds) {
		try {
			return uomService.queryUomSelect2(requestModel, categoryIds, true);
		} catch (Exception e) {
			logger.error("queryUomSelect2", e);
		}
		return null;
	}

	/**
	 * Calculate percentage change
	 */
	@Secured({"UF1402", "UF1405", "UF2601", "RF2009", "RF2003", "UF1406", "UF1410", "UF2602", "UF2101", "UF2102", "UF2103", "UF2201", "UF1503", "UF1504", "UF1506"})
	@RequestMapping(value = "calculatePercentageChange", method = RequestMethod.POST)
	public @ResponseBody Double calculatePercentageChange(
			@RequestParam(value = "historyQuotationRecordId", required = false) int historyQuotationRecordId,
			@RequestParam(value = "currentQuotationRecordUnitId", required = false) int currentQuotationRecordUnitId,
			@RequestParam(value = "historyValue", required = false) Double historyValue,
			@RequestParam(value = "newValue", required = false) Double newValue,
			@RequestParam(value = "uomId", required = false) Integer uomId,
			@RequestParam(value = "uomValue", required = false) Double uomValue) {
		try {
			Double result = quotationRecordService.calculatePercentageChange(historyQuotationRecordId, currentQuotationRecordUnitId, historyValue, newValue, uomId, uomValue);
			return result;
		} catch (Exception e) {
			logger.error("calculatePercentageChange", e);
		}
		return null;
	}

	/**
	 * Get tpu by specific district select2 format
	 */
	@Secured({"UF1405", "UF2602", "UF2601", "RF2009","RF2014","UF1503","UF1504","UF1506", "UF1410", "RF2003"})
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
}
