package capi.service.masterMaintenance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.DistrictDao;
import capi.dal.TpuDao;
import capi.entity.District;
import capi.entity.Tpu;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.DistrictSyncData;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.masterMaintenance.DistrictTableList;
import capi.service.BaseService;

@Service("DistrictService")
public class DistrictService extends BaseService {

	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private TpuDao tpuDao;
	
	/**
	 * Get by ID
	 */
	public District getDistrictById(int id) {
		return districtDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<DistrictTableList> getDistrictList(DatatableRequestModel model){

		Order order = this.getOrder(model, "districtId", "code", "chineseName", "englishName", "coverage", "tpus");
		
		String search = model.getSearch().get("value");
		
		List<DistrictTableList> result = districtDao.selectAllDistrict(search, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<DistrictTableList> response = new DatatableResponseModel<DistrictTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = districtDao.countSelectAllDistrict("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = districtDao.countSelectAllDistrict(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save District
	 * @param model data model from ui
	 * @return true if update successfully, false if district code exist
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveDistrict(DistrictEditModel model) throws Exception {

		District oldEntity = null;
		if (model.getDistrictId() != null && model.getDistrictId() > 0) {
			oldEntity = getDistrictById(model.getDistrictId());
			
			District district = districtDao.getDistrictByCode(model.getCode());
			if(district != null) {
				if( !district.getDistrictId().equals(oldEntity.getDistrictId()) ) {
					return false;
				}
			}
		} else {
			// create district
			District district = districtDao.getDistrictByCode(model.getCode());
			if (district != null) return false;
			
			oldEntity = new District();
		}

		oldEntity.setCode(model.getCode());
		oldEntity.setChineseName(model.getChineseName());
		oldEntity.setEnglishName(model.getEnglishName());
		oldEntity.setCoverage(model.getCoverage());

		districtDao.save(oldEntity);
		districtDao.flush();
		return true;
	}

	/**
	 * Delete district
	 */
	@Transactional
	public boolean deleteDistrict(List<Integer> ids) {

		// Delete all the TPU which is linked with district		
		List<District> districts = districtDao.getDistrictsByIds(ids);
		if (districts.size() != ids.size()){
			return false;
		}
		
		List<Tpu> tpus = tpuDao.getTpuByDistrictIds(ids);
		
		for (Tpu tpu: tpus){
			tpuDao.delete(tpu);
		}	

		for (District district: districts){
			districtDao.delete(district);
		}		
		
		districtDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public DistrictEditModel convertEntityToModel(District entity){

		DistrictEditModel model = new DistrictEditModel();
		BeanUtils.copyProperties(entity, model);

		return model;
	}

	public List<DistrictEditModel> getAllDistrictCode() {
		return districtDao.getAllDistrictCode();
	}
	
	public List<DistrictSyncData> getUpdatedDistricts(Date lastSyncTime){
		return districtDao.getUpdatedDistricts(lastSyncTime);
	}
	

	/**
	 * Get district select2 format
	 */
	public Select2ResponseModel queryDistrictSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<District> entities = districtDao.searchDistrict(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = districtDao.countSearchDistrict(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (District d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getDistrictId()));
			item.setText(d.getCode() + " - " + d.getEnglishName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
}

