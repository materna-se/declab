package de.materna.dmn.tester;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import de.materna.dmn.tester.beans.User;
import de.materna.dmn.tester.beans.repositories.UserRepository;
import de.materna.dmn.tester.servlets.MainApplication;
import de.materna.dmn.tester.servlets.workspace.WorkspaceServlet;
import de.materna.dmn.tester.sockets.MainSocket;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import java.nio.file.Paths;

public class StandaloneServer {
	private static final Logger log = Logger.getLogger(WorkspaceServlet.class);
	
	public static void main(String[] args) throws Exception {
		UserRepository userRepository = new UserRepository();
		
		User user = new User("georg.wolffgang@materna.de", "Shazzarr", "Georg", "Wolffgang", "pass", "bild1.jpg");
		User userLoaded = userRepository.findByUuid(user.getUuid()).get();
		userRepository.save(userLoaded != null ? userLoaded : user);
		
		User user2 = new User("mike.myers@materna.de", "Mikey", "Mike", "Myers", "päswoad", "bild2.jpg");
		userRepository.save(user2);

		System.setProperty("jboss.server.data.dir", "c:\\Users\\gwolffga\\declab");
		System.setProperty("jboss.server.webapp.dir", "c:\\Users\\gwolffga\\declab\\server\\src\\main\\webapp");

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);

		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");
		contextHandler.setBaseResource(new PathResource(Paths.get(System.getProperty("jboss.server.webapp.dir"))));
		contextHandler.setWelcomeFiles(new String[]{"index.html"});

		{
			ServletHolder resourceHolder = new ServletHolder(new DefaultServlet());
			contextHandler.addServlet(resourceHolder, "/*");
		}
		{
			ServletHolder servletHolder = new ServletHolder(new ServletContainer(new MainApplication()));
			contextHandler.addServlet(servletHolder, "/api/*");
		}
		{
			WebSocketServerContainerInitializer.configure(contextHandler, (servletContext, wsContainer) -> wsContainer.addEndpoint(MainSocket.class));
		}
		server.setHandler(contextHandler);

		server.start();
		log.info("Started!");

		org.h2.tools.Server.createWebServer("-webPort", "8081").start();
		log.info("h2!");

		server.join();
	}
}
