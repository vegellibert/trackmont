/*
 * Copyright 2016 Nyash (nyashh@gmail.com)
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
package org.trackmont.protocol;

import org.jboss.netty.channel.Channel;
import org.trackmont.BaseProtocolDecoder;
import org.trackmont.DeviceSession;
import org.trackmont.helper.Parser;
import org.trackmont.helper.PatternBuilder;
import org.trackmont.model.CellTower;
import org.trackmont.model.Network;
import org.trackmont.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class JpKorjarProtocolDecoder extends BaseProtocolDecoder {

    public JpKorjarProtocolDecoder(JpKorjarProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("KORJAR.PL,")
            .number("(d+),")                     // imei
            .number("(dd)(dd)(dd)")              // date (yymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(d+.d+)([NS]),")            // latitude
            .number("(d+.d+)([EW]),")            // longitude
            .number("(d+.d+),")                  // speed
            .number("(d+),")                     // course
            .number("[FL]:(d+.d+)V,")            // battery
            .number("([01]) ")                   // valid
            .number("(d+) ")                     // mcc
            .number("(d+) ")                     // mnc
            .number("(x+) ")                     // lac
            .number("(x+),")                     // cid
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

        position.setTime(parser.nextDateTime());

        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.DEG_HEM));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.DEG_HEM));
        position.setSpeed(parser.nextDouble(0));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_BATTERY, parser.nextDouble(0));

        position.setValid(parser.nextInt(0) == 1);

        position.setNetwork(new Network(CellTower.from(
                parser.nextInt(0), parser.nextInt(0), parser.nextHexInt(0), parser.nextHexInt(0))));

        return position;
    }

}