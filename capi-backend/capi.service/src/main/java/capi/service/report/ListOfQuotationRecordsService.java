package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.ItemDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.UserDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.ListOfQuotationRecords;
import capi.model.report.ListOfQuotationRecordsCriteria;
import capi.service.CommonService;

@Service("ListOfQuotationRecordsService")
public class ListOfQuotationRecordsService extends DataReportServiceBase {

	@Autowired
	private IndoorQuotationRecordDao dao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private ItemDao itemDao;

	@Autowired
	private SubPriceRecordDao subPriceRecordDao;

	@Override
	public String getFunctionCode() {
		return "RF9007";
	}

	private static final String[] headers = new String[] { "No.", "Indoor Quotation record ID",
			"Field Quotation record ID", "Quotation ID", "Reference Month", "Purpose", "Reference Date",
			"CPI based period", "Group Code", "Group name", "Item Code", "Item name", "Variety Code", "Variety name",
			"Quotation Status", "Data Conversion Status", "Outlet Code", 
			//"Outlet Name", 
			"District Code", "Outlet Type",
			"Outlet Type English Name", "Product ID", "Country of origin", "Product Attributes 1",
			"Product Attributes 2", "Product Attributes 3", "Product Attributes 4", "Product Attributes 5",
			"Survey NPrice", "Survey SPrice", "Last Edited N Price", "Last Edited S Price", "Previous Edited N Price",
			"Previous Edited S Price", "Current Edited N Price", "Current Edited S Price", "PR Nprice", "PR Sprice",
			"Quotation record price reason", "Price remarks", "Keep number", "CPI Series", "Outlier case",
			"Outlier remarks", "Seasonal item", "Product change", "本月拎上月價不等於上月價", "RUA since",
			"Edited price is null and (Survey price is not null or price remarks is not null) and not using Sub-price type",
			"Imputed Unit's average price", "Imputed Quotation's average price", "Responsible Field officer Code",
			"Allocated Indoor officer", "Whether fieldwork is needed", "CPI Quotation Type", "Batch code",
			"Sub Price Record Id", "Sub price type", "Sub Price Record N Price", "Sub Price Record S Price", "Sub Price Record Discount"};

	private String[] newHeaders;

	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria not defined");
		}

		ListOfQuotationRecordsCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(),
				ListOfQuotationRecordsCriteria.class);

		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		List<ListOfQuotationRecords> progress = dao.getListOfQuotationRecordsReport(criteria.getPurpose(),
				criteria.getItemId(), criteria.getCpiSurveyForm(), criteria.getDataCollection(), startMonth, endMonth,
				criteria.getImputedVariety(), criteria.getImputedQuotation(), criteria.getNotEqual(),
				criteria.getRuaCase(), criteria.getPriceCondition());

		HashMap<Integer, List<String>> a = new HashMap<Integer, List<String>>();
		Integer totalSubPriceRecord = 0;

		for (ListOfQuotationRecords mapOfSubFieldPrice : progress) {
			if(mapOfSubFieldPrice.getSubPriceRecordId()!= null){
				List<String> l = subPriceRecordDao.getSubPriceFieldAndValue(mapOfSubFieldPrice.getSubPriceRecordId());
				if (totalSubPriceRecord < l.size()) {
					totalSubPriceRecord = l.size();
				}
				if (l != null){
					a.put(Integer.parseInt(mapOfSubFieldPrice.getSubPriceRecordId()), l);
				}
			}
			
		}
		newHeaders = addHeader(totalSubPriceRecord);

		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		int rowCnt = 1;

		for (ListOfQuotationRecords data : progress) {
			SXSSFRow row = sheet.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIndoorQuotationRecordId());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationRecordId());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationId());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurposeCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCPIBasePeriod());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupEnglishName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemEnglishName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitEnglishName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationStatus());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIndoorQuotationRecordStatus());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletFirmCode());

