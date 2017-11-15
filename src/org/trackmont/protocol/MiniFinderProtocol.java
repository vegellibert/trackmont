/*
 * Copyright 2015 - 2016 Vicente Venegas (vicente@trackmont.com)
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

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.trackmont.BaseProtocol;
import org.trackmont.CharacterDelimiterFrameDecoder;
import org.trackmont.TrackerServer;
import org.trackmont.model.Command;

import java.util.List;

public class MiniFinderProtocol extends BaseProtocol {

    public MiniFinderProtocol() {
        super("minifinder");
        setSupportedDataCommands(
                Command.TYPE_SET_TIMEZONE,
                Command.TYPE_VOICE_MONITORING,
                Command.TYPE_ALARM_SPEED,
                Command.TYPE_ALARM_GEOFENCE,
                Command.TYPE_ALARM_VIBRATION,
                Command.TYPE_SET_AGPS,
                Command.TYPE_ALARM_FALL,
                Command.TYPE_MODE_POWER_SAVING,
                Command.TYPE_MODE_DEEP_SLEEP,
                Command.TYPE_SOS_NUMBER,
                Command.TYPE_SET_INDICATOR);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new CharacterDelimiterFrameDecoder(1024, ';'));
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("stringDecoder", new StringDecoder());
                pipeline.addLast("objectEncoder", new MiniFinderProtocolEncoder());
                pipeline.addLast("objectDecoder", new MiniFinderProtocolDecoder(MiniFinderProtocol.this));
            }
        });
    }

}