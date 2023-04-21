package capi.service.report;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
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

import capi.dal.GroupStatisticDao;
import capi.dal.ItemStatisticDao;
import capi.dal.OutletTypeStatisticDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationStatisticDao;
import capi.dal.ReportTaskDao;
import capi.dal.SectionStatisticDao;
import capi.dal.SubGroupStatisticDao;
import capi.dal.SubItemStatisticDao;
import capi.dal.UnitDao;
import capi.dal.UnitStatisticDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryGroupStatistic;
import capi.model.report.SummaryItemStatistic;
import capi.model.report.SummaryOutletTypeStatistic;
import capi.model.report.SummaryQuotationStatistic;
import capi.model.report.SummarySectionStatistic;
import capi.model.report.SummaryStatisticCriteria;
import capi.model.report.SummarySubGroupStatistic;
import capi.model.report.SummarySubItemStatistic;
import capi.model.report.SummaryUnitStatistic;
import capi.service.CommonService;

@Service("SummaryStatisticService")
public class SummaryStatisticService extends DataReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private UnitStatisticDao unitStatisticDao;
	
	@Autowired
	private QuotationStatisticDao quotationStatisticDao;
	
	@Autowired
	private SubItemStatisticDao subItemStatisticDao;

	@Autowired 
	private OutletTypeStatisticDao outletTypeStatisticDao;
	
	@Autowired
	private ItemStatisticDao itemStatisticDao;
	
	@Autowired
	private SubGroupStatisticDao subGroupStatisticDao;
	
	@Autowired
	private GroupStatisticDao groupStatisticDao;
	
	@Autowired
	private SectionStatisticDao sectionStatisticDao;
	
	@Override
	public String getFunctionCode() {
		return "RF9013";
	}
	
	private static final String[] headers = {"QuotationStatisticId","ReferenceMonth","Variety code","Variety Chin Name","Variety Eng Name","CPIBasePeriod",
			"CompilationMethod","CPIQuotationType","SumCurrentSPrice","CountCurrentSPrice","AverageCurrentSPrice","SumLastSPrice","CountLastSPrice",
			"AverageLastSPrice","FinalPRSPrice","CreatedDate","ModifiedDate","CreatedBy","ModifiedBy","StandardDeviationSPrice","MedianSPrice","MinSPrice",
			"MaxSPrice","LastHasPriceSumCurrentSPrice","LastHasPriceCountCurrentSPrice","LastHasPriceAverageCurrentSPrice","LastHasPriceReferenceMonth",
			"QuotationId","DeviationSum","Variance","SumCurrentNPrice","CountCurrentNPrice","AverageCurrentNPrice","LastHasPriceSumCurrentNPrice",
			"LastHasPriceCountCurrentNPrice","LastHasPriceAverageCurrentNPrice","FinalPRNPrice","AverageLastNPrice","CountLastNPrice","SumLastNPrice",
			"LastHasPriceSumLastSPrice","LastHasPriceCountLastSPrice","LastHasPriceAverageLastSPrice","KeepNoStartMonth"
	};
	
	private static final String[] headers1 = {"UnitStatisticId","ReferenceMonth","Variety code","Variety Chin Name","Variety Eng Name","CPIBasePeriod",
			"CompilationMethod","CPIQuotationType","SumCurrentSPrice","CountCurrentSPrice","AverageCurrentSPrice","SumLastSPrice","CountLastSPrice",
			"AverageLastSPrice","FinalPRSPrice","StandardDeviationSPrice","MedianSPrice","MinSPrice","MaxSPrice","LastHasPriceSumCurrentSPrice",
			"LastHasPriceCountCurrentSPrice","LastHasPriceAverageCurrentSPrice","LastHasPriceReferenceMonth","AveragePRSPrice","CountPRSPrice",	"SumPRSPrice",
			"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice","CreatedDate",	"ModifiedDate",	"CreatedBy",	"ModifiedBy",
			"UnitId",	"DeviationSum",	"Variance",	"PRSPriceDeviationSum",	"PRSPriceVariance","CountPRNPrice",	"PRNPriceDeviationSum","PRNPriceVariance",
			"StandardDeviationPRNPrice",	"SumPRNPrice",	"MinNPrice",	"MaxNPrice"	
	};
	
	private static final String[] headers2 = {"SubItemStatisticId",	"Subitem code",	"Subitem Chin name",	"Subitem Eng name",	"CPIBasePeriod",	
			"CompilationMethod",	"ReferenceMonth",	"SumCurrentSPrice",	"CountCurrentSPrice",	"AverageCurrentSPrice",	"SumLastSPrice",	"CountLastSPrice",
			"AverageLastSPrice",	"FinalPRSPrice",	"StandardDeviationSPrice",	"MedianSPrice",	"MinSPrice",	"MaxSPrice",	"LastHasPriceSumCurrentSPrice",
			"LastHasPriceCountCurrentSPrice",	"LastHasPriceAverageCurrentSPrice",	"LastHasPriceReferenceMonth",	"AveragePRSPrice",	"CountPRSPrice",	
			"SumPRSPrice",	"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice",	"SubItemId",	"DeviationSum",	"Variance",
			"PRSPriceDeviationSum",	"PRSPriceVariance"
	};
	
	private static final String[] headers3 = {
			"OutletTypeStatisticId",	"Outlet type code",	"Outlet type Chin Name",	"Outlet type Eng Name",	"CPIBasePeriod",	"ReferenceMonth",
			"SumCurrentSPrice",	"CountCurrentSPrice",	"AverageCurrentSPrice",	"SumLastSPrice",	"CountLastSPrice",	"AverageLastSPrice",	
			"FinalPRSPrice",	"StandardDeviationSPrice",	"MedianSPrice",	"MinSPrice",	"MaxSPrice",	"LastHasPriceSumCurrentSPrice",	
			"LastHasPriceCountCurrentSPrice",	"LastHasPriceAverageCurrentSPrice",	"LastHasPriceReferenceMonth",	"AveragePRSPrice",	"CountPRSPrice",
			"SumPRSPrice",	"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice",	"OutletTypeId",	"DeviationSum",
			"Variance",	"PRSPriceDeviationSum",	"PRSPriceVariance"											
	};
	
	private static final String[] headers4 = {
			"ItemStatisticId",	"Item code",	"Item Chin Name",	"Item Eng Name",	"CPIBasePeriod",	"ReferenceMonth",	"SumCurrentSPrice",
			"CountCurrentSPrice",	"AverageCurrentSPrice",	"SumLastSPrice",	"CountLastSPrice",	"AverageLastSPrice",	"FinalPRSPrice",	
			"StandardDeviationSPrice",	"MedianSPrice",	"MinSPrice",	"MaxSPrice",	"LastHasPriceSumCurrentSPrice",	"LastHasPriceCountCurrentSPrice",
			"LastHasPriceAverageCurrentSPrice",	"LastHasPriceReferenceMonth",	"AveragePRSPrice",	"CountPRSPrice",	"SumPRSPrice",	
			"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice",	"ItemId",	"DeviationSum",	"Variance",	
			"PRSPriceDeviationSum",	"PRSPriceVariance"											
	};
	
	private static final String[] headers5 = {
			"SubGroupStatisticId",	"Sub Group code",	"Sub Group Chin Name",	"Sub Group Eng Name",	"CPIBasePeriod",	"ReferenceMonth",	"SumCurrentSPrice",
			"CountCurrentSPrice",	"AverageCurrentSPrice",	"SumLastSPrice",	"CountLastSPrice",	"AverageLastSPrice",	"FinalPRSPrice",	
			"StandardDeviationSPrice",	"MedianSPrice",	"MinSPrice",	"MaxSPrice",	"LastHasPriceSumCurrentSPrice",	"LastHasPriceCountCurrentSPrice",
			"LastHasPriceAverageCurrentSPrice",	"LastHasPriceReferenceMonth",	"AveragePRSPrice",	"CountPRSPrice",	"SumPRSPrice",	
			"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice",	"SubGroupId",	"DeviationSum",	"Variance",	
			"PRSPriceDeviationSum",	"PRSPriceVariance"											
	};
	
	private static final String[] headers6 = {
			"GroupStatisticId",	"Group code",	"Group Chin Name",	"Group Eng Name",	"CPIBasePeriod",	"ReferenceMonth",	"SumCurrentSPrice",
			"CountCurrentSPrice",	"AverageCurrentSPrice",	"SumLastSPrice",	"CountLastSPrice",	"AverageLastSPrice",	"FinalPRSPrice",	
			"StandardDeviationSPrice",	"MedianSPrice",	"MinSPrice",	"MaxSPrice",	"LastHasPriceSumCurrentSPrice",	"LastHasPriceCountCurrentSPrice",
			"LastHasPriceAverageCurrentSPrice",	"LastHasPriceReferenceMonth",	"AveragePRSPrice",	"CountPRSPrice",	"SumPRSPrice",	
			"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice",	"GroupId",	"DeviationSum",	"Variance",	
			"PRSPriceDeviationSum",	"PRSPriceVariance"
	};
	
	private static final String[] headers7 = {
			"SectionStatisticId",	"Section code",	"Section Chin Name",	"Section Eng Name",	"CPIBasePeriod",	"ReferenceMonth",	"SumCurrentSPrice",
			"CountCurrentSPrice",	"AverageCurrentSPrice",	"SumLastSPrice",	"CountLastSPrice",	"AverageLastSPrice",	"FinalPRSPrice",	
			"StandardDeviationSPrice",	"MedianSPrice",	"MinSPrice",	"MaxSPrice",	"LastHasPriceSumCurrentSPrice",	"LastHasPriceCountCurrentSPrice",
			"LastHasPriceAverageCurrentSPrice",	"LastHasPriceReferenceMonth",	"AveragePRSPrice",	"CountPRSPrice",	"SumPRSPrice",	
			"StandardDeviationPRSPrice",	"MedianPRPrice",	"MinPRPrice",	"MaxPRPrice",	"SectionId",	"DeviationSum",	"Variance",	
			"PRSPriceDeviationSum",	"PRSPriceVariance"
	};

	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryStatisticCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryStatisticCriteria.class);
		
		Date period = commonService.getMonth(criteria.getPeriod());
		
		List<SummaryQuotationStatistic> quotationStat = quotationStatisticDao.getSummaryQuotationStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (SummaryQuotationStatistic data : quotationStat){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitChineseName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitEnglishName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompilationMethod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiQuotationType());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCreateDate()!=null)
				cell.setCellValue(commonService.formatDateStr(data.getCreateDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getModifyDate()!=null)
				cell.setCellValue(commonService.formatDateStr(data.getModifyDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCreateBy());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getModifyBy());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDevitationSPrice()!=null)
				cell.setCellValue(data.getStandardDevitationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarinance()!=null)
				cell.setCellValue(data.getVarinance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumCurrentNPrice()!=null)
				cell.setCellValue(data.getSumCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountCurrentNPrice()!=null)
				cell.setCellValue(data.getCountCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageCurrentNPrice()!=null)
				cell.setCellValue(data.getAverageCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentNPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentNPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentNPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRNPrice()!=null)
				cell.setCellValue(data.getFinalPRNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastNPrice()!=null)
				cell.setCellValue(data.getAverageLastNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastNPrice()!=null)
				cell.setCellValue(data.getCountLastNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastNPrice()!=null)
				cell.setCellValue(data.getSumLastNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumLastSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountLastSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageLastSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageLastSPrice());
		
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getKeepNostartMonth() != null) {
				cell.setCellValue(sdf.format(data.getKeepNostartMonth()));
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		
		List<SummaryUnitStatistic> unitStat = unitStatisticDao.getSummaryUnitStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet1 = workBook.createSheet("Unit Statistic");
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for(String header: headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummaryUnitStatistic data : unitStat){
			row = sheet1.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitChineseName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitEnglishName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompilationMethod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiQuotationType());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCreateDate()!=null)
				cell.setCellValue(commonService.formatDateStr(data.getCreateDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getModifyDate()!=null)
				cell.setCellValue(commonService.formatDateStr(data.getModifyDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCreateBy());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getModifyBy());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRNPrice()!=null)
				cell.setCellValue(data.getCountPRNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRNPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRNPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRNPriceVariance()!=null)
				cell.setCellValue(data.getPRNPriceVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRNPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRNPrice()!=null)
				cell.setCellValue(data.getSumPRNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinNPrice()!=null)
				cell.setCellValue(data.getMinNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxNPrice()!=null)
				cell.setCellValue(data.getMaxNPrice());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet1.flushRows();
			}
		}
		
		List<SummarySubItemStatistic> subItemStat = subItemStatisticDao.getSummarySubItemStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet2 = workBook.createSheet("SubItem Statistic");
		row = sheet2.createRow(0);
		cnt = 0;
		for(String header: headers2) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummarySubItemStatistic data : subItemStat){
			row = sheet2.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubItemStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubItemCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubItemChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubItemEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompilationMethod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubItemId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet2.flushRows();
			}
		}
		
		List<SummaryOutletTypeStatistic> outletTypeStat = outletTypeStatisticDao.getSummaryOutletTypeStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet3 = workBook.createSheet("OutletType Statistic");
		row = sheet3.createRow(0);
		cnt = 0;
		for(String header: headers3) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummaryOutletTypeStatistic data : outletTypeStat){
			row = sheet3.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet3.flushRows();
			}
		}
		
		List<SummaryItemStatistic> itemStat = itemStatisticDao.getSummaryItemStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet4 = workBook.createSheet("Item Statistic");
		row = sheet4.createRow(0);
		cnt = 0;
		for(String header: headers4) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummaryItemStatistic data : itemStat){
			row = sheet4.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getItemId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet4.flushRows();
			}
		}
		
		List<SummarySubGroupStatistic> subGroupStat = subGroupStatisticDao.getSummarySubGroupStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet5 = workBook.createSheet("SubGroup Statistic");
		row = sheet5.createRow(0);
		cnt = 0;
		for(String header: headers5) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummarySubGroupStatistic data : subGroupStat){
			row = sheet5.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSubGroupId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet5.flushRows();
			}
		}
		
		List<SummaryGroupStatistic> groupStat = groupStatisticDao.getSummaryGroupStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet6 = workBook.createSheet("Group Statistic");
		row = sheet6.createRow(0);
		cnt = 0;
		for(String header: headers6) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummaryGroupStatistic data : groupStat){
			row = sheet6.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getGroupId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet6.flushRows();
			}
		}
		
		List<SummarySectionStatistic> sectionStat = sectionStatisticDao.getSummarySectionStatistic(criteria.getPurpose(), criteria.getUnitId(), criteria.getCpiSurveyForm(), period);
		
		SXSSFSheet sheet7 = workBook.createSheet("Section Statistic");
		row = sheet7.createRow(0);
		cnt = 0;
		for(String header: headers7) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		rowCnt = 1;		
		for (SummarySectionStatistic data : sectionStat){
			row = sheet7.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSectionStatisticId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSectionCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSectionChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSectionEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice() != null)
				cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumLastSPrice()!=null)
				cell.setCellValue(data.getSumLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountLastSPrice()!=null)
				cell.setCellValue(data.getCountLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAverageLastSPrice()!=null)
				cell.setCellValue(data.getAverageLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice()!=null)
				cell.setCellValue(data.getFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianSPrice()!=null)
				cell.setCellValue(data.getMedianSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinSPrice()!=null)
				cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxSPrice()!=null)
				cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceAverageCurrentSPrice()!=null)
				cell.setCellValue(data.getLastHasPriceAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(sdf.format(data.getLastHasPriceReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice()!=null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCountPRSPrice()!=null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSumPRSPrice()!=null)
				cell.setCellValue(data.getSumPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getStandardDeviationPRSPrice()!=null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMedianPRPrice()!=null)
				cell.setCellValue(data.getMedianPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMinPRPrice()!=null)
				cell.setCellValue(data.getMinPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getMaxPRPrice()!=null)
				cell.setCellValue(data.getMaxPRPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSectionId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getDeviationSum()!=null)
				cell.setCellValue(data.getDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVariance()!=null)
				cell.setCellValue(data.getVariance());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceDeviationSum()!=null)
				cell.setCellValue(data.getPRSPriceDeviationSum());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPRSPriceVariance()!=null)
				cell.setCellValue(data.getPRSPriceVariance());

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet7.flushRows();
			}
		}
		// Output Excel 
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Quotation Statistic");
		
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
		
		SummaryStatisticCriteria criteria = (SummaryStatisticCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s", criteria.getPeriod()));
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		if (criteria.getUnitId() != null && criteria.getUnitId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Unit> units = unitDao.getByIds(criteria.getUnitId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Unit unit : units){
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
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
		if (criteria.getDataCollection() != null && criteria.getDataCollection().length() > 0){
			descBuilder.append("\n");
			if("Y".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "Field"));
			else if("N".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "No Field"));
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
