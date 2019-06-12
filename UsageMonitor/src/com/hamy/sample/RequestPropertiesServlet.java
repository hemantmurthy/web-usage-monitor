package com.hamy.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 6736189608501591323L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doRequest(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doRequest(request, response);
	}
	
	private void doRequest(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Request Properties</title></head><body>");

		out.println("<table><tr><th>Request Method</th><th>Value</th></tr>");
		out.println("<tr><td>getProtocol()</td><td>" + request.getProtocol() + "</td></tr>");
		out.println("<tr><td>getMethod()</td><td>" + request.getMethod() + "</td></tr>");
		out.println("<tr><td>getRequestURL()</td><td>" + request.getRequestURL() + "</td></tr>");
		out.println("<tr><td>getRequestURI()</td><td>" + request.getRequestURI() + "</td></tr>");
		out.println("<tr><td>getPathInfo()</td><td>" + request.getPathInfo() + "</td></tr>");
		out.println("<tr><td>getContextPath()</td><td>" + request.getContextPath() + "</td></tr>");
		out.println("<tr><td>getPathTranslated()</td><td>" + request.getPathTranslated() + "</td></tr>");
		out.println("<tr><td>getServletPath()</td><td>" + request.getServletPath() + "</td></tr>");
		out.println("</table>");

		out.println("<table><tr><th>Property</th><th>Seq</th><th>Value</th></tr>");
		Map<String, String[]> p = request.getParameterMap();
		for(String pn : p.keySet()) {
			int seq = 1;
			for(String v : p.get(pn)) {
				out.println("<tr><td>" + (seq == 1 ? pn : "") + "</td><td>" + seq + "</td><td>" + v + "</td></tr>");
				++seq;
			}
		}
		out.println("</table>");
		out.println("<div>");
		out.println("<form action=\"postreq?formid=12345\" method=\"POST\">XYZ <input type=\"text\" name=\"xyz\"><input type=\"submit\" value=\"Submit\"></form>");
		out.println("</div>");
		out.println("<a href=\"home?abc=1234&abc=abcdef&pqr=aabbcc\">Home</a>");
		out.println("<a href=\"accounts?userid=XYZ\">Accounts</a>");
		out.println("<a href=\"servicepoints?accountid=123123123123\">Service Points</a>");
		out.println("<a href=\"meters?spid=876234876234\">Meters</a>");
		out.println("<a href=\"logout\">Logout</a>");
		out.println("</body></html>");
		
	}
}
