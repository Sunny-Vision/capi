package capi.service.batchJob;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ProductCleaningDao;
import capi.entity.ProductCleaning;
import capi.service.CommonService;

@Service("CleanProductCleaningService")
public class CleanProductCleaningService implements BatchJobService{
	
	@Autowired
	private ProductCleaningDao dao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Clean Product Cleaning Table";
	}
	
	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date obsoleteDate = DateUtils.addMonths(today, -2);
		
		List<ProductCleaning> cleanings = dao.getObsoleteProductCleaning(obsoleteDate);
		for(ProductCleaning clean : cleanings){
			dao.delete(clean);
		}
		
		dao.flush();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}
}
