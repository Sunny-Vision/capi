package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SubPriceRecord;
import capi.model.SystemConstant;
import capi.model.api.dataSync.SubPriceRecordSyncData;

@Repository("SubPriceRecordDao")
public class SubPriceRecordDao extends GenericDao<SubPriceRecord> {
	@SuppressWarnings("unchecked")
	public List<SubPriceRecord> getAllByQuotationRecordId(int id) {
		Criteria criteria = this.createCriteria("record")
				.add(Restrictions.eq("record.quotationRecord.id", id))
				.addOrder(Order.asc("record.sequence"));
		return criteria.list();
	}
	
	/**
	 *  Data Sync 
	 */
	public List<SubPriceRecordSyncData> getUpdatedSubPriceRecord(Date lastSyncTime, Integer[] assignmentIds, Integer[] quotationRecordIds, Integer[] subPriceRecordIds){
		String hql = "select sr.subPriceRecordId as subPriceRecordId"
				+ ", st.subPriceTypeId as subPriceTypeId, sr.sequence as sequence"
				+ ", qr.quotationRecordId as quotationRecordId, sr.createdDate as createdDate"
				+ ", sr.modifiedDate as modifiedDate, sr.sPrice as sPrice, sr.nPrice as nPrice"
				+ ", sr.discount as discount"
				+ " from SubPriceRecord as sr"
				+ " left join sr.subPriceType as st"
				+ " left join sr.quotationRecord as qr"
				+ " left join qr.assignment as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and sr.modifiedDate >= :modifiedDate";
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " and a.assignmentId in ( :assignmentIds )";
		}
		
		if(quotationRecordIds!=null && quotationRecordIds.length>0){
			hql += " and qr.quotationRecordId in ( :quotationRecordIds )";
		}
		
		if(subPriceRecordIds!=null && subPriceRecordIds.length>0){
			hql += " and sr.subPriceRecordId in ( :subPriceRecordIds )";
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
		
		if(subPriceRecordIds!=null && subPriceRecordIds.length>0){
			query.setParameterList("subPriceRecordIds", subPriceRecordIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SubPriceRecordSyncData.class));
		return query.list();
	}
	
	public List<SubPriceRecordSyncData> getHistorySubPriceRecordByAssignmentIds(List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT sp.* FROM QuotationRecord qr, SubPriceRecord sp, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.QuotationRecordId = sp.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<SubPriceRecordSyncData> result1 = addScalarForSubPriceRecord(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(SubPriceRecordSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT sp.* FROM QuotationRecord qr, SubPriceRecord sp, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.QuotationRecordId = sp.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<SubPriceRecordSyncData> result2 = addScalarForSubPriceRecord(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(SubPriceRecordSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<SubPriceRecordSyncData> returnResult = new ArrayList<SubPriceRecordSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<SubPriceRecordSyncData> getHistorySubPriceRecordByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT sp.* FROM QuotationRecord qr, SubPriceRecord sp, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.QuotationRecordId = sp.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<SubPriceRecordSyncData> result1 = addScalarForSubPriceRecord(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(SubPriceRecordSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT sp.* FROM QuotationRecord qr, SubPriceRecord sp, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.QuotationRecordId = sp.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<SubPriceRecordSyncData> result2 = addScalarForSubPriceRecord(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(SubPriceRecordSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<SubPriceRecordSyncData> returnResult = new ArrayList<SubPriceRecordSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public SQLQuery addScalarForSubPriceRecord(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("subPriceRecordId", StandardBasicTypes.INTEGER)
		.addScalar("subPriceTypeId", StandardBasicTypes.INTEGER)
		.addScalar("sequence", StandardBasicTypes.INTEGER)
		.addScalar("quotationRecordId", StandardBasicTypes.INTEGER)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("sPrice", StandardBasicTypes.DOUBLE)
		.addScalar("nPrice", StandardBasicTypes.DOUBLE)
		.addScalar("discount", StandardBasicTypes.STRING)
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		;
		
		return sqlQuery;
	}	
	
	public List<String> getSubPriceFieldAndValue(String subpriceRecordId) {
		String sql = "select sf.FieldName+' : '+sc.ColumnValue as subPriceFieldValue from SubPriceRecord s "
				+ "join SubPriceColumn sc on sc.SubPriceRecordId = s.SubPriceRecordId " 
				+ "join SubPriceFieldMapping sm on sm.SubPriceFieldMappingId = sc.SubPriceFieldMappingId "  
				+ "join SubPriceField sf on sf.SubPriceFieldId = sm.SubPriceFieldId "
				+ "where s.subPriceRecordId = :subpriceRecordId "
				+ "group by s.QuotationRecordId, s.SubPriceRecordId,sf.FieldName,sc.ColumnValue,sm.Sequence " 
				+ "order by s.SubPriceRecordId,sm.[Sequence]  ";
				
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("subpriceRecordId", subpriceRecordId);
		query.addScalar("subPriceFieldValue",StandardBasicTypes.STRING);
		return query.list();
	}
	
}
