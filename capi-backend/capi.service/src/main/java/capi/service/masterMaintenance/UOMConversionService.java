package capi.service.masterMaintenance;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.UOMConversionDao;
import capi.dal.UomDao;
import capi.entity.UOMConversion;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.api.dataSync.UOMConversionSyncData;
import capi.model.masterMaintenance.UOMConversionEditModel;
import capi.model.masterMaintenance.UOMConversionTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("UOMConversionService")
public class UOMConversionService extends BaseService {
	

	
	@Autowired
	private UOMConversionDao uomConversionDao;
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private CommonService commonService;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<UOMConversionTableList> queryUOMConversion(DatatableRequestModel model){
		
		Order order = this.getOrder(model,"", "uc.baseUOM.uomId", "uc.targetUOM.uomId", "factor");
		
		String search = model.getSearch().get("value");
		
		List<UOMConversionTableList> result = uomConversionDao.listUOMConversion(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<UOMConversionTableList> response = new DatatableResponseModel<UOMConversionTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = uomConversionDao.countUOMConversion();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = uomConversionDao.countUOMConversion(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	

	/**
	 * Get by ID
	 */
	public UOMConversion getUOMConversionById(int id) {
		return uomConversionDao.findById(id);
	}
	
	/**
	 * Get All
	 */
	public List<UOMConversion> getUOMConversions() {
		return uomConversionDao.findAll();
	}
	
	
	
	/**
	 * Save
	 */
	@Transactional
	public boolean saveUOMConversion(UOMConversionEditModel model) {
		
		UOMConversion oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getUOMConversionById(model.getId());
		} else {

			oldEntity = new UOMConversion();
		}
			
		if (model.getBaseUOMId() == null || model.getTargetUOMId() == null){
			return false;
		}
		
		oldEntity.setFactor(model.getFactor());
		oldEntity.setBaseUOM(uomDao.findById(model.getBaseUOMId()));
		oldEntity.setTargetUOM(uomDao.findById(model.getTargetUOMId()));
		
		uomConversionDao.save(oldEntity);
		uomConversionDao.flush();
		
		return true;
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteUOMConversions(List<Integer> id) {
		List<UOMConversion> uomConversions = uomConversionDao.getUOMConversionByIds(id);
		if (id.size() != uomConversions.size()){
			return false;
		}
		
		for (UOMConversion uomConversion : uomConversions){
			uomConversionDao.delete(uomConversion);
		}

		uomConversionDao.flush();

		return true;
	}
	
	
	/**
	 * Convert entity to model
	 */
	public UOMConversionEditModel convertEntityToModel(UOMConversion entity){

		UOMConversionEditModel model = new UOMConversionEditModel();
		
		model.setUomConversionId(entity.getId());
		model.setFactor(entity.getFactor());
		model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
		model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
		
		if(entity.getBaseUOM() != null){
			model.setBaseUOMId(entity.getBaseUOM().getId());
			model.setBaseUOMChineseName(entity.getBaseUOM().getChineseName());
		}
		
		if(entity.getTargetUOM() != null){
			model.setTargetUOMId(entity.getTargetUOM().getId());
			model.setTargetUOMChineseName(entity.getTargetUOM().getChineseName());
		}

		return model;
	}
	
	public List<UOMConversionSyncData> getUpdateUOMConversion(Date lastSyncTime){
		return uomConversionDao.getUpdateUOMConversion(lastSyncTime);
	}
}
