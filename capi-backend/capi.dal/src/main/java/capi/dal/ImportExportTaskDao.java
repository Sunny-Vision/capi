package capi.dal;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.entity.ImportExportTask;
import capi.model.SystemConstant;
import capi.model.dataImportExport.ImportTaskList;

@Repository("ImportExportTaskDao")
public class ImportExportTaskDao  extends GenericDao<ImportExportTask>{

	private static final int NUM_OF_EXPORT_FILES_KEEP_LATEST = 1000;

	public List<ImportTaskList> getImportTaskList(String search, int firstRecord, int displayLength, Order order){		
		return  getTaskList(search, firstRecord, displayLength, order, SystemConstant.TASK_TYPE_IMPORT, null);
	}
	
	public long countImportTaskList(String search){		
		return  countTaskList(search, SystemConstant.TASK_TYPE_IMPORT, null);
	}

	@Deprecated
	public List<ImportTaskList> getExportTaskList(String search, int firstRecord, int displayLength, Order order){
		return  getTaskList(search, firstRecord, displayLength, order, SystemConstant.TASK_TYPE_EXPORT, null);
	}
	public List<ImportTaskList> getExportTaskList(String search, int firstRecord, int displayLength, Order order, Integer userId){		
		return  getTaskList(search, firstRecord, displayLength, order, SystemConstant.TASK_TYPE_EXPORT, userId);
	}

	@Deprecated
	public long countExportTaskList(String search){
		return  countTaskList(search, SystemConstant.TASK_TYPE_EXPORT, null);
	}
	public long countExportTaskList(String search, Integer userId){		
		return  countTaskList(search, SystemConstant.TASK_TYPE_EXPORT, userId);
	}
	
	private List<ImportTaskList> getTaskList(String search, int firstRecord, int displayLength, Order order, String taskType, Integer userId){
		
		Criteria criteria = this.createCriteria("t");
		criteria.createAlias("t.taskDefinition", "def");
		if (userId != null && userId > 0){
			criteria.createAlias("t.user", "u");
		}
		
		String dateStrFormat = "FORMAT({alias}.%s, '%s', 'en-us')";
		String startDateStr = String.format(dateStrFormat, "startDate", SystemConstant.DATE_TIME_FORMAT);
		String endDateStr = String.format(dateStrFormat, "finishedDate", SystemConstant.DATE_TIME_FORMAT);
		
		ProjectionList list = Projections.projectionList();
		list.add(Projections.sqlProjection(startDateStr+" as startDate", new String[]{"startDate"}, new Type[]{StandardBasicTypes.STRING} ),"startDate")
			.add(Projections.sqlProjection(endDateStr+" as finishedDate", new String[]{"finishedDate"}, new Type[]{StandardBasicTypes.STRING} ),"finishedDate")
			.add(Projections.property("t.status"),"status")
			.add(Projections.property("t.errorMessage"),"errorMessage")
			.add(Projections.property("def.taskName"),"taskName")
			.add(Projections.property("t.importExportTaskId"),"importExportTaskId")
			.add(Projections.property("def.taskNo"),"taskNo");
		
		criteria.setProjection(list);
		
		if (userId != null && userId > 0){
			criteria.add(Restrictions.eq("u.id", userId));
		}
		if (!StringUtils.isEmpty(taskType)){
			criteria.add(Restrictions.eq("t.taskType", taskType));
		}
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.or(
					Restrictions.like("t.errorMessage", "%"+search+"%"),
					Restrictions.like("def.taskName", "%"+search+"%"),
					Restrictions.like("t.status", "%"+search+"%"),
					Restrictions.sqlRestriction(startDateStr+" like ? ", "%"+search+"%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction(endDateStr+" like ? ", "%"+search+"%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction("importExportTaskId like ? ", "%"+search+"%", StandardBasicTypes.STRING)
				));
		}
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		criteria.addOrder(order);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ImportTaskList.class));		
		
		return criteria.list();
	}
	
	private long countTaskList(String search, String taskType, Integer userId){
		
		Criteria criteria = this.createCriteria("t");
		criteria.createAlias("t.taskDefinition", "def");
		if (userId != null && userId > 0){
			criteria.createAlias("t.user", "u");
		}
		
		String dateStrFormat = "FORMAT({alias}.%s, '%s', 'en-us')";
		String startDateStr = String.format(dateStrFormat, "startDate", SystemConstant.DATE_FORMAT);
		String endDateStr = String.format(dateStrFormat, "finishedDate", SystemConstant.DATE_FORMAT);
		
		
		criteria.setProjection(Projections.count("t.importExportTaskId"));

		if (userId != null && userId > 0){
			criteria.add(Restrictions.eq("u.id", userId));
		}
		if (!StringUtils.isEmpty(taskType)){
			criteria.add(Restrictions.eq("t.taskType", taskType));
		}
		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.or(
					Restrictions.like("t.errorMessage", "%"+search+"%"),
					Restrictions.like("def.taskName", "%"+search+"%"),
					Restrictions.like("t.status", "%"+search+"%"),
					Restrictions.sqlRestriction(startDateStr+" like ? ", "%"+search+"%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction(endDateStr+" like ? ", "%"+search+"%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction("importExportTaskId like ? ", "%"+search+"%", StandardBasicTypes.STRING)
				));
		}
		
		return (long)criteria.uniqueResult();
	}
	
	public List<ImportExportTask> getObsoletedTask(){
		Criteria criteria = this.createCriteria("t");
		criteria.add(Restrictions.eq("t.taskType", "Export"));
		criteria.setFirstResult(NUM_OF_EXPORT_FILES_KEEP_LATEST);
		criteria.addOrder(Order.desc("t.startDate"));		
		return criteria.list();
	}
	
}
