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
import capi.model.commonLookup.ProductAttributeLookupTableList;
import capi.service.lookup.ProductLookupService;
import capi.service.productMaintenance.ProductService;


/**
 * Product lookup
 */
@Secured({"UF1118", "UF1208", "UF1209", "UF1401"})
@Controller("ProductAttributeLookupController")
@RequestMapping("commonDialog/ProductAttributeLookup")
public class ProductAttributeLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductAttributeLookupController.class);

	@Autowired
	private ProductLookupService service;
	
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
	public @ResponseBody DatatableResponseModel<ProductAttributeLookupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Integer productGroupId, Integer productAttributeId, Integer[] selectedIds) {
		try {
			return service.getAttributeLookupTableList(requestModel, productGroupId, productAttributeId, selectedIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * datatable query all function
	 */
	@RequestMapping(value = "queryAll")
	public @ResponseBody List<ProductAttributeLookupTableList>
		queryAll(@RequestParam(value = "productIds[]") ArrayList<Integer> productIds, Integer productAttributeId) {
		try {
			return service.getAttributeLookupTableList(productIds, productAttributeId);
		} catch (Exception e) {
			logger.error("queryAll", e);
		}
		return null;
	}

	/**
	 * datatable select all
	 */
	@RequestMapping(value = "getLookupTableSelectAll", method = RequestMethod.GET)
	public @ResponseBody List<Integer> getLookupTableSelectAll(String search, String status, Boolean reviewed, Integer productGroupId, Integer skipProductId) {
		try {
			List<Integer> list = service.getLookupTableSelectAll(search, status, reviewed, productGroupId, skipProductId, null);
			return list;
		} catch (Exception e) {
			logger.error("getLookupTableSelectAll", e);
		}
		return null;
	}
}