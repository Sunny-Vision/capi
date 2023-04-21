<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Batch Look up</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal">
					<div class="row">
							<label class="col-xs-2  control-label">Survey Form</label>
							<div class="col-xs-3 ">
							<select class="form-control"  name="surveyForm" multiple
								data-ajax-url="<c:url value='/commonDialog/BatchLookup/querySurveyFormSelect'/>"></select>
							</div>
					</div>
				</form>
				<hr/>
			
				<!-- content -->
				<table class="table table-striped table-bordered table-hover datatable" >
					<thead>
					<tr>
						<th><input type="checkbox" class="select_all select-multiple" /></th>
						<th>Batch Code</th>
						<th>Description</th>
						<th>Assignment Allocation Type</th>
						<th>Survey Form</th>
						<th>Batch Category</th>
						<th class="text-center action select-single"></th>
					</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>