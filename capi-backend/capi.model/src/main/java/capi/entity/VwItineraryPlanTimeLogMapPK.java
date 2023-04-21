package capi.entity;

import java.io.Serializable;

public class VwItineraryPlanTimeLogMapPK implements Serializable{

	protected Integer timeLogId;
    protected Integer itineraryPlanId;

    public VwItineraryPlanTimeLogMapPK() {}

    public VwItineraryPlanTimeLogMapPK(Integer timeLogId, Integer itineraryPlanId) {
        this.timeLogId = timeLogId;
        this.itineraryPlanId = itineraryPlanId;
    }

	public Integer getTimeLogId() {
		return timeLogId;
	}

	public void setTimeLogId(Integer timeLogId) {
		this.timeLogId = timeLogId;
	}

	public Integer getItineraryPlanId() {
		return itineraryPlanId;
	}

	public void setItineraryPlanId(Integer itineraryPlanId) {
		this.itineraryPlanId = itineraryPlanId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itineraryPlanId == null) ? 0 : itineraryPlanId.hashCode());
		result = prime * result
				+ ((timeLogId == null) ? 0 : timeLogId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VwItineraryPlanTimeLogMapPK other = (VwItineraryPlanTimeLogMapPK) obj;
		if (itineraryPlanId == null) {
			if (other.itineraryPlanId != null)
				return false;
		} else if (!itineraryPlanId.equals(other.itineraryPlanId))
			return false;
		if (timeLogId == null) {
			if (other.timeLogId != null)
				return false;
		} else if (!timeLogId.equals(other.timeLogId))
			return false;
		return true;
	}
}
