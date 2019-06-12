package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hamy.usagemonitor.recorder.InMemoryRecorder;
import com.hamy.usagemonitor.recorder.InMemoryRecorderBase.CategoryStatistics;

public class InMemoryRecorderIntervalStatsServlet
extends InMemoryRecorderBaseServlet {
	private static final long serialVersionUID = 7577636063307067786L;
	
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
		
		String intervalStr = (String)request.getParameter("int");
		if(intervalStr == null || "".equals(intervalStr)) return;
		
		LocalDateTime interval = null;
		LocalDate date = null;
		try {
			interval = LocalDateTime.parse(intervalStr);
			date = interval.toLocalDate();
			request.setAttribute("date", date.toString());
			request.setAttribute("dateFormatted", formatDate(date));
		} catch(DateTimeParseException e) {
			return;
		}
		
		InMemoryRecorder recorder = (InMemoryRecorder) o;
		Map<String, CategoryStatistics> intervalStats = getIntervalStats(recorder, interval);
		request.setAttribute("IN_MEMORY_RECORDER_SORTED_STATS", 
				intervalStats == null ? null : sortCategoryStats(intervalStats, request));

		request.setAttribute("intervalFormatted", formatDateTime(interval, recorder));
		
		Map<String, LocalDateTime> intervalLabels = getIntervalLabels(recorder, date, 0);
		request.setAttribute("IN_MEMORY_RECORDER_DATE_INTERVAL_STATS", intervalLabels);
		
		servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
	}
	
	private Map<String, CategoryStatistics> getIntervalStats(InMemoryRecorder recorder, LocalDateTime interval) {
		
		Map<LocalDateTime, Map<String, CategoryStatistics>> stats = recorder.getIntervalStats();
		if(stats == null) return null;
		
		return stats.get(interval);
	}
}
