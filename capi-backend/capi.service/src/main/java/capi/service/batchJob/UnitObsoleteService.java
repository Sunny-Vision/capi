package capi.service.batchJob;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.QuotationDao;
import capi.entity.Quotation;

/**
 * 
 * @author stanley_tsang
 * Check whether there are some varieties obsolete today and update all related quotation to Inactive
 */
@Service("UnitObsoleteService")
public class UnitObsoleteService implements BatchJobService{

	@Autowired
	private QuotationDao quotationDao;

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Unit obsolete Service";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		List<Quotation> quotations = quotationDao.getQuotationWithUnitExpired();
		
		for(Quotation quotation : quotations) {
			quotation.setStatus("Inactive");
			quotationDao.save(quotation);
		}
		
		quotationDao.flush();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
