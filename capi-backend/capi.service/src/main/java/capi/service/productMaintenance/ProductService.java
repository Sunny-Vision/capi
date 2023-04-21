package capi.service.productMaintenance;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import capi.dal.PointToNoteDao;
import capi.dal.ProductAttributeDao;
import capi.dal.ProductCleaningDao;
import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.dal.ProductSpecificationDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.VwProductSpecDao;
import capi.entity.PointToNote;
import capi.entity.Product;
import capi.entity.ProductAttribute;
import capi.entity.ProductCleaning;
import capi.entity.ProductGroup;
import capi.entity.ProductSpecification;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SystemConfiguration;
import capi.entity.VwProductFullSpec;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.ProductAttributeSyncData;
import capi.model.api.dataSync.ProductCleaningSyncData;
import capi.model.api.dataSync.ProductGroupSyncData;
import capi.model.api.dataSync.ProductSpecificationSyncData;
import capi.model.api.dataSync.ProductSyncData;
import capi.model.api.dataSync.ProductZipImageSyncData;
import capi.model.api.dataSync.UpdateProductImageModel;
import capi.model.productMaintenance.ProductEditModel;
import capi.model.productMaintenance.ProductGroupEditModel;
import capi.model.productMaintenance.ProductGroupTableList;
import capi.model.productMaintenance.ProductSpecificationEditModel;
import capi.model.productMaintenance.ProductTableList;
import capi.service.AppConfigService;
import capi.service.BaseService;

import capi.service.CommonService;

@Service("ProductService")
public class ProductService extends BaseService {
	
	@Autowired
	private ProductCleaningDao productCleaningDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private ProductAttributeDao productAttributeDao;

	@Autowired
	private ProductSpecificationDao productSpecificationDao;

	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private VwProductSpecDao vwProductSpecDao;
	
	@Autowired
	private capi.dal.VwProductFullSpecDao vwProductFullSpecDao;
	
	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SystemConfigurationDao systemConfigurationDao;
	
	@Autowired
	private PointToNoteDao pointToNoteDao;

	static private String productImagePath = "product";
	
