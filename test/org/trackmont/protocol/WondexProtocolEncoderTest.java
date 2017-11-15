package org.trackmont.protocol;

import org.junit.Assert;
import org.junit.Test;
import org.trackmont.ProtocolTest;
import org.trackmont.model.Command;

public class WondexProtocolEncoderTest extends ProtocolTest {
    @Test
    public void testEncode() throws Exception {

        WondexProtocolEncoder encoder = new WondexProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(2);
        command.setType(Command.TYPE_POSITION_SINGLE);
        command.set(Command.KEY_DEVICE_PASSWORD, "0000");

        Assert.assertEquals("$WP+GETLOCATION=0000", encoder.encodeCommand(command));

    }

}
