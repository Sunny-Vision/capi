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
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.Batch;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.IndoorStaffProgress;
import capi.model.report.IndoorStaffProgressCriteria;
import capi.service.CommonService;

@Service("IndoorStaffProgressService")
public class IndoorStaffProgressService extends DataReportServiceBase{

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
	private PurposeDao purposeDao;

	@Override
	public String getFunctionCode() {
		return "RF9004";
	}

	private static final String[] headers = new String[]{
			"Reference Month & Year", "Purpose", "CPI based period", "Indoor officer Code",
			"Indoor officer Name", "No. of quotation records received from indoor allocator"
			, "No. of outstanding quotation records in data conversion stage", "No. of completed quotation records in data conversion stage",
			"No. of outstanding quotations records in verification stage"
		};
	
	private static final String[] headers1 = new String[] {
			"Reference Month & Year", "Purpose", "CPI based period", "Total no. of quotation records received from indoor allocator",
			"Total no. of outstanding quotation records in data conversion stage","% of outstanding quotation records in data conversion stage",
			"Total no. of completed quotation records in data conversion stage","%  of completed quotation records in data conversion stage",
			"Total no. of outstanding quotations records in verification stage"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		IndoorStaffProgressCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), IndoorStaffProgressCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		List<IndoorStaffProgress> results = dao.getIndoorStaffProgress(
				criteria.getUserId(), criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getBatch(), 
				startMonth, endMonth, criteria.getCpiBasePeriods());
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (IndoorStaffProgress data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getName());
			
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
		
		//add a sheet 
		List<IndoorStaffProgress> results1 = dao.getIndoorStaffOverAllProgress(
				criteria.getUserId(), criteria.getPurpose(), criteria.getCpiSurveyForm(), criteria.getBatch(), 
				startMonth, endMonth, criteria.getCpiBasePeriods());
		
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for(String header: headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		
		rowCnt = 1;
		for(IndoorStaffProgress data: results1) {
			row = sheet1.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
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
				sheet1.flushRows();
			}
		}
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Progress (By officers)");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Progress (Overall)");
		// End Generate Excel
		
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
		
		IndoorStaffProgressCriteria criteria = (IndoorStaffProgressCriteria)criteriaObject;
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
