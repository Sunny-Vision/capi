package capi.service.masterMaintenance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.PricingMonthDao;
import capi.entity.PricingFrequency;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.PricingFrequencySyncData;
import capi.model.masterMaintenance.PricingMonthTableList;
import capi.service.BaseService;

@Service("PricingMonthService")
public class PricingMonthService extends BaseService {

	@Autowired
	private PricingMonthDao pricingMonthDao;

	/**
	 * Get by ID
	 */
	public PricingFrequency getPricingMonthById(int id) {
		return pricingMonthDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<PricingMonthTableList> getPricingMonthList(DatatableRequestModel model){
		Order order = this.getOrder(model, "pricingFrequencyId", "pricingFrequencyId", "name", "months");

		String search = model.getSearch().get("value");	
		List<PricingMonthTableList> result = pricingMonthDao.selectAllPricingMonth(search, model.getStart(), model.getLength(), order);
		
		for(PricingMonthTableList pricingMonth : result) {
			List<String> monthsArray = new ArrayList<String>();
			if(pricingMonth.getIsJan()) { monthsArray.add("1"); }
			if(pricingMonth.getIsFeb()) { monthsArray.add("2");	}
			if(pricingMonth.getIsMar()) { monthsArray.add("3");	}
			if(pricingMonth.getIsApr()) { monthsArray.add("4"); }
			if(pricingMonth.getIsMay()) { monthsArray.add("5");	}
			if(pricingMonth.getIsJun()) { monthsArray.add("6"); }
			if(pricingMonth.getIsJul()) { monthsArray.add("7");	}
			if(pricingMonth.getIsAug()) { monthsArray.add("8"); }
			if(pricingMonth.getIsSep()) { monthsArray.add("9");	}
			if(pricingMonth.getIsOct()) { monthsArray.add("10"); }
			if(pricingMonth.getIsNov()) { monthsArray.add("11"); }
			if(pricingMonth.getIsDec()) { monthsArray.add("12"); }
			String months = StringUtils.join(monthsArray, ",");
			pricingMonth.setMonths(months);
		}
		
		DatatableResponseModel<PricingMonthTableList> response = new DatatableResponseModel<PricingMonthTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = pricingMonthDao.countSelectAllPricingMonth("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = pricingMonthDao.countSelectAllPricingMonth(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save Pricing Month
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional
	public boolean savePricingMonth(String act, PricingMonthTableList model) throws Exception {

		// Check duplicate set
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("isJan", model.getIsJan());
		map.put("isFeb", model.getIsFeb());
		map.put("isMar", model.getIsMar());
		map.put("isApr", model.getIsApr());
		map.put("isMay", model.getIsMay());
		map.put("isJun", model.getIsJun());
		map.put("isJul", model.getIsJul());
		map.put("isAug", model.getIsAug());
		map.put("isSep", model.getIsSep());
		map.put("isOct", model.getIsOct());
		map.put("isNov", model.getIsNov());
		map.put("isDec", model.getIsDec());
		PricingFrequency pricingMonth = pricingMonthDao.checkDuplicatePricingMonth(map);
		
		if(pricingMonth == null) {
			// find by description(name)
			pricingMonth = pricingMonthDao.getPricingMonthByDescription(model.getName());
		}
		
		PricingFrequency oldEntity = null;
		if (model.getPricingFrequencyId() != null && model.getPricingFrequencyId() > 0) {
			oldEntity = getPricingMonthById(model.getPricingFrequencyId());
		} else {
			// create pricing month
			oldEntity = new PricingFrequency();
		}

		if(act.equals("add")) {
			if (pricingMonth != null) return false;
		} else {
			if (pricingMonth != null) {
				if(!oldEntity.getPricingFrequencyId().equals(pricingMonth.getPricingFrequencyId()))
					return false;
			}
		}

		oldEntity.setName(model.getName());
		oldEntity.setJan(model.getIsJan());
		oldEntity.setFeb(model.getIsFeb());
		oldEntity.setMar(model.getIsMar());
		oldEntity.setApr(model.getIsApr());
		oldEntity.setMay(model.getIsMay());
		oldEntity.setJun(model.getIsJun());
		oldEntity.setJul(model.getIsJul());
		oldEntity.setAug(model.getIsAug());
		oldEntity.setSep(model.getIsSep());
		oldEntity.setOct(model.getIsOct());
		oldEntity.setNov(model.getIsNov());
		oldEntity.setDec(model.getIsDec());
		
		pricingMonthDao.save(oldEntity);
		pricingMonthDao.flush();
		
		return true;
	}

	/**
	 * Delete pricing month
	 */
	@Transactional
	public boolean deletePricingMonth(List<Integer> ids) {

		List<PricingFrequency> pricingMonths = pricingMonthDao.getPricingMonthsByIds(ids);
		if (pricingMonths.size() != ids.size()){
			return false;
		}
		
		for (PricingFrequency pricingMonth: pricingMonths){
			pricingMonthDao.delete(pricingMonth);
		}		
		
		pricingMonthDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public PricingMonthTableList convertEntityToModel(PricingFrequency entity){

		PricingMonthTableList model = new PricingMonthTableList();
		BeanUtils.copyProperties(entity, model);
		
		model.setIsJan(entity.isJan());
		model.setIsFeb(entity.isFeb());
		model.setIsMar(entity.isMar());
		model.setIsApr(entity.isApr());
		model.setIsMay(entity.isMay());
		model.setIsJun(entity.isJun());
		model.setIsJul(entity.isJul());
		model.setIsAug(entity.isAug());
		model.setIsSep(entity.isSep());
		model.setIsOct(entity.isOct());
		model.setIsNov(entity.isNov());
		model.setIsDec(entity.isDec());

		return model;
	}
	
	

	/**
	 * Get Pricing Frequency select2 format
	 */
	public Select2ResponseModel queryPricingFrequencySelect2(Select2RequestModel queryModel) {
		
		queryModel.setRecordsPerPage(10);
		
		List<PricingFrequency> pricingFreqs = pricingMonthDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal =  pricingMonthDao.countSearch(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for ( PricingFrequency pricingFreq : pricingFreqs) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(pricingFreq.getId()));
			item.setText(pricingFreq.getName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	

	/**
	 * Query single pricing frequency
	 */
	public String queryPricingFrequencySelectSingle(Integer id) {
		PricingFrequency entity = pricingMonthDao.findById(id);
		if (entity == null)
			return null;
		else
			return entity.getName();
	}
	
	public List<PricingFrequencySyncData> getUpdatePricingFrequency(Date lastSyncTime){
		return pricingMonthDao.getUpdatePricingFrequency(lastSyncTime);
	}
}
