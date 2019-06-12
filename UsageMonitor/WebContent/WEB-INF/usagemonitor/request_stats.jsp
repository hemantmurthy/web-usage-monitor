<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Request Statistics</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<div id="page">
	<jsp:include page="header.jsp"/>
	<div id="nav">
		<jsp:include page="navigation.jsp"/>
		<div class="minor">
		<h1>Dates</h1>
		<ul>
		<c:choose>
			<c:when test="${inclnoncat eq 'true'}"><c:set var="nextinclnoncat" value="" /></c:when>
			<c:otherwise><c:set var="nextinclnoncat" value="true" /></c:otherwise>
		</c:choose>
		<c:forEach var="interval" items="${IN_MEMORY_RECORDER_DATE_STATS}">
			<li><a href="datestats?date=<c:out value="${interval.value}"/><c:if test="${not empty inclnoncat}">&inclnoncat=<c:out value="${inclnoncat}"/></c:if>"><c:out value="${interval.key}"/></a></li>
		</c:forEach>
		</ul>
		</div>
	</div>
	<div id="content">
		<h1>Request Statistics</h1>
		<p>The table below lists all request categories that are being tracked by this usage monitor.
		The statistics shown below are from the time this server was started. </p>
		
		<jsp:include page="category_stats_table.jsp">
			<jsp:param name="pageurl" value="requeststats?"/>
		</jsp:include>
	</div>	
</div>

</body>
</html>