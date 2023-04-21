package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Batch;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchQuotationActiveModel;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionFilterModel;
import capi.model.dataConversion.quotationRecordDataConversion.PurposeIndoorQuotationRecordCountModel;
import capi.model.masterMaintenance.BatchTableList;
import capi.model.masterMaintenance.OutletBatchCodeModel;

@Repository("BatchDao")
public class BatchDao  extends GenericDao<Batch>{
	@SuppressWarnings("unchecked")
	public List<BatchTableList> selectAllBatch(String search, int firstRecord, int displayLength, Order order, List<String> surveyForm) {

		String hql = " select b.batchId as batchId, b.code as code, b.description as description, b.assignmentType as assignmentType, "
				+ " case "
				+ "	when b.assignmentType is null then '' "
				+ " when b.assignmentType = 1 then :assignmentType1 "
				+ " when b.assignmentType = 2 then :assignmentType2 "
				+ " else :assignmentType3 end  as assignmentTypeLabel, "
				+ " b.surveyForm as surveyForm, "
				+ " b.batchCategory as batchCategory "
				+ " from Batch as b where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and  (b.code like :search or b.description like :search or b.surveyForm like :search or b.batchCategory like :search"
				+ " or ( "
				+ "   case "
				+ "   when b.assignmentType is null then '' "
				+ "   when b.assignmentType = 1 then :assignmentType1 "
				+ "   when b.assignmentType = 2 then :assignmentType2 "
				+ "   else :assignmentType3 end ) like :search "
				+ " ) ";
		}

		if (surveyForm != null && surveyForm.size() > 0) {
			hql += " and b.surveyForm in (:surveyForm) ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("assignmentType1", SystemConstant.ASSIGNMENT_TYPE_1);
		query.setParameter("assignmentType2", SystemConstant.ASSIGNMENT_TYPE_2);
		query.setParameter("assignmentType3", SystemConstant.ASSIGNMENT_TYPE_3);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (surveyForm != null && surveyForm.size() > 0) {
			query.setParameterList("surveyForm", surveyForm);
		}

		query.setResultTransformer(Transformers.aliasToBean(BatchTableList.class));

		return query.list();
	}

	public long countSelectAllBatch(String search, List<String> surveyForms) {

		String hql = " select count(*) as cnt "
				+ " from Batch as b "
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and (b.code like :search or b.description like :search or b.surveyForm like :search or b.batchCategory like :search"
				+ " or ( "
				+ "   case "
				+ "   when b.assignmentType is null then '' "
				+ "   when b.assignmentType = 1 then :assignmentType1 "
				+ "   when b.assignmentType = 2 then :assignmentType2 "
				+ "   else :assignmentType3 end ) like :search "
				+ " ) ";
		}
		
		if (surveyForms != null && surveyForms.size() > 0 ) {
			hql += " and b.surveyForm in (:surveyForm) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("assignmentType1", SystemConstant.ASSIGNMENT_TYPE_1);
			query.setParameter("assignmentType2", SystemConstant.ASSIGNMENT_TYPE_2);
			query.setParameter("assignmentType3", SystemConstant.ASSIGNMENT_TYPE_3);
		}
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (surveyForms != null && surveyForms.size()>0) {
			query.setParameterList("surveyForm", surveyForms);
		}

		return (long)query.uniqueResult();	
		
	}

	@SuppressWarnings("unchecked")
	public List<BatchTableList> selectAllBatchSelect2(String search, int firstRecord, int displayLength, Order order, String surveyForm) {

		String hql = " select b.batchId as batchId, b.code as code "
				+ " from Batch as b where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and b.code like :search ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}

		query.setResultTransformer(Transformers.aliasToBean(BatchTableList.class));

		return query.list();
	}

	public long countSelectAllBatchSelect2(String search, String surveyForm) {

		String hql = " select count(*) as cnt "
				+ " from Batch as b "
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and b.code like :search ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}


