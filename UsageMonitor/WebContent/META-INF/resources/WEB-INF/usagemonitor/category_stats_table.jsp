<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="um" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:choose>
	<c:when test="${ordby eq 'category'}">
	<c:set var="countord" value="desc"/>
	<c:set var="mindurord" value="desc"/>
	<c:set var="maxdurord" value="desc"/>
	<c:set var="avgdurord" value="desc"/>
	<c:choose>
		<c:when test="${ord eq 'asc'}">
		<c:set var="categoryord" value="desc"/>
		</c:when>
		<c:otherwise><c:set var="categoryord" value="asc"/></c:otherwise>
	</c:choose>
	</c:when>
	<c:when test="${ordby eq 'count'}">
	<c:set var="categoryord" value="asc"/>
	<c:set var="mindurord" value="desc"/>
	<c:set var="maxdurord" value="desc"/>
	<c:set var="avgdurord" value="desc"/>
	<c:choose>
		<c:when test="${ord eq 'desc'}">
		<c:set var="countord" value="asc"/>
		</c:when>
		<c:otherwise><c:set var="countord" value="desc"/></c:otherwise>
	</c:choose>
	</c:when>
	<c:when test="${ordby eq 'mindur'}">
	<c:set var="categoryord" value="asc"/>
	<c:set var="countord" value="desc"/>
	<c:set var="maxdurord" value="desc"/>
	<c:set var="avgdurord" value="desc"/>
	<c:choose>
		<c:when test="${ord eq 'desc'}">
		<c:set var="mindurord" value="asc"/>
		</c:when>
		<c:otherwise><c:set var="mindurord" value="desc"/></c:otherwise>
	</c:choose>
	</c:when>
	<c:when test="${ordby eq 'maxdur'}">
	<c:set var="categoryord" value="asc"/>
	<c:set var="countord" value="desc"/>
	<c:set var="mindurord" value="desc"/>
	<c:set var="avgdurord" value="desc"/>
	<c:choose>
		<c:when test="${ord eq 'desc'}">
		<c:set var="maxdurord" value="asc"/>
		</c:when>
		<c:otherwise><c:set var="maxdurord" value="desc"/></c:otherwise>
	</c:choose>
	</c:when>
	<c:when test="${ordby eq 'avgdur'}">
	<c:set var="categoryord" value="asc"/>
	<c:set var="countord" value="desc"/>
	<c:set var="mindurord" value="desc"/>
	<c:set var="maxdurord" value="desc"/>
	<c:choose>
		<c:when test="${ord eq 'desc'}">
		<c:set var="avgdurord" value="asc"/>
		</c:when>
		<c:otherwise><c:set var="avgdurord" value="desc"/></c:otherwise>
	</c:choose>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${inclnoncat eq 'true'}"><c:set var="nextinclnoncat" value="" /></c:when>
	<c:otherwise><c:set var="nextinclnoncat" value="true" /></c:otherwise>
</c:choose>
<a href="<c:out value="${param['pageurl']}"/>ordby=<c:out value="${ordby}"/>&ord=<c:out value="${ord}"/><c:if test="${not empty nextinclnoncat}">&inclnoncat=<c:out value="${nextinclnoncat}"/></c:if>">
<c:choose>
	<c:when test="${nextinclnoncat eq 'true'}">Show Non-Categorized Requests</c:when>
	<c:otherwise>Hide Non-Categorized Requests</c:otherwise>
</c:choose>
</a>
<table id="stats_table">
	<colgroup>
		<col class="col_category">
		<col class="col_description">
		<col class="col_count">
		<col class="col_dur_min">
		<col class="col_dur_max">
		<col class="col_dur_avg"> 
	</colgroup>
	<thead>
		<tr>
			<th rowspan="2"><a href="<c:out value="${param['pageurl']}"/>ordby=category&ord=<c:out value="${categoryord}"/><c:if test="${not empty inclnoncat}">&inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Category</a></th>
			<th rowspan="2">Description</th>
			<th rowspan="2"><a href="<c:out value="${param['pageurl']}"/>ordby=count&ord=<c:out value="${countord}"/><c:if test="${not empty inclnoncat}">&inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Count</a></th>
			<th colspan="3">Request Response Duration</th>
		</tr>
		<tr>
			<th><a href="<c:out value="${param['pageurl']}"/>ordby=mindur&ord=<c:out value="${mindurord}"/><c:if test="${not empty inclnoncat}">&inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Min.</a></th>
			<th><a href="<c:out value="${param['pageurl']}"/>ordby=maxdur&ord=<c:out value="${maxdurord}"/><c:if test="${not empty inclnoncat}">&inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Max.</a></th>
			<th><a href="<c:out value="${param['pageurl']}"/>ordby=avgdur&ord=<c:out value="${avgdurord}"/><c:if test="${not empty inclnoncat}">&inclnoncat=<c:out value="${inclnoncat}"/></c:if>">Avg.</a></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="s" items="${IN_MEMORY_RECORDER_SORTED_STATS}">
		<tr>
			<td><c:out value="${s.value.category}"/></td>
			<td><c:out value="${s.value.description}"/></td>
			<td><c:out value="${s.value.count}"/></td>
			<td><um:duration value="${s.value.minDuration}"/>
<!-- 			<c:choose>
			<c:when test="${s.value.minDuration < 1000}">
			<fmt:formatNumber value="${s.value.minDuration}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> ms
			</c:when>
			<c:otherwise>
			<fmt:formatNumber value="${s.value.minDuration / 1000}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> s
			</c:otherwise>
			</c:choose>-->
			</td>
			<td><um:duration value="${s.value.maxDuration}"/>
<!-- 			<c:choose>
			<c:when test="${s.value.maxDuration < 1000}">
			<fmt:formatNumber value="${s.value.maxDuration}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> ms
			</c:when>
			<c:otherwise>
			<fmt:formatNumber value="${s.value.maxDuration / 1000}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> s
			</c:otherwise>
			</c:choose> -->
			</td>
			<td><um:duration value="${s.value.avgDuration}"/>
<!-- 			<c:choose>
			<c:when test="${s.value.avgDuration < 1000}">
			<fmt:formatNumber value="${s.value.avgDuration}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> ms
			</c:when>
			<c:otherwise>
			<fmt:formatNumber value="${s.value.avgDuration / 1000}" type="number" maxFractionDigits="1" minFractionDigits="1" groupingUsed="false"/> s
			</c:otherwise>
			</c:choose> -->
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
