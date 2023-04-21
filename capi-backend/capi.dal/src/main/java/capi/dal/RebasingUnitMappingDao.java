package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import capi.entity.RebasingUnitMapping;
import capi.model.dataImportExport.ImportRebasingUnitMappingList;

@Repository("RebasingUnitMappingDao")
public class RebasingUnitMappingDao extends GenericDao<RebasingUnitMapping>{

	public void insertRebasingUnitMapping(String values, Date effectiveDate, String cpiBasePeriod, List<ImportRebasingUnitMappingList> unitMappingList ){
		String sql = "INSERT INTO [RebasingUnitMapping] ([OldUnitId], [NewUnitId], [EffectiveDate], [NewCPIBasePeriod], [OldCPIBasePeriod], [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		for(int i=0; i<unitMappingList.size();i++){
			ImportRebasingUnitMappingList unitMapping = unitMappingList.get(i);
			query.setParameter("oldUnitId"+i, unitMapping.getOldUnitId());
			query.setParameter("newUnitId"+i, unitMapping.getNewUnitId());
			query.setParameter("oldCpiBasePeriod"+i, unitMapping.getOldCpiBasePeriod());
		}
		query.setParameter("newCpiBasePeriod", cpiBasePeriod);
		query.setParameter("effectiveDate", effectiveDate);
		query.executeUpdate();
	}
}
