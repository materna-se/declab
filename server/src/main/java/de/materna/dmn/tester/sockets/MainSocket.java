package de.materna.dmn.tester.sockets;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import de.materna.dmn.tester.sockets.managers.SessionManager;

@ServerEndpoint("/sockets/{workspace}")
public class MainSocket {
	@OnOpen
	public void onOpen(@PathParam("workspace") String workspace, Session session) {
		final SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.add(workspace, session);
		sessionManager.notify(workspace,
				"{\"type\": \"listeners\", \"data\": " + sessionManager.listeners(workspace) + "}");
	}

	@OnClose
	public void onClose(@PathParam("workspace") String workspace, Session session) {
		final SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.remove(workspace, session);
		sessionManager.notify(workspace,
				"{\"type\": \"listeners\", \"data\": " + sessionManager.listeners(workspace) + "}");
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.out.println("onError" + throwable.getMessage());
	}
}
