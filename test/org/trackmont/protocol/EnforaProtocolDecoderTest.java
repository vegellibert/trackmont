package org.trackmont.protocol;

import org.junit.Test;
import org.trackmont.ProtocolTest;

public class EnforaProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        EnforaProtocolDecoder decoder = new EnforaProtocolDecoder(new EnforaProtocol());

        verifyNull(decoder, binary(
                "000A08002020202020303131303730303030353730323637"));

        verifyNull(decoder, binary(
                "003B000502000000000820202020202030313130373030303035373032363720383A000000000D00508401358E640032B37700000367B00000A804"));

        verifyPosition(decoder, binary(
                "007100040200202020202020202020382020202020202031323334353637383930313233343520313320244750524D432C3232333135322E30302C412C333530392E3836303539342C4E2C30333332322E3734333838372C452C302E302C302E302C3032303631322C2C2C412A35320D0A"),
                position("2012-06-02 22:31:52.000", true, 35.16434, 33.37906));

        verifyPosition(decoder, binary(
                "007600040200202020202020202020382020202020202030313138393230303036303831383920313320244750524D432C3137313834312E30302C412C333530392E3835323431302C4E2C30333332322E3735393131332C452C302E302C302E302C3137303731322C332E342C572C412A32350D0A00"));

        verifyPosition(decoder, binary(
                "006a000a081000202020202020202020333320202020202038363130373430323137313936353620204750524d432c3136313234382e30302c412c333433322e36393231312c532c30353833312e30323231372c572c302e3034382c2c3232303831342c2c2c412a3734"));

    }

}
