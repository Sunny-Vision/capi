<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<t:layout>
	
	<jsp:body>
		<section class="content-header">
          <h1>Manual And User Guide Maintenance</h1>
        </section>
        
        <section class="content">
        	<div class="row">
				<div class="col-md-12" >
				<!-- content -->
					<div class="box" >
						<div class="box-body">
							<div class="clearfix">&nbsp;</div>
							<form class="form-horizontal" action="<c:url value='/ManualAndUserGuide/uploadWebHelp' />" method="post" enctype="multipart/form-data">
								<div class="form-group">
									<label class="col-md-2 control-label">Web online help</label>
									<div class="col-md-6">
										<input type="file" name="webHelp" />
									</div>
								</div>
								<sec:authorize access="hasPermission(#user, 256)">
									<div class="box-footer">	
										<button type="submit" class="btn btn-info">Submit</button>
										<button type="button" class="btn btn-info" onclick="window.location.href='<c:url value="/ManualAndUserGuide/exportWebHelp" />'">Export</button>
									</div>
								</sec:authorize>
							</form>
						</div>
					</div>
				</div>
			</div>
        </section>		
	</jsp:body>
</t:layout>