package capi.model.dataConversion.quotationRecordDataConversion;

import java.io.Serializable;
import java.util.List;

import capi.model.DatatableRequestModel;

public class QuotationRecordDataConversionSessionModel implements Serializable{
	private List<Integer> indoorQuotationRecordIds;
	
	private DatatableRequestModel lastRequestModel;

	public List<Integer> getIndoorQuotationRecordIds() {
		return indoorQuotationRecordIds;  
	}

	public void setIndoorQuotationRecordIds(List<Integer> indoorQuotationRecordIds) {
		this.indoorQuotationRecordIds = indoorQuotationRecordIds;
	}
	
	public Integer getIndex(Integer id){
		return this.indoorQuotationRecordIds.indexOf(id)+1;
	}
	
	public Integer getPreviousId(Integer id){
		try{
			return this.indoorQuotationRecordIds.get(this.indoorQuotationRecordIds.indexOf(id) -1);
		}catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public Integer getNextId(Integer id){
		try{
			return this.indoorQuotationRecordIds.get(this.indoorQuotationRecordIds.indexOf(id) +1);
		}catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public Integer getCount(){
		return this.indoorQuotationRecordIds.size();
	}
	
	public void removeId(Integer id){
		try{
			this.indoorQuotationRecordIds.remove(this.indoorQuotationRecordIds.indexOf(id));
		}catch(UnsupportedOperationException oue){
		}catch(IndexOutOfBoundsException e){
		}
	}

	
	public DatatableRequestModel getLastRequestModel() {
		return lastRequestModel;
	}
	

	public void setLastRequestModel(DatatableRequestModel lastRequestModel) {
		this.lastRequestModel = lastRequestModel;
	}
}
