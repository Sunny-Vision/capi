package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.SubPriceColumnDao;
import capi.dal.SubPriceFieldDao;
import capi.dal.SubPriceFieldMappingDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.SubPriceTypeDao;
import capi.entity.SubPriceColumn;
import capi.entity.SubPriceField;
import capi.entity.SubPriceFieldMapping;
import capi.entity.SubPriceRecord;
import capi.entity.SubPriceType;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.SubPriceFieldMappingSyncData;
import capi.model.api.dataSync.SubPriceFieldSyncData;
import capi.model.api.dataSync.SubPriceTypeSyncData;
import capi.model.masterMaintenance.SubPriceComparisonModel;
import capi.model.masterMaintenance.SubPriceFieldList;
import capi.model.masterMaintenance.SubPriceRowModel;
import capi.model.masterMaintenance.SubPriceTypeModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("SubPriceService")
public class SubPriceService extends BaseService {
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;

	@Autowired
	private SubPriceFieldDao subPriceFieldDao;
	
	@Autowired
	private SubPriceFieldMappingDao subPriceFieldMappingDao;
	
	@Autowired
	private SubPriceRecordDao subPriceRecordDao;
	
	@Autowired
	private SubPriceColumnDao subPriceColumnDao;
	
	@Autowired
	private CommonService commonServivce;
	
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<SubPriceTypeModel> querySubPrice(DatatableRequestModel model){
		
		Order order = this.getOrder(model,"", "category", "name", "status");
		
		String search = model.getSearch().get("value");
		
		List<SubPriceType> result = subPriceTypeDao.listSubPriceType(search, model.getStart(), model.getLength(), order);
		
		List<SubPriceTypeModel> returnList = new ArrayList<SubPriceTypeModel>();
		
		for(SubPriceType spt: result){
			SubPriceTypeModel sptModel = new SubPriceTypeModel();
			BeanUtils.copyProperties(spt, sptModel);			
			sptModel.setDividedByField(spt.getDividedByField() != null ? spt.getDividedByField().getId().toString() : null);
			sptModel.setGroupByField(spt.getGroupByField() != null ? spt.getGroupByField().getId().toString() : null);				
			returnList.add(sptModel);
		}
		
		DatatableResponseModel<SubPriceTypeModel> response = new DatatableResponseModel<SubPriceTypeModel>();
		response.setDraw(model.getDraw());
		response.setData(returnList);
		Long recordTotal =subPriceTypeDao.countSubPriceType("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = subPriceTypeDao.countSubPriceType(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Get by ID
	 */
	public SubPriceType getSubPriceById(int id) {
		return subPriceTypeDao.findById(id);
	}
	

	/**
	 * Save
	 */
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public void saveSubPriceType(SubPriceTypeModel model, Integer[] subPriceFieldIds) {
		SubPriceType oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getSubPriceById(model.getId());
		} else {
			oldEntity = new SubPriceType();
		}
		
		BeanUtils.copyProperties(model, oldEntity);
		
		oldEntity.setGroupByField(subPriceFieldDao.findById(model.getGroupByField().equalsIgnoreCase("") ? 0 : Integer.parseInt(model.getGroupByField())));
		oldEntity.setDividedByField(subPriceFieldDao.findById(model.getDividedByField().equalsIgnoreCase("") ? 0 : Integer.parseInt(model.getDividedByField())));

		subPriceTypeDao.save(oldEntity);
		
		List<SubPriceFieldMapping> mappings = new ArrayList<SubPriceFieldMapping>();
		if(oldEntity.getId() != null){
			mappings = subPriceFieldMappingDao.getMapByTypeId(oldEntity.getId(), true);
		}
		
		List<Integer> oldIds = new ArrayList<Integer>();
		for(SubPriceFieldMapping element: mappings){
			oldIds.add(element.getSubPriceField().getId());
		}
		
		List<Integer> newIds = new ArrayList<Integer>();
		if(subPriceFieldIds != null){
			for(Integer ids: subPriceFieldIds){
				newIds.add(ids);
			}
		}
		
		Collection<Integer> existEntries = CollectionUtils.retainAll(oldIds, newIds);
		Collection<Integer> newEntries = CollectionUtils.subtract(newIds, oldIds);
		Collection<Integer> removeEntries = CollectionUtils.subtract(oldIds, newIds);
	
		if (existEntries.size() > 0){
			List<SubPriceFieldMapping> existingMappings = subPriceFieldMappingDao.getByFieldIdsTypeId(new ArrayList<Integer>(existEntries), oldEntity.getId(), true);
			for (SubPriceFieldMapping map: existingMappings){
				map.setSequence(ArrayUtils.indexOf(subPriceFieldIds, map.getSubPriceField().getId()));
				subPriceFieldMappingDao.save(map);
			}		
		}
		
		if (newEntries.size() > 0){
			List<SubPriceField> fields = subPriceFieldDao.getSubPriceFieldsByIds(new ArrayList<Integer>(newEntries));
			for (SubPriceField field : fields){
				SubPriceFieldMapping map = new SubPriceFieldMapping();
				map.setSubPriceField(field);
				map.setSubPriceType(oldEntity);
				map.setSequence(ArrayUtils.indexOf(subPriceFieldIds, field.getId()));
				subPriceFieldMappingDao.save(map);
			}
		}
		
		if (removeEntries.size() > 0){
			List<SubPriceFieldMapping> removeMappings = subPriceFieldMappingDao.getByFieldIdsTypeId(new ArrayList<Integer>(removeEntries), oldEntity.getId(), false);
			for (SubPriceFieldMapping map: removeMappings){
				subPriceFieldMappingDao.delete(map);
			}
		}

		//subPriceFieldMappingDao.flush();
		subPriceTypeDao.flush();
	}

	/**
	 * Delete
	 */
	@Transactional
	public boolean deleteSubPriceTypes(List<Integer> id) {
		
		List<SubPriceType> types = subPriceTypeDao.getSubPriceTypesByIds(id);
		if (types.size() != id.size()){
			return false;
		}
		
		List<SubPriceFieldMapping> mappings = subPriceFieldMappingDao.getSubPriceFieldMappingBySubPriceTypeIds(id);
		
		for (SubPriceFieldMapping mapping: mappings){
			subPriceFieldMappingDao.delete(mapping);
		}
		
		for(SubPriceType element: types){
			subPriceTypeDao.delete(element);
		}
		
		subPriceTypeDao.flush();
		
		return true;
	}
	
	/**
	 * Get sub price field by type
	 */
	public List<SubPriceFieldList> getSubPriceFieldByType(int typeId){
		return subPriceFieldMappingDao.getFieldByTypeId(typeId);
	}
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<SubPriceField> querySubPriceField(DatatableRequestModel model, Integer[] skipIds){

		Order order = this.getOrder(model, "", "fieldName", "fieldType");
		
		String search = model.getSearch().get("value");
		
		List<SubPriceField> result = subPriceFieldDao.listSubPriceField(search, skipIds, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<SubPriceField> response = new DatatableResponseModel<SubPriceField>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = subPriceFieldDao.countSubPriceField("", null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = subPriceFieldDao.countSubPriceField(search, skipIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Get by ID
	 */
	public SubPriceField getSubPriceFieldById(int id) {
		return subPriceFieldDao.findById(id);
	}
	

	/**
	 * Save
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void saveSubPriceField(SubPriceField model) {
		SubPriceField oldEntity = null;
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getSubPriceFieldById(model.getId());
		} else {
			oldEntity = new SubPriceField();
			int id = subPriceFieldDao.getCurrentMaxId();
			oldEntity.setVariableName("V" + (id+1));
		}
		oldEntity.setFieldName(model.getFieldName());
		oldEntity.setFieldType(model.getFieldType());
		subPriceFieldDao.save(oldEntity);
		subPriceFieldDao.flush();
	}

	/**
	 * Delete
	 */
	@Transactional
	public boolean deleteSubPriceField(List<Integer> id) {
		List<SubPriceField> fields = subPriceFieldDao.getSubPriceFieldsByIds(id);
		if (fields.size() != id.size()){
			return false;
		}
		for (SubPriceField field : fields){
			subPriceFieldDao.delete(field);
		}
		
		subPriceFieldDao.flush();
		
		return true;
	}
	

	/**
	 * Get sub-price type select format
	 */
	public Select2ResponseModel querySubPriceTypeSelect2(Select2RequestModel queryModel) {
		List<SubPriceType> entities = subPriceTypeDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		HashMap<String, List<SubPriceType>> grouped = new HashMap<String, List<SubPriceType>>();
		for (SubPriceType entity : entities) {
			List<SubPriceType> types = null;
			if (!grouped.containsKey(entity.getCategory())) {
				types = new ArrayList<SubPriceType>();
				grouped.put(entity.getCategory(), types);
			} else {
				types = grouped.get(entity.getCategory());
			}
			types.add(entity);
		}
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : grouped.keySet()) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			List<Select2ResponseModel.Select2Item> children = new ArrayList<Select2ResponseModel.Select2Item>();
			item.setChildren(children);
			for (SubPriceType type : grouped.get(category)) {
				Select2ResponseModel.Select2Item child = responseModel.new Select2Item();
				child.setId(String.valueOf(type.getId()));
				child.setText(type.getName());
				children.add(child);
			}
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	

	/**
	 * Query single sub-price type
	 */
	public String querySubPriceTypeSelectSingle(Integer id) {
		SubPriceType entity = subPriceTypeDao.findById(id);
		if (entity == null)
			return null;
		else
			return entity.getName();
	}
	
	public List<SubPriceFieldSyncData> getUpdateSubPriceField(Date lastSyncTime){
		return subPriceFieldDao.getUpdateSubPriceField(lastSyncTime);
	}
	
	public List<SubPriceTypeSyncData> getUpdateSubPriceType(Date lastSyncTime){
		return subPriceTypeDao.getUpdateSubPriceType(lastSyncTime);
	}
	
	public List<SubPriceFieldMappingSyncData> getUpdateSubPriceFieldMapping(Date lastSyncTime){
		return subPriceFieldMappingDao.getUpdateSubPriceFieldMapping(lastSyncTime);
	}
	
	public SubPriceComparisonModel prepareSubPriceComparisonModel(Integer quotationRecordId1, Integer quotationRecordId2){
		List<SubPriceRecord> spr1 = null;
		List<SubPriceRecord> spr2 = null;
		
		if (quotationRecordId1 != null){
			spr1 = subPriceRecordDao.getAllByQuotationRecordId(quotationRecordId1);
		}
		if (quotationRecordId2 != null){
			spr2 = subPriceRecordDao.getAllByQuotationRecordId(quotationRecordId2);
		}
		
		SubPriceComparisonModel compareModel = new SubPriceComparisonModel();
		
		if (spr1 != null && spr1.size() > 0){
			List<SubPriceRowModel> records = new ArrayList<SubPriceRowModel>();
			SubPriceRecord record = spr1.get(0);
			Date date = record.getQuotationRecord().getCollectionDate();
			compareModel.setDate1(commonServivce.formatDate(date));
			compareModel.setType1(record.getSubPriceType());
			
			compareModel.setFieldList1(subPriceFieldMappingDao.getFieldByTypeId(record.getSubPriceType().getId()));			
			List<SubPriceColumn> columns = subPriceColumnDao.getAllByQuotationRecordId(quotationRecordId1);
			
			SubPriceRecord subPrice = null;
			SubPriceRowModel row = null;
			for (SubPriceColumn column : columns){
				if (subPrice == null || !subPrice.equals(column.getSubPriceRecord())){
					subPrice = column.getSubPriceRecord();
					row = new SubPriceRowModel();
					BeanUtils.copyProperties(subPrice, row);
					row.setColumns(new ArrayList<SubPriceColumn>()); 
					records.add(row);
				}
				row.getColumns().add(column);
			}
			
			compareModel.setRows1(records);
			
		}
		
		if (spr2 != null && spr2.size() > 0){
			List<SubPriceRowModel> records = new ArrayList<SubPriceRowModel>();
			SubPriceRecord record = spr2.get(0);
			Date date = record.getQuotationRecord().getCollectionDate();
			compareModel.setType2(record.getSubPriceType());
			compareModel.setDate2(commonServivce.formatDate(date));
			compareModel.setFieldList2(subPriceFieldMappingDao.getFieldByTypeId(record.getSubPriceType().getId()));
			List<SubPriceColumn> columns = subPriceColumnDao.getAllByQuotationRecordId(quotationRecordId2);
			
			SubPriceRecord subPrice = null;
			SubPriceRowModel row = null;
			for (SubPriceColumn column : columns){
				if (subPrice == null || !subPrice.equals(column.getSubPriceRecord())){
					subPrice = column.getSubPriceRecord();
					row = new SubPriceRowModel();
					BeanUtils.copyProperties(subPrice, row);
					row.setColumns(new ArrayList<SubPriceColumn>()); 
					records.add(row);
				}
				row.getColumns().add(column);
			}
			
			compareModel.setRows2(records);
		}
		
		return compareModel;
	}
	
	public SubPriceComparisonModel prepareSubPriceDetailsModel(Integer quotationRecordId1){
		List<SubPriceRecord> spr1 = null;
		
		if (quotationRecordId1 != null){
			spr1 = subPriceRecordDao.getAllByQuotationRecordId(quotationRecordId1);
		}
		
		SubPriceComparisonModel compareModel = new SubPriceComparisonModel();
		
		if (spr1 != null && spr1.size() > 0){
			List<SubPriceRowModel> records = new ArrayList<SubPriceRowModel>();
			SubPriceRecord record = spr1.get(0);
			Date date = record.getQuotationRecord().getCollectionDate();
			compareModel.setDate1(commonServivce.formatDate(date));
			compareModel.setType1(record.getSubPriceType());
			
			compareModel.setFieldList1(subPriceFieldMappingDao.getFieldByTypeId(record.getSubPriceType().getId()));			
			List<SubPriceColumn> columns = subPriceColumnDao.getAllByQuotationRecordId(quotationRecordId1);
			
			SubPriceRecord subPrice = null;
			SubPriceRowModel row = null;
			for (SubPriceColumn column : columns){
				if (subPrice == null || !subPrice.equals(column.getSubPriceRecord())){
					subPrice = column.getSubPriceRecord();
					row = new SubPriceRowModel();
					BeanUtils.copyProperties(subPrice, row);
					row.setColumns(new ArrayList<SubPriceColumn>()); 
					records.add(row);
				}
				row.getColumns().add(column);
			}
			
			compareModel.setRows1(records);
			
		}
		
		return compareModel;
	}
	
}
