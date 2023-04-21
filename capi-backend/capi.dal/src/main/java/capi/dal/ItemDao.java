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
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Item;
import capi.model.JsTreeResponseModel;
import capi.model.dataImportExport.ImportRebasingItemList;
import capi.model.dataImportExport.ImportRebasingSubGroupList;
import capi.model.masterMaintenance.UnitCommonModel;
import capi.model.masterMaintenance.VarietySimpleModel;

@Repository("ItemDao")
public class ItemDao  extends GenericDao<Item>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " case when count (distinct b.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as b "
				+ " inner join b.item as a "
				+ " where a.subGroup.id = :id "
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
	
	public Item getByCode(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as b "
				+ " inner join b.item as a "
				+ " where a.code = :code and u.cpiBasePeriod = :basePeriod "
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
		
//		query.setResultTransformer(Transformers.aliasToBean(Item.class));
		return (Item)query.uniqueResult();
	}
	
	public Item getByCodeWithoutDate(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as b "
				+ " inner join b.item as a "
				+ " where a.code = :code and u.cpiBasePeriod = :basePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("basePeriod", basePeriod);
		
		return (Item)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> search(String search, int firstRecord, int displayLength, String cpiBasePeriod, String subGroupCode) {
		String hql = "select a.id as itemId, a.code as code, a.chineseName as chineseName, a.englishName as englishName "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as b "
				+ " inner join b.item as a "
				+ " inner join a.subGroup as sg"
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql	+= " and u.cpiBasePeriod = :cpiBasePeriod ";
		}
		if (!StringUtils.isEmpty(subGroupCode)){
			hql += " and sg.code = :subGroupCode ";
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
		if (!StringUtils.isEmpty(subGroupCode)){
			query.setParameter("subGroupCode", subGroupCode);
		}
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		query.setResultTransformer(Transformers.aliasToBean(Item.class));
		return query.list();
	}

	public long countSearch(String search, String cpiBasePeriod, String subGroupCode) {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		
		Criteria criteria = this.createCriteria("i")
				.createAlias("i.outletTypes", "ot")
				.createCriteria("ot.subItems", "si")
				.createAlias("si.units", "u")
				.createAlias("i.subGroup", "sg");
		
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
		if(!StringUtils.isEmpty(subGroupCode)){
			criteria.add(Restrictions.eq("sg.code", subGroupCode));
		}
		
		criteria.setProjection(SQLProjectionExt.groupCount(
					Projections.groupProperty("i.id"),
					Projections.groupProperty("i.code"),
					Projections.groupProperty("i.chineseName"),
					Projections.groupProperty("i.englishName")
				));
		
		
//		String hql = "select count(distinct a.id) "
//				+ " from Unit as u "
//				+ " inner join u.subItem as si "
//				+ " inner join si.outletType as b "
//				+ " inner join b.item as a "
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

	public String getItemCodeByOutletTypeCode(String code, String cpiBasePeriod) {
		String hql = "select distinct a.code as code "
				+ " from OutletType as b "
				+ " inner join b.item as a "
				+ " inner join b.subItems as subItem "
				+ " inner join subItem.units as unit "
				+ " where b.code = :code "
				+ " and unit.cpiBasePeriod = :cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("cpiBasePeriod", cpiBasePeriod);
		
		return (String)query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select i.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " and u.status = 'Active' "
				+ " group by i.id ";
		
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
		String hql = "select i.id as id "
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
				+ " group by i.id ";
		
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
		String hql = "select i.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by i.id ";
		
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
		String hql = "select i.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by i.id ";
		
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
	

	public List<VarietySimpleModel> getItemDetailByIds(Integer[] ids){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(Projections.property("itemId"), "id")
			.add(Projections.property("chineseName"),"chineseName")
			.add(Projections.property("englishName"),"englishName")
			.add(Projections.property("code"),"code");
		
		criteria.setProjection(list);
		if (ids !=null && ids.length >0){
			criteria.add(Restrictions.in("itemId", ids));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(VarietySimpleModel.class));
		
		return criteria.list();
	}
	
	public UnitCommonModel getCommonModelById(Integer id){
		String hql = "select a.id as id, a.code as code, a.chineseName as chineseName, a.englishName as englishName, u.cpiBasePeriod as  cpiBasePeriod "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as a "
				+ " where a.id = :id  "
				+ " group by a.id, a.code, a.chineseName, a.englishName, u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("id", id);
		query.setResultTransformer(Transformers.aliasToBean(UnitCommonModel.class));
		return (UnitCommonModel)query.uniqueResult();
	}
	
	public List<ImportRebasingItemList> insertItemByRebasing(String values, Date effectiveDate, List<ImportRebasingItemList> newItemList, Map<String, ImportRebasingSubGroupList> newSubGroupList){
		String sql = "INSERT INTO [Item] ([Code], [ChineseName], [EnglishName], [EffectiveDate], [SubGroupId] , [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingItemList item : newItemList){
			query.setParameter("code"+i, item.getCode());
			query.setParameter("chineseName"+i, item.getChineseName());
			query.setParameter("englishName"+i, item.getEnglishName());
			query.setParameter("subGroupId"+i, newSubGroupList.get(item.getSubGroupCode()).getSubGroupId());
			i++;
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) itemId, code from [Item] order by itemId desc) as a order by itemId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newItemList.size());
		query.addScalar("itemId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingItemList.class));
		return query.list();
	}
	
	public List<Item> getByIds(Integer[] ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("itemId", ids));
		return criteria.list();
	}
	
	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids){
		String hql = "select i.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by i.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
}
