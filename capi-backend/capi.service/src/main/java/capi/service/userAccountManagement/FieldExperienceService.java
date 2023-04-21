package capi.service.userAccountManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.DistrictDao;
import capi.dal.StaffReasonDao;
import capi.dal.UserDao;
import capi.entity.District;
import capi.entity.StaffReason;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.userAccountManagement.FieldExperienceTableList;
import capi.model.userAccountManagement.StaffReasonModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("FieldExperienceService")
public class FieldExperienceService extends BaseService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private StaffReasonDao staffReasonDao;

	/**
	 * Get by ID
	 */
	public User getUserById(int id) {
		return userDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<FieldExperienceTableList> getFieldExperienceList(DatatableRequestModel model){

		Order order = this.getOrder(model, "staffCode", "team", "chineseName", "englishName");
		
		String search = model.getSearch().get("value");
		
		Integer userId = null;
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		if(!((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2 
				|| (detail.getAuthorityLevel() & 512) == 512 || (detail.getAuthorityLevel() & 2048) == 2048)){
			userId = detail.getUserId();
		}
		
		List<FieldExperienceTableList> result = userDao.selectAllFieldExperience(userId, search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<FieldExperienceTableList> response = new DatatableResponseModel<FieldExperienceTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = userDao.countSelectAllFieldExperience(userId, "");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = userDao.countSelectAllFieldExperience(userId, search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save Field Experience
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public void saveFieldExperience(FieldExperienceTableList model) throws Exception {

		User oldEntity = null;
		if (model.getUserId() != null && model.getUserId() > 0) {
			oldEntity = getUserById(model.getUserId());
			
			if(model.getDistrictIds() != null) {
				ArrayList<Integer> oldDistrictIds = new ArrayList<Integer>();
				for (District district : oldEntity.getDistricts()) {
					oldDistrictIds.add(district.getId());
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldDistrictIds, model.getDistrictIds());
				Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getDistrictIds(), oldDistrictIds);
				
				if (deletedIds.size() > 0){
					List<District> deletedDistricts = districtDao.getByIds(deletedIds.toArray(new Integer[0]));
					for (District district: deletedDistricts){
						district.setUser(null);
						districtDao.save(district);
					}
				}
				
				if (newIds.size() > 0) {
					List<District> newDistricts = districtDao.getByIds(newIds.toArray(new Integer[0]));
					for (District district: newDistricts){
						district.setUser(oldEntity);
						districtDao.save(district);
					}
				}
			} else {
				ArrayList<Integer> oldDistrictIds = new ArrayList<Integer>();
				for (District district : oldEntity.getDistricts()) {
					oldDistrictIds.add(district.getId());
				}
				if(oldDistrictIds.size() > 0) {
					List<District> districts =  districtDao.getDistrictsByIds(oldDistrictIds);
					for(District district : districts) {
						district.setUser(null);
						districtDao.save(district);
					}
				}
			}
			
			if (model.getReasons().size() > 0){
				List<Integer> removed = new ArrayList<Integer>();
				List<Integer> oldModelIds = new ArrayList<Integer>();
				List<StaffReason> newReason = new ArrayList<StaffReason>();
				Hashtable<Integer, StaffReasonModel> reasonMap = new Hashtable<Integer, StaffReasonModel>();
				for (StaffReasonModel reasonModel : model.getReasons()){
					if (reasonModel.getStaffReasonId() == null){
						StaffReason reason = new StaffReason();
						reason.setFromDate(commonService.getDate(reasonModel.getFromDate()));
						reason.setToDate(commonService.getDate(reasonModel.getToDate()));
						reason.setReason(reasonModel.getReason());
						reason.setUser(oldEntity);
						newReason.add(reason);
					}
					else{
						oldModelIds.add(reasonModel.getStaffReasonId());
						reasonMap.put(reasonModel.getStaffReasonId(), reasonModel);
					}
				}
				
				// remove
				//2018-01-10 cheung missing handle new reason after remove all
				if(oldModelIds.size() == 0)
					oldModelIds.add(-1);
				
				if (oldModelIds.size() > 0){
					List<StaffReason> notExists = staffReasonDao.getNotExistStaffReasons(oldModelIds);
					for (StaffReason reason : notExists){
						removed.add(reason.getId());
						staffReasonDao.delete(reason);
					}
				}
				
				// update
				Collection<Integer> updateList = CollectionUtils.subtract(oldModelIds, removed);
				if (updateList.size() > 0){
					List<StaffReason> reasons = staffReasonDao.getStaffReasonByIds(updateList.toArray(new Integer[0]));
					for (StaffReason reason : reasons){
						StaffReasonModel reasonModel = reasonMap.get(reason.getStaffReasonId());
						reason.setFromDate(commonService.getDate(reasonModel.getFromDate()));
						reason.setToDate(commonService.getDate(reasonModel.getToDate()));
						reason.setReason(reasonModel.getReason());
					}
				}
				
				// insert
				if (newReason.size() > 0){
					for (StaffReason reason : newReason){
						staffReasonDao.save(reason);
					}
				}
				
			}else{ //remove all
				//2018-01-10 cheung_cheng missing handle remove all reason.
				ArrayList<Integer> removeAllReasons = new ArrayList<Integer>();
				removeAllReasons.add(-1);// remove all reason that reason id not in -1 (no reason id = -1)
				List<StaffReason> notExists = staffReasonDao.getNotExistStaffReasons(removeAllReasons);
				for (StaffReason reason : notExists){
					staffReasonDao.delete(reason);
				}
			}
			
			oldEntity.setTeam(model.getTeam());
			oldEntity.setAccumulatedOT(model.getAccumulatedOT());
			oldEntity.setGic(model.getGic());
			oldEntity.setHomeArea(model.getHomeArea());
			oldEntity.setOmp(model.getOmp());
			oldEntity.setGHS(model.isGHS());

			userDao.save(oldEntity);
			userDao.flush();
		}
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public FieldExperienceTableList convertEntityToModel(User entity){

		FieldExperienceTableList model = new FieldExperienceTableList();
		BeanUtils.copyProperties(entity, model);

		List<Integer> districtIds = userDao.getDistrictIdsFromUser(entity.getUserId());
		model.setDistrictIds(districtIds);

		FieldExperienceTableList info = userDao.getRankNSupervisorFromUser(entity.getUserId());
		model.setRankName(info.getRankName());
		model.setSupervisorId(info.getSupervisorId());
		model.setSupervisorStaffCode(info.getSupervisorStaffCode());
		model.setSupervisorChineseName(info.getSupervisorChineseName());
		model.setSupervisorDestination(info.getSupervisorDestination());
		
		ArrayList<StaffReasonModel> reasons = new ArrayList<StaffReasonModel>();
		List<StaffReason> orgReasons = staffReasonDao.getStaffReasonByUserId(entity.getUserId());
		
		for (StaffReason reason : orgReasons){
			StaffReasonModel rModel = new StaffReasonModel();
			rModel.setFromDate(commonService.formatDate(reason.getFromDate()));
			rModel.setToDate(commonService.formatDate(reason.getToDate()));
			rModel.setReason(reason.getReason());
			rModel.setStaffReasonId(reason.getStaffReasonId());
			reasons.add(rModel);
		}
		model.setReasons(reasons);

		return model;
	}

	public List<DistrictEditModel> getAllDistrict() {
		return districtDao.getAllDistrict();
	}

}
