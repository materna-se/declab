package de.materna.dmn.tester;

import de.materna.dmn.tester.servlets.filters.CSRFFilter;
import de.materna.dmn.tester.servlets.workspace.WorkspaceServlet;
import de.materna.dmn.tester.servlets.input.InputServlet;
import de.materna.dmn.tester.servlets.model.ModelServlet;
import de.materna.dmn.tester.servlets.output.OutputServlet;
import de.materna.dmn.tester.servlets.test.TestServlet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class MainApplication extends Application {
	private Set<Object> singletons = new HashSet<>();
	private Set<Class<?>> classes = new HashSet<>();

	public MainApplication() {
		singletons.add(new ModelServlet());
		singletons.add(new InputServlet());
		singletons.add(new OutputServlet());
		singletons.add(new TestServlet());
		singletons.add(new WorkspaceServlet());

		classes.add(CSRFFilter.class);
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}