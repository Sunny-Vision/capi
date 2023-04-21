package capi.model.assignmentManagement;

import capi.entity.PECheckForm;
import capi.entity.PECheckTask;

public class PECheckTaskAndForm {
	private PECheckTask task;
	private PECheckForm form;
	
	public PECheckTask getTask() {
		return task;
	}
	public void setTask(PECheckTask task) {
		this.task = task;
	}
	public PECheckForm getForm() {
		return form;
	}
	public void setForm(PECheckForm form) {
		this.form = form;
	}
}
