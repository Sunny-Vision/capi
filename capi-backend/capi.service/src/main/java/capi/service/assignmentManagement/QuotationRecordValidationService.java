package capi.service.assignmentManagement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import capi.entity.QuotationRecord;

/**
 * Quotation record validation
 */
@Service("QuotationRecordValidationService")
public class QuotationRecordValidationService {
	public List<String> validate(QuotationRecord entity, QuotationRecord original) throws Exception {
		List<String> messages = new ArrayList<String>();
		
		if (nPrice(entity, original))
			messages.add("N price required");
		
		if (sPrice(entity, original))
			messages.add("S price required");
		
		if (consignmentCounterName(entity, original))
			messages.add("Consignment counter name required");
		
		if (nullProduct(entity, original))
			messages.add("Product required");
		
		if (entity.getFormDisplay() == 2) {
			if (extraPrice1Name(entity, original))
				messages.add("Extra price 1 name required");
			
			if (extraPrice1Value(entity, original))
				messages.add("Extra price 1 value required");
			
			if (extraPrice2Name(entity, original))
				messages.add("Extra price 2 name required");
			
			if (extraPrice2Value(entity, original))
				messages.add("Extra price 2 value required");
			
			if (extraPrice3Name(entity, original))
				messages.add("Extra price 3 name required");
			
			if (extraPrice3Value(entity, original))
				messages.add("Extra price 3 value required");
			
			if (extraPrice4Name(entity, original))
				messages.add("Extra price 4 name required");
			
			if (extraPrice4Value(entity, original))
				messages.add("Extra price 4 value required");
			
			if (extraPrice5Name(entity, original))
				messages.add("Extra price 5 name required");
			
			if (extraPrice5Value(entity, original))
				messages.add("Extra price 5 value required");
		}
		
		if (frMandatoryIfChangeProductTrue(entity, original))
			messages.add("FR required");
		
		return messages;
	}
	
	/**
	 * nPrice
	 */
	public boolean nPrice(QuotationRecord entity, QuotationRecord original) {
		if (original.getQuotation() == null || original.getQuotation().getUnit() == null) return false;
		
		if (entity.isBackNo() && !original.getQuotation().getUnit().isBackdateRequired()) return false;
		
		if (!original.getQuotation().getUnit().isNPriceMandatory()) return false;
		
		if (entity.isSPricePeculiar()) return false;
		
		if (entity.getnPrice() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * sPrice
	 */
	public boolean sPrice(QuotationRecord entity, QuotationRecord original) {
		if (original.getQuotation() == null || original.getQuotation().getUnit() == null) return false;
		
		if (entity.isBackNo() && !original.getQuotation().getUnit().isBackdateRequired()) return false;
		
		if (!original.getQuotation().getUnit().isSPriceMandatory()) return false;
		
		if (entity.isSPricePeculiar()) return false;
		
		if (entity.getsPrice() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * Consignment counter name
	 */
	public boolean consignmentCounterName(QuotationRecord entity, QuotationRecord original) {
		if (!original.isConsignmentCounter()) return false;
		
		if (StringUtils.isWhitespace(entity.getConsignmentCounterName()))
			return true;
		else
			return false;
	}
	
	/**
	 * Consignment counter remark
	 */
	public boolean consignmentCounterRemark(QuotationRecord entity, QuotationRecord original) {
		if (!original.isConsignmentCounter()) return false;
		
		if (StringUtils.isWhitespace(entity.getConsignmentCounterRemark()))
			return true;
		else
			return false;
	}
	
	/**
	 * Visited
	 */
	public boolean visited(QuotationRecord entity, QuotationRecord original) {
		if (!original.getQuotationState().equals("Verify") && !original.getQuotationState().equals("Revisit")) return false;
		
		if (entity.isVisited())
			return false;
		else
			return true;
	}
	
	/**
	 * Extra price 1 name
	 */
	public boolean extraPrice1Name(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (entity.getTourRecord().getExtraPrice1Value() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice1Name()))
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 1 name
	 */
	public boolean extraPrice1Value(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice1Name())) return false;
		
		if (entity.getTourRecord().getExtraPrice1Value() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 2 name
	 */
	public boolean extraPrice2Name(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
				
		if (entity.getTourRecord().getExtraPrice2Value() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice2Name()))
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 2 name
	 */
	public boolean extraPrice2Value(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice2Name())) return false;
		
		if (entity.getTourRecord().getExtraPrice2Value() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 3 name
	 */
	public boolean extraPrice3Name(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (entity.getTourRecord().getExtraPrice3Value() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice3Name()))
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 3 name
	 */
	public boolean extraPrice3Value(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice3Name())) return false;
		
		if (entity.getTourRecord().getExtraPrice3Value() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 4 name
	 */
	public boolean extraPrice4Name(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (entity.getTourRecord().getExtraPrice4Value() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice4Name()))
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 4 name
	 */
	public boolean extraPrice4Value(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
				
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice4Name())) return false;
		
		if (entity.getTourRecord().getExtraPrice4Value() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 5 name
	 */
	public boolean extraPrice5Name(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (entity.getTourRecord().getExtraPrice5Value() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice5Name()))
			return true;
		else
			return false;
	}
	
	/**
	 * Extra price 5 name
	 */
	public boolean extraPrice5Value(QuotationRecord entity, QuotationRecord original) {
		if (entity.getTourRecord() == null) return false;
		
		if (StringUtils.isWhitespace(entity.getTourRecord().getExtraPrice5Name())) return false;
		
		if (entity.getTourRecord().getExtraPrice5Value() == null)
			return true;
		else
			return false;
	}
	
	/**
	 * Null product
	 */
	public boolean nullProduct(QuotationRecord entity, QuotationRecord original) {
		if (entity.getProduct() == null && (entity.getAvailability() != null && (entity.getAvailability() == 1 || entity.getAvailability() == 3))
				&& entity.getAssignment().getStatus() != null && entity.getAssignment().getStatus() == 1)
			return true;
		else
			return false;
	}
	
	public boolean frMandatoryIfChangeProductTrue(QuotationRecord entity, QuotationRecord original) {
		if (entity.isBackNo()) return false;
		if (!original.isProductChange()) return false;
		if (!entity.getQuotation().isFRApplied() && entity.getQuotation().getUnit().isFrRequired() && 
				(entity.getOutlet().isUseFRAdmin() && entity.isConsignmentCounter() || !entity.getOutlet().isUseFRAdmin())
				&& entity.getFr() == null)
			return true;
		else
			return false;
	}
}
