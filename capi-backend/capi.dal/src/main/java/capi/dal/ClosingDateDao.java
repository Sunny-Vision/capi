package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.ClosingDate;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.ClosingDateTableList;

@Repository("ClosingDateDao")
public class ClosingDateDao extends GenericDao<ClosingDate> {
	
	@SuppressWarnings("unchecked")
	public List<ClosingDateTableList> listClosingDate(String search,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria("c")
				.createAlias("c.surveyMonths", "m", JoinType.LEFT_OUTER_JOIN)
				.setFirstResult(firstRecord)
				.setMaxResults(displayLenght)
				.addOrder(order);
		String closingDate = String.format("FORMAT({alias}.closingDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String publishDate = String.format("FORMAT({alias}.publishDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.groupProperty("closingDateId"),"closingDateId")
				.add(Projections.countDistinct("m.surveyMonthId"),"surveyMonthCnt")
				.add(Projections.sqlGroupProjection(closingDate+" as closingDate", "{alias}.closingDate", new String [] {"closingDate"}, new Type[]{StandardBasicTypes.STRING}), "closingDate")
				//.add(Projections.sqlProjection(closingDate+" as closingDate", new String [] {"closingDate"}, new Type[]{StandardBasicTypes.STRING}), "closingDate")
				.add(Projections.sqlGroupProjection(referenceMonth+" as referenceMonth", "{alias}.referenceMonth",  new String [] {"referenceMonth"}, new Type[]{StandardBasicTypes.STRING}), "referenceMonth")
				.add(Projections.sqlGroupProjection(publishDate+" as publishDate", "{alias}.publishDate",  new String [] {"publishDate"}, new Type[]{StandardBasicTypes.STRING}), "publishDate")
			);

		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.sqlRestriction(
							closingDate + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
							,
					Restrictions.sqlRestriction(
							referenceMonth + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
							,
					Restrictions.sqlRestriction(
							publishDate + " LIKE (?)",
							"%" + search + "%", StandardBasicTypes.STRING)
				));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ClosingDateTableList.class));
		
		return criteria.list();
	}

	public long countClosingDate() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countClosingDate(String search) {
		Criteria criteria = this.createCriteria();
		
		String closingDate = String.format("FORMAT({alias}.closingDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String publishDate = String.format("FORMAT({alias}.publishDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		criteria.add(Restrictions.or(
				Restrictions.sqlRestriction(
						closingDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
						,
				Restrictions.sqlRestriction(
						referenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
						,
				Restrictions.sqlRestriction(
						publishDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
		));
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public List<ClosingDate> getClosingDateByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("closingDateId", ids));
		return criteria.list();
		
	}

	public ClosingDate getClosingDateByReferenceMonth(Date refMonth){
		Criteria criteria = this.createCriteria();
		criteria.add(
				Restrictions.eq("referenceMonth", refMonth));
		return (ClosingDate)criteria.uniqueResult();
	}
}
