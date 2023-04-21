package capi.dal;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public abstract class GenericDao <T extends Serializable> {
	private Class<T> clasz;

	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	public GenericDao() {		
	    this.clasz = (Class<T>)((java.lang.reflect.ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}
		
	public SessionFactory getSessionFactory(){
		return this.sessionFactory;
	}
	
	public Criteria createCriteria(){
		return getSessionFactory().getCurrentSession().createCriteria(clasz);
	}
	
	public Criteria createCriteria(String alias){
		return getSessionFactory().getCurrentSession().createCriteria(clasz, alias);
	}
	
	public Session getSession(){
		return getSessionFactory().getCurrentSession();
	}
	
	public long countAll() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public List<T> findAll() {		
		return getSessionFactory().getCurrentSession()
				.createQuery("from "+clasz.getSimpleName()).list();
	}

	public T findById(Serializable id) {
		return (T) getSessionFactory().getCurrentSession().get(clasz, id);
	}
	
	public T save(T data){
		getSession().saveOrUpdate(data);
		return data;
	}	
	
	public List<T> getEntityByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("id", ids));
		return criteria.list();
	}
	
	
	public void delete(T data) {
		getSession().delete(data);
	}
	
	public void flush(){
		getSession().flush();
	}
	
	public void flushAndClearCache(){
		Session session = getSession();
		session.flush();
		session.clear();
	}

	public void evit(Object code){
		getSession().evict(code);
	}
	
}