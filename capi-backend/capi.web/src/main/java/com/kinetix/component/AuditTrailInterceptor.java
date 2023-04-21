package com.kinetix.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.OptimisticLockException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Map;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kinetix.controller.AccessController;

import capi.audit.entity.AuditLog;
import capi.entity.EntityBase;
import capi.entity.OptimisticLockEntity;
import capi.entity.ViewBase;
import capi.model.AuditLogDataSet;

@Component("auditTrailInterceptor")
public class AuditTrailInterceptor extends EmptyInterceptor{
	
	private static final Logger logger = LoggerFactory.getLogger(AccessController.class);
	
	@Resource(name="sessionFactory_audit")
	private SessionFactory sessionFactory;
	
	private static final ThreadLocal<List<AuditLogDataSet>> auditLogs = new ThreadLocal<List<AuditLogDataSet>>(){
		@Override
		protected List<AuditLogDataSet> initialValue(){
			return new ArrayList<AuditLogDataSet>();
		}
	};
	
	@Override
	public boolean onFlushDirty(
			Object entity, 
			Serializable id, 
			Object[] currentState, 
			Object[] previousState, 
			String[] propertyNames, 
			Type[] types) {
		
		if (!(entity instanceof EntityBase)){
			return false;
		}
		
		if (entity instanceof OptimisticLockEntity){
			Integer oldVerison = (Integer)getValue(previousState, propertyNames, "version");
			Integer newVerison = (Integer)getValue(currentState, propertyNames, "version");
			if (oldVerison != null && newVerison != null &&!oldVerison.equals(newVerison)){
				throw new OptimisticLockException();
			}
		}
		
		EntityBase update = (EntityBase)entity;
		String modified = getActorName();
		
		setValue(currentState, propertyNames, "createdBy", getValue(previousState, propertyNames, "createdBy"));
		setValue(currentState, propertyNames, "createdDate", getValue(previousState, propertyNames, "createdDate"));
		
		setValue(currentState, propertyNames, "modifiedBy", modified);
		
		if (!update.isByPassModifiedDate()){
			setValue(currentState, propertyNames, "modifiedDate", new Date());
		}		
		
		if (update.isByPassLog()){
			return false;
		}
				
		List<AuditLogDataSet> list = new ArrayList<AuditLogDataSet>();
		Date now = new Date();
		
		for (int i =0; i < currentState.length; i++){			
			AuditLog header = new AuditLog();
			header.setAction("Update");
			header.setActor(modified);
			header.setCreatedDate(now);
			header.setEntityName(entity.getClass().getSimpleName());
			
			String property = propertyNames[i];
//			if (property.equals("modifiedDate")){
//				continue;
//			}
			try {
				Class<?> clasz = entity.getClass().getDeclaredField(property).getType();
				if (Set.class.isAssignableFrom(clasz)
						|| Map.class.isAssignableFrom(clasz)
						|| List.class.isAssignableFrom(clasz)
						|| Bag.class.isAssignableFrom(clasz)){
					continue;
				}
			} catch (Exception e) {	} 
			
			if (previousState == null){
				if (currentState[i]!=null && currentState[i] instanceof EntityBase){
					EntityBase base = (EntityBase)currentState[i];
					header.setColumnValue(String.valueOf(base.getId()));
				}
				else{
					String str = StringUtils.abbreviate(String.valueOf(currentState[i]), 4000);
					header.setColumnValue(str);
				}
				AuditLogDataSet set = new AuditLogDataSet();
				set.setAuditLog(header);
				set.setEntity((EntityBase)entity);				
				list.add(set);
			}
			else if (previousState[i] != null && currentState[i] != null){
				if (!previousState[i].equals(currentState[i])){
					header.setColumnName(propertyNames[i]);
					AuditLogDataSet set = new AuditLogDataSet();
					
					if (previousState[i] instanceof EntityBase){
						EntityBase prev = (EntityBase)previousState[i];
						EntityBase cur = (EntityBase)currentState[i];
						set.setTargetEntity(cur);
						header.setOldValue(String.valueOf(prev.getId()));
					}
					else{
						String str = StringUtils.abbreviate(String.valueOf(currentState[i]), 4000);
						header.setColumnValue(str);
						str = StringUtils.abbreviate(String.valueOf(previousState[i]), 4000);
						header.setOldValue(str);
					}
					
					set.setAuditLog(header);
					set.setEntity((EntityBase)entity);					
					list.add(set);
				}
			}
			else if (previousState[i] !=  currentState[i]){
				header.setColumnName(propertyNames[i]);
				if (currentState[i] != null){
					if (currentState[i] instanceof EntityBase){
						EntityBase base = (EntityBase)currentState[i];
						header.setColumnValue(String.valueOf(base.getId()));
					}
					else{
						String str = StringUtils.abbreviate(String.valueOf(currentState[i]), 4000);
						header.setColumnValue(str);
					}
				}	
				if (previousState[i] != null){
					if (previousState[i] instanceof EntityBase){
						EntityBase base = (EntityBase)previousState[i];
						header.setOldValue(String.valueOf(base.getId()));
					}
					else{
						String str = StringUtils.abbreviate(String.valueOf(previousState[i]), 4000);
						header.setOldValue(str);
					}
				}

				AuditLogDataSet set = new AuditLogDataSet();
				set.setAuditLog(header);
				set.setEntity((EntityBase)entity);				
				list.add(set);
			}			
			
		}
		
		if (list.size() == 1 && list.get(0).getAuditLog().getColumnName().equals("modifiedDate")){
			return false;
		}
		
		auditLogs.get().addAll(list);
		
		return false;
	}
	
