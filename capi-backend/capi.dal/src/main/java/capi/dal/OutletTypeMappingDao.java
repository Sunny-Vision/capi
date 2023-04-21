package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import capi.entity.OutletTypeMapping;
import capi.model.dataImportExport.ImportRebasingOutletTypeMappingList;

@Repository("OutletTypeMappingDao")
public class OutletTypeMappingDao extends GenericDao<OutletTypeMapping> {
	public void insertOutletTypeMapping(String values, Date effectiveDate, List<ImportRebasingOutletTypeMappingList> newOutletTypeMappingList){
		String sql = "INSERT INTO [OutletTypeMapping] ([OldCode], [ShortCode], [EffectiveDate], [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		for(int i = 0; i<newOutletTypeMappingList.size();i++){
			ImportRebasingOutletTypeMappingList outletTypeMapping = newOutletTypeMappingList.get(i);
			query.setParameter("oldCode"+i, outletTypeMapping.getOldCode());
			query.setParameter("newCode"+i, outletTypeMapping.getNewCode());
		}
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
		
	}
	
	public void updateOutletTypeShortCode(Date date){
		String sql = "exec dbo.UpdateOutletTypeMapping :date ";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.executeUpdate();
		
	}
	
	
	
}
