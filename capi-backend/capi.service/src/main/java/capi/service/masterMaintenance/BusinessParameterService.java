package capi.service.masterMaintenance;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ItemDao;
import capi.dal.PECheckUnitCriteriaDao;
import capi.dal.PEExcludedOutletTypeDao;
import capi.dal.PurposeDao;
import capi.dal.ReportReasonSettingDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UnitDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.dal.WorkingSessionSettingDao;
import capi.entity.Item;
import capi.entity.PECheckUnitCriteria;
import capi.entity.PEExcludedOutletType;
import capi.entity.Purpose;
import capi.entity.ReportReasonSetting;
import capi.entity.SystemConfiguration;
import capi.entity.Unit;
import capi.entity.VwOutletTypeShortForm;
import capi.entity.WorkingSessionSetting;
import capi.model.SystemConstant;
import capi.model.api.dataSync.BusinessParameterSyncData;
import capi.model.masterMaintenance.businessParameterMaintenance.DisplayModel;
import capi.model.masterMaintenance.businessParameterMaintenance.GeneralSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.ItineraryParameterSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.OnSpotValidationMessageSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.PEParameterSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.ReasonForReportSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.UnitCriteriaSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.WorkingSessionModel;
import capi.model.masterMaintenance.businessParameterMaintenance.WorkingSessionSaveModel;
import capi.service.CommonService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;

@Service("BusinessParameterService")
public class BusinessParameterService {
	
	@Autowired
	private SystemConfigurationDao systemConfigurationDao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private PEExcludedOutletTypeDao peExcludedOutletTypeDao;
	
	@Autowired
	private VwOutletTypeShortFormDao OutletTypeDao;
	
	@Autowired
	private PECheckUnitCriteriaDao peCheckUnitCriteriaDao;
	
	@Autowired
	private ReportReasonSettingDao reportReasonSettingDao;
	
	@Autowired
	private WorkingSessionSettingDao workingSessionSettingDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private CalendarEventService calendarService;
	
