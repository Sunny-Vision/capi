package capi.model.api.dataSync;

import java.util.Date;

public class ProductZipImageSyncData {

	private Integer productId;
	
	private Date photoModifiedTime;
	
	private String photoPath;

	private String type;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Date getPhotoModifiedTime() {
		return photoModifiedTime;
	}

	public void setPhotoModifiedTime(Date photoModifiedTime) {
		this.photoModifiedTime = photoModifiedTime;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
