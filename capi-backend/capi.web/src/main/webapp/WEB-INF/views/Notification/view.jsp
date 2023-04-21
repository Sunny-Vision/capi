<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<t:layout>
	<jsp:attribute name="headerCss">
		<%@include file="/WEB-INF/views/includes/jqueryDataTable-css.jsp" %>
	</jsp:attribute>
	<jsp:attribute name="header">
<%@include file="/WEB-INF/views/includes/jqueryValidate.jsp" %>
<%@include file="/WEB-INF/views/includes/jqueryDataTable.jsp" %>
<script src="<c:url value='/resources/js/Sortable.js'/>"></script>

		

</jsp:attribute>
	<jsp:body>
		<section class="content-header">
		<h1>Notification</h1>
		</section>
		<section class="content">
		<form class="form-horizontal" action="<c:url value='/masterMaintenance/ClosingDateMaintenance/save?act='/>${act}" method="post" role="form" id="mainForm">
		    <input name="notificationId" value="<c:out value="${model.notificationId}" />" type="hidden" />
		    <div class="row">
				<div class="col-md-12">
					<!-- content -->
						<div class="box box-primary">
							<div class="box-header with-border">
								<a class="btn btn-default" href="<c:url value='/Notification/home'/>">Back</a>
							</div>
							<!-- /.box-header -->
							
							<!-- form start -->
								<div class="box-body">
									<div class="mailbox-read-info">
						                <h3><c:if test="${model.flag}"><span class='glyphicon glyphicon-star' style='color:rgb(255, 204, 0)'>&nbsp;</span></c:if>${model.subject}
						                  <span class="mailbox-read-time pull-right">Date: ${createdDate}</span></h3>
						              </div>
									
	       								<div class="mailbox-read-message">
	       									<p >${fn:replace(model.content, newLineChar ,"<br/>")}</p>
										</div>
								</div>
							<!-- /.box-body -->
						</div>
				</div>
			</div>
					<!-- /.box-footer -->
					</form>
		</section>
	</jsp:body>
</t:layout>
