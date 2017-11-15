package org.trackmont.geocoder;

import org.junit.Assert;
import org.junit.Test;

public class AddressFormatTest {

    private void test(Address address, String format, String expected) {
        Assert.assertEquals(expected, new AddressFormat(format).format(address));
    }

    @Test
    public void testFormat() {

        Address address = new Address();
        address.setCountry("NZ");
        address.setSettlement("Auckland");
        address.setStreet("Queen St");
        address.setHouse("1A");

        test(address, "%h %r %t %d %s %c %p", "1A Queen St Auckland NZ");
        test(address, "%h %r %t", "1A Queen St Auckland");
        test(address, "%h %r, %t", "1A Queen St, Auckland");
        test(address, "%h %r, %t %p", "1A Queen St, Auckland");
        test(address, "%t %d %c", "Auckland NZ");
        test(address, "%t, %d, %c", "Auckland, NZ");
        test(address, "%d %c", "NZ");
        test(address, "%d, %c", "NZ");
        test(address, "%p", "");
    }

}
