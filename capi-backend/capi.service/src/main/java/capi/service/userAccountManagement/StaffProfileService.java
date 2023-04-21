package capi.service.userAccountManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import capi.dal.BatchDao;
import capi.dal.PasswordHistoryDao;
import capi.dal.PasswordPolicyDao;
import capi.dal.RankDao;
import capi.dal.RoleDao;
import capi.dal.UserDao;
import capi.entity.Batch;
import capi.entity.PasswordHistory;
import capi.entity.Rank;
import capi.entity.Role;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.userAccountManagement.StaffProfileEditModel;
import capi.model.userAccountManagement.StaffProfileTableList;
import capi.model.userAccountManagement.UserRoleTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("StaffProfileService")
public class StaffProfileService extends BaseService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private RankDao rankDao;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PasswordHistoryService passwordHistoryService;
	/**
	 * Get by ID
	 */
	public User getStaffProfileById(int id) {
		return userDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<StaffProfileTableList> getStaffProfileList(DatatableRequestModel model){

		Order order = this.getOrder(model, "userId", "staffCode", "englishName", "chineseName", "rank", "supervisorStaffCode");
		
		String search = model.getSearch().get("value");
		
		List<StaffProfileTableList> result = userDao.selectAllStaffProfile(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<StaffProfileTableList> response = new DatatableResponseModel<StaffProfileTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = userDao.countSelectAllStaffProfile("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = userDao.countSelectAllStaffProfile(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Get role list
	 */
	public List<Role> getRoleList(List<Integer> ids) {
		return roleDao.getRolesByIds(ids);
	}

	/**
	 * Get all batch Code
	 */
	public List<Batch> getBatchCodes() {
		return batchDao.getAll();
	}

	/**
	 * DataTable query User Role List
	 */
	public DatatableResponseModel<UserRoleTableList> getUserRoleList(DatatableRequestModel model, Integer userId){

		Order order = this.getOrder(model, "code", "description", "roleId");
		
		String search = model.getSearch().get("value");
		
		List<UserRoleTableList> result = userDao.selectUserRoleList(search, model.getStart(), model.getLength(), order, userId);
		
		DatatableResponseModel<UserRoleTableList> response = new DatatableResponseModel<UserRoleTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = userDao.countSelectUserRoleList("", userId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = userDao.countSelectUserRoleList(search, userId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}


	/**
	 * Save Staff Profile
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public String saveStaffProfile(StaffProfileEditModel model, int authorityLevel, String act) throws Exception {

		User oldEntity = null;
		if (model.getUserId() != null && model.getUserId() > 0) {
			oldEntity = getStaffProfileById(model.getUserId());
		} else {
			User userByStaffCode = userDao.getUserByStaffCode(model.getStaffCode());
			if(userByStaffCode != null) return "Fail";
			
			if((authorityLevel & 512) == 512) {
				User userByUserName = userDao.findUserByUsername(model.getUsername());
				if(userByUserName != null) return "Fail";
			}
			
			// create district
			oldEntity = new User();
		}

		// Staff Profile
		oldEntity.setStaffType(model.getStaffType());
		oldEntity.setStaffCode(model.getStaffCode());
		oldEntity.setEnglishName(model.getEnglishName());
		oldEntity.setChineseName(model.getChineseName());
		oldEntity.setOfficePhoneNo(model.getOfficePhoneNo());
		oldEntity.setGender(model.getGender());
		oldEntity.setOffice(model.getOffice());
		oldEntity.setOffice2(model.getOffice2());
		
		if(model.getStaffType() == 2) {
			
			if (model.getBatchCodeIds() != null) {
				ArrayList<Integer> oldBatchCodeIds = new ArrayList<Integer>();
				for (Batch batchCode : oldEntity.getBatches()) {
					oldBatchCodeIds.add(batchCode.getId());
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldBatchCodeIds, model.getBatchCodeIds());
				Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getBatchCodeIds(), oldBatchCodeIds);
				
				if (deletedIds.size() > 0){
					List<Batch> deletedBatchCodes = batchDao.getByIds(deletedIds.toArray(new Integer[0]));
					for (Batch batchCode: deletedBatchCodes){
						oldEntity.getBatches().remove(batchCode);
					}
				}
				
				if (newIds.size() > 0) {
					List<Batch> newBatchCodes = batchDao.getByIds(newIds.toArray(new Integer[0]));
					oldEntity.getBatches().addAll(newBatchCodes);
				}
			} else {
				oldEntity.getBatches().clear();
			}
		} else {
			oldEntity.getBatches().clear();
		}
		
		if(model.getRankId() != null) {
			Rank rank = rankDao.findById(model.getRankId());
			if(rank != null) {
				oldEntity.setRank(rank);
			}
		} else {
			oldEntity.setRank(null);
		}
		
		oldEntity.setDestination(model.getDestination());
		
		if(model.getSupervisorId() != null) {
			User supervisor = userDao.findById(model.getSupervisorId());
			if(supervisor != null) {
				oldEntity.setSupervisor(supervisor);
			}
		} else {
			oldEntity.setSupervisor(null);
		}
		
		// User Account
		if((authorityLevel & 512) == 512) {
			
			oldEntity.setUsername(model.getUsername());
			
			if(act.equals("add")) {
				ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
				String hashPass = encoder.encodePassword(model.getPassword(), null);
				oldEntity.setPassword(hashPass);
			} else {
				if(!StringUtils.isEmpty(model.getPassword())) {
					ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
					String hashPass = encoder.encodePassword(model.getPassword(), null);
					oldEntity.setPassword(hashPass);
					
					//TODO CR6 REQ006
					if (passwordHistoryService.inPasswordMinAge(oldEntity)) {
						return "PasswordMinimumAge";
					}
					
					//TODO CR6 REQ004
					if (passwordHistoryService.isPasswordEnforced(oldEntity, hashPass)) {
						return "EnforcedPassword";
					}
					
					//TODO CR6 REQ004 need to check password history?
					
					//TODO CR6 REQ004
					
					passwordHistoryService.savePasswordHistory(oldEntity, hashPass, true);
				}
			}
			
			if (model.getUserRoleIds() != null) {
				ArrayList<Integer> oldUserRoleIds = new ArrayList<Integer>();
				for (Role role : oldEntity.getRoles()) {
					oldUserRoleIds.add(role.getId());
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldUserRoleIds, model.getUserRoleIds());
				Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getUserRoleIds(), oldUserRoleIds);
				
				if (deletedIds.size() > 0){
					List<Role> deletedUserRoles = roleDao.getByIds(deletedIds.toArray(new Integer[0]));
					for (Role userRole: deletedUserRoles){
						oldEntity.getRoles().remove(userRole);
					}
				}
				
				if (newIds.size() > 0) {
					List<Role> newUserRoles = roleDao.getByIds(newIds.toArray(new Integer[0]));
					oldEntity.getRoles().addAll(newUserRoles);
				}
			} else {
				oldEntity.getRoles().clear();
			}
			
			oldEntity.setStatus(model.getStatus());
			
			
			if(!StringUtils.isEmpty(model.getDateOfEntry()))
				oldEntity.setDateOfEntry(commonService.getDate(model.getDateOfEntry()));
			else
				oldEntity.setDateOfEntry(null);
			if(!StringUtils.isEmpty(model.getDateOfLeaving()))
				oldEntity.setDateOfLeaving(commonService.getDate(model.getDateOfLeaving()));
			else
				oldEntity.setDateOfLeaving(null);
		}

		userDao.save(oldEntity);
		userDao.flush();

		//TODO CR6 REQ004
		if(act.equals("add")) {
			User newUser = userDao.findUserByUsername(oldEntity.getUsername());
			passwordHistoryService.savePasswordHistory(newUser, oldEntity.getPassword(), true);
		}
		
		return "Success";
	}

	/**
	 * Delete Staff Profile
	 */
	@Transactional
	public boolean deleteStaffProfile(List<Integer> ids) {

		List<User> users = userDao.getUsersByIds(ids);
		if (users.size() != ids.size()){
			return false;
		}
		
		for (User user: users){
			user.getRoles().clear();
			user.getBatches().clear();
			userDao.delete(user);
		}		
		
		userDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public StaffProfileEditModel convertEntityToModel(User entity){

		StaffProfileEditModel model = new StaffProfileEditModel();
		BeanUtils.copyProperties(entity, model);

		model.setUsername(entity.getUsername());
		
		List<UserRoleTableList> userRoles = new ArrayList<UserRoleTableList>();
		
		Iterator<Role> roles = entity.getRoles().iterator();
		ArrayList<Integer> userRoleIds = new ArrayList<Integer>();
		/*while (roles.hasNext()) {
			userRoleIds.add(roles.next().getId());
		}*/
		while (roles.hasNext()) {
			Role role = roles.next();
			userRoleIds.add(role.getId());
			
			UserRoleTableList userRole = new UserRoleTableList();
			userRole.setRoleId(role.getRoleId());
			userRole.setCode(role.getName());
			userRole.setDescription(role.getDescription());
			userRoles.add(userRole);
		}
		model.setUserRoleIds(userRoleIds);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(userRoles);
			model.setUserRoleJSON(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		model.setDateOfEntry(commonService.formatDate(entity.getDateOfEntry()));
		model.setDateOfLeaving(commonService.formatDate(entity.getDateOfLeaving()));
		
		if(model.getStaffType() == 2) {
			Iterator<Batch> batchCodes = entity.getBatches().iterator();
			ArrayList<Integer> batchCodeIds = new ArrayList<Integer>();
			while (batchCodes.hasNext()) {
				batchCodeIds.add(batchCodes.next().getId());
			}
			model.setBatchCodeIds(batchCodeIds);
		}
		
		if(entity.getRank() != null) {
			Rank rank = entity.getRank();
			model.setRankId(rank.getId());
			model.setRankLabel(rank.getCode() + " - " + rank.getName());
		}
		
		if(entity.getSupervisor() != null) {
			User supervisor = entity.getSupervisor();
			model.setSupervisorId(supervisor.getId());
			model.setSupervisor(supervisor.getStaffCode() + " - " + supervisor.getChineseName());
		}

		return model;
	}
	
	/**
	 * Get sum authority level by user
	 */
	public int getSumAuthorityLevelByUser(int userId) {
		List<Role> roles = roleDao.getRolesByUser(userId);
		int authorityLevel = 0;
		for (Role r : roles) {
			authorityLevel = authorityLevel | r.getAuthorityLevel();
		}
		return authorityLevel;
	}
	
	
}
