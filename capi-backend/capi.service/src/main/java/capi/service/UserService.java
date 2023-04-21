package capi.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import capi.dal.NotificationDao;
import capi.dal.RoleDao;
import capi.dal.UserDao;
import capi.entity.Notification;
import capi.entity.Role;
import capi.entity.SurveyMonth;
import capi.entity.SystemFunction;
import capi.entity.User;
import capi.entity.UserLockedTimeLog;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.UserAccessModel;
import capi.model.api.dataSync.UserRoleSyncData;
import capi.model.api.dataSync.UserSyncData;
import capi.model.commonLookup.UserLookupTableList;
import capi.model.userAccountManagement.StaffProfileTableList;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import capi.service.userAccountManagement.ActingService;
import capi.service.userAccountManagement.PasswordHistoryService;
import capi.service.userAccountManagement.PasswordPolicyService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("UserService")
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private ActingService actingService;
	
	@Autowired
	private SurveyMonthService surveyMonthService;
	
       @Autowired
       private AppConfigService appConfig;

	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	@Autowired
	private PasswordHistoryService passwordHistoryService;
	
	@Autowired
	private UserLockedTimeLogService userLockedTimeLogServive;
	
	@Autowired
	private NotificationDao notificationDao;
	
	/**
	 * Get system active user
	 * @param username
	 * @param password
	 * @return user
	 */
	public User getActiveUser(String username, String password){		
		return getActiveUser(username, password, false);
	}
	
	/**
	 * Get system active user
	 * @param username
	 * @param password
	 * @return user
	 */
	public User getActiveUser(String username, String password, boolean isHashed){		
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);		
		String hashPass = !isHashed? encoder.encodePassword(password, null): password;
		return userDao.findActiveUserByUsernamePassword(username, hashPass);		
	}
	
	/**
	 * get user by user id
	 * @param id
	 * @return user
	 */
	public User getUserById(Integer id){
		return userDao.findById(id);
	}
	
	/**
	 * get users by user ids
	 * @param ids
	 * @return users
	 */
	public List<User> getUsersByIds(List<Integer> ids) {
		return userDao.getUsersByIds(ids);
	}
	
	/**
	 * update related info in user for successfully login
	 * @param username
	 */
	public void successfullLogin(String username){
		User user = userDao.findActiveUserByUsername(username);;
		user.setAttemptNumber(0);
		user.setLastLoginTime(new Date());
		userDao.save(user);
		userDao.flush();
	}
	
	/**
	 * update related info in user for successfully login in mobile
	 * @param username
	 * @param deviceToken push notification token
	 */
	public void successfullLogin(String username, String deviceToken){
		User user = userDao.findActiveUserByUsername(username);
		user.setAttemptNumber(0);
		user.setLastLoginTime(new Date());
		user.setDeviceKey(deviceToken);
		userDao.save(user);
		userDao.flush();
	}
	
	/**
	 * get user by username
	 * @param username
	 * @return user
	 */
	public User getUser(String username){
		User user = userDao.findUserByUsername(username);
		return user;
	}
	
	/**
	 * update user
	 * @param user
	 */
	public void saveUser(User user){
		userDao.save(user);
		userDao.flush();
	}
	
	/**
	 * get all users in system
	 * @return users
	 */
	public List<User> getAllUser(){
		return userDao.findAll();
	}
	
	public void genUserReport(OutputStream outStream) throws Exception {		
		List<capi.model.UserReport> reports =  userDao.genUserReport();
		JRDataSource dataSource = new JRBeanCollectionDataSource(reports);
		Resource resouce = new ClassPathResource("report/UserReport.jrxml");
		InputStream inStream = resouce.getInputStream();
		JasperReport report = JasperCompileManager.compileReport(inStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(report, null, dataSource);
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	}
	
	public UserAccessModel gatherUserRequiredInfo(User user){
		UserAccessModel model = new UserAccessModel();
		BeanUtils.copyProperties(user, model);
		
		ListOrderedSet<SystemFunction> uniqueList = new ListOrderedSet<SystemFunction>();   		
		
		int authorityLevel = 0;
		int orgAuthorityLevel = 0;
		
		Set<Role> orgRoles = user.getRoles();
		
		List<Role> roles = roleDao.getUserRoleWithActing(user.getUserId());
		
		List<User> actedUsers = userDao.getActedUsers(user.getUserId());
		
		for (Role role : orgRoles){
			orgAuthorityLevel = (orgAuthorityLevel | role.getAuthorityLevel());
		}
		
		for (Role role : roles){
			authorityLevel = (authorityLevel | role.getAuthorityLevel());
			uniqueList.addAll(role.getFunctions());
		}
		
		for (User acted : actedUsers){
			model.getActedUsers().add(acted.getUserId());
		}
		
		for (SystemFunction func : uniqueList){
			model.getFunctionList().add(func.getCode());
		}
		
		model.setOrgAuthorityLevel(orgAuthorityLevel);
		model.setAuthorityLevel(authorityLevel);
		
		return model;
	}
	
	public List<Integer> getActiveUserIdsWithAuthorityLevel(int authorityLevel, Integer[] staffIds, Integer limit){
		return userDao.getActiveUserIdsWithAuthorityLevel(authorityLevel, staffIds, limit);
	}
	
	public List<User> getActiveUsersWithAuthorityLevel(int authorityLevel){
		return userDao.getActiveUsersWithAuthorityLevel(authorityLevel, null);
	}
	
	public List<User> getActiveUsersWithAuthorityLevel(int authorityLevel, Integer[] staffIds){
		return userDao.getActiveUsersWithAuthorityLevel(authorityLevel, staffIds);
	}
	
	public UserLookupTableList getUserByIdForStaffCalendar(Integer id){
		return userDao.getUserByIdForStaffCalendar(id);
	}
	
	public List<UserLookupTableList> getActiveUsersWithAuthorityLevelForStaffCalendar(int authorityLevel){
		return userDao.getActiveUsersWithAuthorityLevelForStaffCalendar(authorityLevel, null);
	}
	
	public List<UserLookupTableList> getActiveUsersWithAuthorityLevelForStaffCalendar(int authorityLevel, Integer[] staffIds){
		return userDao.getActiveUsersWithAuthorityLevelForStaffCalendar(authorityLevel, staffIds);
	}
	
	/**
	 * Get Officer select format
	 */
	public Select2ResponseModel queryOfficerSelect2(Select2RequestModel queryModel, Integer userId) {
		queryModel.setRecordsPerPage(10);		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		List<Integer> supervisorIds = new ArrayList<Integer>();
		supervisorIds.add(userId);
		supervisorIds.addAll(detail.getActedUsers());
		
		List<StaffProfileTableList> entities = null;
		long recordsTotal = 0;
		if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2){
			// field head and section head should view all officer
			entities = userDao.searchOfficer(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
					null, null);
			recordsTotal = userDao.countSearchOfficer(queryModel.getTerm(), null,null);
		}		
		else if ((detail.getAuthorityLevel() & 16) == 16){
			// field officer should view himself and if he has acted supervisor, he should view the team members
			 entities = userDao.searchOfficer(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
						supervisorIds.toArray(new Integer[supervisorIds.size()]), userId);
			 recordsTotal = userDao.countSearchOfficer(queryModel.getTerm(), supervisorIds.toArray(new Integer[supervisorIds.size()]),userId);
		}
		else{
			// view the team member only (supervisor will fall into this condition)
			 entities = userDao.searchOfficer(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
						supervisorIds.toArray(new Integer[supervisorIds.size()]), null);
			 recordsTotal = userDao.countSearchOfficer(queryModel.getTerm(), supervisorIds.toArray(new Integer[supervisorIds.size()]),null);
		}
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			if(!StringUtils.isEmpty(entity.getChineseName())) {
				item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
			} else {
				item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
			}
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Officer select format
	 */
	public Select2ResponseModel queryOfficerSupervisorSelect2(Select2RequestModel queryModel, Integer userId) {
		queryModel.setRecordsPerPage(10);		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		List<Integer> supervisorIds = new ArrayList<Integer>();
		supervisorIds.add(userId);
		supervisorIds.addAll(detail.getActedUsers());
		
		List<StaffProfileTableList> entities = null;
		long recordsTotal = 0;
		if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2){
			// field head and section head should view all officer
			entities = userDao.searchOfficerSupervisor(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
					null, null);
			recordsTotal = userDao.countSearchOfficerSupervisor(queryModel.getTerm(), null,null);
		}		
		else{
			// view the team member only (supervisor will fall into this condition)
			 entities = userDao.searchOfficerSupervisor(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
						supervisorIds.toArray(new Integer[supervisorIds.size()]), userId);
			 recordsTotal = userDao.countSearchOfficerSupervisor(queryModel.getTerm(), supervisorIds.toArray(new Integer[supervisorIds.size()]),userId);
		}
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			if(!StringUtils.isEmpty(entity.getChineseName())) {
				item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
			} else {
				item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
			}
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public Select2ResponseModel queryOfficerSelect2(Select2RequestModel queryModel){
		return queryOfficerSelect2(queryModel, new ArrayList<String>());
	}
	
	public Select2ResponseModel queryOfficerSelect2(Select2RequestModel queryModel, List<String> teamName) {
		queryModel.setRecordsPerPage(10);		
		
		List<StaffProfileTableList> entities = null;
		
		// view the team member only (supervisor will fall into this condition)
		 entities = userDao.searchOfficer(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
				 teamName);
		
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = userDao.countSearchOfficer(queryModel.getTerm(), teamName);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			if(!StringUtils.isEmpty(entity.getChineseName())) {
				item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
			} else {
				item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
			}
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	
	/**
	 * Get Officer select format
	 */
	public Select2ResponseModel queryTeamSelect2(Select2RequestModel queryModel) {
		return queryTeamSelect2(queryModel, true);
	}

	/**
	 * Get Officer select format
	 */
	public Select2ResponseModel queryTeamSelect2(Select2RequestModel queryModel, boolean checkSubordinate) {
		queryModel.setRecordsPerPage(10);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<Integer> subordinates = new ArrayList<Integer>();
		
		if (checkSubordinate){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
			Integer authorityLevel = detail.getAuthorityLevel();
			int excludedRight = (1|2|256); // section head, field head, business data administrator
			if ((authorityLevel & excludedRight) == 0 && (authorityLevel & 4) == 4){
				User user = userDao.findById(detail.getUserId());
				if (user.getSubordinates() != null && user.getSubordinates().size() > 0){
					for (User sub : user.getSubordinates()){
						subordinates.add(sub.getUserId());
					}
				}
			}	
		}
		
		List<String> entities = null;
		entities = userDao.searchTeam(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), subordinates);
		
		long recordsTotal = userDao.countSearchTeam(queryModel.getTerm(), subordinates);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String team : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(team);
			item.setText(team);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Supervisor select format
	 */
	public Select2ResponseModel querySupervisorSelect2(Select2RequestModel queryModel, Integer userId) {
		queryModel.setRecordsPerPage(10);
		
		
		List<StaffProfileTableList> entities = userDao.searchSupervisor(userId, queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = userDao.countSearchSupervisor(userId, queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	

	/**
	 * Get Supervisor select format
	 */
	public Select2ResponseModel querySupervisorWithHeadUserSelect2(Select2RequestModel queryModel, Integer userId) {
		queryModel.setRecordsPerPage(10);
		
		
		List<StaffProfileTableList> entities = userDao.searchSupervisorAndHeadUser(userId, queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = userDao.countSearchSupervisorAndHeadUser(userId, queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Field Head Section head select format
	 */
	public Select2ResponseModel queryFieldHeadSectionSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		
		List<StaffProfileTableList> entities = userDao.searchFieldHeadSectionHead(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = userDao.countSearchFieldHeadSectionHead(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	
	
	/**
	 * Get officer ids by supervisors
	 * @return
	 */
	public List<Integer> getOfficerIdsBySupervisors(List<Integer> supervisorIds) {
		if (supervisorIds.size() == 0) {
			return new ArrayList<Integer>();
		}
		return userDao.getOfficerIdsBySupervisors(supervisorIds);
	}

	/**
	 * Query Officer For Imported Select2
	 */
	public Select2ResponseModel queryOfficerForImportedSelect2(Select2RequestModel queryModel, int authorityLevel) {
		queryModel.setRecordsPerPage(10);		
		
		List<StaffProfileTableList> entities = userDao.searchActiveUsersWithAuthorityLevel(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
				authorityLevel);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = userDao.countSearchActiveUsersWithAuthorityLevel(queryModel.getTerm(), authorityLevel);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
			items.add(item);
		}
		responseModel.setResults(items);
		return responseModel;
	}
	
	public List<UserSyncData> getUpdateUser(Date lastSyncTime){
		return userDao.getUpdateUser(lastSyncTime);
	}
	
	public List<UserRoleSyncData> getUpdateUserRole(Date lastSyncTime){
		return userDao.getUpdateUserRole(lastSyncTime);
	}
	
	public Set<Integer> getLoginUserSubordinatesAndSelfWithActing() {
		Set<Integer> userIds = new HashSet<Integer>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		List<Integer> actedUserIds = detail.getActedUsers();
		actedUserIds.add(detail.getUserId());
		List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(actedUserIds);
		
		userIds.add(detail.getUserId());
		userIds.addAll(actedUserSubordinateIds);
		
		return userIds;
	}
	
	public Set<Integer> getLoginUserSubSubordinatesAndSelfWithActing() {
		Set<Integer> userIds = new HashSet<Integer>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails details = (CapiWebAuthenticationDetails) auth.getDetails();
		userIds.add(details.getUserId());
		
		List<Integer> fieldSupervisorIds = getActiveUserIdsWithAuthorityLevel(
				SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR, null, null);
		if (fieldSupervisorIds != null) {
			userIds.addAll(fieldSupervisorIds);
		}

		List<Integer> fieldOfficerIds = getActiveUserIdsWithAuthorityLevel(
				SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, null, null);
		if (fieldOfficerIds != null) {
			userIds.addAll(fieldOfficerIds);
		}
			
		return userIds;
		
//		Set<Integer> userIds = new HashSet<Integer>();
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
//		
//		List<Integer> actedUserIds = detail.getActedUsers();
//		actedUserIds.add(detail.getUserId());
//		List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(actedUserIds);
//		List<Integer> actedUserSubSubordinateIds = userDao.getSubordinatesByUserId(actedUserSubordinateIds);
//		
//		userIds.add(detail.getUserId());
//		userIds.addAll(actedUserSubSubordinateIds);
//		
//		return userIds;
	}
	
	public Select2ResponseModel queryOfficerSelect2ByTeamId(Select2RequestModel queryModel, String[] teams) {
		return queryOfficerSelect2ByTeamId(queryModel, teams, true);
	}
	
	/**
	 * Get Officer select format by team id
	 */
	public Select2ResponseModel queryOfficerSelect2ByTeamId(Select2RequestModel queryModel, String[] teams, boolean checkSubordinate) {
		queryModel.setRecordsPerPage(10);		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<User> entities = null;
		List<Integer> subordinates = new ArrayList<Integer>();
		if (checkSubordinate){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
			Integer authorityLevel = detail.getAuthorityLevel();
			int excludedRight = (1|2|256); // section head, field head, business data administrator
			if ((authorityLevel & excludedRight) == 0 && (authorityLevel & 4) == 4){
				User user = userDao.findById(detail.getUserId());
				if (user.getSubordinates() != null && user.getSubordinates().size() > 0){
					for (User sub : user.getSubordinates()){
						subordinates.add(sub.getUserId());
					}
				}
			}		
		}
		
		entities = userDao.searchOfficerByTeam(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(),teams, subordinates);
		
		long recordsTotal = userDao.countSearchOfficerByTeam(queryModel.getTerm(), teams, subordinates);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (User entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			//2018-01-08 cheung_cheng The officer name shown in the filter should be consistent with the below "Description" (both shown in English name)
			//use in RF9031 and RF9049
			item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
//			if(!StringUtils.isEmpty(entity.getChineseName())) {
//				item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
//			} else {
//				item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
//			}
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public Select2ResponseModel queryOfficerSelect2ByTeamWithoutIndoor(Select2RequestModel queryModel, String[] teams, Integer authorityLv, Integer staffType) {
		return queryOfficerSelect2ByTeamWithoutIndoor(queryModel, teams, true, authorityLv, staffType);
	}
	public Select2ResponseModel queryOfficerSelect2ByTeamWithoutIndoor(Select2RequestModel queryModel, String[] teams, Integer authorityLv, 
			Integer staffType, Date startDate, Date endDate) {
		return queryOfficerSelect2ByTeamWithoutIndoor(queryModel, teams, true, authorityLv, staffType, startDate, endDate);
	}
	
	/**
	 * Get Officer select format by team id without indoor
	 */
	public Select2ResponseModel queryOfficerSelect2ByTeamWithoutIndoor(Select2RequestModel queryModel, String[] teams, boolean checkSubordinate, Integer authorityLv, Integer staffType) {
		queryModel.setRecordsPerPage(10);		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<User> entities = null;
		List<Integer> subordinates = new ArrayList<Integer>();
		if (checkSubordinate){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
			Integer authorityLevel = detail.getAuthorityLevel();
			int excludedRight = (1|2|256); // section head, field head, business data administrator
			if ((authorityLevel & excludedRight) == 0 && (authorityLevel & 4) == 4){
				User user = userDao.findById(detail.getUserId());
				if (user.getSubordinates() != null && user.getSubordinates().size() > 0){
					for (User sub : user.getSubordinates()){
						subordinates.add(sub.getUserId());
					}
				}
			}		
		}
		
		entities = userDao.searchOfficerWithoutIndoor(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(),teams, subordinates, authorityLv, staffType);
		
		long recordsTotal = userDao.countSearchOfficerByTeam(queryModel.getTerm(), teams, subordinates);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (User entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			//2018-01-08 cheung_cheng The officer name shown in the filter should be consistent with the below "Description" (both shown in English name)
			//use in RF9031 and RF9049
			item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
//			if(!StringUtils.isEmpty(entity.getChineseName())) {
//				item.setText(entity.getStaffCode() + " - " + entity.getChineseName());
//			} else {
//				item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
//			}
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public Select2ResponseModel queryOfficerSelect2ByTeamWithoutIndoor(Select2RequestModel queryModel, String[] teams,
			boolean checkSubordinate, Integer authorityLv, Integer staffType, Date startDate, Date endDate) {
		queryModel.setRecordsPerPage(10);		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		List<User> entities = null;
		List<Integer> subordinates = new ArrayList<Integer>();
		if (checkSubordinate){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
			Integer authorityLevel = detail.getAuthorityLevel();
			int excludedRight = (1|2|256); // section head, field head, business data administrator
			if ((authorityLevel & excludedRight) == 0 && (authorityLevel & 4) == 4){
				User user = userDao.findById(detail.getUserId());
				if (user.getSubordinates() != null && user.getSubordinates().size() > 0){
					for (User sub : user.getSubordinates()){
						subordinates.add(sub.getUserId());
					}
				}
			}		
		}
		
		entities = userDao.searchOfficerWithoutIndoor(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(),teams, 
				subordinates, authorityLv, staffType, startDate, endDate);
		
		long recordsTotal = userDao.countSearchOfficerByTeam(queryModel.getTerm(), teams, subordinates);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (User entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			//use in RF9031 and RF9049
			item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get key value by ids
	 */
	public List<KeyValueModel> getKeyValueByIds(Integer[] ids) {
		List<User> entities = userDao.getUsersByIds(Arrays.asList(ids));
		if (entities == null)
			return null;
		
		List<KeyValueModel> result = new ArrayList<KeyValueModel>();
		for (User entity : entities) {
			KeyValueModel model = new KeyValueModel();
			model.setKey(entity.getId().toString());
			model.setValue(entity.getStaffCode() + " - " + entity.getEnglishName());
			result.add(model);
		}
		return result;
	}
	
	/**
	 * Get key value by ids (StaffCode - Chinese Name)
	 */
	public List<KeyValueModel> getStaffCodeChineseNameByIds(Integer[] ids) {
		List<User> entities = userDao.getUsersByIds(Arrays.asList(ids));
		if (entities == null)
			return null;
		
		List<KeyValueModel> result = new ArrayList<KeyValueModel>();
		for (User entity : entities) {
			KeyValueModel model = new KeyValueModel();
			model.setKey(entity.getId().toString());
			model.setValue(entity.getStaffCode() + " - " + entity.getChineseName());
			result.add(model);
		}
		return result;
	}
	
	public List<Integer> getUserIdsWithSameTeam(String team) {
		// if team equals null -> get all users without team
		List<Integer> ids = userDao.getTeammateUserIds(team);
//		List<String> userIds = new ArrayList<String> ();
//		for(Integer id : ids) {
//			userIds.add(String.valueOf(id));
//		}
		return ids;
	}
	
	public List<Integer> getUserIdsWithSameTeams(List<String> teams) {
		return userDao.getTeammateUserIdsByTeams(teams);
	}
	
	public User getSupervisorByUserId(Integer userId) {
		return userDao.getSupervisorByUserId(userId);
	}
	
	/**
	 * get users by user ids
	 * @param ids
	 * @return users
	 */
	public List<User> getActiveUsersByIds(List<Integer> ids) {
		return userDao.getActiveUserByIds(ids);
	}
	
	public Select2ResponseModel queryReportOfficerSelect2(Select2RequestModel queryModel
			, Integer authorityLevel, Date startDate, Date endDate, Date refMonth
			, Integer[] userIds, Integer staffType) {
		queryModel.setRecordsPerPage(10);		
		
		List<StaffProfileTableList> entities = null;
		
		Date refMonthStart = null;
		Date refMonthEnd = null;
		
		if(refMonth != null){
			SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(refMonth);
			refMonthStart = surveyMonth.getStartDate();
			refMonthEnd = surveyMonth.getEndDate();
		}
		
		 entities = userDao.searchReportOfficer2(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
				 authorityLevel, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = userDao.countSearchReportOfficer2(queryModel.getTerm(), authorityLevel, startDate
				, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (StaffProfileTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getUserId()));
			item.setText(entity.getStaffCode() + " - " + entity.getEnglishName());
			
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	//TODO CR6 REQ001
	//TODO CR6 REQ003
	public void resetStatusAndAttemptByDuration(String username){
		System.out.println("=======================UserService=========setUserStatusByLockoutDuration");
		User user = userDao.findUserByUsername(username);
		if (user == null){
			return;
		}
		if (user.getStatus().equals("Locked") && user.getAttemptNumber() > 0) {
			UserLockedTimeLog log = userLockedTimeLogServive.getUserLockedTimeLogByUserName(username);
			Date userLockedTime = log.getLockedDate();
			if (log != null) {
				long lockedInterval = getDateDiff(userLockedTime, new Date(), TimeUnit.MINUTES);
				Integer resetAttemptDuration=  passwordPolicyService.getResetAttemtDurationInteger();
				if (lockedInterval >= resetAttemptDuration) {
					user.setAttemptNumber(0);
				}
				
				//long lockedTime = getDateDiff(userLockedTime, new Date(), TimeUnit.MINUTES);
				Integer lockedDuration =  passwordPolicyService.getLockoutDurationInteger();
				if (lockedInterval >= lockedDuration) {
					user.setStatus("Active");
				}
				
				userDao.save(user);
				userDao.flush();
			}
		}
	}
	
	//TODO CR6 REQ005
	public boolean isPasswordExpired(User user) {
		return passwordHistoryService.isPasswordExpired(user);
	}
	 
	//TODO CR6 REQ005
	public void sendChangePasswordReminder(User user) {
		
		System.out.println("===================UserService====sendChangePasswordReminder");
		System.out.println("passwordHistoryService.getPasswordExpiredDaysLeft(user)");
		
		
		Date expiredDate = passwordHistoryService.getPasswordExpiredDate(user);
		long dayLeft  = getDateDiff(new Date(), expiredDate, TimeUnit.DAYS);
		System.out.println("dayLeft:"+dayLeft);
		Integer notificationDate=  passwordPolicyService.getNotificationDateInteger();
		
		if (dayLeft < notificationDate) {
			SimpleDateFormat formatter  = new SimpleDateFormat("d-M-y");
			String expiredDateString = formatter.format(expiredDate).toString();
			
		
			Notification notification = new Notification();
			notification.setSubject("Change Password Reminder");
			notification.setContent(String.format("Your user password will be expired on %s. You need to change your password before the expiry date.", expiredDateString));
			//notification.setContent(String.format("You need to change your password within %s day", dayLeft));
			notification.setUser(user);
			notificationDao.save(notification);
			notificationDao.flush();
		}
	}
	/**
	 * Get a difference between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the difference
	 * @return the difference value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

}
