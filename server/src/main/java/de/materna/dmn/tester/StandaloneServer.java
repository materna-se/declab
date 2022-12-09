package de.materna.dmn.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.materna.dmn.tester.servlets.MainApplication;
import de.materna.dmn.tester.servlets.workspace.WorkspaceServlet;
import de.materna.dmn.tester.sockets.MainSocket;

public class StandaloneServer {
	private static final Logger log = LoggerFactory.getLogger(WorkspaceServlet.class);

	public static void main(String[] args) throws Exception {
		System.out.println("     _           _       _     ");
		System.out.println("  __| | ___  ___| | __ _| |__  ");
		System.out.println(" / _` |/ _ \\/ __| |/ _` | '_ \\ ");
		System.out.println("| (_| |  __/ (__| | (_| | |_) |");
		System.out.println(" \\__,_|\\___|\\___|_|\\__,_|_.__/ ");

		log.info("Let's start...");
		log.info("We'll use the detected Java version \"{}\".", System.getProperty("java.version"));

		final URI resourcePath = StandaloneServer.class.getClassLoader().getResource("logback.xml").toURI();
		final URI normalizedResourcePath = new URI(
				resourcePath.toString().substring(0, resourcePath.toString().length() - 10));
		log.info("We'll load all resources from the directory \"{}\".", normalizedResourcePath);

		URI configurationPath = null;
		if (normalizedResourcePath.toString().startsWith("jar:")) {
			// We're running inside a .jar.
			log.info("Detected packaged execution...");
			configurationPath = new URI(
					normalizedResourcePath.toString().substring(4, normalizedResourcePath.toString().indexOf('!')))
					.resolve("..");
		} else {
			configurationPath = normalizedResourcePath.resolve("..");
		}
		log.info("We'll try to load the configuration from the directory \"{}\".", configurationPath);

		final Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configurationPath.resolve("declab.properties"))));
			log.info("The configuration could be loaded: \"{}\".", properties);
		} catch (final IOException e) {
			log.info("The configuration couldn't be loaded: \"{}\".", e.getMessage());
		}

		final String homeDirectory = properties.getProperty("persistence.directory",
				Paths.get(System.getProperty("user.home"), ".declab").toString());
		log.info("We'll use the home directory \"{}\" to persist the workspaces.", homeDirectory);
		System.setProperty("jboss.server.data.dir", homeDirectory);

		final Server server = new Server();
		final ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);

		final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");
		contextHandler.setBaseResource(Resource.newResource(normalizedResourcePath));
		contextHandler.setWelcomeFiles(new String[] { "index.html" });

		final ServletHolder resourceHolder = new ServletHolder(new DefaultServlet());
		contextHandler.addServlet(resourceHolder, "/*");

		final ServletHolder servletHolder = new ServletHolder(
				new ServletContainer(ResourceConfig.forApplication(new MainApplication())));
		contextHandler.addServlet(servletHolder, "/api/*");

		WebSocketServerContainerInitializer.configure(contextHandler,
				(servletContext, wsContainer) -> wsContainer.addEndpoint(MainSocket.class));

		server.setHandler(contextHandler);

		server.start();

		Database.prepare();
		log.info("H2: The database has been prepared successfully.");

		log.info("The server has been started successfully.");
//		if (Desktop.isDesktopSupported()) {
//			Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080"));
//		}

		server.join();
	}
}