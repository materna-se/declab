package de.materna.dmn.tester.servlets.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mindrot.jbcrypt.BCrypt;

import de.materna.dmn.tester.Database;
import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.laboratory.LaboratoryHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.permission.Permission;
import de.materna.dmn.tester.beans.permission.PermissionGroup;
import de.materna.dmn.tester.beans.permission.PermissionHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.rest.RestError;
import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.user.UserHibernateH2RepositoryImpl;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.beans.workspace.WorkspaceHibernateH2RepositoryImpl;
import de.materna.dmn.tester.enums.PermissionType;
import de.materna.dmn.tester.enums.RestErrorCode;
import de.materna.dmn.tester.enums.VisabilityType;
import de.materna.dmn.tester.interfaces.repositories.LaboratoryRepository;
import de.materna.dmn.tester.interfaces.repositories.PermissionRepository;
import de.materna.dmn.tester.interfaces.repositories.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.repositories.UserRepository;
import de.materna.dmn.tester.interfaces.repositories.WorkspaceRepository;
import de.materna.dmn.tester.servlets.exceptions.authorization.MissingRightsException;
import de.materna.dmn.tester.servlets.exceptions.database.LaboratoryNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.UserNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.database.WorkspaceNotFoundException;
import de.materna.dmn.tester.servlets.exceptions.registration.EmailInUseException;
import de.materna.dmn.tester.servlets.exceptions.registration.RegistrationFailureException;
import de.materna.dmn.tester.servlets.exceptions.registration.UsernameInUseException;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.CreateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.DeleteLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.ReadLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.laboratory.UpdateLaboratoryRequest;
import de.materna.dmn.tester.servlets.portal.dto.sessionToken.ReadSessionTokenRequest;
import de.materna.dmn.tester.servlets.portal.dto.sessionToken.UpdateSessionTokenRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ChangeEmailRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ChangePasswordRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ChangeSystemAdminStateRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.DeleteUserRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.LoginRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.ReadUserRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.RegisterRequest;
import de.materna.dmn.tester.servlets.portal.dto.user.UpdateUserRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.CreateWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.DeleteWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.ReadWorkspaceRequest;
import de.materna.dmn.tester.servlets.portal.dto.workspace.UpdateWorkspaceRequest;
import de.materna.jdec.serialization.SerializationHelper;
import io.jsonwebtoken.JwtException;

@Path("/portal")
public class PortalServlet {
	private final UserRepository userRepository = new UserHibernateH2RepositoryImpl();
	private final SessionTokenRepository sessionTokenRepository = new SessionTokenHibernateH2RepositoryImpl();
	private final LaboratoryRepository laboratoryRepository = new LaboratoryHibernateH2RepositoryImpl();
	private final WorkspaceRepository workspaceRepository = new WorkspaceHibernateH2RepositoryImpl();
	private final PermissionRepository permissionRepository = new PermissionHibernateH2RepositoryImpl();

	@POST
	@Path("/user/changeEmail")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeEmail(String body) {
		final ChangeEmailRequest changeEmailRequest = (ChangeEmailRequest) SerializationHelper.getInstance()
				.toClass(body, ChangeEmailRequest.class);

		User userCurrent = null, userTarget = null, userUpdated = null;
		SessionToken sessionToken = null;

		try {
			userTarget = userRepository.getByUuid(changeEmailRequest.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, changeEmailRequest.getUserUuid())).build();
		}

