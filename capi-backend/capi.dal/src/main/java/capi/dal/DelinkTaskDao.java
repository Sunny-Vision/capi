package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.DelinkTask;

@Repository("DelinkTaskDao")
public class DelinkTaskDao  extends GenericDao<DelinkTask>{

	public List<DelinkTask> getPendingDelinkTask(){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("isRun", false));
		
		return criteria.list();
	}
	
	
	public void delink(Date referenceMonth){
		String sql = "exec dbo.DeleteQuotationRecordsInMonth :refernceMonth";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("refernceMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public String getDelinkDate() {

		String sql = "Select EOMONTH( Max(ReferenceMonth) ) as ReferenceMonth from [DelinkTask] WHERE IsRun = 1";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("ReferenceMonth", StandardBasicTypes.STRING);
		String a =  (String) query.list().get(0);
		
		return a;
	}

}
