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

import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitStatisticDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.CrossCheck;
import capi.model.report.CrossCheckCriteria;
import capi.service.CommonService;

@Service("CrossCheckService")
public class CrossCheckService extends DataReportServiceBase{

	@Autowired
	private UnitStatisticDao dao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PurposeDao purposeDao;

	@Override
	public String getFunctionCode() {
		return "RF9006";
	}
	
	private static final String[] headers = {"Reference Month","Purpose","CPI based period","Group Code","Group name","Item Code","Item name",
			"Variety Code","Variety name","Cross check group","CPI Compilation Method","PR"
	};

	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		CrossCheckCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), CrossCheckCriteria.class);
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		List<CrossCheck> results = dao.getCrossCheck(criteria.getPurpose(), refMonth
				, criteria.getCrossCheckGroups());
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (CrossCheck data : results){
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
			cell.setCellValue(data.getGroupCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCrossCheckGroup());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompilationMethod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPr()!=null){
				cell.setCellValue(data.getPr());
			}
			
			//BigDecimal.valueOf(number).setScale(2,RoundingMode.HALF_UP).doubleValue()
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Cross check list");
		
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
		
		CrossCheckCriteria criteria = (CrossCheckCriteria)criteriaObject;
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
		
		descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));
		
		if (criteria.getCrossCheckGroups() != null && criteria.getCrossCheckGroups().size() > 0){
			descBuilder.append("\n");
			descBuilder.append(String.format("Cross Check Group: %s", StringUtils.join(criteria.getCrossCheckGroups(), ", ")));
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
