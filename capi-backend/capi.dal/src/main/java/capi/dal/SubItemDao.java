package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SubItem;
import capi.model.JsTreeResponseModel;
import capi.model.api.dataSync.SubItemSyncData;
import capi.model.dataImportExport.ImportRebasingOutletTypeList;
import capi.model.dataImportExport.ImportRebasingSubItemList;
import capi.model.masterMaintenance.UnitCommonModel;
import capi.model.masterMaintenance.VarietySimpleModel;

@Repository("SubItemDao")
public class SubItemDao  extends GenericDao<SubItem>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " case when count (distinct u.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " where a.outletType.id = :id "
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
	
	public SubItem getByCode(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " where a.code = :code  and u.cpiBasePeriod = :basePeriod  "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) ";
			//	+ " group by a.id, a.code, a.chineseName, a.englishName "
			//	+ " order by a.code, a.chineseName, a.englishName ";
		
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
		
		//query.setResultTransformer(Transformers.aliasToBean(SubItem.class));
		return (SubItem)query.uniqueResult();
	}
	
	public SubItem getByCodeWithoutDate(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " where a.code = :code  and u.cpiBasePeriod = :basePeriod  ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("basePeriod", basePeriod);
		
		return (SubItem)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubItem> search(String search, int firstRecord, int displayLength, String cpiBasePeriod, String outletTypeCode) {
		String hql = "select a.id as subItemId, a.code as code, a.chineseName as chineseName, a.englishName as englishName "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " inner join a.outletType as ot"
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql	+= " and u.cpiBasePeriod = :cpiBasePeriod ";
		}
		if (!StringUtils.isEmpty(outletTypeCode)){
			hql += " and ot.code = :outletTypeCode ";
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
		if (!StringUtils.isEmpty(outletTypeCode)){
			query.setParameter("outletTypeCode", outletTypeCode);
		}
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		query.setResultTransformer(Transformers.aliasToBean(SubItem.class));
		return query.list();
	}

	public long countSearch(String search, String cpiBasePeriod, String outletTypeCode) {
		String hql = "select count(distinct a.id) "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " inner join a.outletType as ot "
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql += " and u.cpiBasePeriod= :cpiBasePeriod ";
		}
		
		if (!StringUtils.isEmpty(outletTypeCode)){
			hql += " and ot.code = :outletTypeCode ";
		}
//				+ " group by a.id, a.code, a.chineseName, a.englishName "
//				+ " order by a.code, a.chineseName, a.englishName ";
		
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
		if (!StringUtils.isEmpty(outletTypeCode)){
			query.setParameter("outletTypeCode", outletTypeCode);
		}
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select si.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " group by si.id ";
		
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
		String hql = "select si.id as id "
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
				+ " group by si.id ";
		
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
		String hql = "select si.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by si.id ";
		
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
		String hql = "select si.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by si.id ";
		
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
		String hql = "select si.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by si.id ";
		
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
	public List<Integer> getIdsByOutletTypeId(List<Integer> ids) {
		String hql = "select si.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by si.id ";
		
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
	
	public List<SubItemSyncData> getUpdateSubItem(Date lastSyncTime){
		Criteria criteria = this.createCriteria("s")
				.createAlias("s.outletType", "o");
		criteria.add(Restrictions.ge("s.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("s.subItemId"), "subItemId");
		projList.add(Projections.property("s.code"), "code");
		projList.add(Projections.property("s.chineseName"), "chineseName");
		projList.add(Projections.property("s.englishName"), "englishName");
		projList.add(Projections.property("o.outletTypeId"), "outletTypeId");
		projList.add(Projections.property("s.obsoleteDate"), "obsoleteDate");
		projList.add(Projections.property("s.effectiveDate"), "effectiveDate");
		projList.add(Projections.property("s.compilationMethod"), "compilationMethod");
		projList.add(Projections.property("s.createdDate"), "createdDate");
		projList.add(Projections.property("s.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(SubItemSyncData.class));
		return criteria.list();
	}
	

	public List<VarietySimpleModel> getSubItemDetailByIds(Integer[] ids){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(Projections.property("subItemId"), "id")
			.add(Projections.property("chineseName"),"chineseName")
			.add(Projections.property("englishName"),"englishName")
			.add(Projections.property("code"),"code");
		criteria.setProjection(list);
		
		if (ids!=null && ids.length > 0){
			criteria.add(Restrictions.in("subItemId", ids));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(VarietySimpleModel.class));
		
		return criteria.list();
	}
	
	
	public UnitCommonModel getCommonModelById(Integer id){
		String hql = "select a.id as id, a.code as code, a.chineseName as chineseName, a.englishName as englishName, u.cpiBasePeriod as  cpiBasePeriod "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " where a.id = :id  "
				+ " group by a.id, a.code, a.chineseName, a.englishName, u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("id", id);
		query.setResultTransformer(Transformers.aliasToBean(UnitCommonModel.class));
		return (UnitCommonModel)query.uniqueResult();
	}
	
	public List<ImportRebasingSubItemList> insertSubItemByRebasing(String values, Date effectiveDate, List<ImportRebasingSubItemList> newSubItemList, Map<String, ImportRebasingOutletTypeList> newOutletTypeList){
		String sql = "INSERT INTO [SubItem] ([Code], [ChineseName], [EnglishName], [CompilationMethod], [EffectiveDate], [OutletTypeId] , [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingSubItemList subItem : newSubItemList){
			query.setParameter("code"+i, subItem.getCode());
			query.setParameter("chineseName"+i, subItem.getChineseName());
			query.setParameter("englishName"+i, subItem.getEnglishName());
			query.setParameter("compilationMethod"+i, subItem.getCompilationMethod());
			query.setParameter("outletTypeId"+i, newOutletTypeList.get(subItem.getOutletTypeCode()).getOutletTypeId());
			i++;
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) subItemId, code from [SubItem] order by subItemId desc) as a order by subItemId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newSubItemList.size());
		query.addScalar("subItemId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingSubItemList.class));
		return query.list();	
	}
	
	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids){
		String hql = "select si.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by si.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
}
