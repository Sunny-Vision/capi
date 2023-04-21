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
import org.springframework.util.StringUtils;

import capi.dal.OutletDao;
import capi.dal.PointToNoteDao;
import capi.dal.ProductDao;
import capi.dal.QuotationDao;
import capi.dal.UnitDao;
import capi.entity.Outlet;
import capi.entity.PointToNote;
import capi.entity.Product;
import capi.entity.Quotation;
import capi.entity.Unit;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.api.dataSync.PointToNoteOutletSyncData;
import capi.model.api.dataSync.PointToNoteProductSyncData;
import capi.model.api.dataSync.PointToNoteQuotationSyncData;
import capi.model.api.dataSync.PointToNoteSyncData;
import capi.model.api.dataSync.PointToNoteUnitSyncData;
import capi.model.masterMaintenance.PointToNoteEditModel;
import capi.model.masterMaintenance.PointToNoteExpiryNowModel;
import capi.model.masterMaintenance.PointToNoteTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("PointToNoteService")
public class PointToNoteService extends BaseService {
	
	@Autowired
	private PointToNoteDao pointToNoteDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private CommonService commonService;

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<PointToNoteTableList> getTableList(DatatableRequestModel model){

		Order order = this.getOrder(model, "", "pn.effectiveDate", "pn.expiryDate", "pn.createdDate", "createdBy", "noOfQuotation", "noOfUnit", "noOfFirm", "noOfProduct", "pointToNote");
		
		String search = model.getSearch().get("value");
		
		List<PointToNoteTableList> result = pointToNoteDao.getTableList(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<PointToNoteTableList> response = new DatatableResponseModel<PointToNoteTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = pointToNoteDao.countTableList("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = pointToNoteDao.countTableList(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Expiry now
	 */
	@Transactional
	public void expiryNow(PointToNoteExpiryNowModel model) {
		List<PointToNote> list = pointToNoteDao.findByIds(model.getIds());
		
		for (PointToNote entity : list) {
			entity.setExpiryDate(new Date());
			pointToNoteDao.save(entity);
		}
		pointToNoteDao.flush();
	}
	
	/**
	 * Delete
	 */
	@Transactional
	public boolean delete(PointToNoteExpiryNowModel model) {
		List<PointToNote> list = pointToNoteDao.findByIds(model.getIds());
		
		if (list.size() != model.getIds().size()) return false;
		
		for (PointToNote entity : list) {
			entity.getOutlets().clear();
			entity.getProducts().clear();
			entity.getQuotations().clear();
			entity.getUnits().clear();
			pointToNoteDao.delete(entity);
		}
		pointToNoteDao.flush();
		
		return true;
	}
	
	/**
	 * Delete
	 */
	@Transactional
	public boolean delete(Integer id) {
		PointToNote entity = pointToNoteDao.findById(id);
		if (entity == null)
			return false;
		
		pointToNoteDao.delete(entity);
		pointToNoteDao.flush();
		return true;
	}
	
	/**
	 * Get by id
	 */
	public PointToNote getById(Integer id) {
		return pointToNoteDao.findById(id);
	}
	
	/**
	 * Convert entity to model
	 */
	public PointToNoteEditModel convertEntityToModel(PointToNote entity) {
		PointToNoteEditModel model = new PointToNoteEditModel();
		BeanUtils.copyProperties(entity, model);
		if (entity.getEffectiveDate() != null)
			model.setEffectiveDate(commonService.formatDate(entity.getEffectiveDate()));
		if (entity.getExpiryDate() != null)
			model.setExpiryDate(commonService.formatDate(entity.getExpiryDate()));
		
		model.setOutletIds(new ArrayList<Integer>());
		for (Outlet child : entity.getOutlets()) {
			model.getOutletIds().add(child.getId());
		}
		
		model.setProductIds(new ArrayList<Integer>());
		for (Product child : entity.getProducts()) {
			model.getProductIds().add(child.getId());
		}
		
		model.setQuotationIds(new ArrayList<Integer>());
		for (Quotation child : entity.getQuotations()) {
			model.getQuotationIds().add(child.getId());
		}
		
		model.setUnitIds(new ArrayList<Integer>());
		for (Unit child : entity.getUnits()) {
			model.getUnitIds().add(child.getId());
		}
		
		model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
		model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
		return model;
	}
	
	@Transactional
	public void save(PointToNoteEditModel model) throws Exception {
		PointToNote oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getById(model.getId());
		} else {
			oldEntity = new PointToNote();
		}
		BeanUtils.copyProperties(model, oldEntity);
		if (StringUtils.isEmpty(model.getEffectiveDate()))
			oldEntity.setEffectiveDate(null);
		else
			oldEntity.setEffectiveDate(commonService.getDate(model.getEffectiveDate()));
		if (StringUtils.isEmpty(model.getExpiryDate()))
			oldEntity.setExpiryDate(null);
		else
			oldEntity.setExpiryDate(commonService.getDate(model.getExpiryDate()));
		
		ArrayList<Integer> oldChildIds = null;
		Collection<Integer> deletedIds = null;
		Collection<Integer> newIds = null;
		
		oldChildIds = new ArrayList<Integer>();
		for (Outlet child : oldEntity.getOutlets()) {
			oldChildIds.add(child.getId());
		}
		deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldChildIds, model.getOutletIds() == null ? new ArrayList<Integer>() : model.getOutletIds());
		newIds = (Collection<Integer>)CollectionUtils.subtract(model.getOutletIds() == null ? new ArrayList<Integer>() : model.getOutletIds(), oldChildIds);
		for (Integer childId : deletedIds) {
			Iterator<Outlet> it = oldEntity.getOutlets().iterator();
			while (it.hasNext()) {
				Outlet child = it.next();
				if (child.getId().intValue() == childId)
					it.remove();
			}
		}
		for (Integer childId : newIds) {
			Outlet child = outletDao.findById(childId);
			oldEntity.getOutlets().add(child);
		}
		
		oldChildIds = new ArrayList<Integer>();
		for (Product child : oldEntity.getProducts()) {
			oldChildIds.add(child.getId());
		}
		deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldChildIds, model.getProductIds() == null ? new ArrayList<Integer>() : model.getProductIds());
		newIds = (Collection<Integer>)CollectionUtils.subtract(model.getProductIds() == null ? new ArrayList<Integer>() : model.getProductIds(), oldChildIds);
		for (Integer childId : deletedIds) {
			Iterator<Product> it = oldEntity.getProducts().iterator();
			while (it.hasNext()) {
				Product child = it.next();
				if (child.getId().intValue() == childId)
					it.remove();
			}
		}
		for (Integer childId : newIds) {
			Product child = productDao.findById(childId);
			oldEntity.getProducts().add(child);
		}
		
		oldChildIds = new ArrayList<Integer>();
		for (Quotation child : oldEntity.getQuotations()) {
			oldChildIds.add(child.getId());
		}
		deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldChildIds, model.getQuotationIds() == null ? new ArrayList<Integer>() : model.getQuotationIds());
		newIds = (Collection<Integer>)CollectionUtils.subtract(model.getQuotationIds() == null ? new ArrayList<Integer>() : model.getQuotationIds(), oldChildIds);
		for (Integer childId : deletedIds) {
			Iterator<Quotation> it = oldEntity.getQuotations().iterator();
			while (it.hasNext()) {
				Quotation child = it.next();
				if (child.getId().intValue() == childId)
					it.remove();
			}
		}
		for (Integer childId : newIds) {
			Quotation child = quotationDao.findById(childId);
			oldEntity.getQuotations().add(child);
		}
		
