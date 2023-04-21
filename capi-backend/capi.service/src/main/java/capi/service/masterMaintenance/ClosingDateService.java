package capi.service.masterMaintenance;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ClosingDateDao;
import capi.entity.ClosingDate;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.masterMaintenance.ClosingDateEditModel;
import capi.model.masterMaintenance.ClosingDateTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("ClosingDateService")
public class ClosingDateService extends BaseService {
	

	
	@Autowired
	private ClosingDateDao closingDateDao;
	
	@Autowired
	private CommonService commonService;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<ClosingDateTableList> queryClosingDate(DatatableRequestModel model){
		
		Order order = this.getOrder(model,"", "c.referenceMonth", "c.closingDate", "c.publishDate");
		
		String search = model.getSearch().get("value");
		
		List<ClosingDateTableList> result = closingDateDao.listClosingDate(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<ClosingDateTableList> response = new DatatableResponseModel<ClosingDateTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = closingDateDao.countClosingDate();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = closingDateDao.countClosingDate(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	

	/**
	 * Get by ID
	 */
	public ClosingDate getClosingDateById(int id) {
		return closingDateDao.findById(id);
	}
	
	/**
	 * Get by Reference Month
	 * @throws ParseException 
	 */
	public ClosingDate getClosingDateByReferenceMonth(String refMonth) throws ParseException {
		Date month = commonService.getMonth(refMonth);		
		return closingDateDao.getClosingDateByReferenceMonth(month);
	}
	

	/**
	 * Save
	 * @throws ParseException 
	 */
	@Transactional
	public void saveClosingDate(ClosingDateEditModel model) throws ParseException {
		
		ClosingDate oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getClosingDateById(model.getId());
		} else {
			oldEntity = new ClosingDate();
		}		
		
		oldEntity.setClosingDate(commonService.getDate(model.getClosingDate())); 
		oldEntity.setPublishDate(commonService.getDate(model.getPublishDate()));
		oldEntity.setReferenceMonth(commonService.getMonth(model.getReferenceMonth()));		

		closingDateDao.save(oldEntity);
		closingDateDao.flush();
		
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteClosingDates(List<Integer> id) {
		List<ClosingDate> closingDates = closingDateDao.getClosingDateByIds(id);
		if (id.size() != closingDates.size()){
			return false;
		}
		
		for (ClosingDate closingDate : closingDates){
			closingDateDao.delete(closingDate);
		}

		closingDateDao.flush();

		return true;
	}
	
	
	/**
	 * Convert entity to model
	 */
	public ClosingDateEditModel convertEntityToModel(ClosingDate entity) {
		
		ClosingDateEditModel model = new ClosingDateEditModel();
		model.setClosingDateId(entity.getClosingDateId());
		if (entity.getClosingDate() != null){
			model.setClosingDate(commonService.formatDate(entity.getClosingDate()));
			model.setPublishDate(commonService.formatDate(entity.getPublishDate()));	
			model.setReferenceMonth(commonService.formatMonth(entity.getReferenceMonth()));
			model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
			model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
		}

		
		return model;
	
	}
}
