package org.trackmont.protocol;

import org.junit.Assert;
import org.junit.Test;
import org.trackmont.ProtocolTest;

public class UlbotechFrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        UlbotechFrameDecoder decoder = new UlbotechFrameDecoder();

        Assert.assertEquals(
                binary("f8010103515810532780699f7e2e3f010e015ee4c906bde45c00000000008b0304004000000404002c776005060373193622110b00240b00fee8ffff807dffff606d0b00fee9af000000af0000000b00feee7d78807dffffffff100101cc2af8"),
                decoder.decode(null, null, binary("f8010103515810532780699f7e2e3f010e015ee4c906bde45c00000000008b0304004000000404002c776005060373193622110b00240b00fee8ffff807dffff606d0b00fee9af000000af0000000b00feee7d78807dffffffff100101cc2af8")));

        Assert.assertEquals(
                binary("2a545330312c33353430343330353133383934363023"),
                decoder.decode(null, null, binary("2a545330312c33353430343330353133383934363023")));

        Assert.assertEquals(
                binary("f8010108679650230646339de69054010e015ee17506bde2c60000000000ac0304024000000404000009f705060390181422170711310583410c0000310d00312f834131018608040003130a100101136cf8"),
                decoder.decode(null, null, binary("f8010108679650230646339de69054010e015ee17506bde2c60000000000ac0304024000000404000009f70005060390181422170711310583410c0000310d00312f834131018608040003130a100101136cf8")));

    }

}
