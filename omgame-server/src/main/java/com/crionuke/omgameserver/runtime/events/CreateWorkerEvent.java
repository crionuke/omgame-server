package com.crionuke.omgameserver.runtime.events;

import com.crionuke.omgameserver.core.Address;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
public class CreateWorkerEvent extends AddressedEvent {

    public static final String TOPIC = "CreateWorkerEvent";

    final String rootDirectory;
    final String mainScript;
    final int tickEveryMillis;

    public CreateWorkerEvent(String rootDirectory, String mainScript, Address address, int tickEveryMillis) {
        super(address);
        this.rootDirectory = rootDirectory;
        this.mainScript = mainScript;
        this.tickEveryMillis = tickEveryMillis;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public String getMainScript() {
        return mainScript;
    }

    public int getTickEveryMillis() {
        return tickEveryMillis;
    }

    @Override
    public String toString() {
        return "CreateWorkerEvent{" +
                "address=" + address +
                ", rootDirectory='" + rootDirectory + '\'' +
                ", mainScript='" + mainScript + '\'' +
                ", tickEveryMillis=" + tickEveryMillis +
                '}';
    }
}
