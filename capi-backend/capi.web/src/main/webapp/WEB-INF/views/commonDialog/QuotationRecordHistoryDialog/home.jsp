<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-xlg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Quotation Record History Dialog</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
				<!--  User Request hide the data
				<div class="row" style="margin-top: 10px;">
					<div class="col-md-4">
						<span>
							Max S Price in the last 2 year: <c:if test="${statModel.getLast2Year().getMaxSPrice() == null}">-</c:if><c:if test="${statModel.getLast2Year().getMaxSPrice() != null}">${statModel.getLast2Year().getMaxSPrice()}</c:if><br/>
							Min S Price in the last 2 year: <c:if test="${statModel.getLast2Year().getMinSPrice() == null}">-</c:if><c:if test="${statModel.getLast2Year().getMinSPrice() != null}">${statModel.getLast2Year().getMinSPrice()}</c:if><br/>
							Last Month Average S Price of Variety: <c:if test="${statModel.getPreviousStatistic().getAverageCurrentSPrice() == null}">-</c:if><c:if test="${statModel.getPreviousStatistic().getAverageCurrentSPrice() != null}">${statModel.getPreviousStatistic().getAverageCurrentSPrice()}</c:if><br/>
							
						</span>
					</div>
					<div class="col-md-4">
						<span>
							Median S Price in Current Month of Variety: <c:if test="${statModel.getCurrentStatistic().getMedianSPrice() == null}">-</c:if><c:if test="${statModel.getCurrentStatistic().getMedianSPrice() != null}">${statModel.getCurrentStatistic().getMedianSPrice()}</c:if><br/>
							Average PR in Current Month of Variety: <c:if test="${statModel.getCurrentStatistic().getAveragePRSPrice() == null}">-</c:if><c:if test="${statModel.getCurrentStatistic().getAveragePRSPrice() != null}">${statModel.getCurrentStatistic().getAveragePRSPrice()}</c:if><br/>
							Standard Deviation PR in Current Month of Variety: <c:if test="${statModel.getCurrentStatistic().getStandardDeviationPRSPrice() == null}">-</c:if><c:if test="${statModel.getCurrentStatistic().getStandardDeviationPRSPrice() != null}">${statModel.getCurrentStatistic().getStandardDeviationPRSPrice()}</c:if><br/>
							Count of Quotation Records of Variety: <c:if test="${statModel.getCurrentCount()== null}">-</c:if><c:if test="${statModel.getCurrentCount() != null}">${statModel.getCurrentCount()}</c:if><br/>
						</span>
					</div>
					<div class="col-md-4">
						<span>
							Median S Price in Previous Month of Variety: <c:if test="${statModel.getPreviousStatistic().getMedianSPrice() == null}">-</c:if><c:if test="${statModel.getPreviousStatistic().getMedianSPrice() != null}">${statModel.getPreviousStatistic().getMedianSPrice()}</c:if><br/>
							Average PR in Previous Month of Variety: <c:if test="${statModel.getPreviousStatistic().getAveragePRSPrice() == null}">-</c:if><c:if test="${statModel.getPreviousStatistic().getAveragePRSPrice() != null}">${statModel.getPreviousStatistic().getAveragePRSPrice()}</c:if><br/>
							Standard Deviation PR in Previous Month of Variety: <c:if test="${statModel.getPreviousStatistic().getStandardDeviationPRSPrice() == null}">-</c:if><c:if test="${statModel.getPreviousStatistic().getStandardDeviationPRSPrice() != null}">${statModel.getPreviousStatistic().getStandardDeviationPRSPrice()}</c:if><br/>
							Count of Quotation Records of Variety: <c:if test="${statModel.getPreviousCount()== null}">-</c:if><c:if test="${statModel.getPreviousCount() != null}">${statModel.getPreviousCount()}</c:if><br/>
						</span>
					</div>
				</div>-->
				<table class="table table-striped table-bordered table-hover datatable" style="margin-top: 10px;">
					<thead>
					<tr>
						<th class="text-center action select-multiple" rowspan="2" ></th>
						<th rowspan="2">Submission Date (DD/MM/YYYY)</th>
						<th rowspan="2">Reference Month</th>
						<th colspan="6">Collected Data</th>
						<th colspan="5">Editor Data</th>
						<th colspan="3">Statistics</th>
					</tr>
					<tr>
						<th>Collected N Price</th>
						<th>Collected S Price</th>
						<th>Special Price</th>
						<th>Discounts</th>
						<th>Availability</th>
						<th>FR</th>
						<th>Previous N Price</th>
						<th>Previous S Price</th>
						<th>Current N Price</th>
						<th>Current S Price</th>
						<th>Remark / Flagged</th>
						<th>Max</th>
						<th>Min</th>
						<th>Average</th>
					</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>