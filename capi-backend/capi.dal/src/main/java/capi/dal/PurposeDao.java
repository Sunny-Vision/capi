package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Purpose;
import capi.model.api.dataSync.PurposeSyncData;
import capi.model.dataConversion.quotationRecordDataConversion.PurposeIndoorQuotationRecordCountModel;
import capi.model.masterMaintenance.SurveyTypeTableList;

@Repository("PurposeDao")
public class PurposeDao extends GenericDao<Purpose>{
	@SuppressWarnings("unchecked")
	public List<Purpose> getAll() {
		return this.createCriteria().addOrder(Order.asc("code")).addOrder(Order.asc("name")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Purpose> search(String search, int firstRecord, int displayLength) {
		// to be modified
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("code")).addOrder(Order.asc("name"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> searchSurvey(String search, Integer purposeId, int firstRecord, int displayLength) {
		// to be modified
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.survey LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		if(purposeId != null){
			criteria.add(
					Restrictions.sqlRestriction("{alias}.purposeId = (?)", purposeId, StandardBasicTypes.INTEGER)
				);
		}
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("survey"), "survey");
		
		criteria.setProjection(projList);
		
		return criteria.list();
	}

	public long countSearch(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SurveyTypeTableList> selectAllSurveyType(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria();

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("purposeId"), "purposeId");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("name"), "name");
		projList.add(Projections.property("survey"), "survey");
		projList.add(Projections.property("note"), "note");
		
		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("name", "%" + search + "%"),
					Restrictions.like("survey", "%" + search + "%"),
					Restrictions.like("note", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(SurveyTypeTableList.class));

		return criteria.list();
	}

	public long countSelectAllSurveyType(String search) {

		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("name", "%" + search + "%"),
					Restrictions.like("survey", "%" + search + "%"),
					Restrictions.like("note", "%" + search + "%")
			));
		}

		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<Purpose> getSurveyTypesByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("purposeId", ids));
		
		return criteria.list();
	}

	public Purpose getSurveyTypeByCode(String surveyTypeCode){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", surveyTypeCode));
		return (Purpose)criteria.uniqueResult();
	}
	
	public List<Purpose> findPurposeBySurvey(String survey){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("survey", survey));
		return criteria.list();
	}
	
	public List<PurposeSyncData> getUpdatePurpose(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("purposeId"), "purposeId");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("name"), "name");
		projList.add(Projections.property("survey"), "survey");
		projList.add(Projections.property("note"), "note");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		projList.add(Projections.property("peIncluded"), "peIncluded");
		projList.add(Projections.property("enumerationOutcomes"), "enumerationOutcomes");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(PurposeSyncData.class));
		return criteria.list();
	}

	public List<String> getDistinctSurvey() {
		// to be modified
		Criteria criteria = this.createCriteria();		
		criteria.setProjection(Projections.distinct(Projections.property("survey")));		
		return criteria.list();
	}
	
	
	public List<Purpose> getPEIncludedPurposes(){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("peIncluded", true));
		return criteria.list();
	}
	
	
	public List<Purpose> getNotExistedPEIncludedPurpose(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("purposeId", ids)))
				.add(Restrictions.eq("peIncluded", true));
		return criteria.list();
	}
	
	public List<PurposeIndoorQuotationRecordCountModel> countIndoorQuotationRecords(Date referenceMonth, String[] status, String notEqStatus, Integer userId){
		String hql = "select p.name as purposeName,"
				+ " p.id as purposeId,"
				+ " count(iqr.indoorQuotationRecordId) as countOfIndoorQuotationRecord"
				+ " from Purpose p"
				+ " left join p.units u"
				+ " left join u.quotations q"
				+ " left join q.indoorQuotationRecords iqr on (iqr.referenceMonth = :referenceMonth ";
		if (userId != null)
			hql += " and iqr.user.userId = :userId ";
		
		if(status != null && status.length > 0){
			hql = hql + " and iqr.status in (:status)";
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			hql = hql + " and iqr.status != :notEqStatus";
		}
		hql = hql + " )";
		
		hql += " where p.survey = 'MRPS' ";
		
		hql += " group by p.name, p.purposeId";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("referenceMonth", referenceMonth);
		if (userId != null)
			query.setParameter("userId", userId);
		if(status != null && status.length > 0){
			query.setParameterList("status", status);
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			query.setParameter("notEqStatus", notEqStatus);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(PurposeIndoorQuotationRecordCountModel.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PurposeIndoorQuotationRecordCountModel> countIndoorQuotationRecordsByBatches(Date referenceMonth, String[] status, String notEqStatus, List<Integer> batchIds){
		String conditionSql = "";

		if (batchIds != null && batchIds.size() > 0)
			conditionSql += " and q.batchId in :batchIds ";
		else
			conditionSql += " and q.batchId is null";
		
		if(status != null && status.length > 0){
			conditionSql += " and iqr.status in (:status)";
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			conditionSql += " and iqr.status <> :notEqStatus";
		}
		
		String sql = " select p.Name as purposeName, p.PurposeId as purposeId, case when countOfIndoorQuotationRecord is null then 0 else countOfIndoorQuotationRecord end as countOfIndoorQuotationRecord "
						+ " from Purpose as p "
						+ " left join ( "
						+ " select count(iqr.IndoorQuotationRecordId) as countOfIndoorQuotationRecord, u.PurposeId "
						+ " from IndoorQuotationRecord as iqr "
						+ " inner join Quotation as q on q.QuotationId = iqr.QuotationId "
						+ " inner join Unit as u on u.UnitId = q.UnitId "
						+ " where "
						+ " iqr.ReferenceMonth = :referenceMonth "
						+ conditionSql
						+ " group by u.PurposeId "
						+ " ) as iqr on iqr.PurposeId = p.PurposeId ";
		
		sql += " where p.survey = 'MRPS' ";
		
		SQLQuery query = getSession().createSQLQuery(sql);

		query.setParameter("referenceMonth", referenceMonth);
		if (batchIds != null && batchIds.size() > 0)
			query.setParameterList("batchIds", batchIds);
		if(status != null && status.length > 0){
			query.setParameterList("status", status);
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			query.setParameter("notEqStatus", notEqStatus);
		}
		
		query.addScalar("purposeName", StandardBasicTypes.STRING);
		query.addScalar("purposeId", StandardBasicTypes.INTEGER);
		query.addScalar("countOfIndoorQuotationRecord", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(PurposeIndoorQuotationRecordCountModel.class));
		
		return query.list();
	}
	
	public List<Purpose> getPEIncludedPurpose(){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("peIncluded", true));
		
		return criteria.list();
	}
	
	public List<Purpose> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("purposeId", ids));
		
		return criteria.list();
	}
	
}