		oldChildIds = new ArrayList<Integer>();
		for (Unit child : oldEntity.getUnits()) {
			oldChildIds.add(child.getId());
		}
		deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldChildIds, model.getUnitIds() == null ? new ArrayList<Integer>() : model.getUnitIds());
		newIds = (Collection<Integer>)CollectionUtils.subtract(model.getUnitIds() == null ? new ArrayList<Integer>() : model.getUnitIds(), oldChildIds);
		for (Integer childId : deletedIds) {
			Iterator<Unit> it = oldEntity.getUnits().iterator();
			while (it.hasNext()) {
				Unit child = it.next();
				if (child.getId().intValue() == childId)
					it.remove();
			}
		}
		for (Integer childId : newIds) {
			Unit child = unitDao.findById(childId);
			oldEntity.getUnits().add(child);
		}
		
		pointToNoteDao.save(oldEntity);
		pointToNoteDao.flush();
	}
	
	public List<PointToNoteSyncData> getUpdatePointToNote(Date lastSyncTime){
		return pointToNoteDao.getUpdatePointToNote(lastSyncTime);
	}
	
	public List<PointToNoteOutletSyncData> getUpdatePointToNoteOutlet(Date lastSyncTime){
		return pointToNoteDao.getUpdatePointToNoteOutlet(lastSyncTime);
	}
	
	public List<PointToNoteProductSyncData> getUpdatePointToNoteProduct(Date lastSyncTime){
		return pointToNoteDao.getUpdatePointToNoteProduct(lastSyncTime);
	}
	
	public List<PointToNoteQuotationSyncData> getUpdatePointToNoteQuotation(Date lastSyncTime){
		return pointToNoteDao.getUpdatePointToNoteQuotation(lastSyncTime);
	}
	
	public List<PointToNoteUnitSyncData> getUpdatePointToNoteUnit(Date lastSyncTime){
		return pointToNoteDao.getUpdatePointToNoteUnit(lastSyncTime);
	}
}
