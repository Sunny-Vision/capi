package capi.service.report;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.dal.VwProductFullSpecDao;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.entity.VwProductFullSpec;
import capi.model.SystemConstant;
import capi.model.report.ProductReviewCriteria;
import capi.model.report.ProductReviewReport;
import capi.service.CommonService;

@Service("ProductReviewService")
public class ProductReviewService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private VwProductFullSpecDao vwProductFullSpecDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		return "RF9009";
	}
	
	private static final String[] headers = {"No.","Product Id","Product Category Id","Product Category Code","Product Category Chinese Name",
			"Product Category English Name","Country of Origin","Product Attributes 1","Product Attributes 2","Product Attributes 3","Product Attributes 4",
			"Product Attributes 5","Product Attributes 6","Product Attributes 7","Product Attributes 8","Product Attributes 9","Product Attributes 10",
			"Product Attributes 11","Product Attributes 12","Product Attributes 13","Product Attributes 14","Product Attributes 15","Product Attributes 16",
			//2017-01-05 cheung_cheng change Column of Product attribute set up from 18 to 25
			"Product Attributes 17","Product Attributes 18","Product Attributes 19","Product Attributes 20","Product Attributes 21","Product Attributes 22",
			"Product Attributes 23","Product Attributes 24","Product Attributes 25",
			"No. of quotations related","Product create date","Product Reviewed","Last modify date",
			"Last modify by (Officer Code)","Last modify by (Officer English Name)"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defind");
		}

		Integer MAX_ATTRIBUTE_SEQ = 24;
		ProductReviewCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), ProductReviewCriteria.class);
		
		Date refMonth = null;
		if(!criteria.getRefMonth().isEmpty())
			refMonth = commonService.getMonth(criteria.getRefMonth());
		
//		List<ProductReviewReport> progress = productDao.getProductReviewReport(criteria.getProductGroup(), refMonth, criteria.getReviewed(), criteria.getPurpose());
		List<Map<String, Object>> progress = productDao.getProductReviewReport(criteria.getProductGroup(), refMonth, criteria.getReviewed(), criteria.getPurpose());
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
//		System.out.println(progress);
		for (Map<String, Object> data : progress){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("productId") != null){
				cell.setCellValue(String.valueOf(data.get("productId")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("productGroupId") != null){
				cell.setCellValue(String.valueOf(data.get("productGroupId")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("productGroupCode") != null){
				cell.setCellValue(String.valueOf(data.get("productGroupCode")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("productGroupChineseName") != null){
				cell.setCellValue(String.valueOf(data.get("productGroupChineseName")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("productGroupEnglishName") != null){
				cell.setCellValue(String.valueOf(data.get("productGroupEnglishName")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("countyOfOrigin") != null){
				cell.setCellValue(String.valueOf(data.get("countyOfOrigin")));
			}

			//2018-01-05 cheung_cheng change Column of Product attribute set up from 18 to 25
//			List<VwProductFullSpec> productAttributes = vwProductFullSpecDao.GetAllByProductId(data.getProductId(),25);
//			System.out.println(new Date() + "productAttributes : " + productAttributes.toString());
//			for(VwProductFullSpec p : productAttributes) {
//				cellCnt++;
//				cell = row.createCell(cellCnt);
//				String n = p.getSpecificationName();
//				String v = p.getValue() == null?"":p.getValue();
//				cell.setCellValue(n+" : "+v);
//			}

//			if(productAttributes.size() < 25)
//				cellCnt+= 25-productAttributes.size();
			cellCnt++;
			Map<Integer, String> productAttributesMap = new HashMap<Integer, String>();
			
			//Get productAttrubutes N by Sequence
			for(String alias : data.keySet()){
				if(alias.toLowerCase().startsWith("productAttributes".toLowerCase())){
					int id = Integer.valueOf(StringUtils.substringAfter(alias, "productAttributes"));
					productAttributesMap.put(id, String.valueOf(data.get(alias)));
				}
			}
			
			List<String> productAttributes = new ArrayList<String>();
			if(!productAttributesMap.isEmpty()){
				for(int i=1; i<productAttributesMap.size()+1; i++) {
					productAttributes.add(productAttributesMap.get(i));
				}
			}
			int productRowCount = cellCnt;
			if(!productAttributes.isEmpty()){
				for (String productAttribute : productAttributes) {
					cell = row.createCell(productRowCount++);
					if(!productAttribute.isEmpty()){
						cell.setCellValue(productAttribute);
					}
				}
			}
			cellCnt+=MAX_ATTRIBUTE_SEQ;
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//2018-01-05 cheung_cheng no of quotation should be in number
			if(data.get("noOfQuotation") != null && !data.get("noOfQuotation").toString().equals("")){
				cell.setCellValue(Integer.parseInt(data.get("noOfQuotation").toString()));
			}else{
				cell.setCellValue(0);
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("productCreateDate") != null){
				cell.setCellValue(myFormat.format(data.get("productCreateDate")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//2018-01-05 cheung_cheng "Product Reviewed" should use "Y" or "N" to indicate (0 - "N", 1 - "Y")
			if(data.get("productReviewed") != null){
				switch (String.valueOf(data.get("productReviewed"))) {
					case "0" : cell.setCellValue("N");
					break;
					case "1" : cell.setCellValue("Y");
					break;				
					default : //do nothing
					break;
				}
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("lastModifyDate") != null){
				cell.setCellValue(myFormat.format(data.get("lastModifyDate")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("lastModifyBy") != null){
				cell.setCellValue(String.valueOf(data.get("lastModifyBy")));
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.get("lastModifyUser") != null){
				cell.setCellValue(String.valueOf(data.get("lastModifyUser")));
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Product Review List");
		
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
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId)throws Exception{
		// TODO Auto-generated method stub
		ProductReviewCriteria criteria = (ProductReviewCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if(criteria.getPurpose()!=null && criteria.getPurpose().size()>0){
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for(Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
			descBuilder.append("\n");
		}
		
		if(criteria.getProductGroup()!=null&&criteria.getProductGroup().size()>0){
			List<ProductGroup> productGroups = productGroupDao.getEntityByIds(criteria.getProductGroup());
			List<String> codes = new ArrayList<String>();
			for(ProductGroup productGroup : productGroups){
				codes.add(productGroup.getChineseName());
			}
			descBuilder.append(String.format("Product Category: %s", StringUtils.join(codes, ", ")));
			descBuilder.append("\n");
		}
		
		descBuilder.append(String.format("Product create period: %s", criteria.getRefMonth()));
		descBuilder.append("\n");
		
		if(criteria.getReviewed()!=null){
			switch (criteria.getReviewed()) {
	        case "Y":  descBuilder.append(String.format("Reviewed: %s", "Yes"));
	                 break;
	        case "N":  descBuilder.append(String.format("Reviewed: %s", "No"));
	                 break;
	        case "A":  descBuilder.append(String.format("Reviewed: %s", "All"));
	                 break;
			}
		}
		descBuilder.append("\n");
		
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
