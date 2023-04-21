package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.DiscountFormulaDao;
import capi.dal.SystemConfigurationDao;
import capi.entity.DiscountFormula;
import capi.entity.SystemConfiguration;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.DiscountFormulaSyncData;
import capi.model.masterMaintenance.DiscountCalculatorItemsList;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("DiscountFormulaService")
public class DiscountFormulaService extends BaseService {
	
	@Autowired
	private SystemConfigurationDao systemConfigurationDao;

	@Autowired
	private DiscountFormulaDao discountFormulaDao;
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * Get calculator discount items settings
	 * @return
	 */
	public DiscountCalculatorItemsList getDiscountItems() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_01));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_02));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_03));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_04));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_05));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_06));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_07));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_08));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_09));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_10));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_11));
		list.add(getSystemConfiguration(SystemConstant.DISCOUNT_TEXT_12));

		DiscountCalculatorItemsList items = new DiscountCalculatorItemsList();
		items.setValues(list);
		return items;
	}
	
	private String getSystemConfiguration(String name) {
		SystemConfiguration config = systemConfigurationDao.findByName(name);
		return config == null ? "" : config.getValue();
	}

	/**
	 * Save discount items
	 */
	@Transactional
	public void saveDiscountItems(DiscountCalculatorItemsList model) {
		String value = null;
		
		value = model.getValues().get(0);
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_01, value);
		
		value = model.getValues().get(1);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_02, value);
		
		value = model.getValues().get(2);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_03, value);
		
		value = model.getValues().get(3);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_04, value);
		
		value = model.getValues().get(4);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_05, value);
		
		value = model.getValues().get(5);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_06, value);
		
		value = model.getValues().get(6);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_07, value);
		
		value = model.getValues().get(7);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_08, value);
		
		value = model.getValues().get(8);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_09, value);
		
		value = model.getValues().get(9);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_10, value);
		
		value = model.getValues().get(10);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_11, value);
		
		value = model.getValues().get(11);		
		saveSystemConfiguration(SystemConstant.DISCOUNT_TEXT_12, value);
		
		systemConfigurationDao.flush();
	}
	
	private void saveSystemConfiguration(String name, String value) {
		SystemConfiguration config = systemConfigurationDao.findByName(name);
		if (config == null) {
			config = new SystemConfiguration();
			config.setName(name);
		}
		config.setValue(value);
		systemConfigurationDao.save(config);
	}
	
	/**
	 * Get by ID
	 */
	public DiscountFormula getDiscountFormulaById(int id) {
		return discountFormulaDao.findById(id);
	}
	
	/**
	 * Save
	 */
	@Transactional
	public void saveDiscountFormula(DiscountFormula model) {
		DiscountFormula oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getDiscountFormulaById(model.getId());
		} else {
			oldEntity = new DiscountFormula();
		}
		BeanUtils.copyProperties(model, oldEntity);
		
		discountFormulaDao.save(oldEntity);
		discountFormulaDao.flush();
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<DiscountFormula> getDiscountFormulaList(DatatableRequestModel model){
		return getDiscountFormulaList(model, null);
	}
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<DiscountFormula> getDiscountFormulaList(DatatableRequestModel model, String status){

		Order order = this.getOrder(model, "id", "displayPattern", "formula", "status");
		
		String search = model.getSearch().get("value");
		
		List<DiscountFormula> result = discountFormulaDao.getTableList(search, model.getStart(), model.getLength(), order, status);
		
		DatatableResponseModel<DiscountFormula> response = new DatatableResponseModel<DiscountFormula>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = discountFormulaDao.countTableList("", null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = discountFormulaDao.countTableList(search, null);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Delete
	 */
	@Transactional
	public boolean deleteDiscountFormula(List<Integer> id) {
		List<DiscountFormula> formulas = discountFormulaDao.getDiscountFormulasByIds(id);
		if (formulas.size() != id.size()){
			return false;
		}
		
		for (DiscountFormula formula : formulas){
			discountFormulaDao.delete(formula);
		}
		
		discountFormulaDao.flush();
		
		return true;
	}
				
	public List<DiscountFormulaSyncData> getUpdateDiscountFormula(Date lastSyncTime){
		return discountFormulaDao.getUpdateDiscountFormula(lastSyncTime);
	}
}
