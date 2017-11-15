/*
 * Copyright 2012 - 2017 Vicente Venegas (vicente@trackmont.com)
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
import org.trackmont.Context;
import org.trackmont.DeviceSession;
import org.trackmont.helper.Checksum;
import org.trackmont.helper.Parser;
import org.trackmont.helper.PatternBuilder;
import org.trackmont.helper.UnitsConverter;
import org.trackmont.model.CellTower;
import org.trackmont.model.Network;
import org.trackmont.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class MeitrackProtocolDecoder extends BaseProtocolDecoder {

    private ChannelBuffer photo;

    public MeitrackProtocolDecoder(MeitrackProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$$").expression(".")          // flag
            .number("d+,")                       // length
            .number("(d+),")                     // imei
            .number("xxx,")                      // command
            .number("d+,").optional()
            .number("(d+),")                     // event
            .number("(-?d+.d+),")                // latitude
            .number("(-?d+.d+),")                // longitude
            .number("(dd)(dd)(dd)")              // date (yymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("([AV]),")                   // validity
            .number("(d+),")                     // satellites
            .number("(d+),")                     // rssi
            .number("(d+.?d*),")                 // speed
            .number("(d+),")                     // course
            .number("(d+.?d*),")                 // hdop
            .number("(-?d+),")                   // altitude
            .number("(d+),")                     // odometer
            .number("(d+),")                     // runtime
            .number("(d+)|")                     // mcc
            .number("(d+)|")                     // mnc
            .number("(x+)|")                     // lac
            .number("(x+),")                     // cid
            .number("(x+),")                     // state
            .number("(x+)?|")                    // adc1
            .number("(x+)?|")                    // adc2
            .number("(x+)?|")                    // adc3
            .number("(x+)|")                     // battery
            .number("(x+)?,")                    // power
            .groupBegin()
            .expression("([^,]+)?,")             // event specific
            .expression("[^,]*,")                // reserved
            .number("(d+)?,")                    // protocol
            .number("(x{4})?")                   // fuel
            .number("(?:,(x{6}(?:|x{6})*))?")    // temperature
            .groupEnd("?")
            .any()
            .text("*")
            .number("xx")
            .text("\r\n").optional()
            .compile();

    private String decodeAlarm(int event) {
        switch (event) {
            case 1:
                return Position.ALARM_SOS;
            case 17:
                return Position.ALARM_LOW_BATTERY;
            case 18:
                return Position.ALARM_LOW_POWER;
            case 19:
                return Position.ALARM_OVERSPEED;
            case 20:
                return Position.ALARM_GEOFENCE_ENTER;
            case 21:
                return Position.ALARM_GEOFENCE_EXIT;
            case 22:
                return Position.ALARM_POWER_RESTORED;
            case 23:
                return Position.ALARM_POWER_CUT;
            case 36:
                return Position.ALARM_TOW;
            default:
                return null;
        }
    }

    private Position decodeRegular(Channel channel, SocketAddress remoteAddress, ChannelBuffer buf) {

        Parser parser = new Parser(PATTERN, buf.toString(StandardCharsets.US_ASCII));
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

        int event = parser.nextInt(0);
        position.set(Position.KEY_EVENT, event);
        position.set(Position.KEY_ALARM, decodeAlarm(event));

        position.setLatitude(parser.nextDouble(0));
        position.setLongitude(parser.nextDouble(0));

        position.setTime(parser.nextDateTime());

        position.setValid(parser.next().equals("A"));

        position.set(Position.KEY_SATELLITES, parser.nextInt());
        int rssi = parser.nextInt(0);

        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_HDOP, parser.nextDouble());

        position.setAltitude(parser.nextDouble(0));

        position.set(Position.KEY_ODOMETER, parser.nextInt(0));
        position.set("runtime", parser.next());

        position.setNetwork(new Network(CellTower.from(
                parser.nextInt(0), parser.nextInt(0), parser.nextHexInt(0), parser.nextHexInt(0), rssi)));

        position.set(Position.KEY_STATUS, parser.next());

        for (int i = 1; i <= 3; i++) {
            if (parser.hasNext()) {
                position.set(Position.PREFIX_ADC + i, parser.nextHexInt(0));
            }
        }

        String deviceModel = Context.getIdentityManager().getById(deviceSession.getDeviceId()).getModel();
        if (deviceModel == null) {
            deviceModel = "";
        }
        switch (deviceModel.toUpperCase()) {
            case "MVT340":
            case "MVT380":
                position.set(Position.KEY_BATTERY, parser.nextHexInt(0) * 3.0 * 2.0 / 1024.0);
                position.set(Position.KEY_POWER, parser.nextHexInt(0) * 3.0 * 16.0 / 1024.0);
                break;
            case "MT90":
                position.set(Position.KEY_BATTERY, parser.nextHexInt(0) * 3.3 * 2.0 / 4096.0);
                position.set(Position.KEY_POWER, parser.nextHexInt(0));
                break;
            case "T1":
            case "T3":
            case "MVT100":
            case "MVT600":
            case "MVT800":
            case "TC68":
            case "TC68S":
                position.set(Position.KEY_BATTERY, parser.nextHexInt(0) * 3.3 * 2.0 / 4096.0);
                position.set(Position.KEY_POWER, parser.nextHexInt(0) * 3.3 * 16.0 / 4096.0);
                break;
            case "T311":
            case "T322X":
            case "T333":
            case "T355":
                position.set(Position.KEY_BATTERY, parser.nextHexInt(0) / 100.0);
                position.set(Position.KEY_POWER, parser.nextHexInt(0) / 100.0);
                break;
            default:
                position.set(Position.KEY_BATTERY, parser.nextHexInt(0));
                position.set(Position.KEY_POWER, parser.nextHexInt(0));
                break;
        }

        String eventData = parser.next();
        if (eventData != null && !eventData.isEmpty()) {
            switch (event) {
                case 37:
                    position.set(Position.KEY_DRIVER_UNIQUE_ID, eventData);
                    break;
                default:
                    position.set("eventData", eventData);
                    break;
            }
        }

        int protocol = parser.nextInt(0);

        if (parser.hasNext()) {
            String fuel = parser.next();
            position.set(Position.KEY_FUEL_LEVEL,
                    Integer.parseInt(fuel.substring(0, 2), 16) + Integer.parseInt(fuel.substring(2), 16) * 0.01);
        }

        if (parser.hasNext()) {
            for (String temp : parser.next().split("\\|")) {
                int index = Integer.parseInt(temp.substring(0, 2), 16);
                if (protocol >= 3) {
                    double value = (short) Integer.parseInt(temp.substring(2), 16);
                    position.set(Position.PREFIX_TEMP + index, value * 0.01);
                } else {
                    double value = Byte.parseByte(temp.substring(2, 4), 16);
                    value += (value < 0 ? -0.01 : 0.01) * Integer.parseInt(temp.substring(4), 16);
                    position.set(Position.PREFIX_TEMP + index, value);
                }
            }
        }

        return position;
    }

    private List<Position> decodeBinaryC(Channel channel, SocketAddress remoteAddress, ChannelBuffer buf) {
        List<Position> positions = new LinkedList<>();

        String flag = buf.toString(2, 1, StandardCharsets.US_ASCII);
        int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ',');

        String imei = buf.toString(index + 1, 15, StandardCharsets.US_ASCII);
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        buf.skipBytes(index + 1 + 15 + 1 + 3 + 1 + 2 + 2 + 4);

        while (buf.readableBytes() >= 0x34) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            position.set(Position.KEY_EVENT, buf.readUnsignedByte());

            position.setLatitude(buf.readInt() * 0.000001);
            position.setLongitude(buf.readInt() * 0.000001);

            position.setTime(new Date((946684800 + buf.readUnsignedInt()) * 1000)); // 946684800 = 2000-01-01

            position.setValid(buf.readUnsignedByte() == 1);

            position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
            int rssi = buf.readUnsignedByte();

            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort()));
            position.setCourse(buf.readUnsignedShort());

            position.set(Position.KEY_HDOP, buf.readUnsignedShort() * 0.1);

            position.setAltitude(buf.readUnsignedShort());

            position.set(Position.KEY_ODOMETER, buf.readUnsignedInt());
            position.set("runtime", buf.readUnsignedInt());

            position.setNetwork(new Network(CellTower.from(
                    buf.readUnsignedShort(), buf.readUnsignedShort(),
                    buf.readUnsignedShort(), buf.readUnsignedShort(),
                    rssi)));

            position.set(Position.KEY_STATUS, buf.readUnsignedShort());

            position.set(Position.PREFIX_ADC + 1, buf.readUnsignedShort());
            position.set(Position.KEY_BATTERY, buf.readUnsignedShort() * 0.01);
            position.set(Position.KEY_POWER, buf.readUnsignedShort());

            buf.readUnsignedInt(); // geo-fence

            positions.add(position);
        }

        if (channel != null) {
            StringBuilder command = new StringBuilder("@@");
            command.append(flag).append(27 + positions.size() / 10).append(",");
            command.append(imei).append(",CCC,").append(positions.size()).append("*");
            int checksum = 0;
            for (int i = 0; i < command.length(); i += 1) {
                checksum += command.charAt(i);
            }
            command.append(String.format("%02x", checksum & 0xff).toUpperCase());
            command.append("\r\n");
            channel.write(command.toString()); // delete processed data
        }

        return positions;
    }

    private List<Position> decodeBinaryE(Channel channel, SocketAddress remoteAddress, ChannelBuffer buf) {
        List<Position> positions = new LinkedList<>();

        buf.readerIndex(buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ',') + 1);
        String imei = buf.readBytes(15).toString(StandardCharsets.US_ASCII);
        buf.skipBytes(1 + 3 + 1);

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        buf.readUnsignedInt(); // remaining cache
        int count = buf.readUnsignedShort();

        for (int i = 0; i < count; i++) {
            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            buf.readUnsignedShort(); // length
            buf.readUnsignedShort(); // index

            int paramCount = buf.readUnsignedByte();
            for (int j = 0; j < paramCount; j++) {
                int id = buf.readUnsignedByte();
                switch (id) {
                    case 0x01:
                        position.set(Position.KEY_EVENT, buf.readUnsignedByte());
                        break;
                    case 0x05:
                        position.setValid(buf.readUnsignedByte() > 0);
                        break;
                    case 0x06:
                        position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
                        break;
                    default:
                        buf.readUnsignedByte();
                        break;
                }
            }

            paramCount = buf.readUnsignedByte();
            for (int j = 0; j < paramCount; j++) {
                int id = buf.readUnsignedByte();
                switch (id) {
                    case 0x08:
                        position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort()));
                        break;
                    case 0x09:
                        position.setCourse(buf.readUnsignedShort() * 0.1);
                        break;
                    case 0x0B:
                        position.setAltitude(buf.readShort());
                        break;
                    default:
                        buf.readUnsignedShort();
                        break;
                }
            }

            paramCount = buf.readUnsignedByte();
            for (int j = 0; j < paramCount; j++) {
                int id = buf.readUnsignedByte();
                switch (id) {
                    case 0x02:
                        position.setLatitude(buf.readInt() * 0.000001);
                        break;
                    case 0x03:
                        position.setLongitude(buf.readInt() * 0.000001);
                        break;
                    case 0x04:
                        position.setTime(new Date((946684800 + buf.readUnsignedInt()) * 1000)); // 2000-01-01
                        break;
                    default:
                        buf.readUnsignedInt();
                        break;
                }
            }

            paramCount = buf.readUnsignedByte();
            for (int j = 0; j < paramCount; j++) {
                buf.readUnsignedByte(); // id
                buf.skipBytes(buf.readUnsignedByte()); // value
            }

            positions.add(position);
        }

        return positions;
    }

    private void requestPhotoPacket(Channel channel, String imei, int index) {
        if (channel != null) {
            String content = "D00,camera_picture.jpg," + index;
            int length = 1 + imei.length() + 1 + content.length() + 5;
            String response = String.format("@@O%02d,%s,%s*", length, imei, content);
            response += Checksum.sum(response) + "\r\n";
            channel.write(response);
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ',');
        String imei = buf.toString(index + 1, 15, StandardCharsets.US_ASCII);
        index = buf.indexOf(index + 1, buf.writerIndex(), (byte) ',');
        String type = buf.toString(index + 1, 3, StandardCharsets.US_ASCII);

        switch (type) {
            case "D00":
                index = buf.indexOf(index + 1 + type.length() + 1, buf.writerIndex(), (byte) ',') + 1;
                int endIndex =  buf.indexOf(index, buf.writerIndex(), (byte) ',');
                int total = Integer.parseInt(buf.toString(index, endIndex - index, StandardCharsets.US_ASCII));
                index = endIndex + 1;
                endIndex = buf.indexOf(index, buf.writerIndex(), (byte) ',');
                int current = Integer.parseInt(buf.toString(index, endIndex - index, StandardCharsets.US_ASCII));

                buf.readerIndex(endIndex + 1);
                photo.writeBytes(buf.readBytes(buf.readableBytes() - 1 - 2 - 2));

                if (current == total - 1) {
                    Position position = new Position();
                    position.setProtocol(getProtocolName());
                    position.setDeviceId(getDeviceSession(channel, remoteAddress, imei).getDeviceId());

                    getLastLocation(position, null);

                    position.set(Position.KEY_IMAGE, Context.getMediaManager().writeFile(imei, photo, "jpg"));
                    photo = null;

                    return position;
                } else {
                    if ((current + 1) % 8 == 0) {
                        requestPhotoPacket(channel, imei, current + 1);
                    }
                    return null;
                }
            case "D03":
                photo = ChannelBuffers.dynamicBuffer();
                requestPhotoPacket(channel, imei, 0);
                return null;
            case "CCC":
                return decodeBinaryC(channel, remoteAddress, buf);
            case "CCE":
                return decodeBinaryE(channel, remoteAddress, buf);
            default:
                return decodeRegular(channel, remoteAddress, buf);
        }
    }

}
