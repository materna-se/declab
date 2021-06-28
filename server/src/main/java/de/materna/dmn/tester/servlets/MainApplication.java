package de.materna.dmn.tester.servlets;

import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.exceptions.DefaultOptionsMethodExceptionMapper;
import de.materna.dmn.tester.servlets.exceptions.GeneralExceptionMapper;
import de.materna.dmn.tester.servlets.filters.CSRFFilter;
import de.materna.dmn.tester.servlets.filters.ReadAccessFilter;
import de.materna.dmn.tester.servlets.filters.WriteAccessFilter;
import de.materna.dmn.tester.servlets.challenges.ChallengeServlet;
import de.materna.dmn.tester.servlets.challenges.ModelChallengeServlet;
import de.materna.dmn.tester.servlets.input.InputServlet;
import de.materna.dmn.tester.servlets.model.ModelServlet;
import de.materna.dmn.tester.servlets.output.OutputServlet;
import de.materna.dmn.tester.servlets.playground.PlaygroundServlet;
import de.materna.dmn.tester.servlets.test.TestServlet;
import de.materna.dmn.tester.servlets.workspace.MetaWorkspaceServlet;
import de.materna.dmn.tester.servlets.workspace.WorkspaceServlet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class MainApplication extends Application {
	private Set<Object> singletons = new HashSet<>();
	private Set<Class<?>> classes = new HashSet<>();

	public MainApplication() throws IOException {
		// Before we initialize the endpoints, we'll initialize all workspaces.
		WorkspaceManager.getInstance().indexAll();

		singletons.add(new ModelServlet());
		singletons.add(new ChallengeServlet());
		singletons.add(new ModelChallengeServlet());
		singletons.add(new PlaygroundServlet());
		singletons.add(new InputServlet());
		singletons.add(new OutputServlet());
		singletons.add(new TestServlet());
		singletons.add(new MetaWorkspaceServlet());
		singletons.add(new WorkspaceServlet());

		classes.add(CSRFFilter.class);
		classes.add(ReadAccessFilter.class);
		classes.add(WriteAccessFilter.class);
		
		classes.add(DefaultOptionsMethodExceptionMapper.class);
		classes.add(GeneralExceptionMapper.class);
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