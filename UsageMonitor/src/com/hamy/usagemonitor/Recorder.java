package com.hamy.usagemonitor;

import java.time.LocalDateTime;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface Recorder {
	public void init(ServletContext context);
	public void record(LocalDateTime datetime, Category category, String username, String sessionId, int responseStatus, long durationms, ServletRequest request, ServletResponse response);
	public void destroy();
}
