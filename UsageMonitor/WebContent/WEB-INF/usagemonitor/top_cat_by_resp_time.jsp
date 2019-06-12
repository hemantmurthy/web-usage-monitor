<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<h2>Top Categories by Response Time</h2>
<div>
<canvas id="top_cat_by_resp_time" width="400" height="150"></canvas>
</div>
<script type="text/javascript">
	var ctx = document.getElementById('top_cat_by_resp_time').getContext('2d');
	var myChart = new Chart(ctx, {
	    type: 'horizontalBar',
	    data: {
	    	labels: [
	        	<c:forEach items="${TOP_CATEGORIES_BY_AVG_DURATION}" var="c" varStatus="iter">
	        	<c:if test="${iter.count gt 1}">,</c:if>'<c:out value="${c.description}"/>'</c:forEach>
	    		],
	    	datasets: [
	    		{
	    			label: 'Avg.',
	            	data: [
	        		<c:forEach items="${TOP_CATEGORIES_BY_AVG_DURATION}" var="c" varStatus="iter">
	        		<c:if test="${iter.count gt 1}">,</c:if><c:out value="${c.avgDuration}"/></c:forEach>
        	    	],
	    	    	backgroundColor: '#add6cb'
    	    	},
    	    	{
    	    		label: 'Max.',
	            	data: [
	        		<c:forEach items="${TOP_CATEGORIES_BY_AVG_DURATION}" var="c" varStatus="iter">
	        		<c:if test="${iter.count gt 1}">,</c:if><c:out value="${c.maxDuration}"/></c:forEach>
        	    	],
	    	    	backgroundColor: '#a5c0e0'
    	    	}
    	    	]
	    },
	    options: {
	    	legend: {
	    		display: true,
	    		position: 'bottom'
	    	},
	        scales: {
	        	xAxes: [{
	        		ticks: {
		        		beginAtZero: true
	        		},
	                scaleLabel: {
	                	display: true,
	                	labelString: 'Duration in milliseconds'
	                }
	        	}
	        	],
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                }
	            }]
	        }
	    }
    });
</script>