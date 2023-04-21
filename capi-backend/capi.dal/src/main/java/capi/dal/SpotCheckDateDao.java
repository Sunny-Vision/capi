package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
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

import capi.dal.utils.SQLProjectionExt;
import capi.entity.SpotCheckDate;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SpotCheckDateTableList;


@Repository("SpotCheckDateDao")
public class SpotCheckDateDao  extends GenericDao<SpotCheckDate>{

	@SuppressWarnings("unchecked")
	/*public List<SpotCheckDateTableList> selectAllSpotCheckDate(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("scd");
								//.createAlias("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		Criteria subCriteria = criteria.createCriteria("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		String referenceMonth = String.format("FORMAT({sm}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		String searchReferenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		criteria.setProjection(
				Projections.projectionList()
				.add(SQLProjectionExt.sqlProjection(referenceMonth + " as referenceMonth", "{sm}.referenceMonth", 
						new String [] {"referenceMonth"}, new Type[]{StandardBasicTypes.STRING}), "referenceMonth")
				.add(Projections.count("scd.date"), "noOfDays")
				.add(Projections.groupProperty("sm.surveyMonthId"), "surveyMonthId")
			);

		if (!StringUtils.isEmpty(search)) {
			subCriteria.add(
				Restrictions.sqlRestriction(
						searchReferenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			);
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckDateTableList.class));

        return criteria.list();
	}

	public long countSelectAllSpotCheckDate(String search) {

		Criteria criteria = this.createCriteria("scd");
								//.createAlias("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		Criteria subCriteria = criteria.createCriteria("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		String referenceMonth = String.format("FORMAT({sm}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		String searchReferenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("sm.referenceMonth"),
				Projections.groupProperty("sm.surveyMonthId")
		));

		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			subCriteria.add(
				Restrictions.sqlRestriction(
						searchReferenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			);
		}

		return (long)criteria.uniqueResult();
	}*/

	public List<Integer> findSpotCheckDateIdsBySurveyMonthId(int id) {

		Criteria criteria = this.createCriteria("scd")
								.createAlias("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("sm.surveyMonthId", id));

		return criteria.list();
	}

	public List<SpotCheckDateTableList> selectedSpotCheckDates(int surveyMonthId) {

		Criteria criteria = this.createCriteria("scd")
								.createAlias("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		String referenceMonth = String.format("FORMAT({sm}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
		String selectedSpotCheckDate = String.format("FORMAT({alias}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		criteria.setProjection(
				Projections.projectionList()
				.add(SQLProjectionExt.sqlProjection(referenceMonth + " as referenceMonth", 
						new String [] {"referenceMonth"}, new Type[]{StandardBasicTypes.STRING}), "referenceMonth")
				.add(SQLProjectionExt.sqlProjection(selectedSpotCheckDate + " as selectedSpotCheckDate", 
						new String [] {"selectedSpotCheckDate"}, new Type[]{StandardBasicTypes.STRING}), "selectedSpotCheckDate")
			);

		criteria.add(Restrictions.eq("sm.surveyMonthId", surveyMonthId));

		criteria.addOrder(Order.asc("selectedSpotCheckDate"));

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckDateTableList.class));

        return criteria.list();
	}

	public List<String> getSpotCheckDateOfReferenceMonth(Integer surveyMonthId){
		Criteria criteria = this.createCriteria("scd")
				.createAlias("scd.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDateFromDB = String.format("FORMAT({alias}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		criteria.setProjection(
				Projections.projectionList()
				.add(SQLProjectionExt.sqlProjection(spotCheckDateFromDB + " as date",
						new String [] {"date"}, new Type[]{StandardBasicTypes.STRING}), "date")
		);

		criteria.add(Restrictions.eq("sm.surveyMonthId", surveyMonthId));

		return criteria.list();
	}
	
	
	public List<String> getCurrentMonthSpotCheckDates(){
		Criteria criteria = this.createCriteria("scd")
				.createAlias("scd.surveyMonth", "sm");
		
		
		String spotCheckDateFromDB = String.format("FORMAT({alias}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		criteria.setProjection(
				Projections.projectionList()
				.add(SQLProjectionExt.sqlProjection(spotCheckDateFromDB + " as date",
						new String [] {"date"}, new Type[]{StandardBasicTypes.STRING}), "date")
		);
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		criteria.add(Restrictions.le("sm.startDate", date))
		.add(Restrictions.ge("sm.endDate", date));
		
		return criteria.list();
	}
	
	

	public List<SpotCheckDate> searchAvaliableSpotCheckDate(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("date"));
		
		String spotCheckDateFromDB = String.format("FORMAT({alias}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		criteria.add(Restrictions.sqlRestriction("cast({alias}.date as date) >= cast(getDate() as date)"));
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.sqlRestriction(spotCheckDateFromDB+" like ?", "%"+search+"%", StandardBasicTypes.STRING));
		}

		return criteria.list();
	}

	public long countAvaliableSpotCheckDate(String search) {
		Criteria criteria = this.createCriteria();

		String spotCheckDateFromDB = String.format("FORMAT({alias}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		criteria.add(Restrictions.sqlRestriction("cast({alias}.date as date) >= cast(getDate() as date)"));
		
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.sqlRestriction(spotCheckDateFromDB+" like ?", "%"+search+"%", StandardBasicTypes.STRING));
		}

		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public long countSpotCheckDateInMonth(Date month){
		Criteria criteria = this.createCriteria("s")
				.createAlias("s.surveyMonth", "m");
		criteria.add(Restrictions.eq("m.referenceMonth", month));
		criteria.setProjection(Projections.rowCount());
		
		return (long)criteria.uniqueResult();
	}

}
