/*
 * Copyright 2015 Vicente Venegas (vicente@republik.ec)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trackmont.api.resource;

import org.trackmont.Context;
import org.trackmont.api.BaseResource;
import org.trackmont.model.Server;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource extends BaseResource {

    @PermitAll
    @GET
    public Server get() throws SQLException {
        return Context.getPermissionsManager().getServer();
    }

    @PUT
    public Response update(Server entity) throws SQLException {
        Context.getPermissionsManager().checkAdmin(getUserId());
        Context.getPermissionsManager().updateServer(entity);
        return Response.ok(entity).build();
    }

}
