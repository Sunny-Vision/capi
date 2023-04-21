package capi.service.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.GroupDao;
import capi.dal.ItemDao;
import capi.dal.OutletTypeDao;
import capi.dal.SectionDao;
import capi.dal.SubGroupDao;
import capi.dal.SubItemDao;
import capi.dal.UnitDao;
import capi.model.JsTreeRequestGetBottomEntityIdsModel;
import capi.model.JsTreeResponseModel;
import capi.model.commonLookup.UnitLookupTableList;
import capi.service.BaseService;

@Service("UnitLookupService")
public class UnitLookupService extends BaseService {
	
	@Autowired
	private SectionDao sectionDao;
	
	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private SubGroupDao subGroupDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private OutletTypeDao outletTypeDao;
	
	@Autowired
	private SubItemDao subItemDao;
	
	@Autowired
	private UnitDao unitDao;
	
	/**
	 * Get all sections
	 */
	public List<JsTreeResponseModel> getAllSections() {
		return sectionDao.getAll();
	}
	
	/**
	 * Get sections by parent id
	 */
	public List<JsTreeResponseModel> getSectionsByParentId(String id, List<Integer> purposeIds, boolean onlyActive) {
		return sectionDao.getAllByParentId(id, purposeIds, onlyActive);
	}

	/**
	 * Get groups by parent id
	 */
	public List<JsTreeResponseModel> getGroupsByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		return groupDao.getAllByParentId(id, purposeIds, onlyActive);
	}

	/**
	 * Get subgroups by parent id
	 */
	public List<JsTreeResponseModel> getSubGroupsByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		return subGroupDao.getAllByParentId(id, purposeIds, onlyActive);
	}

	/**
	 * Get items by parent id
	 */
	public List<JsTreeResponseModel> getItemsByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		return itemDao.getAllByParentId(id, purposeIds, onlyActive);
	}

	/**
	 * Get outlet types by parent id
	 */
	public List<JsTreeResponseModel> getOutletTypesByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		return outletTypeDao.getAllByParentId(id, purposeIds, onlyActive);
	}

	/**
	 * Get sub items by parent id
	 */
	public List<JsTreeResponseModel> getSubItemsByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		return subItemDao.getAllByParentId(id, purposeIds, onlyActive);
	}

	/**
	 * Get units by parent id
	 */
	public List<JsTreeResponseModel> getUnitsByParentId(int id, List<Integer> purposeIds, boolean onlyActive) {
		return unitDao.getAllByParentId(id, purposeIds, onlyActive);
	}
	
	/**
	 * Get table list by ids
	 */
	public List<UnitLookupTableList> getTableListByIds(Integer[] ids){
		return unitDao.getTableListByIds(ids);
	}
	
	/**
	 * Get bottom entity ids
	 */
	@SuppressWarnings("unchecked")
	public List<String> getBottomEntityIds(JsTreeRequestGetBottomEntityIdsModel model) {
		List<String> bottomEntityIds = new ArrayList<String>();
		
		switch (model.getBottomEntityClass()) {
			case "CpiBasePeriod":
				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds.addAll(model.getCpiBasePeriods());
				break;
			case "Section":
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0) {
					for (Integer id : model.getSectionIds()) {
						bottomEntityIds.add("" + id);
					}
				}
	
				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, sectionDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
				
				break;
			case "Group":
				if (model.getGroupIds() != null && model.getGroupIds().size() > 0) {
					for (Integer id : model.getGroupIds()) {
						bottomEntityIds.add("" + id);
					}
				}
	
				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, groupDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
				
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, groupDao.getIdsBySectionId(model.getSectionIds()));
				
				break;
			case "SubGroup":
				if (model.getSubGroupIds() != null && model.getSubGroupIds().size() > 0) {
					for (Integer id : model.getSubGroupIds()) {
						bottomEntityIds.add("" + id);
					}
				}
	
				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subGroupDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
				
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subGroupDao.getIdsBySectionId(model.getSectionIds()));
				
				if (model.getGroupIds() != null && model.getGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subGroupDao.getIdsByGroupId(model.getGroupIds()));
				
				break;
			case "Item":
				if (model.getItemIds() != null && model.getItemIds().size() > 0) {
					for (Integer id : model.getItemIds()) {
						bottomEntityIds.add("" + id);
					}
				}

				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, itemDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
				
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, itemDao.getIdsBySectionId(model.getSectionIds()));
				
				if (model.getGroupIds() != null && model.getGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, itemDao.getIdsByGroupId(model.getGroupIds()));
				
				if (model.getSubGroupIds() != null && model.getSubGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, itemDao.getIdsBySubGroupId(model.getSubGroupIds()));
				
				break;
			case "OutletType":
				if (model.getOutletTypeIds() != null && model.getOutletTypeIds().size() > 0) {
					for (Integer id : model.getOutletTypeIds()) {
						bottomEntityIds.add("" + id);
					}
				}

				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, outletTypeDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
				
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, outletTypeDao.getIdsBySectionId(model.getSectionIds()));
				
				if (model.getGroupIds() != null && model.getGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, outletTypeDao.getIdsByGroupId(model.getGroupIds()));
				
				if (model.getSubGroupIds() != null && model.getSubGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, outletTypeDao.getIdsBySubGroupId(model.getSubGroupIds()));
				
				if (model.getItemIds() != null && model.getItemIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, outletTypeDao.getIdsByItemId(model.getItemIds()));
				
				break;
			case "SubItem":
				if (model.getSubItemIds() != null && model.getSubItemIds().size() > 0) {
					for (Integer id : model.getSubItemIds()) {
						bottomEntityIds.add("" + id);
					}
				}

				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subItemDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
				
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subItemDao.getIdsBySectionId(model.getSectionIds()));
				
				if (model.getGroupIds() != null && model.getGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subItemDao.getIdsByGroupId(model.getGroupIds()));
				
				if (model.getSubGroupIds() != null && model.getSubGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subItemDao.getIdsBySubGroupId(model.getSubGroupIds()));
				
				if (model.getItemIds() != null && model.getItemIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subItemDao.getIdsByItemId(model.getItemIds()));
				
				if (model.getOutletTypeIds() != null && model.getOutletTypeIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, subItemDao.getIdsByOutletTypeId(model.getOutletTypeIds()));
				
				break;
			case "Unit":
				if (model.getUnitIds() != null && model.getUnitIds().size() > 0) {
					for (Integer id : model.getUnitIds()) {
						bottomEntityIds.add("" + id);
					}
				}
				
				if (model.getCpiBasePeriods() != null && model.getCpiBasePeriods().size() > 0){
					int treePass = 0;
					List<JsTreeResponseModel> treeLength = new ArrayList<JsTreeResponseModel>();
					treeLength = sectionDao.getAllByParentId(model.getCpiBasePeriods().get(0), null, true);
					if(treeLength.size() == 1){
						treeLength = groupDao.getAllByParentId(Integer.parseInt(treeLength.get(0).getId()), null, true);
						if(treeLength.size() == 1){
							treeLength = subGroupDao.getAllByParentId(Integer.parseInt(treeLength.get(0).getId()), null, true);
							if(treeLength.size() == 1){
								treeLength = itemDao.getAllByParentId(Integer.parseInt(treeLength.get(0).getId()), null, true);
								if(treeLength.size() == 1){
									treeLength = outletTypeDao.getAllByParentId(Integer.parseInt(treeLength.get(0).getId()), null, true);
									if(treeLength.size() == 1){
										treeLength = subItemDao.getAllByParentId(Integer.parseInt(treeLength.get(0).getId()), null, true);
										if(treeLength.size() == 1){
											treeLength = unitDao.getAllByParentId(Integer.parseInt(treeLength.get(0).getId()), null, true);
											treePass = 1;
										}
									}
								}
							}
						}
					}

					if(treePass == 1){
						bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsBySubItemId(Integer.parseInt(treeLength.get(0).getId())));
					} else {
						bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsByCpiBasePeriod(model.getCpiBasePeriods()));
					}
				}
				if (model.getSectionIds() != null && model.getSectionIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsBySectionId(model.getSectionIds()));
				
				if (model.getGroupIds() != null && model.getGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsByGroupId(model.getGroupIds()));
				
				if (model.getSubGroupIds() != null && model.getSubGroupIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsBySubGroupId(model.getSubGroupIds()));
				
				if (model.getItemIds() != null && model.getItemIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsByItemId(model.getItemIds()));
				
				if (model.getOutletTypeIds() != null && model.getOutletTypeIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsByOutletTypeId(model.getOutletTypeIds()));
				
				if (model.getSubItemIds() != null && model.getSubItemIds().size() > 0)
					bottomEntityIds = (List<String>) CollectionUtils.union(bottomEntityIds, unitDao.getIdsBySubItemId(model.getSubItemIds()));
				
				break;
		}
		
		return bottomEntityIds;
	}

	/**
	 * Get all cpi base periods
	 */
	public List<JsTreeResponseModel> getAllCpiBasePeriods(List<Integer> purposeIds, boolean onlyActive) {
		return unitDao.getAllCpiBasePeriods(purposeIds, onlyActive);
	}
}
