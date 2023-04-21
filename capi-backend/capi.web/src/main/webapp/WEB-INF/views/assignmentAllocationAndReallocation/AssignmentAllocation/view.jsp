<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout>
	<jsp:attribute name="header">
		<style media="print">
			@page  
			{ 
			    size:  21.0cm 29.7cm;    /* auto is the initial value */ 
			
			    /* this affects the margin in the printer settings */ 
			    margin: 0.5mm;  
			}
			
			img {
			    max-width: none !important;
			}
		  body{
		  	width:auto !important;
			margin:0 !important;				
			padding:0 !important;				
			float:none !important;
		  	font-size: 10px !important;
		  	
		  }
   		  table {
   		  	page-break-inside: avoid;
   		  	
   		  }
   		  .content-header{
   		  	display: block !important;
   		  }
   		  .box-header{
   		  	display: none;
   		  }
			hr{
				display: none;
			} 
			h3{
				font-size: 14px;
			}
			body{
				/* margin-left:-50px !important;  */
				margin-top: 20px !important;  
			} 
		  .printbreak {
			page-break-before: always;
		  }
		</style>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Assignment Allocation</h1>
        </section>
        
        <section class="content">
        	<div class="box" >
        		<div class="box-header with-border">
					<a class="btn btn-default" href="<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/home'/>">Back</a>
					<a class="btn btn-default pull-right" href="javascript:window.print()">Print</a>
				</div>
				<div class="box-body">
					<div class="row">
						<div class="col-md-12">
							<h3>District Head Adjustment</h3>
						</div>
						<div class="col-md-12" >
							<table class="table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th>Field Officer</th>
									<th>Responsible District</th>
									<th>Available Man-Day</th>
									<th>Man-Day Required for<br/>responsible district</th>
									<th>Manual Adjustment</th>
									<th>Adjusted Man-day Required for<br/>responsible district</th>
									<th>Man day of transfer<br/>in(+) / out (-)</th>
									<th>Man-day Balance</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${districtHeadList}" var="dh">
								<tr>
									<td class="text-center text-nowrap">
										<c:out value="${dh.getUserName()}"></c:out>
										<input type="hidden" name="districtHead[${dh.getUserId()}].userId" value="${dh.getUserId()}"/>
										<input type="hidden" name="districtHead[${dh.getUserId()}].userName" value="${dh.getUserName()}"/>
									</td>
									<td class="text-center">
										<c:out value="${dh.getDistricts()}"></c:out>
										<input type="hidden" name="districtHead[${dh.getUserId()}].districts" value="${dh.getDistricts()}"/>
									</td>
									<td class="text-center">
										<c:out value="${dh.getAvailableManDays()}"></c:out>
										<input class="availableManDays" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].availableManDays" value="${dh.getAvailableManDays()}"/>
									</td>
									<td class="text-center">
										<c:out value="${dh.getManDayRequiredForResponsibleDistricts()}"></c:out>
										<input data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayRequiredForResponsibleDistricts" value="${dh.getManDayRequiredForResponsibleDistricts()}"/>
										<input class="manDayRequired" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayRequired" value="${dh.getManDayRequired()}"/>
									</td>
									<td class="text-center">
										<c:out value="${dh.getManualAdjustment()}"></c:out>
										<input type="hidden" class="form-control manualAdjustment" name="districtHead[${dh.getUserId()}].manualAdjustment" value="<c:out value="${dh.getManualAdjustment()}"></c:out>">
									</td>
									<td class="text-center">
										<span class="adjustedManDay" data-districtHeadId="${dh.getUserId()}">${dh.getAdjustedManDayRequiredForResponsibleDistricts()}</span>
										<input class="adjustedManDay" data-user="${dh.getUserId()}" data-original="${dh.getManDayRequired()}" type="hidden" name="districtHead[${dh.getUserId()}].adjustedManDayRequiredForResponsibleDistricts" value="${dh.getAdjustedManDayRequiredForResponsibleDistricts()}"/>
									</td>
									<td class="text-center">
										<span class="manDayTransfer" data-districtHeadId="${dh.getUserId()}">${dh.getManDayOfTransferInOut()==0 ? 0.0 : dh.getManDayOfTransferInOut()*-1}</span>
										<input class="manDayTransfer" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayOfTransferInOut" value="${dh.getManDayOfTransferInOut()}"/>
									</td>
									<td class="text-center">
										<span class="manDayOfBalance" data-districtHeadId="${dh.getUserId()}">${dh.getManDayOfBalance()}</span>
										<input class="manDayOfBalance" data-user="${dh.getUserId()}" type="hidden" name="districtHead[${dh.getUserId()}].manDayOfBalance" value="${dh.getManDayOfBalance()}"/>
									</td>
								</tr>
								</c:forEach>
							</tbody>
							</table>
						</div>
					</div>
					<hr/>
					<div class="row">
						<div class="col-md-12 printbreak">
							<h3>Adjustment</h3>
						</div>
						<div class="col-md-12" >
							<table class="table table-striped table-bordered table-hover" id="adjustmentTable">
							<thead>
								<tr>
									<th>From Field Officer</th>
									<th>To Field Officer</th>
									<th>Man-Day</th>
									<th>Remark</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${adjustmentModelList}" var="adjustment" varStatus="loop">
								<tr>
									<td class='text-center'><c:out value="${adjustment.fromUserName}"/>
										<input type='hidden' class='adjustment fromUser' data-row='${loop.index}' name='adjustment[${loop.index}].fromUserId' value='${adjustment.fromUserId}'>
										<input type='hidden' name='adjustment[${loop.index}].fromUserName' value='${adjustment.fromUserName}'>
									</td>
									<td class='text-center'><c:out value="${adjustment.toUserName}"/>
										<input type='hidden' class='adjustment toUser' data-row='${loop.index}' name='adjustment[${loop.index}].toUserId' value='${adjustment.toUserId}'>
										<input type='hidden' name='adjustment[${loop.index}].toUserName' value='${adjustment.toUserName}'>
									</td>
									<td class='text-center'>${adjustment.manDay}<input type='hidden' class='adjustment manDay' data-row='${loop.index}' name='adjustment[${loop.index}].manDay' value='${adjustment.manDay}'></td>
									<td class='text-center'>${adjustment.remark}<input type='hidden' class='adjustment remark' name='adjustment[${loop.index}].remark' value='${adjustment.remark}'></td>
								</tr>
								</c:forEach>
							</tbody>
							</table>
						</div>
					</div>
			</div>
				
			</div>
		</section>
	</jsp:body>
</t:layout>