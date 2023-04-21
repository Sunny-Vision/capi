package capi.service.masterMaintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import capi.dal.GroupDao;
import capi.dal.ItemDao;
import capi.dal.OnSpotValidationDao;
import capi.dal.OutletTypeDao;
import capi.dal.OutletUnitStatisticDao;
import capi.dal.PricingMonthDao;
import capi.dal.ProductGroupDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.SectionDao;
import capi.dal.SubGroupDao;
import capi.dal.SubItemDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.UOMCategoryDao;
import capi.dal.UnitDao;
import capi.dal.UomDao;
import capi.entity.Group;
import capi.entity.Item;
import capi.entity.OnSpotValidation;
import capi.entity.OutletType;
import capi.entity.PricingFrequency;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.Quotation;
import capi.entity.Section;
import capi.entity.SubGroup;
import capi.entity.SubItem;
import capi.entity.SubPriceType;
import capi.entity.UOMCategory;
import capi.entity.Unit;
import capi.entity.Uom;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.api.dataSync.OnSpotValidationSyncData;
import capi.model.api.dataSync.OutletTypeSyncData;
import capi.model.api.dataSync.OutletUnitStatisticSyncData;
import capi.model.api.dataSync.SubItemSyncData;
import capi.model.api.dataSync.UOMCategoryUnitSyncData;
import capi.model.api.dataSync.UnitSyncData;
import capi.model.masterMaintenance.UnitCommonModel;
import capi.model.masterMaintenance.UnitEditModel;
import capi.model.masterMaintenance.UnitTableList;
import capi.model.masterMaintenance.UnitTableRequestModel;
import capi.model.masterMaintenance.VarietySimpleModel;
import capi.service.BaseService;
import capi.service.CommonService;

@Service("UnitService")
public class UnitService extends BaseService {
	
	@Autowired
	private OutletUnitStatisticDao outletUnitStatisticDao;
	
	@Autowired
	private UnitDao unitDao;

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
	private OnSpotValidationDao onSpotValidationDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private UomDao uomDao;

