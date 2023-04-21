package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.PriceReasonDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.PriceReason;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.api.dataSync.OutletTypePriceReasonSyncData;
import capi.model.api.dataSync.PriceReasonSyncData;
import capi.model.masterMaintenance.PriceReasonEditModel;
import capi.model.masterMaintenance.PriceReasonTableList;
import capi.service.BaseService;

@Service("PriceReasonService")
public class PriceReasonService extends BaseService {
	
	@Autowired
	private PriceReasonDao priceReasonDao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	/**
	 * Get by ID
	 */
	public PriceReason getPriceReasonById(int id) {
		return priceReasonDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<PriceReasonTableList> getPriceReasonList(DatatableRequestModel model, String[] outletTypeId, String reasonType){

		Order order = this.getOrder(model, "priceReasonId", "priceReasonId", "description", "reasonType", "sequence");
		
		String search = model.getSearch().get("value");
		
		List<PriceReasonTableList> result = priceReasonDao.selectAllPriceReason(search, model.getStart(), model.getLength(), order, outletTypeId, reasonType);
		
		DatatableResponseModel<PriceReasonTableList> response = new DatatableResponseModel<PriceReasonTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = priceReasonDao.countSelectAllPriceReason("", null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = priceReasonDao.countSelectAllPriceReason(search, outletTypeId, reasonType);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Get all outlet types
	 */
	public List<VwOutletTypeShortForm> getOutletTypes() {
		return outletTypeDao.getAll();
	}

	/**
	 * Get outlet type by id
	 */
	public VwOutletTypeShortForm getOutletTypeById(String id) {
		return outletTypeDao.findById(id);
	}

	/**
	 * Save Price Reason
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public void savePriceReason(PriceReasonEditModel model) throws Exception {

		PriceReason oldEntity = null;
		if (model.getPriceReasonId() != null && model.getPriceReasonId() > 0) {
			oldEntity = getPriceReasonById(model.getPriceReasonId());
		} else {
			// create price reason
			oldEntity = new PriceReason();
		}

		if(!model.getAllOutletType()) {
			if (model.getOutletTypeIds() != null) {
				ArrayList<String> oldOutletTypeIds = new ArrayList<String>();
				for (VwOutletTypeShortForm outletType : oldEntity.getOutletTypes()) {
					oldOutletTypeIds.add(outletType.getId());
				}
				
				Collection<String> deletedIds = (Collection<String>)CollectionUtils.subtract(oldOutletTypeIds, model.getOutletTypeIds());
	//			Collection<String> updatedIds = (Collection<String>)CollectionUtils.intersection(oldOutletTypeIds, model.getOutletTypeIds());
				Collection<String> newIds = (Collection<String>)CollectionUtils.subtract(model.getOutletTypeIds(), oldOutletTypeIds);
				
				if (deletedIds.size() > 0){
					List<VwOutletTypeShortForm> deletedOutletTypes = outletTypeDao.getByIds(deletedIds.toArray(new String[0]));
					for (VwOutletTypeShortForm outletType: deletedOutletTypes){
						oldEntity.getOutletTypes().remove(outletType);
					}
				}			
				
				if (newIds.size() > 0) {
					List<VwOutletTypeShortForm> newOutletTypes = outletTypeDao.getByIds(newIds.toArray(new String[0]));
					oldEntity.getOutletTypes().addAll(newOutletTypes);
				}
			} else {
				oldEntity.getOutletTypes().clear();
			}
		} else {
			oldEntity.getOutletTypes().clear();
		}
		
		oldEntity.setDescription(model.getDescription());
		oldEntity.setReasonType(model.getReasonType());
		oldEntity.setSequence(model.getSequence());
		oldEntity.setAllOutletType(model.getAllOutletType());

		priceReasonDao.save(oldEntity);
		priceReasonDao.flush();
	}

	/**
	 * Delete Price Reason
	 */
	@Transactional
	public boolean deletePriceReason(List<Integer> ids) {
		
		List<PriceReason> priceReasons = priceReasonDao.getPriceReasonsByIds(ids, true);
		if (ids.size() != priceReasons.size()){
			return false;
		}
		
		for (PriceReason priceReason : priceReasons){
			priceReason.getOutletTypes().clear();
			priceReasonDao.delete(priceReason);
		}

		priceReasonDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 */
	public PriceReasonEditModel convertEntityToModel(PriceReason entity) {

		PriceReasonEditModel model = new PriceReasonEditModel();
		BeanUtils.copyProperties(entity, model);
		
		Iterator<VwOutletTypeShortForm> outletTypes = entity.getOutletTypes().iterator();
		ArrayList<String> outletTypeIds = new ArrayList<String>();
		while (outletTypes.hasNext()) {
			outletTypeIds.add(outletTypes.next().getId());
		}
		model.setOutletTypeIds(outletTypeIds);
		
		return model;
	}
	
	public List<PriceReasonSyncData> getUpdatePriceReasion(Date lastSyncTime){
		return priceReasonDao.getUpdatePriceReasion(lastSyncTime);
	}
	
	public List<OutletTypePriceReasonSyncData> getUpdateOutletTypePriceReason(Date lastSyncTime){
		return priceReasonDao.getUpdateOutletTypePriceReason(lastSyncTime);
	}
	
	/**
	 * Get all by type
	 */
	public List<PriceReason> getAllByType(String[] outletTypeId, String type) {
		return priceReasonDao.getAllByType(outletTypeId, type);
	}
}
