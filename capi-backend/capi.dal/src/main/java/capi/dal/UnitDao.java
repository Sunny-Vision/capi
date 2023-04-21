package capi.dal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Unit;
import capi.model.JsTreeResponseModel;
import capi.model.api.dataSync.UOMCategoryUnitSyncData;
import capi.model.api.dataSync.UnitSyncData;
import capi.model.commonLookup.UnitLookupTableList;
import capi.model.dataImportExport.ImportRebasingSubItemList;
import capi.model.dataImportExport.ImportRebasingUnitList;
import capi.model.masterMaintenance.UnitTableList;
import capi.model.report.ProductCycleReportByVariety;
import capi.model.report.SummaryOfICPQuotationsByICPTypeReport;
import capi.model.timeLogManagement.TimeLogTableList;

@Repository("UnitDao")
public class UnitDao  extends GenericDao<Unit>{
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select concat('', a.id) as id, concat(a.code, ' - ', a.chineseName, ' (', a.englishName, ')') as label "
				+ " from Unit as a "
				+ " where a.subItem.id = :id "
				+ " and (a.effectiveDate = null or :today >= a.effectiveDate) "
				+ " and (a.obsoleteDate = null or :today < a.obsoleteDate) ";
		
		if (purposeIds != null && purposeIds.size() > 0) {
			hql += " and a.purpose.id in :purposeIds ";
		}
		
		if (onlyActive) {
			hql += " and a.status <> 'Inactive'";
		}
		