	@Autowired
	private UOMCategoryDao uomCategoryDao;
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PricingMonthDao pricingMonthDao;
	
	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<UnitTableList> getTableList(UnitTableRequestModel model) {

		Order order = this.getOrder(model, "unitCode", "unitEnglishName", "surveyType", "groupEnglishName", 
				"subGroupEnglishName", "itemEnglishName","outletTypeEnglishName", "cpiBasePeriod", "status", "productCategory",
				"sectionCode", "sectionChineseName", "sectionEnglishName", "groupCode", "groupEnglishName",
				"subGroupCode","subGroupEnglishName", "subItemCode",  "subItemEnglishName", "itemCode", "itemEnglishName",
				"outletTypeCode",  "outletTypeEnglishName");
		
		String search = model.getSearch().get("value");
		
		List<UnitTableList> result = unitDao.getTableList(search, model.getStart(), model.getLength(), order,
				model.getSurveyTypeId(), model.getSectionId(), model.getGroupId(), model.getSubGroupId(), model.getItemId(), 
				model.getOutletTypeId(), model.getSubItemId(), model.getStatus(),
				model.getProductCategoryId(), model.getCpiBasePeriod());
		
		DatatableResponseModel<UnitTableList> response = new DatatableResponseModel<UnitTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = unitDao.countTableList("", null, null, null, null, null, null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = unitDao.countTableList(search, 
				model.getSurveyTypeId(), model.getSectionId(), model.getGroupId(), model.getSubGroupId(), model.getItemId(), 
				model.getOutletTypeId(), model.getSubItemId(), model.getStatus(),
				model.getProductCategoryId(), model.getCpiBasePeriod());
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Save
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void saveUnit(UnitEditModel model, boolean saveUnitOnly) throws Exception {
		// unit
		Unit entity = null;
		
		if (!isUnitCodeValid(model)) {
			throw new ServiceException("E00155");
		}
		
		if (model.getId() != null) {
			entity = unitDao.getUnitWithSubItem(model.getId());
			if (entity == null) {
				throw new ServiceException("E00011");
			}
			
			boolean isPurposeChanged = false;
			if (entity.getPurpose() == null) {
				isPurposeChanged = model.getPurposeId() != null;
			} else {
				isPurposeChanged = model.getPurposeId() != entity.getPurpose().getId();
			}
			if (isPurposeChanged) {
				/*if (model.isMRPS()) {
					if (unitDao.isCodeExistsAmongMRPS(model.getCode(), model.getCpiBasePeriod()))
						throw new ServiceException("E00048");
				} else {
					if (unitDao.isCodeExistsAmongPurpose(model.getCode(), model.getPurposeId(), model.getCpiBasePeriod()))
						throw new ServiceException("E00048");
				}*/
				if (unitDao.isUnitCodeExist(model.getCode(), model.getCpiBasePeriod()))
					throw new ServiceException("E00048");
			}
		} else {
			entity = new Unit();
			/*if (model.isMRPS()) {
				if (unitDao.isCodeExistsAmongMRPS(model.getCode(), model.getCpiBasePeriod()))
					throw new ServiceException("E00048");
			} else {
				if (unitDao.isCodeExistsAmongPurpose(model.getCode(), model.getPurposeId(), model.getCpiBasePeriod()))
					throw new ServiceException("E00048");
			}*/
			if (unitDao.isUnitCodeExist(model.getCode(), model.getCpiBasePeriod()))
				throw new ServiceException("E00048");
		}
		String entityCode = entity.getCode();
		BeanUtils.copyProperties(model, entity);
		if (entity.getId() != null) {
			entity.setCode(entityCode);
		}
		if (model.getPurposeId() == null) {
			entity.setPurpose(null);
		} else {
			if (entity.getPurpose() == null || entity.getPurpose().getId() != model.getPurposeId()) {
				Purpose purpose = purposeDao.findById(model.getPurposeId());
				entity.setPurpose(purpose);
			}
		}
		if (model.getStandardUOMId() == null) {
			entity.setStandardUOM(null);
		} else {
			if (entity.getStandardUOM() == null || entity.getStandardUOM().getId() != model.getStandardUOMId()) {
				Uom uom = uomDao.findById(model.getStandardUOMId());
				entity.setStandardUOM(uom);
			}
		}
		if (model.getUomCategoryIds() == null) {
			entity.getUomCategories().clear();
		} else {
			ArrayList<Integer> oldIds = new ArrayList<Integer>();
			for (UOMCategory child : entity.getUomCategories()) {
				oldIds.add(child.getId());
			}
			Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldIds, model.getUomCategoryIds());
			Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getUomCategoryIds(), oldIds);
			
			Iterator<UOMCategory> iter = entity.getUomCategories().iterator();
			while (iter.hasNext()) {
				UOMCategory category = iter.next();
				for (Integer id : deletedIds) {
					if (category.getId().intValue() == id) {
						iter.remove();
						break;
					}
				}
			}
			
			if (newIds.size() > 0) {
				List<UOMCategory> newCategories = uomCategoryDao.getUomCategoriesByIds(new ArrayList<Integer>(newIds), false);
				entity.getUomCategories().addAll(newCategories);
			}
		}
		if (model.getSubPriceTypeId() == null) {
			entity.setSubPriceType(null);
		} else {
			if (entity.getSubPriceType() == null || entity.getSubPriceType().getId() != model.getSubPriceTypeId()) {
				SubPriceType subPriceType = subPriceTypeDao.findById(model.getSubPriceTypeId());
				entity.setSubPriceType(subPriceType);
			}
		}
		
		if (StringUtils.isEmpty(model.getObsoleteDate()))
			entity.setObsoleteDate(null);
		else
			entity.setObsoleteDate(commonService.getDate(model.getObsoleteDate()));
		if (StringUtils.isEmpty(model.getEffectiveDate()))
			entity.setEffectiveDate(null);
		else
			entity.setEffectiveDate(commonService.getDate(model.getEffectiveDate()));
		
		if (model.getPricingFrequencyId() == null) {
			entity.setPricingFrequency(null);
		} else {
			if (entity.getPricingFrequency() == null || entity.getPricingFrequency().getId() != model.getPricingFrequencyId()) {
				PricingFrequency pricingFrequency = pricingMonthDao.findById(model.getPricingFrequencyId());
				entity.setPricingFrequency(pricingFrequency);
			}
		}
		
		if (model.getProductCategoryId() == null) {
			entity.setProductCategory(null);
		} else {
			if (entity.getProductCategory() == null || entity.getProductCategory().getId() != model.getProductCategoryId()) {
				ProductGroup productCategory = productGroupDao.findById(model.getProductCategoryId());
				entity.setProductCategory(productCategory);
			}
		}
		
		if (!saveUnitOnly){
			// section
			Section section = null;
			if (entity.getId() != null) {
				section = entity.getSubItem().getOutletType().getItem().getSubGroup().getGroup().getSection();
			} else {
				section = sectionDao.getByCode(model.getSectionCode(), model.getCpiBasePeriod());
				if (section != null) {
					section = sectionDao.findById(section.getId());
				}
				if (section == null) {
					section = new Section();
					section.setCode(model.getSectionCode());
				}
			}
			section.setChineseName(model.getSectionChineseName());
			section.setEnglishName(model.getSectionEnglishName());
			sectionDao.save(section);
			
			// group
			Group group = null;
			if (entity.getId() != null) {
				group = entity.getSubItem().getOutletType().getItem().getSubGroup().getGroup();
			} else {
				group = groupDao.getByCode(model.getGroupCode(), model.getCpiBasePeriod());
				if (group != null) {
					group = groupDao.findById(group.getId());
				}
				if (group == null) {
					group = new Group();
					group.setSection(section);
					group.setCode(model.getGroupCode());
				}
			}
			group.setChineseName(model.getGroupChineseName());
			group.setEnglishName(model.getGroupEnglishName());
			groupDao.save(group);
			
			// sub-group
			SubGroup subGroup = null;
			if (entity.getId() != null) {
				subGroup = entity.getSubItem().getOutletType().getItem().getSubGroup();
			} else {
				subGroup = subGroupDao.getByCode(model.getSubGroupCode(), model.getCpiBasePeriod());
				if (subGroup != null) {
					subGroup = subGroupDao.findById(subGroup.getId());
				}
				if (subGroup == null) {
					subGroup = new SubGroup();
					subGroup.setGroup(group);
					subGroup.setCode(model.getSubGroupCode());
				}
			}
			subGroup.setChineseName(model.getSubGroupChineseName());
			subGroup.setEnglishName(model.getSubGroupEnglishName());
			subGroupDao.save(subGroup);
			
			// item
			Item item = null;
			if (entity.getId() != null) {
				item = entity.getSubItem().getOutletType().getItem();
			} else {
				item = itemDao.getByCode(model.getItemCode(), model.getCpiBasePeriod());
				if (item != null) {
					item = itemDao.findById(item.getId());
				}
				if (item == null) {
					item = new Item();
					item.setSubGroup(subGroup);
					item.setCode(model.getItemCode());
				}
			}
			item.setChineseName(model.getItemChineseName());
			item.setEnglishName(model.getItemEnglishName());
			itemDao.save(item);
			
			// outlet type
			OutletType outletType = null;
			if (entity.getId() != null) {
				outletType = entity.getSubItem().getOutletType();
			} else {
				outletType = outletTypeDao.getByCode(model.getOutletTypeCode(), model.getCpiBasePeriod());
				if (outletType != null) {
					outletType = outletTypeDao.findById(outletType.getId());
				}
				if (outletType == null) {
					outletType = new OutletType();
					outletType.setItem(item);
					outletType.setCode(model.getOutletTypeCode());
				}
			}
			if (outletType.getId() == null) {
				outletType.setChineseName(model.getOutletTypeChineseName());
				outletType.setEnglishName(model.getOutletTypeEnglishName());
				outletTypeDao.save(outletType);
			} else {
				if (outletType.getCode().length() >= 3) {
					List<OutletType> outletTypesSameShortCode = outletTypeDao.getAllByShoftCode(outletType.getCode().substring(outletType.getCode().length() - 3));
					for (OutletType outeltTypeSameShortCode : outletTypesSameShortCode) {
						outeltTypeSameShortCode.setChineseName(model.getOutletTypeChineseName());
						outeltTypeSameShortCode.setEnglishName(model.getOutletTypeEnglishName());
						outletTypeDao.save(outeltTypeSameShortCode);
					}
				}
			}
			
			// sub-item
			SubItem subItem = null;
			if (entity.getId() != null) {
				subItem = entity.getSubItem();
			} else {
				subItem = subItemDao.getByCode(model.getSubItemCode(), model.getCpiBasePeriod());
				if (subItem != null) {
					subItem = subItemDao.findById(subItem.getId());
				}
				if (subItem == null) {
					subItem = new SubItem();
					subItem.setOutletType(outletType);
					subItem.setCode(model.getSubItemCode());
				}
			}
			subItem.setChineseName(model.getSubItemChineseName());
			subItem.setEnglishName(model.getSubItemEnglishName());
			subItem.setCompilationMethod(model.getSubItemCompilationMethod());
			subItemDao.save(subItem);
	
			if (entity.getId() == null)
				entity.setSubItem(subItem);
			
			// onspot validation
			OnSpotValidation onSpotValidation;
			if (entity.getOnSpotValidation() == null) {
				onSpotValidation = new OnSpotValidation();
				onSpotValidation.setUnit(entity);
			} else {
				onSpotValidation = entity.getOnSpotValidation();
			}
			BeanUtils.copyProperties(model, onSpotValidation);
			onSpotValidationDao.save(onSpotValidation);
		}

		if(entity.getObsoleteDate() != null) {
			Date today = new Date();
			boolean obsoleted = entity.getObsoleteDate().before(today);
			if(obsoleted) {
				for(Quotation quotation : entity.getQuotations()) {
					quotation.setStatus("Inactive");
					quotationDao.save(quotation);
				}
			}
		}

		unitDao.save(entity);
		unitDao.flush();
	}
	
