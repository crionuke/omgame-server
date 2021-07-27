package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

public class RunWorkerEvent extends RuntimeEvent {

    final String script;
    final Address address;

    public RunWorkerEvent(String script, Address address) {
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
        return "RunWorkerEvent{" +
                "script='" + script + '\'' +
                ", address=" + address +
                '}';
    }
}
