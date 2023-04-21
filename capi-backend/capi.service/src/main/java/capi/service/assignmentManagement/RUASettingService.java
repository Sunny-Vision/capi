package capi.service.assignmentManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.BatchDao;
import capi.dal.DistrictDao;
import capi.dal.OutletDao;
import capi.dal.PricingMonthDao;
import capi.dal.ProductDao;
import capi.dal.QuotationDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.District;
import capi.entity.Quotation;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffNameModel;
import capi.model.assignmentManagement.RUASettingEditModel;
import capi.model.assignmentManagement.RUASettingTableList;
import capi.service.BaseService;

@Service("RUASettingService")
public class RUASettingService extends BaseService {

	@Autowired
	private QuotationDao quotationDao;

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private PricingMonthDao pricingFrequencyDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private OutletDao outletDao;

	@Autowired
	private DistrictDao districtDao;

	@Autowired
	private UserDao userDao;

	/**
	 * Get by ID
	 */
	public Quotation getRUASettingById(int id) {
		return quotationDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<RUASettingTableList> getRUASettingList(DatatableRequestModel model/*, String purpose
																		, Integer unitId, Integer productId, Integer outletId
																		, Integer batchId, Integer pricingFrequencyId, Integer cpiQoutationType*/){

		Order order = this.getOrder(model, "quotationId", "purpose", "unitCode", "unitName", "productId", "productAttribute"
									, "firmId", "firmName", "batchCode", "pricingFrequency", "cpiFormType", "q.ruaDate");
		
		String search = model.getSearch().get("value");

		Integer purposeId = null;
		if(StringUtils.isNotEmpty(model.getSearch().get("purposeId")))
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		
		Integer unitId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))) 
			unitId = Integer.parseInt(model.getSearch().get("unitId"));
		
		Integer productId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("productId")))
			productId = Integer.parseInt(model.getSearch().get("productId"));
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		Integer batchId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("batchId")))
			batchId = Integer.parseInt(model.getSearch().get("batchId"));
		
		Integer pricingFrequencyId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("pricingFrequencyId"))) 
			pricingFrequencyId = Integer.parseInt(model.getSearch().get("pricingFrequencyId"));
		
		Integer cpiQoutationType = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("cpiQoutationType"))) 
			cpiQoutationType = Integer.parseInt(model.getSearch().get("cpiQoutationType"));

		List<RUASettingTableList> result = quotationDao.selectAllRUASetting(search, model.getStart(), model.getLength(), order
																			, purposeId, unitId, productId, outletId
																			, batchId, pricingFrequencyId, cpiQoutationType);
		
		DatatableResponseModel<RUASettingTableList> response = new DatatableResponseModel<RUASettingTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationDao.countSelectAllRUASetting("", purposeId, unitId, productId, outletId, batchId, pricingFrequencyId, cpiQoutationType);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationDao.countSelectAllRUASetting(search, purposeId, unitId, productId, outletId, batchId, pricingFrequencyId, cpiQoutationType);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	public List<StaffNameModel> getSelectedStaffName(List<Integer> ids){
		Iterator<User> users = this.userDao.getUsersByIds(ids).iterator();
		List<StaffNameModel> nameList = new ArrayList<StaffNameModel>();
		while(users.hasNext()){
			User u = users.next();
			StaffNameModel model = new StaffNameModel();
			model.setStaffName(u.getStaffCode() + " - " + u.getChineseName() + " ( " + u.getDestination() + " )");
			model.setUserId(u.getUserId());
			nameList.add(model);
		}
		return nameList;
	}

	/**
	 * Save RUA Setting
	 * @param model data model from ui
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveRUASetting(RUASettingEditModel model) throws Exception {

		Quotation oldEntity = null;
		if (model.getQuotationId() != null && model.getQuotationId() > 0) {
			oldEntity = getRUASettingById(model.getQuotationId());
		
			oldEntity.setRUAAllDistrict(model.getIsRUAAllDistrict());
			if(model.getIsRUAAllDistrict() == true) {
				oldEntity.setDistrict(null);
			} else {
				District district = districtDao.getDistrictById(model.getDistrictId());
				oldEntity.setDistrict(district);
			}
			
			if (model.getUserId() != null) {
				ArrayList<Integer> oldUserIds = new ArrayList<Integer>();
				for (User user : oldEntity.getUsers()) {
					oldUserIds.add(user.getId());
				}
				
				Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldUserIds, model.getUserId());
				Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getUserId(), oldUserIds);
				
				if (deletedIds.size() > 0){
					Integer[] deletedIdsArray = deletedIds.toArray(new Integer[0]);
					List<Integer> deletedIdsList = new ArrayList<Integer>();
					for(int i = 0; i < deletedIdsArray.length; i++) {
						Integer deletedId = deletedIdsArray[i];
						deletedIdsList.add(deletedId);
					}
					
					List<User> deletedUsers = userDao.getUsersByIds(deletedIdsList);
					for (User user: deletedUsers){
						oldEntity.getUsers().remove(user);
					}
				}
				
				if (newIds.size() > 0) {
					Integer[] newIdsArray = newIds.toArray(new Integer[0]);
					List<Integer> newIdsList = new ArrayList<Integer>();
					for(int i = 0; i < newIdsArray.length; i++) {
						Integer newId = newIdsArray[i];
						newIdsList.add(newId);
					}
					
					List<User> newUsers = userDao.getUsersByIds(newIdsList);
					oldEntity.getUsers().addAll(newUsers);
				}
			} else {
				oldEntity.getUsers().clear();
			}
	
			quotationDao.save(oldEntity);
			quotationDao.flush();
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Convert entity to model
	 */
	public RUASettingEditModel convertEntityToModel(Quotation entity) {

		RUASettingEditModel model = new RUASettingEditModel();
		BeanUtils.copyProperties(entity, model);
		
		RUASettingEditModel row = quotationDao.selectRUASettingRowById(entity.getQuotationId());
		
		model.setUnitCode(row.getUnitCode());
		model.setUnitName(row.getUnitName());
		model.setProductAttribute(row.getProductAttribute());
		model.setFirmName(row.getFirmName());
		model.setBatchCode(row.getBatchCode());
		model.setRuaDate(row.getRuaDate());
		model.setIsRUAAllDistrict(row.getIsRUAAllDistrict());
		
		if(row.getIsRUAAllDistrict() == false) {
			model.setDistrictId(row.getDistrictId());
			District district = districtDao.getDistrictById(row.getDistrictId());
			if (district != null){
				model.setDistrictLabel(district.getCode() + " - " + district.getChineseName());
			}
		}
		
		Iterator<User> users = entity.getUsers().iterator();
		ArrayList<Integer> userIds = new ArrayList<Integer>();
		while (users.hasNext()) {
			userIds.add(users.next().getId());
		}
		model.setUserId(userIds);
		
		return model;
	}

}