	/**
	 * Get by ID
	 */
	public Unit getById(int id) {
		return unitDao.findById(id);
	}
	
	/**
	 * Convert entity to model
	 */
	public UnitEditModel convertEntityToModel(Unit entity) {
		UnitEditModel model = new UnitEditModel();
		BeanUtils.copyProperties(entity, model);
		
		if (entity.getId() != null) {
			if (entity.getPurpose() != null)
				model.setPurposeId(entity.getPurpose().getId());
			
			if (entity.getStandardUOM() != null)
				model.setStandardUOMId(entity.getStandardUOM().getId());
			
			model.setUomCategoryIds(new ArrayList<Integer>());
			for (UOMCategory child : entity.getUomCategories()) {
				model.getUomCategoryIds().add(child.getId());
			}
			
			if (entity.getSubPriceType() != null)
				model.setSubPriceTypeId(entity.getSubPriceType().getId());
			
			if (entity.getObsoleteDate() != null)
				model.setObsoleteDate(commonService.formatDate(entity.getObsoleteDate()));
			
			if (entity.getEffectiveDate() != null)
				model.setEffectiveDate(commonService.formatDate(entity.getEffectiveDate()));
			
			if (entity.getPricingFrequency() != null)
				model.setPricingFrequencyId(entity.getPricingFrequency().getId());
			
			if (entity.getProductCategory() != null)
				model.setProductCategoryId(entity.getProductCategory().getId());
			
			if (entity.getSubItem() != null) {
				SubItem subItem = entity.getSubItem();
				model.setSubItemCode(subItem.getCode());
				model.setSubItemChineseName(subItem.getChineseName());
				model.setSubItemEnglishName(subItem.getEnglishName());
				model.setSubItemCompilationMethod(subItem.getCompilationMethod());
				
				if (subItem.getOutletType() != null) {
					OutletType outletType = subItem.getOutletType();
					model.setOutletTypeCode(outletType.getCode());
					model.setOutletTypeChineseName(outletType.getChineseName());
					model.setOutletTypeEnglishName(outletType.getEnglishName());
					
					if (outletType.getItem() != null) {
						Item item = outletType.getItem();
						model.setItemCode(item.getCode());
						model.setItemChineseName(item.getChineseName());
						model.setItemEnglishName(item.getEnglishName());
						
						if (item.getSubGroup() != null) {
							SubGroup subGroup = item.getSubGroup();
							model.setSubGroupCode(subGroup.getCode());
							model.setSubGroupChineseName(subGroup.getChineseName());
							model.setSubGroupEnglishName(subGroup.getEnglishName());
							
							if (subGroup.getGroup() != null) {
								Group group = subGroup.getGroup();
								model.setGroupCode(group.getCode());
								model.setGroupChineseName(group.getChineseName());
								model.setGroupEnglishName(group.getEnglishName());
								
								if (group.getSection() != null) {
									Section section = group.getSection();
									model.setSectionCode(section.getCode());
									model.setSectionChineseName(section.getChineseName());
									model.setSectionEnglishName(section.getEnglishName());
								}
							}
						}
					}
				}
			}
			
			OnSpotValidation onSpotValidation = entity.getOnSpotValidation();
			if (onSpotValidation != null)
				BeanUtils.copyProperties(onSpotValidation, model);
			
			model.setDisplayCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
			model.setDisplayModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
		}
		
		return model;
	}
	
