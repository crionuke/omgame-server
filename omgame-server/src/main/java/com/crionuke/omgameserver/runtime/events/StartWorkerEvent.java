package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class StartWorkerEvent extends AddressedEvent {

    public StartWorkerEvent(Address address) {
        super(address);
    }

    @Override
    public String toString() {
        return "StartWorkerEvent{" +
                "address=" + address +
                '}';
    }
}
