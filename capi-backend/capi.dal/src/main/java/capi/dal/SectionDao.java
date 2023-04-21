package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.Section;
import capi.model.JsTreeResponseModel;
import capi.model.dataImportExport.ImportRebasingSectionList;
import capi.model.masterMaintenance.UnitCommonModel;

@Repository("SectionDao")
public class SectionDao  extends GenericDao<Section>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAll() {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " case when count (distinct b.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " group by a.id, a.code, a.chineseName, a.englishName "
				+ " order by a.code, a.chineseName, a.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setResultTransformer(Transformers.aliasToBean(JsTreeResponseModel.class));
		return query.list();
	}
	
	public Section getByCode(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.code = :code and u.cpiBasePeriod = :basePeriod ";
//				+ " group by a.id, a.code, a.chineseName, a.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		query.setParameter("basePeriod", basePeriod);
		query.setParameter("code", code);
		
//		query.setResultTransformer(Transformers.aliasToBean(Section.class));
		return (Section)query.uniqueResult();
	}
	
	public Section getByCodeWithoutDate(String code, String basePeriod) {
		String hql = "select distinct a "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where a.code = :code and u.cpiBasePeriod = :basePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("basePeriod", basePeriod);
		query.setParameter("code", code);
		
		return (Section)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Section> search(String search, int firstRecord, int displayLength, String cpiBasePeriod) {
		String hql = "select a.id as sectionId, a.code as code, a.chineseName as chineseName, a.englishName as englishName "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql += " and u.cpiBasePeriod = :cpiBasePeriod ";
		}
		
		hql += " group by a.id, a.code, a.chineseName, a.englishName "
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
		query.setParameter("search", String.format("%%%s%%", search == null ? "" : search));
		
		query.setResultTransformer(Transformers.aliasToBean(Section.class));
		return query.list();
	}

	public long countSearch(String search, String cpiBasePeriod) {
		String hql = "select count(distinct a.id) "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and concat(a.code, ' - ', a.chineseName) like :search ";
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			hql+= " and u.cpiBasePeriod = :cpiBasePeriod";
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
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public String getSectionCodeByGroupCode(String code, String cpiBasePeriod) {
		String hql = "select distinct a.code as code "
				+ " from Group as b "
				+ " inner join b.section as a "
				+ " inner join b.subGroups as subGroup "
				+ " inner join subGroup.items as item "
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

	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(String id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label, "
				+ " case when count (distinct b.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where u.cpiBasePeriod = :id "
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

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select s.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as g "
				+ " inner join g.section as s "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " group by s.id ";
		
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
	
	public UnitCommonModel getCommonModelById(Integer sectionId){
		String hql = "select a.id as id, a.code as code, a.chineseName as chineseName, a.englishName as englishName, u.cpiBasePeriod as  cpiBasePeriod "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where a.sectionId = :sectionId  "
				+ " group by a.id, a.code, a.chineseName, a.englishName, u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("sectionId", sectionId);
		query.setResultTransformer(Transformers.aliasToBean(UnitCommonModel.class));
		return (UnitCommonModel)query.uniqueResult();
	}
	
	
	public List<ImportRebasingSectionList> insertSectionByRebasing(String values, Date effectiveDate, List<ImportRebasingSectionList> newSectionList){
		String sql = "INSERT INTO [Section] ([Code], [ChineseName], [EnglishName], [EffectiveDate] , [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingSectionList section : newSectionList){
			query.setParameter("code"+i, section.getCode());
			query.setParameter("chineseName"+i, section.getChineseName());
			query.setParameter("englishName"+i, section.getEnglishName());
			i++;
		}
		
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) sectionId, code from Section order by sectionId desc) as a order by sectionId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newSectionList.size());
		query.addScalar("sectionId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingSectionList.class));
		return query.list();
	}
	
	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select s.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as g "
				+ " inner join g.section as s "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by s.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
}
