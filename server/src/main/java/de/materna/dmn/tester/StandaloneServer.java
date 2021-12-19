package de.materna.dmn.tester;

import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import de.materna.dmn.tester.beans.repositories.UserRepository;
import de.materna.dmn.tester.servlets.MainApplication;
import de.materna.dmn.tester.sockets.MainSocket;

public class StandaloneServer {

	public static void main(String[] args) throws Exception {
		UserRepository userRepository = new UserRepository();

		boolean savedGeorg = userRepository.save("georg.wolffgang@materna.de", "Shazzarr", "Georg", "Wolffgang", "pass",
				"bild1.jpg");
		System.out.println("Saved new user Georg: " + savedGeorg);
		boolean savedMike = userRepository.save("mike.myers@materna.de", "Mikey", "Mike", "Myers", "päswoad",
				"bild2.jpg");
		System.out.println("Saved new user Mike: " + savedMike);

		System.setProperty("jboss.server.data.dir", "c:\\Users\\gwolffga\\declab");
		System.setProperty("jboss.server.webapp.dir", "c:\\Users\\gwolffga\\declab\\server\\src\\main\\webapp");

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);

		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");
		contextHandler.setBaseResource(new PathResource(Paths.get(System.getProperty("jboss.server.webapp.dir"))));
		contextHandler.setWelcomeFiles(new String[] { "index.html" });

		{
			ServletHolder resourceHolder = new ServletHolder(new DefaultServlet());
			contextHandler.addServlet(resourceHolder, "/*");
		}
		{
			ServletHolder servletHolder = new ServletHolder(new ServletContainer(new MainApplication()));
			contextHandler.addServlet(servletHolder, "/api/*");
		}
		{
			WebSocketServerContainerInitializer.configure(contextHandler,
					(servletContext, wsContainer) -> wsContainer.addEndpoint(MainSocket.class));
		}
		server.setHandler(contextHandler);

		server.start();
		System.out.println("Started!");

		org.h2.tools.Server.createWebServer("-webPort", "8081").start();
		System.out.println("h2!");

		server.join();
	}
}
