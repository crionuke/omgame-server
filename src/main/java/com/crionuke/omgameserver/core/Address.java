package com.crionuke.omgameserver.core;

import java.util.Objects;

public final class Address {

    final String tenant;
    final String game;
    final String worker;

    public Address(String tenant, String game, String worker) {
        if (tenant == null) {
            throw new NullPointerException("tenant is null");
        }
        if (game == null) {
            throw new NullPointerException("game is null");
        }
        if (worker == null) {
            throw new NullPointerException("worker is null");
        }
        this.tenant = tenant;
        this.game = game;
        this.worker = worker;
    }

    public String getTenant() {
        return tenant;
    }

    public String getGame() {
        return game;
    }

    public String getWorker() {
        return worker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return tenant.equals(address.tenant) && game.equals(address.game) && worker.equals(address.worker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, game, worker);
    }

    public String asPath() {
        return tenant + "/" + game + "/" + worker;
    }

    @Override
    public String toString() {
        return "Address{" +
                "tenant='" + tenant + '\'' +
                ", game='" + game + '\'' +
                ", worker='" + worker + '\'' +
                '}';
    }
}
