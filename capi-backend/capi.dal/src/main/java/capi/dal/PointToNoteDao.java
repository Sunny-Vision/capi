package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.PointToNote;
import capi.model.SystemConstant;
import capi.model.api.dataSync.PointToNoteOutletSyncData;
import capi.model.api.dataSync.PointToNoteProductSyncData;
import capi.model.api.dataSync.PointToNoteQuotationSyncData;
import capi.model.api.dataSync.PointToNoteSyncData;
import capi.model.api.dataSync.PointToNoteUnitSyncData;
import capi.model.masterMaintenance.PointToNoteTableList;

@Repository("PointToNoteDao")
public class PointToNoteDao extends GenericDao<PointToNote>{

	@SuppressWarnings("unchecked")
	public List<PointToNoteTableList> getTableList(String search,
			int firstRecord, int displayLength, Order order) {
		
//		Criteria criteria = this.createCriteria("n")
//				.createAlias("n.quotations","q", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("n.units","u", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("n.outlets","o", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("n.products","p", JoinType.LEFT_OUTER_JOIN)
//				.setFirstResult(firstRecord)
//				.setMaxResults(displayLength).addOrder(order);
//
//		String effectiveDate = String.format("FORMAT({alias}.effectiveDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		String expiryDate = String.format("FORMAT({alias}.expiryDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		String createdDate = String.format("FORMAT({alias}.createdDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		
//		criteria.setProjection(
//				Projections.projectionList()
//				.add(Projections.groupProperty("id"), "id")
//				.add(Projections.sqlGroupProjection(effectiveDate + " as effectiveDate", "{alias}.effectiveDate", new String [] {"effectiveDate"}, new Type[]{StandardBasicTypes.STRING}), "effectiveDate")
//				.add(Projections.sqlGroupProjection(expiryDate + " as expiryDate", "{alias}.expiryDate", new String [] {"expiryDate"}, new Type[]{StandardBasicTypes.STRING}), "expiryDate")
//				.add(Projections.sqlGroupProjection(createdDate + " as createdDate", "{alias}.createdDate", new String [] {"createdDate"}, new Type[]{StandardBasicTypes.STRING}), "createdDate")
//				.add(Projections.groupProperty("n.createdBy"), "createdBy")
//				.add(Projections.countDistinct("q.id"), "noOfQuotation")
//				.add(Projections.countDistinct("u.id"), "noOfUnit")
//				.add(Projections.countDistinct("o.id"), "noOfFirm")
//				.add(Projections.countDistinct("p.id"), "noOfProduct")
//				.add(Projections.groupProperty("n.note"), "pointToNote")
//			);
//		
//		if (!StringUtils.isEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.sqlRestriction(
//							effectiveDate + " LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//							,
//					Restrictions.sqlRestriction(
//							expiryDate + " LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//					,
//					Restrictions.sqlRestriction(
//							createdDate + " LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//					,
//					Restrictions.sqlRestriction(
//							"{alias}.createdBy LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//					,
//					Restrictions.sqlRestriction(
//							"{alias}.note LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//							));
//		}
//
//		criteria.setResultTransformer(Transformers.aliasToBean(PointToNoteTableList.class));
//		
//		return criteria.list();
		
		String sql = "exec dbo.GetPointToNoteTableList :search, :orderBy, :order, :date_format";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("search", StringUtils.isEmpty(search)?null:"%"+search+"%");
		query.setParameter("orderBy", order.getPropertyName());
		query.setParameter("order", order.isAscending()? "asc":"desc");
		query.setParameter("date_format", SystemConstant.DATE_FORMAT);
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("effectiveDate", StandardBasicTypes.STRING);
		query.addScalar("expiryDate", StandardBasicTypes.STRING);
		query.addScalar("createdDate", StandardBasicTypes.STRING);
		query.addScalar("createdBy", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("noOfUnit", StandardBasicTypes.LONG);
		query.addScalar("noOfFirm", StandardBasicTypes.LONG);
		query.addScalar("noOfProduct", StandardBasicTypes.LONG);
		query.addScalar("pointToNote", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(PointToNoteTableList.class));
		
		return query.list();
		
	}

	public long countTableList(String search) {
		String effectiveDate = String.format("FORMAT({alias}.effectiveDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String expiryDate = String.format("FORMAT({alias}.expiryDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String createdDate = String.format("FORMAT({alias}.createdDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		
		Criteria criteria = this.createCriteria();
				
		criteria.setProjection(Projections.countDistinct("id"));
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.sqlRestriction(
							effectiveDate + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
							,
					Restrictions.sqlRestriction(
							expiryDate + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
					,
					Restrictions.sqlRestriction(
							createdDate + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
					,
					Restrictions.sqlRestriction(
							"{alias}.createdBy LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
					,
					Restrictions.sqlRestriction(
							"{alias}.note LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
							));
		}
		
		return (long)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<PointToNote> findByIds(ArrayList<Integer> ids) {
		Criteria criteria = this.createCriteria("n").add(Restrictions.in("id", ids));
		return criteria.list();
	}
	
	public List<PointToNoteSyncData> getUpdatePointToNote(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("pointToNoteId"), "pointToNoteId");
		projList.add(Projections.property("note"), "note");
		projList.add(Projections.property("effectiveDate"), "effectiveDate");
		projList.add(Projections.property("expiryDate"), "expiryDate");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		projList.add(Projections.property("isAllOutlet"), "isAllOutlet");
		projList.add(Projections.property("isAllProduct"), "isAllProduct");
		projList.add(Projections.property("isAllQuotation"), "isAllQuotation");
		projList.add(Projections.property("isAllUnit"), "isAllUnit");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(PointToNoteSyncData.class));
		return criteria.list();
	}
	
	public List<PointToNoteOutletSyncData> getUpdatePointToNoteOutlet(Date lastSyncTime){
		String sql = "Select outlet.pointToNoteId as pointToNoteId, "
				+ "outlet.outletId as outletId, "
				+ "outlet.createdDate as createdDate, "
				+ "outlet.modifiedDate as modifiedDate "
				+ "from PointToNoteOutlet outlet where "
				+ "outlet.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("pointToNoteId", StandardBasicTypes.INTEGER)
				.addScalar("outletId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(PointToNoteOutletSyncData.class));
		
		return query.list();
	}
	
	public List<PointToNoteProductSyncData> getUpdatePointToNoteProduct(Date lastSyncTime){
		String sql = "Select product.pointToNoteId as pointToNoteId, "
				+ "product.productId as productId, "
				+ "product.createdDate as createdDate, "
				+ "product.modifiedDate as modifiedDate "
				+ "from PointToNoteProduct product where "
				+ "product.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("pointToNoteId", StandardBasicTypes.INTEGER)
				.addScalar("productId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(PointToNoteProductSyncData.class));
		
		return query.list();
	}
	
	public List<PointToNoteQuotationSyncData> getUpdatePointToNoteQuotation(Date lastSyncTime){
		String sql = "Select quotation.pointToNoteId as pointToNoteId, "
				+ "quotation.quotationId as quotationId, "
				+ "quotation.createdDate as createdDate, "
				+ "quotation.modifiedDate as modifiedDate "
				+ "from PointToNoteQuotation quotation where "
				+ "quotation.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("pointToNoteId", StandardBasicTypes.INTEGER)
				.addScalar("quotationId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(PointToNoteQuotationSyncData.class));
		
		return query.list();
	}
	
	public List<PointToNoteUnitSyncData> getUpdatePointToNoteUnit(Date lastSyncTime){
		String sql = "Select unit.pointToNoteId as pointToNoteId, "
				+ "unit.unitId as unitId, "
				+ "unit.createdDate as createdDate, "
				+ "unit.modifiedDate as modifiedDate "
				+ "from PointToNoteUnit unit where "
				+ "unit.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("pointToNoteId", StandardBasicTypes.INTEGER)
				.addScalar("unitId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(PointToNoteUnitSyncData.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PointToNote> getBySelectAll() {
		return this.createCriteria()
				.add(Restrictions.or(Restrictions.eq("isAllOutlet", true), Restrictions.eq("isAllProduct", true), Restrictions.eq("isAllQuotation", true), Restrictions.eq("isAllUnit", true)))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<PointToNote> getBySelectAllOutlet() {
		return this.createCriteria()
				.add(Restrictions.eq("isAllOutlet", true))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<PointToNote> getBySelectAllProductQuotationUnit() {
		return this.createCriteria()
				.add(Restrictions.or(Restrictions.eq("isAllProduct", true), Restrictions.eq("isAllQuotation", true), Restrictions.eq("isAllUnit", true)))
				.list();
	}
}
