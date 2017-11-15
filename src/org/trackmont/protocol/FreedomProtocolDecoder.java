/*
 * Copyright 2014 Vicente Venegas (vicente@republik.ec)
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

import org.jboss.netty.channel.Channel;
import org.trackmont.BaseProtocolDecoder;
import org.trackmont.DeviceSession;
import org.trackmont.helper.Parser;
import org.trackmont.helper.PatternBuilder;
import org.trackmont.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class FreedomProtocolDecoder extends BaseProtocolDecoder {

    public FreedomProtocolDecoder(FreedomProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("IMEI,")
            .number("(d+),")                     // imei
            .number("(dddd)/(dd)/(dd), ")        // date (yyyy/dd/mm)
            .number("(dd):(dd):(dd), ")          // time (hh:mm:ss)
            .expression("([NS]), ")
            .number("Lat:(dd)(d+.d+), ")         // latitude
            .expression("([EW]), ")
            .number("Lon:(ddd)(d+.d+), ")        // longitude
            .text("Spd:").number("(d+.d+)")      // speed
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        position.setValid(true);

        position.setTime(parser.nextDateTime());

        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));

        position.setSpeed(parser.nextDouble(0));

        return position;
    }

}
