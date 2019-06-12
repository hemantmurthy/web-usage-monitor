package com.hamy.usagemonitor.categorizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.hamy.usagemonitor.Categorizer;
import com.hamy.usagemonitor.Category;

/**
 * This Categorizer categorizes requests based on the request URI and parameters on the request.
 * It will try to find the most specific category match.
 * @author Hemant Murthy
 */
public abstract class URIParametersCategorizer
implements Categorizer {
	private static Logger logger = Logger.getLogger(URIParametersCategorizer.class);

	private static boolean DEFAULT_IGNORE_CONTEXT_PATH = true;
	private static boolean DEFAULT_FALLBACK_TO_URI_AS_CATEGORY = false;
	
	private Map<String, URICategory> uriCategories = new HashMap<>();
	
	@Override
	public void init(ServletContext context) {
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public Category categorize(ServletRequest request) {
		if(logger.isTraceEnabled()) logger.trace("into categorize()");

		Category category = null;
		if(request instanceof HttpServletRequest) {
			// Retrieve the request URI and parameters (query string and form parameters) ...
			HttpServletRequest hr = (HttpServletRequest) request;
			String uri = hr.getRequestURI();
			Map<String, String[]> reqparms = hr.getParameterMap();
			
			if(logger.isTraceEnabled()) logger.trace("URI: " + uri);
			
			String finalUri = null;
			if(ignoreContextPath() && uri != null) finalUri = uri.substring(hr.getContextPath().length());
			else finalUri = uri;
			
			// Check if there are categories mapped for the request URI ...
			URICategory uc = uriCategories.get(finalUri);
			if(uc != null) {
				Stack<ParameterValue> valueStack = new Stack<>();
				
				// Find the most specific match based on request parameters ...
				ParameterCategory matchedparm = findMatch(reqparms, uc.nextParams, valueStack);
				
				// If a match was found, then return it's category. Otherwise
				// check if just the URI is mapped to a category, and if it is, then return 
				// the category mapped to the URI ...
				if(matchedparm != null) {
					category = constructCategory(matchedparm.category, valueStack);
					valueStack.clear();
				} else {
					if(uc.category != null) 
						category = uc.category;
				}
			}
		
			// If no category was determined, and if fallback to URI is enabled, 
			// then return URI as category ...
			if(category == null && fallBackToURIAsCategory())
				category = new RequestCategory(finalUri, finalUri, true);
		}
		
		return category;
	}
	
	private ParameterCategory findMatch(Map<String, String[]> reqparms, List<ParameterCategory> nextExpectedParms, Stack<ParameterValue> valueStack) {
		String[] parmvalues;
		ParameterCategory matchedparm = null;
		
		// Go through each parameter in the list to find a match ...
		for(ParameterCategory expectedParm : nextExpectedParms) {
			
			// Check if the request contains the parameter ...
			parmvalues = reqparms.get(expectedParm.name);
			if(parmvalues != null && parmvalues.length > 0) {
				
				// Check if a specific parameter value is expected ...
				if(expectedParm.value != null) {
					
					// Check if the request parameter has the specified value ...
					for(String v : parmvalues) {
						if(expectedParm.value.equals(v)) {
							// Add matched value to the value stack ...
							ParameterValue matchedParmValue = new ParameterValue(expectedParm.name, v);
							valueStack.push(matchedParmValue);
							
							// A matching parameter value has been found. 
							// Check other parameters to find a more specific match ...
							matchedparm = findMatch(reqparms, expectedParm.nextParams, valueStack);

							// If a more specific match was not found, set the current parameter 
							// as the match if it is mapped to a category ...
							if(matchedparm == null) {
								if(expectedParm.category != null) matchedparm = expectedParm;
								else 
									// Take off matched value from value stack ...
									valueStack.pop();
							}
							
							break;
						}
					}
				} else {
					// Add first value of matched parameter to value stack ...
					ParameterValue matchedParmValue = new ParameterValue(expectedParm.name, parmvalues[0]);
					valueStack.push(matchedParmValue);
					
					// A specific parameter value is not expected. 
					// Check other parameters to find a more specific match ...
					matchedparm = findMatch(reqparms, expectedParm.nextParams, valueStack);
					
					// If a more specific match was not found, set the current parameter 
					// as the match if it is mapped to a category ...
					if(matchedparm == null) {
						if(expectedParm.category != null) matchedparm = expectedParm;
						else 
							// Take off matched parameter value from value stack ...
							valueStack.pop();
						
					}
				}
			}
			
			// Don't check other other parms in the list if a match was found ...
			if(matchedparm != null) break;
		}
		
		return matchedparm;
	}
	
	private Category constructCategory(Category category, Stack<ParameterValue> valueStack) {
		String catcode = category.getCategoryCode();
		String catdesc = category.getDescription();
		for(ParameterValue pv : valueStack) {
			String regex = "\\{" + pv.name + "\\}";
			if(catcode != null) catcode = catcode.replaceAll(regex, pv.value);
			if(catdesc != null) catdesc = catdesc.replaceAll(regex, pv.value);
		}
		
		return new RequestCategory(catcode, catdesc, false);
	}
	
	static class URICategory {
		List<ParameterCategory> nextParams;
		RequestCategory category;
		
		URICategory() {
			this.nextParams = new ArrayList<>();
		}
		
	}
	
	static class ParameterCategory {
		String name;
		String value;
		List<ParameterCategory> nextParams;
		RequestCategory category;
		
		ParameterCategory(String name, String value) {
			this.name = name;
			this.value = value;
			this.nextParams = new ArrayList<>();
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null) return false;
			if(o == this) return true;
			
			if(o instanceof ParameterCategory) {
				ParameterCategory pc = (ParameterCategory) o;
				if(
					this.name.equals(pc.name) && (
						(this.value == null && pc.value == null) ||
						(this.value != null && this.value.equals(pc.value))
					)
				) return true;
				
				return false;
			} else return false;
		}
	}
	
	static class RequestCategory implements Category {
		String categoryCode;
		String description;
		boolean fallbackCategory;
		
		RequestCategory(String categoryCode, String description, boolean fallbackCategory) {
			this.categoryCode = categoryCode;
			this.description = description;
			this.fallbackCategory = fallbackCategory;
		}
		
		@Override
		public String getCategoryCode() {
			return categoryCode;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
		@Override
		public boolean isNonCategorized() {
			return fallbackCategory;
		}
	}
	
	public static class ParameterValue {
		String name, value;
		
		public ParameterValue(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() { return this.name; }
		public String getValue() { return this.value; }
	}
	
	protected boolean ignoreContextPath() {
		return DEFAULT_IGNORE_CONTEXT_PATH;
	}
	
	protected boolean fallBackToURIAsCategory() {
		return DEFAULT_FALLBACK_TO_URI_AS_CATEGORY;
	}
	
	public void addCategory(String uri, List<ParameterValue> parameterValues, String category, String categoryDescription) {
		if(uri == null) throw new RuntimeException("URI Cannot be null");
		
		// Default Category Description to uri,parm1=value1,parm2=value2,...
		if(categoryDescription == null) {
			categoryDescription = uri;
			for(ParameterValue pv : parameterValues) 
				categoryDescription += "," + pv.name + (pv.value != null ? "=" + pv.value : "");
		}
		RequestCategory requestCategory = new RequestCategory(category, categoryDescription, false);
				
		// Check if a category is already mapped to URI. If not create a new one and map it to the URI ...
		URICategory uric = this.uriCategories.get(uri);
		if(uric == null) {
			uric = new URICategory();
			uriCategories.put(uri, uric);
		}
		if(logger.isDebugEnabled()) logger.debug("URI " + uri);
		
		// If there are no parameters needed to map the category ...
		if(parameterValues == null || parameterValues.isEmpty()) {
			if(uric.category != null) {
				throw new RuntimeException("Category already mapped to URI " + uri);
			} else {
				uric.category = requestCategory;
			}
		} else {
			// Traverse the parameter categories using mapping parameters ...
			List<ParameterCategory> curlevel = uric.nextParams;
			ParameterCategory lastParmCategory = null;
			
			for(ParameterValue pv : parameterValues) {
				if(pv.name == null || "".equals(pv.name.trim()))
					throw new RuntimeException("Parameter Name cannot be null or blank");
				
				// Check if the parameter and value matches any at current level
				ParameterCategory mpc = null;
				for(ParameterCategory pc : curlevel) {
					if(pc.name.equals(pv.name) && 
						(
							(pc.value != null && pc.value.equals(pv.value)) || 
							(pc.value == null && (pc.value == null || "".equals(pc.value)) )
						)
					) {
						mpc = pc;
						break;
					}
				}
				
				if(mpc == null) {
					// No. Create a new parameter value node at current level ...
					mpc = new ParameterCategory(pv.name, pv.value);
					curlevel.add(mpc);
					if(logger.isDebugEnabled()) logger.debug("New Parm Node: name: " + pv.name + ", value: " + pv.value);
				} else {
					if(logger.isDebugEnabled()) logger.debug("Parm Node: name: " + pv.name + ", value: " + pv.value);
				}
				
				// Move to next level of parameter values ...
				lastParmCategory = mpc;
				curlevel = mpc.nextParams;
			}
			
			if(lastParmCategory.category != null) {
				throw new RuntimeException("A category has already been mapped to URI and parameter values list. " + 
						"Category already mapped is " + lastParmCategory.category.getCategoryCode() +
						", Category being mapped is " + requestCategory.getCategoryCode());
			}
			
			lastParmCategory.category = requestCategory;
		}
		
		if(logger.isDebugEnabled()) logger.debug("Added category " + requestCategory.getCategoryCode());
	}
}
