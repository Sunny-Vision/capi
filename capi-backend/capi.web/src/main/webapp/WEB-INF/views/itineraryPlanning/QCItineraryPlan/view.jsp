<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
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
				padding-right: 0px;
				margin-bottom: 4px;		
			}
			.hidden {
				display: none;
			}

		</style>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp"%>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/peCheckLookup.jsp" %>
		<script src="<c:url value='/resources/js/jquery-ui.min.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>"></script>
		<script src="<c:url value='/resources/js/jquery.tmpl.min.js'/>"></script>
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
			];
			--%>
			
			function renderTask(planItem, session) {
				$("#taskTemplate").tmpl(planItem).appendTo("#listSession"+session);
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
				}
			}
			
			function getSessionId(session) {
				if (session ==  "A") {
					return "1";
				} else if (session ==  "P") {
					return "2";
				} else if (session ==  "E") {
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

				$("#checkTemplate").tmpl(planItem).appendTo("#listSession"+session);
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
				for (var i=0 ; i < ids.length ;  i++) {
					if (ids[i].indexOf("_") > 0) {
						var index = ids[i].split("_")[1];
					} else {
						var index = ids[i];
					}
					
					item = getItemByIndex(index);
					item.session = sessionName;
					item.sequence = i+1;
					
					setItemStyle(index,item.session)				
				}
			}
			
			function setItemStyle(index, session) {
				if (session == "") {
					$("#item_"+index).attr('class', 'col-md-3 checklist list-group-item');
					$("#itemName_"+index).attr('class', 'col-sm-12');
					$("#itemRemark_"+index).attr('class', 'hidden');
				} else {
					$("#item_"+index).attr('class', 'col-md-12 assigned list-group-item');
					$("#itemName_"+index).attr('class', 'col-sm-3');
					$("#itemRemark_"+index).attr('class', 'col-sm-9');
				}
			}	

			// Onload

			$(document).ready(function() {
				
				planItemIndex = planItems.length + 1;
				renderItems();
			});
		</script>
		
		<script id="taskTemplate" type="text/x-jquery-tmpl">
			<li id="item_\${index}" class="col-md-3 checklist list-group-item">
				<div id="itemName_\${index}" class="col-sm-12">
					<div class="col-sm-12">
						<label>Task: </label>\${taskName}
					</div>
					<div class="col-sm-12">
						<label>Location: </label> \${location}
					</div>
				</div>
				<div id="itemRemark_\${index}" class="hidden">
					<input type="text" id="remark_\${index}" name="remark_\${index}" class="form-control" placeholder="Remark" value="\${remark}" readonly/>
				</div>
			</li>
		</script>
		<script id="checkTemplate" type="text/x-jquery-tmpl">
			<li id="item_\${index}" class="col-md-3 checklist list-group-item">
				<div id="itemName_\${index}" class="col-sm-12">
					<label>\${taskName} : </label> \${displayItem}
				</div>
				<div id="itemRemark_\${index}" class="hidden">
					<input type="text" id="remark_\${index}" name="remark_\${index}" class="form-control" placeholder="Remark" value="\${remark}" readonly/>
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
        		<input id="status" name="status" value="<c:out value="${model.submitStatus}" />" type="hidden" />
        		<input id="rejectReason" name="rejectReason" type="hidden" value="<c:out value="${model.rejectReason}" />"/>
	        	<div class="row">
	        		<div class="col-md-12">
	        			<!-- general form elements -->
	        			<div class="box box-primary">
	        				<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/itineraryPlanning/QCItineraryPlan/home'/>">Back</a>
							</div>
	       					<div class="box-body">
	       						<div class="form-horizontal">
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
														<div class="form-control-static"><c:out value="${commonService.formatDate(model.date)}"/></div>
													</div>
												</div>
											</div>
											<div class="col-md-5">
												<div class="row row-eq-height">
													<div class="col-md-3">
														<label class="control-label">Submit To:</label>
													</div>
													<div class="col-md-9">
														<div class="input-group">
															<input name="submitTo" type="text" class="form-control" value="<c:out value="${model.submitTo}" />" required readonly />
															<input id="submitToId" name="submitToId" value="<c:out value="${model.submitToId}" />" type="hidden" />
															<div class="input-group-addon searchSubmitToId">
																<i class="fa fa-search"></i>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="col-md-3">
												<div class="row row-eq-height">
													<div class="col-md-4">
														<label class="control-label"><input type="radio" id="session" name="session" value="AP" <c:out value='${model.session == "AP" ? "checked" : "disabled"}' /> > AP Session</label>
													</div>
													<div class="col-md-4">
														<label class="control-label"><input type="radio" id="session" name="session" value="PE" <c:out value='${model.session == "PE" ? "checked" : "disabled"}' /> > PE Session</label>
													</div>
													<div class="col-md-4">
														<label class="control-label"><input type="radio" id="session" name="session" value="APE" <c:out value='${model.session == "APE" ? "checked" : "disabled"}' /> > APE Session</label>
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
									<div class="clearfix">&nbsp;</div>
       							   	<div class="col-md-12">
       							   		<label id="labelSession1" class="control-label" <c:if test="${model.session == 'PE' }">style="display:none"</c:if>>A Session:</label>
       							   		<ul class="list-group" <c:if test="${model.session == 'PE' }">style="display:none"</c:if>>
       							   			<li class="col-md-12 list-group-item">
       								   		<ul id="listSession1" class="col-md-12 list-group sortable sortableItem">
       								   		</ul>
       								   		</li>
       							   		</ul>
       							   		<label id="labelSession2" class="control-label">P Session:</label>
       							   		<ul class="list-group">
       							   			<li class="col-md-12 list-group-item">
       								   		<ul id="listSession2" class="col-md-12 list-group sortable sortableItem">
       								   		</ul>
       								   		</li>
       							   		</ul>
       							   		<label id="labelSession3" class="control-label" <c:if test="${model.session == 'AP' }">style="display:none"</c:if>>E Session:</label>
       							   		<ul class="list-group" <c:if test="${model.session == 'AP' }">style="display:none"</c:if>>
       							   			<li class="col-md-12 list-group-item">
       								   		<ul id="listSession3" class="col-md-12 list-group sortable sortableItem">
       								   		</ul>
       								   		</li>
       							   		</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
	        	</div>
	        </form>
      </section>
	</jsp:body>
</t:layout>

