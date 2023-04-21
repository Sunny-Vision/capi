package capi.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.BatchDao;
import capi.dal.GroupDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ReportTaskDao;
import capi.dal.SubGroupDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.Batch;
import capi.entity.Group;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.SubGroup;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.QuotationRecordProgress;
import capi.model.report.QuotationRecordProgressCriteria;
import capi.service.CommonService;

@Service("QuotationRecordProgressService")
public class QuotationRecordProgressService extends ReportServiceBase{

	@Autowired
	private IndoorQuotationRecordDao dao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private BatchDao batchDao;
	
	@Autowired
	private SubGroupDao subGroupDao;
	
	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private UnitDao unitDao;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9002";
	}

	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		QuotationRecordProgressCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), QuotationRecordProgressCriteria.class);
		
		Calendar calendar = Calendar.getInstance();
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		calendar.setTime(startMonth);
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
		startMonth = calendar.getTime();
		calendar.clear();
		
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		calendar.setTime(endMonth);
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		endMonth = calendar.getTime();
		calendar.clear();
		
		List<QuotationRecordProgress.IndoorProgress> indoors = dao.getIndoorProgress(
				criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), criteria.getBatch(), 
				startMonth, endMonth, criteria.getGroup(), criteria.getSubGroup(), criteria.getCpiBasePeriods());
		
		List<QuotationRecordProgress.QRProgress> qrs = quotationRecordDao.getQRProgress(
				criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), criteria.getBatch(), 
				startMonth, endMonth, criteria.getGroup(), criteria.getSubGroup(), criteria.getCpiBasePeriods());
		
		Hashtable<Integer, QuotationRecordProgress> mapProgress = new Hashtable<Integer, QuotationRecordProgress>();
		
		for(QuotationRecordProgress.IndoorProgress indoor : indoors){
			if(mapProgress.containsKey(indoor.getSubGroupId())){
				QuotationRecordProgress model = mapProgress.get(indoor.getSubGroupId());
				BeanUtils.copyProperties(indoor, model);
				mapProgress.put(indoor.getSubGroupId(), model);
			} else {
				QuotationRecordProgress model = new QuotationRecordProgress();
				BeanUtils.copyProperties(indoor, model);
				mapProgress.put(indoor.getSubGroupId(), model);
			}
		}
		
		for(QuotationRecordProgress.QRProgress qr : qrs){
			if(mapProgress.containsKey(qr.getSubGroupId())){
				QuotationRecordProgress model = mapProgress.get(qr.getSubGroupId());
				BeanUtils.copyProperties(qr, model);
				mapProgress.put(qr.getSubGroupId(), model);
			} else {
				QuotationRecordProgress model = new QuotationRecordProgress();
				BeanUtils.copyProperties(qr, model);
				mapProgress.put(qr.getSubGroupId(), model);
			}
		}
		
		List<QuotationRecordProgress> progress = new ArrayList<QuotationRecordProgress>(mapProgress.values());
//		List<QuotationRecordProgress> progress = dao.getQuotationRecordProgress(
//				criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), criteria.getBatch(), 
//				startMonth, endMonth, criteria.getGroup(), criteria.getSubGroup(), criteria.getCpiBasePeriods());
		
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		if (criteria.getBatch() != null && criteria.getBatch().size() > 0){
			List<Batch> batches = batchDao.getByIds(criteria.getBatch().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Batch batch : batches){
				codes.add(batch.getCode());
			}

			parameters.put("batchCode",StringUtils.join(codes, ","));
		}
		else{
			parameters.put("batchCode", "-");
		}
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}			
			parameters.put("purpose",StringUtils.join(codes, ","));
		}
		else{
			parameters.put("purpose", "-");
		}
		
		if (criteria.getCpiSurveyForm() != null && criteria.getCpiSurveyForm().size() > 0){
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCpiSurveyForm()){
				switch (form){
					case 1:codes.add("Market"); break;
					case 2:codes.add("Supermarket"); break;
					case 3:codes.add("Batch"); break;
					default:codes.add("Others"); break;
				}
				
			}
			parameters.put("cpiSurveyForm", StringUtils.join(codes, ","));
		}
		else{
			parameters.put("cpiSurveyForm", "-");
		}
		
		if (criteria.getCpiBasePeriods() != null && criteria.getCpiBasePeriods().size() > 0){
			parameters.put("cpiBasePeriod", StringUtils.join(criteria.getCpiBasePeriods(), ","));
		}
		else{
			parameters.put("cpiBasePeriod", "-");
		}
		
		parameters.put("period", criteria.getStartMonth() + " - " + criteria.getEndMonth());
		
		String path = this.exportReport(task, "QuotationRecordProgress", progress, parameters);
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		QuotationRecordProgressCriteria criteria = (QuotationRecordProgressCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		if (criteria.getUnitId() != null && criteria.getUnitId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Unit> units = unitDao.getByIds(criteria.getUnitId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Unit unit : units){
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}
		if (criteria.getCpiSurveyForm() != null && criteria.getCpiSurveyForm().size() > 0){
			descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCpiSurveyForm()){
				switch (form){
					case 1:codes.add("Market"); break;
					case 2:codes.add("Supermarket"); break;
					case 3:codes.add("Batch"); break;
					default:codes.add("Others"); break;
				}
				
			}
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(codes, ", ")));
		}
		if (criteria.getBatch()!= null && criteria.getBatch().size() > 0){
			List<Batch> batches = batchDao.getByIds(criteria.getBatch().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Batch batch : batches){
				codes.add(batch.getCode());
			}
			descBuilder.append("\n");
			descBuilder.append(String.format("Batch Code: %s", StringUtils.join(codes, ", ")));
		}
		descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		if (criteria.getSubGroup() != null && criteria.getSubGroup().size() > 0){
			List<SubGroup> subGroups = subGroupDao.getByIds(criteria.getSubGroup().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (SubGroup subGroup : subGroups){
				codes.add(subGroup.getCode());
			}		
			descBuilder.append("\n");
			descBuilder.append(String.format("Sub Group: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getGroup() != null && criteria.getGroup().size() > 0){
			List<Group> groups = groupDao.getByIds(criteria.getGroup().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Group group : groups){
				codes.add(group.getCode());
			}		
			descBuilder.append("\n");
			descBuilder.append(String.format("Group: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getCpiBasePeriods() != null && criteria.getCpiBasePeriods().size() > 0){
			descBuilder.append("\n");
			descBuilder.append(String.format("CPI Base Period: %s", StringUtils.join(criteria.getCpiBasePeriods(), ", ")));
		}
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
		}
		
		User creator = userDao.findById(userId);
		
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(taskType);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;		
	}

}
