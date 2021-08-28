package com.crionuke.omgameserver;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0,
 */
@QuarkusMain
public class Omgs {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}