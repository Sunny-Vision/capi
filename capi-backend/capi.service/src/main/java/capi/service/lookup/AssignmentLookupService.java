package capi.service.lookup;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.commonLookup.AssignmentLookupSurveyMonthTableList;
import capi.model.commonLookup.AssignmentLookupTableList;
import capi.model.itineraryPlanning.MetricSelectedAssignmentModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("AssignmentLookupService")
public class AssignmentLookupService extends BaseService {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentLookupService.class);

	@Autowired
	private AssignmentDao assignmentDao;

	@Autowired
	private CommonService commonService;
	
	/**
	 * Outlet DataTable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<AssignmentLookupTableList> getLookupOutletTableList(DatatableRequestModel model,
			Integer userId, String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String date, Integer[] excludedOutletIds) throws ParseException{

			DatatableResponseModel<AssignmentLookupTableList> response = new DatatableResponseModel<AssignmentLookupTableList>();
		
			try{
				Order order = this.getOrder(model, "", "referenceNo", "batchCode", "firm", "district", "tpu", "deadline2", "address", "noOfQuotation", "convenientTime", "outletRemark");
				
				String search = model.getSearch().get("value");
				
				Date planDate = commonService.getDate(date);
				
				List<AssignmentLookupTableList> result = assignmentDao.getOutletTableList(search, model.getStart(), model.getLength(), order,
						userId, outletTypeId, districtId, tpuId, planDate, excludedOutletIds);
				
				for (AssignmentLookupTableList entry: result) {
					entry.setConvenientTime(commonService.formatTime(entry.getConvenientStartTime())+"-"+commonService.formatTime(entry.getConvenientEndTime()));
				}
				
				response.setDraw(model.getDraw());
				response.setData(result);
				Long recordTotal = assignmentDao.countOutletLookupTableList("", userId, null, null, null, planDate, excludedOutletIds);
				response.setRecordsTotal(recordTotal.intValue());
				Long recordFiltered = assignmentDao.countOutletLookupTableList(search, userId, outletTypeId, districtId, tpuId, planDate, excludedOutletIds);
				response.setRecordsFiltered(recordFiltered.intValue());
			}
		    catch (Exception e) {
				logger.error("getLookupOutletTableList", e);
			}
		return response;
	}
	
	/**
	 * Building DataTable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<AssignmentLookupTableList> getLookupBuildingTableList(DatatableRequestModel model,
			Integer userId, Integer[] districtId, Integer[] tpuId, String[] survey, Integer[] surveyMonthId, String date, Integer[] excludedAssignmentIds) throws ParseException{

		Order order = this.getOrder(model, "","referenceNo", "firm", "district", "tpu", "deadline", "address");
		
		String search = model.getSearch().get("value");
		
		Date planDate = commonService.getDate(date);
		
		List<AssignmentLookupTableList> result = assignmentDao.getBuildingTableList(search, model.getStart(), model.getLength(), order,
				userId, districtId, tpuId, survey, surveyMonthId, planDate, excludedAssignmentIds);
		
		for (AssignmentLookupTableList entry: result) {
			entry.setConvenientTime(commonService.formatTime(entry.getConvenientStartTime())+"-"+commonService.formatTime(entry.getConvenientEndTime()));
		}
		
		DatatableResponseModel<AssignmentLookupTableList> response = new DatatableResponseModel<AssignmentLookupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countBuildingLookupTableList("", userId, null, null, null, null, planDate, excludedAssignmentIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countBuildingLookupTableList(search, userId, districtId, tpuId, survey, surveyMonthId, planDate, excludedAssignmentIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Assignment (with Survey Month) DataTable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<AssignmentLookupSurveyMonthTableList> getLookupSurveyMonthTableList(DatatableRequestModel model,
			Integer surveyMonthId, Integer officerId) {

		Order order = this.getOrder(model, "", "referenceNo", "collectionDate", "startDate", "endDate", "firm", 
							"district", "tpu", "batchCode", "noOfQuotation");
		
		String search = model.getSearch().get("value");
		
		List<AssignmentLookupSurveyMonthTableList> result = assignmentDao.getAssignmentLookupSurveyMonthTableList(search, model.getStart(), model.getLength(), order,
				surveyMonthId, officerId);
		
		DatatableResponseModel<AssignmentLookupSurveyMonthTableList> response = new DatatableResponseModel<AssignmentLookupSurveyMonthTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countAssignmentLookupSurveyMonthTableList("", surveyMonthId, officerId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countAssignmentLookupSurveyMonthTableList(search, surveyMonthId, officerId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public MetricSelectedAssignmentModel metricForAssignmentSelectionPopup (Integer userId, String date, Integer[] selectedOutletIds, Integer[] selectedAssignmentIds) throws ParseException{
		Date planDate = commonService.getDate(date);
		MetricSelectedAssignmentModel model = assignmentDao.countOutletLookupSelectedAssignment(userId, planDate, selectedOutletIds);
		
		if (selectedAssignmentIds != null){
			model.selectedBuildings = (long) selectedAssignmentIds.length;
		} else {
			model.selectedBuildings = (long) 0;
		}
		
		return model;
	}

}
