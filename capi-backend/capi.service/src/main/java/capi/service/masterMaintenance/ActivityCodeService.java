package capi.service.masterMaintenance;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ActivityCodeDao;
import capi.entity.ActivityCode;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.service.BaseService;

@Service("ActivityCodeService")
public class ActivityCodeService extends BaseService {
	

	
	@Autowired
	private ActivityCodeDao activityCodeDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<ActivityCode> queryActivityCode(DatatableRequestModel model){
		
		Order order = this.getOrder(model,"", "code", "description", "manDay");
		
		String search = model.getSearch().get("value");
		
		List<ActivityCode> result = activityCodeDao.listActivityCode(search, model.getStart(), model.getLength(), order);

		
		DatatableResponseModel<ActivityCode> response = new DatatableResponseModel<ActivityCode>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = activityCodeDao.countActivityCode();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = activityCodeDao.countActivityCode(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	

	/**
	 * Get by ID
	 */
	public ActivityCode getActivityCodeById(int id) {
		return activityCodeDao.findById(id);
	}
	
	/**
	 * Get All
	 */
	public List<ActivityCode> getActivityCodes() {
		return activityCodeDao.findAll();
	}
	

	/**
	 * Save
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveActivityCode(ActivityCode model) {
		
		ActivityCode oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getActivityCodeById(model.getId());
		} else {
			ActivityCode activityCode = activityCodeDao.getActivityCodeByCode(model.getCode());
			if (activityCode!=null) return false;
			
			oldEntity = new ActivityCode();
		}
		
		oldEntity.setCode(model.getCode());
		oldEntity.setDescription(model.getDescription());
		oldEntity.setManDay(model.getManDay());
		
		activityCodeDao.save(oldEntity);
		activityCodeDao.flush();
		
		return true;
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteActivityCodes(List<Integer> id) {
		List<ActivityCode> activityCodes = activityCodeDao.getUActivityCodeByIds(id);
		if (id.size() != activityCodes.size()){
			return false;
		}
		
		for (ActivityCode activityCode : activityCodes){
			activityCodeDao.delete(activityCode);
		}

		activityCodeDao.flush();

		return true;
	}
	@Transactional
	public ActivityCode createActivityCode(String code, String description, Double manDay){
		ActivityCode ac = new ActivityCode();
		ac.setCode(code);
		ac.setDescription(description);
		ac.setManDay(manDay);
		
		activityCodeDao.save(ac);
		activityCodeDao.flush();
		return ac;
	}

}
