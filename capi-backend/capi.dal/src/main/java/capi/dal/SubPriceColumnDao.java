package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SubPriceColumn;
import capi.model.SystemConstant;
import capi.model.api.dataSync.SubPriceColumnSyncData;

@Repository("SubPriceColumnDao")
public class SubPriceColumnDao extends GenericDao<SubPriceColumn> {
	@SuppressWarnings("unchecked")
	public List<SubPriceColumn> getAllByRecordId(int id) {
		Criteria criteria = this.createCriteria("column")
				.createAlias("column.subPriceFieldMapping", "mapping", JoinType.INNER_JOIN)
				.add(Restrictions.eq("column.subPriceRecord.id", id))
				.addOrder(Order.asc("mapping.sequence"));
		criteria.setFetchMode("subPriceFieldMapping", FetchMode.JOIN);
		criteria.setFetchMode("subPriceFieldMapping.subPriceField", FetchMode.JOIN);
		return criteria.list();
	}
	
	
	public List<SubPriceColumn> getAllByQuotationRecordId(int id) {
		Criteria criteria = this.createCriteria("column")
				.createAlias("column.subPriceFieldMapping", "mapping", JoinType.INNER_JOIN)
				.createAlias("column.subPriceRecord", "subprice")
				.createAlias("subprice.quotationRecord", "qr")
				.add(Restrictions.eq("qr.id", id))
				.addOrder(Order.asc("subprice.sequence"))
				.addOrder(Order.asc("mapping.sequence"));
		
		criteria.setFetchMode("subPriceRecord", FetchMode.JOIN);
		criteria.setFetchMode("subPriceFieldMapping", FetchMode.JOIN);
		criteria.setFetchMode("subPriceFieldMapping.subPriceField", FetchMode.JOIN);
		return criteria.list();
	}
	
	public List<SubPriceColumnSyncData> getUpdatedSubPriceColumn(Date lastSyncTime, Integer[] assignmentIds, Integer[] quotationRecordIds, Integer[] subPriceColumnIds){
		String hql = "select sc.subPriceColumnId as subPriceColumnId"
				+ ", sc.columnValue as columnValue, sc.createdDate as createdDate"
				+ ", sc.modifiedDate as modifiedDate, sfm.subPriceFieldMappingId as subPriceFieldMappingId"
				+ ", sr.subPriceRecordId as subPriceRecordId"
				+ " from SubPriceColumn as sc"
				+ " left join sc.subPriceFieldMapping as sfm"
				+ " left join sc.subPriceRecord as sr"
				+ " left join sr.quotationRecord as qr"
				+ " left join qr.assignment as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and sc.modifiedDate >= :modifiedDate";
		}
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " and a.assignmentId in ( :assignmentIds )";
		}
		if(quotationRecordIds!=null && quotationRecordIds.length>0){
			hql += " and qr.quotationRecordId in ( :quotationRecordIds )";
		}
		if(subPriceColumnIds!=null && subPriceColumnIds.length>0){
			hql += " and sc.subPriceColumnId in ( :subPriceColumnIds )";
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
		if(subPriceColumnIds!=null && subPriceColumnIds.length>0){
			query.setParameterList("subPriceColumnIds", subPriceColumnIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SubPriceColumnSyncData.class));
		return query.list();
	}
	
	public List<SubPriceColumnSyncData> getHistorySubPriceColumnByAssignmentIds(List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT sc.* FROM QuotationRecord qr, SubPriceRecord sp, SubPriceColumn sc, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND sp.quotationRecordId = qr.quotationRecordId AND sc.subPriceRecordId = sp.subPriceRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<SubPriceColumnSyncData> result1 = addScalarForSubPriceColumn(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(SubPriceColumnSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT sc.* FROM QuotationRecord qr, SubPriceRecord sp, SubPriceColumn sc, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND sp.quotationRecordId = qr.quotationRecordId AND sc.subPriceRecordId = sp.subPriceRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<SubPriceColumnSyncData> result2 = addScalarForSubPriceColumn(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(SubPriceColumnSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<SubPriceColumnSyncData> returnResult = new ArrayList<SubPriceColumnSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<SubPriceColumnSyncData> getHistorySubPriceColumnByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT sc.* FROM QuotationRecord qr, SubPriceRecord sp, SubPriceColumn sc, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND sp.quotationRecordId = qr.quotationRecordId AND sc.subPriceRecordId = sp.subPriceRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<SubPriceColumnSyncData> result1 = addScalarForSubPriceColumn(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(SubPriceColumnSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT sc.* FROM QuotationRecord qr, SubPriceRecord sp, SubPriceColumn sc, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND sp.quotationRecordId = qr.quotationRecordId AND sc.subPriceRecordId = sp.subPriceRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<SubPriceColumnSyncData> result2 = addScalarForSubPriceColumn(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(SubPriceColumnSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<SubPriceColumnSyncData> returnResult = new ArrayList<SubPriceColumnSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public SQLQuery addScalarForSubPriceColumn(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("subPriceColumnId", StandardBasicTypes.INTEGER)
		.addScalar("columnValue", StandardBasicTypes.STRING)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("subPriceFieldMappingId", StandardBasicTypes.INTEGER)
		.addScalar("subPriceRecordId", StandardBasicTypes.INTEGER)
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		;
		
		return sqlQuery;
	}	
}
