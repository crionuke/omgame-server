package com.crionuke.omgameserver;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
@QuarkusMain
public class OMGameServer {

    public static void main(String... args) {
        Thread.currentThread().setName("omg.s-main-thread-1");
        Quarkus.run(args);
    }
}