<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script src="<c:url value='/resources/js/jquery.validate.min.js'/>" ></script>
<script src="<c:url value='/resources/js/jquery.validate.methods.min.js'/>" ></script>
<script src="<c:url value='/resources/js/jquery.validate.custom.js'/>" ></script>
<script>
$.extend($.validator.messages, {
	required: "<spring:message code='E00010' />",
	number: "<spring:message code='E00071' />",
	digits: "<spring:message code='E00072' />",
	smaller_than_or_eq: "<spring:message code='E00123' />",
	not_smaller_than_or_eq: "<spring:message code='E00123' />"
});
</script>