<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Application Usage monitor Dashboard</title>
<link rel="stylesheet" type="text/css" href="style.css">
<script type="text/javascript" src="Chart.bundle.min.js"></script>
</head>
<body>
<div id="page">
	<jsp:include page="header.jsp"/>
	<div id="nav">
		<jsp:include page="navigation.jsp"/>
	</div>
	<div id="content">
		<h1>Usage Monitor Dashboard</h1>
		<ul class="toolbar">
			<li <c:if test="${since eq 'hour'}">class="selected"</c:if>><a href="?since=hour">Hour</a></li>
			<li <c:if test="${since eq '4hours'}">class="selected"</c:if>><a href="?since=4hours">4 Hours</a></li>
			<li <c:if test="${since eq 'today'}">class="selected"</c:if>><a href="?since=today">Today</a></li>
			<li <c:if test="${since eq '24hours'}">class="selected"</c:if>><a href="?since=24hours">24 Hours</a></li>
			<li <c:if test="${since eq '7days'}">class="selected"</c:if>><a href="?since=7days">7 Days</a></li>
			<li <c:if test="${since eq 'startup'}">class="selected"</c:if>><a href="?since=startup">Since Startup</a></li>
		</ul>
		
		<jsp:include page="top_cat_by_count.jsp"/>
		<jsp:include page="top_cat_by_resp_time.jsp"/>
	</div>	
</div>

</body>
</html>