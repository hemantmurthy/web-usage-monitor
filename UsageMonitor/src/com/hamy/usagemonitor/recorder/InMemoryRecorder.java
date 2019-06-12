package com.hamy.usagemonitor.recorder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.Category;
import com.hamy.usagemonitor.Recorder;


/**
 * This implementation of Recorder gathers statistics in JVM memory from the time the JVM
 * is started till it's shut down. The statistics are available on the application context
 * to view online
 * @author Hemant Murthy
 *
 */
public class InMemoryRecorder
extends InMemoryRecorderBase
implements Recorder {
	public static final String HOUR_INTERVAL = "HOUR";
	public static final String DAY_INTERVAL = "DAY";
	public static final String MINUTE_INTERVAL = "MINUTE";
	public static final String FIVE_MINUTES_INTERVAL = "FIVE_MINUTES";
	public static final String TEN_MINUTES_INTERVAL = "TEN_MINUTES";
	public static final String FIFTEEN_MINUTES_INTERVAL = "FIFTEEN_MINUTES";
	public static final String TWENTY_MINUTES_INTERVAL = "TWENTY_MINUTES";
	public static final String THIRTY_MINUTES_INTERVAL = "THIRTY_MINUTES";
	public static final String HALF_HOUR_INTERVAL = "HALF_HOUR";
	
	private static Logger logger = Logger.getLogger(InMemoryRecorder.class);
	private static final String DEFAULT_RECORDER_SERVLET_CONTEXT_ATTRIBUTE_NAME = "com.hamy.usagemonitor.InMemoryRecorder";
	private static final String DEFAULT_RECORDER_INTERVAL_TYPE = FIVE_MINUTES_INTERVAL;
	
	private ServletContext context;
	private Map<String, CategoryStatistics> globalCategoryStats = null;
	private Map<LocalDateTime, Map<String, CategoryStatistics>> allIntervalStats = null;

	public static String getDefaultSevletContextAttributeName() {
		return DEFAULT_RECORDER_SERVLET_CONTEXT_ATTRIBUTE_NAME;
	}
	
	protected String getServletContextAttributeName() {
		return DEFAULT_RECORDER_SERVLET_CONTEXT_ATTRIBUTE_NAME;
	}
	
	public String getIntervalDuration() {
		return DEFAULT_RECORDER_INTERVAL_TYPE;
	}
	
	interface IntervalDeriver {
		LocalDateTime getInterval(LocalDateTime datetime);
	}
	
	IntervalDeriver intervalDeriver = null;
	
	@Override
	public void init(ServletContext context) {
		this.globalCategoryStats = new HashMap<String, CategoryStatistics>();
		this.allIntervalStats = new HashMap<LocalDateTime, Map<String, CategoryStatistics>>();
		this.context = context;
		
		setupIntervalDeriver();
		
		context.setAttribute(getServletContextAttributeName(), this);
		logger.info("In Memory Recorder initialized successfull.");
	}
	
	private void setupIntervalDeriver() {
		switch(getIntervalDuration()) {
		case HOUR_INTERVAL:
			intervalDeriver = datetime -> datetime.truncatedTo(ChronoUnit.HOURS);
			break;
		case MINUTE_INTERVAL:
			intervalDeriver = datetime -> datetime.truncatedTo(ChronoUnit.MINUTES);
			break;
		case FIVE_MINUTES_INTERVAL:
			intervalDeriver = datetime -> truncateToMinutes(datetime, 5);
			break;
		case TEN_MINUTES_INTERVAL:
			intervalDeriver = datetime -> truncateToMinutes(datetime, 10);
			break;
		case FIFTEEN_MINUTES_INTERVAL:
			intervalDeriver = datetime -> truncateToMinutes(datetime, 15);
			break;
		case TWENTY_MINUTES_INTERVAL:
			intervalDeriver = datetime -> truncateToMinutes(datetime, 20);
			break;
		case THIRTY_MINUTES_INTERVAL:
		case HALF_HOUR_INTERVAL:
			intervalDeriver = datetime -> truncateToMinutes(datetime, 30);
			break;
		case DAY_INTERVAL:
		default:
			intervalDeriver = datetime -> datetime.truncatedTo(ChronoUnit.DAYS);
			break;
		}
	}
	
	private LocalDateTime truncateToMinutes(LocalDateTime datetime, int minutes) {
		return datetime.withMinute(((int) Math.floor( new Double( datetime.getMinute() ) / minutes ) ) * minutes).truncatedTo(ChronoUnit.MINUTES); 
	}
	
	@Override 
	public void destroy() {
		context.removeAttribute(getServletContextAttributeName());
		context = null;
		globalCategoryStats = null;
		logger.info("In Memory Recorder destroyed successfull.");
	}
	
	public Map<String, CategoryStatistics> getGlobalCategoryStats() {
		return this.globalCategoryStats;
	}
	
	public Map<LocalDateTime, Map<String, CategoryStatistics>> getIntervalStats() {
		return this.allIntervalStats;
	}
	
	@Override
	public void record(LocalDateTime datetime, Category category, String username, String sessionId, int responseStatus, long durationms, ServletRequest request,
			ServletResponse response) {
		
		if(category != null) {
			recordGlobalStats(category, durationms);
			recordIntervalStats(datetime, category, durationms);
		}
	}

	private void recordGlobalStats(Category category, long durationms) {
		recordCategoryStats(globalCategoryStats, category, durationms);
	}
	
	private void recordIntervalStats(LocalDateTime datetime, Category category, long durationms) {
		LocalDateTime interval = intervalDeriver.getInterval(datetime);
		logger.trace(interval);
		Map<String, CategoryStatistics> intervalStats = allIntervalStats.get(interval);
		if(intervalStats == null) {
			synchronized(allIntervalStats) {
				intervalStats = allIntervalStats.get(interval);
				if(intervalStats == null) {
					intervalStats = new HashMap<String, CategoryStatistics>();
					allIntervalStats.put(interval, intervalStats);
				}
			}
		}
		
		recordCategoryStats(intervalStats, category, durationms);
	}	
}
