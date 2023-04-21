package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.DistrictDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.District;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.DynamicSummaryOfQuotations;
import capi.model.report.SummaryOfQuotations;
import capi.model.report.SummaryOfQuotationsCriteria;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("SummaryOfQuotationsService")
public class SummaryOfQuotationsService extends DataReportServiceBase{

	@Autowired
	private AssignmentDao dao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private PurposeDao purposeDao;

	@Override
	public String getFunctionCode() {
		return "RF9026";
	}

	private List<String> headers;
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfQuotationsCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfQuotationsCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		List<SummaryOfQuotations> progress = dao.getSummaryOfAssignments(startMonth, endMonth, criteria.getOutletType(), criteria.getDistrict(), criteria.getPurpose());
//		List<SummaryOfQuotations> purposes = dao.getSummaryOfQuotationsPurpose(startMonth, endMonth, criteria.getOutletType(), criteria.getDistrict(), criteria.getPurpose());
		
//		Map<String, String> purposeIdMap = new HashMap<>();
//		if(purposes != null){
//			for (SummaryOfQuotations row : purposes) {
//				String values = purposeIdMap.get(row.getId());
//				values = (values != null && !values.isEmpty()) ? values + ", " + row.getPurpose() : row.getPurpose();
//				purposeIdMap.put(row.getId(), values);
//			}
//		}
		
		List<DynamicSummaryOfQuotations> results = setDynamicSummaryOfQuotations(progress);
		
		List<District> district = new ArrayList<District>();
		if(criteria.getDistrict() != null && criteria.getDistrict().size() > 0) {
			district = districtDao.getOrderedDistrictsByIds(criteria.getDistrict(), true);
		}else {
			district = districtDao.getOrderedDistrictsByIds(null, true);
		}
		setHeader(district);
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Summary of Assignments");
		
		int rowCnt = 1;
		
		for (DynamicSummaryOfQuotations data : results){
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
//			if(purposeIdMap != null){
//				String items = purposeIdMap.get(data.getId());
//				cell.setCellValue(items);
//			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeName());
			
			for(District d : district) {
				cellCnt++;
				cell = row.createCell(cellCnt);
				int value = 0;
				for(Map.Entry<String,Integer> entry : data.getDistrcitAndQuantity().entrySet()) {
					if(entry.getKey().equals(d.getCode())) {
						value = entry.getValue();
						break;
					}
				}
				cell.setCellValue(value);
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		List<SummaryOfQuotations> progress1 = dao.getSummaryOfQuotations(startMonth, endMonth, criteria.getOutletType(), criteria.getDistrict(), criteria.getPurpose());
		List<DynamicSummaryOfQuotations> results1 = setDynamicSummaryOfQuotations(progress1);
		
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFRow row = sheet1.createRow(0);
		
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Summary of Quotations");
		
		int i = 0;
		for(String header : headers) {
			SXSSFCell cell = row.createCell(i);
			cell.setCellValue(header);
			i++;
		}
		
		rowCnt = 1;
		
		for (DynamicSummaryOfQuotations data : results1){
			
			row = sheet1.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
//			if(purposeIdMap != null){
//				String items = purposeIdMap.get(data.getId());
//				cell.setCellValue(items);
//			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeName());
			
			for(District d : district) {
				cellCnt++;
				cell = row.createCell(cellCnt);
				int value = 0;
				for(Map.Entry<String,Integer> entry : data.getDistrcitAndQuantity().entrySet()) {
					if(entry.getKey().equals(d.getCode())) {
						value = entry.getValue();
						break;
					}
				}
				cell.setCellValue(value);
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
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

		SummaryOfQuotationsCriteria criteria = (SummaryOfQuotationsCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		if (criteria.getOutletType() != null && criteria.getOutletType().size() > 0){
			descBuilder.append("\n");
			descBuilder.append(String.format("OutletType: %s", StringUtils.join(criteria.getOutletType(), ", ")));
		}
		if (criteria.getDistrict()!= null && criteria.getDistrict().size() > 0){
			List<District> districts = districtDao.getByIds(criteria.getDistrict().toArray(new Integer[0]));
			List<String> districtNames = new ArrayList<String>();
			for (District district : districts){
				districtNames.add(district.getEnglishName());
			}
			descBuilder.append("\n");
			descBuilder.append(String.format("District: %s", StringUtils.join(districtNames, ", ")));
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
	
	private void setHeader(List<District> district) {	
		String [] header = {"ReferenceMonth","Purpose","OutletType","Outlet Type English name"};
		List<String> temp = new ArrayList<String>();
		temp.addAll(Arrays.asList(header));
		for(District d : district) {
			temp.add(d.getCode());
		}
		temp.add("Total");
		
		headers = temp;
	}
	
	private List<DynamicSummaryOfQuotations> setDynamicSummaryOfQuotations(List<SummaryOfQuotations> results) {
		List<DynamicSummaryOfQuotations> list = new ArrayList<DynamicSummaryOfQuotations>();
		Map<String,Integer> innerMap = new HashMap<String,Integer>();
		
		SummaryOfQuotations temp = null;
		for(SummaryOfQuotations data : results) {
			if(temp != null && 
					(!data.getOutletTypeCode().equals(temp.getOutletTypeCode()) || !data.getReferenceMonth().equals(temp.getReferenceMonth())
						|| !data.getPurpose().equals(temp.getPurpose()))) {
				DynamicSummaryOfQuotations d = new DynamicSummaryOfQuotations(temp,innerMap);
				list.add(d);
				innerMap = new HashMap<String,Integer>();	
			}
			innerMap.put(data.getDistrictCode(), data.getQuantity());
			temp = data;
		}
		list.add(new DynamicSummaryOfQuotations(temp,innerMap));
		
		return list;	
	}
}
