package capi.service.userAccountManagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ActingDao;
import capi.dal.RoleDao;
import capi.dal.UserDao;
import capi.entity.Acting;
import capi.entity.Role;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.userAccountManagement.ActingModel;
import capi.model.userAccountManagement.ActingTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("ActingService")
public class ActingService extends BaseService {

	@Autowired
	private ActingDao actingDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private CommonService commonService;

	/**
	 * Get by ID
	 */
	public Acting getActingById(int id) {
		return actingDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<ActingTableList> getActingList(DatatableRequestModel model){

		Order order = this.getOrder(model, "actingId", "staff", "replacement", "roleName", "a.startDate", "a.endDate");
		
		String search = model.getSearch().get("value");
		
		List<ActingTableList> result = actingDao.searchActingList(search, model.getStart(), model.getLength(), order);
		
		/*for(ActingTableList record : result) {
			record.setStaff(record.getStaffCode() + " - " + record.getStaffChineseName());
			record.setReplacement(record.getReplacementCode() + " - " + record.getReplacementChineseName());
		}*/
		
		DatatableResponseModel<ActingTableList> response = new DatatableResponseModel<ActingTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = actingDao.countActingList("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = actingDao.countActingList(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	

	/**
	 * Save acting
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveActing(ActingTableList model) throws Exception {

		Acting oldEntity = null;
		if (model.getActingId() != null && model.getActingId() > 0) {
			oldEntity = getActingById(model.getActingId());
		} else {
			// create acting
			oldEntity = new Acting();
		}

		if(model.getStaffId().equals(model.getReplacementId())) {
			return false;
		}

		User staff = userDao.findById(model.getStaffId());
		User replacement = userDao.findById(model.getReplacementId());
		Role role = null;
		if(model.getRoleId() != null) {
			role = roleDao.findById(model.getRoleId());
		}

		oldEntity.setStaff(staff);
		oldEntity.setReplacement(replacement);
		oldEntity.setRole(role);
		
//		SimpleDateFormat formatter = new SimpleDateFormat(SystemConstant.DATE_FORMAT);
//		oldEntity.setStartDate(formatter.parse(model.getStartDate()));
//		oldEntity.setEndDate(formatter.parse(model.getEndDate()));

		oldEntity.setStartDate(commonService.getDate(model.getStartDate()));
		oldEntity.setEndDate(commonService.getDate(model.getEndDate()));
		
		actingDao.save(oldEntity);
		actingDao.flush();

		return true;
	}

	/**
	 * Delete Acting
	 */
	@Transactional
	public boolean deleteActing(List<Integer> ids) {
		
		List<Acting> actings = actingDao.getActingsByIds(ids);
		if (ids.size() != actings.size()){
			return false;
		}
		
		for (Acting acting : actings){
			actingDao.delete(acting);
		}

		actingDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 */
	public ActingTableList convertEntityToModel(Acting entity) {

		ActingTableList model = new ActingTableList();
		BeanUtils.copyProperties(entity, model);
		
		if(entity.getActingId() != null) {
			model.setStaffId(entity.getStaff().getUserId());
			model.setStaffCode(entity.getStaff().getStaffCode());
			model.setStaffEnglishName(entity.getStaff().getEnglishName());
			model.setStaffChineseName(entity.getStaff().getChineseName());
			model.setStaff(model.getStaffCode() + " - " + model.getStaffChineseName());
			
			model.setReplacementId(entity.getReplacement().getUserId());
			model.setReplacementCode(entity.getReplacement().getStaffCode());
			model.setReplacementEnglishName(entity.getReplacement().getEnglishName());
			model.setReplacementChineseName(entity.getReplacement().getChineseName());
			model.setReplacement(model.getReplacementCode() + " - " + model.getReplacementChineseName());
			
			model.setStartDate(commonService.formatDate(entity.getStartDate()));
			model.setEndDate(commonService.formatDate(entity.getEndDate()));
			
			if(entity.getRole() != null) {
				model.setRoleId(entity.getRole().getRoleId());
				model.setRoleName(entity.getRole().getName());
			}
		}
		
		return model;
	}
	
	/**
	 * Get by ID
	 */
	public List<Integer> getActingUserIdsByUserId(int userId) {
		return actingDao.getActingUserIdsByUserId(userId);
	}
	
	/*
	 * Get Acting Details
	 */
	public List<ActingModel> getActingDetailsByReplacementUserId(int userId) {
		List<ActingModel> actings = actingDao.getActingDetailsByReplacementUserId(userId);
		
		HashMap<Integer, ActingModel> actingModelMap = new HashMap<Integer, ActingModel>();
		
		if ( actings != null && !actings.isEmpty() ) {
			while (!actings.isEmpty()) {
				ActingModel item = actings.remove(0);
				
				if ( !actingModelMap.containsKey(item.getStaffId()) ) {
					actingModelMap.put(item.getStaffId(), item);
				} else {
					ActingModel tempItem = actingModelMap.get(item.getStaffId());
					tempItem.setGrantAuthorityLevel(tempItem.getGrantAuthorityLevel() | item.getGrantAuthorityLevel());
				}
			}
		}
		
		List<ActingModel> resultSet = new ArrayList<ActingModel>(actingModelMap.values());
		
		if (resultSet != null && !resultSet.isEmpty()) {
			Collections.sort(resultSet, new Comparator<ActingModel>() {

				@Override
				public int compare(ActingModel o1, ActingModel o2) {
					return o1.getGrantAuthorityLevel().compareTo(o2.getGrantAuthorityLevel());
				}
				
			});
		}
		
		return resultSet;
	}

}
