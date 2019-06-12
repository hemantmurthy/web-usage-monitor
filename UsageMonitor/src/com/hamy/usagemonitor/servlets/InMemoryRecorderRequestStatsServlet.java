package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hamy.usagemonitor.recorder.InMemoryRecorder;

public class InMemoryRecorderRequestStatsServlet
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
		
		InMemoryRecorder recorder = (InMemoryRecorder) o;
		request.setAttribute("IN_MEMORY_RECORDER_SORTED_STATS", 
				sortCategoryStats(recorder.getGlobalCategoryStats(), request));
		
		Map<String, LocalDate> dateLabels = getDateLabels(recorder, request, maxIntervalCount);
		request.setAttribute("IN_MEMORY_RECORDER_DATE_STATS", dateLabels);
		
		servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
	}
	
}
