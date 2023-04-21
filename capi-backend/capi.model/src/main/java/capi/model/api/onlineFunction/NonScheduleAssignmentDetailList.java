package capi.model.api.onlineFunction;

import java.util.List;

import capi.model.api.dataSync.AssignmentSyncData;
import capi.model.api.dataSync.AssignmentUnitCategoryInfoSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.QuotationRecordSyncData;
import capi.model.api.dataSync.QuotationSyncData;
import capi.model.api.dataSync.SubPriceColumnSyncData;
import capi.model.api.dataSync.SubPriceRecordSyncData;
import capi.model.api.dataSync.TourRecordSyncData;


public class NonScheduleAssignmentDetailList {

	private List<AssignmentSyncData> assignments;
	
	private List<OutletSyncData> outlets;
	
	private List<QuotationRecordSyncData> quotationRecords;
	
	private List<QuotationSyncData> quotations;
	
	private List<SubPriceColumnSyncData> subPriceColumns;
	
	private List<SubPriceRecordSyncData> subPriceRecords;
	
	private List<TourRecordSyncData> tourRecords;
	
	private List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategoryInfos;

	public List<AssignmentSyncData> getAssignments() {
		return assignments;
	}

	public void setAssignments(List<AssignmentSyncData> assignments) {
		this.assignments = assignments;
	}

	public List<OutletSyncData> getOutlets() {
		return outlets;
	}

	public void setOutlets(List<OutletSyncData> outlets) {
		this.outlets = outlets;
	}

	public List<QuotationRecordSyncData> getQuotationRecords() {
		return quotationRecords;
	}

	public void setQuotationRecords(List<QuotationRecordSyncData> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}

	public List<QuotationSyncData> getQuotations() {
		return quotations;
	}

	public void setQuotations(List<QuotationSyncData> quotations) {
		this.quotations = quotations;
	}

	public List<SubPriceColumnSyncData> getSubPriceColumns() {
		return subPriceColumns;
	}

	public void setSubPriceColumns(List<SubPriceColumnSyncData> subPriceColumns) {
		this.subPriceColumns = subPriceColumns;
	}

	public List<SubPriceRecordSyncData> getSubPriceRecords() {
		return subPriceRecords;
	}

	public void setSubPriceRecords(List<SubPriceRecordSyncData> subPriceRecords) {
		this.subPriceRecords = subPriceRecords;
	}

	public List<TourRecordSyncData> getTourRecords() {
		return tourRecords;
	}

	public void setTourRecords(List<TourRecordSyncData> tourRecords) {
		this.tourRecords = tourRecords;
	}

	public List<AssignmentUnitCategoryInfoSyncData> getAssignmentUnitCategoryInfos() {
		return assignmentUnitCategoryInfos;
	}

	public void setAssignmentUnitCategoryInfos(List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategoryInfos) {
		this.assignmentUnitCategoryInfos = assignmentUnitCategoryInfos;
	}

	
}
