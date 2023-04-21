<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Search Authority</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<table class="table table-striped table-bordered table-hover datatable">
					<thead>
					<tr>
						<th class="text-center action select-multiple" ><input class="select_all" type="checkbox" /></th>
						<th>Authority Level</th>
					</tr>
					</thead>
					<tbody>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="1" <c:if test="${checkboxChecking.hasAuthority(1)}">checked</c:if> /></td>
							<td class="text-center">Section Head</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="2" <c:if test="${checkboxChecking.hasAuthority(2)}">checked</c:if> /></td>
							<td class="text-center">Field Team Head</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="4" <c:if test="${checkboxChecking.hasAuthority(4)}">checked</c:if> /></td>
							<td class="text-center">Field Supervisor</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="8" <c:if test="${checkboxChecking.hasAuthority(8)}">checked</c:if> /></td>
							<td class="text-center">Field Allocation Coordinator</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="16" <c:if test="${checkboxChecking.hasAuthority(16)}">checked</c:if> /></td>
							<td class="text-center">Field Officers</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="32" <c:if test="${checkboxChecking.hasAuthority(32)}">checked</c:if> /></td>
							<td class="text-center">Indoor Allocation Coordinator</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="64" <c:if test="${checkboxChecking.hasAuthority(64)}">checked</c:if> /></td>
							<td class="text-center">Indoor Review</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="128" <c:if test="${checkboxChecking.hasAuthority(128)}">checked</c:if> /></td>
							<td class="text-center">Indoor Data Conversion</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="256" <c:if test="${checkboxChecking.hasAuthority(256)}">checked</c:if> /></td>
							<td class="text-center">Business Data Administrator</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="512" <c:if test="${checkboxChecking.hasAuthority(512)}">checked</c:if> /></td>
							<td class="text-center">System Administrator</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="1024" <c:if test="${checkboxChecking.hasAuthority(1024)}">checked</c:if> /></td>
							<td class="text-center">Indoor Supervisor</td>
						</tr>
						<tr>
							<td class="text-center"><input name="id" type="checkbox" value="2048" <c:if test="${checkboxChecking.hasAuthority(2048)}">checked</c:if> /></td>
							<td class="text-center">Business Data Viewer</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>