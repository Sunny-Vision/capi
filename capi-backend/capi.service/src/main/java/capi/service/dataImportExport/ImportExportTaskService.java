package capi.service.dataImportExport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ImportExportTaskDao;
import capi.dal.ImportExportTaskDefinitionDao;
import capi.dal.NotificationDao;
import capi.dal.ProductGroupDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UserDao;
import capi.entity.ImportExportTask;
import capi.entity.ImportExportTaskDefinition;
import capi.entity.Notification;
import capi.entity.SubPriceType;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.dataImportExport.ImportTaskList;
import capi.service.AppConfigService;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("ImportExportTaskService")
public class ImportExportTaskService extends BaseService{
	
	@Autowired
	private AppConfigService config;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDefinitionDao definitionDao;
	
	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Transactional
	public ImportExportTask createImportTask(InputStream file, int taskNo, Integer userId, Integer productGroupId, String referenceMonth, String effectiveDate, String cpiBasePeriod) throws IOException, ParseException{
		ImportExportTaskDefinition def = definitionDao.getDefinitionByTaskNo(taskNo);
		User user = userDao.findById(userId);
		String importPath = saveImportFile(file, taskNo);
		ImportExportTask task = new ImportExportTask();
		task.setFilePath(importPath);
		task.setStartDate(new Date());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setTaskType(SystemConstant.TASK_TYPE_IMPORT);
		task.setCpiBasePeriod(cpiBasePeriod);
		task.setTaskDefinition(def);
		if (!StringUtils.isEmpty(effectiveDate)){
			task.setEffectiveDate(commonService.getDate(effectiveDate));
		}
		if (productGroupId != null){
			task.setProductGroup(productGroupDao.findById(productGroupId));
		}
		
		if (!StringUtils.isEmpty(referenceMonth)){
			Date month = commonService.getMonth(referenceMonth);
			if (month != null){
				task.setSurveyMonth(surveyMonthDao.getSurveyMonthByReferenceMonth(month));
			}
			task.setReferenceMonth(month);
		}
		
		task.setUser(user);
		taskDao.save(task);
		taskDao.flush();
		return task;
	}
	
	@Transactional
	public ImportExportTask createExportTask(int taskNo, Integer userId, Integer productGroupId, String referenceMonth, Integer subPriceTypeId, String cpiBasePeriod, 
			 String timeLogUserIds, String timeLogPeriodStart, String timeLogPeriodEnd, Integer purposeId) throws IOException, ParseException{
		ImportExportTaskDefinition def = definitionDao.getDefinitionByTaskNo(taskNo);
		User user = userDao.findById(userId);
		ImportExportTask task = new ImportExportTask();
		task.setStartDate(new Date());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setTaskType(SystemConstant.TASK_TYPE_EXPORT);
		task.setTaskDefinition(def);
		task.setCpiBasePeriod(cpiBasePeriod);
		if (productGroupId != null){
			task.setProductGroup(productGroupDao.findById(productGroupId));
		}
		
		if (subPriceTypeId != null){
			SubPriceType type = subPriceTypeDao.findById(subPriceTypeId);
			task.setSubPriceType(type);
		}
		
		if (!StringUtils.isEmpty(referenceMonth)){
			Date month = commonService.getMonth(referenceMonth);
			if (month != null){
				task.setSurveyMonth(surveyMonthDao.getSurveyMonthByReferenceMonth(month));
			}
			task.setReferenceMonth(month);
		}
		
		if (StringUtils.isNotBlank(timeLogPeriodStart) && StringUtils.isNotBlank(timeLogPeriodEnd)){
			task.setTimeLogDate(timeLogPeriodStart + "," + timeLogPeriodEnd);
		}
		
		if (StringUtils.isNotBlank(timeLogUserIds)){
			task.setTimeLogUserId(timeLogUserIds);
		}
		
		if (purposeId != null){
			task.setPurposeId(purposeId);
		}
			
		task.setUser(user);
		taskDao.save(task);
		taskDao.flush();
		return task;
	}
	
	@Transactional
	public void updateTaskStatus(int taskId, String status){
		this.updateTaskStatus(taskId, status, null);
	}
	
