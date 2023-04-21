package capi.service.userAccountManagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.RankDao;
import capi.entity.Rank;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.userAccountManagement.RankEditModel;
import capi.model.userAccountManagement.RankTableList;
import capi.service.BaseService;

@Service("RankService")
public class RankService extends BaseService {
	

	
	@Autowired
	private RankDao rankDao;
	
	/** 
	 * datatable query
	 */
	public DatatableResponseModel<RankTableList> getRankList(DatatableRequestModel model){
		
		Order order = this.getOrder(model,"", "code", "name");
		
		String search = model.getSearch().get("value");
		
		List<RankTableList> result = rankDao.listRank(search, model.getStart(), model.getLength(), order);

		
		DatatableResponseModel<RankTableList> response = new DatatableResponseModel<RankTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = rankDao.countRank();
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = rankDao.countRank(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	

	/**
	 * Get by ID
	 */
	public Rank getRankById(int id) {
		return rankDao.findById(id);
	}
	

	/**
	 * Save
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public boolean saveRank(RankEditModel model) {
		
		Rank oldEntity = null;
		
		
		if (model.getId() != null && model.getId() > 0) {
			oldEntity = getRankById(model.getId());
		} else {
			Rank rank = rankDao.getRankByCode(model.getCode());
			if (rank!=null) return false;
			
			oldEntity = new Rank();
		}
		
		oldEntity.setCode(model.getCode());
		oldEntity.setName(model.getName());
		
		rankDao.save(oldEntity);
		rankDao.flush();
		
		return true;
	}
	

	/**
	 * Delete
	 */	
	@Transactional
	public boolean deleteRanks(List<Integer> id) {
		List<Rank> ranks = rankDao.getRankByIds(id);
		if (id.size() != ranks.size()){
			return false;
		}
		
		for (Rank rank : ranks){
			rankDao.delete(rank);
		}

		rankDao.flush();

		return true;
	}
	
	
	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public RankEditModel convertEntityToModel(Rank entity){

		RankEditModel model = new RankEditModel();
		BeanUtils.copyProperties(entity, model);

		return model;
	}
	


	/**
	 * Get rank select format
	 */
	public Select2ResponseModel queryRankSelect(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<Rank> entities = rankDao.searchRank(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = rankDao.countSearchRank(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Rank r : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(r.getRankId()));
			item.setText(r.getCode() + " - " + r.getName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
}
