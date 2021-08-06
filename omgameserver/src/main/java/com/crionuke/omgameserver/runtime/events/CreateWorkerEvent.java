package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class CreateWorkerEvent extends AddressedEvent {

    final String script;
    final int tickEveryMillis;

    public CreateWorkerEvent(String script, Address address, int tickEveryMillis) {
        super(address);
        this.script = script;
        this.tickEveryMillis = tickEveryMillis;
    }

    public String getScript() {
        return script;
    }

    public int getTickEveryMillis() {
        return tickEveryMillis;
    }

    @Override
    public String toString() {
        return "CreateWorkerEvent{" +
                "address=" + address +
                ", script='" + script + '\'' +
                ", tickEveryMillis=" + tickEveryMillis +
                '}';
    }
}
