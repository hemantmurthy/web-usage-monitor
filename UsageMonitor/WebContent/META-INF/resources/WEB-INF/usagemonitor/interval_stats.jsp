<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Interval Statistics</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<div id="page">
	<div id="header">
		<h1>Usage Monitor</h1>
	</div>
	<div id="nav">
		<jsp:include page="navigation.jsp"/>
		<div class="minor">
		<h1>Intervals</h1>
		<ul>
			<li class="special"><a href="datestats?date=<c:out value="${requestScope['date']}"/>"><c:out value="${requestScope['dateFormatted']}"/></a></li>
		<c:forEach var="interval" items="${IN_MEMORY_RECORDER_DATE_INTERVAL_STATS}">
			<li <c:if test="${interval.value eq param['int']}">class="selected"</c:if>><a href="intervalstats?int=<c:out value="${interval.value}"/>"><c:out value="${interval.key}"/></a></li>
		</c:forEach>
		</ul>
		</div>
	</div>
	<div id="content">
		<h1>Request Statistics for Interval</h1>
		<p>The table below lists all request categories for interval starting at <b><c:out value="${requestScope['intervalFormatted'] }"/></b>.
		</p>
		<jsp:include page="category_stats_table.jsp">
			<jsp:param name="pageurl" value="intervalstats?int=${param['int']}&" />
		</jsp:include>
	</div>	
</div>
</body>
</html>