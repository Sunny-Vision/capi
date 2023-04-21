package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.OutletType;
import capi.model.JsTreeResponseModel;
import capi.model.api.dataSync.OutletTypeSyncData;
import capi.model.dataImportExport.ImportRebasingItemList;
import capi.model.dataImportExport.ImportRebasingOutletTypeList;
import capi.model.itineraryPlanning.OutletTypeFilter;
import capi.model.masterMaintenance.UnitCommonModel;

@Repository("OutletTypeDao")
public class OutletTypeDao  extends GenericDao<OutletType>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, "
				//+ " concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " (a.code+ ' - '+ isnull(a.chineseName,'') + ' ('+ isnull(a.englishName, '')+ ')') as label, "
				+ " case when count (distinct b.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as b "
				+ " inner join b.outletType as a "
				+ " where a.item.id = :id "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) ";
		
		if (purposeIds != null && purposeIds.size() > 0) {
			hql += " and u.purpose.id in :purposeIds ";
		}
		
		if (onlyActive) {
			hql += " and u.status <> 'Inactive'";
		}
		
		hql += " group by a.id, a.code, a.chineseName, a.englishName "
				+ " order by a.code, a.chineseName, a.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("id", id);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		if (purposeIds != null && purposeIds.size() > 0) {
			query.setParameterList("purposeIds", purposeIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(JsTreeResponseModel.class));
		return query.list();
	}
	
	public OutletType getByCode(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as b "
				+ " inner join b.outletType as a "
				+ " where a.code = :code and u.cpiBasePeriod = :basePeriod  "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) ";
//				+ " group by a.id, a.code, a.chineseName, a.englishName "
//				+ " order by a.code, a.chineseName, a.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		query.setParameter("basePeriod", basePeriod);
		
//		query.setResultTransformer(Transformers.aliasToBean(OutletType.class));
		return (OutletType)query.uniqueResult();
	}
	
	public OutletType getByCodeWithoutDate(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as b "
				+ " inner join b.outletType as a "
				+ " where a.code = :code and u.cpiBasePeriod = :basePeriod  ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("basePeriod", basePeriod);
		
		return (OutletType)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<OutletType> search(String search, int firstRecord, int displayLength, String cpiBasePeriod, String itemCode) {
		String hql = "select a.id as outletTypeId, a.code as code, a.chineseName as chineseName, a.englishName as englishName "
				+ " from Unit as u "
				+ " inner join u.subItem as b "
				+ " inner join b.outletType as a "
				+ " inner join a.item as i"
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql	+= " and u.cpiBasePeriod = :cpiBasePeriod ";
		}
		if (!StringUtils.isEmpty(itemCode)){
			hql += " and i.code = :itemCode ";
		}
		hql	+= " group by a.id, a.code, a.chineseName, a.englishName "
				+ " order by a.code, a.chineseName, a.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			query.setParameter("cpiBasePeriod", cpiBasePeriod);
		}
		if(!StringUtils.isEmpty(itemCode)){
			query.setParameter("itemCode", itemCode);
		}
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		query.setResultTransformer(Transformers.aliasToBean(OutletType.class));
		return query.list();
	}

	public long countSearch(String search, String cpiBasePeriod, String itemCode) {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		
		Criteria criteria = this.createCriteria("ot")
				.createCriteria("ot.subItems", "si")
				.createAlias("si.units", "u")
				.createAlias("ot.item", "i");
		
		criteria.add(Restrictions.or(
				Restrictions.isNull("u.effectiveDate"),
				Restrictions.le("u.effectiveDate", today)
			));
		criteria.add(Restrictions.or(
				Restrictions.isNull("u.obsoleteDate"),
				Restrictions.gt("u.obsoleteDate", today)
			));
		
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.sqlRestriction("concat({alias}.code, ' - ', {alias}.chineseName) like ?", 
				String.format("%%%s%%", search), StandardBasicTypes.STRING));
		}
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			criteria.add(Restrictions.eq("u.cpiBasePeriod", cpiBasePeriod));
		}
		if (!StringUtils.isEmpty(itemCode)){
			criteria.add(Restrictions.eq("i.code", itemCode));
		}
		
		criteria.setProjection(SQLProjectionExt.groupCount(
					Projections.groupProperty("ot.id"),
					Projections.groupProperty("ot.code"),
					Projections.groupProperty("ot.chineseName"),
					Projections.groupProperty("ot.englishName")
				));
		