	@Override
	public boolean onSave(
			Object entity, 
			Serializable id, 
			Object[] state, 
			String[] propertyNames, 
			Type[] types) {
		
		if (!(entity instanceof EntityBase)){
			return false;
		}
		
		Date now = new Date();
		EntityBase base = (EntityBase)entity;
		String created = getActorName();
		base.setCreatedBy(created);
		base.setModifiedBy(created);
		base.setCreatedDate(now);
		
		setValue(state, propertyNames, "modifiedBy", created);
		setValue(state, propertyNames, "createdBy", created);
		setValue(state, propertyNames, "createdDate", now);
		
		if (!base.isByPassModifiedDate()){
			base.setModifiedDate(now);
			setValue(state, propertyNames, "modifiedDate", now);			
		}
		
		if (base.isByPassLog()){
			return false;
		}
		
		List<AuditLogDataSet> list = auditLogs.get();
		
		for (int i =0; i < state.length; i++){
			AuditLog header = new AuditLog();
			header.setAction("Create");
			header.setActor(created);
			header.setCreatedDate(now);
			header.setEntityName(entity.getClass().getSimpleName());
			
			header.setColumnName(propertyNames[i]);
			AuditLogDataSet set = new AuditLogDataSet();
			if (state[i] != null){
				if (state[i] instanceof EntityBase){
					set.setTargetEntity((EntityBase)state[i]);
				}
				else{
					String str = StringUtils.abbreviate(String.valueOf(state[i]), 4000);
					header.setColumnValue(str);					
				}
			}		
			
			set.setAuditLog(header);
			set.setEntity((EntityBase)entity);
			
			list.add(set);
		}
		
		return false;
	}
	
	@Override
	public void onDelete(
			Object entity, 
			Serializable id, 
			Object[] state, 
			String[] propertyNames, 
			Type[] types) {
		if (!(entity instanceof EntityBase)){
			return;
		}
		
		EntityBase base = (EntityBase)entity;
		if (base.isByPassLog()){
			return;
		}
		
		String created = getActorName();
		List<AuditLogDataSet> list = auditLogs.get();
		AuditLog header = new AuditLog();
		header.setAction("Delete");
		header.setActor(created);
		header.setCreatedDate(new Date());
		header.setEntityId((Integer)id);
		header.setEntityName(entity.getClass().getSimpleName());
		
		AuditLogDataSet set = new AuditLogDataSet();
		set.setAuditLog(header);
		list.add(set);	
	}
	
