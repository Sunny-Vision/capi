package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.StaffReason;
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckRemark;

@Repository("StaffReasonDao")
public class StaffReasonDao   extends GenericDao<StaffReason>{

	public List<StaffReason> getStaffReasonByUserId(Integer userId){
		Criteria criteria = this.createCriteria("r");
		criteria.createAlias("r.user", "u");		
		criteria.add(Restrictions.eq("u.userId", userId));
		criteria.addOrder(Order.desc("r.fromDate"));
		criteria.addOrder(Order.desc("r.toDate"));
		
		return criteria.list();
	}
	
	public List<StaffReason> getNotExistStaffReasons(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("staffReasonId", ids)));
		return criteria.list();
	}
	
	public List<StaffReason> getStaffReasonByIds(Integer [] ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("staffReasonId", ids));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfSupervisoryVisitSpotCheckRemark> getSummaryOfSupervisoryVisitSpotCheckReportRemark(Integer year, List<Integer> userIds){
		
		String sql = "select userId, fromDate, ToDate, cast (reason as varchar) as reason from StaffReason "
				+ "where ( year(fromDate) = :year or year(toDate) = :year )"
				+ "and userId in (:userIds) "
				+ "order by fromDate ";
		
		Query query = this.getSession().createSQLQuery(sql);

		query.setParameter("year", year);
		query.setParameterList("userIds", userIds);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfSupervisoryVisitSpotCheckRemark.class));
		
		return query.list();
	}
	
}
