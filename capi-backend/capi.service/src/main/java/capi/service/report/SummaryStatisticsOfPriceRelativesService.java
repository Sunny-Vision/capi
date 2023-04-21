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

import capi.dal.ItemDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitStatisticDao;
import capi.dal.UserDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.entity.VwOutletTypeShortForm;
import capi.model.SystemConstant;
import capi.model.report.SummaryStatisticsOfPriceRelativesCriteria;
import capi.model.report.SummaryStatisticsOfPriceRelativesReport;
import capi.service.CommonService;

@Service("SummaryStatisticsOfPriceRelativesService")
public class SummaryStatisticsOfPriceRelativesService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UnitStatisticDao unitStatisticDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		return "RF9033";
	}
	
	private final static String[] headers = {
			"No.",
			"Variety Code",
			"Variety Chinese Name",
			"Variety English Name",
			"Reference Month",
			"Purpose",
			"CPI base period",
			"Section Code",
			"Section Chinese Name",
			"Section English Name",
			"Group Code",
			"Group Chinese Name",
			"Group English Name",
			"SubGroup Code",
			"SubGroup Chinese Name",
			"SubGroup English Name",
			"Item Code",
			"Item Chinese Name",
			"Item English Name",
			"OutletType Code",
			"OutletType Chinese Name",
			"OutletType English Name",
			"Outlet Type",
			"SubItem Code",
			"SubItem Chinese Name",
			"SubItem English Name",
			"Section PR",
			"Group PR",
			"Sub Group PR",
			"Item PR",
			"Outlet Type PR",
			"Sub Item PR",
			"Variety PR",
			"Variety PR Standard deviation",
			"Variety PR (Max)",
			"Variety PR (Min)",
			"Variety Average Price (Current Month)",
			"Variety Average Price (Previous Month)",
			"Variety Average Price (Last Have Average Price)",
			"CPI Compilation method"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryStatisticsOfPriceRelativesCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryStatisticsOfPriceRelativesCriteria.class);
		Date fromMonth = commonService.getMonth(criteria.getFromMonth());
		Date toMonth = commonService.getMonth(criteria.getToMonth());
		
		List<SummaryStatisticsOfPriceRelativesReport> result = unitStatisticDao.getByPriceRelatives(fromMonth, toMonth, criteria.getPurpose(), criteria.getItemId(), criteria.getOutletTypeId(), criteria.getCpiSurveyForm());
		
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Summary Statistics");
		
		for(SummaryStatisticsOfPriceRelativesReport data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);
			
			cell = row.createCell(++cellCnt);
			//2018-01-09 cheung_cheng wrong lookup
//			cell.setCellValue(data.getStaffCode());
			cell.setCellValue(data.getUnitCode()); //variety Code
			
			cell = row.createCell(++cellCnt);
//			cell.setCellValue(data.getStaffChinName());
			cell.setCellValue(data.getUnitChinName()); //variety Chinese name
			
			cell = row.createCell(++cellCnt);
//			cell.setCellValue(data.getStaffEngName());
			cell.setCellValue(data.getUnitEngName()); //variety English name
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSectionCode());			
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSectionChinName());
						
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSectionEngName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getGroupCode());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getGroupChinName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getGroupEngName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSubGroupCode());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSubGroupChinName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSubGroupEngName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getItemCode());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getItemChinName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getItemEngName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getOutletTypeChinName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getOutletTypeEngName());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getOutletType());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSubItemCode());
			
			cell = row.createCell(++cellCnt);
			cell.setCellValue(data.getSubItemChinName());
			
			cell = row.createCell(++cellCnt);
			//2018-01-09 cheung_cheng wrong lookup
//			cell.setCellValue(data.getSubGroupEngName());
			cell.setCellValue(data.getSubItemEngName());
			
			cell = row.createCell(++cellCnt);
			if(data.getSectionPR()!=null)
			cell.setCellValue(data.getSectionPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getGroupPR()!=null)
			cell.setCellValue(data.getGroupPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getSubGroupPR()!=null)
			cell.setCellValue(data.getSubGroupPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getItemPR()!=null)
			cell.setCellValue(data.getItemPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getOutletTypePR()!=null)
			cell.setCellValue(data.getOutletTypePR());
			
			cell = row.createCell(++cellCnt);
			if(data.getSubItemPR()!=null)
			cell.setCellValue(data.getSubItemPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitPR()!=null)
			cell.setCellValue(data.getUnitPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitStandardDeviationPR()!=null)
			cell.setCellValue(data.getUnitStandardDeviationPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitMaxPR()!=null)
			cell.setCellValue(data.getUnitMaxPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitMinPR()!=null)
			cell.setCellValue(data.getUnitMinPR());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitAvgCurrentSPrice()!=null)
			cell.setCellValue(data.getUnitAvgCurrentSPrice());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitAvgLastSPrice()!=null)
			cell.setCellValue(data.getUnitAvgLastSPrice());
			
			cell = row.createCell(++cellCnt);
			if(data.getUnitLastHasPriceAvgCurrSPrcie()!=null)
			cell.setCellValue(data.getUnitLastHasPriceAvgCurrSPrcie());
			
			cell = row.createCell(++cellCnt);
			if(data.getCompilationMethod()!=null)
			cell.setCellValue(data.getCompilationMethod());
				
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
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
		SummaryStatisticsOfPriceRelativesCriteria criteria = (SummaryStatisticsOfPriceRelativesCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s", criteria.getFromMonth()+" - "+criteria.getToMonth()));
		
		if(criteria.getPurpose()!=null && criteria.getPurpose().size()>0){
			descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for(Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		if(criteria.getItemId()!=null && criteria.getItemId().size()>0){
			descBuilder.append("\n");
			List<Item> items = itemDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for(Item item : items){
				codes.add(item.getCode() + " - " + item.getEnglishName());
			}
			descBuilder.append(String.format("Item: %s", StringUtils.join(codes, ", ")));
		}
		if(criteria.getOutletTypeId()!=null && criteria.getOutletTypeId().size()>0){
			descBuilder.append("\n");
			List<VwOutletTypeShortForm> outletTypes = outletTypeDao.getByIds(criteria.getOutletTypeId().toArray(new String[0]));
			List<String> codes = new ArrayList<String>();
			for(VwOutletTypeShortForm outletType : outletTypes){
				codes.add(outletType.getShortCode() + " - " + outletType.getChineseName());
			}
			descBuilder.append(String.format("Outlet Type: %s", StringUtils.join(codes, ", ")));
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
