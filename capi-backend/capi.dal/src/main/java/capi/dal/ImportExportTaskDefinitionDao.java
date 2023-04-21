package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.ImportExportTaskDefinition;
@Repository("ImportExportTaskDefinitionDao")
public class ImportExportTaskDefinitionDao extends GenericDao<ImportExportTaskDefinition>{

	public ImportExportTaskDefinition getDefinitionByTaskNo(Integer taskNo){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("taskNo", taskNo));
		return (ImportExportTaskDefinition)criteria.uniqueResult();
	}
	
	public List<ImportExportTaskDefinition> getImportDefinition(){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("hasImport", true));
		return criteria.list();
	}
	
	public List<ImportExportTaskDefinition> getExportDefinition(){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("hasExport", true));
		return criteria.list();
	}
}
