<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/bootstrap-modal-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup-css.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<%@include file="/WEB-INF/views/includes/bootstrap-modal.jsp" %>
		<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
		<%@include file="/WEB-INF/views/includes/select2.jsp" %>
		<%@include file="/WEB-INF/views/includes/jstree.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/productLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/userLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outletLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/quotationLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/unitLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/roleLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/systemFunctionLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/authorityLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentReallocationLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/outstandingAssignmentLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/peCertaintyCaseLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/assignmentLookupSurveyMonth.jsp" %>
		<%@include file="/WEB-INF/views/includes/commonDialog/quotationRecordReallocationLookup.jsp" %>
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/changeListener.jsp"%>
		<script>
			$(function() {
				Modals.init();
				$('.searchProductIds').productLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return this.$element.find('input').val();
					}
				});
				$('.searchProductId').productLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					}
				});
				$('.searchProductIdSpecificGroup').productLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					productGroupId: 2
				});
				$('.searchProductIdSpecificGroup2').productLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					productGroupId: 0,
					queryDataCallback: function(model) {
						model.productGroupId = this.$element.closest('.form-group').find('.specificGroup2').val();
						model.skipProductId = this.$element.closest('.form-group').find('.skipProductId').val();
					}
				});

				$('.searchUserId').userLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						this.$element.find('input').val(id + " - "
								+ singleRowData.staffCode
								+ "," + singleRowData.team
								+ "," + singleRowData.englishName
								+ "," + singleRowData.chineseName
								+ "," + singleRowData.destination);
						
					},
					queryDataCallback: function(model) {
						model.authorityLevel = 1;
					},
					multiple: false
				});
				$('.searchUserIds').userLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return this.$element.find('input').val();
					}
				});
				
				$('.searchUserTeamId').userLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					queryDataCallback: function(model) {
						model.teamOnly = true;
					},
					alreadySelectedIdsCallback: function() {
						return this.$element.find('input').val();
					},
					multiple: false
				});

				$('.searchOutletIds').outletLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return this.$element.find('input').val();
					}
				});

				$('.searchOutletId').outletLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false
				});

				$('.searchQuotationIds').quotationLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return this.$element.find('input').val();
					}
				});

				$('.searchQuotationId').quotationLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false
				});

				$('.searchCpiBasePeriods').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'CpiBasePeriod',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchCpiBasePeriod').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'CpiBasePeriod'
				});

				$('.searchSectionIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'Section',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchSectionId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'Section'
				});

				$('.searchGroupIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'Group',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchGroupId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'Group'
				});

				$('.searchSubGroupIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'SubGroup',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchSubGroupId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'SubGroup'
				});

				$('.searchItemIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'Item',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchItemId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'Item'
				});

				$('.searchOutletTypeIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'OutletType',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchOutletTypeId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'OutletType'
				});

				$('.searchSubItemIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					bottomEntityClass: 'SubItem',
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchSubItemId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false,
					bottomEntityClass: 'SubItem'
				});

				$('.searchUnitIds').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					queryDataCallback: function(data) {
						data.purposeIds = [1,2];
					}
				});

				$('.searchUnitId').unitLookup({
					selectedIdsCallback: function(selectedIds) {
						this.$element.find('input').val(selectedIds.join());
					},
					multiple: false
				});
				
				$('[name="roleId"],.searchRoleId').roleLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="roleId"]').val(id + " - "
								+ singleRowData.name
								+ "," + singleRowData.description);
						
					},
					multiple: false
				});
				
				$('[name="roleIds"],.searchRoleIds').roleLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="roleIds"]').val(selectedIds.join());
					}
				});
				
				$('[name="backendSystemFunctionId"],.searchBackendSystemFunctionId').systemFunctionLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="backendSystemFunctionId"]').val(id + " - "
								+ singleRowData.code
								+ "," + singleRowData.description);
						
					},
					queryDataCallback: function(model) {
						model.isMobile = false;
					},
					multiple: false
				});
				
				$('[name="backendSystemFunctionIds"],.searchBackendSystemFunctionIds').systemFunctionLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="backendSystemFunctionIds"]').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return $('[name="backendSystemFunctionIds"]').val();
					},
					queryDataCallback: function(model) {
						model.isMobile = false;
					}
				});
				
				$('[name="frontendSystemFunctionId"],.searchFrontendSystemFunctionId').systemFunctionLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="frontendSystemFunctionId"]').val(id + " - "
								+ singleRowData.code
								+ "," + singleRowData.description);
						
					},
					queryDataCallback: function(model) {
						model.isMobile = true;
					},
					multiple: false
				});
				
				$('[name="frontendSystemFunctionIds"],.searchFrontendSystemFunctionIds').systemFunctionLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="frontendSystemFunctionIds"]').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return $('[name="frontendSystemFunctionIds"]').val();
					},
					queryDataCallback: function(model) {
						model.isMobile = true;
					}
				});
				
				$('[name="authorityIds"],.searchAuthorityIds').authorityLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="authorityIds"]').val(selectedIds.join());
					},
					alreadySelectedIdsCallback: function() {
						return $('[name="authorityIds"]').val();
					}
				});
				
				$('[name="assignmentReallocationId"],.searchAssignmentReallocationId').assignmentReallocationLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="assignmentReallocationId"]').val(id);
						
					},
					multiple: false,
					queryDataCallback: function(model) {
						model.originalUserId = 5;
						model.assignmentIdList = "";
					}
				});
				$('[name="assignmentReallocationIds"],.searchAssignmentReallocationIds').assignmentReallocationLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="assignmentReallocationIds"]').val(selectedIds.join());
					},
					queryDataCallback: function(model) {
						model.originalUserId = 5;
						model.assignmentIdList = "";
					}
				});
				
				$('[name="outstandingAssignmentId"],.searchOutstandingAssignmentId').outstandingAssignmentLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="outstandingAssignmentId"]').val(id);
						
					},
					multiple: false,
					queryDataCallback: function(model) {
						model.assignmentIdList = "";
					}
				});
				$('[name="outstandingAssignmentIds"],.searchOutstandingAssignmentIds').outstandingAssignmentLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="outstandingAssignmentIds"]').val(selectedIds.join());
					},
					queryDataCallback: function(model) {
						model.assignmentIdList = "";
					}
				});
				
				$('[name="peCertaintyCaseId"],.searchPECertaintyCaseId').peCertaintyCaseLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="peCertaintyCaseId"]').val(id);
						
					},
					multiple: false,
					queryDataCallback: function(model) {
						model.surveyMonthId = 5;
						model.peCheckTaskIdList = "";
					}
				});
				$('[name="peCertaintyCaseIds"],.searchPECertaintyCaseIds').peCertaintyCaseLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="peCertaintyCaseIds"]').val(selectedIds.join());
					},
					queryDataCallback: function(model) {
						model.surveyMonthId = 5;
						model.peCheckTaskIdList = "";
					}
				});
				
				$('[name="assignmentSurveyMonthId"],.searchAssignmentSurveyMonthId').assignmentLookupSurveyMonth({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="assignmentSurveyMonthId"]').val(id);
						
					},
					multiple: false,
					queryDataCallback: function(model) {
						model.surveyMonthId = 5;
					}
				});
				
				$('[name="quotationRecordReallocationId"],.searchQuotationRecordReallocationId').quotationRecordReallocationLookup({
					selectedIdsCallback: function(selectedIds, singleRowData) {
						var id = selectedIds[0];
						$('[name="quotationRecordReallocationId"]').val(id);
						
					},
					multiple: false,
					queryDataCallback: function(model) {
						model.originalUserId = 5;
						model.quotationRecordIdList = "";
					}
				});
				$('[name="quotationRecordReallocationIds"],.searchQuotationRecordReallocationIds').quotationRecordReallocationLookup({
					selectedIdsCallback: function(selectedIds) {
						$('[name="quotationRecordReallocationIds"]').val(selectedIds.join());
					},
					queryDataCallback: function(model) {
						model.originalUserId = 5;
						model.quotationRecordIdList = "";
					}
				});
				
				$('#selectCustomValue').select2({
					tags: true,
					multiple: false
				});
				$('#btnSelectCustomValue').click(function() {
					alert($('#selectCustomValue').val());
				});
				
				/** multi-datepicker
				*	include select2, include datepicker
				*/
				// init select2, require attr: disabled
				$(".date-picker .select2").select2({closeOnSelect: false});
				/**
				* 	init datepicker, 
				*	REQUIRE attr:
				*	
				*	data-miltidate="true"
				*
				*	data-target pointing the select2 element
				*	eg: data-target="[name=dateDisplay]"
				*
				*	data-input pointing the inputbox for submiting data.
				*	eg: data-input="[name='date']" 
				*
				*	server recieve string of date list: dd-mm-yyyy,dd-mm-yyyy,dd-mm-yyyy
				*/
				Datepicker();
				
				$('.listen-change').changeListener({
					backButtons: ['.listen-change .btn-back'],
					changeCallback: function() {
						alert('changed');
					}
				});
				
				$('#btnBackButton').click(function(){
					if ($('.listen-change').changeListener('isChanged')) {
						alert('changed');
					} else {
						alert('no change');
					}
				});
				
				$('#btnOpenWindow').click(function(){
					var quotationRecordId = 33;
					window.open('<c:url value='/assignmentManagement/QuotationRecordPEEdit/home' />' + '?id=' + quotationRecordId);
				});
				$(document).on('returnPECheckRemark', function(e, peCheckRemark) {
					$('#peCheckRemark').text(peCheckRemark);
				});
			});
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Hello World</h1>
        </section>
        
        <section class="content">
        	<P>The time on the server is ${serverTime}.</P>
        	
        	<!-- check if user has permission 4  -->
        	<sec:authorize access="hasPermission(#user, 4)">
        		<div>Permission Message Example</div>
        	</sec:authorize>
        	
        	<!-- check if user has role UF1001 -->
        	<sec:authorize access="hasRole('UF1101')">
        		<div>Role Message Example</div>
        	</sec:authorize>
        	
        	<!-- get system message -->
        	<div><spring:message code="E00017"  /></div>
        	
        	<div><a href="<c:url value='/masterMaintenance/Test/redirectTest' />">Redirect</a></div>
        	
        	<div><a href="<c:url value='/masterMaintenance/Test/reportTest' />">Generate Report Real-time</a></div>
        	
        	
        	<div><a href="<c:url value='/masterMaintenance/Test/reportTestAsync' />">Generate Report Async</a></div>
        	        	
        	
        	<div class="row">
				<div class="col-md-12">
					<!-- content -->
				
					<!-- Itinerary Planning Table -->
					<div class="box box-primary">
						<!-- /.box-header -->
						<!-- form start -->
						<form class="form-horizontal" action="" method="post" role="form">
							<div class="box-body">
								<div class="form-group">
									<label for="inputEmail3" class="col-sm-2 control-label">Multiple Products</label>
									<div class="col-sm-6">
										<div class="input-group searchProductIds">
											<input type="text" name="productIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="inputEmail3" class="col-sm-2 control-label">Single Product</label>
									<div class="col-sm-6">
										<div class="input-group searchProductId" data-productlookup-multiple="false">
											<input type="text" name="productId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="inputEmail3" class="col-sm-2 control-label">Single Product Specific Product Type</label>
									<div class="col-sm-6">
										<div class="input-group searchProductIdSpecificGroup">
											<input type="text" name="productIdSpecificGroup" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Product Specific Product Type 2</label>
									<div class="col-sm-2">
										<select class="form-control specificGroup2">
											<option>1</option>
											<option>2</option>
											<option>3</option>
										</select>
									</div>
									<label class="col-sm-2 control-label">Skip Product ID</label>
									<div class="col-sm-2">
										<input class="form-control skipProductId" value="1"/>
									</div>
									<div class="col-sm-4">
										<div class="input-group searchProductIdSpecificGroup2">
											<input type="text" name="productIdSpecificGroup2" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single user</label>
									<div class="col-sm-6">
										<div class="input-group searchUserId">
											<input type="text" name="userId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Single user (team)</label>
									<div class="col-sm-6">
										<div class="input-group searchUserTeamId">
											<input type="text" name="userTeamId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple users</label>
									<div class="col-sm-6">
										<div class="input-group searchUserIds">
											<input type="text" name="userIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single outlet</label>
									<div class="col-sm-6">
										<div class="input-group searchOutletId">
											<input type="text" name="outletId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple outlets</label>
									<div class="col-sm-6">
										<div class="input-group searchOutletIds">
											<input type="text" name="outletIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single quotation</label>
									<div class="col-sm-6">
										<div class="input-group searchQuotationId">
											<input type="text" name="quotationId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple quotations</label>
									<div class="col-sm-6">
										<div class="input-group searchQuotationIds">
											<input type="text" name="quotationIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single CpiBasePeriod</label>
									<div class="col-sm-6">
										<div class="input-group searchCpiBasePeriod">
											<input type="text" name="itemId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple CpiBasePeriods</label>
									<div class="col-sm-6">
										<div class="input-group searchCpiBasePeriods">
											<input type="text" name="itemIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Section</label>
									<div class="col-sm-6">
										<div class="input-group searchSectionId">
											<input type="text" name="itemId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple Sections</label>
									<div class="col-sm-6">
										<div class="input-group searchSectionIds">
											<input type="text" name="itemIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Group</label>
									<div class="col-sm-6">
										<div class="input-group searchGroupId">
											<input type="text" name="itemId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple Groups</label>
									<div class="col-sm-6">
										<div class="input-group searchGroupIds">
											<input type="text" name="itemIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Sub Group</label>
									<div class="col-sm-6">
										<div class="input-group searchSubGroupId">
											<input type="text" name="itemId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple Sub Groups</label>
									<div class="col-sm-6">
										<div class="input-group searchSubGroupIds">
											<input type="text" name="itemIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single item</label>
									<div class="col-sm-6">
										<div class="input-group searchItemId">
											<input type="text" name="itemId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple items</label>
									<div class="col-sm-6">
										<div class="input-group searchItemIds">
											<input type="text" name="itemIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single outlet type</label>
									<div class="col-sm-6">
										<div class="input-group searchOutletTypeId">
											<input type="text" name="outletTypeId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple outlet types</label>
									<div class="col-sm-6">
										<div class="input-group searchOutletTypeIds">
											<input type="text" name="outletTypeIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single sub item</label>
									<div class="col-sm-6">
										<div class="input-group searchSubItemId">
											<input type="text" name="subItemId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple sub item</label>
									<div class="col-sm-6">
										<div class="input-group searchSubItemIds">
											<input type="text" name="subItemIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single unit</label>
									<div class="col-sm-6">
										<div class="input-group searchUnitId">
											<input type="text" name="unitId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Duplicate Single unit</label>
									<div class="col-sm-6">
										<div class="input-group searchUnitId">
											<input type="text" name="unitId" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple units</label>
									<div class="col-sm-6">
										<div class="input-group searchUnitIds">
											<input type="text" name="unitIds" class="form-control" readonly>
											<div class="input-group-addon">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single role</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="roleId" class="form-control" readonly>
											<div class="input-group-addon searchRoleId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple roles</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="roleIds" class="form-control" readonly>
											<div class="input-group-addon searchRoleIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single backend system function</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="backendSystemFunctionId" class="form-control" readonly>
											<div class="input-group-addon searchBackendSystemFunctionId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple backend system functions</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="backendSystemFunctionIds" class="form-control" readonly>
											<div class="input-group-addon searchBackendSystemFunctionIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single frontend system function</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="frontendSystemFunctionId" class="form-control" readonly>
											<div class="input-group-addon searchFrontendSystemFunctionId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple frontend system functions</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="frontendSystemFunctionIds" class="form-control" readonly>
											<div class="input-group-addon searchFrontendSystemFunctionIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple authority levels</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="authorityIds" class="form-control" readonly>
											<div class="input-group-addon searchAuthorityIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Assignment Reallocation</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="assignmentReallocationId" class="form-control" readonly>
											<div class="input-group-addon searchAssignmentReallocationId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple Assignment Reallocation</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="assignmentReallocationIds" class="form-control" readonly>
											<div class="input-group-addon searchAssignmentReallocationIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Outstanding Assignment</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="outstandingAssignmentId" class="form-control" readonly>
											<div class="input-group-addon searchOutstandingAssignmentId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple Outstanding Assignment</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="outstandingAssignmentIds" class="form-control" readonly>
											<div class="input-group-addon searchOutstandingAssignmentIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single PE Certainty Case</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="peCertaintyCaseId" class="form-control" readonly>
											<div class="input-group-addon searchPECertaintyCaseId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple PE Certainty Case</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="peCertaintyCaseIds" class="form-control" readonly>
											<div class="input-group-addon searchPECertaintyCaseIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Assignment (with Survey Month)</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="assignmentSurveyMonthId" class="form-control" readonly>
											<div class="input-group-addon searchAssignmentSurveyMonthId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Single Quotation Record Reallocation</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="quotationRecordReallocationId" class="form-control" readonly>
											<div class="input-group-addon searchQuotationRecordReallocationId">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label">Multiple Quotation Record Reallocation</label>
									<div class="col-sm-6">
										<div class="input-group">
											<input type="text" name="quotationRecordReallocationIds" class="form-control" readonly>
											<div class="input-group-addon searchQuotationRecordReallocationIds">
												<i class="fa fa-search"></i>
											</div>
										</div>
									</div>
								</div>
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Select custom value</label>
									<div class="col-sm-6">
										<select id="selectCustomValue" class="form-control">
											<option>Given Value 1</option>
											<option>Given Value 2</option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<div class="col-md-6">
				        				<label class="col-md-4 control-label">Multi Date Picker</label>
	       								<div class="col-md-8">
											<div class="input-group date date-picker" data-orientation="top" data-multidate="true"
												 data-target="[name=dateDisplay]" data-input="[name='date']" >
												<input type="text" class="form-control" style="display: none;" name="date" required />
		       									<select class="form-control select2" disabled name="dateDisplay" multiple style="display:none"></select>
												<div class="input-group-addon">
													<i class="fa fa-calendar"></i>
												</div>
											</div>
										</div>
				        			</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label"></label>
									<div class="col-sm-6">
										<button id="btnSelectCustomValue" type="button" class="btn btn-default">Submit</button>
									</div>
								</div>
								
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">changeListener</label>
									<div class="col-sm-6">
										<div class="listen-change">
											<input type="text"/><br/>
											<input type="checkbox"/><br/>
											<input name="radio1" type="radio"/><input name="radio1" type="radio" checked/><br/>
											<select><option>1</option><option>2</option></select>
											<a class="btn btn-default btn-back" href="http://www.google.com">back button</a>
											<button id="btnBackButton">Check change</button>
										</div>
									</div>
								</div>
								
								<hr/>
								<div class="form-group">
									<label class="col-sm-2 control-label">Tab data passing test</label>
									<div class="col-sm-6">
										<textarea id="peCheckRemark" type="text"></textarea><br/>
										<button id="btnOpenWindow">Open Window</button>
									</div>
								</div>
							</div>
							<!-- /.box-body -->
						</form>
					</div>
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>

