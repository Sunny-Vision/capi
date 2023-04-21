package capi.service.batchJob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.GroupStatisticDao;
import capi.dal.ItemStatisticDao;
import capi.dal.OutletTypeMappingDao;
import capi.dal.OutletTypeStatisticDao;
import capi.dal.QuotationDao;
import capi.dal.RebasingDao;
import capi.dal.SectionStatisticDao;
import capi.dal.SubGroupStatisticDao;
import capi.dal.SubItemStatisticDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UnitDao;
import capi.dal.UnitStatisticDao;
import capi.entity.Quotation;
import capi.entity.Rebasing;
import capi.entity.SurveyMonth;
import capi.service.CommonService;

/**
 * 
 * @author stanley_tsang
 * update the foreign key, change the outlet short code and perform statistic recalculation for last 2 year on rebasing effective date
 */
@Service("RebasingEffectiveService")
public class RebasingEffectiveService implements BatchJobService{
	
	@Autowired
	private RebasingDao rebasingDao;
	
	@Autowired
	private CommonService commonService;

	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private OutletTypeMappingDao outletTypeMappingDao;
	
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
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Rebasing Effective Service";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date curMonth = DateUtils.truncate(today, Calendar.MONTH);
		List<Rebasing> rebasings = rebasingDao.getNotEffectedRebasing(today);
		if (rebasings != null && rebasings.size() > 0){
			markEffective();
			outletTypeMappingDao.updateOutletTypeShortCode(today);
			for (Rebasing rebasing : rebasings){
				rebasing.setEffected(true);
			}
			recalculateStatistic(curMonth);			
		}		
	}
	
	private void recalculateStatistic(Date curMonth){
		Date today = commonService.getDateWithoutTime(new Date());
		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(curMonth);
		if (month != null){
			Date publishDate = month.getClosingDate().getPublishDate();
			curMonth = publishDate.after(today)?DateUtils.addMonths(curMonth, -1): curMonth;
		}
		Date before = DateUtils.addYears(curMonth, -2);
		while (!before.equals(curMonth)){
			before = DateUtils.addMonths(before, 1);
			unitStatisticDao.calculateUnitStatistic(before, true);
			subItemStatisticDao.calculateSubItemStatistic(before, true);
			outletTypeStatisticDao.calculateOutletTypeStatistic(before, true);
			itemStatisticDao.calculateItemStatistic(before, true);
			subGroupStatisticDao.calculateSubGroupStatistic(before, true);
			groupStatisticDao.calculateGroupStatistic(before, true);
			sectionStatisticDao.calculateSectionStatistic(before, true);
		}

		unitDao.CalculateConsolidatedUnitStatisitc();
	}
	
	private void markEffective(){
		List<Quotation> quotations = quotationDao.getActiveQuotation();
		if (quotations != null && quotations.size() > 0){
			for (Quotation quotation : quotations){
				quotation.setOldUnit(quotation.getUnit());
				if (quotation.getNewUnit() != null){
					quotation.setUnit(quotation.getNewUnit());
				}
				quotation.setNewUnit(null);
				if (quotation.getNewProduct() != null){
					quotation.setProduct(quotation.getNewProduct());
				}
				quotation.setNewProduct(null);
			}
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
