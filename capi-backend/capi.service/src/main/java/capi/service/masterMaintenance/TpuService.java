package capi.service.masterMaintenance;

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
import capi.model.api.dataSync.TpuSyncData;
import capi.model.masterMaintenance.TpuEditModel;
import capi.model.masterMaintenance.TpuTableList;
import capi.service.BaseService;

@Service("TpuService")
public class TpuService extends BaseService {

	@Autowired
	private TpuDao tpuDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	/**
	 * Get by ID
	 */
	public Tpu getTpuById(int id) {
		return tpuDao.findById(id);
	}
	
	
	public List<Tpu> getAllTpus() {
		return tpuDao.findAll();
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<TpuTableList> getTpuList(DatatableRequestModel model, Integer districtId){

		Order order = this.getOrder(model, "tpuId", "code", "districtCode", "districtChineseName", "districtEnglishName", "councilDistrict");
		
		String search = model.getSearch().get("value");
		
		List<TpuTableList> result = tpuDao.selectAllTpu(search, model.getStart(), model.getLength(), order, districtId);
		
		DatatableResponseModel<TpuTableList> response = new DatatableResponseModel<TpuTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = tpuDao.countSelectAllTpu("", districtId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = tpuDao.countSelectAllTpu(search, districtId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save tpu
	 * @param model data model from ui
	 * @return true if update success, false if the code of TPU exist
	 * @throws Exception
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveTpu(TpuEditModel model) throws Exception {

		Tpu oldEntity = null;
		if (model.getTpuId() != null && model.getTpuId() > 0) {
			oldEntity = getTpuById(model.getTpuId());
		} else {
			// create tpu
			Tpu tpu = tpuDao.getTpuByCode(model.getCode());
			if (tpu!=null) return false;
			
			oldEntity = new Tpu();
		}

		District district = districtDao.findById(model.getDistrictId());

		oldEntity.setCode(model.getCode());
		//oldEntity.setDescription(model.getDescription());
		oldEntity.setCouncilDistrict(model.getCouncilDistrict());
		oldEntity.setDistrict(district);

		tpuDao.save(oldEntity);
		tpuDao.flush();
		
		return true;
	}

	/**
	 * Delete TPU
	 */
	@Transactional
	public boolean deleteTpu(List<Integer> ids) {
		
		List<Tpu> tpus = tpuDao.getTpusByIds(ids);
		if (ids.size() != tpus.size()){
			return false;
		}
		
		for (Tpu tpu : tpus){
			tpuDao.delete(tpu);
		}

		tpuDao.flush();

		return true;
	}

	/**
	 * Convert entity to model
	 */
	public TpuEditModel convertEntityToModel(Tpu entity) {

		TpuEditModel model = new TpuEditModel();
		BeanUtils.copyProperties(entity, model);
		
		if(entity.getDistrict() != null) {
			model.setDistrictId(entity.getDistrict().getId());
			model.setDistrictLabel(entity.getDistrict().getCode() + " - " + entity.getDistrict().getEnglishName());
		}

		return model;
	}

	public List<TpuEditModel> getAllTpuCode() {
		return tpuDao.getAllTpuCode();
	}
	

	public String getDistrictLabel(int districtId) {

		District district = districtDao.findById(districtId);
		
		return (district.getCode() + " - " + district.getEnglishName());
	}
	
	
	public List<TpuSyncData> getUpdatedTpus(Date lastSyncTime){
		return tpuDao.getUpdatedTpus(lastSyncTime);
	}
	

	
	/**
	 * Get tpu select2 format
	 */
	public Select2ResponseModel queryTpuSelect2(Select2RequestModel queryModel, Integer[] districtId) {
		queryModel.setRecordsPerPage(10);
		List<Tpu> entities = tpuDao.searchTpuByDistrict(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), districtId);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = tpuDao.countSearchTpuByDistrict(queryModel.getTerm(), districtId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Tpu d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getTpuId()));
			//item.setText(d.getCode() + " - " + d.getDescription());
			item.setText(d.getCode());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
}
