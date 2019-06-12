package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hamy.usagemonitor.recorder.InMemoryRecorderBase.CategoryStatistics;
import com.hamy.usagemonitor.recorder.InMemorySessionRecorder;
import com.hamy.usagemonitor.recorder.InMemorySessionRecorder.SessionStatistics;

public class InMemoryRecorderSessionStatsServlet
extends InMemoryRecorderBaseServlet {
	private static final long serialVersionUID = 2246470760711885481L;

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
		
		Object o = servletContext.getAttribute(InMemorySessionRecorder.getDefaultSevletContextAttributeName());
		if(o == null || !(o instanceof InMemorySessionRecorder)) return;
		
		InMemorySessionRecorder recorder = (InMemorySessionRecorder) o;
		
		String sessionId = request.getParameter("sessionId");
		if(sessionId != null && !"".equals(sessionId)) {
			Map<String, SessionStatistics> allSessionStats = recorder.getSessionStats();
			SessionStatistics sessionStats = allSessionStats == null ? null : allSessionStats.get(sessionId);

			if(sessionStats != null) {
				Map<String, CategoryStatistics> stats = sessionStats.getCategoryStats();
				request.setAttribute("IN_MEMORY_RECORDER_SORTED_STATS", 
						sortCategoryStats(stats, request));
			}
			servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
		}
	}
		
}
