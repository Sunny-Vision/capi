package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Notification;
import capi.model.NotifcationPreviewModel;
import capi.model.NotificationTableList;
import capi.model.SystemConstant;
import capi.model.api.dataSync.NotificationSyncData;

@Repository("NotificationDao")
public class NotificationDao extends GenericDao<Notification> {


	@SuppressWarnings("unchecked")
	public List<NotificationTableList> listNotification(String search,
			int firstRecord, int displayLenght, Order order, Integer userId, Boolean isUnReadOnly, Boolean isFlagOnly) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);
		
		criteria.createAlias("user", "u", JoinType.LEFT_OUTER_JOIN)
		.add(Restrictions.eq("u.userId", userId));
		
		String date = String.format("FORMAT({alias}.createdDate, '%s', 'en-us')", SystemConstant.DATE_TIME_FORMAT);
		
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("notificationId"), "notificationId");
		projList.add(Projections.property("subject"), "subject");
		projList.add(Projections.property("isRead"), "read");
		projList.add(Projections.property("isFlag"), "flag");
		projList.add(Projections.sqlProjection(date+" as date",  new String [] {"date"}, new Type[]{StandardBasicTypes.STRING}), "date");
		//Count number of unread message
	
		
		criteria.setProjection(projList);
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("subject", "%" + search + "%"),
					Restrictions.sqlRestriction(
							date + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)));
		}
		
		if (isUnReadOnly != null && isUnReadOnly){
			criteria.add(Restrictions.eq("isRead", false));
		}
		if (isFlagOnly != null && isFlagOnly){
			criteria.add(Restrictions.eq("isFlag", true));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificationTableList.class));
		
		return criteria.list();
	}

	public long countUnreadNotification(Integer userId) {
		Criteria criteria = this.createCriteria();
		
		criteria.createAlias("user", "u", JoinType.LEFT_OUTER_JOIN)
		.add(Restrictions.eq("u.userId", userId));
		
		criteria.add(Restrictions.eq("isRead", false));
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countNotification(Integer userId){
		return this.countNotification("", userId, null, null);
	}
	
	public long countNotification(String search,Integer userId, Boolean isUnReadOnly, Boolean isFlagOnly) {
		Criteria criteria = this.createCriteria();
		
		criteria.createAlias("user", "u", JoinType.LEFT_OUTER_JOIN)
		.add(Restrictions.eq("u.userId", userId));
		
		if (StringUtils.isEmpty(search)){
			String date = String.format("FORMAT({alias}.createdDate, '%s', 'en-us')", SystemConstant.DATE_TIME_FORMAT);			
			criteria.add(Restrictions.or(
					Restrictions.like("subject", "%" + search + "%"),
					Restrictions.sqlRestriction(
							date + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)));
		}
		
		if (isUnReadOnly != null && isUnReadOnly){
			criteria.add(Restrictions.eq("isRead", false));
		}
		
		if (isFlagOnly != null && isFlagOnly){
			criteria.add(Restrictions.eq("isFlag", true));
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<NotifcationPreviewModel> getLatestUnReadMessagePreview(Integer userId){
		
		String date = String.format("FORMAT({alias}.createdDate, '%s', 'en-us')", SystemConstant.DATE_TIME_FORMAT);
		Criteria criteria = this.createCriteria("n");		
		criteria.createAlias("user", "u", JoinType.LEFT_OUTER_JOIN)
			.add(Restrictions.eq("u.userId", userId))
			.add(Restrictions.eq("isRead", false));
		
		criteria.setMaxResults(200)
			.addOrder(Order.desc("n.createdDate"));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("notificationId"),"notificationId");
		projList.add(Projections.property("subject"),"subject");
		projList.add(Projections.property("isRead"),"isRead");
		projList.add(Projections.sqlProjection(date+" as createdDate",  new String [] {"createdDate"}, new Type[]{StandardBasicTypes.STRING}), "createdDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(NotifcationPreviewModel.class));		
		
		return criteria.list();
	}
	
	
	public List<Notification> getNotificationsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("notificationId", ids));
		return criteria.list();
	}
	
	public List<NotificationSyncData> getUpdatedNotification(Date lastSyncTime){
		Criteria criteria = this.createCriteria("n")
				.createAlias("n.user", "u");
		criteria.add(Restrictions.ge("n.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("n.notificationId"), "notificationId");
		projList.add(Projections.property("n.subject"), "subject");
		projList.add(Projections.property("n.content"), "content");
		projList.add(Projections.property("n.isRead"), "isRead");
		projList.add(Projections.property("n.createdDate"), "createdDate");
		projList.add(Projections.property("n.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("n.isFlag"), "isFlag");
		projList.add(Projections.property("n.rejectedQuotationIds"), "rejectedQuotationIds");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(NotificationSyncData.class));
		return criteria.list();
	}
	
}
