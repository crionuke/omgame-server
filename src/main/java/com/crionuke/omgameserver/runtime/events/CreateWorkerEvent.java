package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class CreateWorkerEvent extends RuntimeEvent {

    final String script;
    final Address address;

    public CreateWorkerEvent(String script, Address address) {
        this.script = script;
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "CreateWorkerEvent{" +
                "script='" + script + '\'' +
                ", address=" + address +
                '}';
    }
}
