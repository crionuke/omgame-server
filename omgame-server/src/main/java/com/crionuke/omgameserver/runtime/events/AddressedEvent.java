package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;
import com.crionuke.omgameserver.core.Event;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public abstract class AddressedEvent extends Event {

    final Address address;

    public AddressedEvent(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
