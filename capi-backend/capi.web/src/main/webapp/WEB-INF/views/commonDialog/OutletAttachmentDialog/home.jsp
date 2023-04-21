<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="modal fade" role="dialog">
	<div class="modal-dialog modal-xlg">
		<div class="modal-content">
			<form action="" method="post" role="form"
				enctype="multipart/form-data">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">X</span></button>
				<h4 class="modal-title">Attachment Lookup Dialog</h4>
			</div>
			<div class="modal-body">
				<!-- content -->
						<input name="id" type="hidden" value="<c:out value="${outletId}"/>"/>
						<input name="redirectUrl" type="hidden" />
						<div class="container-fluid">
							<div class="row">
								<div class="col-md-12">
									<div class="row attachment-container"></div>
									<script class="attachment-template" type="text/html">
									{{#each attachments}}
									<div class="col-sm-3 attachment">
										{{#if this.isDummy}}
										<img class="img-responsive" src="<c:url value='/resources/images/dummyphoto.png'/>"/>
											{{#unless this.readonly}}
											<input name="newAttachment{{this.id}}" type="file" style="width:100%" />
											{{/unless}}
										{{else}}
										<img class="img-responsive" id="img{{this.id}}" src="<c:url value='/commonDialog/OutletAttachmentDialog/getAttachment'/>?id={{this.id}}"/>
											{{#unless this.readonly}}
											<input name="file{{this.id}}" class="fileImg" type="file" style="width:100%" />
											<button style="width:40%" type="button" id="deleteImage{{this.id}}" class="deleteImage" value="{{this.id}}"><span>Remove</span></button>
											{{/unless}}
										{{/if}}
									</div>
									{{#moduloIf @index 4}}
									<div class="clearfix"></div>
									{{/moduloIf}}
									{{/each}}
									</script>
								</div>
							</div>
						</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
				<button type="submit" class="btn btn-primary modal-submit">Submit</button>
			</div>
			</form>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>