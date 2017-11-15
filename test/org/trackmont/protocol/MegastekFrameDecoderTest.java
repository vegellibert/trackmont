package org.trackmont.protocol;

import org.junit.Assert;
import org.junit.Test;
import org.trackmont.ProtocolTest;

public class MegastekFrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        MegastekFrameDecoder decoder = new MegastekFrameDecoder();

        verifyFrame(
                binary("30313337244d47563030322c3335343535303035303239323636392c4756543930302c522c3134313231352c3033313830342c412c2c532c2c452c30302c30332c30302c332e36372c302e3030302c302e30302c3131372e312c302e302c3531302c31302c2c2c2c303030302c303030302c32322c31322c302c202c202c2c312d312c39382c5057204f4e3b21"),
                decoder.decode(null, null, binary("30313337244d47563030322c3335343535303035303239323636392c4756543930302c522c3134313231352c3033313830342c412c2c532c2c452c30302c30332c30302c332e36372c302e3030302c302e30302c3131372e312c302e302c3531302c31302c2c2c2c303030302c303030302c32322c31322c302c202c202c2c312d312c39382c5057204f4e3b21")));

        verifyFrame(
                binary("244d47563030322c3031333737373030373533363433342c2c522c3031303131342c3030303035372c562c303030302e303030302c4e2c30303030302e303030302c452c30302c30302c30302c39392e392c302e3030302c302e30302c302e302c38302e3236332c3531302c38392c323334322c303330422c2c303030302c303030302c3230302c39362c302c202c202c2c2c2c54696d65723b21"),
                decoder.decode(null, null, binary("244d47563030322c3031333737373030373533363433342c2c522c3031303131342c3030303035372c562c303030302e303030302c4e2c30303030302e303030302c452c30302c30302c30302c39392e392c302e3030302c302e30302c302e302c38302e3236332c3531302c38392c323334322c303330422c2c303030302c303030302c3230302c39362c302c202c202c2c2c2c54696d65723b210d0a")));

        verifyFrame(
                binary("53545832363034373520202020202020202020024f244750524d432c3133313131302e30302c562c2c2c2c2c2c2c3036303931332c2c2c4e2a37362c3232322c30312c383135412c443435352c31312c39372c303030302c303030312c302c54696d65723b3735"),
                decoder.decode(null, null, binary("53545832363034373520202020202020202020024f244750524d432c3133313131302e30302c562c2c2c2c2c2c2c3036303931332c2c2c4e2a37362c3232322c30312c383135412c443435352c31312c39372c303030302c303030312c302c54696d65723b37350d0a")));

    }

}