	@Override
	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		if (collection instanceof PersistentSet){
			PersistentSet newValues = (PersistentSet) collection; 
          
          	Object owner = newValues.getOwner();   
          	String actor = getActorName();
          	Collection oldValues = ((java.util.Map)newValues.getStoredSnapshot()).values();
          	List<AuditLogDataSet> list = auditLogs.get();
  			
          	Date now = new Date();
  			String targetFields = newValues.getRole();
  			Pattern pattern = Pattern.compile("[^.][0-9a-zA-Z_]+$");
  			Matcher matcher = pattern.matcher(targetFields);  
  			if (matcher.find()){  							
  				MatchResult result = matcher.toMatchResult();
  				targetFields = result.group();  				
  			}
  			String originalClass = owner.getClass().getSimpleName();
  			if (owner instanceof EntityBase){
  				EntityBase base = (EntityBase)owner;
  	  			if (base.isByPassLog()){
  	  				return;
  	  			}
  			}
	  		//EntityBase base = (EntityBase)owner;
  			
  			for (Object newValue: newValues){
  				if (!oldValues.contains(newValue)){
  					AuditLog header = new AuditLog();  		  			
  		  			header.setEntityName(originalClass);
  		  			header.setActor(actor);
  		  			header.setCreatedDate(now);
  		  			header.setAction("Add Collection");
  		  			header.setColumnName(targetFields);
  		  			
  		  			AuditLogDataSet set = new AuditLogDataSet();
  		  			if (newValue instanceof EntityBase){
  		  				EntityBase targetBase = (EntityBase)newValue;
  		  				set.setTargetEntity(targetBase);
  		  			}
  		  			else if (newValue instanceof ViewBase){
  		  				header.setColumnValue(((ViewBase) newValue).getId());
  		  			}
	  				set.setAuditLog(header);
	  				set.setEntity((EntityBase)owner);
	  				list.add(set);	
  		  			 		  			
  				}
  			}
  			
  			for (Object oldValue: oldValues){
  				if (!newValues.contains(oldValue)){
  					AuditLog header = new AuditLog();  		  			
  		  			header.setEntityName(originalClass);
  		  			header.setActor(actor);
  		  			header.setCreatedDate(now);
  		  			header.setAction("Remove Collection");
  		  			header.setColumnName(targetFields);
	  		  		AuditLogDataSet set = new AuditLogDataSet();
	  		  		if (oldValue instanceof EntityBase){
		  				EntityBase targetBase = (EntityBase)oldValue;
		  				header.setColumnValue(String.valueOf(targetBase.getId()));
		  			}
		  			else if (oldValue instanceof ViewBase){
		  				header.setColumnValue(((ViewBase) oldValue).getId());
		  			}
	  		  		
	  				set.setAuditLog(header);
	  				set.setEntity((EntityBase)owner);
	  				list.add(set);		  			
  				}
  			}  			
  			
		}
	}
	
	@Override
	public void postFlush(Iterator entities) {
		Session session = sessionFactory.openSession();
		Transaction trans = session.beginTransaction();
		try{
			List<AuditLogDataSet> list = auditLogs.get();			
			for (AuditLogDataSet set : list){
				
				AuditLog log = set.getAuditLog();
				if (set.getEntity() != null){
					log.setEntityId(set.getEntity().getId());
				}
				if (set.getTargetEntity() != null){
					log.setColumnValue(String.valueOf(set.getTargetEntity().getId()));
				}
				session.save(log);
			}			
			session.flush();
			trans.commit();
		}
		catch(Exception ex){
			trans.rollback();
			logger.error("update audit log fail", ex);
		}
		finally{
			auditLogs.get().clear();
			auditLogs.remove();			
			session.close();
		}
	}

	private String getActorName(){
		SecurityContext context = SecurityContextHolder.getContext();
		if (context!=null){
			Authentication auth = context.getAuthentication();
			if (auth != null){
				return auth.getName();
			}			
		}		
		return null;
	}
	
	private void setValue(Object[] currentState, String[] propertyNames,
            String propertyToSet, Object value) {
		int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
		if (index >= 0) {
			currentState[index] = value;
		}
	}
	
	private Object getValue(Object[] currentState, String[] propertyNames,
            String propertyToSet) {
		int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
		if (index >= 0) {
			return currentState[index];
		}
		return null;
	}
	
	
}