	/**
	 * Get section by code
	 */
	public Section getSectionByCode(String code, String basePeriod) {
		return sectionDao.getByCode(code, basePeriod);
	}
	
	/**
	 * Get section by id
	 */
	public Section getSectionById(Integer id) {
		return sectionDao.findById(id);
	}
	
	/**
	 * Get section by id
	 */
	public UnitCommonModel getSectionCommonModelById(Integer id) {
		return sectionDao.getCommonModelById(id);
	}
	
	/**
	 * Get group by code
	 */
	public Group getGroupByCode(String code, String basePeriod) {
		return groupDao.getByCode(code, basePeriod);
	}
	
	/**
	 * Get group by id
	 */
	public Group getGroupById(Integer id) {
		return groupDao.findById(id);
	}
	
	/**
	 * Get Group by id
	 */
	public UnitCommonModel getGroupCommonModelById(Integer id) {
		return groupDao.getCommonModelById(id);
	}
	
	
	/**
	 * Get sub-group by code
	 */
	public SubGroup getSubGroupByCode(String code, String basePeriod) {
		return subGroupDao.getByCode(code, basePeriod);
	}
	
	/**
	 * Get sub-group by id
	 */
	public SubGroup getSubGroupById(Integer id) {
		return subGroupDao.findById(id);
	}
	