	public DisplayModel getParameters(){
		DisplayModel displayModel = new DisplayModel();
		Iterator<SystemConfiguration> configs = systemConfigurationDao.findAll().iterator();
		while(configs.hasNext()){
			SystemConfiguration config = configs.next();
			
			if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR)){
				displayModel.setCommonAssignmentEventColor(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR)){
				displayModel.setCommonCalendarEventColor(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_COUNTRY_OF_ORIGINS)){
				displayModel.setCommonCountryOfOrigins(config.getValue().split(";"));
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH)){
				displayModel.setCommonFreezeSurveyMonth(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_MOBILE_SYNC_PERIOD)){
				displayModel.setCommonMobileSynchronizationPeriod(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_PUBLIC_HOLIDAY_URL)){
				displayModel.setCommonPublicHolidayUrl(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_RUA_RATIO)){
				displayModel.setCommonRuaRatio(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_SUMMER_END_DATE)){
				displayModel.setCommonSummerEndDate(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_SUMMER_START_DATE)){
				displayModel.setCommonSummerStartDate(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_WINTER_END_DATE)){
				displayModel.setCommonWinterEndDate(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_WINTER_START_DATE)){
				displayModel.setCommonWinterStartDate(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_LAST_MODIFY)){
				displayModel.setCommonLastModify(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_DELINK_PERIOD)) {
				displayModel.setDelinkPeriod(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH)){
				displayModel.setPeExcludePECheckMonth(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_INCLUDE_NEW_RECRUITMENT)){
				displayModel.setPeIncludeNewRecruitment(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_INCLUDE_RUA_CASE)){
				displayModel.setPeIncludeRUACase(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_SELECT_PE_CHECK_PERCENTAGE)){
				displayModel.setPeSelectPECheckPercentage(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION)){
				displayModel.setItineraryNoofAssignmentDeviation(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_MINUS)){
				displayModel.setItineraryNoofAssignmentDeviationMinus(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS)){
				displayModel.setItineraryNoofAssignmentDeviationPlus(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION)){
				displayModel.setItinerarySequenceDeviation(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS)){
				displayModel.setItinerarySequencePercents(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION)){
				displayModel.setItineraryTPUSequenceDeviation(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES)){
				displayModel.setItineraryTPUSequenceDeviationTimes(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1)){
				displayModel.setOnSpotMessage1(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2)){
				displayModel.setOnSpotMessage2(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3)){
				displayModel.setOnSpotMessage3(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4)){
				displayModel.setOnSpotMessage4(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_5)){
				displayModel.setOnSpotMessage5(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_6)){
				displayModel.setOnSpotMessage6(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_7)){
				displayModel.setOnSpotMessage7(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_8)){
				displayModel.setOnSpotMessage8(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_9)){
				displayModel.setOnSpotMessage9(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_10)){
				displayModel.setOnSpotMessage10(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11)){
				displayModel.setOnSpotMessage11(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12)){
				displayModel.setOnSpotMessage12(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_13)){
				displayModel.setOnSpotMessage13(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14)){
				displayModel.setOnSpotMessage14(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_15)){
				displayModel.setOnSpotMessage15(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_16)){
				displayModel.setOnSpotMessage16(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_17)){
				displayModel.setOnSpotMessage17(config.getValue());
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_18)){
				displayModel.setOnSpotMessage18(config.getValue());
			}
			
			displayModel.setpECheckUnitCriteriaList(this.peCheckUnitCriteriaDao.findAll());
			displayModel.setReportReasonList(this.reportReasonSettingDao.findAll());
			displayModel.setWorkingSessionList(this.workingSessionSettingDao.findAll());
			displayModel.setPeIncludedPurpose(purposeDao.getPEIncludedPurposes());
		}
		return displayModel;
	}
	
	@Transactional
	public void saveGeneralParameters(GeneralSaveModel model) throws Exception{
		Iterator<SystemConfiguration> configurations = this.systemConfigurationDao.findAll().iterator();
		List<String> updatedConfig = new ArrayList<String>();
		CommonService commonService = new  CommonService();
		while(configurations.hasNext()){
			SystemConfiguration config = configurations.next();
			if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR)){
				config.setValue(model.getAssignmentEventColor());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR)){
				config.setValue(model.getcalendarEventColor());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_COUNTRY_OF_ORIGINS)){
				config.setValue(StringUtils.join(model.getCountryOfOrigins(), ";"));
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_COUNTRY_OF_ORIGINS);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH)){
				config.setValue(model.getFreezeSurveyMonth());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_MOBILE_SYNC_PERIOD)){
				config.setValue(model.getMobileSynchronizationPeriod());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_MOBILE_SYNC_PERIOD);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_PUBLIC_HOLIDAY_URL)){
				config.setValue(model.getPublicHolidayUrl());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_PUBLIC_HOLIDAY_URL);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_RUA_RATIO)){
				config.setValue(model.getRuaRatio());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_RUA_RATIO);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_SUMMER_END_DATE)){
				config.setValue(model.getSummerEndDate());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_SUMMER_END_DATE);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_SUMMER_START_DATE)){
				config.setValue(model.getSummerStartDate());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_SUMMER_START_DATE);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_WINTER_END_DATE)){
				config.setValue(model.getWinterEndDate());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_WINTER_END_DATE);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_WINTER_START_DATE)){
				config.setValue(model.getWinterStartDate());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_WINTER_START_DATE);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_DELINK_PERIOD)){
				config.setValue(model.getDelinkPeriod());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_DELINK_PERIOD);
				
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_LAST_MODIFY)){
				config.setValue(commonService.formatDateTime(Calendar.getInstance().getTime()));
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_LAST_MODIFY);
				
			}
		}
		
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR);
			config.setValue(model.getAssignmentEventColor());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR);
			config.setValue(model.getcalendarEventColor());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_COUNTRY_OF_ORIGINS)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_COUNTRY_OF_ORIGINS);
			config.setValue(StringUtils.join(model.getCountryOfOrigins(), ";"));
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_FREEZE_SURVEY_MONTH);
			config.setValue(model.getFreezeSurveyMonth());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_MOBILE_SYNC_PERIOD)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_MOBILE_SYNC_PERIOD);
			config.setValue(model.getMobileSynchronizationPeriod());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_PUBLIC_HOLIDAY_URL)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_PUBLIC_HOLIDAY_URL);
			config.setValue(model.getPublicHolidayUrl());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_RUA_RATIO)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_RUA_RATIO);
			config.setValue(model.getRuaRatio());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_SUMMER_END_DATE)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_SUMMER_END_DATE);
			config.setValue(model.getSummerEndDate());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_SUMMER_START_DATE)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_SUMMER_START_DATE);
			config.setValue(model.getSummerStartDate());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_WINTER_END_DATE)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_WINTER_END_DATE);
			config.setValue(model.getWinterEndDate());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_WINTER_START_DATE)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_WINTER_START_DATE);
			config.setValue(model.getWinterStartDate());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_DELINK_PERIOD)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_DELINK_PERIOD);
			config.setValue(model.getDelinkPeriod());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_LAST_MODIFY)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_LAST_MODIFY);
			config.setValue(commonService.formatDateTime(Calendar.getInstance().getTime()));
			this.systemConfigurationDao.save(config);
		}
		
		systemConfigurationDao.flush();
		
		if(model.getSyncCalendar()){
			calendarService.syncPublicCalendar();
		}
	}
	
	@Transactional
	public void savePEParameters(PEParameterSaveModel model){
		Iterator<SystemConfiguration> configurations = this.systemConfigurationDao.findAll().iterator();
		List<String> updatedConfig = new ArrayList<String>();
		CommonService commonService = new  CommonService();
		while(configurations.hasNext()){
			SystemConfiguration config = configurations.next();
			
			if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_LAST_MODIFY)){
				config.setValue(commonService.formatDateTime(Calendar.getInstance().getTime()));
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_LAST_MODIFY);
				
			} else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH)){
				config.setValue(model.getPeCheckMonth().toString());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH);
				
			} else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_SELECT_PE_CHECK_PERCENTAGE)){
				config.setValue(model.getPePercentage().toString());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.PE_PARAM_SELECT_PE_CHECK_PERCENTAGE);
				
			} else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_INCLUDE_NEW_RECRUITMENT)){
				config.setValue(model.getIncludeNewRecruitment()!= null ? "1" : "0");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.PE_PARAM_INCLUDE_NEW_RECRUITMENT);
				
			} else if(config.getName().equalsIgnoreCase(SystemConstant.PE_PARAM_INCLUDE_RUA_CASE)){
				config.setValue(model.getIncludeRUACase()!= null ? "1" : "0");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.PE_PARAM_INCLUDE_RUA_CASE);
				
			}
		}
		
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_LAST_MODIFY)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_LAST_MODIFY);
			config.setValue(commonService.formatDateTime(Calendar.getInstance().getTime()));
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH);
			config.setValue(model.getPeCheckMonth().toString());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.PE_PARAM_SELECT_PE_CHECK_PERCENTAGE)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.PE_PARAM_SELECT_PE_CHECK_PERCENTAGE);
			config.setValue(model.getPePercentage().toString());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.PE_PARAM_INCLUDE_NEW_RECRUITMENT)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.PE_PARAM_INCLUDE_NEW_RECRUITMENT);
			config.setValue(model.getIncludeNewRecruitment()!= null ? "1" : "0");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.PE_PARAM_INCLUDE_RUA_CASE)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.PE_PARAM_INCLUDE_RUA_CASE);
			config.setValue(model.getIncludeRUACase()!= null ? "1" : "0");
			this.systemConfigurationDao.save(config);
		}
		
		// PE included purpose
		if (model.getPurposeIds() != null && model.getPurposeIds().size() > 0){
			List<Purpose> removedPurposes = purposeDao.getNotExistedPEIncludedPurpose(model.getPurposeIds());
			if (removedPurposes != null &&  removedPurposes.size() > 0){
				for (Purpose purpose: removedPurposes){
					purpose.setPeIncluded(false);
				}
			}
			List<Purpose> includedPurpose = purposeDao.getSurveyTypesByIds(model.getPurposeIds());
			if (includedPurpose != null &&  includedPurpose.size() > 0){
				for (Purpose purpose: includedPurpose){
					purpose.setPeIncluded(true);
				}
			}
		}else{
			List<Purpose> allPurposes = purposeDao.getPEIncludedPurposes();
			if (allPurposes != null &&  allPurposes.size() > 0){
				for (Purpose purpose: allPurposes){
					purpose.setPeIncluded(false);
				}
			}
		}
		
		//delete record in database which PEExcludedOutletType not in request list
		Iterator<PEExcludedOutletType> outletTypeRemoveList = peExcludedOutletTypeDao.getExcludedPEExcludedOutletType(model.getExcludedOutletType()).iterator();
		while(outletTypeRemoveList.hasNext()){
			peExcludedOutletTypeDao.delete(outletTypeRemoveList.next());
		}
		
		//delete exist PEExcludedOutletType in request list, remain request list would be new entries
		List<String> newList = model.getExcludedOutletType();
		Iterator<PEExcludedOutletType> outletTypeList = peExcludedOutletTypeDao.getPEExcludedOutletType(model.getExcludedOutletType()).iterator();
		while(outletTypeList.hasNext()){
			PEExcludedOutletType outletType = outletTypeList.next();
			if(newList.contains(outletType.getOutletType().getShortCode())){
				newList.remove(outletType.getOutletType().getShortCode());
			}
		}
		
		//insert new entries
		if (newList != null && newList.size() > 0){
			Iterator<String> createList = newList.iterator();
			while(createList.hasNext()){
				String outletCode = createList.next();
				VwOutletTypeShortForm outlet = OutletTypeDao.findById(outletCode);
				PEExcludedOutletType outletType = new PEExcludedOutletType();
				outletType.setOutletType(outlet);
				peExcludedOutletTypeDao.save(outletType);
			}
		}
		
		//update PECheckUnitCriteria
		List<Integer> remainIds = new ArrayList<Integer>();
		if( model.getUnitCriteria() != null){
			Iterator<UnitCriteriaSaveModel> unitCriterias = model.getUnitCriteria().iterator();
			while(unitCriterias.hasNext()){
				UnitCriteriaSaveModel ucModel = unitCriterias.next();
				//if(ucModel.getUnitId() != null){
				if (ucModel.getUnitCriteriaId() != null){
					PECheckUnitCriteria pecuc = this.peCheckUnitCriteriaDao.findById(ucModel.getUnitCriteriaId());
					if(pecuc != null){
						remainIds.add(pecuc.getId());
						pecuc.setNoOfMonth(ucModel.getNoOfMonth());
						pecuc.setQuotationPercentage(ucModel.getPercentageOfQuotation());
						pecuc.setPrSymbol(ucModel.getPrOperator());
						pecuc.setPrValue(ucModel.getPrPercentage());
						//Unit u = unitDao.findById(ucModel.getUnitId());
						//pecuc.setUnit(u);
						List<Item> itemList = new ArrayList<Item>();
						itemList.addAll(pecuc.getItems());
						List<Integer> givevnItemIds = new ArrayList<Integer>();
						CollectionUtils.addAll(givevnItemIds, ucModel.getItemIds());
						List<Integer> itemIds = new ArrayList<Integer>();
						for (Item item : itemList){
							if (itemIds.contains(item.getId())){
								itemIds.add(item.getId());
							}
							else{
								pecuc.getItems().remove(item);
							}
						}
						Collection<Integer> newItemIds = CollectionUtils.subtract(givevnItemIds, itemIds);
						for (Integer id : newItemIds){
							pecuc.getItems().add(itemDao.findById(id));
						}
						
						this.peCheckUnitCriteriaDao.save(pecuc);
					}
				}
				//}
			}
		}
		
		// delete removed record
