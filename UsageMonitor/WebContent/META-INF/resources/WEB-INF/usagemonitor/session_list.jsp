<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Session List</title>
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
		<h1>Session List</h1>
		<p>The table below lists all sessions currently active on this server.</p>
		<table>
			<thead>
				<tr>
					<th>Session ID</th>
					<th>User Name</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="rec" value="${applicationScope['com.hamy.usagemonitor.InMemorySessionRecorder']}"></c:set>
				<c:forEach var="ss" items="${rec.sessionStats }">
				<tr>
					<td><c:out value="${ss.key}"/></td>
					<td><c:out value="${ss.value.userName}"/></td>
					<td><a href="sessionstats?sessionId=<c:out value="${ss.key}"/>">Session Requests</a></td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>	
</div>
</body>
</html>