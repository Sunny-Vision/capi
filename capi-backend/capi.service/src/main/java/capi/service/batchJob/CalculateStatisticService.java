package capi.service.batchJob;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.GroupStatisticDao;
import capi.dal.ItemStatisticDao;
import capi.dal.OutletTypeStatisticDao;
import capi.dal.OutletUnitStatisticDao;
import capi.dal.QuotationStatisticDao;
import capi.dal.SectionStatisticDao;
import capi.dal.SpotCheckStatisticDao;
import capi.dal.SubGroupStatisticDao;
import capi.dal.SubItemStatisticDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UnitDao;
import capi.dal.UnitStatisticDao;
import capi.entity.SurveyMonth;
import capi.service.CommonService;

/**
 * 
 * @author stanley_tsang
 *	calculate all statistics in capi system
 *
 */
@Service("CalculateStatisticService")
public class CalculateStatisticService implements BatchJobService{

	@Autowired
	private QuotationStatisticDao quotationStatisticDao;
	
	@Autowired
	private UnitStatisticDao unitStatisticDao;
	
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
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private OutletUnitStatisticDao outletUnitStatisticDao;
	
	@Autowired
	private SpotCheckStatisticDao spotCheckStatisticDao;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Calculate statistic";
	}

	@Override
	public void runTask() {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		if (Calendar.getInstance().get(Calendar.DATE) == 1){
			spotCheckStatisticDao.calculateSpotCheckStatistic(today);
		}	
		
		unitDao.CalculateConsolidatedUnitStatisitc();
		outletUnitStatisticDao.CalculateQuotationCountInUnitOutlet();	
		
		SurveyMonth month = surveyMonthDao.getLatestSurveyMonthByPublishDate(today);
		if (month == null){
			return;
		}
		
		Date refMonth = month.getReferenceMonth();
		long statisticCnt = quotationStatisticDao.countQuotationStatisticInMonth(refMonth);
		if (statisticCnt == 0){
			quotationStatisticDao.calculateQuotationStatistic(refMonth);
			unitStatisticDao.calculateUnitStatistic(refMonth);
			subItemStatisticDao.calculateSubItemStatistic(refMonth);
			outletTypeStatisticDao.calculateOutletTypeStatistic(refMonth);
			itemStatisticDao.calculateItemStatistic(refMonth);
			subGroupStatisticDao.calculateSubGroupStatistic(refMonth);
			groupStatisticDao.calculateGroupStatistic(refMonth);
			sectionStatisticDao.calculateSectionStatistic(refMonth);
		}
			
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
