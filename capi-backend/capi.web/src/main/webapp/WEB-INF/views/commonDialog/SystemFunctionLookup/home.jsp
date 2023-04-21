<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Search System Function</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Function Code</th>
						<th>Description</th>
						<th class="action select-single"></th>
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