	/**
	 * Get Sub Group by id
	 */
	public UnitCommonModel getSubGroupCommonModelById(Integer id) {
		return subGroupDao.getCommonModelById(id);
	}
	
	/**
	 * Get item by code
	 */
	public Item getItemByCode(String code, String basePeriod) {
		return itemDao.getByCode(code, basePeriod);
	}
	
	/**
	 * Get Item by id
	 */
	public UnitCommonModel getItemCommonModelById(Integer id) {
		return itemDao.getCommonModelById(id);
	}
	
	/**
	 * Get outlet type by code
	 */
	public OutletType getOutletTypeByCode(String code, String basePeriod) {
		return outletTypeDao.getByCode(code, basePeriod);
	}
	
	/**
	 * Get Item by id
	 */
	public UnitCommonModel getOutletTypeCommonModelById(Integer id) {
		return outletTypeDao.getCommonModelById(id);
	}
	
	/**
	 * Get sub-item by code
	 */
	public SubItem getSubItemByCode(String code, String basePeriod) {
		return subItemDao.getByCode(code, basePeriod);
	}
	
	/**
	 * Get sub-item by code
	 */
	public UnitCommonModel getSubItemCommonModelById(Integer id) {
		return subItemDao.getCommonModelById(id);
	}
	
	public String queryUnitSelectSingle(Integer id){
		Unit entity = getById(id);
		if (entity == null)
			return null;
		else
			return entity.getCode() + " - " + entity.getChineseName();
	}
	
	
	
	/**
	 * Get unit with sub item
	 */
	public Unit getUnitWithSubItem(Integer id) {
		return unitDao.getUnitWithSubItem(id);
	}
	
	
	
