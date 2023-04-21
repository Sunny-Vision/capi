package capi.service.batchJob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.QuotationDao;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Quotation;
import capi.service.CommonService;
import capi.service.assignmentManagement.DataConversionService;
import capi.service.assignmentManagement.QuotationService;

/**
 * Run By Start of the Month
 * Create last Month IndoorQuotationRecord which count(RUA Quotation.IndoorQuotationRecord) = 0
 * @author man_leung
 *
 */
@Service("RUAEndMonthDataConversion")
public class RUAEndMonthDataConversion implements BatchJobService{

	@Autowired
	private DataConversionService conversionService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationService quotationService;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private IndoorQuotationRecordDao indoorQuotationRecordDao;
	
	@Override
	public String getJobName(){
		// TODO Auto-generated method stub
		return "RUA End Month Indoor Data Conversion";
	}
	
	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		
		if(!today.equals(commonService.getReferenceMonth(today)))
			return;
		
		Date lastMonth = DateUtils.addMonths(commonService.getReferenceMonth(today), -1);
		
		List<Integer> quotationIds = quotationDao.getNoIndoorRUAQuotationByReference(lastMonth);
		HashMap<Integer, Quotation> quotationMap = new HashMap<Integer, Quotation>();
		
		if(quotationIds!=null && quotationIds.size()>0){
			
			int i = 1;
			for(Integer id : quotationIds){
				Quotation quotation = null;
				if (quotationMap.containsKey(id)){
					quotation = quotationMap.get(id);
				}
				else{
					quotation = quotationDao.findById(id);	
					quotationMap.put(id, quotation);
				}
				
				IndoorQuotationRecord record = conversionService.generateIndoorQuotationRecord(quotation, DateUtils.addDays(today, -1), lastMonth);
				indoorQuotationRecordDao.save(record);
				
				if(i % 20==0){
					indoorQuotationRecordDao.flushAndClearCache();
				}
				i++;
			}
		}
		
		indoorQuotationRecordDao.flushAndClearCache();
	}
	
	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}
}
