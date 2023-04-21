<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>

	<jsp:attribute name="header">
		<style>			
			.btn.pull-right {
				margin-left: 10px;
			}
			.ui-sortable tr {
			    cursor:pointer; 
			}
			#telEnuTable {
				width: 80%;
			}
			.glyphicon-pencil, .glyphicon-remove, .glyphicon-plus {
    			cursor: pointer;
			}
			.radio
			{
				margin-left: 5px;
				margin-right: 5px;
			}
			.sorting, .sorting_asc, .sorting_desc {
			    background : none!;
			}
			.row .reject-container{
				color: #ff0000;
				margin-left: 0;
				margin-right: 0;
			}
			.reject-btn{
				padding-left: 0;
			}
			.reject-label{
				text-align: right;
			}
		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/timepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/moment.jsp" %>
		<script>
		
		var surveyType = [
			<c:forEach var="survey" items="${surveyCountList}">
				"<c:out value='${survey}'/>",
			</c:forEach>		                  
		];
		
		var telephoneTimeLogs = [];
		var fieldworkTimeLogs = [];
		

		function calculateTelephoneCounts() {
			var count = new Array(surveyType.length);
			for (var i=0 ; i < count.length ; i++) {
				count[i] = new Array(2);
				count[i][0] = 0;
				count[i][1] = 0;
			}
			
			for (var i=0 ; i < telephoneTimeLogs.length ; i++) {
				for (var j=0 ; j < surveyType.length ; j++) {
					if (telephoneTimeLogs[i].survey == surveyType[j]) {
						count[j][0] += Number(telephoneTimeLogs[i].completionQuotationCount);
						count[j][1] += Number(telephoneTimeLogs[i].completionTotalQuotation);
						break;
					} 
				}
			}
			
			for (var j=0 ; j < surveyType.length ; j++) {
				$('#'+surveyType[j]+'_C_TI_count').text(count[j][0]+' / '+count[j][1]);
			}	
		}
		

		function calculateFieldworkCounts() {
			
			var count = new Array(surveyType.length);
			for (var i=0 ; i < count.length ; i++) {
				count[i] = new Array(2);
				count[i][0] = 0;
				count[i][1] = 0;
			}
			
			for (var i=0 ; i < fieldworkTimeLogs.length ; i++) {
				if (fieldworkTimeLogs[i].survey == surveyType[0] && fieldworkTimeLogs[i].enumerationOutcome == 'C' ) {
					if (fieldworkTimeLogs[i].marketQuotationCount != '')
						count[0][0] += eval(fieldworkTimeLogs[i].marketQuotationCount);
					if (fieldworkTimeLogs[i].nonMarketQuotationCount != '')
						count[0][0] += eval(fieldworkTimeLogs[i].nonMarketQuotationCount);
					if (fieldworkTimeLogs[i].marketTotalQuotation != '')
						count[0][1] += eval(fieldworkTimeLogs[i].marketTotalQuotation);
					if (fieldworkTimeLogs[i].nonMarketTotalQuotation != '')
						count[0][1] += eval(fieldworkTimeLogs[i].nonMarketTotalQuotation);
				} else if (fieldworkTimeLogs[i].survey == surveyType[1] && fieldworkTimeLogs[i].activity == 'FI' ) {
					var compareEnumerationOutcome = ['C', 'D', 'U', 'ND', 'O'];
										
					if (compareEnumerationOutcome.includes(fieldworkTimeLogs[i].enumerationOutcome)){
						count[1][0] ++;
					}
					count[1][1]++;
				} else if (fieldworkTimeLogs[i].survey == surveyType[2] && fieldworkTimeLogs[i].activity == 'FI') {
					count[2][0] ++;
				}
			}
			
			for (var j=0 ; j < surveyType.length ; j++) {
				if (j < 2) {
					$('#'+surveyType[j]+'_C_FI_count').text(count[j][0]+' / '+count[j][1]);
				} else {
					$('#'+surveyType[j]+'_C_FI_count').text(count[j][0]);
				}
			}
		}
		

		$(document).ready(function() {
							
			telephoneTable = $('#telEnuTable').DataTable( {
				"rowId" : 'index',
				"bPaginate": false,
			    "bInfo": false,
				"buttons" : [],
			    "order": [[ 2, "asc" ]],
			    "ordering" : true,
	            "columns": [
	                        { "data": "referenceMonth" },
	                        { "data": "survey" },
	                        { "data": "caseReferenceNo" },
	                        { 
	                        	"data": "completionQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.completionQuotationCount + " / " + full.completionTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { 
	                        	"data": "deletionQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.deletionQuotationCount + " / " + full.deletionTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { 
	                        	"data": "session",
	                        	"render" : function(data, type, full, meta){
                            		if (data == 'A')
                            			return 'AM';
                            		else if (data == 'P')
                            			return 'PM';
                            		else
                            			return '';
                           		}
	                        	
	                        }

						],
                "columnDefs": [
			                    {
			                        "targets": [2],
			                        "className" : "text-center",
			                        "orderable": true,
			                        "orderSequence": [ "asc" ],
			                        
			                    },	
                            	{
                            		"targets": "_all",
                               		"className" : "text-center",
                                   	"orderable": false,
                               	},
                           ]

			} );
			
			var buttons = [];	
			$.fn.dataTable.addResponsiveButtons(buttons);
				
			fieldworkTable = $('#fieldworkTable').DataTable( {
				"rowId" : 'index',
			    "bPaginate": false,
			    "bInfo": false,
			    "order": [[ 1, "asc" ]],
			    "ordering" : true,
				"buttons" : buttons,
				"autoWidth": false,
	            "columns": [
	                        { "data": "referenceMonth" },
	                        { "data": "startTime" },
	                        { "data": "survey" },
	                        { "data": "caseReferenceNo" },
	                        { "data": "activity" },
	                        { "data": "enumerationOutcome" },
	                        { "data": "marketQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.marketQuotationCount + " / " + full.marketTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { "data": "nonMarketQuotationCount",
                            	"render" : function(data, type, full, meta){
                            		var html = full.nonMarketQuotationCount + " / " + full.nonMarketTotalQuotation;
                            		return html;
                           		}
	                        },
	                        { "data": "building" },
	                        { "data": "destination",
	                        	"render" : function(data, type, full, meta){
                            		if (full.activity == "TR"){
                            			return "From: "+full.fromLocation + "<br />To: " + full.toLocation
                            		}
                            		else{
                            			return data;
                            		}
                           		}
	                       	},
	                        { "data": "transport" },
	                        { "data": "expenses" },
	                        { "data": "includeInTransportForm",
	                        	"render": function (data, type, full, meta){
	                        		return data? "Y" : "N";
	                        	}
	                        },
	                        { "data": "transit",
	                        	"render": function (data, type, full, meta){
	                        		return data? "Y" : "N";
	                        	}
	                        },
	                        { "data": "remark" }

						],
                "columnDefs": [
                               {
                                   "targets": [1],
                                   "className" : "text-center",
                                   "orderable": true,
                                   "orderSequence": [ "asc" ],
                                   
                               },	 	
                               {
                            		"targets": "_all",
                               		"className" : "text-center",
                               		"orderable": false,
                               },
                           ],
      				"drawCallback" : function (){
   						$('#fieldworkTable').DataTable().columns.adjust()
   						$('#fieldworkTable').DataTable().responsive.recalc();
   					}
				} );
			$("#collapseBtn").click(function(){
				$("#filters").toggle();
				var icon = $(this).find('i');
				if (icon.hasClass('fa-minus')){
					icon.removeClass('fa-minus').addClass('fa-plus')
				}else{
					icon.removeClass('fa-plus').addClass('fa-minus')
				}
			})
			
			$("#btnResubmissionRequest").on('click', function() {
				$('#resubmissionRequestForm').submit();
			});
			
			telephoneTimeLogs = ${commonService.jsonEncode(model.telephoneTimeLogs)};
			telephoneTimeLogs = telephoneTimeLogs || [];
			for (i = 0; i < telephoneTimeLogs.length; i++){
				var p = telephoneTimeLogs[i];
				p.index = i + 1;
				p.completionQuotationCount = p.completionQuotationCount == null? "" : p.completionQuotationCount;
				p.completionTotalQuotation = p.completionTotalQuotation == null? "" : p.completionTotalQuotation;
				p.deletionQuotationCount = p.deletionQuotationCount == null? "" : p.deletionQuotationCount;
				p.deletionTotalQuotation = p.deletionTotalQuotation == null? "" : p.deletionTotalQuotation;
			}
			
			<%--
			telephoneTimeLogs = [
			<c:forEach items="${model.telephoneTimeLogs}" var="ti" varStatus="i">
				{
					telephoneTimeLogId : <c:out value="${ti.telephoneTimeLogId}" />,
					index : "<c:out value='${i.index+1}' />",
					referenceMonth : "<c:out value='${ti.referenceMonth}' />",
					survey : "<c:out value='${ti.survey}' />",
					caseReferenceNo : "<c:out value='${ti.caseReferenceNo}' />",
					status : "<c:out value='${ti.status}' />",
					completionQuotationCount : "<c:out value='${ti.completionQuotationCount}' />",
					completionTotalQuotation : "<c:out value='${ti.completionTotalQuotation}' />",
					deletionQuotationCount : "<c:out value='${ti.deletionQuotationCount}' />",
					deletionTotalQuotation : "<c:out value='${ti.deletionTotalQuotation}' />",
					session : "<c:out value='${ti.session}' />",

				},
			</c:forEach>
			];
			--%>
			
			telephoneTable.rows.add(telephoneTimeLogs).draw();
			calculateTelephoneCounts();
			
			fieldworkTimeLogs = ${commonService.jsonEncode(model.fieldworkTimeLogs)};
			fieldworkTimeLogs = fieldworkTimeLogs || [];
			for (i = 0; i < fieldworkTimeLogs.length; i++){
				var p = fieldworkTimeLogs[i];
				p.index = i + 1;
				p.marketQuotationCount = p.marketQuotationCount == null? "" : p.marketQuotationCount;
				p.marketTotalQuotation = p.marketTotalQuotation == null? "" : p.marketTotalQuotation;
				p.nonMarketQuotationCount = p.nonMarketQuotationCount == null? "" : p.nonMarketQuotationCount;
				p.nonMarketTotalQuotation = p.nonMarketTotalQuotation == null? "" : p.nonMarketTotalQuotation;
				p.building = p.building == null? "" : p.building;
			}
			
			<%--
			fieldworkTimeLogs = [
    				<c:forEach items="${model.fieldworkTimeLogs}" var="fi" varStatus="i">
    					{
    						fieldworkTimeLogId : <c:out value="${fi.fieldworkTimeLogId}" />,
    						index : "<c:out value='${i.index+1}' />",
    						referenceMonth : "<c:out value='${fi.referenceMonth}' />",
    						startTime : "<c:out value='${fi.startTime}' />",
    						survey : "<c:out value='${fi.survey}' />",
    						caseReferenceNo : "<c:out value='${fi.caseReferenceNo}' />",
    						activity : "<c:out value='${fi.activity}' />",
    						enumerationOutcome : "<c:out value='${fi.enumerationOutcome}' />",
    						marketQuotationCount : "<c:out value='${fi.marketQuotationCount}' />",
    						marketTotalQuotation : "<c:out value='${fi.marketTotalQuotation}' />",
    						nonMarketQuotationCount : "<c:out value='${fi.nonMarketQuotationCount}' />",
    						nonMarketTotalQuotation : "<c:out value='${fi.nonMarketTotalQuotation}' />",
    						building : "<c:out value='${fi.building}' />",
    						destination : "<c:out value='${fi.destination}' />",
    						transport : "<c:out value='${fi.transport}' />",
    						remark : "<c:out value='${fi.remark}' />",
    						expenses : "<c:out value='${fi.expenses}' />",
    						recordType : "<c:out value='${fi.recordType}' />",
    					},
    				</c:forEach>
    			];
    		--%>

			fieldworkTable.rows.add(fieldworkTimeLogs).draw();
			calculateFieldworkCounts();
		});
		</script>	
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Time Log Maintenance</h1>
        	<c:if test="${not empty model.timeLogId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7">${model.createdDateDisplay}</div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7">${model.modifiedDateDisplay}</div>
			         </div>
		      	</div>
	      	</c:if>
        </section>

        <section class="content">
        	<form id="mainForm"  method="post" role="form"
        		enctype="multipart/form-data">
        		<input name="timeLogId" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input id="assignmentDeviation" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input id="sequenceDeviation" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		<input id="tpuDeviation" value="<c:out value="${model.timeLogId}" />" type="hidden" />
        		
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<div class="row reject-container">
									<div class="col-md-1 reject-btn">
										<a class="btn btn-default" href="<c:url value='/timeLogManagement/TimeLogMaintenance/home'/>">Back</a>
									</div>
									<c:if test="${model.status == 'Rejected'}">
										<label class="col-md-1 reject-label">Reject Reason:</label>
										<div class="col-md-9">
											<c:out value="${model.rejectReason}" escapeXml="false"/>
										</div>
									</c:if>
								</div>
								<div class="row reject-container">
									<c:if test="${model.status == 'Rejected'}">
										<label class="col-md-1 col-md-offset-1 reject-label">Rejected By:</label>
										<div class="col-md-9">
											<c:out value="${model.approvedBy}" escapeXml="false"/>
										</div>
									</c:if>
								</div>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
	       							<div class="col-sm-12">
	       								<c:forEach var="survey" items="${surveyCountList}">						
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label"><c:out value="${survey}" /> - C(FI):</label>
	       							    	<div class="col-md-1">
	       							    		<p id="<c:out value="${survey}" />_C_FI_count" class="form-control-static">0</p>
	       							    	</div>
	       							    	<label class="col-md-1 control-label"><c:out value="${survey}" /> - C(TI):</label>
	       							    	<div class="col-md-1">
	       							    		<p id="<c:out value="${survey}" />_C_TI_count" class="form-control-static">0</p>
	       							    	</div>
	       							    </div>
       							        </c:forEach>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">Training:</label>
	       							    	<div class="checkbox col-md-1">
											  <label><input id="isTrainingAM" name="isTrainingAM" type="checkbox" value="true" readonly disabled <c:if test="${model.isTrainingAM}">checked</c:if>>AM</label>
											</div>
											&nbsp;
											<div class="checkbox col-md-1">
											  <label><input id="isTrainingPM" name="isTrainingPM" type="checkbox" value="true" readonly disabled <c:if test="${model.isTrainingPM}">checked</c:if>>PM</label>
											</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">VL/SL:</label>
	       							    	<div class="checkbox col-md-1">
											  <label><input id="isVLSLAM" name="isVLSLAM" type="checkbox" value="true" readonly disabled <c:if test="${model.isVLSLAM}">checked</c:if>>AM</label>
											</div>
											&nbsp;
											<div class="checkbox col-md-1">
											  <label><input id="isVLSLPM" name="isVLSLPM" type="checkbox" value="true" readonly disabled <c:if test="${model.isVLSLPM}">checked</c:if>>PM</label>
											</div>
	       							    </div>
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label">Field Officer:</label>
		       							    <div class="col-md-2">
	       							    			<p class="form-control-static"><c:out value="${model.userCode}" /></p>
	       							    	</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">Date:</label>
	       							   		<div class="col-md-2">
	       							   			<p class="form-control-static"><c:out value="${model.date}" /></p>
											</div>
	       							    </div>
	       							    <div class="form-group">
	       							    	<label class="col-md-2 control-label">Working Sessions:</label>
	       							    	<div class="col-md-2">
												<p class="form-control-static"><c:out value='${model.workingSessionText}' /></p>
											</div>
	       							    </div>
	       							    <div class="form-group">
       							    		<label class="col-md-2 control-label">OT claimed:</label>
       							    		<div class="col-md-2">
	       							           <p class="form-control-static"><c:out value='${model.otClaimed}'/></p>
	       							    	</div>
	       							    	<div class="checkbox col-md-2">
											  <label><input name='isClaimOT' type="checkbox" value="true" readonly disabled <c:if test="${model.isClaimOT}">checked</c:if>>Claim OT</label>
											</div>
       							    	</div>
       							    	<div class="form-group">
       							    		<label class="col-md-2 control-label">Timeoff taken:</label>
       							    		<div class="col-md-2">
	       							           <p class="form-control-static"><c:out value='${model.timeoffTaken}'/></p>
	       							    	</div>
       							    	</div>	 						    	
		       							<div class="form-group">
			       							<label class="col-sm-2 control-label">Telephone Enumeration:</label>
			       							<div class="col-sm-10">&nbsp;</div>
			       							<div class="col-sm-12">
				       							<div class="col-sm-1">&nbsp;</div>
				       							<div class="col-sm-11">
				       								<table id="telEnuTable" class="table nowrap">
												        <thead>
												            <tr>
												                <th>Reference<br>Month</th>
												                <th>Survey</th>
												                <th>Case Reference No.</th>
												                <th>Completion</th>
												                <th>Deletion</th>
												                <th>Section</th>
												            </tr>
												     	</thead>
												     	<tbody>	
												     	</tbody>
												     </table>
											     </div>
										     </div>
		       							</div>
		       							<div class="form-group">
			       							<label class="col-sm-2 control-label">Fieldwork Activities:</label>
		       								<table id="fieldworkTable" class="table nowrap responsive">
										        <thead>
										            <tr>
										                <th>Reference<br>Month</th>
										                <th>Start time</th>
										                <th>Survey</th>
										                <th>Case Reference No. </th>
										                <th>Activity</th>
										                <th>Enumeration<br>Outcome</th>
										                <th>Market</th>
										                <th>Non-Market</th>
										                <th>Building</th>
										                <th>Destination</th>
										                <th>Mode of Transport</th>
										                <th>Expense<br>HK$</th>
										                <th>Include in Transport Form</th>
										                <th>Transit</th>
										                <th>Remark</th>
										            </tr>
										     	</thead>
										     	<tbody>
										     	</tbody>
										     </table>
										</div>
										<div id="hiddenData">
										<input type="hidden" name="status" id="status" value="<c:out value="${model.status}" />">
										<input type="hidden" name="preApproval" id="preApproval" value="false">
										</div>
									</div>
								</div>
							</div>
							<sec:authorize  access="(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
      							<c:if test="${model.status == 'Submitted'|| model.status == 'Approved'}">
      								<div class="box-footer">
										<button id="btnResubmissionRequest" type="button" class="btn btn-info">Resubmission requested by Field Officer</button>  
									</div>
								</c:if>
       						</sec:authorize>
						</div>
	        		</div>
	        	</div>
        	</form>
        	<form id="resubmissionRequestForm" action="<c:url value='/timeLogManagement/TimeLogMaintenance/resetTimeLogStatus'/>" method="post">
        		<input name="id" value="${model.timeLogId}" type="hidden" />
        	</form>
        </section>
	</jsp:body>
</t:layout>

