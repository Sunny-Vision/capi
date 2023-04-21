package capi.dal.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.SQLProjection;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class SQLProjectionExt extends SQLProjection{

	private static final long serialVersionUID = 1130471138106200639L;

	private String groupBy;
	private Projection[] projections;

	protected SQLProjectionExt(String sql, String[] columnAliases, Type[] types) {
		super(sql, columnAliases, types);   
	}
	
	protected SQLProjectionExt(Projection[] projections) {
		super("", "", new String[] {"cnt"}, new Type[]{StandardBasicTypes.LONG});  
		this.projections = projections;		
	}

	protected SQLProjectionExt(String sql, String groupBy, String[] columnAliases, Type[] types) {
		super(sql, groupBy, columnAliases, types);
		this.groupBy = groupBy;
	}

	public static SQLProjection sqlProjection(String sql, String[] columnAliases, Type... types) {
		return new SQLProjectionExt(sql, columnAliases, types);
	}
	
	public static SQLProjection sqlProjection(String sql, String groupBy, String[] columnAliases, Type... types) {
		return new SQLProjectionExt(sql, groupBy, columnAliases, types);
	}

	@Override
	public boolean isGrouped(){
		return !StringUtils.isEmpty(groupBy) || projections!=null && projections.length > 0;
	}
	
	@Override
	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
		if (this.projections != null && this.projections.length > 0){
			StringBuffer sb = new StringBuffer(" count(*) as cnt from (select ");
			for (int i = 0; i < projections.length; i++) {
				sb.append(projections[i].toSqlString(criteria, loc+i, criteriaQuery));
				if (i < projections.length - 1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
		else{
			String sql = super.toSqlString(criteria, loc, criteriaQuery);
			Pattern p = Pattern.compile("\\{(\\w++)\\}");
			Matcher m = p.matcher(sql);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String s = m.group(1) + ".";
				m.appendReplacement(sb, criteriaQuery.getSQLAlias(criteria, s));
			}
			m.appendTail(sb);   
			return sb.toString();
		}
		
	}

	@Override
	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {    
		if (this.projections != null && this.projections.length > 0){
			StringBuffer sb = new StringBuffer();
			ArrayList<Projection> filtered = new ArrayList<Projection>();
			
			for (int i = 0; i < projections.length; i++) {
				if (projections[i].isGrouped()){
					filtered.add(projections[i]);					
				}
			}
			for (int i = 0; i < filtered.size(); i++) {
				sb.append(filtered.get(i).toGroupSqlString(criteria, criteriaQuery));
				if (i < filtered.size() - 1) {
					sb.append(",");
				}
			}			
			sb.append(" ) as dataSet ");
			return sb.toString();
		}
		else{			
			Pattern p = Pattern.compile("\\{(\\w++)\\}");
			Matcher m = p.matcher(groupBy);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String s = m.group(1) + ".";
				m.appendReplacement(sb, criteriaQuery.getSQLAlias(criteria, s));
			}
			m.appendTail(sb);   
			return sb.toString();
		}
	}

	public static Projection groupByHaving(String column, Type type, String having) {
		return groupByHaving(new String[] { column }, null, new Type[] { type }, having);
	}

	public static Projection groupByHaving(String column, String alias, Type type, String having) {
		return groupByHaving(new String[] { column }, new String[] { alias }, new Type[] { type }, having);
	}

	public static Projection groupByHaving(String[] columns, Type[] types, String having) {
		return groupByHaving(columns, null, types, having);
	}
	
	public SQLProjectionExt add(Projection projection){
		projections = ArrayUtils.add(projections, projection);
		return this;
	}

	public static Projection groupByHaving(String[] columns, String[] aliases, Type[] types, String having) {
		if (aliases != null && columns.length != aliases.length)
			return null;

		if (columns.length != types.length)
			return null;

		StringBuffer sb = new StringBuffer();
		StringBuffer gp = new StringBuffer();
		for (int i = 0; i < columns.length; i++) {
			sb.append(columns[i]);
			gp.append(columns[i]);
			if (aliases != null) {
				sb.append(" as " + aliases[i]);
			}
			if (i < columns.length - 1) {
				sb.append(",");
				gp.append(",");
			}
		}

		return new SQLProjectionExt(sb.toString(), gp.toString()+ " having " + having, aliases, types);
		//return Projections.sqlGroupProjection(sb.toString(), gp.toString() + " having " + having, aliases, types);
	}
	
	public static SQLProjectionExt groupCount(Projection ... projections){
		return new SQLProjectionExt(projections);
	}
}
