package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.ActivityCode;

@Repository("ActivityCodeDao")
public class ActivityCodeDao extends GenericDao<ActivityCode> {
	
	@SuppressWarnings("unchecked")
	public List<ActivityCode> listActivityCode(String search,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.sqlRestriction(
				              "{alias}.manDay LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("description", "%" + search + "%")));
		}
	
		return criteria.list();
	}

	public long countActivityCode() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countActivityCode(String search) {
		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.or(
				Restrictions.sqlRestriction(
			              "{alias}.manDay LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING),
				Restrictions.like("code", "%" + search + "%"),
				Restrictions.like("description", "%" + search + "%")));
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public List<ActivityCode> getUActivityCodeByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("activityCodeId", ids));
		return criteria.list();		
	}
	
	public List<ActivityCode> getNotExistedActivityCode(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("activityCodeId", ids)));
		return criteria.list();		
	}
	
	public List<Integer> getExistingActivityCodeId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<ActivityCode> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("activityCodeId", ids));
		return criteria.list();
	}
	
	public ActivityCode getActivityCodeByCode(String activityCode){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", activityCode));
		return (ActivityCode)criteria.uniqueResult();
	}
	
	public ScrollableResults getAllActivityCodeResult(){
		Criteria criteria = this.createCriteria();
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
}
