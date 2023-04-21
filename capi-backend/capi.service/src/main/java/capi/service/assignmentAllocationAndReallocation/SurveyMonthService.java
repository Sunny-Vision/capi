package capi.service.assignmentAllocationAndReallocation;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentAdjustmentDao;
import capi.dal.AssignmentAttributeDao;
import capi.dal.AssignmentDao;
import capi.dal.BatchCollectionDateDao;
import capi.dal.BatchDao;
import capi.dal.CalendarEventDao;
import capi.dal.ClosingDateDao;
import capi.dal.DistrictHeadAdjustmentDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.PECheckTaskDao;
import capi.dal.PECheckUnitCriteriaDao;
import capi.dal.PEExcludedOutletTypeDao;
import capi.dal.PETopManagementDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SubPriceColumnDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.SurveyMonthDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.TourRecordDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.AssignmentAdjustment;
import capi.entity.AssignmentAttribute;
import capi.entity.Batch;
import capi.entity.BatchCollectionDate;
import capi.entity.ClosingDate;
import capi.entity.District;
import capi.entity.DistrictHeadAdjustment;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Item;
import capi.entity.Outlet;
import capi.entity.PECheckTask;
import capi.entity.PECheckUnitCriteria;
import capi.entity.PEExcludedOutletType;
import capi.entity.PETopManagement;
import capi.entity.PricingFrequency;
import capi.entity.Purpose;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.entity.SystemConfiguration;
import capi.entity.Tpu;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.SurveyMonthSyncData;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffNameModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.AllocationBatchEditModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.AssignmentAttributeEditModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BackTrackDateDisplayModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchCategoryModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchCollectionDateEditModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchQuotationActiveModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.IndexingModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthEditModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthListModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthSession;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.generation.MatchedPEQuotationRecordResult;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.generation.OutletQuotationModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.generation.QuotationRecordGroupingModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDate;
import capi.service.BaseService;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Collections;;

@Service("SurveyMonthService")
public class SurveyMonthService extends BaseService  {
	
	@Autowired
	SurveyMonthDao surveyMonthDao;
	
	@Autowired
	AllocationBatchDao allocationBatchDao;
	
	@Autowired
	AssignmentAdjustmentDao assignmentAdjDao;
	
	@Autowired
	AssignmentAttributeDao assignmentAttrDao;
	
	@Autowired
	AssignmentDao assignmentDao;
	
	@Autowired
	BatchCollectionDateDao batchCollectionDateDao;

	@Autowired
	BatchDao batchDao;
	
	@Autowired
	ClosingDateDao closingDateDao;

	@Autowired
	CalendarEventDao calEventDao;
	
	@Autowired
	DistrictHeadAdjustmentDao districtHeadAdjustmentDao;
	
	@Autowired
	PECheckTaskDao pECheckTaskDao;
	
	@Autowired
	PEExcludedOutletTypeDao pEExcludedOutletTypeDao;
	
	@Autowired
	PECheckUnitCriteriaDao pECheckUnitCriteriaDao;
	
	@Autowired
	PurposeDao purposeDao;

	@Autowired
	QuotationRecordDao quotationRecordDao;

	@Autowired
	QuotationDao quotationDao;
	
	@Autowired
	IndoorQuotationRecordDao indoorQuotationRecordDao;

	@Autowired
	SubPriceRecordDao subPriceRecordDao;
	
	@Autowired
	SubPriceColumnDao subPriceColumnDao;	

	@Autowired
	SystemConfigurationDao sysConfigDao;
	
	@Autowired
	TourRecordDao tourRecordDao;
	
	@Autowired
	UserDao userDao;
		
	@Autowired
	CommonService commonService;
	
