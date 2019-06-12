<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Application Page - Default</title>
</head>
<body>
	<table>
		<thead>
			<tr>
				<th>Property</th>
				<th>Value</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>User</td><td><c:out value="${pageContext.request.remoteUser}"/></td>
			</tr>
			<tr>
				<td>Request URI</td><td><c:out value="${pageContext.request.requestURI}"/></td>
			</tr>
			<tr><td colspan="2">Request Parameters</td></tr>
			<c:forEach var="p" items="${pageContext.request.parameterMap}">
			<c:forEach var="pv" items="${p.value}">
			<tr>
				<td><c:out value="${p.key}"/></td><td><c:out value="${pv}"/></td>
			</tr>
			</c:forEach>
			</c:forEach>
			
		</tbody>
	</table>
	
	<a href="portal">/portal</a><br/>
	<a href="portal?abc=1234">/portal?abc=1234</a><br/>
	<a href="portal?abc=1234&pqr=9876">/portal?abc=1234&pqr=9876</a><br/>
	<a href="portal?xyz=112233">/portal?xyz=112233</a><br/>
	<a href="somepage?xyz=112233">/somepage?xyz=112233</a><br/>
	<a href="zonemap?ZONE_CD=D1-SPDISP&PORTAL_CD=D1SP">/zonemap?ZONE_CD=D1-SPDISP&PORTAL_CD=D1SP</a><br/>
	<a href="zonemap?ZONE_CD=SOMEOTHERZONE&PORTAL_CD=D1SP">/zonemap?ZONE_CD=SOMEOTHERZONE&PORTAL_CD=D1SP</a><br/>
	<a href="zonemap?ZONE_CD=ANOTHERZONE&PORTAL_CD=SOMEOTHERPORTAL">/zonemap?ZONE_CD=ANOTHERZONE&PORTAL_CD=SOMEOTHERPORTAL</a><br/>
	<a href="zonemap?TYPE=XYZ&ZONE_CD=PQR">/zonemap?TYPE=XYZ&ZONE_CD=PQR</a><br/>
	<br/>
	<a href="usagemonitor/requeststats" target="_blank" >Request Stats</a>
	<br/><br/>
	<a href="logout">Logout</a>
	
</body>
</html>