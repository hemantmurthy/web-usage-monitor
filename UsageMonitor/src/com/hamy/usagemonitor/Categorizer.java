package com.hamy.usagemonitor;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
/**
 * 
 * @author Hemant Murthy
 *
 */
public interface Categorizer {
	public void init(ServletContext context);
	public Category categorize(ServletRequest request);
	public void destroy();
}
