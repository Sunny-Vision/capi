package capi.model.masterMaintenance.businessParameterMaintenance;

import java.util.List;

import capi.entity.PECheckUnitCriteria;
import capi.entity.Purpose;
import capi.entity.ReportReasonSetting;
import capi.entity.WorkingSessionSetting;

public class DisplayModel {
	
	private String commonSummerStartDate;
	private String commonSummerEndDate;
	private String commonWinterStartDate;
	private String commonWinterEndDate;
	private String[] commonCountryOfOrigins;
	private String commonPublicHolidayUrl;
	private String commonRuaRatio;
	private String commonCalendarEventColor;
	private String commonAssignmentEventColor;
	private String commonFreezeSurveyMonth;
	private String commonMobileSynchronizationPeriod;
	private String commonLastModify;
	private String peExcludePECheckMonth;
	private String peSelectPECheckPercentage;
	private String peIncludeNewRecruitment;
	private String peIncludeRUACase;
	private String itineraryNoofAssignmentDeviation;
	private String itineraryNoofAssignmentDeviationPlus;
	private String itineraryNoofAssignmentDeviationMinus;
	private String itinerarySequenceDeviation;
	private String itinerarySequencePercents;
	private String itineraryTPUSequenceDeviation;
	private String itineraryTPUSequenceDeviationTimes;
	private String delinkPeriod;
	
	private String onSpotMessage1;
	private String onSpotMessage2;
	private String onSpotMessage3;
	private String onSpotMessage4;
	private String onSpotMessage5;
	private String onSpotMessage6;
	private String onSpotMessage7;
	private String onSpotMessage8;
	private String onSpotMessage9;
	private String onSpotMessage10;
	private String onSpotMessage11;
	private String onSpotMessage12;
	private String onSpotMessage13;
	private String onSpotMessage14;
	private String onSpotMessage15;
	private String onSpotMessage16;
	private String onSpotMessage17;
	private String onSpotMessage18;
	
	private List<PECheckUnitCriteria> pECheckUnitCriteriaList;
	private List<ReportReasonSetting> reportReasonList;
	private List<WorkingSessionSetting> workingSessionList;
	
	private List<Purpose> peIncludedPurpose;
	
