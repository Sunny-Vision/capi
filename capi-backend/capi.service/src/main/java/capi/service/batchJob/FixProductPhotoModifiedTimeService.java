package capi.service.batchJob;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ProductDao;
import capi.entity.Product;

/**
 * Fix Product rows with photo path but null photo modified time, set to time of
 * this batch job execution. Null photo modified time affects mobile client
 * housekeep found missing product photo. This is a temp fix data patch via
 * batch job before processing root cause of PIR-261.
 * 
 * @author toby_ng
 */
@Service("FixProductPhotoModifiedTimeService")
public class FixProductPhotoModifiedTimeService implements BatchJobService {

	private static final Logger logger = LoggerFactory.getLogger(FixProductPhotoModifiedTimeService.class);

	@Autowired
	private ProductDao productDao;

	@Override
	public String getJobName() {
		return "fix product photo modified time";
	}

	@Override
	public void runTask() throws Exception {
		logger.info("FixProductPhotoModifiedTimeService - Start");

		List<Product> products = productDao.getProductWithPhotoPathButNullPhotoModifiedTime();

		List<Integer> editedPhoto1ProductIds = new java.util.ArrayList<>();
		List<Integer> editedPhoto2ProductIds = new java.util.ArrayList<>();
		int totalUpdateCount = 0;
		for (Product product : products) {
			boolean edited = false;

			if (product.getPhoto1Path() != null && product.getPhoto1ModifiedTime() == null) {
				product.setPhoto1ModifiedTime(new Date());
				edited = true;
				editedPhoto1ProductIds.add(product.getProductId());
			}
			if (product.getPhoto2Path() != null && product.getPhoto2ModifiedTime() == null) {
				product.setPhoto2ModifiedTime(new Date());
				edited = true;
				editedPhoto2ProductIds.add(product.getProductId());
			}

			if (edited) {
				productDao.save(product);
				
				if (++totalUpdateCount % 100 == 0) {
					productDao.flush();
					logProductIdsAndClear(editedPhoto1ProductIds, editedPhoto2ProductIds);
				}
			}
		}

		if (totalUpdateCount > 0) {
			productDao.flush();
			logProductIdsAndClear(editedPhoto1ProductIds, editedPhoto2ProductIds);
			
			logger.info("done: updated {} products with photo path missing modified time", totalUpdateCount);
		} else {
			logger.info("done: no products with photo path missing modified time");
		}

		logger.info("FixProductPhotoModifiedTimeService - End");
	}
	
	private void logProductIdsAndClear(List<Integer> editedPhoto1ProductIds, List<Integer> editedPhoto2ProductIds) {
		logger.info("updated product IDs missing photo 1 modified time to current time: {}",
				editedPhoto1ProductIds.stream().map(id -> id.toString()).collect(Collectors.joining(", ")));
		logger.info("updated product IDs missing photo 2 modified time to current time: {}",
				editedPhoto2ProductIds.stream().map(id -> id.toString()).collect(Collectors.joining(", ")));
		editedPhoto1ProductIds.clear();
		editedPhoto2ProductIds.clear();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}
}
