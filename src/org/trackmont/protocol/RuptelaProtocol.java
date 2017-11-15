/*
 * Copyright 2015 - 2016 Vicente Venegas (vicente@republik.ec)
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
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.trackmont.BaseProtocol;
import org.trackmont.TrackerServer;
import org.trackmont.model.Command;

import java.util.List;

public class RuptelaProtocol extends BaseProtocol {

    public RuptelaProtocol() {
        super("ruptela");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_CONFIGURATION,
                Command.TYPE_GET_VERSION,
                Command.TYPE_FIRMWARE_UPDATE,
                Command.TYPE_OUTPUT_CONTROL,
                Command.TYPE_SET_CONNECTION,
                Command.TYPE_SET_ODOMETER);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, 2, 0));
                pipeline.addLast("objectEncoder", new RuptelaProtocolEncoder());
                pipeline.addLast("objectDecoder", new RuptelaProtocolDecoder(RuptelaProtocol.this));
            }
        });
    }

}
