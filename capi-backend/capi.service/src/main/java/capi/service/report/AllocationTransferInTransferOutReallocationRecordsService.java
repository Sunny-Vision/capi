package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentReallocationDao;
import capi.dal.AssignmentAdjustmentDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.AllocationTransferInTransferOutReallocationRecordsCriteria;
import capi.model.report.AssignmentAdjustmentReport;
import capi.model.report.AssignmentAdjustmentSummaryReport;
import capi.service.CommonService;

@Service("AllocationTransferInTransferOutReallocationRecordsService")
public class AllocationTransferInTransferOutReallocationRecordsService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;

	@Autowired
	private AssignmentAdjustmentDao assignmentAdjustmentDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		return "RF9031";
	}
	
	private final static String[] headers = {
			"Stage","Reference Month",	"Allocation Batch",	"From Officer",	"To Officer",	"Outlet ID",	"Outlet Name","District",	
			"TPU",	"Collection Method",	"No. of Quotations",	"Recommended by",	"Recommended Date", "Approved by",	"Approved Date"
	};
	
	private final static String[] headers1 = {
			"Stage",	"Reference Month",	"Allocation Batch",	"Collection Method",	"From Officer",	"To Officer",	
			"No. of Assignments transferred",	"No. of Quotations transferred",	"Recommended by",	"Recommended date",	"Approved by",
			"Approved Date",	"Reasons"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		AllocationTransferInTransferOutReallocationRecordsCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), AllocationTransferInTransferOutReallocationRecordsCriteria.class);
		Date fromMonth = commonService.getMonth(criteria.getFromMonth());
		Date toMonth = commonService.getMonth(criteria.getToMonth());
				
		List<AssignmentAdjustmentReport> result = null;
		List<AssignmentAdjustmentSummaryReport> result1 = null;
		if(criteria.getStage() == 1) {
			result = assignmentAdjustmentDao.getAssignmentAdjustmentReport(criteria.getOfficerIds(), criteria.getTeams(), criteria.getPurpose(), criteria.getCollectionMode(), criteria.getAllocationBatch(), fromMonth, toMonth);		
			result1 = assignmentAdjustmentDao.getAssignmentAdjustmentSummaryReport(criteria.getOfficerIds(), criteria.getTeams(), criteria.getPurpose(), criteria.getCollectionMode(), criteria.getAllocationBatch(), fromMonth, toMonth);
		}else if(criteria.getStage() == 2) {
			result = assignmentReallocationDao.getAssignmentReallocationReport(criteria.getOfficerIds(), criteria.getTeams(), criteria.getPurpose(), criteria.getCollectionMode(), criteria.getAllocationBatch(), fromMonth, toMonth);
			result1 = assignmentReallocationDao.getAssignmentReallocationSummaryReport(criteria.getOfficerIds(), criteria.getTeams(), criteria.getPurpose(), criteria.getCollectionMode(), criteria.getAllocationBatch(), fromMonth, toMonth);
		}
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for(AssignmentAdjustmentReport data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStage());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));

			//2018-01-08 cheung_cheng  "Allocation Batch" (all sheets) should show blank for Stage =  "Reallocation"
			//batch name 
			cell = row.createCell(++cellCnt);
			if(criteria.getStage() == 2)
				cell.setCellValue("");
			else
				cell.setCellValue(data.getBatchName());
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getFromUser());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getToUser());
			
//			cell = row.createCell(++cellCnt);
//			cell.setCellValue(data.getOutletCode());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getOutletId());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getOutletName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getDistrict());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getTpu());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getCollectionMethod());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getNumOfQuotation());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getRecommendBy());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatReportDate(data.getRecommendDate()));
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getApproveBy());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatReportDate(data.getApproveDate()));
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}

		workBook.setSheetName(workBook.getSheetIndex(sheet), "Listing of Assignment");
		
		SXSSFSheet sheet1 = workBook.createSheet("Summary");
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for(String header: headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;
		
		for(AssignmentAdjustmentSummaryReport data : result1) {
			row = sheet1.createRow(rowCnt);
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStage());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getBatchName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getCollectionMethod());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getFromUser());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getToUser());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getNumOfAssignments());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getNumOfQuotations());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getRecommendBy());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatReportDate(data.getRecommendDate()));
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getApproveBy());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatReportDate(data.getApproveDate()));
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getReasons());
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet1.flushRows();
			}
		}
		
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
		AllocationTransferInTransferOutReallocationRecordsCriteria criteria = (AllocationTransferInTransferOutReallocationRecordsCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if(criteria.getTeams()!=null && criteria.getTeams().size()>0){
			descBuilder.append("Team: ");
			for(String team : criteria.getTeams()){
				descBuilder.append(team+" ");
			}
			descBuilder.append("\n");
		}
		if(criteria.getOfficerIds()!=null && criteria.getOfficerIds().size()>0){
			List<User> users = userDao.getUsersByIds(criteria.getOfficerIds());
			descBuilder.append("Officers: ");
			String fieldOfficer = "";
			for (User user : users) {
				//2018-01-08 cheung_cheng The officer name shown in the filter should be consistent with the below "Description" (both shown in English name)
//				descBuilder.append(user.getChineseName()+" ");
				fieldOfficer += user.getStaffCode()+" - "+user.getEnglishName() +",";
			}
			fieldOfficer = fieldOfficer.replaceAll(",$", "");
			descBuilder.append(fieldOfficer);
			descBuilder.append("\n");
		}
		if(criteria.getPurpose()!=null && criteria.getPurpose().size()>0){
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for(Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
			descBuilder.append("\n");
		}
		if (criteria.getCollectionMode() != null && criteria.getCollectionMode().size() > 0){
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCollectionMode()){
				switch (form){
					case 1:codes.add("Field Visit"); break;
					case 2:codes.add("Telephone"); break;
					case 3:codes.add("Fax"); break;
					default:codes.add("Others"); break;
				}
				
			}
			descBuilder.append(String.format("Collection Mode: %s", StringUtils.join(codes, ", ")));
			descBuilder.append("\n");
		}
		if(criteria.getAllocationBatch()!=null && criteria.getAllocationBatch().size()>0){
			descBuilder.append(String.format("Allocation Batch: %s", StringUtils.join(criteria.getAllocationBatch(), ", ")));
			descBuilder.append("\n");
		}
		
		if (criteria.getStage() == 1)
			descBuilder.append("Stage: Transfer in/out");
		else
			descBuilder.append("Stage: Reallocation");

		descBuilder.append("\n");
		
		descBuilder.append(String.format("Period: %s", criteria.getFromMonth()+" - "+criteria.getToMonth()));
		
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
	public void createHeader(SXSSFRow row) {
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
}
