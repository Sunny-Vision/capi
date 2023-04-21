package capi.dal;

import java.util.Date;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import capi.entity.SpotCheckStatistic;

@Repository("SpotCheckStatisticDao")
public class SpotCheckStatisticDao  extends GenericDao<SpotCheckStatistic>{
	public void calculateSpotCheckStatistic(Date referenceMonth){
		Query query = this.getSession().createSQLQuery("exec [CalculateSpotCheckStatistic] :refMonth");
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
}
