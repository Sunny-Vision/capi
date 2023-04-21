package capi.dal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Batch;
import capi.entity.Quotation;
import capi.model.SystemConstant;
import capi.model.api.dataSync.QuotationSyncData;
import capi.model.api.dataSync.RUAUserSyncData;
import capi.model.assignmentManagement.QuotationTableList;
import capi.model.assignmentManagement.RUASettingEditModel;
import capi.model.assignmentManagement.RUASettingTableList;
import capi.model.batch.NoFieldQuotationResult;
import capi.model.commonLookup.QuotationLookupTableList;
import capi.model.dataImportExport.ExportQuotationFRRelatedTableList;
import capi.model.dataImportExport.ExportQuotationIndoorAllocationTableList;
import capi.model.dataImportExport.ExportQuotationsUnitProductOutletList;
import capi.model.dataImportExport.ImportRebasingQuotationList;
import capi.model.report.SummaryOfICPQuotationsByICPCodeReport;


@Repository("QuotationDao")
public class QuotationDao  extends GenericDao<Quotation>{
	@SuppressWarnings("unchecked")
	public List<Quotation> getAllByOutletId(int outletId) {
		return this.createCriteria()
				.add(Restrictions.eq("outlet.outletId", outletId))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<QuotationLookupTableList> getLookupTableList(String search,
			int firstRecord, int displayLenght, Order order,
			Integer[] purposeId, String[] outletTypeId, Integer[] unitId) {

		String sql = "select q.QuotationId as id, "
				+ " purpose.Survey as survey, "
				+ " u.Code as unitCode, "
				+ " u.DisplayName as unitName, "
				+ " p.ProductId as productId, "
                + " concat(pa.SpecificationName, '=', ps.Value) as productAttribute, "
                + " o.FirmCode as firmCode, "
                + " o.Name as firmName "
                + " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1 = 1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in :unitId ";
		}
		
		sql += " group by q.QuotationId, purpose.Survey, u.Code, u.DisplayName, p.ProductId,"
				+ " pa.SpecificationName, ps.Value, "
				+ " o.FirmCode, o.Name";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(QuotationLookupTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("productAttribute", StandardBasicTypes.STRING);
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("firmName", StandardBasicTypes.STRING);

		return query.list();
	}

	public long countLookupTableList(String search,
			Integer[] purposeId, String[] outletTypeId, Integer[] unitId) {

		String sql = "select count(distinct q.QuotationId) "
				+ " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1=1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in :unitId ";
		}
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}

//		Long count = (Long)query.uniqueResult();
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search,
			Integer[] purposeId, String[] outletTypeId) {

		String sql = "select q.quotationId as quotationId "
				+ " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1=1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		sql += " group by q.quotationId ";
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);

		return (List<Integer>)query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Quotation> getAllByDistrictOutletTypes(int districtId, String outletTypeId) {
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.outlet", "o", JoinType.INNER_JOIN)
				.createAlias("o.tpu", "t", JoinType.INNER_JOIN)
				.createAlias("t.district", "d", JoinType.INNER_JOIN)
				.createAlias("q.unit","u", JoinType.INNER_JOIN)
				.createAlias("u.subItem","i", JoinType.INNER_JOIN)
				.createAlias("i.outletType", "ot", JoinType.INNER_JOIN);
	
		criteria.add(Restrictions.eq("d.districtId", districtId))
		.add(Restrictions.like("ot.code", "%"+outletTypeId));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<QuotationLookupTableList> getTableListByIds(Integer[] ids) {

		String hql = "select q.id as id, "
				+ " purpose.survey as survey, "
				+ " u.code as unitCode, "
				+ " u.displayName as unitName, "
				+ " p.id as productId, "
                + " case when spec1.productSpecificationId is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end as productAttribute, "
                + " o.firmCode as firmCode, "
                + " o.name as firmName "
                + " from Quotation as q "
                + " inner join q.unit as u "
                + " left join u.purpose as purpose "
                + " left join q.product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join q.outlet as o "
                + " left outer join o.outletTypes as t "
                + " where 1=1 ";
        if (ids != null && ids.length > 0) {
        	hql += " and q.id in :ids";
        }
		
		hql += " group by q.id, purpose.survey, u.code, u.displayName, p.id,"
				+ " spec1.productSpecificationId,spec1.specificationName,spec1.value, "
				+ " o.firmCode, o.name";
		
		Query query = this.getSession().createQuery(hql);

		if (ids != null && ids.length > 0) {
			query.setParameterList("ids", ids);
		}

		query.setResultTransformer(Transformers.aliasToBean(QuotationLookupTableList.class));

		return query.list();
	}

	public List<ExportQuotationIndoorAllocationTableList> getAllQuotationIndoorAllocationResult(){
		Criteria criteria = this.createCriteria("q");
		criteria.createAlias("q.batch", "b", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("q.outlet", "o", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("q.unit", "u", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("u.purpose", "p", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("u.pricingFrequency", "pf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("u.subItem", "si", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("si.outletType", "ot", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ot.item", "i", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("i.subGroup", "sg", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sg.group", "g", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("q.quotationId"), "quotationId");
		projList.add(Projections.property("u.code"), "unitCode");
		projList.add(Projections.property("u.chineseName"), "unitChineseName");
		projList.add(Projections.property("u.englishName"), "unitEnglishName");
		projList.add(Projections.property("u.cpiBasePeriod"), "cpiBasePeriod");
		projList.add(Projections.property("p.code"), "purposeCode");
		projList.add(Projections.property("g.code"), "groupCode");
		projList.add(Projections.property("sg.code"), "subGroupCode");
		projList.add(Projections.property("ot.code"), "outletTypeCode");
		projList.add(Projections.property("b.code"), "batchCode");
		projList.add(Projections.property("o.firmCode"), "outletFirmCode");
		projList.add(Projections.property("o.name"), "outletName");
		projList.add(Projections.property("pf.name"), "pricingMonthName");
		projList.add(Projections.property("u.seasonality"), "seasonality");
		projList.add(Projections.property("u.frRequired"), "isFRRequired");
		projList.add(Projections.property("q.indoorAllocationCode"), "indoorAllocationCode");
		
		criteria.setProjection(projList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ExportQuotationIndoorAllocationTableList.class));
		
		//return criteria.scroll(ScrollMode.FORWARD_ONLY);
		return criteria.list();
	}

	public List<ExportQuotationFRRelatedTableList> getAllQuotationFRRelatedResult(){
		String hql = " select q.quotationId as quotationId "
				+ ", o.firmCode as firmCode"
				+ ", o.name as FirmName"
				+ ", p.code as purposeCode"
				+ ", u.code as unitCode"
				+ ", u.cpiBasePeriod as cpiBasePeriod"
				+ ", u.englishName as unitEnglishName"
				+ ", u.chineseName as unitChineseName"
				+ ", u.seasonality as seasonality"
				+ ", pf.name as pricingFrequencyName"
				+ ", q.isICP as isICP"
				+ ", q.icpProductCode as icpProductCode"
				+ ", q.icpProductName as icpProductName"
				+ ", q.icpType as icpType"
				+ ", q.status as quotationStatus"
				+ ", u.frRequired as isFRRequired"
				+ ", q.isReturnGoods as isReturnGoods"
				+ ", q.isReturnNewGoods as isReturnNewGoods"
				+ ", q.lastSeasonReturnGoods as lastSeasonReturnGoods"
				+ ", q.isFRApplied as isFRApplied"
				+ ", q.isUseFRAdmin as isUseFRAdmin"
				+ ", q.usedFRValue as usedFRValue"
				+ ", q.isUsedFRPercentage as isUsedFRPercentage"
				+ ", q.lastFRAppliedDate as lastFRAppliedDate" 
				+ ", q.frAdmin as frAdmin"
				+ ", q.isFRAdminPercentage as isFRAdminPercentage"
				+ ", q.seasonalWithdrawal as seasonalWithdrawal"
				+ " from Quotation as q"
				+ " left join q.outlet as o"
				+ " left join q.unit as u"
				+ " left join u.purpose as p"
				+ " left join u.pricingFrequency as pf"
				+ " where u.frRequired = true"
				+ " and u.seasonality in (2, 3)";

		Query query = this.getSession().createQuery(hql);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportQuotationFRRelatedTableList.class));
		
		return query.list();
		//return query.scroll(ScrollMode.FORWARD_ONLY);
	}

	@SuppressWarnings("unchecked")
	public List<QuotationTableList> getQuotationTableList(String search, Integer purposeId, List<Integer> unitId, Integer productId,
			Integer firmId, Integer batchId, Integer pricingFrequencyId, String status,
			Boolean isICP, String indoorAllocationCode, int firstRecord, int displayLenght, Order order) {

		String sql = String.format("select q.QuotationId as id, pp.Name as purpose, u.Code as unitCode, " 
				+ " u.EnglishName as englishName, "
				+ " u.CPIBasePeriod as cpiBasePeriod, "
				+ " u.ChineseName as chineseName, "
				+ " concat(u.EnglishName, ' ' , u.ChineseName) as unitName, "
				+ " p.productId as productId, "
				
				+ " concat(pa.SpecificationName , '=' , ps.Value) as productAttribute1, "
				+ " concat(pa1.SpecificationName , '=' , ps1.Value) as productAttribute2, "
				+ " concat(pa2.SpecificationName , '=' , ps2.Value) as productAttribute3, "
				+ " concat(pa3.SpecificationName , '=' , ps3.Value) as productAttribute4, "
				+ " concat(pa4.SpecificationName , '=' , ps4.Value) as productAttribute5, "
				
//				+ " u.CPIQoutationType as cpiQuotationType, "
				+ " case when u.CPIQoutationType = 1 then 'Market' "
				+ " when u.CPIQoutationType = 2 then 'Supermarket' "
				+ " when u.CPIQoutationType = 3 then 'Batch' "
				+ " when u.CPIQoutationType = 4 then 'Others' "
				+ " else '' end as cpiQuotationType, "
				
				+ " o.OutletId as outletId, o.Name as firmName, b.Code as batchCode, "
				+ " pf.Name as pricingFrequency, q.Status as Status, q.IsICP as isICP, "
				+ " q.IndoorAllocationCode as indoorAllocationCode, q.CPICompilationSeries as cpiCompilationSeries, "
				+ " case when q.SeasonalWithdrawal is null then '' else format(q.SeasonalWithdrawal, '%s', 'en-us') end as seasonalWithdrawal, "
				+ " q.FRAdmin as frAdmin, q.FRField as frField, "
				+ " case when q.LastFRAppliedDate is null then '' else format(q.LastFRAppliedDate, '%s', 'en-us') end as lastFRAppliedDate, "
				+ " u.Seasonality as seasonality, case when q.RUADate is null then '' else format(q.RUADate, '%s', 'en-us') end as ruaDate "
				+ ", q.formType as formType "
				
				+ " from Quotation q "
				+ " inner join Unit u on q.UnitId = u.UnitId "
				+ " left outer join PricingFrequency pf on u.PricingFrequencyId = pf.PricingFrequencyId "
				+ " left outer join Purpose pp on u.PurposeId = pp.PurposeId "
				+ " inner join Batch b on q.BatchId = b.BatchId "
				+ " left outer join Outlet o on q.OutletId = o.OutletId "
				+ " left outer join Product p on q.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 2 "
				+ " left outer join ProductSpecification as ps1 on ps1.ProductAttributeId = pa1.ProductAttributeId and ps1.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.Sequence = 3 "
				+ " left outer join ProductSpecification as ps2 on ps2.ProductAttributeId = pa2.ProductAttributeId and ps2.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.Sequence = 4 "
				+ " left outer join ProductSpecification as ps3 on ps3.ProductAttributeId = pa3.ProductAttributeId and ps3.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.Sequence = 5 "
				+ " left outer join ProductSpecification as ps4 on ps4.ProductAttributeId = pa4.ProductAttributeId and ps4.ProductId = p.ProductId "
				
				+ " where 1 = 1 " , SystemConstant.MONTH_FORMAT, SystemConstant.DATE_FORMAT, SystemConstant.MONTH_FORMAT);
		
		if (!StringUtils.isEmpty(search)){
			sql += " and (pp.Name like :search or u.Code like :search "
				+ " or u.CPIBasePeriod like :search or q.QuotationId like :search"
//				+ " or concat(u.englishName, ' ' , u.chineseName) like :search "
				//+ " or concat(pa.specificationName , '=' , pa.value) like :search "
				+ " or u.EnglishName like :search or u.ChineseName like :search"
				+ " or pa.SpecificationName like :search or ps.Value like :search "
				+ " or pa1.SpecificationName like :search or ps1.Value like :search "
				+ " or pa2.SpecificationName like :search or ps2.Value like :search "
				+ " or pa3.SpecificationName like :search or ps3.Value like :search "
				+ " or pa4.SpecificationName like :search or ps4.Value like :search "
				+ " or o.Name like :search or b.Code like :search "
				+ " or pf.Name like :search or q.IndoorAllocationCode like :search "
//				+ " or q.seasonalWithdrawal like :search "
				+ " or q.formType like :search "
				+ " or ( "
				+ " case when u.CPIQoutationType = 1 then 'Market' "
				+ " when u.CPIQoutationType = 2 then 'Supermarket' "
				+ " when u.CPIQoutationType = 3 then 'Batch' "
				+ " when u.CPIQoutationType = 4 then 'Others' "
				+ " else '' end ) like :search "
				+ " ) ";
		}
		
		if (purposeId != null) {
			sql += " and pp.PurposeId = :purposeId ";
		}
		/*
		if (unitId != null) {
			sql += " and u.UnitId = :unitId ";
		}
		*/
		if (unitId != null && unitId.size() > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (productId != null) {
			sql += " and p.ProductId = :productId ";
		}
		
		if (firmId != null) {
			sql += " and o.OutletId = :firmId ";
		}
		
		if (batchId != null) {
			sql += " and b.BatchId = :batchId ";
		}
		
		if (pricingFrequencyId != null) {
			sql += " and pf.PricingFrequencyId = :pricingFrequencyId ";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			sql += " and q.Status = :status ";
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			sql += " and q.IndoorAllocationCode like :indoorAllocationCode ";
		}
		
		if (isICP != null) 
		{
			if (isICP == Boolean.TRUE)
				sql += " and q.IsICP = 1 ";
			else 
				sql += " and q.IsICP = 0 ";
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		SQLQuery query = getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (purposeId != null) {
			query.setParameter("purposeId", purposeId);
		}
		/*
		if (unitId != null) {
			query.setParameter("unitId", unitId);
		}
		*/
		if (unitId != null && unitId.size() > 0) {
			query.setParameterList("unitId", unitId);
		}
		
		if (productId != null) {
			query.setParameter("productId", productId);
		}
		
		if (firmId != null) {
			query.setParameter("firmId", firmId);
		}
		
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		
		if (pricingFrequencyId != null) {
			query.setParameter("pricingFrequencyId", pricingFrequencyId);
		}
		
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			query.setParameter("indoorAllocationCode", indoorAllocationCode);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(QuotationTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("englishName", StandardBasicTypes.STRING);
		query.addScalar("chineseName", StandardBasicTypes.STRING);
		
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("productAttribute1", StandardBasicTypes.STRING);
		query.addScalar("productAttribute2", StandardBasicTypes.STRING);
		query.addScalar("productAttribute3", StandardBasicTypes.STRING);
		query.addScalar("productAttribute4", StandardBasicTypes.STRING);
		query.addScalar("productAttribute5", StandardBasicTypes.STRING);
		
		query.addScalar("outletId", StandardBasicTypes.INTEGER);
		query.addScalar("firmName", StandardBasicTypes.STRING);
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		query.addScalar("pricingFrequency", StandardBasicTypes.STRING);
		query.addScalar("Status", StandardBasicTypes.STRING);
		query.addScalar("isICP", StandardBasicTypes.BOOLEAN);
		query.addScalar("indoorAllocationCode", StandardBasicTypes.STRING);
		query.addScalar("cpiCompilationSeries", StandardBasicTypes.STRING);
		query.addScalar("seasonalWithdrawal", StandardBasicTypes.STRING);
		query.addScalar("frAdmin", StandardBasicTypes.DOUBLE);
		query.addScalar("frField", StandardBasicTypes.DOUBLE);
		query.addScalar("lastFRAppliedDate", StandardBasicTypes.STRING);
		query.addScalar("seasonality", StandardBasicTypes.INTEGER);
		query.addScalar("ruaDate", StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType", StandardBasicTypes.STRING);
		
		query.addScalar("formType", StandardBasicTypes.STRING);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<QuotationTableList> getQuotationRUATableList(String search, Integer purposeId, List<Integer> unitId, Integer productId,
			Integer firmId, Integer batchId, Integer pricingFrequencyId, String status,
			Boolean isICP, String indoorAllocationCode, int firstRecord, int displayLenght, Order order,
			boolean todayIsSummerDate, boolean todayIsWinterDate, List<Integer> districtIds, int loginUserId) {
		
		Calendar today = Calendar.getInstance();
		int todayMonth = today.get(Calendar.MONTH) + 1;
		
		String pricingFrequencyCriteria = "";
		switch (todayMonth) {
			case 1:
				pricingFrequencyCriteria += "u.pricingFrequency.isJan = true";
				break;
			case 2:
				pricingFrequencyCriteria += "u.pricingFrequency.isFeb = true";
				break;
			case 3:
				pricingFrequencyCriteria += "u.pricingFrequency.isMar = true";
				break;
			case 4:
				pricingFrequencyCriteria += "u.pricingFrequency.isApr = true";
				break;
			case 5:
				pricingFrequencyCriteria += "u.pricingFrequency.isMay = true";
				break;
			case 6:
				pricingFrequencyCriteria += "u.pricingFrequency.isJun = true";
				break;
			case 7:
				pricingFrequencyCriteria += "u.pricingFrequency.isJul = true";
				break;
			case 8:
				pricingFrequencyCriteria += "u.pricingFrequency.isAug = true";
				break;
			case 9:
				pricingFrequencyCriteria += "u.pricingFrequency.isSep = true";
				break;
			case 10:
				pricingFrequencyCriteria += "u.pricingFrequency.isOct = true";
				break;
			case 11:
				pricingFrequencyCriteria += "u.pricingFrequency.isNov = true";
				break;
			case 12:
				pricingFrequencyCriteria += "u.pricingFrequency.isDec = true";
				break;
		}
		
		String summerCriteria = "";
		String winterCriteria = "";
		String occasionalCriteria = "";
		
		if (todayIsSummerDate) {
			summerCriteria += " 1 = 1 ";
		} else {
			summerCriteria += " 1 = 0 ";
		}

		if (todayIsWinterDate) {
			winterCriteria += " 1 = 1 ";
		} else {
			winterCriteria += " 1 = 0 ";
		}
		
		String districtCriteria = "";
		if (districtIds != null && districtIds.size() > 0) {
			districtCriteria = "or q.district.id in :districtIds";
		}
		
		occasionalCriteria = " (u.seasonStartMonth = :todayMonth "
				+ " or u.seasonEndMonth = :todayMonth "
				+ " or (u.seasonStartMonth < u.seasonEndMonth and u.seasonStartMonth < :todayMonth and u.seasonEndMonth > :todayMonth) "
				+ " or (u.seasonStartMonth > u.seasonEndMonth and (u.seasonStartMonth < :todayMonth or u.seasonEndMonth > :todayMonth)) "
				+ " ) ";

		String hql = String.format("select q.id as id, pp.name as purpose, u.code as unitCode, "
				+ " u.cpiBasePeriod as cpiBasePeriod, "
				+ " u.englishName as englishName, "
				+ " u.chineseName as chineseName, "
				+ " concat(u.englishName, ' ' , u.chineseName) as unitName, "
				+ " p.id as productId, "
				+ " concat(pa.specificationName , '=' , pa.value) as productAttribute1, "
				+ " concat(pa2.specificationName , '=' , pa2.value) as productAttribute2, "
				+ " concat(pa3.specificationName , '=' , pa3.value) as productAttribute3, "
				+ " concat(pa4.specificationName , '=' , pa4.value) as productAttribute4, "
				+ " concat(pa5.specificationName , '=' , pa5.value) as productAttribute5, "
//				+ " u.cpiQoutationType as cpiQuotationType, "
				+ " case when u.cpiQoutationType = 1 then 'Market' "
				+ " when u.cpiQoutationType = 2 then 'Supermarket' "
				+ " when u.cpiQoutationType = 3 then 'Batch' "
				+ " when u.cpiQoutationType = 4 then 'Others' "
				+ " else '' end as cpiQuotationType, "
				+ " o.id as outletId, o.name as firmName, b.code as batchCode, "
				+ " pf.name as pricingFrequency, q.status as Status, q.isICP as isICP, "
				+ " q.indoorAllocationCode as indoorAllocationCode, q.cpiCompilationSeries as cpiCompilationSeries, "
				+ " case when q.seasonalWithdrawal is null then '' else format(q.seasonalWithdrawal, '%s', 'en-us') end as seasonalWithdrawal, "
				+ " q.frAdmin as frAdmin, q.frField as frField, "
				+ " case when q.lastFRAppliedDate is null then '' else format(q.lastFRAppliedDate, '%s', 'en-us') end as lastFRAppliedDate, "
				+ " u.seasonality as seasonality, case when q.ruaDate is null then '' else format(q.ruaDate, '%s', 'en-us') end as ruaDate "
				//+ " u.cpiBasePeriod as cpiBasePeriod"
				+ " from Quotation as q "
				+ " inner join q.unit as u "
				+ " inner join q.batch as b "
				+ " left join q.users as user "
				+ " left outer join q.outlet as o "
				+ " left outer join q.product as p "
				+ " left outer join u.pricingFrequency as pf "
				+ " left outer join u.purpose as pp "
				+ " left outer join p.specificationViews as pa on pa.sequence = 1 "
				+ " left outer join p.specificationViews as pa2 on pa2.sequence = 2 "
				+ " left outer join p.specificationViews as pa3 on pa3.sequence = 3 "
				+ " left outer join p.specificationViews as pa4 on pa4.sequence = 4 "
				+ " left outer join p.specificationViews as pa5 on pa5.sequence = 5 "
				+ " where 1 = 1 "
				+ " and q.status = 'RUA' "
				+ " and (u.pricingFrequency is null or " + pricingFrequencyCriteria + ") "
				+ " and (u.seasonality = 1 "
					+ " or (u.seasonality = 2 and " + summerCriteria + ") "
					+ " or (u.seasonality = 3 and " + winterCriteria + ") "
					+ " or (u.seasonality = 4 and " + occasionalCriteria + ") "
				+ " ) "
				+ " and (q.isRUAAllDistrict = true " + districtCriteria + " or user.id = :loginUserId) "
				, SystemConstant.MONTH_FORMAT, SystemConstant.DATE_FORMAT, SystemConstant.MONTH_FORMAT);
		
		if (!StringUtils.isEmpty(search)){
			hql += " and (pp.name like :search or u.code like :search "
//					+ " or concat(u.englishName, ' ' , u.chineseName) like :search "
					//+ " or concat(pa.specificationName , '=' , pa.value) like :search " 
					+ " or q.quotationId like :search"
					+ " or u.englishName like :search or u.chineseName like :search"
					+ " or pa.specificationName like :search or pa.value like :search "
					+ " or pa2.specificationName like :search or pa2.value like :search "
					+ " or pa3.specificationName like :search or pa3.value like :search "
					+ " or pa4.specificationName like :search or pa4.value like :search "
					+ " or pa5.specificationName like :search or pa5.value like :search "
					+ " or o.name like :search or b.code like :search "
					+ " or pf.name like :search or q.indoorAllocationCode like :search "
//					+ " or q.seasonalWithdrawal like :search "
					+ " ) ";
		}
			
		
		
		if (purposeId != null) {
			hql += " and pp.id = :purposeId ";
		}
		
		if (unitId != null && unitId.size()>0) {
			hql += " and u.id in ( :unitId )";
		}
		
		if (productId != null) {
			hql += " and p.id = :productId ";
		}
		
		if (firmId != null) {
			hql += " and o.id = :firmId ";
		}
		
		if (batchId != null) {
			hql += " and b.id = :batchId ";
		}
		
		if (pricingFrequencyId != null) {
			hql += " and pf.id = :pricingFrequencyId ";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			hql += " and q.status = :status ";
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			hql += " and q.indoorAllocationCode like :indoorAllocationCode ";
		}
		
		if (isICP != null) 
		{
			if (isICP == Boolean.TRUE)
				hql += " and q.isICP is true ";
			else 
				hql += " and q.isICP is false ";
		}
		
		hql += " group by q.id, pp.name, u.code, u.cpiBasePeriod"
				+ ", u.englishName, u.chineseName , p.id, pa.specificationName, pa.value"
				+ ", pa2.specificationName, pa2.value"
				+ ", pa3.specificationName, pa3.value"
				+ ", pa4.specificationName, pa4.value"
				+ ", pa5.specificationName, pa5.value"
				+ ", u.cpiQoutationType, o.id, o.name, b.code, pf.name, q.status"
				+ ", q.isICP, q.indoorAllocationCode, q.cpiCompilationSeries, q.seasonalWithdrawal"
				+ ", q.frAdmin, q.frField, q.lastFRAppliedDate, u.seasonality, q.ruaDate";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);
		
		query.setParameter("todayMonth", todayMonth);
		
		if (districtIds != null && districtIds.size() > 0) {
			query.setParameterList("districtIds", districtIds);
		}
		
		query.setParameter("loginUserId", loginUserId);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (purposeId != null) {
			query.setParameter("purposeId", purposeId);
		}
		
		if (unitId != null && unitId.size()>0) {
			query.setParameterList("unitId", unitId);
		}
		
		if (productId != null) {
			query.setParameter("productId", productId);
		}
		
		if (firmId != null) {
			query.setParameter("firmId", firmId);
		}
		
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		
		if (pricingFrequencyId != null) {
			query.setParameter("pricingFrequencyId", pricingFrequencyId);
		}
		
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			query.setParameter("indoorAllocationCode", indoorAllocationCode);
		}

		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(QuotationTableList.class));

		return query.list();
	}
	
	public List<Quotation> getAllActiveQuotationByBatch(Batch b){
		Criteria criteria = this.createCriteria("q");
		
		criteria.add(Restrictions.eq("q.status", "Active")).add(Restrictions.eq("q.batch", b));
		
		return criteria.list();
	}
	
	public List<Quotation> getAllActiveQuotationWithDependency(Batch b){
		Criteria criteria = this.createCriteria("q");
		criteria.setFetchMode("unit", FetchMode.JOIN);
		criteria.setFetchMode("product", FetchMode.JOIN);
		criteria.setFetchMode("outlet", FetchMode.JOIN);
		criteria.add(Restrictions.eq("q.status", "Active")).add(Restrictions.eq("q.batch", b));
		
		return criteria.list();
	}
	
	public long countQuotationTableList(String search, Integer purposeId, List<Integer> unitId, Integer productId,
			Integer firmId, Integer batchId, Integer pricingFrequencyId, String status,
			Boolean isICP, String indoorAllocationCode) {
		
		String sql = "select count(distinct q.QuotationId) "
				+ " from Quotation q "
				+ " inner join Unit u on q.UnitId = u.UnitId "
				+ " left outer join PricingFrequency pf on u.PricingFrequencyId = pf.PricingFrequencyId "
				+ " left outer join Purpose pp on u.PurposeId = pp.PurposeId "
				+ " inner join Batch b on q.BatchId = b.BatchId "
				+ " left outer join Outlet o on q.OutletId = o.OutletId "
				+ " left outer join Product p on q.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 2 "
				+ " left outer join ProductSpecification as ps1 on ps1.ProductAttributeId = pa1.ProductAttributeId and ps1.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.Sequence = 3 "
				+ " left outer join ProductSpecification as ps2 on ps2.ProductAttributeId = pa2.ProductAttributeId and ps2.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.Sequence = 4 "
				+ " left outer join ProductSpecification as ps3 on ps3.ProductAttributeId = pa3.ProductAttributeId and ps3.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.Sequence = 5 "
				+ " left outer join ProductSpecification as ps4 on ps4.ProductAttributeId = pa4.ProductAttributeId and ps4.ProductId = p.ProductId "
				+ " where 1 = 1 ";
		
		
		if (!StringUtils.isEmpty(search)){
			sql += " and (pp.Name like :search or u.Code like :search "
					+ " or u.CPIBasePeriod like :search or q.QuotationId like :search"
//					+ " or concat(u.englishName, ' ' , u.chineseName) like :search "
					//+ " or concat(pa.specificationName , '=' , pa.value) like :search "
					+ " or u.EnglishName like :search or u.ChineseName like :search"
					+ " or pa.SpecificationName like :search or ps.Value like :search "
					+ " or pa1.SpecificationName like :search or ps1.Value like :search "
					+ " or pa2.SpecificationName like :search or ps2.Value like :search "
					+ " or pa3.SpecificationName like :search or ps3.Value like :search "
					+ " or pa4.SpecificationName like :search or ps4.Value like :search "
					+ " or o.Name like :search or b.Code like :search "
					+ " or pf.Name like :search or q.IndoorAllocationCode like :search "
//					+ " or q.seasonalWithdrawal like :search "
					+ " or q.formType like :search "
					+ " or ( "
					+ " case when u.CPIQoutationType = 1 then 'Market' "
					+ " when u.CPIQoutationType = 2 then 'Supermarket' "
					+ " when u.CPIQoutationType = 3 then 'Batch' "
					+ " when u.CPIQoutationType = 4 then 'Others' "
					+ " else '' end ) like :search "
					+ " ) ";
		}
		
		if (purposeId != null) {
			sql += " and pp.PurposeId = :purposeId ";
		}
		/*
		if (unitId != null) {
			sql += " and u.UnitId = :unitId ";
		}
		*/
		if (unitId != null && unitId.size() > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (productId != null) {
			sql += " and p.ProductId = :productId ";
		}
		
		if (firmId != null) {
			sql += " and o.OutletId = :firmId ";
		}
		
		if (batchId != null) {
			sql += " and b.BatchId = :batchId ";
		}
		
		if (pricingFrequencyId != null) {
			sql += " and pf.PricingFrequencyId = :pricingFrequencyId ";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			sql += " and q.Status = :status ";
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			sql += " and q.IndoorAllocationCode like :indoorAllocationCode ";
		}
		
		if (isICP != null) 
		{
			if (isICP.booleanValue() == true)
				sql += " and q.IsICP = 1 ";
			else 
				sql += " and q.IsICP = 0 ";
		}
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (purposeId != null) {
			query.setParameter("purposeId", purposeId);
		}
		/*
		if (unitId != null) {
			query.setParameter("unitId", unitId);
		}
		*/
		if (unitId != null && unitId.size() > 0) {
			query.setParameterList("unitId", unitId);
		}
		
		if (productId != null) {
			query.setParameter("productId", productId);
		}
		
		if (firmId != null) {
			query.setParameter("firmId", firmId);
		}
		
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		
		if (pricingFrequencyId != null) {
			query.setParameter("pricingFrequencyId", pricingFrequencyId);
		}
		
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			query.setParameter("indoorAllocationCode", indoorAllocationCode);
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}

	public long countQuotationRUATableList(String search, Integer purposeId, List<Integer> unitId, Integer productId,
			Integer firmId, Integer batchId, Integer pricingFrequencyId, String status,
			Boolean isICP, String indoorAllocationCode,
			boolean todayIsSummerDate, boolean todayIsWinterDate, List<Integer> districtIds, int loginUserId) {

		Calendar today = Calendar.getInstance();
		int todayMonth = today.get(Calendar.MONTH) + 1;
		
		String pricingFrequencyCriteria = "";
		switch (todayMonth) {
			case 1:
				pricingFrequencyCriteria += "u.pricingFrequency.isJan = true";
				break;
			case 2:
				pricingFrequencyCriteria += "u.pricingFrequency.isFeb = true";
				break;
			case 3:
				pricingFrequencyCriteria += "u.pricingFrequency.isMar = true";
				break;
			case 4:
				pricingFrequencyCriteria += "u.pricingFrequency.isApr = true";
				break;
			case 5:
				pricingFrequencyCriteria += "u.pricingFrequency.isMay = true";
				break;
			case 6:
				pricingFrequencyCriteria += "u.pricingFrequency.isJun = true";
				break;
			case 7:
				pricingFrequencyCriteria += "u.pricingFrequency.isJul = true";
				break;
			case 8:
				pricingFrequencyCriteria += "u.pricingFrequency.isAug = true";
				break;
			case 9:
				pricingFrequencyCriteria += "u.pricingFrequency.isSep = true";
				break;
			case 10:
				pricingFrequencyCriteria += "u.pricingFrequency.isOct = true";
				break;
			case 11:
				pricingFrequencyCriteria += "u.pricingFrequency.isNov = true";
				break;
			case 12:
				pricingFrequencyCriteria += "u.pricingFrequency.isDec = true";
				break;
		}
		
		String summerCriteria = "";
		String winterCriteria = "";
		String occasionalCriteria = "";
		
		if (todayIsSummerDate) {
			summerCriteria += " 1 = 1 ";
		} else {
			summerCriteria += " 1 = 0 ";
		}

		if (todayIsWinterDate) {
			winterCriteria += " 1 = 1 ";
		} else {
			winterCriteria += " 1 = 0 ";
		}
		
		String districtCriteria = "";
		if (districtIds != null && districtIds.size() > 0) {
			districtCriteria = "or q.district.id in :districtIds";
		}
		
		occasionalCriteria = " (u.seasonStartMonth = :todayMonth "
				+ " or u.seasonEndMonth = :todayMonth "
				+ " or (u.seasonStartMonth < u.seasonEndMonth and u.seasonStartMonth < :todayMonth and u.seasonEndMonth > :todayMonth) "
				+ " or (u.seasonStartMonth > u.seasonEndMonth and (u.seasonStartMonth < :todayMonth or u.seasonEndMonth > :todayMonth)) "
				+ " ) ";

		String hql = "select count(distinct q.id) "
				+ " from Quotation as q "
				+ " inner join q.unit as u "
				+ " inner join q.batch as b "
				+ " left join q.users as user "
				+ " left outer join q.outlet as o "
				+ " left outer join q.product as p "
				+ " left outer join u.pricingFrequency as pf "
				+ " left outer join u.purpose as pp "
				+ " left outer join p.specificationViews as pa on pa.sequence = 1"
				+ " left outer join p.specificationViews as pa2 on pa2.sequence = 2 "
				+ " left outer join p.specificationViews as pa3 on pa3.sequence = 3 "
				+ " left outer join p.specificationViews as pa4 on pa4.sequence = 4 "
				+ " left outer join p.specificationViews as pa5 on pa5.sequence = 5 "
				+ " where 1 = 1 "
				+ " and q.status = 'RUA' "
				+ " and (u.pricingFrequency is null or " + pricingFrequencyCriteria + ") "
				+ " and (u.seasonality = 1 "
					+ " or (u.seasonality = 2 and " + summerCriteria + ") "
					+ " or (u.seasonality = 3 and " + winterCriteria + ") "
					+ " or (u.seasonality = 4 and " + occasionalCriteria + ") "
				+ " ) "
				+ " and (q.isRUAAllDistrict = true " + districtCriteria + " or user.id = :loginUserId) ";
				
				
		if (!StringUtils.isEmpty(search)){
			hql += " and (pp.name like :search or u.code like :search "
//					+ " or concat(u.englishName, ' ' , u.chineseName) like :search "
					//+ " or concat(pa.specificationName , '=' , pa.value) like :search " 
					+ " or q.quotationId like :search"
					+ " or u.englishName like :search or u.chineseName like :search "
					+ " or pa.specificationName like :search or pa.value like :search "
					+ " or pa2.specificationName like :search or pa2.value like :search "
					+ " or pa3.specificationName like :search or pa3.value like :search "
					+ " or pa4.specificationName like :search or pa4.value like :search "
					+ " or pa5.specificationName like :search or pa5.value like :search "
					+ " or o.name like :search or b.code like :search "
					+ " or pf.name like :search or q.indoorAllocationCode like :search "
//					+ " or q.seasonalWithdrawal like :search "
					+ " ) ";
		}
				
		
		if (purposeId != null) {
			hql += " and pp.id = :purposeId ";
		}
		
		if (unitId != null && unitId.size()>0) {
			hql += " and u.id in ( :unitId )";
		}
		
		if (productId != null) {
			hql += " and p.id = :productId ";
		}
		
		if (firmId != null) {
			hql += " and o.id = :firmId ";
		}
		
		if (batchId != null) {
			hql += " and b.id = :batchId ";
		}
		
		if (pricingFrequencyId != null) {
			hql += " and pf.id = :pricingFrequencyId ";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			hql += " and q.status = :status ";
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			hql += " and q.indoorAllocationCode like :indoorAllocationCode ";
		}
		
		if (isICP != null) 
		{
			if (isICP.booleanValue() == true)
				hql += " and q.isICP is true ";
			else 
				hql += " and q.isICP is false ";
		}
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("todayMonth", todayMonth);
		
		if (districtIds != null && districtIds.size() > 0) {
			query.setParameterList("districtIds", districtIds);
		}
		
		query.setParameter("loginUserId", loginUserId);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (purposeId != null) {
			query.setParameter("purposeId", purposeId);
		}
		
		if (unitId != null && unitId.size()>0) {
			query.setParameterList("unitId", unitId);
		}
		
		if (productId != null) {
			query.setParameter("productId", productId);
		}
		
		if (firmId != null) {
			query.setParameter("firmId", firmId);
		}
		
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		
		if (pricingFrequencyId != null) {
			query.setParameter("pricingFrequencyId", pricingFrequencyId);
		}
		
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}	
		
		if (StringUtils.isNotEmpty(indoorAllocationCode)) {
			query.setParameter("indoorAllocationCode", indoorAllocationCode);
		}
		
		Long count = (Long) query.uniqueResult();
		
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> searchIndoorAllocationCode(String search, int firstRecord, int displayLength){
		
		Criteria critera = this.createCriteria("quotation");
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.distinct(Projections.property("quotation.indoorAllocationCode")), "indoorAllocationCode");

        critera.setProjection(projList);
        
        if (StringUtils.isNotEmpty(search)) {
	        critera.add(Restrictions.like("quotation.indoorAllocationCode", "%"+search+"%"));
        }
        
        critera.setFirstResult(firstRecord);
        critera.setMaxResults(displayLength);
        critera.addOrder(Order.asc("quotation.indoorAllocationCode"));
        
        return critera.list();
	}

	public long countSearchIndoorAllocationCode (String search){
		
		Criteria critera = this.createCriteria("quotation");
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.countDistinct("quotation.indoorAllocationCode"));

        critera.setProjection(projList);
        
        if (StringUtils.isNotEmpty(search)) {
	        critera.add(Restrictions.like("quotation.indoorAllocationCode", "%"+search+"%"));
        }

        return (long) critera.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Quotation> getQuotationByIds(List<Integer> ids) {
		
		Criteria criteria = this.createCriteria("q");
		criteria.add(Restrictions.in("quotationId", ids));
		return criteria.list();
	}
	
	public long validateOutletType(Integer unitId, Integer outletId) {
		
		String hql = "select count(distinct u.id ) "
				+ " from Unit as u "
				+ " join u.subItem as si"
				+ " where u.unitId = :unitId "
				+ " and substring(si.outletType.code, length(si.outletType.code)-2, 3) in "
				+ " ( select ot.shortCode from Outlet o join o.outletTypes ot where o.outletId = :outletId) ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("unitId", unitId);
		query.setParameter("outletId", outletId);
		
		Long result = (Long) query.uniqueResult();
		return result == null ? 0 : result;
	}
	
	public Double getQuotationLoadingByUnitIdOutletId(Integer unitId, Integer outletId) {
	
		String hql = "select ql.quotationPerManDay "
				+ " from QuotationLoading as ql"
				+ " join ql.outletTypes ot "
				+ " where ot.shortCode = ( "
				+ "  select substring(si.outletType.code, length(si.outletType.code)-2, 3) from "
				+ "  Unit as u join u.subItem as si where u.unitId = :unitId "
				+ " ) and ql.district.districtId = ( "
				+ "  select tpu.district.districtId from Outlet o join o.tpu tpu where o.outletId = :outletId "
				+ " ) ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("unitId", unitId);
		query.setParameter("outletId", outletId);
		
		return (Double) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<RUASettingTableList> selectAllRUASetting(String search, int firstRecord, int displayLength, Order order
														, Integer purposeId, Integer unitId, Integer productId, Integer outletId
														, Integer batchId, Integer pricingFrequencyId, Integer cpiQoutationType) {

		String ruaDate = String.format("FORMAT(q.ruaDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = " select q.quotationId as quotationId "
				+ ", concat(str(purpose.purposeId), ' - ', purpose.code, ' ', purpose.name) as purpose "
				+ ", u.code as unitCode "
				+ ", u.chineseName as unitName "
				+ ", p.productId as productId "
				+ ", concat(vps.specificationName, ', ', vps.value) as productAttribute "
				+ ", o.firmCode as firmId "
				+ ", o.name as firmName "
				+ ", b.code as batchCode "
				+ ", pf.name as pricingFrequency "
				+ ", case "
				+ " when u.cpiQoutationType is null then '' "
				+ " when u.cpiQoutationType = 1 then :cpiQoutationType1 "
				+ " when u.cpiQoutationType = 2 then :cpiQoutationType2 "
				+ " when u.cpiQoutationType = 3 then :cpiQoutationType3 "
				+ " else :cpiQoutationType4 end as cpiFormType "
				+ ", case "
				+ "	when q.ruaDate is null then '' "
				+ " else " + ruaDate + " end  as ruaDate "
				+ " from Quotation as q "
				+ " left join q.batch as b "
				+ " left join q.outlet as o "
				+ " left join q.unit as u "
				+ " left join q.product as p "
				+ " left join u.purpose as purpose "
				+ " left join u.pricingFrequency as pf"
				+ " left join p.fullSpecifications vps on vps.sequence = 1" 
				+ " where 1=1 and q.status = 'RUA' ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( str(q.quotationId) like :search or u.code like :search or u.chineseName like :search "
				+ " or o.firmCode like :search or o.name like :search or b.code like :search or pf.name like :search "
//				+ " or ( "
//				+ "   case "
//				+ "   when u.cpiQoutationType is null then '' "
//				+ "   when u.cpiQoutationType = 1 then :cpiQoutationType1 "
//				+ "   when u.cpiQoutationType = 2 then :cpiQoutationType2 "
//				+ "   when u.cpiQoutationType = 3 then :cpiQoutationType3 "
//				+ "   else :cpiQoutationType4 end ) like :search "
//				+ " ) "
				+ " or ( "
				+ "   case "
				+ "   when q.ruaDate is null then '' "
				+ "   else " + ruaDate + " end ) like :search "
				+ " ) ";
		}
		
		if (purposeId != null) hql += " and purpose.purposeId = :purposeId ";
		
		if (unitId != null) hql += " and u.unitId = :unitId ";
		
		if (productId != null) hql += " and p.productId = :productId ";

		if (outletId != null) hql += " and o.outletId = :outletId ";
		
		if (batchId != null) hql += " and b.batchId = :batchId ";
		
		if (pricingFrequencyId != null) hql += " and pf.pricingFrequencyId = :pricingFrequencyId ";
		
		if (cpiQoutationType != null) hql += " and u.cpiQoutationType = :cpiQoutationType ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("cpiQoutationType1", SystemConstant.CPI_QOUTATION_TYPE_1);
		query.setParameter("cpiQoutationType2", SystemConstant.CPI_QOUTATION_TYPE_2);
		query.setParameter("cpiQoutationType3", SystemConstant.CPI_QOUTATION_TYPE_3);
		query.setParameter("cpiQoutationType4", SystemConstant.CPI_QOUTATION_TYPE_4);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		if (purposeId != null) query.setParameter("purposeId", purposeId);
		
		if (unitId != null) query.setParameter("unitId", unitId);
		
		if (productId != null) query.setParameter("productId", productId);
		
		if (outletId != null) query.setParameter("outletId", outletId);
		
		if (batchId != null) query.setParameter("batchId", batchId);
		
		if (pricingFrequencyId != null) query.setParameter("pricingFrequencyId", pricingFrequencyId);
		
		if (cpiQoutationType != null) query.setParameter("cpiQoutationType", cpiQoutationType);

		query.setResultTransformer(Transformers.aliasToBean(RUASettingTableList.class));

		return query.list();
	}

	public long countSelectAllRUASetting(String search, Integer purposeId, Integer unitId, Integer productId, Integer outletId
										, Integer batchId, Integer pricingFrequencyId, Integer cpiQoutationType) {

		String ruaDate = String.format("FORMAT(q.ruaDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = " select count(*) as cnt "
				+ " from Quotation as q "
				+ " left join q.batch as b "
				+ " left join q.outlet as o "
				+ " left join q.unit as u "
				+ " left join q.product as p "
				+ " left join u.purpose as purpose "
				+ " left join u.pricingFrequency as pf"
				+ " left join p.specificationViews vps on vps.sequence = 1 "
				+ " where 1=1 and q.status = 'RUA' ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( str(q.quotationId) like :search or u.code like :search or u.chineseName like :search "
				+ " or o.firmCode like :search or o.name like :search or b.code like :search or pf.name like :search "
//				+ " or ( "
//				+ "   case "
//				+ "   when u.cpiQoutationType is null then '' "
//				+ "   when u.cpiQoutationType = 1 then :cpiQoutationType1 "
//				+ "   when u.cpiQoutationType = 2 then :cpiQoutationType2 "
//				+ "   when u.cpiQoutationType = 3 then :cpiQoutationType3 "
//				+ "   else :cpiQoutationType4 end ) like :search "
//				+ " ) "
				+ " or ( "
				+ "   case "
				+ "   when q.ruaDate is null then '' "
				+ "   else " + ruaDate + " end ) like :search "
				+ " ) ";
		}
		
		if (purposeId != null) hql += " and purpose.purposeId = :purposeId ";
		
		if (unitId != null) hql += " and u.unitId = :unitId ";
		
		if (productId != null) hql += " and p.productId = :productId ";
		
		if (outletId != null) hql += " and o.outletId = :outletId ";
		
		if (batchId != null) hql += " and b.batchId = :batchId ";
		
		if (pricingFrequencyId != null) hql += " and pf.pricingFrequencyId = :pricingFrequencyId ";
		
		if (cpiQoutationType != null) hql += " and u.cpiQoutationType = :cpiQoutationType ";

		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
//			query.setParameter("cpiQoutationType1", SystemConstant.CPI_QOUTATION_TYPE_1);
//			query.setParameter("cpiQoutationType2", SystemConstant.CPI_QOUTATION_TYPE_2);
//			query.setParameter("cpiQoutationType3", SystemConstant.CPI_QOUTATION_TYPE_3);
//			query.setParameter("cpiQoutationType4", SystemConstant.CPI_QOUTATION_TYPE_4);
		}
		
		if (purposeId != null) query.setParameter("purposeId", purposeId);
		
		if (unitId != null) query.setParameter("unitId", unitId);
		
		if (productId != null) query.setParameter("productId", productId);
		
		if (outletId != null) query.setParameter("outletId", outletId);
		
		if (batchId != null) query.setParameter("batchId", batchId);
		
		if (pricingFrequencyId != null) query.setParameter("pricingFrequencyId", pricingFrequencyId);
		
		if (cpiQoutationType != null) query.setParameter("cpiQoutationType", cpiQoutationType);

		return (long)query.uniqueResult();
	}

	public RUASettingEditModel selectRUASettingRowById(Integer quotationId) {

		String ruaDate = String.format("FORMAT(q.ruaDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select u.code as unitCode "
				+ ", u.chineseName as unitName "
				+ ", p.productId as productId "
				+ ", vps.specificationName as productAttribute "
				+ ", o.name as firmName "
				+ ", b.code as batchCode "
				+ ", case "
				+ "	when q.ruaDate is null then '' "
				+ " else " + ruaDate + " end  as ruaDate "
				+ ", q.isRUAAllDistrict as isRUAAllDistrict "
				+ ", d.districtId as districtId "
				+ " from Quotation as q "
				+ " left join q.batch as b "
				+ " left join q.outlet as o "
				+ " left join q.unit as u "
				+ " left join q.product as p "
				+ " left join p.fullSpecifications vps on vps.sequence = 1 "
				+ " left join q.district d "
				+ " where 1=1 ";
		
		if (quotationId != null) hql += " and q.quotationId = :quotationId ";
		
		Query query = this.getSession().createQuery(hql);
		
		if (quotationId != null) query.setParameter("quotationId", quotationId);
		
		query.setResultTransformer(Transformers.aliasToBean(RUASettingEditModel.class));
		
		return (RUASettingEditModel)query.uniqueResult();
	}
	
	public List<ExportQuotationsUnitProductOutletList> getAllQuotation(){
		//String ruaDate = String.format("FORMAT(q.ruaDate, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
				
		String hql = "Select q.quotationId as quotationId"
				+ ", pu.code as purposeCode"
				+ ", u.code as unitCode"
				+ ", u.cpiBasePeriod as cpiBasePeriod"
				+ ", u.englishName as unitEnglishName"
				+ ", u.chineseName as unitChineseName"
				+ ", o.firmCode as firmCode"
				+ ", o.name as outletName"
				+ ", p.productId as productId"
				+ ", b.code as batchCode"
				+ ", q.cpiCompilationSeries as cpiCompilationSeries"
				+ ", q.formType as formType"
				+ ", q.isICP as isICP"
				+ ", q.icpProductCode as icpProductCode"
				+ ", q.icpProductName as icpProductName"
				+ ", q.icpType as icpType"
				+ ", q.oldFormBarSerial as oldFormBarSerial"
				+ ", q.oldFormSequence as oldFormSequence"
				+ ", q.status as quotationStatus"
				+ ", q.ruaDate as ruaDate"
				+ ", q.isRUAAllDistrict as isRUAAllDistrict"
				+ ", d.code as districtCode"
				+ ", q.quotationLoading as quotationLoading"
				+ ", q.keepNoMonth as keepNoMonth"
				+ ", q.lastProductChangeDate as lastProductChangeDate"
				+ " from Quotation as q"
				+ " left join q.unit as u"
				+ " left join u.purpose as pu"
				+ " left join q.outlet as o"
				+ " left join q.product as p"
				+ " left join q.batch as b"
				+ " left join q.district as d";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportQuotationsUnitProductOutletList.class));
		System.out.println(query);
		return query.list();
	}

	public void updateQuotationUnitByRebasing(Integer oldUnitId, Integer newUnitId, Integer quotationId, Integer newProductId){
		String sql = "UPDATE [Quotation] SET [OldUnitId] = :oldUnitId , [NewUnitId] = :newUnitId , [newProductId] = :newProductId , [ModifiedDate] = getDate() where [quotationId] = :quotationId ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("oldUnitId", oldUnitId);
		query.setParameter("newUnitId", newUnitId);
		query.setParameter("newProductId", newProductId == null ? null : newProductId);
		query.setParameter("quotationId", quotationId);
		
		query.executeUpdate();
	}
	
	public List<ImportRebasingQuotationList> getAllQuotationByRebasing(List<Integer> oldUnitIds){
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.unit", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.product", "p", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.productCategory", "g", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("q.quotationId"), "quotationId");
		projList.add(Projections.property("u.unitId"), "unitId");
		projList.add(Projections.property("p.productId"), "productId");
		projList.add(Projections.property("g.productGroupId"), "productGroupId");
		
		criteria.add(Restrictions.in("u.unitId", oldUnitIds));
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ImportRebasingQuotationList.class));
		return criteria.list();
	}
	
	
	public List<Quotation> getQuotationsByProductIds(List<Integer> productIds){
		Criteria criteria = this.createCriteria("q");
		criteria.add(Restrictions.in("q.product.productId", productIds));
		
		return criteria.list();
	}
	
	/**
	 * Data Sync
	 */
	
	public List<QuotationSyncData> getUpdateRUAQuotation(Date lastSyncTime, List<Integer> quotationIds){
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.outlet", "o", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.unit", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.product", "p", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.batch", "b", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.oldUnit", "oldu", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.district", "d", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.newUnit", "newu", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.newProduct", "newp", JoinType.LEFT_OUTER_JOIN);
		
		if(quotationIds!=null&&quotationIds.size()>0){
			criteria.add(Restrictions.or
					(
						Restrictions.eq("q.status", "RUA"),
						Restrictions.in("q.quotationId", quotationIds)
					));
			
		} else {
			criteria.add(Restrictions.eq("q.status", "RUA"));
		}
		criteria.add(Restrictions.ge("q.modifiedDate", lastSyncTime));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("q.quotationId"), "quotationId");
		projList.add(Projections.property("o.outletId"), "outletId");
		projList.add(Projections.property("u.unitId"), "unitId");
		projList.add(Projections.property("q.createdDate"), "createdDate");
		projList.add(Projections.property("q.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("q.status"), "status");
		projList.add(Projections.property("p.productId"), "productId");
		projList.add(Projections.property("b.batchId"), "batchId");
		projList.add(Projections.property("q.quotationLoading"), "quotationLoading");
		projList.add(Projections.property("q.indoorAllocationCode"), "indoorAllocationCode");
		projList.add(Projections.property("q.isICP"), "isICP");
		projList.add(Projections.property("q.cpiCompilationSeries"), "cpiCompilationSeries");
		projList.add(Projections.property("q.oldFormBarSerial"), "oldFormBarSerial");
		projList.add(Projections.property("q.oldFormSequence"), "oldFormSequence");
		projList.add(Projections.property("q.frAdmin"), "frAdmin");
		projList.add(Projections.property("q.frField"), "frField");
		projList.add(Projections.property("q.isUseFRAdmin"), "isUseFRAdmin");
		projList.add(Projections.property("q.seasonalWithdrawal"), "seasonalWithdrawal");
		projList.add(Projections.property("q.lastFRAppliedDate"), "lastFRAppliedDate");
		projList.add(Projections.property("q.isFRApplied"), "isFRApplied");
		projList.add(Projections.property("q.isReturnGoods"), "isReturnGoods");
		projList.add(Projections.property("q.isReturnNewGoods"), "isReturnNewGoods");
		projList.add(Projections.property("oldu.unitId"), "oldUnitId");
		projList.add(Projections.property("q.usedFRValue"), "usedFRValue");
		projList.add(Projections.property("q.isUsedFRPercentage"), "isUsedFRPercentage");
		projList.add(Projections.property("q.isFRAdminPercentage"), "isFRAdminPercentage");
		projList.add(Projections.property("q.isFRFieldPercentage"), "isFRFieldPercentage");
		projList.add(Projections.property("q.ruaDate"), "ruaDate");
		projList.add(Projections.property("q.isRUAAllDistrict"), "isRUAAllDistrict");
		projList.add(Projections.property("d.districtId"), "districtId");
		projList.add(Projections.property("q.icpProductCode"), "icpProductCode");
		projList.add(Projections.property("newu.unitId"), "newUnitId");
		projList.add(Projections.property("q.productPosition"), "productPosition");
		projList.add(Projections.property("q.productRemark"), "productRemark");
		projList.add(Projections.property("q.lastProductChangeDate"), "lastProductChangeDate");
		projList.add(Projections.property("q.tempIsFRApplied"), "tempIsFRApplied");
		projList.add(Projections.property("q.tempIsReturnGoods"), "tempIsReturnGoods");
		projList.add(Projections.property("q.tempIsReturnNewGoods"), "tempIsReturnNewGoods");
		projList.add(Projections.property("q.tempLastFRAppliedDate"), "tempLastFRAppliedDate");
		projList.add(Projections.property("newp.productId"), "newProductId");
		projList.add(Projections.property("q.tempIsUseFRAdmin"), "tempIsUseFRAdmin");
		projList.add(Projections.property("q.keepNoMonth"), "keepNoMonth");
		projList.add(Projections.property("q.tempKeepNoMonth"), "tempKeepNoMonth");
		projList.add(Projections.property("q.lastSeasonReturnGoods"), "lastSeasonReturnGoods");
		projList.add(Projections.property("q.formType"), "formType");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(QuotationSyncData.class));
		return criteria.list();
	}
	
	public List<RUAUserSyncData> getUpdateRUAUser(Date lastSyncTime){
		String sql = "Select a.quotationId as quotationId, a.userId as userId"
				+ ", a.createdDate as createdDate, a.modifiedDate as modifiedDate "
				+ "from RUAUser a ";
//				+ "where a.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("quotationId", StandardBasicTypes.INTEGER)
				.addScalar("userId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		
//		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(RUAUserSyncData.class));
		
		return query.list();
	}
	
	public List<Integer> getAllIdsByAssignment(Integer[] assignmentIds){
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN)
				.createAlias("qr.assignment", "a", JoinType.INNER_JOIN)
				.setProjection(Projections.property("q.quotationId"))
				.add(Restrictions.in("a.assignmentId", assignmentIds));
		
		return criteria.list();
	}
	
	public List<QuotationSyncData> getUpdatedDownloadedQuotation(Date lastSyncTime, Integer[] quotationIds, Integer[] assignmentIds){
		String hql = "select q.quotationId as quotationId"
				+ ", o.outletId as outletId, ut.unitId as unitId"
				+ ", q.createdDate as createdDate, q.modifiedDate as modifiedDate"
				+ ", q.status as status, p.productId as productId"
				+ ", b.batchId as batchId, q.quotationLoading as quotationLoading"
				+ ", q.indoorAllocationCode as indoorAllocationCode, q.isICP as isICP"
				+ ", q.cpiCompilationSeries as cpiCompilationSeries, q.oldFormBarSerial as oldFormBarSerial"
				+ ", q.oldFormSequence as oldFormSequence, q.frAdmin as frAdmin"
				+ ", q.frField as frField, q.isUseFRAdmin as isUseFRAdmin"
				+ ", q.seasonalWithdrawal as seasonalWithdrawal, q.lastFRAppliedDate as lastFRAppliedDate"
				+ ", q.isFRApplied as isFRApplied, q.isReturnGoods as isReturnGoods"
				+ ", q.isReturnNewGoods as ReturnNewGoods, oldut.unitId as oldUnitId" 
				+ ", q.usedFRValue as usedFRValue, q.isUsedFRPercentage as isUsedFRPercentage"
				+ ", q.isFRAdminPercentage as isFRAdminPercentage, q.isFRFieldPercentage as isFRFieldPercentage"
				+ ", q.ruaDate as ruaDate, q.isRUAAllDistrict as isRUAAllDistrict"
				+ ", d.districtId as districtId, q.icpProductCode as icpProductCode"
				+ ", newut.unitId as newUnitId, q.productPosition as productPosition"
				+ ", q.productRemark as productRemark, q.lastProductChangeDate as lastProductChangeDate"
				+ ", q.tempIsFRApplied as tempIsFRApplied, q.tempIsReturnGoods as tempIsReturnGoods"
				+ ", q.tempIsReturnNewGoods as tempIsReturnNewGoods, q.tempLastFRAppliedDate as tempLastFRAppliedDate"
				+ ", newp.productId as newProductId, q.tempIsUseFRAdmin as tempIsUseFRAdmin"
				+ ", q.keepNoMonth as keepNoMonth, q.tempKeepNoMonth as tempKeepNoMonth"
				+ ", q.lastSeasonReturnGoods as lastSeasonReturnGoods, q.tempFRValue as tempFRValue"
				+ ", q.tempFRPercentage as tempFRPercentage"
				+ ", q.formType as formType"
				+ " from Quotation as q"
				+ " left join q.outlet as o"
				+ " left join q.unit as ut"
				+ " left join q.product as p"
				+ " left join q.batch as b"
				+ " left join q.oldUnit as oldut"
				+ " left join q.district as d"
				+ " left join q.newUnit as newut"
				+ " left join q.newProduct as newp"
				+ " left join q.quotationRecords as qr"
				+ " left join qr.assignment as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and q.modifiedDate >= :modifiedDate";
		}
		
		hql += " and (";
		
		if(quotationIds!=null && quotationIds.length>0){
			hql += " q.quotationId in ( :quotationIds )";
		}
		
		if(quotationIds!=null && quotationIds.length>0 && assignmentIds!=null && assignmentIds.length>0){
			hql += " or";
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " a.assignmentId in ( :assignmentIds )";
		}
		
		hql += " )";
		
		hql += " group by q.quotationId"
				+ ", o.outletId, ut.unitId"
				+ ", q.createdDate, q.modifiedDate"
				+ ", q.status, p.productId"
				+ ", b.batchId, q.quotationLoading"
				+ ", q.indoorAllocationCode, q.isICP"
				+ ", q.cpiCompilationSeries, q.oldFormBarSerial"
				+ ", q.oldFormSequence, q.frAdmin"
				+ ", q.frField, q.isUseFRAdmin"
				+ ", q.seasonalWithdrawal, q.lastFRAppliedDate"
				+ ", q.isFRApplied, q.isReturnGoods"
				+ ", q.isReturnNewGoods, oldut.unitId"
				+ ", q.usedFRValue, q.isUsedFRPercentage"
				+ ", q.isFRAdminPercentage, q.isFRFieldPercentage"
				+ ", q.ruaDate, q.isRUAAllDistrict"
				+ ", d.districtId, q.icpProductCode"
				+ ", newut.unitId, q.productPosition"
				+ ", q.productRemark, q.lastProductChangeDate"
				+ ", q.tempIsFRApplied, q.tempIsReturnGoods"
				+ ", q.tempIsReturnNewGoods, q.tempLastFRAppliedDate"
				+ ", newp.productId, q.tempIsUseFRAdmin"
				+ ", q.keepNoMonth, q.tempKeepNoMonth"
				+ ", q.lastSeasonReturnGoods, q.tempFRValue"
				+ ", q.tempFRPercentage"
				+ ", q.formType";
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		if(quotationIds!=null && quotationIds.length>0){
			query.setParameterList("quotationIds", quotationIds);
		}
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(QuotationSyncData.class));
		return query.list();
	}
	
	public List<Quotation> getNoFieldQuotaionByAssignmentAttribute(Integer attrId){
		String hql = "select q "
				+ " from AssignmentAttribute as attr "
				+ " inner join attr.batch as b "
				+ " inner join b.quotations as q on q.status != 'Inactive' "
				+ " left join q.indoorQuotationRecords as iqr "
				+ " where iqr.indoorQuotationRecordId is null and attr.assignmentAttributeId = :attrId ";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("attrId", attrId);
		
		return query.list();
	}
	
	public List<Quotation> getActiveQuotation(){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ne("status", "Inactive"));
		return criteria.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getQuotationIdsByQuotationStatisticsReportByQuotation(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm) {
		Criteria criteria = this.createCriteria()
				.createAlias("unit", "u")
				.createAlias("u.purpose", "p");
		
		if (purpose != null && purpose.size() > 0)
			criteria.add(Restrictions.in("p.purposeId", purpose));
		
		if (unitId != null && unitId.size() > 0)
			criteria.add(Restrictions.in("u.unitId", unitId));
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0)
			criteria.add(Restrictions.in("u.cpiQoutationType", cpiSurveyForm));
		
		criteria.setProjection(Projections.groupProperty("quotationId"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> searchCpiCompilationSeries(String search, int firstRecord, int displayLength) {
		Criteria critera = this.createCriteria()
				.setProjection(Projections.distinct(Projections.property("cpiCompilationSeries")))
				.setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("cpiCompilationSeries"));

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("{alias}.cpiCompilationSeries LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return critera.list();
	}

	public long countSearchCpiCompilationSeries(String search) {
		Criteria critera = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("{alias}.cpiCompilationSeries LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return (long) critera.setProjection(Projections.countDistinct("cpiCompilationSeries")).uniqueResult();
	}
	
	public List<String> searchAllICPType(String search, int firstRecord, int displayRecord){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(Projections.groupProperty("icpType"), "icpType");
		criteria.setProjection(list);
		
		if(!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.like("icpType",  "%"+search+"%"));
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
	
	public List<String> searchAllICPProductCode(String search, int firstRecord, int displayRecord){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(Projections.groupProperty("icpProductCode"), "icpProductCode");
		criteria.setProjection(list);
		
		if(!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.like("icpProductCode", "%"+search+"%"));
		}
		
		criteria.setFirstResult(firstRecord).setMaxResults(displayRecord);
		criteria.addOrder(Order.asc("icpProductCode"));
		
		return criteria.list();
	}
	
	public long countICPProductCode(String search){
		Criteria criteria = this.createCriteria();
		ProjectionList list = Projections.projectionList();
		list.add(SQLProjectionExt.groupCount(Projections.groupProperty("icpProductCode")));
		criteria.setProjection(list);
		
		if(!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.like("icpProductCode", "%"+search+"%"));
		}
		
		return (long)criteria.uniqueResult();
	}
	
	public List<SummaryOfICPQuotationsByICPCodeReport> countQuotationByICPTypeAndCode(List<String> icpTypes, List<String> icpProductCodes, Date fromMonth, Date toMonth){
	String sql = "SELECT icpType, icpProductCode, icpProductName, icpQuotationCount, icpOutletCount, cpiQuotationCount, cpiOutletCount, purpose FROM ("
				+ "SELECT DISTINCT ISNULL(q.icpType, '') as icpType"
				+ ", q.icpProductCode as icpProductCode"
				+ ", q.icpProductName as icpProductName"
//				+ ", count(DISTINCT icp.quotationId) as icpQuotationCount"
//				+ ", count(DISTINCT icp.outletId) as icpOutletCount"
//				+ ", count(DISTINCT cpi.quotationId) as cpiQuotationCount"
//				+ ", count(DISTINCT cpi.outletId) as cpiOutletCount"
				//+ ", DENSE_RANK() over (partition by q.icpProductCode order by icp.outletId) + DENSE_RANK() over (partition by q.icpProductCode order by icp.outletId desc) - 1 as icpOutletCount "
				//V13
				+ ", DENSE_RANK() over (partition BY q.icpProductCode ORDER BY o.outletId) + DENSE_RANK() over (partition BY q.icpProductCode ORDER BY o.outletId DESC) - 1 AS icpOutletCount"
				+ " , CASE WHEN p.Code = 'ICP' THEN DENSE_RANK() over (partition BY q.icpProductCode, p.Code ORDER BY q.quotationId) + DENSE_RANK() over (partition BY q.icpProductCode, p.Code "
				+ " ORDER BY q.quotationId DESC) - 1 ELSE DENSE_RANK() over (partition BY q.icpProductCode, p.Code ORDER BY q.quotationId) + DENSE_RANK() over (partition BY q.icpProductCode, p.Code "
				+ " ORDER BY q.quotationId DESC) - 1 END AS cpiQuotationCount "
				+ " , CASE WHEN p.Code = 'ICP' THEN DENSE_RANK() over (partition BY q.icpProductCode, p.Code ORDER BY  o.FirmCode) + DENSE_RANK() over (partition BY q.icpProductCode, p.Code "
				+ " ORDER BY  o.FirmCode DESC) - 1 ELSE DENSE_RANK() over (partition BY q.icpProductCode, p.Code ORDER BY  o.FirmCode) + DENSE_RANK() over (partition BY q.icpProductCode, p.Code "
				+ " ORDER BY o.FirmCode DESC) - 1 END AS cpiOutletCount "		
				
				+ ", DENSE_RANK() over (partition by q.icpProductCode order by icp.quotationId) + DENSE_RANK() over (partition by q.icpProductCode order by icp.quotationId desc) - 1 as icpQuotationCount "
				//+ ", count(cpi.quotationId) over ( PARTITION BY q.icpProductCode, p.Code)  AS cpiQuotationCount "
				//+ ", count(cpi.outletId) over ( PARTITION BY q.icpProductCode, p.Code) AS cpiOutletCount "
//				+ " ,case when p.Code = 'ICP' Then "
//				+ " DENSE_RANK() over (partition by q.icpProductCode order by cpi.quotationId ) + DENSE_RANK() over (partition by q.icpProductCode order by cpi.quotationId desc) - 1 "
//				+ " else"
//				+ " DENSE_RANK() over (partition by q.icpProductCode, p.Code order by cpi.quotationId ) + DENSE_RANK() over (partition by q.icpProductCode, p.Code order by cpi.quotationId desc) - 1 "
//				+ " end as cpiQuotationCount "


//				+ " ,case when p.Code = 'ICP' Then "
//				+ " DENSE_RANK() over (partition by q.icpProductCode order by cpi.outletId ) + DENSE_RANK() over (partition by q.icpProductCode order by cpi.outletId desc) - 1 "
//				+ " else "
//				+ " DENSE_RANK() over (partition by q.icpProductCode, p.Code order by cpi.outletId ) + DENSE_RANK() over (partition by q.icpProductCode, p.Code order by cpi.outletId desc) - 1"
//				+ " end as cpiOutletCount "
				+ ", p.Code as purpose "
				+ " from Quotation as q"
				+ " inner join Unit as u on u.unitId = q.unitId"
				+ " inner join Purpose as p on u.purposeId = p.purposeId"
				+ " inner join QuotationRecord as qr on qr.quotationId = q.quotationId"
				+ " INNER join [Outlet] as o on qr.outletId = o.outletId"				
				+ " left join Product as pd on pd.ProductId = qr.ProductId "
				+ " inner join Assignment as a on a.assignmentId = qr.assignmentId"
				+ " inner join SurveyMonth as sm on a.surveyMonthId = sm.surveyMonthId"
				+ " left join quotation as icp on q.quotationId = icp.quotationId"
					//+ " and p.code = :purposeCode"
				+ " left join quotation as cpi on q.quotationId = cpi.quotationId"
					//+ " and p.code != :purposeCode"
					+ " and cpi.isIcp = :isICP"
					+ " and q.[status] = :status"
					+ " where sm.referenceMonth >= :fromMonth"
					+ " and sm.referenceMonth <= :toMonth"
					+ " and q.icpProductCode IS NOT NULL"
					+ " and qr.IsBackNo = 0"
					//+ " and cpi.OutletId is not null"
					//+ " and cpi.QuotationId is not null"
					+ " and q.IsICP = 1 ";				
				
		if(icpTypes!=null && icpTypes.size()>0){
			sql += " and q.icpType in (:icpTypes)"; 
		}
		if(icpProductCodes!=null && icpProductCodes.size()>0){
			sql += " and q.icpProductCode in (:icpProductCodes)";
		}
		
		sql += " group by ISNULL(q.icpType ,''), q.icpProductCode, q.icpProductName, p.Code"
				+ " ,icp.quotationId, icp.outletId, cpi.quotationId,"
				+ " q.quotationId, o.outletId, o.FirmCode";
//				+ "cpi.outletId";				
		
		sql += ") A";
		
		sql += " group by A.icpType, A.icpProductCode, A.icpProductName, A.icpOutletCount, A.icpQuotationCount, A.icpOutletCount, A.cpiQuotationCount, A.cpiOutletCount, A.purpose ";
		
		sql += " order by A.icpProductCode, A.icpType, A.purpose ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("icpType", StandardBasicTypes.STRING)
				.addScalar("icpProductCode", StandardBasicTypes.STRING)
				.addScalar("icpProductName", StandardBasicTypes.STRING)
				.addScalar("icpOutletCount", StandardBasicTypes.LONG)
				.addScalar("icpQuotationCount", StandardBasicTypes.LONG)
				.addScalar("cpiOutletCount", StandardBasicTypes.LONG)
				.addScalar("cpiQuotationCount", StandardBasicTypes.LONG)
				.addScalar("purpose",StandardBasicTypes.STRING);
		
		//query.setParameter("purposeCode", "ICP");
		query.setParameter("status", "Active");
		query.setParameter("isICP", true);
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		if(icpTypes!=null && icpTypes.size()>0){
			query.setParameterList("icpTypes", icpTypes);
		}
		if(icpProductCodes!=null && icpProductCodes.size()>0){
			query.setParameterList("icpProductCodes", icpProductCodes);
		}
				
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfICPQuotationsByICPCodeReport.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Quotation> search(String search, int firstRecord, int displayLength,
			Integer[] purposeId, Integer[] unitId) {
		// to be modified
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.unit", "u", JoinType.INNER_JOIN)
				.createAlias("u.purpose", "p", JoinType.LEFT_OUTER_JOIN)
				.setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("quotationId"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("str({alias}.quotationId) LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		if (purposeId != null && purposeId.length > 0) {
			criteria.add(Restrictions.in("p.purposeId", purposeId));
		}
		if (unitId != null && unitId.length > 0) {
			criteria.add(Restrictions.in("u.unitId", unitId));
		}
		
		return criteria.list();
	}
	
	public long countSearch(String search,
			Integer[] purposeId, Integer[] unitId) {
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.unit", "u", JoinType.INNER_JOIN)
				.createAlias("u.purpose", "p", JoinType.LEFT_OUTER_JOIN);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("str({alias}.quotationId) LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		if (purposeId != null && purposeId.length > 0) {
			criteria.add(Restrictions.in("p.purposeId", purposeId));
		}
		if (unitId != null && unitId.length > 0) {
			criteria.add(Restrictions.in("u.unitId", unitId));
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	//Wtith cpiSurveyForm
	@SuppressWarnings("unchecked")
	public List<Quotation> searchWithcpiSurveyForm(String search, int firstRecord, int displayLength,
			Integer[] purposeId, Integer[] unitId,  Integer[] cpiSurveyForm) {
		// to be modified
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.unit", "u", JoinType.INNER_JOIN)
				.createAlias("u.purpose", "p", JoinType.LEFT_OUTER_JOIN)
				.setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("quotationId"));
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("str({alias}.quotationId) LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
					);
		}
		
		if (purposeId != null && purposeId.length > 0) {
			criteria.add(Restrictions.in("p.purposeId", purposeId));
		}
		if (unitId != null && unitId.length > 0) {
			criteria.add(Restrictions.in("u.unitId", unitId));
		}
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			criteria.add(Restrictions.in("u.cpiQoutationType", cpiSurveyForm));
		}
		return criteria.list();
	}
	
	public long countSearchWithcpiSurveyForm(String search,
			Integer[] purposeId, Integer[] unitId,  Integer[] cpiSurveyForm) {
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.unit", "u", JoinType.INNER_JOIN)
				.createAlias("u.purpose", "p", JoinType.LEFT_OUTER_JOIN);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("str({alias}.quotationId) LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
					);
		}
		
		if (purposeId != null && purposeId.length > 0) {
			criteria.add(Restrictions.in("p.purposeId", purposeId));
		}
		if (unitId != null && unitId.length > 0) {
			criteria.add(Restrictions.in("u.unitId", unitId));
		}
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
		criteria.add(Restrictions.in("u.cpiQoutationType", cpiSurveyForm));
	}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	/**
	 * Get Quotation include Assingment search select2 format
	 */
	@SuppressWarnings("unchecked")
	public List<QuotationLookupTableList> search(String search, int firstRecord, int displayLength,
			Integer[] purposeId, Integer[] unitId, Integer[] assignmentIds) {
		// to be modified
		String sql = "select q.QuotationId as id "
                + " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join quotationRecord as qr on qr.QuotationId = q.QuotationId "
                + " left outer join Assignment as a on a.AssignmentId = qr.AssignmentId "
                + " where 1 = 1 ";

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (assignmentIds != null && assignmentIds.length > 0) {
			sql += " and a.assignmentId in (:assignmentIds) ";
		}
		
		 if (StringUtils.isNotEmpty(search)) {
	        sql += " and ( "
	        		+ " q.QuotationId like :search  "
	        		+ " ) ";
	    }
		 
		sql += " group by q.QuotationId ";
		
		//sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
          sql += " order by q.quotationId";
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		/*if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}*/
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
	    }
		
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}
		if (assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(QuotationLookupTableList.class));
		
		
		return query.list();
	}

	public long countSearch(String search,
			Integer[] purposeId, Integer[] unitId, Integer[] assignmentIds) {
		String sql = "select count(distinct q.QuotationId) as id "
                + " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join quotationRecord as qr on qr.QuotationId = q.QuotationId "
                + " left outer join Assignment as a on a.AssignmentId = qr.AssignmentId "
                + " where 1 = 1 ";

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (assignmentIds != null && assignmentIds.length > 0) {
			sql += " and a.assignmentId in (:assignmentIds) ";
		}
		
		 if (StringUtils.isNotEmpty(search)) {
		        sql += " and ( "
		        		+ " q.QuotationId like :search  "
		        		+ " ) ";
		    }
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		/*if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}*/
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
	    }
		
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}
		if (assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		Integer count = (Integer)query.uniqueResult();
		return (long) (count == null ? 0 : count);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Quotation> getAllRUAQuotations(Date startDate, Date endDate,
			List<String> teams, List<Integer> userIds, List<Integer> districtIds) {
		Criteria c = this.createCriteria("q")
				.createAlias("q.quotationRecords", "qr")
				.createAlias("qr.assignment", "a")
				.createAlias("a.user", "u")
				.createAlias("q.outlet", "o")
				.createAlias("o.tpu", "tpu")
				.createAlias("tpu.district", "district");
		
		c.add(Restrictions.eq("q.status", "RUA"));
		c.add(Restrictions.between("qr.referenceDate", startDate, endDate));
		
		if (teams != null && teams.size() > 0) {
			c.add(Restrictions.in("u.team", teams));
		}
		
		if (userIds != null && userIds.size() > 0) {
			c.add(Restrictions.in("u.id", userIds));
		}
		
		if (districtIds != null && districtIds.size() > 0) {
			c.add(Restrictions.in("district.id", districtIds));
		}
		
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return c.list();
	}
	
	public List<NoFieldQuotationResult> generateNoFieldIndoorQuotationRecord(Date refMonth){
		String sql = "exec GenerateNoFieldIndoorQuotationRecord :month";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("month", refMonth);
		
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("recordDate", StandardBasicTypes.DATE);
		query.addScalar("referenceMonth", StandardBasicTypes.DATE);
		query.addScalar("backTrack1", StandardBasicTypes.DATE);
		query.addScalar("backTrack2", StandardBasicTypes.DATE);
		query.addScalar("backTrack3", StandardBasicTypes.DATE);
		
		
		query.setResultTransformer(Transformers.aliasToBean(NoFieldQuotationResult.class));
		
		return query.list();
	}
	
	public List<Integer> getNoFieldQuotationIds(Date referenceMonth){
		String sql = "exec GetNoFieldQuotationRecords :month";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("month", referenceMonth);
		
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		return query.list();
	}
	
	public List<Integer> getExistingQuotationIds(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<Quotation> getQuotationWithUnitExpired() {
		
		String status = "Active";
		Date today = new Date();
		
		String hql = "select q "
				+ " from Quotation as q "
				+ " left join q.unit as u "
				+ " where 1 = 1 and q.status = :status "
				+ " and u.obsoleteDate <= :obsoleteDate ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("status", status);
		query.setParameter("obsoleteDate", today);
		
		return query.list();
	}
	
	public boolean getFrRequiredByQuotationId(Integer quotationId) {
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.unit", "u", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("q.quotationId", quotationId));
		
		criteria.setProjection(Projections.property("u.frRequired"));
		
		return (boolean)criteria.uniqueResult();
	}
	
	public List<QuotationSyncData> getHistoryQuotationByAssignmentIds(List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT q.* FROM QuotationRecord qr, Quotation q, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.QuotationId = q.QuotationId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<QuotationSyncData> result1 = addScalarForQuotation(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(QuotationSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT q.* FROM QuotationRecord qr, Quotation q, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.QuotationId = q.QuotationId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<QuotationSyncData> result2 = addScalarForQuotation(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(QuotationSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<QuotationSyncData> returnResult = new ArrayList<QuotationSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<QuotationSyncData> getHistoryQuotationByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT q.* FROM QuotationRecord qr, Quotation q, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.QuotationId = q.QuotationId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<QuotationSyncData> result1 = addScalarForQuotation(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(QuotationSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT q.* FROM QuotationRecord qr, Quotation q, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.QuotationId = q.QuotationId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<QuotationSyncData> result2 = addScalarForQuotation(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(QuotationSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<QuotationSyncData> returnResult = new ArrayList<QuotationSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public SQLQuery addScalarForQuotation(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("quotationId", StandardBasicTypes.INTEGER)
		.addScalar("outletId", StandardBasicTypes.INTEGER).addScalar("unitId", StandardBasicTypes.INTEGER)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP).addScalar("status", StandardBasicTypes.STRING)
		.addScalar("productId", StandardBasicTypes.INTEGER).addScalar("batchId", StandardBasicTypes.INTEGER)
		.addScalar("quotationLoading", StandardBasicTypes.DOUBLE)
		.addScalar("indoorAllocationCode", StandardBasicTypes.STRING)
		.addScalar("isICP", StandardBasicTypes.BOOLEAN)
		.addScalar("cpiCompilationSeries", StandardBasicTypes.STRING)
		.addScalar("oldFormBarSerial", StandardBasicTypes.STRING)
		.addScalar("oldFormSequence", StandardBasicTypes.STRING).addScalar("frAdmin", StandardBasicTypes.DOUBLE)
		.addScalar("frField", StandardBasicTypes.DOUBLE).addScalar("isUseFRAdmin", StandardBasicTypes.BOOLEAN)
		.addScalar("seasonalWithdrawal", StandardBasicTypes.TIMESTAMP)
		.addScalar("lastFRAppliedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("isFRApplied", StandardBasicTypes.BOOLEAN)
		.addScalar("isReturnGoods", StandardBasicTypes.BOOLEAN)
		.addScalar("isReturnNewGoods", StandardBasicTypes.BOOLEAN)
		.addScalar("oldUnitId", StandardBasicTypes.INTEGER).addScalar("usedFRValue", StandardBasicTypes.DOUBLE)
		.addScalar("isUsedFRPercentage", StandardBasicTypes.BOOLEAN)
		.addScalar("isFRAdminPercentage", StandardBasicTypes.BOOLEAN)
		.addScalar("isFRFieldPercentage", StandardBasicTypes.BOOLEAN)
		.addScalar("ruaDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("isRUAAllDistrict", StandardBasicTypes.BOOLEAN)
		.addScalar("districtId", StandardBasicTypes.INTEGER)
		.addScalar("icpProductCode", StandardBasicTypes.STRING)
		.addScalar("newUnitId", StandardBasicTypes.INTEGER)
		.addScalar("productPosition", StandardBasicTypes.STRING)
		.addScalar("productRemark", StandardBasicTypes.STRING)
		.addScalar("lastProductChangeDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("tempIsFRApplied", StandardBasicTypes.BOOLEAN)
		.addScalar("tempIsReturnGoods", StandardBasicTypes.BOOLEAN)
		.addScalar("tempIsReturnNewGoods", StandardBasicTypes.BOOLEAN)
		.addScalar("tempLastFRAppliedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("newProductId", StandardBasicTypes.INTEGER)
		.addScalar("tempIsUseFRAdmin", StandardBasicTypes.BOOLEAN)
		.addScalar("keepNoMonth", StandardBasicTypes.INTEGER)
		.addScalar("tempKeepNoMonth", StandardBasicTypes.INTEGER)
		.addScalar("lastSeasonReturnGoods", StandardBasicTypes.BOOLEAN)
		.addScalar("tempFRValue", StandardBasicTypes.DOUBLE)
		.addScalar("tempFRPercentage", StandardBasicTypes.BOOLEAN)
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		.addScalar("formType", StandardBasicTypes.STRING).addScalar("icpType", StandardBasicTypes.STRING)
		.addScalar("icpProductName", StandardBasicTypes.STRING);
		
		return sqlQuery;
	}	
	
	public List<Integer> getNoIndoorRUAQuotationByReference(Date ReferenceMonth){
		String sql = " Select q.quotationId"
				+ " from Quotation as q"
				+ " left join IndoorQuotationRecord as indoor on q.QuotationId = indoor.QuotationId"
					+ " and indoor.ReferenceMonth = :referenceMonth"
				+ " where q.status = :status "
				+ " group by q.QuotationId"
				+ " having count(indoor.IndoorQuotationRecordId) = 0";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("referenceMonth", ReferenceMonth);
		query.setParameter("status", "RUA");
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		
		return query.list();
	}
	
	public List<String> getRUAUserStaffCodeByQuotationId(int id){
		String hql = "Select u.staffCode"
				+ " from Quotation as q"
				+ " left join q.users as u"
				+ " where q.quotationId = :id";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("id", id);
		
		return query.list();
	}

	public List<Integer> getLookupTableSelectAllWithCPISurveyForm(String search, Integer[] purposeId,
			String[] outletTypeId, String[] cpiSurveyForm) {
		String sql = "select q.quotationId as quotationId "
				+ " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1=1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			sql += " and u.CPIQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " group by q.quotationId ";
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);

		return (List<Integer>)query.list();
	}

	public List<QuotationLookupTableList> getLookupTableListWithCPISurveyForm(String search, int firstRecord, int displayLength,
			Order order, Integer[] purposeId, String[] outletTypeId, Integer[] unitId, String[] cpiSurveyForm) {
		String sql = "select q.QuotationId as id, "
				+ " purpose.Survey as survey, "
				+ " u.Code as unitCode, "
				+ " u.DisplayName as unitName, "
				+ " p.ProductId as productId, "
                + " concat(pa.SpecificationName, '=', ps.Value) as productAttribute, "
                + " o.FirmCode as firmCode, "
                + " o.Name as firmName "
                + " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1 = 1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			sql += " and u.CPIQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " group by q.QuotationId, purpose.Survey, u.Code, u.DisplayName, p.ProductId,"
				+ " pa.SpecificationName, ps.Value, "
				+ " o.FirmCode, o.Name";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(QuotationLookupTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("productAttribute", StandardBasicTypes.STRING);
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("firmName", StandardBasicTypes.STRING);

		return query.list();
	}

	public Long countLookupTableListWithCPISurveyForm(String search, Integer[] purposeId, String[] outletTypeId,
			Integer[] unitId, String[] cpiSurveyForm) {
		String sql = "select count(distinct q.QuotationId) "
				+ " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1=1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			sql += " and u.CPIQoutationType in (:cpiSurveyForm) ";
		}
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.length > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}

		Integer count = (Integer)query.uniqueResult();
		return (long) (count == null ? 0 : count);
	}
	
	public List<Integer> getLookupTableSelectAllWithAssignmentIds(String search, Integer[] purposeId,
			String[] outletTypeId, Integer[] assignmentIds) {
					
		String sql = "select q.quotationId as quotationId "
				+ " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join quotationRecord as qr on qr.QuotationId = q.QuotationId "
                + " left outer join Assignment as a on a.AssignmentId = qr.AssignmentId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1=1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
				
		if (assignmentIds != null && assignmentIds.length > 0) {
			sql += " and a.assignmentId in (:assignmentIds) ";
		}
		
		sql += " group by q.quotationId ";
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);		
		return (List<Integer>)query.list();
	}

	public List<QuotationLookupTableList> getLookupTableListWithAssignmentIds(String search, int firstRecord, int displayLength,
			Order order, Integer[] purposeId, String[] outletTypeId, Integer[] unitId, Integer[] assignmentIds) {
		String sql = "select q.QuotationId as id, "
				+ " purpose.Survey as survey, "
				+ " u.Code as unitCode, "
				+ " u.DisplayName as unitName, "
				+ " p.ProductId as productId, "
                + " concat(pa.SpecificationName, '=', ps.Value) as productAttribute, "
                + " o.FirmCode as firmCode, "
                + " o.Name as firmName "
                + " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join quotationRecord as qr on qr.QuotationId = q.QuotationId "
                + " left outer join Assignment as a on a.AssignmentId = qr.AssignmentId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1 = 1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (assignmentIds != null && assignmentIds.length > 0) {
			sql += " and a.assignmentId in (:assignmentIds) ";
		}
		
		sql += " group by q.QuotationId, purpose.Survey, u.Code, u.DisplayName, p.ProductId,"
				+ " pa.SpecificationName, ps.Value, "
				+ " o.FirmCode, o.Name";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}
		if (assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(QuotationLookupTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("productAttribute", StandardBasicTypes.STRING);
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("firmName", StandardBasicTypes.STRING);

		return query.list();
	}

	public Long countLookupTableListWithAssignmentIds(String search, Integer[] purposeId, String[] outletTypeId,
			Integer[] unitId, Integer[] assignmentIds) {
		String sql = "select count(distinct q.QuotationId) "
				+ " from Quotation as q "
                + " inner join Unit as u on q.UnitId = u.UnitId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join quotationRecord as qr on qr.QuotationId = q.QuotationId "
                + " left outer join Assignment as a on a.AssignmentId = qr.AssignmentId "
                + " left outer join Product as p on q.ProductId = p.ProductId "
                + " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
                + " left outer join Outlet as o on q.OutletId = o.OutletId "
                + " left outer join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
                + " left outer join vwOutletTypeShortForm as t on oto.ShortCode = t.ShortCode "
                + " where 1=1 ";
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and (str(q.QuotationId) like :search "
        			+ " or purpose.Survey like :search "
        			+ " or u.Code like :search "
        			+ " or u.DisplayName like :search "
        			+ " or str(p.ProductId) like :search "
        			+ " or pa.SpecificationName like :search "
        			+ " or ps.Value like :search "
        			+ " or str(o.FirmCode) like :search "
        			+ " or o.Name like :search)";
        }

		if (purposeId != null && purposeId.length > 0) {
			sql += " and purpose.PurposeId in (:purposeId) ";
		}

		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and left(right(u.Code, 5), 3) in (:outletTypeId) ";
		}
		
		if (unitId != null && unitId.length > 0) {
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if (assignmentIds != null && assignmentIds.length > 0) {
			sql += " and a.assignmentId in (:assignmentIds) ";
		}
                
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null && unitId.length > 0) {
			query.setParameterList("unitId", unitId);
		}
		if (assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}

		Integer count = (Integer)query.uniqueResult();
	    System.out.println("Total quotation : " + count);
		return (long) (count == null ? 0 : count);
	}
}
