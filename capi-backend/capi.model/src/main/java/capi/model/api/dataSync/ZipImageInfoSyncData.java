package capi.model.api.dataSync;

import java.util.Date;

public class ZipImageInfoSyncData{

	private Date startDate;

	private Date endDate;

	private String typeOfImage; 
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTypeOfImage() {
		return typeOfImage;
	}

	public void setTypeOfImage(String typeOfImage) {
		this.typeOfImage = typeOfImage;
	}
	
}
