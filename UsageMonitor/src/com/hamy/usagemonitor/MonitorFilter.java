package com.hamy.usagemonitor;

import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.categorizer.URIParametersCategorizer;
import com.hamy.usagemonitor.categorizer.XMLConfigfiguredURIParametersCategorizer;
import com.hamy.usagemonitor.recorder.InMemoryRecorder;

/**
 * This filter tracks every request handled by the server and categorizes them.
 * Each request is categorized based on the method, URI and request parameters. If a category 
 * is identified, then the request is recorded by Recorders that have been configured.
 * 
 * @author Hemant Murthy
 */
public class MonitorFilter implements Filter {
	private static Logger logger = Logger.getLogger(MonitorFilter.class);
	
	private Set<Recorder> recorders = new HashSet<>();
	private Categorizer categorizer;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("************************************");
		logger.info("     U.S.A.G.E.  M.O.N.I.T.O.R.");
		logger.info("************************************");
		logger.info("Intializing Usage Monitor Filter");
		logger.info("Setting up Categorizer");
		setupCategorizer(config);
		logger.info("Categorizer setup successfully");

		logger.info("Setting up Recorders");
		setupRecorders(config);
		logger.info(recorders.size() + " recorders setup");
		logger.info("Recorders setup successfully");
		logger.info("Usage Monitor Filter initialization completed successfully");
	}
	
	private void setupCategorizer(FilterConfig config) {
		String categorizerClassName = config.getInitParameter("CATEGORIZER_CLASS");
		if(categorizerClassName == null || "".equals(categorizerClassName.trim()))
			throw new RuntimeException("CATEGORIZER_CLASS parameter not set");
		
		Class<?> categorizerClass = null;
		try {
			categorizerClass = Class.forName(categorizerClassName.trim());
			if(!Categorizer.class.isAssignableFrom(categorizerClass)) {
				throw new RuntimeException("Class " + categorizerClassName.trim() + " is not an implementation of " + Categorizer.class.getName());
			}
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to find class " + categorizerClassName.trim());
		}
		
		try {
			categorizer = Categorizer.class.cast(categorizerClass.newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException("Unable to instantiate " + categorizerClass.getName());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("No access to instantiate " + categorizerClass.getName());
		} catch (ClassCastException e) {
			throw new RuntimeException("Unable to cast Categorizer to type " + Categorizer.class.getName());
		}
		
		try {
			if(categorizer != null) categorizer.init(config.getServletContext());
		} catch(Exception e) {
			logger.error("Exception encountered when intializing Categorizer " + categorizerClass.getName(), e);
			this.categorizer = null;
			throw new RuntimeException("Unable to initialize Categorizer");
		}
		
		logger.info("Categorizer of type " + categorizer.getClass().getName() + " setup.");
	}
	
	private void setupRecorders(FilterConfig config) {
		String recorderClassNames = config.getInitParameter("RECORDER_CLASSES");
		if(recorderClassNames == null || "".equals(recorderClassNames.trim()))
			throw new RuntimeException("RECORDER_CLASS parameter not set");
		
		for(String recorderClassName : recorderClassNames.split(",")) {
			Class<?> recorderClass = null;
			try {
				recorderClass = Class.forName(recorderClassName.trim());
				if(!Recorder.class.isAssignableFrom(recorderClass)) {
					throw new RuntimeException("Class " + recorderClassName.trim() + " is not an implementation of " + Recorder.class.getName());
				}
				
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Unable to find class " + recorderClassName.trim());
			}
			
			Recorder recorder = null;
			try {
				recorder = Recorder.class.cast(recorderClass.newInstance());
			} catch (InstantiationException e) {
				throw new RuntimeException("Unable to instantiate " + recorderClass.getName());
			} catch (IllegalAccessException e) {
				throw new RuntimeException("No access to instantiate " + recorderClass.getName());
			} catch (ClassCastException e) {
				throw new RuntimeException("Unable to cast Recorder to type " + Recorder.class.getName());
			}
			
			try {
				recorder.init(config.getServletContext());
			} catch(Exception e) {
				logger.error("Exception encountered when intializing Recorder " + recorderClass.getName(), e);
				throw new RuntimeException("Unable to initialize Recorder");
			}
			
			recorders.add(recorder);
			logger.info("Recorder of type " + recorder.getClass().getName() + " added.");
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LocalDateTime reqstdttm = LocalDateTime.now();

		Category requestCategory = null;
		if(categorizer != null)
			requestCategory = categorizer.categorize(request);
		
		if(logger.isTraceEnabled() && requestCategory != null) logger.trace(requestCategory.getCategoryCode());

		chain.doFilter(request, response);
		
		int respStatus = 0;
		String user = null;
		String sessionId = null;
		
		if(request instanceof HttpServletRequest) {
			HttpServletRequest hr = (HttpServletRequest) request;
			Principal principal = hr.getUserPrincipal();
			user = principal == null ? null : principal.getName();
			HttpSession session = hr.getSession(false);
			if(session != null)
				sessionId = session.getId();
		}
		
		if(response instanceof HttpServletResponse) {
			HttpServletResponse hrsp = (HttpServletResponse) response;
			respStatus = hrsp.getStatus();
		}
		
		LocalDateTime reqenddttm = LocalDateTime.now();
		long dur = Duration.between(reqstdttm, reqenddttm).toMillis();
		for(Recorder r : this.recorders) {
			r.record(reqstdttm, requestCategory, user, sessionId, respStatus, dur, request, response);
		}
	}

	@Override
	public void destroy() {
	}
}
