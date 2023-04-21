package capi.service.lookup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.UserDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.UserLookupTableList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("ReportUserLookupService")
public class ReportUserLookupService extends BaseService{
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	/** 
	 * datatable report query
	 */
	public DatatableResponseModel<UserLookupTableList> getReportLookupTableList(DatatableRequestModel model,
			Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType){

		Order order = this.getOrder(model,"", "staffCode", "team", "englishName", "chineseName", "destination");
				
		String search = model.getSearch().get("value");
		
		List<UserLookupTableList> returnList = new ArrayList<UserLookupTableList>();
		
		returnList = userDao.getReportLookupTableList(search, model.getStart(), model.getLength(), order, authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		
		DatatableResponseModel<UserLookupTableList> response = new DatatableResponseModel<UserLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		
		Long recordTotal = userDao.countReportLookupTableList("", authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = userDao.countReportLookupTableList(search, authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		response.setRecordsFiltered(recordFiltered.intValue());			
		
				
		return response;
	}
	
	public DatatableResponseModel<UserLookupTableList> getReportLookupTableList2(DatatableRequestModel model,
			Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType){

		Order order = this.getOrder(model,"", "staffCode", "team", "englishName", "chineseName", "destination");
				
		String search = model.getSearch().get("value");
		
		List<UserLookupTableList> returnList = new ArrayList<UserLookupTableList>();
		
		returnList = userDao.getReportLookupTableList2(search, model.getStart(), model.getLength(), order, authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		
		DatatableResponseModel<UserLookupTableList> response = new DatatableResponseModel<UserLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		
		Long recordTotal = userDao.countReportLookupTableList2("", authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = userDao.countReportLookupTableList2(search, authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		response.setRecordsFiltered(recordFiltered.intValue());			
		
				
		return response;
	}
	
	/** 
	 * datatable select all
	 */
	public List<Integer> getReportLookupTableSelectAll(String search, Integer authorityLevel
			, String team, Integer[] excludedIds, Date startDate, Date endDate, Date refMonthStart
			, Date refMonthEnd, Integer[] userIds, Integer staffType){
		
		return userDao.getReportLookupTableSelectAll(search, authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		
	}
	
	public List<Integer> getReportLookupTableSelectAll2(String search, Integer authorityLevel
			, String team, Integer[] excludedIds, Date startDate, Date endDate, Date refMonthStart
			, Date refMonthEnd, Integer[] userIds, Integer staffType){
		
		return userDao.getReportLookupTableSelectAll2(search, authorityLevel
				, team, excludedIds, startDate, endDate, refMonthStart, refMonthEnd, userIds, staffType);
		
	}
}
