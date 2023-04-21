package capi.service.lookup;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.OutletTypeDao;
import capi.dal.PECheckTaskDao;
import capi.dal.SurveyMonthDao;
import capi.entity.SurveyMonth;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.PECheckAssignmentLookupTableList;
import capi.service.BaseService;

@Service("PECheckAssignmentLookupService")
public class PECheckAssignmentLookupService extends BaseService {
	
	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private OutletTypeDao outletTypeDao;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<PECheckAssignmentLookupTableList> getLookupTableList(DatatableRequestModel model, Integer userId, Integer surveyMonthId,
			String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, Integer[] ignoreAssignmentIds){

		//Order order = this.getOrder(model, "", "firm", "batchCode", "collectionDate", "district", "tpu", "address", "noOfQuotation");
		Order order = this.getOrder(model, "", "referenceNo", "firm", "district", "tpu", "address", "noOfQuotation", "", "firmStatus", "tel", "lastPECheckMonth");
		
		String search = model.getSearch().get("value");
		
		SurveyMonth surveyMonth = surveyMonthDao.findById(surveyMonthId);
		
		List<PECheckAssignmentLookupTableList> results = peCheckTaskDao.getPECheckAssignmentLookupTableList(search, model.getStart(), model.getLength(), order,
				surveyMonthId, userId, outletTypeId, productCategoryId, districtId, tpuId, ignoreAssignmentIds, surveyMonth.getReferenceMonth());
		
		for(PECheckAssignmentLookupTableList result : results){
			List<String> str = outletTypeDao.getShortCodeByAssignmentId(result.getAssignmentId());
			result.setOutletType(StringUtils.join(str, ','));
		}
		
		DatatableResponseModel<PECheckAssignmentLookupTableList> response = new DatatableResponseModel<PECheckAssignmentLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(results);
		Integer recordTotal = peCheckTaskDao.countPECheckAssignmentLookupTableList("", userId, surveyMonthId, outletTypeId, productCategoryId, districtId, tpuId, ignoreAssignmentIds, surveyMonth.getReferenceMonth());
		response.setRecordsTotal(recordTotal);
		Integer recordFiltered = peCheckTaskDao.countPECheckAssignmentLookupTableList(search, userId, surveyMonthId, outletTypeId, productCategoryId, districtId, tpuId, ignoreAssignmentIds, surveyMonth.getReferenceMonth());
		response.setRecordsFiltered(recordFiltered);
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, Integer userId, Integer surveyMonthId
			, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, Integer[] ignoreAssignmentIds){

		return peCheckTaskDao.getPEAssignmentLookupTableSelectAll(search, userId, surveyMonthId
				, outletTypeId, productCategoryId, districtId, tpuId, ignoreAssignmentIds);
	}


}
