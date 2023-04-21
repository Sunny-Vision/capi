package capi.dal;

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
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.ReportTask;
import capi.model.SystemConstant;
import capi.model.report.AccessLogModel;
import capi.model.report.AuditLogModel;
import capi.model.report.ReportTaskList;

@Repository("ReportTaskDao")
public class ReportTaskDao extends GenericDao<ReportTask>{

	public List<ReportTaskList> searchReportTaskList(String search,
			int firstRecord, int displayLength, Order order, Integer userid, String functionCode){
	
		String createdDate = String.format("FORMAT({alias}.createdDate, '%s', 'en-us')", SystemConstant.DATE_TIME_FORMAT);
		Criteria criteria = this.createCriteria("r");
		criteria.createAlias("r.user", "u");
		
		ProjectionList list = Projections.projectionList();
		list.add(Projections.property("reportTaskId"),"reportTaskId");
		list.add(Projections.sqlProjection(createdDate+" as createdDate", new String [] {"createdDate"}, new Type[]{StandardBasicTypes.STRING}),"createdDate");
		list.add(Projections.sqlProjection("replace({alias}.description, CHAR(10), '<br />') as description", new String [] {"description"}, new Type[]{StandardBasicTypes.STRING}),"description");
		list.add(SQLProjectionExt.sqlProjection("{u}.staffCode + ' - ' + {u}.chineseName as createdBy", new String [] {"createdBy"}, new Type[]{StandardBasicTypes.STRING}),"createdBy");
		list.add(Projections.property("r.status"),"status");
		list.add(Projections.property("r.exceptionMessage"),"exceptionMessage");
		
		criteria.add(Restrictions.eq("r.functionCode", functionCode))
			.add(Restrictions.eq("u.id", userid));
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.or(
					Restrictions.like("r.description", "%"+search+"%"),
					Restrictions.like("u.staffCode", "%"+search+"%"),
					Restrictions.like("u.chineseName", "%"+search+"%"),
					Restrictions.like("r.status", "%"+search+"%")
			));
		}
		
		criteria.setProjection(list);
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		criteria.addOrder(order);
		//criteria.addOrder(Order.desc("r.createdDate"));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ReportTaskList.class));
		
		return criteria.list();
	}
	
	public Long countReportTask(String search, Integer userId, String functionCode){
		Criteria criteria = this.createCriteria("r");
		criteria.createAlias("r.user", "u");
		criteria.add(Restrictions.eq("r.functionCode", functionCode));
		criteria.add(Restrictions.eq("u.id", userId));
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.or(
					Restrictions.like("r.description", "%"+search+"%"),
					Restrictions.like("u.staffCode", "%"+search+"%"),
					Restrictions.like("u.chineseName", "%"+search+"%"),
					Restrictions.like("r.status", "%"+search+"%")
			));
		}
		return (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public List<ReportTask> getHouseKeepTask(String functionCode){
		Criteria criteria = this.createCriteria("r");
		criteria.add(Restrictions.eq("functionCode", functionCode));
		criteria.setFirstResult(30);
		criteria.addOrder(Order.desc("createdDate"));
		return criteria.list();
	}
	
	
	public List<AuditLogModel> getAuditLog(Date from, Date to){
		String sql = "exec dbo.GetAuditLog :from, :to, :format";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("format", SystemConstant.DATE_TIME_FORMAT);
		query.addScalar("auditLogId", StandardBasicTypes.INTEGER);
		query.addScalar("entityName", StandardBasicTypes.STRING);
		query.addScalar("entityId", StandardBasicTypes.INTEGER);
		query.addScalar("action", StandardBasicTypes.STRING);
		query.addScalar("actor", StandardBasicTypes.STRING);
		query.addScalar("createdDate", StandardBasicTypes.STRING);
		query.addScalar("columnName", StandardBasicTypes.STRING);
		query.addScalar("columnValue", StandardBasicTypes.STRING);
		query.addScalar("oldValue", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(AuditLogModel.class));
		
		return query.list();
	}
	
	public List<AccessLogModel> getAccessLog(Date from, Date to) {
		String sql = "exec dbo.GetAccessLog :from, :to, :format";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("from", from)
			.setParameter("to", to)
			.setParameter("format", SystemConstant.DATE_TIME_FORMAT);
		
		query.addScalar("accessLogId", StandardBasicTypes.INTEGER)
			.addScalar("username", StandardBasicTypes.STRING)
			.addScalar("event", StandardBasicTypes.STRING)
			.addScalar("eventDateTime", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(AccessLogModel.class));
		
		return query.list();
		
	}
	
	public ReportTask getPreviousReportTask(String functionCode){
		Criteria criteria = this.createCriteria("r");
		criteria.add(/*Restrictions.and(
			Restrictions.eq("r.functionCode", functionCode),
			Restrictions.eq("r.status", "Finished")
		)*/
		Restrictions.eq("r.functionCode", functionCode));
		criteria.addOrder(Order.desc("r.createdDate"));
		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		return (ReportTask)criteria.uniqueResult();
	}
	
}
