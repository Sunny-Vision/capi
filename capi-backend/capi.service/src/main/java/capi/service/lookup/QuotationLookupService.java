package capi.service.lookup;

import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.entity.Purpose;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.QuotationLookupTableList;
import capi.service.BaseService;

@Service("QuotationLookupService")
public class QuotationLookupService extends BaseService {

	@Autowired
	private QuotationDao quotationDao;

	@Autowired
	private PurposeDao purposeDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<QuotationLookupTableList> getLookupTableList(DatatableRequestModel model, Integer[] purposeId, String[] outletTypeId, Integer[] unitId){

		Order order = this.getOrder(model,"", "id", "survey", "unitCode", "unitName", "productId", "productAttribute", "firmCode", "firmName");
		
		String search = model.getSearch().get("value");
		
		List<QuotationLookupTableList> returnList = quotationDao.getLookupTableList(search, model.getStart(), model.getLength(), order, purposeId, outletTypeId, unitId);
		
		DatatableResponseModel<QuotationLookupTableList> response = new DatatableResponseModel<QuotationLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal = quotationDao.countLookupTableList("", purposeId, outletTypeId, unitId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationDao.countLookupTableList(search, purposeId, outletTypeId, unitId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search, Integer[] purposeId, String[] outletTypeId){
		
		return quotationDao.getLookupTableSelectAll(search, purposeId, outletTypeId);
	}
	
	/**
	 * Get all purposes
	 */
	public List<Purpose> getPurposes() {
		return purposeDao.getAll();
	}
	
	/**
	 * Get table list by ids
	 */
	public List<QuotationLookupTableList> getTableListByIds(Integer[] ids){
		return quotationDao.getTableListByIds(ids);
	}

	/**
	 * datatable select all with CPI Survey Form
	 */
	public List<Integer> getLookupTableSelectAllWithCPISurveyForm(String search, Integer[] purposeId,
			String[] outletTypeId, String[] cpiSurveyForm) {
		return quotationDao.getLookupTableSelectAllWithCPISurveyForm(search, purposeId, outletTypeId, cpiSurveyForm);
	}

	public DatatableResponseModel<QuotationLookupTableList> getLookupTableListWithCPISurveyForm(
			DatatableRequestModel model, Integer[] purposeId, String[] outletTypeId, Integer[] unitId,
			String[] cpiSurveyForm) {
		Order order = this.getOrder(model,"", "id", "survey", "unitCode", "unitName", "productId", "productAttribute", "firmCode", "firmName");
				
		String search = model.getSearch().get("value");
		
		List<QuotationLookupTableList> returnList = quotationDao.getLookupTableListWithCPISurveyForm(search, model.getStart(), model.getLength(), order, purposeId, outletTypeId, unitId, cpiSurveyForm);
		
		DatatableResponseModel<QuotationLookupTableList> response = new DatatableResponseModel<QuotationLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal = quotationDao.countLookupTableListWithCPISurveyForm("", purposeId, outletTypeId, unitId, cpiSurveyForm);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationDao.countLookupTableListWithCPISurveyForm(search, purposeId, outletTypeId, unitId, cpiSurveyForm);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * datatable select all with AssignmentIds
	 */
	
	public List<Integer> getLookupTableSelectAllWithAssignmentIds(String search, Integer[] purposeId,
			String[] outletTypeId, Integer[] assignmentIds) {
		return quotationDao.getLookupTableSelectAllWithAssignmentIds(search, purposeId, outletTypeId, assignmentIds);
	}

	public DatatableResponseModel<QuotationLookupTableList> getLookupTableListWithAssignmentIds(
			DatatableRequestModel model, Integer[] purposeId, String[] outletTypeId, Integer[] unitId,
			Integer[] assignmentIds) {
		Order order = this.getOrder(model,"", "id", "survey", "unitCode", "unitName", "productId", "productAttribute", "firmCode", "firmName");
				
		String search = model.getSearch().get("value");
		
		List<QuotationLookupTableList> returnList = quotationDao.getLookupTableListWithAssignmentIds(search, model.getStart(), model.getLength(), order, purposeId, outletTypeId, unitId, assignmentIds);
		
		DatatableResponseModel<QuotationLookupTableList> response = new DatatableResponseModel<QuotationLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal = quotationDao.countLookupTableListWithAssignmentIds("", purposeId, outletTypeId, unitId, assignmentIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationDao.countLookupTableListWithAssignmentIds(search, purposeId, outletTypeId, unitId, assignmentIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
}
