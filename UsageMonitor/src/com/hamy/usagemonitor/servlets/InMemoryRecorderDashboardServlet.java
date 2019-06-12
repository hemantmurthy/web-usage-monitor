package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hamy.usagemonitor.recorder.InMemoryRecorder;
import com.hamy.usagemonitor.recorder.InMemoryRecorderBase.CategoryStatistics;

public class InMemoryRecorderDashboardServlet
extends InMemoryRecorderBaseServlet {
	private static final long serialVersionUID = -2034776093760324513L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doRequest(request, response);
	}
	
	private void doRequest(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// Get In Memory Recorder from application context ...
		ServletContext servletContext = request.getServletContext();
		InMemoryRecorder recorder = getInMemoryRecorder(servletContext);
		if(recorder == null) return;
		
		// Read parameter. Default if not set or unrecognized ...
		String since = request.getParameter("since");
		since = since == null ? "" : since;
		switch(since.trim().toLowerCase()) {
		case "hour":
		case "4hours":
		case "24hours":
		case "today":
		case "7days":
		case "startup":
			break;
			
		default:
			since = "4hours";
			break;
		}
		
		LocalDateTime now = LocalDateTime.now();
		Map<String, ? extends CategoryStatistics> summarizedStats = null;
		
		switch(since.trim().toLowerCase()) {
		case "hour":
			summarizedStats = getSummarizedStats(recorder, now.minusHours(1), null);
			break;
		
		case "today":
			summarizedStats = getSummarizedStats(recorder, now.truncatedTo(ChronoUnit.DAYS), null);
			break;
			
		case "4hours":
			summarizedStats = getSummarizedStats(recorder, now.minusHours(4), null);
			break;
		
		case "24hours":
			summarizedStats = getSummarizedStats(recorder, now.minusHours(24), null);
			break;
			
		case "7days":
			summarizedStats = getSummarizedStats(recorder, now.minusDays(7), null);
			break;
			
		case "startup":
			summarizedStats = recorder.getGlobalCategoryStats();
			break;
		}
		
		request.setAttribute("since", since);
		request.setAttribute("TOP_CATEGORIES_BY_COUNT", 
				getTopCategoriesByCount(summarizedStats));
		request.setAttribute("TOP_CATEGORIES_BY_AVG_DURATION", 
				getTopCategoriesByAvgDuration(summarizedStats));
		
		servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
	}
	
	private List<CategoryStatistics> getTopCategoriesByCount(Map<String, ? extends CategoryStatistics> stats) {
		List<CategoryStatistics> topCats = new ArrayList<>();
		
		Map<String, ? extends CategoryStatistics> sortedStats = sortCategoryStats(stats, InMemoryRecorderBaseServlet.ORDER_BY_COUNT, InMemoryRecorderBaseServlet.ORDER_DESCENDING, true);
		int count = 0;
		for(Entry<String, ? extends CategoryStatistics> entry : sortedStats.entrySet()) {
			if(++count > 10) break;
			
			topCats.add(entry.getValue());
		}
		
		return topCats;
	}
	
	private List<CategoryStatistics> getTopCategoriesByAvgDuration(Map<String, ? extends CategoryStatistics> stats) {
		List<CategoryStatistics> topCats = new ArrayList<>();
		
		Map<String, ? extends CategoryStatistics> sortedStats = sortCategoryStats(stats, InMemoryRecorderBaseServlet.ORDER_BY_AVG_DURATION, InMemoryRecorderBaseServlet.ORDER_DESCENDING, true);
		int count = 0;
		for(Entry<String, ? extends CategoryStatistics> entry : sortedStats.entrySet()) {
			if(++count > 10) break;
			
			topCats.add(entry.getValue());
		}
		
		return topCats;
	}}