		try {
			sessionToken = sessionTokenRepository.getByJwt(changeEmailRequest.getJwt());
			userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final SessionTokenNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, changeEmailRequest.getJwt())).build();
		}

		if (userCurrent.isSystemAdmin() || userTarget.getUuid() == userCurrent.getUuid()) {
			userTarget.setEmail(changeEmailRequest.getEmail());
			try {
				userUpdated = userRepository.put(userTarget);
				return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
						.build();
			} catch (final UserNotFoundException e) {
				e.printStackTrace();
				return Response.status(Response.Status.NO_CONTENT).entity(new RestError(RestErrorCode.NOT_MODIFIED))
						.build();
			}
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();
	}

	@POST
	@Path("/user/changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(String body) {
		final ChangePasswordRequest changePasswordRequest = (ChangePasswordRequest) SerializationHelper.getInstance()
				.toClass(body, ChangePasswordRequest.class);

		User userCurrent = null, userTarget = null, userUpdated = null;
		SessionToken sessionToken = null;

		try {
			userTarget = userRepository.getByUuid(changePasswordRequest.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, changePasswordRequest.getUserUuid())).build();
		}

		try {
			sessionToken = sessionTokenRepository.getByJwt(changePasswordRequest.getJwt());
			userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final SessionTokenNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, changePasswordRequest.getJwt())).build();
		}

		if (userCurrent.isSystemAdmin() || userTarget.getUuid() == userCurrent.getUuid()
				&& BCrypt.checkpw(changePasswordRequest.getPasswordOld(), userTarget.getPassword())) {
			userTarget.setSalt(BCrypt.gensalt());
			userTarget.setPassword(BCrypt.hashpw(changePasswordRequest.getPasswordNew(), userTarget.getSalt()));
			try {
				userUpdated = userRepository.put(userTarget);
				return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
						.build();
			} catch (final UserNotFoundException e) {
				e.printStackTrace();
				return Response.status(Response.Status.NO_CONTENT).entity(new RestError(RestErrorCode.NOT_MODIFIED))
						.build();
			}
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();
	}

	@POST
	@Path("/user/changeSystemAdminState")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeSystemAdminState(String body) {
		final ChangeSystemAdminStateRequest changeSystemAdminStateRequest = (ChangeSystemAdminStateRequest) SerializationHelper
				.getInstance().toClass(body, ChangeSystemAdminStateRequest.class);

		User userCurrent = null, userTarget = null, userUpdated = null;
		SessionToken sessionToken = null;

		try {
			userTarget = userRepository.getByUuid(changeSystemAdminStateRequest.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, changeSystemAdminStateRequest.getUserUuid()))
					.build();
		}

		try {
			sessionToken = sessionTokenRepository.getByJwt(changeSystemAdminStateRequest.getJwt());
			userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final SessionTokenNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, changeSystemAdminStateRequest.getJwt())).build();
		}

		if (userCurrent.isSystemAdmin() && !"administrator".equals(userTarget.getUsername())) {
			userTarget.setSystemAdmin(changeSystemAdminStateRequest.isSystemAdmin());
			try {
				userUpdated = userRepository.put(userTarget);
			} catch (final UserNotFoundException e) {
				e.printStackTrace();
				return Response.status(Response.Status.NO_CONTENT).entity(new RestError(RestErrorCode.NOT_MODIFIED))
						.build();
			}
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
					.build();
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();
	}

	@POST
	@Path("/user/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(String body) {
		final DeleteUserRequest deleteUserRequest = (DeleteUserRequest) SerializationHelper.getInstance().toClass(body,
				DeleteUserRequest.class);
		User userFound = null, userCurrent = null;
		try {
			userFound = userRepository.getByUuid(deleteUserRequest.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, deleteUserRequest.getUserUuid())).build();
		}
		try {
			final SessionToken sessionToken = sessionTokenRepository.getByJwt(deleteUserRequest.getJwt());
			userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final UserNotFoundException | SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, deleteUserRequest.getJwt())).build();
		}
		if (userCurrent.isSystemAdmin() || userFound.getUuid() == userCurrent.getUuid()) {
			if (userRepository.delete(userFound)) {
				final String userUuid = deleteUserRequest.getUserUuid();

				final List<Permission> allUserPermissions = permissionRepository.getByUserUuid(userUuid);
				for (final Permission userPermission : allUserPermissions) {
					permissionRepository.delete(userPermission);
				}

				final List<Workspace> allWorkspaces = workspaceRepository.getByUser(userUuid);
				for (final Workspace workspace : allWorkspaces) {
					workspaceRepository.delete(workspace);
				}

				final List<Laboratory> allLaboratories = laboratoryRepository.getByUser(userUuid);
				for (final Laboratory laboratory : allLaboratories) {
					laboratoryRepository.delete(laboratory);
				}
			}
			return Response.status(Response.Status.OK).build();
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();
	}

	@POST
	@Path("/user/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String body) {
		final LoginRequest loginRequest = (LoginRequest) SerializationHelper.getInstance().toClass(body,
				LoginRequest.class);
		SessionToken sessionToken = null;
		try {
			final User user = userRepository.getByUsername(loginRequest.getUsername());
			if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
				return Response.status(Response.Status.UNAUTHORIZED).entity(new RestError(RestErrorCode.PASSWORD_WRONG))
						.build();
			}
			sessionToken = new SessionToken(user);
			sessionTokenRepository.put(sessionToken);
			return Response.status(Response.Status.OK)
					.entity(SerializationHelper.getInstance().toJSON(sessionToken.getJwt())).build();
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED).entity(new RestError(RestErrorCode.USERNAME_NOT_FOUND))
					.build();
		} catch (final SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_CREATED, sessionToken.getUuid())).build();
		}
	}

	@POST
	@Path("/user/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readUser(String body) {
		final ReadUserRequest readUserRequest = (ReadUserRequest) SerializationHelper.getInstance().toClass(body,
				ReadUserRequest.class);

		User userFound = null, userCurrent = null;
		SessionToken sessionToken = null;

		try {
			userFound = userRepository.getByUuid(readUserRequest.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, readUserRequest.getUserUuid())).build();
		}

		try {
			sessionToken = sessionTokenRepository.getByJwt(readUserRequest.getJwt());
			userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final JwtException | SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, readUserRequest.getJwt())).build();
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, sessionToken.getUserUuid())).build();
		}

		if (userFound.getUuid() == userCurrent.getUuid() || userCurrent.isSystemAdmin()) {
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userFound))
					.build();
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();

	}

	@POST
	@Path("/user/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(String body) {
		final RegisterRequest registerRequest = (RegisterRequest) SerializationHelper.getInstance().toClass(body,
				RegisterRequest.class);

		User newUser = null;

		try {
			newUser = userRepository.register(registerRequest.getEmail(), registerRequest.getUsername(),
					registerRequest.getPassword());
			final Laboratory laboratory = laboratoryRepository.create(Database.MY_LAB.get("NAME"),
					Database.MY_LAB.get("DESCRIPTION"), VisabilityType.PRIVATE);
			permissionRepository.create(newUser.getUuid(), laboratory.getUuid(), null, PermissionType.OWNER);
		} catch (final EmailInUseException e) {
			e.printStackTrace();
			return Response.status(Response.Status.CONFLICT).entity(new RestError(RestErrorCode.USERNAME_TAKEN))
					.build();
		} catch (final UsernameInUseException e) {
			e.printStackTrace();
			return Response.status(Response.Status.CONFLICT).entity(new RestError(RestErrorCode.EMAIL_TAKEN)).build();
		} catch (final RegistrationFailureException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT).entity(new RestError(RestErrorCode.NOT_MODIFIED))
					.build();
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.CREATED).entity(SerializationHelper.getInstance().toJSON(newUser))
				.build();
	}

	@POST
	@Path("/user/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(String body) {
		final UpdateUserRequest updateUserRequest = (UpdateUserRequest) SerializationHelper.getInstance().toClass(body,
				UpdateUserRequest.class);

		User userTarget = null, userCurrent = null, userUpdated = null;
		SessionToken sessionToken = null;

		try {
			userTarget = userRepository.getByUuid(updateUserRequest.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, updateUserRequest.getUserUuid())).build();
		}

		try {
			sessionToken = sessionTokenRepository.getByJwt(updateUserRequest.getJwt());
			userCurrent = userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final JwtException | SessionTokenNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, updateUserRequest.getJwt())).build();
		}

		if (userCurrent.isSystemAdmin() || userTarget.getUuid() == userCurrent.getUuid()) {
			try {
				userTarget.setUsername(updateUserRequest.getUsername());
				userTarget.setLastname(updateUserRequest.getLastname());
				userTarget.setFirstname(updateUserRequest.getFirstname());
				userUpdated = userRepository.put(userTarget);

				return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(userUpdated))
						.build();
			} catch (final UserNotFoundException e) {
				e.printStackTrace();
				return Response.status(Response.Status.NO_CONTENT)
						.entity(new RestError(RestErrorCode.NOT_FOUND, updateUserRequest.getUserUuid())).build();
			}
		}
		return Response.status(Response.Status.FORBIDDEN).entity(new RestError(RestErrorCode.MISSING_RIGHTS)).build();
	}

	@POST
	@Path("/sessiontoken/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readSessionToken(String body) {
		final ReadSessionTokenRequest readSessionTokenRequest = (ReadSessionTokenRequest) SerializationHelper
				.getInstance().toClass(body, ReadSessionTokenRequest.class);

		SessionToken sessionTokenFound = null;
		try {
			sessionTokenFound = sessionTokenRepository.getByJwt(readSessionTokenRequest.getJwt());
		} catch (final JwtException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, readSessionTokenRequest.getJwt())).build();
		} catch (final SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, readSessionTokenRequest.getJwt())).build();
		}

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sessionTokenFound))
				.build();
	}

	@POST
	@Path("/sessiontoken/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSessionToken(String body) {
		final UpdateSessionTokenRequest updateSessionTokenRequest = (UpdateSessionTokenRequest) SerializationHelper
				.getInstance().toClass(body, UpdateSessionTokenRequest.class);

		SessionToken sessionTokenFound = null, sessionTokenUpdated = null;

		try {
			sessionTokenFound = sessionTokenRepository.getByJwt(updateSessionTokenRequest.getJwt());
		} catch (final JwtException | SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, updateSessionTokenRequest.getJwt())).build();
		}

		try {
			sessionTokenUpdated = sessionTokenRepository.update(sessionTokenFound);
		} catch (final SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_MODIFIED)
					.entity(new RestError(RestErrorCode.NOT_MODIFIED, updateSessionTokenRequest.getJwt())).build();
		}

		return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(sessionTokenUpdated))
				.build();
	}

	@POST
	@Path("/laboratory/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLaboratory(String body) {
		final CreateLaboratoryRequest createLaboratoryRequest = (CreateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, CreateLaboratoryRequest.class);

		Laboratory laboratoryNew = null;
		SessionToken sessionToken = null;
		try {
			sessionToken = sessionTokenRepository.getByJwt(createLaboratoryRequest.getJwt());
		} catch (final JwtException | SessionTokenNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, createLaboratoryRequest.getJwt())).build();
		}

		try {
			userRepository.getByUuid(sessionToken.getUserUuid());
		} catch (final UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, sessionToken.getUserUuid())).build();
		}

		try {
			laboratoryNew = laboratoryRepository.create(createLaboratoryRequest.getName(),
					createLaboratoryRequest.getDescription(), createLaboratoryRequest.getVisability());
			return Response.status(Response.Status.CREATED)
					.entity(SerializationHelper.getInstance().toJSON(laboratoryNew)).build();
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_MODIFIED)
					.entity(new RestError(RestErrorCode.NOT_CREATED, createLaboratoryRequest.getName())).build();
		}
	}

	@POST
	@Path("/laboratory/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteLaboratory(String body) {
		final DeleteLaboratoryRequest deleteLaboratoryRequest = (DeleteLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, DeleteLaboratoryRequest.class);

		Laboratory laboratory;
		try {
			laboratory = laboratoryRepository.getByUuid(deleteLaboratoryRequest.getLaboratoryUuid());
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, deleteLaboratoryRequest.getLaboratoryUuid()))
					.build();
		}

		try {
			checkUserPermission(laboratory.getUuid(), deleteLaboratoryRequest.getJwt(), PermissionGroup.ADMINISTRATOR);
		} catch (final MissingRightsException e) {
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN)
					.entity(new RestError(RestErrorCode.MISSING_RIGHTS, laboratory.getUuid())).build();
		}

		if (laboratoryRepository.delete(laboratory)) {
			final List<Permission> allUserPermissions = permissionRepository.getByLaboratoryUuid(laboratory.getUuid());
			for (final Permission userPermission : allUserPermissions) {
				permissionRepository.delete(userPermission);
			}
			return Response.status(Response.Status.OK).build();
		}
		return Response.status(Response.Status.NOT_MODIFIED)
				.entity(new RestError(RestErrorCode.NOT_MODIFIED, laboratory.getUuid())).build();
	}

	@POST
	@Path("/laboratory/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readLaboratory(String body) {
		final ReadLaboratoryRequest readLaboratoryRequest = (ReadLaboratoryRequest) SerializationHelper.getInstance()
				.toClass(body, ReadLaboratoryRequest.class);

		if (readLaboratoryRequest.getLaboratoryUuid() == null) {
			User userCurrent = null;

			try {
				userCurrent = userRepository.getByJwt(readLaboratoryRequest.getJwt());
			} catch (JwtException | UserNotFoundException | SessionTokenNotFoundException e) {
				e.printStackTrace();
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity(new RestError(RestErrorCode.BAD_JWT, readLaboratoryRequest.getJwt())).build();
			}
			final List<Laboratory> laboratories = laboratoryRepository.getByUser(userCurrent.getUuid());

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(laboratories))
					.build();
		}

		try {
			final Laboratory laboratory = laboratoryRepository.getByUuid(readLaboratoryRequest.getLaboratoryUuid());
			checkUserPermission(laboratory.getUuid(), readLaboratoryRequest.getJwt(), PermissionGroup.GUEST);

			return Response.status(Response.Status.OK)
					.entity(SerializationHelper.getInstance().toJSON(new ArrayList<>().add(laboratory))).build();
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, readLaboratoryRequest.getLaboratoryUuid())).build();
		} catch (final MissingRightsException e) {
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN)
					.entity(new RestError(RestErrorCode.MISSING_RIGHTS, readLaboratoryRequest.getLaboratoryUuid()))
					.build();
		}
	}

	@POST
	@Path("/laboratory/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLaboratory(String body) {
		final UpdateLaboratoryRequest updateLaboratoryRequest = (UpdateLaboratoryRequest) SerializationHelper
				.getInstance().toClass(body, UpdateLaboratoryRequest.class);

		Laboratory laboratory = null;

		try {
			laboratory = laboratoryRepository.getByUuid(updateLaboratoryRequest.getLaboratoryUuid());
			checkUserPermission(laboratory.getUuid(), updateLaboratoryRequest.getJwt(), PermissionGroup.CONTRIBUTOR);

			laboratory.setName(updateLaboratoryRequest.getName());
			laboratory.setDescription(updateLaboratoryRequest.getDescription());
			laboratory.setVisability(updateLaboratoryRequest.getVisability());
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, updateLaboratoryRequest.getLaboratoryUuid()))
					.build();
		} catch (final MissingRightsException e) {
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN)
					.entity(new RestError(RestErrorCode.MISSING_RIGHTS, updateLaboratoryRequest.getLaboratoryUuid()))
					.build();
		}
		try {
			final Laboratory laboratoryUpdated = laboratoryRepository.put(laboratory);
			return Response.status(Response.Status.OK)
					.entity(SerializationHelper.getInstance().toJSON(laboratoryUpdated)).build();
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_CREATED, laboratory.getUuid())).build();
		}

	}

	@POST
	@Path("/workspace/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createWorkspace(String body) {
		final CreateWorkspaceRequest createWorkspaceRequest = (CreateWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, CreateWorkspaceRequest.class);

		SessionToken sessionToken;
		try {
			sessionToken = sessionTokenRepository.getByJwt(createWorkspaceRequest.getJwt());
			userRepository.getByUuid(sessionToken.getUserUuid());
			final String laboratoryUuid = createWorkspaceRequest.getLaboratoryUuid();
			laboratoryRepository.getByUuid(laboratoryUuid);

			final Workspace workspaceNew = workspaceRepository.create(createWorkspaceRequest.getName(),
					createWorkspaceRequest.getDescription(), createWorkspaceRequest.getVisability(), laboratoryUuid);

			return Response.status(Response.Status.CREATED)
					.entity(SerializationHelper.getInstance().toJSON(workspaceNew)).build();
		} catch (final JwtException | SessionTokenNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new RestError(RestErrorCode.BAD_JWT, createWorkspaceRequest.getJwt())).build();
		} catch (final LaboratoryNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, createWorkspaceRequest.getLaboratoryUuid())).build();
		} catch (final WorkspaceNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_CREATED, createWorkspaceRequest.getName())).build();
		}
	}

	@POST
	@Path("/workspace/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWorkspace(String body) {
		final DeleteWorkspaceRequest deleteWorkspaceRequest = (DeleteWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, DeleteWorkspaceRequest.class);

		Workspace workspace = null;

		try {
			workspace = workspaceRepository.getByUuid(deleteWorkspaceRequest.getWorkspaceUuid());
			checkUserPermission(workspace.getUuid(), deleteWorkspaceRequest.getJwt(), PermissionGroup.ADMINISTRATOR);
		} catch (final WorkspaceNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, deleteWorkspaceRequest.getWorkspaceUuid())).build();
		} catch (final MissingRightsException e1) {
			try {
				checkUserPermission(workspace.getLaboratoryUuid(), deleteWorkspaceRequest.getJwt(),
						PermissionGroup.ADMINISTRATOR);
			} catch (final MissingRightsException e2) {
				e1.printStackTrace();
				return Response.status(Response.Status.FORBIDDEN)
						.entity(new RestError(RestErrorCode.MISSING_RIGHTS, deleteWorkspaceRequest.getWorkspaceUuid()))
						.build();
			}
		}

		if (workspaceRepository.delete(workspace)) {
			final List<Permission> allUserPermissions = permissionRepository
					.getByWorkspaceUuid(deleteWorkspaceRequest.getWorkspaceUuid());
			for (final Permission userPermission : allUserPermissions) {
				permissionRepository.delete(userPermission);
			}
			return Response.status(Response.Status.OK).build();
		}
		return Response.status(Response.Status.NOT_MODIFIED)
				.entity(new RestError(RestErrorCode.NOT_MODIFIED, deleteWorkspaceRequest.getWorkspaceUuid())).build();
	}

	@POST
	@Path("/workspace/read")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response readWorkspace(String body) {
		final ReadWorkspaceRequest readWorkspaceRequest = (ReadWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, ReadWorkspaceRequest.class);

		final List<Workspace> workspaces = new ArrayList<>();
		Workspace workspace = null;

		if (readWorkspaceRequest.getWorkspaceUuid() == null) { // not only one single workspace
			try {

				final User userCurrent = userRepository.getByJwt(readWorkspaceRequest.getJwt());
				final List<Laboratory> laboratories = laboratoryRepository.getByUser(userCurrent.getUuid());
				for (int lab = 0; lab < laboratories.size(); lab++) {
					final List<Permission> permissions = permissionRepository
							.getByLaboratoryUuid(laboratories.get(lab).getUuid());
					for (int permission = 0; permission < permissions.size(); permission++) {
						final String workspaceUuid = permissions.get(permission).getWorkspaceUuid();
						if (workspaceUuid != null) {
							try {
								workspaces.add(workspaceRepository.getByUuid(workspaceUuid));
							} catch (final WorkspaceNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				}
				return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspaces))
						.build();
			} catch (JwtException | UserNotFoundException | SessionTokenNotFoundException e) {
				e.printStackTrace();
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity(new RestError(RestErrorCode.BAD_JWT, readWorkspaceRequest.getJwt())).build();
			}
		}

		try {
			workspace = workspaceRepository.getByUuid(readWorkspaceRequest.getWorkspaceUuid());
			checkUserPermission(workspace.getUuid(), readWorkspaceRequest.getJwt(), PermissionGroup.GUEST);
			workspaces.add(workspace);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(workspaces))
					.build();
		} catch (final WorkspaceNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, readWorkspaceRequest.getWorkspaceUuid())).build();
		} catch (final MissingRightsException e) {
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN)
					.entity(new RestError(RestErrorCode.MISSING_RIGHTS, readWorkspaceRequest.getWorkspaceUuid()))
					.build();
		}
	}

	@POST
	@Path("/workspace/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateWorkspace(String body) {
		final UpdateWorkspaceRequest updateWorkspaceRequest = (UpdateWorkspaceRequest) SerializationHelper.getInstance()
				.toClass(body, UpdateWorkspaceRequest.class);

		Workspace workspace = null, updatedWorkspace = null;

		try {
			workspace = workspaceRepository.getByUuid(updateWorkspaceRequest.getWorkspaceUuid());
			checkUserPermission(workspace.getUuid(), updateWorkspaceRequest.getJwt(), PermissionGroup.CONTRIBUTOR);
			workspace.setName(updateWorkspaceRequest.getName());
			workspace.setDescription(updateWorkspaceRequest.getDescription());
			workspace.setVisability(updateWorkspaceRequest.getVisability());
		} catch (final WorkspaceNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_FOUND, updateWorkspaceRequest.getWorkspaceUuid())).build();
		} catch (final MissingRightsException e) {
			e.printStackTrace();
			return Response.status(Response.Status.FORBIDDEN)
					.entity(new RestError(RestErrorCode.MISSING_RIGHTS, updateWorkspaceRequest.getWorkspaceUuid()))
					.build();
		}

		try {
			updatedWorkspace = workspaceRepository.put(workspace);
			return Response.status(Response.Status.OK)
					.entity(SerializationHelper.getInstance().toJSON(updatedWorkspace)).build();
		} catch (final WorkspaceNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NO_CONTENT)
					.entity(new RestError(RestErrorCode.NOT_CREATED, workspace.getUuid())).build();
		}
	}

	private void checkUserPermission(String targetUuid, String jwt, PermissionType[] userPermissionGroup)
			throws MissingRightsException {

		SessionToken sessionToken = null;

		try {
			sessionToken = sessionTokenRepository.getByJwt(jwt);
			final String userUuid = sessionToken.getUserUuid();
			if (!userRepository.getByUuid(userUuid).isSystemAdmin()) {
				final Permission userPermissionLaboratory = permissionRepository.getByUserAndLaboratoryUuids(userUuid,
						targetUuid);
				if (userPermissionLaboratory == null
						|| !Arrays.asList(userPermissionGroup).contains(userPermissionLaboratory.getType())) {
					final Permission userPermissionWorkspace = permissionRepository.getByUserAndWorkspaceUuids(userUuid,
							targetUuid);
					if (userPermissionWorkspace == null
							|| !Arrays.asList(userPermissionGroup).contains(userPermissionWorkspace.getType())) {
						throw new MissingRightsException("User is missing rights : " + targetUuid);
					}
				}
			}
		} catch (JwtException | SessionTokenNotFoundException | UserNotFoundException e) {
			e.printStackTrace();
			throw new MissingRightsException("User is missing rights : " + targetUuid);
		}
	}
}