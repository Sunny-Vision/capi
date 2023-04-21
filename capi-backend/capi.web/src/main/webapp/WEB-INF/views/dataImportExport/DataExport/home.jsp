<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>		
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>		
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>	
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<script>
		
		// Warning message before downloading the excel file [Outlet Maintenance] & [Outlet Name & Outlet Code]
		function exportOutletMaintenance(obj) {
			event.preventDefault();
			
			if(obj.dataset.warning == "true"){
				bootbox.confirm({
					title:"<span style='color: red;'>Important Notes</span>",
					message: "<spring:message code='W00044' />",
					callback: function(result){
						if(result){
							window.location = obj.href;
						} 
					}
				});
			}else{
				window.location = obj.href;
			}
	      }  
		
			$(document).ready(function(){
				$("#dataList").DataTable({
					"order": [[ 1, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [],
					"processing": true,
	                "serverSide": true,
	                "ajax": "<c:url value='/dataImportExport/DataExport/query'/>",
	                "columns": [
								
	                            {
	                            	"data": "importExportTaskId",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "startDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "taskName",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "finishedDate",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "status",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "errorMessage",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            }
	                            <sec:authorize access="hasPermission(#user, 256)">
	                            ,{
	                            	"data": "importExportTaskId",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"className" : "text-center",
	                            	"render" : function(data, type, full, meta){
	                            		var html = "";
	                            		if (full.status == "Finished"){
	                            			var shouldWarn = full.taskNo == 1 || full.taskNo == 34;
	                            			html = "<a href='<c:url value='/dataImportExport/DataExport/downloadFile?id='/>"+data+"' onclick='exportOutletMaintenance(this)' class='table-btn' data-warning='" + shouldWarn + "'><span class='glyphicon glyphicon-download-alt' aria-hidden='true'></span></a>";
	                            		}
	                            		return html;
                            		}
	                            }
	                            </sec:authorize>
	                        ]
				});
				
				$("#dataForm").validate({
					rules : {
						taskNo : {
							required:true
						},
						timeLogPeriodStart : {
							required: function(element){
								return !($('#timeLogPeriodStart').val() == "" && $('#timeLogPeriodEnd').val() == "");
							},
							checkEndDate: $('#timeLogPeriodStart'),
							checkDateFormat: true
						},
						timeLogPeriodEnd : {
							required: function(element){
								return !($('#timeLogPeriodStart').val() == "" && $('#timeLogPeriodEnd').val() == "");
							},
							checkEndDate: $('#timeLogPeriodStart'),
							checkDateFormat: true
						},
					}	
				});
				
				$(".select2").select2({width: "100%"}).change(function(){
					$(".productCategory").hide();
					$(".surveyMonth").hide();
					$(".subPriceType").hide();
					$(".cpiBasePeriod").hide();
					$(".timeLogMaintenance").hide();
					$(".purpose").hide();
					
					if ($(this).val() == 14){
						$(".productCategory").show()
					}
					if ($(this).val() == 29 || $(this).val() == 22 || $(this).val() == 24 || $(this).val() == 27 || $(this).val() == 25 || $(this).val() == 33){
						$(".surveyMonth").show()
					}
					if ($(this).val()==22){
						$(".subPriceType").show();
					}
					if ($(this).val() == 11 || $(this).val() == 12 || $(this).val() == 13){
						$(".cpiBasePeriod").show();
					}
					if ($(this).val() == 32){
						$(".timeLogMaintenance").show();
					}
					if ($(this).val() == 24 || $(this).val() == 25 || $(this).val() ==  22 || $(this).val() == 33){
						$(".purpose").show();
					}
				});
				$(".select2ajax").select2ajax({
					width: "100%",
					allowClear: true
				});
				$('.month-picker').datepicker({
					format: monthFormat,
					startView: "months", 
				    minViewMode: "months",
				    autoclose: true
				});
// 				$('.multidate-picker').datepicker({
// 					multidate: true
// 				});
				$('.date-picker').datepicker({
					autoclose: true
				});
				$('#timeLogUserIds').select2ajax({
					placeholder: "",
					closeOnSelect: false,
					width:"100%",
					allowClear: true
				});
// 				$('[name="timeLogDate"]').on('change', function() {
// 					var value = $(this).val();
// 					if(value != ""){
// 						$('[name="timeLogPeriodStart"]').attr('disabled', true);
// 						$('[name="timeLogPeriodEnd"]').attr('disabled', true);
// 					} else {
// 						$('[name="timeLogPeriodStart"]').attr('disabled', false);
// 						$('[name="timeLogPeriodEnd"]').attr('disabled', false);
// 					}
// 				});
// 				$('[name="timeLogPeriodStart"],[name="timeLogPeriodEnd"]').on('change', function() {
// 					var start = $('[name="timeLogPeriodStart"]').val();
// 					var end = $('[name="timeLogPeriodEnd"]').val();
// 					if(start != "" || end != ""){
// 						$('[name="timeLogDate"]').attr('disabled', true);
// 						$('[name="timeLogPeriodStart"]').attr('required', true);
// 						$('[name="timeLogPeriodEnd"]').attr('required', true);
// 					} else if (start == "" && end == "") {
// 						$('[name="timeLogDate"]').attr('disabled', false);
//  						$('[name="timeLogPeriodStart"]').removeAttr('required');
//  						$('[name="timeLogPeriodEnd"]').removeAttr('required');
// 					}
// 				});
				$('.searchTimeLogUser').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var singleUrl = $('#timeLogUserIds').data('singleUrl');
						$.ajax({
							url :singleUrl,
							data: {ids:selectedIds},
							type: 'post',
							dataType: 'json',
							traditional: true,
							success: function (data){
								$('#timeLogUserIds').empty();
								for(i=0; i<data.length; i++){
									var option = new Option(data[i].value, data[i].key);
									option.selected = true;
									$('#timeLogUserIds').append(option);
								}
								$('#timeLogUserIds').trigger('change');
							}
						})
					},
					alreadySelectedIdsCallback: function(){
						var ids = [];
						$('#timeLogUserIds').find('option:selected').each(function(){
							ids.push(this.value);
						})
						return ids;
					},
					multiple: true
				});
				$('#timeLogUserIds').hide();
				
				//Warning message before exporting the excel file [Outlet Maintenance] & [Outlet Name & Outlet Code]
				let dataForm = $('#dataForm');
				let dropDownMenu = $(".select2");
				dataForm.on('submit', function (event) {
		        	event.preventDefault();
		        	if (dataForm.valid() && (dropDownMenu.val() == 1 || dropDownMenu.val() == 34)) {
						bootbox.confirm({
							title:"<span style='color: red;'>Important Notes</span>",
							message: "<spring:message code='W00044' />",
							callback: function(result){
								if(result)
									document.getElementById("dataForm").submit();
								}
							});
		        		}
		        	
		        	if (dataForm.valid() && dropDownMenu.val() != 1 && dropDownMenu.val() != 34){
		        		document.getElementById("dataForm").submit();
		        	}
		        	
		        
				});
				
			})
			
		</script>
		
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Data Export</h1>
        </section>
        
        <section class="content">        	
        	<div class="row">
				<div class="col-md-12">
					<!-- content -->
					<sec:authorize access="hasPermission(#user, 256)">
						<div class="box box-primary">
							<div class="box-body">
								<div class="clearfix">&nbsp;</div>
									<form action="<c:url value="/dataImportExport/DataExport/exportFile" />" method="post" class="form-horizontal table-filter" id="dataForm">
										<div class="form-group">
											<label class="col-sm-2 control-label">Data Export Type</label>
											<div class="col-sm-4">
												<select class="form-control select2"  name="taskNo" >
													<c:forEach var="item" items="${definitions}">
														<c:choose>
														  <c:when test="${item.taskNo == '34'}">
														   <sec:authorize access="hasRole('CR11')">
														     <option value="<c:out value="${item.taskNo}" />" <c:if test="${item.taskNo eq dataType}">selected</c:if>>${item.taskName}
														   </sec:authorize>
														  </c:when>
														  <c:when test="${item.taskNo == '1'}">
														   <sec:authorize access="hasRole('CR12')">
														     <option value="<c:out value="${item.taskNo}" />" <c:if test="${item.taskNo eq dataType}">selected</c:if>>${item.taskName}
														   </sec:authorize>
														  </c:when>
														  <c:otherwise>
															<option value="<c:out value="${item.taskNo}" />" <c:if test="${item.taskNo eq dataType}">selected</c:if>>${item.taskName}
														  </c:otherwise>
														</c:choose>		
