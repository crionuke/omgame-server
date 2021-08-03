package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class CreateWorkerEvent extends AddressedEvent {

    final String script;

    public CreateWorkerEvent(String script, Address address) {
        super(address);
        this.script = script;
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
