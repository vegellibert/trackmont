/*
 * Copyright 2016 Vicente Venegas (vicente@trackmont.com)
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
package org.trackmont.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trackmont.BaseEventHandler;
import org.trackmont.Context;
import org.trackmont.database.GeofenceManager;
import org.trackmont.model.Calendar;
import org.trackmont.model.Device;
import org.trackmont.model.Event;
import org.trackmont.model.Position;

public class GeofenceEventHandler extends BaseEventHandler {

    private GeofenceManager geofenceManager;

    public GeofenceEventHandler() {
        geofenceManager = Context.getGeofenceManager();
    }

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        Device device = Context.getIdentityManager().getById(position.getDeviceId());
        if (device == null) {
            return null;
        }
        if (!Context.getIdentityManager().isLatestPosition(position) || !position.getValid()) {
            return null;
        }

        List<Long> currentGeofences = geofenceManager.getCurrentDeviceGeofences(position);
        List<Long> oldGeofences = new ArrayList<>();
        if (device.getGeofenceIds() != null) {
            oldGeofences.addAll(device.getGeofenceIds());
        }
        List<Long> newGeofences = new ArrayList<>(currentGeofences);
        newGeofences.removeAll(oldGeofences);
        oldGeofences.removeAll(currentGeofences);

        device.setGeofenceIds(currentGeofences);

        Map<Event, Position> events = new HashMap<>();
        for (long geofenceId : newGeofences) {
            long calendarId = geofenceManager.getById(geofenceId).getCalendarId();
            Calendar calendar = calendarId != 0 ? Context.getCalendarManager().getById(calendarId) : null;
            if (calendar == null || calendar.checkMoment(position.getFixTime())) {
                Event event = new Event(Event.TYPE_GEOFENCE_ENTER, position.getDeviceId(), position.getId());
                event.setGeofenceId(geofenceId);
                events.put(event, position);
            }
        }
        for (long geofenceId : oldGeofences) {
            long calendarId = geofenceManager.getById(geofenceId).getCalendarId();
            Calendar calendar = calendarId != 0 ? Context.getCalendarManager().getById(calendarId) : null;
            if (calendar == null || calendar.checkMoment(position.getFixTime())) {
                Event event = new Event(Event.TYPE_GEOFENCE_EXIT, position.getDeviceId(), position.getId());
                event.setGeofenceId(geofenceId);
                events.put(event, position);
            }
        }
        return events;
    }
}
