<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script src="<c:url value='/resources/js/datejs/date.js'/>"></script>


<script>
	function parseDate(dateStr){
		return Date.parseExact(dateStr, "dd-MM-yyyy");
	}
</script>