package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.DistrictDao;
import capi.dal.FieldworkTimeLogDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.report.DynamicSummaryOfWorkload;
import capi.model.report.SummaryOfWorkload;
import capi.model.report.SummaryOfWorkloadCriteria;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("SummaryOfWorkloadService")
public class SummaryOfWorkloadService extends DataReportServiceBase{

	@Autowired
	private FieldworkTimeLogDao dao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Override
	public String getFunctionCode() {
		return "RF9016";
	}

	private List<String> headers;
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfWorkloadCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfWorkloadCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		endMonth = DateUtils.addMonths(endMonth, 1);
		endMonth = DateUtils.addDays(endMonth, -1);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String startMonths = format.format(startMonth);
		String endMonths = format.format(endMonth);
		
		List<SummaryOfWorkload> results = dao.getSummaryOfWorkload(startMonth, endMonth);
		
		List<DynamicSummaryOfWorkload> result = setDynamicSummaryOfWorkload(results);
		
		List<DistrictEditModel> district = districtDao.getAllDistrictCode();
		setHeader(district);
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (DynamicSummaryOfWorkload data : result){
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(startMonths);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(endMonths);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeName());
			
			for(DistrictEditModel d : district) {
				cellCnt++;
				cell = row.createCell(cellCnt);
				double value = 0;
				for(Map.Entry<String,Double> entry : data.getDistrictAndSpent().entrySet()) {
					if(entry.getKey().equals(d.getCode())) {
						DecimalFormat twoDForm = new DecimalFormat("#.####");
						value = Double.valueOf(twoDForm.format(entry.getValue()));
						break;
					}
				}
				cell.setCellValue(value);
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getAverage());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Workload Report");
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
		
		SummaryOfWorkloadCriteria criteria = (SummaryOfWorkloadCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
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

	@SuppressWarnings("unchecked")
	private void setHeader(List<DistrictEditModel> district) {	
		String[] header = {"Reference Month From","Reference Month To","Outlet Type","Outlet Type English name"};
		List<String>temp = new ArrayList<String>();
		temp.addAll(Arrays.asList(header));
		for(DistrictEditModel d : district) {
			temp.add(d.getCode());
		}
		temp.add("Average");
		headers = temp;
	}
	
	private List<DynamicSummaryOfWorkload> setDynamicSummaryOfWorkload(List<SummaryOfWorkload> results) {
		List<DynamicSummaryOfWorkload> list = new ArrayList<DynamicSummaryOfWorkload>();
		Map<String,Double> innerMap = new HashMap<String,Double>();
		
		int count = 0;
		Double average = 0.0;
		SummaryOfWorkload temp = null;
		BigDecimal ave = null;
		for(SummaryOfWorkload data : results) {
			if(temp != null && data.getId() != temp.getId()) {		
				if (count == 0)
				{
					ave = BigDecimal.valueOf(0);
				}
				else
				{
					ave = BigDecimal.valueOf(average).divide(BigDecimal.valueOf(count),4,RoundingMode.HALF_UP);
				}
				DynamicSummaryOfWorkload d = new DynamicSummaryOfWorkload(temp,innerMap,count, ave.doubleValue());
				list.add(d);
				innerMap = new HashMap<String,Double>();
				count = 0;
				average = 0.0;
			}
			innerMap.put(data.getDistrictCode(), data.getSpent());
			count+= data.getCountQuotationRecordId();
			average += (data.getCountQuotationRecordId()*data.getSpent());
			temp = data;
		}
		if (count == 0)
		{
			ave = BigDecimal.valueOf(0);
		}
		else
		{
			ave = BigDecimal.valueOf(average).divide(BigDecimal.valueOf(count),4,RoundingMode.HALF_UP);
		}
		list.add(new DynamicSummaryOfWorkload(temp,innerMap,count, ave.doubleValue()));		
		
		return list;
	}
}
