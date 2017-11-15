/*
 * Copyright 2017 Vicente Venegas (vicente@republik.ec)
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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.trackmont.BaseProtocolDecoder;
import org.trackmont.DeviceSession;
import org.trackmont.helper.DateBuilder;
import org.trackmont.helper.UnitsConverter;
import org.trackmont.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class Gps056ProtocolDecoder extends BaseProtocolDecoder {

    public Gps056ProtocolDecoder(Gps056Protocol protocol) {
        super(protocol);
    }

    private static void sendResponse(Channel channel, String type, String imei, ChannelBuffer content) {
        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.dynamicBuffer();
            String header = "*" + type + imei;
            response.writeBytes(header.getBytes(StandardCharsets.US_ASCII));
            if (content != null) {
                response.writeBytes(content);
            }
            response.writeByte('#');
            channel.write(response);
        }
    }

    private static double decodeCoordinate(ChannelBuffer buf) {
        double degrees = buf.getUnsignedShort(buf.readerIndex()) / 100;
        double minutes = buf.readUnsignedShort() % 100 + buf.readUnsignedShort() * 0.0001;
        degrees += minutes / 60;
        byte hemisphere = buf.readByte();
        if (hemisphere == 'S' || hemisphere == 'W') {
            degrees = -degrees;
        }
        return degrees;
    }

    private static void decodeStatus(ChannelBuffer buf, Position position) {

        position.set(Position.KEY_INPUT, buf.readUnsignedByte());
        position.set(Position.KEY_OUTPUT, buf.readUnsignedByte());

        position.set(Position.PREFIX_ADC + 1, ChannelBuffers.swapShort(buf.readShort()) * 5.06); // mV

        position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
        position.set(Position.KEY_RSSI, buf.readUnsignedByte());

    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.skipBytes(2); // header
        buf.skipBytes(2); // length

        String type = buf.readBytes(7).toString(StandardCharsets.US_ASCII);
        String imei = buf.readBytes(15).toString(StandardCharsets.US_ASCII);

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        if (type.startsWith("LOGN")) {

            sendResponse(channel, "LGSA" + type.substring(4), imei,
                    ChannelBuffers.copiedBuffer("1", StandardCharsets.US_ASCII));

        } else if (type.startsWith("GPSL")) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            DateBuilder dateBuilder = new DateBuilder()
                    .setDateReverse(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte())
                    .setTime(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte());

            position.setValid(true);
            position.setTime(dateBuilder.getDate());
            position.setLatitude(decodeCoordinate(buf));
            position.setLongitude(decodeCoordinate(buf));
            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
            position.setCourse(buf.readUnsignedShort());

            decodeStatus(buf, position);

            sendResponse(channel, "GPSA" + type.substring(4), imei, buf.readBytes(2));

            return position;

        } else if (type.startsWith("SYNC")) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            getLastLocation(position, null);

            decodeStatus(buf, position);

            sendResponse(channel, "SYSA" + type.substring(4), imei, null);

            return position;

        }

        return null;
    }

}