	/** 
	 * datatable query product group
	 */
	public DatatableResponseModel<ProductGroupTableList> queryProductGroup(DatatableRequestModel model){
		
		Order order = this.getOrder(model, "", "code", "chineseName", "englishName", "noOfProduct", "noOfUnit","noOfAttribute", "status");
		
		String search = model.getSearch().get("value");
		
		List<ProductGroupTableList> result = productGroupDao.listProductGroup(search, model.getStart(), model.getLength(), order);
					
		DatatableResponseModel<ProductGroupTableList> response = new DatatableResponseModel<ProductGroupTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal =productGroupDao.countProductGroup("");
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = productGroupDao.countProductGroup(search);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/** 
	 * datatable query product
	 */
	public DatatableResponseModel<ProductTableList> queryProduct(DatatableRequestModel model){
		
		Order order = this.getOrder(model, "", "productId", "productGroupCode", "productGroupChineseName", 														"productGroupEnglishName",  "", "noOfQuotation", "noOfQuotationRecord", "remark", 
									"status", "createdDate", "barcode", "reviewed");
		
		String search = model.getSearch().get("value");
		Integer productGroupId = !StringUtils.isEmpty(model.getSearch().get("productGroupId")) ? Integer.parseInt(model.getSearch().get("productGroupId")) : null;
		String  hasAnyLinkage = model.getSearch().get("hasAnyLinkage");
		String	status = model.getSearch().get("status");
		Boolean	reviewed = !StringUtils.isEmpty(model.getSearch().get("reviewed")) ? (model.getSearch().get("reviewed").equals("Y") ? true : false ): null;
				
		List<ProductTableList> result = productDao.listProduct(search, productGroupId, hasAnyLinkage, status, reviewed, model.getStart(), model.getLength(), order);
					
		DatatableResponseModel<ProductTableList> response = new DatatableResponseModel<ProductTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal =productDao.countProduct("",null,null,null,null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = productDao.countProduct(search, productGroupId, hasAnyLinkage, status, reviewed);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Get Product Group select2 format
	 */
	public Select2ResponseModel queryProductGroupSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<ProductGroup> productGroups = productGroupDao.searchProductGroup(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), Order.asc("code"));
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = productGroupDao.countSearchProductGroup(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (ProductGroup productGroup : productGroups) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(productGroup.getProductGroupId()));
			item.setText(getProductGroupSelectText(productGroup));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	/**
	 * Get Product Attribute Values select2 format
	 */
	public Select2ResponseModel queryProdAttrValueSelect2(Integer productGroupId, Integer productAttributeId, Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<String> values  = vwProductSpecDao.getSelect2Value(queryModel.getTerm(), 
				queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
				Order.asc("attributeValue"), productGroupId, productAttributeId );
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = vwProductSpecDao.countSelect2Value(queryModel.getTerm(), productGroupId, productAttributeId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String attValue : values) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(attValue);
			item.setText(attValue);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	/**
	 * Get Product Group Select Text
	 */
	public String getProductGroupSelectText(ProductGroup productGroup) {
		return productGroup.getCode() + " - " + productGroup.getChineseName();
	}
	
	/**
	 * Get ProductGroup by ID
	 */
	public ProductGroup getProductGroupById(int id) {
		return productGroupDao.findById(id);
	}
	
	/**
	 * Get ProductGroup by Code
	 */
	public ProductGroup getProductGroupByCode(String code) {
		return productGroupDao.getProductGroupsByCode(code);
	}

	/**
	 * Get ProductGroupEditModel by ID
	 */
	public ProductGroupEditModel getProductGroupEditModelById(Integer id) {
		return productGroupDao.getProductGroupEditModelById(id);
	}
	
	/**
	 * Count Product Specification by Product Attribute ID
	 */
	public long countProductSpecByProductAttrId(Integer productAttributeId){
		return productSpecificationDao.countProductSpecByProductAttrId(productAttributeId);
	}
	
	/**
	 * Get Product Attributes by Product Group ID
	 */
	public List<ProductAttribute> getProductAttributesByProductGroupId(int id) {
		return productAttributeDao.getProductAttributeByProductGroupId(id);
	}
	/**
	 * Get Product Attribute by ID
	 */
	public ProductAttribute getProductAttributeById(int id) {
		return productAttributeDao.findById(id);
	}
	
	/**
	 * Get ProductEditModel by ID
	 */
	public ProductEditModel getProductEditModelById(Integer id) {
		return productDao.getProductEditModelById(id);
	}
	
	/**
	 * Get Product by ID
	 * 
	 */
	
	public Product getProductById(Integer id) {
		return productDao.findById(id);
	}
	
	/**
	 * Check Product by ID
	 * 
	 * return true if exist
	 */
	
	public boolean checkProductById(Integer id) {
		return productDao.checkProductById(id);
	}
	
	
	/**
	 * Get ProductSpecificationEditModel by ID
	 */
	public List<ProductSpecificationEditModel>  getProductSpecificationListByIds(Integer productId, Integer productGroupId) {
		
		List<VwProductFullSpec> specs = vwProductFullSpecDao.GetAllByProductId(productId, null);
		if (specs == null || specs.size() == 0){
			return productGroupDao.getProductSpecificationEditModelByProductGroupId(productGroupId);
		}
		else{			
			List<ProductSpecificationEditModel> modelList = new ArrayList<ProductSpecificationEditModel>();
			for (VwProductFullSpec spec : specs){
				ProductSpecificationEditModel model = new ProductSpecificationEditModel();
				BeanUtils.copyProperties(spec, model);
				
				model.setMandatory(spec.getIsMandatory());
				model.setName(spec.getSpecificationName());
				model.setProductAttributeId(spec.getProductAttribute().getId());
				modelList.add(model);
			}			
			
			return modelList;
		}
		
//		
//		List<ProductSpecificationEditModel> all = productGroupDao.getProductSpecificationEditModelByProductGroupId(productGroupId);
//		List<ProductSpecificationEditModel> existing = null;
//		
//		if (productId != null && productId > 0) {
//			existing = productDao.getProductSpecificationEditModelById(productId);
//			if (all.size() != existing.size()) {
//				for (int i = 0 ; i < all.size() ; i++ ) {
//					for (int j = 0 ; j < existing.size() ; j++ ) {
//						if (all.get(i).getProductAttributeId() == existing.get(j).getProductAttributeId()){
//							all.get(i).setValue(existing.get(j).getValue());
//							break;
//						}
//					}
//				}
//			} else {
//				all = existing;
//			}
//		}
//		
//		return all;
	}
	
	/**
	 * Get Product Attribute By Product Specification ID
	 */
	public List<ProductSpecificationEditModel> getProductAttributeByProductSpecificationId(Integer productGroupId) {
		
		return productGroupDao.getProductSpecificationEditModelByProductGroupId(productGroupId);
	}
	
	/**
	 * Save Product Group
	 */
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public boolean saveProductGroup(ProductGroupEditModel model) {
		ProductGroup oldEntity = null;
		if (model.getProductGroupId() != null && model.getProductGroupId() > 0) {
			oldEntity = getProductGroupById(model.getProductGroupId());
		} else {
			ProductGroup checkProductGroup = productGroupDao.getProductGroupsByCode(model.getCode());
			if (checkProductGroup != null) 
				return false;

			oldEntity = new ProductGroup();
		}
		
		BeanUtils.copyProperties(model, oldEntity);
		
		productGroupDao.save(oldEntity);
		
		List<ProductAttribute> oldAttributes = new ArrayList<ProductAttribute>();
		if(oldEntity.getProductGroupId() != null){
			oldAttributes = productAttributeDao.getProductAttributeByProductGroupId(oldEntity.getProductGroupId());
		}

		List<Integer> oldIds = new ArrayList<Integer>();
		for(ProductAttribute oldAttribute: oldAttributes){
			oldIds.add(oldAttribute.getProductAttributeId());
		}
		
		List<ProductAttribute> newAttributes = new ArrayList<ProductAttribute>();
		if(model.getProductAttributes() != null) {
			newAttributes.addAll(model.getProductAttributes().values());	
		}

		List<Integer> newIds = new ArrayList<Integer>();
			
		if(newAttributes.size() > 0){
			for(ProductAttribute newAttribute: newAttributes){
				if (newAttribute.getProductAttributeId() != null && newAttribute.getProductAttributeId() > 0) {
					newIds.add(newAttribute.getProductAttributeId());
					ProductAttribute existingAttribute = productAttributeDao.findById(newAttribute.getProductAttributeId());
					BeanUtils.copyProperties(newAttribute, existingAttribute);
					existingAttribute.setProductGroup(oldEntity);
					productAttributeDao.save(existingAttribute);
				} else {
					newAttribute.setProductGroup(oldEntity);
					productAttributeDao.save(newAttribute);
				}
			}
		}
		
		Collection<Integer> removeEntries = CollectionUtils.subtract(oldIds, newIds);
	
		if (removeEntries.size() > 0){
			for (Integer removeEntry: removeEntries){
				List<ProductSpecification> specEntries = productSpecificationDao.getProductSpecificationByProductAttributeId(removeEntry);
				for (ProductSpecification specEntry : specEntries){
					productSpecificationDao.delete(specEntry);
				}
				ProductAttribute attribute = productAttributeDao.findById(removeEntry);
				productAttributeDao.delete(attribute);						
			}
		}

		productGroupDao.flush();
		return true;

	}

	/**
	 * Save Product
	 * @throws Exception 
	 */
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public void saveProduct(ProductEditModel model, InputStream photo1ImageStream, InputStream photo2ImageStream,String fileBaseLoc) throws Exception {
		Product oldEntity = null;
		ProductGroup groupEntity = null;
		
		if (model.getProductId() != null && model.getProductId() > 0) {
			oldEntity = getProductById(model.getProductId());
		} else {
			oldEntity = new Product();
			groupEntity = getProductGroupById(model.getProductGroupId());
			oldEntity.setProductGroup(groupEntity);
		}
		
		BeanUtils.copyProperties(model, oldEntity);
		
		if (!StringUtils.isEmpty(model.getPhoto1Path()) && model.getPhoto1Path().endsWith("/del")) {
			//Click remove button and no upload new photo
			deleteImage(model.getPhoto1Path().replace("/del", ""), fileBaseLoc);
			oldEntity.setPhoto1Path(null);
			oldEntity.setPhoto1ModifiedTime(null);
		} else {
			if (photo1ImageStream.available() > 0) {
				if (oldEntity.getPhoto1Path() != null) {
					deleteImage(oldEntity.getPhoto1Path(), fileBaseLoc);
				}
				String imagePath = saveImage(photo1ImageStream, fileBaseLoc);
				oldEntity.setPhoto1Path(imagePath);
				oldEntity.setPhoto1ModifiedTime(new java.util.Date());
			}
		}
			if (!StringUtils.isEmpty(model.getPhoto2Path()) && model.getPhoto2Path().endsWith("del")) {
				//Click remove button and no upload new photo
				deleteImage(model.getPhoto2Path().replace("/del", ""), fileBaseLoc);
				oldEntity.setPhoto2Path(null);
				oldEntity.setPhoto2ModifiedTime(null);
			} else {
				if (photo2ImageStream.available() > 0) {
					if (oldEntity.getPhoto2Path() != null) {
						deleteImage(oldEntity.getPhoto2Path(), fileBaseLoc);
					}
					String imagePath = saveImage(photo2ImageStream, fileBaseLoc);
					oldEntity.setPhoto2Path(imagePath);
					oldEntity.setPhoto2ModifiedTime(new java.util.Date());
				}
			}
		
		productDao.save(oldEntity);
		
//		if (photo1ImageStream.available() > 0) {
//			if (oldEntity.getPhoto1Path() != null) {
//				deleteImage(oldEntity.getPhoto1Path(), fileBaseLoc);
//			}
//			
//			String imagePath = saveImage(photo1ImageStream, fileBaseLoc);
//			oldEntity.setPhoto1Path(imagePath);
//			oldEntity.setPhoto1ModifiedTime(new java.util.Date());
//		}
//		
//		if (photo2ImageStream.available() > 0) {
//			if (oldEntity.getPhoto2Path() != null) {
//				deleteImage(oldEntity.getPhoto2Path(), fileBaseLoc);
//			}
//			
//			String imagePath = saveImage(photo2ImageStream, fileBaseLoc);
//			oldEntity.setPhoto2Path(imagePath);
//			oldEntity.setPhoto2ModifiedTime(new java.util.Date());
//		}
//
//		productDao.save(oldEntity);		

		if (oldEntity.getStatus() != null && oldEntity.getStatus().equals("Inactive")){
			List<Integer> productIds = new ArrayList<Integer>();
			productIds.add(oldEntity.getId());
			List<Quotation> quotations = quotationDao.getQuotationsByProductIds(productIds);
			for (Quotation quotation: quotations){
				quotation.setProduct(null);
			}
		}
		
		List<ProductSpecification> oldSpecifications = new ArrayList<ProductSpecification>();
		if(model.getProductId() != null && model.getProductId() > 0){
			oldSpecifications = productSpecificationDao.getProductSpecificationByProductId(oldEntity.getProductId());
		}

		List<ProductSpecificationEditModel> newSpecificationEditModels = new ArrayList<ProductSpecificationEditModel>();
		List<Integer> oldIds = new ArrayList<Integer>();
		
		if(model.getProductSpecificationEditModels() != null) {
			newSpecificationEditModels.addAll(model.getProductSpecificationEditModels().values());
			for(ProductSpecification oldSpecification: oldSpecifications){
				oldIds.add(oldSpecification.getProductSpecificationId());
			}
		}

		List<Integer> newIds = new ArrayList<Integer>();
			
		if(newSpecificationEditModels.size() > 0){
			for(ProductSpecificationEditModel newSpecificationEditModel: newSpecificationEditModels){
				if (newSpecificationEditModel.getProductSpecificationId() != null && newSpecificationEditModel.getProductSpecificationId() > 0) {
					newIds.add(newSpecificationEditModel.getProductSpecificationId());
					ProductSpecification existingSpecification = productSpecificationDao.findById(newSpecificationEditModel.getProductSpecificationId());
					BeanUtils.copyProperties(newSpecificationEditModel, existingSpecification);
					existingSpecification.setProduct(oldEntity);
					productSpecificationDao.save(existingSpecification);
				} else {
					ProductSpecification newSpecification = new ProductSpecification();
					newSpecification.setValue(newSpecificationEditModel.getValue());
					newSpecification.setProductAttribute(productAttributeDao.findById(newSpecificationEditModel.getProductAttributeId()));
					newSpecification.setProduct(oldEntity);
					productSpecificationDao.save(newSpecification);
				}
			}
		}
		
		Collection<Integer> removeEntries = CollectionUtils.subtract(oldIds, newIds);
		if (removeEntries.size() > 0){
			for (Integer removeEntry: removeEntries){
				ProductSpecification specification = productSpecificationDao.findById(removeEntry);
				productSpecificationDao.delete(specification);
			}
		}
		
		productDao.flush();

	}
	
	/**
	 * Save product image
	 */
	public String saveImage(InputStream imageStream, String fileBaseLoc) throws Exception {
		File dir = new File(fileBaseLoc + "/" + productImagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filename = UUID.randomUUID().toString();
		
		Path path = FileSystems.getDefault().getPath(dir.getAbsolutePath(), filename);
		
		if((imageStream.available()/1025) > 250) {
			commonService.ImageCompression(imageStream, path);
		} else {
			Files.copy(imageStream, path);
		}
		
		
		return "/" + productImagePath + "/" + filename;
	}
	
	/**
	 * Delete product image
	 */
	public void deleteImage(String path, String fileBaseLoc) {
		File imageFile = new File(fileBaseLoc + path);
		if (imageFile.exists())
			imageFile.delete();
	}
	
	/**
	 * Delete
	 */
	@Transactional
	public boolean deleteProductGroup(List<Integer> ids) {
		
		List<ProductGroup> groups = productGroupDao.getProductGroupsByIds(ids);
		if (groups.size() != ids.size()){
			return false;
		}
		
		List<ProductAttribute> attributes = productAttributeDao.getProductAttributeByProductGroupIds(ids);
		
		for (ProductAttribute attribute: attributes){
			productAttributeDao.delete(attribute);
		}
		
		for(ProductGroup group: groups){
			productGroupDao.delete(group);
		}
		
		productAttributeDao.flush();
		productGroupDao.flush();
		
		return true;
	}
	
	/**
	 * Delete Product
	 */
	@Transactional
	public boolean deleteProduct(List<Integer> ids, String fileBaseLoc) {
		
		List<Product> products = productDao.getProductByIds(ids);
		if (products.size() != ids.size()){
			return false;
		}
		
		List<ProductSpecification> specifications = productSpecificationDao.getProductSpecificationByProductIds(ids);
		
		List<Quotation> quotations = quotationDao.getQuotationsByProductIds(ids);
		
		for (ProductSpecification specification: specifications){
			productSpecificationDao.delete(specification);
		}
		
		if (quotations != null && quotations.size() > 0){
			for (Quotation quotation : quotations){
				quotation.setProduct(null);
			}
		}
		
		for(Product product: products){
			
			if (product.getPhoto1Path() != null) {
				deleteImage(product.getPhoto1Path(), fileBaseLoc);
			}
			
			if (product.getPhoto2Path() != null) {
				deleteImage(product.getPhoto2Path(), fileBaseLoc);
			}
				
			productDao.delete(product);
		}
		
		productDao.flush();
		
		return true;
	}
	/**
	 *  Product Cleaning
	 */
	@Transactional
	public void productCleaning(Integer productId, Integer[] replaceProductIds) {
		
		Product product = productDao.findById(productId);
		
		for (Integer replaceProductId : replaceProductIds ) {
			Product replaceProduct = productDao.findById(replaceProductId);
			//replaceProduct.setStatus("Inactive");
			
			ProductCleaning productCleaning = new ProductCleaning();
			productCleaning.setOldProductId(replaceProduct.getProductId());
			productCleaning.setNewProductId(product.getProductId());
			productCleaningDao.save(productCleaning);
			
			if(replaceProduct.getStatus().equals("Active")) {
				product.setStatus(replaceProduct.getStatus());
				productDao.save(product);
			}
			
			Set<Quotation> quotations = replaceProduct.getQuotations();
			for(Quotation quotation : quotations) {
				quotation.setProduct(product);
				if (quotation.getNewProduct() != null && quotation.getNewProduct().equals(replaceProduct)){
					quotation.setNewProduct(product);					
				}
				quotationDao.save(quotation);
			}
			Set<QuotationRecord> quotationRecords = replaceProduct.getQuotationRecords();
			for (QuotationRecord quotationRecord : quotationRecords) {
				quotationRecord.setProduct(product);
				quotationRecord.setByPassModifiedDate(true);
				quotationRecordDao.save(quotationRecord);
			}
			
			Set<PointToNote> pointToNotes =replaceProduct.getPointToNotes();
			
			if (pointToNotes != null && pointToNotes.size() > 0){
				for (PointToNote note : pointToNotes){
					note.getProducts().remove(replaceProduct);
					note.getProducts().add(product);
					pointToNoteDao.save(note);
				}
			}
			
			Set<ProductSpecification>  pSpecs = replaceProduct.getProductSpecifications();			
			if (pSpecs != null && pSpecs.size() > 0){
				for (ProductSpecification spec : pSpecs){
					productSpecificationDao.delete(spec);
				}				
			}
			
			productDao.delete(replaceProduct);
			//productDao.save(replaceProduct);
		}

		productDao.flush();
	}
	
	/**
	 *  Bulk Update
	 */
	@Transactional
	public void productBulkUpdate(List<Integer> productIds, Integer productAttributeId, String value) {
		
		List<ProductSpecification> productSpecifications = productSpecificationDao.getProductSpecificationByProductIdsAndProdAttrId(productIds, productAttributeId);
		
		for (ProductSpecification productSpecification : productSpecifications) {
			productSpecification.setValue(value);
			productSpecificationDao.save(productSpecification);
		}
		productSpecificationDao.flush();
	}
	
	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public ProductGroupEditModel convertEntityToModel(ProductGroup entity){

		ProductGroupEditModel model = new ProductGroupEditModel();
		BeanUtils.copyProperties(entity, model);
		return model;
		
	}
	
	public String getProductCountryList() {
		SystemConfiguration sys = systemConfigurationDao.findByName(SystemConstant.COUNTRY_OF_ORIGIN);
		return sys.getValue();
	}

	public void savePhotos(Product entity, InputStream photo1ImageStream, InputStream photo2ImageStream,String fileBaseLoc) throws Exception {
		if (photo1ImageStream != null && photo1ImageStream.available() > 0) {
			if (entity.getPhoto1Path() != null) {
				deleteImage(entity.getPhoto1Path(), fileBaseLoc);
			}
			
			String imagePath = saveImage(photo1ImageStream, fileBaseLoc);
			entity.setPhoto1Path(imagePath);
			entity.setPhoto1ModifiedTime(new java.util.Date());
		}
		
		if (photo2ImageStream != null && photo2ImageStream.available() > 0) {
			if (entity.getPhoto2Path() != null) {
				deleteImage(entity.getPhoto2Path(), fileBaseLoc);
			}
			
			String imagePath = saveImage(photo2ImageStream, fileBaseLoc);
			entity.setPhoto2Path(imagePath);
			entity.setPhoto2ModifiedTime(new java.util.Date());
		}
	}
	

	/**
	 * Get Product select2 format
	 */
	public Select2ResponseModel queryProductSelect2(Select2RequestModel queryModel, Integer productGroupId) {
		
		queryModel.setRecordsPerPage(10);
		
		List<Integer> products = productDao.getSelect2List(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), Order.asc("productId"), productGroupId);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal =  productDao.countSelect2List(queryModel.getTerm(), productGroupId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for ( Integer product : products) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(product));
			item.setText(String.valueOf(product));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	/**
	 * Query single product group
	 */
	public String queryProductGroupSelectSingle(Integer id) {
		ProductGroup entity = productGroupDao.findById(id);
		if (entity == null)
			return null;
		else
			return entity.getCode() + " - " + entity.getChineseName();
	}
	
	/**
	 * Data Sync
	 */
	public List<ProductGroupSyncData> getUpdateProductGroup(Date lastSyncTime){
		return productGroupDao.getUpdateProductGroup(lastSyncTime);
	}
	
	public List<ProductAttributeSyncData> getUpdateProductAttribute(Date lastSyncTime){
		return productAttributeDao.getUpdateProductAttribute(lastSyncTime);
	}
	
	@Transactional
	public List<ProductSyncData> syncProductData(List<ProductSyncData> products, Date lastSyncTime, Boolean dataReturn){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if(products!=null && products.size()>0){
			for(ProductSyncData product : products){
				if ("D".equals(product.getLocalDbRecordStatus())){
					continue;
				}
				Product entity = null;
				if(product.getProductId() == null){
					entity = new Product();
				} else {
					entity = productDao.findById(product.getProductId());
					if (entity !=null && entity.getModifiedDate() != null && entity.getModifiedDate().after(product.getModifiedDate())){
						unUpdateIds.add(entity.getProductId());
						table.put(entity.getProductId(), product.getLocalId());
						continue;
					}
				}
				
				BeanUtils.copyProperties(product, entity);
				if(product.getProductGroupId()!=null){
					ProductGroup productGroup = productGroupDao.findById(product.getProductGroupId());
					if (productGroup != null){
						entity.setProductGroup(productGroup);
					}
				}
				entity.setByPassModifiedDate(true);
				productDao.save(entity);
				unUpdateIds.add(entity.getProductId());
				table.put(entity.getProductId(), product.getLocalId());
			}
			productDao.flush();
		}
		
		if(dataReturn != null && dataReturn){
			List<ProductSyncData> updatedData = new ArrayList<ProductSyncData>();
			updatedData.addAll(productDao.getUpdatedProduct(lastSyncTime, null));
			
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncProductRecursiveQuery(unUpdateIds));
			}
			
			List<ProductSyncData> unique = new ArrayList<ProductSyncData>(new HashSet<ProductSyncData>(updatedData));
			for(ProductSyncData data : unique){
				if(table.containsKey(data.getProductId())){
					data.setLocalId(table.get(data.getProductId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<ProductSyncData>();
	}
	
	@Transactional
	public List<ProductSpecificationSyncData> syncProductSpecificationData(List<ProductSpecificationSyncData> productSpecifications, Date lastSyncTime, Boolean dataReturn){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if(productSpecifications!=null&&productSpecifications.size()>0){
			for(ProductSpecificationSyncData productSpecification : productSpecifications){				
				ProductSpecification entity = null;
				if(productSpecification.getProductSpecificationId() == null){
					if ("D".equals(productSpecification.getLocalDbRecordStatus())){
						continue;
					}
					entity = new ProductSpecification();
				} else {
					entity = productSpecificationDao.findById(productSpecification.getProductSpecificationId());
					if (entity !=null && entity.getModifiedDate() != null && entity.getModifiedDate().after(productSpecification.getModifiedDate())){
						unUpdateIds.add(entity.getProductSpecificationId());
						table.put(entity.getProductSpecificationId(), productSpecification.getLocalId());
						continue;
					}
					if (entity!= null && "D".equals(productSpecification.getLocalDbRecordStatus())){
						productSpecificationDao.delete(entity);
						continue;
					}
					else if ("D".equals(productSpecification.getLocalDbRecordStatus())){
						continue;
					}
				}
				
				BeanUtils.copyProperties(productSpecification, entity);
				
				if(productSpecification.getProductId()!=null){
					Product product = productDao.findById(productSpecification.getProductId());
					if (product != null){
						entity.setProduct(product);						
					}
				}
				
				if(productSpecification.getProductAttributeId()!=null){
					ProductAttribute productAttribute = productAttributeDao.findById(productSpecification.getProductAttributeId());
					if (productAttribute != null){
						entity.setProductAttribute(productAttribute);						
					}
				}
				entity.setByPassModifiedDate(true);
				productSpecificationDao.save(entity);
				unUpdateIds.add(entity.getProductSpecificationId());
				table.put(entity.getProductSpecificationId(), productSpecification.getLocalId());
			}
			productSpecificationDao.flush();
		}
		
		if(dataReturn!=null && dataReturn){
			List<ProductSpecificationSyncData> updatedData = new ArrayList<ProductSpecificationSyncData>();
			
			updatedData.addAll(productSpecificationDao.getUpdatedProductSpecification(lastSyncTime, null));
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncProductSpecificationRecursiveQuery(unUpdateIds));
			}
			
			List<ProductSpecificationSyncData> unique = new ArrayList<ProductSpecificationSyncData>(new HashSet<ProductSpecificationSyncData>(updatedData));
			
			for(ProductSpecificationSyncData data : unique){
				if(table.containsKey(data.getProductSpecificationId())){
					data.setLocalId(table.get(data.getProductSpecificationId()));
				}
			}
			return unique;
		}
		return new ArrayList<ProductSpecificationSyncData>();
	}
	
	/**
	 * Get product specification by product and attribute
	 */
	public ProductSpecification getProductSpecificationByProductAndAttribute(Integer productId, Integer productAttributeId) {
		return productSpecificationDao.getProductSpecificationByProductAndAttribute(productId, productAttributeId);
	}
	
	/**
	 * Get product by attributes
	 */
	public Integer getProductByAttributes(int productGroupId, String countryOfOrigin, String barcode,
			List<ProductSpecificationEditModel> attributes, int numberOfAttributes, Integer productId) {
		return getProductByAttributes(productGroupId, countryOfOrigin, barcode,
				attributes, numberOfAttributes, productId);
	}
	
	/**
	 * Duplicate image
	 */
	public String duplicateImage(String oldProductImagePath, String fileBaseLoc) throws Exception {
		File dir = new File(fileBaseLoc + "/" + productImagePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filename = UUID.randomUUID().toString();
		
		Path path = FileSystems.getDefault().getPath(dir.getAbsolutePath(), filename);
		Path oldPath = FileSystems.getDefault().getPath(fileBaseLoc, oldProductImagePath);
		if (!oldPath.toFile().exists()) return null;
		Files.copy(oldPath, path);
		return "/" + productImagePath + "/" + filename;
	}
	
	/**
	 * upload product image by DataSync
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void uploadProductImage(int id, int image, InputStream attachment) throws Exception{
		Product product = productDao.findById(id);
		if(product == null) return;
		if(image==1){
			deleteImage(product.getPhoto1Path(), configService.getFileBaseLoc());
			String attachmentPath = saveImage(attachment, configService.getFileBaseLoc());
			product.setPhoto1Path(attachmentPath);
			product.setPhoto1ModifiedTime(new Date());
		} else {
			deleteImage(product.getPhoto2Path(), configService.getFileBaseLoc());
			String attachmentPath = saveImage(attachment, configService.getFileBaseLoc());
			product.setPhoto2Path(attachmentPath);
			product.setPhoto2ModifiedTime(new Date());
		}
		productDao.save(product);
		productDao.flush();
	}
	
	
	public List<UpdateProductImageModel> getUpdateProductImage(Date lastSyncTime, int imageType){
		return productDao.getUpdateProductImage(lastSyncTime, imageType);
	}
	
	public List<ProductSyncData> syncProductRecursiveQuery(List<Integer> productIds){
		List<ProductSyncData> entities = new ArrayList<ProductSyncData>();
		if(productIds.size()>2000){
			List<Integer> ids = productIds.subList(0, 2000);
			entities.addAll(syncProductRecursiveQuery(ids));
			
			List<Integer> remainIds = productIds.subList(2000, productIds.size());
			entities.addAll(syncProductRecursiveQuery(remainIds));
		} else if(productIds.size()>0){
			return productDao.getUpdatedProduct(null, productIds.toArray(new Integer[0]));
		}
		return entities;
	}
	
	public List<ProductSpecificationSyncData> syncProductSpecificationRecursiveQuery(List<Integer> productSpecificationIds){
		List<ProductSpecificationSyncData> entities = new ArrayList<ProductSpecificationSyncData>();
		if(productSpecificationIds.size()>2000){
			List<Integer> ids = productSpecificationIds.subList(0, 2000);
			entities.addAll(syncProductSpecificationRecursiveQuery(ids));
			
			List<Integer> remainIds = productSpecificationIds.subList(2000, productSpecificationIds.size());
			entities.addAll(syncProductSpecificationRecursiveQuery(remainIds));
		} else if(productSpecificationIds.size()>0){
			return productSpecificationDao.getUpdatedProductSpecification(null, productSpecificationIds.toArray(new Integer[0]));
		}
		return entities;
	}
	
	public List<ProductCleaningSyncData> getUpdatedProductCleaning(Date lastSyncTime){
		List<ProductCleaning> cleanings = productCleaningDao.getUpdatedProductCleaning(lastSyncTime);
		
		Hashtable<Integer, List<Integer>> mapCleaning = new Hashtable<Integer, List<Integer>>(); 
		
		for(ProductCleaning cleaning : cleanings){
			List<Integer> list = new ArrayList<Integer>();
			if (mapCleaning.containsKey(cleaning.getNewProductId())){
				list = mapCleaning.get(cleaning.getNewProductId());
			}
			list.add(cleaning.getOldProductId());
			
			if (mapCleaning.containsKey(cleaning.getOldProductId())){
				list.addAll(mapCleaning.get(cleaning.getOldProductId()));
				mapCleaning.remove(cleaning.getOldProductId());
			}
			
			mapCleaning.put(cleaning.getNewProductId(), list);
		}
		
		List<ProductCleaningSyncData> list = new ArrayList<ProductCleaningSyncData>();
		for(Integer key : mapCleaning.keySet()){
			ProductCleaningSyncData data = new ProductCleaningSyncData();
			data.setProductId(key);
			data.setReplaceProductId(mapCleaning.get(key));
			list.add(data);
		}
		
		return list;
	}
	
	public List<ProductZipImageSyncData> syncZipProductImage(String endDate){
		List<ProductZipImageSyncData> result = new ArrayList<ProductZipImageSyncData>();
		
		result = productDao.getProductInfoMonthly(endDate);
		
		if(result.size() > 0) {
			return result;
		}
		
		return new ArrayList<ProductZipImageSyncData>();
	}
}
