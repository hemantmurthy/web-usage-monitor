package com.hamy.usagemonitor.recorder;

import java.time.LocalDateTime;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class InMemoryRecorderWebListener
implements HttpSessionListener {
	private static Logger logger = Logger.getLogger(InMemoryRecorderWebListener.class);
	
	public InMemoryRecorderWebListener() {
		logger.info(InMemoryRecorderWebListener.class + " created!!!");
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		String sessionId = null;
		ServletContext context = null;
		
		if(session != null) {
			sessionId = session.getId();
			context = session.getServletContext();
		}
		
		logger.debug("Session Created. " + (sessionId != null ? "Session ID: " + sessionId : ""));

		if(context != null) {
			InMemorySessionRecorder recorder = getSessionRecorder(context);
			if(recorder != null) recorder.createSessionStats(sessionId, LocalDateTime.now());
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		String sessionId = null;
		ServletContext context = null;
		
		if(session != null) {
			sessionId = session.getId();
			context = session.getServletContext();
		}
		
		logger.debug("Session Destroyed. " + (sessionId != null ? "Session ID: " + sessionId : ""));
		
		if(context != null) {
			InMemorySessionRecorder recorder = getSessionRecorder(context);
			if(recorder != null) recorder.removeSessionStats(sessionId);
		}
	}
	
	private InMemorySessionRecorder getSessionRecorder(ServletContext context) {
		Object o = context.getAttribute("com.hamy.usagemonitor.InMemorySessionRecorder");
		if(o instanceof InMemorySessionRecorder)
			return (InMemorySessionRecorder) o;
		
		return null;
	}
}
