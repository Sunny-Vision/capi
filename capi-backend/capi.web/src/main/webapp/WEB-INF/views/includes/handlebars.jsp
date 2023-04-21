<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script src="<c:url value='/resources/js/handlebars-v4.0.2.js'/>"></script>
<script>
Handlebars.registerHelper("moduloIf", function(index_count,mod,block) {
	if((parseInt(index_count) + 1)%(mod)=== 0) {
		return block.fn(this);
	}
});
Handlebars.registerHelper('if_eq', function(a, b, opts) {
    if(a == b)
        return opts.fn(this);
    else
        return opts.inverse(this);
});
</script>