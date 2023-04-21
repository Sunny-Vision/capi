package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import capi.entity.Outlet;
import capi.entity.Unit;

public class UnitOutletMapModel {

	private Outlet outlet;
	
	private Unit unit;

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((outlet == null) ? 0 : outlet.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		UnitOutletMapModel other = (UnitOutletMapModel) obj;
		if (outlet == null) {
			if (other.outlet != null)
				return false;
		} else if (!outlet.equals(other.outlet))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}
	
}
