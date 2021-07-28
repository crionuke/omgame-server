package com.crionuke.omgameserver.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddressTest extends Assertions {

    @Test
    public void testEquals() {
        Address address1 = new Address("crionuke", "hostagecrisis", "1234567890");
        Address address2 = new Address("crionuke", "hostagecrisis", "1234567890");
        Address address3 = new Address("crionuke", "hostagecrisis", "1234567891");
        assertEquals(address1, address2);
        assertNotEquals(address1, address3);
        assertNotEquals(address2, address3);
    }
}
