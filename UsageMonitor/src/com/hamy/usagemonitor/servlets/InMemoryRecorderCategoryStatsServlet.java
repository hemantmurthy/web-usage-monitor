package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.recorder.InMemoryRecorder;
import com.hamy.usagemonitor.recorder.InMemoryRecorderBase.CategoryStatistics;

public class InMemoryRecorderCategoryStatsServlet
extends InMemoryRecorderBaseServlet {
	private static final long serialVersionUID = -6363241717386512041L;
	private static Logger logger = Logger.getLogger(InMemoryRecorderCategoryStatsServlet.class);
	
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
		ServletContext servletContext = request.getServletContext();
		
		Object o = servletContext.getAttribute(InMemoryRecorder.getDefaultSevletContextAttributeName());
		if(o == null || !(o instanceof InMemoryRecorder)) return;
		
		String category = null;
		if((category = getCategory(request)) == null) return;
		
		InMemoryRecorder recorder = (InMemoryRecorder) o;
		
		servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
	}
	
	private String getCategory(HttpServletRequest request) {
		return (String) request.getParameter("cat");
	}

	private Map<LocalDateTime, ? extends CategoryStatistics> getCategoryStats(InMemoryRecorder recorder, String category) {
		Map<LocalDateTime, CategoryStatistics> categoryStats = new HashMap<LocalDateTime, CategoryStatistics>();
		Map<LocalDateTime, Map<String, CategoryStatistics>> intervalStats = recorder.getIntervalStats();
		for(LocalDateTime interval : intervalStats.keySet()) {
			Map<String, CategoryStatistics> intCatStats = intervalStats.get(interval);
			if(intCatStats != null) {
				intCatStats.get(category);
			}
		}
		
		return categoryStats;
	}

	
	public static class DateCategoryStatistics
	extends CategoryStatistics {
		protected DateCategoryStatistics(String category, String description, boolean fallbackCategory) {
			super(category, description, fallbackCategory);
		}		
		
		void mergeStats(CategoryStatistics stat) {
			super.mergeStat(stat);
		}
	}}
