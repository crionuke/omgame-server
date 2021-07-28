package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public abstract class AddressedEvent extends RuntimeEvent {

    final Address address;

    public AddressedEvent(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "AddresedEvent{" +
                "address=" + address +
                '}';
    }
}
