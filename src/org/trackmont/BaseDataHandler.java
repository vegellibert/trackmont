/*
 * Copyright 2015 Vicente Venegas (vicente@republik.ec)
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
package org.trackmont;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.trackmont.model.Position;

public abstract class BaseDataHandler extends OneToOneDecoder {

    @Override
    protected final Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {

        if (msg instanceof Position) {
            return handlePosition((Position) msg);
        }

        return msg;
    }

    protected abstract Position handlePosition(Position position);

}