<%-- 														<option value="<c:out value="${item.taskNo}" />" <c:if test="${item.taskNo eq dataType}">selected</c:if>>${item.taskName}</option> --%>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-group productCategory" <c:if test="${dataType != 14}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">Product Type</label>
											<div class="col-sm-4">
												<select class="form-control select2ajax"  name="productGroupId" required
													data-ajax-url="<c:url value='/dataImportExport/DataExport/queryProductCategorySelect2'/>" ></select>
											</div>
										</div>
										<div class="form-group surveyMonth" <c:if test="${dataType != 29 && dataType != 22 && dataType != 24 && dataType != 27}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">Reference Month</label>
											<div class="col-sm-4">
												<div class="input-group">
													<input type="text" class="form-control month-picker" name="referenceMonth" required  />
													<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
												</div>											
											</div>
										</div>
										<div class="form-group subPriceType" <c:if test="${dataType != 22}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">Sub Price Type</label>
											<div class="col-sm-4">
												<select class="form-control select2ajax"  name="subPriceTypeId" 
													data-ajax-url="<c:url value='/dataImportExport/DataExport/querySubPriceTypeSelect2'/>" ></select>
											</div>
										</div>
										<div class="form-group purpose" <c:if test="${dataType != 24 && dataType != 25  && dataType != 22 && dataType != 33}">style="display:none"</c:if>>
		       								<label class="col-sm-2 control-label">Purpose</label>
											<div class="col-sm-3">
												<select name="purposeId" class="form-control select2ajax"
													data-ajax-url="<c:url value='/dataImportExport/DataExport/queryPurposeSelect2'/>"
													data-single-url="<c:url value='/dataImportExport/DataExport/queryPurposeSelectSingle'/>"
													data-selected-id="${model.purposeId}"
													></select>
											</div>
			       						</div>
										<div class="form-group cpiBasePeriod" <c:if test="${dataType != 11 && dataType != 12  && dataType != 13}">style="display:none"</c:if>>
											<label class="col-sm-2 control-label">CPI Base Period</label>
											<div class="col-sm-4">
												<select class="form-control select2ajax"  name="cpiBasePeriod" required
													data-ajax-url="<c:url value='/dataImportExport/DataExport/queryCPIBasePeriodSelect2'/>" ></select>
											</div>
										</div>
										<div class="timeLogMaintenance" <c:if test="${dataType != 32}">style="display:none"</c:if>>
											<div class="form-group fieldOfficer">
			       								<label class="col-sm-2 control-label">Field Officer</label>
			       								<div class="col-sm-4">
			       									<div class="input-group">
														<select class="form-control select2ajax" id="timeLogUserIds" name="timeLogUserIds" multiple 
																data-single-url="<c:url value="/dataImportExport/DataExport/getTimeLogUserIds" />" 
																data-ajax-url="<c:url value='/dataImportExport/DataExport/queryTimeLogUserSelect2'/>" >
														</select>
														<div class="input-group-addon searchTimeLogUser">
															<i class="fa fa-search"></i>
														</div>
													</div>
												</div>
											</div>
