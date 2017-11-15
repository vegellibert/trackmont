package org.trackmont.protocol;

import org.junit.Test;
import org.trackmont.ProtocolTest;

public class TlvProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        TlvProtocolDecoder decoder = new TlvProtocolDecoder(new TlvProtocol());

        verifyNull(decoder, binary(
                "30430f383630323437303330303934333931ff10393233323132323030303834353433340f533636385f415f56312e30315f454eff1130303a30433a45373a30303a30303a30300132"));

        verifyNull(decoder, binary(
                "30410f383630323437303330303934333931"));

        verifyNull(decoder, binary(
                "30420f3836303234373033303039343339310131"));

    }

}
