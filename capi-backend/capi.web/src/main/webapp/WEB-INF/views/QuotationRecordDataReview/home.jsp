<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/datepicker-css.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2-css.jsp"%>
	</jsp:attribute>
	<jsp:attribute name="header">
		<%@include file="/WEB-INF/views/includes/datepicker.jsp"%>
		<%@include file="/WEB-INF/views/includes/select2.jsp"%>
		<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
		<script>
		$(document).ready(function(){
			Datepicker($(".date-picker:not([readonly])"));
			$("#mainForm").validate();
			$('[name="referenceMonthStr"]').on("change", function(){
				$.ajax({
					type: 'POST',
					url: "<c:url value='/QuotationRecordDataReview/getSurveyMonthDetails'/>",
					data: {refMonthStr: $(this).val()},
					async:false,
					success: function(partialView){
						$("#referenceMonthDetails").html(partialView);
					}
				});
			})
			
		})
		</script>
	</jsp:attribute>
	<jsp:body>
		<section class="content-header">
          <h1>Quotation Record Data Review</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<form class="form-horizontal" action="<c:url value='/QuotationRecordDataReview/selectSurveyType'/>"  role="form" id="mainForm">
						<div class="box" >
							<div class="box-header with-border">
								<h4>Purpose Selection </h4>
							</div>
							<div class="box-body">
								<div class="row">
									<div class="col-md-12" >
										<div class="form-group">
											<label for="" class="col-sm-2 control-label">Reference Month</label>
											<div class="col-sm-4">
												<div class="input-group">
													<input type="text" class="form-control date-picker" data-orientation="top" data-date-minviewmode="months" data-date-format="mm-yyyy"
														name="referenceMonthStr" required value="" />
													<div class="input-group-addon">
														<i class="fa fa-calendar"></i>
													</div>
												</div>
											</div>
										</div>
										<div id="referenceMonthDetails">
										
										</div>
									</div>
								</div>
							</div>
							<div class="box-footer with-border">
								<button type="submit" class="btn btn-info">Submit</button>
							</div>
						</div>	
					</form>
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>