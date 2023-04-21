package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.UOMCategoryDao;
import capi.dal.UomDao;
import capi.entity.UOMCategory;
import capi.entity.Uom;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.Select2ResponseModel.Select2Item;
import capi.model.api.dataSync.UOMCategorySyncData;
import capi.model.masterMaintenance.UOMCategoryModel;
import capi.model.masterMaintenance.UOMCategoryTableList;
import capi.service.BaseService;

@Service("UOMCategoryService")
public class UOMCategoryService extends BaseService {
	
	private UomService uomService;
	
	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	@Autowired
	private UomDao uomDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<UOMCategoryTableList> queryUOMCategory(DatatableRequestModel model){
		
		Order order = this.getOrder(model,"", "uomCategoryId", "description", "numberOfUOM");
		
		String search = model.getSearch().get("value");
		
		List<UOMCategoryTableList> result = uomCategoryDao.listUOMCategory(search, model.getStart(), model.getLength(), order);

		
		DatatableResponseModel<UOMCategoryTableList> response = new DatatableResponseModel<UOMCategoryTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = uomCategoryDao.countUOMCategory();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = uomCategoryDao.countUOMCategory(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	
	/**
	 * Get UOM Category select2 format
	 */
	public Select2ResponseModel queryUOMCategorySelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<UOMCategory> entities = uomCategoryDao.searchUOMCategory(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = uomCategoryDao.countUOMCategory(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (UOMCategory d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getUomCategoryId()));
			item.setText(d.getUomCategoryId() + " - " + d.getDescription());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	/**
	 * Get by ID
	 */
	public UOMCategory getUOMCategoryById(int id) {
		return uomCategoryDao.findById(id);
	}
	

	/**
	 * Save
	 */
	@Transactional
	public boolean saveUOMCategory(UOMCategoryModel model) {
		UOMCategory oldEntity = null;
		if (model.getUomCategoryId() != null && model.getUomCategoryId() > 0) {
			oldEntity = getUOMCategoryById(model.getUomCategoryId());
			if (oldEntity == null)
				return false;
			
		} else {
			oldEntity = new UOMCategory();
		}
		oldEntity.setDescription(model.getDescription());
		uomCategoryDao.save(oldEntity);
		uomCategoryDao.flush();
		return true;
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteUOMCategories(List<Integer> id) {
		List<UOMCategory> categories = uomCategoryDao.getUomCategoriesByIds(id, true);
		if (categories.size() != id.size()) {
			return false;
		}
		
		for (UOMCategory cat : categories){
			Set<Uom> uoms = cat.getUoms();
			for (Uom uom : uoms) {
				uomDao.delete(uom);
			}
			uomCategoryDao.delete(cat);
		}
		
		uomCategoryDao.flush();
		
		return true;
	}
	
	/**
	 * Query multiple UOM Category
	 */
	public Select2ResponseModel queryUomCategorySelectByIds(Integer[] ids) {
		List<UOMCategory> categories = uomCategoryDao.getUomCategoriesByIds(new ArrayList<Integer>(Arrays.asList(ids)), false);
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (UOMCategory category : categories) {
			Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(category.getId()));
			item.setText(category.getUomCategoryId() + " - " + category.getDescription());
			items.add(item);
		}
		responseModel.setResults(items);
		return responseModel;
	}
	
	public List<UOMCategorySyncData> getUpdateUOMCategory(Date lastSyncTime){
		return uomCategoryDao.getUpdateUOMCategory(lastSyncTime);
	}
}
