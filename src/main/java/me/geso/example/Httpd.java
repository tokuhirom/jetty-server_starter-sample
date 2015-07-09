package me.geso.example;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Httpd {
	public static void main(String[] args) throws Exception {
		HttpConfiguration httpConfig = new HttpConfiguration();
		// do not send "Server" header
		httpConfig.setSendServerVersion( false );
		HttpConnectionFactory httpFactory = new HttpConnectionFactory( httpConfig );

		int port = Integer.valueOf(System.getProperty("jetty.port", "18080"));
		Server server = new Server();

		// server.addEventListener(new MyListener());
		ServerConnector connector = new ServerConnector(server, httpFactory);
		connector.setInheritChannel(true);
		connector.setPort(port);
		server.setConnectors(new Connector[]{connector});
		server.setStopAtShutdown(true);
		server.setStopTimeout(7_000);

		HandlerCollection handlers = new HandlerCollection();

		// enable access log
		if (Boolean.valueOf(System.getProperty("jetty.accessLog", "false"))) {
			Slf4jRequestLog requestLog = new Slf4jRequestLog();
			requestLog.setExtended(true);
			requestLog.setLogCookies(false);
			requestLog.setLogTimeZone("GMT");
			RequestLogHandler requestLogHandler = new RequestLogHandler();
			requestLogHandler.setRequestLog(requestLog);
			handlers.addHandler(requestLogHandler);
		}


		// set servlet
		ServletHolder servletHolder = new ServletHolder(HelloServlet.class);
		ServletContextHandler context = new ServletContextHandler(server, "/");
		context.addServlet(servletHolder, "/*");
		handlers.addHandler(context);

		// StatisticsHandler is required for 'setStopTimeout' method.
		StatisticsHandler statisticsHandler = new StatisticsHandler();
		statisticsHandler.setHandler(handlers);

		server.setHandler(statisticsHandler);

		// enable jmx
		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addEventListener(mbContainer);
		server.addBean(mbContainer);

		server.start();
		server.join();
	}

	public static class HelloServlet extends HttpServlet {
		public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			response.setContentType("text/plain; charset=utf-8");
			response.getWriter().println("Hello, world!");
		}
	}

}

