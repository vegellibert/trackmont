package org.trackmont.protocol;

import org.junit.Test;
import org.trackmont.ProtocolTest;
import org.trackmont.model.Command;

public class KhdProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        KhdProtocolEncoder encoder = new KhdProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("2929390006000000003F0D"));

    }

}
