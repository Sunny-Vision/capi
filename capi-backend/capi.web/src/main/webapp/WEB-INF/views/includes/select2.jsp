<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value='/resources/js/select2.full.js'/>" ></script>
<script src="<c:url value='/resources/js/select2ajax.js'/>" ></script>
<script>
$.fn.select2.defaults.set('width', '100%');
$.fn.select2.defaults.set('placeholder', '');
</script>
<style>
.select2-selection__clear {
  position: relative;
}
</style>