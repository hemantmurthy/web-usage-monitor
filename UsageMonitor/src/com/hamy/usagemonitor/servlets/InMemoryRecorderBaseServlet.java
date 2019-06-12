package com.hamy.usagemonitor.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.recorder.InMemoryRecorder;
import com.hamy.usagemonitor.recorder.InMemoryRecorderBase.CategoryStatistics;
import com.hamy.usagemonitor.servlets.InMemoryRecorderBaseServlet.SummarizedCategoryStatistics;

/**
 * This class implements a base servlet for displaying stats from the In Memory Recorders.
 * @author Hemant Murthy
 *
 */
public class InMemoryRecorderBaseServlet
extends HttpServlet {
	private static final long serialVersionUID = -2285604094694094273L;
	private static final Logger logger = Logger.getLogger(InMemoryRecorderBaseServlet.class);
	
	public static final String ORDER_BY_CATEGORY = "category";
	public static final String ORDER_BY_DESCRIPTION = "description";
	public static final String ORDER_BY_COUNT = "count";
	public static final String ORDER_BY_MIN_DURATION = "mindur";
	public static final String ORDER_BY_MAX_DURATION = "maxdur";
	public static final String ORDER_BY_AVG_DURATION = "avgdur";
	public static final String ORDER_ASCENDING = "asc";
	public static final String ORDER_DESCENDING = "desc";
	
	protected int maxIntervalCount = 30;
	
	private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM, yyyy");
	private DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("dd MMM hh:mm a");
	private DateTimeFormatter MINUTE_FORMAT = DateTimeFormatter.ofPattern("dd MMM, hh:mm a");
	private DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm");
	
	private String successView = null;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext sc = config.getServletContext();
		String o = sc.getInitParameter("com.hamy.usagemonitor.servlets.InMemoryRecorderBaseServlet.MAX_INTERVAL_COUNT");
		if(o != null && !"".equals(o)) {
			try {
				maxIntervalCount = Integer.parseInt(o);
			} catch(NumberFormatException e) {};
		}
		
		successView = config.getInitParameter("SUCCESS_VIEW");
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
		
		servletContext.getRequestDispatcher(getSuccessView()).forward(request, response);
	}
	
	protected String getSuccessView() {
		return successView;
	}
	
	protected InMemoryRecorder getInMemoryRecorder(ServletContext servletContext) {
		Object o = servletContext.getAttribute(InMemoryRecorder.getDefaultSevletContextAttributeName());
		if(o == null || !(o instanceof InMemoryRecorder)) return null;
		
		return (InMemoryRecorder) o;
	}
	
	protected Map<String, LocalDateTime> getIntervalLabels(InMemoryRecorder recorder, LocalDate date, int limit) {
		List<LocalDateTime> intervals = new ArrayList<>(recorder.getIntervalStats().keySet());
		Collections.sort(intervals);
		Collections.reverse(intervals);
		
		Map<String, LocalDateTime> intervalLabels = new LinkedHashMap<>();

		int count = 0;
		for(LocalDateTime interval : intervals) {
			if(date == null || date.equals(interval.toLocalDate())) {
				if(limit > 0 && ++count > limit) break;
				intervalLabels.put(formatDateTime(interval, recorder), interval);
			}
		}
		
		return intervalLabels;
	}
	
	protected Map<String, LocalDate> getDateLabels(InMemoryRecorder recorder, HttpServletRequest request, int limit) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
		
		List<LocalDateTime> intervals = new ArrayList<>(recorder.getIntervalStats().keySet());
		Collections.sort(intervals);
		Collections.reverse(intervals);
		
		Map<String, LocalDate> dateLabels = new LinkedHashMap<String, LocalDate>();
		
		for(LocalDateTime interval : intervals) {
			String label = formatter.format(interval);
			if(!dateLabels.containsKey(label)) {
				dateLabels.put(label, interval.toLocalDate());
			}
		}
		
		return dateLabels;
	}
	
	protected String formatDate(LocalDate date) {
		return date.format(DATE_FORMAT);
	}
	
	protected String formatDateTime(LocalDateTime interval, InMemoryRecorder recorder) {
		switch(recorder.getIntervalDuration()) {
		case InMemoryRecorder.DAY_INTERVAL:
			return interval.format(DATE_FORMAT);
		case InMemoryRecorder.HOUR_INTERVAL:
			return interval.format(HOUR_FORMAT);
		case InMemoryRecorder.MINUTE_INTERVAL:
		case InMemoryRecorder.FIVE_MINUTES_INTERVAL:
		case InMemoryRecorder.TEN_MINUTES_INTERVAL:
		case InMemoryRecorder.FIFTEEN_MINUTES_INTERVAL:
		case InMemoryRecorder.TWENTY_MINUTES_INTERVAL:
		case InMemoryRecorder.THIRTY_MINUTES_INTERVAL:
		case InMemoryRecorder.HALF_HOUR_INTERVAL:
			return interval.format(MINUTE_FORMAT);
		default:
			return interval.format(DEFAULT_FORMAT);
		}
	}
	
	protected Map<String, ? extends CategoryStatistics> sortCategoryStats(Map<String, ? extends CategoryStatistics> map,  String orderBy, String order, boolean includeNonCategorized) {
		Map<String, CategoryStatistics> sortedMap = createSortedMap(map, orderBy, order);
		CategoryStatistics stat = null;
		for(String category : map.keySet()) {
			stat = map.get(category);
			if(!stat.isNonCategorized() || includeNonCategorized)
				sortedMap.put(category, map.get(category));
		}
		
		return sortedMap;
	}
	
	protected Map<String, ? extends CategoryStatistics> sortCategoryStats(Map<String, ? extends CategoryStatistics> map, HttpServletRequest request) {
		String orderBy = (String) request.getParameter("ordby");
		String order = (String) request.getParameter("ord");
		
		orderBy = orderBy == null ? ORDER_BY_CATEGORY : orderBy.toLowerCase();
		order = order == null ? ORDER_ASCENDING : order.toLowerCase();
		
		request.setAttribute("ordby", orderBy);
		request.setAttribute("ord", order);
		
		String includeNonCategorized = request.getParameter("inclnoncat");
		includeNonCategorized = includeNonCategorized == null ? null : includeNonCategorized.trim().toLowerCase();
		request.setAttribute("inclnoncat", includeNonCategorized);

		Map<String, ? extends CategoryStatistics> sortedMap = sortCategoryStats(map, orderBy, order, "true".equals(includeNonCategorized));
		
		return sortedMap;
	}
	
	private static class StatCountComparator implements Comparator<String> {
		Map<String, ? extends CategoryStatistics> stats = null;
		StatCountComparator(Map<String, ? extends CategoryStatistics> categoryStats) {
			this.stats = categoryStats;			
		}
		
		@Override
		public int compare(String o1, String o2) {
			CategoryStatistics cs1 = this.stats.get(o1);
			CategoryStatistics cs2 = this.stats.get(o2);
			
			long cnt1 = cs1 == null ? 0 : cs1.getCount();
			long cnt2 = cs2 == null ? 0 : cs2.getCount();
			
			if(cnt1 == cnt2) {
				if(o1 != null) return o1.compareTo(o2);
				
				if(o2 != null) return -1;
				
				return 0;
			} else return cnt1 < cnt2 ? -1 : 1;
		}
	}
	
	private static class StatMaxDurationComparator implements Comparator<String> {
		Map<String, ? extends CategoryStatistics> stats = null;
		StatMaxDurationComparator(Map<String, ? extends CategoryStatistics> categoryStats) {
			this.stats = categoryStats;			
		}
		
		@Override
		public int compare(String o1, String o2) {
			CategoryStatistics cs1 = this.stats.get(o1);
			CategoryStatistics cs2 = this.stats.get(o2);
			
			long dur1 = cs1 == null ? 0 : cs1.getMaxDuration();
			long dur2 = cs2 == null ? 0 : cs2.getMaxDuration();
			
			if(dur1 == dur2) {
				if(o1 != null) return o1.compareTo(o2);
				
				if(o2 != null) return -1;
				
				return 0;
			} else return dur1 < dur2 ? -1 : 1;
		}
	}
	
	private static class StatMinDurationComparator implements Comparator<String> {
		Map<String, ? extends CategoryStatistics> stats = null;
		StatMinDurationComparator(Map<String, ? extends CategoryStatistics> categoryStats) {
			this.stats = categoryStats;			
		}
		
		@Override
		public int compare(String o1, String o2) {
			CategoryStatistics cs1 = this.stats.get(o1);
			CategoryStatistics cs2 = this.stats.get(o2);
			
			long dur1 = cs1 == null ? 0 : cs1.getMinDuration();
			long dur2 = cs2 == null ? 0 : cs2.getMinDuration();
			
			if(dur1 == dur2) {
				if(o1 != null) return o1.compareTo(o2);
				
				if(o2 != null) return -1;
				
				return 0;
			} else return dur1 < dur2 ? -1 : 1;
		}
	}
	
	private static class StatAvgDurationComparator implements Comparator<String> {
		Map<String, ? extends CategoryStatistics> stats = null;
		StatAvgDurationComparator(Map<String, ? extends CategoryStatistics> categoryStats) {
			this.stats = categoryStats;			
		}
		
		@Override
		public int compare(String o1, String o2) {
			CategoryStatistics cs1 = this.stats.get(o1);
			CategoryStatistics cs2 = this.stats.get(o2);
			
			double dur1 = cs1 == null ? 0 : cs1.getAvgDuration();
			double dur2 = cs2 == null ? 0 : cs2.getAvgDuration();
			
			if(dur1 == dur2) {
				if(o1 != null) return o1.compareTo(o2);
				
				if(o2 != null) return -1;
				
				return 0;
			} else return dur1 < dur2 ? -1 : 1;
		}
	}
	
	private Map<String, CategoryStatistics> createSortedMap(Map<String, ? extends CategoryStatistics> categoryStats, String orderBy, String order) {
		Map<String, CategoryStatistics> sortedStats = null;
		
		switch(orderBy) {
		case ORDER_BY_CATEGORY:
			switch(order) {
			case ORDER_ASCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>();
				break;
				
			case ORDER_DESCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						if(o2 != null) return o2.compareTo(o1);
						if(o1 != null) return -1;
						return 0;
					}
				});
				break;	
			}
			break;
			
		case ORDER_BY_COUNT:
			switch(order) {
			case ORDER_ASCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatCountComparator(categoryStats));
				break;
			case ORDER_DESCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatCountComparator(categoryStats) {
					@Override
					public int compare(String o1, String o2) { return super.compare(o2, o1); }
				});
				break;
			}
			break;
			
		case ORDER_BY_MAX_DURATION:
			switch(order) {
			case ORDER_ASCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatMaxDurationComparator(categoryStats));
				break;
			case ORDER_DESCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatMaxDurationComparator(categoryStats) {
					@Override
					public int compare(String o1, String o2) { return super.compare(o2, o1); }
				});
				break;
			}
			break;
			
		case ORDER_BY_MIN_DURATION:
			switch(order) {
			case ORDER_ASCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatMinDurationComparator(categoryStats));
				break;
			case ORDER_DESCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatMinDurationComparator(categoryStats) {
					@Override
					public int compare(String o1, String o2) { return super.compare(o2, o1); }
				});
				break;
			}
			break;
			
		case ORDER_BY_AVG_DURATION:
			switch(order) {
			case ORDER_ASCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatAvgDurationComparator(categoryStats));
				break;
			case ORDER_DESCENDING:
				sortedStats = new TreeMap<String, CategoryStatistics>(new StatAvgDurationComparator(categoryStats) {
					@Override
					public int compare(String o1, String o2) { return super.compare(o2, o1); }
				});
				break;
			}
			break;
		}
		
		if(sortedStats == null)
			sortedStats = new HashMap<String, CategoryStatistics>();
		
		return sortedStats;
	}

	
	protected void addStats(Map<String, CategoryStatistics> stats, Map<String, SummarizedCategoryStatistics> accumulatedStats) {
		for(String category : stats.keySet()) {
			CategoryStatistics stat = stats.get(category);
			SummarizedCategoryStatistics datestat = null;
			if(!accumulatedStats.containsKey(category)) {
				datestat = new SummarizedCategoryStatistics(stat.getCategory(), stat.getDescription(), stat.isNonCategorized());
				accumulatedStats.put(category, datestat);
			} else datestat = accumulatedStats.get(category);
			
			datestat.mergeStats(stats.get(category));
		}
	}

	protected Map<String, ? extends CategoryStatistics> getSummarizedStats(InMemoryRecorder recorder, LocalDateTime intervalStart, LocalDateTime intervalEnd) {
		if(logger.isTraceEnabled())
			logger.trace("Summarizing stats between " + intervalStart + " and " + intervalEnd);
		
		Map<String, SummarizedCategoryStatistics> summarizedStats = new HashMap<String, SummarizedCategoryStatistics>();
		Map<LocalDateTime, Map<String, CategoryStatistics>> intervalStats = recorder.getIntervalStats();
		
		// Loop through each interval and check if interval is between start 
		// and end interval, and if so, add interval stats to summary ...
		for(LocalDateTime interval : intervalStats.keySet()) {
			if(	// (intervalStart <= interval < intervalEnd) ... 
					(intervalStart == null || intervalStart.equals(interval) || intervalStart.isBefore(interval))
				&&	(intervalEnd == null || intervalEnd.isAfter(interval))
			) {
				if(logger.isTraceEnabled()) logger.trace("Adding stats for " + interval);
				addStats(intervalStats.get(interval), summarizedStats);
			}
		}
		
		return summarizedStats;
	}
	
	public static class SummarizedCategoryStatistics
	extends CategoryStatistics {
		protected SummarizedCategoryStatistics(String category, String description, boolean fallbackCategory) {
			super(category, description, fallbackCategory);
		}		
		
		void mergeStats(CategoryStatistics stat) {
			super.mergeStat(stat);
		}
	}
}
