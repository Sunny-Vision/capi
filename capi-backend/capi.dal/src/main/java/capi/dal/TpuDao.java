package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Tpu;
import capi.model.api.dataSync.TpuSyncData;
import capi.model.masterMaintenance.TpuEditModel;
import capi.model.masterMaintenance.TpuTableList;

@Repository("TpuDao")
public class TpuDao  extends GenericDao<Tpu>{
	@SuppressWarnings("unchecked")
	public List<Tpu> searchTpuByDistrict(String search, int firstRecord, int displayLength, Integer[] districtId) {
		Criteria critera = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("code"));//.addOrder(Order.asc("description"));

		if (!StringUtils.isEmpty(search)) {
//			critera.add(
//					Restrictions.sqlRestriction("{alias}.code+' - '+description LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
//				);
			critera.add(Restrictions.like("code", "%" + search + "%"));
		}
		
		if (districtId != null && districtId.length > 0) {
			critera.add(Restrictions.in("district.districtId", districtId));
		}
		
		return critera.list();
	}

	public long countSearchTpuByDistrict(String search, Integer[] districtId) {
		Criteria critera = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
//			critera.add(
//					Restrictions.sqlRestriction("str({alias}.code)+' - '+description LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
//				);
//			
			critera.add(Restrictions.like("code", "%" + search + "%"));
		}
		
		if (districtId != null && districtId.length > 0) {
			critera.add(Restrictions.in("district.districtId", districtId));
		}
		
		return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<TpuTableList> selectAllTpu(String search, int firstRecord, int displayLength, Order order, Integer districtId) {

		Criteria critera = this.createCriteria("t")
										.createAlias("t.district", "d", JoinType.INNER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("t.tpuId"), "tpuId");
		projList.add(Projections.property("t.code"), "code");
		projList.add(Projections.property("d.code"), "districtCode");
		projList.add(Projections.property("d.chineseName"), "districtChineseName");
		projList.add(Projections.property("d.englishName"), "districtEnglishName");
		projList.add(Projections.property("t.councilDistrict"), "councilDistrict");
		
		critera.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			critera.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("d.code", "%" + search + "%"),
					Restrictions.like("d.chineseName", "%" + search + "%"),
					Restrictions.like("d.englishName", "%" + search + "%"),
					Restrictions.like("councilDistrict", "%" + search + "%")
			));
		}

		if (districtId != null && districtId != 0) {
        	critera.add(Restrictions.eq("d.districtId", districtId));
        }

		critera.setFirstResult(firstRecord);
		critera.setMaxResults(displayLength);
        critera.addOrder(order);

		critera.setResultTransformer(Transformers.aliasToBean(TpuTableList.class));

		return critera.list();
	}

	public long countSelectAllTpu(String search, Integer districtId) {

		Criteria criteria = this.createCriteria("t")
										.createAlias("t.district", "d", JoinType.INNER_JOIN);
//	
//		no projection is required since no grouping is needed
		
//		ProjectionList projList = Projections.projectionList();
//		projList.add(Projections.property("t.tpuId"), "tpuId");
//		projList.add(Projections.property("t.code"), "code");
//		projList.add(Projections.property("d.code"), "districtCode");
//		projList.add(Projections.property("d.chineseName"), "districtChineseName");
//		projList.add(Projections.property("d.englishName"), "districtEnglishName");
//		projList.add(Projections.property("t.councilDistrict"), "councilDistrict");
//		
//		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
				Restrictions.like("code", "%" + search + "%"),
				Restrictions.like("d.code", "%" + search + "%"),
				Restrictions.like("d.chineseName", "%" + search + "%"),
				Restrictions.like("d.englishName", "%" + search + "%"),
				Restrictions.like("councilDistrict", "%" + search + "%")
			));
		}
		
		if (districtId != null && districtId != 0) {
        	criteria.add(Restrictions.eq("d.districtId", districtId));
        }
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		
//		ScrollableResults result = criteria.scroll();
//		result.last();
//		int total = result.getRowNumber() + 1;
//		result.close();
//		
//		return total;
//		//return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Tpu> getAll() {
		return this.createCriteria().addOrder(Order.asc("code")).list();
	}

	public List<TpuEditModel> getAllTpuCode() {
		Criteria criteria = this.createCriteria("t");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("t.code"), "code");
		
		criteria.setProjection(projList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(TpuEditModel.class));
		
		return criteria.list();
	}
	
	public List<Tpu> getTpuByDistrictIds(List<Integer> ids){
		Criteria criteria = this.createCriteria("t")
				.createAlias("t.district", "d");
		criteria.add(Restrictions.in("d.districtId", ids));
		return criteria.list();
	}
	
	public List<Tpu> getTpusByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("tpuId", ids));
		return criteria.list();
	}
	
	public Tpu getTpuByCode(String tpuCode){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", tpuCode));
		return (Tpu)criteria.uniqueResult();
	}
	
	public ScrollableResults getAllTPUResult(){
		Criteria criteria = this.createCriteria("t").createAlias("t.district", "d", JoinType.FULL_JOIN);;
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<Tpu> getNotExistedTpu(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("tpuId", ids)));
		return criteria.list();		
	}

	public List<Integer> getExistingTpuId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<Tpu> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("tpuId", ids));
		return criteria.list();
	}
	
	public List<TpuSyncData> getUpdatedTpus(Date lastSyncTime){
		Criteria criteria = this.createCriteria("t")
				.createAlias("t.district", "d");
		criteria.add(Restrictions.ge("t.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("t.tpuId"), "tpuId");
		projList.add(Projections.property("t.code"), "code");
		projList.add(Projections.property("d.districtId"), "districtId");
		projList.add(Projections.property("t.createdDate"), "createdDate");
		projList.add(Projections.property("t.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("t.description"), "description");
		projList.add(Projections.property("t.councilDistrict"), "councilDistrict");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(TpuSyncData.class));
		
		return criteria.list();		
	}
}
