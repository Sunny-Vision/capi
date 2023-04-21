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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Group;
import capi.model.JsTreeResponseModel;
import capi.model.dataImportExport.ImportRebasingGroupList;
import capi.model.dataImportExport.ImportRebasingSectionList;
import capi.model.masterMaintenance.UnitCommonModel;

@Repository("GroupDao")
public class GroupDao  extends GenericDao<Group>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " case when count (distinct b.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where a.section.id = :id "
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
	
	public Group getByCode(String code, String basePeriod) {
		String hql = "select distinct a"
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where a.code = :code  and u.cpiBasePeriod = :basePeriod " 
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) ";
//				+ " group by a.id, a.code, a.chineseName, a.englishName ";
		
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
		
//		query.setResultTransformer(Transformers.aliasToBean(Group.class));
		return (Group)query.uniqueResult();
	}

	public Group getByCodeWithoutDate(String code, String basePeriod) {
		String hql = "select distinct a"
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where a.code = :code  and u.cpiBasePeriod = :basePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("basePeriod", basePeriod);
		
		return (Group)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Group> search(String search, int firstRecord, int displayLength, String cpiBasePeriod, String sectionCode) {
		String hql = "select a.id as groupId, a.code as code, a.chineseName as chineseName, a.englishName as englishName "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " inner join a.section as sc"
				+ " where 1 = 1 "
				+ " and (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql	+= " and u.cpiBasePeriod = :cpiBasePeriod ";
		}		
		if (!StringUtils.isEmpty(search)){
			hql	+= " and concat(a.code, ' - ', a.chineseName) like :search ";
		}
		if(!StringUtils.isEmpty(sectionCode)){
			hql += " and sc.code = :sectionCode ";
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
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (!StringUtils.isEmpty(sectionCode)){
			query.setParameter("sectionCode", sectionCode);
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(Group.class));
		return query.list();
	}

	public long countSearch(String search, String cpiBasePeriod, String sectionCode) {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		
		Criteria criteria = this.createCriteria("g")
				.createAlias("g.subGroups", "sg")
				.createAlias("sg.items", "i")
				.createAlias("i.outletTypes", "ot")
				.createCriteria("ot.subItems", "si")
				.createAlias("si.units", "u")
				.createAlias("g.section", "sc");
		
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
		if(!StringUtils.isEmpty(sectionCode)){
			criteria.add(Restrictions.eq("sc.code", sectionCode));
		}
		
		criteria.setProjection(SQLProjectionExt.groupCount(
					Projections.groupProperty("g.id"),
					Projections.groupProperty("g.code"),
					Projections.groupProperty("g.chineseName"),
					Projections.groupProperty("g.englishName")
				));
		
//		String hql = "select count(distinct a.id) "
//				+ " from Unit as u "
//				+ " inner join u.subItem as si "
//				+ " inner join si.outletType as ot "
//				+ " inner join ot.item as i "
//				+ " inner join i.subGroup as b "
//				+ " inner join b.group as a "
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

	public String getGroupCodeBySubGroupCode(String code, String cpiBasePeriod) {
		String hql = "select distinct a.code as code "
				+ " from SubGroup as b "
				+ " inner join b.group as a "
				+ " inner join b.items as item "
				+ " inner join item.outletTypes as outletType "
				+ " inner join outletType.subItems as subItem "
				+ " inner join subItem.units as unit "
				+ " where b.code = :code "
				+ " and unit.cpiBasePeriod = :cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("code", code);
		query.setParameter("cpiBasePeriod", cpiBasePeriod);
		
		return (String)query.uniqueResult();
	}
	
	public List<Group> getByIds(Integer[] ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("groupId", ids));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select g.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as g "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " group by g.id ";
		
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
		String hql = "select b.id as id "
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
	
	

	public UnitCommonModel getCommonModelById(Integer groupId){
		String hql = "select a.id as id, a.code as code, a.chineseName as chineseName, a.englishName as englishName, u.cpiBasePeriod as  cpiBasePeriod "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as a "
				+ " where a.groupId = :groupId  "
				+ " group by a.id, a.code, a.chineseName, a.englishName, u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("groupId", groupId);
		query.setResultTransformer(Transformers.aliasToBean(UnitCommonModel.class));
		return (UnitCommonModel)query.uniqueResult();
	}
	
	public List<ImportRebasingGroupList> insertGroupByRebasing(String values, Date effectiveDate, List<ImportRebasingGroupList> newGroupList,  Map<String, ImportRebasingSectionList> newSectionList){
		String sql = "INSERT INTO [Group] ([Code], [ChineseName], [EnglishName], [EffectiveDate], [SectionId] , [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingGroupList group : newGroupList){
			query.setParameter("code"+i, group.getCode());
			query.setParameter("chineseName"+i, group.getChineseName());
			query.setParameter("englishName"+i, group.getEnglishName());
			query.setParameter("sectionId"+i, newSectionList.get(group.getSectionCode()).getSectionId());
			i++;
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) groupId, code from [Group] order by groupId desc) as a order by groupId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newGroupList.size());
		query.addScalar("groupId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingGroupList.class));
		return query.list();
	}
	
	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select g.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as g "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by g.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
}
