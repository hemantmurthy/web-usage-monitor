package com.hamy.usagemonitor.recorder;

import java.util.Map;

import com.hamy.usagemonitor.Category;


/**
 * This is a base implementation of a Recorder that gathers statistics in JVM memory.
 * It provides a structure to record statistics for a category.
 * @author Hemant Murthy
 *
 */
public abstract class InMemoryRecorderBase {
	protected static void recordCategoryStats(Map<String, CategoryStatistics> categoryStats, Category category, long durationms) {
		CategoryStatistics stats = categoryStats.get(category.getCategoryCode());
		if(stats == null) {
			synchronized(categoryStats) {
				stats = categoryStats.get(category.getCategoryCode());
				if(stats == null) {
					stats = new CategoryStatistics(category.getCategoryCode(), category.getDescription(), category.isNonCategorized());
					categoryStats.put(category.getCategoryCode(), stats);
				}
			}
		}
			
		stats.recordTransaction(durationms);
	}
	
	public static class CategoryStatistics {
		String category;
		String description;
		boolean nonCategorized;
		
		long count;
		long mindurms = Long.MAX_VALUE;
		long maxdurms = 0;
		double avgdurms = 0;
		
		protected CategoryStatistics(String category, String description, boolean nonCategorized) {
			this.category = category;
			this.description = description;
			this.nonCategorized = nonCategorized;
		}
		
		synchronized void recordTransaction(long duration) {
			if(duration < mindurms) mindurms = duration;
			if(duration > maxdurms) maxdurms = duration;
			avgdurms = ((avgdurms * count) + duration) / (count + 1);
			++count;
		}
		
		public String getCategory() { return this.category; }
		public String getDescription() { return this.description; }
		public boolean isNonCategorized() { return this.nonCategorized; }
		public long getCount() { return this.count; }
		public long getMinDuration() { return this.mindurms; }
		public long getMaxDuration() { return this.maxdurms; }
		public double getAvgDuration() { return this.avgdurms; }
		
		protected void mergeStat(CategoryStatistics stat) {
			if(stat.mindurms < mindurms) mindurms = stat.mindurms;
			if(stat.maxdurms > maxdurms) maxdurms = stat.maxdurms;
			avgdurms = ((avgdurms * count) + (stat.avgdurms * stat.count)) / (count + stat.count);
			count += stat.count;
		}
	}
	

}
