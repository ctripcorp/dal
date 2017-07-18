package com.dal.sqlserver.test;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dal.sqlserver.test.control.WebContext;
import com.xrosstools.xunit.Processor;
import com.xrosstools.xunit.XunitFactory;

@WebServlet("/PeoplePortal")
public class XunitPeoplePortal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PeopleDao dao;
	private Processor demo;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			dao = new PeopleDao();
			demo = XunitFactory.load("dal_demo.xunit").getProcessor("main");
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		WebContext context = new WebContext(request, response, dao);
		try {
			demo.process(context);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
