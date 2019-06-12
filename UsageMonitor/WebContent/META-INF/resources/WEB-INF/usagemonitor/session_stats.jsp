<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Session Statistics</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<div id="page">
	<div id="header">
		<h1>Usage Monitor</h1>
	</div>
	<div id="nav">
		<jsp:include page="navigation.jsp"/>
	</div>
	<div id="content">
		<c:set var="rec" value="${applicationScope['com.hamy.usagemonitor.InMemorySessionRecorder']}"></c:set>
		<c:set var="sessstats" value="${rec.sessionStats[param.sessionId]}"></c:set>
		<h1>Session Statistics</h1>
		<p>The table below lists all requests of interest made for a session.</p>
		<div class="section">
			<div>
				<span class="fieldname">Session ID: </span><span class="fieldvalue"><c:out value="${param['sessionId'] }"/></span>
			</div>
			<div>
				<span class="fieldname">User Name: </span><span class="fieldvalue"><c:out value="${sessstats.userName }"/></span>
			</div>
			<div>
				<span class="fieldname">Time Created: </span><span class="fieldvalue"><c:out value="${sessstats.timeCreated }"/></span>
			</div>
			<div>
				<span class="fieldname">Last Accessed: </span><span class="fieldvalue"><c:out value="${sessstats.timeLastAccessed }"/></span>
			</div>
		</div>
		<jsp:include page="category_stats_table.jsp">
			<jsp:param name="pageurl" value="sessionstats?sessionId=${param['sessionId']}&" />
		</jsp:include>

	</div>	
</div>
</body>
</html>