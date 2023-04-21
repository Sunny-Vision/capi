package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.UOMCategoryDao;
import capi.dal.UomDao;
import capi.entity.Uom;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.UomSyncData;
import capi.model.masterMaintenance.UomEditModel;
import capi.model.masterMaintenance.UomModel;
import capi.service.BaseService;

@Service("UomService")
public class UomService extends BaseService {
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<UomModel> getUomList(DatatableRequestModel model,Integer uomCategoryId){

		Order order = this.getOrder(model,"", "uomId", "chineseName", "englishName", "uomCategoryId", "description");
		
		String search = model.getSearch().get("value");
		
		List<UomModel> returnList = uomDao.listUom(search, model.getStart(), model.getLength(), order, uomCategoryId);
		
		DatatableResponseModel<UomModel> response = new DatatableResponseModel<UomModel>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal = uomDao.countUom();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = uomDao.countUom(search, uomCategoryId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Get by ID
	 */
	public Uom getUomById(int id) {
		return uomDao.findById(id);
	}
	

	/**
	 * Save
	 */
	@Transactional
	public boolean saveUom(UomEditModel model) {
		Uom uom = null;
		if (model.getUomId() != null){
			uom = uomDao.findById(model.getUomId());
			if (uom == null)
				return false;
		}
		else{
			uom = new Uom();
		}
		BeanUtils.copyProperties(model, uom);
		uom.setUomCategory(uomCategoryDao.findById(model.getUomCategoryId()));	
		
		uomDao.save(uom);
		uomDao.flush();
		
		return true;
	}
	
	/**
	 * Delete
	 */
	@Transactional
	public boolean deleteUom(List<Integer> ids) {
		List<Uom> uoms= uomDao.getUomsByIds(ids);
		for (Uom uom : uoms){
			uomDao.delete(uom);
		}
		uomDao.flush();
		return true;
	}
	
	/**
	 * Query single UOM
	 */
	public String queryUomSelectSingle(Integer id) {
		Uom entity = uomDao.findById(id);
		if (entity == null)
			return null;
		else
			return entity.getId() + " - " + entity.getChineseName();
	}
	

	/**
	 * Get Base UOM select2 format
	 */
	public Select2ResponseModel queryUomSelect2(Select2RequestModel queryModel) {
		return queryUomSelect2(queryModel, null, false);
	}
	
	/**
	 * Get Base UOM select2 format
	 */
	public Select2ResponseModel queryUomSelect2(Select2RequestModel queryModel, Integer[] categoryIds, boolean hideId) {
		queryModel.setRecordsPerPage(10);
		List<Uom> entities = uomDao.searchUom(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), categoryIds);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = uomDao.countSearchUom(queryModel.getTerm(), categoryIds);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Uom d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText((hideId ? "" : d.getId() + " - ") + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public List<UomSyncData> getUpdateUom(Date lastSyncTime){
		return uomDao.getUpdateUom(lastSyncTime);
	}
}
