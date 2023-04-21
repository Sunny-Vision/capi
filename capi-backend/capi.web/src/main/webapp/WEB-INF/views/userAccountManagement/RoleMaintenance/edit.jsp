<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<jsp:useBean id="niceDate" class="java.util.Date"/>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>			
			table#authorityLevelList td {
				text-align: center;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/systemFunctionLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/authorityLookup.jsp" %>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<script>
			$.fn.systemFunctionSection = function(opts) {
				var $section = this;
				var _def = {
					dataTableSetting: {
						"order": [[ 0, "asc" ]],
						"searching": false,
						"ordering": true,
						"buttons": [],
						"processing": true,
		                "serverSide": false,
		                "paging": false,
		                "columnDefs": [
			                           {
			                               "targets": "action",
			                               "orderable": false,
			                               "searchable": false
			                           },
			                           {
			                        	   "targets": "_all",
			                        	   "className" : "text-center"
			                           }
			                           <sec:authorize access="!hasPermission(#user, 512)">
			                           ,
			                           {
			                        	   "targets": "action",
			                        	   "visible": false
			                           }
			                           </sec:authorize>
		                               ]
					},
					getDetailUrl: null,
					lookupPluginName: null,
					uniqueDataProperty: 'id'
				};
				
				opts.dataTableSetting = $.extend(_def.dataTableSetting, opts.dataTableSetting);
				
				var settings = $.extend(_def, opts);
				
				var $table = $section.find('table');
				$table.on('click', '.btn-delete', function(e) {
					e.preventDefault();
					var $dataTableRow = $(this).parents('table').DataTable().row($(this).parents('tr'));
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){																		
								$dataTableRow.remove().draw();
							}
						}
					});
				});
				
				var oldChildIds = $section.find('.oldChildIds').val().split(',');
				var mobile = settings.isMobile;
				
				$.get(settings.getDetailUrl,
					{isMobile: mobile, ids: oldChildIds},
					function(data) {
						settings.dataTableSetting.data = data;
						
						$table.DataTable(settings.dataTableSetting);
						
						$.fn[settings.lookupPluginName].apply(
							$section.find('.btn-add'),
							[{
								selectedIdsCallback: function(selectedIds) {
									/*var existingIds = $($table.DataTable().rows().data()).map(function() {
										return this[settings.uniqueDataProperty];
									}).get();
									
									var newIds = _.difference(selectedIds, existingIds);*/
									
									$.post(settings.getDetailUrl,
										{isMobile: mobile, ids: selectedIds},
										function(data) {
											$table.DataTable().rows().remove();
											$table.DataTable().rows.add(data).draw();
										}
									);
								},
								queryDataCallback: function(model) {
									model.isMobile = settings.isMobile;
								},
								alreadySelectedIdsCallback: function(){
									var ids = [];
									$("input[name='"+settings.inputName+"']").each(function(){
										ids.push(this.value)
									})
									return ids;
								}
							}]
						);
					}
				);
				
				return $section;
			};
			
			$(function() {
				var $mainForm = $('#mainForm');
				
				$mainForm.validate();
				
				var backendRows_selected = [];
				var frontendRows_selected = [];
				<c:if test="${model.backendSystemFunctionId != null}">
					backendRows_selected = ${model.backendSystemFunctionId};
				</c:if>
				<c:if test="${model.frontendSystemFunctionId != null}">
					frontendRows_selected = ${model.frontendSystemFunctionId};
				</c:if>
				
				$('#backendSystemFunctionSection').systemFunctionSection({
					dataTableSetting: {
						columns: [
							{"data": "code"},
							{"data": "description"},
							{
								"data": "id",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									html += "<input name='backendSystemFunctionId' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/userAccountManagement/RoleMaintenance/getBackendSystemFunctionDetail'/>",
					lookupPluginName: 'systemFunctionLookup',
					uniqueDataProperty: 'id',
					isMobile: false,
					divId: 'backendSystemFunctionSection',
					inputName: 'backendSystemFunctionId'
				});
				
				$('#frontendSystemFunctionSection').systemFunctionSection({
					dataTableSetting: {
						columns: [
							{"data": "code"},
							{"data": "description"},
							{
								"data": "id",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									html += "<input name='frontendSystemFunctionId' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/userAccountManagement/RoleMaintenance/getFrontendSystemFunctionDetail'/>",
					lookupPluginName: 'systemFunctionLookup',
					uniqueDataProperty: 'id',
					isMobile: true,
					divId: 'frontendSystemFunctionSection',
					inputName: 'frontendSystemFunctionId'
				});
				
				$('#authorityLevelSection').systemFunctionSection({
					dataTableSetting: {
						columns: [
							{"data": "authorityLevel"},
							{
								"data": "id",
								"render" : function(data){
									var html = "<a href='#' class='table-btn btn-delete'><span class='fa fa-times' aria-hidden='true'></span></a>";
									html += "<input name='authorityLevelId' type='hidden' value='" + data + "'/>";
									return html;
								}
							}
						]
					},
					getDetailUrl: "<c:url value='/userAccountManagement/RoleMaintenance/getAuthorityLevelDetail'/>",
					lookupPluginName: 'authorityLookup',
					uniqueDataProperty: 'id',
					divId: 'authorityLevelSection',
					inputName: 'authorityLevelId'
				});
				
				$("#mainForm").on('submit',function(){
					$("#backendSystemFunctionId").val(backendRows_selected.join(','));
					$('#frontendSystemFunctionId').val(frontendRows_selected.join(','));
				});
				
				<sec:authorize access="!hasPermission(#user, 512)">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Role Maintenance</h1>
          <c:if test="${act != 'add'}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${commonService.formatDateTime(model.createdDate)}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${commonService.formatDateTime(model.modifiedDate)}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        
        <section class="content">
        	<form id="mainForm" action="<c:url value='/userAccountManagement/RoleMaintenance/save'/>" method="post" role="form">
        		<input name="roleId" value="<c:out value="${model.roleId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/userAccountManagement/RoleMaintenance/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Code</label>
		       								<div class="col-sm-4">
												<c:choose>
			       									<c:when test="${act eq 'add'}">
														<input name="roleName" type="text" class="form-control" value="<c:out value="${model.roleName}" />" required />
													</c:when>
													<c:otherwise>
														<p class="form-control-static" >${model.roleName}</p>
														<input name="roleName" value="<c:out value="${model.roleName}" />" type="hidden" />
													</c:otherwise>
												</c:choose>
											</div>
										</div>
		       							<div class="form-group">
		       								<label class="col-sm-2 control-label">Description</label>
		       								<div class="col-sm-4">
												<input name="roleDescription" type="text" class="form-control" value="<c:out value="${model.roleDescription}" />" maxlength="2000" />
											</div>
										</div>
									</div>
									
									<div class="row" id="backendSystemFunctionSection">
										<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.backendSystemFunctionId}">${id},</c:forEach>"/>
										<div class="col-md-12">
											<div class="table-header">Back-end Function</div>
											<table class="table table-striped table-bordered table-hover">
												<thead>
												<tr>
													<th>Function Code</th>
													<th>Description</th>
													<th class="action"></th>
												</tr>
												</thead>
											</table>
											<sec:authorize access="hasPermission(#user, 512)">
												<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Back-end Function</button>
											</sec:authorize>
										</div>
									</div>
									<hr/>
									
									<div class="row" id="frontendSystemFunctionSection">
										<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.frontendSystemFunctionId}">${id},</c:forEach>"/>
										<div class="col-md-12">
											<div class="table-header">Front-end Function</div>
											<table class="table table-striped table-bordered table-hover">
												<thead>
												<tr>
													<th>Function Code</th>
													<th>Description</th>
													<th class="action"></th>
												</tr>
												</thead>
											</table>
											<sec:authorize access="hasPermission(#user, 512)">
												<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Front-end Function</button>
											</sec:authorize>
										</div>
									</div>
									<hr/>
									
									 <div class="row" id="authorityLevelSection">
										<input class="oldChildIds" type="hidden" value="<c:forEach var="id" items="${model.authorityLevelId}">${id},</c:forEach>"/>
										<div class="col-md-12">
											<div class="table-header">Authority Level</div>
											<table class="table table-striped table-bordered table-hover">
												<thead>
												<tr>
													<th>Authority Level</th>
													<th class="action"></th>
												</tr>
												</thead>
											</table>
											<sec:authorize access="hasPermission(#user, 512)">
												<button type="button" class="btn btn-default btn-add"><i class="fa fa-plus"></i> Add Authority Level</button>
											</sec:authorize>
										</div>
									</div>									
								</div>
							</div>
							<sec:authorize access="hasPermission(#user, 512)">
								<div class="box-footer">	      					
	        						<button type="submit" class="btn btn-info">Submit</button>	       						
	       						</div>
	       					</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        </section>
	</jsp:body>
</t:layout>
