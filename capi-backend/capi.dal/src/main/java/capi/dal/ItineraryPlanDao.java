package capi.dal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.ItineraryPlan;
import capi.model.SystemConstant;
import capi.model.api.dataSync.ItineraryPlanAssignmentSyncData;
import capi.model.api.dataSync.ItineraryPlanSyncData;
import capi.model.api.dataSync.ItineraryUnPlanAssignmentSyncData;
import capi.model.api.onlineFunction.ItineraryPlanOnlineModel;
import capi.model.itineraryPlanning.ItineraryPlanOutletModel;
import capi.model.itineraryPlanning.ItineraryPlanTableList;

@Repository("ItinerayPlanDao")
public class ItineraryPlanDao  extends GenericDao<ItineraryPlan>{

	@SuppressWarnings("unchecked")
	public List<ItineraryPlanTableList> getTableList(String search,
			int firstRecord, int displayLenght, Order order, 
		    Date date, List<Integer> supervisorId, Integer filterUserId, Integer fieldOfficerId,
		    String[] status, String delinkDateStr) {

		String planDate = String.format("FORMAT(i.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select i.itineraryPlanId as id, "
				+ " i.version as version, "
                + " case when i.date is null then '' else "+planDate+" end as date, "
                + " count(distinct ia.assignmentId) as noOfAssignment, "
                + " i.status as status,  "
                + " iu.staffCode as fieldOfficerCode, "
                + " iu.chineseName as chineseName, "
                + " t.timeLogId as timeLogId "
                + " from ItineraryPlan as i "
                + " left join i.assignments as ia "
                + " left join i.user as iu "
                + " left join iu.supervisor as su "
                + " left join i.timeLogMap as map "
                + " left join map.timeLog as t on t.status not in ('Draft', 'Rejected') "
                + " where (str(i.itineraryPlanId) like :search or "+planDate+" like :search "
                + " or i.status like :search"
                + " or iu.staffCode like :search "
                + " or iu.chineseName like :search "
                + " ) "
                + (date != null ?  " and i.date = :date " : "");
		if (fieldOfficerId !=null && supervisorId!=null && supervisorId.size() > 0){
			hql += " and (su.userId in (:supervisorId) or iu.userId = :fieldOfficerId) " ;
		}
		else if (fieldOfficerId !=null){
			hql += " and iu.userId = :fieldOfficerId " ;
		}
		else if (supervisorId!=null && supervisorId.size() > 0){
			hql += " and su.userId in (:supervisorId) " ;
		}                
        
		
		
        hql += (filterUserId != null? " and iu.userId = :filterUserId ":"") 
            + ((status != null && status.length > 0)  ? " and i.status in (:status) " : "");
        
        if(delinkDateStr != null && !delinkDateStr.isEmpty()) {
        	hql +=" and i.date > :delinkDate ";        	
        }
        
        hql += " group by i.itineraryPlanId, i.version, i.date, "
            + " i.status, iu.staffCode, iu.chineseName, t.timeLogId "
            + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		//Delink - Filter delink table Refer Month
		if(delinkDateStr != null && !delinkDateStr.isEmpty()) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date delinkDate = new Date();
			try {
				delinkDate = formatter.parse(delinkDateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			query.setParameter("delinkDate", delinkDate);			
		}
		
		if (date != null) {
			query.setParameter("date", date);
		}
		if (supervisorId != null && supervisorId.size() > 0) {
			query.setParameterList("supervisorId", supervisorId);
		}
		if (status != null && status.length > 0) {
			query.setParameterList("status", status);
		}
		if (filterUserId != null) {
			query.setParameter("filterUserId", filterUserId);
		}
		if (fieldOfficerId != null) {
			query.setParameter("fieldOfficerId", fieldOfficerId);
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanTableList.class));

		return query.list();
	}
	
	public long countLookupTableList(String search, Date date, List<Integer> supervisorId, Integer filterUserId, Integer fieldOfficerId, String[] status , String delinkDateStr) {

		String planDate = String.format("FORMAT(i.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select count(distinct i.itineraryPlanId) "
                + " from ItineraryPlan as i "
                + " left join i.user as iu "
                + " left join iu.supervisor as su "
                + " where (str(i.itineraryPlanId) like :search or "+planDate+" like :search "
                + " or i.status like :search"
                + " or iu.staffCode like :search "
                + " or iu.chineseName like :search "
                + " ) "
                + (date != null ?  " and i.date = :date " : "");
		if (fieldOfficerId !=null && supervisorId!=null && supervisorId.size() > 0){
			hql += " and (su.userId in (:supervisorId) or iu.userId = :fieldOfficerId) " ;
		}
		else if (fieldOfficerId !=null){
			hql += " and iu.userId = :fieldOfficerId " ;
		}
		else if (supervisorId!=null && supervisorId.size() > 0){
			hql += " and su.userId in (:supervisorId) " ;
		}                
               
		if (delinkDateStr != null && !delinkDateStr.isEmpty()) {
			hql += " and i.date > :delinkDate ";
		}

		hql += (filterUserId != null ? " and iu.userId = :filterUserId " : "")
			    + ((status != null && status.length > 0)  ? " and i.status in (:status) " : "");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		// Delink - Filter delink table Refer Month
		if (delinkDateStr != null && !delinkDateStr.isEmpty()) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date delinkDate = new Date();
			try {
				delinkDate = formatter.parse(delinkDateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			query.setParameter("delinkDate", delinkDate);
		}

		if (date != null) {
			query.setParameter("date", date);
		}
		if (supervisorId != null && supervisorId.size() > 0) {
			query.setParameterList("supervisorId", supervisorId);
		}
		if (status != null && status.length > 0) {
			query.setParameterList("status", status);
		}
		if (filterUserId != null) {
			query.setParameter("filterUserId", filterUserId);
		}
		if (fieldOfficerId != null) {
			query.setParameter("fieldOfficerId", fieldOfficerId);
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ItineraryPlanTableList> getApprovalTableList(String search,
			int firstRecord, int displayLenght, Order order, 
		    Date date, List<Integer> supervisorId, Integer filterUserId,
		    String[] status) {

		String planDate = String.format("FORMAT(i.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select i.itineraryPlanId as id, "
				+ " i.version as version, "
                + " case when i.date is null then '' else "+planDate+" end as date, "
                + " count(distinct ia.assignmentId) as noOfAssignment, "
                + " i.status as status,  "
                + " iu.staffCode as fieldOfficerCode, "
                + " iu.chineseName as chineseName, "
                + " t.timeLogId as timeLogId "
                + " from ItineraryPlan as i "
                + " left join i.assignments as ia "
                + " left join i.user as iu "
                + " left join iu.supervisor as su "
                + " left join i.timeLogMap as map "
                + " left join map.timeLog as t on t.status not in ('Draft', 'Rejected') "
                + " where (str(i.itineraryPlanId) like :search or "+planDate+" like :search "
                + " or i.status like :search"
                + " or iu.staffCode like :search "
                + " or iu.chineseName like :search "
                + " ) "
                + (date != null ?  " and i.date = :date " : "");
		if (supervisorId!=null && supervisorId.size() > 0){
			hql += " and i.supervisor.id in (:supervisorId) " ;
		}              
               
        hql += (filterUserId != null? " and iu.userId = :filterUserId ":"") 
            + ((status != null && status.length > 0)  ? " and i.status in (:status) " : "")
            + " group by i.itineraryPlanId, i.version, i.date, "
            + " i.status, iu.staffCode, iu.chineseName, t.timeLogId "
            + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		if (date != null) {
			query.setParameter("date", date);
		}
		if (supervisorId != null && supervisorId.size() > 0) {
			query.setParameterList("supervisorId", supervisorId);
		}
		if (status != null && status.length > 0) {
			query.setParameterList("status", status);
		}
		if (filterUserId != null) {
			query.setParameter("filterUserId", filterUserId);
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanTableList.class));

		return query.list();
	}
	
	public long countLookupApprovalTableList(String search, Date date, List<Integer> supervisorId, Integer filterUserId, String[] status) {

		String planDate = String.format("FORMAT(i.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select count(distinct i.itineraryPlanId) "
                + " from ItineraryPlan as i "
                + " left join i.user as iu "
                + " left join iu.supervisor as su "
                + " where (str(i.itineraryPlanId) like :search or "+planDate+" like :search "
                + " or i.status like :search"
                + " or iu.staffCode like :search "
                + " or iu.chineseName like :search "
                + " ) "
                + (date != null ?  " and i.date = :date " : "");
		if (supervisorId!=null && supervisorId.size() > 0){
			hql += " and i.supervisor.id in (:supervisorId) " ;
		}        
               
        hql += (filterUserId != null? " and iu.userId = :filterUserId ":"") 
            + ((status != null && status.length > 0)  ? " and i.status in (:status) " : "");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		if (date != null) {
			query.setParameter("date", date);
		}
		if (supervisorId != null && supervisorId.size() > 0) {
			query.setParameterList("supervisorId", supervisorId);
		}
		if (status != null && status.length > 0) {
			query.setParameterList("status", status);
		}
		if (filterUserId != null) {
			query.setParameter("filterUserId", filterUserId);
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Date> getPlanDateByOfficerId(Integer officerId) {
		
		Criteria criteria = this.createCriteria("p").createAlias("p.user","u", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("p.date"),"date");
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		
		criteria.setProjection(projList).add(Restrictions.eq("u.userId", officerId));
		criteria.setProjection(projList).add(Restrictions.ge("p.date", today));

		criteria.addOrder(Order.asc("p.date"));

		return (List<Date>) criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItineraryPlanOutletModel> getPlanOutlet(Integer officerId, List<Integer> outletIds, Date planDate, List<Integer> assignmentIds){
		String assignedCollectionDate = String.format("FORMAT(qr.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select o.outletId as id, "
				+ " o.outletId as outletId, "
				+ " concat(o.firmCode,'') as firmCode, "
				+ " o.name as firm, "
				+ " o.marketName as marketName, "
				+ " d.districtId as districtId, "
				+ " concat(d.code , ' - ', d.chineseName) as district, "
				+ " tpu.tpuId as tpuId, "
				+ " tpu.code as tpu, "
				+ " o.streetAddress as address, "
				+ " o.detailAddress as detailAddress, "
				+ " count(distinct qr.quotationRecordId) as noOfQuotation, "
                + " count(distinct a.assignmentId) as noOfAssignment, "
				+ " o.convenientStartTime as convenientStartTime, "
				+ " o.convenientEndTime as convenientEndTime, "
				+ " o.convenientStartTime2 as convenientStartTime2, "
				+ " o.convenientEndTime2 as convenientEndTime2, "
				+ " o.latitude as latitude, "
				+ " o.longitude as longitude, "
				+ " o.remark as outletRemark, "
                + " Min(case when a.assignedCollectionDate is not null then "+assignedCollectionDate+" else (case when a.endDate is not null then "+endDate+" else '' end) end) as deadline, "
                + " ABS(1) as planType "
                + ", count(distinct case when a.assignedCollectionDate is not null "
					+ " then " + assignedCollectionDate + " else ( "
					+ " case when a.endDate is not null "
					+ " then " + endDate + " else '' end) "
					+ " end) as countAssignedDate, "
				+ " a.referenceNo as referenceNo"
				+ " from Assignment as a"
				+ " inner join a.outlet as o"
				+ " inner join o.tpu as tpu"
				+ " left join tpu.district as d "
                + " left join a.quotationRecords as qr "
	                + "	on qr.isBackNo = false"
					+ " and qr.isBackTrack = false"
                + " left join qr.user as u "
                + " where 1=1";
		
		if(officerId!=null)
			hql += " and u.userId = :userId";
		
		if(planDate!=null){
			hql	+= " and (qr.status not in ( :status ) "
					+ "	or qr.status in ( :status ) and qr.availability = 2)"
					+" and ((qr.assignedCollectionDate is null and qr.assignedStartDate <= :planDate )"
					+ " or (qr.assignedCollectionDate is not null and qr.assignedCollectionDate <= :planDate ))";
		}
		
		if(outletIds!=null && outletIds.size()>0)
			hql += " and o.outletId in :outletIds";
		
		if(assignmentIds!=null && assignmentIds.size()>0)
			hql += " and a.assignmentId in :assignmentIds";
			
		hql += " group by o.outletId, concat(o.firmCode,''), o.name, o.marketName, d.districtId, concat(d.code , ' - ', d.chineseName), "
				+ " tpu.tpuId, tpu.code, o.streetAddress, o.convenientStartTime, o.convenientEndTime, o.convenientStartTime2, o.convenientEndTime2, "
				+ " o.latitude, o.longitude, o.remark,  o.detailAddress, a.referenceNo "
				+ " order by o.outletId asc";
		
		Query query = this.getSession().createQuery(hql);
		
		if(officerId!=null)
			query.setParameter("userId", officerId);
		
		if(planDate!=null){
			query.setParameterList("status", new String[]{"Approved", "Submitted"});
			query.setParameter("planDate", planDate);
		}
		
		if(outletIds!=null && outletIds.size()>0)
			query.setParameterList("outletIds", outletIds);
		
		if(assignmentIds!=null && assignmentIds.size()>0)
			query.setParameterList("assignmentIds", assignmentIds);
		
		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanOutletModel.class));
		
		return query.list();
	}
	
//	@SuppressWarnings("unchecked")
//	public List<ItineraryPlanOutletModel> getPlanOutlet(Integer officerId, List<Integer> outletIds, 
//			Date planDate, Date collectionDate, List<Integer> assignmentIds) {
//		
//		String assignedCollectionDate = String.format("FORMAT(qr.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//
////		String convenientStartTime = String.format("FORMAT(o.convenientStartTime, '%s', 'en-us')", SystemConstant.TIME_FORMAT);
////		String convenientEndTime = String.format("FORMAT(o.convenientEndTime, '%s', 'en-us')", SystemConstant.TIME_FORMAT);
//
//		String whereClase = "";
//		if (collectionDate != null){
//			whereClase += " and qr.assignedCollectionDate = :collectionDate and (qr.status = 'Draft' or qr.status = 'Blank' or qr.status='Rejected') ";
//		}
//		if (collectionDate == null && planDate != null){
//			whereClase += " and ( "
//						+ "( "
//							+ " ( qr.assignedCollectionDate is null and  qr.assignedStartDate <= :planDate and  qr.assignedEndDate > :planDate) "
//							+ " or (qr.assignedCollectionDate is not null and qr.assignedCollectionDate = :planDate) "
//							+ " or qr.quotationState = 'Verify' "
//						+ ") "
//						+ " and (qr.status = 'Draft' or qr.status = 'Blank' or qr.status='Rejected') "
//						+ " or qr.availability = 2 "
//					+ ")";
//		}
//		if (outletIds != null && outletIds.size() > 0){
//			whereClase += " and o.outletId in ( :outletIds ) ";
//		}
//		if (officerId != null){
//			whereClase += " and u.userId = :officerId ";
//		}
//		if (assignmentIds != null){
//			whereClase += " and a.assignmentId in (:assignmentIds) ";
//		}
//		
//		String from = " Assignment a "
//                + " inner join a.outlet as o "               
//                + " inner join o.tpu as tpu "
//               // + " left join o.quotations as q "
//                + " left join tpu.district as d "
//                + " left join a.quotationRecords as qr "
//                + " left join qr.user as u ";
//		
//		String hql = "select o.outletId as id, "
//				+ " o.outletId as outletId, "
//				+ " concat(o.firmCode,'') as firmCode, "
//				+ " o.name as firm, "
//				+ " o.marketName as marketName, "
//				+ " d.districtId as districtId, "
//				+ " concat(d.code , ' - ', d.chineseName) as district, "
//				+ " tpu.tpuId as tpuId, "
//				+ " tpu.code as tpu, "
//				+ " o.streetAddress as address, "
//				+ " o.detailAddress as detailAddress, "
//				+ " count(distinct qr.quotationRecordId) as noOfQuotation, "
//                + " count(distinct a.assignmentId) as noOfAssignment, "
//				+ " o.convenientStartTime as convenientStartTime, "
//				+ " o.convenientEndTime as convenientEndTime, "
//				+ " o.convenientStartTime2 as convenientStartTime2, "
//				+ " o.convenientEndTime2 as convenientEndTime2, "
//				+ " o.latitude as latitude, "
//				+ " o.longitude as longitude, "
//				+ " o.remark as outletRemark, "
//                + " Min(case when a.assignedCollectionDate is not null then "+assignedCollectionDate+" else (case when a.endDate is not null then "+endDate+" else '' end) end) as deadline, "
//               // + " a.status as status, "
//                + " ABS(1) as planType "
//                + ", count(distinct case when a.assignedCollectionDate is not null "
//				+ " then " + assignedCollectionDate + " else ( "
//				+ " case when a.endDate is not null "
//				+ " then " + endDate + " else '' end) "
//				+ " end) as countAssignedDate "
//                + " from "
//                + from
//                + " where 1=1 and qr.isBackTrack = false and qr.isBackNo = false "
//                + whereClase
//                + " group by o.outletId, concat(o.firmCode,''), o.name, o.marketName, d.districtId, concat(d.code , ' - ', d.chineseName), "
//                + " tpu.tpuId, tpu.code, o.streetAddress, o.convenientStartTime, o.convenientEndTime, o.convenientStartTime2, o.convenientEndTime2, "
//                + " o.latitude, o.longitude, o.remark,  o.detailAddress "
//               // + " case when a.assignedCollectionDate is not null then "+assignedCollectionDate+" else (case when a.endDate is not null then "+endDate+" else '' end) end, "
//              //  + " a.status "
//                + " order by o.outletId asc";
//		
//		Query query = getSession().createQuery(hql);
//
//		if (collectionDate != null) {
//			query.setParameter("collectionDate", collectionDate);
//		}
//		if (planDate != null) {
//			query.setParameter("planDate", planDate);
//		}
//		if (officerId != null) {
//			query.setParameter("officerId", officerId);
//		}
//		if (outletIds != null && outletIds.size() > 0) {
//			query.setParameterList("outletIds", outletIds);
//		}
//		
//		if (assignmentIds != null) {
//			query.setParameterList("assignmentIds", assignmentIds);
//		}
//		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanOutletModel.class));
//
//		ArrayList<ItineraryPlanOutletModel> iteinaryPlanAssignment =  (ArrayList<ItineraryPlanOutletModel>) query.list();
//		
//		 
//		return iteinaryPlanAssignment;
//	}
	
	
	@SuppressWarnings("unchecked")
	public List<ItineraryPlanOutletModel> getImportedAssignment(Integer officerId, List<Integer> assignmentIds, Date planDate, Date collectionDate) {
		
		//String assignedCollectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(s.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);


		String hql = "select a.assignmentId as id, "
				+ " a.additionalFirmNo as firmCode, "
				+ " a.additionalFirmName as firm, "
				+ " d.districtId as districtId, "
				+ " concat(d.code , ' - ', d.chineseName) as district, "
				+ " tpu.tpuId as tpuId, "
				+ " tpu.code as tpu, "
				+ " a.additionalFirmAddress as address, "
				+ " a.additionalFirmAddress as detailAddress, "
                + " count(a.assignmentId) as noOfAssignment, "
				+ " a.additionalLatitude as latitude, "
				+ " a.additionalLongitude as longitude, "
                + " case when s.endDate is null then '' else "+endDate+" end as deadline, "
               // + " a.status as status "
                + " ABS(2) as planType,"
                + " a.referenceNo as referenceNo "
                + " from Assignment a "
                + " inner join a.surveyMonth s "
                + " inner join a.user as u "
                + " inner join a.additionalTpu as tpu "
                + " left join tpu.district as d "
                + " where "
                + " 1 = 1 "
                + (assignmentIds != null && assignmentIds.size() > 0 ? " and a.assignmentId in ( :assignmentIds ) " : "")
                
               // + (collectionDate != null ?  " and a.assignedCollectionDate = :collectionDate " : "")
                + (planDate != null ? " and s.endDate >= :planDate " : "")
                + (officerId != null ?  " and u.userId = :officerId " : "")
                + " group by a.assignmentId, a.additionalFirmNo, a.additionalFirmName, d.districtId, "
                + " concat(d.code , ' - ', d.chineseName), tpu.tpuId, tpu.code, "
                + " a.additionalFirmAddress, a.additionalLatitude, a.additionalLongitude, "
                + " s.endDate, a.referenceNo "
                + " order by firmCode asc";

		Query query = getSession().createQuery(hql);

		if (collectionDate != null) {
			query.setParameter("collectionDate", collectionDate);
		}
		if (planDate != null) {
			query.setParameter("planDate", planDate);
		}
		if (officerId != null) {
			query.setParameter("officerId", officerId);
		}
		if (assignmentIds != null && assignmentIds.size() > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanOutletModel.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItineraryPlan> getItineraryByIds(List<Integer> planIds) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.in("itineraryPlanId", planIds));
		return (List<ItineraryPlan>) criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItineraryPlan> getItineraryByIdsDate(List<Integer> planIds, Date fromDate, Date toDate) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.in("itineraryPlanId", planIds));
		criteria.add(Restrictions.between("date", fromDate, toDate));
		return (List<ItineraryPlan>) criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getReferenceList(Integer userId) {
		Criteria criteria = this.createCriteria("ip")
								.createAlias("ip.assignments", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.user", "u", JoinType.LEFT_OUTER_JOIN)
								.addOrder(Order.asc("a.referenceNo"));

//		criteria.setProjection(Projections.property("a.referenceNo"));
		criteria.setProjection(Projections.distinct(Projections.property("a.referenceNo")));

		if(userId != null && userId > 0) {
			criteria.add(Restrictions.eq("u.userId", userId));
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getReferenceList(Integer userId, Date planDate) {
		
		Criteria criteria = this.createCriteria("ip")
				.createAlias("ip.outlets", "o")
				.createAlias("ip.user", "u", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.and(Restrictions.eq("u.userId", userId), Restrictions.eq("ip.date", planDate)));
		criteria.setProjection(Projections.property("o.referenceNo"));
		Order order = Order.asc("o.majorLocationSequence");
		Order order2 = Order.asc("o.sequence");
		criteria.addOrder(order);
		criteria.addOrder(order2);
		return criteria.list();
//		Criteria criteria = this.createCriteria("ip")
//								.createAlias("ip.assignments", "a", JoinType.LEFT_OUTER_JOIN)
//								.createAlias("ip.user", "u", JoinType.LEFT_OUTER_JOIN);
//		
//		criteria.add(Restrictions.and(Restrictions.eq("u.userId", userId), Restrictions.eq("ip.date", planDate)));
//		criteria.setProjection(Projections.groupProperty("a.referenceNo"));
//		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getTpuList(Integer userId, Date planDate) {
		
		
		String hql = "select case when planType = 1 then tpu.code else addTpu.code end as tpu"
				+ " from ItineraryPlan as plan "
				+ " left join plan.outlets as po"
				+ " left join po.outlet as o  "
				+ " left join o.tpu as tpu "
				+ " left join po.assignment as a "
				+ " left join a.additionalTpu as addTpu "
				+ " where plan.user.userId = :userId and plan.date = :planDate "
				+ " order by po.majorLocationSequence asc, po.sequence asc ";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		query.setParameter("planDate", planDate);
		return query.list();
		
//		Criteria criteria = this.createCriteria("ip")
//								.createAlias("ip.user", "u", JoinType.INNER_JOIN)
//								.createAlias("ip.majorLocations", "m", JoinType.INNER_JOIN)
//								.createAlias("m.outlets", "po", JoinType.INNER_JOIN)
//								.createAlias("po.outlet", "o", JoinType.INNER_JOIN)
//								.createAlias("o.tpu", "t", JoinType.INNER_JOIN)
//								.createAlias("o.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN)
//								.createAlias("qr.assignment", "a", JoinType.LEFT_OUTER_JOIN);
//		
//		criteria.add(Restrictions.and(Restrictions.eq("u.userId", userId), Restrictions.eq("ip.date", planDate), Restrictions.not(Restrictions.like("a.referenceNo", "NR-%"))));
//		criteria.setProjection(Projections.property("t.code"));
//		criteria.addOrder(Order.asc("m.sequence")).addOrder(Order.asc("po.sequence"));
//		return criteria.list();
	}
	
	public Integer getMajorLocationIdByReferenceNo(Integer userId, Date planDate, String referenceNo) {
		Criteria criteria = this.createCriteria("ip")
								.createAlias("ip.user", "u", JoinType.INNER_JOIN)
								.createAlias("ip.majorLocations", "m", JoinType.INNER_JOIN)
								.createAlias("m.outlets", "o", JoinType.INNER_JOIN);
								//.createAlias("o.assignment", "a", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.and(Restrictions.eq("u.userId", userId), Restrictions.eq("ip.date", planDate), Restrictions.eq("o.referenceNo", referenceNo)));
		criteria.setProjection(Projections.property("m.majorLocationId"));
		return (Integer)criteria.uniqueResult();
	}
	
	public long countPlanByOfficerIdAndDate( Integer fieldOfficerId, Date date) {

		String planDate = String.format("FORMAT(i.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select count(distinct i.itineraryPlanId) "
                + " from ItineraryPlan as i "
                + " left join i.user as iu "
                + " where i.date = :date "
                + " and i.user.userId = :fieldOfficerId "
        		+ " and i.status != 'Cancelled'";

		Query query = getSession().createQuery(hql);

		query.setParameter("date", date);
		query.setParameter("fieldOfficerId", fieldOfficerId);

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<Integer> getTimeLogNotSubmittedItineraryPlan(){
		Query query = this.getSession().createSQLQuery("exec [GetTimeLogNotSubmittedItineraryPlan]");
		return query.list();
	}
	
	public List<String> getFuturePlanDate(Date today, Integer userId){
		String planDate = String.format("FORMAT(date, '%s', 'en-us') as date", SystemConstant.DATE_FORMAT);
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("date", today));
		criteria.add(Restrictions.eq("user.userId", userId));
		criteria.setProjection(SQLProjectionExt.sqlProjection(planDate, new String[]{"date"}, new Type[]{StandardBasicTypes.STRING}));
		
		return criteria.list();
	}
	
	public List<ItineraryPlanSyncData> getUpdateItineraryPlan(Date lastSyncTime, Integer[] itineraryPlanIds){
		Criteria criteria = this.createCriteria("i")
				.createAlias("i.user", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("i.supervisor", "s", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.and(
				Restrictions.ge("i.modifiedDate", lastSyncTime),
				Restrictions.in("i.itineraryPlanId", itineraryPlanIds)));
		
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("i.itineraryPlanId"), "itineraryPlanId");
		projList.add(Projections.property("i.date"), "date");
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("s.userId"), "supervisorId");
		projList.add(Projections.property("i.session"), "session");
		projList.add(Projections.property("i.createdDate"), "createdDate");
		projList.add(Projections.property("i.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("i.status"), "status");
		projList.add(Projections.property("i.rejectReason"), "rejectReason");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ItineraryPlanSyncData.class));
		return criteria.list();
	}
	
	public List<ItineraryPlanAssignmentSyncData> getUpdateItineraryPlanAssignment(Date lastSyncTime, Integer[] itineraryPlanIds){
		String sql = "Select ipa.assignmentId as assignmentId, ipa.itineraryPlanId as itineraryPlanId"
				+ ", ipa.createdDate as createdDate, ipa.modifiedDate as modifiedDate"
				+ ", count(qr.quotationRecordId) as numOfQuotationRecords"
				+ " from ItineraryPlanAssignment as ipa"
				+ " left join Assignment as a on ipa.assignmentId = a.assignmentId"
				+ " left join QuotationRecord as qr on a.assignmentId = qr.assignmentId"
					+ " and qr.[IsBackNo] = 0 and qr.[IsBackTrack] = 0"
				+ " where ipa.modifiedDate >= :date"
				+ " and ipa.itineraryPlanId in ( :itineraryPlanIds )"
				+ " group by ipa.assignmentId, ipa.itineraryPlanId"
				+ " , ipa.createdDate, ipa.modifiedDate";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("assignmentId", StandardBasicTypes.INTEGER)
				.addScalar("itineraryPlanId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("numOfQuotationRecords", StandardBasicTypes.INTEGER);
		
		if(lastSyncTime==null){
			query.setParameter("date", "");
		} else {
			query.setParameter("date", lastSyncTime);
		}
		
		query.setParameterList("itineraryPlanIds", itineraryPlanIds);
		
		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanAssignmentSyncData.class));
		
		return query.list();
		
	}
	
	public List<ItineraryUnPlanAssignmentSyncData> getUpdateItineraryUnPlanAssignment(Date lastSyncTime, Integer[] itineraryPlanIds){
		String sql = "Select a.assignmentId as assignmentId, a.itineraryPlanId as itineraryPlanId"
				+ ", a.createdDate as createdDate, a.modifiedDate as modifiedDate"
				+ " from ItineraryUnPlanAssignment a where a.modifiedDate >= :date"
				+ " and a.itineraryPlanId in ( :itineraryPlanIds )";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("assignmentId", StandardBasicTypes.INTEGER)
				.addScalar("itineraryPlanId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		
		if(lastSyncTime==null){
			query.setParameter("date", "");
		} else {
			query.setParameter("date", lastSyncTime);
		}
		
		query.setParameterList("itineraryPlanIds", itineraryPlanIds);
		
		query.setResultTransformer(Transformers.aliasToBean(ItineraryUnPlanAssignmentSyncData.class));
		
		return query.list();
		
	}
	
	public Integer getPlanId(Integer userId, Date date) {
		Criteria criteria = this.createCriteria("i")
				.createAlias("i.user", "u", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.and(
				Restrictions.eq("i.date", date),
				Restrictions.eq("u.userId", userId)));

		criteria.setProjection(Projections.property("i.itineraryPlanId"));
		
		return (Integer)criteria.uniqueResult();
	}
	
	public List<ItineraryPlanOnlineModel> houseKeepItineraryPlan(Integer[] userIds, Date date){
		String hql = "select ip.itineraryPlanId as itineraryPlanId"
				+ ", ip.date as date, u.userId as userId"
				+ ", s.userId as supervisorId, ip.session as session"
				+ ", ip.createdDate as createdDate, ip.modifiedDate as modifiedDate"
				+ ", ip.status as status, ip.rejectReason as rejectReason"
				+ " from ItineraryPlan as ip"
				+ " left join ip.user as u"
				+ " left join ip.supervisor as s"
				+ " where u.userId in ( :userIds )"
				+ " and ip.status = :status";
		
		if(date==null){
			hql += " and ip.date >= convert(date, getdate()))";
		} else {
			hql += " and ip.date = :date";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("userIds", userIds);
		query.setParameter("status", "Approved");
		if(date!=null){
			query.setParameter("date", date);
		}		
		query.setResultTransformer(Transformers.aliasToBean(ItineraryPlanOnlineModel.class));
		return query.list();
	}
	
	public List<String> getDistinctReferenceNo(Integer userId, Date date){
		
		String hql = "select distinct o.referenceNo as referenceNo "
				+ " from ItineraryPlanOutlet as o "
				+ " inner join o.itineraryPlan as p "
				+ " where p.user.userId = :userId and p.date = :date ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		query.setParameter("date", date);
		return query.list();
	}
}
