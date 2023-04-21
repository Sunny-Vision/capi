package capi.service.batchJob;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SurveyMonthDao;
import capi.entity.Quotation;
import capi.entity.SurveyMonth;
import capi.service.CommonService;
import capi.service.assignmentManagement.DataConversionService;

/**
 * 
 * @author stanley_tsang
 * update indoor data conversion flag and perform data conversion for the quotation record that marked conversion after closing date
 */
@Service("UpdateIndoorConversionFlag")
public class UpdateIndoorConversionFlag implements BatchJobService{
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;

	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private DataConversionService conversionService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Update Indoor Data Conversion Flag";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		
		SurveyMonth curSurveyMonth = surveyMonthDao.getSurveyMonthByDate(today);
		
		Date lastMonth = DateUtils.addMonths(curSurveyMonth.getReferenceMonth(), -1);
		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(lastMonth);
		
		if (month == null || month.isFlagTransferred()) return;
		
		Date closingDate = month.getClosingDate().getClosingDate();
		if (today.before(closingDate) || today.equals(closingDate)){
			return;
		}
		
		
		List<Quotation> quotations = quotationDao.getActiveQuotation();
		if (quotations != null && quotations.size() > 0){
			for (Quotation quotation : quotations){				
				quotation.setTempIsFRApplied(quotation.isFRApplied());
				quotation.setTempIsReturnGoods(quotation.isReturnGoods());
				quotation.setTempIsReturnNewGoods(quotation.isReturnNewGoods());
				quotation.setTempKeepNoMonth(quotation.getKeepNoMonth());
				quotation.setTempLastFRAppliedDate(quotation.getTempLastFRAppliedDate());
				quotation.setTempIsUseFRAdmin(quotation.getIsUseFRAdmin());
				if (quotation.getIsUseFRAdmin() != null && quotation.getIsUseFRAdmin()){
					quotation.setTempFRPercentage(quotation.getIsFRAdminPercentage());
					quotation.setTempFRValue(quotation.getFrAdmin());
				}
				else{
					
					quotation.setTempFRPercentage(quotation.getIsFRFieldPercentage());
					quotation.setTempFRValue(quotation.getFrField());
				}			
				
//				quotation.setFRApplied(quotation.getTempIsFRApplied()== null ? false: quotation.getTempIsFRApplied());
//				quotation.setReturnGoods(quotation.getTempIsReturnGoods() == null ? false: quotation.getTempIsReturnGoods());
//				quotation.setReturnNewGoods(quotation.getTempIsReturnNewGoods() == null ? false: quotation.getTempIsReturnNewGoods());
//				quotation.setKeepNoMonth(quotation.getTempKeepNoMonth());
//				quotation.setLastFRAppliedDate(quotation.getTempLastFRAppliedDate());
//				quotation.setUseFRAdmin(quotation.isTempIsUseFRAdmin());
//				if (quotation.getIsUseFRAdmin() != null && quotation.getIsUseFRAdmin()){
//					quotation.setIsFRAdminPercentage(quotation.isTempFRPercentage());
//					quotation.setFrAdmin(quotation.getTempFRValue());
//				}
//				else{
//					quotation.setIsFRFieldPercentage(quotation.isTempFRPercentage());
//					quotation.setFrField(quotation.getTempFRValue());
//				}
				
				quotationDao.save(quotation);
			}
		}
		quotationDao.flush();
		
		//SurveyMonth curSurveyMonth = surveyMonthDao.getSurveyMonthByReferenceMonth(curMonth);
//		if (curSurveyMonth != null){
//			List<QuotationRecord> records = quotationRecordDao.getAprovedQuotationRecordForClosingDateConversion(curSurveyMonth.getSurveyMonthId());
//			Collections.sort(records, new Comparator<QuotationRecord>(){
//				@Override
//				public int compare(QuotationRecord o1,
//						QuotationRecord o2){
//					// TODO Auto-generated method stub
//					return o1.getReferenceDate().compareTo(o2.getReferenceDate());
//				}
//			});
//			
//			if (records != null && records.size() > 0){
//				for (QuotationRecord record : records){
//					conversionService.convert(record);
//				}
//			}
//		}
//		quotationRecordDao.flush();
		
		month.setFlagTransferred(true);
		surveyMonthDao.save(month);
		
		surveyMonthDao.flush();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
