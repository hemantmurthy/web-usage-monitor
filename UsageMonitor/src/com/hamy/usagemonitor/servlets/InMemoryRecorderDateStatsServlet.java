package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.recorder.InMemoryRecorder;
import com.hamy.usagemonitor.recorder.InMemoryRecorderBase.CategoryStatistics;

public class InMemoryRecorderDateStatsServlet
extends InMemoryRecorderBaseServlet {
	private static Logger logger = Logger.getLogger(InMemoryRecorderDateStatsServlet.class);
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
		
		InMemoryRecorder recorder = getInMemoryRecorder(servletContext);
		if(recorder == null) return;
		
		LocalDate date = null;
		if((date = getDate(request)) == null) return;
		request.setAttribute("dateFormatted", formatDate(date));
		
		request.setAttribute("IN_MEMORY_RECORDER_SORTED_STATS", 
				sortCategoryStats(getDateStats(recorder, date), request));
		
		Map<String, LocalDateTime> intervalLabels = getIntervalLabels(recorder, date, 0);
		request.setAttribute("IN_MEMORY_RECORDER_DATE_INTERVAL_STATS", intervalLabels);
		
		servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
	}
	
	private LocalDate getDate(HttpServletRequest request) {
		String dateStr = (String)request.getParameter("date");
		if(dateStr == null || "".equals(dateStr)) return null;
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try {
			return LocalDate.parse(dateStr.trim(), format);
		} catch(DateTimeParseException e) {
			if(logger.isTraceEnabled()) logger.trace("Unable to parse input date " + dateStr);
			return null;
		}
	}

	private Map<String, ? extends CategoryStatistics> getDateStats(InMemoryRecorder recorder, LocalDate date) {
		/*Map<String, SummarizedCategoryStatistics> dateStats = new HashMap<String, SummarizedCategoryStatistics>();
		Map<LocalDateTime, Map<String, CategoryStatistics>> intervalStats = recorder.getIntervalStats();
		for(LocalDateTime interval : intervalStats.keySet()) {
			if(date.equals(interval.toLocalDate())) {
				addStats(intervalStats.get(interval), dateStats);
			}
		}
		return dateStats;
		*/
		LocalDateTime dateStart = date.atStartOfDay();
		return getSummarizedStats(recorder, dateStart, dateStart.plusDays(1));
	}

}
