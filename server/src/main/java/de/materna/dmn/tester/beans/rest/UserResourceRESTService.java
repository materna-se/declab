/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.materna.dmn.tester.beans.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 */
@Path("/user-images")
@RequestScoped
public class UserResourceRESTService {

	private final static String PATH = "./declab/img/";

	@GET
	@Path("/{filename}")
	@Produces("image/jpg")
	public Response showUserImageTag(@PathParam("filename") String filename) {
		final String filepath = PATH + filename;
		try {
			return Response.ok(Files.readAllBytes(Paths.get(filepath))).build();
		} catch (final IOException e) {
			return Response.status(404).build();
		}
	}
}