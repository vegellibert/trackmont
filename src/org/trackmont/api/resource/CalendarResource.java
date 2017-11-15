/*
 * Copyright 2016 - 2017 Vicente Venegas (vicente@republik.ec)
 * Copyright 2016 - 2017 Andrey Kunitsyn (andrey@trackmont.com)
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

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.trackmont.api.SimpleObjectResource;
import org.trackmont.model.Calendar;

@Path("calendars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CalendarResource extends SimpleObjectResource<Calendar> {

    public CalendarResource() {
        super(Calendar.class);
    }

}
