<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Usage Monitor Ping</title>
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
		<h1>Ping Successful</h1>
		<p>If you see this page, then Usage Monitor Web Components seem to have been deployed successfully.</p>
	</div>	
</div>
</body>
</html>