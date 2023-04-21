<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/datejs.jsp" %>
		
	</jsp:attribute>
	<jsp:attribute name="header">

<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>

<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>

		<style>
			.btn.pull-right {
				margin-left: 10px;
			}
			
			.highlight {
			    background: #000;
 			    color: #fff
			}
			.datepicker td.day.highlight:hover{
				color:#000
			}
		</style>
<script>

$(document).ready(function(){
	
	var $mainForm = $('#mainForm');

	$mainForm.validate();

	<c:choose>
	    <c:when test="${empty spotCheckDates}">
	    	var spotCheckDates = [];
	    </c:when>    
	    <c:otherwise>
	    	var spotCheckDates = ${spotCheckDates};
	    </c:otherwise>
	</c:choose>
	
	<c:choose>
	    <c:when test="${empty spotCheckDateByUser}">
	    	var spotCheckDateByUser = [];
	    </c:when>    
	    <c:otherwise>
	    	var spotCheckDateByUser = ${spotCheckDateByUser};
	    </c:otherwise>
	</c:choose>
	
	<c:choose>
	    <c:when test="${act == 'add'}">
		    $('#visitDate').datepicker({
				beforeShowDay: function(date) {
				    for (var i = 0; i < spotCheckDates.length; i++) {
				        if (parseDate(spotCheckDates[i]).equals(date)) {
							return 'highlight';
				        }
				    }
				}
			});
	    </c:when>    
	    <c:otherwise>
		    $('#visitDate').datepicker({
				beforeShowDay: function(date) {
				    for (var i = 0; i < spotCheckDateByUser.length; i++) {
				        if (parseDate(spotCheckDateByUser[i]).equals(date)) {
							return 'highlight';
				        }
				    }
				}
			});
	    </c:otherwise>
	</c:choose>
	
	/*
	$('#visitDate').datepicker({
		beforeShowDay: function(date) {
		    /*for (var i = 0; i < spotCheckDates.length; i++) {
		        if (parseDate(spotCheckDates[i]).equals(date)) {
					return 'highlight';
		        }
		    }
		}
	});
	*/

	Modals.init();
	$('[name="userName"],.searchUserId').userLookup({
		selectedIdsCallback: function(selectedIds, singleRowData) {
			var id = selectedIds[0];
			$('[name="userName"]').val(
					singleRowData.staffCode
					+ " - "  
					+ singleRowData.chineseName);
			$('[name="userId"]').val(id);

			$.ajax({
				type: 'POST',
				url: "<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/querySCSVDatesByOfficer'/>",
				data: {officerId: id},
				dataType: 'json',
				async:false,
				success: function(json){
					spotCheckDates = json;
					$('#visitDate').datepicker("update");
				}
			});
		},
		queryDataCallback: function(model) {
			model.teamOnly = true;
			model.authorityLevel = 16;
		},
		multiple: false
	});
	
	$('[name="checkerName"],.searchCheckerId').userLookup({
		selectedIdsCallback: function(selectedIds, singleRowData) {
			var id = selectedIds[0];
			$('[name="checkerName"]').val(
					singleRowData.staffCode
					+ " - "  
					+ singleRowData.chineseName);
			$('[name="checkerId"]').val(id);

			$.ajax({
				type: 'POST',
				url: "<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/querySCSVDatesByOfficer'/>",
				data: {officerId: id},
				dataType: 'json',
				async:false,
				success: function(json){
					spotCheckDates = json;
					$('#visitDate').datepicker("update");
				}
			});
		},
		queryDataCallback: function(model) {
			model.teamOnly = false;
			model.authorityLevel = 1|2|4;
		},
		multiple: false
	});
	
	<c:if test="${isReadOnly and not (act eq 'add')}">
		$("input").attr("disabled","disabled")	
	</c:if>
	
	<sec:authorize access="!(hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256))">
		viewOnly();
	</sec:authorize>

});


</script>
</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>SC/SV Plan Maintenance</h1>
				<c:if test="${act != 'add'}">
			<div class="breadcrumb form-horizontal" style="width:240px">
				<div class="form-group" style="margin-bottom:0px">
		        	<div class="col-sm-5">Created Date:</div>
		        	<div class="col-sm-7">${model.createdDate}</div>
		        </div>
		        <div class="form-group" style="margin-bottom:0px">
		         	<div class="col-sm-5">Modified Date:</div>
		         	<div class="col-sm-7">${model.modifiedDate}</div>
		         </div>
	      	</div>
      	</c:if>
		</section>
		<section class="content">
		<form class="form-horizontal" action="<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="scSvPlanId" value="<c:out value="${model.scSvPlanId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/home'/>">Back</a>
							</div>
							<!-- /.box-header -->
							<!-- form start -->
								<div class="box-body">
									<div class="form-group">
										<label class="col-sm-2 control-label">Field Officer</label>
										<div class="col-sm-4">
											<div class="input-group">
												<input type="text" name="userName" class="form-control" value="<c:out value="${model.userNameDisplay}" />" readonly required>
												<input id="userId" name="userId" value="<c:out value="${model.userId}" />" type="hidden">
												<div class="input-group-addon searchUserId">
													<i class="fa fa-search"></i>
												</div>
											</div>
										</div>
									</div>
									<sec:authorize  access="hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)">
										<div class="form-group">
		       								<label class="col-sm-2 control-label">Checker</label>
		       								<div class="col-sm-4">
		       									<div class="input-group">
													<input name="checkerName" type="text" class="form-control" value="<c:out value="${model.checkerNameDisplay}" />" readonly required/>
													<input id="checkerId" name="checkerId" value="<c:out value="${model.checkerId}" />" type="hidden">
													<div class="input-group-addon searchCheckerId">
														<i class="fa fa-search"></i>
													</div>
												</div>
											</div>
										</div>
									</sec:authorize>
									<div class="form-group">
	       								<label class="col-sm-2 control-label">Visit Date</label>
	       								<div class="col-sm-4">
	       									<div class="input-group">
												<input id="visitDate" name="visitDate" type="text" class="form-control date-picker" value="<c:out value="${model.visitDate}" />" maxlength="10" required/>
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
									</div>
				 					<div class="form-group radio-group">
									<label for="qcType" class="col-sm-2 control-label" style="color:#333">QC Type</label>
									<div class="col-sm-2">
										<label class="radio-inline"> 
											<input type="radio" name="qcType" value="1" <c:if test="${model.qcType == '1'}">checked</c:if> required> Spot Check
										</label>
									</div>
									<div class="col-sm-2">
										<label class="radio-inline">
											<input type="radio" name="qcType" value="2" <c:if test="${model.qcType == '2'}">checked</c:if> > Supervisory Visit
										</label>
									</div>
								</div>
								</div>
							<!-- /.box-body -->
							<c:if test="${not isReadOnly}">
								<div class="box-footer">
										<button type="submit" class="btn btn-info">Submit</button>							
								</div>
							</c:if>
						</div>
				</div>
			</div>

					<!-- /.box-footer -->

					</form>
		</section>
		
	</jsp:body>
</t:layout>
