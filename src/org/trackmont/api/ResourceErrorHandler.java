/*
 * Copyright 2015 - 2016 Vicente Venegas (vicente@republik.ec)
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
package org.trackmont.api;

import org.trackmont.helper.Log;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ResourceErrorHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException exception = (WebApplicationException) e;
            String message;
            if (exception.getCause() != null) {
                message = Log.exceptionStack(exception.getCause());
            } else {
                message = Log.exceptionStack(exception);
            }
            return Response.fromResponse(exception.getResponse()).entity(message).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(Log.exceptionStack(e)).build();
        }
    }

}
