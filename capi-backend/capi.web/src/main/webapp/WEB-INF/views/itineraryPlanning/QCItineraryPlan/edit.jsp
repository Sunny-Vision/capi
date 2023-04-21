<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<sec:authentication property="details" var="userDetail" />
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
		<style>
			.modal.modal-wide .modal-dialog {
			  width: 90%;
			}
			.modal-wide .modal-body {
			  overflow-y: auto;
			}
			.btn.pull-right {
				margin-left: 10px;
			}
			.ui-sortable tr {
			    cursor:pointer; 
			}   
			.checklist {
				margin: 5px 1% 5px 1%;
				width: 31.33%
			}
			.assigned {
				margin: 5px 1% 5px 1%;
				width: 98%
			}
			.sortableItem {
				min-height: 100px;
				padding-right: 0px;
				margin-bottom: 4px;		
			}
			.hidden {
				display: none;
			}
			.btnRemoveItem {
			    float: right; 
			    padding: 2px 0px 6px 2px;
			}
			.glyphicon-pencil, .glyphicon-remove, .glyphicon-plus, #addTaskLink, #addPECheckLink {
    			cursor: pointer;
			}

		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/peCheckLookup.jsp" %>
		<script src="<c:url value='/resources/js/jquery-ui.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.fireOnDisable.js' />"></script>
		<script>

		    // Create an array of plan item
		    planItems = ${commonService.jsonEncode(model.qcItineraryPlanItemModel)};
		    planItems = planItems || []
			for (i = 0; i < planItems.length; i++){
				var p = planItems[i];
				p.index = i + 1;
			}	    
		    
			<%--
			planItems = [
			<c:if test="${act == 'edit'}">
				<c:forEach items="${model.qcItineraryPlanItemModel}" var="item" varStatus="i">
						{
							index : <c:out value='${i.index + 1}' />,
							qcItineraryPlanItemId : <c:out value='${item.qcItineraryPlanItemId}' />,
							session : "<c:out value='${item.session}' />",
							remark : "<c:out value='${item.remark}' />",
							sequence : <c:out value='${item.sequence}' />,
							taskName : "<c:out value='${item.taskName}' />",
							location : "<c:out value='${item.location}' />",
							session : "<c:out value='${item.session}' />",
							itemType : <c:out value='${item.itemType}' />,
							officerName : "<c:out value='${item.officerName}' />",
							<c:if test="${item.spotCheckFormId != null}">
							spotCheckFormId : <c:out value='${item.spotCheckFormId}' />,
							</c:if>
							<c:if test="${item.supervisoryVisitFormId != null}">
							supervisoryVisitFormId : <c:out value='${item.supervisoryVisitFormId}' />,
							</c:if>
							<c:if test="${item.peCheckFormId != null}">
							peCheckFormId : <c:out value='${item.peCheckFormId}' />,
							</c:if>
						},
				</c:forEach>
			</c:if>
			];
		    --%>

			var plannedDates = <c:choose><c:when test="${not empty planDates}">${planDates}</c:when><c:otherwise>[]</c:otherwise></c:choose>;
			var nonWorkingDates = <c:choose><c:when test="${not empty nonWorkingDates}">${nonWorkingDates}</c:when><c:otherwise>[]</c:otherwise></c:choose>;

			
			function addTask() {
				bindTaskBtn();
				$("#taskSubject").val('');
				$("#taskLocation").val('');
				$("#taskDialogLabel").text("Add Task");
				$("#taskForm").modal('show');
			}
			
			function renderTask(planItem, session) {
				$("#taskTemplate").tmpl(planItem).appendTo("#listSession"+session);

				$("#editTask_"+planItem.index).on('click', function() {
					editTask(planItem);
				});	
    			$("#removeTask_"+planItem.index).on('click', function () {
    				removeTask($(this).attr('id').split('_')[1]);
    			}) 
			}
			
			function removeTask(index) {

				for (i = planItems.length; i > 0 ; i--) {
					if (planItems[i-1].index == index) {
						planItems.splice(i-1, 1);
						break;
					}
				}
				$("#item_"+index).remove();
			}

			function renderItems() {
				for (var i=0 ; i < planItems.length ; i++) {
					item = planItems[i];
					if (item.itemType > 3) {
						renderTask(item,getSessionId(item.session));
					} else {
						renderCheck(item,getSessionId(item.session));
					}
					setItemStyle(item.index,item.session)
					addRemarkEvent(item)
				}
			}
			
			function getSessionId(session) {
				if (session ==  "A") {
					return "1";
				} else if (session ==  "P") {
					return "2";
				}
				else if (session == "E"){
					return "3";
				}
				return "";
			}
			
			function renderCheck(planItem, session) {
				
				switch(planItem.itemType) {
				    case 1:
				    	planItem.taskName = "Spot Check";
				        break;
				    case 2:
				    	planItem.taskName = "Supervisory Visit";
				        break;
				    default:
				    	planItem.taskName = "PE Check";
				}
				planItem.canDelete = (planItem.itemType == 3);
				$("#checkTemplate").tmpl(planItem).appendTo("#listSession"+session);
    			$("#removeTask_"+planItem.index).on('click', function () {
    				removeTask($(this).attr('id').split('_')[1]);
    			}) 
			}
			
			function getItemByIndex(index) {
				for (var i=0 ; i < planItems.length ; i++) {
					if (planItems[i].index == index) {
						return planItems[i];
					}
				}
				return null;
			}
			
			function setSessionWithSeq(ids, sessionName) {
				var items = [];
				for (var i=0 ; i < ids.length ;  i++) {
					console.log("indexOf:"+ids[i].indexOf("_"));
					if (ids[i].indexOf("_") > 0) {
						var index = ids[i].split("_")[1];
					} else {
						var index = ids[i];
					}
					
					item = getItemByIndex(index);
					item.session = sessionName;
					item.sequence = i+1;
					console.log("index:"+index);
					console.log("sessionName:"+sessionName);
					setItemStyle(index,item.session)	
					items.push(item);
				}
				return items;
			}
			
			function setItemStyle(index, session) {
				if (session == "") {
					$("#item_"+index).attr('class', 'col-md-3 checklist list-group-item');
					$("#itemName_"+index).attr('class', 'col-sm-11');
					$("#itemRemark_"+index).attr('class', 'hidden');
				} else {
					$("#item_"+index).attr('class', 'col-md-12 assigned list-group-item');
					$("#itemName_"+index).attr('class', 'col-sm-3');
					$("#itemRemark_"+index).attr('class', 'col-sm-8');
				}
			}
			
			
			function checkPool(ids) {
				for (var i=0 ; i < ids.length ;  i++) {
					var index = ids[i].split("_")[1];	
					item = getItemByIndex(index);
					if (item.itemType == 1 || item.itemType == 2) {
						return true;
					}	
				}
				return false;
			}	
			
			function changeSession() {

				taskIds = $("#listSession").sortable('toArray', { attribute: 'id' });
				session1Ids = $("#listSession1").sortable('toArray', { attribute: 'id' });
				session2Ids = $("#listSession2").sortable('toArray', { attribute: 'id' });
				session3Ids = $("#listSession3").sortable('toArray', { attribute: 'id' });
				
				if (taskIds.length > 0 ) {
					setSessionWithSeq(taskIds, "");
				}
				
				if (session1Ids.length > 0 ) {
					setSessionWithSeq(session1Ids, "A");
				}
				if (session2Ids.length > 0 ) {
					setSessionWithSeq(session2Ids, "P");
				}
				if (session3Ids.length > 0 ) {
					setSessionWithSeq(session3Ids, "E");
				}
				
			}
			
			function addRemarkEvent(newItem) {
    			$("#remark_"+newItem.index).on("input", function() {
    				var index = eval($(this).attr("id").split("_")[1]);
    				var item = getItemByIndex(index);
    				item.remark = $(this).val();
    				console.log("remark " + index + " : "+  $(this).val());
    			})
			}
			
			function removeOldChecks() {
				var newItems = [];
				for (var i=0 ; i < planItems.length ; i++) {
					if (planItems[i].itemType != 1 && planItems[i].itemType != 2) {
						newItems.push(planItems[i]);
					} else {
						$("#item_" +planItems[i].index).remove();
					}
				}
				planItems = newItems;
			}
			
			function checkPEexist(peCheckFormId) {
				for (var i=0 ; i < planItems.length ; i++) {
					if (planItems[i].peCheckFormId == peCheckFormId ) {
						return true;
					}
				}
				return false;
			}
			
			function editTask(item) {
				
				$("#taskSubject").val(item.taskName);
				$("#taskLocation").val(item.location);
				$("#addTaskBtn").unbind( "click" );
				$("#addTaskBtn").on('click', function() {
					item.taskName = $("#taskSubject").val();
					item.location = $("#taskLocation").val();
					$("#taskName_"+item.index).text(item.taskName);
					$("#taskLocation_"+item.index).text(item.location);
					$("#taskForm").modal('hide');
				});
				$("#taskDialogLabel").text("Edit Task");
				$("#taskForm").modal('show');	
			}
			
			function bindTaskBtn() {
				// Add Task Dialog
				$("#addTaskBtn").unbind( "click" );
				$("#addTaskBtn").on('click', function() {
					
					var newItem = {};		
					newItem.taskName = $("#taskSubject").val();
					newItem.location = $("#taskLocation").val();
					newItem.session = "";
					newItem.sequence = 0;
					newItem.remark = "";
					newItem.itemType = 4;
					newItem.index = planItemIndex;		
					planItemIndex++;
					planItems.push(newItem);
					
					renderTask(newItem, "");
					addRemarkEvent(newItem)
					
					$("#taskForm").modal("hide");
					$("#taskSubject").val("");
					$("#taskLocation").val("");
					
				});
			}
			
			
			
			// Onload

			$(document).ready(function() {
				
				planItemIndex = planItems.length + 1;
				
				<c:if test="${act == 'edit'}">
					renderItems();
					
					$("#inputDate").datepicker("setDate", parseDate('<c:out value="${commonService.formatDate(model.date)}"/>'));
					$("#inputDate").prop("disabled","true");
				</c:if>
				
				Modals.init();
				
				Datepicker();
				
				// Load Spot and Supervisory Check
				/*
				$("#inputDate").datepicker().on('changeDate', function(e) {
						removeOldChecks();
						var newItems = [];			
						$.ajax({
							url: "<c:url value='/itineraryPlanning/QCItineraryPlan/queryChecks'/>",
							type: 'POST',
							async: false,
							data: { 
								date: $('#inputDate').val(),
								},
							success: function (response) {
								newItems = response;
	
							}
						});
				
						for(var i=0 ; i < newItems.length ; i++) {
							var newItem = newItems[i];
							newItem.session = "";
							newItem.sequence = 0;
							newItem.remark = "";
							newItem.index = planItemIndex;	
							planItemIndex++;
							planItems.push(newItem);
							renderCheck(newItem, "");
							addRemarkEvent(newItem);
						}
				});
				*/
				$("#inputDate").datepicker({
					beforeShowDay: function(date) {
					    if (plannedDates.length >= 3){
					    	return {enabled: false};
					    }
					    else if (Date.today().add({days:30}) <= date || date < Date.today()){
					    	return {enabled: false};
					    }
					    else {
					    	//console.log(plannedDates);
					    	for (i = 0; i < plannedDates.length; i++){
					    		var planned = parseDate(plannedDates[i]);
					    		if (planned.equals(date)){
					    			return {enabled: false};
					    		}
					    	}
					    	
					    	for (i = 0; i < nonWorkingDates.length; i++){
					    		var planned = parseDate(nonWorkingDates[i]);
					    		if (planned.equals(date)){
					    			return {enabled: false};
					    		}
					    	}
					    }
					},
					autoclose : true,
					//datesDisabled: plannedDates
				}).on("changeDate", function(e){

					removeOldChecks();
					var newItems = [];			
					$.ajax({
						url: "<c:url value='/itineraryPlanning/QCItineraryPlan/queryChecks'/>",
						type: 'POST',
						async: false,
						data: { 
							date: $('#inputDate').val(),
						},
						success: function (response) {
							newItems = response;

						}
					});
			
					for(var i=0 ; i < newItems.length ; i++) {
						var newItem = newItems[i];
						newItem.session = "";
						newItem.sequence = 0;
						newItem.remark = "";
						newItem.index = planItemIndex;	
						planItemIndex++;
						planItems.push(newItem);
						renderCheck(newItem, "");
						addRemarkEvent(newItem);
					}
				});
				
				/*
				$('#inputDate').fireOnDisable().select2({
					ajax: {
					    data: function (params) {
					    	var query = {
					    		search: params.term,
					    		userId: '${details.userId}',
					    	}
					      	return query;
					    }
					},
					width:'100%',
					minimumResultsForSearch: -1,
				}).on("change",function(){
					removeOldChecks();
					var newItems = [];			
					$.ajax({
						url: "<c:url value='/itineraryPlanning/QCItineraryPlan/queryChecks'/>",
						type: 'POST',
						async: false,
						data: { 
							date: $('#inputDate').select2('val'),
						},
						success: function (response) {
							newItems = response;

						}
					});
			
					for(var i=0 ; i < newItems.length ; i++) {
						var newItem = newItems[i];
						newItem.session = "";
						newItem.sequence = 0;
						newItem.remark = "";
						newItem.index = planItemIndex;	
						planItemIndex++;
						planItems.push(newItem);
						renderCheck(newItem, "");
						addRemarkEvent(newItem);
					}
				});
				
				$('#inputDate').hide();
				*/
				// Submit to user lookup
				/*
				$('[name="submitTo"],.searchSubmitToId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="submitTo"]').val(singleRowData.staffCode + " - "
								+ singleRowData.chineseName);
						$('[id="submitToId"]').val(id);
					},
					queryDataCallback: function(model) {
						model.authorityLevel = (1 | 2);
					},
					multiple: false
				});
				*/
				
				// Change label for Sessoins
				$('input[type=radio][name=session]').on('change', function(){
					console.log("session: " + $(this).val())
					if (!this.checked){
						return
					}
				   // Get all rows with search applied
					if ($(this).val() == "AP") {
						   $("#session1").show();
						   $("#session3").hide();			
						   var sessionIds = $("#listSession3").sortable('toArray', { attribute: 'id' });
						   var items = setSessionWithSeq(sessionIds, "");
						   for (i = 0; i < items.length; i++){
							   if (items[i].itemType == 4){
								   renderTask(items[i],"");
							   }
							   else{
								   renderCheck(items[i],"");								   
							   }
						   }
						   $("#listSession3").empty();
						  
					   }
					   else if ($(this).val() == "PE"){
						   $("#session1").hide();
						   $("#session3").show();
						   var sessionIds = $("#listSession1").sortable('toArray', { attribute: 'id' });
						   var items = setSessionWithSeq(sessionIds, "");
						   for (i = 0; i < items.length; i++){
							   if (items[i].itemType == 4){
								   renderTask(items[i],"");
							   }
							   else{
								   renderCheck(items[i],"");								   
							   }
						   }
						   $("#listSession1").empty();
					   }
					   else{
						   // APE
						   $("#session1").show();
						   $("#session3").show();
					   }
				   /*
				   if ($(this).val() == "AP") {
					   $('#labelSession1').text("A Session");
					   $('#labelSession2').text("P Session");
					   
				   } else {
					   $('#labelSession1').text("P Session");
					   $('#labelSession2').text("E Session");	   
				   }
					
					session1Ids = $("#listSession1").sortable('toArray', { attribute: 'id' });
					session2Ids = $("#listSession2").sortable('toArray', { attribute: 'id' });

					setSessionWithSeq(session1Ids, $(this).val().substring(0,1));
					setSessionWithSeq(session2Ids, $(this).val().substring(1,2));
					*/
				});	
				
				// Add PE Check Dialog
				$('#addPECheckLink').peCheckLookup({
					selectedIdsCallback: function(peCheckFormId, officerName, displayItem) {
						for (var i=0 ; i < peCheckFormId.length ; i++) {
							
							if (!checkPEexist(peCheckFormId[i])) {
								var newItem = {};
								newItem.peCheckFormId = peCheckFormId[i];
								newItem.officerName = officerName[i];
								newItem.displayItem = displayItem[i];
								newItem.session = "";
								newItem.sequence = 0;
								newItem.itemType = 3;
								newItem.remark = "";
								newItem.index = planItemIndex;	
								planItemIndex++;
								planItems.push(newItem);
								renderCheck(newItem, "");
								addRemarkEvent(newItem);
							} else {
								bootbox.alert({
									title: "Alert",
									message: "<spring:message code='E00115' />"
								});
							}
						}
					},
					multiple: true,
					queryDataCallback: function(model) {
			    		model.date= $('#inputDate').val();
			    		model.excludedPEFormIds = [];
			    		model.userId = ${model.userId};
			    		for (i = 0; i < planItems.length; i++){
			    			if (planItems[i].itemType == 3){
			    				model.excludedPEFormIds.push(planItems[i].peCheckFormId);
			    			}
			    		}
			    		
					}
				});
				
				// Add PE Check Dialog
				$('#addTaskLink').on('click', function() {
					addTask();
				});
				
				bindTaskBtn();
				
				// Session drag and drop
			    $("#listSession, #listSession1, #listSession2, #listSession3").sortable({
			    	connectWith: ".sortable",
					stop : function(event, ui){
						changeSession();
					}
			    });
				
				$(".btn-selectedTask").click(function(){
					var sessionDiv = $(this).parents(".sessionDiv:first");
					var sortableList = sessionDiv.find(".sortableItem");
					
					$(".selectedTask:checked").each(function(){
						sortableList.append($(this).parents('li:first'));
					});			
					
					$(".selectedTask:checked").removeAttr("checked");
					
					changeSession();
				})
				
				$("#submitBtn").on('click', function() {
					$("#status").val("Submitted");
					$("#mainForm").submit();
				});
				
				$("#saveBtn").on('click', function() {
					$("#status").val("Draft");
					//$("#mainForm").submit();
				})
				
				
				$('#submitToId').select2({
					ajax: {
					    data: function (params) {
					    	return params;
					    	/*var query = {
					    		search: params.term
					    	}
					      	return query;*/
					    }
					},
					width:'100%',
				});
				
				$("#mainForm").validate({
					rules : {
						ignore: [],  
						submitToId:{
							conditionRequired: function() {
								return $("#status").val() == "Submitted"
							}
						}
					},
					submitHandler: function(form) {
						
						var taskListIds = $("#listSession").sortable('toArray', { attribute: 'id' });
						
						if (taskListIds.length > 0 && $("#status").val() == "Submitted") {
							bootbox.alert({
								title: "Alert",
								message: "<spring:message code='E00061' />"
							});
							$("#status").val("");
							return false;
						}
						
						
						for ( var i=0 ; i <= planItems.length ; i++) {
							for ( var key in planItems[i] ) {
				                	$('#submitData').append($("<input type='hidden' />")
				                		.attr("name","qcItineraryPlanItemModel["+i+"]."+key).attr("value",planItems[i][key]));
							}
						}
						form.submit();
					}
				});
				
				jQuery.extend(jQuery.validator.messages, {
				    required: "<spring:message code='E00010' />",
				});
				$("input[name='session']").trigger("change")
			});		
		</script>
		
		<script id="taskTemplate" type="text/x-jquery-tmpl">
			<li id="item_\${index}" class="col-md-3 checklist list-group-item">
				<span id="removeTask_\${index}" class="btnRemoveItem glyphicon glyphicon-remove" ></span>
				<div id="itemName_\${index}" class="col-sm-11" style="padding-left:20px">	
					<input type="checkbox" class="selectedTask" style="float:left;margin-left:-20px" />			
					<div class="col-sm-12">
						<label>Task: </label> <span id="taskName_\${index}">\${taskName}</span> <span id="editTask_\${index}" class='glyphicon glyphicon-pencil' aria-hidden='true'></span>
					</div>
					<div class="col-sm-12">
						<label>Location: </label> <span id="taskLocation_\${index}">\${location}</span>
					</div>
				</div>
				<div id="itemRemark_\${index}" class="hidden">
					<input type="text" id="remark_\${index}" name="remark_\${index}" class="form-control" placeholder="Remark" value="\${remark}"/>
				</div>
			</li>
		</script>
		<script id="checkTemplate" type="text/x-jquery-tmpl">
			<li id="item_\${index}" class="col-md-3 checklist list-group-item">
				{{if canDelete }}
                	<span id="removeTask_\${index}" class="btnRemoveItem glyphicon glyphicon-remove"></span>
				{{/if}}

				<div id="itemName_\${index}" class="col-sm-11" style="padding-left:20px">
					<input type="checkbox" class="selectedTask" style="float:left;margin-left:-20px" />		
					<label>\${taskName} : </label> \${displayItem}
				</div>
				<div id="itemRemark_\${index}" class="hidden">
					<input type="text" id="remark_\${index}" name="remark_\${index}" class="form-control" placeholder="Remark" value="\${remark}"/>
				</div>
			</li>
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
        	<h1>Field Supervisor Itinerary Plan Maintenance</h1>
        	<c:if test="${not empty model.qcItineraryPlanId}">
				<div class="breadcrumb form-horizontal" style="width:240px">
					<div class="form-group" style="margin-bottom:0px">
			        	<div class="col-sm-5">Created Date:</div>
			        	<div class="col-sm-7"><c:out value="${commonService.formatDateTime(model.createdDate)}" /></div>
			        </div>
			        <div class="form-group" style="margin-bottom:0px">
			         	<div class="col-sm-5">Last Modified:</div>
			         	<div class="col-sm-7"><c:out value="${commonService.formatDateTime(model.modifiedDate)}" /></div>
			         </div>
		      	</div>
	      	</c:if>
        </section>
        <section class="content">
        	<form id="mainForm" method="post" role="form" action="<c:url value='/itineraryPlanning/QCItineraryPlan/save'/>" enctype="multipart/form-data">
        		<input id="qcItineraryPlanId" name="qcItineraryPlanId" value="<c:out value="${model.qcItineraryPlanId}" />" type="hidden" />
        		<input id="inputModifiedDate" name="inputModifiedDate" value="<c:out value="${commonService.formatDateTime(model.modifiedDate)}"/>" type="hidden" />
        		<input id="status" name="submitStatus" value="<c:out value="${model.submitStatus}" />" type="hidden" />
        		<input id="rejectReason" name="rejectReason" type="hidden" value="<c:out value="${model.rejectReason}" />"/>
        		<input name="version" value="<c:out value="${model.version}" />" type="hidden" />
        		<input name="userId" value="<c:out value="${model.userId}" />" type="hidden" />
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/itineraryPlanning/QCItineraryPlan/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal" style="overflow-y:auto;overflow-x:hidden">
	       							<c:if test="${model.submitStatus == 'Rejected'}">
       									<label class="col-md-2">Reject Reason: <c:out value="${model.rejectReason}" /></label>
  									</c:if>
  									<div class="row">
	  									<div class="col-md-12">
											<div class="col-md-4">
												<div class="row row-eq-height">
													<div class="col-md-2">
														<label class="control-label">Date</label>
													</div>
													<div class="col-md-10">
														<%--
														<div class="input-group">
														
															<input type="text" id="inputDate" name="inputDate" class="form-control filters date-picker" value="<c:out value="${commonService.formatDate(model.date)}"/>" maxlength="10" required>
															<div class="input-group-addon">
																<i class="fa fa-calendar"></i>
															</div>															
														</div>														
														 --%>
														 <%-- 
														<select class="form-control select2 " id="inputDate" name="inputDate" 
																	<c:if test="${act eq 'edit'}" >disabled</c:if>
																	data-ajax-url="<c:url value='/itineraryPlanning/QCItineraryPlan/queryDateSelect2'/>" required>
															<c:if test="${not empty model.date }" >
																<option value="<c:out value="${commonService.formatDate(model.date)}"/>" selected>
																	<c:out value="${commonService.formatDate(model.date)}"/>
																</option>
															</c:if>
														</select>	
														<c:if test="${act eq 'edit'}" >
															<input type="hidden" name="inputDate" value="${commonService.formatDate(model.date)}" />
														</c:if>
														--%>
														<div class="input-group">
													 		<input type="text" <c:if test="${act != 'edit'}">name="inputDate"</c:if> id="inputDate" class="form-control datepicker" />	
													 		<div class="input-group-addon">
																<i class="fa fa-calendar"></i>
															</div>
													 	</div>	
														 <c:if test="${act == 'edit'}">	
														 	<input type="hidden" name="inputDate" value="<c:out value="${commonService.formatDate(model.date)}"/>" />	
														 </c:if>
													</div>
												</div>
											</div>
											<div class="col-md-5">
												<div class="row row-eq-height">
													<div class="col-md-3">
														<label class="control-label">Submit To:</label>
													</div>
													<div class="col-md-9">
														<select class="form-control select2" id="submitToId" name="submitToId" 
															data-ajax-url="<c:url value='/itineraryPlanning/QCItineraryPlan/queryHeadUserSelect2'/>">
															<c:if test="${not empty model.submitToId}" >
																<option value="<c:out value="${model.submitToId}" />" selected><c:out value="${model.submitTo}" /></option>
															</c:if>	
														</select>
														<%--
														<div class="input-group">
															<input name="submitTo" type="text" class="form-control" value="<c:out value="${model.submitTo}" />" required readonly />
															<input id="submitToId" name="submitToId" value="<c:out value="${model.submitToId}" />" type="hidden" />
															<div class="input-group-addon searchSubmitToId">
																<i class="fa fa-search"></i>
															</div>
														</div>
														 --%>
													</div>
												</div>
											</div>
											<div class="col-md-3">
												<div class="row row-eq-height">
													<div class="col-md-4">
														<label class="control-label"><input type="radio" id="session" name="session" value="AP" <c:out value='${model.session == "AP" ? "checked" : ""}' />> AP Session</label>
													</div>
													<div class="col-md-4">
														<label class="control-label"><input type="radio" id="session" name="session" value="PE" <c:out value='${model.session == "PE" ? "checked" : ""}' />> PE Session</label>
													</div>
													<div class="col-md-4">
														<label class="control-label"><input type="radio" id="session" name="session" value="APE" <c:out value='${model.session == "APE" ? "checked" : ""}' />> APE Session</label>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="clearfix">&nbsp;</div>
									<div class="col-md-12">
       							   		<ul class="list-group">
       							   			<li class="col-md-12 list-group-item">
       								   		<ul id="listSession" class="col-md-12 list-group sortable sortableItem">
       								   		</ul>
       								   		</li>
       							   		</ul>
									</div>
									<div class="col-md-12">
									<label class="col-md-2"><a id="addPECheckLink" ><span id="addPECheckIcon" class="glyphicon glyphicon-plus"></span> Add PE Check</a></label> 
									<label class="col-md-2"><a id="addTaskLink" ><span id="addTaskIcon" class="glyphicon glyphicon-plus"  ></span> Add Task</a></label>
									</div>
									<div class="clearfix">&nbsp;</div>
      							   	
     							   		<div id="session1" class="col-sm-12 sessionDiv" data-session="A">
     							   			<p>
	       							   			<label id="labelSession1" class="control-label" style="text-align:left" >A Session:</label>
	       							   			<button type="button" class="btn btn-primary btn-selectedTask">Add Selected Task</button>
	       							   		</p>
	       							   		<ul class="list-group">
	       							   			<li class="col-md-12 list-group-item">
	       								   		<ul id="listSession1" class="col-md-12 list-group sortable sortableItem" style="min-height:60px">
	       								   		</ul>
	       								   		</li>
	       							   		</ul>
     							   		</div>
     							   		<div id="session2" class="col-sm-12 sessionDiv" data-session="P">
     							   			<p>
	       							   			<label id="labelSession2" class="control-label" style="text-align:left">P Session:</label>
	       							   			<button type="button" class="btn btn-primary btn-selectedTask">Add Selected Task</button>
	       							   		</p>
	       							   		<ul class="list-group">
	       							   			<li class="col-md-12 list-group-item">
	       								   		<ul id="listSession2" class="col-md-12 list-group sortable sortableItem" style="min-height:60px">
	       								   		</ul>
	       								   		</li>
	       							   		</ul>
       							   		</div>
       							   		<div id="session3" class="col-sm-12 sessionDiv" data-session="E">
       							   			<p>
	       							   			<label id="labelSession3" class="control-label" style="text-align:left">E Session:</label>
	       							   			<button type="button" class="btn btn-primary btn-selectedTask">Add Selected Task</button>
	       							   		</p>
	       							   		<ul class="list-group">
	       							   			<li class="col-md-12 list-group-item">
	       								   		<ul id="listSession3" class="col-md-12 list-group sortable sortableItem" style="min-height:60px">
	       								   		</ul>
	       								   		</li>
	       							   		</ul>
       							   		</div>
									
									<div class="clearfix">&nbsp;</div>
								</div>
							</div>
							<div class="box-footer">
							<div id="submitData"></div>			
							<sec:authorize access="hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)">				
		       					<button id="saveBtn" type="submit" class="btn btn-primary">Save</button>       
		       					<button id="submitBtn" type="button" class="btn btn-primary">Submit</button>		
		       				</sec:authorize>				
		      				</div>
						</div>
						
					</div>
	        	</div>
	        </form>
			<!-- Add Task Dialog -->
			<div class="modal fade" id="taskForm" tabindex="-1" role="dialog" aria-labelledby="dialogLabel" aria-hidden="true">
			  <div class="modal-dialog">
				<div class="modal-content">
					<div  class="clearfix overlay" data-bind="loading:isLoading, visible:isLoading" ></div>
					  <div class="modal-header">
						<h4 class="modal-title" id="taskDialogLabel" data-bind="text:formTitle">Add Task</h4>
					  </div>
					  <div class="modal-body form-horizontal">
						<div class="form-group">
							<div class="col-md-3 control-label">Subject:</div>
							<div class="col-md-7">
								<input id="taskSubject" type="text" class="form-control"/>
							</div>
						</div>
						<div class="form-group">
							<div class="col-md-3 control-label">Location:</div>
							<div class="col-md-7">
								<input id="taskLocation" type="text" class="form-control" />
							</div>
						</div>
						<div style="margin-top: 30px;" class="col-md-3 control-label"></div>
						<div style="margin-top: 30px;" class="col-md-2">
							<button type="submit" class="btn btn-primary" data-loading-text="Loading..." id="addTaskBtn">Submit</button>
						</div>
						<div style="margin-top: 30px;" class="col-md-5">
							<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						</div>
					</div>
				</div>
			  </div>
			</div>
        </section>
	</jsp:body>
</t:layout>

