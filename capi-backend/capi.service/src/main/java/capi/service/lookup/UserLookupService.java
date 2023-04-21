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

@Service("UserLookupService")
public class UserLookupService extends BaseService {
	
	@Autowired
	private UserDao userDao;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<UserLookupTableList> getLookupTableList(DatatableRequestModel model, Integer authorityLevel, 
			Boolean teamOnly, Integer selfAuthorityLevel, String username, Boolean withSelf){

		Order order = this.getOrder(model,"", "staffCode", "team", "englishName", "chineseName", "destination");
				
		String search = model.getSearch().get("value");
		
		List<UserLookupTableList> returnList = new ArrayList<UserLookupTableList>();
				
		int isHeadUser = SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD |  SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD;
		
		if (teamOnly != null && teamOnly && (selfAuthorityLevel&isHeadUser) == 0){
			returnList = userDao.getLookupTableListTeamOnly(search, model.getStart(), model.getLength(), order, authorityLevel, username, withSelf);
		}
		else{
			returnList = userDao.getLookupTableList(search, model.getStart(), model.getLength(), order, authorityLevel);
		}
		
		DatatableResponseModel<UserLookupTableList> response = new DatatableResponseModel<UserLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		if (teamOnly != null && teamOnly && (selfAuthorityLevel&isHeadUser) == 0){
			Long recordTotal = userDao.countLookupTableListTeamOnly("", authorityLevel, username, withSelf);
			response.setRecordsTotal(recordTotal.intValue());
			Long recordFiltered = userDao.countLookupTableListTeamOnly(search, authorityLevel, username, withSelf);
			response.setRecordsFiltered(recordFiltered.intValue());			
		}
		else{
			Long recordTotal = userDao.countLookupTableList("", authorityLevel);
			response.setRecordsTotal(recordTotal.intValue());
			Long recordFiltered = userDao.countLookupTableList(search, authorityLevel);
			response.setRecordsFiltered(recordFiltered.intValue());			
		}
				
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, Integer authorityLevel, Boolean teamOnly, String username, Boolean withSelf, Integer selfAuthorityLevel){
		int isHeadUser = SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD |  SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD;
		if (teamOnly != null && teamOnly && (selfAuthorityLevel&isHeadUser) > 0){
			return userDao.getLookupTableTeamOnlySelectAll(search, authorityLevel, username, withSelf);
		}
		else{
			return userDao.getLookupTableSelectAll(search, authorityLevel);
		}
	}
}
