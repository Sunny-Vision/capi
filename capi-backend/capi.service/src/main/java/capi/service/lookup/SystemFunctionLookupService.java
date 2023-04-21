package capi.service.lookup;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.SystemFunctionDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.SystemFunctionLookupTableList;
import capi.service.BaseService;

@Service("SystemFunctionLookupService")
public class SystemFunctionLookupService extends BaseService {

	@Autowired
	private SystemFunctionDao systemFunctionDao;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<SystemFunctionLookupTableList> getLookupTableList(DatatableRequestModel model, Boolean isMobile){

		Order order = this.getOrder(model, "id", "code", "description");
		
		String search = model.getSearch().get("value");
		
		List<SystemFunctionLookupTableList> result = systemFunctionDao.getLookupTableList(search, model.getStart(), model.getLength(), order, isMobile);
		
		DatatableResponseModel<SystemFunctionLookupTableList> response = new DatatableResponseModel<SystemFunctionLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = systemFunctionDao.countLookupTableList("", isMobile);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = systemFunctionDao.countLookupTableList(search, isMobile);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, Boolean isMobile){
		return systemFunctionDao.getLookupTableSelectAll(search, isMobile);
	}

	/**
	 * Get table list by ids
	 */
	public List<SystemFunctionLookupTableList> getTableListByIds(Boolean isMobile, Integer[] ids){
		return systemFunctionDao.getTableListByIds(isMobile, ids);
	}

}
