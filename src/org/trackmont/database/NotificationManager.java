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
package org.trackmont.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.trackmont.Context;
import org.trackmont.helper.Log;
import org.trackmont.model.Event;
import org.trackmont.model.Notification;
import org.trackmont.model.Position;
import org.trackmont.model.Typed;
import org.trackmont.notification.NotificationMail;
import org.trackmont.notification.NotificationSms;

public class NotificationManager extends ExtendedObjectManager<Notification> {

    public NotificationManager(DataManager dataManager) {
        super(dataManager, Notification.class);
    }

    private Set<Long> getEffectiveNotifications(long userId, long deviceId) {
        Set<Long> result = new HashSet<>();
        Set<Long> deviceNotifications = getAllDeviceItems(deviceId);
        for (long itemId : getUserItems(userId)) {
            if (getById(itemId).getAlways() || deviceNotifications.contains(itemId)) {
                result.add(itemId);
            }
        }
        return result;
    }

    public void updateEvent(Event event, Position position) {
        try {
            getDataManager().addObject(event);
        } catch (SQLException error) {
            Log.warning(error);
        }

        long deviceId = event.getDeviceId();
        Set<Long> users = Context.getPermissionsManager().getDeviceUsers(deviceId);
        for (long userId : users) {
            if (event.getGeofenceId() == 0 || Context.getGeofenceManager() != null
                    && Context.getGeofenceManager().checkItemPermission(userId, event.getGeofenceId())) {
                boolean sentWeb = false;
                boolean sentMail = false;
                boolean sentSms = Context.getSmppManager() == null;
                for (long notificationId : getEffectiveNotifications(userId, deviceId)) {
                    Notification notification = getById(notificationId);
                    if (getById(notificationId).getType().equals(event.getType())) {
                        if (!sentWeb && notification.getWeb()) {
                            Context.getConnectionManager().updateEvent(userId, event);
                            sentWeb = true;
                        }
                        if (!sentMail && notification.getMail()) {
                            NotificationMail.sendMailAsync(userId, event, position);
                            sentMail = true;
                        }
                        if (!sentSms && notification.getSms()) {
                            NotificationSms.sendSmsAsync(userId, event, position);
                            sentSms = true;
                        }
                    }
                    if (sentWeb && sentMail && sentSms) {
                        break;
                    }
                }
            }
        }
        if (Context.getEventForwarder() != null) {
            Context.getEventForwarder().forwardEvent(event, position);
        }
    }

    public void updateEvents(Map<Event, Position> events) {
        for (Entry<Event, Position> event : events.entrySet()) {
            updateEvent(event.getKey(), event.getValue());
        }
    }

    public Set<Typed> getAllNotificationTypes() {
        Set<Typed> types = new HashSet<>();
        Field[] fields = Event.class.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("TYPE_")) {
                try {
                    types.add(new Typed(field.get(null).toString()));
                } catch (IllegalArgumentException | IllegalAccessException error) {
                    Log.warning(error);
                }
            }
        }
        return types;
    }
}