	/**
	 * Get purpose select format
	 */
	public Select2ResponseModel querySectionSelect2(Select2RequestModel queryModel, String cpiBasePeriod) {
		queryModel.setRecordsPerPage(10);
		List<Section> entities = sectionDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), cpiBasePeriod);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = sectionDao.countSearch(queryModel.getTerm(), cpiBasePeriod);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Section d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get group select format
	 */
	public Select2ResponseModel queryGroupSelect2(Select2RequestModel queryModel, String cpiBasePeriod, String sectionCode) {
		queryModel.setRecordsPerPage(10);
		List<Group> entities = groupDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(),cpiBasePeriod, sectionCode);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = groupDao.countSearch(queryModel.getTerm(), cpiBasePeriod, sectionCode);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Group d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get sub-group select format
	 */
	public Select2ResponseModel querySubGroupSelect2(Select2RequestModel queryModel, String cpiBasePeriod, String groupCode) {
		queryModel.setRecordsPerPage(10);
		List<SubGroup> entities = subGroupDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), cpiBasePeriod, groupCode);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = subGroupDao.countSearch(queryModel.getTerm(), cpiBasePeriod, groupCode);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (SubGroup d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get item select format
	 */
	public Select2ResponseModel queryItemSelect2(Select2RequestModel queryModel, String cpiBasePeriod, String subGroupCode) {
		queryModel.setRecordsPerPage(10);
		List<Item> entities = itemDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), cpiBasePeriod, subGroupCode);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = itemDao.countSearch(queryModel.getTerm(), cpiBasePeriod, subGroupCode);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Item d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get outlet type select format
	 */
	public Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel queryModel, String cpiBasePeriod, String itemCode) {
		queryModel.setRecordsPerPage(10);
		List<OutletType> entities = outletTypeDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), cpiBasePeriod, itemCode);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = outletTypeDao.countSearch(queryModel.getTerm(), cpiBasePeriod, itemCode);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (OutletType d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get sub-item select format
	 */
	public Select2ResponseModel querySubItemSelect2(Select2RequestModel queryModel, String cpiBasePeriod, String outletTypeCode) {
		queryModel.setRecordsPerPage(10);
		List<SubItem> entities = subItemDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), cpiBasePeriod, outletTypeCode);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = subItemDao.countSearch(queryModel.getTerm(),cpiBasePeriod, outletTypeCode);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (SubItem d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get item by id
	 */
	public Item getItemById(Integer id) {
		return itemDao.findById(id);
	}
	
	/**
	 * Get outlet type by id
	 */
	public OutletType getOutletTypeById(Integer id) {
		return outletTypeDao.findById(id);
	}
	
	/**
	 * Get sub-item by id
	 */
	public SubItem getSubItemById(Integer id) {
		return subItemDao.findById(id);
	}
	
	
	
	/**
	 * Get section code by group code
	 */
	public String getSectionCodeByGroupCode(String code, String cpiBasePeriod) {
		return sectionDao.getSectionCodeByGroupCode(code, cpiBasePeriod);
	}
	
	/**
	 * Get group code by sub-group code
	 */
	public String getGroupCodeBySubGroupCode(String code, String cpiBasePeriod) {
		return groupDao.getGroupCodeBySubGroupCode(code, cpiBasePeriod);
	}
	
	/**
	 * Get sub-group code by item code
	 */
	public String getSubGroupCodeByItemCode(String code, String cpiBasePeriod) {
		return subGroupDao.getSubGroupCodeByItemCode(code, cpiBasePeriod);
	}
	
	/**
	 * Get item code by outlet-type code
	 */
	public String getItemCodeByOutletTypeCode(String code, String cpiBasePeriod) {
		return itemDao.getItemCodeByOutletTypeCode(code, cpiBasePeriod);
	}
	
	/**
	 * Get outlet-type code by sub-item code
	 */
	public String getOutletTypeCodeBySubItemCode(String code, String cpiBasePeriod) {
		return outletTypeDao.getOutletTypeCodeBySubItemCode(code, cpiBasePeriod);
	}

	/**
	 * Get unit select format
	 */
	public Select2ResponseModel queryUnitSelect2(Select2RequestModel queryModel) {
		return queryUnitSelect2(queryModel, null);
	}
	
	/**
	 * Get unit select format
	 */
	public Select2ResponseModel queryUnitSelect2(Select2RequestModel queryModel, List<Integer> purposeIds) {
		queryModel.setRecordsPerPage(10);
		List<Unit> entities = unitDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), purposeIds);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countSearch(queryModel.getTerm(), purposeIds);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Unit d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get by ids
	 */
	public List<Unit> getByIds(Integer[] ids) {
		return unitDao.getByIds(ids);
	}

	/**
	 * Get key value by ids
	 */
	public List<KeyValueModel> getKeyValueByIds(Integer[] ids) {
		List<Unit> entities = unitDao.getByIds(ids);
		if (entities == null)
			return null;
		
		List<KeyValueModel> result = new ArrayList<KeyValueModel>();
		for (Unit entity : entities) {
			KeyValueModel model = new KeyValueModel();
			model.setKey(entity.getId().toString());
			model.setValue(entity.getChineseName());
			result.add(model);
		}
		return result;
	}
	
	public List<OutletTypeSyncData> getUpdateOutletType(Date lastSyncTime){
		return outletTypeDao.getUpdateOutletType(lastSyncTime);
	}
	
	public List<SubItemSyncData> getUpdateSubItem(Date lastSyncTime){
		return subItemDao.getUpdateSubItem(lastSyncTime);
	}
	
	public List<UnitSyncData> getUpdateUnit(Date lastSyncTime){
		return unitDao.getUpdateUnit(lastSyncTime);
	}
	
	public List<OnSpotValidationSyncData> getUpdateOnSpotValidation(Date lastSyncTime){
		return onSpotValidationDao.getUpdateOnSpotValidation(lastSyncTime);
	}
	
	public List<UOMCategoryUnitSyncData> getUpdateUOMCategoryUnit(Date lastSyncTime){
		return unitDao.getUpdateUOMCategoryUnit(lastSyncTime);
	}
	
	public List<OutletUnitStatisticSyncData> getUpdateOutletUnitStatistic(Date lastSyncTime){
		return outletUnitStatisticDao.getUpdateOutletUnitStatistic(lastSyncTime);
	}
	
	public List<VarietySimpleModel> getItemDetailByIds(Integer[] ids){
		return itemDao.getItemDetailByIds(ids);
		
	}
	
	public List<VarietySimpleModel> getSubItemDetailByIds(Integer[] ids){
		return subItemDao.getSubItemDetailByIds(ids);
		
	}
	
	
	/**
	 * Get cpi base period select format
	 */
	public Select2ResponseModel queryCPIBasePeriodSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<String> entities = unitDao.searchAllCPIBasePeriod(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countCPIBasePeriod(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get cpi base period select format
	 */
	public Select2ResponseModel queryCPIBasePeriodWithNonEffectiveItemSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<String> entities = unitDao.searchAllCPIBasePeriod(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), true);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countCPIBasePeriod(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get cpi base period select format
	 */
	public Select2ResponseModel queryValidDistinctUnitCategorySelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<String> entities = unitDao.searchValidDistinctUnitCategory(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countValidDistinctUnitCategory(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Query unit category
	 */
	public Select2ResponseModel queryTodayValidDistinctUnitCategorySelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<String> rows = unitDao.searchValidDistinctUnitCategory2(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countValidDistinctUnitCategory2(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String row : rows) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(row));
			item.setText(row);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Query unit category
	 */
	public Select2ResponseModel queryCrossCheckGroupSelect2(Select2RequestModel queryModel, Integer[] purposeIds) {
		queryModel.setRecordsPerPage(10);
		
		List<String> rows = unitDao.searchValidDistinctCrossCheckGroup(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), purposeIds);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countValidDistinctCrossCheckGroup(queryModel.getTerm(), purposeIds);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String row : rows) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(row));
			item.setText(row);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Unit ICP Type select format
	 */
	public Select2ResponseModel queryICPTypeSelect2(Select2RequestModel queryModel){
		queryModel.setRecordsPerPage(10);
		List<String> entities = unitDao.searchAllICPType(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countICPType(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for(String d : entities){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Unit Category select format
	 */
	public Select2ResponseModel queryUnitCategorySelect2(Select2RequestModel queryModel){
		queryModel.setRecordsPerPage(10);
		List<String> entities = unitDao.searchUnitCategory(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = unitDao.countUnitCategory(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for(String d : entities){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get key value by ids
	 */
	public List<KeyValueModel> getItemByIds(Integer[] ids) {
		List<Item> entities = itemDao.getByIds(ids);
		if (entities == null)
			return null;
		
		List<KeyValueModel> result = new ArrayList<KeyValueModel>();
		for (Item entity : entities) {
			KeyValueModel model = new KeyValueModel();
			model.setKey(entity.getId().toString());
			model.setValue(StringUtils.isEmpty(entity.getChineseName())?entity.getEnglishName():entity.getChineseName());
			result.add(model);
		}
		return result;
	}

	public boolean isUnitCodeValid(UnitEditModel model) {
		String unitCode = model.getCode();
		String subItemCode = model.getSubItemCode();
		
		// The first character of Variety should be the same as the code in subitem
		if (!unitCode.startsWith(subItemCode)) {
			return false;
		}
		
		return true;
	}
}
