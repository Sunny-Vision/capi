package capi.model.assignmentManagement.assignmentManagement;

import java.util.List;

import capi.entity.AssignmentUnitCategoryInfo;

public class EditAssignmentUnitCategoryModel {
	public List<AssignmentUnitCategoryInfo> categories;

	public List<AssignmentUnitCategoryInfo> getCategories() {
		return categories;
	}

	public void setCategories(List<AssignmentUnitCategoryInfo> categories) {
		this.categories = categories;
	}
}
