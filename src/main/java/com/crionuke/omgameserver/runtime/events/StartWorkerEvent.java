package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class StartWorkerEvent extends RuntimeEvent {

    final Address address;

    public StartWorkerEvent(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "StartWorkerEvent{" +
                "address=" + address +
                '}';
    }
}