	public String getCommonSummerStartDate() {
		return commonSummerStartDate;
	}
	public void setCommonSummerStartDate(String commonSummerStartDate) {
		this.commonSummerStartDate = commonSummerStartDate;
	}
	public String getCommonSummerEndDate() {
		return commonSummerEndDate;
	}
	public void setCommonSummerEndDate(String commonSummerEndDate) {
		this.commonSummerEndDate = commonSummerEndDate;
	}
	public String getCommonWinterStartDate() {
		return commonWinterStartDate;
	}
	public void setCommonWinterStartDate(String commonWinterStartDate) {
		this.commonWinterStartDate = commonWinterStartDate;
	}
	public String getCommonWinterEndDate() {
		return commonWinterEndDate;
	}
	public void setCommonWinterEndDate(String commonWinterEndDate) {
		this.commonWinterEndDate = commonWinterEndDate;
	}
	public String[] getCommonCountryOfOrigins() {
		return commonCountryOfOrigins;
	}
	public void setCommonCountryOfOrigins(String[] commonCountryOfOrigins) {
		this.commonCountryOfOrigins = commonCountryOfOrigins;
	}
	public String getCommonPublicHolidayUrl() {
		return commonPublicHolidayUrl;
	}
	public void setCommonPublicHolidayUrl(String commonPublicHolidayUrl) {
		this.commonPublicHolidayUrl = commonPublicHolidayUrl;
	}
	public String getCommonRuaRatio() {
		return commonRuaRatio;
	}
	public void setCommonRuaRatio(String commonRuaRatio) {
		this.commonRuaRatio = commonRuaRatio;
	}
	public String getCommonCalendarEventColor() {
		return commonCalendarEventColor;
	}
	public void setCommonCalendarEventColor(String commonCalendarEventColor) {
		this.commonCalendarEventColor = commonCalendarEventColor;
	}
	public String getCommonAssignmentEventColor() {
		return commonAssignmentEventColor;
	}
	public void setCommonAssignmentEventColor(String commonAssignmentEventColor) {
		this.commonAssignmentEventColor = commonAssignmentEventColor;
	}
	public String getCommonFreezeSurveyMonth() {
		return commonFreezeSurveyMonth;
	}
	public void setCommonFreezeSurveyMonth(String commonFreezeSurveyMonth) {
		this.commonFreezeSurveyMonth = commonFreezeSurveyMonth;
	}
	public String getCommonMobileSynchronizationPeriod() {
		return commonMobileSynchronizationPeriod;
	}
	public void setCommonMobileSynchronizationPeriod(
			String commonMobileSynchronizationPeriod) {
		this.commonMobileSynchronizationPeriod = commonMobileSynchronizationPeriod;
	}
	public String getCommonLastModify() {
		return commonLastModify;
	}
	public void setCommonLastModify(String commonLastModify) {
		this.commonLastModify = commonLastModify;
	}
	public List<PECheckUnitCriteria> getpECheckUnitCriteriaList() {
		return pECheckUnitCriteriaList;
	}
	public void setpECheckUnitCriteriaList(
			List<PECheckUnitCriteria> pECheckUnitCriteriaList) {
		this.pECheckUnitCriteriaList = pECheckUnitCriteriaList;
	}
	public String getPeIncludeNewRecruitment() {
		return peIncludeNewRecruitment;
	}
	public void setPeIncludeNewRecruitment(String peIncludeNewRecruitment) {
		this.peIncludeNewRecruitment = peIncludeNewRecruitment;
	}
	public String getPeIncludeRUACase() {
		return peIncludeRUACase;
	}
	public void setPeIncludeRUACase(String peIncludeRUACase) {
		this.peIncludeRUACase = peIncludeRUACase;
	}
	public String getItineraryNoofAssignmentDeviation() {
		return itineraryNoofAssignmentDeviation;
	}
	public void setItineraryNoofAssignmentDeviation(
			String itineraryNoofAssignmentDeviation) {
		this.itineraryNoofAssignmentDeviation = itineraryNoofAssignmentDeviation;
	}
	public String getItineraryNoofAssignmentDeviationPlus() {
		return itineraryNoofAssignmentDeviationPlus;
	}
	public void setItineraryNoofAssignmentDeviationPlus(
			String itineraryNoofAssignmentDeviationPlus) {
		this.itineraryNoofAssignmentDeviationPlus = itineraryNoofAssignmentDeviationPlus;
	}
	public String getItineraryNoofAssignmentDeviationMinus() {
		return itineraryNoofAssignmentDeviationMinus;
	}
	public void setItineraryNoofAssignmentDeviationMinus(
			String itineraryNoofAssignmentDeviationMinus) {
		this.itineraryNoofAssignmentDeviationMinus = itineraryNoofAssignmentDeviationMinus;
	}
	public String getItinerarySequenceDeviation() {
		return itinerarySequenceDeviation;
	}
	public void setItinerarySequenceDeviation(String itinerarySequenceDeviation) {
		this.itinerarySequenceDeviation = itinerarySequenceDeviation;
	}
	public String getItinerarySequencePercents() {
		return itinerarySequencePercents;
	}
	public void setItinerarySequencePercents(String itinerarySequencePercents) {
		this.itinerarySequencePercents = itinerarySequencePercents;
	}
	public String getItineraryTPUSequenceDeviation() {
		return itineraryTPUSequenceDeviation;
	}
	public void setItineraryTPUSequenceDeviation(
			String itineraryTPUSequenceDeviation) {
		this.itineraryTPUSequenceDeviation = itineraryTPUSequenceDeviation;
	}
	public String getItineraryTPUSequenceDeviationTimes() {
		return itineraryTPUSequenceDeviationTimes;
	}
	public void setItineraryTPUSequenceDeviationTimes(
			String itineraryTPUSequenceDeviationTimes) {
		this.itineraryTPUSequenceDeviationTimes = itineraryTPUSequenceDeviationTimes;
	}
	public String getPeExcludePECheckMonth() {
		return peExcludePECheckMonth;
	}
	public void setPeExcludePECheckMonth(String peExcludePECheckMonth) {
		this.peExcludePECheckMonth = peExcludePECheckMonth;
	}
	public String getPeSelectPECheckPercentage() {
		return peSelectPECheckPercentage;
	}
	public void setPeSelectPECheckPercentage(String peSelectPECheckPercentage) {
		this.peSelectPECheckPercentage = peSelectPECheckPercentage;
	}
	public String getOnSpotMessage1() {
		return onSpotMessage1;
	}
	public void setOnSpotMessage1(String onSpotMessage1) {
		this.onSpotMessage1 = onSpotMessage1;
	}
	public String getOnSpotMessage2() {
		return onSpotMessage2;
	}
	public void setOnSpotMessage2(String onSpotMessage2) {
		this.onSpotMessage2 = onSpotMessage2;
	}
	public String getOnSpotMessage3() {
		return onSpotMessage3;
	}
	public void setOnSpotMessage3(String onSpotMessage3) {
		this.onSpotMessage3 = onSpotMessage3;
	}
	public String getOnSpotMessage4() {
		return onSpotMessage4;
	}
	public void setOnSpotMessage4(String onSpotMessage4) {
		this.onSpotMessage4 = onSpotMessage4;
	}
	public String getOnSpotMessage5() {
		return onSpotMessage5;
	}
	public void setOnSpotMessage5(String onSpotMessage5) {
		this.onSpotMessage5 = onSpotMessage5;
	}
	public String getOnSpotMessage6() {
		return onSpotMessage6;
	}
	public void setOnSpotMessage6(String onSpotMessage6) {
		this.onSpotMessage6 = onSpotMessage6;
	}
	public String getOnSpotMessage7() {
		return onSpotMessage7;
	}
	public void setOnSpotMessage7(String onSpotMessage7) {
		this.onSpotMessage7 = onSpotMessage7;
	}
	public String getOnSpotMessage8() {
		return onSpotMessage8;
	}
	public void setOnSpotMessage8(String onSpotMessage8) {
		this.onSpotMessage8 = onSpotMessage8;
	}
	public String getOnSpotMessage9() {
		return onSpotMessage9;
	}
	public void setOnSpotMessage9(String onSpotMessage9) {
		this.onSpotMessage9 = onSpotMessage9;
	}
	public String getOnSpotMessage10() {
		return onSpotMessage10;
	}
	public void setOnSpotMessage10(String onSpotMessage10) {
		this.onSpotMessage10 = onSpotMessage10;
	}
	public String getOnSpotMessage11() {
		return onSpotMessage11;
	}
	public void setOnSpotMessage11(String onSpotMessage11) {
		this.onSpotMessage11 = onSpotMessage11;
	}
	public String getOnSpotMessage12() {
		return onSpotMessage12;
	}
	public void setOnSpotMessage12(String onSpotMessage12) {
		this.onSpotMessage12 = onSpotMessage12;
	}
	public String getOnSpotMessage13() {
		return onSpotMessage13;
	}
	public void setOnSpotMessage13(String onSpotMessage13) {
		this.onSpotMessage13 = onSpotMessage13;
	}
	public String getOnSpotMessage14() {
		return onSpotMessage14;
	}
	public void setOnSpotMessage14(String onSpotMessage14) {
		this.onSpotMessage14 = onSpotMessage14;
	}
	public String getOnSpotMessage15() {
		return onSpotMessage15;
	}
	public void setOnSpotMessage15(String onSpotMessage15) {
		this.onSpotMessage15 = onSpotMessage15;
	}
	public String getOnSpotMessage16() {
		return onSpotMessage16;
	}
	public void setOnSpotMessage16(String onSpotMessage16) {
		this.onSpotMessage16 = onSpotMessage16;
	}
	public String getOnSpotMessage17() {
		return onSpotMessage17;
	}
	public void setOnSpotMessage17(String onSpotMessage17) {
		this.onSpotMessage17 = onSpotMessage17;
	}
	public String getOnSpotMessage18() {
		return onSpotMessage18;
	}
	public void setOnSpotMessage18(String onSpotMessage18) {
		this.onSpotMessage18 = onSpotMessage18;
	}
	public List<PECheckUnitCriteria> getPeCheckUnitCriteriaList() {
		return pECheckUnitCriteriaList;
	}
	public List<ReportReasonSetting> getReportReasonList() {
		return reportReasonList;
	}
	public void setReportReasonList(List<ReportReasonSetting> reportReasonList) {
		this.reportReasonList = reportReasonList;
	}
	public List<WorkingSessionSetting> getWorkingSessionList() {
		return workingSessionList;
	}
	public void setWorkingSessionList(List<WorkingSessionSetting> workingSessionList) {
		this.workingSessionList = workingSessionList;
	}
	public List<Purpose> getPeIncludedPurpose() {
		return peIncludedPurpose;
	}
	public void setPeIncludedPurpose(List<Purpose> peIncludedPurpose) {
		this.peIncludedPurpose = peIncludedPurpose;
	}
	public void setDelinkPeriod(String delinkPeriod) {
		this.delinkPeriod = delinkPeriod;
	}
	public String getDelinkPeriod() {
		return delinkPeriod;
	}
	
}