//			cellCnt++;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getOutletName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDistrictCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletType());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeEnglishName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductId());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCountryOfOrigin());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(":".equals(data.getProductAttr1())){
				cell.setCellValue("");
			} else {
				cell.setCellValue(data.getProductAttr1());
			}
			

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(":".equals(data.getProductAttr2())){
				cell.setCellValue("");
			} else {
				cell.setCellValue(data.getProductAttr2());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(":".equals(data.getProductAttr3())){
				cell.setCellValue("");
			} else {
				cell.setCellValue(data.getProductAttr3());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(":".equals(data.getProductAttr4())){
				cell.setCellValue("");
			} else {
				cell.setCellValue(data.getProductAttr4());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(":".equals(data.getProductAttr5())){
				cell.setCellValue("");
			} else {
				cell.setCellValue(data.getProductAttr5());
			}
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSurveyNPrice() != null)
				cell.setCellValue(data.getSurveyNPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSurveySPrice() != null)
				cell.setCellValue(data.getSurveySPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastNPrice() != null)
				cell.setCellValue(data.getLastNPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastSPrice() != null)
				cell.setCellValue(data.getLastSPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPreviousNPrice() != null)
				cell.setCellValue(data.getPreviousNPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPreviousSPrice() != null)
				cell.setCellValue(data.getPreviousSPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCurrentNPrice() != null)
				cell.setCellValue(data.getCurrentNPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCurrentSPrice() != null)
				cell.setCellValue(data.getCurrentSPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCurrentNPrice() != null && data.getPreviousNPrice() != null) {
				BigDecimal n = BigDecimal.valueOf(data.getCurrentNPrice())
						.divide(BigDecimal.valueOf(data.getPreviousNPrice()), 5, RoundingMode.HALF_UP);
				cell.setCellValue(n.multiply(BigDecimal.valueOf(100)).doubleValue());
			}
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCurrentSPrice() != null && data.getPreviousSPrice() != null) {
				BigDecimal n = BigDecimal.valueOf(data.getCurrentSPrice())
						.divide(BigDecimal.valueOf(data.getPreviousSPrice()), 5, RoundingMode.HALF_UP);
				cell.setCellValue(n.multiply(BigDecimal.valueOf(100)).doubleValue());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQrReason());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQrRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getKeepNumber());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiSeries());

			cellCnt++;
			cell = row.createCell(cellCnt);
			// cell.setCellValue(data.getIsOutlier());
			if (data.getIsOutlier().equals("0")) {
				cell.setCellValue(false);
			} else {
				cell.setCellValue(true);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutlierRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getuSeasonality());

			cellCnt++;
			cell = row.createCell(cellCnt);
			// cell.setCellValue(data.getIsProductChange());
			if(data.getIsProductChange() != null){
				if (data.getIsProductChange().equals("0")) {
					cell.setCellValue(false);
				} else {
					cell.setCellValue(true);
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastSPrice() != null && data.getPreviousSPrice() != null) {
				if (data.getLastSPrice().doubleValue() != data.getPreviousSPrice().doubleValue()) {
					cell.setCellValue("Y");
				} else {
					cell.setCellValue("N");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRUADate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			String checked = checkPriceIsNotNull(data.getCurrentSPrice(),data.getSurveySPrice(),data.getQrRemark(),data.getCountSubPriceRecord());
					cell.setCellValue(checked);

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getVarietyPrice() != null){
				if(data.getVarietyPrice() > 0){
					cell.setCellValue(data.getVarietyPrice());
				} 
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getQuotationPrice() != null){
				if(data.getQuotationPrice() > 0){
					cell.setCellValue(data.getQuotationPrice());
				} 
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUserId() != null){
				User creator = userDao.findById(Integer.valueOf(data.getUserId()));
				cell.setCellValue(creator.getStaffCode());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOfficer());

			cellCnt++;
			cell = row.createCell(cellCnt);
			// cell.setCellValue(data.getIsNoField());
			if(data.getIsNoField() != null){
				if (data.getIsNoField().equals("0")) {
					cell.setCellValue(true);
				} else {
					cell.setCellValue(false);
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiQuotationType());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBatchCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubPriceRecordId());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubPriceTypeName());

//			cellCnt++;
//			cell = row.createCell(cellCnt);
			if(data.getSubPriceRecordId() != null){
				int subId = Integer.valueOf(data.getSubPriceRecordId());
				//if (data.getSubPriceRecordId() != null) {
					if (a.containsKey(subId) && a.get(subId).size() > 0) {
						List<String> subRecord = a.get(subId);
						for (String sub : subRecord) {
							cellCnt++;
							cell = row.createCell(cellCnt);
							cell.setCellValue(sub);
						}
	
					}
				//}
			} else {
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue("");
			}
			
			if (cellCnt < (( totalSubPriceRecord + headers.length) - 4)) {
				cellCnt = (totalSubPriceRecord + headers.length) - 4;				
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpNPrice() != null)
				cell.setCellValue(data.getSpNPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpSPrice() != null)
				cell.setCellValue(data.getSpSPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSpDiscount() != null)
			cell.setCellValue(data.getSpDiscount());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
		}

		workBook.setSheetName(workBook.getSheetIndex(sheet), "Outstanding cases");

		// Output Excel
		try {
			String filename = UUID.randomUUID().toString() + ".xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();

			task.setPath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		reportTaskDao.save(task);
		reportTaskDao.flush();

	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {

		ListOfQuotationRecordsCriteria criteria = (ListOfQuotationRecordsCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();

		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));

		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0) {
			descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes) {
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		if (criteria.getItemId() != null && criteria.getItemId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Item> items = itemDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Item item : items) {
				codes.add(item.getCode() + " - " + item.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}
		if (criteria.getCpiSurveyForm() != null && criteria.getCpiSurveyForm().size() > 0) {
			descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCpiSurveyForm()) {
				switch (form) {
				case 1:
					codes.add("Market");
					break;
				case 2:
					codes.add("Supermarket");
					break;
				case 3:
					codes.add("Batch");
					break;
				default:
					codes.add("Others");
					break;
				}

			}
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(codes, ", ")));
		}

		if (criteria.getDataCollection() != null && criteria.getDataCollection().length() > 0) {
			descBuilder.append("\n");
			if ("Y".equals(criteria.getDataCollection()))
				descBuilder.append(String.format("Data Collection: %s", "Field"));
			else if ("N".equals(criteria.getDataCollection()))
				descBuilder.append(String.format("Data Collection: %s", "No Field"));
		}
		if (criteria.getImputedVariety() != null && criteria.getImputedVariety().length() > 0) {
			descBuilder.append("\n");
			if ("Y".equals(criteria.getImputedVariety()))
				descBuilder.append(String.format("Imputed Variety's  average price: %s", "Not Blank"));
			else if ("N".equals(criteria.getImputedVariety()))
				descBuilder.append(String.format("Imputed Variety's  average price: %s", "Blank"));
		}
		if (criteria.getImputedQuotation() != null && criteria.getImputedQuotation().length() > 0) {
			descBuilder.append("\n");
			if ("Y".equals(criteria.getImputedQuotation()))
				descBuilder.append(String.format("Imputed Quotation's average price: %s", "Not Blank"));
			else if ("N".equals(criteria.getImputedQuotation()))
				descBuilder.append(String.format("Imputed Quotation's average price: %s", "Blank"));
		}
		if (criteria.getNotEqual() != null && criteria.getNotEqual().length() > 0) {
			descBuilder.append("\n");
			if ("Y".equals(criteria.getNotEqual()))
				descBuilder.append(String.format("Previous S Price <> Last S Price: %s", "Y"));
			else if ("N".equals(criteria.getNotEqual()))
				descBuilder.append(String.format("Previous S Price <> Last S Price: %s", "N"));
			else if ("B".equals(criteria.getNotEqual())) {
				descBuilder.append(String.format("Previous S Price <> Last S Price: %s", "Blank"));
			}
		}
		if (criteria.getRuaCase() != null && criteria.getRuaCase().length() > 0) {
			descBuilder.append("\n");
			if ("Y".equals(criteria.getRuaCase()))
				descBuilder.append(String.format("RUA case: %s", "Y"));
			else if ("N".equals(criteria.getRuaCase()))
				descBuilder.append(String.format("RUA cases: %s", "Blank"));
		}
		if (criteria.getPriceCondition() != null && criteria.getPriceCondition().length() > 0) {
			descBuilder.append("\n");
			if ("Y".equals(criteria.getPriceCondition()))
				descBuilder.append(String.format("Edited price is null ...: %s", "Y"));
			else if ("N".equals(criteria.getPriceCondition()))
				descBuilder.append(String.format("Edited price is null ...: %s", "No"));
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
		for (String header : newHeaders) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

	public String[] addHeader(int totalSubPriceRecord) {
		int total = headers.length + totalSubPriceRecord;
		String[] newHeaders = new String[total];
		for (int i = 0; i < headers.length; i++) {
			if (i == (headers.length - 3)) {
				for (int f = 0; f < totalSubPriceRecord + 1; f++) {
					if (f != totalSubPriceRecord) {
						newHeaders[(i + f)] = "Sub price field and value" + (f+1);
					} else {
						newHeaders[(i + f)] = headers[i];
					}
				}
			} else {
				if (i > headers.length - 3) {
					if (i == headers.length) {
						break;
					}
					newHeaders[i + totalSubPriceRecord] = headers[i];
					
				} else {
					newHeaders[i] = headers[i];
				}
			}
		}

		return newHeaders;
	}
	
	public String checkPriceIsNotNull(Double getCurrentSPrice, Double getSurveySPrice, String getQrRemark, Integer getSubPriceRecordId) {
		String checked = "N";
		Double currentSPrice = getCurrentSPrice;
		Double surveySPrice = getSurveySPrice;
		String qrRemark = getQrRemark;
		Integer subPriceTypeName = getSubPriceRecordId;
		
		if(currentSPrice == null && (surveySPrice != null || (qrRemark != null && !qrRemark.equals("") )) && subPriceTypeName == 0)
		//if(currentSPrice == null && surveySPrice != null && qrRemark != null && subPriceTypeName == 0)
		//if(currentSPrice == null && surveySPrice == null && qrRemark == null && subPriceTypeName == 0)
		{
			checked = "Y";
		}
		return checked;
	}
}
