<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<h2>Top Categories by Number of Requests</h2>
<div>
<canvas id="top_cat_by_count" width="400" height="150"></canvas>
</div>
<script type="text/javascript">
	var ctx = document.getElementById('top_cat_by_count').getContext('2d');
	var myChart = new Chart(ctx, {
	    type: 'horizontalBar',
	    data: {
	    	labels: [
	        	<c:forEach items="${TOP_CATEGORIES_BY_COUNT}" var="c" varStatus="iter">
	        	<c:if test="${iter.count gt 1}">,</c:if>'<c:out value="${c.description}"/>'</c:forEach>
	    		],
	    	datasets: [{
	            data: [
	        		<c:forEach items="${TOP_CATEGORIES_BY_COUNT}" var="c" varStatus="iter">
	        		<c:if test="${iter.count gt 1}">,</c:if><c:out value="${c.count}"/></c:forEach>
        	    	],
	    	    	backgroundColor: '#add6cb'
    	    	}],
	    },
	    options: {
	    	legend: {
	    		display: false
	    	},
	        scales: {
	        	xAxes: [{
	        		ticks: {
		        		beginAtZero: true
	        		},
	                scaleLabel: {
	                	display: true,
	                	labelString: '# of requests'
	                }
	        	}
	        	],
	            yAxes: [{
	                ticks: {
	                    
						callback: function(tick) {
							var characterLimit = 40;
							if ( tick.length >= characterLimit) {
								return tick.slice(0, tick.length).substring(0, characterLimit -1).trim() + '...';;
							} 
							return tick;
						}
					}
	            }]

	        },
	        onClick: (evt, item) => {
	        	window.location.href = "categoryStats?categoryCd=" + encodeURIComponent(item[0]['_model'].label);
	        },
			tooltips: {
				callbacks: {
					title: function(tooltipItems, data) {
						return data.labels[tooltipItems[0].index]
					}
				}
      		}	    
      	}
    });
    
    function myFunction() {
    	console.log(this);
    }
</script>