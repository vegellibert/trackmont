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

import java.util.Collections;
import java.util.Map;

import org.trackmont.BaseEventHandler;
import org.trackmont.model.Event;
import org.trackmont.model.Position;

public class AlertEventHandler extends BaseEventHandler {

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        Object alarm = position.getAttributes().get(Position.KEY_ALARM);
        if (alarm != null) {
            Event event = new Event(Event.TYPE_ALARM, position.getDeviceId(), position.getId());
            event.set(Position.KEY_ALARM, (String) alarm);
            return Collections.singletonMap(event, position);
        }
        return null;
    }

}
