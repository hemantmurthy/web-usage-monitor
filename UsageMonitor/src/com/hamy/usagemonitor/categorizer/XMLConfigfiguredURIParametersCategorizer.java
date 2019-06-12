package com.hamy.usagemonitor.categorizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfigfiguredURIParametersCategorizer
extends URIParametersCategorizer {
	private static Logger logger = Logger.getLogger(XMLConfigfiguredURIParametersCategorizer.class);
	
	private boolean ignoreContextPath = true;
	private boolean fallBackToURIAsCategory = false;
	
	@Override
	public void init(ServletContext context) {
		super.init(context);
		ignoreContextPath = super.ignoreContextPath();
		fallBackToURIAsCategory = super.fallBackToURIAsCategory();
		
		String configFilename = context.getInitParameter(XMLConfigfiguredURIParametersCategorizer.class.getName() + ".CATEGORY_CONFIG");
		if(configFilename != null) loadConfig(configFilename);
	}
	
	@Override
	protected boolean fallBackToURIAsCategory() {
		return this.fallBackToURIAsCategory;
	}
	
	private void loadConfig(String filename) {
		logger.info("Loading configuration from file " + filename);
		
		DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		DocumentBuilder parser = null;
		try {
			parser = parserFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Unable to create Document Builder to parse config file", e);
			throw new RuntimeException("Unable to create Document Builder to parse config file", e);
		}
		
		Document dom = null;
		try {
			logger.info("Parsing Config File " + filename);
			dom = parser.parse(XMLConfigfiguredURIParametersCategorizer.class.getClassLoader().getResourceAsStream(filename));
			logger.info("Config file parsed successfully");
		} catch (SAXException e) {
			logger.error("Error parsing config file", e);
			throw new RuntimeException("Error parsing config file", e);
		} catch (IOException e) {
			logger.error("Unable to read config file", e);
			throw new RuntimeException("Unable to read config file", e);
		}
		
		setupParameters(dom);
		setupConfigurations(dom);
	}
	
	private void setupParameters(Document dom) {
		XPath xp = getXPath();
		
		try {
			XPathExpression igncontextxp = xp.compile("/n:uri-parameter-categorizer/n:ignore-context-path");
			if((Boolean) igncontextxp.evaluate(dom, XPathConstants.BOOLEAN)) {
				this.ignoreContextPath = Boolean.parseBoolean((String) igncontextxp.evaluate(dom, XPathConstants.STRING));
				logger.info("Ignore Context Path set to " + this.ignoreContextPath);
			} else {
				logger.info("Ignore Context Path defaulted to " + ignoreContextPath());
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		
		try {
			XPathExpression fallbackxp = xp.compile("/n:uri-parameter-categorizer/n:fall-back-to-uri-as-category");
			if((Boolean) fallbackxp.evaluate(dom, XPathConstants.BOOLEAN)) {
				this.fallBackToURIAsCategory = Boolean.parseBoolean((String) fallbackxp.evaluate(dom, XPathConstants.STRING));
				logger.info("Fall back to URI as Category set to " + this.fallBackToURIAsCategory);
			} else {
				logger.info("Fall back to URI as Category defaulted to " + fallBackToURIAsCategory());
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void setupConfigurations(Document dom) {
		XPath xp = getXPath();
		NodeList categories = null;
		try {
			categories = (NodeList) xp.compile("/n:uri-parameter-categorizer/n:category").evaluate(dom, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		
		XPathExpression catcodexp = null, urixp = null, descxp = null, parmxp = null;
		try {
			catcodexp = xp.compile("n:code");
			urixp = xp.compile("n:uri");
			descxp = xp.compile("n:description");
			parmxp = xp.compile("n:parms/n:parm");
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} 
		
		if(categories.getLength() > 0)
			logger.info("Adding Categories to Categorizer. " + categories.getLength() + " categories to be added");
		else
			logger.warn("No categories to be added to Categorizer");
		
		for(int cn = 0; cn < categories.getLength(); ++cn) {
			Element category = (Element) categories.item(cn);
			
			String catcode = null, uri = null, desc = null;
			List<ParameterValue> parmvals = new ArrayList<>();
			try {
				catcode = (String) catcodexp.evaluate(category, XPathConstants.STRING);
				if(catcode == null || "".equals(catcode.trim()))
					throw new RuntimeException("Category Code not set for Category number " + (cn+1));
				
				uri = (String) urixp.evaluate(category, XPathConstants.STRING);
				if(uri == null || "".equals(uri.trim()))
					throw new RuntimeException("URI not set for Category " + catcode);
				
				if((Boolean) descxp.evaluate(category, XPathConstants.BOOLEAN))
					desc = (String) descxp.evaluate(category, XPathConstants.STRING);
				NodeList parmvalnodes = (NodeList) parmxp.evaluate(category, XPathConstants.NODESET);
				for(int pn = 0; pn < parmvalnodes.getLength(); ++pn) {
					Element parm = (Element) parmvalnodes.item(pn);
					String parmname = parm.getAttribute("name");
					String parmvalue = parm.hasAttribute("value") ? parm.getAttribute("value") : null;
					if(parmname == null || "".equals(parmname))
						throw new RuntimeException("Parm name not set for Category " + catcode);
					
					parmvals.add(new ParameterValue(parmname, parmvalue));
				}
			} catch (XPathExpressionException e) {
				throw new RuntimeException(e);
			}
			
			addCategory(uri, parmvals, catcode, desc);
		}
		
	}
	
	private XPath getXPath() {
		XPath xp = XPathFactory.newInstance().newXPath();
		xp.setNamespaceContext(new NamespaceContext() {
			@Override
			public Iterator getPrefixes(String namespaceURI) {
				return null;
			}
			
			@Override
			public String getPrefix(String namespaceURI) {
				return null;
			}
			
			@Override
			public String getNamespaceURI(String prefix) {
				if("n".equals(prefix)) return "http://com.hamy.usagemonitor/xml/ns/1.0";
				return null;
			}
		});
		
		return xp;
	}
	
}
