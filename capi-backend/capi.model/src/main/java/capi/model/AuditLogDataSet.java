package capi.model;

import capi.audit.entity.AuditLog;
import capi.entity.EntityBase;

public class AuditLogDataSet {

	private AuditLog auditLog;
	
	private EntityBase entity;
	
	private EntityBase targetEntity;

	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

	public EntityBase getEntity() {
		return entity;
	}

	public void setEntity(EntityBase entity) {
		this.entity = entity;
	}

	public EntityBase getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(EntityBase targetEntity) {
		this.targetEntity = targetEntity;
	}
	
}
