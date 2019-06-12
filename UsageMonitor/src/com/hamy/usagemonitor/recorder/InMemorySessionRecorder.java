package com.hamy.usagemonitor.recorder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.Category;
import com.hamy.usagemonitor.Recorder;


/**
 * This implementation of Recorder gathers statistics for each Session from the time the JVM
 * is started till it's shut down. Sessions are gathered for every session till the time the 
 * sessions are destroyed. Once a session is destroyed, it's stats will also be removed from
 * the recorder. 
 * 
 * These statistics are available on the application context to view online
 * @author Hemant Murthy
 *
 */
public class InMemorySessionRecorder
extends InMemoryRecorderBase
implements Recorder {
	private static Logger logger = Logger.getLogger(InMemorySessionRecorder.class);
	private static final String DEFAULT_RECORDER_SERVLET_CONTEXT_ATTRIBUTE_NAME = "com.hamy.usagemonitor.InMemorySessionRecorder";
	
	private ServletContext context;
	private Map<String, SessionStatistics> sessionStats = null;

	private long sessionStatsCleanerTTS = 600000;
	private long sessionStatsCleanerTSI = 7200;
	private SessionStatsCleaner sessionStatsCleaner = null;
	
	public static String getDefaultSevletContextAttributeName() {
		return DEFAULT_RECORDER_SERVLET_CONTEXT_ATTRIBUTE_NAME;
	}
	
	protected String getServletContextAttributeName() {
		return DEFAULT_RECORDER_SERVLET_CONTEXT_ATTRIBUTE_NAME;
	}
	
	@Override
	public void init(ServletContext context) {
		this.context = context;

		sessionStats = new HashMap<String, SessionStatistics>();
		context.setAttribute(getServletContextAttributeName(), this);
		
		sessionStatsCleaner = new SessionStatsCleaner(sessionStatsCleanerTTS, sessionStatsCleanerTSI);
		sessionStatsCleaner.start();
		
		logger.info("In Memory Session Recorder initialized successfull.");
	}
	
	@Override 
	public void destroy() {
		sessionStatsCleaner.stop();
		context.removeAttribute(getServletContextAttributeName());
		context = null;
		sessionStats = null;
		logger.info("In Memory Session Recorder destroyed successfull.");
	}
	
	public Map<String, SessionStatistics> getSessionStats() {
		return this.sessionStats;
	}

	@Override
	public void record(LocalDateTime datetime, Category category, String username, String sessionId, int responseStatus, long durationms, ServletRequest request,
			ServletResponse response) {
		if(sessionId != null)
			recordSessionStats(datetime, sessionId, username, category, durationms);
	}

	private void recordSessionStats(LocalDateTime datetime, String sessionId, String username, Category category, long durationms) {
		SessionStatistics sessionStat = getSessionStats(sessionId, null);
		if(sessionStat != null) sessionStat.recordTransaction(datetime, username, category, durationms);
	}
	
	private SessionStatistics getSessionStats(String sessionId, LocalDateTime timeCreated) {
		SessionStatistics sessionStat = sessionStats.get(sessionId);
		if(sessionStat == null) {
			synchronized(sessionStats) {
				sessionStat = sessionStats.get(sessionId);
				if(sessionStat == null) {
					sessionStat = new SessionStatistics(timeCreated != null ? timeCreated : LocalDateTime.now());
					sessionStats.put(sessionId, sessionStat);
				}
			}
		}
		
		return sessionStat;
	}
	
	void createSessionStats(String sessionId, LocalDateTime timeCreated) {
		getSessionStats(sessionId, timeCreated);
	}
	
	void removeSessionStats(String sessionId) {
		SessionStatistics ss = null;
		synchronized(sessionStats) {
			ss = sessionStats.remove(sessionId);
		}
		if(ss != null) {
			if(logger.isTraceEnabled())	{
				logger.trace("Removing stats for session " + sessionId);
				logger.trace("User " + ss.username);
			}
		}
		else
			if(logger.isTraceEnabled())	logger.trace("Unable to find stats for session " + sessionId);
	}
	
	public static class SessionStatistics {
		String username = null;
		LocalDateTime timeCreated = null;
		LocalDateTime timeLastAccessed = null;
		
		Map<String, CategoryStatistics> categoryStats;
		
		SessionStatistics(LocalDateTime timeCreated) {
			this.timeCreated = timeCreated;
			this.timeLastAccessed = timeCreated;
			categoryStats = new HashMap<>();
		}
		
		public String getUserName() { return this.username; }
		public LocalDateTime getTimeCreated() { return this.timeCreated; }
		public LocalDateTime getTimeLastAccessed() { return this.timeLastAccessed; }
		public Map<String, CategoryStatistics> getCategoryStats() {
			return this.categoryStats;
		}
		
		void recordTransaction(LocalDateTime datetime, String username, Category category, long durationms) {
			this.username = username; 
			if(datetime.isAfter(timeLastAccessed))
				timeLastAccessed = datetime;
			
			if(category != null)
				recordCategoryStats(categoryStats, category, durationms);
		}
	}

	/**
	 * This class monitors and cleans up stats for sessions that have been inactive for a long time. 
	 * @author Hemant Murthy
	 */
	private class SessionStatsCleaner
	implements Runnable {
		Thread thread = null;
		long timeToSleepMillis = 300000;
		long timeSinceInactiveSeconds = 7200;
		
		public SessionStatsCleaner(long timeToSleepMillis, long timeSinceInactiveSeconds) {
			this.timeToSleepMillis = timeToSleepMillis;
			this.timeSinceInactiveSeconds = timeSinceInactiveSeconds;
		}
		
		@Override
		public void run() {
			try {
				while(thread != null) {
					Thread.sleep(timeToSleepMillis);
					
					logger.info("Cleaning up stats for inactive sessions ...");
					LocalDateTime now = LocalDateTime.now();
					List<String> toBeRemoved = new ArrayList<>();
					for(Entry<String, SessionStatistics> sessionStatEntry : sessionStats.entrySet()) {
						SessionStatistics ss = sessionStatEntry.getValue();
						if(ss != null && ss.getTimeLastAccessed().until(now, ChronoUnit.SECONDS) > timeSinceInactiveSeconds)
							toBeRemoved.add(sessionStatEntry.getKey());
					}
					
					for(String sessionId : toBeRemoved)
						sessionStats.remove(sessionId);
					
					System.gc();
				}
			} catch (InterruptedException e) {
				logger.info("Session Stats Cleaner thread terminated", e);
			}
		}
		
		public void start() {
			thread = new Thread(this);
			thread.start();
			logger.info("Session Stats Cleaner thread started");
		}
		
		public void stop() {
			try {
				thread.interrupt();
			} catch(SecurityException e) {
				logger.warn("Unable to interrupt Session Stats Cleaner thread", e);
			} finally {
				thread = null;
			}
		}
	}
	
}
