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

import capi.dal.AssignmentDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.AssignmentAllocationSummaryCriteria;
import capi.model.report.AssignmentAllocationSummaryReport;
import capi.service.CommonService;

@Service("AssignmentAllocationSummaryService")
public class AssignmentAllocationSummaryService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9032";
	}
	
	private static final String[] headers = new String[]{
			"Reference Month", "Outlet ID", "Outlet Name", "District", "TPU", 
			"No. of Quotations", "From", "To", "Recommended By", "Recommended Date", 
			"Approved By", "Approved Date", "Allocation Batch", "Collection Method", "Stage",
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		AssignmentAllocationSummaryCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), AssignmentAllocationSummaryCriteria.class);
		Date fromMonth = commonService.getMonth(criteria.getFromMonth());
		Date toMonth = commonService.getMonth(criteria.getToMonth());
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		workBook.setSheetName(0, "Assignment allocation Summary");
		
		List<User> users = null;
		
		if(criteria.getOfficerIds()!=null && criteria.getOfficerIds().size()>0){
			users = userDao.getUsersByIds(criteria.getOfficerIds());
		} else {
			if(criteria.getTeams()!=null && criteria.getTeams().size()>0){
				users = userDao.searchOfficerByTeam(null, 0, 0, criteria.getTeams().toArray(new String[0]), null);
			} else {
				if((criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD) == 1 
						|| (criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD) == 2
						|| (criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_ALLOCATION_COORDINATOR) == 8 
						|| (criteria.getAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_BUSINESS_DATA_ADMINISTRATOR) == 256){
					users = userDao.findAll();
				} else if ((criteria.getAuthorityLevel() & 4) == 4){
					User tempUser = userDao.findById(criteria.getUserId());
					users = new ArrayList<User>();
					users.add(tempUser);
					users.addAll(tempUser.getSubordinates());
				}
			}
		}
		
		List<Integer> userIds = new ArrayList<Integer>();
		for(User user : users){
			userIds.add(user.getUserId());
		}
		
		List<AssignmentAllocationSummaryReport> data = assignmentDao.getAssignmentAllocationSummary(userIds
				, criteria.getTeams(), criteria.getPurpose(), criteria.getCollectionMode(), criteria.getAllocationBatch(), fromMonth, toMonth);
		
		int rowCnt = 1;
		for(AssignmentAllocationSummaryReport record: data){
			SXSSFRow row = sheet.createRow(rowCnt);
			int cellCnt = 0;
			
			//Reference Month
			SXSSFCell cell = row.createCell(cellCnt++);
			if(record.getReferenceMonth() != null){
				cell.setCellValue(commonService.formatShortMonth(record.getReferenceMonth()));
			}
			
			//Outlet Code -> Outlet Id
			cell = row.createCell(cellCnt++);
			if(record.getOutletId() != null){
				cell.setCellValue(String.valueOf(record.getOutletId()));
			}
			
			//Outlet Name
			cell = row.createCell(cellCnt++);
			if(record.getFirmName() != null){
				cell.setCellValue(record.getFirmName());
			}
			
			//District
			cell = row.createCell(cellCnt++);
			if(record.getDistrict() != null){
				cell.setCellValue(record.getDistrict());
			}
			
			//TPU
			cell = row.createCell(cellCnt++);
			if(record.getTpu() != null){
				cell.setCellValue(record.getTpu());
			}
			
			//No. of Quotations
			cell = row.createCell(cellCnt++);
			if(record.getNumOfQuotation() != null){
				cell.setCellValue(record.getNumOfQuotation());
			}
			
			//From
			cell = row.createCell(cellCnt++);
			if(record.getInFrom() != null && record.getInFromName() != null){
				cell.setCellValue(record.getInFrom() + " - " + record.getInFromName());
			}
			
			//To
			cell = row.createCell(cellCnt++);
			if(record.getOutTo() != null && record.getOutToName() != null){
				cell.setCellValue(record.getOutTo() + " - " + record.getOutToName());
			}
			
			//Recommended By
			cell = row.createCell(cellCnt++);
			if(record.getRecommendedBy() != null && record.getRecommendedByName() != null){
				cell.setCellValue(record.getRecommendedBy() + " - " + record.getRecommendedByName());
			}
			
			//Recommended Date
			cell = row.createCell(cellCnt++);
			if(record.getRecommendedDate() != null){
				cell.setCellValue(commonService.formatReportDate(record.getRecommendedDate()));
			}
			
			//Approved By
			cell = row.createCell(cellCnt++);
			if(record.getApprovedBy() != null  && record.getApprovedByName() != null ){
				cell.setCellValue(record.getApprovedBy() + " - " + record.getApprovedByName());
			}
			
			//Approved Date
			cell = row.createCell(cellCnt++);
			if(record.getApprovedDate() != null){
				cell.setCellValue(commonService.formatReportDate(record.getApprovedDate()));
			}
			
			//Allocation Batch
			cell = row.createCell(cellCnt++);
			if(record.getAllocationBatch() != null){
				cell.setCellValue(record.getAllocationBatch());
			}
			
			//Collection Method
			cell = row.createCell(cellCnt++);
			if(record.getCollectionMethod() != null){
				cell.setCellValue(SystemConstant.COLLECTION_METHOD[record.getCollectionMethod()]);
			}
			
			//Stage
			cell = row.createCell(cellCnt++);
			if(record.getStage() != null){
				cell.setCellValue(record.getStage());
			}
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
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
		// TODO Auto-generated method stub
		AssignmentAllocationSummaryCriteria criteria = (AssignmentAllocationSummaryCriteria)criteriaObject;
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
			descBuilder.append("Officer: ");
			String fieldOfficer = "";
			for (User user : users) {
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
		
		descBuilder.append(String.format("Period: %s", criteria.getFromMonth()+" - "+criteria.getToMonth()));
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		} else {
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
