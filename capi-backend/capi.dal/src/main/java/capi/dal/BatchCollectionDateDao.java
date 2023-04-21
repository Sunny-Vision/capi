package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.AssignmentAttribute;
import capi.entity.BatchCollectionDate;
import capi.model.api.dataSync.BatchCollectionDateSyncData;

@Repository("BatchCollectionDateDao")
public class BatchCollectionDateDao extends GenericDao<BatchCollectionDate>{
	public List<BatchCollectionDate> findBatchCollectionDateByAssignmentAttribute(AssignmentAttribute assignmentAttribute){
		Criteria criteria = this.createCriteria("bcd");
		
		criteria.add(Restrictions.eq("assignmentAttribute", assignmentAttribute));
		
		return criteria.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<BatchCollectionDate> findBatchCollectionDateByAssignmentAttributeAfterDate(AssignmentAttribute assignmentAttribute, Date date){
		return this.createCriteria("bcd")
				.add(Restrictions.eq("bcd.assignmentAttribute", assignmentAttribute))
				.add(Restrictions.gt("bcd.date", date))
				.list();
	}
	
	public List<BatchCollectionDateSyncData> getUpdatedBatchCollectionDate(Date lastSyncTime){
		Criteria criteria = this.createCriteria("bc")
				.createAlias("bc.assignmentAttribute", "aa", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ge("bc.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("bc.batchCollectionDateId"), "batchCollectionDateId");
		projList.add(Projections.property("bc.date"), "date");
		projList.add(Projections.property("bc.createdDate"), "createdDate");
		projList.add(Projections.property("bc.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("bc.hasBackTrack"), "hasBackTrack");
		projList.add(Projections.property("bc.backTrackDate1"), "backTrackDate1");
		projList.add(Projections.property("bc.backTrackDate2"), "backTrackDate2");
		projList.add(Projections.property("bc.backTrackDate3"), "backTrackDate3");
		projList.add(Projections.property("aa.assignmentAttributeId"), "assignmentAttributeId");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(BatchCollectionDateSyncData.class));
		return criteria.list();
	}
}
