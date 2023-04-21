package capi.service.lookup;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.OutletDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.masterMaintenance.OutletTableList;
import capi.service.BaseService;

@Service("OutletLookupService")
public class OutletLookupService extends BaseService {
	
	@Autowired
	private OutletDao outletDao;

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<OutletTableList> getLookupTableList(DatatableRequestModel model,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet,
			String name, String tel){

		Order order = this.getOrder(model, "", "firmCode", "name", "district", "tpu", "activeOutlet", "quotationCount", "streetAddress", "detailAddress");
		
		String search = model.getSearch().get("value");
		
		List<OutletTableList> result = outletDao.getTableList(search, model.getStart(), model.getLength(), order,
				outletTypeId, districtId, tpuId, activeOutlet, true,
				name, tel);
		
		DatatableResponseModel<OutletTableList> response = new DatatableResponseModel<OutletTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = outletDao.countTableList("", null, null, null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = outletDao.countTableList(search, outletTypeId, districtId, tpuId, activeOutlet, true, name, tel);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet){
		
		return outletDao.getLookupTableSelectAll(search, outletTypeId, districtId, tpuId, activeOutlet);
	}
	
	/**
	 * Get table list by ids
	 */
	public List<OutletTableList> getTableListByIds(Integer[] ids){
		return outletDao.getTableListByIds(ids);
	}
}
