package de.materna.dmn.tester.sockets;

import de.materna.dmn.tester.sockets.managers.SessionManager;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/sockets/{workspace}")
public class MainSocket {
	@OnOpen
	public void onOpen(@PathParam("workspace") String workspace, Session session) throws InterruptedException {
		SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.add(workspace, session);
		sessionManager.notify(workspace, "{\"type\": \"listeners\", \"data\": " + sessionManager.listeners(workspace) + "}");
	}

	@OnClose
	public void onClose(@PathParam("workspace") String workspace, Session session) throws InterruptedException {
		SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.remove(workspace, session);
		sessionManager.notify(workspace, "{\"type\": \"listeners\", \"data\": " + sessionManager.listeners(workspace) + "}");
	}

	@OnError
	public void onError(@PathParam("workspace") String workspace, Session session, Throwable throwable) throws InterruptedException {
		System.out.println("onError" + throwable.getMessage());

		SessionManager sessionManager = SessionManager.getInstance();
		sessionManager.remove(workspace, session);
		sessionManager.notify(workspace, "{\"type\": \"listeners\", \"data\": " + sessionManager.listeners(workspace) + "}");
	}
}
