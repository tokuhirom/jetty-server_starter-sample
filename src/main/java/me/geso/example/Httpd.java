package me.geso.example;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.ServerConnector;

public class Httpd {
	public static void main(String[] args) throws Exception {
		int port = 18080;
		Server server = new Server();

		// server.addEventListener(new MyListener());
		ServerConnector connector = new ServerConnector(server);
		connector.setInheritChannel(true);
		connector.setPort(port);
		server.setConnectors(new Connector[]{connector});

		HandlerCollection handlers = new HandlerCollection();
		server.setHandler(handlers);

		Slf4jRequestLog requestLog = new Slf4jRequestLog();
		requestLog.setExtended(true);
		requestLog.setLogCookies(false);
		requestLog.setLogTimeZone("GMT");
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);
		handlers.addHandler(requestLogHandler);

		server.start();
		server.join();
	}

}

