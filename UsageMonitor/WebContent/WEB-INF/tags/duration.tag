<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="value" required="true" type="java.lang.Double"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<c:choose>
	<c:when test="${value < 1000}">
	<fmt:formatNumber value="${value}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> ms
	</c:when>
	<c:otherwise>
	<fmt:formatNumber value="${value / 1000}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> s
	</c:otherwise>
</c:choose>