	@Transactional
	public void updateTaskStatus(int taskId, String status, String exception){
		ImportExportTask task = taskDao.findById(taskId);
		task.setStatus(status);
		if (status.equals(SystemConstant.TASK_STATUS_FINISHED)){
			task.setFinishedDate(new Date());
			Notification notify = new Notification();
			notify.setUser(task.getUser());
			notify.setSubject(messageSource.getMessage("N00001", new Object[]{task.getTaskType()}, Locale.ENGLISH));
			notify.setContent(messageSource.getMessage("N00002", 
					new Object[]{
						task.getTaskType().toLowerCase(),
						task.getImportExportTaskId(),
						task.getTaskDefinition().getTaskName(), 
						commonService.formatDateTime(task.getFinishedDate())
					}, Locale.ENGLISH)
				);	
			notificationDao.save(notify);
		}
		
		if (status.equals(SystemConstant.TASK_STATUS_FAILED)){
			task.setErrorMessage(exception);
			task.setFinishedDate(new Date());
			Notification notify = new Notification();
			notify.setUser(task.getUser());
			notify.setSubject(messageSource.getMessage("N00003", new Object[]{task.getTaskType()}, Locale.ENGLISH));
			notify.setContent(messageSource.getMessage("N00004", 
						new Object[]{
						task.getTaskType().toLowerCase(),
						task.getImportExportTaskId(),
						task.getTaskDefinition().getTaskName(),
						commonService.formatDateTime(task.getFinishedDate())
					}, Locale.ENGLISH)
				);	
			notificationDao.save(notify);
		}
				
		taskDao.save(task);
		taskDao.flush();
	}
	
	
	public String saveImportFile(InputStream stream, int taskNo) throws IOException{
		String baseLocation = config.getImportFileLoc() + "/" + taskNo;
		File dir = new File(baseLocation);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String filename = UUID.randomUUID().toString()+".xlsx";		
		Path path = FileSystems.getDefault().getPath(dir.getAbsolutePath(), filename);
		Files.copy(stream, path);
		return  "/" + taskNo + "/" + filename;		
	}
	
	public List<ImportExportTaskDefinition> getImportDefinition(){
		return definitionDao.getImportDefinition();
	}
	
	public List<ImportExportTaskDefinition> getExportDefinition(){
		return definitionDao.getExportDefinition();
	}
	
	public ImportExportTaskDefinition getImportExportTaskDefinitionByTaskNo(Integer taskNo) {
		return definitionDao.getDefinitionByTaskNo(taskNo);
	}
	
	public ImportExportTask getImportExportTask(Integer id) {
		return taskDao.findById(id);
	}
	
	public DatatableResponseModel<ImportTaskList> getImportTaskList(DatatableRequestModel requestModel){
		Order order = this.getOrder(requestModel, "importExportTaskId", "t.startDate","taskName","t.finishedDate","status","errorMessage");
		
		String search = requestModel.getSearch().get("value");
		
		List<ImportTaskList> result = taskDao.getImportTaskList(search, requestModel.getStart(), requestModel.getLength(), order);
		
		DatatableResponseModel<ImportTaskList> response = new DatatableResponseModel<ImportTaskList>();
		response.setDraw(requestModel.getDraw());
		response.setData(result);
		Long recordTotal = taskDao.countImportTaskList("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = taskDao.countImportTaskList(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	@Deprecated
	public DatatableResponseModel<ImportTaskList> getExportTaskList(DatatableRequestModel requestModel){
		return getExportTaskList(requestModel, null);
	}
	public DatatableResponseModel<ImportTaskList> getExportTaskList(DatatableRequestModel requestModel, Integer userId){
		Order order = this.getOrder(requestModel, "importExportTaskId", "t.startDate","taskName","taskNo", "t.finishedDate","status","errorMessage");
		
		String search = requestModel.getSearch().get("value");
		
		List<ImportTaskList> result = taskDao.getExportTaskList(search, requestModel.getStart(), requestModel.getLength(), order, userId);
		
		DatatableResponseModel<ImportTaskList> response = new DatatableResponseModel<ImportTaskList>();
		response.setDraw(requestModel.getDraw());
		response.setData(result);
		Long recordTotal = taskDao.countExportTaskList("", userId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = taskDao.countExportTaskList(search, userId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	
	public File getExportFileById(Integer id){
		ImportExportTask task = taskDao.findById(id);
		File file = new File(config.getExportFileLoc() + task.getFilePath());
		return file;		
	}
	
	@Transactional
	public void houseKeepExportTask(){
		List<ImportExportTask> tasks = taskDao.getObsoletedTask();
		String basePath = config.getExportFileLoc();
		for (ImportExportTask task : tasks){
			File file = new File(basePath+task.getFilePath());
			if (file.exists()){
				file.delete();
			}
			taskDao.delete(task);
		}
		taskDao.flush();		
	}
}
