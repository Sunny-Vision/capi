package capi.service.masterMaintenance;

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.QuotationDao;
import capi.dal.QuotationLoadingDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.Quotation;
import capi.entity.QuotationLoading;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.masterMaintenance.QuotationLoadingEditModel;
import capi.model.masterMaintenance.QuotationLoadingTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("QuotationLoadingService")
public class QuotationLoadingService extends BaseService {
	

	
	@Autowired
	private QuotationLoadingDao quotationLoadingDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private CommonService commonService;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<QuotationLoadingTableList> queryQuotationLoading(DatatableRequestModel model){
		

		Order order = this.getOrder(model,"", "d.englishName","o.shortCode", "quotationPerManDay");
		
		String search = model.getSearch().get("value");
		
		List<QuotationLoadingTableList> result = quotationLoadingDao.listQuotationLoading(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<QuotationLoadingTableList> response = new DatatableResponseModel<QuotationLoadingTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationLoadingDao.countQuotationLoading();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationLoadingDao.countQuotationLoading(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	

	/**
	 * Get by ID
	 */
	public QuotationLoading getQuotationLoadingById(int id) {
		return quotationLoadingDao.findById(id);
	}
	
	/**
	 * Get by District & Outlet Types
	 */
	public QuotationLoading getQuotationLoadingByDistrictOutlet(int districtId, String outletTypeId) {
		return quotationLoadingDao.getQuotationLoadingByDistrictOutlet(districtId, outletTypeId);
	}
		
	/**
	 * Save
	 */
	@Transactional
	public boolean saveQuotationLoading(QuotationLoadingEditModel model) {
		
		QuotationLoading oldEntity = null;
		QuotationLoading tempEntity = getQuotationLoadingByDistrictOutlet(model.getDistrictId(), model.getOutletTypeId());
		

		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getQuotationLoadingById(model.getId());
		} 
		else if (tempEntity != null){ 	//check combination of district and outlet type
			oldEntity = tempEntity;
		} else {
			oldEntity = new QuotationLoading();
		}
		// save reciprocal value of QuotationPerManDay to related quotations
		List<Quotation> quotations = quotationDao.getAllByDistrictOutletTypes(model.getDistrictId(), model.getOutletTypeId());

		for (Quotation quotation : quotations){
			quotation.setQuotationLoading(1/model.getQuotationPerManDay());
			quotationDao.save(quotation);
		}
		
		oldEntity.setDistrict(districtService.getDistrictById(model.getDistrictId()));
		oldEntity.setQuotationPerManDay(model.getQuotationPerManDay());
		oldEntity.getOutletTypes().add(outletTypeDao.findById(model.getOutletTypeId()));

		quotationLoadingDao.save(oldEntity);
		quotationLoadingDao.flush();
		
		return true;
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteQuotationLoadings(List<Integer> id) {
		List<QuotationLoading> quotationLoadings = quotationLoadingDao.getQuotationLoadingByIds(id);
		if (id.size() != quotationLoadings.size()){
			return false;
		}
		
		for (QuotationLoading quotationLoading : quotationLoadings){
			quotationLoading.getOutletTypes().clear();
			quotationLoadingDao.delete(quotationLoading);
		}

		quotationLoadingDao.flush();

		return true;
	}
	
	
	/**
	 * Convert entity to model
	 */
	public QuotationLoadingEditModel convertEntityToModel(QuotationLoading entity, String shortCode) {
		
		QuotationLoadingEditModel model = new QuotationLoadingEditModel();
		
		BeanUtils.copyProperties(entity, model);
		if(entity.getDistrict() != null){
			model.setDistrictId(entity.getDistrict().getId());
			model.setDistrictLabel(entity.getDistrict().getCode() + " - " + entity.getDistrict().getEnglishName());
		}
		
		model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
		model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
		if(!entity.getOutletTypes().isEmpty()){
			Iterator<VwOutletTypeShortForm> outletTypes = entity.getOutletTypes().iterator();
			while(outletTypes.hasNext()) {
				VwOutletTypeShortForm outletType = outletTypes.next();
				if(shortCode.equals(outletType.getShortCode())) {
					model.setOutletTypeId(outletType.getId());
					break;
				}
			}
//			model.setOutletTypeId(outletTypes.next().getId());
		}
		
		return model;
	
	}
}
