<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="modal fade" id="timeLogDatePickerDialog" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
			<div class="modal-header">
				<h4 class="modal-title" id="majorLocationDialogLabel" data-bind="text:formTitle">Update Time Log</h4>
			</div>
			<div class="modal-body form-horizontal">
				<input name="selectedAssignmentId" type="hidden"/>
				<input name="tabName" type="hidden"/>
				<div class="form-group">
					<div class="col-md-3 control-label">Date</div>
					<div class="col-md-4">
						<div class="input-group">
							<input type="text" class="form-control date-picker"
								data-orientation="top" name="selectedDate" />
							<div class="input-group-addon">
								<i class="fa fa-calendar"></i>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary modal-submit">Submit</button>
			</div>
		</div>
	</div>
</div>