package org.trackmont.protocol;

import org.junit.Test;
import org.trackmont.ProtocolTest;

public class TelicProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        TelicProtocolDecoder decoder = new TelicProtocolDecoder(new TelicProtocol());

        verifyNull(decoder, text(
                "0026355565071347499|206|01|001002008"));

        verifyPosition(decoder, text(
                "052028495198,160917073641,0,160917073642,43879,511958,3,24,223,17,,,-3,142379,,0010,00,64,205,0,0499"));

        verifyPosition(decoder, text(
                "01302849516,160917073503,0,160917073504,43907,512006,3,11,160,14,,,-7,141811,,0010,00,64,206,0,0499"));

        verifyPosition(decoder, text(
                "002135556507134749999,010817171138,0,010817171138,004560973,50667173,3,0,0,11,1,1,100,958071,20601,000000,00,4142,0000,0000,0208,10395,0"));

        verifyPosition(decoder, text(
                "442045993198,290317131935,0,290317131935,269158,465748,3,26,183,,,,184,85316567,226,01,00,68,218"));

        verifyPosition(decoder, text(
                "673091036017,290317131801,0,290317131801,262214,450536,3,40,199,8,,,154,19969553,,0011,00,59,240,0,0406"));

        verifyPosition(decoder, text(
                "092020621198,280317084155,0,280317084156,259762,444356,3,42,278,9,,,89,56793311,,0110,00,67,0,,0400"));

        verifyPosition(decoder, text(
                "502091227598,280317084149,0,280317084149,261756,444358,3,33,286,9,,,77,3143031,,0010,00,171,240,0,0406"));

        verifyPosition(decoder, text(
                "232027997498,230317083900,0,230317083900,260105,444112,3,22,259,,,,111,61110817,226,01,00,255,218,00000000000000"));

        verifyPosition(decoder, text(
                "072027997498,230317082635,0,230317082635,260332,444265,3,28,165,,,,124,61107582,226,01,00,255,219,00000000000000"));

        verifyNull(decoder, text(
                "0026203393|226|10|002004010"));

        verifyPosition(decoder, text(
                "003020339325,190317083052,0,180317103127,259924,445133,3,0,0,9,,,93,12210141,,0010,00,40,240,0,0406"));

        verifyNull(decoder, text(
                "0026296218SCCE01_SCCE|226|10|0267"));

        verifyNull(decoder, text(
                "1242022592TTUV0100,0201,351266000022592,170403114305,0115859,480323,3,30,5,9,3,4,650,250000000,26202,1001,0001,211,233,111,0"));

        verifyPosition(decoder, text(
                "123002259213,170403114305,1234,170403114305,0115859,480323,3,30,5,9,3,4,650,250000000,26202,1001,0001,211,233,111,0,600"));

        verifyNull(decoder, text(
                "0044296218TLOC0267,00,011009000296218,190317083036,255178,445072,3,0,82,,,,168,14741296,,00,00,0,217"));

        verifyPosition(decoder, text(
                "003097061325,220616044200,0,220616044200,247169,593911,3,48,248,8,,,50,1024846,,1111,00,48,0,51,0406"));

        verifyPosition(decoder, text(
                "003097061325,210216112630,0,210216001405,246985,594078,3,0,283,12,,,23,4418669,,0010,00,117,0,0,0108"));

        verifyPosition(decoder, text(
                "592078222222,010100030200,0,240516133500,222222,222222,3,0,0,5,,,37,324,,1010,00,48,0,0,0406"));

        verifyPosition(decoder, text(
                "002017297899,220216111100,0,220216111059,014306446,46626713,3,7,137,7,,,448,266643,,0000,00,0,206,0,0407"));

        verifyPosition(decoder, text(
                "003097061325,210216112630,0,210216001405,246985,594078,3,0,283,12,,,23,4418669,,0010,00,117,0,0,0108"));

        verifyNull(decoder, text(
                "0026970613|248|01|004006011"));

        verifyPosition(decoder, text(
                "032097061399,210216112800,0,210216112759,246912,594076,3,47,291,10,,,46,4419290,,0010,00,100,0,0,0108"));

        verifyPosition(decoder, text(
                "002017297899,190216202500,0,190216202459,014221890,46492170,3,0,0,6,,,1034,43841,,0000,00,0,209,0,0407"));

        verifyPosition(decoder, text(
                "182043672999,010100001301,0,270613041652,166653,475341,3,0,355,6,2,1,231,8112432,23201,01,00,217,0,0,0,0,7"),
                position("2013-06-27 04:16:52.000", true, 47.53410, 16.66530));

        verifyPosition(decoder, text(
                "182043672999,010100001301,0,270613041652,166653,475341,3,0,355,6,2,1,231,8112432,23201,01,00,217,0,0,0,0,7"));

    }

}
