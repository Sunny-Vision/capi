package capi.service.lookup;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ProductDao;
import capi.dal.VwProductSpecDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.ProductAttributeLookupTableList;
import capi.model.commonLookup.ProductLookupDetailTableList;
import capi.model.commonLookup.ProductLookupTableList;
import capi.service.BaseService;

@Service("ProductLookupService")
public class ProductLookupService extends BaseService {

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private VwProductSpecDao vwProductSpecDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<ProductLookupTableList> getLookupTableList(DatatableRequestModel model, String status, Boolean reviewed, 
			Integer productGroupId, Integer skipProductId, String barcode, Integer[] selectedIds){

		Order order = this.getOrder(model,"", "id", "barCode", "numberOfQuotations", "productAttributes", "remark", "status", "reviewed", "category");
		
		String search = model.getSearch().get("value");
		
		List<ProductLookupTableList> returnList = productDao.getLookupTableList(
					search, model.getStart(), model.getLength(), order, status, reviewed, productGroupId, skipProductId, barcode, selectedIds);
		
		DatatableResponseModel<ProductLookupTableList> response = new DatatableResponseModel<ProductLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal = productDao.countLookupTableList("", status, null, null, null, null, selectedIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = productDao.countLookupTableList(search, status, reviewed, productGroupId, skipProductId, barcode, selectedIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/** 
	 * datatable query all
	 */
	public List<ProductLookupTableList> getLookupTableList(List<Integer> productIds){

		List<ProductLookupTableList> returnList = productDao.getLookupTableList(productIds);
		return returnList;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, String status, Boolean reviewed, Integer productGroupId, Integer skipProductId,
			String barcode){
		
		return productDao.getLookupTableSelectAll(search, status, reviewed, productGroupId, skipProductId, barcode);
	}
	
	/**
	 * Get table list by ids
	 */
	public List<ProductLookupDetailTableList> getTableListByIds(Integer[] ids){
		return productDao.getTableListByIds(ids);
	}
	
	/** 
	 * Product Attribute Lookup datatable query
	 */
	public DatatableResponseModel<ProductAttributeLookupTableList> getAttributeLookupTableList(DatatableRequestModel model, 
			Integer productGroupId, Integer productAttributeId, Integer[] selectedIds){

		Order order = this.getOrder(model,"", "productId", "attributeValue");
		
		String search = model.getSearch().get("value");
		
		List<ProductAttributeLookupTableList> returnList = vwProductSpecDao.getLookupTableList(
					search, model.getStart(), model.getLength(), order, productGroupId, productAttributeId, selectedIds);
		
		DatatableResponseModel<ProductAttributeLookupTableList> response = new DatatableResponseModel<ProductAttributeLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal = vwProductSpecDao.countLookupTableList("", null, null, selectedIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = vwProductSpecDao.countLookupTableList(search, productGroupId, productAttributeId, selectedIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/** 
	 * Product Attribute Lookup datatable query all
	 */
	public List<ProductAttributeLookupTableList> getAttributeLookupTableList(List<Integer> productIds, Integer productAttributeId){

		List<ProductAttributeLookupTableList> returnList = vwProductSpecDao.getLookupTableList( productIds,  productAttributeId);
		return returnList;
	}
	
	/** 
	 * Product Attribute Lookup datatable select all
	 */
	public List<Integer> getAttributeLookupTableSelectAll(String search, 
			Integer productGroupId, Integer productAttributeId){
		
		return vwProductSpecDao.getLookupTableSelectAll(search, productGroupId, productAttributeId);
	}
}
