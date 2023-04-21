package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SubGroup;
import capi.model.JsTreeResponseModel;
import capi.model.dataImportExport.ImportRebasingGroupList;
import capi.model.dataImportExport.ImportRebasingSubGroupList;
import capi.model.masterMaintenance.UnitCommonModel;

@Repository("SubGroupDao")
public class SubGroupDao  extends GenericDao<SubGroup>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " case when count (distinct b.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as b "
				+ " inner join b.subGroup as a "
				+ " where a.group.id = :id "
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
	
	public SubGroup getByCode(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as b "
				+ " inner join b.subGroup as a "
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
		
//		query.setResultTransformer(Transformers.aliasToBean(SubGroup.class));
		return (SubGroup)query.uniqueResult();
	}
	
	public SubGroup getByCodeWithoutDate(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as b "
				+ " inner join b.subGroup as a "
				+ " where a.code = :code and u.cpiBasePeriod = :basePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("basePeriod", basePeriod);
		
		return (SubGroup)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubGroup> search(String search, int firstRecord, int displayLength, String cpiBasePeriod, String groupCode) {
		String hql = "select a.id as subGroupId, a.code as code, a.chineseName as chineseName, a.englishName as englishName "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as b "
				+ " inner join b.subGroup as a "
				+ " inner join a.group as g"
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql	+= " and u.cpiBasePeriod = :cpiBasePeriod ";
		}
		if(!StringUtils.isEmpty(groupCode)){
			hql += " and g.code = :groupCode";
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
		if(!StringUtils.isEmpty(groupCode)){
			query.setParameter("groupCode", groupCode);
		}
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(SubGroup.class));
		return query.list();
	}

	public long countSearch(String search, String cpiBasePeriod, String groupCode) {
		String hql = "select count(distinct a.id) "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as b "
				+ " inner join b.subGroup as a "
				+ " inner join a.group as g"
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql += " and u.cpiBasePeriod = :cpiBasePeriod ";
		}
		if (!StringUtils.isEmpty(groupCode)){
			hql += " and g.code = :groupCode";
		}
				//+ " group by a.id, a.code, a.chineseName, a.englishName "
				//+ " order by a.code, a.chineseName, a.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			query.setParameter("cpiBasePeriod", cpiBasePeriod);
		}
		if (!StringUtils.isEmpty(groupCode)){
			query.setParameter("groupCode", groupCode);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	public String getSubGroupCodeByItemCode(String code, String cpiBasePeriod) {
		String hql = "select distinct a.code as code "
				+ " from Item as b "
				+ " inner join b.subGroup as a "
				+ " inner join b.outletTypes as outletType "
				+ " inner join outletType.subItems as subItem "
				+ " inner join subItem.units as unit "
				+ " where b.code = :code "
				+ " and unit.cpiBasePeriod = :cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("cpiBasePeriod", cpiBasePeriod);
		
		return (String)query.uniqueResult();
	}
	
	public List<SubGroup> getByIds(Integer[] ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("subGroupId", ids));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select sg.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " group by sg.id ";
		
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
		String hql = "select sg.id as id "
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
				+ " group by sg.id ";
		
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
		String hql = "select b.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by b.id ";
		
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
	

	public UnitCommonModel getCommonModelById(Integer id){
		String hql = "select a.id as id, a.code as code, a.chineseName as chineseName, a.englishName as englishName, u.cpiBasePeriod as  cpiBasePeriod "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as a "
				+ " where a.id = :id  "
				+ " group by a.id, a.code, a.chineseName, a.englishName, u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("id", id);
		query.setResultTransformer(Transformers.aliasToBean(UnitCommonModel.class));
		return (UnitCommonModel)query.uniqueResult();
	}
	
	public List<ImportRebasingSubGroupList> insertSubGroupByRebasing(String values, Date effectiveDate, List<ImportRebasingSubGroupList> newSubGroupList, Map<String, ImportRebasingGroupList> newGroupList){
		String sql = "INSERT INTO [SubGroup] ([Code], [ChineseName], [EnglishName], [EffectiveDate], [GroupId] , [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingSubGroupList subGroup : newSubGroupList){
			query.setParameter("code"+i, subGroup.getCode());
			query.setParameter("chineseName"+i, subGroup.getChineseName());
			query.setParameter("englishName"+i, subGroup.getEnglishName());
			query.setParameter("groupId"+i, newGroupList.get(subGroup.getGroupCode()).getGroupId());
			i++;
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) subGroupId, code from [SubGroup] order by subGroupId desc) as a order by subGroupId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newSubGroupList.size());
		query.addScalar("subGroupId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingSubGroupList.class));
		return query.list();
	}
	
	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select sg.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by sg.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
}
