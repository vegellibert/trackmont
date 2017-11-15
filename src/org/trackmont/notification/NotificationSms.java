/*
 * Copyright 2017 Vicente Venegas (vicente@republik.ec)
 * Copyright 2017 Andrey Kunitsyn (andrey@trackmont.com)
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
package org.trackmont.notification;

import org.trackmont.Context;
import org.trackmont.model.Event;
import org.trackmont.model.Position;
import org.trackmont.model.User;

import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public final class NotificationSms {

    private NotificationSms() {
    }

    public static void sendSmsAsync(long userId, Event event, Position position) {
        User user = Context.getPermissionsManager().getUser(userId);
        if (Context.getSmppManager() != null && user.getPhone() != null) {
            Context.getStatisticsManager().registerSms();
            Context.getSmppManager().sendMessageAsync(user.getPhone(),
                    NotificationFormatter.formatSmsMessage(userId, event, position), false);
        }
    }

    public static void sendSmsSync(long userId, Event event, Position position) throws RecoverablePduException,
            UnrecoverablePduException, SmppTimeoutException, SmppChannelException, InterruptedException {
        User user = Context.getPermissionsManager().getUser(userId);
        if (Context.getSmppManager() != null && user.getPhone() != null) {
            Context.getStatisticsManager().registerSms();
            Context.getSmppManager().sendMessageSync(user.getPhone(),
                    NotificationFormatter.formatSmsMessage(userId, event, position), false);
        }
    }
}
