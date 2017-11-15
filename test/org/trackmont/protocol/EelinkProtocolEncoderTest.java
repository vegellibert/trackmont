package org.trackmont.protocol;

import org.junit.Test;
import org.trackmont.ProtocolTest;
import org.trackmont.model.Command;

public class EelinkProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        EelinkProtocolEncoder encoder = new EelinkProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("676780000f0000010000000052454c41592c3123"));

    }

}
