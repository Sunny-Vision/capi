package capi.service.lookup;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.RoleDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.RoleLookupTableList;
import capi.service.BaseService;

@Service("RoleLookupService")
public class RoleLookupService extends BaseService {
	
	@Autowired
	private RoleDao roleDao;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<RoleLookupTableList> getLookupTableList(DatatableRequestModel model){

		Order order = this.getOrder(model, "id", "name", "description");
		
		String search = model.getSearch().get("value");
		
		List<RoleLookupTableList> result = roleDao.getLookupTableList(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<RoleLookupTableList> response = new DatatableResponseModel<RoleLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = roleDao.countLookupTableList("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = roleDao.countLookupTableList(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search){

		return roleDao.getLookupTableSelectAll(search);
	}

}