	@Autowired
	PETopManagementDao peTopManagementDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<SurveyMonthListModel> querySurveyMonth(DatatableRequestModel model){
		
		Order order = this.getOrder(model, "referenceMonth", "startDate", "endDate", "closingDate", "status");
		
		String search = model.getSearch().get("value");
		
		List<SurveyMonthListModel> result = surveyMonthDao.listSurveyMonth(search, model.getStart(), model.getLength(), order);
		
		SystemConfiguration sc = this.sysConfigDao.findByName(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
		
		
		
		for(SurveyMonthListModel smm : result){
			smm.setReferenceMonthStr(commonService.formatMonth(smm.getReferenceMonth()));
			smm.setStartDateStr(commonService.formatDate(smm.getStartDate()));
			smm.setEndDateStr(commonService.formatDate(smm.getEndDate()));
			smm.setClosingDateStr(commonService.formatDate(smm.getClosingDate()));
			if(smm.getStatus() == 6 || smm.getStatus() == 7){
				smm.setRemovable(true);
			}else{
				smm.setRemovable(false);
			}
			
		}
		
		DatatableResponseModel<SurveyMonthListModel> response = new DatatableResponseModel<SurveyMonthListModel>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = surveyMonthDao.countSurveyMonth("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = surveyMonthDao.countSurveyMonth(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public ClosingDate getClosingDateByReferenceMonth(Date referenceMonth){
		return closingDateDao.getClosingDateByReferenceMonth(referenceMonth);
	}
	
	public List<BatchCategoryModel> getBatchWithCategory(int surveyMonthId){
		List<BatchCategoryModel> categoryList = new ArrayList<BatchCategoryModel>();
		
		BatchCategoryModel uncategorizedBatch = new BatchCategoryModel(); 
		
//		List<Batch> entityList = batchDao.getAll();
		List<BatchQuotationActiveModel> entityList = batchDao.getAllBatchQuotationActive();
//		for(Batch entity : entityList){
		for(BatchQuotationActiveModel entity : entityList){
			List<AssignmentAttribute> assignmentAttributes = assignmentAttrDao.findAssignmentAttributeByBatchAndSurveyMonth(entity.getBatchId(), surveyMonthId);
			if(assignmentAttributes != null && assignmentAttributes.size() > 0){
				for (AssignmentAttribute assignmentAttribute : assignmentAttributes) {
					BatchModel batchModel = new BatchModel();
					batchModel.setBatchId(entity.getBatchId());
					batchModel.setCode(entity.getCode());
					batchModel.setAssignmentType(entity.getAssignmentType());
					batchModel.setAssignmentAttributeId(assignmentAttribute.getAssignmentAttributeId());
					if(entity.getBatchCategory() == null || entity.getBatchCategory().length() == 0){
						uncategorizedBatch.getBatchList().add(batchModel);
					}else{
						Boolean categoryModelExist = false;
						for(BatchCategoryModel category : categoryList){
							if(category.getBatchCategoryName().equalsIgnoreCase(entity.getBatchCategory())){
								categoryModelExist = true;
								category.getBatchList().add(batchModel);
							}
						}
						if(!categoryModelExist){
							BatchCategoryModel category = new BatchCategoryModel();
							category.setBatchCategoryName(entity.getBatchCategory());
							category.getBatchList().add(batchModel);
							categoryList.add(category);
						}
					}
				}
			} else {
				BatchModel batchModel = new BatchModel();
				batchModel.setBatchId(entity.getBatchId());
				batchModel.setCode(entity.getCode());
				batchModel.setAssignmentType(entity.getAssignmentType());
				if(entity.getBatchCategory() == null || entity.getBatchCategory().length() == 0){
					uncategorizedBatch.getBatchList().add(batchModel);
				}else{
					Boolean categoryModelExist = false;
					for(BatchCategoryModel category : categoryList){
						if(category.getBatchCategoryName().equalsIgnoreCase(entity.getBatchCategory())){
							categoryModelExist = true;
							category.getBatchList().add(batchModel);
						}
					}
					if(!categoryModelExist){
						BatchCategoryModel category = new BatchCategoryModel();
						category.setBatchCategoryName(entity.getBatchCategory());
						category.getBatchList().add(batchModel);
						categoryList.add(category);
					}
				}
			}
		}
		categoryList.add(uncategorizedBatch);
		return categoryList;
	}
	
	public List<Batch> getBatchByCategory(String category){
		return batchDao.getBatchByCategory(category);
	}
	
	public List<BatchQuotationActiveModel> getBatchQuotationActiveByCategory(String category){
		return batchDao.getBatchQuotationActiveByCategory(category);
	}
	
	public List<StaffNameModel> getSelectedStaffName(List<Integer> ids){
		Iterator<User> users = this.userDao.getUsersByIds(ids).iterator();
		List<StaffNameModel> nameList = new ArrayList<StaffNameModel>();
		while(users.hasNext()){
			User u = users.next();
			StaffNameModel model = new StaffNameModel();
			model.setStaffName(u.getStaffCode() + " - " + u.getChineseName() + " ( " + u.getDestination() + " )");
			model.setUserId(u.getUserId());
			nameList.add(model);
		}
		return nameList;
	}
	
	public Batch getBatchById(Integer id){
		return this.batchDao.findById(id);
	}
	
	public List<Date> getPreviousWorkingDates(Date fromDate, int noOfDays){
		return calEventDao.getPreviousNWorkdingDate(fromDate, noOfDays);
	}
	
	@Transactional
	public Integer generateSurveyMonth(SurveyMonthSession sessionModel) throws ParseException{
		SurveyMonth sm = this.surveyMonthDao.getSurveyMonthByReferenceMonth(sessionModel.getSessionSurveyMonth().getReferenceMonth());
		
		if (sm != null) {
			this.clearSurveyMonth(sm);
			this.surveyMonthDao.delete(sm);
		}
		
		sm = new SurveyMonth();
		
		capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth surveyMonthSession = sessionModel.getSessionSurveyMonth();
		Date closingDateDate = commonService.getMonth(surveyMonthSession.getReferenceMonthStr());
		
		
		ClosingDate closingDate = this.closingDateDao.getClosingDateByReferenceMonth(closingDateDate);
		
		sm.setClosingDate(closingDate);
		sm.setStartDate(surveyMonthSession.getStartDate());
		sm.setEndDate(surveyMonthSession.getEndDate());
		sm.setReferenceMonth(surveyMonthSession.getReferenceMonth());
		sm.setStatus(6); // created
		
		this.surveyMonthDao.save(sm);
		
		List<AllocationBatch> allocationBatchSession = sessionModel.getSessionNewAllocationBatch();
		for(AllocationBatch abSession : allocationBatchSession){
			capi.entity.AllocationBatch ab = new capi.entity.AllocationBatch();
			ab.setBatchName(abSession.getNumberOfBatch());
			ab.setStartDate(abSession.getStartDate());
			ab.setEndDate(abSession.getEndDate());
			ab.setSurveyMonth(sm);
			this.allocationBatchDao.save(ab);
			abSession.setAllocationBatchId(ab.getAllocationBatchId());
			//abSession.setEntity(ab);
		}
		
		List<AssignmentAttributes> assignmentAttributeSession = sessionModel.getSessionNewAssignmentAttr();
		if(assignmentAttributeSession != null){
			for(AssignmentAttributes aaSession : assignmentAttributeSession){
				if(aaSession.getReferenceId().length() > 0){
					AssignmentAttribute aa = new AssignmentAttribute();
					Batch b = this.batchDao.findById(aaSession.getBatchId());
					User u = this.userDao.findById(aaSession.getOfficerIds());
					aa.setUser(u);
					aa.setBatch(b);
					aa.setStartDate(aaSession.getStartDate());
					aa.setEndDate(aaSession.getEndDate());
					aa.setSession(aaSession.getSession());
					aa.setBatchCategory(b.getBatchCategory());
					aa.setUser(u);
					aa.setSurveyMonth(sm);
					for(AllocationBatch ab : allocationBatchSession){
						String refId = "new-"+ab.getId();
						if( refId.equalsIgnoreCase(aaSession.getAllocationBatchRefId())){
//							aa.setAllocationBatch(ab.getEntity());
							aa.setAllocationBatch(allocationBatchDao.findById(ab.getAllocationBatchId()));
							break;
						}
						
					}
					String[] collectionDateStrs = aaSession.getCollectionDatesStr().split(",");
					List<Date> collectionDate = new ArrayList<Date>();
					for(String collectionDateStr : collectionDateStrs){
						try{
							collectionDate.add(commonService.getDate(collectionDateStr));
						}catch(Exception e){
							
						}
					}
					this.assignmentAttrDao.save(aa);
					if(collectionDate.size() > 0){
						for( BackTrackDateDisplayModel btdm : sessionModel.getBackTrackDateModelList()){
							for(BackTrackDate btd : btdm.getBackTrackDayList()){
								BatchCollectionDate bcd = new BatchCollectionDate();
								for(Date cd : collectionDate){
									if(cd.compareTo(btd.getReferenceCollectionDate()) == 0 && btd.getBatchId().intValue() == aa.getBatch().getId().intValue()){
										if(btd.getBackTrackDateList() != null){
											if(btd.getBackTrackDateList().size() > 0){
												bcd.setBackTrackDate1(btd.getBackTrackDateList().get(0));
											}
											if(btd.getBackTrackDateList().size() > 1){
												bcd.setBackTrackDate2(btd.getBackTrackDateList().get(1));
											}
											if(btd.getBackTrackDateList().size() > 2){
												bcd.setBackTrackDate3(btd.getBackTrackDateList().get(2));
											}
											bcd.setAssignmentAttribute(aa);
											bcd.setDate(btd.getReferenceCollectionDate());
											bcd.setHasBackTrack(btd.getHasBackTrack());
											this.batchCollectionDateDao.save(bcd);
										}else{
											bcd.setAssignmentAttribute(aa);
											bcd.setDate(btd.getReferenceCollectionDate());
											bcd.setHasBackTrack(false);
											this.batchCollectionDateDao.save(bcd);
										}
									}
								}
							}
						}
					}
				
				}
				
			}
		}
		
		List<AssignmentAttributes> nonCateAssignmentAttributeSession = sessionModel.getSessionNonCateAssignmentAttr();
		if(nonCateAssignmentAttributeSession != null){
			for(AssignmentAttributes aaSession : nonCateAssignmentAttributeSession){
				if(aaSession.getReferenceId().length() > 0){
					AssignmentAttribute aa = new AssignmentAttribute();
					Batch b = this.batchDao.findById(aaSession.getBatchId());
					User u = this.userDao.findById(aaSession.getOfficerIds());
					aa.setBatch(b);
					aa.setStartDate(aaSession.getStartDate());
					aa.setEndDate(aaSession.getEndDate());
					aa.setSession(aaSession.getSession());
					aa.setBatchCategory(b.getBatchCategory());
					aa.setUser(u);
					aa.setSurveyMonth(sm);
					for(AllocationBatch ab : allocationBatchSession){
						String refId = "new-"+ab.getId();
						if( refId.equalsIgnoreCase(aaSession.getAllocationBatchRefId())){
//							aa.setAllocationBatch(ab.getEntity());
							aa.setAllocationBatch(allocationBatchDao.findById(ab.getAllocationBatchId()));
							break;
						}
					}
					String[] collectionDateStrs = aaSession.getCollectionDatesStr().split(",");
					List<Date> collectionDate = new ArrayList<Date>();
					for(String collectionDateStr : collectionDateStrs){
						try{
							collectionDate.add(commonService.getDate(collectionDateStr));
						}catch(Exception e){
							
						}
					}
					this.assignmentAttrDao.save(aa);
					if(collectionDate.size() > 0){
						for( BackTrackDateDisplayModel btdm : sessionModel.getBackTrackDateModelList()){
							for(BackTrackDate btd : btdm.getBackTrackDayList()){
								BatchCollectionDate bcd = new BatchCollectionDate();
								Integer btdbId = btd.getBatchId();
								Integer aaSessionBId = aaSession.getBatchId();
								Date btdRDate = btd.getReferenceCollectionDate();
								if(btdbId.equals(aaSessionBId)){
									for(Date cDate : collectionDate){
										if(cDate.equals(btdRDate)){
											if(btd.getBackTrackDateList() != null){
												if(btd.getBackTrackDateList().size() > 0){
													bcd.setBackTrackDate1(btd.getBackTrackDateList().get(0));
												}
												if(btd.getBackTrackDateList().size() > 1){
													bcd.setBackTrackDate2(btd.getBackTrackDateList().get(1));
												}
												if(btd.getBackTrackDateList().size() > 2){
													bcd.setBackTrackDate3(btd.getBackTrackDateList().get(2));
												}
												bcd.setAssignmentAttribute(aa);
												bcd.setDate(btd.getReferenceCollectionDate());
												bcd.setHasBackTrack(btd.getHasBackTrack());
												this.batchCollectionDateDao.save(bcd);
												aa.getBatchCollectionDates().add(bcd);
											}else{
												bcd.setAssignmentAttribute(aa);
												bcd.setDate(btd.getReferenceCollectionDate());
												bcd.setHasBackTrack(false);
												this.batchCollectionDateDao.save(bcd);
												aa.getBatchCollectionDates().add(bcd);
											}
										}
									}
								}
							}
						}
						this.assignmentAttrDao.save(aa);
					}
				
				}
				
			}
		}

		this.surveyMonthDao.flush();
		
		return sm.getId();
	}

	public SurveyMonthEditModel convertViewModel(SurveyMonthSession session, capi.entity.SurveyMonth oldEntity) throws ParseException{
		SurveyMonthEditModel editModel = new SurveyMonthEditModel();
		editModel.convert(session, 7, true, oldEntity);
		return editModel;
	}
	
	@Transactional
	public int generateDraftSurveyMonth(SurveyMonthSession sessionModel, Boolean isFinish) throws ParseException{
		try{
			SurveyMonth oldSurveyMonthEntity = null;
			if (sessionModel.getSessionSurveyMonth().getId() != null && sessionModel.getSessionSurveyMonth().getId() > 0){
				oldSurveyMonthEntity = surveyMonthDao.findById(sessionModel.getSessionSurveyMonth().getId());
			} else {
				oldSurveyMonthEntity = new SurveyMonth();
			}
	
			SurveyMonthEditModel editModel = convertViewModel(sessionModel, oldSurveyMonthEntity);
			if(oldSurveyMonthEntity.getStatus() != null){
				if (oldSurveyMonthEntity.getStatus() != 6){
					editModel.setStatus(isFinish ? 6 : 7);
				} else {
					editModel.setStatus(oldSurveyMonthEntity.getStatus());
				}
			} else {
				editModel.setStatus(isFinish ? 6 : 7);
			}
			
			/**
			 *  Survey Month
			 */
			BeanUtils.copyProperties(editModel, oldSurveyMonthEntity);
			ClosingDate closingDate = this.closingDateDao.getClosingDateByReferenceMonth(editModel.getReferenceMonth());
			oldSurveyMonthEntity.setClosingDate(closingDate);
			surveyMonthDao.save(oldSurveyMonthEntity);
			
			/**
			 *  Allocation Batch
			 */
			Map<Integer, capi.entity.AllocationBatch> newIdAllocationBatchIdMap = new HashMap<>();
			SurveyMonth surveyMonth = surveyMonthDao.findById(oldSurveyMonthEntity.getId());
			if(surveyMonth.getAllocationBatches() != null){
				editModel.getDeleteUpdateAllocationBatchRecords(new HashSet<>(surveyMonth.getAllocationBatches()));
			}
			surveyMonth.getAllocationBatches().clear();
			
			//Insert new allocation batch
			if(editModel.getNewAllocationBatchRecords() != null && !editModel.getNewAllocationBatchRecords().isEmpty()){
				for (AllocationBatchEditModel model : editModel.getNewAllocationBatchRecords()) {
					capi.entity.AllocationBatch entity = new capi.entity.AllocationBatch();
					BeanUtils.copyProperties(model, entity);
					entity.setSurveyMonth(surveyMonth);
					allocationBatchDao.save(entity);
					surveyMonth.getAllocationBatches().add(entity);
					newIdAllocationBatchIdMap.put(model.getModelRefId(), entity);
				}
			}
			//Update Allocation Batch
			if(editModel.getAllocationBatchUpdateRecordIds() != null && !editModel.getAllocationBatchUpdateRecordIds().isEmpty()){
				for(Integer allocationBatchId : editModel.getAllocationBatchUpdateRecordIds()){
					capi.entity.AllocationBatch entity = null;
					entity = allocationBatchDao.findById(allocationBatchId);
					AllocationBatchEditModel model = editModel.getAllocationBatchIdAllocationBatchMap().get(allocationBatchId); 
					BeanUtils.copyProperties(model, entity);
					entity.setSurveyMonth(surveyMonth);
					allocationBatchDao.save(entity);
					surveyMonth.getAllocationBatches().add(entity);
					newIdAllocationBatchIdMap.put(model.getModelRefId(), entity);
				}
			}
			//Delete Allocation Batch
			if(editModel.getAllocationBatchDeleteRecordIds() != null && !editModel.getAllocationBatchDeleteRecordIds().isEmpty()){
				List<capi.entity.AllocationBatch> deleteItems = new ArrayList<>();
				for(Integer allocationBatchId : editModel.getAllocationBatchDeleteRecordIds()){
					capi.entity.AllocationBatch entity = null;
					entity = allocationBatchDao.findById(allocationBatchId);
					if(entity != null){
						deleteItems.add(entity);
						allocationBatchDao.delete(entity);
					}
				}
				surveyMonth.getAllocationBatches().removeAll(deleteItems);
			}
			
			/**
			 *  Assignment Attribute
			 */
			if(surveyMonth.getAssignmentAttributes() != null){
				editModel.getDeleteUpdateAssignmentAttributeRecords(new HashSet<>(surveyMonth.getAssignmentAttributes()));
			}
			
			surveyMonth.getAssignmentAttributes().clear();
			
			//Insert new assignment attribute
			if(editModel.getNewAssignmentAttributeRecords() != null && !editModel.getNewAssignmentAttributeRecords().isEmpty()){
				for (AssignmentAttributeEditModel model : editModel.getNewAssignmentAttributeRecords()) {
					capi.entity.AssignmentAttribute entity = new capi.entity.AssignmentAttribute();
					BeanUtils.copyProperties(model, entity);
					Batch batch = batchDao.findById(model.getBatchId());
					entity.setBatch(batch);
					if(model.getUserId() != null){
						User user = userDao.findById(model.getUserId());
						entity.setUser(user);
					}
					entity.setSurveyMonth(surveyMonth);
					capi.entity.AllocationBatch allocationBatch = null;
					if(model.getAllocationBatchId() != null){
						allocationBatch = newIdAllocationBatchIdMap.get(model.getAllocationBatchId());
						entity.setAllocationBatch(allocationBatch);
					}
					assignmentAttrDao.save(entity);
					if(allocationBatch != null){
						allocationBatch.getAssignmentAttributes().addAll(new ArrayList<>(Arrays.asList(entity)));
					}
					
					if(model.getBatchCollectionDates() != null && !model.getBatchCollectionDates().isEmpty()){
						Set<BatchCollectionDate> batchCollectionDates = new HashSet<>();
						
						for (BatchCollectionDateEditModel batchCollectionDateEditModel : model.getBatchCollectionDates()) {
							capi.entity.BatchCollectionDate batchCollectionDateEnity = new capi.entity.BatchCollectionDate();
							BeanUtils.copyProperties(batchCollectionDateEditModel, batchCollectionDateEnity);
							batchCollectionDateEnity.setAssignmentAttribute(entity);
							batchCollectionDateDao.save(batchCollectionDateEnity);
							batchCollectionDates.add(batchCollectionDateEnity);
						}
						entity.setBatchCollectionDates(batchCollectionDates);
					}
					surveyMonth.getAssignmentAttributes().addAll(new ArrayList<>(Arrays.asList(entity)));
					
				}
			}
			
			//Update Assignment Attribute
			if(editModel.getAssignmentAttributeUpdateRecordIds() != null && !editModel.getAssignmentAttributeUpdateRecordIds().isEmpty()){
				for(Integer assignmentAttributeId : editModel.getAssignmentAttributeUpdateRecordIds()){
					capi.entity.AssignmentAttribute entity = null;
					entity = assignmentAttrDao.findById(assignmentAttributeId);
					
					AssignmentAttributeEditModel model = editModel.getIdAssignmentAttributeMap().get(assignmentAttributeId);
					
					model.setAssignmentAttributeId(entity.getAssignmentAttributeId());
					BeanUtils.copyProperties(model, entity);
					Batch batch = batchDao.findById(model.getBatchId());
					entity.setBatch(batch);
					if(model.getUserId() != null){
						User user = userDao.findById(model.getUserId());
						entity.setUser(user);
					}
					entity.setSurveyMonth(surveyMonth);
					capi.entity.AllocationBatch allocationBatch = null;
					if(model.getAllocationBatchId() != null){
						allocationBatch = newIdAllocationBatchIdMap.get(model.getAllocationBatchId());
						entity.setAllocationBatch(allocationBatch);
					}
					assignmentAttrDao.save(entity);
					
					if(allocationBatch != null){
						allocationBatch.getAssignmentAttributes().addAll(new ArrayList<>(Arrays.asList(entity)));
					}
					
					updateDeleteBatchCollectionDate(model, editModel);
					
					surveyMonth.getAssignmentAttributes().addAll(new ArrayList<>(Arrays.asList(entity)));
				
				}
			}
			
			//Delete Assignment Attribute
			if(editModel.getAssignmentAttributeDeleteRecordIds() != null && !editModel.getAssignmentAttributeDeleteRecordIds().isEmpty()){
				List<capi.entity.AssignmentAttribute> deleteItems = new ArrayList<>();
				for(Integer assignmentAttributeId : editModel.getAssignmentAttributeDeleteRecordIds()){
					capi.entity.AssignmentAttribute entity =  assignmentAttrDao.findById(assignmentAttributeId);
					if(entity != null){
						List<BatchCollectionDate> batchCollectionDates = new ArrayList<>(entity.getBatchCollectionDates()); 
						for(BatchCollectionDate item : batchCollectionDates){
							batchCollectionDateDao.delete(item);
						}
						
						deleteItems.add(entity);
						assignmentAttrDao.delete(entity);
					}
				}
				surveyMonth.getAssignmentAttributes().removeAll(deleteItems);
			}
			
			surveyMonthDao.save(surveyMonth);
			return surveyMonth.getId();
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Update/Delete Batch Collection Date
	 */
	public void updateDeleteBatchCollectionDate(AssignmentAttributeEditModel assignmentAttributeEditModel, SurveyMonthEditModel editModel){
		if(assignmentAttributeEditModel.getAssignmentAttributeId() != null && assignmentAttributeEditModel.getBatchCollectionDates() != null){
			AssignmentAttribute assignmentAttribute = assignmentAttrDao.findById(assignmentAttributeEditModel.getAssignmentAttributeId());
			editModel.getDeleteUpdateBatchCollectionDateRecords(assignmentAttribute.getAssignmentAttributeId(),assignmentAttribute.getBatchCollectionDates());
			if(assignmentAttribute.getBatchCollectionDates() != null){
				assignmentAttribute.getBatchCollectionDates().clear();
			}
			List<BatchCollectionDate> newBatchCollectionDates = new ArrayList<>();
			
			if(editModel.getNewBatchCollectionDateRecords() != null && !editModel.getNewBatchCollectionDateRecords().isEmpty()){
				for (BatchCollectionDateEditModel model : editModel.getNewBatchCollectionDateRecords()) {
					capi.entity.BatchCollectionDate entity = new capi.entity.BatchCollectionDate();
					BeanUtils.copyProperties(model, entity);
					entity.setAssignmentAttribute(assignmentAttribute);
					batchCollectionDateDao.save(entity);
					newBatchCollectionDates.add(entity);
				}
			}
			
			//Update Batch Collection Date
			if(editModel.getBatchCollectionDateUpdateRecordIds() != null && !editModel.getBatchCollectionDateUpdateRecordIds().isEmpty()){
				for (Integer batchCollectionDateId : editModel.getBatchCollectionDateUpdateRecordIds()) {
					capi.entity.BatchCollectionDate entity = batchCollectionDateDao.findById(batchCollectionDateId);
					BatchCollectionDateEditModel batchCollectionDateEditModel = editModel.getIdBatchCollectionDateMap().get(batchCollectionDateId);
					BeanUtils.copyProperties(batchCollectionDateEditModel, entity);
					entity.setAssignmentAttribute(assignmentAttribute);
					batchCollectionDateDao.save(entity);
					newBatchCollectionDates.add(entity);
				}
			}
			
			//Delete Batch Collection Date
			if(editModel.getBatchCollectionDateDeleteRecordIds() != null && !editModel.getBatchCollectionDateDeleteRecordIds().isEmpty()){
				for(Integer batchCollectionDateId : editModel.getBatchCollectionDateDeleteRecordIds()){
					capi.entity.BatchCollectionDate entity = batchCollectionDateDao.findById(batchCollectionDateId);
					if(entity != null){
						batchCollectionDateDao.delete(entity);
					}
				}
			}
			assignmentAttribute.getBatchCollectionDates().addAll(newBatchCollectionDates);
		}
	}

	/**
	 *  Survey Month
	 */
	public int saveSurveyMonth(SurveyMonthEditModel editModel, SurveyMonth entity){
		BeanUtils.copyProperties(editModel, entity);

		ClosingDate closingDate = closingDateDao.getClosingDateByReferenceMonth(editModel.getReferenceMonth());
		
		entity.setStatus(7);
		entity.setClosingDate(closingDate);
		surveyMonthDao.save(entity);
		
		return entity.getId();
	}
	
	@Deprecated
	public SurveyMonthSession bindDataFromDb(SurveyMonthSession smSession, Integer surveyMonthId, IndexingModel indexing, Integer authLevel){
		Date minCollectionDate = null;
		Date maxCollectionDate = null;
		SurveyMonth sm = this.surveyMonthDao.findById(surveyMonthId);
		capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth smSessionModel = new capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth();
		List<BackTrackDateDisplayModel> btdfSessionList =
				new ArrayList<BackTrackDateDisplayModel>();
		if(sm != null){
			SystemConfiguration sc = this.sysConfigDao.findByName(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
			
			if((sc.getValue() != null && sc.getValue().equalsIgnoreCase("1"))){
				smSession.setReadonly(true);
			}else if(sm.getStatus() == 6 || sm.getStatus() == 7){
				smSession.setReadonly(false);
			}else{
				smSession.setReadonly(true);
			}
			
			if(authLevel == 8 || authLevel == 256){
				smSession.setReadonly(true);
			}
			
			smSession.setIsDraft(sm.getStatus() == 7);
			
			smSessionModel.setId(sm.getId());
			smSessionModel.setClosingDate(sm.getClosingDate().getClosingDate());
			smSessionModel.setEndDate(sm.getEndDate());
			smSessionModel.setStartDate(sm.getStartDate());
			smSessionModel.setReferenceMonth(sm.getReferenceMonth());
			smSessionModel.setClosingDateId(sm.getClosingDate().getClosingDateId());
			
			smSessionModel.setClosingDateStr(commonService.formatDate(smSessionModel.getClosingDate()));
			smSessionModel.setStartDateStr(commonService.formatDate(smSessionModel.getStartDate()));
			smSessionModel.setEndDateStr(commonService.formatDate(smSessionModel.getEndDate()));
			smSessionModel.setReferenceMonthStr(commonService.formatMonth(smSessionModel.getReferenceMonth()));
			
			ArrayList<capi.entity.AllocationBatch> allocationBatchList = new ArrayList<capi.entity.AllocationBatch>(sm.getAllocationBatches());
			Collections.sort(allocationBatchList, new Comparator<capi.entity.AllocationBatch>(){
				@Override
				public int compare(capi.entity.AllocationBatch o1, capi.entity.AllocationBatch o2) {
					// TODO Auto-generated method stub
					return o1.getId().compareTo(o2.getId());
				}
			});
			for(capi.entity.AllocationBatch ab : allocationBatchList){
				capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch allocationBatchModel = 
						new capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch();
				
				allocationBatchModel.setEndDate(ab.getEndDate());
				allocationBatchModel.setStartDate(ab.getStartDate());
				allocationBatchModel.setNumberOfBatch(ab.getBatchName());
				allocationBatchModel.setEndDateStr(commonService.formatDate(ab.getEndDate()));
				allocationBatchModel.setStartDateStr(commonService.formatDate(ab.getStartDate()));
				allocationBatchModel.setId(ab.getId());
				allocationBatchModel.setAllocationBatchId(ab.getId());
				indexing.newBatchAllocationId = ab.getId();
				smSession.getSessionNewAllocationBatch().add(allocationBatchModel);
			}
			
			ArrayList<capi.entity.AssignmentAttribute> assignmentAttributeList = new ArrayList<capi.entity.AssignmentAttribute>(sm.getAssignmentAttributes());
			Collections.sort(assignmentAttributeList, new Comparator<capi.entity.AssignmentAttribute>(){
				@Override
				public int compare(AssignmentAttribute o1,
						AssignmentAttribute o2) {
					// TODO Auto-generated method stub
					return o1.getId().compareTo(o2.getId());
				}
			});
			int aaId = 0;
			for(capi.entity.AssignmentAttribute aa : assignmentAttributeList ){
				capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes assignmentAttrModel = 
						new capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes();
				capi.entity.AllocationBatch ab = aa.getAllocationBatch();
				capi.entity.User u = aa.getUser();
				capi.entity.Batch b = aa.getBatch();
				assignmentAttrModel.setAllocationBatchRefId(ab != null ? "new-"+ab.getId() : null);
//				assignmentAttrModel.setAllocationBatch(ab);
				assignmentAttrModel.setAllocationBatchId(ab != null ? ab.getId() : null);
				assignmentAttrModel.setBatchId(b.getId());
				assignmentAttrModel.setCategory(b.getBatchCategory());
				assignmentAttrModel.setEndDate(aa.getEndDate());
				assignmentAttrModel.setEndDateStr(commonService.formatDate(aa.getEndDate()));
				assignmentAttrModel.setOfficer(u);
				if(u != null){
					assignmentAttrModel.setOfficerIds(u.getId());
				}
				assignmentAttrModel.setReferenceId("new-"+aaId);
				assignmentAttrModel.setSelectedBatchType(b.getAssignmentType());
				assignmentAttrModel.setSession(aa.getSession());
				assignmentAttrModel.setStartDate(aa.getStartDate());
				assignmentAttrModel.setStartDateStr(commonService.formatDate(aa.getStartDate()));
				
				ArrayList<BatchCollectionDate> bcdList = new ArrayList<BatchCollectionDate>(aa.getBatchCollectionDates());
				Collections.sort(bcdList, new Comparator<capi.entity.BatchCollectionDate>(){

					@Override
					public int compare(BatchCollectionDate o1,
							BatchCollectionDate o2) {
						// TODO Auto-generated method stub
						return o1.getId().compareTo(o2.getId());
					}
					
				});
				
				BackTrackDateDisplayModel currentBtddmSession = null;
				for(BackTrackDateDisplayModel btddmSession : btdfSessionList){
					if(btddmSession.getBatchCode().equalsIgnoreCase(b.getCode())){
						currentBtddmSession = btddmSession;
					}
				}
				if(currentBtddmSession == null){
					currentBtddmSession = new BackTrackDateDisplayModel();
					currentBtddmSession.setBatchCode(b.getCode());
					btdfSessionList.add(currentBtddmSession);
					smSession.getBackTrackDateModelList().add(currentBtddmSession);
				}
				//Get the max & min date in this batchCollectionDate
				for(BatchCollectionDate bcd : bcdList){
					if(minCollectionDate == null || bcd.getDate().before(minCollectionDate)){
						minCollectionDate = bcd.getDate();
					}
					if(maxCollectionDate == null || bcd.getDate().after(maxCollectionDate)){
						maxCollectionDate = bcd.getDate();
					}
				}
				
				for(BatchCollectionDate bcd : bcdList){
					
					BackTrackDate btd = new BackTrackDate();
					
					// Add Batch Collection date to AssinmentAttr
					if(assignmentAttrModel.getCollectionDatesStr().length() == 0){
						assignmentAttrModel.setCollectionDatesStr(commonService.formatDate(bcd.getDate()));
					}else{
						assignmentAttrModel.setCollectionDatesStr(
								assignmentAttrModel.getCollectionDatesStr()+","+commonService.formatDate(bcd.getDate()));
					}
					btd.setReferenceCollectionDate(bcd.getDate());
					btd.setReferenceCollectionDateStr(commonService.formatDate(bcd.getDate()));
					btd.setAssignmentAttributesReferenceId("new-"+aaId);
					btd.setBackTrackDateList(new ArrayList<Date>());
					if(bcd.getBackTrackDate1() != null){
						btd.getBackTrackDateList().add(bcd.getBackTrackDate1());
						btd.setBackTrackDateString(commonService.formatDate(bcd.getBackTrackDate1()));
					}
					if(bcd.getBackTrackDate2() != null){
						
						btd.getBackTrackDateList().add(bcd.getBackTrackDate2());
						btd.setBackTrackDateString(btd.getBackTrackDateString()+","+commonService.formatDate(bcd.getBackTrackDate2()));
					}
					if(bcd.getBackTrackDate3() != null){
						btd.getBackTrackDateList().add(bcd.getBackTrackDate3());
						btd.setBackTrackDateString(btd.getBackTrackDateString()+","+commonService.formatDate(bcd.getBackTrackDate3()));
					}
					btd.setBatchCode(b.getCode());
					btd.setBatchId(b.getId());
					btd.setHasBackTrack(bcd.isHasBackTrack());
					btd.setReferenceCollectionDate(bcd.getDate());
					
					
					List<Date> workingDays = new ArrayList<Date>();
					//Get no. of date between max and min collectionDate + 4
					int diffDays = (int)((maxCollectionDate.getTime() - minCollectionDate.getTime()) / (1000 * 60 * 60 * 24)) + 4;
					//Get available working date by today = maxCollection, noOfDate = diffDays
					List<Date> availableWorkingDayList = this.getPreviousWorkingDates(maxCollectionDate, diffDays);
					//Add Available Working and size not > 3
					for(Date workingDate : availableWorkingDayList){
						if(workingDate.before(btd.getReferenceCollectionDate())){
							workingDays.add(workingDate);
							if(workingDays.size() > 3){
								workingDays.remove(workingDays.size() -1);
							}
						}
					}
					btd.setBackTrackDateAvailableFrom(workingDays.get(workingDays.size() -1));
					btd.setBackTrackDateAvailableFromString(commonService.formatDate(workingDays.get(workingDays.size() -1)));
					btd.setBackTrackDateAvailableTo(workingDays.get(0));
					btd.setBackTrackDateAvailableToString(commonService.formatDate(workingDays.get(0)));
					Date date = workingDays.get(workingDays.size() -1);
					while(date.before(workingDays.get(0)) || date.equals(workingDays.get(0))){
						//if the list is not contain the working days, than not add to skip list (available in date picker)
						if(availableWorkingDayList.contains(date) == false){
							btd.getBackTrackDateAvailableSkipList().add(date);
							
						}

						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						cal.add(Calendar.DATE, 1);
						date = cal.getTime();
					}
					
					List<String> skipDateStrList = new ArrayList<String>();
					for(Date skipDate : btd.getBackTrackDateAvailableSkipList()){
						skipDateStrList.add(skipDate.getTime()+"");
					}
					btd.setBackTrackDateAvailableSkipString(StringUtils.join(skipDateStrList, ","));
					//assignmentAttrModel.getCollectionDateList().add(currentBtddmSession);
					currentBtddmSession.getBackTrackDayList().add(btd);
					
				}
				indexing.newAlloactionAttributeId = aaId;
				if(b.getBatchCategory() != null && b.getBatchCategory().length() > 0){
					smSession.getSessionNewAssignmentAttr().add(assignmentAttrModel);	
				}else if(assignmentAttrModel.getReferenceId().length() > 0){
					//Insert uncategorized Assignment Attribute
					assignmentAttrModel.setReferenceId(assignmentAttrModel.getReferenceId().replace("new-", ""));
					for(BackTrackDateDisplayModel btddm : assignmentAttrModel.getCollectionDateList()){
						for(BackTrackDate btd: btddm.getBackTrackDayList()){
							btd.setAssignmentAttributesReferenceId(btd.getAssignmentAttributesReferenceId().replace("new-", ""));
						}
					}
					smSession.getSessionNonCateAssignmentAttr().add(assignmentAttrModel);
				}

				aaId++;
			}
			
			//Initial the page index validation tag
			indexing.tab1 = true;
			indexing.tab2 = true;
			indexing.tab3 = true;
			indexing.tab4 = true;
			
		}
		smSession.setBackTrackDateModelList(btdfSessionList);
		smSession.setSessionSurveyMonth(smSessionModel);
		indexing.newBatchAllocationId++;
		indexing.newAlloactionAttributeId++;
		indexing.readonly = smSession.getReadonly();
		indexing.isDraft = smSession.getIsDraft();
		return smSession;
	}
	
	public SurveyMonthSession prepareViewModel(SurveyMonthSession session, Integer surveyMonthId, IndexingModel indexing, Integer authLevel){
		Date minCollectionDate = null;
		Date maxCollectionDate = null;
		int backTrackDateDisplayModelId = 0;
		SurveyMonth entity = null;
		
		if (surveyMonthId != null){
			 entity = this.surveyMonthDao.findById(surveyMonthId);
		}
		
		capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth sessionModel 
					= new capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth();
		
		List<BackTrackDateDisplayModel> backTrackDates = new ArrayList<BackTrackDateDisplayModel>();
		
		if(entity != null){
			BeanUtils.copyProperties(entity, sessionModel);
			SystemConfiguration isFreezeSurveyMonth = this.sysConfigDao.findByName(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
			
			if(isFreezeSurveyMonth.getValue() != null && isFreezeSurveyMonth.getValue().equalsIgnoreCase("1")){
				session.setReadonly(true);
			}else if(entity.getStatus() == 6 || entity.getStatus() == 7){
				session.setReadonly(false);
				session.setIsDraft(entity.getStatus() == 6 || entity.getStatus() == 7);
			}else{
				session.setReadonly(true);
			}
			
			if(authLevel == 8 || authLevel == 256){
				session.setReadonly(true);
			}
			
			sessionModel.setId(entity.getId());
			sessionModel.setClosingDate(entity.getClosingDate().getClosingDate());
			sessionModel.setEndDate(entity.getEndDate());
			sessionModel.setStartDate(entity.getStartDate());
			sessionModel.setReferenceMonth(entity.getReferenceMonth());
			sessionModel.setClosingDateId(entity.getClosingDate().getClosingDateId());
			
			sessionModel.setClosingDateStr(commonService.formatDate(sessionModel.getClosingDate()));
			sessionModel.setStartDateStr(commonService.formatDate(sessionModel.getStartDate()));
			sessionModel.setEndDateStr(commonService.formatDate(sessionModel.getEndDate()));
			sessionModel.setReferenceMonthStr(commonService.formatMonth(sessionModel.getReferenceMonth()));
			
			List<capi.entity.AllocationBatch> allocationBatches = new ArrayList<capi.entity.AllocationBatch>((Set)entity.getAllocationBatches());

			Collections.sort(allocationBatches, new Comparator<capi.entity.AllocationBatch>(){
				@Override
				public int compare(capi.entity.AllocationBatch o1, capi.entity.AllocationBatch o2) {
					// TODO Auto-generated method stub
					return o1.getId().compareTo(o2.getId());
				}
			});
			
			for(capi.entity.AllocationBatch ab : allocationBatches){
				capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch allocationBatch = 
						new capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch();
				
				allocationBatch.setEndDate(ab.getEndDate());
				allocationBatch.setStartDate(ab.getStartDate());
				allocationBatch.setNumberOfBatch(ab.getBatchName());
				allocationBatch.setEndDateStr(commonService.formatDate(ab.getEndDate()));
				allocationBatch.setStartDateStr(commonService.formatDate(ab.getStartDate()));
				allocationBatch.setId(ab.getId());
				allocationBatch.setAllocationBatchId(ab.getId());
				indexing.newBatchAllocationId = ab.getId();
				session.getSessionNewAllocationBatch().add(allocationBatch);
			}
			
			ArrayList<capi.entity.AssignmentAttribute> assignmentAttributes = new ArrayList<capi.entity.AssignmentAttribute>(entity.getAssignmentAttributes());
			Collections.sort(assignmentAttributes, new Comparator<capi.entity.AssignmentAttribute>(){
				@Override
				public int compare(AssignmentAttribute o1,
						AssignmentAttribute o2) {
					// TODO Auto-generated method stub
					return o1.getId().compareTo(o2.getId());
				}
			});
			int aaId = 0;
			for(capi.entity.AssignmentAttribute aa : assignmentAttributes ){
				capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes assignmentAttrModel = 
						new capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes();
				capi.entity.AllocationBatch ab = aa.getAllocationBatch();
				capi.entity.User u = aa.getUser();
				capi.entity.Batch b = aa.getBatch();
				assignmentAttrModel.setAssignmentAttributeId(aa.getId());
				assignmentAttrModel.setAllocationBatchRefId(ab != null ? "new-"+ab.getId() : null);
				assignmentAttrModel.setAllocationBatchId(ab != null ? ab.getId() : null);
				assignmentAttrModel.setBatchId(b.getId());
				assignmentAttrModel.setCategory(b.getBatchCategory());
				assignmentAttrModel.setEndDate(aa.getEndDate());
				assignmentAttrModel.setEndDateStr(commonService.formatDate(aa.getEndDate()));
				assignmentAttrModel.setOfficer(u);
				if(u != null){
					assignmentAttrModel.setOfficerIds(u.getId());
				}
				assignmentAttrModel.setReferenceId("new-"+aaId);
				assignmentAttrModel.setSelectedBatchType(b.getAssignmentType());
				assignmentAttrModel.setSession(aa.getSession());
				assignmentAttrModel.setStartDate(aa.getStartDate());
				assignmentAttrModel.setStartDateStr(commonService.formatDate(aa.getStartDate()));
				
				ArrayList<BatchCollectionDate> batchCollectionDates = new ArrayList<BatchCollectionDate>(aa.getBatchCollectionDates());
				Collections.sort(batchCollectionDates, new Comparator<capi.entity.BatchCollectionDate>(){
					@Override
					public int compare(BatchCollectionDate o1, BatchCollectionDate o2) {
						// TODO Auto-generated method stub
						return o1.getId().compareTo(o2.getId());
					}
					
				});
				
				BackTrackDateDisplayModel oldBackTrackDates = null;
				for(BackTrackDateDisplayModel btddmSession : backTrackDates){
					if(btddmSession.getBatchCode().equalsIgnoreCase(b.getCode())){
						oldBackTrackDates = btddmSession;
					}
				}
				if(oldBackTrackDates == null){
					oldBackTrackDates = new BackTrackDateDisplayModel();
					oldBackTrackDates.setBatchCode(b.getCode());
					oldBackTrackDates.setBatchId(b.getId());
					oldBackTrackDates.setBackTrackDateDisplayModelId(backTrackDateDisplayModelId);
					backTrackDateDisplayModelId++;
					backTrackDates.add(oldBackTrackDates);
					session.getBackTrackDateModelList().add(oldBackTrackDates);
				}
				
				assignmentAttrModel.setBackTrackDateDisplayModelId(oldBackTrackDates.getBackTrackDateDisplayModelId());
				
				//Get the max & min date in this batchCollectionDate
				for(BatchCollectionDate batchCollectionDate : batchCollectionDates){
					if(minCollectionDate == null || batchCollectionDate.getDate().before(minCollectionDate)){
						minCollectionDate = batchCollectionDate.getDate();
					}
					if(maxCollectionDate == null || batchCollectionDate.getDate().after(maxCollectionDate)){
						maxCollectionDate = batchCollectionDate.getDate();
					}
				}
				
				for(BatchCollectionDate batchCollectionDate : batchCollectionDates){
					BackTrackDate backTrackDate = new BackTrackDate();
					// Add Batch Collection date to AssinmentAttr
					
					if(assignmentAttrModel.getCollectionDatesStr().length() == 0){
						assignmentAttrModel.setCollectionDatesStr(commonService.formatDate(batchCollectionDate.getDate()));
					}else{
						assignmentAttrModel.setCollectionDatesStr(
								assignmentAttrModel.getCollectionDatesStr()+","+commonService.formatDate(batchCollectionDate.getDate()));
					}
					
					backTrackDate.setReferenceCollectionDate(batchCollectionDate.getDate());
					backTrackDate.setReferenceCollectionDateStr(commonService.formatDate(batchCollectionDate.getDate()));
					backTrackDate.setAssignmentAttributesReferenceId("new-"+aaId);
					backTrackDate.setBackTrackDateList(new ArrayList<Date>());
					if(batchCollectionDate.getBackTrackDate1() != null){
						backTrackDate.getBackTrackDateList().add(batchCollectionDate.getBackTrackDate1());
						backTrackDate.setBackTrackDateString(commonService.formatDate(batchCollectionDate.getBackTrackDate1()));
					}
					if(batchCollectionDate.getBackTrackDate2() != null){
						backTrackDate.getBackTrackDateList().add(batchCollectionDate.getBackTrackDate2());
						backTrackDate.setBackTrackDateString(backTrackDate.getBackTrackDateString()+","+commonService.formatDate(batchCollectionDate.getBackTrackDate2()));
					}
					if(batchCollectionDate.getBackTrackDate3() != null){
						backTrackDate.getBackTrackDateList().add(batchCollectionDate.getBackTrackDate3());
						backTrackDate.setBackTrackDateString(backTrackDate.getBackTrackDateString()+","+commonService.formatDate(batchCollectionDate.getBackTrackDate3()));
					}
					backTrackDate.setBatchCode(b.getCode());
					backTrackDate.setBatchId(b.getId());
					backTrackDate.setHasBackTrack(batchCollectionDate.isHasBackTrack());
					backTrackDate.setReferenceCollectionDate(batchCollectionDate.getDate());
					
					backTrackDate.setBackTrackDateAvailableFrom(entity.getStartDate());
					backTrackDate.setBackTrackDateAvailableFromString(commonService.formatDate(entity.getStartDate()));
					backTrackDate.setBackTrackDateAvailableTo(entity.getEndDate());
					backTrackDate.setBackTrackDateAvailableToString(commonService.formatDate(entity.getEndDate()));
					
					backTrackDate.setBatchCollectionDateId(batchCollectionDate.getId());
					oldBackTrackDates.getBackTrackDayList().add(backTrackDate);
				}
				indexing.newAlloactionAttributeId = aaId;
				if(b.getBatchCategory() != null && b.getBatchCategory().length() > 0){
					session.getSessionNewAssignmentAttr().add(assignmentAttrModel);	
				}else if(assignmentAttrModel.getReferenceId().length() > 0){
					//Insert uncategorized Assignment Attribute
					assignmentAttrModel.setReferenceId(assignmentAttrModel.getReferenceId().replace("new-", ""));
					for(BackTrackDateDisplayModel btddm : assignmentAttrModel.getCollectionDateList()){
						for(BackTrackDate backTrackDate: btddm.getBackTrackDayList()){
							backTrackDate.setAssignmentAttributesReferenceId(backTrackDate.getAssignmentAttributesReferenceId().replace("new-", ""));
						}
					}
					session.getSessionNonCateAssignmentAttr().add(assignmentAttrModel);
				}

				aaId++;
			}
			
			//Initial the page index validation tag
			indexing.tab1 = true;
			indexing.tab2 = true;
			indexing.tab3 = true;
			indexing.tab4 = true;
			
		}
		session.setBackTrackDateModelList(backTrackDates);
		session.setSessionSurveyMonth(sessionModel);
		indexing.newBatchAllocationId++;
		indexing.newAlloactionAttributeId++;
		indexing.newBackTrackDateDisplayModelId = backTrackDateDisplayModelId;
		indexing.readonly = session.getReadonly();
		indexing.isDraft = session.getIsDraft();
		return session;
	}
	
	@Transactional
	private void clearSurveyMonth(SurveyMonth surveyMonth){
		List<capi.entity.AllocationBatch> allocationBatchList = this.allocationBatchDao.findAllocationBatchsBySurveyMonthId(surveyMonth);
		
		List<PETopManagement> topManagementList = this.peTopManagementDao.getBySurveyMonth(surveyMonth.getSurveyMonthId());
		if (topManagementList != null && topManagementList.size() > 0){
			for(PETopManagement top : topManagementList){
				peTopManagementDao.delete(top);
			}
		}
		
		List<Integer> allocationBatchIds = new ArrayList<Integer>();
		for(capi.entity.AllocationBatch ab : allocationBatchList){
			allocationBatchIds.add(ab.getId());
		}
		
		List<AssignmentAdjustment> assignmentAdjList = 
				this.assignmentAdjDao.findAssignmentAdjustmentByAllocationBatchIds(allocationBatchIds);
		for(AssignmentAdjustment aa : assignmentAdjList){
			this.assignmentAdjDao.delete(aa);
		}
		
		List<DistrictHeadAdjustment> districtHeadAdjustmentList = 
				this.districtHeadAdjustmentDao.findDistrictHeadAdjustmentByAllocationBatchIds(allocationBatchIds);
		for(DistrictHeadAdjustment dha : districtHeadAdjustmentList){
			this.districtHeadAdjustmentDao.delete(dha);
		}
		
		List<AssignmentAttribute> assignmentAttrList = this.assignmentAttrDao.findAssignmentAttributeBySurveyMonth(surveyMonth);
		for(AssignmentAttribute aa : assignmentAttrList){
			Iterator<BatchCollectionDate> batchCollectionDateList = aa.getBatchCollectionDates().iterator();
			while(batchCollectionDateList.hasNext()){
				BatchCollectionDate bcd =  batchCollectionDateList.next();
				this.batchCollectionDateDao.delete(bcd);
			}
			
			this.assignmentAttrDao.delete(aa);
		}
		
		for(capi.entity.AllocationBatch ab : allocationBatchList){
			List<QuotationRecord> qrList = this.quotationRecordDao.getQuotationRecordsByAlloctationBatch(ab);
			for(QuotationRecord qr : qrList){
				this.quotationRecordDao.delete(qr);
			}
			
			this.allocationBatchDao.delete(ab);
		}
	}
	
	@Transactional
	public void deleteSurveyMonth(Integer SurveyMonthId){
		SurveyMonth sm = this.surveyMonthDao.findById(SurveyMonthId);
		
		if(sm != null){
			this.clearSurveyMonth(sm);
			this.surveyMonthDao.delete(sm);

			this.surveyMonthDao.flush();
			
		}
	}
	
	@Deprecated
	private List<Integer> generateSurveyMonthQuotationRecords(SurveyMonth sm){
		List<Integer> quotationRecordIds = new ArrayList<Integer>();
		
		List<AssignmentAttribute> assignmentAttributeList = new ArrayList<AssignmentAttribute>(sm.getAssignmentAttributes());
		for (AssignmentAttribute aa : assignmentAttributeList){
			Batch b = aa.getBatch();
			List<Quotation> quotationList = this.quotationDao.getAllActiveQuotationByBatch(b);
			
			for(Quotation q : quotationList){
				QuotationRecord baseQr = new QuotationRecord();
				if(q.getOutlet() == null){
					continue;
				}
				
				Unit unit = q.getUnit();
				SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
				Integer smMonth = Integer.parseInt(monthFormat.format(sm.getReferenceMonth()));
				
				// check seasonality
				Integer seasonStartMonth = 0; 
				Integer seasonEndMonth = 0; 
				switch(unit.getSeasonality()){
					case 2: //case summer
						seasonStartMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE).getValue());
						seasonEndMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_END_DATE).getValue());
						break;
					case 3: //case winter
						seasonStartMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE).getValue());
						seasonEndMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_END_DATE).getValue());
						break;
					case 4: //case custom
						seasonStartMonth = unit.getSeasonStartMonth();
						seasonEndMonth = unit.getSeasonEndMonth();
						break;
				}
				//check sm month = start month of seasonal item
				if(smMonth == seasonStartMonth){
					q.setFRApplied(false);
					q.setReturnGoods(false);
					q.setReturnNewGoods(false);
					q.setSeasonalWithdrawal(null);
					
					this.quotationDao.save(q);
				}
				
				if(unit.getSeasonality() != 1 && seasonStartMonth != 0 && seasonEndMonth != 0){
					if (!q.isFRApplied()){
						baseQr.setCollectFR(true);
					}
					
					if (seasonStartMonth <= seasonEndMonth){
						if (seasonStartMonth > smMonth || seasonEndMonth < smMonth){
							continue; // not in seasonality range
						}
					}
					else if (seasonStartMonth > seasonEndMonth){ // across year
						if (seasonStartMonth > smMonth && seasonEndMonth < smMonth){
							continue; // not in seasonality range
						}
					}
					
				}
				// end check sm month = start month;

				// end check seasonality
				
							
				//start check pricing freq
				PricingFrequency pf = unit.getPricingFrequency();
				if(pf != null){
					switch(smMonth){
						case 1:
							if(!pf.isJan()){
								continue; //skip generation;
							}
						break;
						case 2:
							if(!pf.isFeb()){
								continue; //skip generation;
							}
						break;
						case 3:
							if(!pf.isMar()){
								continue; //skip generation;
							}
						break;
						case 4:
							if(!pf.isApr()){
								continue; //skip generation;
							}
						break;
						case 5:
							if(!pf.isMay()){
								continue; //skip generation;
							}
						break;
						case 6:
							if(!pf.isJun()){
								continue; //skip generation;
							}
						break;
						case 7:
							if(!pf.isJul()){
								continue; //skip generation;
							}
						break;
						case 8:
							if(!pf.isAug()){
								continue; //skip generation;
							}
						break;
						case 9:
							if(!pf.isSep()){
								continue; //skip generation;
							}
						break;
						case 10:
							if(!pf.isOct()){
								continue; //skip generation;
							}
						break;
						case 11:
							if(!pf.isNov()){
								continue; //skip generation;
							}
						break;
						case 12:
							if(!pf.isDec()){
								continue; //skip generation;
							}
						break;
					}
				}
				//end check pricing freq
				
				//check batch assignment type
				switch(b.getAssignmentType()){
					case 1:
						List<BatchCollectionDate> bcdList = this.batchCollectionDateDao.findBatchCollectionDateByAssignmentAttribute(aa);//new ArrayList<BatchCollectionDate>(aa.getBatchCollectionDates());
						User user = aa.getUser();
						capi.entity.AllocationBatch ab = aa.getAllocationBatch();
						for( BatchCollectionDate bcd : bcdList){
							QuotationRecord qr = new QuotationRecord();
							qr.setQuotation(q);
							qr.setOutlet(q.getOutlet());
							qr.setCollectFR(baseQr.isCollectFR());
							qr.setAllocationBatch(ab);
							qr.setProduct(q.getProduct());
							qr.setUser(user);
							qr.setHistoryDate(bcd.getDate());
							qr.setReferenceDate(bcd.getDate());
							qr.setAssignedCollectionDate(bcd.getDate());
							qr.setCollectionDate(bcd.getDate());
							qr.setSpecifiedUser(true);
							qr.setFormDisplay(q.getUnit().getFormDisplay());
							qr.setQuotationState("Normal");
							qr.setStatus("Blank");
							qr.setAvailability(1);
							this.quotationRecordDao.save(qr);
							quotationRecordIds.add(qr.getId());
							
							if(bcd.getBackTrackDate1() != null){
								QuotationRecord qrbtd1 = new QuotationRecord();
								qrbtd1.setQuotation(q);
								qrbtd1.setOutlet(q.getOutlet());
								qrbtd1.setProduct(q.getProduct());
								qrbtd1.setCollectFR(baseQr.isCollectFR());
								qrbtd1.setAllocationBatch(ab);
								qrbtd1.setUser(user);
								qrbtd1.setHistoryDate(bcd.getBackTrackDate1());
								qrbtd1.setReferenceDate(bcd.getBackTrackDate1());
								qrbtd1.setAssignedCollectionDate(bcd.getBackTrackDate1());
								qrbtd1.setCollectionDate(bcd.getBackTrackDate1());
								qrbtd1.setSpecifiedUser(true);
								qrbtd1.setBackTrack(true);
								qrbtd1.setOriginalQuotationRecord(qr);
								qrbtd1.setFormDisplay(q.getUnit().getFormDisplay());
								qrbtd1.setQuotationState("Normal");
								qrbtd1.setStatus("Blank");
								qrbtd1.setAvailability(1);
								this.quotationRecordDao.save(qrbtd1);
								quotationRecordIds.add(qrbtd1.getId());
							}
							if(bcd.getBackTrackDate2() != null){
								QuotationRecord qrbtd2 = new QuotationRecord();
								qrbtd2.setQuotation(q);
								qrbtd2.setOutlet(q.getOutlet());
								qrbtd2.setCollectFR(baseQr.isCollectFR());
								qrbtd2.setAllocationBatch(ab);
								qrbtd2.setProduct(q.getProduct());
								qrbtd2.setUser(user);
								qrbtd2.setHistoryDate(bcd.getBackTrackDate2());
								qrbtd2.setReferenceDate(bcd.getBackTrackDate2());
								qrbtd2.setAssignedCollectionDate(bcd.getBackTrackDate2());
								qrbtd2.setCollectionDate(bcd.getBackTrackDate2());
								qrbtd2.setSpecifiedUser(true);
								qrbtd2.setBackTrack(true);
								qrbtd2.setOriginalQuotationRecord(qr);
								qrbtd2.setFormDisplay(q.getUnit().getFormDisplay());
								qrbtd2.setQuotationState("Normal");
								qrbtd2.setStatus("Blank");
								qrbtd2.setAvailability(1);
								this.quotationRecordDao.save(qrbtd2);
								quotationRecordIds.add(qrbtd2.getId());
							}
							if(bcd.getBackTrackDate3() != null){
								QuotationRecord qrbtd3 = new QuotationRecord();
								qrbtd3.setQuotation(q);
								qrbtd3.setOutlet(q.getOutlet());
								qrbtd3.setCollectFR(baseQr.isCollectFR());
								qrbtd3.setAllocationBatch(ab);
								qrbtd3.setProduct(q.getProduct());
								qrbtd3.setUser(user);
								qrbtd3.setHistoryDate(bcd.getBackTrackDate3());
								qrbtd3.setReferenceDate(bcd.getBackTrackDate3());
								qrbtd3.setAssignedCollectionDate(bcd.getBackTrackDate3());
								qrbtd3.setCollectionDate(bcd.getBackTrackDate3());
								qrbtd3.setSpecifiedUser(true);
								qrbtd3.setBackTrack(true);
								qrbtd3.setOriginalQuotationRecord(qr);
								qrbtd3.setFormDisplay(q.getUnit().getFormDisplay());
								qrbtd3.setQuotationState("Normal");
								qrbtd3.setStatus("Blank");
								qrbtd3.setAvailability(1);
								this.quotationRecordDao.save(qrbtd3);
								quotationRecordIds.add(qrbtd3.getId());
							}
						}
						break;
					case 2:
						ab = aa.getAllocationBatch();
						user = aa.getUser();
						baseQr.setQuotation(q);
						baseQr.setOutlet(q.getOutlet());
						baseQr.setAllocationBatch(ab);
						baseQr.setProduct(q.getProduct());
						baseQr.setUser(user);
						baseQr.setAssignedStartDate(aa.getStartDate());
						baseQr.setAssignedEndDate(aa.getEndDate());
						baseQr.setHistoryDate(aa.getStartDate());
						baseQr.setSpecifiedUser(true);
						baseQr.setFormDisplay(q.getUnit().getFormDisplay());
						baseQr.setQuotationState("Normal");
						baseQr.setStatus("Blank");
						baseQr.setAvailability(1);
						this.quotationRecordDao.save(baseQr);
						quotationRecordIds.add(baseQr.getId());
						break;
					case 3:
						ab = aa.getAllocationBatch();
						baseQr.setQuotation(q);
						baseQr.setOutlet(q.getOutlet());
						baseQr.setProduct(q.getProduct());
						baseQr.setAllocationBatch(ab);
						baseQr.setAssignedStartDate(aa.getStartDate());
						baseQr.setAssignedEndDate(aa.getEndDate());
						baseQr.setHistoryDate(aa.getStartDate());
						baseQr.setFormDisplay(q.getUnit().getFormDisplay());
						baseQr.setQuotationState("Normal");
						baseQr.setStatus("Blank");
						baseQr.setAvailability(1);
						this.quotationRecordDao.save(baseQr);
						quotationRecordIds.add(baseQr.getId());
						break;	
				}
			}
		}
		return quotationRecordIds;
	}
	@Deprecated
	private List<Assignment> groupQuotationRecordToAssignment(List<Integer> quotationRecordIds, SurveyMonth sm){
		List<Assignment> assignmentList = new ArrayList<Assignment>();
		
		List<QuotationRecordGroupingModel> groupedQrList = this.quotationRecordDao.groupQuotationRecord(quotationRecordIds);
		
		for(QuotationRecordGroupingModel qrgm : groupedQrList){
			Assignment a = new Assignment();
			Outlet o = qrgm.getOutlet();
			a.setUser(qrgm.getUser());
			a.setCollectionDate(qrgm.getCollectionDate());
			a.setStartDate(qrgm.getAssignedStartDate());
			a.setEndDate(qrgm.getAssignedEndDate());
			a.setOutlet(o);
			a.setAssignedUser(qrgm.getUser());
			a.setAssignedCollectionDate(qrgm.getCollectionDate());
			a.setStatus(1);
			a.setSurveyMonth(sm);
			if(o.getCollectionMethod() != null)
				a.setCollectionMethod(o.getCollectionMethod());
			
			String referenceNo = "";
			try{
				Tpu tpu = o.getTpu();
				District d = tpu.getDistrict();
				referenceNo = d.getCode() + o.getBrCode();
			}catch(Exception e){
				
			}
			a.setReferenceNo(referenceNo);
			
			List<QuotationRecord> relatedQuotationRecordList = 
					this.quotationRecordDao.findQuotationRecordByGroupedResult(qrgm.getCollectionDate(),
							qrgm.getAssignedStartDate(), qrgm.getAssignedEndDate(), qrgm.getOutlet(), qrgm.getUser(), quotationRecordIds);

			a.setQuotationRecords(relatedQuotationRecordList.isEmpty() ? new HashSet<QuotationRecord>() : new HashSet<QuotationRecord>( relatedQuotationRecordList));
			
			for(QuotationRecord qr : relatedQuotationRecordList){
				qr.setAssignment(a);
				this.quotationRecordDao.save(qr);
			}
			
			this.assignmentDao.save(a);
			assignmentList.add(a);
		}
		
		return assignmentList;
	}
	@Deprecated
	private List<PECheckTask> generatePeCheckTask(List<Integer> quotationRecordIds, SurveyMonth sm){
		List<PECheckTask> peCheckTaskList = new ArrayList<PECheckTask>();
		
		List<QuotationRecord> qrList = this.quotationRecordDao.getQuotationRecordsByIds(quotationRecordIds);
		
		List<OutletQuotationModel> oqList = new ArrayList<OutletQuotationModel>();
		
		for(QuotationRecord qr : qrList){
			Quotation q = qr.getQuotation();
			Assignment a = qr.getAssignment();
			Unit u = q.getUnit();
			
			//skip exclude outlet type by short code;
			String code = u.getSubItem().getOutletType().getCode();
			code = code.substring(code.length()-4, code.length()-1);
			
			List<PEExcludedOutletType> excludedList = this.pEExcludedOutletTypeDao.findAll();
			
			Boolean skipQr = false; 
			for(PEExcludedOutletType peeot : excludedList ){
				if(peeot.getOutletType().getShortCode().equalsIgnoreCase(code)){
					skipQr = true;
					break;
				}
			}
			if(skipQr){
				continue; //skip
			}
			// end skip
			
			//skip not included list;
			List<Purpose> includedPurposeList = this.purposeDao.findAll();
			if(includedPurposeList.contains(u.getPurpose()) == false){
				continue; //skip
			}
			//end skip
			
			//group all outlet that matched the variety (quotation -> unit) 
			List<PECheckUnitCriteria> pecucList = this.pECheckUnitCriteriaDao.findAll();
			for(PECheckUnitCriteria pecuc : pecucList){
				if(pecuc.getUnit().equals(u)){
					OutletQuotationModel workingOutletQuotationModel = new OutletQuotationModel();
					workingOutletQuotationModel.setOutlet(a.getOutlet());
					workingOutletQuotationModel.setPecuc(pecuc);
					for(OutletQuotationModel oq : oqList){
						if(oq.getOutlet().equals(a.getOutlet()) && oq.getPecuc().equals(pecuc)){
							workingOutletQuotationModel = oq;
							break;
						}
					}
					workingOutletQuotationModel.getQuotations().add(q);
					oqList.add(workingOutletQuotationModel);
					//ogm.setPecuc(pecuc);
				}
			}
		}
		
		List<QuotationRecord> qrs = null;
		List<Assignment> aList = new ArrayList<Assignment>();
		for(OutletQuotationModel o : oqList){
			qrs = new ArrayList<QuotationRecord>();
			Date currentReferenceMonth = sm.getReferenceMonth();
			PECheckUnitCriteria pecuc = o.getPecuc();
			
			Calendar c = Calendar.getInstance(); 
			c.setTime(currentReferenceMonth); 
			c.add(Calendar.MONTH, -1);
			Date workingReferenceMonth = c.getTime();
			Date finalReferenceMonth = workingReferenceMonth;
			
			Integer actualLookBackMonth = pecuc.getNoOfMonth();
			Integer countOfMatchPr = 0;
			Integer totalCount = 0;
			for(int i = 0; i < actualLookBackMonth; i++){
				c.setTime(finalReferenceMonth);
				c.add(Calendar.MONTH, -1);
				finalReferenceMonth = c.getTime();
				
				List<Quotation> workingQs = o.getQuotations();
				
				for(Quotation q : workingQs){
					List<QuotationRecord> allQrs = new ArrayList<QuotationRecord>(q.getQuotationRecords());
					
					for(QuotationRecord qr : allQrs){
						c.setTime(finalReferenceMonth);
						c.add(Calendar.MONTH, -1);
						finalReferenceMonth = c.getTime();
						
						SurveyMonth checkingSm = this.surveyMonthDao.getSurveyMonthByReferenceMonth(finalReferenceMonth);
						if(checkingSm != null){
							Date checkingEndDate = checkingSm.getEndDate();
							Date checkingStartDate = checkingSm.getStartDate();
							
							if(qr.getCollectionDate() != null){
								if(qr.getCollectionDate().compareTo(checkingStartDate) >= 0 && qr.getCollectionDate().compareTo(checkingEndDate) <= 0){
									if(qr.getIndoorQuotationRecord() != null){
										if(qr.getIndoorQuotationRecord().getCurrentSPrice() != null){
											if(!qrs.contains(qr)){
												qrs.add(qr);
											}
										}
									}
								}
							}else if(qr.getAssignedStartDate() != null && qr.getAssignedEndDate() != null){
								if(qr.getAssignedStartDate().compareTo(checkingStartDate) >= 0 && qr.getAssignedEndDate().compareTo(checkingEndDate) <= 0){
									if(qr.getIndoorQuotationRecord() != null){
										if(qr.getIndoorQuotationRecord().getCurrentSPrice() != null){
											if(!qrs.contains(qr)){
												qrs.add(qr);
											}
										}
									}
								}
							}
						}
					}
				}
				
				totalCount = qrs.size();
				for(QuotationRecord qr : qrs){
					IndoorQuotationRecord iqr = qr.getIndoorQuotationRecord();
					Double cqr = iqr.getCurrentSPrice() / iqr.getLastSPrice() * 100;
					
					String operator = pecuc.getPrSymbol();
					Double settingPr = pecuc.getPrValue();
					
					switch (operator){
						case ">":
							if(cqr <= settingPr){
								countOfMatchPr++;
							}
							break;
						case "<":
							if(cqr >= settingPr){
								countOfMatchPr++;
							}
							break;
						case ">=":
							if(cqr < settingPr){
								countOfMatchPr++;
							}
							break;
						case "<=":
							if(cqr > settingPr){
								countOfMatchPr++;
							}
							break;
						case "=":
							if(cqr != settingPr){
								countOfMatchPr++;
							}
							break;
					}
					
				}
				Double finalPercentageQuotation = (double) 0;
				if(totalCount != 0)
					finalPercentageQuotation = (double) (countOfMatchPr / totalCount * 100);
				
				if(finalPercentageQuotation > pecuc.getPrValue()){
					for(Quotation q : o.getQuotations()){
						Iterator<QuotationRecord> qList = q.getQuotationRecords().iterator();
							
						while(qList.hasNext()){
							Assignment a = qList.next().getAssignment();
							if(!aList.contains(a)){
								aList.add(a);
							}
						}
					}
				}
			}
			
			for(Assignment a: aList){
				PECheckTask pect = new PECheckTask();
				pect.setAssignment(a);
				pect.setSurveyMonth(sm);
				pect.setRandomCase(false);
				pect.setCertaintyCase(true);
				this.pECheckTaskDao.save(pect);
				peCheckTaskList.add(pect);
			}
		}
		
		return peCheckTaskList;
	}
	
	@Transactional
	private List<QuotationRecord> quotationRecordGeneration(SurveyMonth sm){
		
		List<AssignmentAttribute> attrs = assignmentAttrDao.findAssignmentAttributeWithDependency(sm);
		Integer summerStartMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE).getValue());
		Integer summerEndMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_END_DATE).getValue());
		Integer winterStartMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE).getValue());
		Integer winterEndMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_END_DATE).getValue());
		List<QuotationRecord> quotationRecords = new ArrayList<QuotationRecord>();
		
		for (AssignmentAttribute attr: attrs){
			Batch batch = attr.getBatch();
			List<Quotation> quotationList = this.quotationDao.getAllActiveQuotationWithDependency(batch);
			List<BatchCollectionDate> bcdList = this.batchCollectionDateDao.findBatchCollectionDateByAssignmentAttribute(attr);//attr.getBatchCollectionDates();
			
			for(Quotation q : quotationList){
				QuotationRecord baseQr = new QuotationRecord();
				if(q.getOutlet() == null){
					continue;
				}
				
				Unit unit = q.getUnit();
				SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
				Integer smMonth = Integer.parseInt(monthFormat.format(sm.getReferenceMonth()));
				
				// check seasonality
				Integer seasonStartMonth = 0; 
				Integer seasonEndMonth = 0; 
				switch(unit.getSeasonality()){
					case 2: //case summer
						seasonStartMonth = summerStartMonth;
						seasonEndMonth = summerEndMonth;
						break;
					case 3: //case winter
						seasonStartMonth = winterStartMonth;
						seasonEndMonth = winterEndMonth;
						break;
					case 4: //case custom
						seasonStartMonth = unit.getSeasonStartMonth();
						seasonEndMonth = unit.getSeasonEndMonth();
						break;
				}
//				//check sm month = start month of seasonal item
//				if(smMonth == seasonStartMonth){
//					q.setLastSeasonReturnGoods(q.isReturnGoods() || q.isReturnNewGoods());
//					q.setFRApplied(false);
//					q.setReturnGoods(false);
//					q.setReturnNewGoods(false);
//					q.setSeasonalWithdrawal(null);
//					q.setFrAdmin(null);
//					q.setFrField(null);
//					q.setIsUseFRAdmin(null);
//					q.setTempFRPercentage(null);
//					q.setTempFRValue(null);
//					q.setTempIsFRApplied(false);
//					q.setTempIsReturnGoods(false);
//					q.setTempIsReturnNewGoods(false);
//					q.setTempIsUseFRAdmin(null);
//					q.setTempKeepNoMonth(q.getKeepNoMonth());
//					q.setTempLastFRAppliedDate(q.getLastFRAppliedDate());
//					
//					q.setByPassLog(true);					
//					this.quotationDao.save(q);
//				}
				
				if(unit.getSeasonality() != 1 && seasonStartMonth != 0 && seasonEndMonth != 0){
//					if (!q.isFRApplied()){
//						baseQr.setCollectFR(true);
//					}
					
					if (seasonStartMonth <= seasonEndMonth){
						if (seasonStartMonth > smMonth || seasonEndMonth < smMonth){
							continue; // not in seasonality range
						}
					}
					else if (seasonStartMonth > seasonEndMonth){ // across year
						if (seasonStartMonth > smMonth && seasonEndMonth < smMonth){
							continue; // not in seasonality range
						}
					}
					
				}
				
				// Request By User only Seasonality 2 or 3
				// seasonal withdrawal
				if (q.getSeasonalWithdrawal() != null && (unit.getSeasonality()==2 || unit.getSeasonality()==3)){
					continue;
				}
				
				// set is collect FR by checking isFRApplied and isFRRequired
				if (unit.isFrRequired() && !q.isFRApplied()){
					baseQr.setCollectFR(true);
				}
				
				// end check sm month = start month;

				// end check seasonality
				
							
				//start check pricing freq
				PricingFrequency pf = unit.getPricingFrequency();
				if(pf != null){
					switch(smMonth){
						case 1:
							if(!pf.isJan()){
								continue; //skip generation;
							}
						break;
						case 2:
							if(!pf.isFeb()){
								continue; //skip generation;
							}
						break;
						case 3:
							if(!pf.isMar()){
								continue; //skip generation;
							}
						break;
						case 4:
							if(!pf.isApr()){
								continue; //skip generation;
							}
						break;
						case 5:
							if(!pf.isMay()){
								continue; //skip generation;
							}
						break;
						case 6:
							if(!pf.isJun()){
								continue; //skip generation;
							}
						break;
						case 7:
							if(!pf.isJul()){
								continue; //skip generation;
							}
						break;
						case 8:
							if(!pf.isAug()){
								continue; //skip generation;
							}
						break;
						case 9:
							if(!pf.isSep()){
								continue; //skip generation;
							}
						break;
						case 10:
							if(!pf.isOct()){
								continue; //skip generation;
							}
						break;
						case 11:
							if(!pf.isNov()){
								continue; //skip generation;
							}
						break;
						case 12:
							if(!pf.isDec()){
								continue; //skip generation;
							}
						break;
					}
				}
				//end check pricing freq
				
				//check batch assignment type
				switch(batch.getAssignmentType()){
					case 1:					
						//System.out.println("collection date size: "+bcdList.size());
						User user = attr.getUser();
						capi.entity.AllocationBatch ab = attr.getAllocationBatch();
						for( BatchCollectionDate bcd : bcdList){
							QuotationRecord qr = new QuotationRecord();
							qr.setQuotation(q);
							qr.setOutlet(q.getOutlet());
							qr.setCollectFR(baseQr.isCollectFR());
							qr.setAllocationBatch(ab);
							qr.setProduct(q.getProduct());
							qr.setUser(user);
							qr.setHistoryDate(bcd.getDate());
							qr.setReferenceDate(bcd.getDate());
							qr.setAssignedCollectionDate(bcd.getDate());
							qr.setCollectionDate(bcd.getDate());
							qr.setSpecifiedUser(true);
							qr.setFormDisplay(q.getUnit().getFormDisplay());
							qr.setQuotationState("Normal");
							qr.setStatus("Blank");
							qr.setAvailability(1);
							qr.setFirmStatus(1);
							//this.quotationRecordDao.save(qr);
							quotationRecords.add(qr);
							
							if(bcd.getBackTrackDate1() != null){
								QuotationRecord qrbtd1 = new QuotationRecord();
								qrbtd1.setQuotation(q);
								qrbtd1.setOutlet(q.getOutlet());
								qrbtd1.setProduct(q.getProduct());
								qrbtd1.setCollectFR(baseQr.isCollectFR());
								qrbtd1.setAllocationBatch(ab);
								qrbtd1.setUser(user);
								qrbtd1.setHistoryDate(bcd.getBackTrackDate1());
								qrbtd1.setReferenceDate(bcd.getBackTrackDate1());
								qrbtd1.setAssignedCollectionDate(bcd.getBackTrackDate1());
								qrbtd1.setCollectionDate(bcd.getBackTrackDate1());
								qrbtd1.setSpecifiedUser(true);
								qrbtd1.setBackTrack(true);
								qrbtd1.setOriginalQuotationRecord(qr);
								qrbtd1.setFormDisplay(q.getUnit().getFormDisplay());
								qrbtd1.setQuotationState("Normal");
								qrbtd1.setStatus("Blank");
								qrbtd1.setAvailability(1);
								qrbtd1.setFirmStatus(1);
								
								qr.getOtherQuotationRecords().add(qrbtd1);
								//this.quotationRecordDao.save(qrbtd1);
								//quotationRecords.add(qrbtd1);
							}
							if(bcd.getBackTrackDate2() != null){
								QuotationRecord qrbtd2 = new QuotationRecord();
								qrbtd2.setQuotation(q);
								qrbtd2.setOutlet(q.getOutlet());
								qrbtd2.setCollectFR(baseQr.isCollectFR());
								qrbtd2.setAllocationBatch(ab);
								qrbtd2.setProduct(q.getProduct());
								qrbtd2.setUser(user);
								qrbtd2.setHistoryDate(bcd.getBackTrackDate2());
								qrbtd2.setReferenceDate(bcd.getBackTrackDate2());
								qrbtd2.setAssignedCollectionDate(bcd.getBackTrackDate2());
								qrbtd2.setCollectionDate(bcd.getBackTrackDate2());
								qrbtd2.setSpecifiedUser(true);
								qrbtd2.setBackTrack(true);
								qrbtd2.setOriginalQuotationRecord(qr);
								qrbtd2.setFormDisplay(q.getUnit().getFormDisplay());
								qrbtd2.setQuotationState("Normal");
								qrbtd2.setStatus("Blank");
								qrbtd2.setAvailability(1);
								qrbtd2.setFirmStatus(1);
								
								qr.getOtherQuotationRecords().add(qrbtd2);
								//this.quotationRecordDao.save(qrbtd2);
								//quotationRecords.add(qrbtd2);
							}
							if(bcd.getBackTrackDate3() != null){
								QuotationRecord qrbtd3 = new QuotationRecord();
								qrbtd3.setQuotation(q);
								qrbtd3.setOutlet(q.getOutlet());
								qrbtd3.setCollectFR(baseQr.isCollectFR());
								qrbtd3.setAllocationBatch(ab);
								qrbtd3.setProduct(q.getProduct());
								qrbtd3.setUser(user);
								qrbtd3.setHistoryDate(bcd.getBackTrackDate3());
								qrbtd3.setReferenceDate(bcd.getBackTrackDate3());
								qrbtd3.setAssignedCollectionDate(bcd.getBackTrackDate3());
								qrbtd3.setCollectionDate(bcd.getBackTrackDate3());
								qrbtd3.setSpecifiedUser(true);
								qrbtd3.setBackTrack(true);
								qrbtd3.setOriginalQuotationRecord(qr);
								qrbtd3.setFormDisplay(q.getUnit().getFormDisplay());
								qrbtd3.setQuotationState("Normal");
								qrbtd3.setStatus("Blank");
								qrbtd3.setAvailability(1);
								qrbtd3.setFirmStatus(1);
								
								qr.getOtherQuotationRecords().add(qrbtd3);
								//this.quotationRecordDao.save(qrbtd3);
								//quotationRecords.add(qrbtd3);
							}
						}
						break;
					case 2:
						ab = attr.getAllocationBatch();
						user = attr.getUser();
						baseQr.setQuotation(q);
						baseQr.setOutlet(q.getOutlet());
						baseQr.setAllocationBatch(ab);
						baseQr.setProduct(q.getProduct());
						baseQr.setUser(user);
						baseQr.setReferenceDate(attr.getStartDate());
						baseQr.setAssignedStartDate(attr.getStartDate());
						baseQr.setAssignedEndDate(attr.getEndDate());
						baseQr.setHistoryDate(attr.getStartDate());
						baseQr.setSpecifiedUser(true);
						baseQr.setFormDisplay(q.getUnit().getFormDisplay());
						baseQr.setQuotationState("Normal");
						baseQr.setStatus("Blank");
						baseQr.setAvailability(1);
						baseQr.setFirmStatus(1);
						//this.quotationRecordDao.save(baseQr);
						quotationRecords.add(baseQr);
						break;
					case 3:
						ab = attr.getAllocationBatch();
						baseQr.setQuotation(q);
						baseQr.setOutlet(q.getOutlet());
						baseQr.setProduct(q.getProduct());
						baseQr.setAllocationBatch(ab);
						baseQr.setReferenceDate(attr.getStartDate());
						baseQr.setAssignedStartDate(attr.getStartDate());
						baseQr.setAssignedEndDate(attr.getEndDate());
						baseQr.setHistoryDate(attr.getStartDate());
						baseQr.setFormDisplay(q.getUnit().getFormDisplay());
						baseQr.setQuotationState("Normal");
						baseQr.setStatus("Blank");
						baseQr.setAvailability(1);
						baseQr.setFirmStatus(1);
						//this.quotationRecordDao.save(baseQr);
						quotationRecords.add(baseQr);
						break;	
				}
			}
		}
		return quotationRecords;
	}
	
	@Transactional
	public void assignmentGeneration(SurveyMonth sm, List<QuotationRecord> records){				
		Hashtable<QuotationRecordGroupingModel, List<QuotationRecord>> lookup = new Hashtable<QuotationRecordGroupingModel, List<QuotationRecord>>();
		for (QuotationRecord qr : records){
			QuotationRecordGroupingModel model = new QuotationRecordGroupingModel();
			model.setAssignedEndDate(qr.getAssignedEndDate());
			model.setAssignedStartDate(qr.getAssignedStartDate());
			model.setCollectionDate(qr.getAssignedCollectionDate());
			model.setOutlet(qr.getOutlet());
			model.setUser(qr.getUser());
			if (lookup.containsKey(model)){
				List<QuotationRecord> qrs = lookup.get(model);
				QuotationRecord other = qrs.get(0);
				qr.setAssignment(other.getAssignment());
				qrs.add(qr);

				qr.setByPassLog(true);
				this.quotationRecordDao.save(qr);
				
				if (qr.getOtherQuotationRecords() != null && qr.getOtherQuotationRecords().size() > 0){
					for (QuotationRecord backTrack : qr.getOtherQuotationRecords()){
						backTrack.setAssignment(other.getAssignment());
						qrs.add(backTrack);
						
						backTrack.setByPassLog(true);
						this.quotationRecordDao.save(backTrack);
					}
				}
				
			}
			else{
				List<QuotationRecord> qrs = new ArrayList<QuotationRecord>();
					
				Assignment assignment = new Assignment();
				Outlet outlet = qr.getOutlet();
				assignment.setUser(qr.getUser());
				assignment.setCollectionDate(qr.getCollectionDate());
				assignment.setStartDate(qr.getAssignedStartDate());
				assignment.setEndDate(qr.getAssignedEndDate());
				assignment.setOutlet(outlet);
				assignment.setAssignedUser(qr.getUser());
				assignment.setAssignedCollectionDate(qr.getCollectionDate());
				assignment.setStatus(1);
				assignment.setSurveyMonth(sm);
				if(outlet.getCollectionMethod() != null)
					assignment.setCollectionMethod(outlet.getCollectionMethod());
				
				Tpu tpu = outlet.getTpu();
				District d = tpu.getDistrict();
				String referenceNo = d.getCode() + outlet.getFirmCode();
				assignment.setReferenceNo(referenceNo);
				qr.setAssignment(assignment);
				
				assignment.setByPassLog(true);				
				assignmentDao.save(assignment);

				qr.setByPassLog(true);
				this.quotationRecordDao.save(qr);

				if (qr.getOtherQuotationRecords() != null && qr.getOtherQuotationRecords().size() > 0){
					for (QuotationRecord backTrack : qr.getOtherQuotationRecords()){
						backTrack.setAssignment(assignment);
						qrs.add(backTrack);
						backTrack.setByPassLog(true);
						this.quotationRecordDao.save(backTrack);
					}
				}
				
				
				qrs.add(qr);
				lookup.put(model, qrs);		
			}			
		}
		//assignmentDao.flushAndClearCache();
	}
	
	@Transactional
	private void peCheckTaskGeneration(SurveyMonth sm, List<QuotationRecord> quotationRecords){
		List<PECheckTask> peCheckTaskList = new ArrayList<PECheckTask>();
		
		List<PEExcludedOutletType> excludedList = this.pEExcludedOutletTypeDao.getPEExcludedOutletType();
		List<Purpose> includedPurposeList = this.purposeDao.getPEIncludedPurpose();
		
		List<Integer> excludedOutletIds = new ArrayList<Integer>();
		SystemConfiguration sc = this.sysConfigDao.findByName(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH);
		if(sc.getValue()!=null && !StringUtils.isEmpty(sc.getValue())){
			Date excludeMonth = DateUtils.addMonths(sm.getReferenceMonth(), Integer.valueOf(sc.getValue())*-1);
			excludedOutletIds = this.pECheckTaskDao.getExcludedPEOutletId(excludeMonth);
		}
		
		Collection<String> outletTypeCodes = CollectionUtils.collect(excludedList, new Transformer<PEExcludedOutletType, String>(){
			@Override
			public String transform(PEExcludedOutletType input) {
				// TODO Auto-generated method stub
				return input.getOutletType().getShortCode();
			}
			
		});
		
		
		// map outlet unit into quotation ids
//		Hashtable<UnitOutletMapModel, List<Integer>> unitOutletMap = new Hashtable<UnitOutletMapModel, List<Integer>>();
		Hashtable<Integer, List<Integer>> outletMap = new Hashtable<Integer, List<Integer>>();
		Hashtable<Integer, List<Assignment>> outletAssignmentMap = new Hashtable<Integer, List<Assignment>>();
//		Set<Integer> outletIds = new HashSet<Integer>();
		//Set<Integer> quotationIds = new HashSet<Integer>();
		
		for(QuotationRecord qr : quotationRecords){
			Quotation q = qr.getQuotation();
			//quotationIds.add(q.getQuotationId());
			//Assignment a = qr.getAssignment();
			Unit unit = q.getUnit();
			
			//skip exclude outlet type by short code;
			String code = unit.getSubItem().getOutletType().getCode();
			//code = code.substring(code.length()-4, code.length()-1);
			code = code.substring(code.length()-3);
			
			Boolean skipQr = outletTypeCodes.contains(code);
			
//			Boolean skipQr = false; 
//			for(PEExcludedOutletType peeot : excludedList ){
//				if(peeot.getOutletType().getShortCode().equalsIgnoreCase(code)){
//					skipQr = true;
//					break;
//				}
//			}

			if(skipQr){
				continue; //skip
			}
			// end skip

			//skip not included list;
			if(includedPurposeList.contains(unit.getPurpose()) == false){
				continue; //skip
			}
			//end skip			

			//skip exclude Outlet
			if(excludedOutletIds.contains(qr.getOutlet().getOutletId())){
				continue; //skip
			}
			//end skip
						
			// map outlet to list of quotation
			if (outletMap.containsKey(qr.getOutlet().getOutletId())){
				List<Integer> quotationIds = outletMap.get(qr.getOutlet().getOutletId());
				if (!quotationIds.contains(q.getQuotationId())){
					quotationIds.add(q.getQuotationId());
				}
			}
			else{
				List<Integer> quotationIds = new ArrayList<Integer>();
				quotationIds.add(q.getQuotationId());
				outletMap.put(qr.getOutlet().getOutletId(), quotationIds);
			}
			
			// map outlet to assignments
			if (outletAssignmentMap.containsKey(qr.getOutlet().getOutletId())){
				List<Assignment> assignments = outletAssignmentMap.get(qr.getOutlet().getOutletId());
				if (!assignments.contains(qr.getAssignment())){
					assignments.add(qr.getAssignment());
				}
			}
			else{
				List<Assignment> assignments = new ArrayList<Assignment>();
				assignments.add(qr.getAssignment());
				outletAssignmentMap.put(qr.getOutlet().getOutletId(), assignments);
			}
		}
		

		List<PECheckUnitCriteria> pecucList = this.pECheckUnitCriteriaDao.findAll();
		List<Assignment> matchedAssignments = new ArrayList<Assignment>();
		for (PECheckUnitCriteria criteria : pecucList){
			Date currentReferenceMonth = sm.getReferenceMonth();
			Date finalCountMonth = DateUtils.addMonths(currentReferenceMonth, -2);
			Date startCountMonth = DateUtils.addMonths(finalCountMonth, criteria.getNoOfMonth() * -1 + 1);
			Collection<Integer> itemIds = CollectionUtils.collect(criteria.getItems(), new Transformer<Item, Integer>(){
				@Override
				public Integer transform(Item input) {
					// TODO Auto-generated method stub
					return input.getItemId();
				}
				
			});
						
			List<MatchedPEQuotationRecordResult> results =  indoorQuotationRecordDao.getMatchedPEQuotationRecordResult(startCountMonth, finalCountMonth,
					new ArrayList<Integer>(itemIds), criteria.getPrSymbol(), criteria.getPrValue(), outletTypeCodes);
			
						
			for (Integer outletId : outletMap.keySet()){
				Collection<MatchedPEQuotationRecordResult> filteredResults = filterPEQuotationRecordResults(results,outletId,outletMap.get(outletId));
				
				long matchedCount = 0;
				long totalCount = 0;
				for (MatchedPEQuotationRecordResult result : filteredResults){
					matchedCount += result.getMatched();
					totalCount += result.getTotal();
				}				
				
				
				if (totalCount > 0 && (double)matchedCount/totalCount*100 > criteria.getQuotationPercentage()){
					Collection<Assignment> newAssignments = CollectionUtils.subtract(outletAssignmentMap.get(outletId),matchedAssignments);
					matchedAssignments.addAll(newAssignments);
				}
				
			}
			
		}		
		
		
		for(Assignment a: matchedAssignments){
			PECheckTask pect = new PECheckTask();
			pect.setAssignment(a);
			pect.setSurveyMonth(sm);
			pect.setRandomCase(false);
			pect.setCertaintyCase(true);
			
			pect.setByPassLog(true);
			this.pECheckTaskDao.save(pect);
			peCheckTaskList.add(pect);
		}
		
		//this.pECheckTaskDao.flushAndClearCache();
	}
	
	private Collection<MatchedPEQuotationRecordResult> filterPEQuotationRecordResults(List<MatchedPEQuotationRecordResult> results, final Integer outletId, final List<Integer> quotations){
		return CollectionUtils.select(results, new Predicate<MatchedPEQuotationRecordResult>(){
			@Override
			public boolean evaluate(MatchedPEQuotationRecordResult result) {
				// TODO Auto-generated method stub
				return outletId != null && outletId.equals(result.getOutletId()) && quotations.contains(result.getQuotationId());
			}					
		});
	}
	
	
	@Transactional
	public void generateSurveyMonthDetails(Integer surveyMonthId){
		SurveyMonth sm = this.surveyMonthDao.findById(surveyMonthId);
//		List<Integer> quotationRecordIds = this.generateSurveyMonthQuotationRecords(sm);
//		if(quotationRecordIds.size() > 0){
//			List<Assignment> generatedAssignment = this.groupQuotationRecordToAssignment(quotationRecordIds, sm);
//			List<PECheckTask> generatedPeCheckTask = this.generatePeCheckTask(quotationRecordIds, sm);
//		}
		updateQuotationSeasonalItem(sm);
		List<QuotationRecord> quotationRecords = this.quotationRecordGeneration(sm);;
		this.assignmentGeneration(sm, quotationRecords);
		peCheckTaskGeneration(sm, quotationRecords);
		
		peTopManagmentGeneration(sm);

		this.surveyMonthDao.flush();
		
	}
	
	@Transactional
	public void peTopManagmentGeneration(SurveyMonth sm){
		Date referenceMonth = sm.getReferenceMonth();
		Date lastMonth = DateUtils.addMonths(referenceMonth, -1);
		
		List<PETopManagement> lastMonthList = this.peTopManagementDao.getSectionHeadTaskByReferenceMonth(lastMonth);
		List<User> fieldOfficers = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, null);
		
		if (fieldOfficers.size() == 0) return;
		
		Collections.sort(fieldOfficers, new Comparator<User>(){
			@Override
			public int compare(User o1, User o2) {
				// TODO Auto-generated method stub
				return o1.getStaffCode().compareTo(o2.getStaffCode());
			}			
		});
		
		capi.entity.AllocationBatch[] batches = sm.getAllocationBatches().toArray(new capi.entity.AllocationBatch[0]);
		int batchCnt = batches.length;
		int officerCount = fieldOfficers.size();
		
		SecureRandom random = new SecureRandom();
		int startIndex = random.nextInt(officerCount);		
		
		List<PETopManagement> newList = new ArrayList<PETopManagement>();
		List<Integer> officerIdsList = new ArrayList<Integer>();
		for (User user : fieldOfficers){
			PETopManagement item = new PETopManagement();
			item.setFieldHead(true);
			item.setUser(user);
			newList.add(item);
			int batchIndex = startIndex % batchCnt;
			item.setAllocationBatch(batches[batchIndex]);			
			startIndex++;			
			officerIdsList.add(user.getId());
		}
		
		List<Integer> sectionHeadIndex = new ArrayList<Integer>();
		if (lastMonthList != null && lastMonthList.size() > 0){	
			int cnt = 0;
			for (PETopManagement item : lastMonthList){
				sectionHeadIndex.add(officerIdsList.indexOf(item.getUser().getId()));
				cnt++;
				if (cnt == 2) break;
			}
			if (sectionHeadIndex.size() > 1){
				Integer firstIndex = sectionHeadIndex.get(0) + 1;
				while (sectionHeadIndex.contains(firstIndex)){
					firstIndex++;
					if (firstIndex >= officerCount){
						firstIndex = 0;
					}
				}
				PETopManagement item1 = newList.get(firstIndex);
				item1.setSectionHead(true);
				item1.setFieldHead(false);
				firstIndex++;
				if (firstIndex >= officerCount){
					firstIndex = 0;
				}
				PETopManagement item2 = newList.get(firstIndex);
				item2.setSectionHead(true);
				item2.setFieldHead(false);
			}
			else{
				Integer userIndex = sectionHeadIndex.get(0) + 1;
				if (userIndex >= officerCount){
					userIndex = 0;
				}

				PETopManagement item1 = newList.get(userIndex);
				item1.setSectionHead(true);
				item1.setFieldHead(false);
				userIndex++;
				if (userIndex >= officerCount){
					userIndex = 0;
				}
				PETopManagement item2 = newList.get(userIndex);
				item2.setSectionHead(true);
				item2.setFieldHead(false);
			}
		}
		else{
			if (newList.size() > 0){
				PETopManagement item = newList.get(0);
				item.setSectionHead(true);
				item.setFieldHead(false);
			}
			if (newList.size() > 1){
				PETopManagement item = newList.get(1);
				item.setSectionHead(true);
				item.setFieldHead(false);
			}
		}
		
		for (PETopManagement item : newList){
			item.setByPassLog(true);
			peTopManagementDao.save(item);
		}

		peTopManagementDao.flush();
	}
	
	public SurveyMonth getSurveyMonthByReferenceMonth(Date refMonth){
		//return this.surveyMonthDao.getSurveyMonthByDate(refMonth);
		
		return surveyMonthDao.getSurveyMonthByReferenceMonth(refMonth);
	}
	
	public Boolean isFreezedSurveyMonth(){
		SystemConfiguration sc = this.sysConfigDao.findByName(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
		if(sc != null && sc.getValue() != null && sc.getValue().equalsIgnoreCase("1")){
			return true;
		}else{
			return false;
		}
	}
	
	public Boolean checkReadonly(Integer id){
		Boolean readonly = true;
		
		if(id == null){
			readonly = false;
		}else{
			SurveyMonth sm = this.surveyMonthDao.findById(id);
			
			if(sm != null){
				SystemConfiguration sc = this.sysConfigDao.findByName(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
				
				if((sc.getValue() != null && sc.getValue().equalsIgnoreCase("1"))){
					readonly = true;
				}else if(sm.getStatus() == 6 || sm.getStatus() == 7){ //6=created, 7=draft
					readonly = false;
				}else{
					readonly = true;
				}
			}
		}
		
		return readonly;
	}
	
	public Boolean checkIsDraft(Integer id){
		Boolean isDraft = true;
		
		if(id == null){
			isDraft = true;
		}else{
			SurveyMonth sm = this.surveyMonthDao.findById(id);
			
			if(sm != null){
				isDraft = sm.getStatus() == 6 || sm.getStatus() == 7;
			}
		}
		
		return isDraft;
	}
	
	public Boolean checkIsCreated(Integer id){
		Boolean isCreated = true;
		
		if(id == null){
			isCreated = false;
		}else{
			SurveyMonth sm = this.surveyMonthDao.findById(id);
			
			if(sm != null){
				isCreated = sm.getStatus() == 6;
			}
		}
		
		return isCreated;
	}
	
	@Transactional
	public void setSurveyMonthStatus(int status, Integer surveyMonthId){
		SurveyMonth month = surveyMonthDao.findById(surveyMonthId);
		month.setStatus(status);
		surveyMonthDao.save(month);
		surveyMonthDao.flush();
	}
	
	public List<SurveyMonthSyncData> getUpdateSurveyMonth(Date lastSyncTime){
		return surveyMonthDao.getUpdateSurvey(lastSyncTime);
	}
	
	public SurveyMonth getRelativeSurveyMonth(Date date){
		SurveyMonth month = surveyMonthDao.findByDate(date);
		return month;
	}
	
	public void updateQuotationSeasonalItem(SurveyMonth sm){
		Integer summerStartMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE).getValue());
		Integer summerEndMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_END_DATE).getValue());
		Integer winterStartMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE).getValue());
		Integer winterEndMonth = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_END_DATE).getValue());
		List<Quotation> quotationList = quotationDao.getActiveQuotation();
		for(Quotation q : quotationList){
//			Command by User
//			if(q.getOutlet() == null){
//				continue;
//			}
			
			Unit unit = q.getUnit();
			SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
			Integer smMonth = Integer.parseInt(monthFormat.format(sm.getReferenceMonth()));
			
			// check seasonality
			Integer seasonStartMonth = 0; 
			Integer seasonEndMonth = 0; 
			switch(unit.getSeasonality()){
				case 2: //case summer
					seasonStartMonth = summerStartMonth;
					seasonEndMonth = summerEndMonth;
					break;
				case 3: //case winter
					seasonStartMonth = winterStartMonth;
					seasonEndMonth = winterEndMonth;
					break;
				case 4: //case custom
					seasonStartMonth = unit.getSeasonStartMonth();
					seasonEndMonth = unit.getSeasonEndMonth();
					break;
			}
			//check sm month = start month of seasonal item
			if(smMonth == seasonStartMonth){
				q.setLastSeasonReturnGoods(q.isReturnGoods() || q.isReturnNewGoods());
				q.setFRApplied(false);
				q.setReturnGoods(false);
				q.setReturnNewGoods(false);
				q.setSeasonalWithdrawal(null);
				q.setFrAdmin(null);
				q.setFrField(null);
				q.setIsUseFRAdmin(null);
				q.setTempFRPercentage(null);
				q.setTempFRValue(null);
				q.setTempIsFRApplied(false);
				q.setTempIsReturnGoods(false);
				q.setTempIsReturnNewGoods(false);
				q.setTempIsUseFRAdmin(null);
				q.setTempKeepNoMonth(q.getKeepNoMonth());
				q.setTempLastFRAppliedDate(q.getLastFRAppliedDate());
				
				q.setByPassLog(true);					
				this.quotationDao.save(q);
			}
		}
	}
	
	public SurveyMonth getSurveyMonthById(int id) {
		return surveyMonthDao.findById(id);
	}
	
	/**
	 * Get survey month select2 format
	 */
	public Select2ResponseModel querySurveyMonthSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<SurveyMonth> entities = surveyMonthDao.searchSurveyMonth(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = surveyMonthDao.countSurveyMonth(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (SurveyMonth sm : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(sm.getSurveyMonthId()));
			item.setText(commonService.formatMonth(sm.getReferenceMonth()));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
}
