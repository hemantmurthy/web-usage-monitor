<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Request Statistics</title>
<link rel="stylesheet" type="text/css" href="style.css">
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
</head>
<body>
<div id="page">
	<div id="header">
		<h1>Usage Monitor</h1>
	</div>
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
<script type="text/javascript">
google.charts.load('current', {packages: ['corechart', 'bar']});
google.charts.setOnLoadCallback(drawRequestChart);

function drawRequestChart() {
      var data = google.visualization.arrayToDataTable([
        ['City', '2010 Population', '2000 Population'],
        ['New York City, NY', 8175000, 8008000],
        ['Los Angeles, CA', 3792000, 3694000],
        ['Chicago, IL', 2695000, 2896000],
        ['Houston, TX', 2099000, 1953000],
        ['Philadelphia, PA', 1526000, 1517000]
      ]);

      var options = {
        title: 'Population of Largest U.S. Cities',
        chartArea: {width: '50%'},
        isStacked: true,
        hAxis: {
          title: 'Total Population',
          minValue: 0,
        },
        vAxis: {
          title: 'City'
        }
      };
      var chart = new google.visualization.BarChart(document.getElementById('chart_div'));
      chart.draw(data, options);
    }
 </script>
</body>
</html>