<!-- 											<div class="form-group date"> -->
<!-- 												<label class="col-sm-2 control-label">Date</label> -->
<!-- 												<div class="col-sm-4"> -->
<!-- 													<div class="input-group"> -->
<!-- 														<input type="text" class="form-control multidate-picker" data-orientation="top" data-multidate="true" name="timeLogDate" id="timeLogDate"/> -->
<!-- 														<div class="input-group-addon"> -->
<!-- 															<i class="fa fa-calendar"></i> -->
<!-- 														</div> -->
<!-- 													</div> -->
<!-- 												</div> -->
<!-- 											</div> -->
											<div class="form-group date">
												<label class="col-sm-2 control-label">Period</label>
												<div class="col-sm-2">
													<div class="input-group">
														<input type="text" class="form-control date-picker" data-orientation="top" name="timeLogPeriodStart" id="timeLogPeriodStart"/>
														<div class="input-group-addon">
															<i class="fa fa-calendar"></i>
														</div>
													</div>
												</div>
												<div class="col-sm-2">
													<div class="input-group">
														<input type="text" class="form-control date-picker" data-orientation="top" name="timeLogPeriodEnd" id="timeLogPeriodEnd"/>
														<div class="input-group-addon">
															<i class="fa fa-calendar"></i>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="form-group">
											<div class="col-sm-offset-2 col-sm-4">
												<input type="submit" class="btn btn-primary" value="Export" />
											</div>
										</div>
									</form>
							</div>
						</div>
					</sec:authorize>
					
					<div class="box">
						<!-- /.box-header -->
						<!-- form start -->
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
									<tr>
										<th>Task Id</th>
										<th>Start Date</th>
										<th>Task Name</th>
										<th>Finished Date</th>
										<th>Status</th>
										<th>Error Message</th>
										<sec:authorize access="hasPermission(#user, 256)">
											<th class="text-center action"></th>
										</sec:authorize>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
							
						</div>
					</div>
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>