//		String hql = "select count(distinct a.id) "
//				+ " from Unit as u "
//				+ " inner join u.subItem as b "
//				+ " inner join b.outletType as a "
//				+ " where 1 = 1 "
//				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
//				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
//				+ " and concat(a.code, ' - ', a.chineseName) like :search "
//				+ " group by a.id, a.code, a.chineseName, a.englishName "
//				+ " order by a.code, a.chineseName, a.englishName ";
//		
//		Query query = this.getSession().createQuery(hql);
//		
//		Calendar cal = Calendar.getInstance();
//	    cal.set(Calendar.HOUR_OF_DAY, 0);
//	    cal.set(Calendar.MINUTE, 0);
//	    cal.set(Calendar.SECOND, 0);
//	    cal.set(Calendar.MILLISECOND, 0);
//	    Date dateWithoutTime = cal.getTime();
//		query.setParameter("today", dateWithoutTime);
//		
//		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<OutletType> getAllByShoftCode(String code) {
		return (List<OutletType>)this.createCriteria().add(Restrictions.like("code", code, MatchMode.END)).list();
	}

	public String getOutletTypeCodeBySubItemCode(String code, String cpiBasePeriod) {
		String hql = "select distinct a.code as code "
				+ " from SubItem as b "
				+ " inner join b.outletType as a "
				+ " inner join b.units as u "
				+ " where b.code = :code "
				+ " and u.cpiBasePeriod = :cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("cpiBasePeriod", cpiBasePeriod);
		
		return (String)query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select ot.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " group by ot.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIdsBySectionId(List<Integer> ids) {
		String hql = "select ot.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by ot.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByGroupId(List<Integer> ids) {
		String hql = "select ot.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by ot.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIdsBySubGroupId(List<Integer> ids) {
		String hql = "select ot.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by ot.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByItemId(List<Integer> ids) {
		String hql = "select ot.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by ot.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	public List<OutletTypeSyncData> getUpdateOutletType(Date lastSyncTime){
		Criteria criteria = this.createCriteria("o")
				.createAlias("o.item", "i");
		criteria.add(Restrictions.ge("o.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("o.outletTypeId"), "outletTypeId");
		projList.add(Projections.property("o.code"), "code");
		projList.add(Projections.property("o.chineseName"), "chineseName");
		projList.add(Projections.property("o.englishName"), "englishName");
		projList.add(Projections.property("i.itemId"), "itemId");
		projList.add(Projections.property("o.obsoleteDate"), "obsoleteDate");
		projList.add(Projections.property("o.effectiveDate"), "effectiveDate");
		projList.add(Projections.property("o.createdDate"), "createdDate");
		projList.add(Projections.property("o.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(OutletTypeSyncData.class));
		return criteria.list();
	}
	
	public UnitCommonModel getCommonModelById(Integer id){
		String hql = "select a.id as id, a.code as code, a.chineseName as chineseName, a.englishName as englishName, u.cpiBasePeriod as  cpiBasePeriod "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as a "
				+ " where a.id = :id  "
				+ " group by a.id, a.code, a.chineseName, a.englishName, u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("id", id);
		query.setResultTransformer(Transformers.aliasToBean(UnitCommonModel.class));
		return (UnitCommonModel)query.uniqueResult();
	}
	

	@SuppressWarnings("unchecked")
	public List<OutletTypeFilter> searchOutletTypeWithOutletIds(String search, int firstRecord, int displayLength, Integer[] outletIds){
		String hql = "select distinct o.outletId as outletId, t.shortCode as shortCode, t.chineseName as chineseName, t.englishName as englishName "
				+ " from Outlet as o "
				+ " inner join o.outletTypes as t "
				+ " where 1 = 1 "
				+ " and concat(t.shortCode, ' - ', t.chineseName) like :search "
				+ ((outletIds != null && outletIds.length > 0) ? " and o.outletId in (:outletIds) " : "")
				+ " group by o.outletId, t.shortCode, t.chineseName, t.englishName "
				+ " order by t.shortCode, t.chineseName, t.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		if (outletIds != null && outletIds.length > 0) {
			query.setParameterList("outletIds", outletIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(OutletTypeFilter.class));
		return query.list();
	}
	
	public List<ImportRebasingOutletTypeList> insertOutletTypeByRebasing(String values, Date effectiveDate, List<ImportRebasingOutletTypeList> newOutletTypeList, Map<String, ImportRebasingItemList> newItemList){
		String sql  = "INSERT INTO [OutletType] ([Code], [ChineseName], [EnglishName], [EffectiveDate], [ItemId] , [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingOutletTypeList outletType : newOutletTypeList){
			query.setParameter("code"+i, outletType.getCode());
			query.setParameter("chineseName"+i, outletType.getChineseName());
			query.setParameter("englishName"+i, outletType.getEnglishName());
			query.setParameter("itemId"+i, newItemList.get(outletType.getItemCode()).getItemId());
			i++;
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) outletTypeId, code from [outletType] order by outletTypeId desc) as a order by outletTypeId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newOutletTypeList.size());
		query.addScalar("outletTypeId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingOutletTypeList.class));
		return query.list();
	}
	
	public List<OutletType> getByIds(Integer[] ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("outletTypeId", ids));
		return criteria.list();
	}
	
	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids){
		String hql = "select ot.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by ot.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getShortCodeByAssignmentId(int assignmentId){
		String hql = "Select substring(ot.code, len(ot.code)-2, 3)"
				+ " from Assignment as a"
				+ " left join a.quotationRecords as qr"
				+ " left join qr.quotation as q"
				+ " left join q.unit as u"
				+ " left join u.subItem as si"
				+ " left join si.outletType as ot";
		
		hql += " where a.assignmentId = :assignmentId";
		
		hql += " group by substring(ot.code, len(ot.code)-2, 3)";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("assignmentId", assignmentId);
		
		return query.list();
	}
}
