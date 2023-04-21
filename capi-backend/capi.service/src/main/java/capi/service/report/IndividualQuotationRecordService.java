package capi.service.report;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;

import capi.dal.PurposeDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.Outlet;
import capi.entity.Purpose;
import capi.entity.QuotationRecord;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.api.dataSync.AssignmentSyncData;
import capi.model.api.dataSync.OutletAttachmentSyncData;
import capi.model.report.IndividualQuotationRecordReport;
import capi.model.report.IndividualQuotationRecordReport1;
import capi.model.report.IndividualQuotationRecordReport2;
import capi.model.report.IndividualQuotationRecordReport3;
import capi.model.report.IndividualQuotationRecordReport4;
import capi.model.report.IndividualQuotationRecordReport5;
import capi.model.report.IndividualQuotationRecordReportImage;
import capi.model.report.JasperWorkbook;
import capi.model.report.IndividualQuotationRecordCriteria;
import capi.service.AppConfigService;
import capi.service.CommonService;
import net.sf.jasperreports.engine.JasperPrint;

@Service("IndividualQuotationRecordService")
public class IndividualQuotationRecordService extends DataReportServiceBase {

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AppConfigService appConfig;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9035";
	}

	private static final String headers[] = new String[] { "Reference No.", "Outlet Id", "Outlet Code", "Outlet Name",
			"Outlet Type", "Outlet Type Chinese Name", "Outlet Type English Name", "District Code",
			"District English Name", "District Council", "TPU Code", "BR Code", "Map Address", "Detail Address",
			"Last Contact", "Main Contact", "Telephone No.", "Fax No.", "Opening Start Time", "Opening End Time",
			"Opening Start Time2", "Opening End Time2", "Convenient Start Time", "Convenient End Time",
			"Convenient Start Time2", "Convenient End Time2", "Web Site", "Market / Non-market", "Outlet remarks",
			"Outlet Discount remarks", "Collection Method", "Default Major Location", "Indoor Market Name", "Latitude",
			"Longitude", "Use FR(Admin)", "Status", "CreatedDate", "CreatedBy" };

	private static final String headers2[] = new String[] { "Quotation record ID", "Reference Month", "Collection Date",
			"Reference Date", "Is Back No. quotation records", "Is Backtrack quotation records",
			"Original Quotation Record ID (for Backtrack & BackNo.)", "Is Product Change", "Is New Product",
			"Is New Recruitment", "Is New Outlet", "Quotation ID", "Assignment ID", "CPI Base Period", "Seasonality",
			"Outlet Code", 
			//"Outlet Name",
			"Outlet Type Code", "Outlet Type English Name", "District Code", "TPU Code",
			"Tour Record Id", "Firm Status", "Availability", "N Price", "S Price", "Is NS Price not applicable",
			"UOM value", "UOM Chinese Name & English Name", "Is Sub Price Used", "Reason", "Discount",
			"Discount remarks", "Price remarks", "Is collect FR", "FR", "Is FR in percentage", "Is consignment counter",
			//"Consignment Counter Name",
			"Consignment counter remarks", "Outlet Discount Remarks", "Category remarks",
			//"Contact person",
			"Is Point To Note Used", "Verification Remark", "PE Check Remark",
			"Responsible Field Officer Code", "Quotation record status", "Reject Reason", "Enumeration status",
			"Quotation Status", "Quotation State", "RUA since", "Batch Code", "Variety Code", "Purpose",
			"CPI Base Period", "Variety English Name", "Variety Chinese Name", "Ordinary / Tour form",
			"CPI Quotation Type", "Is ICP", "ICP Product Code", "ICP Product Name", "ICP Type (Unit)",
			"ICP Type (Quotation)", "Product ID", "Product Group Code", "Product Group English Name",
			"Product Group Chinese Name", "Country of Origin", "Product Attribute 1", "Product Attribute 2",
			"Product Attribute 3", "Product Attribute 4", "Product Attribute 5", "Product Remarks", "Product Barcode",
			"Product Position", "Is Outlet Information verified", "Is Category Information verified",
			"Is Quotation Information verified", "Verification Remarks", "Verification Reply",
			"Is visited for Verfication / Revisit", "PE Check Remarks", "CPI Form Type" };

	private static final String headers3[] = new String[] { "Quotation record ID", "Product ID", "Is Product Change",
			"Is New Product", "Product Type", "Country of Origin", "Product Attributes 1", "Product Attributes 2",
			"Product Attributes 3", "Product Attributes 4", "Product Attributes 5", "Product Attributes 6",
			"Product Attributes 7", "Product Barcode", "Product Remarks", "Product Position"

	};

	private static final String headers4[] = new String[] { "Quotation record ID", "Sub Price Record Id",
			"Sub Price Type Name", "N Price", "S Price", "Sub Price Record N Price", "Sub Price Record S Price",
			"Sub Price Record Discount" };

	private static final String headers5[] = new String[] { "Quotation record ID", "Tour Record Id", "N Price",
			"S Price", "Day1Price", "Day2Price", "Day3Price", "Day4Price", "Day5Price", "Day6Price", "Day7Price",
			"Day8Price", "Day9Price", "Day10Price", "Day11Price", "Day12Price", "Day13Price", "Day14Price",
			"Day15Price", "Day16Price", "Day17Price", "Day18Price", "Day19Price", "Day20Price", "Day21Price",
			"Day22Price", "Day23Price", "Day24Price", "Day25Price", "Day26Price", "Day27Price", "Day28Price",
			"Day29Price", "Day30Price", "Day31Price", "ExtraPrice1Name", "ExtraPrice1Value", "Is ExtraPrice1 Include",
			"ExtraPrice2Name", "ExtraPrice2Value", "Is ExtraPrice2 Include", "ExtraPrice3Name", "ExtraPrice3Value",
			"Is ExtraPrice3 Include", "ExtraPrice4Name", "ExtraPrice4Value", "Is ExtraPrice4 Include",
			"ExtraPrice5Name", "ExtraPrice5Value", "Is ExtraPrice5 Include" };

	private static final String headers6[] = new String[] { "Quotation record ID", "Point To Note Id", "Point To Note",
			"EffectiveDate", "ExpiryDate", "IsAllOutlet", "IsAllProduct", "IsAllQuotation", "IsAllVariety",
			"Outlet Code", "Product Id", "Quotation Id", "CPI Base Period", "Variety Code", "Is Outlet Point To Note",
			"Is Product Point To Note", "Is Quotation Point To Note", "Is Variety Point To Note" };

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		IndividualQuotationRecordCriteria criteria = (IndividualQuotationRecordCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();

		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes) {
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}

		if (criteria.getIsgetImage() != null && criteria.getIsgetImage() == true) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			descBuilder.append(String.format("Included Image: %s", "Ture"));
		} else {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			descBuilder.append(String.format("Included Image: %s", "False"));
		}

		if (criteria.getUnitId() != null && criteria.getUnitId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Unit> units = unitDao.getByIds(criteria.getUnitId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Unit unit : units) {
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ",")));
		}

		if (criteria.getAssignmentIds() != null && criteria.getAssignmentIds().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer assignmentId : criteria.getAssignmentIds()) {
				codes.add(assignmentId.toString());
			}
			descBuilder.append(String.format("AssignmentId: %s", StringUtils.join(codes, ", ")));
		}

		if (criteria.getQuotationId() != null && criteria.getQuotationId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer quotationId : criteria.getQuotationId()) {
				codes.add(quotationId.toString());
			}
			descBuilder.append(String.format("Quotation: %s", StringUtils.join(codes, ", ")));
		}

		if (criteria.getQuotationRecordId() != null && criteria.getQuotationRecordId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer quotationRecordId : criteria.getQuotationRecordId()) {
				codes.add(quotationRecordId.toString());
			}
			descBuilder.append(String.format("Quotation Record: %s", StringUtils.join(codes, ", ")));
		}

		if (descBuilder.length() > 0)
			descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getReferenceMonth()));

		User creator = userDao.findById(userId);
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(4);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);

		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;
	}

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria not defined");
		}

		IndividualQuotationRecordCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(),
				IndividualQuotationRecordCriteria.class);

		Date refMonth = null;
		Date end = null;
		String referenceMonth = criteria.getReferenceMonth();

		if (referenceMonth != null && referenceMonth != "") {
			refMonth = commonService.getMonth(criteria.getReferenceMonth());
			end = commonService.getMonth(criteria.getReferenceMonth());
			end = DateUtils.addMonths(end, 1);
			end = DateUtils.addDays(end, -1);
		}

		List<IndividualQuotationRecordReport> result = quotationRecordDao.getIndividualQuoationRecordReport(
				criteria.getPurpose(), criteria.getAssignmentIds(), criteria.getQuotationId(), criteria.getUnitId(),
				criteria.getQuotationRecordId(), refMonth);
		List<IndividualQuotationRecordReport1> result1 = quotationRecordDao.getIndividualQuoationRecordReport1(
				criteria.getPurpose(), criteria.getAssignmentIds(), criteria.getQuotationId(), criteria.getUnitId(),
				criteria.getQuotationRecordId(), refMonth);
		List<IndividualQuotationRecordReport2> result2 = quotationRecordDao.getIndividualQuoationRecordReport2(
				criteria.getPurpose(), criteria.getAssignmentIds(), criteria.getQuotationId(), criteria.getUnitId(),
				criteria.getQuotationRecordId(), refMonth);
		List<IndividualQuotationRecordReport3> result3 = quotationRecordDao.getIndividualQuotationReocrdReport3(
				criteria.getPurpose(), criteria.getAssignmentIds(), criteria.getQuotationId(), criteria.getUnitId(),
				criteria.getQuotationRecordId(), refMonth);
		List<IndividualQuotationRecordReport4> result4 = quotationRecordDao.getIndividualQuotationReocrdReport4(
				criteria.getPurpose(), criteria.getAssignmentIds(), criteria.getQuotationId(), criteria.getUnitId(),
				criteria.getQuotationRecordId(), refMonth);
		List<IndividualQuotationRecordReport5> result5 = quotationRecordDao.getIndividualQuotationReocrdReport5(
				criteria.getPurpose(), criteria.getAssignmentIds(), criteria.getQuotationId(), criteria.getUnitId(),
				criteria.getQuotationRecordId(), refMonth, end);

		// Start Generate Excel

		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet;
		SXSSFSheet sheet1;
		SXSSFSheet sheet2;
		SXSSFSheet sheet3;
		SXSSFSheet sheet4;
		SXSSFSheet sheet5;

		// Sheet 0
		/*
		 * sheet = workBook.getSheetAt(0);
		 * workBook.setSheetName(workBook.getSheetIndex(sheet), "Outlet Information");
		 * sheet = createSheetHeader(sheet, headers); fillSheet0(sheet, result);
		 */

		// Sheet 1
		sheet1 = workBook.getSheetAt(0);
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Quotation Record");
		sheet1 = createSheetHeader(sheet1, headers2);

		// Sheet 2
		sheet2 = workBook.createSheet();
		workBook.setSheetName(workBook.getSheetIndex(sheet2), "Product");
		sheet2 = createSheetHeader(sheet2, headers3);
		ArrayList<Integer> quotationRecordIdImage = new ArrayList<Integer>();
		quotationRecordIdImage = fillSheet2(sheet2, result2);

		// Sheet 3
		if (result3.size() > 0 && result3 != null) {
			// int totalSubPriceField = 0;
			int totalSubPriceField = 16;
			sheet3 = workBook.createSheet();
			workBook.setSheetName(workBook.getSheetIndex(sheet3), "Sub Price");
			List<IndividualQuotationRecordReport3> newResult3 = setDynamicSheet(result3);

			/*
			 * for (IndividualQuotationRecordReport3 r3 : newResult3) { if
			 * (r3.getSubPriceField() == null) { continue; } else { if
			 * (r3.getSubPriceField().size() > totalSubPriceField) {
			 * totalSubPriceField = r3.getSubPriceField().size(); } } // } }
			 */
			sheet3 = createSheetHeader(sheet3, headers4, totalSubPriceField);
			fillSheet3(sheet3, newResult3, totalSubPriceField);
		}

		// Sheet 4
		if (result4 != null && result4.size() > 0) {
			boolean generateTourSheet = false;
			for (IndividualQuotationRecordReport4 data : result4) {
				if (data.getTourRecordId() != null && !data.getTourRecordId().equals("")) {
					generateTourSheet = true;
					break;
				} else {
					generateTourSheet = false;
				}
			}
			if (generateTourSheet == true) {
				sheet4 = workBook.createSheet();
				workBook.setSheetName(workBook.getSheetIndex(sheet4), "Tour Form Details");
				sheet4 = createSheetHeader(sheet4, headers5);
				fillSheet4(sheet4, result4);
			}
		}
		// Sheet 5
		if (result5 != null && result5.size() > 0) {
			sheet5 = workBook.createSheet();
			workBook.setSheetName(workBook.getSheetIndex(sheet5), "Point To Note");
			sheet5 = createSheetHeader(sheet5, headers6);
			HashSet<Integer> pointToNote = new HashSet<Integer>();
			pointToNote = fillSheet5(sheet5, result5);
			fillSheet1(sheet1, result1, pointToNote);
		} else {
			HashSet<Integer> pointToNote = new HashSet<Integer>();
			fillSheet1(sheet1, result1, pointToNote);
		}

		// Output Excel
		try {
			String filename = UUID.randomUUID().toString();
			String fileBase = getFileBase() + "/" + filename;
			File dir = new File(fileBase);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String file = fileBase + "/" + "Information of Individual Quotation Records.xlsx";
			FileOutputStream fileOutputStream = new FileOutputStream(file);

			workBook.write(fileOutputStream);
			workBook.close();

			boolean isGetImage = criteria.getIsgetImage();

			if (isGetImage == true) {
				// List<IndividualQuotationRecordReportImage> imageResult =
				// quotationRecordDao.getIndividualQuotationReocrdReportImage(quotationRecordIdImage);

				List<IndividualQuotationRecordReportImage> imageResult = syncImageQuery(quotationRecordIdImage);
				if (imageResult != null && imageResult.size() > 0) {
					List<IndividualQuotationRecordReportImage> newImageResult = setDynamicImage(imageResult);

					for (IndividualQuotationRecordReportImage imagePath : newImageResult) {

						String assignmentDirName = fileBase + "/" + imagePath.getAssignmentId().toString();
						File assignmentDirPath = new File(assignmentDirName);
						if (!assignmentDirPath.exists()) {
							assignmentDirPath.mkdirs();
						}

						if (imagePath.getOutletPath() != null && imagePath.getOutletPath() != "") {
							String image = appConfig.getFileBaseLoc() + imagePath.getOutletPath();
							File f = new File(image);
							if (f.exists() && !f.isDirectory()) {
								String[] imageName = imagePath.getOutletPath().split("/");
								File d = new File(assignmentDirName + "/" + imageName[imageName.length - 1] + ".jpg");
								FileUtils.copyFile(f, d);
							}
						}
						if (imagePath.getImagePath().size() > 0) {
							for (String path : imagePath.getImagePath()) {
								String image = appConfig.getFileBaseLoc() + path;
								File f = new File(image);
								if (f.exists() && !f.isDirectory()) {
									String[] imageName = path.split("/");
									File d = new File(
											assignmentDirName + "/" + imageName[imageName.length - 1] + ".jpg");
									FileUtils.copyFile(f, d);
								}

							}
						}
					}

				}
			}
			File inputDirectory = new File(fileBase);
			File outputZip = new File(fileBase + ".zip");
			List<File> listFiles = new ArrayList();
			listFiles(listFiles, inputDirectory);

			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputZip));

			createZipFile(listFiles, inputDirectory, zipOutputStream);

			File originalFile = new File(fileBase + ".zip");

			if (originalFile.exists()) {
				File directory = new File(fileBase);
				deleteDir(new File(fileBase));
			}

			task.setPath(this.getFileRelativeBase() + "/" + filename + ".zip");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public void createHeader(SXSSFRow row) {
		int cnt = 0;
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

	public SXSSFSheet createSheetHeader(SXSSFSheet sheet, String[] headers) {
		int cnt = 0;

		SXSSFRow row = sheet.createRow(cnt);
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		return sheet;
	}

	public SXSSFSheet createSheetHeader(SXSSFSheet sheet, String[] headers, int totalSubPriceField) {
		int cnt = 0;

		SXSSFRow row = sheet.createRow(cnt);
		for (String header : headers) {

			if (cnt == (headers4.length - 3)) {
				for (int i = 0; i <= totalSubPriceField + 1; i++) {
					SXSSFCell cell = row.createCell(cnt);
					if (i < totalSubPriceField) {
						cell.setCellValue("Sub Price Field " + (i + 1));
						cnt++;
					}
				}
				SXSSFCell cell = row.createCell(cnt);
				cell.setCellValue(header);
				cnt++;
			} else {
				SXSSFCell cell = row.createCell(cnt);
				cell.setCellValue(header);
				cnt++;
			}
		}
		return sheet;
	}

	public void fillSheet0(SXSSFSheet sheet, List<IndividualQuotationRecordReport> result) throws Exception {
		int rowCnt = 1;
		for (IndividualQuotationRecordReport data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferNo());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletId());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletType());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeChName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeEngName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDistrictCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDistrictEngName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDistrictCouncil());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTpuCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBrCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getMapAddress());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDetailAddress());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getLastContact());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getMainContact());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTelNo());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getFaxNo());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOpenStartTime());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOpenEndTime());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOpenStartTime2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOpenEndTime2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConverientStartTime());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConvenientEndTime());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConverientStartTime2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConvenientEndTime2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getWebsite());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getyNonmarket() != null) {
				if (data.getyNonmarket() > 1) {
					cell.setCellValue("Non-Market");
				} else {
					cell.setCellValue("Market");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletRemarks());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletDiscountRemarks());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCollectionMethod());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDefaultMajorLocation());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIndoorMarketName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getLatitude());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getLongitude());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isUseFRAdmin());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStatus());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCreatedDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCreatedBy());

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
			// End Generate Sheet0
		}
	}

	public void fillSheet1(SXSSFSheet sheet1, List<IndividualQuotationRecordReport1> result1,
			HashSet<Integer> pointToNote) throws IOException {
		int rowCnt = 1;

		for (IndividualQuotationRecordReport1 data : result1) {
			SXSSFRow row = sheet1.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			if (data.getQuotationRecordId() != null) {
				cell.setCellValue(data.getQuotationRecordId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCollectionDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isBackNo());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isBackTrack());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOriginalQuotationRecordId() != null) {
				cell.setCellValue(data.getOriginalQuotationRecordId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsProductChange().equals("0")) {
				cell.setCellValue(false);

			} else {
				cell.setCellValue(true);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isNewProduct());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isNewRecruitment());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isNewOutlet());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationId() != null) {
				cell.setCellValue(data.getQuotationId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAssignmentId() != null) {
				cell.setCellValue(data.getAssignmentId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiPeriod());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSeasonality() == 1) {
				cell.setCellValue("All-time");
			} else if (data.getSeasonality() == 2) {
				cell.setCellValue("Summer");
			} else if (data.getSeasonality() == 3) {
				cell.setCellValue("Winter");
			} else if (data.getSeasonality() == 4) {
				cell.setCellValue("Occasional");
			} else {
				cell.setCellValue("");
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOutletCode() != null) {
				cell.setCellValue(data.getOutletCode());
			}

//			cellCnt++; 
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getOutletName());
			

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTpyeEngName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDistrictCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTpuCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getTourRecordId() != null) {
				cell.setCellValue(data.getTourRecordId().toString());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFirmStatus() != null) {
				if (data.getFirmStatus() == 1) {
					cell.setCellValue("Enumeration (EN)");
				} else if (data.getFirmStatus() == 2) {
					cell.setCellValue("Closed (CL)");
				} else if (data.getFirmStatus() == 3) {
					cell.setCellValue("Move (MV)");
				} else if (data.getFirmStatus() == 4) {
					cell.setCellValue("Not Suitable (NS)");
				} else if (data.getFirmStatus() == 5) {
					cell.setCellValue("Refusal (NR)");
				} else if (data.getFirmStatus() == 6) {
					cell.setCellValue("Wrong Outlet (WO)");
				} else if (data.getFirmStatus() == 7) {
					cell.setCellValue("Door Lock (DL)");
				} else if (data.getFirmStatus() == 8) {
					cell.setCellValue("Non-contact (NC)");
				} else if (data.getFirmStatus() == 9) {
					cell.setCellValue("In Progress (IP)");
				} else {
					cell.setCellValue("Duplication (DU)");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAvailability() == 1) {
				cell.setCellValue("Available");
			} else if (data.getAvailability() == 2) {
				cell.setCellValue("IP");
			} else if (data.getAvailability() == 3) {
				cell.setCellValue("有價無貨");
			} else if (data.getAvailability() == 4) {
				cell.setCellValue("缺貨");
			} else if (data.getAvailability() == 5) {
				cell.setCellValue("Not Suitable");
			} else if (data.getAvailability() == 6) {
				cell.setCellValue("回倉");
			} else if (data.getAvailability() == 7) {
				cell.setCellValue("無團");
			} else if (data.getAvailability() == 8) {
				cell.setCellValue("未返");
			} else if (data.getAvailability() == 9) {
				cell.setCellValue("New");
			} else {
				cell.setCellValue("");
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPrice() != null) {
				cell.setCellValue(data.getnPrice());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPrice() != null) {
				cell.setCellValue(data.getsPrice());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isSPricePeculiar());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getUomValue() != null) {
				cell.setCellValue(data.getUomValue());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (!data.getUomEngNameCName().equals(" - ")) {
				cell.setCellValue(data.getUomEngNameCName());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSubPriceRecordId() != null) {
				if (data.getSubPriceRecordId() > 0) {
					cell.setCellValue(true);
				} else if (data.getSubPriceRecordId() == 0) {
					cell.setCellValue(false);
				}
			} else {
				cell.setCellValue("");
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReason());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDiscount());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDiscountRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isCollectFR());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFr() != null) {
				cell.setCellValue(data.getFr());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isFRPercentage());

			
			cellCnt++; 
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isConsignmentCounter());
			

