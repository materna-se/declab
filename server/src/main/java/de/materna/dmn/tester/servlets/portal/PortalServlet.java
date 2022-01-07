package de.materna.dmn.tester.servlets.portal;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserRepository;
import de.materna.dmn.tester.servlets.portal.dto.LoginRequest;
import de.materna.dmn.tester.servlets.portal.dto.RegisterRequest;
import de.materna.dmn.tester.servlets.portal.exceptions.EmailInUseException;
import de.materna.dmn.tester.servlets.portal.exceptions.UsernameInUseException;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/portal")
public class PortalServlet {
	UserRepository userRepository = new UserRepository();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response login(String body) {
		LoginRequest loginRequest = (LoginRequest) SerializationHelper.getInstance().toClass(body, LoginRequest.class);
		User user = userRepository.findByUsername(loginRequest.getUsername());
		if (user != null && user.getHash().equals(User.createHash(loginRequest.getPassword()))) {
			String uuid = UUID.randomUUID().toString();

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(uuid)).build();
		}
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response register(String body) throws UsernameInUseException, EmailInUseException {
		RegisterRequest registerRequest = (RegisterRequest) SerializationHelper.getInstance().toClass(body,
				RegisterRequest.class);
		String username = registerRequest.getUsername();
		String email = registerRequest.getEmail();
		String password = registerRequest.getPassword();
		if (userRepository.findByUsername(username) == null)
			throw new UsernameInUseException("The username is already in use : " + username, null);
		if (userRepository.findByEmail(registerRequest.getEmail()) == null)
			throw new EmailInUseException("The email is already in use : " + email, null);
		User newUser = userRepository.register(email, username, password);
		return newUser != null
				? Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(newUser))
						.build()
				: Response.status(Response.Status.NOT_MODIFIED).build();
	}
}