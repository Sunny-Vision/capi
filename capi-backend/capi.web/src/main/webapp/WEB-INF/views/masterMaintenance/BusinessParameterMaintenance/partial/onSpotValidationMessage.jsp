<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row">
	<form method="post" action="<c:url value='/masterMaintenance/BusinessParameterMaintenance/saveOnSpotValidate'/>" id="onSpotForm" >
	<div class="col-md-12">
		<div class="form-horizontal">
			<h4>On-Spot Validation Message</h4>
		<div class="form-group">
			<div class="col-sm-12">
				<div class="form-group">
					<label class="col-sm-4 control-label">Unit of measurement (UOM1) should be reported</label>
					<div class="col-sm-6">
						<input name="message1" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage1()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Unit of measurement (UOM2) >= 0</label>
					<div class="col-sm-6">
						<input name="message2" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage2()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Normal Price (N Price) > 0</label>
					<div class="col-sm-6">
						<input name="message3" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage3()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Special Price (S Price) > 0</label>
					<div class="col-sm-6">
						<input name="message4" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage4()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if percentage change of N Price > x %</label>
					<div class="col-sm-6">
						<input name="message5" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage5()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if percentage change of N Price < x %</label>
					<div class="col-sm-6">
						<input name="message6" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage6()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if percentage change of S Price > x %</label>
					<div class="col-sm-6">
						<input name="message7" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage7()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if percentage change of S Price < x %</label>
					<div class="col-sm-6">
						<input name="message8" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage8()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if S price is > max or < min S price of the same Variety in the last month</label>
					<div class="col-sm-6">
						<input name="message9" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage9()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if N price is > max or < min N price of the same Variety in the last month</label>
					<div class="col-sm-6">
						<input name="message10" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage10()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Normal price (N Price) >= Special Price (S Price)</label>
					<div class="col-sm-6">
						<input name="message11" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage11()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">If "Not suitable" is chosen for N and S price, remarks have to be provided.</label>
					<div class="col-sm-6">
						<input name="message12" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage12()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">If the quotation record is "Not available", no field except remarks should be filled.</label>
					<div class="col-sm-6">
						<input name="message13" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage13()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">If the pricing cycle of a product is longer than the specified product cycle for different Variety, reminder will be provided.</label>
					<div class="col-sm-6">
						<input name="message14" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage14()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if percentage change of S price exceed<br/>
the ranges of (mean + x / - y S.D.) in last z months</label>
					<div class="col-sm-6">
						<input name="message15" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage15()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if percentage change of N price exceed<br/>
the ranges of (mean + x / - y S.D.) in last z months</label>
					<div class="col-sm-6">
						<input name="message16" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage16()}" />"/>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if S price exceed the ranges of ( x - y )</label>
					<div class="col-sm-6">
						<input name="message17" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage17()}" />" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">Provide Reason if N price exceed the ranges of ( x - y )</label>
					<div class="col-sm-6">
						<input name="message18" type="text" class="form-control" required value="<c:out value="${displayModel.getOnSpotMessage18()}" />"/>
					</div>
				</div>
			</div>
		</div>
		</div>
		<sec:authorize access="hasPermission(#user, 256)">
		<hr/>
		<div class="form-group">
			<button type="submit" class="btn btn-info">Submit</button>
		</div>
		</sec:authorize>
	</div>
	</form>
</div>