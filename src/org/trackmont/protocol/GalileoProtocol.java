/*
 * Copyright 2015 - 2017 Vicente Venegas (vicente@republik.ec)
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
import org.trackmont.BaseProtocol;
import org.trackmont.TrackerServer;
import org.trackmont.model.Command;

import java.nio.ByteOrder;
import java.util.List;

public class GalileoProtocol extends BaseProtocol {

    public GalileoProtocol() {
        super("galileo");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_OUTPUT_CONTROL);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new GalileoFrameDecoder());
                pipeline.addLast("objectEncoder", new GalileoProtocolEncoder());
                pipeline.addLast("objectDecoder", new GalileoProtocolDecoder(GalileoProtocol.this));
            }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