		hql += " order by a.code, a.chineseName, a.englishName ";
		
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
	public List<UnitLookupTableList> getTableListByIds(Integer[] ids) {
		String hql = "select a.id as id, "
				+ " a.code as code, "
				+ " a.chineseName as chineseName, "
				+ " a.englishName as englishName, "
				+ " p.survey as survey "
				+ " from Unit as a "
				+ " left join a.purpose as p "
				+ " where 1 = 1 ";
		if (ids != null && ids.length > 0) {
        	hql += " and a.id in :ids";
        }
		
		Query query = this.getSession().createQuery(hql);
		
		if (ids != null && ids.length > 0) {
			query.setParameterList("ids", ids);
		}

		query.setResultTransformer(Transformers.aliasToBean(UnitLookupTableList.class));

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<UnitTableList> getTableList(String search,
			int firstRecord, int displayLenght, Order order,
			List<Integer> surveyTypeId, List<Integer> sectionId, List<Integer> groupId, List<Integer> subGroupId, List<Integer> itemId, List<String> outletTypeId, 
			List<Integer> subItemId, String status,
			List<Integer> productCategoryId, List<String> cpiBasePeriod) {

		String hql = "select a.id as id, a.code as unitCode, a.chineseName as unitChineseName, a.englishName as unitEnglishName, "
				+ " concat(p.code, ' - ', p.name) as surveyType, "
				+ " s.code as sectionCode, s.chineseName as sectionChineseName, s.englishName as sectionEnglishName, "
				+ " g.code as groupCode, g.chineseName as groupChineseName, g.englishName as groupEnglishName, "
				+ " sg.code as subGroupCode, sg.chineseName as subGroupChineseName, sg.englishName as subGroupEnglishName, "
				+ " i.code as itemCode, i.chineseName as itemChineseName, i.englishName as itemEnglishName, "
				+ " ot.code as outletTypeCode, ot.chineseName as outletTypeChineseName, ot.englishName as outletTypeEnglishName, "
				+ " si.code as subItemCode, si.chineseName as subItemChineseName, si.englishName as subItemEnglishName, "
				+ " concat(pc.code, ' - ', pc.chineseName) as productCategory, "
				+ " a.cpiBasePeriod as cpiBasePeriod, a.status as status "
				+ " from Unit as a "
				+ " left outer join a.purpose as p "
				+ " inner join a.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as g "
				+ " inner join g.section as s "
				+ " left outer join a.productCategory as pc "
				+ " where 1 = 1 "
				+ " and (a.effectiveDate = null or :today >= a.effectiveDate) "
				+ " and (a.obsoleteDate = null or :today < a.obsoleteDate) "
				+ " and (a.code like :search or a.chineseName like :search or a.englishName like :search "
					+ " or concat(p.code, ' - ', p.name) like :search "
					+ " or s.code like :search or s.chineseName like :search or s.englishName like :search "
					+ " or g.code like :search or g.chineseName like :search or g.englishName like :search "
					+ " or sg.code like :search or sg.chineseName like :search or sg.englishName like :search "
					+ " or i.code like :search or i.chineseName like :search or i.englishName like :search "
					+ " or ot.code like :search or ot.chineseName like :search or ot.englishName like :search "
					+ " or si.code like :search or si.chineseName like :search or si.englishName like :search "
					+ " ) ";
		
		if (surveyTypeId != null && surveyTypeId.size() > 0) {
			hql += " and p.id in (:surveyTypeId) ";
		}
		
		if (sectionId != null && sectionId.size() > 0) {
			hql += " and s.id in (:sectionId) ";
		}
		
		if (groupId != null && groupId.size() > 0) {
			hql += " and g.id in (:groupId) ";
		}
		
		if (subGroupId != null && subGroupId.size() > 0) {
			hql += " and sg.id in (:subGroupId) ";
		}
		
		if (itemId != null && itemId.size() > 0) {
			hql += " and i.id in (:itemId) ";
		}
		
		if (outletTypeId != null && outletTypeId.size() > 0) {
			hql += " and substring(ot.code, length(ot.code) - 2, length(ot.code)) in (:outletTypeId) ";
		}
		
		if (subItemId != null && subItemId.size() > 0) {
			hql += " and si.id in (:subItemId) ";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			hql += " and a.status = :status ";
		}
		
		if (productCategoryId != null && productCategoryId.size() > 0) {
			hql += " and pc.id in (:productCategoryId) ";
		}
		
		if (cpiBasePeriod != null && cpiBasePeriod.size() > 0) {
			hql += " and a.cpiBasePeriod in (:cpiBasePeriod) ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameter("search", String.format("%%%s%%", search));
		
		if (surveyTypeId != null && surveyTypeId.size() > 0) {
			query.setParameterList("surveyTypeId", surveyTypeId);
		}
		
		if (sectionId != null && sectionId.size() > 0) {
			query.setParameterList("sectionId", sectionId);
		}
		
		if (groupId != null && groupId.size() > 0) {
			query.setParameterList("groupId", groupId);
		}
		
		if (subGroupId != null && subGroupId.size() > 0) {
			query.setParameterList("subGroupId", subGroupId);
		}
		
		if (itemId != null && itemId.size() > 0) {
			query.setParameterList("itemId", itemId);
		}
		
		if (outletTypeId != null && outletTypeId.size() > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		
		if (subItemId != null && subItemId.size() > 0) {
			query.setParameterList("subItemId", subItemId);
		}
		
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		
		if (productCategoryId != null && productCategoryId.size() > 0) {
			query.setParameterList("productCategoryId", productCategoryId);
		}
		
		if (cpiBasePeriod != null && cpiBasePeriod.size() > 0) {
			query.setParameterList("cpiBasePeriod", cpiBasePeriod);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(UnitTableList.class));

		return query.list();
	}

	public long countTableList(String search,
			List<Integer> surveyTypeId, List<Integer> sectionId, List<Integer> groupId, List<Integer> subGroupId, List<Integer> itemId, 
			List<String> outletTypeId, List<Integer> subItemId, String status,
			List<Integer> productCategoryId, List<String> cpiBasePeriod) {

		String hql = "select count(distinct a.id) "
				+ " from Unit as a "
				+ " left outer join a.purpose as p "
				+ " inner join a.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as g "
				+ " inner join g.section as s "
				+ " left outer join a.productCategory as pc "
				+ " where 1 = 1 "
				+ " and (a.effectiveDate = null or :today >= a.effectiveDate) "
				+ " and (a.obsoleteDate = null or :today < a.obsoleteDate) "
				+ " and (a.code like :search or a.chineseName like :search or a.englishName like :search "
					+ " or concat(p.code, ' - ', p.name) like :search "
					+ " or s.code like :search or s.chineseName like :search or s.englishName like :search "
					+ " or g.code like :search or g.chineseName like :search or g.englishName like :search "
					+ " or sg.code like :search or sg.chineseName like :search or sg.englishName like :search "
					+ " or i.code like :search or i.chineseName like :search or i.englishName like :search "
					+ " or ot.code like :search or ot.chineseName like :search or ot.englishName like :search "
					+ " or si.code like :search or si.chineseName like :search or si.englishName like :search "
					+ " ) ";
		
		if (surveyTypeId != null && surveyTypeId.size() > 0) {
			hql += " and p.id in (:surveyTypeId) ";
		}
		
		if (sectionId != null && sectionId.size() > 0) {
			hql += " and s.id in (:sectionId) ";
		}
		
		if (groupId != null && groupId.size() > 0) {
			hql += " and g.id in (:groupId) ";
		}
		
		if (subGroupId != null && subGroupId.size() > 0) {
			hql += " and sg.id in (:subGroupId) ";
		}
		
		if (itemId != null && itemId.size() > 0) {
			hql += " and i.id in (:itemId) ";
		}
		
		if (outletTypeId != null && outletTypeId.size() > 0) {
			hql += " and substring(ot.code, length(ot.code) - 2, length(ot.code)) in (:outletTypeId) ";
		}
		
		if (subItemId != null && subItemId.size() > 0) {
			hql += " and si.id in (:subItemId) ";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			hql += " and a.status = :status ";
		}
		
		if (productCategoryId != null && productCategoryId.size() > 0) {
			hql += " and pc.id in (:productCategoryId) ";
		}
		
		if (cpiBasePeriod != null && cpiBasePeriod.size() > 0) {
			hql += " and a.cpiBasePeriod in (:cpiBasePeriod) ";
		}
		
		Query query = getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameter("search", String.format("%%%s%%", search));
		
		if (surveyTypeId != null && surveyTypeId.size() > 0) {
			query.setParameterList("surveyTypeId", surveyTypeId);
		}
		
		if (sectionId != null && sectionId.size() > 0) {
			query.setParameterList("sectionId", sectionId);
		}
		
		if (groupId != null && groupId.size() > 0) {
			query.setParameterList("groupId", groupId);
		}
		
		if (subGroupId != null && subGroupId.size() > 0) {
			query.setParameterList("subGroupId", subGroupId);
		}
		
		if (itemId != null && itemId.size() > 0) {
			query.setParameterList("itemId", itemId);
		}
		
		if (outletTypeId != null && outletTypeId.size() > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		
		if (subItemId != null && subItemId.size() > 0) {
			query.setParameterList("subItemId", subItemId);
		}
		
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		
		if (productCategoryId != null && productCategoryId.size() > 0) {
			query.setParameterList("productCategoryId", productCategoryId);
		}
		
		if (cpiBasePeriod != null && cpiBasePeriod.size() > 0) {
			query.setParameterList("cpiBasePeriod", cpiBasePeriod);
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public Unit getUnitWithSubItem(Integer id) {
		Criteria c = this.createCriteria().add(Restrictions.eq("id", id));
		c.setFetchMode("subItem", FetchMode.JOIN);
		c.setFetchMode("subItem.outletType", FetchMode.JOIN);
		c.setFetchMode("subItem.outletType.item", FetchMode.JOIN);
		c.setFetchMode("subItem.outletType.item.subGroup", FetchMode.JOIN);
		c.setFetchMode("subItem.outletType.item.subGroup.group", FetchMode.JOIN);
		c.setFetchMode("subItem.outletType.item.subGroup.group.section", FetchMode.JOIN);
		c.setFetchMode("purpose", FetchMode.JOIN);
		c.setFetchMode("standardUOM", FetchMode.JOIN);
		c.setFetchMode("pricingFrequency", FetchMode.JOIN);
		c.setFetchMode("productCategory", FetchMode.JOIN);
		c.setFetchMode("uomCategories", FetchMode.JOIN);
		c.setFetchMode("subPriceType", FetchMode.JOIN);
		return (Unit)c.uniqueResult();
	}
	
	@Deprecated
	public boolean isCodeExistsAmongMRPS(String code, String cpiBase) {
		Criteria c = this.createCriteria()
				.add(Restrictions.eq("isMRPS", true))
				.add(Restrictions.eq("code", code));
		
		if (!StringUtils.isEmpty(cpiBase)){
			c.add(Restrictions.eq("cpiBasePeriod", cpiBase));
		}				
		return c.uniqueResult() != null;
	}
	@Deprecated
	public boolean isUnitCodeExistsAmongPurpose(String code, Integer purposeId, String cpiBase) {
		Criteria c = this.createCriteria()
				.add(Restrictions.eq("isMRPS", false))
				.add(Restrictions.eq("purpose.id", purposeId))
				.add(Restrictions.eq("code", code));
		if (!StringUtils.isEmpty(cpiBase)){
			c.add(Restrictions.eq("cpiBasePeriod", cpiBase));
		}	
		return c.uniqueResult() != null;
	}

	public boolean isUnitCodeExist(String code, String cpiBasePeriod){
		Criteria c = this.createCriteria()
				.add(Restrictions.eq("code", code));
		if (!StringUtils.isEmpty(cpiBasePeriod)){
			c.add(Restrictions.eq("cpiBasePeriod", cpiBasePeriod));
		}	
		return c.uniqueResult() != null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByCpiBasePeriod(List<String> ids) {
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod in :ids "
				+ " group by u.id ";
		
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
		String hql = "select u.id as id "
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
				+ " group by u.id ";
		
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
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as b "
				+ " inner join b.group as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by u.id ";
		
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
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by u.id ";
		
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
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by u.id ";
		
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
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by u.id ";
		
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
	public List<Integer> getIdsBySubItemId(List<Integer> ids) {
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and a.id in :ids "
				+ " group by u.id ";
		
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
	public List<Integer> getIdsBySubItemId(Integer ids) {
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " inner join u.subItem as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.id in :ids "
				+ " group by u.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		query.setParameter("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Unit> search(String search, int firstRecord, int displayLength, List<Integer> purposeIds) {
		Criteria critera = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("code")).addOrder(Order.asc("chineseName"));

		Date today = new Date();
		critera.add(Restrictions.or(
				Restrictions.isNull("obsoleteDate"),
				Restrictions.gt("obsoleteDate", today)
			));
		critera.add(Restrictions.eq("status", "Active"));
		
		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+chineseName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		if (purposeIds != null && purposeIds.size() > 0) {
			critera.add(Restrictions.in("purpose.id", purposeIds));
		}
		
		return critera.list();
	}

	public long countSearch(String search, List<Integer> purposeIds) {
		Criteria critera = this.createCriteria();

		Date today = new Date();
		critera.add(Restrictions.or(
				Restrictions.isNull("obsoleteDate"),
				Restrictions.gt("obsoleteDate", today)
			));
		
		critera.add(Restrictions.eq("status", "Active"));
		
		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+chineseName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		if (purposeIds != null && purposeIds.size() > 0) {
			critera.add(Restrictions.in("purpose.id", purposeIds));
		}
		
		return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Unit> getByIds(Integer[] ids) {
		Criteria criteria = this.createCriteria("n").add(Restrictions.in("unitId", ids)).addOrder(Order.asc("code")).addOrder(Order.asc("chineseName"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Unit> icpProductCodeToUnitCode(ArrayList<String> ids) {
		Criteria criteria = this.createCriteria("u")
				.createAlias("u.quotations", "q", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.in("q.icpProductCode", ids));
		return criteria.list();
	}
	
	public List<UnitSyncData> getUpdateUnit(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("unitId"), "unitId");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("chineseName"), "chineseName");
		projList.add(Projections.property("englishName"), "englishName");
		projList.add(Projections.sqlProjection("subItemId as subItemId", new String[]{"subItemId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("obsoleteDate"), "obsoleteDate");
		projList.add(Projections.property("effectiveDate"), "effectiveDate");
		projList.add(Projections.property("displayName"), "displayName");
		projList.add(Projections.property("isMRPS"), "isMRPS");
		projList.add(Projections.sqlProjection("purposeId as purposeId", new String[]{"purposeId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("maxQuotation"), "maxQuotation");
		projList.add(Projections.property("minQuotation"), "minQuotation");
		projList.add(Projections.property("unitCategory"), "unitCategory");
		projList.add(Projections.sqlProjection("standardUOMId as standardUOMId", new String[]{"standardUOMId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("uomValue"), "uomValue");
		projList.add(Projections.sqlProjection("productCategoryId as productCategoryId", new String[]{"productCategoryId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.sqlProjection("subPriceTypeId as subPriceTypeId", new String[]{"subPriceTypeId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("spicingRequired"), "spicingRequired");
		projList.add(Projections.property("frRequired"), "frRequired");
		projList.add(Projections.property("seasonality"), "seasonality");
		projList.add(Projections.property("seasonStartMonth"), "seasonStartMonth");
		projList.add(Projections.property("seasonEndMonth"), "seasonEndMonth");
		projList.add(Projections.sqlProjection("pricingFrequencyId as pricingFrequencyId", new String[]{"pricingFrequencyId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("rtnPeriod"), "rtnPeriod");
		projList.add(Projections.property("backdateRequired"), "backdateRequired");
		projList.add(Projections.property("allowEditPMPrice"), "allowEditPMPrice");
		projList.add(Projections.property("ruaAllowed"), "ruaAllowed");
		projList.add(Projections.property("isFreshItem"), "isFreshItem");
		projList.add(Projections.property("allowProductChange"), "allowProductChange");
		projList.add(Projections.property("formDisplay"), "formDisplay");
		projList.add(Projections.property("productCycle"), "productCycle");
		projList.add(Projections.property("status"), "status");
		projList.add(Projections.property("cpiBasePeriod"), "cpiBasePeriod");
		projList.add(Projections.property("crossCheckGroup"), "crossCheckGroup");
		projList.add(Projections.property("cpiQoutationType"), "cpiQoutationType");
		projList.add(Projections.property("isTemporary"), "isTemporary");
		projList.add(Projections.property("isNPriceMandatory"), "isNPriceMandatory");
		projList.add(Projections.property("isSPriceMandatory"), "isSPriceMandatory");
		projList.add(Projections.property("dataTransmissionRule"), "dataTransmissionRule");
		projList.add(Projections.property("consolidatedSPRMean"), "consolidatedSPRMean");
		projList.add(Projections.property("consolidatedSPRSD"), "consolidatedSPRSD");
		projList.add(Projections.property("consolidatedNPRMean"), "consolidatedNPRMean");
		projList.add(Projections.property("consolidatedNPRSD"), "consolidatedNPRSD");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UnitSyncData.class));
		return criteria.list();
	}
	
	public List<UOMCategoryUnitSyncData> getUpdateUOMCategoryUnit(Date lastSyncTime){
		String sql = "Select uom.uomCategoryId as uomCategoryId, "
				+ "uom.unitId as unitId, "
				+ "uom.createdDate as createdDate, "
				+ "uom.modifiedDate as modifiedDate "
				+ "from UOMCategoryUnit uom where "
				+ "uom.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("uomCategoryId", StandardBasicTypes.INTEGER)
				.addScalar("unitId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(UOMCategoryUnitSyncData.class));
		
		return query.list();
	}
	
	
	public void CalculateConsolidatedUnitStatisitc(){
		Query query = this.getSession().createSQLQuery("exec [CalculateConsolidatedUnitStatisitc]");
		query.executeUpdate();
	}
	
	
	
	public ScrollableResults getUnitClassificationResult(String cpiBasePeriod){
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.purpose", "b", JoinType.LEFT_OUTER_JOIN)
				.createAlias("a.subItem", "c", JoinType.LEFT_OUTER_JOIN)
				.createAlias("c.outletType", "d", JoinType.LEFT_OUTER_JOIN)
				.createAlias("d.item", "e", JoinType.LEFT_OUTER_JOIN)
				.createAlias("e.subGroup", "f", JoinType.LEFT_OUTER_JOIN)
				.createAlias("f.group", "g", JoinType.LEFT_OUTER_JOIN)
				.createAlias("g.section", "h", JoinType.LEFT_OUTER_JOIN)
				.createAlias("a.productCategory", "p", JoinType.LEFT_OUTER_JOIN)
				.setFetchMode("a.purpose", FetchMode.JOIN)
				.setFetchMode("a.subItem", FetchMode.JOIN)
				.setFetchMode("c.outletType", FetchMode.JOIN)
				.setFetchMode("d.item", FetchMode.JOIN)
				.setFetchMode("e.subGroup", FetchMode.JOIN)
				.setFetchMode("f.group", FetchMode.JOIN)
				.setFetchMode("g.section", FetchMode.JOIN)
				.setFetchMode("a.productCategory", FetchMode.JOIN);
		criteria.add(Restrictions.eq("a.cpiBasePeriod", cpiBasePeriod));
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public ScrollableResults getAllUnitResult(String cpiBasePeriod){
		Criteria criteria = this.createCriteria("a")
				.setFetchMode("a.purpose", FetchMode.JOIN)
				.setFetchMode("a.standardUOM", FetchMode.JOIN)
				.setFetchMode("a.pricingFrequency", FetchMode.JOIN)
				.setFetchMode("a.productCategory", FetchMode.JOIN)
				.setFetchMode("a.subPriceType", FetchMode.JOIN)
				.setFetchMode("a.subItem", FetchMode.JOIN);
		
		criteria.add(Restrictions.eq("a.cpiBasePeriod", cpiBasePeriod));
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	
	public Unit getUnitByCode(String code, String cpiBasePeriod){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("code", code),
				Restrictions.eq("cpiBasePeriod", cpiBasePeriod)
				));
		return (Unit) criteria.uniqueResult();
	}
	
	public List<String> searchAllCPIBasePeriod(String search, int firstRecord, int displayRecord){
		return this.searchAllCPIBasePeriod(search, firstRecord, displayRecord, false);
	}
	
	 public List<String> searchAllCPIBasePeriod(String search, int firstRecord, int displayRecord, boolean withNonEffective){
		 Criteria criteria = this.createCriteria();
		 ProjectionList list = Projections.projectionList();
		 list.add(Projections.groupProperty("cpiBasePeriod"),"cpiBasePeriod");
		 criteria.setProjection(list);
		 
		 if (!withNonEffective){
			 criteria.add(Restrictions.or(
					 	Restrictions.isNull("effectiveDate"),
					 	Restrictions.sqlRestriction("cast(effectiveDate as date) <= cast(getDate() as date) ")				 
					 ));
		 }
		 criteria.add(Restrictions.or(
				 	Restrictions.isNull("obsoleteDate"),
				 	Restrictions.sqlRestriction("cast(obsoleteDate as date) > cast(getDate() as date) ")				 
				 ));
		 criteria.add(Restrictions.eq("status", "Active"));
		 
		 if (!StringUtils.isEmpty(search)){
			 criteria.add(Restrictions.like("cpiBasePeriod", "%"+search+"%"));
		 }
		 
		 criteria.setFirstResult(firstRecord).setMaxResults(displayRecord);
		 criteria.addOrder(Order.desc("cpiBasePeriod"));		 
		 
		 return criteria.list();
	 }
	
	 public long countCPIBasePeriod(String search){
		 Criteria criteria = this.createCriteria();
		 criteria.setProjection(SQLProjectionExt.groupCount(Projections.groupProperty("cpiBasePeriod")));
		 
		 criteria.add(Restrictions.or(
				 	Restrictions.isNull("effectiveDate"),
				 	Restrictions.sqlRestriction("cast(effectiveDate as date) <= cast(getDate() as date) ")				 
				 ));
		 criteria.add(Restrictions.or(
				 	Restrictions.isNull("obsoleteDate"),
				 	Restrictions.sqlRestriction("cast(obsoleteDate as date) > cast(getDate() as date) ")				 
				 ));
		 criteria.add(Restrictions.eq("status", "Active"));
		 
		 if (!StringUtils.isEmpty(search)){
			 criteria.add(Restrictions.like("cpiBasePeriod", "%"+search+"%"));
		 }
		 
		 return (long)criteria.uniqueResult();
	 }
	 
	@SuppressWarnings("unchecked")
	public List<JsTreeResponseModel> getAllCpiBasePeriods(List<Integer> purposeIds, boolean onlyActive) {
		String hql = "select u.cpiBasePeriod as id, u.cpiBasePeriod as label, "
				+ " case when count (distinct a.id) > 0 then true else false end as children "
				+ " from Unit as u "
				+ " inner join u.subItem as si "
				+ " inner join si.outletType as ot "
				+ " inner join ot.item as i "
				+ " inner join i.subGroup as sg "
				+ " inner join sg.group as b "
				+ " inner join b.section as a "
				+ " where (u.effectiveDate = null or :today >= u.effectiveDate) "
				+ " and (u.obsoleteDate = null or :today < u.obsoleteDate) "
				+ " and u.cpiBasePeriod is not null ";
		
		if (purposeIds != null && purposeIds.size() > 0) {
			hql += " and u.purpose.id in :purposeIds ";
		}
		
		if (onlyActive) {
			hql += " and u.status <> 'Inactive'";
		}
		
		hql += " group by u.cpiBasePeriod "
				+ " order by u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
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
	public List<String> searchValidDistinctUnitCategory(String search, int firstRecord, int displayLength) {
		String hql = "select distinct unit.unitCategory "
                + " from Unit as unit "
                + " where unit.status = 'Active' ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}
	
	public long countValidDistinctUnitCategory(String search){
		String hql = "select count(distinct unit.unitCategory) "
                + " from Unit as unit "
                + " where unit.status = 'Active' ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		 
		 return (long)query.uniqueResult();
	 }
	
	public List<ImportRebasingUnitList> insertUnitByRebasing(String values, Date effectiveDate, String cpiBasePeriod, List<ImportRebasingUnitList> newUnitList, Map<String, ImportRebasingSubItemList> newSubItemList){
		String sql = "INSERT INTO [Unit] ([Code], [ChineseName], [EnglishName], [DisplayName]"
				+ ", [IsMRPS], [PurposeId], [MaxQuotation], [MinQuotation], [UnitCategory]"
				+ ", [StandardUOMId], [UOMValue], [SubPriceTypeId], [SpicingRequired], [FRRequired]"
				+ ", [Seasonality], [SeasonStartMonth], [SeasonEndMonth], [PricingFrequencyId], [RTNPeriod]"
				+ ", [BackdateRequired], [AllowEditPMPrice], [RUAAllowed], [IsFreshItem], [AllowProductChange]"
				+ ", [FormDisplay], [ProductCycle], [CrossCheckGroup], [CPIQoutationType], [IsTemporary]"
				+ ", [IsNPriceMandatory], [IsSPriceMandatory], [DataTransmissionRule], [ConsolidatedSPRMean], [ConsolidatedSPRSD]"
				+ ", [ConsolidatedNPRMean], [ConsolidatedNPRSD], [Status], [SubItemId], [ProductCategoryId], [EffectiveDate], [CPIBasePeriod] "
				+ ", [IcpType], [ConvertAfterClosingDate], [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i=0;
		for(ImportRebasingUnitList unit : newUnitList){
			query.setParameter("code"+i, unit.getCode());
			query.setParameter("chineseName"+i, unit.getUnitChineseName());
			query.setParameter("englishName"+i, unit.getUnitEnglishName());
			//query.setParameter("obsoleteDate"+i, unit.getObsoleteDate());
			query.setParameter("displayName"+i, unit.getDisplayName());
			query.setParameter("isMRPS"+i, unit.getIsMRPS());
			query.setParameter("purposeId"+i, unit.getPurposeId());
			query.setParameter("maxQuotation"+i, unit.getMaxQuotation());
			query.setParameter("minQuotation"+i, unit.getMinQuotation());
			query.setParameter("unitCategory"+i, unit.getUnitCategory());
			query.setParameter("standardUOMId"+i, unit.getStandardUOMId());
			if(unit.getUomValue()!=null){
				query.setParameter("uomValue"+i, String.valueOf(unit.getUomValue()));
			} else {
				query.setParameter("uomValue"+i, unit.getUomValue());
			}
			query.setParameter("subPriceTypeId"+i, unit.getSubPriceTypeId());
			query.setParameter("spicingRequired"+i, unit.getSpicingRequired());
			query.setParameter("frRequired"+i, unit.getFrRequired());
			query.setParameter("seasonality"+i, unit.getSeasonality());
			query.setParameter("seasonStartMonth"+i, unit.getSeasonStartMonth());
			query.setParameter("seasonEndMonth"+i, unit.getSeasonEndMonth());
			query.setParameter("pricingFrequencyId"+i, unit.getPricingFrequencyId());
			query.setParameter("rtnPeriod"+i, unit.getRtnPeriod());
			query.setParameter("backdateRequired"+i, unit.getBackdateRequired());
			query.setParameter("allowEditPMPrice"+i, unit.getAllowEditPMPrice());
			query.setParameter("ruaAllowed"+i, unit.getRuaAllowed());
			query.setParameter("isFreshItem"+i, unit.getIsFreshItem());
			query.setParameter("allowProductChange"+i, unit.getAllowProductChange());
			query.setParameter("formDisplay"+i, unit.getFormDisplay());
			query.setParameter("productCycle"+i, unit.getProductCycle());
			query.setParameter("crossCheckGroup"+i, unit.getCrossCheckGroup());
			query.setParameter("cpiQuotationType"+i, unit.getCpiQuotationType());
			query.setParameter("isTemporary"+i, unit.getIsTemporary());
			query.setParameter("isNPriceMandatory"+i, unit.getIsNPriceMandatory());
			query.setParameter("isSPriceMandatory"+i, unit.getIsSPriceMandatory());
			query.setParameter("dataTransmissionRule"+i, unit.getDataTransmissionRule());
			if(unit.getConsolidatedSPRMean()!=null){
				query.setParameter("consolidatedSPRMean"+i, String.valueOf(unit.getConsolidatedSPRMean()));
			} else {
				query.setParameter("consolidatedSPRMean"+i, unit.getConsolidatedSPRMean());
			}
			if(unit.getConsolidatedSPRMean()!=null){
				query.setParameter("consolidatedSPRSD"+i, String.valueOf(unit.getConsolidatedSPRSD()));
			} else {
				query.setParameter("consolidatedSPRSD"+i, unit.getConsolidatedSPRSD());
			}
			if(unit.getConsolidatedSPRMean()!=null){
				query.setParameter("consolidatedNPRMean"+i, String.valueOf(unit.getConsolidatedNPRMean()));
			} else {
				query.setParameter("consolidatedNPRMean"+i, unit.getConsolidatedNPRMean());
			}
			if(unit.getConsolidatedSPRMean()!=null){
				query.setParameter("consolidatedNPRSD"+i, String.valueOf(unit.getConsolidatedNPRSD()));
			} else {
				query.setParameter("consolidatedNPRSD"+i, unit.getConsolidatedNPRSD());
			}		
			query.setParameter("subItemId"+i, newSubItemList.get(unit.getSubItemCode()).getSubItemId());
			query.setParameter("productCategoryId"+i, unit.getProductCategoryId());
			query.setParameter("icpType"+i, unit.getIcpType());
			query.setParameter("convertAfterClosingDate"+i, unit.convertAfterClosingDate);
			i++;
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.setParameter("cpiBasePeriod", cpiBasePeriod);
		query.executeUpdate();
		
		sql = "SELECT * from (select top ( :count ) unitId, code from [Unit] order by unitId desc) as a order by unitId asc";
		query = this.getSession().createSQLQuery(sql);
		query.setParameter("count", newUnitList.size());
		query.addScalar("unitId", StandardBasicTypes.INTEGER);
		query.addScalar("code", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(ImportRebasingUnitList.class));
		return query.list();
	}
	
	public void updateUnitObsoleteDateByRebasing(List<Integer> unitIds, Date obsoleteDate){
		String sql = "Update [Unit] set [ObsoleteDate] = :obsoleteDate where [UnitId] in ( :unitIds )";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("obsoleteDate", obsoleteDate);
		query.setParameterList("unitIds", unitIds);
		
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<String> searchValidDistinctUnitCategory2(String search, int firstRecord, int displayLength) {
		String hql = "select distinct unit.unitCategory "
                + " from Unit as unit "
                + " where unit.status = 'Active' "
                + " and (unit.effectiveDate = null or :today >= unit.effectiveDate) "
				+ " and (unit.obsoleteDate = null or :today < unit.obsoleteDate) ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}
	
	public long countValidDistinctUnitCategory2(String search){
		String hql = "select count(distinct unit.unitCategory) "
                + " from Unit as unit "
                + " where unit.status = 'Active' "
                + " and (unit.effectiveDate = null or :today >= unit.effectiveDate) "
				+ " and (unit.obsoleteDate = null or :today < unit.obsoleteDate) ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dateWithoutTime = cal.getTime();
		query.setParameter("today", dateWithoutTime);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		 
		 return (long)query.uniqueResult();
	 }
	
	@SuppressWarnings("unchecked")
	public List<String> searchValidDistinctCrossCheckGroup(String search, int firstRecord, int displayLength, Integer[] purposeIds) {
		String hql = " select distinct unit.crossCheckGroup "
                + " from Unit as unit "
				+ "  left join unit.purpose as p "
                + " where getdate() >= unit.effectiveDate and ( getdate() < unit.obsoleteDate or unit.obsoleteDate is null )"
                + " and unit.crossCheckGroup is not null ";
		
		if(purposeIds != null && purposeIds.length > 0) {
			hql += " and p.purposeId in (:purposeIds) ";
		}

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.crossCheckGroup like :search ";
		}

		hql += " group by unit.crossCheckGroup ";
		hql += " order by unit.crossCheckGroup asc ";
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if(purposeIds != null && purposeIds.length > 0) {
			query.setParameterList("purposeIds", purposeIds);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getUnitIdsForSummaryStatisticsReport(List<Integer> purpose, List<Integer> cpiSurveyForm) {
		Criteria criteria = this.createCriteria("u")
				.createAlias("u.purpose", "p");
		
		if (purpose != null && purpose.size() > 0)
			criteria.add(Restrictions.in("p.purposeId", purpose));
		
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
			criteria.add(Restrictions.in("u.cpiQoutationType", cpiSurveyForm));
		
		criteria.setProjection(Projections.groupProperty("unitId"));
		return criteria.list();
	}
	
	public List<String> searchAllICPType(String search, int firstRecord, int displayRecord){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(Projections.groupProperty("icpType"), "icpType");
		criteria.setProjection(list);
		
		if(!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.like("icpType", "%"+search+"%"));
		}
		
		criteria.setFirstResult(firstRecord).setMaxResults(displayRecord);
		criteria.addOrder(Order.asc("icpType"));
		
		return criteria.list();
	}
	
	public long countICPType(String search){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(SQLProjectionExt.groupCount(Projections.groupProperty("icpType")));
		criteria.setProjection(list);
		
		if(!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.like("icpType",  "%"+search+"%"));
		}
		
		return (long)criteria.uniqueResult();
	}
	
	public List<SummaryOfICPQuotationsByICPTypeReport> getICPQuotationByICPType(Date fromMonth, Date toMonth, List<String> icpTypes, List<String> icpProductCodes){
		
		String sql = "exec [dbo].[RealTimeCalculateQuotationStatistic] :fromMonth , :toMonth , :icpType , :icpProductCode";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("icpType", StandardBasicTypes.STRING)
				.addScalar("icpProductCode", StandardBasicTypes.STRING)
				.addScalar("icpProductName", StandardBasicTypes.STRING)
				.addScalar("preferredQuantity", StandardBasicTypes.DOUBLE)
				.addScalar("preferredUOM", StandardBasicTypes.STRING)
				.addScalar("numOfQuotation", StandardBasicTypes.INTEGER)
				.addScalar("averagePrice", StandardBasicTypes.DOUBLE)
				.addScalar("standardDeviation", StandardBasicTypes.DOUBLE)
				.addScalar("cv", StandardBasicTypes.DOUBLE)
				.addScalar("minPrice", StandardBasicTypes.DOUBLE)
				.addScalar("maxPrice", StandardBasicTypes.DOUBLE)
				.addScalar("radio", StandardBasicTypes.DOUBLE);
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		
		StringBuilder typeBuilder = new StringBuilder();
		if(icpTypes!=null && icpTypes.size()>0){
			typeBuilder.append("<query>");
			for (String icpType : icpTypes){
				typeBuilder.append("<icpType>"+icpType+"</icpType>");
			}
			typeBuilder.append("</query>");
			query.setParameter("icpType", typeBuilder.toString());
		} else {
			query.setParameter("icpType", null);
		}
		
		StringBuilder productCodeBuilder = new StringBuilder();
		if(icpProductCodes!=null && icpProductCodes.size()>0){
			productCodeBuilder.append("<query>");
			for (String icpProductCode : icpProductCodes){
				productCodeBuilder.append("<icpProductCode>"+icpProductCode+"</icpProductCode>");
			}
			productCodeBuilder.append("</query>");
			query.setParameter("icpProductCode", productCodeBuilder.toString());
		} else {
			query.setParameter("icpProductCode", null);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfICPQuotationsByICPTypeReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductCycleReportByVariety> getProductCycleReportByVariety(List<Integer> unitId, List<Integer> cpiSurveyForm, List<Integer> purpose, List<Integer> productGroup, Date startMonth, Date endMonth) {
		
		/*
		String hql = ""
				+ "SELECT ab.productGroup "
				+ "	,ab.varietyCode "
				+ "	,ab.varietyChineseName "
				+ "	,ab.varietyEnglishName "
				+ "	,sum(ab.noOfQuotations) AS noOfQuotations "
//				+ "	,ab.productChange "
//				+ "	,ab.totalNoOfPricingMonth "
//				+ "FROM ( "
				+ "	,sum(ab.productChange) AS productChange"
				+ "	,sum(ab.totalNoOfPricingMonth) AS totalNoOfPricingMonth "
				+ " FROM ( "

				+ "	SELECT pg1.Code AS productGroup "
				+ "		,u1.Code AS varietyCode "
				+ "		,u1.ChineseName AS varietyChineseName "
				+ "		,u1.EnglishName AS varietyEnglishName "
				+ "		,count(DISTINCT iqr1.QuotationId) AS noOfQuotations "
				+ "		,iqr1.ReferenceMonth AS referenceMonth "
				+ "		,sum(CASE "
				+ "				WHEN iqr1.IsNewRecruitment = 1 "
				+ "					OR iqrr1.IsRUA = 1 "
				+ "					THEN 0 "
				+ "				ELSE 1 "
				+ "				END) AS totalNoOfPricingMonth "
				+ "		,sum(CASE "
				+ "				WHEN iqr1.IsProductChange = 1 "
				+ "					AND iqr1.IsNewRecruitment = 0 "
				+ "					AND iqrr1.IsRUA = 0 "
				+ "					THEN 1 "
				+ "				ELSE 0 "
				+ "				END) AS productChange "
				+ "	FROM IndoorQuotationRecord AS iqr1 "
				+ "	LEFT JOIN Quotation AS q1 "
				+ "		ON q1.QuotationId = iqr1.QuotationId "
				+ "	LEFT JOIN Unit AS u1 "
				+ "		ON u1.UnitId = q1.UnitId "
				+ "	LEFT JOIN ProductGroup AS pg1 "
				+ "		ON u1.ProductCategoryId = pg1.ProductGroupId "
				+ "	LEFT JOIN IndoorQuotationRecord AS iqrr1 "
				+ "		ON iqr1.IndoorQuotationRecordId = iqrr1.IndoorQuotationRecordId "
				+ "	WHERE iqr1.ReferenceMonth BETWEEN :startMonth "
				+ "			AND :endMonth "
				+ "		AND ( "
				+ "			iqr1.QuotationRecordId IS NOT NULL "
				+ "			OR iqr1.QuotationRecordId != '' "
				+ "			) ";
		if (unitId != null && unitId.size() > 0)
			hql += " and u1.UnitId in (:unitId) ";
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
			hql += " and u1.CPIQoutationType in (:cpiSurveyForm) ";
		
		if (purpose != null && purpose.size() > 0)
			hql += " and u1.PurposeId in (:purpose) ";
		
		if (productGroup != null && productGroup.size() > 0)
			hql += " and u1.ProductCategoryId in (:productGroup) ";
		
		hql += "	GROUP BY pg1.Code "
		+ "		,u1.Code "
		+ "		,u1.ChineseName "
		+ "		,u1.EnglishName "
		+ "		,iqr1.ReferenceMonth "
		+ "	 "
		+ "	UNION ALL "
		+ "	 "
		+ "	SELECT pg2.Code AS productGroup "
		+ "		,u2.Code AS varietyCode "
		+ "		,u2.ChineseName AS varietyChineseName "
		+ "		,u2.EnglishName AS varietyEnglishName "
		+ "		,count(DISTINCT iqr2.QuotationId) AS noOfQuotations "
		+ "		,iqr2.ReferenceMonth AS referenceMonth "
		+ "		,sum(CASE "
		+ "				WHEN iqr2.IsNewRecruitment = 1 "
		+ "					OR iqrr2.IsRUA = 1 "
		+ "					THEN 0 "
		+ "				ELSE 1 "
		+ "				END) AS totalNoOfPricingMonth "
		+ "		,sum(CASE "
		+ "				WHEN iqr2.IsProductChange = 1 "
		+ "					AND iqr2.IsNewRecruitment = 0 "
		+ "					AND iqrr2.IsRUA = 0 "
		+ "					THEN 1 "
		+ "				ELSE 0 "
		+ "				END) AS productChange "
		+ "	FROM IndoorQuotationRecord AS iqr2 "
		+ "	LEFT JOIN Quotation AS q2 "
		+ "		ON q2.QuotationId = iqr2.QuotationId "
		+ "	LEFT JOIN Unit AS u2 "
		+ "		ON u2.UnitId = q2.UnitId "
		+ "	LEFT JOIN ProductGroup AS pg2 "
		+ "		ON u2.ProductCategoryId = pg2.ProductGroupId "
		+ "	LEFT JOIN IndoorQuotationRecord AS iqrr2 "
		+ "		ON iqr2.IndoorQuotationRecordId = iqrr2.IndoorQuotationRecordId "
		+ "	WHERE iqr2.ReferenceMonth BETWEEN :startMonth "
		+ "			AND :endMonth "
		+ "		AND ( "
		+ "			iqr2.QuotationRecordId IS NULL "
		+ "			OR iqr2.QuotationRecordId = '' "
		+ "			) ";
				
		if (unitId != null && unitId.size() > 0)
			hql += " and u2.UnitId in (:unitId) ";
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
			hql += " and u2.CPIQoutationType in (:cpiSurveyForm) ";
		
		if (purpose != null && purpose.size() > 0)
			hql += " and u2.PurposeId in (:purpose) ";
		
		if (productGroup != null && productGroup.size() > 0)
			hql += " and u2.ProductCategoryId in (:productGroup) ";
				
		hql	+= "	GROUP BY pg2.Code "
			+ "		,u2.Code "
			+ "		,u2.ChineseName "
			+ "		,u2.EnglishName "
			+ "		,iqr2.ReferenceMonth "
//			+ "	) ab "
			+ "	) AS ab "
			+ "GROUP BY ab.productGroup "
			+ "	,ab.varietyCode "
			+ "	,ab.varietyChineseName "
			+ "	,ab.varietyEnglishName "
			+ "	,ab.totalNoOfPricingMonth "
			+ "	,ab.productChange "
			+ "ORDER BY ab.productGroup "
			+ "	,ab.varietyCode;";
		*/
		
		String sql = " "
				+ " SELECT pg1.Code AS productGroup, "
				+ " u1.Code AS varietyCode, "
				+ " u1.ChineseName AS varietyChineseName, "
				+ " u1.EnglishName AS varietyEnglishName, "
				+ " count(DISTINCT iqr1.QuotationId) AS noOfQuotations, "
				+ " sum(CASE WHEN iqr1.IsProductChange = 1 "
				+ " AND iqr1.IsNewRecruitment = 0 "
				+ " AND iqrr1.IsRUA = 0 "
				+ " AND iqr1.QuotationRecordId IS NULL THEN 1 ELSE 0 END) + "
				+ " sum(CASE WHEN iqr1.QuotationRecordId IS NOT NULL "
				+ " AND qr.IsProductChange = 1 "
				+ " AND qr.IsNewRecruitment = 0 "
				+ " AND iqr1.IsRUA = 0 THEN 1 ELSE 0 END) "
				+ " AS productChange, "
				+ " result.totalNoOfPricingMonth AS totalNoOfPricingMonth "
				+ " FROM IndoorQuotationRecord AS iqr1 "
				+ " LEFT JOIN Quotation AS q1 ON q1.QuotationId = iqr1.QuotationId "
				+ " LEFT JOIN Unit AS u1 ON u1.UnitId = q1.UnitId "
				+ " LEFT JOIN ProductGroup AS pg1 ON u1.ProductCategoryId = pg1.ProductGroupId "
				+ " LEFT JOIN IndoorQuotationRecord AS iqrr1 ON iqr1.IndoorQuotationRecordId = iqrr1.IndoorQuotationRecordId "
				+ " LEFT JOIN QuotationRecord AS qr ON iqr1.QuotationRecordId = qr.QuotationRecordId "
				+ " LEFT JOIN "
				+ " (SELECT countQuotationId.productGroup, "
				+ " countQuotationId.varietyCode, "
				+ " count(countQuotationId.QuotationId) AS totalNoOfPricingMonth "
				+ " FROM "
				+ " (SELECT DISTINCT pg2.Code AS productGroup , "
				+ " u2.Code AS varietyCode, "
				+ " iqr2.QuotationId, "
				+ " iqr2.ReferenceMonth "
				+ " FROM IndoorQuotationRecord AS iqr2 "
				+ " LEFT JOIN Quotation AS q2 ON q2.QuotationId = iqr2.QuotationId "
				+ " LEFT JOIN Unit AS u2 ON u2.UnitId = q2.UnitId "
				+ " LEFT JOIN ProductGroup AS pg2 ON u2.ProductCategoryId = pg2.ProductGroupId "
				+ " LEFT JOIN IndoorQuotationRecord AS iqrr2 ON iqr2.IndoorQuotationRecordId = iqrr2.IndoorQuotationRecordId "
				+ " WHERE iqr2.ReferenceMonth BETWEEN :startMonth AND :endMonth "
				+ " AND (iqr2.IsNewRecruitment = 0 "
				+ " AND iqrr2.IsRUA = 0) ";
				      
				      if (unitId != null && unitId.size() > 0)
				      sql += " and u2.UnitId in (:unitId) ";
				    
				      if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
				      sql += " and u2.CPIQoutationType in (:cpiSurveyForm) ";
				    
				      if (purpose != null && purpose.size() > 0)
				      sql += " and u2.PurposeId in (:purpose) ";
				    
				      if (productGroup != null && productGroup.size() > 0)
				      sql += " and u2.ProductCategoryId in (:productGroup) ";

				sql += " )countQuotationId "

				+ " GROUP BY countQuotationId.productGroup , countQuotationId.varietyCode) result ON result.productGroup = pg1.Code AND result.varietyCode = u1.Code"
				+ " WHERE iqr1.ReferenceMonth BETWEEN :startMonth AND :endMonth ";
				  if (unitId != null && unitId.size() > 0)
				  sql += " and u1.UnitId in (:unitId) ";
				    
				  if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
				      sql += " and u1.CPIQoutationType in (:cpiSurveyForm) ";
				    
				  if (purpose != null && purpose.size() > 0)
				  sql += " and u1.PurposeId in (:purpose) ";
				    
				  if (productGroup != null && productGroup.size() > 0)
				  sql += " and u1.ProductCategoryId in (:productGroup) ";

				sql += " GROUP BY pg1.Code , "
				+ " u1.Code , "
				+ " u1.ChineseName , "
				+ " u1.EnglishName, "
				+ " result.totalNoOfPricingMonth ";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (unitId != null && unitId.size() > 0)
			query.setParameterList("unitId", unitId);
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		
		if (purpose != null && purpose.size() > 0)
			query.setParameterList("purpose", purpose);
		
		if (productGroup != null && productGroup.size() > 0)
			query.setParameterList("productGroup", productGroup);
		
		query.addScalar("productGroup", StandardBasicTypes.STRING);
		query.addScalar("varietyCode", StandardBasicTypes.STRING);
		query.addScalar("varietyChineseName", StandardBasicTypes.STRING);
		query.addScalar("varietyEnglishName", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotations", StandardBasicTypes.INTEGER);		
		query.addScalar("productChange", StandardBasicTypes.INTEGER);		
		query.addScalar("totalNoOfPricingMonth", StandardBasicTypes.INTEGER);

		query.setResultTransformer(Transformers.aliasToBean(ProductCycleReportByVariety.class));		
		return query.list();
	}

	public List<Integer> getExistingIdsByCpiBasePeriod(List<String> ids){
		String hql = "select u.id as id "
				+ " from Unit as u "
				+ " where u.cpiBasePeriod in :ids "
				+ " group by u.id ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return (List<Integer>)query.list();
	}
	
	public long countValidDistinctCrossCheckGroup(String search, Integer[] purposeIds) {
		String hql = " select count(distinct unit.crossCheckGroup) "
                + " from Unit as unit "
				+ "  left join unit.purpose as p "
                + " where getdate() >= unit.effectiveDate and ( getdate() < unit.obsoleteDate or unit.obsoleteDate is null ) "
                + " and unit.crossCheckGroup is not null ";
		
		if(purposeIds != null && purposeIds.length > 0) {
			hql += " and p.purposeId in (:purposeIds) ";
		}

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.crossCheckGroup like :search ";
		}

		hql += " group by unit.crossCheckGroup ";
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if(purposeIds != null && purposeIds.length > 0) {
			query.setParameterList("purposeIds", purposeIds);
		}
		 
		
		Long t = (Long) query.uniqueResult();
		
		return t == null?0:t;
	}
	
	public List<Unit> getUnitByObsoleteDate(){
		String hql = "Select distinct u"
				+ " From Unit as u"
				+ " where (u.obsoleteDate is null and u.obsoleteDate > getDate()) and u.status = :status ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("status", "Active");
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> searchUnitCategory(String search, int firstRecord, int displayLength) {
		String hql = " select distinct unit.unitCategory "
                + " from Unit as unit "
                + " where 1 = 1 "
                + " and unit.unitCategory is not null and unit.unitCategory != '' ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}
	
	public long countUnitCategory(String search) {
		String hql = " select count(distinct unit.unitCategory) "
                + " from Unit as unit "
                + " where 1 = 1 "
                + " and unit.unitCategory is not null and unit.unitCategory != '' ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		return (long)query.uniqueResult();
	}
	
	public void insertUOMCategoryUnitByRebasing(String insertSQL){
		String sql = "INSERT INTO [UOMCategoryUnit] ("
				+ " [UOMCategoryId], [UnitId] ,[CreatedDate] ,[ModifiedDate] ) VALUES "+insertSQL;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.executeUpdate();
	}
}