		return (long)query.uniqueResult();	
		
	}

	public List<Batch> getBatchsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("batchId", ids));
		return criteria.list();
	}
	
	public Batch getBatchByCode(String batchCode){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", batchCode));
		return (Batch)criteria.uniqueResult();
	}

	public List<Batch> getAll() {
		return this.createCriteria().addOrder(Order.asc("code")).list();
	}

	public List<BatchQuotationActiveModel> getAllBatchQuotationActive(){
		
		String status = "Active";
		
		String hql = "select b.batchId as batchId, b.code as code, q.status as status "
					+ ", b.batchCategory as batchCategory, b.assignmentType as assignmentType "
					+ " from Batch as b "
					+ " left join b.quotations as q "
					+ " where 1 = 1 ";
		
		hql += " group by b.batchId, b.code, q.status, b.batchCategory, b.assignmentType ";
		
		hql += " having q.status = :status ";
		
		hql += " order by b.code asc";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("status", status);
		
		query.setResultTransformer(Transformers.aliasToBean(BatchQuotationActiveModel.class));
		
		return query.list();
	}

	public List<Batch> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("batchId", ids)).list();
	}

	public List<Batch> getBatchByCategory(String category){
		return this.createCriteria()
				.add(Restrictions.eq("batchCategory", category))
				.addOrder(Order.asc("code")).list();
	}
	
	public List<BatchQuotationActiveModel> getBatchQuotationActiveByCategory(String category){
		
		String status = "Active";
		
		String hql = "select b.batchId as batchId, b.code as code, q.status as status "
					+ ", b.assignmentType as assignmentType "
					+ " from Batch as b "
					+ " left join b.quotations as q "
					+ " where b.batchCategory = :batchCategory ";
		
		hql += " group by b.batchId, b.code, q.status, b.assignmentType ";
		
		hql += " having q.status = :status ";
		
		hql += " order by b.code asc";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("batchCategory", category);
		query.setParameter("status", status);
		
		query.setResultTransformer(Transformers.aliasToBean(BatchQuotationActiveModel.class));
		
		return query.list();
	}
	
	public List<Integer> getLookupTableSelectAll(String search, String surveyForm){
		String hql = " select b.batchId as batchId"
				+ " from Batch as b where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and  (b.code like :search or b.description like :search or b.surveyForm like :search or b.batchCategory like :search"
				+ " or ( "
				+ "   case "
				+ "   when b.assignmentType is null then '' "
				+ "   when b.assignmentType = 1 then :assignmentType1 "
				+ "   when b.assignmentType = 2 then :assignmentType2 "
				+ "   else :assignmentType3 end ) like :search "
				+ " ) ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		
		if (!StringUtils.isEmpty(search)){

			query.setParameter("assignmentType1", SystemConstant.ASSIGNMENT_TYPE_1);
			query.setParameter("assignmentType2", SystemConstant.ASSIGNMENT_TYPE_2);
			query.setParameter("assignmentType3", SystemConstant.ASSIGNMENT_TYPE_3);
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		
		return query.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<BatchTableList> selectBatchForReportCriteria(String search, int firstRecord, int displayLength, Integer[] cpiQuotationType) {
		String hql = " select b.batchId as batchId, b.code as code "
                + " from Batch as b "
				+ "  left join b.quotations as q "
                + "  left join q.unit as u "
                + " where 1=1 ";
		
		if(cpiQuotationType != null && cpiQuotationType.length > 0) {
			hql += " and u.unitId is not null and u.cpiQoutationType in (:cpiQuotationType) ";
		}

		if (!StringUtils.isEmpty(search)) {
			hql += " and b.code like :search ";
		}

		hql += " group by b.batchId, b.code ";
		hql += " order by b.code asc ";
		
		Query query = getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if(cpiQuotationType != null && cpiQuotationType.length > 0) {
			query.setParameterList("cpiQuotationType", cpiQuotationType);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(BatchTableList.class));
		
		return query.list();
	}
	
	public long countSelectBatchForReportCriteria(String search, Integer[] cpiQuotationType) {
		String hql = " select count(distinct b.batchId) as cnt "
                + " from Batch as b "
				+ "  left join b.quotations as q "
                + "  left join q.unit as u "
                + " where 1=1 ";
		
		if(cpiQuotationType != null && cpiQuotationType.length > 0) {
			hql += " and u.unitId is not null and u.cpiQoutationType in (:cpiQuotationType) ";
		}

		if (!StringUtils.isEmpty(search)) {
			hql += " and b.code like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if(cpiQuotationType != null && cpiQuotationType.length > 0) {
			query.setParameterList("cpiQuotationType", cpiQuotationType);
		}

		return (long)query.uniqueResult();
	}
	
	
	public List<String> getSurveyFormList(){
		
		String hql = "select distinct b.surveyForm "
				+ " from Batch as b "
				+ " where b.surveyForm is not null and b.surveyForm != '' ";
		
		Query query = this.getSession().createQuery(hql);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BatchTableList> selectBatchByUserSelect2(String search, int firstRecord, int displayLength, Order order, String surveyForm, Integer userId) {

		String hql = " select b.batchId as batchId, b.code as code "
				+ " from Batch as b "
				+ " inner join b.users as u "
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and b.code like :search ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		if (userId!=null){
			hql += " and u.userId = :userId ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		if (userId!=null){
			query.setParameter("userId", userId);
		}

		query.setResultTransformer(Transformers.aliasToBean(BatchTableList.class));

		return query.list();
	}

	public long countSelectBatchByUserSelect2(String search, String surveyForm, Integer userId) {

		String hql = " select count(*) as cnt "
				+ " from Batch as b "
				+ " inner join b.users as u"
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and b.code like :search ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		if (userId!=null){
			hql += " and u.userId = :userId ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		if (userId!=null){
			query.setParameter("userId", userId);
		}
		
		return (long)query.uniqueResult();	
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getBatchCodeByAssignmentId(int assignmentId){
		String hql = "Select b.code"
				+ " from Assignment as a"
				+ " left join a.quotationRecords as qr"
				+ " left join qr.quotation as q"
				+ " left join q.batch as b";
		
		hql += " where a.assignmentId = :assignmentId";
		
		hql += " group by b.code";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("assignmentId", assignmentId);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Boolean> validateBatch(Integer outletId, List<Integer> batchIds){
		String hql = "select case when count(o) = 0 then true else false end"
				+ " from Batch as b "
				+ " left join b.quotations as q on q.status <> 'Inactive' "
				+ " left join q.outlet as o on o.id = :outletId "
				+ " where b.batchId in (:batchIds) "
				+ " group by b.batchId, b.code";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("outletId", outletId);
		query.setParameterList("batchIds", batchIds);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<OutletBatchCodeModel> searchBatchCodes(String search, int firstRecord, int displayLength, int outletId){
		String hql = " select str(batch.batchId) as key, batch.code as value, "
				+ " case when count(o) = 0 then true else false end as emptyOutlet "
				+ " from Batch as batch "
				+ " left join batch.quotations as q on q.status <> 'Inactive' "
				+ " left join q.outlet as o on o.id = :outletId "
//				+ " where batch.assignmentType <> 1 ";
				+ " where 1 = 1 ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and batch.code like :search ";
		}
		
		hql += " group by batch.batchId, batch.code ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("outletId", outletId);
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(OutletBatchCodeModel.class));
		
		return query.list();
	}
	
	public long countSearchBatchCodes(String search, int outletId) {
		String hql = " select count(distinct b.batchId) "
				+ " from Batch as b "
				+ " where 1 = 1 ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and b.code like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		//query.setParameter("outletId", outletId);
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		return (long) query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getBatchByUserId(Integer userId){
		String sql = "Select BatchId as batchId from UserBatch where UserId = :userId ";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.addScalar("batchId", StandardBasicTypes.INTEGER);
		return query.list();
	}
}
