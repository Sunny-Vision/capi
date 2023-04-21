package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import capi.entity.AllocationBatch;
import capi.entity.DistrictHeadAdjustment;

@Repository("DistrictHeadAdjustmentDao")
public class DistrictHeadAdjustmentDao extends GenericDao<DistrictHeadAdjustment>{
	public List<DistrictHeadAdjustment> findDistrictHeadAdjustmentByAllocationBatch(AllocationBatch allocationBatch){
		Criteria criteria = this.createCriteria("dha");
		
		criteria.add(Restrictions.eq("allocationBatch", allocationBatch));
		
		return criteria.list();
		
	}
	
	public List<DistrictHeadAdjustment> findDistrictHeadAdjustmentByAllocationBatchIds(List<Integer> allocationBatchIds){
		if(allocationBatchIds.size() > 0){
			Criteria criteria = this.createCriteria("dha")
					.createAlias("dha.allocationBatch", "ab", JoinType.LEFT_OUTER_JOIN);;
			
			criteria.add(Restrictions.in("ab.allocationBatchId", allocationBatchIds.toArray()));
			
			return criteria.list();
		}else{
			return new ArrayList<DistrictHeadAdjustment>();
		}
		
	}
	
	public DistrictHeadAdjustment findDistrictHeadAdjustmentByAllocationBatchIdUserId(int allocationBatchId, int userId){
		Criteria criteria = this.createCriteria("dha")
				.createAlias("dha.allocationBatch", "ab", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ab.allocationBatchId", allocationBatchId));
		criteria.add(Restrictions.eq("user.id", userId)); 
		return (DistrictHeadAdjustment)criteria.uniqueResult();
	}
	

	
	public long countDistrictHeadAdjustmentInMonth(Date month){
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.eq("referenceMonth", month));		
		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<DistrictHeadAdjustment> getAssignmentAllocationView(Integer allocationBatchId){
		
		String hql = "select dha "
				+ "from DistrictHeadAdjustment as dha "
				+ "left join dha.allocationBatch as ab "
				+ "inner join fetch dha.user as u "
				+ "where 1=1 "
				+ "and ab.allocationBatchId = :allocationBatchId "
				+ "order by case when dha.district is null or dha.district = '' then 1 else 0 end, "
				+ "dha.district ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);
		
		return query.list();
	}
}
