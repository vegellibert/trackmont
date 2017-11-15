package org.trackmont.protocol;

import java.nio.ByteOrder;

import org.junit.Test;
import org.trackmont.ProtocolTest;

public class GranitProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        GranitProtocolDecoder decoder = new GranitProtocolDecoder(new GranitProtocol());

        verifyPositions(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b444441547e8400c500040130050c43495808002839aee3150200000000640000000000000008002839aee3150200000000640000000000000008002839aee3150200000000640000000000000008002839aee3150200000000640000000000000008002839aee3150200000000640000000000000008002839aee3150200000000640000000000000014002a37420d0a"));

        verifyPosition(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b525243427e1a00c5008443495808002839aee315020000000064000000000000002a37410d0a"),
                position("2016-12-08 11:27:00.000", false, 57.00888, 40.97143));

        verifyPosition(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b525243427e1a00c500ec904858b842283997e30002000000005e000000000d00002a32390d0a"),
                position("2016-12-07 22:45:00.000", true, 57.00853, 40.97105));

        verifyPosition(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b525243427e1a00c500009148580800283997e30002000000005f000000000000002a33410d0a"));

        verifyPositions(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b444441547e84003b6d0401b10e9217445800b051398f35d34a313b000072000000010b000080b051398f35d34a313b000072000000010b0000f0b051390f33314c303b900371000000010b0000f0b05139cd31e54c2f3cd0016f000000010b0000f0b051396831204d303d950071000000010b0000f0b051397530aa4d323c610171000000010b00000a002a30420d0a"));

        verifyPosition(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b525243427e1a003e2934757c57b8b03c38d279b4e61e9bd7006b000000001c00002a4533"));

        verifyPositions(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b444441547e84003e290401d01690737c57b8903c383c7fa0e5081b64006b000000001c0000b8803c388e7fe7e5102197006c000000001c0000b8813c38ad7f02e6042035006c000000001d0000b8813c38bf7f13e6001d1e006c000000001d0000b8813c38bf7f13e6001d00006c000000001d0000b8903c38977f34e6091065006c000000001e000014002a3932"));

        verifyPositions(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b444441547e84003e290401d41680747c57f8a03c38987f50e6005300006c000000001c0000f8b03c38987f50e6005300006c000000001c0000fefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefefe14002a4346"));

        // +IDNT: Navigator.04x  Firmware version  0712GLN *21
        verifyAttributes(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "2b49444e543a204e6176696761746f722e30347820204669726d776172652076657273696f6e202030373132474c4e202a3231"));

        // ERROR WRONG CHECKSUM_1
        verifyAttributes(decoder, binary(ByteOrder.LITTLE_ENDIAN,
                "4552524f522057524f4e4720434845434b53554d5f31"));

    }

}
