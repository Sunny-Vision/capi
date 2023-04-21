package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.TourRecord;
import capi.model.SystemConstant;
import capi.model.api.dataSync.TourRecordSyncData;

@Repository("TourRecordDao")
public class TourRecordDao  extends GenericDao<TourRecord>{
	
	public List<TourRecordSyncData> getUpdatedTourRecord(Date lastSyncTime, Integer[] assignmentIds, Integer[] quotationRecordIds, Integer[] tourRecordIds){
		String hql = "select tr.tourRecordId as tourRecordId"
				+ ", tr.day1Price as day1Price, tr.day2Price as day2Price"
				+ ", tr.day3Price as day3Price, tr.day4Price as day4Price"
				+ ", tr.day5Price as day5Price, tr.day6Price as day6Price"
				+ ", tr.day7Price as day7Price, tr.day8Price as day8Price"
				+ ", tr.day9Price as day9Price, tr.day10Price as day10Price"
				+ ", tr.day11Price as day11Price, tr.day12Price as day12Price"
				+ ", tr.day13Price as day13Price, tr.day14Price as day14Price"
				+ ", tr.day15Price as day15Price, tr.day16Price as day16Price"
				+ ", tr.day17Price as day17Price, tr.day18Price as day18Price"
				+ ", tr.day19Price as day19Price, tr.day20Price as day20Price"
				+ ", tr.day21Price as day21Price, tr.day22Price as day22Price"
				+ ", tr.day23Price as day23Price, tr.day24Price as day24Price"
				+ ", tr.day25Price as day25Price, tr.day26Price as day26Price"
				+ ", tr.day27Price as day27Price, tr.day28Price as day28Price"
				+ ", tr.day29Price as day29Price, tr.day30Price as day30Price"
				+ ", tr.day31Price as day31Price, tr.extraPrice1Name as extraPrice1Name"
				+ ", tr.extraPrice1Value as extraPrice1Value, tr.isExtraPrice1Count as isExtraPrice1Count"
				+ ", tr.extraPrice2Name as extraPrice2Name, tr.extraPrice2Value as extraPrice2Value"
				+ ", tr.isExtraPrice2Count as isExtraPrice2Count, tr.extraPrice3Name as extraPrice3Name"
				+ ", tr.extraPrice3Value as extraPrice3Value, tr.isExtraPrice3Count as isExtraPrice3Count"
				+ ", tr.extraPrice4Name as extraPrice4Name, tr.extraPrice4Value as extraPrice4Value"
				+ ", tr.isExtraPrice4Count as isExtraPrice4Count, tr.extraPrice5Name as extraPrice5Name"
				+ ", tr.extraPrice5Value as extraPrice5Value, tr.isExtraPrice5Count as isExtraPrice5Count"
				+ ", tr.createdDate as createdDate, tr.modifiedDate as modifiedDate"
				+ " from TourRecord as tr"
				+ " left join tr.quotationRecord as qr"
				+ " left join qr.assignment as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and tr.modifiedDate >= :modifiedDate";
		}
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " and a.assignmentId in ( :assignmentIds )";
		}
		if(quotationRecordIds!=null && quotationRecordIds.length>0){
			hql += " and qr.quotationRecordId in ( :quotationRecordIds )";
		}
		if(tourRecordIds!=null && tourRecordIds.length>0){
			hql += " and tr.tourRecordId in ( :tourRecordIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		if(quotationRecordIds!=null && quotationRecordIds.length>0){
			query.setParameterList("quotationRecordIds", quotationRecordIds);
		}
		if(tourRecordIds!=null && tourRecordIds.length>0){
			query.setParameterList("tourRecordIds", tourRecordIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(TourRecordSyncData.class));
		return query.list();
	}
	
	public List<TourRecordSyncData> getHistoryTourRecordByAssignmentIds(List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT tr.* FROM QuotationRecord qr, TourRecord tr, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND tr.TourRecordId = qr.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<TourRecordSyncData> result1 = addScalarForTourRecord(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(TourRecordSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT tr.* FROM QuotationRecord qr, TourRecord tr, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND tr.TourRecordId = qr.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<TourRecordSyncData> result2 = addScalarForTourRecord(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(TourRecordSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<TourRecordSyncData> returnResult = new ArrayList<TourRecordSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<TourRecordSyncData> getHistoryTourRecordByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT tr.* FROM QuotationRecord qr, TourRecord tr, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND tr.TourRecordId = qr.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<TourRecordSyncData> result1 = addScalarForTourRecord(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(TourRecordSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT tr.* FROM QuotationRecord qr, TourRecord tr, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND tr.TourRecordId = qr.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<TourRecordSyncData> result2 = addScalarForTourRecord(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(TourRecordSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<TourRecordSyncData> returnResult = new ArrayList<TourRecordSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public SQLQuery addScalarForTourRecord(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("tourRecordId", StandardBasicTypes.INTEGER)
		.addScalar("day1Price", StandardBasicTypes.DOUBLE).addScalar("day2Price", StandardBasicTypes.DOUBLE)
		.addScalar("day3Price", StandardBasicTypes.DOUBLE).addScalar("day4Price", StandardBasicTypes.DOUBLE)
		.addScalar("day5Price", StandardBasicTypes.DOUBLE).addScalar("day6Price", StandardBasicTypes.DOUBLE)
		.addScalar("day7Price", StandardBasicTypes.DOUBLE).addScalar("day8Price", StandardBasicTypes.DOUBLE)
		.addScalar("day9Price", StandardBasicTypes.DOUBLE).addScalar("day10Price", StandardBasicTypes.DOUBLE)
		.addScalar("day11Price", StandardBasicTypes.DOUBLE).addScalar("day12Price", StandardBasicTypes.DOUBLE)
		.addScalar("day13Price", StandardBasicTypes.DOUBLE).addScalar("day14Price", StandardBasicTypes.DOUBLE)
		.addScalar("day15Price", StandardBasicTypes.DOUBLE).addScalar("day16Price", StandardBasicTypes.DOUBLE)
		.addScalar("day17Price", StandardBasicTypes.DOUBLE).addScalar("day18Price", StandardBasicTypes.DOUBLE)
		.addScalar("day19Price", StandardBasicTypes.DOUBLE).addScalar("day20Price", StandardBasicTypes.DOUBLE)
		.addScalar("day21Price", StandardBasicTypes.DOUBLE).addScalar("day22Price", StandardBasicTypes.DOUBLE)
		.addScalar("day23Price", StandardBasicTypes.DOUBLE).addScalar("day24Price", StandardBasicTypes.DOUBLE)
		.addScalar("day25Price", StandardBasicTypes.DOUBLE).addScalar("day26Price", StandardBasicTypes.DOUBLE)
		.addScalar("day27Price", StandardBasicTypes.DOUBLE).addScalar("day28Price", StandardBasicTypes.DOUBLE)
		.addScalar("day29Price", StandardBasicTypes.DOUBLE).addScalar("day30Price", StandardBasicTypes.DOUBLE)
		.addScalar("day31Price", StandardBasicTypes.DOUBLE)
		.addScalar("extraPrice1Name", StandardBasicTypes.STRING)
		.addScalar("extraPrice1Value", StandardBasicTypes.DOUBLE)
		.addScalar("isExtraPrice1Count", StandardBasicTypes.BOOLEAN)
		.addScalar("extraPrice2Name", StandardBasicTypes.STRING)
		.addScalar("extraPrice2Value", StandardBasicTypes.DOUBLE)
		.addScalar("isExtraPrice2Count", StandardBasicTypes.BOOLEAN)
		.addScalar("extraPrice3Name", StandardBasicTypes.STRING)
		.addScalar("extraPrice3Value", StandardBasicTypes.DOUBLE)
		.addScalar("isExtraPrice3Count", StandardBasicTypes.BOOLEAN)
		.addScalar("extraPrice4Name", StandardBasicTypes.STRING)
		.addScalar("extraPrice4Value", StandardBasicTypes.DOUBLE)
		.addScalar("isExtraPrice4Count", StandardBasicTypes.BOOLEAN)
		.addScalar("extraPrice5Name", StandardBasicTypes.STRING)
		.addScalar("extraPrice5Value", StandardBasicTypes.DOUBLE)
		.addScalar("isExtraPrice5Count", StandardBasicTypes.BOOLEAN)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		;
		
		return sqlQuery;
	}	
}