//			cellCnt++;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getConsignmentCounterName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConsignmentCounterRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletDiscountRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCategoryRemark());

			
//			cellCnt++; 
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getContactPerson());
			

			cellCnt++;
			cell = row.createCell(cellCnt);

			if (pointToNote.contains(Integer.parseInt(data.getQuotationRecordId()))) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVerificationRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPeCheckRemark());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationReocrdStatus());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRejectReason());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getEnumerationStatus() != null) {
				if (data.getEnumerationStatus() == 1) {
					cell.setCellValue("Enumeration (EN)");
				} else if (data.getEnumerationStatus() == 2) {
					cell.setCellValue("Closed (CL)");
				} else if (data.getEnumerationStatus() == 3) {
					cell.setCellValue("Move (MV)");
				} else if (data.getEnumerationStatus() == 4) {
					cell.setCellValue("Not Suitable (NS)");
				} else if (data.getEnumerationStatus() == 5) {
					cell.setCellValue("Refusal (NR)");
				} else if (data.getEnumerationStatus() == 6) {
					cell.setCellValue("Wrong Outlet (WO)");
				} else if (data.getEnumerationStatus() == 7) {
					cell.setCellValue("Door Lock (DL)");
				} else if (data.getEnumerationStatus() == 8) {
					cell.setCellValue("Non-contact (NC)");
				} else if (data.getEnumerationStatus() == 9) {
					cell.setCellValue("In Progress (IP)");
				} else {
					cell.setCellValue("Duplication (DU)");
				}

			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationStatus());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationState());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRuaDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getbCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getuCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPpCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getuEngName() != null) {
				cell.setCellValue(data.getuEngName());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVarietyChineseName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOrdinaryTourForm() != null) {
				// cell.setCellValue(data.getOrdinaryTourForm());
				if (data.getOrdinaryTourForm() > 1) {
					cell.setCellValue("Tour");
				} else {
					cell.setCellValue("Normal");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCpiQoutationType() != null) {
				cell.setCellValue(data.getCpiQoutationType());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isIcp());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIcpProductCodeQ());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIcpProductNameQ());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIcpTypeU());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIcpTypeQ());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getProductId() != null) {
				cell.setCellValue(data.getProductId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductGroupCode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductGroupEngName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductGroupCName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCountryOfOrigin());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (!data.getPa1Name().equals(":")) {
				cell.setCellValue(data.getPa1Name());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (!data.getPa2Name().equals(":")) {
				cell.setCellValue(data.getPa2Name());

			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (!data.getPa3Name().equals(":")) {
				cell.setCellValue(data.getPa3Name());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (!data.getPa4Name().equals(":")) {
				cell.setCellValue(data.getPa4Name());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (!data.getPa5Name().equals(":")) {
				cell.setCellValue(data.getPa5Name());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductRemarks());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductBarcode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductPosition());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isVerifyFirm());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isVerifyCategory());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isVerifyQuotation());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVerificationRemarks());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getVerificationReply());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.isVerficationRevisit());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPeCheckRemarks());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCapiFormType());

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet1.flushRows();
			}
		}
		// End Generate Sheet1
	}

	public ArrayList<Integer> fillSheet2(SXSSFSheet sheet2, List<IndividualQuotationRecordReport2> result2)
			throws Exception {
		int rowCnt = 1;

		ArrayList<Integer> quotationRecordIdImage = new ArrayList<Integer>();

		for (IndividualQuotationRecordReport2 data : result2) {
			SXSSFRow row = sheet2.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			if (data.getQuotationRecordId() != null) {
				quotationRecordIdImage.add(Integer.parseInt(data.getQuotationRecordId()));
				cell.setCellValue(data.getQuotationRecordId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getProductID() != null) {
				cell.setCellValue(data.getProductID());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIsProductChange());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIsNewProduct());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductType());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCountryofOrigin());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName() != null) {
				if (data.getpValue() != null) {
					cell.setCellValue(data.getSpecificationName() + ":" + data.getpValue());
				} else {
					cell.setCellValue(data.getSpecificationName() + ":");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName1() != null) {
				if (data.getpValue1() != null) {
					cell.setCellValue(data.getSpecificationName1() + ":" + data.getpValue1());
				} else {
					cell.setCellValue(data.getSpecificationName1() + ":");
				}
			}
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName2() != null) {
				if (data.getpValue2() != null) {
					cell.setCellValue(data.getSpecificationName2() + ":" + data.getpValue2());
				} else {
					cell.setCellValue(data.getSpecificationName2() + ":");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName3() != null) {
				if (data.getpValue3() != null) {
					cell.setCellValue(data.getSpecificationName3() + ":" + data.getpValue3());
				} else {
					cell.setCellValue(data.getSpecificationName3() + ":");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName4() != null) {
				if (data.getpValue4() != null) {
					cell.setCellValue(data.getSpecificationName4() + ":" + data.getpValue4());
				} else {
					cell.setCellValue(data.getSpecificationName4() + ":");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName5() != null) {
				if (data.getpValue5() != null) {
					cell.setCellValue(data.getSpecificationName5() + ":" + data.getpValue5());
				} else {
					cell.setCellValue(data.getSpecificationName5() + ":");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSpecificationName6() != null) {
				if (data.getpValue6() != null) {
					cell.setCellValue(data.getSpecificationName6() + ":" + data.getpValue6());
				} else {
					cell.setCellValue(data.getSpecificationName6() + ":");
				}
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductBarcode());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductRemarks());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductPosition());

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet2.flushRows();
			}
		}
		// End Generate Sheet2
		return quotationRecordIdImage;
	}

	public void fillSheet3(SXSSFSheet sheet3, List<IndividualQuotationRecordReport3> result3, int totalSubPriceField)
			throws Exception {
		int rowCnt = 1;

		for (IndividualQuotationRecordReport3 data : result3) {
			SXSSFRow row = sheet3.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			if (data.getQuotationReocrdId() != null) {
				cell.setCellValue(data.getQuotationReocrdId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSubPriceRecordId() != null) {
				cell.setCellValue(data.getSubPriceRecordId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getName());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPrice() != null) {
				cell.setCellValue(data.getnPrice());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPrice() != null) {
				cell.setCellValue(data.getsPrice());
			}

			if (cellCnt < (headers4.length - 3)) {
				ArrayList<String> subPriceField = data.getSubPriceField();
				if (subPriceField != null && subPriceField.size() > 0) {
					for (String subPriceFieldContent : subPriceField) {
						cellCnt++;
						cell = row.createCell(cellCnt);
						cell.setCellValue(subPriceFieldContent);
					}
				} else {
					cellCnt++;
					cell = row.createCell(cellCnt);
					cell.setCellValue(data.getSubNprice());
				}
			}

			if (cellCnt < ((totalSubPriceField + headers4.length) - 3)) {
				cellCnt = (totalSubPriceField + headers4.length) - 4;
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSubNprice() != null) {
				cell.setCellValue(data.getSubNprice());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSubSprice() != null) {
				cell.setCellValue(data.getSubSprice());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDiscount());

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet3.flushRows();
			}
		}
		// End Generate Sheet3
	}

	public void fillSheet4(SXSSFSheet sheet4, List<IndividualQuotationRecordReport4> result4) throws Exception {
		int rowCnt = 1;

		for (IndividualQuotationRecordReport4 data : result4) {
			SXSSFRow row = sheet4.createRow(rowCnt);

			if (data.getTourRecordId() != null) {

				// Input Row of Data start
				int cellCnt = 0;
				SXSSFCell cell = row.createCell(cellCnt);
				if (data.getQuotationRecordId() != null) {
					cell.setCellValue(data.getQuotationRecordId());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getTourRecordId() != null) {
					cell.setCellValue(data.getTourRecordId());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getnPrice() != null) {
					cell.setCellValue(data.getnPrice());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getsPrice() != null) {
					cell.setCellValue(data.getsPrice());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay1Price() != null) {
					cell.setCellValue(data.getDay1Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay2Price() != null) {
					cell.setCellValue(data.getDay2Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay3Price() != null) {
					cell.setCellValue(data.getDay3Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay4Price() != null) {
					cell.setCellValue(data.getDay4Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay5Price() != null) {
					cell.setCellValue(data.getDay5Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay6Price() != null) {
					cell.setCellValue(data.getDay6Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay7Price() != null) {
					cell.setCellValue(data.getDay7Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay8Price() != null) {
					cell.setCellValue(data.getDay8Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay9Price() != null) {
					cell.setCellValue(data.getDay9Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay10Price() != null) {
					cell.setCellValue(data.getDay10Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay11Price() != null) {
					cell.setCellValue(data.getDay11Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay12Price() != null) {
					cell.setCellValue(data.getDay12Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay13Price() != null) {
					cell.setCellValue(data.getDay13Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay14Price() != null) {
					cell.setCellValue(data.getDay14Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay15Price() != null) {
					cell.setCellValue(data.getDay15Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay16Price() != null) {
					cell.setCellValue(data.getDay16Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay17Price() != null) {
					cell.setCellValue(data.getDay17Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay18Price() != null) {
					cell.setCellValue(data.getDay18Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay19Price() != null) {
					cell.setCellValue(data.getDay19Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay20Price() != null) {
					cell.setCellValue(data.getDay20Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay21Price() != null) {
					cell.setCellValue(data.getDay21Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay22Price() != null) {
					cell.setCellValue(data.getDay22Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay23Price() != null) {
					cell.setCellValue(data.getDay23Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay24Price() != null) {
					cell.setCellValue(data.getDay24Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay25Price() != null) {
					cell.setCellValue(data.getDay25Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay26Price() != null) {
					cell.setCellValue(data.getDay26Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay27Price() != null) {
					cell.setCellValue(data.getDay27Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay28Price() != null) {
					cell.setCellValue(data.getDay28Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay29Price() != null) {
					cell.setCellValue(data.getDay29Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay30Price() != null) {
					cell.setCellValue(data.getDay30Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getDay31Price() != null) {
					cell.setCellValue(data.getDay31Price());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getExtraPrice1Name());

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getExtraPrice1Value() != null) {
					cell.setCellValue(data.getExtraPrice1Value());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIsExtraPrice1Count() != null) {
					if (data.getIsExtraPrice1Count() == true) {
						cell.setCellValue("Y");
					} else {
						cell.setCellValue("N");

					}
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getExtraPrice2Name());

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getExtraPrice2Value() != null) {
					cell.setCellValue(data.getExtraPrice2Value());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIsExtraPrice2Count() != null) {
					if (data.getIsExtraPrice2Count() == true) {
						cell.setCellValue("Y");
					} else {
						cell.setCellValue("N");

					}
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getExtraPrice3Name());

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getExtraPrice3Value() != null) {
					cell.setCellValue(data.getExtraPrice3Value());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIsExtraPrice3Count() != null) {
					if (data.getIsExtraPrice3Count() == true) {
						cell.setCellValue("Y");
					} else {
						cell.setCellValue("N");

					}
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getExtraPrice4Name());

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getExtraPrice4Value() != null) {
					cell.setCellValue(data.getExtraPrice4Value());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIsExtraPrice4Count() != null) {
					if (data.getIsExtraPrice4Count() == true) {
						cell.setCellValue("Y");
					} else {
						cell.setCellValue("N");

					}
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getExtraPrice5Name());

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getExtraPrice5Value() != null) {
					cell.setCellValue(data.getExtraPrice5Value());
				}

				cellCnt++;
				cell = row.createCell(cellCnt);
				if (data.getIsExtraPrice5Count() != null) {
					if (data.getIsExtraPrice5Count() == true) {
						cell.setCellValue("Y");
					} else {
						cell.setCellValue("N");

					}
				}

				rowCnt++;
				if (rowCnt % 2000 == 0) {
					sheet4.flushRows();
				}
			}
		}
		// End Generate Sheet4
	}

	public HashSet<Integer> fillSheet5(SXSSFSheet sheet5, List<IndividualQuotationRecordReport5> result5)
			throws Exception {
		HashSet<Integer> pointToNote = new HashSet<Integer>();
		int rowCnt = 1;

		for (IndividualQuotationRecordReport5 data : result5) {
			SXSSFRow row = sheet5.createRow(rowCnt);

			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			if (data.getQuotationRecordId() != null) {
				pointToNote.add(Integer.parseInt(data.getQuotationRecordId()));
				cell.setCellValue(data.getQuotationRecordId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPointToNoteId() != null) {
				cell.setCellValue(data.getPointToNoteId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNote());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getEffectiveDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getExpiryDate());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsAllOutlet() != null && data.getIsAllOutlet() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsAllProduct() != null && data.getIsAllProduct() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsAllQuotation() != null && data.getIsAllQuotation() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsAllVariety() != null && data.getIsAllVariety() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			/*
			 * cellCnt++; cell = row.createCell(cellCnt); if (data.getOutletId()
			 * != null) { cell.setCellValue(data.getOutletId()); } else {
			 * cell.setCellValue("Outlet"); }
			 */

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFirmCode() != null) {
				cell.setCellValue(data.getFirmCode());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getProductId() != null) {
				cell.setCellValue(data.getProductId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationId() != null) {
				cell.setCellValue(data.getQuotationId());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCpiBasePeriod() != null) {
				cell.setCellValue(data.getCpiBasePeriod());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getUnitCode() != null) {
				cell.setCellValue(data.getUnitCode());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsOutlet() != null && data.getIsOutlet() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsProduct() != null && data.getIsProduct() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsQuotation() != null && data.getIsQuotation() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getIsVariety() != null && data.getIsVariety() == true) {
				cell.setCellValue(true);
			} else {
				cell.setCellValue(false);
			}

			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet5.flushRows();
			}
		}
		// End Generate Sheet5

		return pointToNote;
	}

	private List<IndividualQuotationRecordReport3> setDynamicSheet(List<IndividualQuotationRecordReport3> results) {
		List<IndividualQuotationRecordReport3> list = new ArrayList<IndividualQuotationRecordReport3>();
		ArrayList<String> subPriceField = new ArrayList<String>();
		IndividualQuotationRecordReport3 temp = null;

		if (results != null && results.size() > 0) {
			for (IndividualQuotationRecordReport3 data : results) {
				String subPriceFieldContent = "";
				if (data.getFieldName() != null) {
					if (data.getColumnValue() != null) {
						subPriceFieldContent = data.getFieldName() + ":" + data.getColumnValue();
					} else {
						subPriceFieldContent = data.getFieldName() + ":";
					}
				}
				if (temp != null && (!(data.getSubPriceRecordId() != null
						&& data.getSubPriceRecordId().equals(temp.getSubPriceRecordId())))) {
					temp.setSubPriceField(subPriceField);
					list.add(temp);
					subPriceField = new ArrayList<String>();
				}
				subPriceField.add(subPriceFieldContent);
				temp = data;
			}
			temp.setSubPriceField(subPriceField);
			list.add(temp);
		}
		return list;
	}

	private List<IndividualQuotationRecordReportImage> setDynamicImage(
			List<IndividualQuotationRecordReportImage> results) {
		List<IndividualQuotationRecordReportImage> list = new ArrayList<IndividualQuotationRecordReportImage>();
		ArrayList<String> imagePath = new ArrayList<String>();
		IndividualQuotationRecordReportImage temp = null;
		if (results != null && results.size() > 0) {
			for (IndividualQuotationRecordReportImage data : results) {
				if (temp != null && (!(data.getAssignmentId() != null
						&& data.getAssignmentId().equals(temp.getAssignmentId())))) {
					temp.setImagePath(imagePath);
					// temp.setOutletPath(data.getOutletPath());
					list.add(temp);
					imagePath = new ArrayList<String>();
				}

				if (data.getOutletAttachmentPath() != null && data.getOutletAttachmentPath() != "") {
					imagePath.add(data.getOutletAttachmentPath());
				}
				if (data.getPhoto1Path() != null && data.getPhoto1Path() != "") {
					imagePath.add(data.getPhoto1Path());
				}
				if (data.getPhoto1Path() != null && data.getPhoto1Path() != "") {
					imagePath.add(data.getPhoto1Path());
				}
				temp = data;
			}
			temp.setImagePath(imagePath);
			list.add(temp);
		}
		return list;
	}

	public void deleteDir(File file) throws IOException {
		FileUtils.deleteQuietly(file);
	}

	private void createZipFile(List<File> listFile, File inputDirectory, ZipOutputStream zipOutputStream)
			throws IOException {

		for (File file : listFile) {
			String filePath = file.getCanonicalPath();
			int lengthDirectoryPath = inputDirectory.getCanonicalPath().length();
			int lengthFilePath = file.getCanonicalPath().length();

			// Get path of files relative to input directory.
			String zipFilePath = filePath.substring(lengthDirectoryPath + 1, lengthFilePath);

			ZipEntry zipEntry = new ZipEntry(zipFilePath);
			zipOutputStream.putNextEntry(zipEntry);

			FileInputStream inputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = inputStream.read(bytes)) >= 0) {
				zipOutputStream.write(bytes, 0, length);
			}
			inputStream.close();
			zipOutputStream.closeEntry();
		}
		zipOutputStream.close();
	}

	private List<File> listFiles(List<File> listFiles, File inputDirectory) throws IOException {

		File[] allFiles = inputDirectory.listFiles();
		for (File file : allFiles) {
			if (file.isDirectory()) {
				listFiles(listFiles, file);
			} else {
				listFiles.add(file);
			}
		}
		return listFiles;
	}

	private List<IndividualQuotationRecordReportImage> syncImageQuery(List<Integer> quotationRecordIdImage) {
		List<IndividualQuotationRecordReportImage> entities = new ArrayList<IndividualQuotationRecordReportImage>();
		if (quotationRecordIdImage.size() > 2000) {
			List<Integer> ids = quotationRecordIdImage.subList(0, 2000);
			entities.addAll(syncImageQuery(ids));

			List<Integer> remainIds = quotationRecordIdImage.subList(2000, quotationRecordIdImage.size());
			entities.addAll(syncImageQuery(remainIds));
		} else if (quotationRecordIdImage.size() > 0) {
			return quotationRecordDao
					.getIndividualQuotationReocrdReportImage(quotationRecordIdImage.toArray(new Integer[0]));
		}

		return entities;
		// List<IndividualQuotationRecordReportImage> imageResult =
		// quotationRecordDao.getIndividualQuotationReocrdReportImage(quotationRecordIdImage);

	}
}