//		if(remainIds.size() > 0){
			List<PECheckUnitCriteria> removeEntityList = this.peCheckUnitCriteriaDao.getExcludedPECheckUnitCriteria(remainIds);
			if(removeEntityList != null){
				Iterator<PECheckUnitCriteria> removeList = removeEntityList.iterator();
				while(removeList.hasNext()){
					PECheckUnitCriteria entity = removeList.next();
					entity.getItems().clear();
					this.peCheckUnitCriteriaDao.delete(entity);
				}
			}
//		}
		// delete all record if not entry submitted
//		if(remainIds.size() == 0 && (model.getUnitCriteria() == null || model.getUnitCriteria().size() == 0)){
//			List<PECheckUnitCriteria> removeEntityList = this.peCheckUnitCriteriaDao.findAll();
//			if(removeEntityList != null){
//				Iterator<PECheckUnitCriteria> removeList = removeEntityList.iterator();
//				while(removeList.hasNext()){
//					PECheckUnitCriteria entity = removeList.next();
//					entity.getItems().clear();
//					this.peCheckUnitCriteriaDao.delete(entity);
//				}
//			}
//		}
		
		//insert new PECheckUnitCriteria
		if( model.getNewUnitCriteria() != null){
			Iterator<UnitCriteriaSaveModel> newUnitCriterias = model.getNewUnitCriteria().iterator();
			while(newUnitCriterias.hasNext()){
				UnitCriteriaSaveModel ucModel = newUnitCriterias.next();
				//if(ucModel.getUnitId() != null){
					PECheckUnitCriteria pecuc =  new PECheckUnitCriteria();
					pecuc.setNoOfMonth(ucModel.getNoOfMonth());
					pecuc.setQuotationPercentage(ucModel.getPercentageOfQuotation());
					pecuc.setPrSymbol(ucModel.getPrOperator());
					pecuc.setPrValue(ucModel.getPrPercentage());
//					Unit u = unitDao.findById(ucModel.getUnitId());
//					pecuc.setUnit(u);
					for (Integer id : ucModel.getItemIds()){
						pecuc.getItems().add(itemDao.findById(id));
					}
					this.peCheckUnitCriteriaDao.save(pecuc);
				//}
			}
		}
		 
		systemConfigurationDao.flush();
		//peExcludedOutletTypeDao.flush();
		//peCheckUnitCriteriaDao.flush();
	}
	
	public Unit getUnitById(Integer id){
		return unitDao.findById(id);
				
	}
	
	public List<PEExcludedOutletType> getPEExcludedOutletType(){
		return this.peExcludedOutletTypeDao.getPEExcludedOutletType();
	}
	@Transactional
	public boolean deleteUnitCriteria(Integer id){
		PECheckUnitCriteria pecuc = this.peCheckUnitCriteriaDao.findById(id);
		if(pecuc != null){
			this.peCheckUnitCriteriaDao.delete(pecuc);
		}
		peCheckUnitCriteriaDao.flush();
		return true;
	}
	
	@Transactional
	public void saveItineraryParameters(ItineraryParameterSaveModel model){
		Iterator<SystemConfiguration> configurations = this.systemConfigurationDao.findAll().iterator();
		List<String> updatedConfig = new ArrayList<String>();
		CommonService commonService = new  CommonService();
		while(configurations.hasNext()){
			SystemConfiguration config = configurations.next();
			if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION)){
				config.setValue((model.getAssignmentDeviation() == null ? 0 : 1)+"");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_MINUS)){
				// assignment deviation minus removed
//				config.setValue((model.getAssignmentDeviationMinus())+"");
//				this.systemConfigurationDao.save(config);
//				updatedConfig.add(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_MINUS);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS)){
				config.setValue((model.getAssignmentDeviationPlus())+"");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION)){
				config.setValue((model.getSequenceDeviation())+"");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS)){
				config.setValue((model.getSequenceDeviationPercentage())+"");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION)){
				config.setValue((model.getTpuSequenceDeviation())+"");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES)){
				config.setValue((model.getTpuSequenceDeviationTimes())+"");
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.BUS_PARAM_LAST_MODIFY)){
				config.setValue(commonService.formatDateTime(Calendar.getInstance().getTime()));
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.BUS_PARAM_LAST_MODIFY);
				
			}
		}
		
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION);
			config.setValue(model.getAssignmentDeviation()+"");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_MINUS)){
			// assignment deviation minus removed
//			SystemConfiguration config = new SystemConfiguration();
//			config.setName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_MINUS);
//			config.setValue(model.getAssignmentDeviationMinus()+"");
//			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS);
			config.setValue(model.getAssignmentDeviationPlus()+"");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION);
			config.setValue(model.getSequenceDeviation()+"");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS);
			config.setValue(model.getSequenceDeviationPercentage()+"");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION);
			config.setValue(model.getTpuSequenceDeviation()+"");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES);
			config.setValue(model.getTpuSequenceDeviationTimes()+"");
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.BUS_PARAM_LAST_MODIFY)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.BUS_PARAM_LAST_MODIFY);
			config.setValue(commonService.formatDateTime(Calendar.getInstance().getTime()));
			this.systemConfigurationDao.save(config);
		}
		
		systemConfigurationDao.flush();
	}
	
	@Transactional
	public void saveReasonForReports(ReasonForReportSaveModel model){
		//update ReportReasonSetting
		List<Integer> remainIds = new ArrayList<Integer>();
		if( model.getFieldworkAssignmentReason() != null){
			Iterator<ReportReasonSetting> reportReasonSettings = model.getFieldworkAssignmentReason().iterator();
			while(reportReasonSettings.hasNext()){
				ReportReasonSetting reasonModel = reportReasonSettings.next();
				if(reasonModel.getId() != null && reasonModel.getId() != 0){
					remainIds.add(reasonModel.getId());
					ReportReasonSetting rrs = this.reportReasonSettingDao.findById(reasonModel.getId());
					if(rrs != null){
						rrs.setReason(reasonModel.getReason());
						this.reportReasonSettingDao.save(rrs);
					}
				}
			}
		}
		// delete removed record
		if(remainIds.size() > 0){
			List<ReportReasonSetting> removeEntityList = this.reportReasonSettingDao.getExcludedReportReasonSetting(remainIds);
			if(removeEntityList != null){
				Iterator<ReportReasonSetting> removeList = removeEntityList.iterator();
				while(removeList.hasNext()){
					ReportReasonSetting entity = removeList.next();
					this.reportReasonSettingDao.delete(entity);
				}
			}
		}
		// delete all record if not entry submitted
		if(remainIds.size() == 0 && (model.getFieldworkAssignmentReason() == null || model.getFieldworkAssignmentReason().size() == 0)){
			List<ReportReasonSetting> removeEntityList = this.reportReasonSettingDao.findAll();
			if(removeEntityList != null){
				Iterator<ReportReasonSetting> removeList = removeEntityList.iterator();
				while(removeList.hasNext()){
					ReportReasonSetting entity = removeList.next();
					this.reportReasonSettingDao.delete(entity);
				}
			}
		}
		
		
		//insert new PECheckUnitCriteria
		if( model.getNewFieldworkAssignmentReason() != null){
			Iterator<ReportReasonSetting> reportReasonSettings = model.getNewFieldworkAssignmentReason().iterator();
			while(reportReasonSettings.hasNext()){
				ReportReasonSetting reasonModel = reportReasonSettings.next();
				if(reasonModel.getReason() != null && reasonModel.getReason().length() > 0){
					ReportReasonSetting rrs = new ReportReasonSetting();
					rrs.setReason(reasonModel.getReason());
					this.reportReasonSettingDao.save(rrs);
				}
			}
		}
		reportReasonSettingDao.flush();
	}
	@Transactional
	public boolean deleteReportReason(Integer id){
		ReportReasonSetting rrs = this.reportReasonSettingDao.findById(id);
		if(rrs != null){
			this.reportReasonSettingDao.delete(rrs);
		}
		reportReasonSettingDao.flush();
		return true;
	}
	
	@Transactional
	public void saveWorkingSessions(WorkingSessionSaveModel model){
		//update ReportReasonSetting
		List<Integer> remainIds = new ArrayList<Integer>();
		if( model.getWorkingSessions() != null){
			Iterator<WorkingSessionModel> workingSessionSettings = model.getWorkingSessions().iterator();
			while(workingSessionSettings.hasNext()){
				WorkingSessionModel sessionModel = workingSessionSettings.next();
				if(sessionModel.getId() != null && sessionModel.getId() != 0){
					remainIds.add(sessionModel.getId());
					WorkingSessionSetting wss = this.workingSessionSettingDao.findById(sessionModel.getId());
					if(wss != null){
						try {
							wss.setFromTime(commonService.getTime(sessionModel.getFromTime()));
							wss.setToTime(commonService.getTime(sessionModel.getToTime()));

							this.workingSessionSettingDao.save(wss);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		// delete removed record
		if(remainIds.size() > 0){
			List<WorkingSessionSetting> removeEntityList = this.workingSessionSettingDao.getExcludedWorkingSessionSetting(remainIds);
			if(removeEntityList != null){
				Iterator<WorkingSessionSetting> removeList = removeEntityList.iterator();
				while(removeList.hasNext()){
					WorkingSessionSetting entity = removeList.next();
					this.workingSessionSettingDao.delete(entity);
				}
			}
		}
		// delete all record if not entry submitted
		if(remainIds.size() == 0 && (model.getWorkingSessions() == null || model.getWorkingSessions().size() == 0)){
			List<WorkingSessionSetting> removeEntityList = this.workingSessionSettingDao.findAll();
			if(removeEntityList != null){
				Iterator<WorkingSessionSetting> removeList = removeEntityList.iterator();
				while(removeList.hasNext()){
					WorkingSessionSetting entity = removeList.next();
					this.workingSessionSettingDao.delete(entity);
				}
			}
		}
		
		//insert new PECheckUnitCriteria
		if( model.getNewWorkingSessions() != null){
			Iterator<WorkingSessionModel> workingSessionSettings = model.getNewWorkingSessions().iterator();
			while(workingSessionSettings.hasNext()){
				WorkingSessionModel sessionModel = workingSessionSettings.next();
				if(sessionModel.getFromTime() != null && sessionModel.getToTime() != null){
					WorkingSessionSetting wss =  new WorkingSessionSetting();
					try {
						wss.setFromTime(commonService.getTime(sessionModel.getFromTime()));
						wss.setToTime(commonService.getTime(sessionModel.getToTime()));
	
						this.workingSessionSettingDao.save(wss);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	
		workingSessionSettingDao.flush();
	}
	@Transactional
	public boolean deleteWorkingSession(Integer id){
		WorkingSessionSetting wss = this.workingSessionSettingDao.findById(id);
		if(wss != null){
			this.workingSessionSettingDao.delete(wss);
		}
		workingSessionSettingDao.flush();
		return true;
	}
	
	@Transactional
	public void saveOnSpotValidationMessage(OnSpotValidationMessageSaveModel model){
		Iterator<SystemConfiguration> configurations = this.systemConfigurationDao.findAll().iterator();
		List<String> updatedConfig = new ArrayList<String>();
		while(configurations.hasNext()){
			SystemConfiguration config = configurations.next();
			if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1)){
				config.setValue(model.getMessage1());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2)){
				config.setValue(model.getMessage2());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3)){
				config.setValue(model.getMessage3());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4)){
				config.setValue(model.getMessage4());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_5)){
				config.setValue(model.getMessage5());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_5);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_6)){
				config.setValue(model.getMessage6());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_6);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_7)){
				config.setValue(model.getMessage7());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_7);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_8)){
				config.setValue(model.getMessage8());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_8);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_9)){
				config.setValue(model.getMessage9());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_9);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_10)){
				config.setValue(model.getMessage10());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_10);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11)){
				config.setValue(model.getMessage11());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12)){
				config.setValue(model.getMessage12());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_13)){
				config.setValue(model.getMessage13());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_13);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14)){
				config.setValue(model.getMessage14());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_15)){
				config.setValue(model.getMessage15());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_15);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_16)){
				config.setValue(model.getMessage16());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_16);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_17)){
				config.setValue(model.getMessage17());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_17);
			}else if(config.getName().equalsIgnoreCase(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_18)){
				config.setValue(model.getMessage18());
				this.systemConfigurationDao.save(config);
				updatedConfig.add(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_18);
			}
		}
		
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_1);
			config.setValue(model.getMessage1());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_2);
			config.setValue(model.getMessage2());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3);
			config.setValue(model.getMessage3());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4);
			config.setValue(model.getMessage4());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_5)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_5);
			config.setValue(model.getMessage5());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_6)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_6);
			config.setValue(model.getMessage6());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_7)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_7);
			config.setValue(model.getMessage7());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_8)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_8);
			config.setValue(model.getMessage8());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_9)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_9);
			config.setValue(model.getMessage9());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_10)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_10);
			config.setValue(model.getMessage10());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_11);
			config.setValue(model.getMessage11());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_12);
			config.setValue(model.getMessage12());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_13)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_13);
			config.setValue(model.getMessage13());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_14);
			config.setValue(model.getMessage14());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_15)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_15);
			config.setValue(model.getMessage15());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_16)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_16);
			config.setValue(model.getMessage16());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_17)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_17);
			config.setValue(model.getMessage17());
			this.systemConfigurationDao.save(config);
		}
		if(!updatedConfig.contains(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_18)){
			SystemConfiguration config = new SystemConfiguration();
			config.setName(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_18);
			config.setValue(model.getMessage18());
			this.systemConfigurationDao.save(config);
		}
		systemConfigurationDao.flush();
	}
	
	public List<BusinessParameterSyncData> getUpdateBusinessParameter(Date lastSyncTime){
		return systemConfigurationDao.getUpdateBusinessParameter(lastSyncTime);
	}
}
