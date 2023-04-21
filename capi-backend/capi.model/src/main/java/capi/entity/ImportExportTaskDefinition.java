package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ImportExportTaskDefinition")
public class ImportExportTaskDefinition extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ImportExportTaskDefinitionId")
	private Integer importExportTaskDefinitionId;
	
	@Column(name="TaskNo")
	private Integer taskNo;
	
	@Column(name="TaskName")
	private String taskName;
	
	@Column(name="HasImport")
	private boolean hasImport;
	
	@Column(name="HasExport")
	private boolean hasExport;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getImportExportTaskDefinitionId();
	}

	public Integer getImportExportTaskDefinitionId() {
		return importExportTaskDefinitionId;
	}

	public void setImportExportTaskDefinitionId(Integer importExportTaskDefinitionId) {
		this.importExportTaskDefinitionId = importExportTaskDefinitionId;
	}

	public Integer getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(Integer taskNo) {
		this.taskNo = taskNo;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public boolean isHasImport() {
		return hasImport;
	}

	public void setHasImport(boolean hasImport) {
		this.hasImport = hasImport;
	}

	public boolean isHasExport() {
		return hasExport;
	}

	public void setHasExport(boolean hasExport) {
		this.hasExport = hasExport;
	}

}
