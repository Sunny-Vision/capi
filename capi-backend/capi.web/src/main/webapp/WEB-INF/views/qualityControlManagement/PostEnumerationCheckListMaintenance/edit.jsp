<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>		
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>	
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>	
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/peCheckAssignmentLookup.jsp" %>
		<script src="<c:url value='/resources/js/underscore-min.js'/>" ></script>
		<style>
		#dataList .discount span {
			display: inline-block;
			margin: 0 2px;
		}
		#dataList .discount span.number {
			border: solid 1px #d2d6de;
			padding: 0 20px;
		}
		.filter {
			margin-bottom: 10px;
		}
		.filter .form-group {
			margin-right: 10px;
		}
		.filter .form-control {
			margin-left: 10px;
		}
		.filter .form-control.select2 {
			width: 250px;
		}
		.glyphicon-pencil, .glyphicon-remove {
    		cursor: pointer;
		}
		</style>
		<script>

			function setOnClick() {
				$('#dataList input[type="checkbox"]').unbind( 'click' );
				$('#dataList input[type="checkbox"]').on( 'click', function () {
					var cell = $("#dataList").DataTable().cell(  $(this).closest('td') );
				    cell.data( cell.data()==true?false:true);
	
				    $("#dataList").DataTable().draw();
				    setOnClick();
				} );
				
				$('#dataList #caseRemove').unbind( 'click' );
				$('#dataList #caseRemove').on( 'click', function () {
					var tr = $(this).parents('tr');
					bootbox.confirm({
						title:"Confirmation",
						message: "<spring:message code='W00001' />",
						callback: function(result){
							if (result){
								$("#dataList").DataTable().row(tr).remove().draw();
								setOnClick();
							}
						}
					});
				});
			}
			
			$(document).ready(function(){
				
				var $dataTable = $("#dataList");
				
				$dataTable.DataTable({
					"order": [[ 0, "desc" ]],
					"buttons": [
								{
									"text": "Add",
									"className": "searchPECheckAssignment",
								},
					           ],
					"searching": true,
					"bInfo" : false,
					"paging": false,
					"ordering": false,
					"sDom": 'itrlp',
	                "columns": [
								{
									"data": "sectionHead",
									"orderable": false,
									"searchable": false,
									"className" : "text-center",
									"visible" : true,
									//"visible" : <c:out value="${model.isSectionHead == 1}"/>,
									"render" : function(data, type, full, meta){
										var checked = '';
										if (data == true) {
											checked = 'checked';
										} 
	                                	var html = "<input type='checkbox' " + checked + ">";
	                                	return html;
	                                },
								},
								{
									"data": "fieldTeamHead",
									"orderable": false,
									"searchable": false,
									"className" : "text-center",
									"visible" : true,
									//"visible" : <c:out value="${model.isFieldTeamHead == 1}"/>,
									"render" : function(data, type, full, meta, cell){
										var checked = '';
										if (data == true) {
											checked = 'checked';
										} 
	                                	var html = "<input type='checkbox' " + checked + ">";
	                                	return html;
	                                },
								},
								{
									"data": "firm",
									"orderable": false,
									"searchable": false,
									"className" : "text-center"
								},
	                            {
	                            	"data": "district",
	                            	"orderable": false,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "tpu",
	                            	"orderable": true,
	                            	"className" : "text-center"
	                            },
	                            {
	                            	"data": "address",
	                            	"orderable": false,
	                            	"className" : "text-center"
                            	},
	                            {
	                            	"data": "noOfQuotation",
	                            	"orderable": false,
	                            	"className" : "text-center"
                            	},       
                            	{
	                            	"data": "outletType",
	                            	"orderable": false,
	                            	"className" : "text-center"
                            	},
                            	{
	                            	"data": "firmStatus",
	                            	"orderable": false,
	                            	"className" : "text-center"
                            	},
                            	{ "data": "certaintyCase",
	                            	"orderable": true,
		                            "searchable": false,
		                            "render": function(data) {
		                            	if(data == true){
		                            		return '<i class="fa fa-check"></i>';
		                            	}
		                        		return '';
		                            },
		                            "className" : "text-center"
		                        },
	                            {
	                            	"data": "assignmentId",
	                            	"orderable": false,
	                                "render" : function(data, type, full, meta){
	                                	var html = "<a href='<c:url value='/assignmentManagement/PEViewAssignmentMaintenance/home?assignmentId='/>"+ full.assignmentId + "' target='_blank'><span class='glyphicon glyphicon-list' aria-hidden='true'></span></a> ";
	                                	if (full.certaintyCase == true || full.randomCase == true || full.status == 'Submitted') {
	                                		
	                                	} else {
	                                		html += "<a id='caseRemove'><span class='glyphicon glyphicon-remove' aria-hidden='true'></span></a> ";
	                                	}
	                                	/* if (full.peCheckFormId != null && full.peCheckFormId != '') {
	                                		html += "<a href='<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/edit?peCheckFormId='/>"+ full.peCheckFormId + "'><span class='glyphicon glyphicon-pencil' aria-hidden='true'></span></a> ";
	                                	} */
	                                	return html;
	                                },
	                                "width" : "60px",
	                            	"className" : "text-center"
                            	},
                            	/* 
                            	{
                            		"data": "randomCase",
                            		"visible": false
                            	},
                            	{
                            		"data": "peCheckFormId",
                            		"visible": false
                            	}, */
                            	{
                            		"data": "peCheckTaskId",
                            		"visible": false
                            	},
	                        ],
					"rowCallback": function( row, data ) {
						if ( data[ 'fieldTeamHead' ] == true ) {
							$('td', row).closest('tr').css('background-color', 'LightPink');
							$('td', row).closest('tr').find('[type=checkbox]:not(:checked)').prop('disabled', true);
						} else if ( data[ 'sectionHead' ] == true ) {
							$('td', row).closest('tr').css('background-color', 'LightSkyBlue');
							$('td', row).closest('tr').find('[type=checkbox]:not(:checked)').prop('disabled', true);
						} else {
							$('td', row).closest('tr').removeAttr('style');
							$('td', row).closest('tr').find('[type=checkbox]').prop('disabled', false);
						}
					} 
				});
				
				peCheckForms = ${commonService.jsonEncode(model.peCheckTaskList)};
				peCheckForms = peCheckForms || [];
				for (i = 0; i < peCheckForms.length; i++){
					var omj = peCheckForms[i];
					omj.index = i + 1;
				}
				
				<%--
				peCheckForms = [
				<c:forEach items="${model.peCheckTaskList}" var="cf" varStatus="i">
					{
						peCheckFormId : "<c:out value="${cf.peCheckFormId}" />",
						peCheckTaskId : "<c:out value="${cf.peCheckTaskId}" />",
						assignmentId : "<c:out value="${cf.assignmentId}" />",
						index : "<c:out value='${i.index+1}' />",
						firm : "<c:out value='${cf.firm}' />",
						batchCode : "<c:out value='${cf.batchCode}' />",
						collectionDate : "<c:out value='${cf.collectionDate}' />",
						district : "<c:out value='${cf.district}' />",
						tpu : "<c:out value='${cf.tpu}' />",
						address : "<c:out value='${cf.address}' />",
						noOfQuotation : "<c:out value='${cf.noOfQuotation}' />",
						status : "<c:out value='${cf.status}' />",
						fieldTeamHead : <c:out value='${cf.fieldTeamHead}' />,
						sectionHead : <c:out value='${cf.sectionHead}' />,
						certaintyCase : <c:out value='${cf.certaintyCase}' />,
						randomCase : <c:out value='${cf.randomCase}' />,
					},
				</c:forEach>
				];
				--%>
				
				$dataTable.DataTable().rows.add(peCheckForms).draw();			
				setOnClick();
				
				$('.searchPECheckAssignment').peCheckAssignmentLookup({
					selectedIdsCallback: function(selectedIds) {
						
						var existingIds = $($dataTable.DataTable().rows().data()).map(function(){
							  return this.assignmentId;
						}).get();
						
						var differenceIds =  _.difference(selectedIds, existingIds);
						var newIds = $.merge(differenceIds, existingIds);
						
						$('[name="peCheckTaskIdList"]').val(newIds);
						
						$.post("<c:url value='/qualityControlManagement/PostEnumerationCheckListMaintenance/getPECheckFormDetail'/>",
							{surveyMonthId: $('[name="surveyMonthId"]').val(), assignmentIds: newIds},
							function(data) {
								$dataTable.DataTable().rows().remove();
								$dataTable.DataTable().rows.add(data).draw();
								setOnClick();
							}
						);
					},
					queryDataCallback: function(model) {
						model.surveyMonthId = $('[name="surveyMonthId"]').val();
						model.userId = $('[name="userId"]').val();
						model.assignmentIds = $dataTable.DataTable().columns( 7 ).data().eq( 0 ).join( ',' );
					}
				});
				
				$('#confirmBtn').on('click', function() {
					$('#hiddenData').empty();
					var i = 0;
					$dataTable.DataTable().rows().every( function () {
					    var data = this.data();
					    for ( var key in data ) {
						    $('#hiddenData').append($("<input type='hidden' />")
					    		.attr("name","peCheckTaskList["+i+"]."+key).attr("value",data[key]));
					    }
					    i++;
					});
					$('#mainForm').submit();
				});
				
				<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
					viewOnly();
				</sec:authorize>
			});
		</script>
	</jsp:attribute>
	<jsp:body>
	   	<form id="mainForm" action="<c:url value='/qualityControlManagement/PostEnumerationCheckListMaintenance/save'/>" method="post" role="form"
        	enctype="multipart/form-data">
		<section class="content-header">
          <h1>Post-Enumeration Check List Maintenance</h1>
        </section>
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
					<!-- content -->
					<div class="box" >
						<div class="box-header with-border">
							<a class="btn btn-default" href="<c:url value='/qualityControlManagement/PostEnumerationCheckListMaintenance/home?referenceMonth='/><c:out value='${model.referenceMonth}'/>">Back</a>
						</div>
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div class="row">
								<div class="col-md-4">
									<div class="row row-eq-height">
										<div class="col-md-4">
											<label class="control-label">Reference Month:</label>
										</div>
										<div class="col-md-8">
       							   			<div class="input-group">
       							   				<c:out value="${model.referenceMonth}"/>
											</div>									
										</div>
									</div>
									<div class="row row-eq-height">
										<div class="col-md-4">
											<label class="control-label">Field Officer:</label>
										</div>
										<div class="col-md-8">
       							   			<div class="input-group">
       							   				<c:out value="${model.fieldOfficer}"/>
											</div>
										</div>
									</div>
									<input type="hidden" name="surveyMonthId" value="<c:out value="${model.surveyMonthId}"/>">
									<input type="hidden" name="userId" value="<c:out value="${model.userId}"/>">
									
								</div>
							</div>
							<hr/>
							<div class="clearfix">&nbsp;</div>
							<button type="button" class='btn btn-default searchPECheckAssignment'>Add</button>
							<table class="table table-striped table-bordered table-hover" id="dataList">
								<thead>
									<tr>
										<th>Section Head</th>
										<th>Field Team Head</th>
										<th>Firm</th>
										<th>District</th>
										<th>TPU</th>
										<th>Address</th>
										<th>No. of Quotation</th>
										<th>OutletType</th>
										<th>Firm Status</th>
										<th>Is Certainty Case</th>
										<th class="text-center action"></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<div id="hiddenData"></div>
						<sec:authorize access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">
							<div class="box-footer">
	       						<button id="confirmBtn" type="button" class="btn btn-info">Confirm</button>
	      					</div>
	      				</sec:authorize>
					</div>
				</div>
			</div>
        </section>
        </form>
	</jsp:body>
</t:layout>
