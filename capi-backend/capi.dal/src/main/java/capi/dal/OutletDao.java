package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
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
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationParameter;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Outlet;
import capi.model.SystemConstant;
import capi.model.api.dataSync.OutletAttachmentSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.OutletTypeOutletSyncData;
import capi.model.api.dataSync.UpdateOutletImageModel;
import capi.model.masterMaintenance.OutletTableList;
import capi.model.report.ExperienceSummaryReport;
import capi.model.report.OutletAmendmentReport;

@Repository("OutletDao")
public class OutletDao extends GenericDao<Outlet>{

	public List<OutletTableList> getTableList(String search,
			int firstRecord, int displayLength, Order order,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet, Boolean validOnly) {
		return getTableList(search,
				firstRecord, displayLength, order,
				outletTypeId, districtId, tpuId, activeOutlet, validOnly,
				null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<OutletTableList> getTableList(String search,
			int firstRecord, int displayLength, Order order,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet, Boolean validOnly,
			String name, String tel) {

		Criteria critera = this.createCriteria("outlet")
                .createAlias("outlet.tpu", "tpu", JoinType.INNER_JOIN)
                .createAlias("tpu.district", "d", JoinType.INNER_JOIN)
                .createAlias("outlet.outletTypes", "t", JoinType.LEFT_OUTER_JOIN)
                .createAlias("outlet.quotations", "q", JoinType.LEFT_OUTER_JOIN);
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("outlet.outletId"), "outletId");
        projList.add(Projections.groupProperty("outlet.firmCode"), "firmCode");
        projList.add(Projections.groupProperty("outlet.brCode"), "brCode");
        projList.add(Projections.groupProperty("outlet.name"), "name");
        projList.add(Projections.groupProperty("outlet.streetAddress"), "streetAddress");
        projList.add(Projections.groupProperty("outlet.detailAddress"), "detailAddress");
        projList.add(Projections.groupProperty("tpu.code"), "tpu");
        
        projList.add(Projections.countDistinct("q.quotationId"), "quotationCount");

        projList.add(SQLProjectionExt.sqlProjection(
                "CASE WHEN count({q}.quotationId) > 0 THEN 'Y' ELSE 'N' END as activeOutlet ", 
                new String[]{"activeOutlet"}, 
                new Type[]{StandardBasicTypes.STRING}), "activeOutlet");
        
        if (StringUtils.isNotEmpty(activeOutlet)){
            // should be the last group column
            if (activeOutlet.equals("Y")){
                projList.add(SQLProjectionExt.groupByHaving("{d}.code", "district", StandardBasicTypes.STRING, "count({q}.quotationId) > 0"), "district");
            }
            else{
                projList.add(SQLProjectionExt.groupByHaving("{d}.code", "district", StandardBasicTypes.STRING, "count({q}.quotationId) = 0"), "district");
            }
        } else {
        	projList.add(Projections.groupProperty("d.code"), "district");
        }
        
        critera.setProjection(projList);
        
        if (StringUtils.isNotEmpty(search)) {
	        critera.add(Restrictions.or(
	                Restrictions.sqlRestriction(" {alias}.firmCode LIKE ? ", "%"+search+"%", StandardBasicTypes.STRING),
	                Restrictions.like("outlet.name", "%"+search+"%"),
	                Restrictions.like("outlet.brCode", "%"+search+"%"),
	                Restrictions.like("outlet.streetAddress", "%"+search+"%"),
	                Restrictions.like("outlet.detailAddress", "%"+search+"%"),
	                Restrictions.like("tpu.code", "%"+search+"%"),
	                Restrictions.like("d.code", "%"+search+"%")
	            ));
        }

        if (outletTypeId != null && outletTypeId.length > 0) {
        	critera.add(Restrictions.in("t.shortCode", outletTypeId));
        }
        
        if (districtId != null && districtId.length > 0) {
        	critera.add(Restrictions.in("d.districtId", districtId));
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	critera.add(Restrictions.in("tpu.tpuId", tpuId));
        }
        
        if (validOnly != null && validOnly){
        	critera.add(Restrictions.eq("outlet.status", "Valid"));
        }
        
        if (StringUtils.isNotBlank(name)) {
        	critera.add(Restrictions.eq("outlet.name", name));
        }
        
        if (StringUtils.isNotBlank(tel)) {
        	critera.add(Restrictions.eq("outlet.tel", tel));
        }
        
        critera.setFirstResult(firstRecord);
        critera.setMaxResults(displayLength);
        critera.addOrder(order);
        
        critera.setResultTransformer(Transformers.aliasToBean(OutletTableList.class));
        
        return critera.list();
	}
	
	public long countTableList(String search,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet, Boolean validOnly) {
		return countTableList(search,
				outletTypeId, districtId, tpuId, activeOutlet, validOnly,
				null, null);
	}

	public long countTableList(String search,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet, Boolean validOnly,
			String name, String tel) {
		Criteria criteria = this.createCriteria("outlet")
                .createAlias("outlet.tpu", "tpu", JoinType.INNER_JOIN)
                .createAlias("tpu.district", "d", JoinType.INNER_JOIN)
                .createAlias("outlet.outletTypes", "t", JoinType.LEFT_OUTER_JOIN)
                .createAlias("outlet.quotations", "q", JoinType.LEFT_OUTER_JOIN);
        
        ProjectionList projList = Projections.projectionList();
        
        SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("outlet.outletId"),
        		Projections.groupProperty("outlet.firmCode"),
        		Projections.groupProperty("outlet.name"),
        		Projections.groupProperty("outlet.brCode"),
        		Projections.groupProperty("outlet.streetAddress"),
        		Projections.groupProperty("outlet.detailAddress"),
        		Projections.groupProperty("tpu.code")
		);
        
        if (StringUtils.isNotEmpty(activeOutlet)){
            // should be the last group column
            if (activeOutlet.equals("Y")){
            	project.add(SQLProjectionExt.groupByHaving("{d}.code", "district", StandardBasicTypes.STRING, "count({q}.quotationId) > 0"));
            }
            else{
            	project.add(SQLProjectionExt.groupByHaving("{d}.code", "district", StandardBasicTypes.STRING, "count({q}.quotationId) = 0"));
            }
        } else {
        	project.add(Projections.groupProperty("d.code"));
        }
        projList.add(project);
//        projList.add(Projections.groupProperty("outlet.outletId"), "outletId");
//        projList.add(Projections.groupProperty("outlet.firmCode"), "firmCode");
//        projList.add(Projections.groupProperty("outlet.name"), "name");
//        projList.add(Projections.groupProperty("outlet.streetAddress"), "streetAddress");
//        projList.add(Projections.groupProperty("outlet.detailAddress"), "detailAddress");
//        projList.add(Projections.groupProperty("tpu.code"), "tpu");
//        
//        projList.add(Projections.countDistinct("q.quotationId"), "quotationCount");
//
//        projList.add(SQLProjectionExt.sqlProjection(
//                "CASE WHEN count({q}.quotationId) > 0 THEN 'Y' ELSE 'N' END as activeOutlet ", 
//                new String[]{"activeOutlet"}, 
//                new Type[]{StandardBasicTypes.STRING}), "activeOutlet");
        
//        if (StringUtils.isNotEmpty(activeOutlet)){
//            // should be the last group column
//            if (activeOutlet.equals("Y")){
//                projList.add(SQLProjectionExt.groupByHaving("{d}.code", "district", StandardBasicTypes.STRING, "count({q}.quotationId) > 0"), "district");
//            }
//            else{
//                projList.add(SQLProjectionExt.groupByHaving("{d}.code", "district", StandardBasicTypes.STRING, "count({q}.quotationId) = 0"), "district");
//            }
//        } else {
//        	projList.add(Projections.groupProperty("d.code"), "district");
//        }
        
        criteria.setProjection(projList);
        
        if (StringUtils.isNotEmpty(search)) {
	        criteria.add(Restrictions.or(
	                Restrictions.sqlRestriction(" {alias}.firmCode LIKE ? ", "%"+search+"%", StandardBasicTypes.STRING),
	                Restrictions.like("outlet.name", "%"+search+"%"),
	                Restrictions.like("outlet.brCode", "%"+search+"%"),
	                Restrictions.like("outlet.streetAddress", "%"+search+"%"),
	                Restrictions.like("outlet.detailAddress", "%"+search+"%"),
	                Restrictions.like("tpu.code", "%"+search+"%"),
	                Restrictions.like("d.code", "%"+search+"%")
	            ));
        }

        if (outletTypeId != null && outletTypeId.length > 0) {
        	criteria.add(Restrictions.in("t.shortCode", outletTypeId));
        }
        
        if (districtId != null && districtId.length > 0) {
        	criteria.add(Restrictions.in("d.districtId", districtId));
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	criteria.add(Restrictions.in("tpu.tpuId", tpuId));
        }

        if (validOnly != null && validOnly){
        	criteria.add(Restrictions.eq("outlet.status", "Valid"));
        }
        
        if (StringUtils.isNotBlank(name)) {
        	criteria.add(Restrictions.eq("outlet.name", name));
        }
        
        if (StringUtils.isNotBlank(tel)) {
        	criteria.add(Restrictions.eq("outlet.tel", tel));
        }
		
        return (long) criteria.uniqueResult();

//        ScrollableResults result = criteria.scroll();
//		result.last();
//		int total = result.getRowNumber() + 1;
//		result.close();
//		
//		return total;
		
		//return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public Integer getMaxFirmCode() {
		return (Integer)this.createCriteria()
				.setProjection(Projections.max("firmCode"))
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getQuotationOutletTypeCodes(int outletId) {
		Criteria criteria = this.createCriteria("outlet")
                .createAlias("outlet.quotations", "q", JoinType.INNER_JOIN)
                .createAlias("q.unit", "u", JoinType.INNER_JOIN)
                .createAlias("u.subItem", "s", JoinType.INNER_JOIN)
                .createAlias("s.outletType", "t", JoinType.INNER_JOIN);
		
        criteria.setProjection(Projections.distinct(SQLProjectionExt.sqlProjection("Right({t}.code,3) as shortCode", new String[]{"shortCode"}, new Type[]{StandardBasicTypes.STRING})));
        
        criteria.add(Restrictions.eq("outletId", outletId));
        
        return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet) {

		String hql = "select o.outletId "
				+ " from Outlet as o"
				+ " inner join o.tpu as tpu "	
				+ " inner join tpu.district as d "
				+ " left outer join o.outletTypes as t "
				+ " left outer join o.quotations as q "
				+ " where 1=1 and o.status = 'Valid' ";
		if (StringUtils.isNotEmpty(search)) {
			hql += " and (str(o.firmCode) like :search "
					+ " or o.name like :search "
					+ " or o.streetAddress like :search "
					+ " or o.detailAddress like :search "
					+ " or tpu.code like :search "
					+ " or d.code like :search )";
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
			hql += " and t.shortCode in (:outletTypeId) ";
		}

        if (districtId != null && districtId.length > 0) {
        	hql += " and d.districtId in (:districtId) ";
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	hql += " and tpu.tpuId in (:tpuId) ";
        }

		hql += " group by o.outletId ";

		if (StringUtils.isNotEmpty(activeOutlet)) {
			if (activeOutlet.equals("Y")) {
				hql += " having count(q.quotationId) > 0 ";
			} else {
				hql += " having count(q.quotationId) = 0 ";
			}
		}
		
		Query query = this.getSession().createQuery(hql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (districtId != null && districtId.length > 0) {
			query.setParameterList("districtId", districtId);
		}
		if (tpuId != null && tpuId.length > 0) {
			query.setParameterList("tpuId", tpuId);
		}
		
        return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<OutletTableList> getTableListByIds(Integer[] ids) {
		Criteria critera = this.createCriteria("outlet")
                .createAlias("outlet.tpu", "tpu", JoinType.INNER_JOIN)
                .createAlias("tpu.district", "d", JoinType.INNER_JOIN)
                .createAlias("outlet.outletTypes", "t", JoinType.LEFT_OUTER_JOIN)
                .createAlias("outlet.quotations", "q", JoinType.LEFT_OUTER_JOIN);
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("outlet.outletId"), "outletId");
        projList.add(Projections.groupProperty("outlet.firmCode"), "firmCode");
        projList.add(Projections.groupProperty("outlet.name"), "name");
        projList.add(Projections.groupProperty("outlet.streetAddress"), "streetAddress");
        projList.add(Projections.groupProperty("outlet.detailAddress"), "detailAddress");
        projList.add(Projections.groupProperty("tpu.code"), "tpu");
        
        projList.add(Projections.countDistinct("q.quotationId"), "quotationCount");

        projList.add(SQLProjectionExt.sqlProjection(
                "CASE WHEN count({q}.quotationId) > 0 THEN 'Y' ELSE 'N' END as activeOutlet ", 
                new String[]{"activeOutlet"}, 
                new Type[]{StandardBasicTypes.STRING}), "activeOutlet");
        
        projList.add(Projections.groupProperty("d.code"), "district");
        
        critera.setProjection(projList);
        
        if (ids != null && ids.length > 0) {
        	critera.add(Restrictions.in("outlet.id", ids));
        }
        
        critera.setResultTransformer(Transformers.aliasToBean(OutletTableList.class));
        
        return critera.list();
        
	}
	
	@SuppressWarnings("unchecked")
	public List<OutletTableList> searchTableList(String search, int firstRecord, int displayLength, Order order){
		
		Criteria critera = this.createCriteria("outlet");
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property("outlet.outletId"), "outletId");
        projList.add(Projections.property("outlet.firmCode"), "firmCode");
        projList.add(Projections.property("outlet.name"), "name");

        critera.setProjection(projList);
        
        if (StringUtils.isNotEmpty(search)) {
	        critera.add(Restrictions.or(
                Restrictions.sqlRestriction(" outlet.firmCode LIKE ? ", "%"+search+"%", StandardBasicTypes.STRING),
                Restrictions.sqlRestriction(" outlet.outletId LIKE ? ", "%"+search+"%", StandardBasicTypes.STRING),
                Restrictions.like("outlet.name", "%"+search+"%")
            ));
        }
        
        critera.setFirstResult(firstRecord);
        critera.setMaxResults(displayLength);
        critera.addOrder(order);
        
        critera.setResultTransformer(Transformers.aliasToBean(OutletTableList.class));
        
        return critera.list();
	}

	public long searchTableListCount (String search){
		
		Criteria critera = this.createCriteria("outlet");
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.countDistinct("outlet.outletId"));

        critera.setProjection(projList);
        
        if (StringUtils.isNotEmpty(search)) {
	        critera.add(Restrictions.or(
                Restrictions.sqlRestriction(" outlet.firmCode LIKE ? ", "%"+search+"%", StandardBasicTypes.STRING),
                Restrictions.sqlRestriction(" outlet.outletId LIKE ? ", "%"+search+"%", StandardBasicTypes.STRING),
                Restrictions.like("outlet.name", "%"+search+"%")
            ));
        }

        return (long) critera.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Outlet> search(String search, int firstRecord, int displayLength) {
		Criteria critera = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("firmCode")).addOrder(Order.asc("name"));

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("str({alias}.firmCode)+' - '+name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return critera.list();
	}

	public long countSearch(String search) {
		Criteria critera = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("str({alias}.firmCode)+' - '+name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	/**
	 * Data Sync
	 */
	
	public List<OutletSyncData> getUpdatedOutlets(Date lastSyncTime, Integer[] outletIds){
		String openingStartTime = String.format("dbo.FormatTime(o.openingStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String openingEndTime = String.format("dbo.FormatTime(o.openingEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientStartTime = String.format("dbo.FormatTime(o.convenientStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime = String.format("dbo.FormatTime(o.convenientEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String openingStartTime2 = String.format("dbo.FormatTime(o.openingStartTime2, '%s')", SystemConstant.TIME_FORMAT);
		String openingEndTime2 = String.format("dbo.FormatTime(o.openingEndTime2, '%s')", SystemConstant.TIME_FORMAT);
		String convenientStartTime2 = String.format("dbo.FormatTime(o.convenientStartTime2, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime2 = String.format("dbo.FormatTime(o.convenientEndTime2, '%s')", SystemConstant.TIME_FORMAT);
		
		if (lastSyncTime != null){
			lastSyncTime = DateUtils.addSeconds(lastSyncTime, -5);
		}
		
		String hql = "select o.outletId as outletId"
				+ ", t.tpuId as tpuId, o.firmCode as firmCode"
				+ ", o.name as name, o.tel as tel"
				+ ", o.detailAddress as detailAddress, o.streetAddress as streetAddress"
				+ ", o.mainContact as mainContact, o.lastContact as lastContact"
				+ ", o.fax as fax, o.webSite as webSite"
				+ ", o.marketName as marketName, o.remark as remark"
				+ ", o.discountRemark as discountRemark, o.latitude as latitude"
				+ ", o.longitude as longitude, o.imageModifiedTime as imageModifiedTime"
				+ ", o.collectionMethod as collectionMethod, o.status as status"
				+ ", case when o.openingStartTime is null then '' else "+openingStartTime+" end as openingStartTime"
				+ ", case when o.openingEndTime is null then '' else "+openingEndTime+" end as openingEndTime"
				+ ", case when o.convenientStartTime is null then '' else "+convenientStartTime+" end as convenientStartTime"
				+ ", case when o.convenientEndTime is null then '' else "+convenientEndTime+" end as convenientEndTime"
				+ ", case when o.openingStartTime2 is null then '' else "+openingStartTime2+" end as openingStartTime2"
				+ ", case when o.openingEndTime2 is null then '' else "+openingEndTime2+" end as openingEndTime2"
				+ ", case when o.convenientStartTime2 is null then '' else "+convenientStartTime2+" end as convenientStartTime2"
				+ ", case when o.convenientEndTime2 is null then '' else "+convenientEndTime2+" end as convenientEndTime2"
				+ ", o.brCode as brCode, o.outletMarketType as outletMarketType"
				+ ", o.indoorMarketName as indoorMarketName, o.isUseFRAdmin as isUseFRAdmin"
				+ ", o.createdDate as createdDate, o.modifiedDate as modifiedDate"
				+ " from Outlet as o"
				+ " left join o.tpu as t"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and ( o.modifiedDate >= :modifiedDate or o.createdDate >= :modifiedDate )";
		}
		
		if(outletIds!=null && outletIds.length>0){
			hql += " and o.outletId in ( :outletIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		if(outletIds!=null && outletIds.length>0){
			query.setParameterList("outletIds", outletIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class));
		return query.list();
	}
	
	public List<OutletTypeOutletSyncData> getUpdateOutletTypeOutlet(Date lastSyncTime){

		String sql = "Select outlet.outletId as outletId, "
				+ "outlet.shortCode as shortCode, "
				+ "outlet.createdDate as createdDate, "
				+ "outlet.modifiedDate as modifiedDate "
				+ "from OutletTypeOutlet outlet where "
				+ "outlet.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("outletId", StandardBasicTypes.INTEGER)
				.addScalar("shortCode", StandardBasicTypes.STRING)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(OutletTypeOutletSyncData.class));
		
		return query.list();
	}
	
	/**
	 * Online Function
	 */
	public List<OutletSyncData> getOutletbyNamePhoneBRCode(String name, String phone, String brCode){
		String openingStartTime = String.format("dbo.FormatTime(o.openingStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String openingEndTime = String.format("dbo.FormatTime(o.openingEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientStartTime = String.format("dbo.FormatTime(o.convenientStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime = String.format("dbo.FormatTime(o.convenientEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String openingStartTime2 = String.format("dbo.FormatTime(o.openingStartTime2, '%s')", SystemConstant.TIME_FORMAT);
		String openingEndTime2 = String.format("dbo.FormatTime(o.openingEndTime2, '%s')", SystemConstant.TIME_FORMAT);
		String convenientStartTime2 = String.format("dbo.FormatTime(o.convenientStartTime2, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime2 = String.format("dbo.FormatTime(o.convenientEndTime2, '%s')", SystemConstant.TIME_FORMAT);
		
		String hql = "select o.outletId as outletId"
				+ ", t.tpuId as tpuId, o.firmCode as firmCode"
				+ ", o.name as name, o.tel as tel"
				+ ", o.detailAddress as detailAddress, o.streetAddress as streetAddress"
				+ ", o.mainContact as mainContact, o.lastContact as lastContact"
				+ ", o.fax as fax, o.webSite as webSite"
				+ ", o.marketName as marketName, o.remark as remark"
				+ ", o.discountRemark as discountRemark, o.latitude as latitude"
				+ ", o.longitude as longitude, o.imageModifiedTime as imageModifiedTime"
				+ ", o.collectionMethod as collectionMethod, o.status as status"
				+ ", case when o.openingStartTime is null then '' else "+openingStartTime+" end as openingStartTime"
				+ ", case when o.openingEndTime is null then '' else "+openingEndTime+" end as openingEndTime"
				+ ", case when o.convenientStartTime is null then '' else "+convenientStartTime+" end as convenientStartTime"
				+ ", case when o.convenientEndTime is null then '' else "+convenientEndTime+" end as convenientEndTime"
				+ ", case when o.openingStartTime2 is null then '' else "+openingStartTime2+" end as openingStartTime2"
				+ ", case when o.openingEndTime2 is null then '' else "+openingEndTime2+" end as openingEndTime2"
				+ ", case when o.convenientStartTime2 is null then '' else "+convenientStartTime2+" end as convenientStartTime2"
				+ ", case when o.convenientEndTime2 is null then '' else "+convenientEndTime2+" end as convenientEndTime2"
				+ ", o.brCode as brCode, o.outletMarketType as outletMarketType"
				+ ", o.indoorMarketName as indoorMarketName, o.isUseFRAdmin as isUseFRAdmin"
				+ ", o.createdDate as createdDate, o.modifiedDate as modifiedDate"
				+ " from Outlet as o"
				+ " left join o.tpu as t"
				+ " where 1=0";
		
		if(!StringUtils.isEmpty(name)){
			hql += " or o.name like :name";
		}
		if(!StringUtils.isEmpty(phone)){
			hql += " or o.tel = :phone";
		}
		if(!StringUtils.isEmpty(brCode)){
			hql += " or o.brCode = :brCode";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(!StringUtils.isEmpty(name)){
			query.setParameter("name", "%" + name + "%");
		}
		if(!StringUtils.isEmpty(phone)){
			query.setParameter("phone", phone);
		}
		if(!StringUtils.isEmpty(brCode)){
			query.setParameter("brCode", brCode);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class));
		return query.list();
	}
	
	public Outlet getOutletbyFirmCode(Integer firmCode){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eqOrIsNull("firmCode", firmCode));
		return (Outlet) criteria.uniqueResult();
	}
	
	public ScrollableResults getAllOutletResult(){
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.tpu", "b", JoinType.LEFT_OUTER_JOIN)
				.createAlias("b.district", "c", JoinType.LEFT_OUTER_JOIN)
				.setFetchMode("a.outletTypes", FetchMode.JOIN)
				.setFetchMode("a.tpu", FetchMode.JOIN)
				.setFetchMode("b.district", FetchMode.JOIN)
				.setFetchMode("a.quotations", FetchMode.JOIN);
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<Outlet> getNotExistedOutlet(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("outletId", ids)));
		return criteria.list();
	}
	
	public List<Integer> getExistingOutletId(){
		Criteria criteria = this.createCriteria();
		//criteria.add(Restrictions.not(Restrictions.in("outletId", ids)));
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<Outlet> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("outletId", ids));
		return criteria.list();
	}
	
	public List<OutletAmendmentReport> generateOutletAmendmentReport(Date fromDate, Date toDate){		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GenerateOutletAmendmentReport] :fromDate,:toDate");
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		
		query.addScalar("rowNum",StandardBasicTypes.INTEGER);
		query.addScalar("firmCode",StandardBasicTypes.INTEGER);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("outletId",StandardBasicTypes.INTEGER);
		query.addScalar("username",StandardBasicTypes.STRING);
		query.addScalar("amendmentDate",StandardBasicTypes.TIMESTAMP);
		query.addScalar("name",StandardBasicTypes.STRING);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("rankCode",StandardBasicTypes.STRING);
		query.addScalar("changeDetail",StandardBasicTypes.STRING);
		query.addScalar("oldDetail",StandardBasicTypes.STRING);
		
			
		query.setResultTransformer(Transformers.aliasToBean(OutletAmendmentReport.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getBatchCodes(int outletId) {
		return this.createCriteria("o")
				.setProjection(Projections.distinct(Projections.property("batch.code")))
				.createAlias("o.quotations", "q")
				.createAlias("q.batch", "batch")
				.add(Restrictions.eq("o.id", outletId))
				.addOrder(Order.asc("batch.code"))
				.list();
	}
	
	public List<UpdateOutletImageModel> getUpdatedOutletImage(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.gt("imageModifiedTime", lastSyncTime));
		
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("imageModifiedTime"), "modifiedDate");
		projections.add(Projections.property("outletId"), "outletId");
		criteria.setProjection(projections);
		
		criteria.setResultTransformer(Transformers.aliasToBean(UpdateOutletImageModel.class));
		
		return criteria.list();
	}
	
	public List<ExperienceSummaryReport> getExperienceSummaryReport(){
		return null;
	}
	
	public List<OutletSyncData> getHistoryOutletByAssignmentIds(List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT o.* FROM QuotationRecord qr, Assignment at, Outlet o, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.AssignmentId = at.AssignmentId and at.OutletId = o.OutletId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<OutletSyncData> result1 = addScalarForOutlet(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT o.* FROM QuotationRecord qr, Assignment at, Outlet o, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.AssignmentId = at.AssignmentId and at.OutletId = o.OutletId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<OutletSyncData> result2 = addScalarForOutlet(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<OutletSyncData> returnResult = new ArrayList<OutletSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<OutletSyncData> getHistoryOutletByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
//		String query1 = "SELECT DISTINCT o.* FROM QuotationRecord qr, Quotation q, Outlet o, "
//				+ getQuotationHistoryByQuotationIdsHistoryDatesSQL
//				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.QuotationId = q.QuotationId AND q.outletId = o.outletId ORDER BY  1  DESC ";
		String query1 = "SELECT DISTINCT o.* FROM QuotationRecord qr, assignment at, Outlet o, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.assignmentId = at.assignmentId AND at.outletId = o.outletId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<OutletSyncData> result1 = addScalarForOutlet(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

//		String query2 = "SELECT DISTINCT o.* FROM QuotationRecord qr, Quotation q, Outlet o, "
//				+ getQuotationHistoryByQuotationIdsHistoryDatesSQL
//				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.QuotationId = q.QuotationId AND q.outletId = o.outletId ORDER BY  1  DESC ";
		String query2 = "SELECT DISTINCT o.* FROM QuotationRecord qr, assignment at, Outlet o, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.assignmentID = at.assignmentId AND at.outletId = o.outletId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<OutletSyncData> result2 = addScalarForOutlet(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<OutletSyncData> returnResult = new ArrayList<OutletSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public SQLQuery addScalarForOutlet(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("outletId", StandardBasicTypes.INTEGER)
		.addScalar("firmCode", StandardBasicTypes.INTEGER).addScalar("name", StandardBasicTypes.STRING)
		.addScalar("tel", StandardBasicTypes.STRING).addScalar("detailAddress", StandardBasicTypes.STRING)
		.addScalar("streetAddress", StandardBasicTypes.STRING)
		.addScalar("mainContact", StandardBasicTypes.STRING).addScalar("lastContact", StandardBasicTypes.STRING)
		.addScalar("fax", StandardBasicTypes.STRING).addScalar("webSite", StandardBasicTypes.STRING)
		.addScalar("marketName", StandardBasicTypes.STRING).addScalar("remark", StandardBasicTypes.STRING)
		.addScalar("discountRemark", StandardBasicTypes.STRING).addScalar("latitude", StandardBasicTypes.STRING)
		.addScalar("longitude", StandardBasicTypes.STRING)
		.addScalar("outletImagePath", StandardBasicTypes.STRING)
		.addScalar("imageModifiedTime", StandardBasicTypes.TIMESTAMP)
		.addScalar("collectionMethod", StandardBasicTypes.INTEGER)
		.addScalar("status", StandardBasicTypes.STRING).addScalar("tpuId", StandardBasicTypes.INTEGER)
		.addScalar("openingStartTime", StandardBasicTypes.STRING)
		.addScalar("openingEndTime", StandardBasicTypes.STRING)
		.addScalar("convenientStartTime", StandardBasicTypes.STRING)
		.addScalar("convenientEndTime", StandardBasicTypes.STRING)
		.addScalar("openingStartTime2", StandardBasicTypes.STRING)
		.addScalar("openingEndTime2", StandardBasicTypes.STRING)
		.addScalar("convenientStartTime2", StandardBasicTypes.STRING)
		.addScalar("convenientEndTime2", StandardBasicTypes.STRING)
		.addScalar("brCode", StandardBasicTypes.STRING)
		.addScalar("outletMarketType", StandardBasicTypes.INTEGER)
		.addScalar("indoorMarketName", StandardBasicTypes.STRING)
		.addScalar("isUseFRAdmin", StandardBasicTypes.BOOLEAN)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		
		return sqlQuery;
	}	

	public List<String> getOutletAttachmentImageMonthly(String endDate, String startDate, Boolean isFirst) {
		String sql = " SELECT Path AS ImagePath" 
				+ " FROM OutletAttachment " 
				+ " WHERE 1 = 1 ";
				if(!isFirst) {
					sql += " AND (ModifiedDate between '" + startDate + "' AND '" + endDate + "') ";
				} else {
					sql +=  " AND (ModifiedDate <= '" + endDate + "') ";
				}
				sql += " AND (Path <> '' AND Path IS NOT NULL) ";

		SQLQuery query = this.getSession().createSQLQuery(sql);

		// query.setParameterList("startDate", startDate);
		// query.setParameterList("endDate", endDate);

		query.addScalar("ImagePath", StandardBasicTypes.STRING);

		return query.list();
	}

	public List<String> getOutletImageMonthly(String endDate, String startDate, Boolean isFirst) {
		String sql = " SELECT OutletImagePath AS ImagePath" 
				+ " FROM Outlet " + " WHERE 1 = 1 ";
				if(!isFirst) {
					sql += " AND (ImageModifiedTime between '" + startDate + "' AND '" + endDate + "') ";
				} else {
					sql += " AND (ImageModifiedTime <= '" + endDate + "') ";
				}
				sql += " AND (OutletImagePath <> '' AND OutletImagePath IS NOT NULL) ";

		SQLQuery query = this.getSession().createSQLQuery(sql);

		// query.setParameterList("startDate", startDate);
		// query.setParameterList("endDate", endDate);

		query.addScalar("ImagePath", StandardBasicTypes.STRING);

		return query.list();
	}
	
	public List<OutletSyncData> getOutletInfoMonthly(String endDate) {

		String sql = " "
				+ " Select outletId as outletId, " 
				+ " ImageModifiedTime AS imageModifiedTime, "
				+ " OutletImagePath AS outletImagePath "
				+ " FROM Outlet " 
				+ " WHERE 1 = 1 "
				//+ " AND (imageModifiedTime between '" + startDate + "' AND '" + endDate + "') "
				+ " AND (imageModifiedTime <= '" + endDate + "') "
				+ " AND (outletImagePath <> '' AND outletImagePath IS NOT NULL) ";

				
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("outletId", StandardBasicTypes.INTEGER)
				.addScalar("imageModifiedTime", StandardBasicTypes.TIMESTAMP)
				.addScalar("outletImagePath", StandardBasicTypes.STRING);
		
		//query.setParameter("startDate", startDate);
		//query.setParameter("endDate", endDate);
		
		query.setResultTransformer(Transformers.aliasToBean(OutletSyncData.class));

		return query.list();
	}
	
	public List<String> getOutletMinDateTime() {
		String sql = " ";
		sql += " SELECT Min(DownloadDateTime.MinDateTime) AS MinDateTime FROM ( "
		+ " SELECT Min(ImageModifiedTime) AS MinDateTime  "
		+ " FROM Outlet "
		+ " WHERE (outletImagePath <> '' AND outletImagePath IS NOT NULL) "
		+ " UNION ALL "
		+ " SELECT Min(CreatedDate) AS MinDateTime  "
		+ " FROM OutletAttachment "
		+ " WHERE  (Path <> '' AND Path IS NOT NULL)  "
		+ " UNION ALL "
		+ " SELECT Min(Photo1ModifiedTime) AS MinDateTime  "
		+ " FROM Product "
		+ " WHERE Photo1Path <> '' AND Photo1Path IS NOT NULL  "
		+ " UNION ALL "
		+ " SELECT Min(Photo2ModifiedTime) AS MinDateTime  "
		+ " FROM Product "
		+ " WHERE Photo2Path <> '' AND Photo2Path IS NOT NULL  "
		+ " ) AS DownloadDateTime ";
		
		System.out.println(sql);
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		// query.setParameterList("startDate", startDate);
		// query.setParameterList("endDate", endDate);

		query.addScalar("MinDateTime", StandardBasicTypes.STRING);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public long countQuotationByOutletId(int outletId){
		Criteria criteria = this.createCriteria("o")
				.createAlias("o.quotations", "q", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("q.status", "Active"));
		criteria.add(Restrictions.eq("o.outletId", outletId));
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
}
