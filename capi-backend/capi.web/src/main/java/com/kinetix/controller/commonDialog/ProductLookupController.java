package com.kinetix.controller.commonDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.commonLookup.ProductLookupTableList;
import capi.service.lookup.ProductLookupService;
import capi.service.productMaintenance.ProductService;


/**
 * Product lookup
 */
@Secured({"UF1118", "UF1208", "UF1209", "UF1401", "UF1409"})
@Controller("ProductLookupController")
@RequestMapping("commonDialog/ProductLookup")
public class ProductLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductLookupController.class);

	@Autowired
	private ProductLookupService service;
	
	@Autowired
	private ProductService productService;

	/**
	 * Lookup dialog
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}
	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ProductLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, String status, Boolean reviewed, Integer productGroupId, Integer skipProductId,
				String barcode, Integer[] selectedIds) {
		try {
			return service.getLookupTableList(requestModel, status, reviewed, productGroupId, skipProductId, barcode, selectedIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * datatable query all function
	 */
	@RequestMapping(value = "queryAll")
	public @ResponseBody List<ProductLookupTableList>
		queryAll(@RequestParam(value = "productIds[]") ArrayList<Integer> productIds) {
		try {
			return service.getLookupTableList(productIds);
		} catch (Exception e) {
			logger.error("queryAll", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search, String status, Boolean reviewed, Integer productGroupId, Integer skipProductId,
			String barcode) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search, status, reviewed, productGroupId, skipProductId, barcode);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}

	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryProductGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryProductGroupSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return productService.queryProductGroupSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryProductGroupSelect2", e);
		}
		return null;
	}
}