package capi.service.lookup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.DiscountFormulaDao;
import capi.model.commonLookup.DiscountFormulaLookupModel;

@Service("DiscountCalculatorLookupService")
public class DiscountCalculatorLookupService {
	@Autowired
	private DiscountFormulaDao dao;

	/**
	 * Get all enabled formula
	 */
	public List<DiscountFormulaLookupModel> getAllEnabledFormula() {
		return dao.getAllEnabledFormula();
	}
}
