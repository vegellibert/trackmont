/*
 * Copyright 2017 Vicente Venegas (vicente@trackmont.com)
 * Copyright 2017 Anatoliy Golubev (darth.naihil@gmail.com)
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
package org.trackmont.protocol;

import org.trackmont.StringProtocolEncoder;
import org.trackmont.helper.Log;
import org.trackmont.model.Command;

public class AdmProtocolEncoder extends StringProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_GET_DEVICE_STATUS:
                return formatCommand(command, "STATUS\r\n");

            case Command.TYPE_CUSTOM:
                return formatCommand(command, "{%s}\r\n", Command.KEY_DATA);

            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }
}
