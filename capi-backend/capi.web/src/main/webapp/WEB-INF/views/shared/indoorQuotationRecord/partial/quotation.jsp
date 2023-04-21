<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
/**
 * Display outlet section
 * 
 * Controller permission required:
 * /shared/General/getOutletImage
 */
%>
<div class="box box-primary ">
	<div class="box-header with-border">
		<h3 class="box-title">
			Quotation
		</h3>
		<div class="box-tools pull-right">
			<button class="btn btn-box-tool btn-header-collapse" type="button" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
		</div>
	</div>
	<div class="box-body" >
		<%@include file="/WEB-INF/views/shared/indoorQuotationRecord/partial/quotationRecord/page.jsp" %>
	</div>
</div>