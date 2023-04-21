package capi.service.lookup;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.UserDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.commonLookup.UserLookupTableList;
import capi.service.BaseService;

@Service("StaffCalendarUserLookupService")
public class StaffCalendarUserLookupService extends BaseService {
	
	@Autowired
	private UserDao userDao;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<UserLookupTableList> getStaffCalendarLookupTableList(DatatableRequestModel model, Integer authorityLevel, 
			Boolean teamOnly, Integer selfAuthorityLevel, String username, Boolean withSelf, String ghs, String team){

		Order order = this.getOrder(model,"", "staffCode", "team", "englishName", "chineseName", "destination", "district", "isGHS");
				
		String search = model.getSearch().get("value");
		
		List<UserLookupTableList> returnList = new ArrayList<UserLookupTableList>();
				
		int isHeadUser = SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD |  SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD;
		
		if (teamOnly != null && teamOnly && (selfAuthorityLevel&isHeadUser) == 0){
			returnList = userDao.getStaffCalendarLookupTableListTeamOnly(search, model.getStart(), model.getLength(), order, authorityLevel, username, withSelf, ghs, team);
		}
		else{
			returnList = userDao.getStaffCalendarLookupTableList(search, model.getStart(), model.getLength(), order, authorityLevel, ghs, team);
		}
		
		DatatableResponseModel<UserLookupTableList> response = new DatatableResponseModel<UserLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		if (teamOnly != null && teamOnly && (selfAuthorityLevel&isHeadUser) == 0){
			Long recordTotal = userDao.countStaffCalendarLookupTableListTeamOnly("", authorityLevel, username, withSelf, "", "");
			response.setRecordsTotal(recordTotal.intValue());
			Long recordFiltered = userDao.countStaffCalendarLookupTableListTeamOnly(search, authorityLevel, username, withSelf, ghs, team);
			response.setRecordsFiltered(recordFiltered.intValue());			
		}
		else{
			Long recordTotal = userDao.countStaffCalendarLookupTableList("", authorityLevel, "", "");
			response.setRecordsTotal(recordTotal.intValue());
			Long recordFiltered = userDao.countStaffCalendarLookupTableList(search, authorityLevel, ghs, team);
			response.setRecordsFiltered(recordFiltered.intValue());			
		}
				
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getStaffCalendarLookupTableSelectAll(String search, Integer authorityLevel, Boolean teamOnly, String username, 
			Boolean withSelf, Integer selfAuthorityLevel, String ghs, String team){
		int isHeadUser = SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD |  SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD;
		if (teamOnly != null && teamOnly && (selfAuthorityLevel&isHeadUser) > 0){
			return userDao.getStaffCalendarLookupTableTeamOnlySelectAll(search, authorityLevel, username, withSelf, ghs, team);
		}
		else{
			return userDao.getStaffCalendarLookupTableSelectAll(search, authorityLevel, ghs, team);
		}
	}
}
