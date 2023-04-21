package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.BatchDao;
import capi.dal.GroupDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.SubGroupDao;
import capi.dal.UserDao;
import capi.entity.Batch;
import capi.entity.Group;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.SubGroup;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.IndoorStaffIndividualProgress;
import capi.model.report.IndoorStaffIndividualProgressCriteria;
import capi.service.CommonService;

@Service("IndoorStaffIndividualProgressService")
public class IndoorStaffIndividualProgressService extends DataReportServiceBase{

	@Autowired
	private IndoorQuotationRecordDao dao;
	
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
	
	@Override
	public String getFunctionCode() {
		return "RF9003";
	}

	private static final String[] headers = new String[]{
			"Indoor officer Name","Reference Month & Year", "Purpose", "CPI based period", "Group Code","Group Chinese Name", "Group English Name",
			"Sub-group code","Sub-group Chinese Name","Sub-group English Name",
			"No. of quotation records received from indoor allocator", "No. of outstanding quotation records in data conversion stage",
			"No. of completed quotation records in data conversion stage","No. of outstanding quotations records in verification stage"
	};
	
	private static final String[] headers1 = new String[]{
			"Indoor officer Name","Reference Month & Year", "Purpose", "CPI based period", "Group Code","Group Chinese Name", "Group English Name",
			"No. of quotation records received from indoor allocator", "No. of outstanding quotation records in data conversion stage",
			"No. of completed quotation records in data conversion stage","No. of outstanding quotations records in verification stage"
	};
	
	private static final String[] headers2 = new String[]{
			"Indoor officer Name","Reference Month & Year", "Purpose", "CPI based period", 
			"Total no. of quotation records received from indoor allocator", "Total no. of outstanding quotation records in data conversion stage",
			"% of outstanding quotation records in data conversion stage","Total no. of completed quotation records in data conversion stage",
			"%  of completed quotation records in data conversion stage","Total no. of outstanding quotations records in verification stage"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		IndoorStaffIndividualProgressCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), IndoorStaffIndividualProgressCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		List<IndoorStaffIndividualProgress> results = dao.getIndoorStaffIndividualProgress(criteria.getUserId(), criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getBatch(), startMonth, endMonth, criteria.getGroup(), criteria.getSubGroup(), criteria.getCpiBasePeriods());
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (IndoorStaffIndividualProgress data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUsername());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupChineseName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupEnglishName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupChineseName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupEnglishName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getAllocator());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConversion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getComplete());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVerification());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		List<IndoorStaffIndividualProgress> results1 = dao.getIndoorStaffIndividualProgressGroup(criteria.getUserId(), criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getBatch(), startMonth, endMonth, criteria.getGroup(), criteria.getSubGroup(), criteria.getCpiBasePeriods());
		
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for(String header: headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;
		
		for (IndoorStaffIndividualProgress data : results1){
			row = sheet1.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUsername());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupChineseName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupEnglishName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getAllocator());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConversion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getComplete());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVerification());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet1.flushRows();
			}
		}
		
		List<IndoorStaffIndividualProgress> results2 = dao.getIndoorStaffIndividualProgressOverall(criteria.getUserId(), criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getBatch(), startMonth, endMonth, criteria.getGroup(), criteria.getSubGroup(), criteria.getCpiBasePeriods());
		
		SXSSFSheet sheet2 = workBook.createSheet();
		row = sheet2.createRow(0);
		cnt = 0;
		for(String header: headers2) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;
		
		for (IndoorStaffIndividualProgress data : results2){
			row = sheet2.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUsername());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getAllocator());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConversion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPercentageOustanding());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getComplete());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPercentageComplete());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVerification());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet2.flushRows();
			}
		}
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Progress (SubGroup)");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Progress (Group)");
		workBook.setSheetName(workBook.getSheetIndex(sheet2), "Progress (Overall)");
		
		// Output Excel 
		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			task.setPath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		IndoorStaffIndividualProgressCriteria criteria = (IndoorStaffIndividualProgressCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getUserId() != null && criteria.getUserId().size() > 0) {
			List<User> users = userDao.getUsersByIds(criteria.getUserId());
			if(users != null && users.size() > 0) {
				List<String> userStr = new ArrayList<String>();
				for(User user : users) {
					String str = user.getStaffCode() + " - " + user.getEnglishName();
					userStr.add(str);
				}
				descBuilder.append(String.format("Indoor Staff: %s", StringUtils.join(userStr, ", ")));
			}
		}
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
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

	@Override
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
	
	
}
