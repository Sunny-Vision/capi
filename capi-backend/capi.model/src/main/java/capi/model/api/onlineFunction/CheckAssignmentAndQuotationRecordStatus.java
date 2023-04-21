package capi.model.api.onlineFunction;

import java.util.List;

import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.QuotationRecordSyncData;

public class CheckAssignmentAndQuotationRecordStatus{
   /*
   private Integer assignmentId;
   
   private Integer quotationRecordId;
 
   public Integer getAssignmentId() {
       return assignmentId;
   }
 
   public void setAssignmentId(Integer assignmentId) {
       this.assignmentId = assignmentId;
   }
 
   public Integer getQuotationRecordId() {
       return quotationRecordId;
   }
 
   public void setQuotationRecordId(Integer quotationRecordId) {
       this.quotationRecordId = quotationRecordId;
   }
   */
   
   private Integer[] assignmentId;
   
   private Integer[] quotationRecordId;
   
   private String type;
   
	private List<QuotationRecordSyncData> quotationRecords;
 
   public Integer[] getAssignmentId() {
       return assignmentId;
   }
 
   public void setAssignmentId(Integer[] assignmentId) {
       this.assignmentId = assignmentId;
   }
 
   public Integer[] getQuotationRecordId() {
       return quotationRecordId;
   }
 
   public void setQuotationRecordId(Integer[] quotationRecordId) {
       this.quotationRecordId = quotationRecordId;
   }

public List<QuotationRecordSyncData> getQuotationRecords() {
	return quotationRecords;
}

public void setQuotationRecords(List<QuotationRecordSyncData> quotationRecords) {
	this.quotationRecords = quotationRecords;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}
   
}