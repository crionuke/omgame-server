package com.crionuke.omgameserver.core;

import java.util.Objects;

public final class Address {

    private final String tenantId;
    private final String gameId;
    private final String workerId;

    public Address(String tenantId, String gameId, String workerId) {
        if (tenantId == null) {
            throw new NullPointerException("tenantId is null");
        }
        if (gameId == null) {
            throw new NullPointerException("gameId is null");
        }
        if (workerId == null) {
            throw new NullPointerException("workerId is null");
        }
        this.tenantId = tenantId;
        this.gameId = gameId;
        this.workerId = workerId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getWorkerId() {
        return workerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return tenantId.equals(address.tenantId) && gameId.equals(address.gameId) && workerId.equals(address.workerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, gameId, workerId);
    }

    @Override
    public String toString() {
        return "Address{" +
                "tenantId='" + tenantId + '\'' +
                ", gameId='" + gameId + '\'' +
                ", workerId='" + workerId + '\'' +
                '}';
    }
}
