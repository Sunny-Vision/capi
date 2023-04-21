<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<style>
		#dataList .discount span {
			display: inline-block;
			margin: 0 2px;
		}
		#dataList .discount span.number {
			border: solid 1px #d2d6de;
			padding: 0 20px;
		}
		td.read {
			background-color: #f9f9f9;
		}
		td.unread {
			font-weight: bold;
		}
		#dataList tr{
			cursor:pointer
		}
		
		</style>
		<script>
			function deleteRecordsWithConfirm(data) {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00001' />",
					callback: function(result){
						if (result){
							/*$.post("<c:url value='/Notification/delete'/>",
								data,
								function(response) {
									$("#dataList").DataTable().ajax.reload();
									$("#MessageRibbon").html(response);
								}
							);*/
							window.location = "<c:url value='/Notification/delete' />?" + data;
						}
					}
				})
			}
		
		
			$(document).ready(function(){
				var $dataTable = $("#dataList");
				
				$("#unReadOnly").change(function(){
					$dataTable.DataTable().ajax.reload();
				})
				
				$("#flagOnly").change(function(){
					$dataTable.DataTable().ajax.reload();
				});
								
				$('#dataList tbody').on( 'click', 'tr', function (evt) {
					if ($(evt.target).find(':checkbox').length > 0){
						return
					}
			        var id = $(this).find(':checkbox').val();
			        window.location.href="<c:url value='/Notification/view?id='/>"+id;
			    });
				
				$('#dataList tbody').on( 'click', 'input', function (evt) {
					evt.stopPropagation();
			    });
				
				
				$dataTable.DataTable({
					"order": [[ 2, "desc" ]],
					"searching": true,
					"ordering": true,
					"buttons": [
							{
								"text": "Read",
								"action": function( nButton, oConfig, flash ) {
									var data = $dataTable.find(':checked').serialize();
									if (data == '') {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										return;
									}
									/*$.post("<c:url value='/Notification/read'/>",
										data,
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											$("#MessageRibbon").html(response);
										}
									);*/
									window.location = "<c:url value='/Notification/read' />?" + data;
								}
							},
							{
								"text": "Delete",
								"action": function( nButton, oConfig, flash ) {
									var data = $dataTable.find(':checked').serialize();
									if (data == '') {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}
									
									deleteRecordsWithConfirm(data);
								}
							},
							{
								"text": "Flag",
								"action": function( nButton, oConfig, flash ) {
									var data = $dataTable.find(':checked').serialize()+"&flag=true";
									if (data == '') {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}
									
									$.post("<c:url value='/Notification/flag'/>",
											data,
											function(response) {
												$("#dataList").DataTable().ajax.reload(null,false);
												$("#MessageRibbon").html(response);
											}
										);
								}
							},
							{
								"text": "UnFlag",
								"action": function( nButton, oConfig, flash ) {
									var data = $dataTable.find(':checked').serialize()+"&flag=false";
									
									if (data == '') {
										bootbox.alert({
											title: "Alert",
											message: "<spring:message code='E00009' />"
										});
										//alert('<spring:message code="E00009" />');
										return;
									}
									
									$.post("<c:url value='/Notification/flag'/>",
										data,
										function(response) {
											$("#dataList").DataTable().ajax.reload(null,false);
											$("#MessageRibbon").html(response);
										}
									);
								}
							}							
							
					],
					"processing": true,
	                "serverSide": true,
	                "ajax": {
	                	url:"<c:url value='/Notification/query'/>",
	                	data: function (d){
	                		d.isUnReadOnly = $("#unReadOnly").is(":checked");
	                		d.isFlagOnly = $("#flagOnly").is(":checked");
	                	}
	                },
	                "columns": [
								{
									"data": "id",
									"orderable": false,
									"searchable": false,
									"render" : function(data, type, full, meta){
										return "<input type='checkbox' value='"+data+"' name='id' class='tblChk' />";
									},
									"className" : "discount text-center"
								},
	                            
	                            {
	                            	"data": "subject",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center",
	                            	"render": function(data, type, full, meta){
	                            		var html = data;
	                            		if (full.flag){
	                            			html = "<span class='glyphicon glyphicon-star' style='color:rgb(255, 204, 0)'>&nbsp;</span>" + html
	                            		}
	                            		
	                            		return html;
	                            	}
                            		
	                            },
	                            {
	                            	"data": "date",
	                            	"orderable": true,
	                            	"searchable": true,
	                            	"className" : "text-center"
	                            }
	                            /*,
	                            {
	                            	"data": "id",
	                            	"orderable": false,
	                            	"searchable": false,
	                            	"render" : function(data, type, full, meta){
	                            		
	                            		
	                            		//var html = "<a href='<c:url value='/Notification/view?id='/>"+data+"' class='table-btn'><span class='glyphicon glyphicon-eye-open' aria-hidden='true'></span></a>";
	                            		//html += " &nbsp;<a href='javascript:void(0)' onclick='deleteRecordsWithConfirm(\"id="+data+"\")' class='table-btn btn-delete' data-id='"+data+"'><span class='fa fa-times' aria-hidden='true'></span></a>";
	                            		//return html;
	                            		
	                            		return '';
                            		},
	                            	"className" : "text-center"
                            	}*/
	                        ],
		            "rowCallback": function( row, data, index ) {
		                // Add diff css class for the read/unread notification
		                if ( data.read == true ) {
		                	$('td', row).addClass("read");
		                } else {
		                	$('td', row).addClass("unread");
		                }
		              }
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Notification<span class="label label-primary">${numberOfUnread}</span></h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<div class="row">
								<div class="col-sm-12">
									<label class="form-control-static">
										<input type="checkbox" id="unReadOnly" />
										<span class="form-control-static">View Unread Only</span>
									</label>
									&nbsp;
									<label class="form-control-static">
										<input type="checkbox" id="flagOnly" />
										<span class="form-control-static">View Flag Only</span>
									</label>
								</div>
							</div>
							
							<table class="table 1table-striped1 table-bordered table-hover" id="dataList">
								<thead>
								<tr>
									<th class="text-center action" ><input class="select_all" type="checkbox" /></th>
									<th>Subject</th>
									<th>Date</th>
									<!--<th class="text-center action"></th>-->
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