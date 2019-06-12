<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<h1>Views</h1>
<c:choose>
	<c:when test="${inclnoncat eq 'true'}"><c:set var="nextinclnoncat" value="" /></c:when>
	<c:otherwise><c:set var="nextinclnoncat" value="true" /></c:otherwise>
</c:choose>
<ul>
	<li><a href="requeststats<c:if test="${not empty inclnoncat}">?inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Request Stats</a></li>
	<li><a href="sessionlist<c:if test="${not empty inclnoncat}">?inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Session List</a></li>
</ul>

