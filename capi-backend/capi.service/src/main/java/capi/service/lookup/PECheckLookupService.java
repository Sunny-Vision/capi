package capi.service.lookup;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.PECertaintyCaseLookupTableList;
import capi.model.commonLookup.PECheckLookupTableList;
import capi.service.BaseService;

@Service("PECheckLookupService")
public class PECheckLookupService extends BaseService {

	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private PECheckFormDao peCheckFormDao;

	/** 
	 * datatable query
	 */
	public DatatableResponseModel<PECheckLookupTableList> getLookupTableList(DatatableRequestModel model, String[] outletTypeId, 
			Integer[] districtId, Integer[] tpuId, Integer[] excludedPEFormIds, Integer userId){

		Order order = this.getOrder(model, "", "firm", "district", "tpu", "deadline", "address", "noOfQuotation", "convenientTime", "outletRemark", "fieldOfficerCode", "chineseName", "englishName");
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		if (userId == null){
			userId = detail.getUserId();
		}
		List<Integer> relatedUserIds = new ArrayList<Integer>();
		relatedUserIds.add(userId);		
		
		List<PECheckLookupTableList> result = peCheckFormDao.getPECheckLookupTableList(search, model.getStart(), model.getLength(), order,
				 outletTypeId, districtId, tpuId, excludedPEFormIds, relatedUserIds);
		
		DatatableResponseModel<PECheckLookupTableList> response = new DatatableResponseModel<PECheckLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		
		Long recordTotal = peCheckFormDao.countPECheckLookupTableList("", outletTypeId, districtId, tpuId, excludedPEFormIds, relatedUserIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = peCheckFormDao.countPECheckLookupTableList(search, outletTypeId, districtId, tpuId, excludedPEFormIds, relatedUserIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, Integer userId, Integer surveyMonthId
			, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, Integer[] ignoredAssignmentIds ){

		return peCheckTaskDao.getPEAssignmentLookupTableSelectAll(search, userId, surveyMonthId
				, outletTypeId, productCategoryId, districtId, tpuId, ignoredAssignmentIds );
	}

	/**
	 * Get table list by ids
	 */
	public List<PECertaintyCaseLookupTableList> getTableListByIds(Integer surveyMonthId, Integer[] peCheckTaskIds){
		return peCheckTaskDao.getPECertaintyCaseListByIds(surveyMonthId, peCheckTaskIds);
	}

}
