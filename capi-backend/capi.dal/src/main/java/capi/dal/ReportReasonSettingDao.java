package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.ReportReasonSetting;

@Repository("ReportReasonSetting")
public class ReportReasonSettingDao extends GenericDao<ReportReasonSetting> {

	public List<ReportReasonSetting> getExcludedReportReasonSetting(List<Integer> ids ){
		Criteria criteria = this.createCriteria("rrs")
				.add(Restrictions.not(Restrictions.in("rrs.reportReasonSettingId", ids)));
		return criteria.list();
	}
}
