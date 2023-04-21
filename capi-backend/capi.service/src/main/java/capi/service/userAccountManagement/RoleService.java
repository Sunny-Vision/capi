package capi.service.userAccountManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import capi.dal.RoleDao;
import capi.dal.SystemFunctionDao;
import capi.entity.Role;
import capi.entity.SystemFunction;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.RoleSyncData;
import capi.model.userAccountManagement.RoleEditModel;
import capi.model.userAccountManagement.RoleTableList;
import capi.service.BaseService;

@Service("RoleService")
public class RoleService extends BaseService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private SystemFunctionDao systemFunctionDao;

	/**
	 * Get by ID
	 */
	public Role getRoleById(int id) {
		return roleDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<RoleTableList> getRoleList(DatatableRequestModel model){

		Order order = this.getOrder(model, "roleId", "roleName", "roleDescription");
		
		String search = model.getSearch().get("value");
		
		List<RoleTableList> result = roleDao.selectAllRole(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<RoleTableList> response = new DatatableResponseModel<RoleTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = roleDao.countSelectAllRole("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = roleDao.countSelectAllRole(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query Back-end Function List
	 */
	public DatatableResponseModel<RoleTableList> getBackendFunctionList(DatatableRequestModel model){

		Order order = this.getOrder(model, "systemFunctionId", "functionCode", "functionDescription");
		
		String search = model.getSearch().get("value");
		
		List<RoleTableList> result = roleDao.selectAllBackendFunctionList(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<RoleTableList> response = new DatatableResponseModel<RoleTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = roleDao.countSelectAllBackendFunctionList("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = roleDao.countSelectAllBackendFunctionList(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query Front-end Function List
	 */
	public DatatableResponseModel<RoleTableList> getFrontendFunctionList(DatatableRequestModel model){

		Order order = this.getOrder(model, "systemFunctionId", "functionCode", "functionDescription");
		
		String search = model.getSearch().get("value");
		
		List<RoleTableList> result = roleDao.selectAllFrontendFunctionList(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<RoleTableList> response = new DatatableResponseModel<RoleTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = roleDao.countSelectAllFrontendFunctionList("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = roleDao.countSelectAllFrontendFunctionList(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save role
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public String saveRole(RoleEditModel model) throws Exception {

		Role oldEntity = null;
		if (model.getRoleId() != null && model.getRoleId() > 0) {
			oldEntity = getRoleById(model.getRoleId());
		} else {
			// create acting
			Role role = roleDao.getRoleByCode(model.getRoleName());
//			if (role != null) return false;
			if (role != null) return "Duplicated";
			
			oldEntity = new Role();
		}

		oldEntity.setName(model.getRoleName());
		oldEntity.setDescription(model.getRoleDescription());
		
		int authorityLevel = 0;
		if(model.getAuthorityLevelId() != null && model.getAuthorityLevelId().size() > 0) {
			for(Integer authority : model.getAuthorityLevelId()) {
				authorityLevel = (authorityLevel | authority);
			}
		}
		if(authorityLevel == 0) {
			return "Authority";
		}
		oldEntity.setAuthorityLevel(authorityLevel);

		boolean backendFunctionFlag = true;
		List<Integer> backendFunctionListId = model.getBackendSystemFunctionId();
		// remove null value (first one) because of convert entity to model when get into add page
		/*for(int i = 0; i < backendFunctionListId.size(); i++) {
			if(backendFunctionListId.get(i) == null) {
				backendFunctionListId.remove(i);
			} else {
				
			}
		}*/
		if(backendFunctionListId == null) {
			backendFunctionListId = new ArrayList<Integer>();
			backendFunctionFlag = false;
		}
		
		boolean frontendFunctionFlag = true;
		List<Integer> frontendFunctionListId = model.getFrontendSystemFunctionId();
		// remove null value (first one) because of convert entity to model when get into add page
		/*for(int i = 0; i < frontendFunctionListId.size(); i++) {
			if(frontendFunctionListId.get(i) == null) {
				frontendFunctionListId.remove(i);
			}
		}*/
		if(frontendFunctionListId == null) {
			frontendFunctionListId = new ArrayList<Integer>();
			frontendFunctionFlag = false;
		}
		
		if( !(backendFunctionFlag || frontendFunctionFlag) ) {
			return "Function";
		}
		
		if (model.getBackendSystemFunctionId() != null || model.getFrontendSystemFunctionId() != null) {
			ArrayList<Integer> oldFunctionIds = new ArrayList<Integer>();
			for (SystemFunction systemFunction : oldEntity.getFunctions()) {
				oldFunctionIds.add(systemFunction.getId());
			}
			
			Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldFunctionIds, backendFunctionListId);
			deletedIds = (Collection<Integer>)CollectionUtils.subtract(deletedIds, frontendFunctionListId);
			Collection<Integer> newIdsOfBackendFunction = (Collection<Integer>)CollectionUtils.subtract(backendFunctionListId, oldFunctionIds);
			Collection<Integer> newIdsOfFrontendFunction = (Collection<Integer>)CollectionUtils.subtract(frontendFunctionListId, oldFunctionIds);
			Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.union(newIdsOfBackendFunction, newIdsOfFrontendFunction);
			
			if (deletedIds.size() > 0){
				List<SystemFunction> deletedRoleFunctions = systemFunctionDao.getDeleteRoleFunctionsByIds(model.getRoleId(), deletedIds.toArray(new Integer[0]));
				for (SystemFunction systemFunction: deletedRoleFunctions){
					oldEntity.getFunctions().remove(systemFunction);
				}
			}
			
			if (newIds.size() > 0) {
				List<SystemFunction> newRoleFunctions = systemFunctionDao.getNewRoleFunctionsByIds(newIds.toArray(new Integer[0]));
				oldEntity.getFunctions().addAll(newRoleFunctions);
			}
		} else {
			oldEntity.getFunctions().clear();
		}

		roleDao.save(oldEntity);
		roleDao.flush();

		return "Success";
	}

	/**
	 * Delete Role
	 */
	@Transactional
	public boolean deleteRole(List<Integer> ids) {
		
		List<Role> roles = roleDao.getRolesByIds(ids);
		if (ids.size() != roles.size()){
			return false;
		}
		
		for (Role role : roles){
			role.getFunctions().clear();
			roleDao.delete(role);
		}

		roleDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws JsonProcessingException 
	 */
	public RoleEditModel convertEntityToModel(Role entity) throws JsonProcessingException {

		RoleEditModel model = new RoleEditModel();
		BeanUtils.copyProperties(entity, model);
		
		model.setRoleName(entity.getName());
		model.setRoleDescription(entity.getDescription());
		
		List<Integer> authority = new ArrayList<Integer> ();
		for(int i = 1; i <= 2048; i *= 2) {
			if(model.hasAuthority(i)) {
				authority.add(new Integer(i));
			}
		}
		model.setAuthorityLevelId(authority);
		
		List<Integer> backendSystemFunctionId = roleDao.selectBackendFunctionWithRoleId(entity.getRoleId());
		model.setBackendSystemFunctionId(backendSystemFunctionId);
		
		List<Integer> frontendSystemFunctionId = roleDao.selectFrontendFunctionWithRoleId(entity.getRoleId());
		model.setFrontendSystemFunctionId(frontendSystemFunctionId);
		
		/*
		if(entity.getAuthorityLevel() != null) {
			ArrayList<Integer> displayAuthorityLevel = new ArrayList<Integer>();
			int authorityLevel = entity.getAuthorityLevel();
			for(int compareValue = 1; compareValue <= 2048; compareValue = (compareValue << 1)) {
				if( (authorityLevel & compareValue) == compareValue ) {
					displayAuthorityLevel.add(compareValue);
				}
			}
			model.setAuthorityLevelId(displayAuthorityLevel);
		}*/

		return model;
	}

	/**
	 * Get role select format
	 */
	public Select2ResponseModel queryRoleSelect(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<Role> entities = roleDao.searchRole(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = roleDao.countSearchRole(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Role r : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(r.getRoleId()));
			item.setText(r.getName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public List<RoleSyncData> getUpdateRole(Date lastSyncTime){
		return roleDao.getUpdateRole(lastSyncTime);
	}
}
